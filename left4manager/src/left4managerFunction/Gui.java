package left4managerFunction;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import javax.swing.table.*;

import com.connorhaigh.javavpk.core.Archive;
import com.connorhaigh.javavpk.core.ArchiveEntry;
import com.connorhaigh.javavpk.core.Directory;
import com.connorhaigh.javavpk.exceptions.ArchiveException;
import com.connorhaigh.javavpk.exceptions.EntryException;
import com.google.gson.stream.MalformedJsonException;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import left4managerFunction.L4Mexceptions.ConfigValueException;
import left4managerFunction.L4Mexceptions.InfoSourceException;
import left4managerFunction.L4Mexceptions.ModInfoNotFoundException;
import left4managerFunction.L4Mexceptions.NoConnectionException;
import left4managerFunction.TableModels.GroupListTableModel;
import left4managerFunction.TableModels.GroupModTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.RowFilter.Entry;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Gui {

	private JFrame frame;
	ModList modList;
	UpdateModFile updateModFile;
	AllTags allTags;
	Config config = new Config();
	//GroupListTableModel groupListModel = new GroupListTableModel();
	GroupTab groupTab;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Gui window = new Gui();
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Gui() {
		try {
			config.readConfig();
			checkConfigs();
		} catch (Exception e) {
			chooseDirectoryWindow();
		}

	}

	public void chooseDirectoryWindow() {
		String steamFolder = new String(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
				"SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath"));
		File l4d2Folder = new File(steamFolder + File.separator + "steamapps" + File.separator + "common"
				+ File.separator + "Left 4 Dead 2" + File.separator);

		CustomFrame chooseDirectory = new CustomFrame("L4M: Choose directory");
		JPanel mainContainer = new JPanel();
		JPanel directoryBox = new JPanel();

		mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.PAGE_AXIS));
		directoryBox.setLayout(new BoxLayout(directoryBox, BoxLayout.LINE_AXIS));

		JButton applyButton = new JButton("Apply");
		Icon folderIcon = new ImageIcon("." + File.separator + "icon" + File.separator + "Open16.gif");
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
				File l4d2Dir = new File(input.getText());
				if (l4d2Dir.getName().equals("Left 4 Dead 2")) {
					config.setL4D2Dir(l4d2Dir);
					checkConfigs();
					chooseDirectory.dispose();
				} else {
					JOptionPane.showMessageDialog(frame,
							"The selected folder doesn't seem to be a Left 4 Dead 2 folder", "Invalid folder",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		folderButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				File defaultFolder = new File(input.getText());
				fileChooserWindow(defaultFolder, input);
			}
		});
		chooseDirectory.setVisible(true);
	}

	public void fileChooserWindow(File defaultFolder, JTextField input) {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setDialogTitle("L4M: Choose Left 4 Dead 2 directory");
		fileChooser.setCurrentDirectory(defaultFolder);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		// fileChooserFrame.add(fileChooser);
		// fileChooserFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		int result = fileChooser.showDialog(fileChooser, "Apply");
		System.out.println(result);
		if (result == JFileChooser.APPROVE_OPTION) {
			input.setText(fileChooser.getSelectedFile().getAbsolutePath());
			System.out.println(fileChooser.getSelectedFile());
		} else if (result == JFileChooser.CANCEL_OPTION) {
			System.out.println("Cancel was selected");
		}
	}
	
	private void settingsWindow() {
		CustomFrame settingsFrame = new CustomFrame("L4M: Settings");
		JPanel mainFrame = new JPanel();
		mainFrame.add(new JPanel());
		
		JPanel row1 = new JPanel();
		row1.add(new JLabel("Offline mode"));
		JCheckBox offlineMode = new JCheckBox();
		row1.add(offlineMode);
		mainFrame.add(row1);
		
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				config.setConfigs(1, Boolean.toString(offlineMode.isSelected()));
				settingsFrame.dispose();
				checkConfigs();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				settingsFrame.dispose();
			}
		});
		mainFrame.add(cancelButton);
		mainFrame.add(applyButton);
		settingsFrame.add(mainFrame);
		settingsFrame.setVisible(true);
	}
	
	private void checkConfigs() {
		try {
			initialize();
		} catch (ConfigValueException e) {
			e.printStackTrace();
			settingsWindow();
		}
	}

	private void initialize() throws ConfigValueException {
		try {
			config.writeConfig();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		updateModFile = new UpdateModFile(config);
		allTags = new AllTags(config);
		modList = new ModList(config);
		
		CustomFrame loadingFrame = new CustomFrame("L4M: Mod info loading");
		loadingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		JProgressBar loadingBar = new JProgressBar(0, 100);
		loadingBar.setStringPainted(true);
		Color loadingBarColor = new Color(28, 99, 214);
		loadingBar.setForeground(loadingBarColor);
		JLabel modInfoLabel = new JLabel();
		JLabel fractionLabel = new JLabel();
		JPanel labelPanel = new JPanel();
		labelPanel.setLayout(new BorderLayout());
		labelPanel.add(modInfoLabel, BorderLayout.LINE_START);
		labelPanel.add(fractionLabel, BorderLayout.LINE_END);
		JPanel headerPanel = new JPanel();
		JLabel headerLabel = new JLabel("Fetching mod info. The first time could take a while");
		headerPanel.add(headerLabel);
		mainPanel.add(headerPanel);
		mainPanel.add(loadingBar);
		mainPanel.add(labelPanel);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		loadingFrame.add(mainPanel);
		loadingFrame.setVisible(true);
		
		class PopulateModListVPK extends SwingWorker<Void, Double> {	
			@Override
			protected Void doInBackground() throws Exception {
				List<ModInfo> l4d2ModList = new ArrayList<ModInfo>();
				
				try {
					l4d2ModList = modList.getL4D2ModList();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				double totalProgress = 0;
				int listSize = l4d2ModList.size();
				double progress = (100.0 / listSize);
				for(int i = 0; i < l4d2ModList.size(); i++) {
					ModInfo singleMod = l4d2ModList.get(i);
					boolean enabled = singleMod.getEnabled();
					String modName = "";

					try {
						ModInfo objectFromJson = Utilities.modInfoFromJson(modList.getJsonFile(), singleMod.getCode());
						System.out.println("Mod found in json");
						if(objectFromJson.getInfoSource() > 0) {
							objectFromJson.setEnabled(enabled);
							modList.getModList().add(objectFromJson);	
							modName = objectFromJson.getName();
						}
						else throw new InfoSourceException(objectFromJson.getInfoSource());
					} catch (InfoSourceException | ModInfoNotFoundException e) {
						System.out.println("Mod not found in json");
						File vpkArchive = new File(config.getL4D2Dir() +File.separator +"left4dead2" +File.separator
								+"addons" +File.separator +"workshop" +File.separator +singleMod.getCode() +".vpk");
						String vpkText = Utilities.getVPKInfo(vpkArchive);
						if(vpkText != null) {
							String[] vpkInfo = modList.parseVPKInfo(vpkText);
							singleMod.setInfo(vpkInfo[0], vpkInfo[1], vpkInfo[2], null);
							singleMod.setInfoSource((short) 1);
							modList.getModList().add(singleMod);
							modName = singleMod.getName();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					
					totalProgress += progress;
					setProgress((int) Math.round(totalProgress));
					modInfoLabel.setText("Getting info for " +modName);
					fractionLabel.setText("Mod " +(i + 1) +"/" +listSize);
				}
				
				groupTab = new GroupTab(config, modList);
				System.out.println(groupTab.getGroupListModel().getList());
				return null;
			}
			
			@Override
			protected void done() {
				Utilities.createFile(modList.getJsonFile());
				try {
					Utilities.jsonWriter(modList.getJsonFile(), modList.getModList(), false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frame = new CustomFrame("Left4Manager");
				frame.setBounds(200, 200, 1080, 720);
				frame.add(createMainPanel());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				loadingFrame.dispose();
			}
		}
		
		class PopulateModListHTML extends PopulateModListVPK {	
			@Override
			protected Void doInBackground() throws Exception {
				List<ModInfo> l4d2ModList = new ArrayList<ModInfo>();
				
				try {
					l4d2ModList = modList.getL4D2ModList();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				double totalProgress = 0;
				int listSize = l4d2ModList.size();
				double progress = (100.0 / listSize);
				String modName = "";
				for(int i = 0; i < l4d2ModList.size(); i++) {
					ModInfo singleMod = l4d2ModList.get(i);
					try {
						ModInfo objectFromJson = Utilities.modInfoFromJson(modList.getJsonFile(), singleMod.getCode());
						if(objectFromJson.getInfoSource() == 2) {
							objectFromJson.setEnabled(singleMod.getEnabled());
							modList.getModList().add(objectFromJson);	
							modName = objectFromJson.getName();
							System.out.println("Mod found in json");
						}
						else throw new InfoSourceException(objectFromJson.getInfoSource());
					} catch (InfoSourceException | ModInfoNotFoundException e) {
						System.out.println("Mod NOT found in json");
						try {
					    	String url = "https://steamcommunity.com/sharedfiles/filedetails/?id=" +singleMod.getCode();
							String html = Utilities.getHtml(url, "<div class=\"detailBox\"><script type=\"text/javascript\">");
							String[] additionalInfo =  modList.getAdditionalInfo(html);
							List<Tags> tags =  modList.getTags(html);
							modName = additionalInfo[0];
							singleMod.setInfo(additionalInfo[0], additionalInfo[1], additionalInfo[2], tags);
							singleMod.setInfoSource((short) 2);
							modList.getModList().add(singleMod);		
						} catch(NoConnectionException f) {
							File vpkArchive = new File(config.getL4D2Dir() +File.separator +"left4dead2" +File.separator
									+"addons" +File.separator +"workshop" +File.separator +singleMod.getCode() +".vpk");
							String vpkText = Utilities.getVPKInfo(vpkArchive);
							if(vpkText != null) {
								String[] vpkInfo = modList.parseVPKInfo(vpkText);
								singleMod.setInfo(vpkInfo[0], vpkInfo[1], vpkInfo[2], null);
								singleMod.setInfoSource((short) 1);
								modList.getModList().add(singleMod);
								modName = singleMod.getName();
							}
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
					totalProgress += progress;
					setProgress((int) Math.round(totalProgress));
					modInfoLabel.setText("Getting info for " +modName);
					fractionLabel.setText("Mod " +(i + 1) +"/" +listSize);
				}
				return null;
			}
		}
		
		if(config.getConfigs()[1][1] == null) throw new ConfigValueException(config.getConfigs()[1][0], config.getConfigs()[1][1]);
		if(config.getConfigs()[1][1].equals("true")) {
			PopulateModListVPK pupulateModList = new PopulateModListVPK();
			pupulateModList.addPropertyChangeListener(
					new PropertyChangeListener() {
						public  void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								loadingBar.setValue((Integer) evt.getNewValue());
							}
						}
					});
			pupulateModList.execute();
		}
		else if(config.getConfigs()[1][1].equals("false")) {
			PopulateModListHTML pupulateModList = new PopulateModListHTML();
			pupulateModList.addPropertyChangeListener(
					new PropertyChangeListener() {
						public  void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								loadingBar.setValue((Integer) evt.getNewValue());
							}
						}
					});
			pupulateModList.execute();
		}
		else throw new ConfigValueException(config.getConfigs()[1][0], config.getConfigs()[1][1]);
	}
	
	public JPanel createMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("List", createListTab());
		tabbedPane.add("Group", groupTab);
		tabbedPane.add("Order", new SortingTab(modList.getModList()));
		tabbedPane.add("Tag", createTagTab());
		JPanel tab5 = new JPanel();
		tabbedPane.add("Options", tab5);
		mainPanel.add(tabbedPane);
		
		JPanel bottomPane = new JPanel();
		bottomPane.setPreferredSize(new Dimension(10, 20));
		bottomPane.setBackground(Color.RED);
		mainPanel.add(bottomPane, BorderLayout.PAGE_END);
		return mainPanel;
	}

	public JPanel createListTab() {
		JPanel listPane = new JPanel();
		listPane.setBackground(Color.cyan);
		listPane.setLayout(new GridLayout(0, 2));

		JCheckBox selectAll = new JCheckBox("Select all");
		selectAll.setAlignmentX(Component.RIGHT_ALIGNMENT);

		GroupModTableModel model = new GroupModTableModel();
		model.add(modList.getModList());
		JTable table = new JTable(model);
		table.getColumnModel().getColumn(0).setPreferredWidth(200);
		table.getColumnModel().getColumn(1).setPreferredWidth(50);
		table.getColumnModel().getColumn(3).setPreferredWidth(30);
		TableRowSorter<GroupModTableModel> sorter = new TableRowSorter<>(model);
		JTextField searchFilterText = new JTextField();
		searchFilterText.setMaximumSize(new Dimension(1000, 50));
		searchFilterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				sorter.setRowFilter(searchFilter(searchFilterText.getText()));
				System.out.println(table.getRowCount());
			}

		});
		JButton moreFilters = new JButton("Filters");
		JPanel secondaryFilterPane = new JPanel();
		secondaryFilterPane.setLayout(new BorderLayout());
		JScrollPane filterScrollPane = new JScrollPane(createFilterPanel(sorter, model));
		filterScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		filterScrollPane.getVerticalScrollBar().setUnitIncrement(8);
		secondaryFilterPane.add(filterScrollPane, BorderLayout.CENTER);
		secondaryFilterPane.setVisible(false);
		moreFilters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				secondaryFilterPane.setVisible(!secondaryFilterPane.isVisible());
			}
		});

		JPanel leftPane = new JPanel();
		JPanel upperPane = new JPanel();
		upperPane.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
		JPanel mainFilterPane = new JPanel();
		mainFilterPane.setLayout(new BoxLayout(mainFilterPane, BoxLayout.LINE_AXIS));
		upperPane.setLayout(new BoxLayout(upperPane, BoxLayout.PAGE_AXIS));
		leftPane.setLayout(new BoxLayout(leftPane, BoxLayout.PAGE_AXIS));
		mainFilterPane.add(searchFilterText);
		mainFilterPane.add(moreFilters);
		mainFilterPane.add(selectAll);

		upperPane.add(mainFilterPane);
		upperPane.add(secondaryFilterPane);
		leftPane.add(upperPane);

		JPopupMenu tablePopupMenu = new JPopupMenu();
		tablePopupMenu.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				JMenu addToGroupButton = new JMenu("Add to group");
				JMenu removeFromGroupButton = new JMenu("Remove from group");
				JMenuItem refreshInfoesButton = new JMenuItem("Refresh info");
				JMenuItem copyButton = new JMenuItem("Copy");

				tablePopupMenu.add(addToGroupButton);
				tablePopupMenu.add(removeFromGroupButton);
				tablePopupMenu.add(refreshInfoesButton);
				tablePopupMenu.add(copyButton);
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						int rowAtPoint = table.rowAtPoint(SwingUtilities.convertPoint(tablePopupMenu, new Point(0, 0), table));
						int columnAtPoint = table.columnAtPoint(SwingUtilities.convertPoint(tablePopupMenu, new Point(0, 0), table));
 
						if (rowAtPoint > -1) {
							table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
							table.setColumnSelectionInterval(columnAtPoint, columnAtPoint);
							String selectedModCode = table.getValueAt(table.getSelectedRow(), 1).toString();
							refreshInfoesButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									String html = "";
									try {
										String url = "https://steamcommunity.com/sharedfiles/filedetails/?id=" +selectedModCode;
										html = Utilities.getHtml(url, "<div class=\"detailBox\"><script type=\"text/javascript\">");
										String[] additionalInfo = modList.getAdditionalInfo(html);
										List<Tags> tagList = modList.getTags(html);
										int modIndex = modList.getModIndexByCode(selectedModCode);
										modList.getModList().get(modIndex).setInfo(additionalInfo[0],
												additionalInfo[1],
												additionalInfo[2],
												tagList
												);
										System.out.println(selectedModCode + " refreshed");
									} catch (Exception e1) {
										System.out.println("No connection");
										//e1.printStackTrace();
									}
								}
							});
							copyButton.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
									StringSelection content = new StringSelection(table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString());
								    clipboard.setContents(content, content);
								}
							});						
							JMenuItem newGroupItem = new JMenuItem("New group");
							newGroupItem.addActionListener(new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									groupTab.createPopUpMenu(-2);
									tablePopupMenu.removeAll();
								}
							});
							addToGroupButton.add(newGroupItem);
							addToGroupButton.addSeparator();
							GroupListTableModel groupListModel = groupTab.getGroupListModel();
							for (int i = 0; i < groupListModel.getRowCount(); i++) {
								boolean exist = false;
								for (int j = 0; j < groupListModel.getRow(i).getSize(); j++) {
									if (groupListModel.getRow(i).getGroupMod(j).equals(selectedModCode)) {
										final int index = i;

										JMenuItem removeFromGroupItem = new JMenuItem(
												groupListModel.getGroupName(i));
										removeFromGroupItem.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												groupListModel.getRow(index).remove(selectedModCode);
												//config.writeModGroupFile(groupListModel.getList());
											}
										});
										removeFromGroupButton.add(removeFromGroupItem);
										exist = true;
									}
								}
								if (!exist) {
									final int index = i;
									JMenuItem addToGroupItem = new JMenuItem(groupListModel.getGroupName(i));
									addToGroupItem.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											groupListModel.getRow(index).add(selectedModCode);
											//config.writeModGroupFile(groupListModel.getList());
										}
									});
									addToGroupButton.add(addToGroupItem);
								}
							}
						}
					}
				});
			}

			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				tablePopupMenu.removeAll();
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				tablePopupMenu.removeAll();
			}
		});

		table.setComponentPopupMenu(tablePopupMenu);
		table.setShowGrid(false);
		table.setShowHorizontalLines(true);
		table.setRowSorter(sorter);

		JScrollPane tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(null);
		leftPane.add(tableScrollPane);
		
		selectAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TableModel model = table.getModel();
				boolean selectedBool = selectAll.isSelected();
				for (int i = 0; i < table.getRowCount(); i++) {
					table.setValueAt(selectedBool, i, 3);
				}
			}
		});
		
		JPanel rightPanel = new JPanel();
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {	
				JPanel panel = new JPanel();
				try {
					panel = createRigthPanel(modList.getModList().get(table.convertRowIndexToModel(table.getSelectedRow())));
				} catch(Exception e) {
				}
				listPane.remove(1);
				listPane.revalidate();
				listPane.add(panel);
			}
		});
		listPane.add(leftPane);
		rightPanel = createRigthPanel(modList.getModList().get(0));
		listPane.add(rightPanel);

		return listPane;
	}
	
	public JPanel createRigthPanel(ModInfo mod) {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		
		JLabel modTitle = new JLabel(mod.getName());
		modTitle.setFont(new Font(null, Font.PLAIN, 22));
		modTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel tagsPanel = new JPanel();
		//tagsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		tagsPanel.setLayout(new BoxLayout(tagsPanel, BoxLayout.PAGE_AXIS));
		
		List<Tags> modTag = mod.getTags();
		try {
			for(int i = 0; i < modTag.size(); i++) {
				Tags singleModTag = modTag.get(i);
				JPanel singleModPanel = new JPanel();
				JLabel primaryTagLabel = new JLabel("<html>" +"<B>" +singleModTag.getPrimaryTag() +"</B>" +" -" +"</html>");
				List<String> secondaryTagList = singleModTag.getSecondaryTag();
				singleModPanel.add(primaryTagLabel);
				for(int j = 0; j < secondaryTagList.size(); j++) {
					JLabel secondaryTagLabel = new JLabel(secondaryTagList.get(j));
					singleModPanel.add(secondaryTagLabel);
				}
				tagsPanel.add(singleModPanel);
			}
		} catch (Exception e1) {
		}
		
		JPanel descriptionPane = new JPanel();
		descriptionPane.setLayout(new BoxLayout(descriptionPane, BoxLayout.PAGE_AXIS));
		
		JPanel textDescriptionPanel = new JPanel();
		textDescriptionPanel.setLayout(new BoxLayout(textDescriptionPanel, BoxLayout.PAGE_AXIS));
		
		JTextPane textDescription = new JTextPane();
		textDescription.setEditable(false);
		textDescription.setContentType("text/html");
		textDescription.setFont(new Font("MS Song", Font.PLAIN, 14));
		textDescription.setText(mod.getDescription());
		textDescription.setBackground(mainPanel.getBackground());
		textDescriptionPanel.add(textDescription);

		
		textDescriptionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		textDescriptionPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		
		
		JButton saveButton = new JButton("Save");
		JScrollPane rightScrollPane = new JScrollPane(descriptionPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		rightScrollPane.setBorder(null);
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateModFile.writeFile(updateModFile.buildString(modList.getModList()));
			}
		});
		int scrollPaneWidth = 0;
		if(rightScrollPane.getVerticalScrollBar().isVisible()) {
			scrollPaneWidth = rightScrollPane.getVerticalScrollBar().getPreferredSize().width;
		}
		textDescription.setMaximumSize(new Dimension(((frame.getSize().width/2) - 20 - scrollPaneWidth), 100000));

		frame.addComponentListener(new ComponentAdapter() {  
			int scrollPaneWidth = 0;
			public void componentResized(ComponentEvent evt) {
				if(rightScrollPane.getVerticalScrollBar().isVisible()) {
					scrollPaneWidth = rightScrollPane.getVerticalScrollBar().getPreferredSize().width;
				}
				textDescription.setMaximumSize(new Dimension(((frame.getSize().width/2) - 20 - scrollPaneWidth), 100000));
				textDescription.revalidate();
			}
		});
		
		mainPanel.add(modTitle);
		mainPanel.add(tagsPanel);
		
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File(config.getL4D2Dir() + File.separator + "left4dead2" + File.separator
					+ "addons" + File.separator + "workshop" + File.separator + mod.getCode() + ".jpg"));
		} catch (IOException e1) {
			try {
				img = ImageIO.read(new File(config.getL4D2Dir() + File.separator + "left4dead2" + File.separator
						+ "addons" + File.separator + mod.getCode() + ".jpg"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		
		if(img != null) {
			double panelWidth = (frame.getSize().width/2) - 30 - scrollPaneWidth;
			
			BufferedImage resizedImg;
			if(img.getWidth() > panelWidth) {
				double ratio = (double)(img.getWidth()) / img.getHeight();				
				int newWidth = (int) panelWidth;
				int newHeight = (int) (panelWidth / ratio);
				resizedImg = new BufferedImage(newWidth, newHeight, img.getType());
				Graphics2D g = resizedImg.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(),img.getHeight(), null);
				g.dispose();
			}
			else {
				resizedImg = img;
			}
			JLabel imgLabel = new JLabel();
			imgLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
			imgLabel.setIcon(new ImageIcon(resizedImg));
			descriptionPane.add(imgLabel);
		}
		
		descriptionPane.add(textDescriptionPanel);
		mainPanel.add(rightScrollPane);
		mainPanel.add(saveButton);
		
		return mainPanel;
	}

	public JPanel createFilterPanel(TableRowSorter<GroupModTableModel> sorter, GroupModTableModel tableModel) {
		List<Tags> tagList = new ArrayList<Tags>();
		tagList = allTags.getAllTags();
		JCheckBox[][] checkBoxMatrix = new JCheckBox[6][11];

		JPanel mainPane = new JPanel();
		mainPane.setLayout(new BorderLayout());
		JPanel gridPane = new JPanel();
		gridPane.setLayout(new BoxLayout(gridPane, BoxLayout.PAGE_AXIS));
		JPanel row1 = new JPanel();
		row1.setLayout(new BoxLayout(row1, BoxLayout.LINE_AXIS));
		JPanel row2 = new JPanel();
		row2.setLayout(new BoxLayout(row2, BoxLayout.LINE_AXIS));
		JPanel row3 = new JPanel();
		row3.setLayout(new BorderLayout());
		JButton applyButton = new JButton("Apply");
		
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean[][] checkedTag = checkCheckbox(checkBoxMatrix);
				
				for (boolean[] row : checkedTag)
		            System.out.println(Arrays.toString(row));
					
				RowFilter<GroupModTableModel, Integer> advancedTagFilter = new RowFilter<GroupModTableModel, Integer>() {
					@Override
					public boolean include(Entry<? extends GroupModTableModel, ? extends Integer> entry) {
						List<Tags> tagList = new ArrayList<Tags>();
						tagList = allTags.getAllTags();
						boolean toFilter = true;
						GroupModTableModel model = entry.getModel();
						ModInfo mod = model.getRow(entry.getIdentifier());
						List<Tags> modTagList = mod.getTags();
						System.out.println(mod.getName());
						for (int i = 0; i < 6; i++) {
							if (checkedTag[i][0] == true) {
								for (int j = 0; j < modTagList.size(); j++) {
									Tags modTag = modTagList.get(j);
									System.out.println("searching for " +tagList.get(i).getPrimaryTag());
									System.out.println(modTag.getPrimaryTag() +"/" +tagList.get(i).getPrimaryTag());
									System.out.println(modTag.getPrimaryTag().equals(tagList.get(i).getPrimaryTag()));
									if (modTag.getPrimaryTag().equals(tagList.get(i).getPrimaryTag())) {
										toFilter = true;
										for(int k = 1; k < 11; k++) {
											if(checkedTag[i][k] == true) {
												toFilter = false;
												System.out.println("searching for " +tagList.get(i).getSecondaryTag().get(k - 1));
												List<String> modSecondaryTagList = modTag.getSecondaryTag();
												for(int l = 0; l < modSecondaryTagList.size(); l++) {
													System.out.println(modSecondaryTagList.get(l) +"/" +tagList.get(i).getSecondaryTag().get(k - 1));
													System.out.println(modSecondaryTagList.get(l).equals(tagList.get(i).getSecondaryTag().get(k - 1)));
													if (modSecondaryTagList.get(l).equals(tagList.get(i).getSecondaryTag().get(k - 1))) {
														toFilter = true;
														break;
													}
												}
												System.out.println("to filter: " +toFilter);
												System.out.println("---------------------------------------------------------");
												if(toFilter == false) {
													return toFilter;
												}
											}
										}
										break;
									}
									else {
										toFilter = false;
									}
								}
								System.out.println("to filter: " +toFilter);
								System.out.println("---------------------------------------------------------");
								if(toFilter == false) {
									return toFilter;
								}
							}				
						}
						return toFilter;
					}
				};
				//sorter.setRowFilter(null);
				sorter.setRowFilter(advancedTagFilter);
			}
		});
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sorter.setRowFilter(null);
				setCheckBoxes(checkBoxMatrix, false);
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(clearButton);
		buttonPanel.add(applyButton);
		row3.add(buttonPanel, BorderLayout.LINE_END);

		for (int i = 0; i < tagList.size(); i++) {
			final int k = i;
			checkBoxMatrix[i][0] = new JCheckBox(tagList.get(i).getPrimaryTag());
			checkBoxMatrix[i][0].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setCheckBoxes(checkBoxMatrix[k]);
				}
			});
			checkBoxMatrix[i][0].setPreferredSize(new Dimension(120, 20));
			JPanel collapsableBody = new JPanel();
			collapsableBody.setMinimumSize(new Dimension(120, 0));
			collapsableBody.setLayout(new BoxLayout(collapsableBody, BoxLayout.PAGE_AXIS));
			for (int j = 0; j < tagList.get(i).getSecondaryTag().size(); j++) {
				final int l = j;
				checkBoxMatrix[i][j + 1] = new JCheckBox(tagList.get(i).getSecondaryTag().get(j));
				checkBoxMatrix[i][j + 1].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						System.out.println(checkBoxMatrix[k][l + 1].getText());
						if(checkBoxMatrix[k][l + 1].isSelected() == true) {
							checkBoxMatrix[k][0].setSelected(true);
						}
					}
				});
				collapsableBody.add(checkBoxMatrix[i][j + 1]);
			}
			if (i < 3) {
				row1.add(createCollapsable(checkBoxMatrix[i][0], collapsableBody, false));
			} else {
				row2.add(createCollapsable(checkBoxMatrix[i][0], collapsableBody, false));
			}
		}
		gridPane.add(row1);
		gridPane.add(row2);
		gridPane.add(row3);
		mainPane.add(gridPane, BorderLayout.PAGE_START);
		return mainPane;
	}
	
	private void setCheckBoxes(JCheckBox checkBoxes[][], boolean checked) {
		for(int i = 0; i < checkBoxes.length; i++) {
			for(int j = 0; j < checkBoxes[i].length; j++) {
				if(checkBoxes[i][j] != null) {
					checkBoxes[i][j].setSelected(checked);
				}
			}
		}
	}
	
	private boolean[][] checkCheckbox(JCheckBox checkBoxes[][]){
		boolean[][] checkedCheckboxes = new boolean[6][11];
		for(int i = 0; i < checkBoxes.length; i++) {
			for(int j = 0; j < checkBoxes[i].length; j++) {
				if(checkBoxes[i][j] != null) {
					if(checkBoxes[i][j].isSelected()) {
						checkedCheckboxes[i][j] = true;
					}
				}
			}
		}
		return checkedCheckboxes;
	}
	
	private void setCheckBoxes(JCheckBox checkBoxes[]) {
		System.out.println(checkBoxes[0].isSelected());
		if(!checkBoxes[0].isSelected()) {
			for(int j = 1; j < checkBoxes.length; j++) {
				if(checkBoxes[j] != null) {
					checkBoxes[j].setSelected(false);
					System.out.println(checkBoxes[j].isSelected());
				}
			}
		}	
	}

	private RowFilter<GroupModTableModel, Object> searchFilter(String text) {
		RowFilter<GroupModTableModel, Object> rf = null;
		rf = RowFilter.regexFilter("(?i)" + text);
		return rf;
	}

	public JPanel createTagTab() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());

		JPanel boxLayoutPanel = new JPanel();
		List<Tags> allTagsList = new ArrayList<Tags>();
		allTagsList = allTags.getAllTags();

		boxLayoutPanel.setLayout(new BoxLayout(boxLayoutPanel, BoxLayout.PAGE_AXIS));
		boxLayoutPanel.setBackground(Color.orange);
		boxLayoutPanel.add(createCollapsable(new JLabel("test"), new JLabel("test"), false));
		boxLayoutPanel.add(createCollapsable(new JLabel("test"), new JLabel("test"), false));
		boxLayoutPanel.add(createCollapsable(new JLabel("test"), new JLabel("test"), false));

		mainPanel.add(boxLayoutPanel, BorderLayout.PAGE_START);
		return mainPanel;

	}

	public JPanel createCollapsable(Component headerComponent, Component bodyComponent, boolean visible) {
		JPanel collapsablePanel = new JPanel();
		collapsablePanel.setLayout(new BorderLayout());
		collapsablePanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		JPanel content = new JPanel();
		content.add(bodyComponent);
		content.setVisible(visible);

		JPanel header = new JPanel();
		Icon upIcon = new ImageIcon("." + File.separator + "icon" + File.separator + "angle-up16.gif");
		Icon downIcon = new ImageIcon("." + File.separator + "icon" + File.separator + "angle-down16.gif");
		JToggleButton headerButton = new JToggleButton(downIcon);
		headerButton.setSelectedIcon(upIcon);
		headerButton.setPreferredSize(new Dimension(25, 25));

		headerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				content.setVisible(!content.isVisible());
			}
		});
		header.add(headerComponent);
		header.add(headerButton);

		collapsablePanel.add(header, BorderLayout.PAGE_START);
		collapsablePanel.add(content, BorderLayout.LINE_START);
		return collapsablePanel;
	}
	
	public class CustomFrame extends JFrame {
		CustomFrame(String frameTitle){
			super(frameTitle);
			try {
				BufferedImage icon = ImageIO.read(new File(config.getL4managerIconDir() +File.separator +"icon.png"));
				this.setIconImage(icon);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setBounds(0, 0, 500, 150);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}
}
