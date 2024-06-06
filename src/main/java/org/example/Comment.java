package org.example;

import org.bson.types.ObjectId;

public class Comment {

    private final ObjectId commenterID;
    private final String commenterName;
    private String comment;

    public Comment(ObjectId commenterID, String commenterName, String comment) {
        this.commenterID = commenterID;
        this.commenterName = commenterName;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public ObjectId getCommenterID() {
        return commenterID;
    }

}
