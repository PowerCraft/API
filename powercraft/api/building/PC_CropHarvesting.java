package powercraft.api.building;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import powercraft.api.PC_Api;
import powercraft.api.PC_Direction;
import powercraft.api.PC_Logger;
import powercraft.api.PC_Utils;
import powercraft.api.building.PC_Build.ItemStackSpawn;
import powercraft.api.reflect.PC_Security;

public final class PC_CropHarvesting implements PC_ISpecialHarvesting {

	private static final PC_CropHarvesting INSTANCE = new PC_CropHarvesting();
	private static final File folder = PC_Utils.getPowerCraftFile("crops", null);
	private static final PC_Direction sideList[] = {PC_Direction.UP, PC_Direction.NORTH, PC_Direction.SOUTH, PC_Direction.EAST, PC_Direction.WEST, PC_Direction.DOWN};
	private static List<Crop> crops;
	static Random rand = new Random();
	
	private PC_CropHarvesting(){
		if(INSTANCE!=null){
			PC_Utils.staticClassConstructor();
		}
	}
	
	public static void register(){
		PC_Security.allowedCaller("PC_TreeHarvesting.register()", PC_Api.class);
		loadCrops();
		PC_Build.addSpecialHarvesting(INSTANCE);
	}
	
	@Override
	public boolean useFor(World world, int x, int y, int z, Block block, int meta, int priority) {
		if(priority<2){
			return false;
		}
		if(getCropFor(block, meta)!=null){
			return true;
		}
		return block instanceof BlockCrops;
	}

	@Override
	public List<ItemStackSpawn> harvest(World world, int x, int y, int z, Block block, int meta, int fortune) {
		Crop crop = getCropFor(block, meta);
		if(crop!=null){
			return harvestSpecialCrop(world, x, y, z, block, meta, crop);
		}
		return harvestNormalCrop(world, x, y, z, block, meta, fortune);
	}

	private static Crop getCropFor(Block block, int meta){
		for(Crop crop:crops){
			if(crop.isCrop(PC_Utils.getBlockSID(block), meta)){
				return crop;
			}
		}
		return null;
	}
	
	private static List<ItemStackSpawn> harvestNormalCrop(World world, int x, int y, int z, Block block, int meta, int fortune){
		if(!(block instanceof BlockCrops))
			return null;
		if(meta<7){
			return null;
		}
		List<ItemStack> drops = PC_Build.harvestEasy(world, x, y, z, fortune);
		List<ItemStackSpawn> dropsWithPlace = new ArrayList<ItemStackSpawn>();
		for(ItemStack drop:drops){
			if(drop.stackSize>0){
				dropsWithPlace.add(new ItemStackSpawn(x, y, z, drop));
			}
		}
		if(!world.isRemote){
			for(int s=0; s<6; s++){
				PC_Direction dir = sideList[s];
				Iterator<ItemStackSpawn> i = dropsWithPlace.iterator();
				while(i.hasNext()){
					ItemStackSpawn drop = i.next();
					if(PC_Build.tryUseItem(world, x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ, dir, drop.itemStack)){
						if(drop.itemStack.stackSize<=0){
							i.remove();
						}
						return dropsWithPlace;
					}
				}
			}
		}
		return dropsWithPlace;
	}
	
	private static List<ItemStackSpawn> harvestSpecialCrop(World world, int x, int y, int z, Block block, int meta, Crop crop){
		String blockSID = PC_Utils.getBlockSID(block);
		if(crop.isFinished(blockSID, meta)){
			CropReplant replant = crop.getReplant(blockSID, meta);
			if(!world.isRemote){
				Block b = PC_Utils.getBlock(replant.replant.blockSID);
				if(b==null){
					PC_Logger.warning("Can't find block %s", replant.replant.blockSID);
					PC_Utils.setAir(world, x, y, z);
				}else{
					PC_Utils.setBlock(world, x, y, z, b, replant.replant.metadata);
				}
			}
			List<ItemStackSpawn> drops = new ArrayList<ItemStackSpawn>();
			for(ItemStack drop:replant.getDrops(meta)){
				drops.add(new ItemStackSpawn(x, y, z, drop));
			}
			return drops;
		}
		return null;
	}
	
