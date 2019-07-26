package com.kavy.springsecuritydemo.config;

import com.kavy.springsecuritydemo.filter.JwtAuthenticationEntryPoint;
import com.kavy.springsecuritydemo.filter.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @EnableWebSecurity注解继承WebSecurityConfigurerAdapter的类，这样就构成了Spring Security的配置。
 *
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    public WebSecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDetailsService = userDetailsService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        // We don't need CSRF for this example
        httpSecurity.csrf().disable()
                //用来解决匿名用户访问无权限资源时的异常
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                //禁用session 无状态
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                // dont authenticate this particular request
                .and()
                .authorizeRequests()
                //注册 register 和登录 login 不需要验证 就可以访问，其他的都需要token验证才可以访问
                .antMatchers(HttpMethod.POST, "/api/login", "/api/register", "/error/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // disable page caching
        httpSecurity
                .headers()
                .frameOptions().sameOrigin()  // required to set for H2 else H2 Console will be blank.
                .cacheControl();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);

    }

/*    @Override
    protected void configure(HttpSecurity http) throws Exception {
         http.formLogin() // 表单方式
      //  http.httpBasic() // HTTP Basic方式
                .loginPage("/login.html")   //.loginPage("/login.html")指定了跳转到登录页面的请求URL
                .loginProcessingUrl("/login") //.loginProcessingUrl("/login")对应登录页面form表单的action="/login"
                .and()
                .authorizeRequests() // 授权配置
                .antMatchers("/login.html").permitAll()   // 表示跳转到登录页面的请求不被拦截，否则会进入无限循环。
                .anyRequest()  // 所有请求
                .authenticated() // 都需要认证
                .and().csrf().disable();
    }*/



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


}