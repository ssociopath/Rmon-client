package ui.views;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

/**
 * @author bobo
 * @date 2021/6/27
 */

public class MainFrame extends JFrame{
    private String[][] tableVales;
    private String[] columnNames;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField chosenUsername;
    private JTextField chosenPermission;
    private JButton addButton;
    private JButton updateButton;
    private JButton delButton;

    public void initView(){
        setTitle("用户权限管理");
        setSize(600,300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tableModel = new DefaultTableModel(tableVales,columnNames);
        table = new JTable(tableModel);
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, r);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollPane.setViewportView(table);
        final JPanel panel = new JPanel();
        getContentPane().add(panel,BorderLayout.SOUTH);
        panel.add(new JLabel("用户名: "));
        chosenUsername = new JTextField(10);
        panel.add(chosenUsername);
        panel.add(new JLabel("权限: "));
        chosenPermission = new JTextField(10);
        panel.add(chosenPermission);

        addButton = new JButton("添加");
        panel.add(addButton);
        updateButton = new JButton("修改");
        panel.add(updateButton);
        delButton = new JButton("删除");
        panel.add(delButton);
    }

    public void event(){
        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                int selectedRow = table.getSelectedRow(); //获得选中行索引
                Object oa = tableModel.getValueAt(selectedRow, 1);
                Object ob = tableModel.getValueAt(selectedRow, 2);
                chosenUsername.setText(oa.toString());  //给文本框赋值
                chosenPermission.setText(ob.toString());
            }
        });

        addButton.addActionListener(e -> {
            String []rowValues = {chosenUsername.getText(), chosenPermission.getText()};
            tableModel.addRow(rowValues);
            int rowCount = table.getRowCount() +1;
            chosenUsername.setText("A"+rowCount);
            chosenPermission.setText("B"+rowCount);
        });


        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow!= -1)
            {
                //修改指定的值：
                tableModel.setValueAt(chosenUsername.getText(), selectedRow, 1);
                tableModel.setValueAt(chosenPermission.getText(), selectedRow, 2);
            }
        });

        delButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow!=-1)
            {
                tableModel.removeRow(selectedRow);
            }
        });

    }

    public MainFrame(ArrayList<String[]> strList) {
        super();
        tableVales = strList.toArray(new String[0][]);
        columnNames = new String[]{"权限号","主控用户","权限等级"};
        initView();
        event();
    }

}
