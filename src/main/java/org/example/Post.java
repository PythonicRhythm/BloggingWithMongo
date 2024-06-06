package org.example;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Post {
    private final ObjectId postId;
    private String title;
    private String body;
    private ArrayList<String> tags;
    private ArrayList<Comment> comments;
    private String authorName;
    private ObjectId authorID;

    public Post(ObjectId postId, String title, String body, ArrayList<String> tags,
                String authorName, ObjectId authorID, ArrayList<Comment> comments) {
        this.postId = postId;
        this.title = title;
        this.body = body;
        this.tags = tags;
        this.authorName = authorName;
        this.authorID = authorID;
        this.comments = comments;
    }

    public ObjectId getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String author) {
        this.authorName = author;
    }

    public ObjectId getAuthorID() {
        return authorID;
    }

    public void setAuthorID(ObjectId authorID) {
        this.authorID = authorID;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment com) {
        comments.add(com);
    }

    public void displayComments() {
        if(comments.isEmpty()) return;

        System.out.println("\nCOMMENTS:");
        for(Comment com: comments) {
            System.out.format("%s%n\t- %s%n", com.getCommenterName(), com.getComment());
        }
    }

    public void displayTags() {
        if(tags.isEmpty()) return;

        System.out.println();
        for(int i = 0; i < tags.size()-1; i++) {
            System.out.format("%s,", tags.get(i));
        }
        System.out.println(tags.get(tags.size()-1));
    }

    public void displayPost() {

        String[] splitBySpace = body.split(" ");
        StringBuffer sb = new StringBuffer();
        int wordCounter = 0;
        for(int i = 0; i < splitBySpace.length; i++) {
            if(wordCounter == 6) {
                sb.append(splitBySpace[i]);
                sb.append("\n");
                wordCounter = 0;
            }
            else {
                sb.append(splitBySpace[i]);
                sb.append(" ");
                wordCounter++;
            }

        }

        System.out.println("\nPOST:");
        System.out.println("-------------------------");
        System.out.format("%s %n%n%s %n%nWritten by: %s%n",
                title, sb, authorName);
        displayTags();
        displayComments();
        System.out.println("-------------------------");
    }
}
