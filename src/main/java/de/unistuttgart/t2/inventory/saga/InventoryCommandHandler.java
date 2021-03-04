package de.unistuttgart.t2.inventory.saga;

import org.springframework.beans.factory.annotation.Autowired;

import de.unistuttgart.t2.common.commands.CommitReservationCommand;
import de.unistuttgart.t2.common.commands.InventoryCommand;
import de.unistuttgart.t2.common.commands.UndoReservationCommand;
import de.unistuttgart.t2.inventory.InventoryService;
import io.eventuate.tram.commands.consumer.CommandHandlerReplyBuilder;
import io.eventuate.tram.commands.consumer.CommandHandlers;
import io.eventuate.tram.commands.consumer.CommandMessage;
import io.eventuate.tram.messaging.common.Message;
import io.eventuate.tram.sagas.participant.SagaCommandHandlersBuilder;

public class InventoryCommandHandler {

	@Autowired
	private InventoryService inventoryService;
	
	public CommandHandlers commandHandlers() {
		return SagaCommandHandlersBuilder.fromChannel(InventoryCommand.channel)
				.onMessage(CommitReservationCommand.class, this::commitReservation)
				.onMessage(UndoReservationCommand.class, this::undoReservation)
				.build();
	}
	

	/**
	 * commit the previously reserved item to the actual inventory. 
	 * 
	 * @param cm
	 * @return
	 */
	public Message undoReservation(CommandMessage<UndoReservationCommand> cm) {
		
		UndoReservationCommand cmd = cm.getCommand();
		inventoryService.undoReservation(cmd.getId());
		
		return CommandHandlerReplyBuilder.withSuccess();
	}
	
	
	/**
	 * commit the previously reserved item to the actual inventory. 
	 * 
	 * @param cm
	 * @return
	 */
	public Message commitReservation(CommandMessage<CommitReservationCommand> cm) {
		
		CommitReservationCommand cmd = cm.getCommand();
		inventoryService.commitReservation(cmd.getId());
		
		return CommandHandlerReplyBuilder.withSuccess();
	}
}
