package util;

import java.util.List;

public class Constants {

  public static final String BUCKET_NAME = "albumimagebucket";
  public static final List<String> IMAGE_TYPES =  List.of("png","jpg","jpeg");
  public static final String PROFILE = "Profile";
  public static final String LIKE = "like";
  public static final String DISLIKE = "dislike";
  public static final List<String> reviewValues = List.of(LIKE,DISLIKE);
  public static final String QUEUE_NAME = "AlbumReview";
  public static final int PUBLISHER_CHANNEL_COUNT = 40;//20 20 5 - 779  //50 15 5 - 779
  public static final int CONSUMER_CHANNEL_COUNT = 20;//40 40 10 - 789  //50 10 5 - 810
  public static final int MESSAGE_PROCESSOR_COUNT = 5;//40 20 5 - 845 - 882  //40 10 5 - 833
  public static final String CHARSET_NAME = "UTF-8";//75 20 5 - 789 - 821
  public static final String PARAM_SEPARATOR = "/";
  public static final String EXIT_MESSAGE = "EXIT";
}
