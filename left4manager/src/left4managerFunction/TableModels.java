package left4managerFunction;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.AbstractTableModel;

public class TableModels {
	public static class GroupListTableModel extends AbstractTableModel {

		protected static final String[] COLUMN_NAMES = { "Name" };
		private Config config;
		private List<ModGroup> rowData;
		private int columnCount;
		private List<Boolean> editableRow;

		public GroupListTableModel(Config config) {
			rowData = new ArrayList<ModGroup>();
			this.config = config;
			editableRow = new ArrayList<Boolean>();
			this.columnCount = COLUMN_NAMES.length;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return this.editableRow.get(row);
		}
		
		public void setRowEditable(int row, boolean value) {
			this.editableRow.set(row, value);
		}
		public void setAllRowEditable(boolean value) {
			for(int i = 0; i < editableRow.size(); i++) {
				this.editableRow.set(i, value);
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return String.class;
			default:
				return String.class;
			}
		}

		public void add(List<ModGroup> modList) {
			for(int i = 0; i < modList.size(); i++) {
				this.editableRow.add(false);
			}
			rowData.addAll(modList);
			fireTableDataChanged();
			updateModGroupFile();
		}

		public void add(ModGroup... pd) {
			add(Arrays.asList(pd));
		}

		public void clear() {
			rowData.clear();
			fireTableDataChanged();
			updateModGroupFile();
		}

		public void remove(int index) {
			this.editableRow.remove(index);
			rowData.remove(index);
			fireTableDataChanged();
			updateModGroupFile();
		}

		@Override
		public int getRowCount() {
			return rowData.size();
		}

