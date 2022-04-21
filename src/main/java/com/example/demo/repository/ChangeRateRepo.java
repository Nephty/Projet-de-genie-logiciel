package com.example.demo.repository;

import com.example.demo.model.ChangeRate;
import com.example.demo.model.CompositePK.ChangeRatePK;
import com.example.demo.model.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Date;
import java.util.List;

/**
 * All the DB request on the {@link ChangeRate} table.
 */
@Repository
@Transactional
public interface ChangeRateRepo extends JpaRepository<ChangeRate, ChangeRatePK> {

    /**
     * Find all {@link ChangeRate} between two {@link CurrencyType currencies}.
     *
     * @param currencyTypeFrom Currency we change
     * @param currencyTypeTo Currency we get
     * @return A list of ChangeRate for all the changeRate between the two currencies.
     */
    @Query("SELECT c FROM ChangeRate c WHERE c.currencyTypeFrom = ?1 AND c.currencyTypeTo = ?2")
    List<ChangeRate> findAllByCurrency(CurrencyType currencyTypeFrom, CurrencyType currencyTypeTo);

    /**
     * Gets the last date that the api fetched all the changeRates.
     *
     * @return The date of the last fetch.
     */
    @Query("SELECT case " +
            "when max(c.changeRatePK.date) is null then '1970-1-1' " +
            "else max(c.changeRatePK.date) end " +
            "FROM ChangeRate c")
    Date findLastFetch();
}
