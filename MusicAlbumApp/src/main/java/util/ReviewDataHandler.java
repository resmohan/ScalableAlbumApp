package util;

import static util.Constants.CHARSET_NAME;
import static util.Constants.DISLIKE;
import static util.Constants.LIKE;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import db.DatabaseClient;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import message.ConnectionHandler;
import message.ConnectionPoolHandler;
import message.MessagePublisher;
import model.AlbumReview;
import model.Message;
import model.ReviewData;

public class ReviewDataHandler {
  private final Gson gson;
  private final ConnectionHandler connectionHandler;
  private final ConnectionPoolHandler connectionPoolHandler;
  private final MessagePublisher messagePublisher;
  private final DatabaseClient databaseClient;

  public ReviewDataHandler() {
    gson = new Gson();
    this.databaseClient = new DatabaseClient();
    connectionHandler = new ConnectionHandler();
    connectionPoolHandler = new ConnectionPoolHandler(connectionHandler);
    messagePublisher = new MessagePublisher(connectionPoolHandler);
  }

  public void handlePostData(String review, String albumId){
    try {
      AlbumReview albumReview = new AlbumReview(albumId, LIKE.equals(review));
      byte[] reviewData = gson.toJson(albumReview, AlbumReview.class).getBytes(CHARSET_NAME);
      messagePublisher.publishMessage(reviewData);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public Object handleGetData(String albumId){
    Map<String, AttributeValue> albumReviewData = databaseClient.getAlbumReviewData(albumId);
    if(albumReviewData == null || albumReviewData.isEmpty())
      return new Message("Couldn't retrieve the album review information. User is expected to pass a valid album id.");
    int likes=0, dislikes=0;
    if(albumReviewData.get(LIKE) != null){
      likes = Integer.parseInt(albumReviewData.get(LIKE).getN());
    }
    if(albumReviewData.get(DISLIKE) != null){
      dislikes = Integer.parseInt(albumReviewData.get(DISLIKE).getN());
    }
    return new ReviewData(likes,dislikes);
  }
  public void shutdownResources(){
    databaseClient.shutDown();
    connectionPoolHandler.closeChannels();
    connectionHandler.closeConnection();
  }
}
