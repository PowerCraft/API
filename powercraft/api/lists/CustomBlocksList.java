package powercraft.api.lists;

import powercraft.api.block.PC_Block;

/**
 * @author James
 * What the custom blocks added are
 */
 //TODO why not using HashMap<String, PC_Block> for fast access to Blocks by names????
public class CustomBlocksList {

	// TODO why not using blocks.length instead of blocksAmount??
	private short blocksAmount = 0;
	
	/**
	 * The blocks list
	 */
	public PC_Block[] blocks;
	
	/**
	 * Adds a block to the block list
	 * @param block The block to be added
	 */
	public void addBlock(PC_Block block){
		PC_Block[] tmp = new PC_Block[this.blocksAmount + 1];
		for(short i = 0; i < this.blocksAmount + 1; i++){
			tmp[i] = this.blocks[i];
		}
		tmp[this.blocksAmount] = block;
		this.blocks = tmp;
		//TODO you are never increasing blocksAmount, it will be always 0!!
	}
}
