/*
 * 2021 Dmitrij Kobilin.
 *
 * Nenia rajtigilo ekzistas.
 * Faru bone, ne faru malbone.
 */
package org.dkoby;
/* */
import java.awt.Color;
import java.awt.Font;

public class Config {
    public int videoWidth   = 320;
    public int videoHeight  = 240;
    public String inputGpx;
    public int fps          = 2;
    public Font font        = new Font(Font.MONOSPACED, Font.PLAIN, 8);

    /**
     *
     */
    public void print()
    {
        System.err.println("inputGpx    : " + inputGpx);
        System.err.println("videoWith   : " + videoWidth);
        System.err.println("videoHeight : " + videoHeight);
        System.err.println("fps         : " + fps);
        System.err.println("font        : " + font.getName());
    }
}

