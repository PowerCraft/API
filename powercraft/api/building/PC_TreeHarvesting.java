package powercraft.api.building;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import powercraft.api.PC_Api;
import powercraft.api.PC_Logger;
import powercraft.api.PC_MathHelper;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec3I;
import powercraft.api.reflect.PC_Security;


public class PC_TreeHarvesting implements PC_ISpecialHarvesting {
	
	private static final PC_TreeHarvesting INSTANCE = new PC_TreeHarvesting();
	private static final File folder = PC_Utils.getPowerCraftFile("trees", null);
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
	public boolean useFor(World world, int x, int y, int z, int priority) {
		if(priority<2){
			return false;
		}
		Block block = PC_Utils.getBlock(world, x, y, z);
		int meta = PC_Utils.getMetadata(world, x, y, z);
		return getTreeFor(block, meta)!=null;
	}
	
	@Override
	public PC_Harvest harvest(World world, int x, int y, int z, int usesLeft) {
		PC_Harvest harvest = new PC_Harvest();
		Block block = PC_Utils.getBlock(world, x, y, z);
		int meta = PC_Utils.getMetadata(world, x, y, z);
		Tree tree = getTreeFor(block, meta);
		List<PC_Vec3I> list = harvest.positions = new ArrayList<PC_Vec3I>();
		List<PC_Vec3I> markedVorWoodTest = new ArrayList<PC_Vec3I>();
		List<PC_Vec3I> markedVorLeaveTest = new ArrayList<PC_Vec3I>();
		markedVorWoodTest.add(new PC_Vec3I(x, y, z));
		while(!markedVorWoodTest.isEmpty()){
			PC_Vec3I pos = markedVorWoodTest.remove(markedVorWoodTest.size()-1);
			if(testWoodBlock(world, pos, tree)){
				list.add(pos);
				for(int i=-1; i<=1; i++){
					for(int j=-1; j<=1; j++){
						for(int k=-1; k<=1; k++){
							if(i!=0 || j!=0 || k!=0){
								PC_Vec3I p = pos.offset(i, j, k);
								if(!(list.contains(p) || markedVorWoodTest.contains(p) || markedVorLeaveTest.contains(p))){
									markedVorWoodTest.add(p);
								}
							}
						}
					}
				}
			}else{
				markedVorLeaveTest.add(pos);
			}
		}
		markedVorWoodTest.addAll(list);
		harvest.itemUse = list.size();
		if(harvest.itemUse>usesLeft && usesLeft!=-1){
			int max = 0;
			HashMap<Integer, List<PC_Vec3I>> hm = new HashMap<Integer, List<PC_Vec3I>>();
			for(PC_Vec3I pos:list){
				if(pos.y>max)
					max = pos.y;
				List<PC_Vec3I> l = hm.get(Integer.valueOf(pos.y));
				if(l==null){
					hm.put(Integer.valueOf(pos.y), l = new ArrayList<PC_Vec3I>());
				}
				l.add(pos);
			}
			list.clear();
			for(int i=max; i>=0; i--){
				List<PC_Vec3I> l = hm.get(Integer.valueOf(i));
				if(l!=null){
					if(l.size()+list.size()>usesLeft){
						int diff = usesLeft-list.size();
						while(diff>0){
							diff--;
							list.add(l.remove((int)(Math.random()*l.size())));
						}
					}else{
						list.addAll(l);
					}
				}
				if(list.size()>=usesLeft){
					break;
				}
			}
			harvest.itemUse = list.size();
		}
		harvest.digTimeMultiply = 1+((list.size()-1)/2.0f);
		while(!markedVorLeaveTest.isEmpty()){
			PC_Vec3I pos = markedVorLeaveTest.remove(markedVorLeaveTest.size()-1);
			if(testLeaveBlock(world, pos, tree, markedVorWoodTest)){
				list.add(pos);
				for(int i=-1; i<=1; i++){
					for(int j=-1; j<=1; j++){
						for(int k=-1; k<=1; k++){
							if(i!=0 || j!=0 || k!=0){
								PC_Vec3I p = pos.offset(i, j, k);
								if(!(list.contains(p) || markedVorLeaveTest.contains(p))){
									markedVorLeaveTest.add(p);
								}
							}
						}
					}
				}
			}
		}
		return harvest;
	}
	
	public static boolean testWoodBlock(World world, PC_Vec3I pos, Tree tree){
		Block block = PC_Utils.getBlock(world, pos);
		int meta = PC_Utils.getMetadata(world, pos);
		return block!=null && tree.woods.contains(new TreeState(PC_Utils.getBlockSID(block), meta));
	}
	
	private static boolean isConnectedToLog(World world, PC_Vec3I pos, Tree tree, List<PC_Vec3I> marked){
		int range = 4;
		for(int i=-range; i<=range; i++){
			for(int j=-range; j<=range; j++){
				for(int k=-range; k<=range; k++){
					if(PC_MathHelper.abs(i)+PC_MathHelper.abs(j)+PC_MathHelper.abs(k)<=range){
						PC_Vec3I p = pos.offset(i, j, k);
						if(!marked.contains(p) && testWoodBlock(world, pos.offset(i, j, k), tree)){
							boolean ok = true;
							for(int l=1; l<range; l++){
								if(!isLeave(world, (int)(pos.x+i*l/4.0f+0.5f), (int)(pos.y+j*l/4.0f+0.5f), (int)(pos.z+k*l/4.0f+0.5f), tree)){
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
	
	public static boolean testLeaveBlock(World world, PC_Vec3I pos, Tree tree, List<PC_Vec3I> marked){
		Block block = PC_Utils.getBlock(world, pos);
		int meta = PC_Utils.getMetadata(world, pos);
		return block!=null && tree.leaves.contains(new TreeState(PC_Utils.getBlockSID(block), meta)) && !isConnectedToLog(world, pos, tree, marked);
	}
	
	private static boolean isLeave(World world, int x, int y, int z, Tree tree){
		Block b = PC_Utils.getBlock(world, x, y, z);
		int m = PC_Utils.getMetadata(world, x, y, z);
		TreeState ts = new TreeState(PC_Utils.getBlockSID(b), m);
		return tree.leaves.contains(ts);
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
			trees.add(tree);
		}
		for(int i=0; i<2; i++){
			Tree tree = new Tree();
			tree.woods.add(new TreeState(Blocks.log2, i, 3));
			tree.leaves.add(new TreeState(Blocks.leaves2, i, 3));
			trees.add(tree);
		}
		
		Tree tree = new Tree();
		tree.woods.add(new TreeState(Blocks.brown_mushroom_block, -1));
		trees.add(tree);
		
		tree = new Tree();
		tree.woods.add(new TreeState(Blocks.red_mushroom_block, -1));
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


					// parse wood.

					TreeState woodStruct;
					TreeState leavesStruct = null;

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

					Tree ttree = new Tree();
					
					ttree.woods.add(woodStruct);
					if(leavesStruct!=null){
						ttree.leaves.add(leavesStruct);
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
		
		public Tree() {
			
		}
		
	}
	
}
