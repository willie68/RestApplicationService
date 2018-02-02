/**
 * 
 */
package de.mcs.microservice.application.api;

import java.util.ArrayList;
import java.util.List;

import de.mcs.jmeasurement.DefaultMeasurePoint;
import de.mcs.jmeasurement.MeasurePoint;
import de.mcs.jmeasurement.MeasurePoint.PRIORITY;
import de.mcs.jmeasurement.exception.InvalidMeasureDataTypeException;

/**
 * @author w.klaas
 */
public class MeasureDataFactory {

  /**
   * @param measurePoints
   *          The points to convert.
   * @return The transformed list
   */
  public static List<MeasureData> transform(MeasurePoint[] measurePoints) {
    List<MeasureData> list = new ArrayList<MeasureData>();
    for (MeasurePoint measurePoint : measurePoints) {
      list.add(transform(measurePoint));
    }
    return list;
  }

  private static MeasureData transform(MeasurePoint point) {
    if (point != null) {
      MeasureData data = new MeasureData();
      data.setName(point.getName());
      de.mcs.jmeasurement.MeasureData[] data2 = point.getData();
      for (de.mcs.jmeasurement.MeasureData measureData : data2) {
        try {
          if (DefaultMeasurePoint.DATA_KEY_ACCESS_COUNT.equals(measureData.getName())) {
            data.setAccessCount(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_ACTIVE.equals(measureData.getName())) {
            data.setActive(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_AVERAGE_MSEC.equals(measureData.getName())) {
            data.setAverageMSec(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_DEATH_COUNT.equals(measureData.getName())) {
            data.setDeathCount(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_DEVIATION.equals(measureData.getName())) {
            data.setDeviation((Float) measureData.getValue());
          }
          if (DefaultMeasurePoint.DATA_KEY_EXCEPTION_COUNT.equals(measureData.getName())) {
            data.setExceptionCount(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_EXCEPTION_LIST.equals(measureData.getName())) {
            data.setExceptionList(measureData.getAsString());
          }
          if (DefaultMeasurePoint.DATA_KEY_LAST_ACTIVATION.equals(measureData.getName())) {
            data.setLastActivation(measureData.getAsDate().getTime());
          }
          if (DefaultMeasurePoint.DATA_KEY_MAX_ACTIVE.equals(measureData.getName())) {
            data.setMaxActive(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_MAX_MSEC.equals(measureData.getName())) {
            data.setMaxMSec(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_MIN_MSEC.equals(measureData.getName())) {
            data.setMinMSec(measureData.getAsLong());
          }
          if (DefaultMeasurePoint.DATA_KEY_PRIORITY.equals(measureData.getName())) {
            data.setPriority(PRIORITY.valueOf(measureData.getAsString()).ordinal());
          }
          if (DefaultMeasurePoint.DATA_KEY_TOTAL_MSEC.equals(measureData.getName())) {
            data.setTotalMSec(measureData.getAsLong());
          }
        } catch (InvalidMeasureDataTypeException e) {
          e.printStackTrace();
        }
      }
      return data;
    }
    return null;
  }
}
