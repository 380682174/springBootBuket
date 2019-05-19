package cn.fish.springbatch.patch;

import cn.fish.springbatch.bean.UserBean;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;

/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 22:00
 */
@Configuration
@EnableBatchProcessing
public class CsvBatchConfig {

    @Resource
    private DruidProperties druidProperties;

    /**
     * 单数据源连接池配置
     */
    @Bean("dataSource")
    public DruidDataSource singleDatasource() {
        DruidDataSource dataSource = new DruidDataSource();
        druidProperties.config(dataSource);
        return dataSource;
    }

    /**
     * ItemReader定义,用来读取数据
     * @param pathToFile
     * @return
     */
    @Bean
    @StepScope
    public FlatFileItemReader<UserBean> reader(@Value("#{jobParameters['input.file.name']}") String pathToFile) {

        //使用FlatFileItemReader读取文件
        FlatFileItemReader<UserBean> reader = new FlatFileItemReader<>();

        //使用FlatFileItemReader的setResource方法设置csv文件的路径
        reader.setResource(new FileSystemResource(pathToFile));

        //对此对cvs文件的数据和领域模型类做对应映射
        reader.setLineMapper(new DefaultLineMapper<UserBean>(){
            @Override
            public void setLineTokenizer(LineTokenizer tokenizer) {
                super.setLineTokenizer(new DelimitedLineTokenizer(",") {
                    @Override
                    public void setNames(String... names) {
                        super.setNames(
                                new String[]{"id","userName","password","sex","age","phoneNo","email"}
                        );
                    }
                });
            }

            @Override
            public void setFieldSetMapper(FieldSetMapper<UserBean> fieldSetMapper) {
                super.setFieldSetMapper(new BeanWrapperFieldSetMapper<UserBean>() {
                    @Override
                    public void setTargetType(Class type) {
                        super.setTargetType(UserBean.class);
                    }
                });
            }
        });

        return reader;

    }

    /**
     * ItemProcessor定义，用来处理数据
     * @return
     */
    @Bean
    public ItemProcessor<UserBean, UserBean> processor() {

        //使用我们自定义的ItemProcessor的实现CsvItemProcessor
        CsvItemProcessor itemProcessor = new CsvItemProcessor();
        //使用我们自定义的ItemProcessor的实现CsvItemProcessor
        itemProcessor.setValidator(csvBeanValidator());

        return itemProcessor;

    }

    /**
     * ItemWriter定义，用来输出数据
     * 1、spring能让容器中已有的Bean以参数的形式注入，Spring Boot已经为我们定义了dataSource
     * @param dataSource
     * @return
     */
    @Bean
    public ItemWriter<UserBean> writer(DruidDataSource dataSource) {

        JdbcBatchItemWriter<UserBean> writer = new JdbcBatchItemWriter<>();

        //我们使用JDBC批处理的JdbcBatchItemWriter来写数据到数据库
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        String sql = "insert into t_user(id, username, password, sex, age, phone_no, email) values (?,?,?,?,?,?,?)";
        //在此设置要执行批处理的SQL语句
        writer.setSql(sql);
        writer.setDataSource(dataSource);

        return writer;

    }

    /**
     * JobRepository，用来注册Job的容器
     * 1、jobRepositor的定义需要dataSource和transactionManager，Spring Boot已为我们自动配置了这两个类，Spring可通过方法注入已有的Bean
     * @param dataSource
     * @param platformTransactionManager
     * @return
     * @throws Exception
     */
    @Bean
    public JobRepository jobRepository(DruidDataSource dataSource, PlatformTransactionManager platformTransactionManager) throws Exception {

        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(platformTransactionManager);
        jobRepositoryFactoryBean.setDatabaseType(String.valueOf(DatabaseType.MYSQL));
        jobRepositoryFactoryBean.afterPropertiesSet();

        return jobRepositoryFactoryBean.getObject();

    }

    /**
     * JobLauncher定义，用来启动Job的接口
     * @param dataSource
     * @param transactionManager
     * @return
     * @throws Exception
     */
    @Bean
    public JobLauncher jobLauncher(DruidDataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {

        SimpleJobLauncher simpleJobLauncher = new SimpleJobLauncher();
        simpleJobLauncher.setJobRepository(jobRepository(dataSource, transactionManager ));

        return simpleJobLauncher;

    }

    /**
     * Job定义，我们要实际执行的任务，包含一个或多个Step
     * @param jobBuilderFactory
     * @param s1
     * @return
     */
    @Bean
    public Job importJob(JobBuilderFactory jobBuilderFactory, Step s1) {

        return jobBuilderFactory
                .get("importJob")
                .incrementer(new RunIdIncrementer())
                ////为Job指定Step
                .flow(s1)
                .end()
                //绑定监听器csvJobListener
                .listener(csvJobListener())
                .build();

    }

    @Bean
    public JobExecutionListener csvJobListener() {

        return new CsvJobListener();

    }

    /**
     * step步骤，包含ItemReader，ItemProcessor和ItemWriter
     * @param stepBuilderFactory
     * @param reader
     * @param processor
     * @param writer
     * @return
     */
    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<UserBean> reader,
                      ItemProcessor<UserBean, UserBean> processor, ItemWriter<UserBean> writer) {

        return stepBuilderFactory
                .get("step1")
                //批处理每次提交65000条数据
                .<UserBean, UserBean>chunk(65000)
                //给step绑定reader
                .reader(reader)
                //给step绑定processor
                .processor(processor)
                //给step绑定writer
                .writer(writer)
                .build();

    }


    @Bean
    public Validator csvBeanValidator() {

        return new MyBeanValidator();

    }


}
