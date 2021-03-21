/*
 * 2021 Dmitrij Kobilin.
 *
 * Nenia rajtigilo ekzistas.
 * Faru bone, ne faru malbone.
 */
package org.dkoby;

import java.util.ArrayList;

public class GpxData {
    private static final int INIT_BUF_SIZE = 1024;
    public ArrayList<GpxPoint> points;
    private long firstPointTime;
    private int currentIndex;

    public GpxData() {
        points = new ArrayList<GpxPoint>(INIT_BUF_SIZE);
        currentIndex = 0;
    }
    /**
     *
     */
    public void addPoint(GpxPoint point) {
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
    public class GpxPoint {
        public double latitude; /* degrees. */
        public double longitude; /* degrees. */
        public double altitude; /* meters. */
        public int speed; /* km/h. */
        public long time; /* ms */
    } 
}

