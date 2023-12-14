package client.part3;

import static util.Constants.DISLIKE;
import static util.Constants.LIKE;
import static util.Constants.PARAM_SEPARATOR;

import connection.ResponseContent;
import java.util.concurrent.CountDownLatch;
import util.BaseServiceThread;
import util.PerformanceTracker;
import util.RequestType;
import util.ServiceInvoker;

public class ServiceThread extends BaseServiceThread {
  private final CountDownLatch countDownLatch;
  private final int counter;
  private final ServiceInvoker serviceInvoker;
  private final PerformanceTracker performanceTracker;

  public ServiceThread(CountDownLatch countDownLatch, int counter,
      ServiceInvoker serviceInvoker, PerformanceTracker performanceTracker) {
    this.countDownLatch = countDownLatch;
    this.counter = counter;
    this.serviceInvoker = serviceInvoker;
    this.performanceTracker = performanceTracker;
  }

  @Override
  public void run() {
    for(int i=0;i<counter;i++) {
      long startTime = System.currentTimeMillis();
      ResponseContent responseContent = serviceInvoker.invokePostRequest();
      String albumId = serviceInvoker.getAlbumId(responseContent);
      performanceTracker.addLatencyInfo(RequestType.POST, startTime, System.currentTimeMillis(), responseContent.getResponseCode());
      startTime = System.currentTimeMillis();
      responseContent = serviceInvoker.invokeReviewPostRequest(LIKE+PARAM_SEPARATOR+albumId);
      performanceTracker.addLatencyInfo(RequestType.POST_LIKE1, startTime, System.currentTimeMillis(), responseContent.getResponseCode());
      startTime = System.currentTimeMillis();
      responseContent = serviceInvoker.invokeReviewPostRequest(LIKE+PARAM_SEPARATOR+albumId);
      performanceTracker.addLatencyInfo(RequestType.POST_LIKE2, startTime, System.currentTimeMillis(), responseContent.getResponseCode());
      startTime = System.currentTimeMillis();
      responseContent = serviceInvoker.invokeReviewPostRequest(DISLIKE+PARAM_SEPARATOR+albumId);
      performanceTracker.addLatencyInfo(RequestType.POST_DISLIKE, startTime, System.currentTimeMillis(), responseContent.getResponseCode());
    }
    countDownLatch.countDown();
  }
}