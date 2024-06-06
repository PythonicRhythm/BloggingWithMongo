package org.example;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class User {
    private final ObjectId userID;
    private String name;
    private ArrayList<ObjectId> postIDs;

    public User(ObjectId userID, String name, ArrayList<ObjectId> postIDs) {
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

    public ArrayList<ObjectId> getPostIDs() {
        return postIDs;
    }

    public void insertPostID(ObjectId newPostID) {
        if(newPostID.toString().isEmpty()) return;
        postIDs.add(newPostID);
    }
}
