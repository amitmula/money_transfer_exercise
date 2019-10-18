package com.revolut.exerice.util;

import com.revolut.exerice.core.Account;
import com.revolut.exerice.core.TransferRequest;
import com.revolut.exerice.core.TransferStatus;
import com.revolut.exerice.db.AccountDAO;
import com.revolut.exerice.db.TransferRequestDAO;
import io.dropwizard.hibernate.UnitOfWork;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class MoneyTransferProcessor implements Runnable{

    protected static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    SessionFactory sessionFactory;
    BlockingQueue<TransferRequest> transferRequests;
    AccountDAO accountDAO;
    TransferRequestDAO transferRequestDAO;

    public MoneyTransferProcessor(SessionFactory sessionFactory, BlockingQueue<TransferRequest> transferRequests, AccountDAO accountDAO, TransferRequestDAO transferRequestDAO) {
        this.sessionFactory = sessionFactory;
        this.transferRequests = transferRequests;
        this.accountDAO = accountDAO;
        this.transferRequestDAO = transferRequestDAO;
    }

    private void queueTransfer(TransferRequest transferRequest, SessionFactory sessionFactory) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Optional<Account> senderAccountOpt = accountDAO.findById(transferRequest.getSenderAccountId());
            Optional<Account> recipientAccountOpt = accountDAO.findById(transferRequest.getRecipientAccountId());
            if(!senderAccountOpt.isPresent()) throw new IllegalStateException("invalid senderAccountId !!");
            Account senderAccount = senderAccountOpt.get();
            if(senderAccount.getBalance().compareTo(transferRequest.getAmount()) <= 0) throw new IllegalStateException("insufficient balance in senderAccount !!");
            if(!recipientAccountOpt.isPresent()) throw new IllegalStateException("invalid recipientAccountId !!");
            Account recipientAccount = recipientAccountOpt.get();
            senderAccount.setBalance(senderAccount.getBalance().subtract(transferRequest.getAmount()));
            recipientAccount.setBalance(recipientAccount.getBalance().add(transferRequest.getAmount()));
            session.saveOrUpdate(senderAccount);
            session.saveOrUpdate(recipientAccount);
            transferRequest.setStatus(TransferStatus.PROCESSED);
            session.saveOrUpdate(transferRequest);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        finally {
            session.close();
        }
    }

    @UnitOfWork
    @Override
    public void run() {
        logger.info("consumer started");
        try{
            while(true){
                queueTransfer(transferRequests.take(), this.sessionFactory);
            }
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
}
