package net.powercrafting.API.registry;

import java.util.Vector;
import net.powercrafting.API.block.PC_BlockGeneric;
import net.powercrafting.API.block.PC_InventoryBlockGeneric;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class BlockRegistry {
	public static Vector<PC_BlockGeneric> genericBlocks;
	public static Vector<PC_InventoryBlockGeneric> inventoryBlocks;

	/**
	 * Registers a generic Block
	 * 
	 * @param block
	 *            the Block to register
	 * @param unlocName
	 *            the unlocalized name of the Block
	 * @param engName
	 *            the ENGLISH name of the Block
	 */
	public static void registerBlock(PC_BlockGeneric block, String unlocName,
			String engName) {
		genericBlocks.add(block);
		GameRegistry.registerBlock(block, unlocName);
		LanguageRegistry.addName(block, engName);
	}

	/**
	 * Registers an Inventory Block
	 * 
	 * @param block
	 *            the Block to register
	 * @param unlocName
	 *            the unlocalized name of the Block
	 * @param engName
	 *            the ENGLISH name of the Block
	 */
	public static void registerInventoryBlock(PC_InventoryBlockGeneric block,
			String unlocName, String engName) {
		inventoryBlocks.add(block);
		GameRegistry.registerBlock(block, unlocName);
		LanguageRegistry.addName(block, engName);
	}
}
