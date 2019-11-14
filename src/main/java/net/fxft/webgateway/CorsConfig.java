package net.fxft.webgateway;

import net.fxft.common.util.BasicUtil;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.Set;

@Configuration
public class CorsConfig {
    
    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    private static CorsConfiguration corsConfig;
    private static Set<String> orginSet = new ConcurrentHashSet<>();

    @Bean
    public CorsWebFilter corsFilter(@Value("${cors.orgins}") String origins) {
        corsConfig = new CorsConfiguration();
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.setAllowCredentials(true);
        log.info("cors.AllowedOrigin=" + origins);
        for (String origin : origins.split(",")) {
            origin = origin.trim();
            if(BasicUtil.isNotEmpty(origin)) {
                corsConfig.addAllowedOrigin(origin);
            }
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }

    public static void addOrigin(String origin) {
        corsConfig.addAllowedOrigin(origin);
    }

    @Scheduled(fixedDelay = 60000)
    public void updateOrgin() {

    }

}
