package powercraft.api.lists;

import powercraft.api.block.Block;

/**
 * @author James
 * What the custom blocks added are
 */
public class CustomBlocksList {

	private int blocksAmount = 0;
	
	/**
	 * The blocks list
	 */
	public Block[] blocks;
	
	/**
	 * Adds a block to the block list
	 * @param block The block to be added
	 */
	public void addBlock(Block block){
		Block[] tmp = new Block[this.blocksAmount + 1];
		for(int i = 0; i < this.blocksAmount + 1; i++){
			tmp[i] = this.blocks[i];
		}
		tmp[this.blocksAmount] = block;
		this.blocks = tmp;
	}
}
