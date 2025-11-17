package com.ecommerce.ops.jobs;

import com.ecommerce.ops.entity.AuditLogs;
import com.ecommerce.ops.entity.CronJobBatchRetry;
import com.ecommerce.ops.entity.CronJobLogs;
import com.ecommerce.ops.entity.Orders;
import com.ecommerce.ops.enums.OrderStatus;
import com.ecommerce.ops.repository.AuditLogRepository;
import com.ecommerce.ops.repository.CronJobBatchRetryRepository;
import com.ecommerce.ops.repository.CronJobLogRepository;
import com.ecommerce.ops.repository.OrderRepository;
import com.ecommerce.ops.utils.AuditUtil;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
public class OrderProcessingJob {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CronJobLogRepository cronJobLogRepository;

    @Autowired
    private CronJobBatchRetryRepository cronJobBatchRetryRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    private static final Logger logger = LoggerFactory.getLogger(OrderProcessingJob.class);

    private static final int BATCH_SIZE = 500;

    private static final int RETRY_THRESHOLD = 3;
    private static final int RETRY_BATCH_COUNT = 3;
    private static final String ORDER_PENDING_TO_PROCESSING_JOB = "ORDER_PENDING_TO_PROCESSING_JOB";
    private static final String RETRY_ORDER_PENDING_TO_PROCESSING_JOB = "RETRY_ORDER_PENDING_TO_PROCESSING_JOB";

    @Transactional
    @Scheduled(cron = "0 */1 * * * ?") // every 5 minutes
    public void processOrders() {
        try {
            logger.info("Start processOrders");
            CronJobLogs lastLog = cronJobLogRepository
                    .findTopByJobNameOrderByEndIndexDesc("ORDER_PENDING_TO_PROCESSING_JOB")
                    .orElse(null);

            Long lastId = lastLog != null ? lastLog.getEndIndex() : 0L;
            logger.info("processOrders lastId:{}", lastId);
            orderProcessing(lastId);
            logger.info("End processOrders");
        } catch (Exception e) {
            logger.error("Error occurred in processOrders:{}", e.getStackTrace());
        }

    }

    @Transactional
    @Scheduled(cron = "0 */15 * * * ?") // every 5 minutes
    public void retryPendingOrders() {
        try {
            logger.info("Start retryPendingOrders");
            List<CronJobBatchRetry> cronJobBatchRetries =
                    cronJobBatchRetryRepository
                            .findPendingRetryBatches(RETRY_ORDER_PENDING_TO_PROCESSING_JOB, RETRY_THRESHOLD,
                                    PageRequest.of(0, RETRY_BATCH_COUNT));

            for (CronJobBatchRetry cronJobBatchRetry : cronJobBatchRetries) {
                retryOrderProcessing(cronJobBatchRetry);
            }
            logger.info("End retryPendingOrders");
        } catch (Exception e) {
            logger.error("Error occurred in retryPendingOrders:{}", e.getStackTrace());
        }
    }


