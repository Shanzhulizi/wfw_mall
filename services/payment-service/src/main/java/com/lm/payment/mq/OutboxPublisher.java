package com.lm.payment.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OutboxPublisher {
//
//    @Autowired
//    OutboxRepository outboxRepository;
//    @Autowired
//    RocketMQTemplate rocketMQTemplate;
//
//    // 定时任务/单线程轮询
//    @Scheduled(fixedDelay = 2000)
//    public void pollAndPublish() {
//        List<OutboxEvent> events = outboxRepository.findUnsentEvents(PageRequest.of(0, 20));
//        for (OutboxEvent ev : events) {
//            try {
//                rocketMQTemplate.convertAndSend("PAYMENT_TOPIC:PAYMENT_EVENTS", ev.getPayload());
//                ev.setSent(true);
//                ev.setSentTime(LocalDateTime.now());
//                outboxRepository.save(ev);
//            } catch (Exception e) {
//                // 发送失败：记录日志，下一轮重试
//            }
//        }
//    }
}