package ch.fhnw.stizzo;

import java.util.ArrayList;

//This Java-class defines an instance in the Ontology

public class OntologyInstance extends OntologyItem{

	
	public OntologyInstance(String name, ArrayList<String> types, ArrayList<OntologyAttribute> attributes) {
		super(name, types, attributes);
		
	}

}
