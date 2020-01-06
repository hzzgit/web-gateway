package net.fxft.webgateway;

import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.JdbcUtil.Operator;
import net.fxft.common.util.BasicUtil;
import org.apache.mina.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);

    private static CorsConfiguration corsConfig;
    private static Set<String> orginSet = new ConcurrentHashSet<>();
    @Autowired
    private JdbcUtil jdbc;

    @Bean
    public CorsWebFilter corsFilter(@Value("${cors.orgins}") String origins) {
        corsConfig = new CorsConfiguration();
        corsConfig.addAllowedMethod("*");
        corsConfig.addAllowedHeader("*");
        corsConfig.addExposedHeader("Authorization");
        corsConfig.setAllowCredentials(true);
        log.info("cors.AllowedOrigin=" + origins);
        for (String origin : origins.split(",")) {
            origin = origin.trim();
            if (BasicUtil.isNotEmpty(origin)) {
                addOrigin(origin);
            }
        }
        updateOrgin();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(new PathPatternParser());
        source.registerCorsConfiguration("/**", corsConfig);
        return new CorsWebFilter(source);
    }

    public static void addOrigin(String origin) {
        corsConfig.addAllowedOrigin(origin);
        orginSet.add(origin);
        log.info("添加跨域origin: " + origin);
    }

    @Scheduled(fixedDelay = 30000)
    public void updateOrgin() {
        Set<String> ipDomain = getipDomain();
        if (ipDomain.size() <= 0) {
            return;
        }
        ipDomain.stream().filter(info -> {
            if (orginSet.contains(info)) {
            return false;
            }
            return true;
        }).forEach(info -> {
            addOrigin(info);

        });
      }

    public Set<String> getipDomain() {
        String sql = "select ip_domain from platform_config where #{aaa}";
        List<String> query = jdbc.sql(sql)
                .whereName("aaa")
                .and("ip_domain", Operator.RightLike, "http")
                .endWhere()
                .query(String.class);
        Set<String> ipDomain = new HashSet<>(query);
        return ipDomain;
    }


}
