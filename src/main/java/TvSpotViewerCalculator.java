import java.io.*;
import java.time.LocalDateTime;
import java.util.HashMap;

public class TvSpotViewerCalculator {
    private static HashMap<LocalDateTime, String> tvSpots = new HashMap<>();
    private static HashMap<LocalDateTime, Integer> users = new HashMap<>();
    private static HashMap<LocalDateTime, Integer> viewCountByTvSpot = new HashMap<>();

    private static final String TIME = "\"time\"";

    private static void recordTvSpots(String timeLine, String idLine) {
        String dateInString = timeLine.split("\"")[3];
        String tvSpotId = idLine.split(":")[1];

        LocalDateTime date = LocalDateTime.parse(dateInString);

        tvSpots.put(date, tvSpotId);
        viewCountByTvSpot.put(date, 0);
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

            for (int i = 0; i < 10; i++) {
                // calculates sum of users from the past 10 minutes
                Integer userCount = users.get(key.minusMinutes(i));
                if (userCount != null) averageUsers +=userCount;

                // calculates sum of viewers from the next 10 minutes;
                userCount = users.get(key.plusMinutes(i));
                if (userCount != null) newUsers += userCount;
            }


            viewCountByTvSpot.put(key, newUsers - averageUsers);
        });
    }

    private static void printViewCount() {
        viewCountByTvSpot.forEach((key, value) -> System.out.println("Spot" + tvSpots.get(key) + ": " + value + " new users"));
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
            if (line.startsWith("\"newUsers\"")) newUserSwitch = true;

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
