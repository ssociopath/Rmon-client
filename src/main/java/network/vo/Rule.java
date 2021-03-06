package network.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author bobo
 * @date 2021/6/25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    private Integer ruleId;
    private String account;
    private String permission;
}
