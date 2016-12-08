package ch.fhnw.stizzo;

public class VariableMatch {
private String variableName;
private OntologyItem ontologyItem;
public VariableMatch(String variableName, OntologyItem ontologyItem) {
	super();
	this.variableName = variableName;
	this.ontologyItem = ontologyItem;
}
public String getVariableName() {
	return variableName;
}
public void setVariableName(String variableName) {
	this.variableName = variableName;
}
public OntologyItem getOntologyItem() {
	return ontologyItem;
}
public void setOntologyItem(OntologyItem ontologyItem) {
	this.ontologyItem = ontologyItem;
}



}
