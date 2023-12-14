package message;

import static util.Constants.PUBLISHER_CHANNEL_COUNT;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;

public class ConnectionPoolHandler {
  private ConnectionHandler connectionHandler;
  private BlockingQueue<Channel> channelPool;

  public ConnectionPoolHandler(ConnectionHandler connectionHandler) {
    this.connectionHandler = connectionHandler;
    this.channelPool = new LinkedBlockingQueue<>();
    initializePool();
  }

  private void initializePool(){
    Connection connection = connectionHandler.getConnection();
    try {
      for(int i=0;i< PUBLISHER_CHANNEL_COUNT;i++)
        channelPool.add(connection.createChannel());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public Channel getChannel(){
    try {
      return channelPool.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void releaseChannel(Channel channel){
    if(channel != null)
      channelPool.add(channel);
  }

  public void closeChannels(){
    try {
      for (Channel channel : channelPool) {
        channel.close();
      }
    }catch (IOException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }
}
