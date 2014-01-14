package powercraft_new.api.lists;

import java.util.HashMap;

import powercraft_new.api.block.PC_Block;

/**
 * @author James
 * What the custom blocks added are
 */
public class CustomBlocksList {
	
	/**
	 * The blocks list
	 */
	public HashMap<String, PC_Block> blocks;
	
	/**
	 * Adds a block to the block list
	 * @param block The block to be added
	 */
	public void addBlock(PC_Block block){
		this.blocks.put(block.blockName, block);
	}
}
