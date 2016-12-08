package ch.fhnw.stizzo;

public class ADOxxDecisionTableNormalizedEntry {
	private String object_name;
	private String property;
	private String dest_name;
	private Boolean isARelation;//Is a relation to write on the SparQL rule
	private Boolean haveValue;	//Have a value to match in the decision table
	private int num_entry;	//Match with the input/output entry  column
	private int output_priority;
	
	public String getObject_name() {
		return object_name;
	}

	public void setObject_name(String object_name) {
		this.object_name = object_name;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getDest_name() {
		return dest_name;
	}

	public void setDest_name(String dest_name) {
		this.dest_name = dest_name;
	}

	public Boolean getIsARelation() {
		return isARelation;
	}

	public void setIsARelation(Boolean isARelation) {
		this.isARelation = isARelation;
	}

	public Boolean getHaveValue() {
		return haveValue;
	}

	public void setHaveValue(Boolean haveValue) {
		this.haveValue = haveValue;
	}

	public int getNum_entry() {
		return num_entry;
	}

	public void setNum_entry(int num_entry) {
		this.num_entry = num_entry;
	}


	public int getOutput_priority() {
		return output_priority;
	}

	public void setOutput_priority(int output_priority) {
		this.output_priority = output_priority;
	}

	public ADOxxDecisionTableNormalizedEntry(String object_name, String property, String dest_name, Boolean isARelation,
			Boolean haveValue) {
		super();
		this.object_name = object_name;
		this.property = property;
		this.dest_name = dest_name;
		this.isARelation = isARelation;
		this.haveValue = haveValue;
		
	}

	public ADOxxDecisionTableNormalizedEntry(String object_name, String property, String dest_name, Boolean isARelation,
			Boolean haveValue, int num_entry, int output_priority) {
		super();
		this.object_name = object_name;
		this.property = property;
		this.dest_name = dest_name;
		this.isARelation = isARelation;
		this.haveValue = haveValue;
		this.num_entry = num_entry;
		this.output_priority = output_priority;
		
	}


	
}
