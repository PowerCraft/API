package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import nodecode.core.INodeFactoryDescriptor;
import nodecode.core.Node;
import nodecode.core.NodeFactory;
import nodecode.core.PinBaseImp;
import nodecode.core.PinProgramIn;
import nodecode.core.PinProgramOut;
import nodecode.core.PinValueIn;
import nodecode.core.PinValueOut;
import nodecode.core.ValueHandler;
import nodecode.core.ValueType;
import nodecode.core.ValueType.COLOR;
import nodecode.type.ItemStackData;
import nodecode.type.SelectionData;
import powercraft.api.PC_ImmutableArrayList;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.H;
import powercraft.api.gres.PC_GresComboBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresItemSelect;
import powercraft.api.gres.PC_GresListBoxElement;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.events.PC_GresEvent;
import powercraft.api.gres.events.PC_GresKeyEvent;
import powercraft.api.gres.events.PC_IGresEventListener;
import powercraft.api.gres.nodesys.PC_GresNodesysNode.ButtonPressed;


public final class PC_GresNodesysHelper {
	
	public final static List<PC_GresListBoxElement> allNodes;
	
	private PC_GresNodesysHelper(){
		PC_Utils.staticClassConstructor();
	}
	
	static{
		List<PC_GresListBoxElement> base = new ArrayList<PC_GresListBoxElement>();
		List<PC_GresListBoxElement> prog = new ArrayList<PC_GresListBoxElement>();
		List<PC_GresListBoxElement> maths = new ArrayList<PC_GresListBoxElement>();
		for(INodeFactoryDescriptor descriptor:NodeFactory.getAvailableNodes()){
			switch(descriptor.getSpecialType()){
			case FLOW:
				prog.add(new PC_GresListBoxElement(descriptor.getUniqueTypeID(), descriptor.getDefaultName()));
				break;
			case CONVERT:
			case MERGE:
			case SPLIT:
				maths.add(new PC_GresListBoxElement(descriptor.getUniqueTypeID(), descriptor.getDefaultName()));
				break;
			case STORAGE:
			default:
				break;
			}
		}
		base.add(new PC_GresListBoxElement("Prog",new PC_ImmutableList<PC_GresListBoxElement>(prog)));
		base.add(new PC_GresListBoxElement("Convert",new PC_ImmutableList<PC_GresListBoxElement>(maths)));
		List<PC_GresListBoxElement> layout = new ArrayList<PC_GresListBoxElement>();
		layout.add(new PC_GresListBoxElement(7, "Split"));
		layout.add(new PC_GresListBoxElement(8, "Frame"));
		layout.add(new PC_GresListBoxElement(9, "Group"));
		base.add(new PC_GresListBoxElement("Layout",new PC_ImmutableList<PC_GresListBoxElement>(layout)));
		allNodes = new PC_ImmutableList<PC_GresListBoxElement>(base);
	}
	
	public static void addNodeToGrid(PC_GresNodesysGrid grid, PC_Vec2I pos, int id){
		if(id==7){
			grid.add(new PC_GresNodesysConnectionSplit());
		}else if(id==8){
			grid.add(new PC_GresNodesysNodeFrame());
		}else if(id==9){
			PC_GresNodesysNode node = new PC_GresNodesysNodeGroup("Group");
			PC_GresNodesysEntry entry = new PC_GresNodesysEntry("Group");
			List<String> groups = new ArrayList<String>();
			groups.add("This");
			entry.add(new PC_GresComboBox(groups, 0));
			node.add(entry);
			grid.add(node);
		}else{
			grid.add(nodeToGuiNode(makeNodeByID(id)));
		}
	}
	
	public static Node makeNodeByID(int id){
		return NodeFactory.getNewNodeForTypeID(id);
	}
	
