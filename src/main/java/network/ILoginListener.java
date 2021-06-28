package network;

import java.util.List;

/**
 * @author bobo
 * @date 2021/6/29
 */

public interface ILoginListener {
    void onLogin(byte result, String content);
}
