package cn.fish.springbatch.batch;

import cn.fish.springbatch.bean.UserBean;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 21:34
 */
public class CsvItemProcessor extends ValidatingItemProcessor<UserBean> {

    @Override
    public UserBean process(UserBean item) throws ValidationException {
        super.process(item);
        if(item.getNation().equals("汉族")){
            item.setNation("01");
        }else {
            item.setNation("02");
        }
        return item;
    }
}
