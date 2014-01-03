package powercraft.api.block;

import powercraft.api.PowerCraft;
import powercraft.api.lists.CustomBlocksList;

/**
 * @author James
 * Block for use in ONLY powercraft API
 */
public class BlockUtils {
	
	/**
	 * Get a minecraft block from a block name
	 * @param name The block name
	 * @return The minecraft block
	 */
	public static net.minecraft.block.Block getBlockFromName(String name){
		switch(name){
		case "anvil":
			return net.minecraft.block.Block.anvil;
		case "bed":
			return net.minecraft.block.Block.bed;
		case "bedrock":
			return net.minecraft.block.Block.bedrock;
		case "clay":
			return net.minecraft.block.Block.blockClay;
		case "diamond block":
			return net.minecraft.block.Block.blockDiamond;
		case "emerald block":
			return net.minecraft.block.Block.blockEmerald;
		case "gold block":
			return net.minecraft.block.Block.blockGold;
		case "iron block":
			return net.minecraft.block.Block.blockIron;
		case "lapis block":
			return net.minecraft.block.Block.blockLapis;
		case "nether quartz ore":
			return net.minecraft.block.Block.blockNetherQuartz;
		case "redstone block":
			return net.minecraft.block.Block.blockRedstone;
		case "snow":
			return net.minecraft.block.Block.blockSnow;
		case "bookshelf":
			return net.minecraft.block.Block.bookShelf;
		case "brewing stand":
			return net.minecraft.block.Block.brewingStand;
		case "bricks":
			return net.minecraft.block.Block.brick;
		case "beacon":
			return net.minecraft.block.Block.beacon;
		case "cactus":
			return net.minecraft.block.Block.cactus;
		case "cake":
			return net.minecraft.block.Block.cake;
		case "carpet":
			return net.minecraft.block.Block.carpet;
		case "carrot":
			return net.minecraft.block.Block.carrot;
		case "trapped chest":
			return net.minecraft.block.Block.chestTrapped;
		case "cloth":
			return net.minecraft.block.Block.cloth;
		case "coal block":
			return net.minecraft.block.Block.coalBlock;
		case "cobblestone":
			return net.minecraft.block.Block.cobblestone;
		case "mossy cobblestone":
			return net.minecraft.block.Block.cobblestoneMossy;
		case "cobblestone wall":
			return net.minecraft.block.Block.cobblestoneWall;
		case "cocoa plant":
			return net.minecraft.block.Block.cocoaPlant;
		case "command block":
			return net.minecraft.block.Block.commandBlock;
		case "crops":
			return net.minecraft.block.Block.crops;
		case "cauldron":
			return net.minecraft.block.Block.cauldron;
		case "chest":
			return net.minecraft.block.Block.chest;
		case "dirt":
			return net.minecraft.block.Block.dirt;
		case "dispenser":
			return net.minecraft.block.Block.dispenser;
		case "iron door":
			return net.minecraft.block.Block.doorIron;
		case "wood door":
			return net.minecraft.block.Block.doorWood;
		case "dragon egg":
			return net.minecraft.block.Block.dragonEgg;
		case "dropper":
			return net.minecraft.block.Block.dropper;
		case "daylight sensor":
			return net.minecraft.block.Block.daylightSensor;
		case "dead bush":
			return net.minecraft.block.Block.deadBush;
		case "enchantment table":
			return net.minecraft.block.Block.enchantmentTable;
		case "ender chest":
			return net.minecraft.block.Block.enderChest;
		case "end portal":
			return net.minecraft.block.Block.endPortal;
		case "end portal frame":
			return net.minecraft.block.Block.endPortalFrame;
		case "fence":
			return net.minecraft.block.Block.fence;
		case "fence gate":
			return net.minecraft.block.Block.fenceGate;
		case "iron fence":
			return net.minecraft.block.Block.fenceIron;
		case "flower pot":
			return net.minecraft.block.Block.flowerPot;
		case "furnace burning":
			return net.minecraft.block.Block.furnaceBurning;
		case "furnace":
			return net.minecraft.block.Block.furnaceIdle;
		case "fire":
			return net.minecraft.block.Block.fire;
		case "glass":
			return net.minecraft.block.Block.glass;
		case "glowstone":
			return net.minecraft.block.Block.glowStone;
		case "gravel":
			return net.minecraft.block.Block.gravel;
		case "grass":
			return net.minecraft.block.Block.grass;
		case "hardened clay":
			return net.minecraft.block.Block.hardenedClay;
		case "hay":
			return net.minecraft.block.Block.hay;
		case "hopper block":
			return net.minecraft.block.Block.hopperBlock;
		case "ice":
			return net.minecraft.block.Block.ice;
		case "jukebox":
			return net.minecraft.block.Block.jukebox;
		case "melon":
			return net.minecraft.block.Block.melon;
		case "melon stem":
			return net.minecraft.block.Block.melonStem;
		case "mob spawner":
			return net.minecraft.block.Block.mobSpawner;
		case "brown mushroom cap":
			return net.minecraft.block.Block.mushroomCapBrown;
		case "red mushroom cap":
			return net.minecraft.block.Block.mushroomCapRed;
		case "noteblock":
			return net.minecraft.block.Block.music;
		case "brown mushroom":
			return net.minecraft.block.Block.mushroomBrown;
		case "red mushroom":
			return net.minecraft.block.Block.mushroomRed;
		case "mycelium":
			return net.minecraft.block.Block.mycelium;
		case "nether brick":
			return net.minecraft.block.Block.netherBrick;
		case "nether fence":
			return net.minecraft.block.Block.netherFence;
		case "netherrack":
			return net.minecraft.block.Block.netherrack;
		case "netherwart":
			return net.minecraft.block.Block.netherStalk;
		case "obsidian":
			return net.minecraft.block.Block.obsidian;
		case "coal ore":
			return net.minecraft.block.Block.oreCoal;
		case "diamond ore":
			return net.minecraft.block.Block.oreDiamond;
		case "emerald ore":
			return net.minecraft.block.Block.oreEmerald;
		case "gold ore":
			return net.minecraft.block.Block.oreGold;
		case "iron ore":
			return net.minecraft.block.Block.oreIron;
		case "lapis ore":
			return net.minecraft.block.Block.oreLapis;
		case "quartz ore":
			return net.minecraft.block.Block.oreNetherQuartz;
		case "redstone ore":
			return net.minecraft.block.Block.oreRedstone;
		case "on redstone ore":
			return net.minecraft.block.Block.oreRedstoneGlowing;
		case "planks":
			return net.minecraft.block.Block.planks;
		case "potato":
			return net.minecraft.block.Block.potato;
		case "gold pressure plate":
			return net.minecraft.block.Block.pressurePlateGold;
		case "iron pressure plate":
			return net.minecraft.block.Block.pressurePlateIron;
		case "wood pressure plate":
			return net.minecraft.block.Block.pressurePlatePlanks;
		case "stone pressure plate":
			return net.minecraft.block.Block.pressurePlateStone;
		case "pumpkin":
			return net.minecraft.block.Block.pumpkin;
		case "jack o lantern":
			return net.minecraft.block.Block.pumpkinLantern;
		case "pumpkin stem":
			return net.minecraft.block.Block.pumpkinStem;
		case "piston":
			return net.minecraft.block.Block.pistonBase;
		case "piston extension":
			return net.minecraft.block.Block.pistonExtension;
		case "moving piston":
			return net.minecraft.block.Block.pistonMoving;
		case "sticky piston":
			return net.minecraft.block.Block.pistonStickyBase;
		case "rose":
			return net.minecraft.block.Block.plantRed;
		case "dandilion":
			return net.minecraft.block.Block.plantYellow;
		case "nether portal":
			return net.minecraft.block.Block.portal;
		case "rail":
			return net.minecraft.block.Block.rail;
		case "activator rail":
			return net.minecraft.block.Block.railActivator;
		case "detector rail":
			return net.minecraft.block.Block.railDetector;
		case "powered rail":
			return net.minecraft.block.Block.railPowered;
		case "redstone lamp on":
			return net.minecraft.block.Block.redstoneLampActive;
		case "redstone lamp":
			return net.minecraft.block.Block.redstoneLampIdle;
		case "reed":
			return net.minecraft.block.Block.reed;
		case "sugar cane":
			return net.minecraft.block.Block.reed;
		case "comparator on":
			return net.minecraft.block.Block.redstoneComparatorActive;
		case "comparator":
			return net.minecraft.block.Block.redstoneComparatorIdle;
		case "redstone repeater on":
			return net.minecraft.block.Block.redstoneRepeaterActive;
		case "redstone repeater":
			return net.minecraft.block.Block.redstoneRepeaterIdle;
		case "redstone":
			return net.minecraft.block.Block.redstoneWire;
		case "sand":
			return net.minecraft.block.Block.sand;
		case "sandstone":
			return net.minecraft.block.Block.sandStone;
		case "sapling":
			return net.minecraft.block.Block.sapling;
		case "sign post":
			return net.minecraft.block.Block.signPost;
		case "wall sign":
			return net.minecraft.block.Block.signWall;
		case "silverfish":
			return net.minecraft.block.Block.silverfish;
		case "head":
			return net.minecraft.block.Block.skull;
		case "soulsand":
			return net.minecraft.block.Block.slowSand;
		case "fallen snow":
			return net.minecraft.block.Block.snow;
		case "sponge":
			return net.minecraft.block.Block.sponge;
		case "stained clay":
			return net.minecraft.block.Block.stainedClay;
		case "brick stairs":
			return net.minecraft.block.Block.stairsBrick;
		case "cobblestone stairs":
			return net.minecraft.block.Block.stairsCobblestone;
		case "nether brick stairs":
			return net.minecraft.block.Block.stairsNetherBrick;
		case "quartz stairs":
			return net.minecraft.block.Block.stairsNetherQuartz;
		case "sandstone stairs":
			return net.minecraft.block.Block.stairsSandStone;
		case "stone brick stairs":
			return net.minecraft.block.Block.stairsStoneBrick;
		case "birch stairs":
			return net.minecraft.block.Block.stairsWoodBirch;
		case "jungle stairs":
			return net.minecraft.block.Block.stairsWoodJungle;
		case "oak stairs":
			return net.minecraft.block.Block.stairsWoodOak;
		case "spruce wood":
			return net.minecraft.block.Block.stairsWoodSpruce;
		case "stone":
			return net.minecraft.block.Block.stone;
		case "stone brick":
			return net.minecraft.block.Block.stoneBrick;
		case "stone button":
			return net.minecraft.block.Block.stoneButton;
		case "double stone slab":
			return net.minecraft.block.Block.stoneDoubleSlab;
		case "stone slab":
			return net.minecraft.block.Block.stoneSingleSlab;
		case "glass pane":
			return net.minecraft.block.Block.thinGlass;
		case "tilled dirt":
			return net.minecraft.block.Block.tilledField;
		case "tnt":
			return net.minecraft.block.Block.tnt;
		case "redstone torch on":
			return net.minecraft.block.Block.torchRedstoneActive;
		case "redstone torch off":
			return net.minecraft.block.Block.torchRedstoneIdle;
		case "torch":
			return net.minecraft.block.Block.torchWood;
		case "trapdoor":
			return net.minecraft.block.Block.trapdoor;
		case "trip wire":
			return net.minecraft.block.Block.tripWire;
		case "tall grass":
			return net.minecraft.block.Block.tallGrass;
		case "trip wire hook":
			return net.minecraft.block.Block.tripWireSource;
		case "vine":
			return net.minecraft.block.Block.vine;
		case "water lily":
			return net.minecraft.block.Block.waterlily;
		case "still water":
			return net.minecraft.block.Block.waterStill;
		case "web":
			return net.minecraft.block.Block.web;
		case "endstone":
			return net.minecraft.block.Block.whiteStone;
		case "log":
			return net.minecraft.block.Block.wood;
		case "wooden button":
			return net.minecraft.block.Block.woodenButton;
		case "crafting table":
			return net.minecraft.block.Block.workbench;
		case "workbench":
			return net.minecraft.block.Block.workbench;
		case "flowing water":
			return net.minecraft.block.Block.waterMoving;
		case "dobule wood slab":
			return net.minecraft.block.Block.woodDoubleSlab;
		case "wood slab":
			return net.minecraft.block.Block.woodSingleSlab;
		default:
			break;
		}
		
		CustomBlocksList cbl = PowerCraft.pc.blocksList;
		for(short i = 0; i < cbl.blocks.length; i++){
			if(name == cbl.blocks[i].blockName){
				return cbl.blocks[i];
			}
		}
		return null;
	}
}
