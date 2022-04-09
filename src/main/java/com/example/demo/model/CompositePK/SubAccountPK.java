package com.example.demo.model.CompositePK;


import com.example.demo.model.SubAccount;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


/**
 * This class is used to make composite primary keys for {@link SubAccount} with Spring data JPA.<br>
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
@ToString
@Embeddable
public class SubAccountPK implements Serializable {
    private String iban;
    private Integer currencyTypeId;

    /**
     * Use for composite PK
     * @see Object#hashCode()
     * @return The hash value of the object.
     */
    @Override
    public int hashCode(){
        return Objects.hash(getIban(), getCurrencyTypeId());
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
        SubAccountPK other = (SubAccountPK) obj;

        return Objects.equals(getIban(), other.getIban())
                && Objects.equals(getCurrencyTypeId(),other.getCurrencyTypeId());
    }
}
