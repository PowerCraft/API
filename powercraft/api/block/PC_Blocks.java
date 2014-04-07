package powercraft.api.block;

import java.util.ArrayList;
import java.util.List;

import powercraft.api.PC_Api;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Logger;
import powercraft.api.reflect.PC_Security;

public final class PC_Blocks {

	private static boolean doneConstruct;
	private static boolean doneRecipes;
	private static List<PC_AbstractBlockBase> blocks = new ArrayList<PC_AbstractBlockBase>();
	private static List<PC_AbstractBlockBase> immutableBlocks = new PC_ImmutableList<PC_AbstractBlockBase>(blocks);
	
	static void addBlock(PC_AbstractBlockBase block) {
		if(doneConstruct){
			PC_Logger.severe("A block want to register while startup is done");
		}else{
			PC_Logger.info("Block-ADD: %s", block);
			blocks.add(block);
		}
	}
	
	public static List<PC_AbstractBlockBase> getBlocks(){
		return immutableBlocks;
	}

	public static void construct(){
		PC_Security.allowedCaller("PC_Blocks.construct()", PC_Api.class);
		if(!doneConstruct){
			doneConstruct = true;
			for(PC_AbstractBlockBase block:blocks){
				PC_Logger.info("CONSTRUCT: %s", block);
				block.construct();
			}
		}
	}
	
	public static void initRecipes() {
		PC_Security.allowedCaller("PC_Blocks.initRecipes()", PC_Api.class);
		if(!doneRecipes && doneConstruct){
			doneRecipes = true;
			for(PC_AbstractBlockBase block:blocks){
				block.initRecipes();
			}
		}
	}
	
	private PC_Blocks(){
		throw new InstantiationError();
	}
	
}
