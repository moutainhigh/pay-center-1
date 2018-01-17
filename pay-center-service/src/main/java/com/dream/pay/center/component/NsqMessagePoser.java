package com.dream.pay.center.component;

import com.google.common.collect.Maps;
import com.youzan.dts.client.api.TransactionActivityService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;

/**
 * @Author mengzhenbin
 * @Since 2018/1/10
 */
@Slf4j
public class NsqMessagePoser {

    @Resource
    private TransactionActivityService transactionActivityService;

    /**
     * 发送消息
     */
    public void send(String topic, String txId, String message) {
        log.info("发送支付消息开始:[{}]-[{}]", topic, message);
        try {
            //开启分布式事务
            transactionActivityService
                    .start("pay_center", txId, Maps.newHashMap());

            transactionActivityService.enrollAction(topic, message);
        } catch (DuplicateKeyException e) {
            log.error("开启消息事务失败,业务号重复:业务类型=[{}],业务号=[{}]", "pay_center", txId);
        } catch (Exception e) {
            log.error("开启消息事务失败,未知异常:业务类型=[{}],业务号=[{}]", "pay_center", txId, e);
        }
        log.info("发送支付消息结束:[{}]-[{}]", topic, message);

    }
}