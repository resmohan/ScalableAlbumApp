package message;

import static util.Constants.CHARSET_NAME;
import static util.Constants.QUEUE_NAME;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class MessageHandler implements Runnable {
  private Connection connection;
  private BlockingQueue<String> messageQueue;
  public MessageHandler(Connection connection, BlockingQueue<String> messageQueue) {
    this.connection = connection;
    this.messageQueue = messageQueue;
  }

  @Override
  public void run() {
    try {
      Channel channel = connection.createChannel();
      channel.queueDeclare(QUEUE_NAME,false,false,true,null);
      channel.basicQos(1);

      DeliverCallback deliverCallback = (consumerTag, delivery) -> {
        String message = new String(delivery.getBody(), CHARSET_NAME);
        //channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        messageQueue.add(message);
      };

//      channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
      channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
