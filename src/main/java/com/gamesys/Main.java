package com.gamesys;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static String EXCHANGERATES_FILENAME;
    private static String TRANSACTIONS_FILENAME;
    private static String PARTNER;
    private static String CURRENCY;
    private static Map<String, BigDecimal> EXCHANGERATES;

    public static void main(String[] args) throws IOException {
        EXCHANGERATES_FILENAME = args[0];
        TRANSACTIONS_FILENAME = args[1];
        PARTNER = args[2];
        CURRENCY = args[3];

        EXCHANGERATES = loadExchangeRates();
    }

    private static Map<String, BigDecimal> loadExchangeRates() throws IOException {
        String filter = "," + CURRENCY;
        Path path = Paths.get(EXCHANGERATES_FILENAME);
        Map<String, BigDecimal> EXCHANGERATES;

        try (Stream<String> filteredLines = Files.lines(path).filter(l -> l.startsWith(filter, 3))) {
            EXCHANGERATES
                    = filteredLines
                    .collect(Collectors.toMap(e -> e.substring(0, 3), e -> new BigDecimal(e.substring(8))));
        }

        return EXCHANGERATES;
    }
}
