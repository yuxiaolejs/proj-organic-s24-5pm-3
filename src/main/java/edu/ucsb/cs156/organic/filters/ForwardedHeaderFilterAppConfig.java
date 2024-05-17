package edu.ucsb.cs156.organic.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.filter.ForwardedHeaderFilter;

@Configuration
public class ForwardedHeaderFilterAppConfig extends WebSecurityConfigurerAdapter{

   @Bean // https://stackoverflow.com/questions/51404552/spring-boot-oauth-always-redirecting-to-http-ibm-cloud-cf-spring-boot-2/51500554#51500554
   FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {

      final FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<ForwardedHeaderFilter>();

      filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
      filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

      return filterRegistrationBean;
   }

}
