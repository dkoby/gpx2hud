/*
 * 2021 Dmitrij Kobilin.
 *
 * Nenia rajtigilo ekzistas.
 * Faru bone, ne faru malbone.
 */
package org.dkoby;

import java.util.ArrayList;
import java.util.LinkedList;

public class GpxData {
    private static final int INIT_BUF_SIZE = 1024;
    private static final int FIFO_SIZE     = 8;


    private static final String[]   plusEnc0 = {"2", "3", "4", "5", "6", "7", "8", "9", "C", "F", "G", "H", "J", "M", "P", "Q", "R", "V", "W", "X"};
    private static final String[][] plusEnc1 = {
        {"2", "3", "4", "5"},
        {"6", "7", "8", "9"},
        {"C", "F", "G", "H"},
        {"J", "M", "P", "Q"},
        {"R", "V", "W", "X"},
    };

    public ArrayList<GpxPoint> points;
    public LinkedList<GpxPoint> speedFIFO;
    private long firstPointTime;
    private int currentIndex;

    public GpxData() {
        points = new ArrayList<GpxPoint>(INIT_BUF_SIZE);
        speedFIFO = new LinkedList<GpxPoint>();
        currentIndex = 0;
    }
    /**
     *
     */
    public void addPoint(GpxPoint point) {
        speedFIFO.add(point);
        if (speedFIFO.size() > FIFO_SIZE)
            speedFIFO.remove();

        point.speedAvg = speedAvg();
        point.plusCode = plusCode(point);
        if (points.size() == 0)
        {
            firstPointTime = point.time;
        }
        points.add(point);

    }
    /**
     *
     */
    public GpxPoint getPoint(long time) {
        if (points.size() == 0)
            return null;
        for (int i = currentIndex; i < points.size(); i++)
        {
            GpxPoint point = points.get(i);
            if ((point.time - firstPointTime) >= time)
            {
                currentIndex = i;
                return point;
            }
        }
        return null;
    }
    /**
     *
     */
    private double deg2rad(double val) {
        return val * Math.PI / 180;
    }
    /**
     * Return distance in meters.
     */
    private double distance(double lat0, double lon0, double lat1, double lon1) {
        double result;
 
        lat0 = deg2rad(lat0);
        lat1 = deg2rad(lat1);
        lon0 = deg2rad(lon0);
        lon1 = deg2rad(lon1);

        double dlon = lon1 - lon0;
        double dlat = lat1 - lat0;

        result = 2 * Math.asin(
                Math.sqrt(
                    Math.pow(Math.sin(dlat / 2), 2) +
                    Math.pow(Math.sin(dlon / 2), 2) * Math.cos(lat0) * Math.cos(lat1)
                )
            );

        double R = 6371; /* Km. */

        return result * R * 1000;
    }
    /**
     * Calculate average speed.
     */
    private int speedAvg() {
        if (speedFIFO.size() == 0)
            return 0;

        double dist = 0;
        for (int i = 1; i < speedFIFO.size(); i++)
        {
            GpxPoint point0 = speedFIFO.get(i - 1);
            GpxPoint point1 = speedFIFO.get(i);

            dist += distance(
                    point0.latitude, point0.longitude,
                    point1.latitude, point1.longitude);
        }
        return (int)Math.round(dist / 1000 / ((double)(speedFIFO.peekLast().time - speedFIFO.peekFirst().time) / 3600));
    }
    /**
     *
     */
    private String plusCode(GpxPoint point)
    {
        StringBuilder code = new StringBuilder(12);

        double lat  = point.latitude  + 90;
        double lon  = point.longitude + 180;

        int vlat = 0;
        int vlon = 0;
        double plat = 20;
        double plon = 20;

        for (int i = 1; i <= 6; i++)
        {
            vlat = (int)Math.floor(lat / plat);
            vlon = (int)Math.floor(lon / plon);

            if (i < 6) {
                code.append(plusEnc0[vlat]);
                code.append(plusEnc0[vlon]);
            } else {
                code.append(plusEnc1[vlat][vlon]);
            }

           lat = lat - vlat * plat;
           lon = lon - vlon * plon;

           if (i == 4) {
               code.append("+");
           }

           if (i < 5) {
               plat /= 20.0;
               plon /= 20.0;
           } else {
               plat /= 5.0;
               plon /= 4.0;
           }
        }

        return code.toString();
    }

    /**
     *
     */
    public class GpxPoint {
        public double latitude; /* degrees. */
        public double longitude; /* degrees. */
        public double altitude; /* meters. */
        public int speedDesc; /* km/h. */
        public int speedAvg;  /* km/h. */
        public long time; /* sec. */
        public String plusCode;
    } 
}

