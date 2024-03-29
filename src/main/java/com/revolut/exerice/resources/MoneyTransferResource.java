package com.revolut.exerice.resources;


import com.revolut.exerice.core.TransferRequest;
import com.revolut.exerice.db.TransferRequestDAO;
import com.revolut.exerice.util.MoneyTransferQueue;
import io.dropwizard.hibernate.UnitOfWork;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;

@Path("/transfer")
@Produces(MediaType.APPLICATION_JSON)
public class MoneyTransferResource {

    TransferRequestDAO transferRequestDAO;
    private MoneyTransferQueue moneyTransferQueue;

    public MoneyTransferResource(TransferRequestDAO transferRequestDAO, MoneyTransferQueue moneyTransferQueue) {
        this.transferRequestDAO = transferRequestDAO;
        this.moneyTransferQueue = moneyTransferQueue;
    }

    @POST
    @UnitOfWork
    public TransferRequest transferMoney(TransferRequest transferRequest) throws InterruptedException {
        return moneyTransferQueue.addTransferRequest(transferRequestDAO.save(transferRequest));
    }

    @GET
    @Path("{id}")
    @UnitOfWork
    public Optional<TransferRequest> getAccount(@PathParam("id") String id) {
        return transferRequestDAO.findById(Long.valueOf(id));
    }

    @GET
    @UnitOfWork
    public List<TransferRequest> listTransferRequests() {
        return transferRequestDAO.findAll();
    }

}
