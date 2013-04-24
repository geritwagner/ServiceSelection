package qos;

import javax.swing.table.DefaultTableModel;

public class BasicTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	private boolean isCheckboxTable = false;
	
	BasicTableModel(
			int rows, int columns, boolean isCheckboxTable) {
		super.setRowCount(rows);
		super.setColumnCount(columns);
		this.isCheckboxTable = isCheckboxTable;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
        return (columnIndex == 0 && 
        		isCheckboxTable) ? Boolean.class : String.class;
    }
	
}
