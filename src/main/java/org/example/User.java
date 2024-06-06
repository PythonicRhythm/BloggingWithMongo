package org.example;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class User {
    private final ObjectId userID;
    private String name;
    private ArrayList<String> postIDs;

    public User(ObjectId userID, String name, ArrayList<String> postIDs) {
        this.userID = userID;
        this.name = name;
        this.postIDs = postIDs;
    }

    public ObjectId getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getPostIDs() {
        return postIDs;
    }

    public void insertPostID(String newPostID) {
        if(newPostID.isEmpty()) return;
        postIDs.add(newPostID);
    }
}
