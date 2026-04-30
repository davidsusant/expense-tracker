package io.davidsusanto.expensetracker.writer;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

import io.davidsusanto.expensetracker.model.Transaction;

/**
 * Appends transactions to a Google Sheet via the Sheets API v4 using a service-account key.
 * <p>
 * Sheet must be shared with the service-account email.
 * Columns: {@code Date | Description | Amount | Currency | Category | Source}.
 * </p>
 */
public class GoogleSheetsWriter implements TransactionWriter {

    private static final Logger log = LoggerFactory.getLogger(GoogleSheetsWriter.class);

    private static final String APP_NAME = "expense-tracker";
    private static final List<String> SCOPES = List.of(SheetsScopes.SPREADSHEETS);

    private final String spreadsheetId;
    private final String sheetName;
    private final Sheets sheetsService;
    
    public GoogleSheetsWriter(Path credentialsJson, String spreadsheetId, String sheetName) throws Exception {
        this.spreadsheetId = Objects.requireNonNull(spreadsheetId, "spreadsheetId");
        this.sheetName = Objects.requireNonNull(sheetName, "sheetName");
        this.sheetsService = buildService(Objects.requireNonNull(credentialsJson, "credentialsJson"));
    }

    // --- Private methods ---

    private static Sheets buildService(Path credentialsJson) throws Exception {
        // try-with-resources ensures the JSON key is always closed
        try (InputStream in = Files.newInputStream(credentialsJson)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(in).createScoped(SCOPES);

            return new Sheets.Builder(
                new NetHttpTransport(), 
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APP_NAME)
                .build();
        }
    }

    @Override
    public void write(List<Transaction> transactions) throws Exception {
        if (transactions == null || transactions.isEmpty()) {
            log.info("No transactions to write -- skipping");
            return;
        }

        List<List<Object>> rows = transactions.stream()
                                    .map(GoogleSheetsWriter::toRow)
                                    .toList();

        // A:F matches 6-column layout
        // Sheets will pick the next empty row
        String range = sheetName + "!A:F";
        ValueRange body = new ValueRange().setValues(rows);

        AppendValuesResponse response = sheetsService.spreadsheets().values()
                                            .append(spreadsheetId, range, body)
                                            .setValueInputOption("USER_ENTERED")
                                            .setInsertDataOption("INSERT_ROWS")
                                            .execute();

        String updated = (response.getUpdates() != null)
                            ? response.getUpdates().getUpdatedRange()
                            : "<unknown>";

        log.info("Appended {} row(s) to {}", rows.size(), updated);
    }

    /**
     * Maps a Transaction to a single Sheets row.
     * Order must match the A:F contract above.
     */
    private static List<Object> toRow(Transaction t) {
        return Arrays.asList(
            t.date().toString(),
            t.description(),
            t.amount().toPlainString(),
            t.currency(),
            t.category().name(),
            t.sourceAccount()
        );
    }
}
