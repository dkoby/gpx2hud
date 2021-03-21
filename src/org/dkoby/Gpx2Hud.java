/*
 * 2021 Dmitrij Kobilin.
 *
 * Nenia rajtigilo ekzistas.
 * Faru bone, ne faru malbone.
 */
package org.dkoby;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 *
 */
public class Gpx2Hud {
    /**
     *
     */
    private static void printHelp() {
        System.out.println("gpx2hud. Auxtoro estas Dmitrij Kobilin.");
        System.out.println("");
        System.out.println("Usage: gpx2hud <OPTIONS> [GPX FILE]");
        System.out.println("");
        System.out.println("<OPTIONS> are:");
        System.out.println("    -h                             Show this help.");
        System.out.println("    --geometry=<WIDTH>x<HEIGHT>    Geometry of output video.");
        System.exit(1);
    }
    
    /**
     *
     */
    public static void main(String[] args) {
        Config config =  new Config();

        if (args.length < 1)
            printHelp();

        /* Parse Args. */
        for (String arg: args) {
            if (arg.equals("-h")) {
                printHelp();
            }
            Pattern pattern = Pattern.compile("^--geometry=(\\d+)x(\\d+)");
            Matcher matcher = pattern.matcher(arg);
            if (matcher.find()) {
                if (matcher.start(1) >= 0 && matcher.start(2) >= 0) {
                    int width  = Integer.decode(matcher.group(1));
                    int height = Integer.decode(matcher.group(2));

                    config.videoWidth  = width;
                    config.videoHeight = height;
                    continue;
                }
            }

            /* Last option is input file. */
            if (arg == args[args.length -1 ] && arg.substring(0, 1) != "-")
            {
                config.inputGpx = arg;
                continue; /* Or break. */
            }

            System.err.println("Unknown command line option \"" + arg + "\"");
            System.exit(1);
        }

        if (config.inputGpx == null)
        {
            System.err.println("No input GPX file specified.");
            System.exit(1);
        }

        config.print();

        System.err.println("Reading GPX data...");
        Gpx gpx = new Gpx(config.inputGpx);
        GpxData data = gpx.getData();
        System.err.format("GPX data is ready. Points %d.%n", data.points.size());

        new Render(config, gpx.getData()).run();
        System.err.println("Done.");

    }
}

