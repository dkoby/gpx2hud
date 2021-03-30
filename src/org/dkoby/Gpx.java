/*
 * 2021 Dmitrij Kobilin.
 *
 * Nenia rajtigilo ekzistas.
 * Faru bone, ne faru malbone.
 */
package org.dkoby;

import java.io.File;
import java.io.FileInputStream;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class Gpx {
    private GpxData data;
    private GpxData.GpxPoint point;

    /**
     *
     */
    public Gpx (String path) {
        data = new GpxData();

        File file = new File(path);

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(path));
            readXML(reader, null);
        } catch (Exception e) {
            System.err.println("(E) Failed to read XML (" + path + "), " + e);
            System.exit(1);
        }
    }
    /*
     *
     */
    public GpxData getData() {
        return data;
    }
    /**
     *
     */
    private void readXML(XMLEventReader reader, String parent) throws XMLStreamException {
        while(reader.hasNext()) {
            XMLEvent event = reader.nextEvent();

            if (event.isStartElement()) {
                StartElement element = event.asStartElement();
                String elementName = element.getName().getLocalPart();
                String name = (parent == null) ? elementName : parent + "." + elementName;

                processXMLEvent(event, parent);
                readXML(reader, name);
            }
            if (event.isCharacters())
            {
                processXMLEvent(event, parent);
            }
            if (event.isEndElement()) {
                processXMLEvent(event, parent);
                break;
            }
        }
    }
    /**
     *
     */
    private void processXMLEvent(XMLEvent event, String parent) throws XMLStreamException {
        /* NOTE */
        if (parent == null)
            return;

        if (event.isStartElement()) {
            StartElement element = event.asStartElement();
            String elementName = element.getName().getLocalPart();

            if (parent.equals("gpx.trk.trkseg") && elementName.equals("trkpt")) {
                point = data.new GpxPoint();

                for (SimpleAttr attr: new SimpleAttrIterator(element)) {
                    if (attr.name.equals("lat")) {
                        try {
                            point.latitude = Double.parseDouble(attr.value);
                        } catch (Exception e) {
                            throw new XMLStreamException("Invalid value of latitude: " + e, element.getLocation());
                        }
                    }
                    if (attr.name.equals("lon")) {
                        try {
                            point.longitude = Double.parseDouble(attr.value);
                        } catch (Exception e) {
                            throw new XMLStreamException("Invalid value of latitude: " + e, element.getLocation());
                        }
                    }
                }
            }
        } else if (event.isCharacters()) {
            Characters element = event.asCharacters();
            if (parent.equals("gpx.trk.trkseg.trkpt.ele")) {
                if (point != null)
                {
                    try {
                        point.altitude = Double.parseDouble(element.getData().trim());
                    } catch (Exception e) {
                        throw new XMLStreamException("Invalid value of elevation: " + e, element.getLocation());
                    }
                } else {
                    throw new XMLStreamException("Point is null.", element.getLocation());
                }
            } else if (parent.equals("gpx.trk.trkseg.trkpt.desc")) {
                if (point != null)
                {
                    Pattern pattern = Pattern.compile("speed\\s+(\\d+),(\\d+)\\s+km/h");
                    Matcher matcher = pattern.matcher(element.getData().trim());
                    if (matcher.find()) {
                        if (matcher.start(1) >= 0 && matcher.start(2) >= 0) {
                            try {
                                point.speedDesc = Integer.decode(matcher.group(1));
                            } catch (Exception e) {
                                throw new XMLStreamException("Invalid value of speed: " + e, element.getLocation());
                            }
                        }
                    }
                } else {
                    throw new XMLStreamException("Point is null.", element.getLocation());
                }
            } else if (parent.equals("gpx.trk.trkseg.trkpt.time")) {
                if (point != null)
                {
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                    OffsetDateTime time = OffsetDateTime.parse(element.getData().trim(), formatter);
                    point.time = time.toEpochSecond();
                } else {
                    throw new XMLStreamException("Point is null.", element.getLocation());
                }
            }
        } else if (event.isEndElement()) {
            EndElement element = event.asEndElement();
            String elementName = element.getName().getLocalPart();
            if (parent.equals("gpx.trk.trkseg.trkpt") && elementName.equals("trkpt")) {

                if (point != null)
                {
                    data.addPoint(point);
                    point = null;
                }
            }
        }
    }

    /**
     *
     */
    private class SimpleAttr {
        public String name;
        public String value;
    }
    /**
     *
     */
    private class SimpleAttrIterator implements Iterable<SimpleAttr> {
        private Iterator<Attribute> attrs;

        public SimpleAttrIterator(StartElement element) {
            @SuppressWarnings("unchecked")
            Iterator<Attribute> iterator = element.getAttributes();
            attrs = iterator;
        }
        public Iterator<SimpleAttr> iterator() {
            return new Iterator<SimpleAttr>() {
                @Override
                public boolean hasNext() {
                    return attrs.hasNext();
                }
                @Override
                public SimpleAttr next() {
                    Attribute attr = attrs.next();
                    SimpleAttr sAttr = new SimpleAttr();

                    sAttr.name  = attr.getName().toString();
                    sAttr.value = attr.getValue();

                    return sAttr;
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }


}

