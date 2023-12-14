package client.part1;

import static util.Constants.DISLIKE;
import static util.Constants.LIKE;
import static util.Constants.PARAM_SEPARATOR;

import connection.ResponseContent;
import java.util.concurrent.CountDownLatch;
import util.BaseServiceThread;
import util.ServiceInvoker;

public class ServiceThread extends BaseServiceThread {
  private final CountDownLatch countDownLatch;
  private final int counter;
  private final ServiceInvoker serviceInvoker;

  public ServiceThread(CountDownLatch countDownLatch, int counter, ServiceInvoker serviceInvoker) {
    this.countDownLatch = countDownLatch;
    this.counter = counter;
    this.serviceInvoker = serviceInvoker;
  }
/*
  @Override
  public void run() {
    for(int i=0;i<counter;i++) {
      ResponseContent responseContent = serviceInvoker.invokePostRequest();
      String albumId = serviceInvoker.getAlbumId(responseContent);
      serviceInvoker.invokeGetRequest(albumId);
    }
    countDownLatch.countDown();
  }*/

  @Override
  public void run() {
    for(int i=0;i<counter;i++) {
      ResponseContent responseContent = serviceInvoker.invokePostRequest();
      String albumId = serviceInvoker.getAlbumId(responseContent);
      serviceInvoker.invokeReviewPostRequest(LIKE+PARAM_SEPARATOR+albumId);
      serviceInvoker.invokeReviewPostRequest(LIKE+PARAM_SEPARATOR+albumId);
      serviceInvoker.invokeReviewPostRequest(DISLIKE+PARAM_SEPARATOR+albumId);
    }
    countDownLatch.countDown();
  }

}
