package util;

import static util.Constants.END_MESSAGE;
import static util.Constants.INITIAL_LOOP_COUNT;
import static util.Constants.INITIAL_THREAD_COUNT;
import static util.Constants.LOOP_COUNT;

import client.part2.ServiceThread;
import client.part4.ReviewThread;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadHandler {
  private final int threadGroupSize;
  private final int numThreadGroups;
  private final int delay;
  private final PerformanceTracker performanceTracker;

  public ThreadHandler(int threadGroupSize, int numThreadGroups, int delay, PerformanceTracker performanceTracker) {
    this.threadGroupSize = threadGroupSize;
    this.numThreadGroups = numThreadGroups;
    this.delay = delay;
    this.performanceTracker = performanceTracker;
  }

  public void startInvocation(String albumServiceUrl){
    ServiceInvoker serviceInvoker = new ServiceInvoker(albumServiceUrl);
    serviceInvoker.initialize();
    startThreadInvocation(serviceInvoker);
  }

  public void startInvocation(String albumServiceUrl, String reviewServiceUrl){
    ServiceInvoker serviceInvoker = new ServiceInvoker(albumServiceUrl, reviewServiceUrl);
    serviceInvoker.initialize();
    startThreadInvocation(serviceInvoker);
  }

  public void startInvocation(String albumServiceUrl, String reviewServiceUrl, int numThreads){
    ServiceInvoker serviceInvoker = new ServiceInvoker(albumServiceUrl, reviewServiceUrl);
    serviceInvoker.initialize();
    startThreadInvocation(serviceInvoker,numThreads);
  }

  public void startInvocation(String albumServiceUrl, String reviewServiceUrl, String reviewServiceUrl1, int numThreads){
    ServiceInvoker serviceInvoker = new ServiceInvoker(albumServiceUrl, reviewServiceUrl,reviewServiceUrl1);
    serviceInvoker.initialize();
    startThreadInvocation(serviceInvoker,numThreads);
  }

  private void startThreadInvocation(ServiceInvoker serviceInvoker){
    try {
      ExecutorService executorService = Executors.newCachedThreadPool();
      startThreads(executorService, INITIAL_THREAD_COUNT, INITIAL_LOOP_COUNT,serviceInvoker,false).await();

      long startTime = System.currentTimeMillis();
      List<CountDownLatch> countDownLatchList = new ArrayList<>();
      for(int i=0;i<numThreadGroups;i++){
        countDownLatchList.add(startThreads(executorService, threadGroupSize, LOOP_COUNT,serviceInvoker,
            performanceTracker.isCaptureDetails()));
        TimeUnit.SECONDS.sleep(delay);
      }

      for(CountDownLatch countDownLatch: countDownLatchList){
        countDownLatch.await();
      }

      executorService.shutdown();
      serviceInvoker.closeConnection();
      performanceTracker.setTotalTime(System.currentTimeMillis() - startTime);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private void startThreadInvocation(ServiceInvoker serviceInvoker, int numThreads){
    try {
      ExecutorService executorService = Executors.newCachedThreadPool();
      startThreads(executorService, INITIAL_THREAD_COUNT, INITIAL_LOOP_COUNT,serviceInvoker,false).await();

      BlockingQueue<String> albumQueue = new LinkedBlockingQueue<>();
      AtomicBoolean processData = new AtomicBoolean(true);
      List<CountDownLatch> countDownLatchList = new ArrayList<>();
      long reviewStartTime = 0;
      long startTime = System.currentTimeMillis();
      for(int i=0;i<numThreadGroups;i++){
        if(i==1){
          reviewStartTime = System.currentTimeMillis();
          startThreads(executorService,numThreads,serviceInvoker,processData,albumQueue);
        }
        countDownLatchList.add(startThreads(executorService, threadGroupSize, LOOP_COUNT,serviceInvoker,albumQueue));
        TimeUnit.SECONDS.sleep(delay);
      }

      for(CountDownLatch countDownLatch: countDownLatchList){
        countDownLatch.await();
      }

      stopThreads(numThreads,processData,albumQueue);

      executorService.shutdown();
      executorService.awaitTermination(1,TimeUnit.SECONDS);
      serviceInvoker.closeConnection();
      performanceTracker.setTotalReviewTime(System.currentTimeMillis() - reviewStartTime);
      performanceTracker.setTotalTime(System.currentTimeMillis() - startTime);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private CountDownLatch startThreads(ExecutorService executorService, int threadCount, int loopCount,
                                ServiceInvoker serviceInvoker, boolean captureDetails){
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
//      BaseServiceThread serviceThread = captureDetails ? new ServiceThread(countDownLatch, loopCount, serviceInvoker,performanceTracker)
//                                          : new client.part1.ServiceThread(countDownLatch, loopCount, serviceInvoker);
      BaseServiceThread serviceThread = captureDetails ? new client.part3.ServiceThread(countDownLatch, loopCount, serviceInvoker,performanceTracker)
          : new client.part1.ServiceThread(countDownLatch, loopCount, serviceInvoker);
      executorService.execute(serviceThread);
    }
    return countDownLatch;
  }

  private CountDownLatch startThreads(ExecutorService executorService, int threadCount, int loopCount,
      ServiceInvoker serviceInvoker, BlockingQueue<String> albumQueue){
    CountDownLatch countDownLatch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      BaseServiceThread serviceThread = new client.part4.ServiceThread(countDownLatch,loopCount,serviceInvoker,performanceTracker,albumQueue);
      executorService.execute(serviceThread);
    }
    return countDownLatch;
  }

  private void startThreads(ExecutorService executorService, int threadCount, ServiceInvoker serviceInvoker,
      AtomicBoolean processData, BlockingQueue<String> albumQueue){
    for(int i=0;i<threadCount;i++){
      ReviewThread reviewThread = new ReviewThread(serviceInvoker,performanceTracker,processData,albumQueue);
      executorService.submit(reviewThread);
    }
  }

  private void stopThreads(int threadCount, AtomicBoolean processData, BlockingQueue<String> albumQueue){
    processData.set(false);
    for(int i=0;i<threadCount;i++){
      albumQueue.add(END_MESSAGE);
    }
  }
}
