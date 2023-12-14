package util;

import com.google.gson.Gson;
import connection.ClientHandler;
import connection.ContentType;
import connection.RequestContent;
import connection.RequestHandler;
import connection.ResponseHandler;
import java.io.File;
import java.io.IOException;
import java.util.List;
import model.Album;
import connection.ResponseContent;
import model.ImageInfo;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

public class ServiceInvoker {
  private final String albumServiceUrl;
  private String reviewServiceUrl;
  private String reviewServiceUrl1;
  private final ClientHandler clientHandler;
  private RequestHandler requestHandler;
  private final ResponseHandler responseHandler;
  private final Gson gson;
  private List<RequestContent> reqContentList;
//  private static final String ALBUM_ID = "123";
  public ServiceInvoker(String albumServiceUrl){
    this.albumServiceUrl = albumServiceUrl;
    this.clientHandler = new ClientHandler();
    this.requestHandler = new RequestHandler();
    this.responseHandler = new ResponseHandler();
    this.gson = new Gson();
  }

  public ServiceInvoker(String albumServiceUrl, String reviewServiceUrl) {
    this.albumServiceUrl = albumServiceUrl;
    this.reviewServiceUrl = reviewServiceUrl;
    this.clientHandler = new ClientHandler();
    this.requestHandler = new RequestHandler();
    this.responseHandler = new ResponseHandler();
    this.gson = new Gson();
  }

  public ServiceInvoker(String albumServiceUrl, String reviewServiceUrl, String reviewServiceUrl1) {
    this.albumServiceUrl = albumServiceUrl;
    this.reviewServiceUrl = reviewServiceUrl;
    this.reviewServiceUrl1 = reviewServiceUrl1;
    this.clientHandler = new ClientHandler();
    this.requestHandler = new RequestHandler();
    this.responseHandler = new ResponseHandler();
    this.gson = new Gson();
  }

  public void initialize(){
    String albumData = gson.toJson(new Album("A1", "T1", "1977"));
    reqContentList = List.of(new RequestContent("profile", albumData, ContentType.TEXT),
                new RequestContent("image", new File("images/ad.png"), ContentType.FILE));
  }

  public ResponseContent invokeGetRequest(String albumId){
    HttpGet getRequest = requestHandler.createGetRequest(albumServiceUrl + albumId);
    try(CloseableHttpResponse response = clientHandler.executeRequest(getRequest)){
      return responseHandler.getResponseDetails(response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      getRequest.releaseConnection();
    }
  }

  public ResponseContent invokePostRequest(){
    HttpPost  postRequest = requestHandler.createPostRequest(albumServiceUrl, reqContentList);
    try(CloseableHttpResponse response = clientHandler.executeRequest(postRequest)){
      return responseHandler.getResponseDetails(response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      postRequest.releaseConnection();
    }
  }

  public ResponseContent invokeReviewPostRequest(String params){
    HttpPost  postRequest = requestHandler.createPostRequest(reviewServiceUrl + params);
    try(CloseableHttpResponse response = clientHandler.executeRequest(postRequest)){
      return responseHandler.getResponseDetails(response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      postRequest.releaseConnection();
    }
  }

  public ResponseContent invokeReviewGetRequest(String albumId){
    HttpGet getRequest = requestHandler.createGetRequest(reviewServiceUrl1 + albumId);
    try(CloseableHttpResponse response = clientHandler.executeRequest(getRequest)){
      return responseHandler.getResponseDetails(response);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      getRequest.releaseConnection();
    }
  }

  public void closeConnection(){
    clientHandler.closeConnection();
  }

  public String getAlbumId(ResponseContent responseContent){
    ImageInfo imageInfo = gson.fromJson(responseContent.getResponseBody(), ImageInfo.class);
    return imageInfo.getAlbumID();
  }
}
