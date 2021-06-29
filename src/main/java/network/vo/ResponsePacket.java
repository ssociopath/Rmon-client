package network.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

/**
 * @author bobo
 * @date 2021/6/29
 */

@Message
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePacket {
    private byte type;
    private byte result;
    private int id;
    private byte flag;
    private byte[] content;
}
