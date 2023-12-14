package message;

import static util.Constants.EXIT_MESSAGE;

import com.google.gson.Gson;
import db.DatabaseClient;
import java.util.concurrent.BlockingQueue;
import model.AlbumReview;

public class MessageProcessor implements Runnable {

  private BlockingQueue<String> messageQueue;
  private DatabaseClient databaseClient;

  public MessageProcessor(BlockingQueue<String> messageQueue, DatabaseClient databaseClient) {
    this.messageQueue = messageQueue;
    this.databaseClient = databaseClient;
  }

  @Override
  public void run() {
    try {
      Gson gson = new Gson();
      String message;
      while(!((message = messageQueue.take()).equals(EXIT_MESSAGE))){
        AlbumReview albumReview = gson.fromJson(message, AlbumReview.class);
        String attrName = albumReview.isLikeAlbum() ? "Likes" : "Dislikes";
        databaseClient.updateReviewData(albumReview.getAlbumId(), attrName,1);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
