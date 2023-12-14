package client.part1;

import static util.Constants.GO_SERVER_PATH;
import static util.Constants.HTTP_PATH;
import static util.Constants.JAVA_SERVER_PATH;

import java.util.List;
import util.PerformanceTracker;
import util.ThreadHandler;

public class ServiceHandler {
  public static void main(String[] args) {
    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delay = Integer.parseInt(args[2]);
    String IPAddr = args[3];
    System.out.println("threadGroupSize: "+threadGroupSize+" numThreadGroups: "+numThreadGroups+" delay: "+delay);

//    List<String> serviceUrls = List.of(HTTP_PATH + IPAddr + JAVA_SERVER_PATH,
//                                    HTTP_PATH + IPAddr + GO_SERVER_PATH);
    List<String> serviceUrls = List.of(HTTP_PATH + IPAddr + JAVA_SERVER_PATH);
//    List<String> serviceUrls = List.of(HTTP_PATH + IPAddr + GO_SERVER_PATH);
    for(String url: serviceUrls) {
      String serverName = url.contains(JAVA_SERVER_PATH) ? "Java" : "Go";
      System.out.println("\n***** Running on "+serverName+" server *****");

      PerformanceTracker performanceTracker = new PerformanceTracker(false);
      ThreadHandler threadHandler = new ThreadHandler(threadGroupSize,numThreadGroups,delay,performanceTracker);
      threadHandler.startInvocation(url);

      performanceTracker.printPerformanceDetails(numThreadGroups*threadGroupSize);
    }
  }
}
