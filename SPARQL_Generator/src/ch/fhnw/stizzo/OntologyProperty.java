package ch.fhnw.stizzo;

import java.util.ArrayList;

public class OntologyProperty extends OntologyItem{
	//This JAVA-class define a property of an instance in the Ontology
	public OntologyProperty(String name, ArrayList<String> types, ArrayList<OntologyAttribute> attributes) {
		super(name, types, attributes);
		
	}
	
	public boolean isAnObjectProperty(){
		boolean result = false;
		for (int i = 0; i < this.getTypes().size();i++){
			if (this.getTypes().get(i).equals("owl:ObjectProperty")){
				result = true;
				break;
			}
		}
		return result;
	}
	
	public boolean isADataTypeProperty(){
		boolean result = false;
		for (int i = 0; i < this.getTypes().size();i++){
			if (this.getTypes().get(i).equals("owl:DatatypeProperty")){
				result = true;
				break;
			}
		}
		return result;
	}
	
	
	}
	
