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

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

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
	
	Config config = new Config();
	ExtractModList extractModList;
	UpdateModFile updateModFile;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Gui() {
		config.readFile();
		if(config.getL4D2Dir().isEmpty()) {
			chooseDirectoryWindow();
		}
		else {
			initialize();
		}
	}
	
	private void debug() {	
		//config.writeFile();
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
	
	public JScrollPane createTable() {
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
		JTable table = new JTable(model);
		JScrollPane scrollPane = new JScrollPane(table); 
		
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
		
		return scrollPane;
	}
	
	public void chooseDirectoryWindow() {
		String steamFolder = new String(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath"));
		File l4d2Folder = new File(steamFolder +File.separator +"steamapps" +File.separator +"common" +File.separator +"Left 4 Dead 2" +File.separator);
		
		JFrame chooseDirectory = new JFrame("Choose directory");
		chooseDirectory.setVisible(true);
		chooseDirectory.setBounds(0, 0, 500, 150);	
		chooseDirectory.setLocationRelativeTo(null);
		chooseDirectory.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel mainContainer = new JPanel();
		JPanel directoryBox = new JPanel();
		
		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.PAGE_AXIS));
		directoryBox.setLayout(new BoxLayout(directoryBox, BoxLayout.LINE_AXIS));
		
		JButton applyButton = new JButton("Apply");
		Icon folderIcon = new ImageIcon(".\\icon\\Open16.gif");
		JButton folderButton = new JButton(folderIcon);
		JLabel label = new JLabel("Select Left4Dead2 folder:");
		JTextField input = new JTextField();
		input.setText(l4d2Folder.getPath());
		
		mainContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		directoryBox.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		
		directoryBox.add(input);
		directoryBox.add(folderButton);
		chooseDirectory.add(mainContainer);
		mainContainer.add(label);
		mainContainer.add(directoryBox);
		mainContainer.add(applyButton);
		
		applyButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	config.setL4D2Dir(input.getText());
		    	File file = new File(config.getL4D2Dir());
		    	if(file.getName().equals("Left 4 Dead 2")) {
		    		initialize();
		    		config.writeFile();
		    		chooseDirectory.dispose();
		    	}
		    	else {
		    		JOptionPane.showMessageDialog(frame,
		    			    "The selected folder doesn't seem to be a Left 4 Dead 2 folder",
		    			    "Invalid folder",
		    			    JOptionPane.WARNING_MESSAGE);
		    	}
		    }
		});
		
		folderButton.addActionListener(new ActionListener() {

		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	File defaultFolder = new File (input.getText());
		    	fileChooserWindow(defaultFolder, input);
		    }
		});
	}
	
	public void fileChooserWindow(File defaultFolder, JTextField input) {
		JFileChooser fileChooser = new JFileChooser();
		
		fileChooser.setDialogTitle("Choose Left 4 Dead 2 directory");
		fileChooser.setCurrentDirectory(defaultFolder);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		//fileChooserFrame.add(fileChooser);
		//fileChooserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		int result = fileChooser.showDialog(fileChooser, "Apply");
		System.out.println(result);
		if (result == JFileChooser.APPROVE_OPTION) {
			input.setText(fileChooser.getSelectedFile().getAbsolutePath());
			System.out.println(fileChooser.getSelectedFile());
		} else if (result == JFileChooser.CANCEL_OPTION) {
		    System.out.println("Cancel was selected");
		}
	}
	
	private void initialize() {		
		extractModList = new ExtractModList(config);
		updateModFile = new UpdateModFile(config);
		
		try {
			extractModList.populateModList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		frame = new JFrame("Left4Manager");
		frame.setBounds(200, 200, 1080, 720);
		frame.setLocationRelativeTo(null);
		frame.add(createTabbedPane());
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		debug();
	}
	
	public JTabbedPane createTabbedPane() {
		JPanel tab1 = new JPanel();  
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
		JCheckBox selectAll = new JCheckBox("Select all");  
		selectAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel leftPane = new JPanel(); 
		leftPane.setBackground(Color.green);
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		leftPane.add(selectAll);
		leftPane.add(createTable());
		
		JPanel rightPane = new JPanel(); 
		rightPane.setBackground(Color.red);
		JButton saveButton = new JButton("Save");
		rightPane.add(saveButton);
		
		saveButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				updateModFile.writeFile(updateModFile.buildString(extractModList.getModList()));
	        }  
	    });  
		
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
	
	//TODO riscrivere
	/*private void enableAll(boolean value) {
		DefaultTableModel model = createTableModel();
		for(int i = 0; i < model.getRowCount(); i++) {
			model.setValueAt(Boolean.valueOf(value), i, 3);
		}
		System.out.print(model.getValueAt(0,2)); 
	}*/
}
