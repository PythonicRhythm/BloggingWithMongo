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

/**
 *  BloggingWithMongo is a java blogging program that works with MongoDB.
 *  Users can view posts saved by other users, post comments on everyone's
 *  posts that exist in the database, and manage their own posts that exist
 *  in the program. Users can create their own posts and delete their own posts
 *  but they can not alter other user's posts besides commenting.
 */
public class BloggingWithMongo
{
    private final String DB_String = "mongodb://localhost:27017";   // The connection string to connect to MongoDB
    private final String DB_Name = "BloggingWithMongo";             // The database name.
    private final String postCollection = "posts";                  // The collection name that contains all posts.
    private final String userCollection = "users";                  // The collection name that contains all users.
    private final Scanner consoleReader = new Scanner(System.in);   // Console Reader.
    private MongoClient client;                                     // The client connection to Mongo.
    private MongoDatabase db;                                       // MongoDB database.
    private MongoCollection<Document> postDocuments;                // Collection for all posts.
    private MongoCollection<Document> userDocuments;                // Collection for all users.
    private PostFeed postFeed;                                      // The list of all posts that exist in system.
    private User currentUser;                                       // The active user of the program.

    public BloggingWithMongo() {
        attemptDBConnection();
        postFeed = new PostFeed(postDocuments);
    }

    // Get the active user in the program.
    public User getCurrentUser() {
        return currentUser;
    }

    // attemptDBConnection() will attempt to connection to the
    // mongoDB database that is saved locally.
    public void attemptDBConnection() {
        System.out.println("Attempting to connect with database...");
        client = MongoClients.create(DB_String);
        db = client.getDatabase(DB_Name);
        postDocuments = db.getCollection(postCollection);
        userDocuments = db.getCollection(userCollection);
        System.out.println("Connection was successful!");
    }

    // authenticateUser() will receive a username and password from the user
    // and check if it matches any users that are saved in MongoDB. If
    // the credentials match a user that exists in the DB return true. If
    // there is no match, then keep looping until there is a match or the
    // user wants to exit the program.
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

    // promptUser() will display the menu to the user via the terminal.
    // Checks the response received from the user and if it's valid,
    // it returns the choice by the user, else it loops.
    public int promptUser() {
        // Print menu
        System.out.println("1. View Post Feed\n2. View Your Posts\n3. Add New Post\n4. Exit (or type 'exit')");
        while (true) {
            // Gather user response.
            System.out.print("> ");
            String response = consoleReader.nextLine().strip().toLowerCase();
            if(response.equals("exit")) return -1;
            try {
                // Convert to int and if invalid, try again.
                int value = Integer.parseInt(response);
                if(value < 1 || value > 4) {
                    System.out.println("Invalid response. Try again.");
                    continue;
                }
                return value;
            } catch(NumberFormatException ex) {
                // User entered a string.
                System.out.println("Please enter a number.");
            }
        }
    }

    // postComment() will allow users to post comments on all posts
    // in the database. They can post a comment on their own posts
    // or other user's posts. The parameter that is received is the id
    // of the post that is getting commented to.
    public void postComment(ObjectId postID) {

        // Prompt for content of new comment.
        System.out.println("\nWhat would you like to comment?");
        System.out.print("> ");
        String commentBody = consoleReader.nextLine().strip();

        // Create document representing new comment.
        Document newComment = new Document();
        newComment.put("comment", commentBody);
        newComment.put("commenterName", currentUser.getName());
        newComment.put("commenterID", currentUser.getUserID());

        // Post new commment.
        Document updateFilter = new Document("_id", postID);
        Bson command = Updates.addToSet("comments", newComment);
        UpdateResult result = postDocuments.updateOne(updateFilter, command);
        postFeed.initializePosts(postDocuments);
        System.out.println("Comment Posted!");
    }

    // interactWithPosts() will display all the posts from
    // all the users that have used the program. Users can not
    // delete posts from this menu. Users can post a comment on
    // other user's posts and view them. They can cycle through
    // posts using next and previous which moves using the
    // arraylist index of postFeed.getPosts().
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

    // deletePost() will delete the post that is represented
    // by the objectID that is passed as a parameter.
    public void deletePost(ObjectId postToBeDeleted) {
        Document updateFilter = new Document("_id", postToBeDeleted);
        postDocuments.deleteOne(updateFilter);
        postFeed.initializePosts(postDocuments);
        System.out.println("Post was deleted!");
    }

    // addAPost() will allow users to create their own
    // posts in viewYourPosts(). They are prompted for
    // a title and body for their new post. If all input
    // is valid, the post will be created and mongoDB is
    // updated.
    public void addAPost() {
        Document newPost = new Document();

        // Gather title of new post.
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

        // Gather body of new post.
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

        // Create new post and update mongoDB.
        // Reinitialize the current status of
        // all the posts in mongo using
        // postFeed.initializePosts().
        newPost.put("tags", new BasicDBList());
        newPost.put("comments", new BasicDBList());
        newPost.put("authorID", currentUser.getUserID());
        newPost.put("authorName", currentUser.getName());
        postDocuments.insertOne(newPost);
        currentUser.insertPostID((ObjectId) newPost.get("_id"));
        postFeed.initializePosts(postDocuments);
        System.out.println("Post was added!");
    }

    // viewYourPosts() will allow the currentUser to view all
    // the posts that they have made. They can move to next
    // and to previous posts which is based on arraylist index
    // that wraps around for QOL. They can also delete and comment
    // on their own posts from this menu.
    public void viewYourPosts() {
        // Gather currentUser posts.
        ArrayList<Post> userPosts = postFeed.getUserPosts(currentUser.getUserID());
        if(userPosts.isEmpty()) {
            System.out.println("\nYou haven't created any posts. Try making some!");
            return;
        }

        // While the user doesn't want to exit
        // cycle through all user posts.
        int postIndex = 0;
        while(true) {
            // Display post and options
            userPosts.get(postIndex).displayPost();
            System.out.println("Interact with Post?\n1. Post Comment\n2. Previous Post\n" +
                    "3. Next Post\n4. Delete Post\n5. Exit (or type 'exit');");

            // inner while loop manages input gathered.
            // if input was invalid, cycle again.
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
                        // user posted comment.
                        case 1:
                            postComment(userPosts.get(postIndex).getPostId());
                            userPosts = postFeed.getUserPosts(currentUser.getUserID());
                            break;
                        // user is going left which can wrap around.
                        case 2:
                            if(postIndex-1 < 0) {
                                postIndex = userPosts.size()-1;
                                break;
                            }
                            else
                                postIndex--;
                            break;
                        // user went right which can wrap around.
                        case 3:
                            if(postIndex+1 >= userPosts.size()) {
                                postIndex = 0;
                                break;
                            }
                            else
                                postIndex++;
                            break;
                        // user deleted a post, which causes them to return.
                        case 4:
                            deletePost(userPosts.get(postIndex).getPostId());
                            return;
                        // user chose to exit the menu.
                        case 5:
                            return;
                        default:
                            break;
                    }

                    break;

                } catch(NumberFormatException ex) {
                    // user entered a string.
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
