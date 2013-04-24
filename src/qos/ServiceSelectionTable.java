package qos;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class ServiceSelectionTable extends JTable {

	private static final long serialVersionUID = 1L;
	
	public ServiceSelectionTable(int rows, int cols, boolean isCheckBoxTable) {
		this.setModel(new BasicTableModel(rows, cols, isCheckBoxTable));
	}
	
	public void setColumnWidthRelative(double[] columnWidthPercentages) {
		double tableWidth = this.getPreferredSize().getWidth();
		for (int count = 0; count < columnWidthPercentages.length; count++) {
			this.getColumnModel().getColumn(count).setPreferredWidth(
					(int)((columnWidthPercentages[count] * tableWidth) + 0.5));
		}
	}

	public void setColumnTextAlignment(int column, int columnAlignment) {
		DefaultTableCellRenderer defaultRenderer = 
			new DefaultTableCellRenderer();
		defaultRenderer.setHorizontalAlignment(columnAlignment);
		this.getColumnModel().getColumn(column).setCellRenderer(
				defaultRenderer);
	}

}
