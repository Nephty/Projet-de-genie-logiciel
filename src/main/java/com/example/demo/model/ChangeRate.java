package com.example.demo.model;

import com.example.demo.model.CompositePK.ChangeRatePK;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="change_rate")
public class ChangeRate {
    @EmbeddedId
    private ChangeRatePK changeRatePK;

    @JsonIgnore
    @MapsId("currencyTo")
    @ManyToOne
    @JoinColumn(
            name="currency_to",
            referencedColumnName = "currency_type_id",
            nullable = false
    )
    private CurrencyType currencyTypeTo;

    @JsonIgnore
    @MapsId("currencyFrom")
    @ManyToOne
    @JoinColumn(
            name="currency_from",
            referencedColumnName = "currency_type_id",
            nullable = false
    )
    private CurrencyType currencyTypeFrom;

    @Column(
            name = "change_rate"
    )
    private Double changeRate;

    public ChangeRate(CurrencyType currencyTypeFrom, CurrencyType currencyTypeTo, Double changeRate,Date date) {
        this.currencyTypeTo = currencyTypeTo;
        this.currencyTypeFrom = currencyTypeFrom;
        this.changeRate = changeRate;

        this.changeRatePK = new ChangeRatePK(date,currencyTypeFrom.getCurrencyId(),currencyTypeTo.getCurrencyId());
    }
}
