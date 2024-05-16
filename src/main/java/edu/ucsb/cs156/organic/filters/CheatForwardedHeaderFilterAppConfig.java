package edu.ucsb.cs156.organic.filters;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class CheatForwardedHeaderFilterAppConfig {

   @Bean // https://stackoverflow.com/questions/51404552/spring-boot-oauth-always-redirecting-to-http-ibm-cloud-cf-spring-boot-2/51500554#51500554
   FilterRegistrationBean<CheatForwardedHeaderFilter> forwardedHeaderFilter() {

      final FilterRegistrationBean<CheatForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<CheatForwardedHeaderFilter>();

      filterRegistrationBean.setFilter(new CheatForwardedHeaderFilter());
      filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);

      return filterRegistrationBean;
   }

}
