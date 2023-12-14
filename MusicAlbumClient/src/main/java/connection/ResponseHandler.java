package connection;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;

public class ResponseHandler {

  public ResponseContent getResponseDetails(CloseableHttpResponse response){
    try {
      HttpEntity httpEntity = response.getEntity();
      String responseBody = IOUtils.toString(httpEntity.getContent(), StandardCharsets.UTF_8);
      int responseCode = response.getStatusLine().getStatusCode();
      return new ResponseContent(responseCode,responseBody);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
