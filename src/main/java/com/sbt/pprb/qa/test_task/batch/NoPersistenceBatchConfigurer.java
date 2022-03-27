package com.sbt.pprb.qa.test_task.batch;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.DefaultExecutionContextSerializer;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Component
public class NoPersistenceBatchConfigurer extends DefaultBatchConfigurer {

    private final JobRepository jobRepository;
    private final JobExplorer jobExplorer;
    private final JobLauncher jobLauncher;

    public NoPersistenceBatchConfigurer(PlatformTransactionManager transactionManager,
                                        JdbcOperations jdbcOperations) throws Exception {

        JobRepositoryFactoryBean repositoryFactory = new JobRepositoryFactoryBean();
        repositoryFactory.setDataSource(dataSource());
        repositoryFactory.setDatabaseType("H2");
        repositoryFactory.setTransactionManager(transactionManager);
        repositoryFactory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        repositoryFactory.setTablePrefix("BATCH_");
        repositoryFactory.setMaxVarCharLength(1000);

        this.jobRepository = repositoryFactory.getObject();

        JobExplorerFactoryBean explorerFactory = new JobExplorerFactoryBean();
        explorerFactory.setJdbcOperations(jdbcOperations);
        explorerFactory.setSerializer(new DefaultExecutionContextSerializer());

        this.jobExplorer = explorerFactory.getObject();

        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(this.jobRepository);
        jobLauncher.setTaskExecutor(new SyncTaskExecutor());

        this.jobLauncher = jobLauncher;
    }

    @Override
    public JobRepository getJobRepository() {
        return jobRepository;
    }

    @Override
    public JobExplorer getJobExplorer() {
        return jobExplorer;
    }

    @Override
    public JobLauncher getJobLauncher() {
        return jobLauncher;
    }

    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("/org/springframework/batch/core/schema-drop-h2.sql")
                .addScript("/org/springframework/batch/core/schema-h2.sql")
                .build();
    }
}
