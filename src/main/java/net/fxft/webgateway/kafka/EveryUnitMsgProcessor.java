package net.fxft.webgateway.kafka;

import net.fxft.gateway.event.EventMsg;
import net.fxft.gateway.event.IEventBody;
import net.fxft.gateway.event.IEveryUnitMsgProcessor;
import net.fxft.gateway.event.everyunit.UpdateCacheEvent;
import net.fxft.webgateway.service.UserInfoService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EveryUnitMsgProcessor implements IEveryUnitMsgProcessor {

    private static final Logger log = LoggerFactory.getLogger(EveryUnitMsgProcessor.class);
    
    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void beforeKafkaShutdown() {
    }

    @Override
    public void pocessEventMsg(String key, EventMsg eventMsg, ConsumerRecord<String, byte[]> record) {
        if (eventMsg.getEventType() == IEventBody.EventType_UpdateCache) {
            UpdateCacheEvent uce = (UpdateCacheEvent) eventMsg.getEventBody();
            if ("userinfo".equalsIgnoreCase(uce.getCacheName())) {
                userInfoService.updateCache();
                log.debug("接收到UpdateCacheEvent！更新userInfoService的缓存！");
            }
        }
    }
}
