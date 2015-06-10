package src.util.filefilter;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class RMPLFileFilter extends FileFilter {

	public boolean accept(File f) {
		// TODO Auto-generated method stub
		return f.isDirectory() || f.getName().toLowerCase().endsWith(".rmpl");
				
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return "RMPL files";
	}

}
