package powercraft.api.script.weasel.events;

import powercraft.api.script.weasel.PC_IWeaselEvent;

public class PC_WeaselEventInventorySlotEmpty implements PC_IWeaselEvent {
	private final String inventory;
	private final int address, slot;
	
	public PC_WeaselEventInventorySlotEmpty(int address, String inventory, int slot){
		this.address = address;
		this.inventory = inventory;
		this.slot = slot;
	}
	
	
	@Override
	public String getEventName() {
		return "Slot Empty Event";
	}

	@Override
	public String getEntryClass() {
		return "weasel.miner.Miner";
	}

	@Override
	public String getEntryMethod() {
		return "inventorySlotEmptyInterruptEntryPoint(int, xscript.lang.String, int)void";
	}

	@Override
	public Object[] getParams() {
		
		return new Object[]{Long.valueOf(address), inventory, Long.valueOf(slot)};
	}

}
