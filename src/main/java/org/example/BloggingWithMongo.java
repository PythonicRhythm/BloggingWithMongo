package org.example;

import com.mongodb.BasicDBList;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Scanner;

import static com.mongodb.client.model.Filters.eq;

/**
 *
 */
public class BloggingWithMongo
{
    private final String DB_String = "mongodb://localhost:27017";
    private final String DB_Name = "BloggingWithMongo";
    private final String postCollection = "posts";
    private final String userCollection = "users";
    private final Scanner consoleReader = new Scanner(System.in);
    private MongoClient client;
    private MongoDatabase db;
    private MongoCollection<Document> postDocuments;
    private MongoCollection<Document> userDocuments;
    private PostFeed postFeed;
    private User currentUser;

    public BloggingWithMongo() {
        attemptDBConnection();
        postFeed = new PostFeed(postDocuments);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public PostFeed getPostFeed() {
        return postFeed;
    }

    public void attemptDBConnection() {
        System.out.println("Attempting to connect with database...");
        client = MongoClients.create(DB_String);
        db = client.getDatabase(DB_Name);
        postDocuments = db.getCollection(postCollection);
        userDocuments = db.getCollection(userCollection);
        System.out.println("Connection was successful!");
    }

    public boolean authenticateUser() {
        while(true) {
            ArrayList<Document> possibleMatches = new ArrayList<>();

            System.out.println("\nEnter your username.\nEnter 'exit' to return to menu.");
            System.out.print("> ");
            String user = consoleReader.nextLine().strip().toLowerCase();
            if(user.equals("exit")) return false;

            System.out.println("\nEnter your password.\nEnter 'exit' to return to menu.");
            System.out.print("> ");
            String pass = consoleReader.nextLine().strip();
            if(pass.equals("exit")) return false;

            Bson filter  = Filters.and(Filters.eq("username", user), Filters.eq("password", pass));
            userDocuments.find(filter).forEach(possibleMatches::add);

            if(possibleMatches.size() > 1) {
                System.out.println("Error: multiple users with that login info. Server issue must be resolved. Closing...");
                System.exit(0);
            }
            else if(possibleMatches.isEmpty()) {
                System.out.println("No user found with that username and password");
            }
            else {
                Document doc = possibleMatches.get(0);
                ArrayList<ObjectId> allPosts;
                if(doc.get("posts") == null) allPosts = new ArrayList<>();
                else allPosts = (ArrayList<ObjectId>) doc.get("posts");
                currentUser = new User((ObjectId) doc.get("_id"), (String) doc.get("name"), allPosts);
                return true;
            }
        }
    }

    public int promptUser() {

        System.out.println("1. View Post Feed\n2. View Your Posts\n3. Add New Post\n4. Exit (or type 'exit')");
        while (true) {
            System.out.print("> ");
            String response = consoleReader.nextLine().strip().toLowerCase();
            if(response.equals("exit")) return -1;
            try {
                int value = Integer.parseInt(response);
                if(value < 1 || value > 4) {
                    System.out.println("Invalid response. Try again.");
                    continue;
                }
                return value;
            } catch(NumberFormatException ex) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public void postComment(ObjectId postID) {
        System.out.println("\nWhat would you like to comment?");
        System.out.print("> ");
        String commentBody = consoleReader.nextLine().strip();
        Document newComment = new Document();
        newComment.put("comment", commentBody);
        newComment.put("commenterName", currentUser.getName());
        newComment.put("commenterID", currentUser.getUserID());

        Document updateFilter = new Document("_id", postID);
        Bson command = Updates.addToSet("comments", newComment);
        UpdateResult result = postDocuments.updateOne(updateFilter, command);
        postFeed.initializePosts(postDocuments);
        System.out.println("Comment Posted!");
    }

    public void interactWithPosts() {
        if(postFeed.getPosts().isEmpty()) {
            System.out.println("Currently there are no posts. Make one!");
            return;
        }

        int postIndex = 0;
        while(true) {
            postFeed.getPosts().get(postIndex).displayPost();
            System.out.println("Interact with Post?\n1. Post Comment\n2. Previous Post\n" +
                    "3. Next Post\n4. Exit (or type 'exit');");

            while(true) {
                System.out.print("> ");
                String response = consoleReader.nextLine().strip().toLowerCase();
                if(response.equals("exit")) return;

                try {
                    int value = Integer.parseInt(response);
                    if(value < 0 | value > 4) {
                        System.out.println("Invalid Response. Not a value on the list.");
                        continue;
                    }

                    switch (value) {
                        case 1:
                            postComment(postFeed.getPosts().get(postIndex).getPostId());
                            break;
                        case 2:
                            if(postIndex-1 < 0) {
                                postIndex = postFeed.getPosts().size()-1;
                                break;
                            }
                            else
                                postIndex--;
                            break;
                        case 3:
                            if(postIndex+1 >= postFeed.getPosts().size()) {
                                postIndex = 0;
                                break;
                            }
                            else
                                postIndex++;
                            break;
                        case 4:
                            return;
                        default:
                            break;
                    }

                    break;

                } catch(NumberFormatException ex) {
                    System.out.println("Please enter a number.");
                }
            }
        }
    }

    public void deletePost(ObjectId postToBeDeleted) {
        Document updateFilter = new Document("_id", postToBeDeleted);
        postDocuments.deleteOne(updateFilter);
        postFeed.initializePosts(postDocuments);
        System.out.println("Post was deleted!");
    }

    public void addAPost() {
        Document newPost = new Document();
        System.out.println("\nEnter the title of the post.\nEnter 'exit' to return to menu.");
        while(true) {
            System.out.print("> ");
            String response = consoleReader.nextLine().strip();
            if(response.equalsIgnoreCase("exit")) return;
            if(response.length() < 5) {
                System.out.println("Not enough characters for a title.");
                continue;
            }
            newPost.put("title", response);
            break;
        }
        System.out.println("\nEnter the body of the post.\nEnter 'exit' to return to menu.");
        while(true) {
            System.out.print("> ");
            String response = consoleReader.nextLine().strip();
            if(response.equalsIgnoreCase("exit")) return;
            if(response.length() < 10) {
                System.out.println("Not enough characters for a body.");
                continue;
            }
            newPost.put("body", response);
            break;
        }

        newPost.put("tags", new BasicDBList());
        newPost.put("comments", new BasicDBList());
        newPost.put("authorID", currentUser.getUserID());
        newPost.put("authorName", currentUser.getName());
        postDocuments.insertOne(newPost);
        postFeed.initializePosts(postDocuments);
        System.out.println("Post was added!");
    }

    public void viewYourPosts() {
        if(currentUser.getPostIDs().isEmpty()) {
            System.out.println("\nYou haven't created any posts. Try making some!");
            return;
        }

        int postIndex = 0;
        while(true) {
            postFeed.getPosts().get(postIndex).displayPost();
            System.out.println("Interact with Post?\n1. Post Comment\n2. Previous Post\n" +
                    "3. Next Post\n4. Delete Post\n5. Exit (or type 'exit');");

            while(true) {
                System.out.print("> ");
                String response = consoleReader.nextLine().strip().toLowerCase();
                if(response.equals("exit")) return;

                try {
                    int value = Integer.parseInt(response);
                    if(value < 0 | value > 5) {
                        System.out.println("Invalid Response. Not a value on the list.");
                        continue;
                    }

                    switch (value) {
                        case 1:
                            postComment(postFeed.getPosts().get(postIndex).getPostId());
                            break;
                        case 2:
                            if(postIndex-1 < 0) {
                                postIndex = postFeed.getPosts().size()-1;
                                continue;
                            }
                            else
                                postIndex--;
                            break;
                        case 3:
                            if(postIndex+1 >= postFeed.getPosts().size()) {
                                postIndex = 0;
                                continue;
                            }
                            else
                                postIndex++;
                            break;
                        case 4:
                            deletePost(postFeed.getPosts().get(postIndex).getPostId());
                            return;
                        case 5:
                            return;
                        default:
                            break;
                    }

                    break;

                } catch(NumberFormatException ex) {
                    System.out.println("Please enter a number.");
                }
            }
        }
    }

    public static void main( String[] args )
    {
        BloggingWithMongo blogProgram = new BloggingWithMongo();
        if(!blogProgram.authenticateUser()) System.exit(0);
        while(true) {
            System.out.format("\nWelcome to BloggingWithMongo, %s!%n", blogProgram.getCurrentUser().getName());
            int choice = blogProgram.promptUser();
            switch (choice) {
                // View Post Feed.
                case 1:
                    blogProgram.interactWithPosts();
                    break;
                // View Your Posts.
                case 2:
                    blogProgram.viewYourPosts();
                    break;
                // Add New Post.
                case 3:
                    blogProgram.addAPost();
                    break;
                // Exit
                case 4:
                    System.out.println("Goodbye");
                    System.exit(0);
                    break;
                default:
                    break;

            }
        }

    }
}
