package message;

import static util.Constants.QUEUE_NAME;

import com.rabbitmq.client.Channel;
import java.io.IOException;
public class MessagePublisher {
  private ConnectionPoolHandler connectionPoolHandler;
  public MessagePublisher(ConnectionPoolHandler connectionPoolHandler) {
    this.connectionPoolHandler = connectionPoolHandler;
  }
  public void publishMessage(byte[] message){
    try {
      Channel channel = connectionPoolHandler.getChannel();
      channel.queueDeclare(QUEUE_NAME,false,false,true,null);
      channel.basicPublish("",QUEUE_NAME,null,message);
      connectionPoolHandler.releaseChannel(channel);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
