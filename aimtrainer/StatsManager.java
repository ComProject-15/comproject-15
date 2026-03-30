package aimtrainer;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatsManager {

    public static class Record {
        public String date;
        public String mode;
        public int slimesKilled;
        public long clearTimeSeconds;

        public Record(String mode, int slimesKilled, long clearTimeSeconds) {
            this.mode = mode;
            this.slimesKilled = slimesKilled;
            this.clearTimeSeconds = clearTimeSeconds;
            this.date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        // สำหรับโหลดจากไฟล์
        public Record(String date, String mode, int slimesKilled, long clearTimeSeconds) {
            this.date = date;
            this.mode = mode;
            this.slimesKilled = slimesKilled;
            this.clearTimeSeconds = clearTimeSeconds;
        }

        @Override
        public String toString() {
            return date + "|" + mode + "|" + slimesKilled + "|" + clearTimeSeconds;
        }
    }

    private static final String FILE_PATH = "slime_stats.txt";

    public static void saveRecord(Record record) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH, true))) {
            pw.println(record.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Record> loadRecords() {
        List<Record> list = new ArrayList<>();
        File f = new File(FILE_PATH);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 4) {
                    list.add(new Record(
                        parts[0], parts[1],
                        Integer.parseInt(parts[2]),
                        Long.parseLong(parts[3])
                    ));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}