package connection;

public class RequestContent {

  private String key;
  private Object value;
  private ContentType contentType;

  public RequestContent(String key, Object value, ContentType contentType) {
    this.key = key;
    this.value = value;
    this.contentType = contentType;
  }

  public String getKey() {
    return key;
  }

  public Object getValue() {
    return value;
  }

  public ContentType getContentType() {
    return contentType;
  }
}
