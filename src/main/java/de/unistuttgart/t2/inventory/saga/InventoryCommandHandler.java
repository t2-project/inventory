package de.unistuttgart.t2.inventory.saga;

import org.springframework.beans.factory.annotation.Autowired;

import de.unistuttgart.t2.common.commands.CheckCreditCommand;
import de.unistuttgart.t2.common.commands.DecreaseInventoryCommand;
import de.unistuttgart.t2.common.commands.IncreaseInventoryCommand;
import de.unistuttgart.t2.common.commands.UpdateInventoryCommand;
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
		return SagaCommandHandlersBuilder.fromChannel(UpdateInventoryCommand.channel)
				.onMessage(IncreaseInventoryCommand.class, this::increaseInventory)
				.onMessage(DecreaseInventoryCommand.class, this::decreaseInventory)
				.build();
	}
	
	/**
	 * 
	 * @param cm
	 * @return
	 */
	public Message increaseInventory(CommandMessage<IncreaseInventoryCommand> cm) {
		IncreaseInventoryCommand cmd = cm.getCommand();
		
		//TODO logic with failures
		inventoryService.increase(cmd.getProductId(), cmd.getAmount());
		return CommandHandlerReplyBuilder.withSuccess();
	}
	
	/**
	 * 
	 * @param cm
	 * @return
	 */
	public Message decreaseInventory(CommandMessage<DecreaseInventoryCommand> cm) {
		DecreaseInventoryCommand cmd = cm.getCommand();
		
		//TODO logic with failures
		inventoryService.decrease(cmd.getProductId(), cmd.getAmount());
		return CommandHandlerReplyBuilder.withSuccess();
	}
}
