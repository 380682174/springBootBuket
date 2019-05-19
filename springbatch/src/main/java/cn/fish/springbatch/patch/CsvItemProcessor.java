package cn.fish.springbatch.patch;

import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 21:37
 */
public class CsvItemProcessor extends ValidatingItemProcessor {

    @Override
    public Object process(Object item) throws ValidationException {

        /*需要执行super.process(item)才会调用自定义校验器*/
        return super.process(item);

    }
}
