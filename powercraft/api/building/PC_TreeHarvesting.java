package powercraft.api.building;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
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
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.building.PC_Build.ItemStackSpawn;
import powercraft.api.reflect.PC_Security;


public class PC_TreeHarvesting implements PC_ISpecialHarvesting {
	
	private static final PC_TreeHarvesting INSTANCE = new PC_TreeHarvesting();
	private static final File folder = PC_Utils.getPowerCraftFile("trees", null);
	private static int MAXRECURSION = 1000;
	private static List<Tree> trees;
	
	private PC_TreeHarvesting(){
		if(INSTANCE!=null){
			PC_Utils.staticClassConstructor();
		}
	}
	
	public static void register(){
		PC_Security.allowedCaller("PC_TreeHarvesting.register()", PC_Api.class);
		loadTrees();
		PC_Build.addSpecialHarvesting(INSTANCE);
	}
	
	@Override
	public boolean useFor(World world, int x, int y, int z, Block block, int meta, int priority) {
		if(priority<2){
			return false;
		}
		return getTreeFor(block, meta)!=null;
	}
	
	@Override
	public List<ItemStackSpawn> harvest(World world, int x, int y, int z, Block block, int meta, int fortune) {
		Tree tree = getTreeFor(block, meta);
		List<ItemStackSpawn> drops = new ArrayList<ItemStackSpawn>();
		if(!world.isRemote)
			harvestWood(world, x, y, z, block, meta, fortune, tree, drops, 0);
		return drops;
	}
	
	public void harvestWood(World world, int x, int y, int z, Block block, int meta, int fortune, Tree tree, List<ItemStackSpawn> drops, int recursion){
		List<ItemStack> blockDrops = PC_Build.harvestEasy(world, x, y, z, fortune);
		if(blockDrops!=null){
			for(ItemStack blockDrop:blockDrops){
				drops.add(new ItemStackSpawn(x, y, z, blockDrop));
			}
		}
		Block b;
		int m;
		if(recursion<MAXRECURSION){
			for (int nx = x - 1; nx <= x + 1; nx++) {
				for (int ny = y - 1; ny <= y + 1; ny++) {
					for (int nz = z - 1; nz <= z + 1; nz++) {
						b = PC_Utils.getBlock(world, nx, ny, nz);
						m = PC_Utils.getMetadata(world, nx, ny, nz);
						if(b!=null){
							if(tree.woods.contains(new TreeState(PC_Utils.getBlockSID(b), m))){
								harvestWood(world, nx, ny, nz, b, m, fortune, tree, drops, recursion+1);
							}
						}
					}
				}
			}
			for (int nx = x - 1; nx <= x + 1; nx++) {
				for (int ny = y - 1; ny <= y + 1; ny++) {
					for (int nz = z - 1; nz <= z + 1; nz++) {
						b = PC_Utils.getBlock(world, nx, ny, nz);
						m = PC_Utils.getMetadata(world, nx, ny, nz);
						if(b!=null){
							if(tree.leaves.contains(new TreeState(PC_Utils.getBlockSID(b), m))){
								harvestLeaves(world, nx, ny, nz, b, m, fortune, tree, drops, recursion+1);
							}
						}
					}
				}
			}
			b = PC_Utils.getBlock(world, x, y-1, z);
			if(b!=null){
				Iterator<ItemStackSpawn> i = drops.iterator();
				while(i.hasNext()){
					ItemStackSpawn drop = i.next();
					for(TreeState sampling:tree.saplings){
						if(PC_Utils.getItemForBlock(PC_Utils.getBlock(sampling.blockSID)) == drop.itemStack.getItem() && (sampling.metadata == drop.itemStack.getItemDamage() || sampling.metadata==-1)){
							if(PC_Build.tryUseItem(world, x, y-1, z, PC_Direction.UP, drop.itemStack)){
								if(drop.itemStack.stackSize<=0){
									i.remove();
								}
								return;
							}
						}
					}
				}
			}
		}
	}
	
	private static boolean isLeaveOrLog(World world, int x, int y, int z, Tree tree){
		Block b = PC_Utils.getBlock(world, x, y, z);
		int m = PC_Utils.getMetadata(world, x, y, z);
		TreeState ts = new TreeState(PC_Utils.getBlockSID(b), m);
		return tree.woods.contains(ts)||tree.leaves.contains(ts);
	}
	
