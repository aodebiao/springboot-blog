package com.loocc.config;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.jdbc.core.JdbcTemplate;
        import org.springframework.jdbc.datasource.DriverManagerDataSource;
@Configuration
public class DataSourceConfig {
    ////@Value("spring.datasource.url")
    //private String url = "jdbc:mysql://localhost:3306/tb_blog?serverTimezone=Asia/Shanghai&useUnicode=false";
    ////@Value("spring.datasource.password")
    //private String password = "1460088689";
    ////@Value("spring.datasource.username")
    //private String username = "root";
    @Value("${spring.datasource.url}")
    private String url ;
    @Value("${spring.datasource.username}")
    private String username ;
    @Value("${spring.datasource.password}")
    private String password ;
    private DriverManagerDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    @Bean
    DriverManagerDataSource dataSource(){
         dataSource = new DriverManagerDataSource(url,username,password);
         return dataSource;
    }

    @Bean
    JdbcTemplate jdbcTemplate(DriverManagerDataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }

}
