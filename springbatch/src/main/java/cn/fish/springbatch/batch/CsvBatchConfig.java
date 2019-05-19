package cn.fish.springbatch.batch;

import cn.fish.springbatch.bean.UserBean;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
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
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.validator.Validator;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


/**
 * @Description:
 * @Author devin.jiang
 * @CreateDate 2019/5/11 21:34
 */
@Configuration
@EnableBatchProcessing
public class CsvBatchConfig {
    @Autowired
    DataSource dataSource;
    @Autowired
    PlatformTransactionManager platformTransactionManager;
    @Bean
    public ItemReader<UserBean> reader() throws Exception{
        FlatFileItemReader<UserBean> reader = new FlatFileItemReader<>();
        reader.setName("readCsv");
        reader.setResource(new ClassPathResource("user.csv"));
        DefaultLineMapper<UserBean> defaultLineMapper = new DefaultLineMapper<>();
        reader.setLineMapper(defaultLineMapper);

        DelimitedLineTokenizer tokenizer ;
        defaultLineMapper.setLineTokenizer((tokenizer =new DelimitedLineTokenizer()));
        tokenizer.setNames(new String[]{"name","age","nation","address"});

        BeanWrapperFieldSetMapper<UserBean> setMapper;
        defaultLineMapper.setFieldSetMapper((setMapper = new BeanWrapperFieldSetMapper<>()));
        setMapper.setTargetType(UserBean.class);

        return reader;
    }

    @Bean
    public ItemProcessor<UserBean,UserBean> processor(){
        CsvItemProcessor processor = new CsvItemProcessor();
        processor.setValidator(csvBeanValidator());
        return processor;
    }

    @Bean
    public ItemWriter<UserBean> writer(){
        JdbcBatchItemWriter<UserBean> writer = new JdbcBatchItemWriter<>();
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<UserBean>());
        String sql = "insert into person (name,age,nation,address) values (:name,:age,:nation,:address)";
        writer.setSql(sql);
        writer.setDataSource(dataSource);
        return writer;
    }

    @Bean
    public JobRepository jobRepository() throws Exception{
        JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
        jobRepositoryFactoryBean.setDataSource(dataSource);
        jobRepositoryFactoryBean.setTransactionManager(platformTransactionManager);
        jobRepositoryFactoryBean.setDatabaseType(DatabaseType.MYSQL.name());
        return jobRepositoryFactoryBean.getObject();
    }

    @Bean
    public SimpleJobLauncher jobLauncher()throws Exception{
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository());
        return jobLauncher;

    }

    @Bean
    public Job importJob(JobBuilderFactory jobs, Step step1){
        return jobs.get("importJob")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .end()
                .listener(csvJobListner())
                .build();
    }

    @Bean
    public Job importJob2(JobBuilderFactory jobs, Step step1,Step step2,Step step3,Step step4){
        return jobs.get("importJob2")
                .incrementer(new RunIdIncrementer())
                .flow(step1)
                .next(step2)
                .next(step3)
                .next(step4)
                .end()
                .listener(csvJobListner())
                .build();
    }

    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory,ItemReader<UserBean> reader,ItemWriter<UserBean> writer,
                      ItemProcessor<UserBean,UserBean> processor){
        return stepBuilderFactory.get("step1")
                    .<UserBean,UserBean>chunk(1)
                    .reader(reader)
                    .processor(processor)
                    .writer(writer)
                    .build();
    }

    @Bean
    public Step step2(StepBuilderFactory stepBuilderFactory,ItemReader<UserBean> reader,ItemWriter<UserBean> writer,
                      ItemProcessor<UserBean,UserBean> processor){
        return stepBuilderFactory.get("step2")
                .<UserBean,UserBean>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step step3(StepBuilderFactory stepBuilderFactory,ItemReader<UserBean> reader,ItemWriter<UserBean> writer,
                      ItemProcessor<UserBean,UserBean> processor){
        return stepBuilderFactory.get("step3")
                .<UserBean,UserBean>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Step step4(StepBuilderFactory stepBuilderFactory,ItemReader<UserBean> reader,ItemWriter<UserBean> writer,
                      ItemProcessor<UserBean,UserBean> processor){
        return stepBuilderFactory.get("step4")
                .<UserBean,UserBean>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();

    }

    @Bean
    public CsvJobListner csvJobListner(){
        return new CsvJobListner();
    }

    @Bean
    public Validator<UserBean> csvBeanValidator(){
        return new CsvBeanValidator<UserBean>();
    }
}