		@Override
		public int getColumnCount() {
			return this.columnCount;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		public ModGroup getRow(int row) {
			return rowData.get(row);
		}

		public List<ModGroup> getRow(int[] row) {
			List<ModGroup> groupList = new ArrayList<>();
			for (int i = 0; i < row.length; i++) {
				groupList.add(getRow(row[i]));
			}
			return groupList;
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			switch (column) {
			case 0:
				getRow(row).setGroupName((String) value);
				fireTableCellUpdated(row, column);
			default:
				break;
			}
			updateModGroupFile();
		}
		
		public String getGroupName(int row) {
			return rowData.get(row).getGroupName();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ModGroup modGroup = getRow(rowIndex);
			Object value = null;
			switch (columnIndex) {
			case 0:
				value = modGroup.getGroupName();
				break;
			default:
				break;
			}
			return value;
		}

		public void repaint() {
			fireTableDataChanged();
		}
		
		public int getIndexByModInfo(ModGroup mod) {
			return rowData.indexOf(mod);
		}
		
		public List<ModGroup> getList() {
			return this.rowData;
		}
		
		public void updateModGroupFile() {
			//config.writeModGroupFile(rowData);
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static class GroupModTableModel extends AbstractTableModel {

		protected static final String[] COLUMN_NAMES = { "Name", "Code", "Author", "Enabled" };

		private List<ModInfo> rowData;
		private int columnCount;

		public GroupModTableModel() {
			rowData = new ArrayList<>();
			this.columnCount = COLUMN_NAMES.length;
		}

		public GroupModTableModel(int columnCount) {
			rowData = new ArrayList<>();
			this.columnCount = columnCount;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			switch (column) {
			case 0:
			case 1:
			case 2:
				return false;
			case 3:
				return true;
			default:
				return false;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
			case 1:
			case 2:
				return String.class;
			case 3:
				return Boolean.class;
			default:
				return String.class;
			}
		}

		public void add(List<ModInfo> modList) {
			rowData.addAll(modList);
			fireTableDataChanged();
		}

		public void add(ModInfo... pd) {
			add(Arrays.asList(pd));
		}

		public void clear() {
			rowData.clear();
			fireTableDataChanged();
		}

		public void remove(int index) {
			rowData.remove(index);
			fireTableDataChanged();
		}

		@Override
		public int getRowCount() {
			return rowData.size();
		}

		@Override
		public int getColumnCount() {
			return this.columnCount;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}

		public ModInfo getRow(int row) {
			return rowData.get(row);
		}

		public List<ModInfo> getRow(int[] row) {
			List<ModInfo> mods = new ArrayList<>();
			for (int i = 0; i < row.length; i++) {
				mods.add(getRow(row[i]));
			}
			return mods;
		}

		@Override
		public void setValueAt(Object value, int row, int column) {
			switch (column) {
			case 0:
			case 1:
			case 2:
				break;
			case 3:
				getRow(row).setEnabled((boolean) value);
				fireTableCellUpdated(row, column);
			default:
				break;
			}
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ModInfo mod = getRow(rowIndex);
			Object value = null;
			switch (columnIndex) {
			case 0:
				value = mod.getName();
				break;
			case 1:
				value = mod.getCode();
				break;
			case 2:
				value = mod.getAuthor();
				break;
			case 3:
				value = mod.getEnabled();
				break;
			}
			return value;
		}

		public void repaint() {
			fireTableDataChanged();
		}
		
		public int getIndexByModInfo(ModInfo mod){
			return rowData.indexOf(mod);
		}
		
		public int getIndexByCode(String code) {
			for(int i = 0; i < rowData.size(); i++) {
	    		if(rowData.get(i).getCode().equals(code)) {
	    			return i;
	    		}
	    	}
			return -1;
		}
	}
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static class ModListOrderModel extends AbstractTableModel {
		private List<ModInfo> rowData;
		protected static final String[] COLUMN_NAMES = { "Index", "Name", "Code", "Author" };
		private int columnCount;

		public ModListOrderModel() {
			rowData = new ArrayList<>();
			this.columnCount = COLUMN_NAMES.length;
		}
		
		public void add(List<ModInfo> modList) {
			rowData.addAll(modList);
			fireTableDataChanged();
		}

		public void add(ModInfo... pd) {
			add(Arrays.asList(pd));
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			switch (column) {
			case 0:
				return true;
			case 1:
			case 2:
			case 3:
				return false;
			default:
				return false;
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case 0:
				return Integer.class;
			case 1:
			case 2:
			case 3:
				return String.class;
			default:
				return String.class;
			}
		}
		
		@Override
		public void setValueAt(Object value, int row, int column) {
			switch (column) {
			case 0:
				int position = (Integer) value;
				reorder(row, position - 1);
				fireTableDataChanged();
			case 1:
			case 2:
			case 3:
				break;
			default:
				break;
			}
		}
		
		public ModInfo getRow(int row) {
			return rowData.get(row);
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			ModInfo mod = getRow(rowIndex);
			Object value = null;
			switch (columnIndex) {
			case 0:
				value = rowIndex + 1;
				break;
			case 1:
				value = mod.getName();
				break;
			case 2:
				value = mod.getCode();
				break;
			case 3:
				value = mod.getAuthor();
				break;
			}
			return value;
		}
		
		@Override
		public int getRowCount() {
			return rowData.size();
		}

		@Override
		public int getColumnCount() {
			return this.columnCount;
		}

		@Override
		public String getColumnName(int column) {
			return COLUMN_NAMES[column];
		}
		
		public void reorder(int rowIndex, int position) {
	    	rowData.add(position, rowData.remove(rowIndex));
			fireTableDataChanged();
		}

	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static class TableRowTransferHandler extends TransferHandler {

		private final DataFlavor localObjectFlavor = new DataFlavor(String.class, "ModInfo object");
	    private JTable table = null;
	    private ModListOrderModel model = null;

	    public TableRowTransferHandler(JTable table) {
	        this.table = table;
	        this.model = (ModListOrderModel) table.getModel();
	    }

	    @Override
	    protected Transferable createTransferable(JComponent c) {
	        assert (c == table);
	        String dataToTransfer = Integer.toString(table.getSelectedRow());
	    	System.out.println(dataToTransfer);
	        return new StringSelection(dataToTransfer);
	    }

	    @Override
	    public boolean canImport(TransferHandler.TransferSupport info) {
	    	System.out.println(1);
	        boolean b = info.getComponent() == table && info.isDrop() && info.isDataFlavorSupported(localObjectFlavor);
	        table.setCursor(b ? DragSource.DefaultMoveDrop : DragSource.DefaultMoveNoDrop);
	        return b;
	    }

	    @Override
	    public int getSourceActions(JComponent c) {
	    	System.out.println(2);

	        return TransferHandler.COPY_OR_MOVE;
	    }

	    @Override
	    public boolean importData(TransferHandler.TransferSupport info) {
	    	System.out.println(3);

	    	JTable target = (JTable) info.getComponent();
	         JTable.DropLocation dl = (JTable.DropLocation) info.getDropLocation();
	         int index = dl.getRow();
	         int max = table.getModel().getRowCount();
	         if (index < 0 || index > max) {
	             index = max;
	         }
	         target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

	         try {
	             Integer rowFrom = Integer.valueOf((String) info.getTransferable().getTransferData(localObjectFlavor));
	             if (rowFrom != -1 && rowFrom != index) {

	                 int[] rows = table.getSelectedRows();
	                 int iter = 0;
	                 for (int row : rows) {
	                     if (index > row) {
	                         index--;
	                         model.reorder(row - iter, index);
	                     }

	                     else {
	                    	 model.reorder(row, index);
	                     }
	                     index++;
	                     iter++;
	                 }
	                 table.setRowSelectionInterval(index - rows.length, index - 1);
	                 return true;
	             }

	       } catch (Exception e) {
	    	   e.printStackTrace();
	       }
	       return false;
	    }

	    @Override
	    protected void exportDone(JComponent c, Transferable t, int act) {
	    	System.out.println(4);

	        if (act == TransferHandler.MOVE) {
	            table.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	        }
	    }
	}
}
