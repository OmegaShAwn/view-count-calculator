import java.util.ArrayList;

public class JsonMap {
    private ArrayList<TvSpot> tvSpots;
    private ArrayList<NewUser> newUsers;

    public JsonMap() {}

    public ArrayList<TvSpot> getTvSpots() {
        return tvSpots;
    }

    public void setTvSpots(ArrayList<TvSpot> tvSpots) {
        this.tvSpots = tvSpots;
    }

    public ArrayList<NewUser> getNewUsers() {
        return newUsers;
    }

    public void setNewUsers(ArrayList<NewUser> newUsers) {
        this.newUsers = newUsers;
    }
}

class TvSpot {
    private String time;
    private String spotId;

    TvSpot() {}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }
}

class NewUser {
    private String time;
    private Integer userId;

    NewUser() {}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}