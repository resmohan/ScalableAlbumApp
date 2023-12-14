package servlet;

import static util.Constants.PARAM_SEPARATOR;
import static util.Constants.reviewValues;

import com.google.gson.Gson;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import message.MessageConsumer;
import model.Message;
import model.ReviewData;
import util.ReviewDataHandler;

@WebServlet(name = "AlbumReviewServlet", value = "/review/*")
@javax.servlet.annotation.MultipartConfig
public class AlbumReviewServlet extends HttpServlet {

  private ReviewDataHandler reviewDataHandler;
  private Gson gson;
  private MessageConsumer messageConsumer;

  @Override
  public void init() {
    reviewDataHandler = new ReviewDataHandler();
    gson = new Gson();
    messageConsumer = new MessageConsumer();
    messageConsumer.initiateMessageProcess();
  }

  @Override
  public void destroy() {
    messageConsumer.closeProcess();
    reviewDataHandler.shutdownResources();
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
    String urlPath = req.getPathInfo();
    Object status = processPostRequest(urlPath);
    int statusCode = (status == null) ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST;
    populateResponse(resp, statusCode, status);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    String urlPath = request.getPathInfo();
    Object status = validateGetReqFormat(urlPath);
    int statusCode = isGetReqUrlInValid(urlPath) ? HttpServletResponse.SC_BAD_REQUEST :
        status instanceof ReviewData ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND;
    populateResponse(response, statusCode, status);
  }

  private void populateResponse(HttpServletResponse response, int statusCode, Object status){
    try {
      response.setContentType("application/json");
      response.setStatus(statusCode);
      if(status != null)
        response.getWriter().write(gson.toJson(status));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  private Object processPostRequest(String urlPath){
    if(isPostReqUrlInValid(urlPath))
      return new Message("Format of POST request is not valid.");

    String[] urlData = urlPath.split(PARAM_SEPARATOR);
    String review = urlData[urlData.length-2];
    String albumIdParam = urlData[urlData.length-1];
    reviewDataHandler.handlePostData(review, albumIdParam);
    return null;
  }

  private boolean isPostReqUrlInValid(String urlPath){
    return (urlPath == null || urlPath.split(PARAM_SEPARATOR).length != 3
        || !reviewValues.contains(urlPath.split(PARAM_SEPARATOR)[1]));
  }

  private boolean isGetReqUrlInValid(String urlPath){
    return (urlPath == null || urlPath.split(PARAM_SEPARATOR).length != 2);
  }

  private Object validateGetReqFormat(String urlPath){
    if(isGetReqUrlInValid(urlPath))
      return new Message("Format of GET request is not valid.");

    String[] urlData = urlPath.split(PARAM_SEPARATOR);
    String albumIdParam = urlData[urlData.length-1];
    return reviewDataHandler.handleGetData(albumIdParam);
  }
}