	/**
	 * Call this method to explicitly init static fields -> list of crops from
	 * XML files
	 */
	private static void loadCrops() {
		if (crops!=null) {
			return;
		}

		crops = new ArrayList<Crop>();
		
		PC_Logger.finer("Loading XML configuration for crops.");

		if (!(new File(folder + "/default.xml")).exists()) {

			try {
				PC_Logger.finest("Generating default crops config in " + folder + "/default.xml");

				FileWriter out = new FileWriter(new File(folder + "/default.xml"));

				//@formatter:off
				// write the default crops
				out.write("<?xml version='1.1' encoding='UTF-8' ?>\n" + "<!-- \n" + "  BLOCK HARVESTER CONFIG FILE\n"
						+ "  You can add your own crops into this file.\n" + "  Any other xml files in this folder will be parsed too.\n\n"
						+ "  If you make a setup file for some mod, please post it on forums.\n\n" + "  Special values:\n"
						+ "    metaMature  = -1  ...  any metadata\n" + "    metaReplant = -1  ...  do not replant\n\n"
						+ "    Item meta   <  0  ...  get item with meta = blockMeta & abs(THIS_NUMBER) - useful for leaves\n\n"
						+ "  Item meta can be ranged - use 4-7 for random meta in range 4 to 7 (inclusive).\n"
						+ "  You can also use range for item count (eg. 0-5). \n\n"
						+ "  Higher rarity number means more rare. Use 1 for regular drops. \n"
						+ "-->\n\n"
						+ "<crops>\n"
						+ "\n"
						+ "</crops>");
				//@formatter:on

				out.close();

			} catch (IOException e) {
				PC_Logger.severe("Generating default crops config file failed due to an IOException.");
				e.printStackTrace();
			}

		}

		String[] files = folder.list(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				return arg1.matches("[^.]+[.]xml");
			}
		});

		for (String filename : files) {

			PC_Logger.finest("* loading file " + filename + "...");
			File file = new File(folder + "/" + filename);
			parseFile(file);

		}

		PC_Logger.finer("Crops configuration loaded.");

	}

	/*
	 * <?xml version="1.0" encoding="UTF-8" ?>
	 * <crops>
	 * <crop name="My Crop">
	 * <block id="79" metaReplant="0" metaMature="7">
	 * <item id="318" meta="0" count="1-2" rarity="1">
	 * <item id="319" meta="1-5" count="1-2" rarity="4">
	 * </crop>
	 * </crops>
	 */

	/**
	 * Load and parse XML file with crops specs
	 * 
	 * @param file the file to load
	 */
	private static void parseFile(File file) {

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList cropsList = doc.getElementsByTagName("crop");

			croploop:
			for (int i = 0; i < cropsList.getLength(); i++) {

				Node cropNode = cropsList.item(i);
				if (cropNode.getNodeType() == Node.ELEMENT_NODE) {

					// process one crop entry

					Element crop = (Element) cropNode;

					// <block>
					NodeList blocks = crop.getElementsByTagName("block");

					// <item>
					NodeList items = crop.getElementsByTagName("item");
					if (blocks.getLength() < 1) {
						PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - no <item>s in <crop>");
						continue croploop;
					}

					int itemCount = items.getLength();
					
					Crop c = new Crop();
					
					for(int j=0; j<blocks.getLength(); j++){
						
						Element block = (Element) blocks.item(j);
	
						// <block attrs>
						String block_id_s = block.getAttribute("id");
	
						if (block_id_s.equals("")) {
							PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad block ID");
							continue croploop;
						}
	
						String block_meta_replant_s = block.getAttribute("metaReplant");
	
						if (block_meta_replant_s.equals("") || !block_meta_replant_s.matches("[-]?[0-9]+")) {
							PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad replant meta");
							continue croploop;
						}
	
						int meta_replant = Integer.parseInt(block_meta_replant_s);
	
						String block_meta_mature_s = block.getAttribute("metaMature");
	
						if (block_meta_mature_s.equals("") || !block_meta_mature_s.matches("[-]?[0-9]+")) {
							PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad mature meta");
							continue croploop;
						}
	
						int meta = Integer.parseInt(block_meta_mature_s);
						
						c.replant.put(new CropState(block_id_s, meta), new CropReplant(block_id_s, meta_replant));
						
					}

					itemloop:
					for (int j = 0; j < itemCount; j++) {

						try {
							int itemMetaA, itemMetaB, itemCountA, itemCountB, itemRarityA, itemRarityB, itemPriority;

							Element item = (Element) items.item(j);

							// id
							String item_id_s = item.getAttribute("id");

							if (item_id_s.equals("")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item ID");
								continue croploop;
							}

							// priority
							String item_priority_s = item.getAttribute("priority");

							if (item_id_s.equals("")) {

								item_priority_s = "1";

							} 

							itemPriority = Integer.parseInt(item_priority_s);

							// rarity 1/200
							String item_rarity_s = item.getAttribute("rarity");

							if (item_rarity_s.equals("")) {

								item_rarity_s = "1";

							}
							if (!item_rarity_s.matches("[0-9]+([/][0-9]+)?")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item rarity");
								continue croploop;
							}

							String[] item_rarity_parts = item_rarity_s.split("/");

							if (item_rarity_parts.length == 1) {
								itemRarityA = 1;
								itemRarityB = Integer.parseInt(item_rarity_parts[0]);
							} else {
								itemRarityA = Integer.parseInt(item_rarity_parts[0]);
								itemRarityB = Integer.parseInt(item_rarity_parts[1]);

								if (itemRarityA > itemRarityB) {
									itemRarityA = itemRarityB = 1;
								}
							}

							// meta start-stop
							String item_meta_s = item.getAttribute("meta");

							if (item_meta_s.equals("")) {
								item_meta_s = "0";
							} else if (!item_meta_s.matches("[-]?[0-9]+") && !item_meta_s.matches("[0-9]+[-][0-9]+")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item meta");
								continue croploop;
							}

							String[] item_meta_parts;

							if (item_meta_s.matches("[-]?[0-9]+")) {
								item_meta_parts = new String[1];
								item_meta_parts[0] = item_meta_s;

							} else {
								item_meta_parts = item_meta_s.split("-");
							}

							if (item_meta_parts.length == 1) {
								itemMetaA = itemMetaB = Integer.parseInt(item_meta_parts[0]);
							} else {
								itemMetaA = Integer.parseInt(item_meta_parts[0]);
								itemMetaB = Integer.parseInt(item_meta_parts[1]);

								if (itemMetaB < itemMetaA) {
									itemMetaB = itemMetaA;
								}
							}

							// cout start-stop
							String item_count_s = item.getAttribute("count");

							if (item_count_s.equals("")) {

								item_count_s = "1";

							} else if (!item_count_s.matches("[0-9]+(-[0-9]+)?")) {
								PC_Logger.warning("Crop manager - parseFile - Error while parsing " + file + " - bad item count");
								continue croploop;
							}

							String[] item_count_parts = item_count_s.split("-");

							if (item_count_parts.length == 1) {
								itemCountA = itemCountB = Integer.parseInt(item_count_parts[0]);
							} else {
								itemCountA = Integer.parseInt(item_count_parts[0]);
								itemCountB = Integer.parseInt(item_count_parts[1]);

								if (itemCountB < itemCountA) {
									itemCountB = itemCountA;
								}
							}

							for(CropReplant replant:c.replant.values()){
								replant.drops.add(new CropDrops(item_id_s, itemMetaA, itemMetaB, itemCountA, itemCountB, itemRarityA, itemRarityB, itemPriority));
							}

						} catch (NumberFormatException e) {
							continue itemloop;
						}

					}

					crops.add(c);
					
					
					HashMap<String, Integer> mainMeta = new HashMap<String, Integer>();
					for(CropReplant replant:c.replant.values()){
						Integer meta;
						if((meta = mainMeta.get(replant.replant.blockSID))==null){
							mainMeta.put(replant.replant.blockSID, Integer.valueOf(replant.replant.metadata));
						}else{
							int m = meta.intValue();
							if((m<replant.replant.metadata || replant.replant.metadata==-1) && m!=-1){
								mainMeta.put(replant.replant.blockSID, Integer.valueOf(replant.replant.metadata));
							}
						}
					}
					
					for(Entry<String, Integer> growing:mainMeta.entrySet()){
						int meta = growing.getValue().intValue();
						if(meta>0){
							for(int j=0; j<meta; j++){
								c.replant.put(new CropState(growing.getKey(), j), null);
							}
						}
					}
					
					PC_Logger.finest("   - Loaded crop \"" + crop.getAttribute("name") + "\".");

				}

			}

		} catch (SAXParseException err) {
			PC_Logger.severe("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
			PC_Logger.severe(" " + err.getMessage());
		} catch (SAXException e) {
			Exception x = e.getException();
			((x == null) ? e : x).printStackTrace();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private static class CropState{
		
		public String blockSID;
		public int metadata;
		
		public CropState(String blockSID, int metadata){
			this.blockSID = blockSID;
			this.metadata = metadata;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.blockSID == null) ? 0 : this.blockSID.hashCode());
			result = prime * result + this.metadata;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CropState other = (CropState) obj;
			if (this.blockSID == null) {
				if (other.blockSID != null) return false;
			} else if (!this.blockSID.equals(other.blockSID)) return false;
			if (this.metadata != other.metadata && this.metadata!=-1) return false;
			return true;
		}
		
	}
	
	private static class CropDrops{
		
		public String itemId;
		public int itemMetaA;
		public int itemMetaB;
		public int itemCountA;
		public int itemCountB;
		public int itemRarityA;
		public int itemRarityB;
		public int itemPriority;
		
		public CropDrops(String itemId, int itemMetaA, int itemMetaB,
				int itemCountA, int itemCountB, int itemRarityA,
				int itemRarityB, int itemPriority) {
			this.itemId = itemId;
			this.itemMetaA = itemMetaA;
			this.itemMetaB = itemMetaB;
			this.itemCountA = itemCountA;
			this.itemCountB = itemCountB;
			this.itemRarityA = itemRarityA;
			this.itemRarityB = itemRarityB;
			this.itemPriority = itemPriority;
		}
		
	}
	
	private static class CropReplant{
		
		public CropState replant;
		public List<CropDrops> drops = new ArrayList<CropDrops>();
		
		public CropReplant(String blockSID, int metadata) {
			this.replant = new CropState(blockSID, metadata);
		}
		
		public List<ItemStack> getDrops(int meta){
			ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();

			for (int priorityTurn = 1; priorityTurn < 20; priorityTurn++) {

				int itemsOfPriority = 0;
				int itemsDropped = 0;

				for (CropDrops drop:this.drops) {
					if (drop.itemPriority == priorityTurn) {

						itemsOfPriority++;

						if (drop.itemRarityB > 0 && rand.nextInt(drop.itemRarityB) < drop.itemRarityA) {

							int stackMeta;

							if (drop.itemMetaA < 0) {
								stackMeta = meta & (-drop.itemMetaA);
							} else {
								stackMeta = drop.itemMetaA + rand.nextInt(drop.itemMetaB - drop.itemMetaA + 1);
							}

							int stackCount = drop.itemCountA + rand.nextInt(drop.itemCountB - drop.itemCountA + 1);

							Item item = PC_Utils.getItem(drop.itemId);
							if(item==null){
								PC_Logger.warning("Can't find Item %s", drop.itemId);
							}
							
							if (stackMeta >= 0 && stackMeta < 32000 && stackCount > 0 && item != null) {

								stacks.add(new ItemStack(item, stackCount, stackMeta));
								itemsDropped++;

							}
						}

					}

				}

				if (itemsOfPriority == 0) {
					break;
				}

				if (itemsOfPriority > 0 && itemsDropped > 0) {
					break;
				}

			}

			if (stacks.size() == 0) {
				return null;
			}

			return stacks;
		}
		
	}
	
	private static class Crop{
		
		public HashMap<CropState, CropReplant> replant = new HashMap<CropState, CropReplant>();
		
		public Crop() {
			
		}

		public boolean isCrop(String blockSID, int metadata){
			if(isGrowing(blockSID, metadata)){
				return true;
			}
			return isFinished(blockSID, metadata);
		}
		
		public boolean isGrowing(String blockSID, int metadata){
			CropState cs = new CropState(blockSID, metadata);
			return this.replant.containsKey(cs) && this.replant.get(cs)==null;
		}
		
		public boolean isFinished(String blockSID, int metadata){
			CropState cs = new CropState(blockSID, metadata);
			return this.replant.containsKey(cs) && this.replant.get(cs)!=null;
		}
		
		public CropReplant getReplant(String blockSID, int metadata){
			return this.replant.get(new CropState(blockSID, metadata));
		}
		
	}
	
}
