package ch.fhnw.stizzo;

import java.util.ArrayList;

public class ADOxxDecisionTableEntry {
	private String object_name;
	private ArrayList<String> properties;
	private String dest_name;
	private int output_priority;
	
	public ADOxxDecisionTableEntry(String object_name, ArrayList<String> properties, String dest_name) {
		
		this.object_name = object_name;
		this.properties = properties;
		this.dest_name = dest_name;
	}
	
	public ADOxxDecisionTableEntry(String object_name, ArrayList<String> properties, String dest_name, int output_priority) {
		
		this.object_name = object_name;
		this.properties = properties;
		this.dest_name = dest_name;
		this.output_priority = output_priority;
	}
	
	

	public String getObject_name() {
		return object_name;
	}
	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}
	public ArrayList<String> getProperties() {
		return properties;
	}
	public void setProperties(ArrayList<String> properties) {
		this.properties = properties;
	}
	public String getDest_name() {
		return dest_name;
	}
	public void setDest_name(String dest_name) {
		this.dest_name = dest_name;
	}

	public int getOutput_priority() {
		return output_priority;
	}

	public void setOutput_priority(int output_priority) {
		this.output_priority = output_priority;
	}
	
}
