package left4managerFunction;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.table.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import left4managerFunction.TableModels.GroupListTableModel;
import left4managerFunction.TableModels.GroupModTableModel;
import left4managerFunction.TableModels.TableRowTransferHandler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

public class Gui {

	private JFrame frame;
	Config config = new Config();
	ExtractModList extractModList;
	UpdateModFile updateModFile;
	AllTags allTags;
	GroupListTableModel groupListModel = new GroupListTableModel();

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
			config.readFile();
			initialize();
		} catch (IOException e) {
			chooseDirectoryWindow();
		}
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
			
			public void reorder(int rowIndex, int position) {
				extractModList.moveToIndex(rowIndex, position);
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
					System.out.println(tme.getFirstRow() + tme.getColumn()
							+ (String) model.getValueAt(tme.getFirstRow(), tme.getColumn()));
					extractModList.moveToIndex(tme.getFirstRow(),
							Integer.parseInt((String) model.getValueAt(tme.getFirstRow(), tme.getColumn())) - 1);
					// TODO refresh
				}
			}
		});

		List<ModInfo> modList = extractModList.getModList();
		for (int i = 0; i < modList.size(); i++) {
			model.addRow(new Object[] { i + 1, modList.get(i).getName(), modList.get(i).getCode(),
					modList.get(i).getAuthor() });
		}

		return model;
	}

	public void chooseDirectoryWindow() {
		String steamFolder = new String(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE,
				"SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath"));
		File l4d2Folder = new File(steamFolder + File.separator + "steamapps" + File.separator + "common"
				+ File.separator + "Left 4 Dead 2" + File.separator);

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
				config.createFolder();
				config.setL4D2Dir(input.getText());
				File file = new File(config.getL4D2Dir());
				if (file.getName().equals("Left 4 Dead 2")) {
					JProgressBar progressBar = new JProgressBar(0, 100);
					config.writeFile();
					initialize();
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
	}

	public void fileChooserWindow(File defaultFolder, JTextField input) {
		JFileChooser fileChooser = new JFileChooser();

		fileChooser.setDialogTitle("Choose Left 4 Dead 2 directory");
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

	private void initialize() {
		updateModFile = new UpdateModFile(config);
		allTags = new AllTags(config);
		extractModList = new ExtractModList(config);
		
		JFrame loadingFrame = new JFrame("Mod info loading");
		loadingFrame.setBounds(200, 200, 500, 150);
		loadingFrame.setLocationRelativeTo(null);
		loadingFrame.setVisible(true);
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
		
		class PopulateModList extends SwingWorker<Void, Double> {	
			@Override
			protected Void doInBackground() throws Exception {
				List<ModInfo> l4d2ModList = new ArrayList<ModInfo>();
				
				try {
					l4d2ModList = extractModList.readL4d2ModList();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				extractModList.createJsonFile();
				double totalProgress = 0;
				int listSize = l4d2ModList.size();
				double progress = (100.0 / listSize);
				String modName = "";
				for(int i = 0; i < l4d2ModList.size(); i++) {
					ModInfo singleMod = l4d2ModList.get(i);
					try {
						ModInfo objectFromJson = extractModList.getObjectFromJson(singleMod.getCode());
						System.out.println("Mod found in json");
						objectFromJson.setEnabled(singleMod.getEnabled());
						extractModList.getModList().add(objectFromJson);	
						modName = objectFromJson.getName();
						System.out.println(objectFromJson.getName());
					} catch (NullPointerException | IOException e) {
						System.out.println("Mod NOT found in json");
						try {
							String html = extractModList.getHtml(singleMod.getCode());
							String[] additionalInfo =  extractModList.getAdditionalInfo(html);
							List<Tags> tags =  extractModList.getTags(html);
							modName = additionalInfo[0];
							singleMod.setInfo(additionalInfo[0], additionalInfo[1], additionalInfo[2], tags);
							extractModList.getModList().add(singleMod);		
						} catch(IOException f) {
							System.out.println("No internet");
							headerLabel.setText("No internet connection");
							extractModList.getModList().add(l4d2ModList.get(i));
						}
					}
					totalProgress += progress;
					setProgress((int) Math.round(totalProgress));
					modInfoLabel.setText("Getting info for " +modName);
					fractionLabel.setText("Mod " +(i + 1) +"/" +listSize);
				}
				
				try {
					extractModList.addObjectToJson(extractModList.getModList());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
			@Override
			protected void done() {
				frame = new JFrame("Left4Manager");
				BufferedImage icon;
				try {
					System.out.println(config.getL4D2Dir() +File.separator +"icon" +File.separator +"icon.jpg");
					icon = ImageIO.read(new File(config.getL4managerDir() +File.separator +"icon" +File.separator +"icon.png"));
					frame.setIconImage(icon);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				frame.setBounds(200, 200, 1080, 720);
				frame.setLocationRelativeTo(null);
				frame.add(createTabbedPane());
				frame.setVisible(true);
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				loadingFrame.setVisible(false);
	       }
		}
		
		PopulateModList pupulateModList = new PopulateModList();
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

	public JTabbedPane createTabbedPane() {
		JPanel tab1 = new JPanel();
		JPanel tab2 = new JPanel();
		JPanel tab3 = new JPanel();
		JPanel tab5 = new JPanel();
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.add("List", createListTab());
		tabbedPane.add("Group", createGroupTab());
		tabbedPane.add("Order", createOrderTab());
		tabbedPane.add("Tag", createTagTab());
		tabbedPane.add("Options", tab5);
		return tabbedPane;
	}

	public JPanel createListTab() {
		JPanel listPane = new JPanel();
		listPane.setBackground(Color.cyan);
		listPane.setLayout(new GridLayout(0, 2));

		JCheckBox selectAll = new JCheckBox("Select all");
		selectAll.setAlignmentX(Component.RIGHT_ALIGNMENT);

		GroupModTableModel model = new GroupModTableModel();
		model.add(extractModList.getModList());
		JTable table = new JTable(model);
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
		leftPane.setBackground(Color.green);
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
										html = extractModList.getHtml(selectedModCode);
										String[] additionalInfo = extractModList.getAdditionalInfo(html);
										List<Tags> tagList = extractModList.getTags(html);
										int modIndex = extractModList.getModIndexByCode(selectedModCode);
										extractModList.getModList().get(modIndex).setInfo(additionalInfo[0],
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
									addNewGroupPopUp(-2);
									tablePopupMenu.removeAll();
								}
							});
							addToGroupButton.add(newGroupItem);
							addToGroupButton.addSeparator();
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
												config.writeModGroupFile(groupListModel.getList());
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
											config.writeModGroupFile(groupListModel.getList());
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
					panel = createRigthPanel(extractModList.getModList().get(table.convertRowIndexToModel(table.getSelectedRow())));
				} catch(Exception e) {
				}
				listPane.remove(1);
				listPane.revalidate();
				listPane.add(panel);
			}
		});
		listPane.add(leftPane);
		rightPanel = createRigthPanel(extractModList.getModList().get(0));
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
				updateModFile.writeFile(updateModFile.buildString(extractModList.getModList()));
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

	public JPanel createOrderTab() {
		JPanel mainPane = new JPanel();

		GroupModTableModel model = new GroupModTableModel();
		model.add(extractModList.getModList());
		JTable table = new JTable(model);
		table.setDragEnabled(true);
	    table.setDropMode(DropMode.INSERT_ROWS);
	    table.setTransferHandler(new TableRowTransferHandler(table));

		mainPane.add(new JScrollPane(table));

		JPanel buttonPane = new JPanel();
		JButton up = new JButton("Up");
		up.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeModOrder(table, 1);
			}
		});
		buttonPane.add(up);

		JButton down = new JButton("Down");
		down.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeModOrder(table, -1);
			}
		});
		buttonPane.add(down);

		JButton top = new JButton("Top");
		top.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeModOrder(table, true);
			}
		});
		buttonPane.add(top);

		JButton bottom = new JButton("Bottom");
		bottom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeModOrder(table, false);
			}
		});
		buttonPane.add(bottom);

		mainPane.add(buttonPane);

		return mainPane;
	}

	public void changeModOrder(JTable table, int positionsMoved) {
		int finalPosition = table.convertRowIndexToModel(table.getSelectedRow()) - positionsMoved;
		extractModList.moveToIndex(table.convertRowIndexToModel(table.getSelectedRow()), finalPosition);
		table.setModel(createOrderModel());
		table.setRowSelectionInterval(finalPosition, finalPosition);
	}

	public void changeModOrder(JTable table, boolean toTop) {
		if (toTop) {
			extractModList.moveToTop(table.convertRowIndexToModel(table.getSelectedRow()));
			table.setModel(createOrderModel());
			table.setRowSelectionInterval(0, 0);
		} else {
			extractModList.moveToBottom(table.convertRowIndexToModel(table.getSelectedRow()));
			table.setModel(createOrderModel());
			table.setRowSelectionInterval(extractModList.getModList().size() - 1,
					extractModList.getModList().size() - 1);
		}
	}

	

	/*public class CustomCellEditor extends DefaultCellEditor  implements TableCellEditor {
		public CustomCellEditor(JTextField textField) {
            super(textField);
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
            if (c instanceof JTextComponent) {
                JTextComponent jtc = (JTextComponent) c;
                jtc.requestFocus();
                jtc.selectAll();
            }
            return c;
        }
    }*/
	
	public JPanel createGroupTab() {
		GroupModTableModel model = new GroupModTableModel();
		JTable table = new JTable(model);

		try {
			groupListModel.add(config.readModGroupFile());
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
		c.weightx = 0.1;
		c.weighty = 1;
		c.gridx = 0; 
		
		//JTextField editorField = new JTextField();
		//CustomCellEditor groupTableRenderer = new CustomCellEditor(editorField);
		JTable groupListTable = new JTable(groupListModel); /*{
			 @Override
	         public CustomCellEditor getCellEditor(int row, int column) {
				 return groupTableRenderer;
			 }
			 @Override
			 public void terminateEditOnFocusLost(boolean value) {
				 
			 }
		};*/
		
		/*editorField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                TableCellEditor cellEditor = groupListTable.getCellEditor();
        		System.out.println(cellEditor.getCellEditorValue());

                if (cellEditor != null) {
                	if (!cellEditor.stopCellEditing()) {
                		cellEditor.cancelCellEditing();
                	}
                }
                groupListModel.setAllRowEditable(false);
            }
        });
		groupListTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                TableCellEditor cellEditor = groupListTable.getCellEditor();

        		System.out.println(cellEditor.getCellEditorValue());
                if (cellEditor != null) {
                	if (!cellEditor.stopCellEditing()) {
                		cellEditor.cancelCellEditing();
                	}
                }
                groupListModel.setAllRowEditable(false);
            }
        });*/
		
		groupListTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		groupListTable.setRowSelectionInterval(0, 0);

		groupListTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
				int selectionIndex = groupListTable.getSelectedRow();
				model.clear();
				if (selectionIndex < 0 || selectionIndex > groupListModel.getRowCount() - 1) {
					selectionIndex = 0;
				}
				if (groupListModel.getRowCount() > 0) {
					List<ModInfo> newModList = new ArrayList<ModInfo>();
					List<ModInfo> modList = extractModList.getModList();
					System.out.println(selectionIndex);
					ModGroup selectedGroup = groupListModel.getRow(selectionIndex);
					for (int i = 0; i < selectedGroup.getGroupModList().size(); i++) {
						ModInfo singleMod = extractModList.getModInfoByCode(selectedGroup.getGroupMod(i));
						newModList.add(singleMod);
					}
					model.add(newModList);
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
								addNewGroupPopUp(-2);
							}
						});
						removeGroupButton.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								groupListModel.remove(selectedRow);
								if (selectedRow < 0 | selectedRow >= groupListModel.getRowCount()) {
									groupListTable.setRowSelectionInterval(0, 0);
								}
								config.writeModGroupFile(groupListModel.getList());
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
				addNewGroupPopUp(-2);
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
				config.writeModGroupFile(groupListModel.getList());
			}
		});

		buttonPanel.add(addGroup, BorderLayout.LINE_START);
		buttonPanel.add(removeGroup, BorderLayout.LINE_END);
		column.add(buttonPanel, BorderLayout.PAGE_END);

		column.add(listScrollPane, BorderLayout.CENTER);
		column.setPreferredSize(new Dimension(100, 1000));
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
		c.anchor = GridBagConstraints.LINE_END; // bottom of space
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 1; 

		JPanel selectAllPane = new JPanel();
		JCheckBox selectAllCheckBox = new JCheckBox("Select all");
		selectAllCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean selectedBool = selectAllCheckBox.isSelected();
				for (int i = 0; i < model.getRowCount(); i++) {
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
				if (groupListTable.getSelectedRow() >= 0) {
					addNewGroupPopUp(groupListTable.getSelectedRow());
				}
			}
		});
		JButton removeModButton = new JButton("Remove mod");
		removeModButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(table.getSelectedRows().length > 0) {
					List<ModInfo> selectedRows = new ArrayList<ModInfo>();
					for (int row : table.getSelectedRows()) {
						int modelRowIndex = table.convertRowIndexToModel(row);
						ModInfo rowValue = model.getRow(modelRowIndex);
						selectedRows.add(rowValue);
					}
					
					for (ModInfo rowValue : selectedRows) {
						int rowIndex = model.getIndexByModInfo(rowValue);
						model.remove(rowIndex);
					}
					config.writeModGroupFile(groupListModel.getList());
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

	/***
	 * Create a popup to either add or modify the mods in a mod group
	 * 
	 * @param selectedIndex -2: add new group, > 1: index of the ModGroup listModel
	 *                      to modify
	 */
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
		groupNamePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JLabel groupNameLabel = new JLabel("Group name");
		groupNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		JTextField groupNameInput = new JTextField();
		groupNameInput.setText("Group" + (groupListModel.getRowCount() + 1));

		if (selectedIndex != -2) {
			addNewGroupFrame.setTitle("Modify group");
			groupNamePane.add(groupNameLabel, BorderLayout.LINE_START);
			groupNameInput.setText(groupListModel.getGroupName(selectedIndex));
			groupNamePane.add(groupNameInput);
			for (int i = 0; i < groupListModel.getRow(selectedIndex).getSize(); i++) {
				newGroupModel.add(extractModList.getModInfoByCode(groupListModel.getRow(selectedIndex).getGroupMod(i)));
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
		allModModel.add(extractModList.getModList());
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
					JOptionPane.showMessageDialog(addNewGroupFrame, "Mod groups need to be named");
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
					config.writeModGroupFile(groupListModel.getList());
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

	/*
	 * public List<ModGroup> readModGroupFile() { List<ModGroup> groupList = new
	 * ArrayList<ModGroup>();
	 * 
	 * try { Gson modGroup = new GsonBuilder().setPrettyPrinting().create();
	 * FileReader fr = new FileReader(config.getL4managerDir() +File.separator
	 * +"modGroup.json"); Type modGroupListType = new
	 * TypeToken<List<ModGroup>>(){}.getType(); groupList = modGroup.fromJson(fr,
	 * modGroupListType); System.out.println(groupList.get(0).getGroupName());
	 * 
	 * fr.close();
	 * 
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } return groupList; }
	 */
}
