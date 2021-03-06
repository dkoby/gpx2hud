/*
 * 2021 Dmitrij Kobilin.
 *
 * Nenia rajtigilo ekzistas.
 * Faru bone, ne faru malbone.
 */
package org.dkoby;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.FontMetrics;

import java.util.Date;
import java.text.SimpleDateFormat;

public class Render {
    private Config config;
    private Graphics2D graphics;
    private GpxData data;

    private BufferedImage image;
    private byte[] dataBuffer;
    private int[] dataBufferInt;

    public Render(Config config, GpxData data) {
        this.config = config;
        this.data   = data;

        image = new BufferedImage(
            config.videoWidth, config.videoHeight,
            BufferedImage.TYPE_4BYTE_ABGR
        ); 

        graphics = image.createGraphics();

        dataBuffer    = new byte[config.videoWidth * config.videoHeight * 4];
        dataBufferInt = new int[config.videoWidth * config.videoHeight * 4];
    }
    /**
     *
     */
    private byte[] drawFrame(GpxData.GpxPoint point) {
        Graphics2D g = graphics;


        g.setColor(Color.BLUE);
        g.fillRect(0, 0, config.videoWidth, config.videoHeight);

        g.setFont(config.font);
        g.setPaint(Color.WHITE);

        Date date = new Date(point.time * 1000);

        FontMetrics metrics = g.getFontMetrics(config.font);

        int x = 0;
        int y = 0;

        x += 4;
        y += metrics.getHeight();

        g.drawString(String.format(
                    "Скорость : %d км/ч", point.speedDesc), x, y);
        y += metrics.getHeight();
        if (false)
        {
            g.drawString(String.format(
                        "Время    : %s"     , new SimpleDateFormat("HH:mm:ss").format(date)), x, y);
            y += metrics.getHeight();
        }
        g.drawString(String.format(
                    "Высота   : %d м"   , Math.round(point.altitude)), x, y);
        y += metrics.getHeight();
        g.drawString(String.format(
                    "Plus Code: %s", point.plusCode), x, y);
        y += metrics.getHeight();

        return getData();
    }
    /**
     *
     */
    /**
     *
     */
    private byte[] getData() {
        image.getData().getPixels(0, 0, image.getWidth(), image.getHeight(), dataBufferInt);

        for (int i = 0; i < config.videoWidth * config.videoHeight * 4; i++)
            dataBuffer[i] = (byte)dataBufferInt[i];

        return dataBuffer;
    }
    /**
     *
     */
    public void run() {

        System.err.println("Run render...");

        if (this.data.points.size() == 0)
        {
            System.err.println("No points to render.");
            System.exit(1);
        }

        long time = 0;
        GpxData.GpxPoint point;
        while ((point = data.getPoint(time)) != null)
        {
            for (int frame = 0; frame < this.config.fps; frame++)
            {
                byte[] buf = this.drawFrame(point);
                System.out.write(buf, 0, buf.length);
                System.out.flush();
//                System.err.format("Point %d %d%n", point.time, time);
            }
            time++;
        }
    }
}

