package left4managerFunction;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.table.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

public class Gui {

	private JFrame frame;
	JLabel imgLabel = new JLabel();
	JTextPane textDescription = new JTextPane();
	Config config = new Config();
	ExtractModList extractModList;
	UpdateModFile updateModFile;
	GroupListModel listModel = new GroupListModel();

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
	
	public DefaultTableModel createOrderModel() {
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			
			@Override
		    public boolean isCellEditable(int row, int column) {
				switch (column) {
		         	case 0:
		         		return true;
		         default:
		             return false;
		      }
		    }
			
			@Override
	         public Class getColumnClass(int columnIndex) {
				switch (columnIndex) {
	                case 0:
	                	return String.class;
	                case 1:
	                case 2:
	                    return String.class;
	                default:
	                    return String.class;
				}
	         }
		}; 
	
		model.addColumn("order");
		model.addColumn("name");
		model.addColumn("code");
		model.addColumn("author");
		
		model.addTableModelListener(new TableModelListener() {	
			@Override
			public void tableChanged(TableModelEvent tme) {
				if (tme.getType() == TableModelEvent.UPDATE) {
					System.out.println(tme.getFirstRow() + tme.getColumn() + (String) model.getValueAt(tme.getFirstRow(), tme.getColumn()));
					extractModList.moveToIndex(tme.getFirstRow(), Integer.parseInt((String) model.getValueAt(tme.getFirstRow(), tme.getColumn())) - 1);
					//TODO refresh
				}
			}
		});

		List<ModInfo> modList = extractModList.getModList();
		for(int i = 0; i < modList.size(); i++) {
			model.addRow(new Object[]{i + 1, modList.get(i).getName(), modList.get(i).getCode(), modList.get(i).getAuthor()});
		}
		
