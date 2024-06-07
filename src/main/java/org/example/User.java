package org.example;

import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * The User class represents a user that interacts with the
 * program. The class contains the id of the user on Mongo,
 * the name of the user, and an array of ids that represent
 * the posts that the user has made.
 */

public class User {
    private final ObjectId userID;          // The ID of the user.
    private String name;                    // The name of the user.
    private ArrayList<ObjectId> postIDs;    // An array of ids that represent the posts the user has made.

    public User(ObjectId userID, String name, ArrayList<ObjectId> postIDs) {
        this.userID = userID;
        this.name = name;
        this.postIDs = postIDs;
    }

    // Return ID of user.
    public ObjectId getUserID() {
        return userID;
    }

    // Return the name of user.
    public String getName() {
        return name;
    }

    // Insert a new ID into the array of postIDs
    public void insertPostID(ObjectId newPostID) {
        if(newPostID.toString().isEmpty()) return;
        postIDs.add(newPostID);
    }
}
