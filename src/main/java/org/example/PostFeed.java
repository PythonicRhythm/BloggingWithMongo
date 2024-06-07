package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

/**
 * The PostFeed class represents all the posts that exist in the program.
 * The user can interact with the post feed by viewing all posts like a blog
 * feed and accessing all the posts that were created by the specific user.
 * Users can interact with every post on the feed by commenting on the posts.
 * They can also contribute to the feed by deleting and adding posts.
 */

public class PostFeed {
    private ArrayList<Post> posts;  // All posts on the feed.

    public PostFeed(MongoCollection<Document> postDocuments) {
        initializePosts(postDocuments);
    }

    // initializePosts() will create the arraylist of posts
    // that is interacted with in the postFeed. Attempts to
    // gather all the posts saved in the database and inserts
    // it into an arraylist to keep track of all posts.
    public void initializePosts(MongoCollection<Document> postDocuments) {

        // READ (cRud)

        // Gather all posts.
        posts = new ArrayList<>();
        for(Document d: postDocuments.find()) {
            ArrayList<String> allTags;
            if(d.get("tags") == null) allTags = new ArrayList<>();
            else allTags = (ArrayList<String>) d.get("tags");


            // Gather all Comments dedicated to the post.
            List<Document> allComments = new ArrayList<>();
            ArrayList<Comment>  commentList = new ArrayList<>();
            if(d.getList("comments", Document.class) != null) {
                allComments = d.getList("comments", Document.class);
                for(Document doc: allComments) {
                    commentList.add(new Comment(
                            (ObjectId) doc.get("commenterID"), (String) doc.get("commenterName"),
                            (String) doc.get("comment") ));
                }
            }

            // Create post and insert it into the arraylist.
            posts.add(new Post((ObjectId) d.get("_id"), (String) d.get("title"), (String) d.get("body"),
                    allTags, (String) d.get("authorName"), (ObjectId) d.get("authorID"), commentList));
        }

    }

    // Return all posts.
    public ArrayList<Post> getPosts() {
        return posts;
    }

    // getUserPosts() will gather all the posts dedicated to
    // the user that is represented by the userID and return them.
    public ArrayList<Post> getUserPosts(ObjectId userID) {

        // For every post, if the authorID
        // matches the userID, add them to
        // the userPosts array and return it
        // after completion.
        ArrayList<Post> userPosts = new ArrayList<>();
        for(Post p: posts) {
            if(p.getAuthorID().equals(userID))
                userPosts.add(p);
        }

        return userPosts;
    }
}