		return model;
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
	    tabbedPane.add("Group", createGroupTab());  
	    tabbedPane.add("Order", createOrderTab());
	    tabbedPane.add("Options", tab4);
		return tabbedPane;
	}
	
	public JPanel createListTab() {
		JPanel listPane = new JPanel(); 
		listPane.setBackground(Color.cyan);
		listPane.setLayout(new GridLayout(0, 2));
		JCheckBox selectAll = new JCheckBox("Select all");  
		selectAll.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel leftPane = new JPanel(); 
		leftPane.setBackground(Color.green);
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		leftPane.add(selectAll);
		
		GroupModTableModel model = new GroupModTableModel();
		model.add(extractModList.getModList());
		TableRowSorter<GroupModTableModel> sorter = new TableRowSorter<>(model);
		JTable table = new JTable(model);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setRowSorter(sorter);

		leftPane.add(new JScrollPane(table));
		
		JPanel rightPane = new JPanel(); 
		JPanel descriptionPane = new JPanel(); 
		descriptionPane.add(imgLabel);
		descriptionPane.add(textDescription);	
		rightPane.setBackground(Color.red);
		JButton saveButton = new JButton("Save");
		rightPane.add(new JScrollPane(descriptionPane));
		rightPane.add(saveButton);
		
		selectAll.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				TableModel model = table.getModel();
				boolean selectedBool = selectAll.isSelected();
					for(int i = 0; i < extractModList.getModList().size(); i++) {
						model.setValueAt(selectedBool, i, 3);
					}
	        }  
	    });  
		
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	String code = table.getValueAt(table.getSelectedRow(), 1).toString();
		    	BufferedImage img = null;
				try {
					img = ImageIO.read(new File(config.getL4D2Dir() +File.separator +"left4dead2" +File.separator +"addons" +File.separator +"workshop" +File.separator +code +".jpg"));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				imgLabel.setIcon(new ImageIcon(img));
				//imgLabel.setIcon(new ImageIcon(img.getScaledInstance(100, 100, Image.SCALE_DEFAULT)));
				//textDescription.setText(extractModList.getModList().get(table.getSelectedRow()).getDescription());
				textDescription.setEditable(false);
			}
		});
			
		saveButton.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				updateModFile.writeFile(updateModFile.buildString(extractModList.getModList()));
	        }  
	    });  

		listPane.add(leftPane);
		listPane.add(rightPane);
		return listPane;
	}
	
	
	public JPanel createOrderTab() {
		JPanel mainPane = new JPanel();
				
		DefaultTableModel model = createOrderModel();
		JTable table = new JTable(model);
		
		mainPane.add(new JScrollPane(table));
		
		JPanel buttonPane = new JPanel();
		JButton up = new JButton("Up");	
		up.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				changeModOrder(table, 1);
			}  
		}); 
		buttonPane.add(up);
		
		JButton down = new JButton("Down");	
		down.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				changeModOrder(table, -1);
			}  
		}); 
		buttonPane.add(down);
		
		JButton top = new JButton("Top");	
		top.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				changeModOrder(table, true);
			}  
		}); 
		buttonPane.add(top);
		
		JButton bottom = new JButton("Bottom");	
		bottom.addActionListener(new ActionListener(){  
			public void actionPerformed(ActionEvent e){  
				changeModOrder(table, false);
			}  
		}); 
		buttonPane.add(bottom);
	
		mainPane.add(buttonPane);
		
		return mainPane;
	}
	

	public void changeModOrder(JTable table, int positionsMoved){
		int finalPosition = table.getSelectedRow() - positionsMoved;
		extractModList.moveToIndex(table.getSelectedRow(), finalPosition);
		table.setModel(createOrderModel());
		table.setRowSelectionInterval(finalPosition, finalPosition);
	}
	
	public void changeModOrder(JTable table, boolean toTop){
		if(toTop) {
			extractModList.moveToTop(table.getSelectedRow());
			table.setModel(createOrderModel());
			table.setRowSelectionInterval(0, 0);
		}
		else {
			extractModList.moveToBottom(table.getSelectedRow());
			table.setModel(createOrderModel());
			table.setRowSelectionInterval(extractModList.getModList().size() - 1, extractModList.getModList().size() - 1);
		}
	}
	
	public static class GroupListModel extends AbstractListModel {
		private List<ModGroup> rowData;
		
		public GroupListModel() {
			rowData = new ArrayList<ModGroup>();
		}
		
		public GroupListModel(List<ModGroup> data) {
			this.rowData = new ArrayList<ModGroup>();
			add(data);
		}
		
		@Override
		public int getSize() {
			return rowData.size();
		}

		@Override
		public ModGroup getElementAt(int index) {
			return rowData.get(index);
		}
		
		public ModGroup getLastElement() {
			return rowData.get(getSize() - 1);
		}
		
		public ModGroup getFirstElement() {
			return rowData.get(0);
		}
		
		public void add(List<ModGroup> data) {
			int oldLenght = getSize() - 1;
			if(oldLenght < 0) {
				oldLenght = 0;
			}
			rowData.addAll(data);
			fireIntervalAdded(this, oldLenght, getSize() - 1);
		}
		
		public List<ModGroup> get(){
			return rowData;
		}
		
		public void add(ModGroup... pd) {
		    add(Arrays.asList(pd));
		}
		
		public void remove(int index) {
			rowData.remove(index);
			fireIntervalRemoved(this, index, index);
		}
		
		public void removeModByIndex(int groupIndex, int modIndex) {
			rowData.get(groupIndex).remove(modIndex);
		}
		
		public void removeModByIndex(int groupIndex, int[] index) {
			for(int i = 0; i < index.length; i++) {
				rowData.get(groupIndex).remove(index[i] - i);
			}
		}
	
		public void clear() {
			rowData.clear();
			fireIntervalRemoved(this, 0, getSize());
		}
	}
	
	public class GroupListRenderer extends JLabel implements ListCellRenderer<ModGroup> {
		@Override
		public Component getListCellRendererComponent(JList<? extends ModGroup> list, ModGroup value, int index,
				boolean isSelected, boolean cellHasFocus) {
				setText(value.getGroupName());
				if (isSelected) {
					setBackground(list.getSelectionBackground());
					setForeground(list.getSelectionForeground());
				} else {
					setBackground(list.getBackground());
					setForeground(list.getForeground());
		        }
				setEnabled(list.isEnabled());
				setFont(list.getFont());
				setOpaque(true);
				return this;
		}
	}
	
	public JPanel createGroupTab() {
		GroupModTableModel model = new GroupModTableModel(); 
		JTable table = new JTable(model);
		
		try {
			listModel.add(config.readModGroupFile());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
				
		JPanel groupPanel = new JPanel();
		
		groupPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel column = new JPanel();
		column.setLayout(new BorderLayout());
		column.setBackground(Color.red);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.10;
		c.weighty = 1;
		
		JList groupJList = new JList(listModel);
		groupJList.setCellRenderer(new GroupListRenderer());
		groupJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
		
		groupJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
            	int selectionIndex = groupJList.getMinSelectionIndex();
            	model.clear();
            	if(selectionIndex < 0 || selectionIndex > listModel.getSize() - 1) {
            		selectionIndex = 0;
            	}
            	if(listModel.getSize() > 0) {
            		List<ModInfo> newModList = new ArrayList<ModInfo>();
            		List<ModInfo> modList = extractModList.getModList();
            		System.out.println(selectionIndex);
            		ModGroup selectedGroup = listModel.getElementAt(selectionIndex);
            		for(int i = 0; i < selectedGroup.getGroupModList().size(); i++) {
            			int index = extractModList.getModIndexByCode(selectedGroup.getGroupMod(i));
            			if(index >= 0) {
            				newModList.add(modList.get(index));
            			}
            		}
            		model.add(newModList);
            	}
	        }
	    });
		
		groupJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupJList.setSelectedIndex(0);
		groupJList.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(groupJList);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        
        JButton addGroup = new JButton("Add group");
        addGroup.addActionListener(new ActionListener() {
        	
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		addNewGroupPopUp(-2);
        	}
        });
        
        JButton removeGroup = new JButton("Remove group");
        removeGroup.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		int index = groupJList.getMinSelectionIndex();
        		if (index >= 0) {
        			listModel.remove(index);
        			if(index > 0) {
        				groupJList.setSelectedIndex(index);
        			}
        			config.writeModGroupFile(listModel);
        		}
        	}
        });
        
        buttonPanel.add(addGroup, BorderLayout.LINE_START);
        buttonPanel.add(removeGroup, BorderLayout.LINE_END);
        column.add(buttonPanel, BorderLayout.PAGE_END);
        
        
		column.add(listScrollPane, BorderLayout.CENTER);
		groupPanel.add(column, c);
		
		TableRowSorter<GroupModTableModel> sorter = new TableRowSorter<>(model);
		table.setRowSorter(sorter);
		table.setShowGrid(false);
		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setRowMargin(0);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setFillsViewportHeight(true);
		JScrollPane tableScrollPane = new JScrollPane(table);
		
		JPanel column2 = new JPanel();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_END; //bottom of space
		c.weightx = 1;
		c.weighty = 1;
		
		JPanel selectAllPane = new JPanel();
		JCheckBox selectAllCheckBox = new JCheckBox("Select all");  
		selectAllCheckBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){  
				boolean selectedBool = selectAllCheckBox.isSelected();
					for(int i = 0; i < model.getRowCount(); i++) {
						model.setValueAt(selectedBool, i, 3);
				}
	        }  
	    });  
		selectAllPane.add(selectAllCheckBox);
		
		JPanel addRemoveModPane = new JPanel();
		JButton addModButton = new JButton("Add mod");
		addModButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(groupJList.getMinSelectionIndex() >= 0) {
        			addNewGroupPopUp(groupJList.getMinSelectionIndex());
        		}
        	}
        });
		JButton removeModButton = new JButton("Remove mod");
		removeModButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
            	int[] selectedRows = table.getSelectedRows();
            	if(selectedRows.length > 0) {
            		listModel.removeModByIndex(groupJList.getMinSelectionIndex(), selectedRows);
            		model.remove(selectedRows);
            		config.writeModGroupFile(listModel);
            	}
			}
        });
		addRemoveModPane.add(addModButton);
		addRemoveModPane.add(removeModButton);
		
		column2.setLayout(new BorderLayout());
		column2.add(selectAllPane, BorderLayout.PAGE_START);
		column2.add(tableScrollPane, BorderLayout.CENTER);
		column2.add(addRemoveModPane, BorderLayout.PAGE_END);
		groupPanel.add(column2, c);
		
		return groupPanel;
	}
	
	public void addNewGroupPopUp(int selectedIndex) {
		GroupModTableModel allModModel = new GroupModTableModel(1); 
		GroupModTableModel newGroupModel = new GroupModTableModel(1); 
		
		JFrame addNewGroupFrame = new JFrame("Add new group");
		addNewGroupFrame.setVisible(true);
		addNewGroupFrame.setBounds(0, 0, 800, 500);	
		addNewGroupFrame.setLocationRelativeTo(null);
		addNewGroupFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addNewGroupFrame.setLayout(new BoxLayout(addNewGroupFrame.getContentPane(), BoxLayout.PAGE_AXIS));
		
		JPanel groupNamePane = new JPanel();
		groupNamePane.setLayout(new BorderLayout());
		JLabel groupNameLabel = new JLabel("Group name");
		JTextField groupNameInput = new JTextField();
		groupNameInput.setText("Group" +(listModel.getSize() + 1));

		if(selectedIndex != -2) {
			addNewGroupFrame.setTitle("Modify group");
			groupNameLabel.setText(listModel.getElementAt(selectedIndex).getGroupName());
			groupNamePane.add(groupNameLabel, BorderLayout.LINE_START);
			for(int i = 0; i < listModel.getElementAt(selectedIndex).getSize(); i++) {
				newGroupModel.add(extractModList.getModInfoByCode(listModel.getElementAt(selectedIndex).getGroupMod(i)));
			}
		}
		else {
			groupNamePane.add(groupNameLabel, BorderLayout.LINE_START);
			groupNamePane.add(groupNameInput);
		}
		
		JPanel tablePane = new JPanel();
		tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.LINE_AXIS));
		
		JTable allModTable = new JTable(allModModel);
		TableRowSorter<GroupModTableModel> sorter = new TableRowSorter<>(allModModel);
		allModTable.setRowSorter(sorter);
		allModModel.add(extractModList.getModList());
		
		JTable groupModTable = new JTable(newGroupModel);
		TableRowSorter<GroupModTableModel> sorter2 = new TableRowSorter<>(newGroupModel);
		groupModTable.setRowSorter(sorter2);
		
		JPanel swapButtonsPanel = new JPanel();
		swapButtonsPanel.setLayout(new BoxLayout(swapButtonsPanel, BoxLayout.PAGE_AXIS));
		JButton addButton = new JButton("Add");
		addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		addButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
            	int[] selectedRows = allModTable.getSelectedRows();
					newGroupModel.add(allModModel.getRow(selectedRows));
					allModModel.remove(selectedRows);
        	}
        });
		JButton removeButton = new JButton("Remove");
		removeButton.addActionListener(new ActionListener() {
			@Override
        	public void actionPerformed(ActionEvent e) {
            	int[] selectedRows = groupModTable.getSelectedRows();
        		allModModel.add(newGroupModel.getRow(selectedRows));
        		newGroupModel.remove(selectedRows);
			}
		});
		removeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		swapButtonsPanel.add(addButton);
		swapButtonsPanel.add(removeButton);
				
		JPanel leftTable = new JPanel();
		leftTable.setLayout(new BoxLayout(leftTable, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane = new JScrollPane(allModTable);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel label = new JLabel("All mods");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftTable.add(label);
		leftTable.add(scrollPane);

		JPanel rightTable = new JPanel();
		rightTable.setLayout(new BoxLayout(rightTable, BoxLayout.PAGE_AXIS));
		JScrollPane scrollPane2 = new JScrollPane(groupModTable);
		scrollPane2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane2.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel label2 = new JLabel("New group mods");
		label2.setAlignmentX(Component.CENTER_ALIGNMENT);
		rightTable.add(label2);
		rightTable.add(scrollPane2);
		
		tablePane.add(leftTable);
		tablePane.add(swapButtonsPanel);
		tablePane.add(rightTable);
		
		JPanel buttonsPane = new JPanel();
		JButton applyButton = new JButton("Confirm");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(groupNameInput.getText().equals("")) {
					JOptionPane.showMessageDialog(addNewGroupFrame, "Mod groups need to be named");
				}
				else {
					String groupName = groupNameInput.getText();
					if(selectedIndex != -2) {
						groupName = listModel.getElementAt(selectedIndex).getGroupName();
						listModel.remove(selectedIndex);
					}
					ModGroup newGroup = new ModGroup(groupName);
					for(int i = 0; i < newGroupModel.getRowCount(); i++) {
						newGroup.add(newGroupModel.getRow(i).getCode());
					}
					listModel.add(newGroup);
					config.writeModGroupFile(listModel);
					addNewGroupFrame.dispose();
				}
			}
		});

		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addNewGroupFrame.dispose();
			}
		});
		buttonsPane.add(cancelButton);
		buttonsPane.add(applyButton);

		addNewGroupFrame.add(groupNamePane);
		addNewGroupFrame.add(tablePane);
		addNewGroupFrame.add(buttonsPane);
	}
	
	public static class GroupModTableModel extends AbstractTableModel {
		
		protected static final String[] COLUMN_NAMES = {
				"Name",
				"Code",
				"Author",
				"Enabled"
		};
		
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
		
		public void add(List<ModInfo> mods) {
			rowData.addAll(mods);
			fireTableDataChanged();
		}
		
		public void add(ModInfo... pd) {
		    add(Arrays.asList(pd));
		}
		
		public void clear() {
			rowData.clear();
			fireTableDataChanged();
		}
		
		public void remove(int[] index) {
			for(int i = 0; i < index.length; i++) {
				remove(index[i] - i);
			}
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
		
		public List<ModInfo> getRow(int[] row){
			List<ModInfo> mods = new ArrayList<>();
			for(int i = 0; i < row.length; i++) {
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
	}
	
	/*public List<ModGroup> readModGroupFile() {
		List<ModGroup> groupList = new ArrayList<ModGroup>();

		try {
			Gson modGroup = new GsonBuilder().setPrettyPrinting().create(); 
	    	FileReader fr = new FileReader(config.getL4managerDir() +File.separator +"modGroup.json");
	        Type modGroupListType = new TypeToken<List<ModGroup>>(){}.getType();
	        groupList = modGroup.fromJson(fr, modGroupListType);
			System.out.println(groupList.get(0).getGroupName());

	        fr.close();


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groupList;
	}*/
}
