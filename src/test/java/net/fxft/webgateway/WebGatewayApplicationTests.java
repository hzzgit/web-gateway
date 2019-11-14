package net.fxft.webgateway;

import net.fxft.cloud.http.HttpUtil;
import net.fxft.common.jdbc.JdbcUtil;
import net.fxft.common.jdbc.JdbcUtil.Operator;
import org.apache.mina.util.ConcurrentHashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebGatewayApplicationTests {


    @Autowired
    private RestTemplate rt;
    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private JdbcUtil jdbc;

    @Test
    public void contextLoads() {
        String str = rt.getForObject("http://security/api/userVehiclePermission?userId=65", String.class);
        System.out.println("==========" + str);
    }

    @Test
    public void test2() {

        RestTemplate rt2 = new RestTemplate();

//        String url = "http://116.62.48.228:8048/CardState/Get";
        String url = "http://116.62.48.228:8048/CardState/Get?cNo=1064890287719&eId=30001";
        ResponseEntity<String> forEntity = rt2.getForEntity(url, String.class);

//        System.out.println(str);

        String str1 = httpUtil.get().url(url).executeString();
        System.out.println("==============" + str1);
//        map.remove("cId");
//        String responseEntity= restTemplate.getForObject(url,String.class,map);
    }

    private static Set<String> orginSet = new ConcurrentHashSet<>();

    @Test
    public void test3() {
        Set<String> ipDomain = getipDomain();
        Set<String> set = new HashSet<>(ipDomain);
        if (orginSet.size() > 0) {
            set.retainAll(orginSet);
        }
        set.stream().forEach(info -> {
            System.out.println(info);
        });
        orginSet = ipDomain;
    }


    public Set<String> getipDomain() {
        String sql = "select ip_domain from platform_config where #{aaa}";
        List<String> query = jdbc.sql(sql)
                .whereName("aaa")
                .and("ip_domain", Operator.RightLike, "http")
                .endWhere()
                .query(String.class);
       return new HashSet<>(query);

    }


}
