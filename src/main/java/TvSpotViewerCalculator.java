import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

class TvSpotCount {
    String id;
    Integer count;

    TvSpotCount(String id, Integer count) {
        this.id = id;
        this.count = count;
    }
}

public class TvSpotViewerCalculator {
    private static LinkedHashMap<LocalDateTime, Integer> usersMap = new LinkedHashMap<>();
    private static LinkedHashMap<LocalDateTime, TvSpotCount> viewCountMap = new LinkedHashMap<>();

    private static void recordTvSpots(ArrayList<TvSpot> tvSpots) {
        tvSpots.forEach((tvSpot -> {
            LocalDateTime time = LocalDateTime.parse(tvSpot.getTime());
            TvSpotCount tvSpotCount = new TvSpotCount(tvSpot.getSpotId(), 0);

            viewCountMap.put(time, tvSpotCount);
        }));
    }

    private static void recordUsers(ArrayList<NewUser> newUsers) {
        newUsers.forEach((newUser -> {
            LocalDateTime time = LocalDateTime.parse(newUser.getTime()).withSecond(0);
            Integer count = usersMap.get(time);

            if (count != null) usersMap.put(time, ++count);
            else usersMap.put(time, 1);
        }));
    }

    private static void computeViewCount() {
        viewCountMap.forEach((key, value) -> {
            Integer averageUsers = 0;
            Integer newUsers = 0;

            for (int i = 1; i <= 10; i++) {
                // calculates sum of users from the past 10 minutes
                Integer userCount = usersMap.get(key.minusMinutes(i));
                if (userCount != null) averageUsers += userCount;

                // calculates sum of viewers from the next 10 minutes;
                userCount = usersMap.get(key.plusMinutes(i - 1));
                if (userCount != null) newUsers += userCount;
            }

            value.count = newUsers - averageUsers;
            viewCountMap.put(key, value);
        });
    }

    private static void printViewCount() {
        viewCountMap.forEach((key, value) -> System.out.println("Spot " + value.id + ": " + value.count + " new users"));
    }

    public static void main(String[] args) throws IOException {

        // Reads the file from resources
        InputStream inputStream = TvSpotViewerCalculator.class
                .getClassLoader().getResourceAsStream("new_users.json");
        assert inputStream != null;

        // Record json to JsonMap object
        JsonMap jsonMap = new ObjectMapper().readValue(inputStream, JsonMap.class);

        // records the values into the respective hashmaps
        recordUsers(jsonMap.getNewUsers());
        recordTvSpots(jsonMap.getTvSpots());

        // computes and prints the view counts
        computeViewCount();
        printViewCount();
    }
}
