package org.example;

import org.bson.types.ObjectId;

import java.util.ArrayList;

/**
 * The Post class represents a blog post that can be interacted with by
 * users. A post class will display a simple console UI when displayPost()
 * is called. This class is mostly used for a UI representation of a blog
 * post.
 */

public class Post {
    private final ObjectId postId;          // The ID of the post on Mongo.
    private String title;                   // The title of the post.
    private String body;                    // The body content of the post itself.
    private ArrayList<String> tags;         // An array of tags that the post has.
    private ArrayList<Comment> comments;    // An array of comments that the post has.
    private String authorName;              // The name of the user who made the post.
    private ObjectId authorID;              // The id of the user who made the post.

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

    // Get postID
    public ObjectId getPostId() {
        return postId;
    }

    // Get the authorID
    public ObjectId getAuthorID() {
        return authorID;
    }

    // displayComments() will display all the comments
    // the posts has by printing the commenter name then the
    // comment body.
    public void displayComments() {
        if(comments.isEmpty()) return;

        System.out.println("\nCOMMENTS:");
        for(Comment com: comments) {
            System.out.format("%s%n\t- %s%n", com.getCommenterName(), com.getComment());
        }
    }

    // displayTags() will display all the tags
    // dedicated to the post.
    public void displayTags() {
        if(tags.isEmpty()) return;

        System.out.println();
        for(int i = 0; i < tags.size()-1; i++) {
            System.out.format("%s,", tags.get(i));
        }
        System.out.println(tags.get(tags.size()-1));
    }

    // displayPost() will display the post and everything
    // that contains a post such as title, body, tags,
    // and comments. Displays the post to the console.
    public void displayPost() {

        // Appends a new line on every 6th word.
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

        // Print the post UI.
        System.out.println("\nPOST:");
        System.out.println("-------------------------");
        System.out.format("%s %n%n%s %n%nWritten by: %s%n",
                title, sb, authorName);
        displayTags();
        displayComments();
        System.out.println("-------------------------");
    }
}
