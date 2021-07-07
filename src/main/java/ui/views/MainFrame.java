package ui.views;

import network.IRuleListener;
import network.SocketClient;
import network.vo.Rule;
import utils.Constant;
import utils.JsonUtil;
import utils.ThreadPoolUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;

/**
 * @author bobo
 * @date 2021/6/27
 */

public class MainFrame extends JFrame implements IRuleListener {
    private String[][] tableVales;
    private String[] columnNames;
    private String[] permissions;
    private SocketClient socketClient;

    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField chosenUsername;
    private JComboBox<String> chosenPermission;
    private JButton addButton;
    private JButton updateButton;
    private JButton delButton;

    public MainFrame(String[][] tableVales, SocketClient socketClient) {
        super();
        this.socketClient = socketClient;
        socketClient.setIRuleListener(this);
        this.tableVales = tableVales;
        columnNames = new String[]{"权限号","主控用户","权限等级"};
        permissions = new String[]{"允许操作","允许访问"};
        initView();
        event();
    }

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
        chosenPermission = new JComboBox<>(permissions);
        chosenPermission.setBackground(Color.white);
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
                chosenPermission.setSelectedItem(ob.toString());
            }
        });

        addButton.addActionListener(e -> {
            Rule rule = new Rule(null, chosenUsername.getText(), Objects.requireNonNull(chosenPermission.getSelectedItem()).toString());
            socketClient.sendData(Constant.DATA_UPDATE,JsonUtil.toJsonString(rule).getBytes());
        });


        updateButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow!= -1)
            {
                Rule rule = new Rule(new Integer(tableModel.getValueAt(selectedRow,0).toString()),
                        chosenUsername.getText(),
                        Objects.requireNonNull(chosenPermission.getSelectedItem()).toString());
                socketClient.sendData(Constant.DATA_UPDATE,JsonUtil.toJsonString(rule).getBytes());
            }
        });

        delButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if(selectedRow!=-1)
            {
                socketClient.sendData(Constant.DATA_DELETE,tableModel.getValueAt(selectedRow,0).toString().getBytes());
            }
        });

    }


    @Override
    public void onChange(byte result, String content) {
        ThreadPoolUtil.executor(()->{
            if(Constant.RESPONSE_SUCCEED == result){
                List<Rule> ruleList = JsonUtil.parseList(content, Rule.class);
                System.out.println(ruleList.toString());
                tableVales = ruleList.stream()
                        .map(rule -> new String[]{rule.getRuleId().toString(), rule.getAccount(), rule.getPermission()})
                        .toArray(String[][]::new);
                tableModel = new DefaultTableModel(tableVales,columnNames);
                table.setModel(tableModel);
            }else{
                JOptionPane.showMessageDialog(null, content, "提示",JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
