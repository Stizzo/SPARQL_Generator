package ch.fhnw.stizzo;

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;
//libreries of reading xml data
import javax.xml.parsers.DocumentBuilderFactory;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class Operations {
	private ArrayList<OntologyClass> classes; // it contains all the classes of
												// the ontology
	private ArrayList<OntologyProperty> properties; // it contains all the
													// properties of the
													// ontology
	private ArrayList<OntologyInstance> instances; // it contains all the
													// instances of the ontology
	private ArrayList<String> ontologyPreamble; // it contains all the lines
												// that occurs between the start
												// of the file (ttl) and the
												// start of the first ontology
												// object
	private ArrayList<ADOxxDecisionTable> adoxxDecisionTables; // it contains
																// all the ADOxx
																// decision
																// tables
	private ArrayList<VariableMatch> variables;
	private PrintWriter writer_status;
	private String temp_file = ".temp";
	private String status_file = "report_";
	private String output_file = "rules_";

	public Operations() {
		this.classes = new ArrayList<OntologyClass>();
		this.properties = new ArrayList<OntologyProperty>();
		this.instances = new ArrayList<OntologyInstance>();
		this.ontologyPreamble = new ArrayList<String>();
		this.adoxxDecisionTables = new ArrayList<ADOxxDecisionTable>();
	}

	public int[] parseOntology(String path_file, boolean offline) {
		// this method parse the ontology
		// takes in input the path of the file and it fills the four arrays
		// stored this java-class (classes, properties, instances and the
		// ontologyPreamble)
		// returns an array with the number of classes, properties and instances
		// loaded
		String line = null;
		FileReader reader = null;
		Scanner scanner = null;
		try {
			if (offline) {
				reader = new FileReader(path_file);
				scanner = new Scanner(reader);
			} else {
				URL url = new URL(path_file);
				scanner = new Scanner(url.openStream());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

		// Set the variable "startPreamble" to detect when the preamble finish
		boolean preamble = true;
		ArrayList<String> temp_type = null;
		String temp_name = null;
		ArrayList<OntologyAttribute> temp_attributes = null;
		boolean titleLine = true;
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();

			if (preamble) {
				// here we are in the preamble
				if (!line.startsWith(".")) {
					ontologyPreamble.add(line);
				} else {
					// here we are in the first dot
					// where the ontology elements starts
					preamble = false;

				}
				// here we are still in the preamble
				// where the line doesn't start with "."

			} else {
				// here we are not in the preamble and we start to analyse
				// the elements of the ontology
				if (titleLine && !line.startsWith(".")) {
					// first line of the object of the ontology
					// here we can find prefix:name of the object
					// and we instantiate all the data structures
					temp_type = new ArrayList<String>();
					temp_name = new String();
					temp_attributes = new ArrayList<OntologyAttribute>();

					temp_name = line;
					titleLine = false;
				} else if (!titleLine && line.trim().startsWith(".")) {
					// end of the object of ontology
					titleLine = true;
					for (int t = 0; t < temp_type.size(); t++) {
						if (temp_type.get(t).equals("owl:Class")) {
							// Parsing a Class
							OntologyClass c = new OntologyClass(temp_name, temp_type, temp_attributes);
							classes.add(c);
							break;
						} else if (temp_type.get(t).equals("owl:AnnotationProperty")
								|| temp_type.get(t).equals("owl:DatatypeProperty")
								|| temp_type.get(t).equals("owl:DeprecatedProperty")
								|| temp_type.get(t).equals("owl:FunctionalProperty")
								|| temp_type.get(t).equals("owl:ObjectProperty")) {
							// Parsing a Property
							OntologyProperty p = new OntologyProperty(temp_name, temp_type, temp_attributes);
							properties.add(p);
							break;

						} else if (t == temp_type.size() - 1) {
							// Parsing an Istance
							OntologyInstance i = new OntologyInstance(temp_name, temp_type, temp_attributes);
							instances.add(i);
						}
					}

				} else {
					// body of the object of the ontology
					String[] arraySplittate = parseAttributeName(line);
					if (arraySplittate[0].equals("rdf:type")) {
						temp_type.add(arraySplittate[1].replaceAll(";", "").trim());
					} else {
						// parsing attributes
						OntologyAttribute oa;
						String[] arraySplittate2 = arraySplittate[1].trim().split("\\^\\^");
						// splitting the string with "^^" so we can define type
						// and value
						if (arraySplittate2.length == 2) {
							// if the split has type and value
							oa = new OntologyAttribute(arraySplittate[0].replaceAll(";", "").trim(),
									arraySplittate2[1].replaceAll(";", "").trim(),
									arraySplittate2[0].replaceAll("\"", "").replaceAll(";", "").trim()); // name,type,value

						} else {
							oa = new OntologyAttribute(arraySplittate[0].replaceAll(";", "").trim(), "",
									arraySplittate2[0].replaceAll(";", "").trim()); // name,type,value
						}
						temp_attributes.add(oa);
					}
				}

			} // end not preamble

		} // end while scanner
		scanner.close();

		// -------------TESTING THE WHOLE ONTOLOGY-------------
		/*
		 * System.out.println("Number of lines of preamble: " +
		 * ontologyPreamble.size()); System.out.println("Number of classes: "
		 * +classes.size()); for (int i = 0; i < classes.size(); i++){
		 * System.out.print("    "
		 * +classes.get(i).getPrefix()+":"+classes.get(i).getName());
		 * System.out.print(" - Type: "); for(int
		 * j=0;j<classes.get(i).getTypes().size();j++){
		 * System.out.print(classes.get(i).getTypes().get(j)+" ,"); }
		 * System.out.println(""); } System.out.println("Number of properties: "
		 * +properties.size()); for (int i = 0; i < properties.size(); i++){
		 * System.out.print("    "
		 * +properties.get(i).getPrefix()+":"+properties.get(i).getName());
		 * System.out.print(" - Type: "); for(int
		 * j=0;j<properties.get(i).getTypes().size();j++){
		 * System.out.print(properties.get(i).getTypes().get(j)+" ,"); }
		 * System.out.println(""); } System.out.println("Number of instances: "
		 * +instances.size()); for (int i = 0; i < instances.size(); i++){
		 * System.out.print("    "
		 * +instances.get(i).getPrefix()+":"+instances.get(i).getName());
		 * System.out.print(" - Type: "); for(int
		 * j=0;j<instances.get(i).getTypes().size();j++){
		 * System.out.print(instances.get(i).getTypes().get(j)+" ,"); }
		 * System.out.println(""); }
		 */

		// --------------------------TESTING THE CLASSES OF
		// ONTOLOGY---------------------
		/*
		 * System.out.println("Classes:"); for (int i = 0; i < classes.size();
		 * i++){ System.out.println("   Name: "
		 * +classes.get(i).getPrefix()+":"+classes.get(i).getName());
		 * System.out.println("     Types:"); for (int j = 0; j <
		 * classes.get(i).getTypes().size();j++){ System.out.println("       "
		 * +classes.get(i).getTypes().get(j)); } System.out.println(
		 * "     Attributes:"); for (int j = 0; j <
		 * classes.get(i).getAttributes().size(); j++){ System.out.println(
		 * "       name: "+classes.get(i).getAttributes().get(j).getName());
		 * System.out.println("       type: "
		 * +classes.get(i).getAttributes().get(j).getType());
		 * System.out.println("       value: "
		 * +classes.get(i).getAttributes().get(j).getValue());
		 * System.out.println("       -------"); }
		 * System.out.println("--------------------"); }
		 * 
		 * //--------------------------TESTING THE PROPERTIES OF
		 * ONTOLOGY---------------------
		 * 
		 * System.out.println("Property:"); for (int i = 0; i <
		 * properties.size(); i++){ System.out.println("   Name: "
		 * +properties.get(i).getPrefix()+":"+properties.get(i).getName());
		 * System.out.println("     Types:"); for (int j = 0; j <
		 * properties.get(i).getTypes().size();j++){ System.out.println(
		 * "       "+properties.get(i).getTypes().get(j)); } System.out.println(
		 * "     Attributes:"); for (int j = 0; j <
		 * properties.get(i).getAttributes().size(); j++){ System.out.println(
		 * "       name: "+properties.get(i).getAttributes().get(j).getName());
		 * System.out.println("       type: "
		 * +properties.get(i).getAttributes().get(j).getType());
		 * System.out.println("       value: "
		 * +properties.get(i).getAttributes().get(j).getValue());
		 * System.out.println("       -------"); }
		 * System.out.println("--------------------"); }
		 * 
		 * //--------------------------TESTING THE PROPERTIES OF
		 * ONTOLOGY--------------------- System.out.println("Instances:"); for
		 * (int i = 0; i < instances.size(); i++){ System.out.println(
		 * "   Name: "
		 * +instances.get(i).getPrefix()+":"+instances.get(i).getName());
		 * System.out.println("     Types:"); for (int j = 0; j <
		 * instances.get(i).getTypes().size();j++){ System.out.println("       "
		 * +instances.get(i).getTypes().get(j)); } System.out.println(
		 * "     Attributes:"); for (int j = 0; j <
		 * instances.get(i).getAttributes().size(); j++){ System.out.println(
		 * "       name: "+instances.get(i).getAttributes().get(j).getName());
		 * System.out.println("       type: "
		 * +instances.get(i).getAttributes().get(j).getType());
		 * System.out.println("       value: "
		 * +instances.get(i).getAttributes().get(j).getValue());
		 * System.out.println("       -------"); }
		 * System.out.println("--------------------"); }
		 */
		return new int[] { classes.size(), properties.size(), instances.size() };

	}// end method

	public int[] loadXML(String path_file) {// this method load the xml file
											// with the ADOxx DecisionTable(s)
											// and instances

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(true);

		DocumentBuilder builder = null;
		Document doc = null;

		try {
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			builder = factory.newDocumentBuilder();
			doc = builder.parse(new File(test.ui.getTxtOutputDirectory().getText() + "\\" + temp_file));
		} catch (Exception e) {
			e.printStackTrace();
		}

		Node root = doc.getFirstChild();
		Node models = null;
		Node model = null;
		Node instance = null;

		int numb_model = 0; // this counts how many models have we analysed

		for (int i = 0; i < root.getChildNodes().getLength(); i++) {
			// We look for the node MODELS
			if (root.getChildNodes().item(i).getNodeName().equals("MODELS")) {
				models = root.getChildNodes().item(i);

				for (int j = 0; j < models.getChildNodes().getLength(); j++) {
					// We look for the node MODEL
					if (models.getChildNodes().item(j).getNodeName().equals("MODEL")) {
						numb_model++;
						model = models.getChildNodes().item(j);

						for (int k = 0; k < model.getChildNodes().getLength(); k++) {
							// We look for the model INSTANCE

							if (model.getChildNodes().item(k).getNodeName().equals("INSTANCE")) {
								instance = model.getChildNodes().item(k);
								// creating a new ADOxxInstance
								if (!instance.getAttributes().getNamedItem("class").getNodeValue()
										.equals("Boxed Expression")) {
									/*
									 * //here it is checking if the class of the
									 * adoxx instance is present in the excel
									 * file for (int y = 0; y <
									 * objectMatches.size();y++){ if
									 * (instance.getAttributes().getNamedItem(
									 * "class").getNodeValue().equals(
									 * objectMatches.get(y).
									 * getAdoxxAttributeName()) &&
									 * objectMatches.get(y).getAdoxxType().trim(
									 * ).equals("Class")){
									 * 
									 * //here we are parsing a non-decision
									 * table instance String inst_id =
									 * instance.getAttributes().getNamedItem(
									 * "id").getNodeValue(); String inst_class =
									 * instance.getAttributes().getNamedItem(
									 * "class").getNodeValue(); String inst_name
									 * = instance.getAttributes().getNamedItem(
									 * "name").getNodeValue();
									 * ArrayList<ADOxxAttribute> inst_attributes
									 * = new ArrayList<ADOxxAttribute>();;
									 * //this arraylist is used to create the
									 * object ADOxxAttribute
									 * temp_attributes_to_save = null;
									 * 
									 * 
									 * ArrayList<Node> temp_attributes = new
									 * ArrayList<Node>(); //arraylist where to
									 * collect all the <ATTRIBUTES> of the XML
									 * of an object for (int z = 0; z <
									 * instance.getChildNodes().getLength();
									 * z++){ if
									 * (instance.getChildNodes().item(z).
									 * getNodeName().equals("ATTRIBUTE")){ //i
									 * save all the node ATTRIBUTE in an array
									 * called temp_attributes
									 * temp_attributes.add(instance.
									 * getChildNodes().item(z));
									 * 
									 * } }
									 * 
									 * for (int l = 0; l <
									 * temp_attributes.size();l++){
									 * 
									 * for (int m = 0; m < objectMatches.size();
									 * m++){
									 * 
									 * if
									 * (temp_attributes.get(l).getAttributes().
									 * getNamedItem("name").getNodeValue().
									 * equals(objectMatches.get(m).
									 * getAdoxxAttributeName()) &&
									 * inst_class.equals(objectMatches.get(m).
									 * getAdoxxType())){
									 * 
									 * if
									 * (temp_attributes.get(l).getChildNodes().
									 * getLength() == 0){ //Checking for a
									 * non-null value; temp_attributes_to_save =
									 * new ADOxxAttribute(
									 * temp_attributes.get(l).getAttributes().
									 * getNamedItem("name").getNodeValue(),
									 * temp_attributes.get(l).getAttributes().
									 * getNamedItem("type").getNodeValue(), "");
									 * inst_attributes.add(
									 * temp_attributes_to_save);
									 * 
									 * } else { temp_attributes_to_save = new
									 * ADOxxAttribute(
									 * temp_attributes.get(l).getAttributes().
									 * getNamedItem("name").getNodeValue(),
									 * temp_attributes.get(l).getAttributes().
									 * getNamedItem("type").getNodeValue(),
									 * temp_attributes.get(l).getLastChild().
									 * getNodeValue()); inst_attributes.add(
									 * temp_attributes_to_save); }
									 * 
									 * } }
									 * 
									 * }
									 * 
									 * adoxxInstances.add(new
									 * ADOxxInstance(inst_id,inst_class,
									 * inst_name,inst_attributes)); }
									 * 
									 * }
									 */
								} else {
									String DT_name;
									String hit_policy = null; // variable for
																// ADOxxDecisionTable
									String aggregation_indicator = null; // variable
																			// for
																			// ADOxxDecisionTable
									int priority_of_execution = 0;
									ArrayList<Node> temp_attributes = new ArrayList<Node>();
									Node temp_record = null;
									ArrayList<String> output_values = new ArrayList<String>();
									; // arraylist for ADOxxDecisionTable

									DT_name = instance.getAttributes().getNamedItem("name").getNodeValue();
									// save all the attributes in a temp array:
									for (int z = 0; z < instance.getChildNodes().getLength(); z++) {
										if (instance.getChildNodes().item(z).getNodeName().equals("ATTRIBUTE")) {
											// i save all the node ATTRIBUTE in
											// an array called temp_attributes
											temp_attributes.add(instance.getChildNodes().item(z));

										} else if (instance.getChildNodes().item(z).getNodeName().equals("RECORD")
												&& instance.getChildNodes().item(z).getAttributes().item(0)
														.getNodeValue().equals("Decision Table GraphRep")) {
											// we get the item(0) because
											// we suppose that the item(0) is
											// the "name=" attribute
											temp_record = instance.getChildNodes().item(z);
										}
									}
									Node temp_node = null;
									for (int a = 0; a < temp_attributes.size(); a++) {
										// searchin for hit policy value
										for (int b = 0; b < temp_attributes.get(a).getAttributes().getLength(); b++) {

											if (temp_attributes.get(a).getAttributes().item(b).getNodeValue()
													.equals("Hit Policy")) {
												temp_node = temp_attributes.get(a);
												hit_policy = temp_node.getLastChild().getNodeValue();

											}
											if (temp_attributes.get(a).getAttributes().item(b).getNodeValue()
													.equals("Aggregation Indicator")) {
												temp_node = temp_attributes.get(a);
												aggregation_indicator = temp_node.getLastChild().getNodeValue();

											}
											if (temp_attributes.get(a).getAttributes().item(b).getNodeValue()
													.equals("Priority of Execution")) {
												temp_node = temp_attributes.get(a);
												if (temp_node.getLastChild().getNodeValue().equals("")) {
													priority_of_execution = 0;
												} else {
													priority_of_execution = Integer
															.parseInt(temp_node.getLastChild().getNodeValue());

												}

											}
										}

									}
									// end cycle for assign hit_policy value
									ArrayList<ADOxxDecisionTableEntry> input_names = new ArrayList<ADOxxDecisionTableEntry>(); // arraylist
																																// for
																																// ADOxxDecisionTable
									ArrayList<ADOxxDecisionTableEntry> output_names = new ArrayList<ADOxxDecisionTableEntry>(); // arraylist
																																// for
																																// ADOxxDecisionTable
									ArrayList<ADOxxDecisionTableRow> rows = new ArrayList<ADOxxDecisionTableRow>(); // arraylist
																													// for
																													// ADOxxDecisionTable
									int num_row = 0;
									// 1 means names of variable;
									// 2 could means output values if the hit
									// policy is "Single Hit Priority" or
									// "Multiple Hit Rule Order" > 1 could mean
									// input/output data for other Hit policies
									int num_rule = 0; // variable for DTRow
									ArrayList<String> input = null;
									// arraylist for DTRow
									ArrayList<String> output = null;
									// arraylist for DTRow

									Node temp_row = null;
									Node temp_attribute = null;

									for (int a = 0; a < temp_record.getChildNodes().getLength(); a++) {
										// iterating all the rows

										if (temp_record.getChildNodes().item(a).getNodeName().equals("ROW")) {
											temp_row = temp_record.getChildNodes().item(a);
											input = new ArrayList<String>();
											output = new ArrayList<String>();

											for (int b = 0; b < temp_row.getAttributes().getLength(); b++) {
												// iterating attributes of the
												// rows
												if (temp_row.getAttributes().item(b).getNodeName().equals("number")) {
													num_row = Integer
															.parseInt(temp_row.getAttributes().item(b).getNodeValue());

												}
											} // end iterating attributes of the
												// row

											for (int c = 0; c < temp_row.getChildNodes().getLength(); c++) {
												// iterating all ATTRIBUTE of
												// the ROW

												if (temp_row.getChildNodes().item(c).getNodeName()
														.equals("ATTRIBUTE")) {
													temp_attribute = temp_row.getChildNodes().item(c);

													if (num_row == 1) {
														// parsing
														// name
														// of
														// values

														if (temp_attribute.getChildNodes().getLength() != 0) {
															// chicking for
															// no-null value
															if (temp_attribute.getAttributes().item(0).getNodeValue()
																	.startsWith("Input")) {
																String[] inputSplittate = temp_attribute.getLastChild()
																		.getNodeValue().trim().replaceAll(" ", "_")
																		.split("\\.");
																ArrayList<String> temp_properties = new ArrayList<String>();
																String temp_obj_name = inputSplittate[0];
																String temp_dest_name = inputSplittate[inputSplittate.length
																		- 1];
																;
																for (int x = 1; x < inputSplittate.length - 1; x++) {
																	temp_properties.add(inputSplittate[x]);
																}

																input_names
																		.add(new ADOxxDecisionTableEntry(temp_obj_name,
																				temp_properties, temp_dest_name));
															} else if (temp_attribute.getAttributes().item(0)
																	.getNodeValue().startsWith("Output")) {
																String[] outputSplittate = temp_attribute.getLastChild()
																		.getNodeValue().trim().replaceAll(" ", "_")
																		.split("\\.");
																ArrayList<String> temp_properties = new ArrayList<String>();
																temp_properties.add(outputSplittate[1]);
																String temp_obj_name = outputSplittate[0];
																String temp_dest_name = outputSplittate[2];

																output_names
																		.add(new ADOxxDecisionTableEntry(temp_obj_name,
																				temp_properties, temp_dest_name));

															}
															num_rule = -1;
														} // end if checking for
															// no-null values

													} else if (num_row == 2 && (hit_policy.equals("Single Hit Priority")
															|| hit_policy.equals("Multiple Hit Output Order"))) {

														if (temp_attribute.getAttributes().item(0).getNodeValue()
																.startsWith("Output")) {
															
															if (temp_attribute.getChildNodes().getLength() != 0){
																
															output_values.add(temp_attribute.getLastChild().getNodeValue().replaceAll("&quot;", "\""));
															}
														}
														
														
														
													} else if (num_row >= 1)
															 { // parsing
														
																				// data
														if (temp_attribute.getChildNodes().getLength() != 0) { // chicking
																												// for
																												// no-null
																												// values
															if (temp_attribute.getAttributes().item(0).getNodeValue()
																	.startsWith("Input")) {
																input.add(temp_attribute.getLastChild().getNodeValue()
																		.replaceAll("&quot;", "\""));
																// System.out.println("input:
																// "+temp_attribute.getLastChild().getNodeValue());
															} else if (temp_attribute.getAttributes().item(0)
																	.getNodeValue().startsWith("Output")) {
																output.add(temp_attribute.getLastChild().getNodeValue()
																		.replaceAll("&quot;", "\""));
																// System.out.println("output:
																// "+temp_attribute.getLastChild().getNodeValue());
															} else if (temp_attribute.getAttributes().item(0)
																	.getNodeValue().startsWith("Rule")) {
																num_rule = Integer.parseInt(
																		temp_attribute.getLastChild().getNodeValue());
																// System.out.println("rule
																// number:
																// "+temp_attribute.getLastChild().getNodeValue());
															}

														} // end if checking for
															// no-null values
													} // end checking number of
														// row

												} // end if we are analyzing an
													// ATTRIBUTE
											} // end iterating ATTRIBUTE of ROW
											if (num_row > 2 && (hit_policy.equals("Single Hit Priority")
													|| hit_policy.equals("Multiple Hit Output Order"))) {
												rows.add(new ADOxxDecisionTableRow(num_rule, input, output));
											} else if (num_row > 1 && !hit_policy.equals("Single Hit Priority")
													&& !hit_policy.equals("Multiple Hit Output Order")) {
												rows.add(new ADOxxDecisionTableRow(num_rule, input, output));
											}
										} // end if the node it's a row
									} // end iterating rows
									if (hit_policy.equals("Single Hit Priority")
											|| hit_policy.equals("Multiple Hit Output Order")) {
										adoxxDecisionTables.add(new ADOxxDecisionTable(DT_name, hit_policy,
												aggregation_indicator, input_names, output_names, rows,
												priority_of_execution, output_values));
									} else {
										adoxxDecisionTables
												.add(new ADOxxDecisionTable(DT_name, hit_policy, aggregation_indicator,
														input_names, output_names, rows, priority_of_execution));
									}

								}

							} // end if
						} // end looking for instance
					} // end if
				} // end for model
			} // end if
		} // end for models

		File a = new File(test.ui.getTxtOutputDirectory().getText() + File.separator + temp_file);
		a.delete();
		// TEST, INSTANCES ONLY
		/*
		 * for (int i = 0; i < adoxxInstances.size(); i++){
		 * System.out.println("------------------------"); System.out.println(
		 * "ID: "+adoxxInstances.get(i).getInst_id()+"    Name: "
		 * +adoxxInstances.get(i).getInst_name()+"    Type: "
		 * +adoxxInstances.get(i).getInst_class()+"    #Instances: "
		 * +adoxxInstances.get(i).getAttributes().size()); for (int j = 0; j <
		 * adoxxInstances.get(i).getAttributes().size(); j++){
		 * System.out.println("    Name: "
		 * +adoxxInstances.get(i).getAttributes().get(j).get(0) + " - Type: "
		 * +adoxxInstances.get(i).getAttributes().get(j).get(1) + " - Value: "
		 * +adoxxInstances.get(i).getAttributes().get(j).get(2)); } }
		 */
		// TEST, DECISION TABLE ONLY (only the first decision table)
		/*
		 * System.out.println("Decision table: " +
		 * adoxxDecisionTables.get(0).getHit_policy()); System.out.println(
		 * "Number of names: "+
		 * adoxxDecisionTables.get(0).getInput_names().size()); for (int i = 0;
		 * i < adoxxDecisionTables.get(0).getInput_names().size(); i++){
		 * System.out.println("    "
		 * +adoxxDecisionTables.get(0).getInput_names().get(i)); }
		 * System.out.println("Number of types: "+
		 * adoxxDecisionTables.get(0).getInput_types().size()); for (int i = 0;
		 * i < adoxxDecisionTables.get(0).getInput_types().size(); i++){
		 * System.out.println("    "
		 * +adoxxDecisionTables.get(0).getInput_types().get(i)); }
		 * System.out.println("Number of rules: "+
		 * adoxxDecisionTables.get(0).getRows().size());
		 * 
		 * for (int i = 0; i < adoxxDecisionTables.get(0).getRows().size();
		 * i++){ System.out.println("Rule number: "+
		 * adoxxDecisionTables.get(0).getRows().get(i).num_row);
		 * System.out.println("    inputs:"); for (int j = 0; j <
		 * adoxxDecisionTables.get(0).getRows().get(i).getInput().size(); j++){
		 * System.out.println("        "
		 * +adoxxDecisionTables.get(0).getRows().get(i).getInput().get(j));
		 * 
		 * 
		 * } for (int k = 0; k <
		 * adoxxDecisionTables.get(0).getRows().get(i).getOutput().size(); k++){
		 * 
		 * System.out.println("    outputs:");
		 * 
		 * System.out.println("        "
		 * +adoxxDecisionTables.get(0).getRows().get(i).getOutput().get(k));
		 * 
		 * } }
		 */
		return new int[] { numb_model, adoxxDecisionTables.size() }; // models,instances,decision
																		// tables
	}

	public void patchXML(String path_file, boolean offline) {// this method
																// remove the
																// DOCTYPE tag
																// from the xml
																// file and
																// re-save the
																// file without
																// it
		ArrayList<String> patched_file = new ArrayList<String>();
		String line = null;
		FileReader reader = null;
		Scanner scanner = null;
		try {
			if (offline) {
				reader = new FileReader(path_file);
				scanner = new Scanner(reader);
			} else {
				URL url = new URL(test.windowPrefs.getTxtWebADOxx().getText());
				scanner = new Scanner(url.openStream());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());

		}

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			if (!line.startsWith("<!DOCTYPE")) {
				patched_file.add(line);
			}
		}
		// delete the old file
		scanner.close();

		// create the new file patched
		PrintWriter writer = null;
		try {
			writer = new PrintWriter(test.ui.getTxtOutputDirectory().getText() + File.separator + temp_file, "UTF-8");
			for (int i = 0; i < patched_file.size(); i++) {
				writer.println(patched_file.get(i));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			;
		}
		writer.close();
	}

	private String[] parseAttributeName(String attribute_line) { // this method
																	// parse the
																	// name-value
																	// of an
																	// Ontology
																	// attribute
		attribute_line.replaceAll(";", "").trim(); // we remove the final ";"
		String[] arraySplittate = attribute_line.trim().split(" ");
		String temp_value = "";
		for (int i = 1; i < arraySplittate.length; i++) {
			temp_value = temp_value + " " + arraySplittate[i].trim();
		}
		return new String[] { arraySplittate[0], temp_value };
	}

	public void printDecisionTables() {
		for (int i = 0; i < adoxxDecisionTables.size(); i++) {
			System.out.print(adoxxDecisionTables.get(i).getName() + " - " + "Hit Policy: "
					+ adoxxDecisionTables.get(i).getHit_policy() + " - "
					+ adoxxDecisionTables.get(i).getAggregation_indicator() + " with "
					+ adoxxDecisionTables.get(i).getOutput_names().size() + " outputs and priority "
					+ adoxxDecisionTables.get(i).getPriority());

			System.out.println();
			for (int j = 0; j < adoxxDecisionTables.get(i).getRows().size(); j++) {
				System.out.println(adoxxDecisionTables.get(i).getRows().get(j).getNum_rule() + "   |   "
						+ adoxxDecisionTables.get(i).getRows().get(j).getInput() + "   |   "
						+ adoxxDecisionTables.get(i).getRows().get(j).getOutput());
			}

			System.out.println("===================");

		}
	}

	public void executeDecisionTables() {
		writer_status = null;
		PrintWriter writer = null;
		try {
			if (test.windowPrefs.getChkRenameTheOutput().isSelected()) {
				writer = new PrintWriter(test.ui.getTxtOutputDirectory().getText() + File.separator + output_file
						+ System.currentTimeMillis() + ".txt", "UTF-8");
				writer_status = new PrintWriter(test.ui.getTxtOutputDirectory().getText() + File.separator + status_file
						+ System.currentTimeMillis() + ".txt", "UTF-8");
			} else {
				writer = new PrintWriter(
						test.ui.getTxtOutputDirectory().getText() + File.separator + output_file + ".txt", "UTF-8");
				writer_status = new PrintWriter(
						test.ui.getTxtOutputDirectory().getText() + File.separator + status_file + ".txt", "UTF-8");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
			;
		}
		// SORTING DECISION TABLES
		ArrayList<ADOxxDecisionTable> sorted_DT = new ArrayList<ADOxxDecisionTable>();
		while (adoxxDecisionTables.size() != 0) {
			int id = -100;
			int min = 1000;
			for (int h = 0; h < adoxxDecisionTables.size(); h++) {
				if (adoxxDecisionTables.get(h).getPriority() < min) {
					id = h;
					min = adoxxDecisionTables.get(h).getPriority();
				}
			}
			sorted_DT.add(adoxxDecisionTables.get(id));
			adoxxDecisionTables.remove(id);
		}

		adoxxDecisionTables = sorted_DT;

		for (int i = 0; i < adoxxDecisionTables.size(); i++) {
			ADOxxDecisionTable DT = adoxxDecisionTables.get(i);
			variables = new ArrayList<VariableMatch>();
			ArrayList<ADOxxDecisionTableNormalizedEntry> temp_input_entries = normalizeInputEntries(
					DT.getInput_names());
			ArrayList<ADOxxDecisionTableNormalizedEntry> temp_output_entries = normalizeInputEntries(
					DT.getOutput_names());
			
			/*
			 //TEST FOR NORMALIZED INPUT ENTRY
			for (int k = 0; k < temp_input_entries.size();k++){
				printNormalizedEntry(temp_input_entries.get(k));
				System.out.println("-------------");
			}
			for (int k = 0; k < temp_output_entries.size(); k++){
				printNormalizedEntry(temp_output_entries.get(k));
				System.out.println("-------------");
			}
			*/
			
			
			// INSTANTIATE VARIABLES OF CLASSES
			for (int j = 0; j < temp_input_entries.size(); j++) {
				if (!temp_input_entries.get(j).getObject_name().startsWith("?")
						&& !variableIsInList(temp_input_entries.get(j).getObject_name())) {
					variables.add(new VariableMatch(temp_input_entries.get(j).getObject_name(),
							getClassFromString(temp_input_entries.get(j).getObject_name())));
				}
			}
			// UPDATING THE CLASS VARIABLE ON NORMALIZED INPUT
			for (int j = 0; j < temp_input_entries.size(); j++) {
				for (int k = 0; k < variables.size(); k++) {
					if (temp_input_entries.get(j).getObject_name().equals(variables.get(k).getVariableName())) {
						temp_input_entries.get(j).setObject_name("?" + temp_input_entries.get(j).getObject_name());
					}
					
					if (temp_input_entries.get(j).getDest_name().equals(variables.get(k).getVariableName()) ) {
						temp_input_entries.get(j).setDest_name("?" + temp_input_entries.get(j).getDest_name());
					}
					
				}
			}
			// UPDATING THE CLASS VARIABLE ON NORMALIZED OUTPUT
			for (int j = 0; j < temp_output_entries.size(); j++) {
				for (int k = 0; k < variables.size(); k++) {
					if (temp_output_entries.get(j).getObject_name().equals(variables.get(k).getVariableName())) {
						temp_output_entries.get(j).setObject_name("?" + temp_output_entries.get(j).getObject_name());
					}
					if (temp_output_entries.get(j).getDest_name().equals(variables.get(k).getVariableName())) {
						temp_output_entries.get(j).setDest_name("?" + temp_output_entries.get(j).getDest_name());
					}
				}
			}
			// UPDATING THE CLASS VARIABLE ON OUTPUT
			for (int j = 0; j < DT.getOutput_names().size(); j++) {
				for (int k = 0; k < variables.size(); k++) {
					if (DT.getOutput_names().get(j).getObject_name().equals(variables.get(k).getVariableName())) {
						DT.getOutput_names().get(j).setObject_name("?" + DT.getOutput_names().get(j).getObject_name());
					}
					if (DT.getOutput_names().get(j).getDest_name().equals(variables.get(k).getVariableName())) {
						DT.getOutput_names().get(j).setDest_name("?" + DT.getOutput_names().get(j).getDest_name());
					}
				}
			}
	
		
			if (DT.getHit_policy().equals("Single Hit Unique") ||
					//DT.getHit_policy().equals("Single Hit First") ||
					DT.getHit_policy().equals("Single Hit Any")) {
				// ***********************
				// 1 - SINGLE HIT UNIQUE
				// ***********************
				
				// ***********************
				// 2 - SINGLE HIT ANY
				// ***********************
				if (DT.getHit_policy().equals("Single Hit Any")){
					writer_status.println("WARNING: Cannot check the consistency for the Decision Table \""
							+ adoxxDecisionTables.get(i).getName() + "\" with the Hit Policy \"Single Hit Any\" (A)");
				}
				
				
				String alternative_rule_index = "";
				for (int l = 0; l < DT.getRows().size(); l++) {
					boolean null_value = true;
					for (int k = 0; k < DT.getRows().get(l).getInput().size(); k++) {
						
						if (!DT.getRows().get(l).getInput().get(k).equals("-")) {
							null_value = false;
						}
					}
					if (null_value) {
						alternative_rule_index = l + "";
					}
				}
				
				
				
				for (int j = 0; j < temp_output_entries.size(); j++) {
					
					// PARSING ALL THE OUTPUT
					writer.println("CONSTRUCT {");

					if (DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name()
							.startsWith("?")) {
						writer.print(
								DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name());
					} else {
						for (int x = 0; x < variables.size(); x++) {
							if (variables.get(x).getOntologyItem().getNameWithoutPrefix().equals(DT.getOutput_names()
									.get(temp_output_entries.get(j).getNum_entry()).getObject_name())) {
								writer.print(variables.get(x).getVariableName());
							}
						}
					}
					// HERE THERE IS THE LIMITATION OF THE ONE PROPERTY ON THE
					// OUTPUT ENTRY
					writer.print(" "
							+ getPropertyFromString(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
									.getProperties().get(0)).getName()
							+ " " + DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getDest_name());
					if (temp_output_entries.get(j).getNum_entry() != DT.getOutput_names().size() - 1) {
						writer.println(" . ");
					}
					writer.println("}");
					writer.println("WHERE {");

					// DECLARATIONS OF CLASSES
					for (int b = 0; b < variables.size(); b++) {
						writer.println("?" + variables.get(b).getVariableName() + " rdf:type "
								+ variables.get(b).getOntologyItem().getName() + " . ");
					}
					// INSTANCE OF INPUT ALL RELATIONS
					for (int y = 0; y < temp_input_entries.size(); y++) {
						if (temp_input_entries.get(y).getIsARelation()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(y).getObject_name() + " "
									+ getPropertyFromString(temp_input_entries.get(y).getProperty()).getName() + " "
									+ temp_input_entries.get(y).getDest_name() + "}");
							writer.println(" . ");
						}
					}
					// INSTANCE OF INPUT THAT ARE NOT A RELATION
					for (int c = 0; c < temp_input_entries.size(); c++) {
						if (temp_input_entries.get(c).getHaveValue() && !temp_input_entries.get(c).getIsARelation() && !temp_input_entries.get(c).getIsAnInstance()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(c).getObject_name() + " ");
							if (temp_input_entries.get(c).getProperty().equals("label") ) {
								writer.print("rdfs:label");
							} else {
								writer.print(getPropertyFromString(temp_input_entries.get(c).getProperty()).getName());
							}
							writer.print(" " + temp_input_entries.get(c).getDest_name() + "}");
							writer.println(" . ");
						}

					}

					// DECLARATIONS OF OUTPUT

					// IF THE OUTPUT IS NOT A DATATYPE, WE NEED TO DEFINE THE
					// LABEL OF THE DESTINATION OBJECT

					if (temp_output_entries.get(j).getProperty().equals("label")) {
						writer.print("OPTIONAL {" + temp_output_entries.get(j).getObject_name() + " " + "rdfs:label"
								+ " " + temp_output_entries.get(j).getDest_name() + "}");
						if (j != temp_output_entries.size() - 1) {
							writer.println(" . ");
						}
					}

					writer.println("BIND(");
					int num_input = 0;
					if (temp_output_entries.get(j).getHaveValue()) {
						// GETTING ALL THE ROWS OF THE DT
						
						for (int k = 0; k < DT.getRows().size(); k++) {
								
							if (!("" + k).equals(alternative_rule_index)) {

								writer.print("IF (");
								num_input++;
								boolean firstInput = true;
								// PARSING ALL THE EFFECTIVE INPUTS
								for (int l = 0; l < temp_input_entries.size(); l++) {

									if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
											.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
										if (!firstInput) {
											writer.print(" && ");
										}

										// WRITING THE DESTINATION INPUT
										// CRITERIA
										writer.print(temp_input_entries.get(l).getDest_name());
										// WRITING THE OPERATOR OF INPUT
										// CRITERIA
										String[] arraySplittate = DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).split(" ");
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is parsing a number/date
											// operator
											writer.print(arraySplittate[0]);
										} else {
											// Here is parsing a non number/date
											// operator
											writer.print(" = ");
										}
										// WRITING THE INPUT DATA CRITERIA
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is writing a date/number
											// value
											writer.print(arraySplittate[1]);
										} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
											writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry())).getName());
										} else {
											// Here is writing a "string" or
											// value
											writer.print(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()));
										}

										firstInput = false;

									}
								} // for every input

								// WRITING THE OUTPUT FOR THE ROW
								writer.print(", ");
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-")) {
									// IF THE OUTPUT NAME IS DIFFERENT FROM "-"
									// HERE THERE IS THE LIMIT OF 1 PROPERTY IN
									// THE OUTPUT NAME
									if (getPropertyFromString(DT.getOutput_names()
											.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
													.isADataTypeProperty()) {
										// Here it is expecting to find an
										// instance as output
										writer.print(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry()));
									} else {
										// Here it is expeecting to find a
										// datatype value as output
										writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry())).getName());
									}
								} else {
									// IF THE OUTPUT IS "-"
									writer.print("\"\"");
								}

								writer.println(", ");
							} // end if output is not "-"
							if (k == DT.getRows().size() - 1) {
								if(!alternative_rule_index.equals("")){
									writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index))
											.getOutput().get(temp_output_entries.get(j).getNum_entry()));
								}else {
									writer.print("\"\"");
								}
								
								for (int z = 0; z < num_input; z++) {
									writer.print(")");
								}
								writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
								writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");

							}

						} // for every row

					}

					writer.println("}");
					writer.println("");

				} // end output
				

				variables.clear();

			} else if (DT.getHit_policy().equals("Single Hit Priority")) {
				// ***********************
				// 3 - SINGLE HIT PRIORITY
				// ***********************
				
				for (int j = 0; j < temp_output_entries.size(); j++) {
					//===== START THE ORDERING OF ROWS
					ArrayList<ADOxxDecisionTableRow> ordered_rows = new ArrayList<ADOxxDecisionTableRow>();
					String temp_output_values_array[] = DT.getOutput_values().get(temp_output_entries.get(j).getNum_entry()).split(",");
					while (DT.getRows().size()>0){
					for (int k = 0; k < temp_output_values_array.length; k++){
						Boolean found = false;
						while (found == false){
						for (int l = DT.getRows().size()-1; l >= 0;  l--){
							//Finding the rows and adding them to an ordered list
							
							if (DT.getRows().get(l).getOutput().get(temp_output_entries.get(j).getNum_entry()).equals(temp_output_values_array[k].trim())){
								ordered_rows.add(DT.getRows().get(l));
								DT.getRows().remove(l);
								found = true;
								
							}
							
						}
						}
						
						
					
						
					}
					if (DT.getRows().size()>0){
						for (int l = DT.getRows().size()-1; l >= 0; l--){
							//adding the remainders rows
							ordered_rows.add(DT.getRows().get(l));
							DT.getRows().remove(l);
							
						}	
						}
						
					}
					
					
					DT.setRows(ordered_rows);
					
					//===== FINISH THE ORDERING OF ROWS
					
					int numberOfInputValues = 0; //THIS NUMBER DEFINES HOW MANY INPUT COLUMNS WITH VALUES ARE IN A TABLE
					for (int l = 0; l < temp_input_entries.size(); l++){
						if (temp_input_entries.get(l).getHaveValue() == true){
							numberOfInputValues++;
						}
					}
					
					String alternative_rule_index = "";
					for (int l = 0; l < DT.getRows().size(); l++) {
						boolean null_value = true;
						for (int k = 0; k < DT.getRows().get(l).getInput().size(); k++) {
							
							if (!DT.getRows().get(l).getInput().get(k).equals("-")) {
								null_value = false;
							}
						}
						if (null_value) {
							alternative_rule_index = l + "";
						}
					}
					
					
					// PARSING ALL THE OUTPUT
					writer.println("CONSTRUCT {");

					if (DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name()
							.startsWith("?")) {
						writer.print(
								DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name());
					} else {
						for (int x = 0; x < variables.size(); x++) {
							if (variables.get(x).getOntologyItem().getNameWithoutPrefix().equals(DT.getOutput_names()
									.get(temp_output_entries.get(j).getNum_entry()).getObject_name())) {
								writer.print(variables.get(x).getVariableName());
							}
						}
					}
					// HERE THERE IS THE LIMITATION OF THE ONE PROPERTY ON THE
					// OUTPUT ENTRY
					writer.print(" "
							+ getPropertyFromString(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
									.getProperties().get(0)).getName()
							+ " " + DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getDest_name());
					if (temp_output_entries.get(j).getNum_entry() != DT.getOutput_names().size() - 1) {
						writer.println(" . ");
					}
					writer.println("}");
					writer.println("WHERE {");

					// DECLARATIONS OF CLASSES
					for (int b = 0; b < variables.size(); b++) {
						writer.println("?" + variables.get(b).getVariableName() + " rdf:type "
								+ variables.get(b).getOntologyItem().getName() + " . ");
					}
					// INSTANCE OF INPUT ALL RELATIONS
					for (int y = 0; y < temp_input_entries.size(); y++) {
						if (temp_input_entries.get(y).getIsARelation()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(y).getObject_name() + " "
									+ getPropertyFromString(temp_input_entries.get(y).getProperty()).getName() + " "
									+ temp_input_entries.get(y).getDest_name() + "}");
							writer.println(" . ");
						}
					}
					// INSTANCE OF INPUT THAT ARE NOT A RELATION
					for (int c = 0; c < temp_input_entries.size(); c++) {
						if (temp_input_entries.get(c).getHaveValue() && !temp_input_entries.get(c).getIsARelation() && !temp_input_entries.get(c).getIsAnInstance()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(c).getObject_name() + " ");
							if (temp_input_entries.get(c).getProperty().equals("label")) {
								writer.print("rdfs:label");
							} else {
								writer.print(getPropertyFromString(temp_input_entries.get(c).getProperty()).getName());
							}
							writer.print(" " + temp_input_entries.get(c).getDest_name() + "}");
							writer.println(" . ");
						}

					}

					// DECLARATIONS OF OUTPUT

					// IF THE OUTPUT IS NOT A DATATYPE, WE NEED TO DEFINE THE
					// LABEL OF THE DESTINATION OBJECT

					if (temp_output_entries.get(j).getProperty().equals("label")) {
						writer.print("OPTIONAL {" + temp_output_entries.get(j).getObject_name() + " " + "rdfs:label"
								+ " " + temp_output_entries.get(j).getDest_name() + "}");
						if (j != temp_output_entries.size() - 1) {
							writer.println(" . ");
						}
					}

					writer.println("BIND(");
					int num_input = 0;
					if (temp_output_entries.get(j).getHaveValue()) {
						// GETTING ALL THE ROWS OF THE DT
						
						for (int k = 0; k < DT.getRows().size(); k++) {
								
							if (!("" + k).equals(alternative_rule_index)) {

								writer.print("IF (");
								num_input++;
								boolean firstInput = true;
								// PARSING ALL THE EFFECTIVE INPUTS
								for (int l = 0; l < temp_input_entries.size(); l++) {

									if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
											.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
										if (!firstInput) {
											writer.print(" && ");
										}

										// WRITING THE DESTINATION INPUT
										// CRITERIA
										writer.print(temp_input_entries.get(l).getDest_name());
										// WRITING THE OPERATOR OF INPUT
										// CRITERIA
										String[] arraySplittate = DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).split(" ");
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is parsing a number/date
											// operator
											writer.print(arraySplittate[0]);
										} else {
											// Here is parsing a non number/date
											// operator
											writer.print(" = ");
										}
										// WRITING THE INPUT DATA CRITERIA
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is writing a date/number
											// value
											writer.print(arraySplittate[1]);
										} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
											writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry())).getName());
										} else {
											// Here is writing a "string" or
											// value
											writer.print(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()));
										}

										firstInput = false;

									}
								} // for every input

								// WRITING THE OUTPUT FOR THE ROW
								writer.print(", ");
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-")) {
									// IF THE OUTPUT NAME IS DIFFERENT FROM "-"
									// HERE THERE IS THE LIMIT OF 1 PROPERTY IN
									// THE OUTPUT NAME
									if (getPropertyFromString(DT.getOutput_names()
											.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
													.isADataTypeProperty()) {
										// Here it is expecting to find an
										// instance as output
										writer.print(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry()));
									} else {
										// Here it is expeecting to find a
										// datatype value as output
										writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry())).getName());
									}
								} else {
									// IF THE OUTPUT IS "-"
									writer.print("\"\"");
								}

								writer.println(", ");
							} // end if output is not "-"
							if (k == DT.getRows().size() - 1) {
								if(!alternative_rule_index.equals("")){
									writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index))
											.getOutput().get(temp_output_entries.get(j).getNum_entry()));
								}else {
									writer.print("\"\"");
								}
								
								for (int z = 0; z < num_input; z++) {
									writer.print(")");
								}
								writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
								writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");

							}

						} // for every row

					}

					writer.println("}");
					writer.println("");

				} // end output
				

				variables.clear();
			} else if (DT.getHit_policy().equals("Single Hit First")) {
				// ***********************
				// 4 - SINGLE HIT FIRST
				// ***********************
				ArrayList<ADOxxDecisionTableRow> sorted_rows = new ArrayList<ADOxxDecisionTableRow>();
				while (DT.getRows().size() != 0) {
					int id = -100;
					int min = 1000;
					for (int h = 0; h < DT.getRows().size(); h++) {
						if (DT.getRows().get(h).getNum_rule() < min) {
							id = h;
							min = DT.getRows().get(h).getNum_rule();
						}
					}
					sorted_rows.add(DT.getRows().get(id));
					DT.getRows().remove(id);
				}

				DT.setRows(sorted_rows);
				
				
				
				
				
				

				for (int j = 0; j < temp_output_entries.size(); j++) {
					int numberOfInputValues = 0; //THIS NUMBER DEFINES HOW MANY INPUT COLUMNS WITH VALUES ARE IN A TABLE
					for (int l = 0; l < temp_input_entries.size(); l++){
						if (temp_input_entries.get(l).getHaveValue() == true){
							numberOfInputValues++;
						}
					}
					
					String alternative_rule_index = "";
					for (int l = 0; l < DT.getRows().size(); l++) {
						boolean null_value = true;
						for (int k = 0; k < DT.getRows().get(l).getInput().size(); k++) {
							
							if (!DT.getRows().get(l).getInput().get(k).equals("-")) {
								null_value = false;
							}
						}
						if (null_value) {
							alternative_rule_index = l + "";
						}
					}
					
					
					// PARSING ALL THE OUTPUT
					writer.println("CONSTRUCT {");

					if (DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name()
							.startsWith("?")) {
						writer.print(
								DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name());
					} else {
						for (int x = 0; x < variables.size(); x++) {
							if (variables.get(x).getOntologyItem().getNameWithoutPrefix().equals(DT.getOutput_names()
									.get(temp_output_entries.get(j).getNum_entry()).getObject_name())) {
								writer.print(variables.get(x).getVariableName());
							}
						}
					}
					// HERE THERE IS THE LIMITATION OF THE ONE PROPERTY ON THE
					// OUTPUT ENTRY
					writer.print(" "
							+ getPropertyFromString(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
									.getProperties().get(0)).getName()
							+ " " + DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getDest_name());
					if (temp_output_entries.get(j).getNum_entry() != DT.getOutput_names().size() - 1) {
						writer.println(" . ");
					}
					writer.println("}");
					writer.println("WHERE {");

					// DECLARATIONS OF CLASSES
					for (int b = 0; b < variables.size(); b++) {
						writer.println("?" + variables.get(b).getVariableName() + " rdf:type "
								+ variables.get(b).getOntologyItem().getName() + " . ");
					}
					// INSTANCE OF INPUT ALL RELATIONS
					for (int y = 0; y < temp_input_entries.size(); y++) {
						if (temp_input_entries.get(y).getIsARelation()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(y).getObject_name() + " "
									+ getPropertyFromString(temp_input_entries.get(y).getProperty()).getName() + " "
									+ temp_input_entries.get(y).getDest_name() + "}");
							writer.println(" . ");
						}
					}
					// INSTANCE OF INPUT THAT ARE NOT A RELATION
					for (int c = 0; c < temp_input_entries.size(); c++) {
						if (temp_input_entries.get(c).getHaveValue() && !temp_input_entries.get(c).getIsARelation()&& !temp_input_entries.get(c).getIsAnInstance()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(c).getObject_name() + " ");
							if (temp_input_entries.get(c).getProperty().equals("label")) {
								writer.print("rdfs:label");
							} else {
								writer.print(getPropertyFromString(temp_input_entries.get(c).getProperty()).getName());
							}
							writer.print(" " + temp_input_entries.get(c).getDest_name() + "}");
							writer.println(" . ");
						}

					}

					// DECLARATIONS OF OUTPUT

					// IF THE OUTPUT IS NOT A DATATYPE, WE NEED TO DEFINE THE
					// LABEL OF THE DESTINATION OBJECT

					if (temp_output_entries.get(j).getProperty().equals("label")) {
						writer.print("OPTIONAL {" + temp_output_entries.get(j).getObject_name() + " " + "rdfs:label"
								+ " " + temp_output_entries.get(j).getDest_name() + "}");
						if (j != temp_output_entries.size() - 1) {
							writer.println(" . ");
						}
					}

					writer.println("BIND(");
					int num_input = 0;
					if (temp_output_entries.get(j).getHaveValue()) {
						// GETTING ALL THE ROWS OF THE DT
						
						for (int k = 0; k < DT.getRows().size(); k++) {
								
							if (!("" + k).equals(alternative_rule_index)) {

								writer.print("IF (");
								num_input++;
								boolean firstInput = true;
								// PARSING ALL THE EFFECTIVE INPUTS
								for (int l = 0; l < temp_input_entries.size(); l++) {

									if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
											.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
										if (!firstInput) {
											writer.print(" && ");
										}

										// WRITING THE DESTINATION INPUT
										// CRITERIA
										writer.print(temp_input_entries.get(l).getDest_name());
										// WRITING THE OPERATOR OF INPUT
										// CRITERIA
										String[] arraySplittate = DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).split(" ");
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is parsing a number/date
											// operator
											writer.print(arraySplittate[0]);
										} else {
											// Here is parsing a non number/date
											// operator
											writer.print(" = ");
										}
										// WRITING THE INPUT DATA CRITERIA
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is writing a date/number
											// value
											writer.print(arraySplittate[1]);
										} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
											writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry())).getName());
										} else {
											// Here is writing a "string" or
											// value
											writer.print(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()));
										}

										firstInput = false;

									}
								} // for every input

								// WRITING THE OUTPUT FOR THE ROW
								writer.print(", ");
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-")) {
									// IF THE OUTPUT NAME IS DIFFERENT FROM "-"
									// HERE THERE IS THE LIMIT OF 1 PROPERTY IN
									// THE OUTPUT NAME
									if (getPropertyFromString(DT.getOutput_names()
											.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
													.isADataTypeProperty()) {
										// Here it is expecting to find an
										// instance as output
										writer.print(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry()));
									} else {
										// Here it is expeecting to find a
										// datatype value as output
										writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry())).getName());
									}
								} else {
									// IF THE OUTPUT IS "-"
									writer.print("\"\"");
								}

								writer.println(", ");
							} // end if output is not "-"
							if (k == DT.getRows().size() - 1) {
								if(!alternative_rule_index.equals("")){
									writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index))
											.getOutput().get(temp_output_entries.get(j).getNum_entry()));
								}else {
									writer.print("\"\"");
								}
								
								for (int z = 0; z < num_input; z++) {
									writer.print(")");
								}
								writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
								writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");

							}

						} // for every row

					}

					writer.println("}");
					writer.println("");

				} // end output
				

				variables.clear();
			
			} else if (DT.getHit_policy().equals("Multiple Hit Output Order")) {
				// ***********************
				// 6 - MULTIPLE HIT OUTPUT ORDER
				// ***********************
				

				for (int j = 0; j < temp_output_entries.size(); j++) {
					// GETTING ALL THE ROWS OF THE DT
					
					//===== START THE ORDERING OF ROWS
					ArrayList<ADOxxDecisionTableRow> ordered_rows = new ArrayList<ADOxxDecisionTableRow>();
					String temp_output_values_array[] = DT.getOutput_values().get(temp_output_entries.get(j).getNum_entry()).split(",");
					while (DT.getRows().size()>0){
					for (int k = 0; k < temp_output_values_array.length; k++){
						Boolean found = false;
						while (found == false){
						for (int l = DT.getRows().size()-1; l >= 0;  l--){
							//Finding the rows and adding them to an ordered list
							
							if (DT.getRows().get(l).getOutput().get(temp_output_entries.get(j).getNum_entry()).equals(temp_output_values_array[k].trim())){
								ordered_rows.add(DT.getRows().get(l));
								DT.getRows().remove(l);
								found = true;
								
							}
							
						}
						}
						
						
					}
					if (DT.getRows().size()>0){
						for (int l = DT.getRows().size()-1; l >= 0; l--){
							//adding the remainders rows
							ordered_rows.add(DT.getRows().get(l));
							DT.getRows().remove(l);
							
						}	
						}
						
					}
					
					
					DT.setRows(ordered_rows);
					
					//===== FINISH THE ORDERING OF ROWS
					
					for (int k = 0; k < DT.getRows().size(); k++) {
						// PARSING ALL THE OUTPUT
						writer.println("CONSTRUCT {");

						if (DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name()
								.startsWith("?")) {
							writer.print(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
									.getObject_name());
						} else {
							for (int x = 0; x < variables.size(); x++) {
								if (variables.get(x).getOntologyItem().getNameWithoutPrefix()
										.equals(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
												.getObject_name())) {
									writer.print(variables.get(x).getVariableName());
								}
							}
						}
						// HERE THERE IS THE LIMITATION OF THE ONE PROPERTY ON
						// THE OUTPUT ENTRY
						writer.print(" "
								+ getPropertyFromString(DT.getOutput_names()
										.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
												.getName()
								+ " "
								+ DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getDest_name());
						if (temp_output_entries.get(j).getNum_entry() != DT.getOutput_names().size() - 1) {
							writer.println(" . ");
						}
						writer.println("}");
						writer.println("WHERE {");

						// DECLARATIONS OF CLASSES
						for (int b = 0; b < variables.size(); b++) {
							writer.println("?" + variables.get(b).getVariableName() + " rdf:type "
									+ variables.get(b).getOntologyItem().getName() + " . ");
						}
						// INSTANCE OF INPUT ALL RELATIONS
						for (int y = 0; y < temp_input_entries.size(); y++) {
							if (temp_input_entries.get(y).getIsARelation()) {
								writer.print("OPTIONAL {" + temp_input_entries.get(y).getObject_name() + " "
										+ getPropertyFromString(temp_input_entries.get(y).getProperty()).getName() + " "
										+ temp_input_entries.get(y).getDest_name() + "}");
								writer.println(" . ");
							}
						}
						// INSTANCE OF INPUT THAT ARE NOT A RELATION
						for (int c = 0; c < temp_input_entries.size(); c++) {
							if (temp_input_entries.get(c).getHaveValue()
									&& !temp_input_entries.get(c).getIsARelation() && !temp_input_entries.get(c).getIsAnInstance()) {
								writer.print("OPTIONAL {" + temp_input_entries.get(c).getObject_name() + " ");
								if (temp_input_entries.get(c).getProperty().equals("label")) {
									writer.print("rdfs:label");
								} else {
									writer.print(
											getPropertyFromString(temp_input_entries.get(c).getProperty()).getName());
								}
								writer.print(" " + temp_input_entries.get(c).getDest_name() + "}");
								writer.println(" . ");
							}

						}

						// DECLARATIONS OF OUTPUT

						// IF THE OUTPUT IS NOT A DATATYPE, WE NEED TO DEFINE
						// THE LABEL OF THE DESTINATION OBJECT

						if (temp_output_entries.get(j).getProperty().equals("label")) {
							writer.print("OPTIONAL {" + temp_output_entries.get(j).getObject_name() + " " + "rdfs:label"
									+ " " + temp_output_entries.get(j).getDest_name() + "}");
							if (j != temp_output_entries.size() - 1) {
								writer.println(" . ");
							}
						}

						writer.println("BIND(");
						// int num_input = 0;
						if (temp_output_entries.get(j).getHaveValue()) {

							// IF THE OUTPUT IS DIFFERENT FROM "-"
							if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
									.equals("-")) {
								writer.print("IF (");
								// num_input++;
								boolean firstInput = true;
								// PARSING ALL THE EFFECTIVE INPUTS
								for (int l = 0; l < temp_input_entries.size(); l++) {

									if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
											.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
										if (!firstInput) {
											writer.print(" && ");
										}

										// WRITING THE DESTINATION INPUT
										// CRITERIA
										writer.print(temp_input_entries.get(l).getDest_name());
										// WRITING THE OPERATOR OF INPUT
										// CRITERIA
										String[] arraySplittate = DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).split(" ");
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is parsing a number/date
											// operator
											writer.print(arraySplittate[0]);
										} else {
											// Here is parsing a non number/date
											// operator
											writer.print(" = ");
										}
										// WRITING THE INPUT DATA CRITERIA
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is writing a date/number
											// value
											writer.print(arraySplittate[1]);
										} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
											writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry())).getName());
										} else {
											// Here is writing a "string" or
											// value
											writer.print(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()));
										}

										firstInput = false;

									}
								} // for every input

								// WRITING THE OUTPUT FOR THE ROW
								writer.print(", ");
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-")) {
									// IF THE OUTPUT NAME IS DIFFERENT FROM "-"
									// HERE THERE IS THE LIMIT OF 1 PROPERTY IN
									// THE OUTPUT NAME
									if (getPropertyFromString(DT.getOutput_names()
											.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
													.isADataTypeProperty()) {
										// Here it is expecting to find an
										// instance as output
										writer.print(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry()));
									} else {
										// Here it is expeecting to find a
										// datatype value as output
										writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry())).getName());
									}
								} else {
									// IF THE OUTPUT IS "-"
									writer.print("\"\"");
								}

								writer.println(", ");
							} // end if output is not "-"

							writer.print("\"\"");
							writer.print(")");
							writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
							writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");
						}
						writer.println("}");
						writer.println("");
					} // end rows

				} // end output
				

				variables.clear();
			} else if (DT.getHit_policy().equals("Multiple Hit Rule Order")) {
				// ***********************
				// 7 - MULTIPLE HIT RULE ORDER
				// ***********************

				ArrayList<ADOxxDecisionTableRow> sorted_rows = new ArrayList<ADOxxDecisionTableRow>();
				while (DT.getRows().size() != 0) {
					int id = -100;
					int min = 1000;
					for (int h = 0; h < DT.getRows().size(); h++) {
						if (DT.getRows().get(h).getNum_rule() < min) {
							id = h;
							min = DT.getRows().get(h).getNum_rule();
						}
					}
					sorted_rows.add(DT.getRows().get(id));
					DT.getRows().remove(id);
				}

				DT.setRows(sorted_rows);
				// GETTING ALL THE ROWS OF THE DT
				for (int k = 0; k < DT.getRows().size(); k++) {
					// PARSING ALL THE OUTPUT
					for (int j = 0; j < temp_output_entries.size(); j++) {

						writer.println("CONSTRUCT {");

						if (DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name()
								.startsWith("?")) {
							writer.print(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
									.getObject_name());
						} else {
							for (int x = 0; x < variables.size(); x++) {
								if (variables.get(x).getOntologyItem().getNameWithoutPrefix()
										.equals(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
												.getObject_name())) {
									writer.print(variables.get(x).getVariableName());
								}
							}
						}
						// HERE THERE IS THE LIMITATION OF THE ONE PROPERTY ON
						// THE OUTPUT ENTRY
						writer.print(" "
								+ getPropertyFromString(DT.getOutput_names()
										.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
												.getName()
								+ " "
								+ DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getDest_name());
						if (temp_output_entries.get(j).getNum_entry() != DT.getOutput_names().size() - 1) {
							writer.println(" . ");
						}
						writer.println("}");
						writer.println("WHERE {");

						// DECLARATIONS OF CLASSES
						for (int b = 0; b < variables.size(); b++) {
							writer.println("?" + variables.get(b).getVariableName() + " rdf:type "
									+ variables.get(b).getOntologyItem().getName() + " . ");
						}
						// INSTANCE OF INPUT ALL RELATIONS
						for (int y = 0; y < temp_input_entries.size(); y++) {
							if (temp_input_entries.get(y).getIsARelation()) {
								writer.print("OPTIONAL {" + temp_input_entries.get(y).getObject_name() + " "
										+ getPropertyFromString(temp_input_entries.get(y).getProperty()).getName() + " "
										+ temp_input_entries.get(y).getDest_name() + "}");
								writer.println(" . ");
							}
						}
						// INSTANCE OF INPUT THAT ARE NOT A RELATION
						for (int c = 0; c < temp_input_entries.size(); c++) {
							if (temp_input_entries.get(c).getHaveValue()
									&& !temp_input_entries.get(c).getIsARelation() && !temp_input_entries.get(c).getIsAnInstance()) {
								writer.print("OPTIONAL {" + temp_input_entries.get(c).getObject_name() + " ");
								if (temp_input_entries.get(c).getProperty().equals("label")) {
									writer.print("rdfs:label");
								} else {
									writer.print(
											getPropertyFromString(temp_input_entries.get(c).getProperty()).getName());
								}
								writer.print(" " + temp_input_entries.get(c).getDest_name() + "}");
								writer.println(" . ");
							}

						}

						// DECLARATIONS OF OUTPUT

						// IF THE OUTPUT IS NOT A DATATYPE, WE NEED TO DEFINE
						// THE LABEL OF THE DESTINATION OBJECT

						if (temp_output_entries.get(j).getProperty().equals("label")) {
							writer.print("OPTIONAL {" + temp_output_entries.get(j).getObject_name() + " " + "rdfs:label"
									+ " " + temp_output_entries.get(j).getDest_name() + "}");
							if (j != temp_output_entries.size() - 1) {
								writer.println(" . ");
							}
						}

						writer.println("BIND(");
						// int num_input = 0;
						if (temp_output_entries.get(j).getHaveValue()) {

							// IF THE OUTPUT IS DIFFERENT FROM "-"
							if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
									.equals("-")) {

								writer.print("IF (");
								// num_input++;
								boolean firstInput = true;
								// PARSING ALL THE EFFECTIVE INPUTS
								for (int l = 0; l < temp_input_entries.size(); l++) {

									if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
											.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
										if (!firstInput) {
											writer.print(" && ");
										}

										// WRITING THE DESTINATION INPUT
										// CRITERIA
										writer.print(temp_input_entries.get(l).getDest_name());
										// WRITING THE OPERATOR OF INPUT
										// CRITERIA
										String[] arraySplittate = DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).split(" ");
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is parsing a number/date
											// operator
											writer.print(arraySplittate[0]);
										} else {
											// Here is parsing a non number/date
											// operator
											writer.print(" = ");
										}
										// WRITING THE INPUT DATA CRITERIA
										if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
												|| arraySplittate[0].startsWith(">")
												|| arraySplittate[0].startsWith("<")) {
											// Here is writing a date/number
											// value
											writer.print(arraySplittate[1]);
										} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
											writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry())).getName());
										} else {
											// Here is writing a "string" or
											// value
											writer.print(DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()));
										}

										firstInput = false;

									}
								} // for every input

								// WRITING THE OUTPUT FOR THE ROW
								writer.print(", ");
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-")) {
									// IF THE OUTPUT NAME IS DIFFERENT FROM "-"
									// HERE THERE IS THE LIMIT OF 1 PROPERTY IN
									// THE OUTPUT NAME
									if (getPropertyFromString(DT.getOutput_names()
											.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
													.isADataTypeProperty()) {
										// Here it is expecting to find an
										// instance as output
										writer.print(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry()));
									} else {
										// Here it is expeecting to find a
										// datatype value as output
										writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
												.get(temp_output_entries.get(j).getNum_entry())).getName());
									}
								} else {
									// IF THE OUTPUT IS "-"
									writer.print("\"\"");
								}

								writer.println(", ");
							} // end if output is not "-"

							writer.print("\"\"");
							writer.print(")");
							writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
							writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");
						}
						writer.println("}");
						writer.println("");
					} // end outputs

				} // end rows
				

				variables.clear();

			} else if (DT.getHit_policy().equals("Multiple Hit Collection")) {
				// ***********************
				// 8 - MULTIPLE HIT COLLECTION
				// ***********************
				int numberOfInputValues = 0; //THIS NUMBER DEFINES HOW MANY INPUT COLUMNS WITH VALUES ARE IN A TABLE
				for (int l = 0; l < temp_input_entries.size(); l++){
					if (temp_input_entries.get(l).getHaveValue() == true){
						numberOfInputValues++;
					}
				}
				
				String alternative_rule_index = "";
				for (int l = 0; l < DT.getRows().size(); l++) {
					boolean null_value = true;
					for (int k = 0; k < DT.getRows().get(l).getInput().size(); k++) {
						System.out.println(DT.getRows().get(l).getInput().get(k));
						if (!DT.getRows().get(l).getInput().get(k).equals("-")) {
							null_value = false;
						}
					}
					if (null_value) {
						alternative_rule_index = l + "";
					}
				}
				

				for (int j = 0; j < temp_output_entries.size(); j++) {
					// PARSING ALL THE OUTPUT
					writer.println("CONSTRUCT {");

					if (DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name()
							.startsWith("?")) {
						writer.print(
								DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getObject_name());
					} else {
						for (int x = 0; x < variables.size(); x++) {
							if (variables.get(x).getOntologyItem().getNameWithoutPrefix().equals(DT.getOutput_names()
									.get(temp_output_entries.get(j).getNum_entry()).getObject_name())) {
								writer.print(variables.get(x).getVariableName());
							}
						}
					}
					// HERE THERE IS THE LIMITATION OF THE ONE PROPERTY ON THE
					// OUTPUT ENTRY
					writer.print(" "
							+ getPropertyFromString(DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry())
									.getProperties().get(0)).getName()
							+ " " + DT.getOutput_names().get(temp_output_entries.get(j).getNum_entry()).getDest_name());
					if (temp_output_entries.get(j).getNum_entry() != DT.getOutput_names().size() - 1) {
						writer.println(" . ");
					}
					writer.println("}");
					writer.println("WHERE {");

					// DECLARATIONS OF CLASSES
					for (int b = 0; b < variables.size(); b++) {
						writer.println("?" + variables.get(b).getVariableName() + " rdf:type "
								+ variables.get(b).getOntologyItem().getName() + " . ");
					}
					// INSTANCE OF INPUT ALL RELATIONS
					for (int y = 0; y < temp_input_entries.size(); y++) {
						if (temp_input_entries.get(y).getIsARelation()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(y).getObject_name() + " "
									+ getPropertyFromString(temp_input_entries.get(y).getProperty()).getName() + " "
									+ temp_input_entries.get(y).getDest_name() + "}");
							writer.println(" . ");
						}
					}
					// INSTANCE OF INPUT THAT ARE NOT A RELATION
					for (int c = 0; c < temp_input_entries.size(); c++) {
						if (temp_input_entries.get(c).getHaveValue() && !temp_input_entries.get(c).getIsARelation() && !temp_input_entries.get(c).getIsAnInstance()) {
							writer.print("OPTIONAL {" + temp_input_entries.get(c).getObject_name() + " ");
							if (temp_input_entries.get(c).getProperty().equals("label")) {
								writer.print("rdfs:label");
							} else {
								writer.print(getPropertyFromString(temp_input_entries.get(c).getProperty()).getName());
							}
							writer.print(" " + temp_input_entries.get(c).getDest_name() + "}");
							writer.println(" . ");
						}

					}

					// DECLARATIONS OF OUTPUT

					// IF THE OUTPUT IS NOT A DATATYPE, WE NEED TO DEFINE THE
					// LABEL OF THE DESTINATION OBJECT

					if (temp_output_entries.get(j).getProperty().equals("label")) {
						writer.print("OPTIONAL {" + temp_output_entries.get(j).getObject_name() + " " + "rdfs:label"
								+ " " + temp_output_entries.get(j).getDest_name() + "}");
						if (j != temp_output_entries.size() - 1) {
							writer.println(" . ");
						}
					}

					if (DT.getAggregation_indicator().equals("Min")) {
						// ===============
						// MIN AGGREGATOR
						// ===============
						int num_input = 0;
						boolean firstRow = true;
						if (temp_output_entries.get(j).getHaveValue()) {
							// GETTING ALL THE ROWS OF THE DT
							for (int k = 0; k < DT.getRows().size(); k++) {
								// IF THE OUTPUT IS DIFFERENT FROM "-"
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-") && !("" + k).equals(alternative_rule_index)) {
									boolean firstInput = true;
									writer.print("BIND(");
									writer.print("IF (");
									num_input++;

									// PARSING ALL THE EFFECTIVE INPUTS
									for (int l = 0; l < temp_input_entries.size(); l++) {

										if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
											if (!firstInput) {
												writer.print(" && ");
											}

											// WRITING THE DESTINATION INPUT
											// CRITERIA
											writer.print(temp_input_entries.get(l).getDest_name());
											// WRITING THE OPERATOR OF INPUT
											// CRITERIA
											String[] arraySplittate = DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()).split(" ");
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is parsing a number/date
												// operator
												writer.print(arraySplittate[0]);
											} else {
												// Here is parsing a non
												// number/date operator
												writer.print(" = ");
											}

											// WRITING THE INPUT DATA CRITERIA
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is writing a date/number
												// value
												writer.print(arraySplittate[1]);
											} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
												writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry())).getName());
											} else {
												// Here is writing a "string" or
												// value
												writer.print(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry()));
											}

											firstInput = false;

										}

									} // end for every input
									if (!firstRow) {
										writer.print(" && ?MIN" + (num_input - 1) + " > " + DT.getRows().get(k)
												.getOutput().get(temp_output_entries.get(j).getNum_entry()));
									}

									// WRITING THE OUTPUT FOR THE ROW IF TRUE
									writer.print(", ");
									if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
											.equals("-")) {
										// IF THE OUTPUT NAME IS DIFFERENT FROM
										// "-"
										// HERE THERE IS THE LIMIT OF 1 PROPERTY
										// IN THE OUTPUT NAME
										if (getPropertyFromString(DT.getOutput_names()
												.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
														.isADataTypeProperty()) {
											// Here it is expecting to find an
											// instance as output
											writer.print(DT.getRows().get(k).getOutput()
													.get(temp_output_entries.get(j).getNum_entry()));
										} else {
											// Here it is expeecting to find a
											// datatype value as output
											writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
													.get(temp_output_entries.get(j).getNum_entry())).getName());
										}
									} else {
										// IF THE OUTPUT IS "-"
										writer.print("\"\"");
									}

									// WRITING THE OUTPUT FOR THE ROW IF FALSE
									writer.print(", ");
									if (firstRow) {
										// HERE I WRITE THE ALTERNATIVE VALUE IF
										// IT EXISTS
										if (!alternative_rule_index.equals("")) {
											writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index))
													.getOutput().get(temp_output_entries.get(j).getNum_entry()));
										} else {
											writer.print("\"\"");
										}

									} else {
										writer.print("?MIN" + (num_input - 1));
									}
									writer.print(")");
									if (k == DT.getRows().size() - 1) {
										writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
									} else {
										writer.println(" AS " + "?MIN" + num_input + ") .");
									}

								} // end if output is not "-"
								firstRow = false;
							} // for every row
							writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");
						}
						writer.println("}");
						writer.println("");
					} else if (DT.getAggregation_indicator().equals("Max")) {
						// ===============
						// MAX AGGREGATOR
						// ===============
						int num_input = 0;
						boolean firstRow = true;
						if (temp_output_entries.get(j).getHaveValue()) {
							// GETTING ALL THE ROWS OF THE DT
							for (int k = 0; k < DT.getRows().size(); k++) {
								// IF THE OUTPUT IS DIFFERENT FROM "-"
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-") && !("" + k).equals(alternative_rule_index)) {
									boolean firstInput = true;
									writer.print("BIND(");
									writer.print("IF (");
									num_input++;

									// PARSING ALL THE EFFECTIVE INPUTS
									for (int l = 0; l < temp_input_entries.size(); l++) {

										if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
											if (!firstInput) {
												writer.print(" && ");
											}

											// WRITING THE DESTINATION INPUT
											// CRITERIA
											writer.print(temp_input_entries.get(l).getDest_name());
											// WRITING THE OPERATOR OF INPUT
											// CRITERIA
											String[] arraySplittate = DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()).split(" ");
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is parsing a number/date
												// operator
												writer.print(arraySplittate[0]);
											} else {
												// Here is parsing a non
												// number/date operator
												writer.print(" = ");
											}

											// WRITING THE INPUT DATA CRITERIA
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is writing a date/number
												// value
												writer.print(arraySplittate[1]);
											} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
												writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry())).getName());
											} else {
												// Here is writing a "string" or
												// value
												writer.print(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry()));
											}

											firstInput = false;

										}

									} // end for every input
									if (!firstRow) {
										writer.print(" && ?MAX" + (num_input - 1) + " < " + DT.getRows().get(k)
												.getOutput().get(temp_output_entries.get(j).getNum_entry()));
									}

									// WRITING THE OUTPUT FOR THE ROW IF TRUE
									writer.print(", ");
									if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
											.equals("-")) {
										// IF THE OUTPUT NAME IS DIFFERENT FROM
										// "-"
										// HERE THERE IS THE LIMIT OF 1 PROPERTY
										// IN THE OUTPUT NAME
										if (getPropertyFromString(DT.getOutput_names()
												.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
														.isADataTypeProperty()) {
											// Here it is expecting to find an
											// instance as output
											writer.print(DT.getRows().get(k).getOutput()
													.get(temp_output_entries.get(j).getNum_entry()));
										} else {
											// Here it is expeecting to find a
											// datatype value as output
											writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
													.get(temp_output_entries.get(j).getNum_entry())).getName());
										}
									} else {
										// IF THE OUTPUT IS "-"
										writer.print("\"\"");
									}

									// WRITING THE OUTPUT FOR THE ROW IF FALSE
									writer.print(", ");
									if (firstRow) {
										// HERE I WRITE THE ALTERNATIVE VALUE IF
										// IT EXISTS
										if (!alternative_rule_index.equals("")) {
											writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index))
													.getOutput().get(temp_output_entries.get(j).getNum_entry()));
										} else {
											writer.print("\"\"");
										}

									} else {
										writer.print("?MAX" + (num_input - 1));
									}
									writer.print(")");
									if (k == DT.getRows().size() - 1) {
										writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
									} else {
										writer.println(" AS " + "?MAX" + num_input + ") .");
									}

								} // end if output is not "-"
								firstRow = false;
							} // for every row
							writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");
						}
						writer.println("}");
						writer.println("");
					} else if (DT.getAggregation_indicator().equals("Sum")) {
						// ===============
						// SUM AGGREGATOR
						// ===============
						int num_input = 0;
						boolean firstRow = true;
						if (temp_output_entries.get(j).getHaveValue()) {
							// GETTING ALL THE ROWS OF THE DT
							for (int k = 0; k < DT.getRows().size(); k++) {
								// IF THE OUTPUT IS DIFFERENT FROM "-"
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-") && !("" + k).equals(alternative_rule_index)) {
									boolean firstInput = true;
									writer.print("BIND(");
									writer.print("IF (");
									num_input++;

									// PARSING ALL THE EFFECTIVE INPUTS
									for (int l = 0; l < temp_input_entries.size(); l++) {

										if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
											if (!firstInput) {
												writer.print(" && ");
											}

											// WRITING THE DESTINATION INPUT
											// CRITERIA
											writer.print(temp_input_entries.get(l).getDest_name());
											// WRITING THE OPERATOR OF INPUT
											// CRITERIA
											String[] arraySplittate = DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()).split(" ");
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is parsing a number/date
												// operator
												writer.print(arraySplittate[0]);
											} else {
												// Here is parsing a non
												// number/date operator
												writer.print(" = ");
											}

											// WRITING THE INPUT DATA CRITERIA
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is writing a date/number
												// value
												writer.print(arraySplittate[1]);
											} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
												writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry())).getName());
											} else {
												// Here is writing a "string" or
												// value
												writer.print(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry()));
											}

											firstInput = false;

										}

									} // end for every input

									// WRITING THE OUTPUT FOR THE ROW IF TRUE
									writer.print(", ");
									if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
											.equals("-")) {
										// IF THE OUTPUT NAME IS DIFFERENT FROM
										// "-"
										// HERE THERE IS THE LIMIT OF 1 PROPERTY
										// IN THE OUTPUT NAME
										if (getPropertyFromString(DT.getOutput_names()
												.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
														.isAnObjectProperty()) {
											// Here it is expecting to find an
											// instance as output
											writer.print(
													"?SUM" + (num_input - 1) + "+"
															+ DT.getRows().get(k).getOutput()
																	.get(temp_output_entries.get(j).getNum_entry()));
										} else {
											if (firstRow) {
												writer.print(DT.getRows().get(k).getOutput()
														.get(temp_output_entries.get(j).getNum_entry()));
											} else {
												writer.print("?SUM" + (num_input - 1) + "+" + DT.getRows().get(k)
														.getOutput().get(temp_output_entries.get(j).getNum_entry()));
											}
											// Here it is expeecting to find a
											// datatype value as output
											// writer.print(DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry()));

										}
									} else {
										// IF THE OUTPUT IS "-"
										writer.print("\"\"");
									}

									// WRITING THE OUTPUT FOR THE ROW IF FALSE
									writer.print(", ");
									if (firstRow) {
										// HERE I WRITE THE ALTERNATIVE VALUE IF
										// IT EXISTS
										if (!alternative_rule_index.equals("")) {
											//THERE IS NO ALTERNATIVE RULE IN THe SUM AGGREGATOR
											// writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index)).getOutput().get(temp_output_entries.get(j).getNum_entry()));
											writer.print("0");
										} else {
											writer.print("0");
										}

									} else {
										writer.print("?SUM" + (num_input - 1));
									}
									writer.print(")");
									if (k == DT.getRows().size() - 1) {
										writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
									} else {
										writer.println(" AS " + "?SUM" + num_input + ") .");
									}

								} // end if output is not "-"
								firstRow = false;
							} // for every row
							//writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");
						}
						writer.println("}");
						writer.println("");
					} else if (DT.getAggregation_indicator().equals("Count")) {
						// ===============
						// COUNT AGGREGATOR
						// ===============
						int num_input = 0;
						boolean firstRow = true;
						if (temp_output_entries.get(j).getHaveValue()) {
							// GETTING ALL THE ROWS OF THE DT
							for (int k = 0; k < DT.getRows().size(); k++) {
								// IF THE OUTPUT IS DIFFERENT FROM "-"
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-") && !("" + k).equals(alternative_rule_index)) {
									boolean firstInput = true;
									writer.print("BIND(");
									writer.print("IF (");
									num_input++;

									// PARSING ALL THE EFFECTIVE INPUTS
									for (int l = 0; l < temp_input_entries.size(); l++) {

										if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
											if (!firstInput) {
												writer.print(" && ");
											}

											// WRITING THE DESTINATION INPUT
											// CRITERIA
											writer.print(temp_input_entries.get(l).getDest_name());
											// WRITING THE OPERATOR OF INPUT
											// CRITERIA
											String[] arraySplittate = DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()).split(" ");
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is parsing a number/date
												// operator
												writer.print(arraySplittate[0]);
											} else {
												// Here is parsing a non
												// number/date operator
												writer.print(" = ");
											}

											// WRITING THE INPUT DATA CRITERIA
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is writing a date/number
												// value
												writer.print(arraySplittate[1]);
											} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
												writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry())).getName());
											} else {
												// Here is writing a "string" or
												// value
												writer.print(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry()));
											}

											firstInput = false;

										}

									} // end for every input

									// WRITING THE OUTPUT FOR THE ROW IF TRUE
									writer.print(", ");
									if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
											.equals("-")) {
										// IF THE OUTPUT NAME IS DIFFERENT FROM
										// "-"
										// HERE THERE IS THE LIMIT OF 1 PROPERTY
										// IN THE OUTPUT NAME
										if (getPropertyFromString(DT.getOutput_names()
												.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
														.isAnObjectProperty()) {
											// Here it is expecting to find an
											// instance as output
											writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
													.get(temp_output_entries.get(j).getNum_entry())).getName());
										} else {
											if (firstRow) {
												writer.print("1");
											} else {
												writer.print("?COUNT" + ((num_input) - 1) + "+1");
											}
											// Here it is expeecting to find a
											// datatype value as output
											// writer.print(DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry()));

										}
									} else {
										// IF THE OUTPUT IS "-"
										writer.print("\"\"");
									}

									// WRITING THE OUTPUT FOR THE ROW IF FALSE
									writer.print(", ");
									if (firstRow) {
										// HERE I WRITE THE ALTERNATIVE VALUE IF
										// IT EXISTS
										if (!alternative_rule_index.equals("")) {
											//THIS AGGREGATOR DOESN'T HAVE AN ALTERNATIVE RULE
											// writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index)).getOutput().get(temp_output_entries.get(j).getNum_entry()));
											writer.print("0");
										} else {
											writer.print("0");
										}

									} else {
										writer.print("?COUNT" + (num_input - 1));
									}
									writer.print(")");
									if (k == DT.getRows().size() - 1) {
										writer.println(" AS " + temp_output_entries.get(j).getDest_name() + ") .");
									} else {
										writer.println(" AS " + "?COUNT" + num_input + ") .");
									}

								} // end if output is not "-"
								firstRow = false;
							} // for every row
							//writer.println("FILTER(" + temp_output_entries.get(j).getDest_name() + " != \"\") .");
						}
						writer.println("}");
						writer.println("");
					} else if (DT.getAggregation_indicator().equals("Avg")) {
						// ===============
						// AVG AGGREGATOR
						// ===============
						
						int num_input = 0;
						boolean firstRow = true;
						
						//HERE STARTS THE SUM PART
						
						if (temp_output_entries.get(j).getHaveValue()) {
							// GETTING ALL THE ROWS OF THE DT
							for (int k = 0; k < DT.getRows().size(); k++) {
								// IF THE OUTPUT IS DIFFERENT FROM "-"
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-") && !("" + k).equals(alternative_rule_index)) {
									boolean firstInput = true;
									writer.print("BIND(");
									writer.print("IF (");
									num_input++;

									// PARSING ALL THE EFFECTIVE INPUTS
									for (int l = 0; l < temp_input_entries.size(); l++) {

										if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
											if (!firstInput) {
												writer.print(" && ");
											}

											// WRITING THE DESTINATION INPUT
											// CRITERIA
											writer.print(temp_input_entries.get(l).getDest_name());
											// WRITING THE OPERATOR OF INPUT
											// CRITERIA
											String[] arraySplittate = DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()).split(" ");
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is parsing a number/date
												// operator
												writer.print(arraySplittate[0]);
											} else {
												// Here is parsing a non
												// number/date operator
												writer.print(" = ");
											}

											// WRITING THE INPUT DATA CRITERIA
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is writing a date/number
												// value
												writer.print(arraySplittate[1]);
											} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
												writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry())).getName());
											} else {
												// Here is writing a "string" or
												// value
												writer.print(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry()));
											}

											firstInput = false;

										}

									} // end for every input

									// WRITING THE OUTPUT FOR THE ROW IF TRUE
									writer.print(", ");
									if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
											.equals("-")) {
										// IF THE OUTPUT NAME IS DIFFERENT FROM
										// "-"
										// HERE THERE IS THE LIMIT OF 1 PROPERTY
										// IN THE OUTPUT NAME
										if (getPropertyFromString(DT.getOutput_names()
												.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
														.isAnObjectProperty()) {
											// Here it is expecting to find an
											// instance as output
											writer.print(
													"?SUM" + (num_input - 1) + "+"
															+ DT.getRows().get(k).getOutput()
																	.get(temp_output_entries.get(j).getNum_entry()));
										} else {
											if (firstRow) {
												writer.print(DT.getRows().get(k).getOutput()
														.get(temp_output_entries.get(j).getNum_entry()));
											} else {
												writer.print("?SUM" + (num_input - 1) + "+" + DT.getRows().get(k)
														.getOutput().get(temp_output_entries.get(j).getNum_entry()));
											}
											// Here it is expeecting to find a
											// datatype value as output
											// writer.print(DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry()));

										}
									} else {
										// IF THE OUTPUT IS "-"
										writer.print("\"\"");
									}

									// WRITING THE OUTPUT FOR THE ROW IF FALSE
									writer.print(", ");
									if (firstRow) {
										// HERE I WRITE THE ALTERNATIVE VALUE IF
										// IT EXISTS
										if (!alternative_rule_index.equals("")) {
											//THERE IS NO ALTERNATIVE RULE IN THe SUM AGGREGATOR
											// writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index)).getOutput().get(temp_output_entries.get(j).getNum_entry()));
											writer.print("0");
										} else {
											writer.print("0");
										}

									} else {
										writer.print("?SUM" + (num_input - 1));
									}
									writer.print(")");
									if (k == DT.getRows().size() - 1) {
										writer.println(" AS " + "?TOTSUM" + ") .");
									} else {
										writer.println(" AS " + "?SUM" + num_input + ") .");
									}

								} // end if output is not "-"
								firstRow = false;
							} // for every row
							//writer.println("FILTER(" + "?TOTSUM" + " != \"\") .");
						}
						
						//HERE STARTS THE COUNT PART:
						 num_input = 0;
						 firstRow = true;
						
						if (temp_output_entries.get(j).getHaveValue()) {
							// GETTING ALL THE ROWS OF THE DT
							for (int k = 0; k < DT.getRows().size(); k++) {
								// IF THE OUTPUT IS DIFFERENT FROM "-"
								if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
										.equals("-") && !("" + k).equals(alternative_rule_index)) {
									boolean firstInput = true;
									writer.print("BIND(");
									writer.print("IF (");
									num_input++;

									// PARSING ALL THE EFFECTIVE INPUTS
									for (int l = 0; l < temp_input_entries.size(); l++) {

										if (temp_input_entries.get(l).getHaveValue() && !DT.getRows().get(k).getInput()
												.get(temp_input_entries.get(l).getNum_entry()).equals("-")) {
											if (!firstInput) {
												writer.print(" && ");
											}

											// WRITING THE DESTINATION INPUT
											// CRITERIA
											writer.print(temp_input_entries.get(l).getDest_name());
											// WRITING THE OPERATOR OF INPUT
											// CRITERIA
											String[] arraySplittate = DT.getRows().get(k).getInput()
													.get(temp_input_entries.get(l).getNum_entry()).split(" ");
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is parsing a number/date
												// operator
												writer.print(arraySplittate[0]);
											} else {
												// Here is parsing a non
												// number/date operator
												writer.print(" = ");
											}

											// WRITING THE INPUT DATA CRITERIA
											if (arraySplittate.length == 2 && arraySplittate[0].startsWith("=")
													|| arraySplittate[0].startsWith(">")
													|| arraySplittate[0].startsWith("<")) {
												// Here is writing a date/number
												// value
												writer.print(arraySplittate[1]);
											} else if (temp_input_entries.get(l).getIsARelation() || temp_input_entries.get(l).getIsAnInstance()) {
												writer.print(getInstanceFromString(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry())).getName());
											} else {
												// Here is writing a "string" or
												// value
												writer.print(DT.getRows().get(k).getInput()
														.get(temp_input_entries.get(l).getNum_entry()));
											}

											firstInput = false;

										}

									} // end for every input

									// WRITING THE OUTPUT FOR THE ROW IF TRUE
									writer.print(", ");
									if (!DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry())
											.equals("-")) {
										// IF THE OUTPUT NAME IS DIFFERENT FROM
										// "-"
										// HERE THERE IS THE LIMIT OF 1 PROPERTY
										// IN THE OUTPUT NAME
										if (getPropertyFromString(DT.getOutput_names()
												.get(temp_output_entries.get(j).getNum_entry()).getProperties().get(0))
														.isAnObjectProperty()) {
											// Here it is expecting to find an
											// instance as output
											writer.print(getInstanceFromString(DT.getRows().get(k).getOutput()
													.get(temp_output_entries.get(j).getNum_entry())).getName());
										} else {
											if (firstRow) {
												writer.print("1");
											} else {
												writer.print("?COUNT" + ((num_input) - 1) + "+1");
											}
											// Here it is expeecting to find a
											// datatype value as output
											// writer.print(DT.getRows().get(k).getOutput().get(temp_output_entries.get(j).getNum_entry()));

										}
									} else {
										// IF THE OUTPUT IS "-"
										writer.print("\"\"");
									}

									// WRITING THE OUTPUT FOR THE ROW IF FALSE
									writer.print(", ");
									if (firstRow) {
										// HERE I WRITE THE ALTERNATIVE VALUE IF
										// IT EXISTS
										if (!alternative_rule_index.equals("")) {
											//THIS AGGREGATOR DOESN'T HAVE AN ALTERNATIVE RULE
											// writer.print(DT.getRows().get(Integer.parseInt(alternative_rule_index)).getOutput().get(temp_output_entries.get(j).getNum_entry()));
											writer.print("0");
										} else {
											writer.print("0");
										}

									} else {
										writer.print("?COUNT" + (num_input - 1));
									}
									writer.print(")");
									if (k == DT.getRows().size() - 1) {
										writer.println(" AS " + "?TOTCOUNT" + ") .");
									} else {
										writer.println(" AS " + "?COUNT" + num_input + ") .");
									}

								} // end if output is not "-"
								firstRow = false;
							} // for every row
							writer.println("FILTER(" + "?TOTCOUNT" + " != 0) .");
							writer.println("BIND(?TOTSUM/?TOTCOUNT AS " + temp_output_entries.get(j).getDest_name() + ")");
						}
						
						
						
						writer.println("}");
						writer.println("");
					} else {
						writer_status.println(
								"ERROR: AGGREGATOR not found/detected for the Decision Table \"" + DT.getName() + "\"");
					}

				} // end output
				

				variables.clear();

			} else {
				writer_status.println(
						"ERROR: HIT POLICY not found/detected for the Decision Table \"" + DT.getName() + "\"");
			}

		}
		writer.close();
		writer_status.close();
		JOptionPane.showMessageDialog(null, "Decision Tables transformed with success!", "Success!",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private OntologyClass getClassFromString(String class_name) {
		OntologyClass result = null;
		if (class_name.contains(":")) {
			for (int i = 0; i < classes.size(); i++) {
				if (classes.get(i).getName().equals(class_name)) {
					result = classes.get(i);
				}
			}
		} else {
			for (int i = 0; i < classes.size(); i++) {
				if (classes.get(i).getNameWithoutPrefix().equals(class_name)) {
					result = classes.get(i);
				}
			}
		}

		if (result == null) {
			writer_status.println("WARNING: Cannot find a class for the variable: " + class_name);
			result = new OntologyClass(class_name, new ArrayList<String>(), new ArrayList<OntologyAttribute>());
		}
		return result;
	}

	private OntologyInstance getInstanceFromString(String instance_name) {

		OntologyInstance result = null;
		if (instance_name.contains(":")) {
			for (int i = 0; i < instances.size(); i++) {
				if (instances.get(i).getName().equals(instance_name)) {
					result = instances.get(i);
				}
			}
		} else {
			for (int i = 0; i < instances.size(); i++) {
				if (instances.get(i).getNameWithoutPrefix().equals(instance_name)) {
					result = instances.get(i);
				}
			}
		}
		if (result == null && !instance_name.trim().toLowerCase().equals("true") && !instance_name.trim().toLowerCase().equals("false") && !instance_name.startsWith("\"")) {
			writer_status.println("WARNING: Cannot find an instance for the variable: " + instance_name);
		}
		if (result == null) {
			result = new OntologyInstance(instance_name, new ArrayList<String>(), new ArrayList<OntologyAttribute>());
		}
		
		return result;
	}

	private OntologyProperty getPropertyFromString(String property_name) {
		OntologyProperty result = null;
		if (property_name.contains(":")) {
			for (int i = 0; i < properties.size(); i++) {
				if (properties.get(i).getName().equals(property_name)) {
					result = properties.get(i);
				}
			}
		} else {
			for (int i = 0; i < properties.size(); i++) {
				if (properties.get(i).getNameWithoutPrefix().equals(property_name)) {
					result = properties.get(i);
				}
			}
		}
		if (result == null) {
			result = new OntologyProperty(property_name, new ArrayList<String>(), new ArrayList<OntologyAttribute>());
			writer_status.println("WARNING: Cannot find a property with the name: " + property_name);
		}
		return result;
	}

	private boolean variableIsInList(String variable_name) {
		boolean result = false;
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getVariableName().equals(variable_name)) {
				result = true;
			}
		}
		return result;
	}

	private boolean stringIsInList(String name, ArrayList<String> list) {
		boolean result = false;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(name)) {
				result = true;
			}
		}
		return result;
	}

	private ArrayList<ADOxxDecisionTableNormalizedEntry> normalizeInputEntries(
			ArrayList<ADOxxDecisionTableEntry> entries) {
		ArrayList<ADOxxDecisionTableNormalizedEntry> result = new ArrayList<ADOxxDecisionTableNormalizedEntry>();

		for (int i = 0; i < entries.size(); i++) {
			
			if (entries.get(i).getProperties().size() == 0){
				//HERE we are parsing a single instance without relation
				result.add(new ADOxxDecisionTableNormalizedEntry(entries.get(i).getObject_name(), entries.get(i).getObject_name(), false, true, i,true));
			} else if (entries.get(i).getProperties().size() == 1) {

				if (getPropertyFromString(entries.get(i).getProperties().get(0)).isAnObjectProperty()) {
					// Here we are parsing both an INPUT entry and a RELATION
					result.add(new ADOxxDecisionTableNormalizedEntry(entries.get(i).getObject_name(),
							entries.get(i).getProperties().get(0), entries.get(i).getDest_name(), true, true, i, false));
				} else if (getPropertyFromString(entries.get(i).getProperties().get(0)).isADataTypeProperty()) {
					// Here we are parsing an INPUT entry only
					result.add(new ADOxxDecisionTableNormalizedEntry(entries.get(i).getObject_name(),
							entries.get(i).getProperties().get(0), entries.get(i).getDest_name(), false, true, i, false));
				} else {
					result.add(new ADOxxDecisionTableNormalizedEntry(entries.get(i).getObject_name(),
							entries.get(i).getProperties().get(0), entries.get(i).getDest_name(), false, true, i, false));
				}

			} else {
				// Here we are parsing a COMBO
				String next_source = "";
				for (int j = 0; j < entries.get(i).getProperties().size(); j++) {

					if (j == 0) {
						next_source = entries.get(i).getObject_name();
					}

					if (j != entries.get(i).getProperties().size() - 1) {
						result.add(new ADOxxDecisionTableNormalizedEntry(next_source,
								entries.get(i).getProperties().get(j), "?" + i + "temp" + j, true, false, false));
						next_source = "?" + i + "temp" + j;
					} else {
						// Here we are parsing the last element of a COMBO
						if (getPropertyFromString(entries.get(i).getProperties().get(j)).isAnObjectProperty()) {
							
							// Here the last property is an objectProperty
							result.add(new ADOxxDecisionTableNormalizedEntry(next_source,
									entries.get(i).getProperties().get(j), entries.get(i).getDest_name(), true, true, i, false));

						} else if (getPropertyFromString(entries.get(i).getProperties().get(j)).isADataTypeProperty()) {
							
							// Here the last property is a DataType Property
							result.add(new ADOxxDecisionTableNormalizedEntry(next_source,
									entries.get(i).getProperties().get(j), entries.get(i).getDest_name(), false, true,
									i, false));
						} else {
							
							// Here i cannot identify the property
							result.add(new ADOxxDecisionTableNormalizedEntry(next_source,
									entries.get(i).getProperties().get(j), entries.get(i).getDest_name(), true, true, false));
						}

					} // end if we are parsing the last property
				}

			} // end parsing a combo
		}

		return result;
	}

	public void testing() {
		// THIS METHOD TESTS ALL THE RELATIONS, INPUTS AND OUTPUTS
		for (int i = 0; i < adoxxDecisionTables.size(); i++) {
			ADOxxDecisionTable DT = adoxxDecisionTables.get(i);
			ArrayList<ADOxxDecisionTableNormalizedEntry> temp_output_entries = normalizeInputEntries(
					DT.getOutput_names());
			ArrayList<ADOxxDecisionTableNormalizedEntry> temp_input_entries = normalizeInputEntries(
					DT.getInput_names());

			for (int j = 0; j < temp_input_entries.size(); j++) {
				System.out.println("Relation: " + temp_input_entries.get(j).getIsARelation() + " / " + "Input: "
						+ temp_input_entries.get(j).getHaveValue() + " - " + temp_input_entries.get(j).getNum_entry()
						+ " - " + temp_input_entries.get(j).getObject_name() + " - "
						+ temp_input_entries.get(j).getProperty() + " - " + temp_input_entries.get(j).getDest_name());
			}
			System.out.println("-------------");
			for (int j = 0; j < temp_output_entries.size(); j++) {
				System.out.println("Relation: " + temp_output_entries.get(j).getIsARelation() + " / " + "Input: "
						+ temp_output_entries.get(j).getHaveValue() + " - " + temp_output_entries.get(j).getNum_entry()
						+ " - " + temp_output_entries.get(j).getObject_name() + " - "
						+ temp_output_entries.get(j).getProperty() + " - " + temp_output_entries.get(j).getDest_name());
			}
			System.out.println("===========");
			System.out.println("===========");
		}

	
	}
	/*
	 * public void performConstructRule(ParameterizedSparqlString queryStr) {
	 * Model temp = ModelFactory.createOntologyModel();
	 * addNamespacesToQuery(queryStr); QueryExecution qexec =
	 * QueryExecutionFactory.create(queryStr.toString(), rdfModel); temp =
	 * qexec.execConstruct(); rdfModel.add(temp); }
	 */
	
public void printNormalizedEntry (ADOxxDecisionTableNormalizedEntry normalized_entry){
	System.out.println("NEW ENTRY:");
	System.out.println("     Object_name: " + normalized_entry.getObject_name());
	//if (normalized_entry.getProperty() != null){
		System.out.println("    Property: " + normalized_entry.getProperty());
	//}else {
		//System.out.println("    Property: " + "NOT DEFINED");
	//}
	System.out.println("    Dest_name: " + normalized_entry.getDest_name());
	System.out.println("    isARelation: " + normalized_entry.getIsARelation());
	System.out.println("    haveValue: " + normalized_entry.getHaveValue());	
	//if (Integer.toString(normalized_entry.getNum_entry()) != null){
		System.out.println("    num_entry: " + normalized_entry.getNum_entry());
	//}else {
	//	System.out.println("    num_entry: " + "NOT DEFINED");
	//}
	System.out.println("    isAnInstance: " + normalized_entry.getIsAnInstance());
}
}

