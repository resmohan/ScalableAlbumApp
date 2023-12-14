package client.part4;

import static util.Constants.DISLIKE;
import static util.Constants.END_MESSAGE;
import static util.Constants.PARAM_SEPARATOR;

import connection.ResponseContent;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import util.PerformanceTracker;
import util.RequestType;
import util.ServiceInvoker;

public class ReviewThread implements Runnable{

  private final ServiceInvoker serviceInvoker;
  private final PerformanceTracker performanceTracker;
  private final AtomicBoolean processData;
  private final BlockingQueue<String> albumQueue;

  public ReviewThread(ServiceInvoker serviceInvoker, PerformanceTracker performanceTracker,
      AtomicBoolean processData, BlockingQueue<String> albumQueue) {
    this.serviceInvoker = serviceInvoker;
    this.performanceTracker = performanceTracker;
    this.processData = processData;
    this.albumQueue = albumQueue;
  }

  @Override
  public void run() {
    try {
      while(processData.get()){
        String albumId = albumQueue.take();
        if(END_MESSAGE.equals(albumId))
          break;
        long startTime = System.currentTimeMillis();
        ResponseContent responseContent = serviceInvoker.invokeReviewGetRequest(albumId);
        performanceTracker.addLatencyInfo(RequestType.GET_REVIEW, startTime, System.currentTimeMillis(), responseContent.getResponseCode());
      }
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
