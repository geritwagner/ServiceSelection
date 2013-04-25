package qos;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class ServiceSelectionFileChooser extends JFileChooser {
	
	private static final long serialVersionUID = 1L;
	
	public ServiceSelectionFileChooser(String fileName) {
		setFileFilter(new FileFilter() {
			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith("csv") || 
				f.isDirectory();
			}
			@Override
			public String getDescription() {
				return "CSV Datei (Comma Seperated Values)";
			}
		});
		setSelectedFile(new File(fileName + ".csv"));	
	}
	
}
