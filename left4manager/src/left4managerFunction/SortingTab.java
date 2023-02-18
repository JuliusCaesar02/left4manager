package left4managerFunction;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;

import left4managerFunction.TableModels.ModListOrderModel;
import left4managerFunction.TableModels.TableRowTransferHandler;

public class SortingTab extends JPanel {
	private ModListOrderModel orderModel;
	private JTable orderTable;
		
	public SortingTab(List<ModInfo> modList) {
		super();
		orderModel = new ModListOrderModel();
		orderModel.add(modList);
		orderTable = new JTable(orderModel);
		orderTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		orderTable.setDragEnabled(true);
		orderTable.setDropMode(DropMode.INSERT_ROWS);
		orderTable.setTransferHandler(new TableRowTransferHandler(orderTable));
		
		add(new JScrollPane(orderTable));
		
		ButtonTab buttonTab = new ButtonTab();
		add(buttonTab);
	}
	
	public class ButtonTab extends JPanel{
		public ButtonTab() {
			super();
			JButton up = new JButton("Up");
			up.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = orderTable.getSelectedRows().length - 1;
					System.out.println(orderTable.getSelectedRows()[index] - 2);
					changeRowOrder(orderTable.getSelectedRows(), orderTable.getSelectedRows()[0] - 1);
				}
			});
			add(up);
			JButton down = new JButton("Down");
			down.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int index = orderTable.getSelectedRows().length - 1;
					changeRowOrder(orderTable.getSelectedRows(), orderTable.getSelectedRows()[index] + 2);
				}
			});
			add(down);
			JButton top = new JButton("Top");
			top.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changeRowOrder(orderTable.getSelectedRows(), 0);
				}
			});
			add(top);
			JButton bottom = new JButton("Bottom");
			bottom.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					changeRowOrder(orderTable.getSelectedRows(), orderTable.getRowCount());
				}
			});
			add(bottom);
		}
	}
	
	public void changeRowOrder(int[] selectedRows, int positionMoved) {
		int rowToSelect = positionMoved;
		if (selectedRows[0] != -1 && selectedRows[0] != positionMoved) {
			for (int i = 0; i < selectedRows.length; i++) {
				if (positionMoved > selectedRows[i]) {
					positionMoved--;
					orderModel.reorder(selectedRows[i] - i, positionMoved);
				}
				
				else {
					orderModel.reorder(selectedRows[i], positionMoved);
				}
				positionMoved++;
			}
            orderTable.setRowSelectionInterval(positionMoved - selectedRows.length, positionMoved - 1);
            orderTable.scrollRectToVisible(new Rectangle(orderTable.getCellRect(rowToSelect, 0, true)));       
		}
	}
}
