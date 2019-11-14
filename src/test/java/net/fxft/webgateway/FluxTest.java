package net.fxft.webgateway;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

public class FluxTest {
    
    private static final Logger log = LoggerFactory.getLogger(FluxTest.class);

    @Test
    public void testMap() throws InterruptedException {
        Flux.just(1, 2, 3, 4).concatWithValues(5,6,7)
                .log()
                .map(i -> {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return i * 2;
                })
                .subscribe(e -> log.info("get:{}",e));
    }

}
