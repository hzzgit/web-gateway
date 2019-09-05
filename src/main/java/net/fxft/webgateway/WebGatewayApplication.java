package net.fxft.webgateway;

import net.fxft.cloud.spring.SpringUtil;
import net.fxft.gateway.event.EveryUnitKafkaHelper;
import net.fxft.gateway.event.IEveryUnitKafkaHelper;
import net.fxft.gateway.event.impl.UpdateCacheEventListenerProcesser;
import net.fxft.gateway.kafka.UnitConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * https://github.com/alibaba/Sentinel/wiki/%E7%BD%91%E5%85%B3%E9%99%90%E6%B5%81
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WebGatewayApplication {

    public static void main(String[] args) {
        System.setProperty("nacos.logging.path", "logs/nacos");
        ApplicationContext context = SpringApplication.run(WebGatewayApplication.class, args);
        SpringUtil.invokeAfterStartedRunner(context);
    }

    @Bean
    public UnitConfig createUnitConfig() {
        return new UnitConfig();
    }

    @Bean
    public IEveryUnitKafkaHelper createEveryUnitKafkaHelper() {
        EveryUnitKafkaHelper helper = new EveryUnitKafkaHelper();
        return helper;
    }

    @LoadBalanced
    @Bean
    public RestTemplate createRestTemplate() {
        return new RestTemplate();
    }
}
