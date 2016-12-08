package UI;

import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import ch.fhnw.stizzo.test;

import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JSeparator;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;
import java.awt.event.ActionEvent;
import javax.swing.JPasswordField;

public class frmPreferences extends JFrame {

	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtDefaultDirectory;
	private JTextField txtWebOntology;
	private JTextField txtWebADOxx;
	private JCheckBox chkOntology;
	private JCheckBox chkADOxx;
	private JCheckBox chkRenameTheOutput;
	private JTextField txtFTPAddress;
	private JTextField txtFTPPort;
	private JTextField txtFTPUsername;
	private JPasswordField PwdFTPPassword;
	private JCheckBox chkFTPEnabled;


	

	public JTextField getTxtDefaultDirectory() {
		return txtDefaultDirectory;
	}

	public void setTxtDefaultDirectory(JTextField txtDefaultDirectory) {
		this.txtDefaultDirectory = txtDefaultDirectory;
	}

	public JTextField getTxtWebOntology() {
		return txtWebOntology;
	}

	public void setTxtWebOntology(JTextField txtWebOntology) {
		this.txtWebOntology = txtWebOntology;
	}

	public JTextField getTxtWebADOxx() {
		return txtWebADOxx;
	}

	public void setTxtWebADOxx(JTextField txtWebADOxx) {
		this.txtWebADOxx = txtWebADOxx;
	}

	public JCheckBox getChkOntology() {
		return chkOntology;
	}

	public void setChkOntology(JCheckBox chkOntology) {
		this.chkOntology = chkOntology;
	}

	public JCheckBox getChkADOxx() {
		return chkADOxx;
	}

	public void setChkADOxx(JCheckBox chkADOxx) {
		this.chkADOxx = chkADOxx;
	}

	public JCheckBox getChkRenameTheOutput() {
		return chkRenameTheOutput;
	}

	public void setChkRenameTheOutput(JCheckBox chkRenameTheOutput) {
		this.chkRenameTheOutput = chkRenameTheOutput;
	}

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frmPreferences frame = new frmPreferences();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public frmPreferences() {
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
		
		setResizable(false);
		setTitle("Preferences");
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/preferences-menu.png")));
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 593, 352);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 587, 266);
		contentPane.add(tabbedPane);
		
		JLayeredPane layeredPane = new JLayeredPane();
		tabbedPane.addTab("General", null, layeredPane, null);
		
		JLabel lblDefaultPathFor = new JLabel("Default directory for output files:");
		lblDefaultPathFor.setBounds(12, 13, 255, 16);
		layeredPane.add(lblDefaultPathFor);
		
		txtDefaultDirectory = new JTextField();
		txtDefaultDirectory.setEditable(false);
		txtDefaultDirectory.setBounds(12, 30, 439, 30);
		layeredPane.add(txtDefaultDirectory);
		txtDefaultDirectory.setColumns(10);
		
		JButton btnSetDirectory = new JButton("Set Directory");
		btnSetDirectory.setBounds(463, 30, 107, 31);
		layeredPane.add(btnSetDirectory);
		
		chkRenameTheOutput = new JCheckBox("Rename the output file with the timestamp of the execution");
		chkRenameTheOutput.setBounds(12, 69, 418, 25);
		layeredPane.add(chkRenameTheOutput);
		
		JLayeredPane layeredPane_1 = new JLayeredPane();
		tabbedPane.addTab("Web sources", null, layeredPane_1, null);
		
		JLabel lblWebAddressFor = new JLabel("Web address of ontology file:");
		lblWebAddressFor.setBounds(12, 29, 255, 16);
		layeredPane_1.add(lblWebAddressFor);
		
		txtWebOntology = new JTextField();
		txtWebOntology.setColumns(10);
		txtWebOntology.setBounds(12, 46, 558, 30);
		layeredPane_1.add(txtWebOntology);
		
		JLabel lblWebAddressOf = new JLabel("Web address of ADOxx file:");
		lblWebAddressOf.setBounds(12, 126, 255, 16);
		layeredPane_1.add(lblWebAddressOf);
		
		txtWebADOxx = new JTextField();
		txtWebADOxx.setColumns(10);
		txtWebADOxx.setBounds(12, 143, 558, 30);
		
		layeredPane_1.add(txtWebADOxx);
		
		chkOntology = new JCheckBox("Allow load of Ontology file from web");
		chkOntology.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
					txtWebOntology.setEnabled(chkOntology.isSelected());
					test.ui.getBtnOpenOntology().setEnabled(!chkOntology.isSelected());
				
			}
		});
		chkOntology.setBounds(8, 0, 275, 25);
		layeredPane_1.add(chkOntology);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 90, 558, 2);
		layeredPane_1.add(separator);
		
		chkADOxx = new JCheckBox("Allow load of ADOxx file from web");
		chkADOxx.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
					txtWebADOxx.setEnabled(chkADOxx.isSelected());
					test.ui.getBtnOpenXML().setEnabled(!chkADOxx.isSelected());
			}
		});
		chkADOxx.setBounds(8, 99, 275, 25);
		layeredPane_1.add(chkADOxx);
		
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Preferences prefs = Preferences.userNodeForPackage(frmPreferences.class);
				prefs.put("TxtDefaultDirectory", txtDefaultDirectory.getText());
				prefs.putBoolean("chkRenameTheOutput", chkRenameTheOutput.isSelected());
				prefs.putBoolean("chkOntology", chkOntology.isSelected());
				prefs.put("txtWebOntology", txtWebOntology.getText());
				prefs.putBoolean("chkADOxx", chkADOxx.isSelected());
				prefs.put("txtWebADOxx", txtWebADOxx.getText());
				test.windowPrefs.setVisible(false);
			}
		});
		btnOk.setBounds(461, 279, 114, 30);
		contentPane.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				test.windowPrefs.setVisible(false);
			}
		});
		btnCancel.setBounds(10, 279, 114, 30);
		contentPane.add(btnCancel);
	}
}
