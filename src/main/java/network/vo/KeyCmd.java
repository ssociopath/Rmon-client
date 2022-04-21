package network.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bobo
 * @date 2022/4/21
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyCmd {
    private String openType;
    private int keyCode;
    private int clientX;
    private int clientY;
    private int button;
    private int imageWidth;
    private int imageHeight;

    public KeyCmd(String openType, int keyCode) {
        this.openType = openType;
        this.keyCode = keyCode;
    }

    public KeyCmd(String openType, int clientX, int clientY, int button, int imageWidth, int imageHeight) {
        this.openType = openType;
        this.clientX = clientX;
        this.clientY = clientY;
        this.button = button;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }
}
