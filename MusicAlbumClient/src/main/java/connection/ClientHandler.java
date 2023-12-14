package connection;

import static util.Constants.MAX_CONNECTIONS;
import static util.Constants.MAX_PER_ROUTE;
import static util.Constants.RETRY_COUNT;
import static util.Constants.RETRY_RESP_CODE_END;
import static util.Constants.RETRY_RESP_CODE_START;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

public class ClientHandler {
  private final CloseableHttpClient client;

  public ClientHandler() {
    PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
    connectionManager.setMaxTotal(MAX_CONNECTIONS);
    connectionManager.setDefaultMaxPerRoute(MAX_PER_ROUTE);
    this.client = HttpClientBuilder
                    .create()
                    .setServiceUnavailableRetryStrategy(
                        new DefaultServiceUnavailableRetryStrategy() {
                            @Override
                            public boolean retryRequest(HttpResponse response,
                                        int executionCount, HttpContext context) {
                              int responseCode = response.getStatusLine().getStatusCode();
                              return responseCode >= RETRY_RESP_CODE_START
                                  && responseCode < RETRY_RESP_CODE_END && executionCount <= RETRY_COUNT;
                            }
                        })
                    .setConnectionManager(connectionManager)
                    .build();
  }

  public CloseableHttpResponse executeRequest(HttpUriRequest request){
    try {
      return client.execute(request);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void closeConnection(){
    try {
      client.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
