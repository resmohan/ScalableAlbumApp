package util;

import static util.Constants.IMAGE_TYPES;
import static util.Constants.PROFILE;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.google.gson.Gson;
import db.DatabaseClient;
import image.ImageClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.Part;
import model.Album;
import model.AlbumDetails;
import model.ImageInfo;
import model.ImageMetadata;
import model.Message;
import org.apache.commons.io.IOUtils;

public class AlbumDataHandler {

  private final DatabaseClient databaseClient;
  private final ImageClient imageClient;
  private final Gson gson;

  public AlbumDataHandler() {
    this.databaseClient = new DatabaseClient();
    this.imageClient = new ImageClient();
    gson = new Gson();
  }

  public Object handleGetData(String albumId){
    Map<String, AttributeValue> albumData = databaseClient.getAlbumData(albumId);
    if(albumData == null || albumData.isEmpty())
      return new Message("Couldn't retrieve the album information. User is expected to pass a valid album id.");
    return gson.fromJson(albumData.get(PROFILE).getS(),Album.class);
  }
  public Object handlePostData(Collection<Part> requestParts){
    ImageInfo imageInfo = new ImageInfo();
    AlbumDetails albumDetails = new AlbumDetails();

    Optional<Part> profilePart = requestParts.stream().filter((part) -> "profile".equals(part.getName())).findFirst();
    if(profilePart.isEmpty())
      return new Message("The format of POST request body is not correct.");
    Object validationMsg = validateProfile(profilePart.get(),albumDetails,imageInfo);
    if(validationMsg != null)
      return validationMsg;

    Optional<Part> imagePart = requestParts.stream().filter((part) -> "image".equals(part.getName())).findFirst();
    if(imagePart.isEmpty())
      return new Message("The format of POST request body is not correct.");
    validationMsg = validateImage(imagePart.get(),imageInfo);
    if(validationMsg != null)
      return validationMsg;

    insertRecord(imagePart.get(),albumDetails);
    return imageInfo;
  }

  private Object validateProfile(Part profilePart, AlbumDetails albumDetails, ImageInfo imageInfo){
    try{
      String profileContent = IOUtils.toString(profilePart.getInputStream(), StandardCharsets.UTF_8);
      Album album = gson.fromJson(profileContent, Album.class);
      if(!album.isValidAlbum())
        return new Message("User is expected to populate artist, title and year values in profile.");
      String albumId = UUID.randomUUID().toString();
      imageInfo.setAlbumID(albumId);
      albumDetails.setAlbumId(albumId);
      albumDetails.setAlbum(album);

      return null;
    } catch (IOException e) {
      return new Message("IOException occurred: "+e.getMessage());
    }
  }
  private Object validateImage(Part imagePart, ImageInfo imageInfo){
    try{
      String[] fileNames = imagePart.getSubmittedFileName().split("\\.");
      if(fileNames.length==0 || !IMAGE_TYPES.contains(fileNames[fileNames.length-1]))
        return new Message("User is expected to pass the path of an image file. Given file is not an image.");

      imageInfo.setImageSize(String.valueOf(imagePart.getInputStream().readAllBytes().length));
      return null;
    } catch (IOException e) {
      return new Message("IOException occurred: "+e.getMessage());
    }
  }
  private void insertRecord(Part imagePart, AlbumDetails albumDetails){
    ImageMetadata imageMetadata = imageClient.uploadImage(imagePart,albumDetails.getAlbumId());
    albumDetails.setImageMetadata(imageMetadata);
    databaseClient.insertAlbumDetails(albumDetails);
  }

  public void shutdownResources(){
    databaseClient.shutDown();
    imageClient.shutDown();
  }
}
