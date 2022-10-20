package left4managerFunction;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Vector;
import java.awt.event.*;
import java.io.File;

import javax.swing.table.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

public class Gui {

	private JFrame frame;
	private JTable table;
	Config config = new Config("C:\\Program Files (x86)\\Steam\\steamapps\\common\\Left 4 Dead 2\\", System.getProperty("user.home") +File.separator +".left4manager");
	ExtractModList extractModList = new ExtractModList(config);
	UpdateModFile updateModFile = new UpdateModFile(config);


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
		try {
			extractModList.populateModList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
		debug();	
	}
	
	private void debug() {		
		//extractModList.addObjectToJsonDebug(extractModList.getModList().get(0));
		//updateModFile.setFileName("addonlist2.txt");
		//updateModFile.setDirectory(config.getL4D2Dir() +File.separator +"left4dead2" +File.separator);
		//ModGroup group1 = new ModGroup("gruppo1", extractModList);
		//System.out.println();
		//System.out.print(group1.getGroupName());
		//group1.addModToList(0);
		//System.out.print(group1.getGroupMod(0).getEnabled());
		//extractModList.getModList().get(0).setEnabled(true);;
		//System.out.print(group1.getGroupMod(0).getEnabled());
		//group1.getGroupMod(0).setEnabled(false);
		//System.out.print(extractModList.getModList().get(0).getEnabled());
		//System.out.print(group1.getGroupMod(0).getEnabled());
		//extractModList.getModList().get(0).setEnabled(true);
	}
	
	//https://www.javatpoint.com/java-jtabbedpane  
	public JTabbedPane createTabbedPane() {
	    JPanel tab2 = new JPanel();  
	    JPanel tab3 = new JPanel();  
	    JPanel tab4 = new JPanel();
	    JTabbedPane tabbedPane = new JTabbedPane();  
	    tabbedPane.setBounds(50,50,200,200);  
	    tabbedPane.add("List", createListTab());  
	    tabbedPane.add("Group", tab2);  
	    tabbedPane.add("Order", tab3);
	    tabbedPane.add("Options", tab4);
		return tabbedPane;
	}
	
	public JPanel createListTab() {
		JPanel listPane = new JPanel(); 
		listPane.setBackground(Color.cyan);
		listPane.setLayout(new GridBagLayout());
		JCheckBox enableAll = new JCheckBox("Enable all");  
		enableAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
		enableAll.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				if(enableAll.isSelected()) enableAll(true);
				else enableAll(false);
				
			}  
		});  
		
		
		JPanel leftPane = new JPanel(); 
		leftPane.setBackground(Color.green);
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		leftPane.add(enableAll);
		leftPane.add(createTable());
		
		
		JPanel rightPane = new JPanel(); 
		rightPane.setBackground(Color.red);
		
		JButton saveButton = new JButton("Save");  
		saveButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				updateModFile.writeFile(updateModFile.buildString(extractModList.getModList()));
	        }  
	    });  
		rightPane.add(saveButton);
		
		
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.weightx = 0.4;
		c.weighty = 1;
		listPane.add(leftPane, c);
		
		c.gridx = 1;
		c.weightx = 0.6;
		listPane.add(rightPane, c);
		return listPane;
	}

	public JScrollPane createTable() {
		
		JTable table = new JTable(createTableModel());
		table.setFont(new Font("Arial MS Unicode", Font.PLAIN, 12));
		JScrollPane scrollPane = new JScrollPane(table); 
		
		
		return scrollPane;
	}
	
	private void initialize() {		
		frame = new JFrame();
		frame.setBounds(200, 200, 850, 600);
		frame.add(createTabbedPane());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private DefaultTableModel createTableModel() {
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
		
		model.addTableModelListener(new TableModelListener() {	
			@Override
			public void tableChanged(TableModelEvent tme) {
				if (tme.getType() == TableModelEvent.UPDATE) {
					boolean newValue = (Boolean) model.getValueAt(tme.getFirstRow(),tme.getColumn());
					extractModList.getModList().get(tme.getFirstRow()).setEnabled(newValue);
				}
			}
		});
		
		List<ModInfo> modList = extractModList.getModList();
		for(int i=0; i<modList.size(); i++) {
			model.addRow(modList.get(i).getObject());
		}
		
		return model;
	}
	
	private void enableAll(boolean value) {
		DefaultTableModel model = createTableModel();
		for(int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt(Boolean.valueOf(value), i, 3);
		}
		System.out.print(model.getValueAt(0,2)); 
	}
}
