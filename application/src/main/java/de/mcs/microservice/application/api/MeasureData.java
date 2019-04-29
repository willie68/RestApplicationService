/**
 * 
 */
package de.mcs.microservice.application.api;

/**
 * JSON compatible data class for jmeasurement.
 * 
 * @author w.klaas
 *
 */
public class MeasureData {
  private String name;
  private int priority;
  private long accessCount;
  private long averageMSec;
  private long totalMSec;
  private long minMSec;
  private long maxMSec;
  private long active;
  private long maxActive;
  private long deathCount;
  private long lastActivation;
  private double deviation;
  private double squaresum;
  private long exceptionCount;
  private String exceptionList;

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name
   *          the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the priority
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @param priority
   *          the priority to set
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * @return the accessCount
   */
  public long getAccessCount() {
    return accessCount;
  }

  /**
   * @param accessCount
   *          the accessCount to set
   */
  public void setAccessCount(long accessCount) {
    this.accessCount = accessCount;
  }

  /**
   * @return the averageMSec
   */
  public long getAverageMSec() {
    return averageMSec;
  }

  /**
   * @param averageMSec
   *          the averageMSec to set
   */
  public void setAverageMSec(long averageMSec) {
    this.averageMSec = averageMSec;
  }

  /**
   * @return the totalMSec
   */
  public long getTotalMSec() {
    return totalMSec;
  }

  /**
   * @param totalMSec
   *          the totalMSec to set
   */
  public void setTotalMSec(long totalMSec) {
    this.totalMSec = totalMSec;
  }

  /**
   * @return the minMSec
   */
  public long getMinMSec() {
    return minMSec;
  }

  /**
   * @param minMSec
   *          the minMSec to set
   */
  public void setMinMSec(long minMSec) {
    this.minMSec = minMSec;
  }

  /**
   * @return the maxMSec
   */
  public long getMaxMSec() {
    return maxMSec;
  }

  /**
   * @param maxMSec
   *          the maxMSec to set
   */
  public void setMaxMSec(long maxMSec) {
    this.maxMSec = maxMSec;
  }

  /**
   * @return the active
   */
  public long getActive() {
    return active;
  }

  /**
   * @param active
   *          the active to set
   */
  public void setActive(long active) {
    this.active = active;
  }

  /**
   * @return the maxActive
   */
  public long getMaxActive() {
    return maxActive;
  }

  /**
   * @param maxActive
   *          the maxActive to set
   */
  public void setMaxActive(long maxActive) {
    this.maxActive = maxActive;
  }

  /**
   * @return the deathCount
   */
  public long getDeathCount() {
    return deathCount;
  }

  /**
   * @param deathCount
   *          the deathCount to set
   */
  public void setDeathCount(long deathCount) {
    this.deathCount = deathCount;
  }

  /**
   * @return the lastActivation
   */
  public long getLastActivation() {
    return lastActivation;
  }

  /**
   * @param lastActivation
   *          the lastActivation to set
   */
  public void setLastActivation(long lastActivation) {
    this.lastActivation = lastActivation;
  }

  /**
   * @return the deviation
   */
  public double getDeviation() {
    return deviation;
  }

  /**
   * @param deviation
   *          the deviation to set
   */
  public void setDeviation(double deviation) {
    this.deviation = deviation;
  }

  /**
   * @return the squaresum
   */
  public double getSquaresum() {
    return squaresum;
  }

  /**
   * @param squaresum
   *          the squaresum to set
   */
  public void setSquaresum(double squaresum) {
    this.squaresum = squaresum;
  }

  /**
   * @return the exceptionCount
   */
  public long getExceptionCount() {
    return exceptionCount;
  }

  /**
   * @param exceptionCount
   *          the exceptionCount to set
   */
  public void setExceptionCount(long exceptionCount) {
    this.exceptionCount = exceptionCount;
  }

  /**
   * @return the exceptionList
   */
  public String getExceptionList() {
    return exceptionList;
  }

  /**
   * @param exceptionList
   *          the exceptionList to set
   */
  public void setExceptionList(String exceptionList) {
    this.exceptionList = exceptionList;
  }

}
