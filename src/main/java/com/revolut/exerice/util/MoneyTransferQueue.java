package com.revolut.exerice.util;

import com.revolut.exerice.core.TransferRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.BlockingQueue;

@Singleton
public class MoneyTransferQueue {

    protected static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private BlockingQueue<TransferRequest> transferRequests;

    public MoneyTransferQueue(BlockingQueue transferQueue) {
        this.transferRequests = transferQueue;
    }

    public long addTransferRequest(TransferRequest transferRequest) throws InterruptedException {
        logger.info("adding request to queue -> {}", transferRequest);
        transferRequests.put(transferRequest);
        return transferRequest.getId();
    }
}
