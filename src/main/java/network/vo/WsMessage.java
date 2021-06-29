package network.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bobo
 * @date 2021/6/29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsMessage {
    private String fromUserId;
    private String toUserId;
    private String type;
    private String content;
}
