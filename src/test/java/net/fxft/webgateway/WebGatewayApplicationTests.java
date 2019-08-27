package net.fxft.webgateway;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebGatewayApplicationTests {



    @Autowired
    private RestTemplate rt;

    @Test
    public void contextLoads() {
        String str = rt.getForObject("http://security/api/userVehiclePermission?userId=65", String.class);
        System.out.println("=========="+str );

    }

}
