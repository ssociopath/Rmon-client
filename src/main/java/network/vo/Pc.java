package network.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bobo
 * @date 2021/6/27
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pc {
    private Integer id;
    private String mac;
    private String password;
}
