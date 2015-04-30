package com.gamesys;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static String EXCHANGE_RATES_FILENAME;
    private static String TRANSACTIONS_FILENAME;
    private static String PARTNER;
    private static String CURRENCY;
    private static Map<String, BigDecimal> EXCHANGE_RATES;
    private static Map<String, BigDecimal> TOTAL_TRANSACTIONS_AMOUNTS = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        EXCHANGE_RATES_FILENAME = args[0];
        TRANSACTIONS_FILENAME = args[1];
        PARTNER = args[2];
        CURRENCY = args[3];

        EXCHANGE_RATES = loadExchangeRates();

        calculateTotalTransactionsAmounts();

        // write TOTAL_TRANSACTIONS_AMOUNTS to file

        System.out.println(TOTAL_TRANSACTIONS_AMOUNTS.get(PARTNER));
    }

    private static Map<String, BigDecimal> loadExchangeRates() throws IOException {
        String filter = "," + CURRENCY;
        Path path = Paths.get(EXCHANGE_RATES_FILENAME);
        Map<String, BigDecimal> EXCHANGE_RATES;

        try (Stream<String> filteredLines = Files.lines(path).filter(line -> line.startsWith
                (filter, 3))) {
            EXCHANGE_RATES
                    = filteredLines
                    .collect(Collectors.toMap(exchangeRate -> exchangeRate.substring(0, 3),
                                              exchangeRate -> new BigDecimal(exchangeRate.substring(8))));
        }

        return EXCHANGE_RATES;
    }

    private static void calculateTotalTransactionsAmounts() throws IOException {
        Path path = Paths.get(TRANSACTIONS_FILENAME);

        try (Stream<String> transactions = Files.lines(path).parallel()) {
            transactions.forEach(transaction -> {
                String[] strings = transaction.split(",");
                if (strings[1].equals(CURRENCY)) {
                    TOTAL_TRANSACTIONS_AMOUNTS.compute(strings[0], (k, currentAmount) -> currentAmount == null ?
                                                                                         new BigDecimal(strings[2])
                                                                                                               : currentAmount.add(new BigDecimal(strings[2])));
                } else {
                    TOTAL_TRANSACTIONS_AMOUNTS
                            .compute(strings[0], (k, currentAmount) -> currentAmount == null ?
                                                                       new BigDecimal(strings[2]).multiply(EXCHANGE_RATES.get(strings[1]))
                                                                                             : currentAmount.add(new BigDecimal(strings[2])
                                                                                                                         .multiply(
                                                                                                                                 EXCHANGE_RATES
                                                                                                                                         .get(strings[1]))));
                }
            });
        }
    }

}
