package org.example;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class PostFeed {
    private ArrayList<Post> posts;

    public PostFeed(MongoCollection<Document> postDocuments) {
        initializePosts(postDocuments);
    }

    public void initializePosts(MongoCollection<Document> postDocuments) {

        // READ (cRud)
        posts = new ArrayList<>();
        for(Document d: postDocuments.find()) {
            ArrayList<String> allTags;
            if(d.get("tags") == null) allTags = new ArrayList<>();
            else allTags = (ArrayList<String>) d.get("tags");

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

            posts.add(new Post((ObjectId) d.get("_id"), (String) d.get("title"), (String) d.get("body"),
                    allTags, (String) d.get("authorName"), (ObjectId) d.get("authorID"), commentList));
        }

    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public ArrayList<Post> getUserPosts(ObjectId userID) {

        ArrayList<Post> userPosts = new ArrayList<>();
        for(Post p: posts) {
            if(p.getAuthorID().equals(userID))
                userPosts.add(p);
        }

        return userPosts;
    }
}
