import network.SocketClient;
import network.protocol.ClientHandler;
import ui.views.LoginFrame;
import utils.ThreadPoolUtil;

/**
 * @author bobo
 * @date 2021/6/28
 */

public class Main {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        ThreadPoolUtil.executor(socketClient::connect);
        LoginFrame loginFrame = new LoginFrame(socketClient);
        loginFrame.setVisible(true);
    }
}
