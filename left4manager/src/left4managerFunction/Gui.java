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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;

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
	GroupListModel listModel = new GroupListModel();

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

	private void debug() {
		// config.writeFile();
		// extractModList.addObjectToJsonDebug(extractModList.getModList().get(0));
		// updateModFile.setFileName("addonlist2.txt");
		// updateModFile.setDirectory(config.getL4D2Dir() +File.separator +"left4dead2"
		// +File.separator);
		// ModGroup group1 = new ModGroup("gruppo1", extractModList);
		// System.out.println();
		// System.out.print(group1.getGroupName());
		// group1.addModToList(0);
		// System.out.print(group1.getGroupMod(0).getEnabled());
		// extractModList.getModList().get(0).setEnabled(true);;
		// System.out.print(group1.getGroupMod(0).getEnabled());
		// group1.getGroupMod(0).setEnabled(false);
		// System.out.print(extractModList.getModList().get(0).getEnabled());
		// System.out.print(group1.getGroupMod(0).getEnabled());
		// extractModList.getModList().get(0).setEnabled(true);
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
							for (int i = 0; i < listModel.getSize(); i++) {
								boolean exist = false;
								for (int j = 0; j < listModel.getElementAt(i).getSize(); j++) {
									if (listModel.getElementAt(i).getGroupMod(j).equals(selectedModCode)) {
										final int index = i;

										JMenuItem removeFromGroupItem = new JMenuItem(
												listModel.getElementAt(i).getGroupName());
										removeFromGroupItem.addActionListener(new ActionListener() {
											public void actionPerformed(ActionEvent e) {
												listModel.getElementAt(index).remove(selectedModCode);
												config.writeModGroupFile(listModel);
											}
										});
										removeFromGroupButton.add(removeFromGroupItem);
										exist = true;
									}
								}
								if (!exist) {
									final int index = i;
									JMenuItem addToGroupItem = new JMenuItem(listModel.getElementAt(i).getGroupName());
									addToGroupItem.addActionListener(new ActionListener() {
										public void actionPerformed(ActionEvent e) {
											listModel.getElementAt(index).add(selectedModCode);
											config.writeModGroupFile(listModel);
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
		try {
			BufferedImage img = ImageIO.read(new File(config.getL4D2Dir() + File.separator + "left4dead2" + File.separator
					+ "addons" + File.separator + "workshop" + File.separator + mod.getCode() + ".jpg"));
			double panelWidth = (frame.getSize().width/2) - 30 - scrollPaneWidth;
			
			BufferedImage resizedImg;
			if(img.getWidth() > panelWidth) {
				double ratio = (double)(img.getWidth()) / img.getHeight();
				System.out.println("true");
				
				int newWidth = (int) panelWidth;
				int newHeight = (int) (panelWidth / ratio);
				resizedImg = new BufferedImage(newWidth, newHeight, img.getType());
				Graphics2D g = resizedImg.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(),img.getHeight(), null);
				g.dispose();
			}
			else {
				System.out.println("false");
				resizedImg = img;
			}
			JLabel imgLabel = new JLabel();
			imgLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
			imgLabel.setIcon(new ImageIcon(resizedImg));
			descriptionPane.add(imgLabel);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		descriptionPane.add(textDescriptionPanel);
		mainPanel.add(rightScrollPane);
		mainPanel.add(saveButton);
		return mainPanel;
	}

	public JPanel createFilterPanel(TableRowSorter<GroupModTableModel> sorter, GroupModTableModel tableModel) {
		List<Tags> tagList = new ArrayList<Tags>();
		List<Tags> checkedTags = new ArrayList<Tags>();
		tagList = allTags.getAllTags();
		boolean[][] checkedTag = new boolean[6][11];

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
				RowFilter<GroupModTableModel, Integer> primaryTagFilter = new RowFilter<GroupModTableModel, Integer>() {
					@Override
					public boolean include(Entry<? extends GroupModTableModel, ? extends Integer> entry) {
						List<Tags> tagList = new ArrayList<Tags>();
						tagList = allTags.getAllTags();
						boolean toFilter = true;
						GroupModTableModel model = entry.getModel();
						ModInfo mod = model.getRow(entry.getIdentifier());
						List<Tags> modTag = mod.getTags();
						for (int i = 0; i < 6; i++) {
							if (checkedTag[i][0] == true) {
								for (int j = 0; j < modTag.size(); j++) {
									System.out.println(
											toFilter + modTag.get(j).getPrimaryTag() != tagList.get(i).getPrimaryTag());
									if (modTag.get(j).getPrimaryTag().equals(tagList.get(i).getPrimaryTag())) {
										toFilter = true;
									} else {
										toFilter = false;
										System.out.println("[]" + toFilter);
									}
								}
							}
						}
						return toFilter;
					}
				};
				if (sorter.getRowFilter() != null) {
					sorter.setRowFilter(null);
				} else {
					sorter.setRowFilter(primaryTagFilter);
				}
			}
		});
		row3.add(applyButton, BorderLayout.LINE_END);

		for (int i = 0; i < tagList.size(); i++) {
			final int k = i;
			JCheckBox collapsableTitle = new JCheckBox(tagList.get(i).getPrimaryTag());
			collapsableTitle.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					checkedTag[k][0] = collapsableTitle.isSelected();
				}
			});
			collapsableTitle.setPreferredSize(new Dimension(120, 20));
			JPanel collapsableBody = new JPanel();
			collapsableBody.setMinimumSize(new Dimension(120, 0));
			collapsableBody.setLayout(new BoxLayout(collapsableBody, BoxLayout.PAGE_AXIS));
			for (int j = 0; j < tagList.get(i).getSecondaryTag().size(); j++) {
				final int l = j;
				JCheckBox secondaryTagItem = new JCheckBox(tagList.get(i).getSecondaryTag().get(j));
				secondaryTagItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						checkedTag[k][l + 1] = secondaryTagItem.isSelected();
					}
				});
				collapsableBody.add(secondaryTagItem);
			}
			if (i < 3) {
				row1.add(createCollapsable(collapsableTitle, collapsableBody, false));
			} else {
				row2.add(createCollapsable(collapsableTitle, collapsableBody, false));
			}
		}
		gridPane.add(row1);
		gridPane.add(row2);
		gridPane.add(row3);
		mainPane.add(gridPane, BorderLayout.PAGE_START);
		return mainPane;
	}

	private RowFilter<GroupModTableModel, Object> searchFilter(String text) {
		RowFilter<GroupModTableModel, Object> rf = null;
		rf = RowFilter.regexFilter("(?i)" + text);
		return rf;
	}

	public JPanel createOrderTab() {
		JPanel mainPane = new JPanel();

		DefaultTableModel model = createOrderModel();
		JTable table = new JTable(model);

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
		int finalPosition = table.getSelectedRow() - positionsMoved;
		extractModList.moveToIndex(table.getSelectedRow(), finalPosition);
		table.setModel(createOrderModel());
		table.setRowSelectionInterval(finalPosition, finalPosition);
	}

	public void changeModOrder(JTable table, boolean toTop) {
		if (toTop) {
			extractModList.moveToTop(table.getSelectedRow());
			table.setModel(createOrderModel());
			table.setRowSelectionInterval(0, 0);
		} else {
			extractModList.moveToBottom(table.getSelectedRow());
			table.setModel(createOrderModel());
			table.setRowSelectionInterval(extractModList.getModList().size() - 1,
					extractModList.getModList().size() - 1);
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
			if (oldLenght < 0) {
				oldLenght = 0;
			}
			if(data != null) {
				rowData.addAll(data);
				fireIntervalAdded(this, oldLenght, getSize() - 1);
			}
		}

		public List<ModGroup> get() {
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
			for (int i = 0; i < index.length; i++) {
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
		groupJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		groupJList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectionIndex = groupJList.getMinSelectionIndex();
				model.clear();
				if (selectionIndex < 0 || selectionIndex > listModel.getSize() - 1) {
					selectionIndex = 0;
				}
				if (listModel.getSize() > 0) {
					List<ModInfo> newModList = new ArrayList<ModInfo>();
					List<ModInfo> modList = extractModList.getModList();
					System.out.println(selectionIndex);
					ModGroup selectedGroup = listModel.getElementAt(selectionIndex);
					for (int i = 0; i < selectedGroup.getGroupModList().size(); i++) {
						int index = extractModList.getModIndexByCode(selectedGroup.getGroupMod(i));
						if (index >= 0) {
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
					if (index > 0) {
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
		c.anchor = GridBagConstraints.LINE_END; // bottom of space
		c.weightx = 1;
		c.weighty = 1;

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
				if (groupJList.getMinSelectionIndex() >= 0) {
					addNewGroupPopUp(groupJList.getMinSelectionIndex());
				}
			}
		});
		JButton removeModButton = new JButton("Remove mod");
		removeModButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int[] selectedRows = table.getSelectedRows();
				if (selectedRows.length > 0) {
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
		JLabel groupNameLabel = new JLabel("Group name");
		JTextField groupNameInput = new JTextField();
		groupNameInput.setText("Group" + (listModel.getSize() + 1));

		if (selectedIndex != -2) {
			addNewGroupFrame.setTitle("Modify group");
			groupNameLabel.setText(listModel.getElementAt(selectedIndex).getGroupName());
			groupNamePane.add(groupNameLabel, BorderLayout.LINE_START);
			for (int i = 0; i < listModel.getElementAt(selectedIndex).getSize(); i++) {
				newGroupModel
						.add(extractModList.getModInfoByCode(listModel.getElementAt(selectedIndex).getGroupMod(i)));
			}
		} else {
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
				if (groupNameInput.getText().equals("")) {
					JOptionPane.showMessageDialog(addNewGroupFrame, "Mod groups need to be named");
				} else {
					String groupName = groupNameInput.getText();
					if (selectedIndex != -2) {
						groupName = listModel.getElementAt(selectedIndex).getGroupName();
						listModel.remove(selectedIndex);
					}
					ModGroup newGroup = new ModGroup(groupName);
					for (int i = 0; i < newGroupModel.getRowCount(); i++) {
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

		public void remove(int[] index) {
			for (int i = 0; i < index.length; i++) {
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

		JPanel content = new JPanel();
		content.add(bodyComponent);
		content.setVisible(visible);

		JPanel header = new JPanel();
		JButton headerButton = new JButton();
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
