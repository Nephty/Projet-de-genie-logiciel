package com.example.demo.request;

import com.example.demo.model.ChangeRate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRateReq {
    private Date date;

    private Integer currencyTypeFrom;
    private String currencyTypeFromName;

    private Integer currencyTypeTo;
    private String currencyTypeToName;

    private Double changeRate;


    public ChangeRateReq(ChangeRate changeRate){
        date = changeRate.getChangeRatePK().getDate();

        currencyTypeFrom = changeRate.getCurrencyTypeFrom().getCurrencyId();
        currencyTypeFromName = changeRate.getCurrencyTypeFrom().getCurrencyTypeName();

        currencyTypeTo = changeRate.getCurrencyTypeTo().getCurrencyId();
        currencyTypeToName = changeRate.getCurrencyTypeTo().getCurrencyTypeName();

        this.changeRate = changeRate.getChangeRate();
    }
}
