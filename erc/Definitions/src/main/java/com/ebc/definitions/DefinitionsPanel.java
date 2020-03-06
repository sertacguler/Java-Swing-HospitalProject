package com.ebc.definitions;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.GridBagLayout;

import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JTable;

import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import com.ebc.core.DBHibernate;
import com.ebc.definitions.entity.DefinitionDTO;
import com.ebc.definitions.entity.TestCategoriesDTO;
import com.ebc.deginitions.test.categories.TestCategoriesEditor;



public class DefinitionsPanel extends JPanel {
	private DefinitionsTableModel tableModel = new DefinitionsTableModel();
	private JTable table;
	private JPanel panel;
	private ArrayList<DefinitionDTO> definition = new ArrayList<DefinitionDTO>();
	public DefinitionsPanel() {
		setForeground(new Color(153, 204, 255));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 5, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 5, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0,
				0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 0.0,
				Double.MIN_VALUE };
		setLayout(gridBagLayout);
		GenerealEventListener eventListener = new GenerealEventListener(this);
		JButton btnAdd_1 = new JButton("ADD");
		btnAdd_1.setFont(new Font("Tahoma", Font.PLAIN, 21));
		GridBagConstraints gbc_btnAdd_1 = new GridBagConstraints();
		gbc_btnAdd_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAdd_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnAdd_1.gridx = 5;
		gbc_btnAdd_1.gridy = 1;
		add(btnAdd_1, gbc_btnAdd_1);
		btnAdd_1.setActionCommand("ADD");
		btnAdd_1.addActionListener(eventListener);

		JButton btnEdit_1 = new JButton("EDIT");
		btnEdit_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GridBagConstraints gbc_btnEdit_1 = new GridBagConstraints();
		gbc_btnEdit_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnEdit_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnEdit_1.gridx = 6;
		gbc_btnEdit_1.gridy = 1;
		add(btnEdit_1, gbc_btnEdit_1);
		btnEdit_1.setActionCommand("EDIT");
		btnEdit_1.addActionListener(eventListener);

		JButton btnDelete_1 = new JButton("DELETE");
		btnDelete_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		GridBagConstraints gbc_btnDelete_1 = new GridBagConstraints();
		gbc_btnDelete_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnDelete_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnDelete_1.gridx = 7;
		gbc_btnDelete_1.gridy = 1;
		add(btnDelete_1, gbc_btnDelete_1);
		btnDelete_1.setActionCommand("DELETE");
		btnDelete_1.addActionListener(eventListener);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridwidth = 7;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 2;
		add(scrollPane, gbc_scrollPane);

		table = new JTable();
		table.setBackground(new Color(255, 255, 255));

		// table.setRowHeight(2, 30);
		table.setRowHeight(45);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 21));
		table.setForeground(new Color(0, 0, 51));
		scrollPane.setColumnHeaderView(table);
		scrollPane.setViewportView(table);
		table.setModel(tableModel);
		SimpleExpression rest = Restrictions.eq("definitionStatus", true);
		definition = DBHibernate.getAll(DefinitionDTO.class, new ArrayList(),rest);
		tableModel.setDefinition(definition);
		tableModel.fireTableDataChanged();
	}
	
	class GenerealEventListener implements ActionListener {

		private JPanel panel;

		public GenerealEventListener(JPanel panel) {
			// TODO Auto-generated constructor stub
			this.panel = panel;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			DBHibernate db = new DBHibernate();
			String wrt = arg0.getActionCommand();
			DefinitionDTO definition = null;
			if(wrt.equals("ADD")) {
				definition = openEditor(definition);
				if(definition!=null) {
					try {
					definition.setDefinitionStatus(true);
					db.save(definition);
					db.update(definition);
					}
					catch(SQLException e) {
						e.printStackTrace();
					}
					tableModel.getAllDatas().add(definition);
					tableModel.fireTableDataChanged();
					
				}
			}
			else if(wrt.equals("EDIT")) {
				int selectedIndex = table.getSelectedRow();
				
				DefinitionDTO d = tableModel.getDefinition().get(selectedIndex);
				d = openEditor(d);
				db.update(d);
				tableModel.fireTableDataChanged();
			}
			else if(wrt.equals("DELETE")) {
				SimpleExpression rest = Restrictions.eq("definitionStatus", true);
				int selectedIndex = table.getSelectedRow();
				
				if(selectedIndex==-1) {
					JOptionPane.showMessageDialog(panel,
							"Please select a record");
				}
				else {
					JOptionPane.showConfirmDialog(null, "Are u sure?",
							"Delete Process", JOptionPane.YES_NO_OPTION);
					DefinitionDTO d = tableModel.getDefinition().get(selectedIndex);
					d.setDefinitionStatus(false);
					db.update(d);
					tableModel.setDefinition(db.getAll(DefinitionDTO.class, new ArrayList(),rest));
					tableModel.fireTableDataChanged();
				}
			}
			
		}
	}
	private DefinitionDTO openEditor(DefinitionDTO def) {

		JDialog dialog = new JDialog(
				JOptionPane.getFrameForComponent(panel), "Create", true);
		DefinitionsEditor defEditor = new DefinitionsEditor(def);
		defEditor.setDialog(dialog);
		dialog.getContentPane().add(defEditor);
		dialog.setLocationByPlatform(true);
		dialog.setSize(600, 250);
		dialog.setVisible(true);
		def = defEditor.getDefinition(); 
		return def;
	}
	
}
