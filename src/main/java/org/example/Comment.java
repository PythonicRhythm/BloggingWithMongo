package org.example;

import org.bson.types.ObjectId;

/**
 * The comment class represents a comment that a user has
 * made on a post. A comment keeps track of the user who
 * commented, the name of the user, and the comment body
 * itself.
 */

public class Comment {

    private final ObjectId commenterID;     // ID of the user who made the comment
    private final String commenterName;     // The name of the user who made the comment.
    private String comment;                 // The content of the comment itself.

    public Comment(ObjectId commenterID, String commenterName, String comment) {
        this.commenterID = commenterID;
        this.commenterName = commenterName;
        this.comment = comment;
    }

    // Get the comment body.
    public String getComment() {
        return comment;
    }

    // Get the name of the user who made the comment.
    public String getCommenterName() {
        return commenterName;
    }

    // Get the id of the user who made the comment.
    public ObjectId getCommenterID() {
        return commenterID;
    }

}
