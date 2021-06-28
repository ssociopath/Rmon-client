package network.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

/**Pc
 * @author bobo
 * @date 2021/6/26
 */

@Message
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPacket {
    private byte type;
    private int id;
    private byte flag;
    private byte[] content;
}
