package de.unistuttgart.t2.inventory.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.unistuttgart.t2.common.saga.SagaData;
import de.unistuttgart.t2.common.saga.commands.ActionCommand;
import de.unistuttgart.t2.common.saga.commands.CompensationCommand;
import de.unistuttgart.t2.common.saga.commands.SagaCommand;
import de.unistuttgart.t2.inventory.InventoryService;
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;

/**
 * handles messages for the inventroy service.
 * 
 * listens to the {@code inventory} queue.
 * 
 * commits reservations upon receiving a
 * {@link de.unistuttgart.t2.common.saga.commands.ActionCommand ActionCommand}
 * or deletes reservations without committing them upon receiving a
 * {@link de.unistuttgart.t2.common.saga.commands.CompensationCommand
 * CompensationCommand}.
 * 
 * @author stiesssh
 *
 */
public class InventoryCommandHandler {
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private InventoryService inventoryService;

    public CommandHandlers commandHandlers() {
        return SagaCommandHandlersBuilder.fromChannel(SagaCommand.inventory)
                .onMessage(ActionCommand.class, this::commitReservation)
                .onMessage(CompensationCommand.class, this::undoReservations).build();
    }

    /**
     * delete reservations associated with given sessionId from products.
     * 
     * @param cm message with the command. also holds the session id
     * @return the reply message
     */
    public Message undoReservations(CommandMessage<CompensationCommand> cm) {
        LOG.info("inventory Compensation");
        CompensationCommand cmd = cm.getCommand();
        SagaData data = cmd.getData();

        inventoryService.handleSagaCompensation(data.getSessionId());

        return CommandHandlerReplyBuilder.withSuccess();
    }

    /**
     * commit reservations associated with given sessionId to a products.
     * 
     * @param cm message with the command. also holds the session id
     * @return the reply message
     */
    public Message commitReservation(CommandMessage<ActionCommand> cm) {
        LOG.info("inventory action");
        ActionCommand cmd = cm.getCommand();
        SagaData data = cmd.getData();

        inventoryService.handleSagaAction(data.getSessionId());

        return CommandHandlerReplyBuilder.withSuccess();
    }
}
