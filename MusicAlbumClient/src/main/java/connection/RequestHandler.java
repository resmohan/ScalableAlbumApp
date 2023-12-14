package connection;

import java.io.File;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class RequestHandler {

  public HttpGet createGetRequest(String urlPath) {
    return new HttpGet(urlPath);
  }
  public HttpPost createPostRequest(String urlPath) {
    return new HttpPost(urlPath);
  }
  public HttpPost createPostRequest(String urlPath, List<RequestContent> contentList) {
    HttpPost postRequest = new HttpPost(urlPath);
    postRequest.setEntity(generateRequestBody(contentList));
    return postRequest;
  }
  private HttpEntity generateRequestBody(List<RequestContent> contentList){
    MultipartEntityBuilder multipartBuilder = MultipartEntityBuilder.create();
    for(RequestContent requestContent: contentList){
      switch (requestContent.getContentType()){
        case TEXT -> multipartBuilder.addTextBody(requestContent.getKey(), requestContent.getValue().toString());
        case FILE -> multipartBuilder.addBinaryBody(requestContent.getKey(), (File)requestContent.getValue());
      }
    }
    return multipartBuilder.build();
  }
}
