package com.revolut.exerice.core;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Entity
@Table(name = "transfers")
@NamedQueries({
    @NamedQuery(
        name = "com.revolut.exerice.core.TransferRequest.findAll",
        query = "SELECT t FROM TransferRequest t"
    )
})
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class TransferRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "sender_account_id", nullable = false)
    private long senderAccountId;

    @Column(name = "recipient_account_id", nullable = false)
    private long recipientAccountId;

    @Column(name = "amount", nullable = false)
    @DecimalMin(value = "0.1", message = "transfer value must be greater than 0.1")
    private BigDecimal amount;

    @Column(name = "status")
    private TransferStatus status = TransferStatus.SUBMITTED;

    public TransferRequest(long senderAccountId, long recipientAccountId, BigDecimal amount) {
        this.senderAccountId = senderAccountId;
        this.recipientAccountId = recipientAccountId;
        this.amount = amount;
    }
}
