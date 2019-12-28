import java.io.*;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

class TvSpot {
    String id;
    Integer count;

    TvSpot(String id, Integer count) {
        this.id = id;
        this.count = count;
    }
}

public class TvSpotViewerCalculator {
    private static LinkedHashMap<LocalDateTime, Integer> users = new LinkedHashMap<>();
    private static LinkedHashMap<LocalDateTime, TvSpot> viewCountByTvSpot = new LinkedHashMap<>();

    private static final String TIME = "\"time\"";
    private static final String NEW_USER = "\"newUsers\"";

    private static void recordTvSpots(String timeLine, String idLine) {
        String dateInString = timeLine.split("\"")[3];
        String tvSpotId = idLine.split(":")[1];

        LocalDateTime date = LocalDateTime.parse(dateInString);

        TvSpot tvSpot = new TvSpot(tvSpotId, 0);
        viewCountByTvSpot.put(date, tvSpot);
    }

    private static void recordUsers(String line) {
        line = line.split("\"")[3];
        String dateInString = line.substring(0, line.length() - 3); // removes seconds.

        LocalDateTime date = LocalDateTime.parse(dateInString);

        Integer count = users.get(date);
        if (count != null) users.put(date, ++count);
        else users.put(date, 1);
    }

    private static void computeViewCount() {
        viewCountByTvSpot.forEach((key, value) -> {
            Integer averageUsers = 0;
            Integer newUsers = 0;

            for (int i = 1; i <= 10; i++) {
                // calculates sum of users from the past 10 minutes
                Integer userCount = users.get(key.minusMinutes(i));
                if (userCount != null) averageUsers +=userCount;

                // calculates sum of viewers from the next 10 minutes;
                userCount = users.get(key.plusMinutes(i - 1));
                if (userCount != null) newUsers += userCount;
            }

            value.count = newUsers - averageUsers;
            viewCountByTvSpot.put(key, value);
        });
    }

    private static void printViewCount() {
        viewCountByTvSpot.forEach((key, value) -> System.out.println("Spot" + value.id + ": " + value.count + " new users"));
    }

    public static void main(String[] args) throws IOException {
        // used to switch to recording users
        boolean newUserSwitch = false;

        // Reads the file from resources
        InputStream inputStream = TvSpotViewerCalculator.class
                .getClassLoader().getResourceAsStream("new_users_large_set.json");
        assert inputStream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        // records the values into the respective hashmaps
        String line = reader.readLine();
        while (line != null) {
            line = line.strip();

            // switches to record users;
            if (line.startsWith(NEW_USER)) newUserSwitch = true;

            if (line.startsWith(TIME))
                if (!newUserSwitch) recordTvSpots(line, reader.readLine().strip());
                else recordUsers(line);

            line = reader.readLine();
        }

        // computes and prints the view counts
        computeViewCount();
        printViewCount();
    }
}
