import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ReadCSVWithHeaders {
    private Workout workout;

    public ReadCSVWithHeaders(String filename) {
        workout = new Workout();

        String filePath = filename;
        String[] expectedHeaders = {"directCorrectedElevation", "directDoubleCadence", "directTimestamp", "directLatitude", "sumDistance",
                "directRunCadence", "sumElapsedDuration", "directLongitude", "sumMovingDuration", "directElevation",
                "directSpeed", "sumDuration", "directHeartRate", "directVerticalSpeed"};
        Map<String, Integer> headerIndexMap = new HashMap<>();

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] headers = reader.readNext();// Read the first row (header)

            // Map header names to their indices
            for (int i = 0; i < headers.length; i++) {
                headerIndexMap.put(headers[i], i);
            }

            String[] line;
            while ((line = reader.readNext()) != null) {
                Split split = new Split();
                int count = -1;
                for (String header : expectedHeaders) {
                    if (headerIndexMap.containsKey(header)) {
                        count++;
                        int index = headerIndexMap.get(header);
                        switch (count) {
                            case 0: split.setAltitude(converter(line[index]));
                            case 1: split.setCadence(converter(line[index]));
                            case 3: split.setLatitude(converter(line[index]));
                            case 4: split.setDistance(converter(line[index]));
                            case 7: split.setLongitude(converter(line[index]));
                            case 10: split.setSpeed(3.6 * converter(line[index]));
                            case 11: split.setSumDuration(converter(line[index]));
                            case 12: split.setHeartRate(converter(line[index]));
                        }
                    }
                }
                // 3.6 is the conversion factor for meters to kilometers
                //Split split = new Split(converter(line[7]), converter(line[3]), converter(line[0]), converter(line[4]), 3.6 * converter(line[10]), converter(line[1]), converter(line[12]));
                workout.addSplit(split);
            }
        } catch (IOException | com.opencsv.exceptions.CsvValidationException e) {
            e.printStackTrace();
        }
    }

    public Workout getWorkout()
    {
        return workout;
    }

    public double converter(String str)
    {
        if (str.equals("")) return 0;
        return Double.parseDouble(str);
    }
}
