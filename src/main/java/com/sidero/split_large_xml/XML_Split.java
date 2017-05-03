package com.sidero.split_large_xml;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author thamm
 */

public class XML_Split {
 
    private static final String HEADER_ELEMENT = "Header";
    private static final String FOOTER_ELEMENT = "Footer";    

    private ArrayList<XMLEvent> header = new ArrayList<XMLEvent>();
    private ArrayList<XMLEvent> footer = new ArrayList<XMLEvent>();

    XMLOutputFactory factory = XMLOutputFactory.newInstance();
    XMLEventFactory eventFactory = XMLEventFactory.newInstance();

    //Read through the file until </Header> is reached, save this as the header (ArrayList, called header, of XMLEvents)
    public void readHeader(XMLEventReader xmlEventReader) throws FileNotFoundException, XMLStreamException {
        
        boolean headerFinished = false;
        
        //Iterate through events.
        while (xmlEventReader.hasNext() && headerFinished == false) {
            
            //Get next event.
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
            String line = xmlEvent.toString();

            //flag to exit loop when Header is finished 
            if (xmlEvent.isEndElement() && xmlEvent.asEndElement().toString().contains(HEADER_ELEMENT)) {
                headerFinished = true;
            }
            
            //ignore spaces and line breaks and add The Event to the header 
            if (!line.trim().isEmpty()) {
                header.add(xmlEvent);
            }
        }
    }

    //save Footer from <Footer> until END DOCUMENT (ArrayList, called footer, of XMLEvents)
    public void readFooter(XMLEventReader xmlEventReader) throws FileNotFoundException, XMLStreamException {
        
        //Iterate through events.
        while (xmlEventReader.hasNext()) {

            //Get next event.
            XMLEvent xmlEvent = xmlEventReader.nextEvent();
             
            //check if elemnt is <footer>
            if (xmlEvent.isStartElement() && xmlEvent.asStartElement().toString().contains(FOOTER_ELEMENT)) {

                footer.add(xmlEvent);

                while (xmlEventReader.hasNext()) {
                    
                    //Get next event.
                    XMLEvent xmlEventFooter = xmlEventReader.nextEvent();

                    //ignore spaces and line breaks and add The Event to the header 
                    String line = xmlEventFooter.toString();
                    if (!line.trim().isEmpty()) {
                        footer.add(xmlEventFooter);
                    }
                }
            }
        }
    }

    public void SplitByElement(String fileInputPath, String elementName, String fileOutputPath) throws FileNotFoundException, XMLStreamException, IOException {

        
        //Read XML file.
        Reader fileReader = new FileReader(fileInputPath);

        //Get XMLInputFactory instance.
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        //Create XMLEventReader object.
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(fileReader);

        int fileNumber = 1;       

        while (xmlEventReader.hasNext()) {

            XMLEvent event = xmlEventReader.nextEvent();

            if (event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(elementName)) {

                XMLEventWriter xmlEventWriter = factory.createXMLEventWriter(new FileWriter(fileOutputPath + fileNumber + ".xml"));

                //Add Header
                for (XMLEvent headerElement : header) {
                    xmlEventWriter.add(headerElement);
                }

                //Add Everything between <Data></Data>
                while (!(event.isEndElement() && event.asEndElement().getName().getLocalPart()
                        .equals(elementName))) {
                    
                    xmlEventWriter.add(event);
                    event = xmlEventReader.nextEvent();
                    
                    if(event.isEndElement() && event.asEndElement().getName().getLocalPart()
                        .equals(elementName)){
                        xmlEventWriter.add(event);
                    }
                }
                
                //Add Footer
                for (XMLEvent headerElement : footer) {
                    xmlEventWriter.add(headerElement);
                }
                xmlEventWriter.close();
                fileNumber++;
            }
        }
    }

    public void SplitBySize(String fileInputPath, long byteSize, String fileOutputPath) throws FileNotFoundException, XMLStreamException, IOException {

        String directory = fileInputPath;
        FileReader fileReader = new FileReader(directory);
        BufferedReader Buffreader = new BufferedReader(fileReader);

        int fileNumber = 1;
        int bytes = 0;

        String newFileName = fileOutputPath + fileNumber + ".xml";
        File newFile = new File(newFileName);
        newFile.createNewFile();
        FileWriter fileW = new FileWriter(newFile);
        BufferedWriter buffW = new BufferedWriter(fileW);

        String line = Buffreader.readLine();

        //write first Line and count the bytes of the line 
        if (!line.trim().isEmpty()) {
            buffW.write(line + "\n");
            bytes += line.length();
        }

        //while there is lines in the document write each Line and count the bytes of each line 
        while (line != null) {

            line = Buffreader.readLine();

            if (line != null && !line.trim().isEmpty()) {
                buffW.write(line + "\n");
                bytes += line.length();
            }

            //when specfied byte size is reached flush the content, close current file and open a new file to write to
            if (bytes >= byteSize) {
                buffW.flush();
                fileW.close();
                fileNumber++;
                bytes = 0;
                fileW = new FileWriter(fileOutputPath + fileNumber + ".xml");
                buffW = new BufferedWriter(fileW);
            }
            
            if (line == null) {
                buffW.flush();
                fileW.close();
            }
        }
    }
}
