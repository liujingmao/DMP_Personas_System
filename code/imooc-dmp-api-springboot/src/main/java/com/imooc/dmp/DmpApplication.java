package com.imooc.dmp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(nameGenerator = DmpApplication.SpringBeanNameGenerator.class)
@EnableElasticsearchRepositories(basePackages = "com.imooc.dmp.dao.es")
public class DmpApplication {

    /**
     * 解决Spring类名不能重复的冲突
     */
    public static class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {
        @Override
        protected String buildDefaultBeanName(BeanDefinition definition) {
            if (definition instanceof AnnotatedBeanDefinition) {
                String beanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
                if (StringUtils.hasText(beanName)) {
                    // Explicit bean name found.
                    return beanName;
                }
            }
            return definition.getBeanClassName();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(DmpApplication.class, args);
    }

}
