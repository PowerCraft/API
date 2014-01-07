package powercraft.api.item;

import powercraft.api.PowerCraft;
import powercraft.api.block.BlockUtils;
import powercraft.api.lists.CustomItemsList;

/**
 * @author James
 * Item to NOT be used by anything other then the API
 */
public class ItemUtils {
	
	/**
	 * @param itemname The item to break the block with
	 * @param blockname The block to be broken
	 * @return Can the item break the block?
	 */
	public static boolean canHarvestBlock(String itemname, String blockname){
		return canHarvestBlock(getItemFromName(itemname), blockname);
	}
	
	/*
	 * Warning: The following code is a
       _..--=--..._
    .-'            '-.  .-.
   /.'              '.\/  /
  |=-                -=| (
   \'.              .'/\  \
    '-.,_____ _____.-'  '-'
         [_____]=8
            
         Shrink it if you want
	 */
	
	/**
	 * Gets the net.minecraft.item.Item from it's name
	 * @param name The name of the item 
	 */
	private static final net.minecraft.item.Item getItemFromName(String name) {
		switch(name){
		case "gold apple":
			return net.minecraft.item.Item.appleGold;
		case "apple":
			return net.minecraft.item.Item.appleRed;
		case "arrow":
			return net.minecraft.item.Item.arrow;
		case "diamond axe":
			return net.minecraft.item.Item.axeDiamond;
		case "gold axe":
			return net.minecraft.item.Item.axeGold;
		case "iron axe":
			return net.minecraft.item.Item.axeIron;
		case "stone axe":
			return net.minecraft.item.Item.axeStone;
		case "wood axe":
			return net.minecraft.item.Item.axeWood;
		case "baked potato":
			return net.minecraft.item.Item.bakedPotato;
		case "bed":
			return net.minecraft.item.Item.bed;
		case "cooked beef":
			return net.minecraft.item.Item.beefCooked;
		case "raw beef":
			return net.minecraft.item.Item.beefRaw;
		case "blaze powder":
			return net.minecraft.item.Item.blazePowder;
		case "blaze rod":
			return net.minecraft.item.Item.blazeRod;
		case "boat":
			return net.minecraft.item.Item.boat;
		case "bone":
			return net.minecraft.item.Item.bone;
		case "book":
			return net.minecraft.item.Item.book;
		case "bowl":
			return net.minecraft.item.Item.bowlEmpty;
		case "mushroom soup":
			return net.minecraft.item.Item.bowlSoup;
		case "bread":
			return net.minecraft.item.Item.bread;
		case "brewing stand":
			return net.minecraft.item.Item.brewingStand;
		case "brick":
			return net.minecraft.item.Item.brick;
		case "bucket":
			return net.minecraft.item.Item.bucketEmpty;
		case "water bucket":
			return net.minecraft.item.Item.bucketWater;
		case "lava bucket":
			return net.minecraft.item.Item.bucketLava;
		case "milk bucket":
			return net.minecraft.item.Item.bucketMilk;
		case "cake":
			return net.minecraft.item.Item.cake;
		case "carrot":
			return net.minecraft.item.Item.carrot;
		case "carrot on a stick":
			return net.minecraft.item.Item.carrotOnAStick;
		case "cauldron":
			return net.minecraft.item.Item.cauldron;
		case "cooked chicken":
			return net.minecraft.item.Item.chickenCooked;
		case "raw chicken":
			return net.minecraft.item.Item.chickenRaw;
		case "clay":
			return net.minecraft.item.Item.clay;
		case "coal":
			return net.minecraft.item.Item.coal;
		case "comparator":
			return net.minecraft.item.Item.comparator;
		case "compass":
			return net.minecraft.item.Item.compass;
		case "cookie":
			return net.minecraft.item.Item.cookie;
		case "diamond":
			return net.minecraft.item.Item.diamond;
		case "iron door":
			return net.minecraft.item.Item.doorIron;
		case "wood door":
			return net.minecraft.item.Item.doorWood;
		case "dye":
			return net.minecraft.item.Item.dyePowder;
		case "egg":
			return net.minecraft.item.Item.egg;
		case "emerald":
			return net.minecraft.item.Item.emerald;
		case "ender pearl":
			return net.minecraft.item.Item.enderPearl;
		case "exp bottle":
			return net.minecraft.item.Item.expBottle;
		case "eye of ender":
			return net.minecraft.item.Item.eyeOfEnder;
		case "empty map":
			return net.minecraft.item.Item.emptyMap;
		case "enchanted book":
			return net.minecraft.item.Item.enchantedBook;
		case "feather":
			return net.minecraft.item.Item.feather;
		case "fermented spider eye":
			return net.minecraft.item.Item.fermentedSpiderEye;
		case "fireball":
			return net.minecraft.item.Item.fireballCharge;
		case "firework":
			return net.minecraft.item.Item.firework;
		case "firework charge":
			return net.minecraft.item.Item.fireworkCharge;
		case "cooked fish":
			return net.minecraft.item.Item.fishCooked;
		case "raw fish":
			return net.minecraft.item.Item.fishRaw;
		case "flint":
			return net.minecraft.item.Item.flint;
		case "flint and steel":
			return net.minecraft.item.Item.flintAndSteel;
		case "flower pot":
			return net.minecraft.item.Item.flowerPot;
		case "fishing rod":
			return net.minecraft.item.Item.fishingRod;
		case "ghast tear":
			return net.minecraft.item.Item.ghastTear;
		case "glass bottle":
			return net.minecraft.item.Item.glassBottle;
		case "glowstone":
			return net.minecraft.item.Item.glowstone;
		case "gold carrot":
			return net.minecraft.item.Item.goldenCarrot;
		case "gold nugget":
			return net.minecraft.item.Item.goldNugget;
		case "gunpowder":
			return net.minecraft.item.Item.gunpowder;
		case "diamond hoe":
			return net.minecraft.item.Item.hoeDiamond;
		case "gold hoe":
			return net.minecraft.item.Item.hoeGold;
		case "iron hoe":
			return net.minecraft.item.Item.hoeIron;
		case "stone hoe":
			return net.minecraft.item.Item.hoeStone;
		case "wood hoe":
			return net.minecraft.item.Item.hoeWood;
		case "diamond horse armor":
			return net.minecraft.item.Item.horseArmorDiamond;
		case "gold horse armor":
			return net.minecraft.item.Item.horseArmorGold;
		case "iron horse armor":
			return net.minecraft.item.Item.horseArmorIron;
		case "chain helmet":
			return net.minecraft.item.Item.helmetChain;
		case "gold helmet":
			return net.minecraft.item.Item.helmetGold;
		case "iron helmet":
			return net.minecraft.item.Item.helmetIron;
		case "diamond helmet":
			return net.minecraft.item.Item.helmetDiamond;
		case "leather helmet":
			return net.minecraft.item.Item.helmetLeather;
		case "gold ingot":
			return net.minecraft.item.Item.ingotGold;
		case "iron ingot":
			return net.minecraft.item.Item.ingotIron;
		case "leash":
			return net.minecraft.item.Item.leash;
		case "leather":
			return net.minecraft.item.Item.leather;
		case "chain legs":
			return net.minecraft.item.Item.legsChain;
		case "gold legs":
			return net.minecraft.item.Item.legsGold;
		case "diamond legs":
			return net.minecraft.item.Item.legsDiamond;
		case "iron legs":
			return net.minecraft.item.Item.legsIron;
		case "leather legs":
			return net.minecraft.item.Item.legsLeather;
		case "magma cream":
			return net.minecraft.item.Item.magmaCream;
		case "melon":
			return net.minecraft.item.Item.melon;
		case "melon seeds":
			return net.minecraft.item.Item.melonSeeds;
		case "chest minecart":
			return net.minecraft.item.Item.minecartCrate;
		case "minecart":
			return net.minecraft.item.Item.minecartEmpty;
		case "hopper minecart":
			return net.minecraft.item.Item.minecartHopper;
		case "furnace minecart":
			return net.minecraft.item.Item.minecartPowered;
		case "tnt minecart":
			return net.minecraft.item.Item.minecartTnt;
		case "monster spawner":
			return net.minecraft.item.Item.monsterPlacer;
		case "written map":
			return net.minecraft.item.Item.map;
		case "name tag":
			return net.minecraft.item.Item.nameTag;
		case "quartz":
			return net.minecraft.item.Item.netherQuartz;
		case "nether brick":
			return net.minecraft.item.Item.netherrackBrick;
		case "netherwart":
			return net.minecraft.item.Item.netherStalkSeeds;
		case "nether star":
			return net.minecraft.item.Item.netherStar;
		case "painting":
			return net.minecraft.item.Item.painting;
		case "paper":
			return net.minecraft.item.Item.paper;
		case "diamond pick":
			return net.minecraft.item.Item.pickaxeDiamond;
		case "gold pick":
			return net.minecraft.item.Item.pickaxeGold;
		case "iron pick":
			return net.minecraft.item.Item.pickaxeIron;
		case "stone pick":
			return net.minecraft.item.Item.pickaxeStone;
		case "wood pick":
			return net.minecraft.item.Item.pickaxeWood;
		case "clock":
			return net.minecraft.item.Item.pocketSundial;
		case "poisonous potato":
			return net.minecraft.item.Item.poisonousPotato;
		case "raw pork":
			return net.minecraft.item.Item.porkRaw;
		case "cooked port":
			return net.minecraft.item.Item.porkCooked;
		case "potato":
			return net.minecraft.item.Item.potato;
		case "pumpkin pie":
			return net.minecraft.item.Item.pumpkinPie;
		case "pumpkin seeds":
			return net.minecraft.item.Item.pumpkinSeeds;
		case "chain chestplate":
			return net.minecraft.item.Item.plateChain;
		case "diamond chestplate":
			return net.minecraft.item.Item.plateDiamond;
		case "gold chestplate":
			return net.minecraft.item.Item.plateGold;
		case "iron chestplate":
			return net.minecraft.item.Item.plateIron;
		case "leather chestplate":
			return net.minecraft.item.Item.plateLeather;
		case "potion":
			return net.minecraft.item.Item.potion;
		case "record 11":
			return net.minecraft.item.Item.record11;
		case "record 13":
			return net.minecraft.item.Item.record13;
		case "record blocks":
			return net.minecraft.item.Item.recordBlocks;
		case "record cat":
			return net.minecraft.item.Item.recordCat;
		case "record chirp":
			return net.minecraft.item.Item.recordChirp;
		case "record far":
			return net.minecraft.item.Item.recordFar;
		case "record mall":
			return net.minecraft.item.Item.recordMall;
		case "record mellohi":
			return net.minecraft.item.Item.recordMellohi;
		case "record stal":
			return net.minecraft.item.Item.recordStal;
		case "record strad":
			return net.minecraft.item.Item.recordStrad;
		case "record wait":
			return net.minecraft.item.Item.recordWait;
		case "record ward":
			return net.minecraft.item.Item.recordWard;
		case "redstone":
			return net.minecraft.item.Item.redstone;
		case "redstone repeater":
			return net.minecraft.item.Item.redstoneRepeater;
		case "reed":
			return net.minecraft.item.Item.reed;
		case "rotten flesh":
			return net.minecraft.item.Item.rottenFlesh;
		case "saddle":
			return net.minecraft.item.Item.saddle;
		case "seeds":
			return net.minecraft.item.Item.seeds;
		case "diamond shovel":
			return net.minecraft.item.Item.shovelDiamond;
		case "gold shovel":
			return net.minecraft.item.Item.shovelGold;
		case "iron shovel":
			return net.minecraft.item.Item.shovelIron;
		case "stone shovel":
			return net.minecraft.item.Item.shovelStone;
		case "wood shovel":
			return net.minecraft.item.Item.shovelWood;
		case "sign":
			return net.minecraft.item.Item.sign;
		case "silk":
			return net.minecraft.item.Item.silk;
		case "head":
			return net.minecraft.item.Item.skull;
		case "slimeball":
			return net.minecraft.item.Item.slimeBall;
		case "snowball":
			return net.minecraft.item.Item.snowball;
		case "gold melon":
			return net.minecraft.item.Item.speckledMelon;
		case "spider eye":
			return net.minecraft.item.Item.spiderEye;
		case "stick":
			return net.minecraft.item.Item.stick;
		case "sugar":
			return net.minecraft.item.Item.sugar;
		case "diamond sword":
			return net.minecraft.item.Item.swordDiamond;
		case "gold sword":
			return net.minecraft.item.Item.swordGold;
		case "iron sword":
			return net.minecraft.item.Item.swordIron;
		case "stone sword":
			return net.minecraft.item.Item.swordStone;
		case "wood sword":
			return net.minecraft.item.Item.swordWood;
		case "shears":
			return net.minecraft.item.Item.shears;
		case "wheat":
			return net.minecraft.item.Item.wheat;
		case "book and quill":
			return net.minecraft.item.Item.writableBook;
		case "written book":
			return net.minecraft.item.Item.writtenBook;
		default:
			break;
		}
		
		CustomItemsList cbl = PowerCraft.pc.itemsList;
		for(int i = 0; i < cbl.items.length; i++){
			if(name == cbl.items[i].itemName){
				return cbl.items[i];
			}
		}
		return null;
	}

	/**
	 * @param item The item to try with
	 * @param name The block to be harvested
	 * @return Can the item harvest the block?
	 */
	public static final boolean canHarvestBlock(net.minecraft.item.Item item, String name){
		return item.canHarvestBlock(BlockUtils.getBlockFromName(name));
	}
}
