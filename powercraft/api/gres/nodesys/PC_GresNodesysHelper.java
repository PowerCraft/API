package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.List;

import node.NodeBranch;
import node.NodeCountLoop;
import node.NodeItemCompareOutCount;
import node.NodeItemStackSeperate;
import node.NodeMaths;
import powercraft.api.PC_ImmutableArrayList;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresAlign.Fill;
import powercraft.api.gres.PC_GresComboBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresGroupContainer;
import powercraft.api.gres.PC_GresListBoxElement;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.gres.layout.PC_GresLayoutVertical;
import type.ItemStackData;
import type.SelectionData;
import core.Node;
import core.PinBaseImp;
import core.PinProgramIn;
import core.PinProgramOut;
import core.PinValueIn;
import core.PinValueOut;
import core.ValueHandler;
import core.ValueType;
import core.ValueType.COLOR;


public final class PC_GresNodesysHelper {
	
	public final static List<PC_GresListBoxElement> allNodes;
	
	private PC_GresNodesysHelper(){
		PC_Utils.staticClassConstructor();
	}
	
	static{
		List<PC_GresListBoxElement> base = new ArrayList<PC_GresListBoxElement>();
		List<PC_GresListBoxElement> prog = new ArrayList<PC_GresListBoxElement>();
		prog.add(new PC_GresListBoxElement(1, "Entry"));
		prog.add(new PC_GresListBoxElement(2, "Branch"));
		prog.add(new PC_GresListBoxElement(3, "Loop"));
		base.add(new PC_GresListBoxElement("Prog",new PC_ImmutableList<PC_GresListBoxElement>(prog)));
		List<PC_GresListBoxElement> maths = new ArrayList<PC_GresListBoxElement>();
		maths.add(new PC_GresListBoxElement(4, "Math"));
		maths.add(new PC_GresListBoxElement(5, "ItemStackSeperator"));
		maths.add(new PC_GresListBoxElement(6, "ItemAmountSelector"));
		base.add(new PC_GresListBoxElement("Convert",new PC_ImmutableList<PC_GresListBoxElement>(maths)));
		allNodes = new PC_ImmutableList<PC_GresListBoxElement>(base);
	}
	
	public static void addNodeToGrid(PC_GresNodesysGrid grid, PC_Vec2I pos, int id){
		grid.add(nodeToGuiNode(makeNodeByID(id)));
	}
	
	public static Node makeNodeByID(int id){
		switch(id){
		case 2:
			return new NodeBranch();
		case 3:
			return new NodeCountLoop();
		case 4:
			return new NodeMaths();
		case 5:
			return new NodeItemStackSeperate();
		case 6:
			return new NodeItemCompareOutCount();
		default:
			return null;
		}
	}
	
	public static PC_GresNodesysNode nodeToGuiNode(Node node){
		PC_GresNodesysNode guiNode = new PC_GresNodesysNode(node.getDefaultName());
		PinProgramIn progIn = node.getProgIn();
		PC_GresNodesysEntry entry = makeEntry(progIn);
		if(node.getAmountOfProgOut()>0){
			int pinColor = getColorInt(node.getProgOut(0).getColor());
			entry.add(new PC_GresNodesysConnection(true, false, pinColor, 0));
		}
		guiNode.add(entry);
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
			entry = new PC_GresNodesysEntry(((PinBaseImp)obj).getName());
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
			PC_GresGroupContainer c = new PC_GresGroupContainer();
			c.setLayout(new PC_GresLayoutVertical());
			c.setFill(Fill.HORIZONTAL);
			c.add(new PC_GresTextEdit("name", 10).setFill(Fill.HORIZONTAL));
			c.add(new PC_GresTextEdit("0", 10, PC_GresInputType.UNSIGNED_INT).setFill(Fill.HORIZONTAL));
			c.add(new PC_GresTextEdit("0", 10, PC_GresInputType.UNSIGNED_INT).setFill(Fill.HORIZONTAL));
			return c;
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
	
}
