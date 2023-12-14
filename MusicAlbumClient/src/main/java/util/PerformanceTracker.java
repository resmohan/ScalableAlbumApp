package util;

import static util.Constants.LOOP_COUNT;
import static util.Constants.REQUEST_COUNT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.LongSummaryStatistics;

public class PerformanceTracker {

  private long totalTime;
  private long totalReviewTime;
  private boolean captureDetails;
  private List<ResponseLatency> responseLatencyList;

  public PerformanceTracker(boolean captureDetails) {
    this.captureDetails = captureDetails;
    responseLatencyList = Collections.synchronizedList(new ArrayList<>());
  }

  public void setTotalTime(long totalTime) {
    this.totalTime = totalTime;
  }

  public void setTotalReviewTime(long totalReviewTime) {
    this.totalReviewTime = totalReviewTime;
  }

  public boolean isCaptureDetails() {
    return captureDetails;
  }

  public void addLatencyInfo(RequestType requestType, long startTime, long endTime, int responseCode){
    responseLatencyList.add(new ResponseLatency(startTime,requestType,(endTime-startTime),responseCode));
  }

  public void printPerformanceDetails(int threadCount){
    long totalSeconds = totalTime/1000;
    System.out.println("\nWall Time: "+totalSeconds+" seconds");
    System.out.println("Throughput: "+(threadCount*LOOP_COUNT*REQUEST_COUNT)/(float)totalSeconds);
  }

  public void printPerformanceDetails(){
    long totalSeconds = totalTime/1000;
    System.out.println("\nWall Time: "+totalSeconds+" seconds");
    System.out.println("Throughput: "+(responseLatencyList.size())/(float)totalSeconds);
  }

  public void printReviewPerformanceDetails(){
    long totalSeconds = totalReviewTime/1000;
    long count = responseLatencyList
        .stream()
        .filter((respLatency) -> respLatency.getRequestType().equals(RequestType.GET_REVIEW))
        .count();
    System.out.println("\nWall Time for Review: "+totalSeconds+" seconds");
    System.out.println("Throughput for Review: "+count/(float)totalSeconds);
  }
  public void printLatencyDetails(RequestType requestType){
    List<Long> responseLatencies = responseLatencyList
                                    .stream()
                                    .filter((respLatency) -> respLatency.getRequestType().equals(requestType))
                                    .mapToLong((respLatency)-> respLatency.getLatency())
                                    .sorted()
                                    .boxed()
                                    .toList();
    LongSummaryStatistics requestStatistics = responseLatencies
                                    .stream()
                                    .mapToLong((x)->x)
                                    .summaryStatistics();

    long listSize = responseLatencies.size();
    double median = listSize%2 != 0 ? responseLatencies.stream().skip(listSize/2).findFirst().get()
        : responseLatencies.stream().skip(listSize/2 - 1).limit(2)
            .mapToLong((x)->x).average().getAsDouble();
    long percentile = responseLatencies.get((int)Math.floor(0.99*listSize));
    System.out.println("\nMean response time for "+requestType+" request in ms: "+requestStatistics.getAverage());
    System.out.println("Median response time for "+requestType+" request in ms: "+median);
    System.out.println("99th percentile response time for "+requestType+" request in ms: "+percentile);
    System.out.println("Minimum response time for "+requestType+" request in ms: "+requestStatistics.getMin());
    System.out.println("Maximum response time for "+requestType+" request in ms: "+requestStatistics.getMax());
  }

  public void printCountDetails(){
    long totalCount = responseLatencyList.size();
    long failedCount = responseLatencyList
                        .stream()
                        .filter((responseLatency -> responseLatency.getResponseCode() != 200))
                        .count();
    System.out.println("\nNumber of successful requests: "+(totalCount-failedCount));
    System.out.println("Number of failed requests: "+failedCount);
  }

  public void printCountDetails(List<RequestType> requestTypeList){
    List<ResponseLatency> latencyList = responseLatencyList.stream()
        .filter((respLatency) -> requestTypeList.contains(respLatency.getRequestType()))
        .toList();
    long totalCount = latencyList.stream().count();
    long failedCount = latencyList.stream()
        .filter((responseLatency -> responseLatency.getResponseCode() != 200))
        .count();
    System.out.println("\nNumber of successful requests: "+(totalCount-failedCount));
    System.out.println("Number of failed requests: "+failedCount);
  }

  public List<Integer> getGraphData(){
    List<Integer> requestCountList = new ArrayList<>();
    List<Long> startTimes = responseLatencyList
                              .stream()
                              .mapToLong((respLatency) -> respLatency.getStartTime())
                              .sorted()
                              .boxed()
                              .toList();
    int listSize = startTimes.size();
    long startTime = startTimes.get(0);
    long endTime = startTimes.get(listSize-1);
    int prevPos = 0;
    while(endTime >= startTime){
      int position = getPosition(prevPos,listSize-1,startTime + 1000,startTimes);
      int count = prevPos == 0 ? (position - prevPos + 1) : (position - prevPos);
      requestCountList.add(count);
      prevPos = position;
      startTime += 1000;
    }
    return requestCountList;
  }
  private int getPosition(int low, int high, long value, List<Long> startTimes){
    while(low <= high){
      int mid = (low+high)/2;
      long midVal = startTimes.get(mid);
      if(midVal == value){
        return mid;
      }
      else if(midVal > value){
        high = mid - 1;
      }
      else{
        low = mid + 1;
      }
    }
    return high;
  }

  public List<ResponseLatency> getResponseLatencyList() {
    return responseLatencyList;
  }
}