    private void orderProcessing(Long lastId) {
        List<Orders> pendingOrders = orderRepository.findPendingOrdersAfterId(OrderStatus.PENDING,
                lastId, PageRequest.of(0, BATCH_SIZE));


        if (CollectionUtils.isEmpty(pendingOrders)) {
            CronJobLogs cronJobLogs = new CronJobLogs(ORDER_PENDING_TO_PROCESSING_JOB, 0,
                    0, 0, 0l, 0l);
            cronJobLogRepository.save(cronJobLogs);
            logger.info("No pending orders to process");
            return;
        }

        int success = 0;
        int failure = 0;
        Long pendingOrderStartId = pendingOrders.get(0).getId();
        Long pendingOrderEndId = pendingOrders.get(pendingOrders.size() - 1).getId();

        logger.info("orderProcessing -  pendingOrderStartId:{}, and pendingOrderEndId: {}", pendingOrderStartId, pendingOrderEndId);
        CronJobLogs cronJobLogs = new CronJobLogs(ORDER_PENDING_TO_PROCESSING_JOB, pendingOrders.size(), 0, 0,
                pendingOrderStartId, pendingOrderEndId);

        for (Orders order : pendingOrders) {
            try {
                String prevData = AuditUtil.toJson(order);

                order.setOrderStatus(OrderStatus.PROCESSING);

                String currData = AuditUtil.toJson(order);
                saveAuditLogs(Orders.class.getSimpleName(), prevData, currData);

                orderRepository.save(order);
                success++;
            } catch (Exception e) {
                failure++;
                logger.error("Exception ocuured in order processing for order Id:{}", order.getId());
            }
        }

        logger.info("Success count is : {}", success);
        if (failure > 0) {
            logger.info("Failure count is : {}", failure);
            CronJobBatchRetry cronJobBatchRetry = new CronJobBatchRetry(pendingOrderStartId, pendingOrderEndId,
                    RETRY_ORDER_PENDING_TO_PROCESSING_JOB);
            cronJobBatchRetryRepository.save(cronJobBatchRetry);
        }

        cronJobLogs.setSuccessCount(success);
        cronJobLogs.setFailureCount(failure);

        cronJobLogRepository.save(cronJobLogs);
        logger.info("saved cronJobLogs");
    }

    private void retryOrderProcessing(CronJobBatchRetry cronJobBatchRetry) {
        List<Orders> pendingOrders = orderRepository.findPendingOrdersBetweenStartAndEndId(OrderStatus.PENDING,
                cronJobBatchRetry.getStartIndex(), cronJobBatchRetry.getEndIndex(), PageRequest.of(0, BATCH_SIZE));

        if (CollectionUtils.isEmpty(pendingOrders)) {
            CronJobLogs cronJobLogs = new CronJobLogs(RETRY_ORDER_PENDING_TO_PROCESSING_JOB, 0,
                    0, 0, 0l, 0l);
            cronJobLogRepository.save(cronJobLogs);
            logger.info("No pending orders to process in retryOrderProcessing");
            return;
        }

        int success = 0;
        int failure = 0;

        CronJobLogs cronJobLogs = new CronJobLogs(RETRY_ORDER_PENDING_TO_PROCESSING_JOB, pendingOrders.size(), 0, 0,
                cronJobBatchRetry.getStartIndex(), cronJobBatchRetry.getEndIndex());

        for (Orders order : pendingOrders) {
            try {
                String prevData = AuditUtil.toJson(order);

                order.setOrderStatus(OrderStatus.PROCESSING);

                String currData = AuditUtil.toJson(order);
                saveAuditLogs(Orders.class.getSimpleName(), prevData, currData);

                orderRepository.save(order);
                success++;
            } catch (Exception e) {
                failure++;
                logger.error("Exception ocuured in order processing for order Id:{}", order.getId());
            }
        }

        logger.info("Success count in retryOrderProcessing : {}", success);
        logger.info("Failure count in retryOrderProcessing : {}", failure);

        String prevData = AuditUtil.toJson(cronJobBatchRetry);

        cronJobBatchRetry.setRetryCount(cronJobBatchRetry.getRetryCount() + 1);

        String currData = AuditUtil.toJson(cronJobBatchRetry);
        saveAuditLogs(cronJobBatchRetry.getClass().getSimpleName(), prevData, currData);

        cronJobBatchRetryRepository.save(cronJobBatchRetry);

        logger.info("Retry count is :{}", cronJobBatchRetry.getRetryCount());

        cronJobLogs.setSuccessCount(success);
        cronJobLogs.setFailureCount(failure);

        cronJobLogRepository.save(cronJobLogs);
        logger.info("Saved cronJobLogs in retryOrderProcessing");
    }

    private void saveAuditLogs(String prevData, String currData, String tableName){
        logger.info("creating audit logs");
        AuditLogs auditLogs = new AuditLogs(tableName, prevData, currData);
        auditLogRepository.save(auditLogs);
    }
}
