package client.part4;

import static util.Constants.HTTP_PATH;
import static util.Constants.JAVA_ALBUM_SERVER_PATH;
import static util.Constants.JAVA_REVIEW_SERVER_PATH;

import java.util.List;
import util.PerformanceTracker;
import util.RequestType;
import util.ThreadHandler;

public class ServiceHandler {

  public static void main(String[] args) {
    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delay = Integer.parseInt(args[2]);
    String albumIPAddr = args[3];
    String reviewIPAddr = args[4];
    System.out.println("threadGroupSize: "+threadGroupSize+" numThreadGroups: "+numThreadGroups+" delay: "+delay);

    String albumServiceUrl = HTTP_PATH + albumIPAddr + JAVA_ALBUM_SERVER_PATH;
    String reviewServiceUrl = HTTP_PATH + albumIPAddr + JAVA_REVIEW_SERVER_PATH;
    String reviewServiceUrl1 = HTTP_PATH + reviewIPAddr + JAVA_REVIEW_SERVER_PATH;
    System.out.println("Server info: "+albumServiceUrl+", "+reviewServiceUrl1);

    PerformanceTracker performanceTracker = new PerformanceTracker(true);
    ThreadHandler threadHandler = new ThreadHandler(threadGroupSize,numThreadGroups,delay,performanceTracker);
//    threadHandler.startInvocation(albumServiceUrl,reviewServiceUrl,3);
    threadHandler.startInvocation(albumServiceUrl,reviewServiceUrl,reviewServiceUrl1,3);

    performanceTracker.printPerformanceDetails(numThreadGroups*threadGroupSize);
    performanceTracker.printLatencyDetails(RequestType.POST);
    performanceTracker.printLatencyDetails(RequestType.POST_LIKE1);
    performanceTracker.printLatencyDetails(RequestType.POST_LIKE2);
    performanceTracker.printLatencyDetails(RequestType.POST_DISLIKE);
    performanceTracker.printCountDetails(List.of(RequestType.POST,RequestType.POST_LIKE1,RequestType.POST_LIKE2,RequestType.POST_DISLIKE));

    performanceTracker.printReviewPerformanceDetails();
    performanceTracker.printLatencyDetails(RequestType.GET_REVIEW);
    performanceTracker.printCountDetails(List.of(RequestType.GET_REVIEW));
    //ec2-35-161-126-160.us-west-2.compute.amazonaws.com
    //ec2-35-85-58-197.us-west-2.compute.amazonaws.com
    //ec2-35-92-147-130.us-west-2.compute.amazonaws.com
  }
}
