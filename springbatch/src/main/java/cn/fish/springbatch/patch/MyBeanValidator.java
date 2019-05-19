package cn.fish.springbatch.patch;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.InitializingBean;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 22:18
 */
public class MyBeanValidator<T> implements Validator<T>,InitializingBean {

    @Override
    public void validate(T t) throws ValidationException {

    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
