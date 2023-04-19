package com.atguigu.auth;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan("com.atguigu") //默认搜索当前包及其子包的内容。这里可以搜索到所有com.atguigu路径下的component
public class ServiceAuthApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceAuthApplication.class, args);

//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ServiceAuthApplication.class);
//
//        for (String beanName : context.getBeanDefinitionNames()) {
//            System.out.println(beanName);
//        }
    }
}
