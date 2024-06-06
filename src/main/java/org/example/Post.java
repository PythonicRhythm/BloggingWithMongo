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
    private String authorID;

    public Post(ObjectId postId, String title, String body, ArrayList<String> tags,
                String authorName, String authorID, ArrayList<Comment> comments) {
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

    public String getAuthorID() {
        return authorID;
    }

    public void setAuthorID(String authorID) {
        this.authorID = authorID;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment com) {
        comments.add(com);
    }

    public void displayComments() {
        System.out.println("COMMENTS:");
        for(Comment com: comments) {
            System.out.format("%s%n\t%s%n", com.getCommenterName(), com.getComment());
        }
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
        System.out.format("%n%s %n%n%s %n%nWritten by: %s%n%nTags: %s%n%n",
                title, sb, authorName, tags.toString());
        displayComments();
        System.out.println("-------------------------");
    }
}
