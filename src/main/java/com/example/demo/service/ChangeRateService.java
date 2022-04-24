package com.example.demo.service;

import com.example.demo.controller.AccountAccessController;
import com.example.demo.controller.ChangeRateController;
import com.example.demo.exception.throwables.ResourceNotFound;
import com.example.demo.model.AccountAccess;
import com.example.demo.model.ChangeRate;
import com.example.demo.model.CompositePK.ChangeRatePK;
import com.example.demo.model.CurrencyType;
import com.example.demo.repository.AccountAccessRepo;
import com.example.demo.repository.ChangeRateRepo;
import com.example.demo.repository.CurrencyTypeRepo;
import com.example.demo.request.AccountAccessReq;
import com.example.demo.request.ChangeRateReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Links the {@link ChangeRateController} with the {@link ChangeRateRepo}.
 * In this class, all the modifications and the calls to the {@link ChangeRateRepo} are made.
 *
 * @see ChangeRate
 */
@RequiredArgsConstructor
@Transactional
@Slf4j
@Service
public class ChangeRateService {
    private final ChangeRateRepo changeRateRepo;
    private final CurrencyTypeRepo currencyTypeRepo;

    /**
     * Gets the ChangeRate that match the params
     * @param date The date of the changeRate
     * @param currencyFrom CurrencyId we want to change
     * @param currencyTo CurrencyId we want to get
     * @return The req body of the ChangeRate
     * @throws ResourceNotFound If the changeRate doesn't exist
     */
    public ChangeRateReq getChangeRate(Date date, Integer currencyFrom, Integer currencyTo)
            throws ResourceNotFound {

        return new ChangeRateReq(changeRateRepo.findById(new ChangeRatePK(date,currencyFrom,currencyTo))
                .orElseThrow(() -> new ResourceNotFound(
                        "No change rate on this date "+date +" between those currencies : "+currencyFrom+" : "+currencyTo
                )));
    }

    /**
     * Gets the history of the change rate between two currencies.
     *
     * @param currencyFrom CurrencyId we want to change
     * @param currencyTo CurrencyId we want to get
     * @return A list of req body of the ChangeRate
     * @throws ResourceNotFound If one (or both) currencies doesn't exist.
     */
    public List<ChangeRateReq> getChangeRateHistory(Integer currencyFrom, Integer currencyTo)
        throws ResourceNotFound {

        CurrencyType currencyTypeFrom = currencyTypeRepo.findById(currencyFrom)
                .orElseThrow(() -> new ResourceNotFound("No currency with this id "+currencyFrom));
        CurrencyType currencyTypeTo = currencyTypeRepo.findById(currencyTo)
                .orElseThrow(() -> new ResourceNotFound("No currency with this id "+currencyTo));

        return changeRateRepo.findAllByCurrency(currencyTypeFrom,currencyTypeTo)
                .stream()
                .map(ChangeRateReq::new)
                .collect(Collectors.toList());
    }
}
