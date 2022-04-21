package com.example.demo.model.CompositePK;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ChangeRatePK implements Serializable {

    private Date date;
    private Integer currencyFrom;
    private Integer currencyTo;


    /**
     * Use for composite PK
     *
     * @return The hash value of the object.
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCurrencyFrom(),getCurrencyTo(), getDate());
    }

    /**
     * Use for compositePK
     *
     * @param obj The object to compare with
     * @return true if it's the same object, false otherwise
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChangeRatePK other = (ChangeRatePK) obj;

        return Objects.equals(getCurrencyFrom(), other.getCurrencyFrom())
                && Objects.equals(getDate(), other.getDate())
                && Objects.equals(getCurrencyTo(),other.getCurrencyTo());
    }
}
