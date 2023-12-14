package client.part3;

import static util.Constants.HTTP_PATH;
import static util.Constants.JAVA_ALBUM_SERVER_PATH;
import static util.Constants.JAVA_REVIEW_SERVER_PATH;

import util.PerformanceTracker;
import util.RequestType;
import util.ThreadHandler;
public class ServiceHandler {

  public static void main(String[] args) {
    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delay = Integer.parseInt(args[2]);
    String albumIPAddr = args[3];
    String reviewIPAddr = args[3];
    System.out.println("threadGroupSize: "+threadGroupSize+" numThreadGroups: "+numThreadGroups+" delay: "+delay);

    String albumServiceUrl = HTTP_PATH + albumIPAddr + JAVA_ALBUM_SERVER_PATH;
    String reviewServiceUrl = HTTP_PATH + reviewIPAddr + JAVA_REVIEW_SERVER_PATH;
    System.out.println("\n***** Running on Java server *****");

    PerformanceTracker performanceTracker = new PerformanceTracker(true);
    ThreadHandler threadHandler = new ThreadHandler(threadGroupSize,numThreadGroups,delay,performanceTracker);
    threadHandler.startInvocation(albumServiceUrl,reviewServiceUrl);

    performanceTracker.printPerformanceDetails(numThreadGroups*threadGroupSize);
    performanceTracker.printLatencyDetails(RequestType.POST);
    performanceTracker.printCountDetails();
    performanceTracker.printLatencyDetails(RequestType.POST_LIKE1);
    performanceTracker.printCountDetails();
    performanceTracker.printLatencyDetails(RequestType.POST_LIKE2);
    performanceTracker.printCountDetails();
    performanceTracker.printLatencyDetails(RequestType.POST_DISLIKE);
    performanceTracker.printCountDetails();
  }
}
