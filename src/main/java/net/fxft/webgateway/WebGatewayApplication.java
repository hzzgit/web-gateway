package net.fxft.webgateway;

import net.fxft.cloud.spring.SpringUtil;
import net.fxft.gateway.event.EveryUnitKafkaHelper;
import net.fxft.gateway.kafka.UnitConfig;
import net.fxft.webgateway.kafka.EveryUnitMsgProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * https://github.com/alibaba/Sentinel/wiki/%E7%BD%91%E5%85%B3%E9%99%90%E6%B5%81
 */
@SpringBootApplication
@EnableDiscoveryClient
public class WebGatewayApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(WebGatewayApplication.class, args);
        SpringUtil.invokeAfterStartedRunner(context);
    }

    @Bean
    public UnitConfig createUnitConfig() {
        return new UnitConfig();
    }

    @Bean
    public EveryUnitKafkaHelper createEveryUnitKafkaHelper(EveryUnitMsgProcessor msgProcessor) {
        EveryUnitKafkaHelper helper = new EveryUnitKafkaHelper();
        helper.addIEveryUnitMsgProcessor(msgProcessor);
        return helper;
    }

}
