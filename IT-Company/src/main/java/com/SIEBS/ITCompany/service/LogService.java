package com.SIEBS.ITCompany.service;

import com.SIEBS.ITCompany.model.Logs;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LogService {

    private static final String LOG_FILE_NAME = "logs/app-logback.log";

    public ArrayList<Logs> getLast24HoursLogs() {
        ArrayList<Logs> logs = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime twentyFourHoursAgo = now.minusHours(24);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. HH:mm:ss");


        File logFile = new File(LOG_FILE_NAME);
        if (logFile.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(logFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|", 4);

                    String logTimestampStr = parts[0].trim();
                    LocalDateTime logTimestamp = LocalDateTime.parse(logTimestampStr, formatter);

                    if (logTimestamp.isAfter(twentyFourHoursAgo) && logTimestamp.isBefore(now)) {
                        Date date = convertToDate(logTimestamp);
                        String type = parts[1].trim();
                        String component = parts[2].trim();
                        String message = parts[3].trim();

                        Logs log = new Logs(date, type, component, message);
                        logs.add(log);
                    }
                }
            } catch (IOException e) {
                // Handle exception
            }
        }

        return logs;
    }


    private void readLogsFromFile(File file, List<Logs> logs, LocalDateTime startTime, LocalDateTime endTime, DateTimeFormatter formatter) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 4);

                String logTimestampStr = parts[0].trim();
                LocalDateTime logTimestamp = LocalDateTime.parse(logTimestampStr, formatter);

                if (logTimestamp.isAfter(startTime) && logTimestamp.isBefore(endTime)) {
                    Date date = convertToDate(logTimestamp);
                    String type = parts[1].trim();
                    String component = parts[2].trim();
                    String message = parts[3].trim();

                    Logs log = new Logs(date, type, component, message);
                    logs.add(log);
                }
            }
        } catch (IOException e) {
            // Handle exception
        }
    }

    private Date convertToDate(LocalDateTime dateTime) {
        return java.sql.Timestamp.valueOf(dateTime);
    }
}
