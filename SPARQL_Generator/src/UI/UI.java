package UI;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import ch.fhnw.stizzo.Operations;
import ch.fhnw.stizzo.test;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import java.awt.Font;
import java.awt.Toolkit;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

public class UI extends JFrame{

	private static final long serialVersionUID = 1L;
	private Operations op;
	private String title = "ADOxx DMN Parser";
	//Graphical elements:
	private JFrame frmDmnAnalyser;
	private JTextField txtPathOntology;
	private JTextField txtPathXML;
	private JLabel lblOntologyClasses;
	private JLabel lblOntologyProperties;
	private JLabel lblOntologyInstances;
	private JLabel lblAdoModels;
	private JLabel lblAdoDecisionTables;
	private JButton btnExecuteDT;
	private JTextField txtOutputDirectory;
	private JButton btnOpenOntology;
	private JButton btnOpenXML;

	public JButton getBtnOpenOntology() {
		return btnOpenOntology;
	}

	public void setBtnOpenOntology(JButton btnOpenOntology) {
		this.btnOpenOntology = btnOpenOntology;
	}

	public JButton getBtnOpenXML() {
		return btnOpenXML;
	}

	public void setBtnOpenXML(JButton btnOpenXML) {
		this.btnOpenXML = btnOpenXML;
	}

	public JTextField getTxtOutputDirectory() {
		return txtOutputDirectory;
	}

	public void setTxtOutputDirectory(JTextField txtOutputDirectory) {
		this.txtOutputDirectory = txtOutputDirectory;
	}


