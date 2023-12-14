package message;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionHandler {

  private Connection connection;

  public ConnectionHandler() {
    this.connection = initializeConnection();
  }

  private Connection initializeConnection(){
    try {
      ConnectionFactory connectionFactory = new ConnectionFactory();
//      connectionFactory.setHost(System.getProperty("MESSAGE_HOST_NAME"));
//      connectionFactory.setPort(Integer.parseInt(System.getProperty("MESSAGE_PORT")));
//      connectionFactory.setUsername(System.getProperty("MESSAGE_USER_NAME"));
//      connectionFactory.setPassword(System.getProperty("MESSAGE_PASSWORD"));
      connectionFactory.setHost("ec2-54-245-105-121.us-west-2.compute.amazonaws.com");
      connectionFactory.setPort(5672);
      connectionFactory.setUsername("guest");
      connectionFactory.setPassword("guest");

      return connectionFactory.newConnection();
    } catch (IOException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }

  public Connection getConnection() {
    return connection;
  }

  public void closeConnection(){
    try {
      connection.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
