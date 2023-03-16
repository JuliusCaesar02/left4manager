package left4managerFunction;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;


public class OptionsTab extends JPanel {
		public OptionsTab() {
			
		}
		public OptionsTab(Config config, ModList modList){
		super();
		setLayout(new GridLayout(2, 5));
		add(new JLabel("Change l4d2 directory"));
		
		JButton applyButton = new JButton("Apply");
		Icon folderIcon = new ImageIcon("." + File.separator + "icon" + File.separator + "Open16.gif");
		JButton folderButton = new JButton(folderIcon);
		JLabel label = new JLabel("Select Left4Dead2 folder:");
		
		JPanel selectFolderPanel = new JPanel();
		JTextField input = new JTextField();
		//input.setText(config.getL4D2Dir());
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(input.getText());
				if (file.getName().equals("Left 4 Dead 2")) {
					//config.writeFile();

				} else {
					JOptionPane.showMessageDialog(input,
							"The selected folder doesn't seem to be a Left 4 Dead 2 folder", "Invalid folder",
							JOptionPane.WARNING_MESSAGE);
					//input.setText(config.getL4D2Dir());
				}
			}
		});
		folderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File defaultFolder = new File(input.getText());
				new CustomFileChooser(defaultFolder, input);
			}
		});
		selectFolderPanel.add(input);
		selectFolderPanel.add(folderButton);
		selectFolderPanel.add(applyButton);
	
		add(selectFolderPanel);
		add(new JLabel("Refresh all mods"));
		JButton refreshAllButton = new JButton("Refresh all");
		refreshAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		add(refreshAllButton);
	}
	
	public class CustomFileChooser extends JFileChooser {
		public CustomFileChooser(File defaultFolder, JTextField input) {
			super();
			setDialogTitle("L4M: Choose Left 4 Dead 2 directory");
			setCurrentDirectory(defaultFolder);
			setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = showDialog(this, "Apply");
			System.out.println(result);
			if (result == JFileChooser.APPROVE_OPTION) {
				input.setText(this.getSelectedFile().getAbsolutePath());
				System.out.println(this.getSelectedFile());
			} else if (result == JFileChooser.CANCEL_OPTION) {
				System.out.println("Cancel was selected");
			}
		}
	}
	
	public void openFileChooser(File file, JTextField component) {
		new CustomFileChooser(file, component);
	}
}
