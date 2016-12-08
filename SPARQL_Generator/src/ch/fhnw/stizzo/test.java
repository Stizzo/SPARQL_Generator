package ch.fhnw.stizzo;

import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import UI.UI;
import UI.frmPreferences;

public class test {
	public static UI ui;
	public static frmPreferences windowPrefs;
	
	public static void main(String[] args) {
		//creating the graphical user interface
		ui = new UI();
		windowPrefs = new frmPreferences();
		ui.newScreen();
		
		//loading all the preferences
		Preferences prefs = Preferences.userNodeForPackage(frmPreferences.class);
		
		windowPrefs.getTxtDefaultDirectory().setText(prefs.get("TxtDefaultDirectory", new JFileChooser().getCurrentDirectory().toString()));
		windowPrefs.getChkRenameTheOutput().setSelected(prefs.getBoolean("chkRenameTheOutput", true));
		windowPrefs.getChkOntology().setSelected(prefs.getBoolean("chkOntology", false));
		windowPrefs.getTxtWebOntology().setText(prefs.get("txtWebOntology", ""));
		windowPrefs.getChkADOxx().setSelected(prefs.getBoolean("chkADOxx", false));
		windowPrefs.getTxtWebADOxx().setText(prefs.get("txtWebADOxx", ""));
		
		windowPrefs.getTxtWebADOxx().setEnabled(windowPrefs.getChkADOxx().isSelected());
		windowPrefs.getTxtWebOntology().setEnabled(windowPrefs.getChkOntology().isSelected());
		ui.getTxtOutputDirectory().setText(windowPrefs.getTxtDefaultDirectory().getText());
		ui.getBtnOpenOntology().setEnabled(!windowPrefs.getChkOntology().isSelected());
		ui.getBtnOpenXML().setEnabled(!windowPrefs.getChkADOxx().isSelected());
		
		//run the User Interface	
			
			
	}

}
