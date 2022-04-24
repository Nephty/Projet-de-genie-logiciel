package com.example.demo.controller;

import com.example.demo.request.ChangeRateReq;
import com.example.demo.service.ChangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;

@RequestMapping(path = "/api/change-rate")
@RequiredArgsConstructor
@RestController
@Slf4j
public class ChangeRateController {
    private final ChangeRateService changeRateService;


    /**
     * Returns the change rate from one currency to another on a certain date.
     *
     * <br>Http codes:
     * <ul>
     *     <li>200 - OK</li>
     *     <li>400 - Bad request</li>
     *     <li>404 - Not Found</li>
     * </ul>
     *
     * @param date         The date of the change Rate
     * @param currencyFrom currencyId we want to change
     * @param currencyTo   currencyId we want to get
     * @return change rate matching params
     */
    @GetMapping
    public ResponseEntity<ChangeRateReq> sendChangeRate(
            @RequestParam Date date,
            @RequestParam Integer currencyFrom, @RequestParam Integer currencyTo) {
        return new ResponseEntity<>(
                changeRateService.getChangeRate(date, currencyFrom, currencyTo),
                HttpStatus.OK
        );
    }

    /**
     * Returns the history of the change rate between the two currencies.
     *
     * <br>Http codes:
     * <ul>
     *     <li>200 - OK</li>
     *     <li>400 - Bad request</li>
     *     <li>404 - Not Found</li>
     * </ul>
     *
     * @param currencyFrom currencyId we want to change
     * @param currencyTo   currencyId we want to get
     * @return A list of the history of change rate matching param
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChangeRateReq>> sendHistory(
            @RequestParam Integer currencyFrom, @RequestParam Integer currencyTo) {
        return new ResponseEntity<>(
                changeRateService.getChangeRateHistory(currencyFrom, currencyTo),
                HttpStatus.OK
        );
    }

}
