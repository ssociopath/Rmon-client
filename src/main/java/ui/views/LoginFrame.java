package ui.views;

import network.ILoginListener;
import network.SocketClient;
import network.vo.Pc;
import network.vo.Rule;
import ui.FontManager;
import utils.Constant;
import utils.JsonUtil;
import utils.SystemUtil;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author bobo
 * @date 2021/6/27
 */

public class LoginFrame extends JFrame implements ILoginListener {
    private final SocketClient socketClient;
    private MainFrame mainFrame;

    private final JLabel frameLabel;
    private final Container container;
    private final JLabel usernameLabel;
    private final JTextField username;
    private final JLabel passwordLabel;
    private final JPasswordField password;
    private final JButton okBtn;
    private final JButton cancelBtn;

    public LoginFrame(SocketClient socketClient) {
        this.socketClient = socketClient;
        socketClient.setILoginListener(this);
        setTitle("登录");
        container = getContentPane();
        frameLabel = new JLabel("远程桌面监控系统");
        usernameLabel = new JLabel("用户名");
        username = new JTextField();
        passwordLabel = new JLabel("密  码");
        password = new JPasswordField();
        okBtn = new JButton("确定");
        cancelBtn = new JButton("取消");
        init();
        event();
    }

    public void init() {
        setSize(320,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container.setLayout(new BorderLayout());
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout());
        frameLabel.setFont(FontManager.SimSHei.deriveFont(Font.BOLD,18));
        titlePanel.add(frameLabel);
        container.add(titlePanel, "North");

        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(null);
        usernameLabel.setBounds(30, 15, 50, 25);
        passwordLabel.setBounds(30, 55, 50, 25);
        fieldPanel.add(usernameLabel);
        fieldPanel.add(passwordLabel);
        username.setText(SystemUtil.getMacAddress());
        username.setEnabled(false);
        username.setDisabledTextColor(Color.darkGray);
        username.setBounds(90, 15, 180, 25);
        password.setBounds(90, 55, 180, 25);
        fieldPanel.add(username);
        fieldPanel.add(password);
        container.add(fieldPanel, "Center");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        container.add(buttonPanel, "South");
    }

    public void event(){
        okBtn.addActionListener(e -> {
            char[] pwd = password.getPassword();
            if(pwd.length<1||pwd.length>8){
                JOptionPane.showMessageDialog(null, "密码不能为空，且需要小于8位", "提示",JOptionPane.ERROR_MESSAGE);
            }else{
                String pcJson = JsonUtil.toJsonString(Pc.builder()
                        .mac(username.getText())
                        .password(String.valueOf(pwd))
                        .build());
                socketClient.sendData(Constant.LOGIN, pcJson.getBytes(StandardCharsets.UTF_8));
            }
        });
    }



    @Override
    public void onLogin(byte result, String content) {
        if(Constant.RESPONSE_SUCCEED == result){
            List<Rule> ruleList = JsonUtil.parseList(content, Rule.class);
            String[][] tableVales = ruleList.stream()
                    .map(rule -> new String[]{rule.getRuleId().toString(),rule.getAccount(),rule.getPermission()})
                    .toArray(String[][]::new);
            setVisible(false);
            mainFrame = new MainFrame(tableVales,socketClient);
            mainFrame.setVisible(true);
        }else{
            JOptionPane.showMessageDialog(null, content, "提示",JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onFail() {
        setVisible(true);
        mainFrame.setVisible(false);
    }
}
