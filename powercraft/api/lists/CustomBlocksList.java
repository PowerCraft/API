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
	 * The block names list
	 */
	public String[] blockNames;
	
	/**
	 * Adds a block to the block list
	 * @param block The block to be added
	 * @param name The block name to be added
	 */
	public void addBlock(Block block, String name){
		Block[] tmp = new Block[this.blocksAmount + 1];
		String[] tmp2 = new String[this.blocksAmount + 1];
		for(int i = 0; i < this.blocksAmount + 1; i++){
			tmp[i] = this.blocks[i];
			tmp2[i] = this.blockNames[i];
		}
		tmp[this.blocksAmount] = block;
		tmp2[this.blocksAmount] = name;
		this.blocks = tmp;
		this.blockNames = tmp2;
	}
}
