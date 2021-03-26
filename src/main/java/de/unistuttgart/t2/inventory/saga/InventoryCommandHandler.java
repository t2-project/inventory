package de.unistuttgart.t2.inventory.saga;

import org.springframework.beans.factory.annotation.Autowired;

import de.unistuttgart.t2.common.commands.inventory.InventoryAction;
import de.unistuttgart.t2.common.commands.inventory.InventoryCommand;
import de.unistuttgart.t2.common.commands.inventory.InventoryCompensation;
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
				.onMessage(InventoryAction.class, this::commitReservation)
				.onMessage(InventoryCompensation.class, this::undoReservation)
				.build();
	}
	

	/**
	 * commit the previously reserved item to the actual inventory. 
	 * 
	 * @param cm
	 * @return
	 */
	public Message undoReservation(CommandMessage<InventoryCompensation> cm) {
		
		InventoryCompensation cmd = cm.getCommand();
		inventoryService.handleSagaCompensation("TODO");
		
		return CommandHandlerReplyBuilder.withSuccess();
	}
	
	
	/**
	 * commit the previously reserved item to the actual inventory. 
	 * 
	 * @param cm
	 * @return
	 */
	public Message commitReservation(CommandMessage<InventoryAction> cm) {
		
		InventoryAction cmd = cm.getCommand();
		// do somethign with return value, such that you can deside on sucess on failure
		if inventoryService.handleSagaAction("TODO");
		
		return CommandHandlerReplyBuilder.withSuccess();
	}
}
