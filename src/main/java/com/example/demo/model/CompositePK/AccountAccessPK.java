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
public class AccountAccessPK implements Serializable {
    /*
    This class is used to make composite primary keys with JPA.
    composite PK : - must have a non-args constructor
                   - must define the equals() and hashcode() methods
                   - must be serializable
     more info : https://www.baeldung.com/jpa-composite-primary-keys
     */
    private String accountId;
    private String userId;


    @Override
    public int hashCode(){
        return Objects.hash(getAccountId(), getUserId());
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountAccessPK other = (AccountAccessPK) obj;

        return Objects.equals(getAccountId(), other.getAccountId())
                && Objects.equals(getUserId(),other.getUserId());
    }





}
