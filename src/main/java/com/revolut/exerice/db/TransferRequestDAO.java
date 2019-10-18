package com.revolut.exerice.db;

import com.revolut.exerice.core.TransferRequest;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class TransferRequestDAO extends AbstractDAO<TransferRequest> {
    public TransferRequestDAO(SessionFactory factory) {
        super(factory);
    }

    public TransferRequest save(TransferRequest transferRequest) {
        return persist(transferRequest);
    }

    public Optional<TransferRequest> findById(Long id) {
        return Optional.ofNullable(get(id));
    }

    @SuppressWarnings("unchecked")
    public List<TransferRequest> findAll() {
        return list((Query<TransferRequest>) namedQuery("com.revolut.exerice.core.TransferRequest.findAll"));
    }
}
