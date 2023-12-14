package db;

import static util.Constants.PROFILE;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Album;
import model.AlbumDetails;
import model.ImageMetadata;

public class DatabaseClient {

  private final AmazonDynamoDB dynamoDbClient;
  private final Gson gson;
  private static final String ALBUM_ID = "AlbumId";
  private static final String IMAGE = "Image";
  private static final String ALBUM = "Album";
  private static final String ALBUM_REVIEW = "AlbumReview";
  private static final String INCR_VAL_EXP = ":incrValue";
  private static final List<String> TABLE_NAMES = List.of(ALBUM,ALBUM_REVIEW);

  public DatabaseClient() {
    dynamoDbClient = AmazonDynamoDBClientBuilder.standard()
        .withRegion(Regions.US_WEST_2)
        .build();
    gson = new Gson();
    initTables();
  }

  private void initTables(){
    try {
      List<AttributeDefinition> attributeDefinitions = List.of(
                    new AttributeDefinition().withAttributeName(ALBUM_ID).withAttributeType("S"));
      List<KeySchemaElement> keySchema = List.of(
                    new KeySchemaElement().withAttributeName(ALBUM_ID).withKeyType(KeyType.HASH));
      ProvisionedThroughput throughput = new ProvisionedThroughput()
                                            .withReadCapacityUnits(5L)
                                            .withWriteCapacityUnits(6L);
      for(String tableName: TABLE_NAMES) {
        CreateTableRequest request = new CreateTableRequest()
                                      .withTableName(tableName)
                                      .withKeySchema(keySchema)
                                      .withAttributeDefinitions(attributeDefinitions)
                                      .withProvisionedThroughput(throughput);
        TableUtils.createTableIfNotExists(dynamoDbClient, request);
        TableUtils.waitUntilActive(dynamoDbClient,tableName);
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void insertAlbumDetails(AlbumDetails albumDetails){
    Map<String,AttributeValue> keyMap = new HashMap<>();
    keyMap.put(ALBUM_ID, new AttributeValue(albumDetails.getAlbumId()));
    keyMap.put(PROFILE, new AttributeValue(gson.toJson(albumDetails.getAlbum(), Album.class)));
    keyMap.put(IMAGE, new AttributeValue(gson.toJson(albumDetails.getImageMetadata(), ImageMetadata.class)));
    PutItemRequest request = new PutItemRequest()
                  .withTableName(ALBUM)
                  .withItem(keyMap);
    dynamoDbClient.putItem(request);
  }

  public Map<String,AttributeValue> getAlbumData(String albumId){
    GetItemRequest request = new GetItemRequest()
                .withKey(Map.of(ALBUM_ID, new AttributeValue(albumId)))
                .withTableName(ALBUM)
                .withAttributesToGet(PROFILE);
    return dynamoDbClient.getItem(request).getItem();
  }

  public void updateReviewData(String albumId, String attributeName, int count){
    try {
      AttributeValue incrExpAttrVal = new AttributeValue();
      incrExpAttrVal.setN(String.valueOf(count));

      UpdateItemRequest request = new UpdateItemRequest()
          .withTableName(ALBUM_REVIEW)
          .withKey(Map.of(ALBUM_ID, new AttributeValue(albumId)))
          .withUpdateExpression("ADD "+ attributeName + " " + INCR_VAL_EXP)
          .withExpressionAttributeValues(Map.of(INCR_VAL_EXP, incrExpAttrVal));
      dynamoDbClient.updateItem(request);
    }catch (RuntimeException exception){
      exception.printStackTrace();
      throw exception;
    }
  }

  public Map<String,AttributeValue> getAlbumReviewData(String albumId){
    GetItemRequest request = new GetItemRequest()
        .withKey(Map.of(ALBUM_ID, new AttributeValue(albumId)))
        .withTableName(ALBUM_REVIEW);
    return dynamoDbClient.getItem(request).getItem();
  }

  public void shutDown(){
    dynamoDbClient.shutdown();
  }
}
