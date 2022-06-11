package com.exercise.api.helper;

import com.exercise.api.model.MedicalObservation;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {
    private final static String TYPE = "text/csv";
    private final static String[] HEADER = {"source", "codeListCode", "code", "displayValue", "longDescription", "fromDate", "toDate", "sortingPriority"};
    private final static CSVFormat csvFormat = CSVFormat.Builder.create().setHeader(HEADER).setSkipHeaderRecord(true).build();

    private final static SimpleDateFormat spf = new SimpleDateFormat("dd-MM-yyyy");

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<MedicalObservation> csvToMedicalObservation(InputStream is) {
        List<MedicalObservation> medicalObservationsList = new ArrayList<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(fileReader, csvFormat)) {
            for (CSVRecord csvRecord : csvParser) {
                medicalObservationsList.add(createMedicalObservation(csvRecord));
            }
        } catch (IOException e) {
            throw new RuntimeException("failed to parse CSV file: " + e.getMessage());
        }

        return medicalObservationsList;
    }

     public static ResponseEntity medicalObservationsToCSV(List<MedicalObservation> medicalObservationList) {

        ByteArrayInputStream byteArrayOutputStream;

        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.EXCEL);) {
            csvPrinter.printRecord(HEADER);
            for (MedicalObservation medicalObservation : medicalObservationList) {
                csvPrinter.printRecord(toCSVLine(medicalObservation));
            }

            csvPrinter.flush();

            byteArrayOutputStream = new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        InputStreamResource fileInputStream = new InputStreamResource(byteArrayOutputStream);

        String csvFileName = "medicalObservations.csv";

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + csvFileName);
        headers.set(HttpHeaders.CONTENT_TYPE, "text/csv");

        return new ResponseEntity<>(fileInputStream,headers,HttpStatus.OK);
    }

    private static MedicalObservation createMedicalObservation(CSVRecord attributes) {
        MedicalObservation medicalObservation = new MedicalObservation();
        medicalObservation.setSource(attributes.get("source"));
        medicalObservation.setCodeListCode(attributes.get("codeListCode"));
        medicalObservation.setCode(attributes.get("code"));
        medicalObservation.setDisplayValue(attributes.get("displayValue"));
        medicalObservation.setLongDescription(attributes.get("longDescription"));
        try {
            medicalObservation.setFromDate(spf.parse(attributes.get("fromDate")));
        } catch(Exception ex) {
            medicalObservation.setFromDate(null);
        }
        try {
            medicalObservation.setToDate(spf.parse(attributes.get("toDate")));
        } catch(Exception ex) {
            medicalObservation.setToDate(null);
        }
        medicalObservation.setSortingPriority(attributes.get("sortingPriority"));
        return medicalObservation;
    }

    private static String toCSVLine(MedicalObservation medicalObservation) {
        String CSV_SEPARATOR = ",";
        String quote = "\"";
        StringBuilder oneLine = new StringBuilder();
        oneLine.append(quote);
        oneLine.append(medicalObservation.getSource().trim().length() == 0 ? "" : medicalObservation.getSource());
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getCodeListCode().trim().length() == 0 ? "" : medicalObservation.getCodeListCode());
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getCode().trim().length() == 0 ? "" : medicalObservation.getCode());
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getDisplayValue().trim().length() == 0 ? "" : medicalObservation.getDisplayValue());
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getLongDescription().trim().length() == 0 ? "" : medicalObservation.getLongDescription());
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getFromDate() != null ? spf.format(medicalObservation.getFromDate()) : "");
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getToDate() != null ? spf.format(medicalObservation.getToDate()) : "");
        oneLine.append(quote);
        oneLine.append(CSV_SEPARATOR);
        oneLine.append(quote);
        oneLine.append(medicalObservation.getSortingPriority());
        oneLine.append(quote);
        return oneLine.toString();
    }

}