package com.expense.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expense.models.Transaction;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.AppendValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

public class GoogleSheetsService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    private Sheets sheetsService;
    private String spreadsheetId;
    
    public GoogleSheetsService() throws FileNotFoundException, IOException, GeneralSecurityException {
        this.sheetsService = getSheetsService();
        this.spreadsheetId = ConfigReader.getSpreadsheetId();

        logger.info("GoogleSheetsService initialize for spreadsheet: {}", spreadsheetId);
    }

    private Sheets getSheetsService() throws FileNotFoundException, IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new FileInputStream(ConfigReader.getGoogleCredentialsPath()))
            .createScoped(SCOPES);

        return new Sheets.Builder(httpTransport, JSON_FACTORY, new HttpCredentialsAdapter(credentials))
            .setApplicationName(ConfigReader.getApplicationName())
            .build();
    }

    public void writeHeader(String sheetName) throws IOException {
        List<List<Object>> values = new ArrayList<>();
        List<Object> header = new ArrayList<>();
        header.add("Date");
        header.add("Description");
        header.add("Amount");
        header.add("Category");
        values.add(header);

        String range = sheetName + "!A1:D1";
        ValueRange body = new ValueRange().setValues(values);

        sheetsService.spreadsheets().values()
            .update(spreadsheetId, range, body)
            .setValueInputOption("RAW")
            .execute();

        logger.info("Header row written to Google Sheets");
    }

    public void appendTransactions(List<Transaction> transactions, String sheetName) throws IOException {
        if (transactions == null || transactions.isEmpty()) {
            logger.warn("No transactions to append");
            return;
        }

        List<List<Object>> values = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy");

        for (Transaction transaction : transactions) {
            List<Object> row = new ArrayList<>();
            row.add(transaction.getDate().format(dateFormatter));
            row.add(transaction.getDescription());
            row.add(transaction.getAmount());
            row.add(transaction.getCategory());
            values.add(row);
        }

        String range = sheetName + "!A:D";
        ValueRange body = new ValueRange().setValues(values);

        AppendValuesResponse result = sheetsService.spreadsheets().values()
            .append(spreadsheetId, range, body)
            .setValueInputOption("RAW")
            .setInsertDataOption("INSERT_ROWS")
            .execute();

        logger.info("Successfully appended {} rows to Google Sheets", result.getUpdates().getUpdatedRows());
    }

    public void clearSheet(String sheetName) throws IOException {
        String range = sheetName + "!A:D";
        sheetsService.spreadsheets().values()
            .clear(spreadsheetId, range, null)
            .execute();

        logger.info("Sheet cleared successfully");
    }
}
