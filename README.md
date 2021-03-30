
# About

gpx2hud can be used for generating video stream from GPX file.
For example show speed, time, elevation, coordinates, etc.

# Build

To build gpx2hud you need JDK and Apache Ant.
Just run command "ant" in console.

$ ant

Resulting jar-file will be in "build" directory (or directory, provided by
"BUILD\_DIR" environment variable).

# Usage

### Play resulting video with ffplay

$ java -jar gpx2hud.jar input.gpx | ffplay -f rawvideo -vcodec rawvideo -video\_size 250x64 -pixel\_format rgba -framerate 2 -i -

