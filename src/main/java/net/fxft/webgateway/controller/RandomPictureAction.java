package net.fxft.webgateway.controller;

import io.netty.buffer.UnpooledByteBufAllocator;
import net.fxft.webgateway.route.GatewayRoutes;
import net.fxft.webgateway.service.AutoCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.Random;

/**
 * 验证码输出
 *
 * @author admin
 */
@Controller
@RequestMapping({"/", GatewayRoutes.Base_Prefix})
public class RandomPictureAction extends GenericAction {

    private static final Logger logger = LoggerFactory.getLogger(RandomPictureAction.class);

    @Autowired
    private AutoCacheService cacheService;

    @RequestMapping("/randomPicture.action")
    public Mono<Void> execute(ServerHttpRequest req, ServerHttpResponse resp) throws Exception {
        try {
            Date d = new Date();
            int width = 60;
            int height = 20;
            BufferedImage image = new BufferedImage(width, height, 1);

            Graphics g = image.getGraphics();

            Random random = new Random();

            g.setColor(getRandColor(200, 250));
            g.fillRect(0, 0, width, height);

            g.setFont(new Font("Times New Roman", 0, 18));

            g.setColor(getRandColor(160, 200));
            for (int i = 0; i < 155; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int xl = random.nextInt(12);
                int yl = random.nextInt(12);
                g.drawLine(x, y, x + xl, y + yl);
            }

            String sRand = "";
            for (int i = 0; i < 4; i++) {
                String rand = String.valueOf(random.nextInt(10));
                sRand = sRand + rand;

                g.setColor(new Color(20 + random.nextInt(110), 20 + random
                        .nextInt(110), 20 + random.nextInt(110)));

                g.drawString(rand, 13 * i + 6, 16);
            }
            g.dispose();

            String pickey = req.getQueryParams().getFirst("v");
            cacheService.getCacheMap("randomCode").put(pickey, sRand, 3*60);

            return resp.writeWith(Flux.create(sink -> {
                NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(new UnpooledByteBufAllocator(false));
                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ImageIO.write(image, "jpeg", bos);
                    DataBuffer dataBuffer= nettyDataBufferFactory.wrap(bos.toByteArray());
                    sink.next(dataBuffer);
                } catch (Exception e) {
                    logger.error("写入验证码图片出错！", e);
                }
                sink.complete();
            }));

        } catch (Exception ex) {
            logger.error("生成验证码图片出错! ", ex);
            throw ex;
        }
    }

    private Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

}