	private static boolean isConnectedToLog(World world, int x, int y, int z, Tree tree){
		int range = 4;
		for(int i=-range; i<=range; i++){
			for(int j=-range; j<=range; j++){
				for(int k=-range; k<=range; k++){
					if(PC_MathHelper.abs(i)+PC_MathHelper.abs(j)+PC_MathHelper.abs(k)<=range){
						Block b = PC_Utils.getBlock(world, x+i, y+j, z+k);
						int m = PC_Utils.getMetadata(world, x+i, y+j, z+k);
						if(tree.woods.contains(new TreeState(PC_Utils.getBlockSID(b), m))){
							boolean ok = true;
							for(int l=1; l<range; l++){
								if(!isLeaveOrLog(world, (int)(x+i*l/4.0f+0.5f), (int)(y+j*l/4.0f+0.5f), (int)(z+k*l/4.0f+0.5f), tree)){
									ok = false;
									break;
								}
							}
							if(ok)
								return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	public void harvestLeaves(World world, int x, int y, int z, Block block, int meta, int fortune, Tree tree, List<ItemStackSpawn> drops, int recursion){
		if(isConnectedToLog(world, x, y, z, tree))
			return;
		List<ItemStack> blockDrops = PC_Build.harvestEasy(world, x, y, z, fortune);
		if(blockDrops!=null){
			for(ItemStack blockDrop:blockDrops){
				drops.add(new ItemStackSpawn(x, y, z, blockDrop));
			}
		}
		Block b;
		int m;
		if(recursion<MAXRECURSION){
			for (int nx = x - 1; nx <= x + 1; nx++) {
				for (int ny = y - 1; ny <= y + 1; ny++) {
					for (int nz = z - 1; nz <= z + 1; nz++) {
						b = PC_Utils.getBlock(world, nx, ny, nz);
						m = PC_Utils.getMetadata(world, nx, ny, nz);
						if(b!=null){
							if(tree.leaves.contains(new TreeState(PC_Utils.getBlockSID(b), m))){
								harvestLeaves(world, nx, ny, nz, b, m, fortune, tree, drops, recursion+1);
							}
						}
					}
				}
			}
		}
	}

	private static Tree getTreeFor(Block block, int meta){
		String blockSID = PC_Utils.getBlockSID(block);
		for(Tree tree:trees){
			if(tree.woods.contains(new TreeState(blockSID, meta))){
				return tree;
			}
		}
		return null;
	}
	
	
	/**
	 * Load trees data from file.
	 */
	public static void loadTrees() {
		if (trees!=null) {
			return;
		}

		trees = new ArrayList<Tree>();
		
		for(int i=0; i<4; i++){
			Tree tree = new Tree();
			tree.woods.add(new TreeState(Blocks.log, i, 3));
			tree.leaves.add(new TreeState(Blocks.leaves, i, 3));
			tree.saplings.add(new TreeState(Blocks.sapling, i));
			trees.add(tree);
		}
		for(int i=0; i<2; i++){
			Tree tree = new Tree();
			tree.woods.add(new TreeState(Blocks.log2, i, 3));
			tree.leaves.add(new TreeState(Blocks.leaves2, i, 3));
			tree.saplings.add(new TreeState(Blocks.sapling, i+4));
			trees.add(tree);
		}
		
		Tree tree = new Tree();
		tree.woods.add(new TreeState(Blocks.brown_mushroom_block, -1));
		tree.saplings.add(new TreeState(Blocks.brown_mushroom, 0));
		trees.add(tree);
		
		tree = new Tree();
		tree.woods.add(new TreeState(Blocks.red_mushroom_block, -1));
		tree.saplings.add(new TreeState(Blocks.red_mushroom, 0));
		trees.add(tree);
		
		PC_Logger.finer("Loading XML configuration for trees.");

		if (!(new File(folder + "/" + "default.xml")).exists()) {

			try {
				PC_Logger.finest("Generating default trees configuration file in " + folder + "/trees.xml");

				FileWriter out;

				out = new FileWriter(new File(folder + "/" + "default.xml"));

				//@formatter:off
				// write the default crops
				try {
					out.write("<?xml version='1.1' encoding='UTF-8' ?>\n" + "<!-- \n"
							+ " This file defines trees harvestable automatically (eg. by harvester machine)\n"
							+ " The purpose of this system is to make PowerCraft compatible with new trees from mods.\n"
							+ " All files in 'trees' directory will be parsed, so please make your own.\n"
							+ "-->\n\n"
							+ "<trees>\n"
							+ "\n"
							+ "</trees>");
				} catch (IOException e) {
					e.printStackTrace();
				}
				//@formatter:on

				out.close();
			} catch (IOException e) {
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

		PC_Logger.finer("Trees configuration loaded.");

	}

	/**
	 * Load and parse XML file with tree specs
	 * 
	 * @param file the file to load
	 */
	private static void parseFile(File file) {

		try {

			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			doc.getDocumentElement().normalize();

			NodeList treesList = doc.getElementsByTagName("tree");

			treeloop:
			for (int i = 0; i < treesList.getLength(); i++) {

				Node treeNode = treesList.item(i);
				if (treeNode.getNodeType() == Node.ELEMENT_NODE) {

					// process one crop entry

					Element tree = (Element) treeNode;


					// <wood>
					NodeList woodlist = tree.getElementsByTagName("wood");
					if (woodlist.getLength() != 1) {
						PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - invalid no. of <wood> tags in <tree>");
						continue treeloop;
					}

					Element wood = (Element) woodlist.item(0);


					// <leaves>
					NodeList leaveslist = tree.getElementsByTagName("leaves");
					Element leaves = null;
					if (leaveslist.getLength() == 1) {
						leaves = (Element) leaveslist.item(0);
					}


					// <sapling>
					NodeList saplinglist = tree.getElementsByTagName("sapling");
					Element sapling = null;
					if (saplinglist.getLength() == 1) {
						sapling = (Element) saplinglist.item(0);
					}


					// parse wood.

					TreeState woodStruct;
					TreeState leavesStruct = null;
					TreeState saplingStruct = null;

					String woodId_s = wood.getAttribute("id");

					if (woodId_s.equals("")) {
						PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad wood ID");
						continue treeloop;
					}

					String woodMeta_s = wood.getAttribute("meta");

					if (woodMeta_s.equals("") || !woodMeta_s.matches("-?[0-9]+")) {
						PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad wood meta");
						continue treeloop;
					}

					int wood_meta = Integer.parseInt(woodMeta_s);

					woodStruct = new TreeState(woodId_s, wood_meta);


					if (leaves != null) {

						String leavesId_s = leaves.getAttribute("id");

						if (leavesId_s.equals("")) {
							PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad leaves ID");
							continue treeloop;
						}

						String leavesMeta_s = leaves.getAttribute("meta");

						if (leavesMeta_s.equals("") || !leavesMeta_s.matches("-?[0-9]+")) {
							PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad leaves meta");
							continue treeloop;
						}

						int leaves_meta = Integer.parseInt(leavesMeta_s);

						leavesStruct = new TreeState(leavesId_s, leaves_meta);

					}

					if (sapling != null) {

						String saplingId_s = sapling.getAttribute("id");

						if (saplingId_s.equals("")) {
							PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad sapling ID");
							continue treeloop;
						}

						String saplingMeta_s = sapling.getAttribute("meta");

						if (saplingMeta_s.equals("") || !saplingMeta_s.matches("[0-9]+")) {
							PC_Logger.warning("Tree manager - parseFile - Error while parsing " + file + " - bad sapling meta");
							continue treeloop;
						}

						int sapling_meta = Integer.parseInt(saplingMeta_s);

						saplingStruct = new TreeState(saplingId_s, sapling_meta);

					}

					Tree ttree = new Tree();
					
					ttree.woods.add(woodStruct);
					if(leavesStruct!=null){
						ttree.leaves.add(leavesStruct);
					}
					if(saplingStruct!=null){
						ttree.saplings.add(saplingStruct);
					}
					trees.add(ttree);

					PC_Logger.finest("   - Tree \"" + tree.getAttribute("name") + "\" loaded. -> " + ttree);

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
	
	
	private static class TreeState{
		
		public String blockSID;
		public int metadata;
		public int bitMask;
		
		public TreeState(Block block, int metadata){
			this.blockSID = PC_Utils.getBlockSID(block);
			this.metadata = metadata;
			this.bitMask = -1;
		}
		
		public TreeState(Block block, int metadata, int bitMask){
			this.blockSID = PC_Utils.getBlockSID(block);
			this.metadata = metadata;
			this.bitMask = bitMask;
		}
		
		public TreeState(String blockSID, int metadata){
			this.blockSID = blockSID;
			this.metadata = metadata;
			this.bitMask = -1;
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
			TreeState other = (TreeState) obj;
			if (this.blockSID == null) {
				if (other.blockSID != null) return false;
			} else if (!this.blockSID.equals(other.blockSID)) return false;
			if ((this.metadata & other.bitMask) != (other.metadata & this.bitMask) && this.metadata!=-1 && other.metadata!=-1) return false;
			return true;
		}
		
	}
	
	private static class Tree{
		
		public List<TreeState> woods = new ArrayList<TreeState>();
		public List<TreeState> leaves = new ArrayList<TreeState>();
		public List<TreeState> saplings = new ArrayList<TreeState>();
		
		public Tree() {
			
		}
		
	}
	
}
