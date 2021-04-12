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

public class InventoryCommandHandler {
	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private InventoryService inventoryService;
	
	public CommandHandlers commandHandlers() {
		return SagaCommandHandlersBuilder.fromChannel(SagaCommand.inventory)
				.onMessage(ActionCommand.class, this::commitReservation)
				.onMessage(CompensationCommand.class, this::undoReservations)
				.build();
	}
	

	/**
	 * delete reservations... i guess? 
	 * 
	 * @param cm
	 * @return
	 */
	public Message undoReservations(CommandMessage<CompensationCommand> cm) {
		LOG.info("inventory Compensation");
		CompensationCommand cmd = cm.getCommand();
		SagaData data = cmd.getData();
		
		inventoryService.handleSagaCompensation(data.getSessionId());
		
		return CommandHandlerReplyBuilder.withSuccess();
	}
	
	
	/**
	 * commit the previously reserved item to the actual inventory. 
	 * 
	 * @param cm
	 * @return
	 */
	public Message commitReservation(CommandMessage<ActionCommand> cm) {
		LOG.info("inventory action");
		ActionCommand cmd = cm.getCommand();
		SagaData data = cmd.getData();
		
		inventoryService.handleSagaAction(data.getSessionId());
		
		return CommandHandlerReplyBuilder.withSuccess();
	}
}