	public static PC_GresNodesysNode nodeToGuiNode(Node node){
		PC_GresNodesysNode guiNode = new PC_GresNodesysNode(node.getDefaultName());
		PC_GresNodesysEntry entry = null;
		if(node.getAmountOfProgIn()>0){
			entry = makeEntry(node.getProgIn(0));
		}
		if(node.getAmountOfProgOut()>0){
			if(entry==null){
				entry = makeEntry(node.getProgOut(0));
			}else{
				int pinColor = getColorInt(node.getProgOut(0).getColor());
				entry.add(new PC_GresNodesysConnection(true, false, pinColor, 0));
			}
		}
		guiNode.add(entry);
		for(int i=1; i<node.getAmountOfProgIn(); i++){
			guiNode.add(makeEntry(node.getProgIn(i)));
		}
		for(int i=1; i<node.getAmountOfProgOut(); i++){
			guiNode.add(makeEntry(node.getProgOut(i)));
		}
		for(int i=0; i<node.getAmountOfValOut(); i++){
			guiNode.add(makeEntry(node.getValOut(i)));
		}
		for(int i=0; i<node.getAmountOfConfigs(); i++){
			guiNode.add(makeEntry(node.getConfig(i)));
		}
		for(int i=0; i<node.getAmountOfValIn(); i++){
			guiNode.add(makeEntry(node.getValIn(i)));
		}
		return guiNode;
	}
	
	public static PC_GresNodesysEntry makeEntry(Object obj){
		PC_GresNodesysEntry entry;
		if(obj instanceof PinBaseImp){
			if(obj instanceof PinProgramIn){
				entry = new PC_GresNodesysEntry("Prog Flow");
				entry.setAlignH(H.CENTER);
			}else{
				entry = new PC_GresNodesysEntry(((PinBaseImp)obj).getName());
			}
			int pinColor = getColorInt(((PinBaseImp)obj).getColor());
			if(obj instanceof PinProgramIn){
				PinProgramIn progIn = (PinProgramIn)obj;
				entry.add(new PC_GresNodesysConnection(false, true, pinColor, 0));
			}else if(obj instanceof PinProgramOut){
				PinProgramOut progOut = (PinProgramOut)obj;
				entry.add(new PC_GresNodesysConnection(true, false, pinColor, 0));
			}else if(obj instanceof PinValueIn){
				PinValueIn<?> valueIn = (PinValueIn<?>)obj;
				entry.add(new PC_GresNodesysConnection(true, true, pinColor, 1));
				entry.add(makeValueIn(valueIn.getData()));
			}else if(obj instanceof PinValueOut){
				PinValueOut<?> valueOut = (PinValueOut<?>)obj;
				entry.add(new PC_GresNodesysConnection(false, false, pinColor, 1));
			}
		}else if(obj instanceof ValueHandler){
			entry = new PC_GresNodesysEntry("unknown");
			entry.add(makeValueIn(((ValueHandler<?>)obj).getData()));
		}else{
			return null;
		}
		return entry;
	}
	
	public static PC_GresComponent makeValueIn(ValueType<?> valueType){
		if(valueType instanceof ItemStackData){
			return new PC_GresItemSelect();
		}else if(valueType instanceof SelectionData){
			PC_GresComboBox comboBox = new PC_GresComboBox(new PC_ImmutableArrayList<String>(((SelectionData)valueType).getOptions()), 0);
			return comboBox;
		}
		Class<?> c = valueType.getType();
		if(c == Number.class || c==Double.class || c==Float.class){
			return new PC_GresTextEdit("0", 10, PC_GresInputType.SIGNED_FLOAT);
		}else if(c==Integer.class || c==Long.class || c==Short.class|| c==Byte.class){
			return new PC_GresTextEdit("0", 10, PC_GresInputType.INT);
		}else if(c == String.class){
			return new PC_GresTextEdit("", 10);
		}
		return null;
	}
	
	public static int getColorInt(COLOR c){
		return 0x80000000|c.rgb;
	}
	
	public static PC_IGresNodesysConnection getConnection(PC_IGresNodesysConnection forConnection, PC_GresComponent c){
		if(c instanceof PC_IGresNodesysConnection && canConnectTo(forConnection, (PC_IGresNodesysConnection)c)){
			return (PC_IGresNodesysConnection)c;
		}
		return null;
	}
	
	public static boolean canConnectTo(PC_IGresNodesysConnection con, PC_IGresNodesysConnection to){
		if(con==to)
			return false;
		int c1 = con.getCompGroup();
		int c2 = to.getCompGroup();
		if(c1!=-1 && c2!=-1 && c1!=c2)
			return false;
		PC_GresNodesysNode node = con.getNode();
		if(node==to.getNode() && node!=null)
			return false;
		int t1 = con.getType(true);
		int t2 = to.getType(false);
		return ((t1&1)!=0 && (t2&2)!=0) || ((t1&2)!=0 && (t2&1)!=0);
	}
	
}
