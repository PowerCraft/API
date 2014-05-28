package powercraft.api.gres.nodesys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import powercraft.api.PC_ImmutableArrayList;
import powercraft.api.PC_ImmutableList;
import powercraft.api.PC_Utils;
import powercraft.api.PC_Vec2I;
import powercraft.api.gres.PC_GresComboBox;
import powercraft.api.gres.PC_GresComponent;
import powercraft.api.gres.PC_GresListBoxElement;
import powercraft.api.gres.PC_GresTextEdit;
import powercraft.api.gres.PC_GresTextEdit.PC_GresInputType;
import powercraft.api.nodesys.PC_INodeValueInput;
import powercraft.api.nodesys.PC_NodeComponent;
import powercraft.api.nodesys.PC_NodeDescriptor;
import powercraft.api.nodesys.PC_NodeGridHelper;
import powercraft.api.nodesys.PC_NodeValueInputDropdown;
import powercraft.api.nodesys.PC_NodeValueInputTextbox;
import powercraft.api.nodesys.node.PC_Node;
import powercraft.api.nodesys.node.PC_NodeGroup;
import powercraft.api.nodesys.node.descriptor.PC_NodeDescriptorGroup;
import powercraft.api.nodesys.type.PC_NodeObjectType;


@SuppressWarnings("unchecked")
public final class PC_GresNodesysHelper {
	
	public final static List<PC_GresListBoxElement> allNodes;
	
	private PC_GresNodesysHelper(){
		PC_Utils.staticClassConstructor();
	}
	
	static{
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(PC_NodeDescriptor descriptor:PC_NodeGridHelper.getNodeDescroptors()){
			String name = descriptor.getName();
			String[] keys = name.split("\\.");
			HashMap<String, Object> m = map;
			for(int i=0; i<keys.length-1; i++){
				Object mm = m.get(keys[i]);
				if(!(mm instanceof HashMap)){
					mm = new HashMap<String, Object>();
					m.put(keys[i], mm);
				}
				m = (HashMap<String, Object>)mm;
			}
			m.put(keys[keys.length-1], new PC_GresListBoxElement(descriptor.getName(), keys[keys.length-1]));
		}
		HashMap<String, Object> layout = (HashMap<String, Object>) map.get("Layout");
		if(layout==null){
			map.put("Layout", layout = new HashMap<String, Object>());
		}
		layout.put("Split", new PC_GresListBoxElement("Layout.Split", "Split"));
		layout.put("Frame", new PC_GresListBoxElement("Layout.Frame", "Frame"));
		allNodes = makeTree(map);
	}
	
	private static List<PC_GresListBoxElement> makeTree(HashMap<String, Object> map){
		List<PC_GresListBoxElement> list = new ArrayList<PC_GresListBoxElement>();
		for(Entry<String, Object>e:map.entrySet()){
			Object o = e.getValue();
			if(o instanceof PC_GresListBoxElement){
				list.add((PC_GresListBoxElement)o);
			}else{
				list.add(new PC_GresListBoxElement(e.getKey(), makeTree((HashMap<String, Object>) o)));
			}
		}
		return new PC_ImmutableList<PC_GresListBoxElement>(list);
	}
	
	public static void addNodeToGrid(PC_GresNodesysGrid grid, PC_Vec2I pos, String name){
		if(name.equals("Layout.Split")){
			grid.add(new PC_GresNodesysConnectionSplit().setLocation(pos));
		}else if(name.equals("Layout.Frame")){
			grid.add(new PC_GresNodesysNodeFrame().setLocation(pos));
		}else if(name.equals("Group.Group")){
			PC_GresNodesysNode node = new PC_GresNodesysNodeGroup(new PC_NodeGroup(PC_NodeDescriptorGroup.INSTANCE, grid.getGrid(), false));
			PC_GresNodesysEntry entry = new PC_GresNodesysEntry("Group");
			List<String> groups = new ArrayList<String>();
			groups.add("This");
			entry.add(new PC_GresComboBox(groups, 0));
			node.add(entry);
			grid.add(node.setLocation(pos));
		}else{
			grid.add(nodeToGuiNode(makeNodeByID(grid, name)).setLocation(pos));
		}
	}
	
	public static PC_Node makeNodeByID(PC_GresNodesysGrid grid, String name){
		return PC_NodeGridHelper.makeEmptyNode(grid.getGrid(), name);
	}
	
	public static PC_GresNodesysNode nodeToGuiNode(PC_Node node){
		PC_GresNodesysNode guiNode = new PC_GresNodesysNode(node);
		PC_NodeComponent[] components = node.getComponents();
		for(int i=0; i<components.length; i++){
			guiNode.add(makeEntry(components[i]));
		}
		return guiNode;
	}
	
	public static PC_GresNodesysEntry makeEntry(PC_NodeComponent component){
		PC_GresNodesysEntry entry = new PC_GresNodesysEntry(component.getName());
		PC_NodeObjectType type = component.getType();
		int io = component.getIOType();
		int color = type.getColor();
		if((io&PC_NodeComponent.TYPE_IN)!=0){
			entry.add(new PC_GresNodesysConnection(!type.swap(), true, color, type.group()));
		}
		if((io&PC_NodeComponent.TYPE_OUT)!=0){
			entry.add(new PC_GresNodesysConnection(type.swap(), false, color, type.group()));
		}
		PC_GresComponent c = makeValueIn(type, component.getValueInputType(), component.getDefault());
		if(c!=null)
			entry.add(c);
		return entry;
	}
	
	public static PC_GresComponent makeValueIn(PC_NodeObjectType type, PC_INodeValueInput input, Object _default){
		if(input instanceof PC_NodeValueInputTextbox){
			PC_NodeValueInputTextbox textbox = (PC_NodeValueInputTextbox)input;
			PC_GresInputType t;
			switch(textbox.getInputType()){
			case FLOAT:
				t = PC_GresInputType.SIGNED_FLOAT;
				break;
			case INTEGER:
				t = PC_GresInputType.INT;
				break;
			case STRING:
				t = PC_GresInputType.TEXT;
				break;
			case UFLOAT:
				t = PC_GresInputType.UNSIGNED_FLOAT;
				break;
			case UINTEGER:
				t = PC_GresInputType.UNSIGNED_INT;
				break;
			default:
				t = PC_GresInputType.TEXT;
				break;
			}
			return new PC_GresTextEdit(_default==null?"":_default.toString(), 10, t);
		}else if(input instanceof PC_NodeValueInputDropdown){
			PC_NodeValueInputDropdown dropdown = (PC_NodeValueInputDropdown)input;
			List<String> l = new PC_ImmutableArrayList<String>(dropdown.getValues());
			int select;
			if(_default instanceof Number){
				select = ((Number)_default).intValue();
			}else{
				select = l.indexOf(_default);
			}
			if(select<0)
				select=0;
			return new PC_GresComboBox(l, select);
		}
		return null;
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
