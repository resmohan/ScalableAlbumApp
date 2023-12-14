package connection;

public class ResponseContent {
  private int responseCode;
  private String responseBody;

  public ResponseContent(int responseCode, String responseBody) {
    this.responseCode = responseCode;
    this.responseBody = responseBody;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public String getResponseBody() {
    return responseBody;
  }
}
