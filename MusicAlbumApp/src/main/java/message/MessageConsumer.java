package message;

import static util.Constants.CONSUMER_CHANNEL_COUNT;
import static util.Constants.EXIT_MESSAGE;
import static util.Constants.MESSAGE_PROCESSOR_COUNT;

import com.rabbitmq.client.Connection;
import db.DatabaseClient;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageConsumer {
  private ConnectionHandler connectionHandler;
  private DatabaseClient databaseClient;
  private BlockingQueue<String> messageQueue;
  private ExecutorService executorService;

  public void initiateMessageProcess(){
    connectionHandler = new ConnectionHandler();
    Connection connection = connectionHandler.getConnection();
    databaseClient = new DatabaseClient();
    messageQueue = new LinkedBlockingQueue<>();

    executorService = Executors.newCachedThreadPool();
    for(int i=0;i<CONSUMER_CHANNEL_COUNT;i++){
      executorService.submit(new MessageHandler(connection,messageQueue));
    }

    for(int i=0;i<MESSAGE_PROCESSOR_COUNT;i++){
      executorService.submit(new MessageProcessor(messageQueue,databaseClient));
    }
  }

  public void closeProcess(){
    for(int i=0;i<MESSAGE_PROCESSOR_COUNT;i++){
      messageQueue.add(EXIT_MESSAGE);
    }

    databaseClient.shutDown();
    executorService.shutdown();
    connectionHandler.closeConnection();
  }
}