	public void newScreen() {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					test.ui.frmDmnAnalyser.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public UI() {
		initialize();
		//Create the set of operations (all instances of classes and attributes will be stored
		//in this object).
		op = new Operations();
	}

	private void initialize() {
		try {

		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		   System.out.println(e.getMessage());
		}
		frmDmnAnalyser = new JFrame();
		frmDmnAnalyser.setResizable(false);
		frmDmnAnalyser.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
		frmDmnAnalyser.setTitle(title);
		frmDmnAnalyser.setBounds(100, 100, 510, 515);
		frmDmnAnalyser.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDmnAnalyser.getContentPane().setLayout(null);
		
		JLabel lblOntology = new JLabel("Path of the Ontology File:");
		lblOntology.setBounds(12, 13, 394, 16);
		frmDmnAnalyser.getContentPane().add(lblOntology);
		
		txtPathOntology = new JTextField();
		txtPathOntology.setEditable(false);
		txtPathOntology.setBounds(12, 30, 338, 34);
		frmDmnAnalyser.getContentPane().add(txtPathOntology);
		txtPathOntology.setColumns(10);
		
		btnOpenOntology = new JButton("Open File...");

		btnOpenOntology.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Please, select the file of the Ontology");
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.showOpenDialog(null);
					
				txtPathOntology.setText(chooser.getSelectedFile().toString());
			}
		});
		btnOpenOntology.setBounds(362, 30, 125, 35);
		frmDmnAnalyser.getContentPane().add(btnOpenOntology);
		
		txtPathXML = new JTextField();
		txtPathXML.setEditable(false);
		txtPathXML.setColumns(10);
		txtPathXML.setBounds(12, 94, 338, 34);
		frmDmnAnalyser.getContentPane().add(txtPathXML);
		
		JLabel lblPathOfThe = new JLabel("Path of the XML data from ADOxx:");
		lblPathOfThe.setBounds(12, 77, 394, 16);
		frmDmnAnalyser.getContentPane().add(lblPathOfThe);
		
		btnOpenXML = new JButton("Open File...");
		btnOpenXML.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
					chooser.setDialogTitle("Please, select the XML file from ADOxx");
					chooser.setCurrentDirectory(new java.io.File("."));
					chooser.setAcceptAllFileFilterUsed(false);
					chooser.showOpenDialog(null);
				txtPathXML.setText(chooser.getSelectedFile().toString());
			}
		});
		btnOpenXML.setBounds(362, 94, 125, 35);
		frmDmnAnalyser.getContentPane().add(btnOpenXML);
		
		JLabel lbl7 = new JLabel("Ontology:");
		lbl7.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl7.setBounds(12, 229, 184, 16);
		frmDmnAnalyser.getContentPane().add(lbl7);
		
		JLabel lbl10 = new JLabel("Classes loaded:");
		lbl10.setBounds(12, 258, 116, 16);
		frmDmnAnalyser.getContentPane().add(lbl10);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 214, 475, 2);
		frmDmnAnalyser.getContentPane().add(separator);
		
		JLabel lbl11 = new JLabel("Properties loaded:");
		lbl11.setBounds(12, 287, 116, 16);
		frmDmnAnalyser.getContentPane().add(lbl11);
		
		JLabel lbl12 = new JLabel("Instances loaded:");
		lbl12.setBounds(12, 316, 116, 16);
		frmDmnAnalyser.getContentPane().add(lbl12);
		
		lblOntologyClasses = new JLabel("0");
		lblOntologyClasses.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOntologyClasses.setBounds(169, 258, 38, 16);
		frmDmnAnalyser.getContentPane().add(lblOntologyClasses);
		
		lblOntologyProperties = new JLabel("0");
		lblOntologyProperties.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOntologyProperties.setBounds(169, 287, 38, 16);
		frmDmnAnalyser.getContentPane().add(lblOntologyProperties);
		
		lblOntologyInstances = new JLabel("0");
		lblOntologyInstances.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOntologyInstances.setBounds(169, 316, 38, 16);
		frmDmnAnalyser.getContentPane().add(lblOntologyInstances);
		
		JLabel lbl8 = new JLabel("ADOxx:");
		lbl8.setFont(new Font("Tahoma", Font.BOLD, 13));
		lbl8.setBounds(251, 229, 184, 16);
		frmDmnAnalyser.getContentPane().add(lbl8);
		
		JLabel lbl13 = new JLabel("Models analysed:");
		lbl13.setBounds(251, 258, 116, 16);
		frmDmnAnalyser.getContentPane().add(lbl13);
		
		lblAdoModels = new JLabel("0");
		lblAdoModels.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdoModels.setBounds(436, 258, 38, 16);
		frmDmnAnalyser.getContentPane().add(lblAdoModels);
		
		JLabel lbl15 = new JLabel("Decision table(s) detected:");
		lbl15.setBounds(251, 287, 156, 16);
		frmDmnAnalyser.getContentPane().add(lbl15);
		
		lblAdoDecisionTables = new JLabel("0");
		lblAdoDecisionTables.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAdoDecisionTables.setBounds(436, 287, 38, 16);
		frmDmnAnalyser.getContentPane().add(lblAdoDecisionTables);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		separator_1.setBounds(240, 229, 7, 120);
		frmDmnAnalyser.getContentPane().add(separator_1);
		
		
		
		JMenuBar menuBar = new JMenuBar();
		frmDmnAnalyser.setJMenuBar(menuBar);
		
		JMenu mnDmnAnalyser = new JMenu(title);
		menuBar.add(mnDmnAnalyser);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setIcon(new ImageIcon(getClass().getResource("/button-round-cancel-menu.png")));
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				test.windowPrefs.setVisible(true);
			}
		});
		mntmPreferences.setIcon(new ImageIcon(getClass().getResource("/preferences-menu.png")));
		mnDmnAnalyser.add(mntmPreferences);
		mnDmnAnalyser.add(mntmExit);
		
		JMenu menu = new JMenu("?");
		menuBar.add(menu);
		
		JMenuItem mntmAboutDmnAnalyser = new JMenuItem("About "+title);
		mntmAboutDmnAnalyser.setIcon(new ImageIcon(getClass().getResource("/button-bubble-info-menu.png")));
		menu.add(mntmAboutDmnAnalyser);
		
		JButton btnLoadData = new JButton("Load data from Files");
		btnLoadData.setIcon(new ImageIcon(getClass().getResource("/load-icon.png")));
		btnLoadData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			loadOntology(!test.windowPrefs.getChkOntology().isSelected());
			loadADOxx(!test.windowPrefs.getChkADOxx().isSelected());
				
			btnExecuteDT.setEnabled(true);
	
			}
			
		});
		btnLoadData.setBounds(12, 368, 222, 73);
		frmDmnAnalyser.getContentPane().add(btnLoadData);
		
		btnExecuteDT = new JButton("Execute Decision Tables");
		btnExecuteDT.setEnabled(false);
		btnExecuteDT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//op.printDecisionTables();
				op.executeDecisionTables();
				//op.testing();
			}
		});
		btnExecuteDT.setIcon(new ImageIcon(getClass().getResource("/wand-icon.png")));
		btnExecuteDT.setBounds(252, 368, 235, 73);
		frmDmnAnalyser.getContentPane().add(btnExecuteDT);
		
		JLabel lblSetOutputDirectory = new JLabel("Set output Directory:");
		lblSetOutputDirectory.setBounds(12, 141, 394, 16);
		frmDmnAnalyser.getContentPane().add(lblSetOutputDirectory);
		
		txtOutputDirectory = new JTextField();
		txtOutputDirectory.setEditable(false);
		txtOutputDirectory.setColumns(10);
		txtOutputDirectory.setBounds(12, 158, 338, 34);
		frmDmnAnalyser.getContentPane().add(txtOutputDirectory);
		
		JButton btnSetDirectory = new JButton("Set Directory...");
		btnSetDirectory.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setDialogTitle("Please, select the output folder");
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.showOpenDialog(null);
			txtOutputDirectory.setText(chooser.getSelectedFile().toString());
		
			}
		});
		btnSetDirectory.setBounds(362, 158, 125, 35);
		frmDmnAnalyser.getContentPane().add(btnSetDirectory);
		
	}
	
	void loadOntology(boolean offline){
		
		//loading ontology
		int[] resultFromOntologyLoading = new int[3];
		if (offline){
			resultFromOntologyLoading = op.parseOntology(txtPathOntology.getText(),offline);
		} else {
			resultFromOntologyLoading = op.parseOntology(test.windowPrefs.getTxtWebOntology().getText(),offline);
		}
		
		lblOntologyClasses.setText(""+resultFromOntologyLoading[0]);
		lblOntologyProperties.setText(""+resultFromOntologyLoading[1]);
		lblOntologyInstances.setText(""+resultFromOntologyLoading[2]);
		
		
	
	}
	void loadADOxx(boolean offline){
	
		//patching xml
		op.patchXML(txtPathXML.getText(), offline);
	
		//loading xml
		int[] resultFromXMLLoading = new int[2];
		resultFromXMLLoading = op.loadXML(txtPathXML.getText());
		lblAdoModels.setText(""+resultFromXMLLoading[0]);
		lblAdoDecisionTables.setText(""+resultFromXMLLoading[1]);
		
		
		
	}
}
