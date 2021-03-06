package com.spring.data.cassandra.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cassandra.config.CassandraCqlClusterFactoryBean;
import org.springframework.cassandra.config.CassandraCqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CassandraSessionFactoryBean;
import org.springframework.data.cassandra.config.SchemaAction;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.convert.MappingCassandraConverter;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.mapping.SimpleUserTypeResolver;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@SpringBootApplication
public class SpringDataCassandraDemo1Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringDataCassandraDemo1Application.class, args);
	}
	
	@Bean
	public CassandraCqlClusterFactoryBean cluster(){
		CassandraCqlClusterFactoryBean factory = new CassandraCqlClusterFactoryBean();
		factory.setContactPoints("localhost");
		return factory;
	}
	
	@Bean
	public CassandraMappingContext mappingContext(){
		BasicCassandraMappingContext mapping = new BasicCassandraMappingContext();
		mapping.setUserTypeResolver(new SimpleUserTypeResolver(cluster().getObject(), "espacio_1"));
		return mapping;
	}
	
	@Bean
	public MappingCassandraConverter converter(){
		MappingCassandraConverter converter = new MappingCassandraConverter(mappingContext());
		return converter;
	}
	
	@Bean
	public CassandraSessionFactoryBean session(){
		CassandraSessionFactoryBean factory = new CassandraSessionFactoryBean();
		factory.setCluster(cluster().getObject());
		factory.setKeyspaceName("espacio_1");
		factory.setConverter(converter());
		factory.setSchemaAction(SchemaAction.CREATE_IF_NOT_EXISTS);
		return factory;
	}
	
	@Bean
	public CassandraOperations cassandraTemplate(){
		CassandraTemplate template = new CassandraTemplate(session().getObject());
		return template;
	}
	
	@Configuration
	@EnableCassandraRepositories(basePackages = "com.spring.data.cassandra.demo1.repository")
	public class CassandraConfig extends AbstractCassandraConfiguration {
		
		@Override
		protected String getKeyspaceName() {
			return "espacio_1";
		}
		
		@Override
		public String[] getEntityBasePackages() {
			return new String[]{"com.spring.data.cassandra.demo1.entity"};
		}
	}
}
