package ch.fhnw.stizzo;

import java.util.ArrayList;


public class OntologyClass extends OntologyItem{
	//This JAVA-class define the "Class" of an Ontology
	
	public OntologyClass(String name, ArrayList<String> types, ArrayList<OntologyAttribute> attributes) {
		super(name, types, attributes);
		
	}

	public ArrayList<OntologyClass> getParentClasses(ArrayList<OntologyClass> oc){	
		//takes in input a list of class; 
		//it search for the parents of this.class 
		//it returns an array of OntologyClass matched with the list of class taken in input
		ArrayList<OntologyAttribute> attribute_result = new ArrayList<OntologyAttribute>();
		ArrayList<OntologyClass> class_result = new ArrayList<OntologyClass>();
		if (this.getAttributes().size()!=0){
		for (int i = 0; i < this.getAttributes().size(); i++){
			if (this.getAttributes().get(i).getName().equals("rdfs:subClassOf") &&
					!this.getAttributes().get(i).getValue().equals("owl:Thing")){
				attribute_result.add(this.getAttributes().get(i));
			}
		}
		}
		
			for (int j = 0; j < attribute_result.size(); j++){
				for (int k = 0; k < oc.size(); k++){
					if (attribute_result.get(j).getValue().equals(oc.get(k).getName())){
						class_result.add(oc.get(k));
					}
				}
			
		}
			/* TESTING THE NUMBER OF PARENTS AND THE FIRST PARENT
			System.out.println("parsing"+this.getName()+" - "+attribute_result.size() + " - " + class_result.size());
			if (class_result.size()!=0){
				System.out.println("    "+class_result.get(0).getName());
			}
			*/
		return class_result;
	}
	
	public int getNumberOfParent(){	
		//not used
		//TODO: can be used for code optimization, possible mismatching with attribute/class results, must be tested!
		ArrayList<OntologyAttribute> attribute_result = new ArrayList<OntologyAttribute>();
	
		if (this.getAttributes().size()!=0){
		for (int i = 0; i < this.getAttributes().size(); i++){
			if (this.getAttributes().get(i).getName().equals("rdfs:subClassOf") &&
					!this.getAttributes().get(i).getValue().equals("owl:Thing")){
				attribute_result.add(this.getAttributes().get(i));
			}
		}
		}
		
	return attribute_result.size();
	
	}

}
