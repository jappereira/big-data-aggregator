import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MainUTest {

    @Test
    public void testCalculateTotalTransactionsAmounts() throws IOException {
        Main.main(new String[]{"input/exchangerates.csv", "input/transactions.csv", "Unlimited " +
                "ltd.", "GBP"});

        Path path = Paths.get("aggregated_transactions_by_partner.csv");

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                String[] strings = line.split(",");
                switch (strings[0]) {
                    case "Defence ltd.":
                        assertEquals("234.11941214805", strings[1]);
                        break;
                    case "Local plumber ltd.":
                        assertEquals("134.1500760000", strings[1]);
                        break;
                    case "Unlimited ltd.":
                        assertEquals("310.70600322070", strings[1]);
                        break;
                    default:
                        fail();
                }
            });
        }
    }

}