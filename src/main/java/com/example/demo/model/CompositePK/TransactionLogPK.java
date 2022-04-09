package com.example.demo.model.CompositePK;

import com.example.demo.model.TransactionLog;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;


/**
 * This class is used to make composite primary keys for {@link TransactionLog} with Spring data JPA.<br>
 * CompositePK : <ul>
 *                  <li>must have a non-args constructor</li>
 *                  <li>must define the equals() and hashcode() methods</li>
 *                  <li>must be serializable</li>
 *              </ul> <br>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionLogPK implements Serializable {

    private Integer transactionId;
    private Boolean isSender;

    /**
     * Use for composite PK
     * @see Object#hashCode()
     * @return The hash value of the object.
     */
    @Override
    public int hashCode(){
        return Objects.hash(getTransactionId(), getIsSender());
    }

    /**
     * Use for compositePK
     * @see Object#equals(Object)
     * @param obj The object to compare with
     * @return true if it's the same object, false otherwise
     */
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
                && Objects.equals(getIsSender(),other.getIsSender());
    }
}
