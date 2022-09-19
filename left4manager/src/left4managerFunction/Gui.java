package left4managerFunction;

import java.awt.EventQueue;
import java.util.List;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.JScrollPane;  
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.*;

public class Gui {

	private JFrame frame;
	private JTable table;
	ExtractModList extractModList = new ExtractModList();
	UpdateModFile updateModFile = new UpdateModFile();


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Gui() {
		initialize();
		populateTable();
		debug();
	}
	
	private void debug() {
		extractModList.populateModList();
		List<ModInfo> modList = extractModList.getModList();
		
		updateModFile.setFileName("addonlist2.txt");
		updateModFile.setDirectory("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Left 4 Dead 2\\left4dead2\\");
		updateModFile.writeFile(updateModFile.buildString(modList));
	}

	private void initialize() {		
		frame = new JFrame();
		frame.setBounds(200, 200, 850, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

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
	         public Class getColumnClass(int columnIndex) {
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
		}; 
	
		model.addColumn("name");
		model.addColumn("code");
		model.addColumn("author");
		model.addColumn("enabled");
		table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table); 
		frame.getContentPane().add(scrollPane, BorderLayout.WEST);
		addTableListener();
	}

	private void addTableListener() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
		model.addTableModelListener(new TableModelListener() {
			
			@Override
			public void tableChanged(TableModelEvent tme) {
				if (tme.getType() == TableModelEvent.UPDATE) {
					extractModList.populateModList();
					List<ModInfo> modList = extractModList.getModList();
					boolean newValue = (Boolean) model.getValueAt(tme.getFirstRow(),tme.getColumn());
					modList.get(tme.getFirstRow()).setEnabled(newValue);
					
					
					/*System.out.println("Cell " + tme.getFirstRow() + ", "
							+ tme.getColumn() + " changed. The new value: "
							+ model.getValueAt(tme.getFirstRow(),
									tme.getColumn()));
					System.out.println(modList.get(tme.getFirstRow()).getCode() + 
							modList.get(tme.getFirstRow()).getEnabled());*/
					
					updateModFile.writeFile(updateModFile.buildString(modList)); //TODO: Spostarlo ad un bottone "save"
				}
			}
		});
	}
	
	private void populateTable() {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        extractModList.populateModList();
		List<ModInfo> modList = extractModList.getModList();
		for(int i=0; i<modList.size(); i++) {
			//model.addRow(modList.get(i).getVector());
			model.addRow(modList.get(i).getObject());
		}
	}

}
