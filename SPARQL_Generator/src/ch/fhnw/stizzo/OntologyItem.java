package ch.fhnw.stizzo;

import java.util.ArrayList;

public class OntologyItem {
	private String name;
	private ArrayList<String> types;
	private ArrayList<OntologyAttribute> attributes;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getTypes() {
		return types;
	}
	public void setTypes(ArrayList<String> types) {
		this.types = types;
	}
	public ArrayList<OntologyAttribute> getAttributes() {
		return attributes;
	}
	public void setAttributes(ArrayList<OntologyAttribute> attributes) {
		this.attributes = attributes;
	}
	public OntologyItem(String name,  ArrayList<String> types, ArrayList<OntologyAttribute> attributes) {
		
		this.name = name;
		this.types = types;
		this.attributes = attributes;
	}
	
	public String getNameWithoutPrefix() {
		String[] arraySplittate = this.name.split(":");
		return arraySplittate[1];
	}

}
