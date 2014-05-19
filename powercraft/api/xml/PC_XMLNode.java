package powercraft.api.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


public class PC_XMLNode {

	private String name;
	
	private List<PC_XMLProperty> properties = new ArrayList<PC_XMLProperty>();
	
	private List<PC_XMLNode> childs = new ArrayList<PC_XMLNode>();
	
	private String text = "";
	
	public PC_XMLNode(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setProperty(String key, String value){
		PC_XMLProperty property = getProperty(key);
		if(property==null){
			this.properties.add(new PC_XMLProperty(key, value));
		}else{
			property.setValue(value);
		}
	}
	
	public void setProperty(PC_XMLProperty property){
		ListIterator<PC_XMLProperty> i = this.properties.listIterator();
		while(i.hasNext()){
			if(i.next().getKey().equals(property.getKey())){
				i.set(property);
				return;
			}
		}
		this.properties.add(property);
	}
	
	public PC_XMLProperty getProperty(String key){
		for(PC_XMLProperty property:this.properties){
			if(property.getKey().equals(key)){
				return property;
			}
		}
		return null;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return this.text;
	}
	
	public void addChild(PC_XMLNode child){
		if(!this.childs.contains(child))
			this.childs.add(child);
	}
	
	public int getChildCount(){
		return this.childs.size();
	}
	
	public PC_XMLNode getChild(int i){
		return this.childs.get(i);
	}
	
	protected String save(String ls){
		String out = ls + "<"+this.name;
		for(PC_XMLProperty property:this.properties){
			out += " "+property.getKey()+" = \""+property.getValue()+"\"";
		}
		if(this.childs.isEmpty() && this.text.trim().isEmpty()){
			return out + "/>";
		}
		out += ">\n";
		String ls2 = ls+"\t";
		for(PC_XMLNode child:this.childs){
			out += child.save(ls2)+"\n";
		}
		if(!(this.text==null || this.text.trim().isEmpty())){
			String[] s = this.text.split("\n");
			for(String ss:s){
				out += ls2+ss+"\n";
			}
		}
		return out + ls+"</"+this.name+">";
	}

	public String save() {
		return save("");
	}
	
}
