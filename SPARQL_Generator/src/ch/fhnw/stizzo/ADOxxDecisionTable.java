package ch.fhnw.stizzo;

import java.util.ArrayList;

public class ADOxxDecisionTable {
private String name;
private String hit_policy;
private String aggregation_indicator;
private ArrayList<ADOxxDecisionTableEntry> input_names;
private ArrayList<ADOxxDecisionTableEntry> output_names;
private ArrayList<ADOxxDecisionTableRow> rows;
private int priority;

public int getPriority() {
	return priority;
}

public void setPriority(int priority) {
	this.priority = priority;
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public String getHit_policy() {
	return hit_policy;
}

public void setHit_policy(String hit_policy) {
	this.hit_policy = hit_policy;
}

public ArrayList<ADOxxDecisionTableEntry> getInput_names() {
	return input_names;
}

public void setInput_names(ArrayList<ADOxxDecisionTableEntry> input_names) {
	this.input_names = input_names;
}

public ArrayList<ADOxxDecisionTableEntry> getOutput_names() {
	return output_names;
}

public void setOutput_names(ArrayList<ADOxxDecisionTableEntry> output_names) {
	this.output_names = output_names;
}

public ArrayList<ADOxxDecisionTableRow> getRows() {
	return rows;
}

public void setRows(ArrayList<ADOxxDecisionTableRow> rows) {
	this.rows = rows;
}

public String getAggregation_indicator() {
	return aggregation_indicator;
}

public void setAggregation_indicator(String aggregation_indicator) {
	this.aggregation_indicator = aggregation_indicator;
}

public ADOxxDecisionTable(
		String name,
		String hit_policy, 
		String aggregation_indicator,
		ArrayList<ADOxxDecisionTableEntry> input_names,
		ArrayList<ADOxxDecisionTableEntry> output_names,
		ArrayList<ADOxxDecisionTableRow> rows,
		int priority) {
	
	this.name = name;
	this.hit_policy = hit_policy;
	this.aggregation_indicator = aggregation_indicator;
	this.input_names = input_names;
	this.output_names = output_names;
	this.rows = rows;
	this.priority = priority;
}

}
