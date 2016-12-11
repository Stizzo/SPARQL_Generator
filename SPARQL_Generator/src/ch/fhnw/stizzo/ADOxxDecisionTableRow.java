package ch.fhnw.stizzo;

import java.util.ArrayList;

public class ADOxxDecisionTableRow {
int num_rule;
ArrayList<String> input;
ArrayList<String> output;
ArrayList<String> output_values;

public int getNum_rule() {
	return num_rule;
}
public void setNum_row(int num_rule) {
	this.num_rule = num_rule;
}

public ArrayList<String> getInput() {
	return input;
}

public void setInput(ArrayList<String> input) {
	this.input = input;
}

public ArrayList<String> getOutput() {
	return output;
}

public void setOutput(ArrayList<String> output) {
	this.output = output;
}

public void setNum_rule(int num_rule) {
	this.num_rule = num_rule;
}

public ADOxxDecisionTableRow(int num_rule, ArrayList<String> input, ArrayList<String> output) {
	super();
	this.num_rule = num_rule;
	this.input = input;
	this.output = output;
}

public int getOutputPriorityNumber(){
	//THIS METHOD RETURNS AN INTEGER BASED ON THE OUTPUT PERMUTATION VALUE OF THE ROW
	//Maybe not used anymore
	int result = 0;
	for (int i = output.size()-1 ; i >= 0; i--){
		if (!output.get(i).equals("-")){
			result = (int) (result + Math.pow(10,i));
		}
	}
	return result;
}
public String getOutputPriorityString(){
	//THIS METHOD RETURNS A STRING BASED ON THE OUTPUT PERMUTATION VALUE OF THE ROW
	//Maybe not used anymore
	String result = "";
	for (int i = output.size()-1 ; i >= 0; i--){
		if (!output.get(i).equals("-")){
			result = "1"+result;
		}else{
			result = "0"+result;
		}
	}
	return result;
}
}
