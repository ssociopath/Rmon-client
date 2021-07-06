package network.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author bobo
 * @date 2021/7/6
 */

@Builder
@Data
@AllArgsConstructor
public class Task {
    private String pid;
    private String name;
    private String user;
    private String mem;
    private String cpu;
}
