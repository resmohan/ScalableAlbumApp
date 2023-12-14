package servlet;

import static util.Constants.PARAM_SEPARATOR;

import model.Album;
import model.ImageInfo;
import model.Message;
import java.util.Collection;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import com.google.gson.Gson;
import util.AlbumDataHandler;

@WebServlet(name = "MusicServlet", value = "/albums/*")
@javax.servlet.annotation.MultipartConfig
public class MusicServlet extends HttpServlet {
    private AlbumDataHandler albumDataHandler;
    private Gson gson;

  @Override
  public void destroy() {
    albumDataHandler.shutdownResources();
  }

  @Override
    public void init() {
      gson = new Gson();
      albumDataHandler = new AlbumDataHandler();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
      String urlPath = request.getPathInfo();
      Object status = validateGetReqFormat(urlPath);
      int statusCode = isGetReqUrlInValid(urlPath) ? HttpServletResponse.SC_BAD_REQUEST :
          status instanceof Album ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND;
      populateResponse(response, statusCode, status);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
      Object status = validatePostReqFormat(request);
      int statusCode = status instanceof ImageInfo ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST;
      populateResponse(response, statusCode, status);
    }

    private void populateResponse(HttpServletResponse response, int statusCode, Object status){
      try {
        response.setContentType("application/json");
        response.setStatus(statusCode);
        response.getWriter().write(gson.toJson(status));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private boolean isGetReqUrlInValid(String urlPath){
      return (urlPath == null || urlPath.split(PARAM_SEPARATOR).length != 2);
    }

    private Object validateGetReqFormat(String urlPath){
      if(isGetReqUrlInValid(urlPath))
        return new Message("Format of GET request is not valid.");

      String[] urlData = urlPath.split(PARAM_SEPARATOR);
      String albumIdParam = urlData[urlData.length-1];
      return albumDataHandler.handleGetData(albumIdParam);
    }

    private Object validatePostReqFormat(HttpServletRequest request) {
      try {
        Collection<Part> requestParts = request.getParts();
        if(requestParts == null || requestParts.size() != 2)
          return new Message("User is expected to send POST request as multipart/form-data format with image and profile values.");

        return albumDataHandler.handlePostData(requestParts);
      } catch (IOException e) {
        return new Message("IOException occurred: "+e.getMessage());
      } catch (ServletException e) {
        return new Message("ServletException occurred: "+e.getMessage());
      }
    }
}
