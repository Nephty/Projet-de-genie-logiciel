package com.example.demo.scheduler;

import com.example.demo.model.ChangeRate;
import com.example.demo.model.CurrencyType;
import com.example.demo.repository.ChangeRateRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.fasterxml.jackson.core.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChangeRateScheduler extends AbstractScheduler {

    private final ChangeRateRepo changeRateRepo;
    private final CurrencyTypeRepo currencyTypeRepo;

    @Scheduled(initialDelay = 2, fixedRate = day, timeUnit = TimeUnit.SECONDS)
    public void fetchAllChangeRates() {
        if (changeRateRepo.findLastFetch().before(Date.valueOf(LocalDate.now()))){
            List<CurrencyType> currencies = currencyTypeRepo.findAll();
            for (CurrencyType currencyTypeFrom : currencies) {
                JSONObject obj = fetchChangeRateForCurrency(currencyTypeFrom.getCurrencyTypeName());
                if (obj != null) {
                    log.info(obj.getString("result"));
                    JSONObject rates = obj.getJSONObject("conversion_rates");
                    for (CurrencyType currencyTypeTo : currencies) {
                        if (currencyTypeTo != currencyTypeFrom) {
                            log.info("Fetching From " + currencyTypeFrom.getCurrencyTypeName() + " To " + currencyTypeTo.getCurrencyTypeName());
                            ChangeRate tmp = new ChangeRate(
                                    currencyTypeTo,
                                    currencyTypeFrom,
                                    rates.getDouble(currencyTypeTo.getCurrencyTypeName()),
                                    Date.valueOf(LocalDate.now())
                            );
                            changeRateRepo.save(tmp);
                            log.info("ChangeRate = " + tmp);
                        }
                    }
                }
            }
        } else {
            log.warn("fetching was already done today");
        }
    }


    public static JSONObject fetchChangeRateForCurrency(String currencyName) {
        try {
            // Setting URL
            String url_str = "https://v6.exchangerate-api.com/v6/5ed135a84ef1b1a9dfa1eeb0/latest/" + currencyName;

            // Making Request
            HttpResponse<String> res = Unirest.get(url_str)
                    .asString();

            // Convert to JSON
            return new JSONObject(res.getBody());
        } catch (UnirestException e) {
            log.error("Can't fetch change rate for "+currencyName +"\n"+e.getMessage());
            return null;
        }
    }
}
