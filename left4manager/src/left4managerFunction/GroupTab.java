package left4managerFunction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import left4managerFunction.TableModels.GroupListTableModel;
import left4managerFunction.TableModels.GroupModTableModel;

public class GroupTab extends JPanel {
	Config config;
	ModList modList;
	GroupModTableModel modListModel;
	private GroupListTableModel groupListModel;
	ModGroupPopUp newModGroupPopUp;
	JTable groupListTable;
	JTable modListTable;
	
	public GroupTab(Config config, ModList modList) {
		super();
		this.config = config;
		this.modList = modList;
				
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_END;
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1; 
		add(new ModListPanel(), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.1;
		c.weighty = 1;
		c.gridx = 0; 
		add(new ColumnPanel(), c);
		
	}

	public class ColumnPanel extends JPanel {
		
		public ColumnPanel() {
			super();
			setLayout(new BorderLayout());
			groupListModel = new GroupListTableModel(config);
			/*try {
				groupListModel.add(config.readModGroupFile());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
			groupListTable = new JTable(groupListModel);
			groupListTable.setDefaultEditor(String.class, new TextFieldCellEditor());
			groupListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			groupListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
		        public void valueChanged(ListSelectionEvent event) {
					int selectionIndex = groupListTable.getSelectedRow();
					modListModel.clear();
					if (selectionIndex < 0 || selectionIndex > groupListModel.getRowCount() - 1) {
						selectionIndex = 0;
					}
					if (groupListModel.getRowCount() > 0) {
						List<ModInfo> newModList = new ArrayList<ModInfo>();
						ModGroup selectedGroup = groupListModel.getRow(selectionIndex);
						for (int i = 0; i < selectedGroup.getGroupModList().size(); i++) {
							ModInfo singleMod = modList.getModInfoByCode(selectedGroup.getGroupMod(i));
							newModList.add(singleMod);
						}
						modListModel.add(newModList);
					}
		        }
		    });
			groupListTable.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseReleased(MouseEvent e) {
			        if (SwingUtilities.isRightMouseButton(e)) {      
				        int r = groupListTable.rowAtPoint(e.getPoint());
				        System.out.println(r);
				        if (r < 0 || r >= groupListTable.getRowCount()) {
				        	System.out.println("ciao");
				        	groupListTable.setRowSelectionInterval(0, 0);
				        }
				        else {
				        	groupListTable.setRowSelectionInterval(r, r);
				        }
				        
				        int selectedRow = groupListTable.getSelectedRow();
				        
				        if (e.isPopupTrigger() && e.getComponent() instanceof JTable ) {
				            JPopupMenu popupMenu = new JPopupMenu();
				            JMenuItem renameButton = new JMenuItem("Rename");
							JMenuItem addGroupButton = new JMenuItem("Add group");
							JMenuItem removeGroupButton = new JMenuItem("Remove group");
							popupMenu.add(renameButton);
							popupMenu.add(addGroupButton);
							popupMenu.add(removeGroupButton);
							renameButton.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									groupListModel.setRowEditable(selectedRow, true);
									groupListTable.editCellAt(selectedRow, 0);
									
								}
							});
							addGroupButton.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									new ModGroupPopUp(-2);
								}
							});
							removeGroupButton.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									groupListModel.remove(selectedRow);
									if (selectedRow < 0 | selectedRow >= groupListModel.getRowCount()) {
										groupListTable.setRowSelectionInterval(0, 0);
									}
								}
							});
				            
				            popupMenu.show(e.getComponent(), e.getX(), e.getY());
				        }
			        }
			    }
			});
			JScrollPane listScrollPane = new JScrollPane(groupListTable);
			listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BorderLayout());

			JButton addGroup = new JButton("Add group");
			addGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new ModGroupPopUp(-2);
				}
			});
			JButton removeGroup = new JButton("Remove group");
			removeGroup.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int index = groupListTable.getSelectedRow();
					groupListModel.remove(index);
					if (index > 0 | index >= groupListModel.getRowCount()) {
						groupListTable.setRowSelectionInterval(0, 0);
					}
				}
			});
			buttonPanel.add(addGroup, BorderLayout.LINE_START);
			buttonPanel.add(removeGroup, BorderLayout.LINE_END);
			add(buttonPanel, BorderLayout.PAGE_END);
			add(listScrollPane, BorderLayout.CENTER);
			setPreferredSize(new Dimension(100, 1000));
			groupListTable.setRowSelectionInterval(0, 0);
		}
		
	}
	
	public class ModListPanel extends JPanel {
		
		public ModListPanel() {
			super();
			setLayout(new BorderLayout());
			modListModel = new GroupModTableModel();
			modListTable = new JTable(modListModel);
			modListTable.getColumnModel().getColumn(0).setPreferredWidth(300);
			modListTable.getColumnModel().getColumn(1).setPreferredWidth(50);
			modListTable.getColumnModel().getColumn(3).setPreferredWidth(30);
			TableRowSorter<GroupModTableModel> sorter = new TableRowSorter<>(modListModel);
			modListTable.setRowSorter(sorter);
			modListTable.setShowGrid(false);
			modListTable.setShowHorizontalLines(false);
			modListTable.setShowVerticalLines(false);
			modListTable.setRowMargin(0);
			modListTable.setIntercellSpacing(new Dimension(0, 0));
			modListTable.setFillsViewportHeight(true);
			JScrollPane tableScrollPane = new JScrollPane(modListTable);
			
			JPanel selectAllPane = new JPanel();
			JCheckBox selectAllCheckBox = new JCheckBox("Select all");
			selectAllCheckBox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean selectedBool = selectAllCheckBox.isSelected();
					for (int i = 0; i < modListModel.getRowCount(); i++) {
						modListModel.setValueAt(selectedBool, i, 3);
					}
				}
			});
			selectAllPane.add(selectAllCheckBox);
			
			JPanel addRemoveModPane = new JPanel();
			JButton addModButton = new JButton("Add mod");
			addModButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (groupListTable.getSelectedRow() >= 0) {
						new ModGroupPopUp(groupListTable.getSelectedRow());
					}
				}
			});
			JButton removeModButton = new JButton("Remove mod");
			removeModButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(modListTable.getSelectedRows().length > 0) {
						List<ModInfo> selectedRows = new ArrayList<ModInfo>();
						for (int row : modListTable.getSelectedRows()) {
							int modelRowIndex = modListTable.convertRowIndexToModel(row);
							ModInfo rowValue = modListModel.getRow(modelRowIndex);
							selectedRows.add(rowValue);
						}
						
						for (ModInfo rowValue : selectedRows) {
							int rowIndex = modListModel.getIndexByModInfo(rowValue);
							modListModel.remove(rowIndex);
						}
						//config.writeModGroupFile(groupListModel.getList());
					}
				}
			});
			addRemoveModPane.add(addModButton);
			addRemoveModPane.add(removeModButton);
			add(selectAllPane, BorderLayout.PAGE_START);
			add(tableScrollPane, BorderLayout.CENTER);
			add(addRemoveModPane, BorderLayout.PAGE_END);
		}
	}
	/***
	 * Create a popup to either add or modify the mods in a mod group
	 * 
	 * @param selectedIndex -2: add new group, > 1: index of the ModGroup listModel
	 *                      to modify
	 */
	public class ModGroupPopUp extends JFrame {
		GroupModTableModel allModModel;
		GroupModTableModel newGroupModel;
		public ModGroupPopUp(int selectedIndex) {
			super("L4M: Add new group");  
			try {
				BufferedImage icon = ImageIO.read(new File(config.getL4managerDir() +File.separator +"icon" +File.separator +"icon.png"));
				this.setIconImage(icon);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			allModModel = new GroupModTableModel(1);
			newGroupModel = new GroupModTableModel(1);
			setBounds(0, 0, 800, 500);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
			setVisible(true);

			JPanel groupNamePane = new JPanel();
			groupNamePane.setLayout(new BorderLayout());
			groupNamePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			JLabel groupNameLabel = new JLabel("Group name");
			groupNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
			JTextField groupNameInput = new JTextField();
			groupNameInput.setText("Group" + (groupListModel.getRowCount() + 1));

			if (selectedIndex != -2) {
				this.setTitle("Modify group");
				groupNamePane.add(groupNameLabel, BorderLayout.LINE_START);
				groupNameInput.setText(groupListModel.getGroupName(selectedIndex));
				groupNamePane.add(groupNameInput);
				for (int i = 0; i < groupListModel.getRow(selectedIndex).getSize(); i++) {
					newGroupModel.add(modList.getModInfoByCode(groupListModel.getRow(selectedIndex).getGroupMod(i)));
				}
			} else {
				groupNamePane.add(groupNameLabel, BorderLayout.LINE_START);
				groupNamePane.add(groupNameInput);
			}

			JPanel tablePane = new JPanel();
			tablePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			tablePane.setLayout(new BoxLayout(tablePane, BoxLayout.LINE_AXIS));

			JTable allModTable = new JTable(allModModel);
			TableRowSorter<GroupModTableModel> sorter = new TableRowSorter<>(allModModel);
			allModTable.setRowSorter(sorter);
			allModModel.add(modList.getModList());
			if (selectedIndex != -2) {
				ModGroup group = groupListModel.getRow(selectedIndex);
				for(String modCode : group.getGroupModList()) {
					allModModel.remove(allModModel.getIndexByCode(modCode));
				}
			}

			JTable newGroupTable = new JTable(newGroupModel);
			TableRowSorter<GroupModTableModel> sorter2 = new TableRowSorter<>(newGroupModel);
			newGroupTable.setRowSorter(sorter2);

			JPanel swapButtonsPanel = new JPanel();
			swapButtonsPanel.setLayout(new BoxLayout(swapButtonsPanel, BoxLayout.PAGE_AXIS));
			JButton addButton = new JButton("Add");
			addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			//TODO problem with multiple selection when moving mods with sorted tables
			addButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<ModInfo> selectedRows = new ArrayList<ModInfo>();
					for (int row : allModTable.getSelectedRows()) {
				        int modelRowIndex = allModTable.convertRowIndexToModel(row);
				        ModInfo rowValue = allModModel.getRow(modelRowIndex);
				        selectedRows.add(rowValue);
				    }
					
					for (ModInfo rowValue : selectedRows) {
				        int rowIndex = allModModel.getIndexByModInfo(rowValue);
				        allModModel.remove(rowIndex);
				        newGroupModel.add(rowValue);
				    }
				}
			});
			JButton removeButton = new JButton("Remove");
			removeButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					List<ModInfo> selectedRows = new ArrayList<ModInfo>();
					for (int row : newGroupTable.getSelectedRows()) {
				        int modelRowIndex = newGroupTable.convertRowIndexToModel(row);
				        ModInfo rowValue = newGroupModel.getRow(modelRowIndex);
				        selectedRows.add(rowValue);
				    }
					
					for (ModInfo rowValue : selectedRows) {
				        int rowIndex = newGroupModel.getIndexByModInfo(rowValue);
				        newGroupModel.remove(rowIndex);
				        allModModel.add(rowValue);
				    }
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
			JScrollPane scrollPane2 = new JScrollPane(newGroupTable);
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
					if (groupNameInput.getText().equals("")) {
						JOptionPane.showMessageDialog(groupNameInput, "Mod groups need to be named");
					} else {
						String groupName = groupNameInput.getText();
						if (selectedIndex != -2) {
							groupListModel.remove(selectedIndex);
						}
						ModGroup newGroup = new ModGroup(groupName);
						for (int i = 0; i < newGroupModel.getRowCount(); i++) {
							newGroup.add(newGroupModel.getRow(i).getCode());
						}
						groupListModel.add(newGroup);
						dispose();
					}
				}
			});

			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			buttonsPane.add(cancelButton);
			buttonsPane.add(applyButton);

