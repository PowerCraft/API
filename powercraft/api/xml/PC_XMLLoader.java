package powercraft.api.xml;

import java.io.ByteArrayInputStream;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import powercraft.api.PC_Logger;


public class PC_XMLLoader {
	
	public static PC_XMLNode load(File file){
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);
			doc.getDocumentElement().normalize();
			return load(doc.getChildNodes().item(0));
		}catch(Exception e){
			PC_Logger.severe("Error while reading xml");
			PC_Logger.throwing("PC_XMLLoader", "load", e);
			return null;
		}
	}
	
	public static PC_XMLNode load(String file){
		try{
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new ByteArrayInputStream(file.getBytes("UTF-8")));
			doc.getDocumentElement().normalize();
			return load(doc.getChildNodes().item(0));
		}catch(Exception e){
			PC_Logger.severe("Error while reading xml");
			PC_Logger.throwing("PC_XMLLoader", "load", e);
			return null;
		}
	}

	private static PC_XMLNode load(Node item) {
		PC_XMLNode node = new PC_XMLNode(item.getNodeName());
		node.setText(item.getTextContent());
		NamedNodeMap nnm = item.getAttributes();
		if(nnm!=null){
			int l = nnm.getLength();
			for(int i=0; i<l; i++){
				Node n = nnm.item(i);
				node.setProperty(n.getNodeName(), n.getNodeValue());
			}
		}
		NodeList nl = item.getChildNodes();
		for(int i=0; i<nl.getLength(); i++){
			if(!nl.item(i).getNodeName().equals("#text")){
				node.addChild(load(nl.item(i)));
			}
		}
		return node;
	}
	
}
