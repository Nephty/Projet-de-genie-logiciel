package com.example.demo.model.CompositePK;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLogPK implements Serializable {

    private Integer transactionId;
    private Integer direction;

    @Override
    public int hashCode(){
        return Objects.hash(getTransactionId(), getDirection());
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransactionLogPK other = (TransactionLogPK) obj;

        return Objects.equals(getTransactionId(), other.getTransactionId())
                && Objects.equals(getDirection(),other.getDirection());
    }
}