			add(groupNamePane);
			add(tablePane);
			add(buttonsPane);
		}
	}
	
	public GroupListTableModel getGroupListModel() {
		return this.groupListModel;
	}
	
	public void createPopUpMenu(int index) {
		new ModGroupPopUp(index);
	}
	
	public class TextFieldCellEditor extends DefaultCellEditor {
	    public TextFieldCellEditor() {
	    	super(new JTextField());
	    }

	    // Retrieve e dited value
	    @Override
	    public Object getCellEditorValue() {
			System.out.println("getCellEditorValue");
			String text = ((JTextField) getComponent()).getText();
			System.out.println(text);

			return text;
	    }

	    @Override
	    public boolean stopCellEditing() {
			System.out.println("stopCellEditing");
			boolean value = super.stopCellEditing();
			groupListModel.setRowEditable(groupListTable.getSelectedRow(), false);
	        return value;
	    }

	    @Override
	    public void cancelCellEditing() {
			System.out.println("cancelCellEditing");
			super.stopCellEditing();
			groupListModel.setRowEditable(groupListTable.getSelectedRow(), false);
	    }
		@Override
		public boolean isCellEditable(EventObject anEvent) {
			System.out.println(groupListTable.getSelectedRow());

			return groupListModel.isCellEditable(groupListTable.getSelectedRow() , 0);
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			System.out.println("getTableCellEditorComponent");
			Component textField = super.getTableCellEditorComponent(table, value, isSelected, row, column);
			if (isSelected) {
				((JTextField) textField).selectAll();
			}
			return textField;
		}
	}
}

		




