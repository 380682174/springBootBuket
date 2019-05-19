package cn.fish.springbatch.bean;

import lombok.Data;

import javax.validation.constraints.Size;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 21:34
 */
@Data
public class UserBean {

    private Integer id;

    @Size(min = 2,max = 4)
    private String name;

    private String address;

    private String nation;

    private Integer age;

}
