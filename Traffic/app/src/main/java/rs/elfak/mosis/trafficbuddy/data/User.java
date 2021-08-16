package rs.elfak.mosis.trafficbuddy.data;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String uid;
    private String username;
    private String password; // password stored for testing purposes
    private String email;
    private String name;
    private String lastName;
    private String phone;

    private String imageUrl;
    private String rank;
    private int rankPoints; // 0-2 newbie; 3-5 party guy; 6+ party monster
    private double lat;
    private double lon;
    private List<String> friends;

    public User() {
        friends = new ArrayList<String>(1);
        updateRankBasedOnCurrentPoints();
    }

    public int getRankPoints() {
        return rankPoints;
    }

    public void setRankPoints(int rankPoints) {
        this.rankPoints = rankPoints;
    }

    public void setLastName(String lN) {
        this.lastName = lN;
    }
    public String getLastName(){return this.lastName;}
    public String getPhoneNumber(){
       return this.phone;
    }
    public void setPhoneNumber(String lN) {
        this.phone = lN;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public void increaseRankPoints() {
        rankPoints++;
        updateRankBasedOnCurrentPoints();
    }

    private void updateRankBasedOnCurrentPoints() {
        if (rankPoints < 3)
            rank = "Newbie";
        else if (rankPoints < 6)
            rank = "Party guy";
        else
            rank = "Party monster";
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public ArrayList<String> getFriends() {
        return (ArrayList<String>) friends;
    }

    public void setFriends (ArrayList<String> friends) {
        this.friends.clear();
        this.friends = friends;
    }

    public void setNewFriend (String newFriend) {
        Integer currentNum;
        if (friends.size() == 0)
             currentNum = 0;
        else
            currentNum = friends.size();
        this.friends.add(currentNum, newFriend);
    }
}


