package com.sidero.split_large_xml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author thamm
 */
public class Main {
    
    public static void main (String[] args) throws FileNotFoundException, XMLStreamException, Exception{
        
        
        XML_Split splitter = new XML_Split();
        
        //File Input Path
        String fileInputPath = args[1]; 
        
        //File Output Path
        String fileOutputPath = args[3]; 
                                        
        //Read XML file.
        Reader fileReader = new FileReader(fileInputPath);

        //Get XMLInputFactory instance.
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

        //Create XMLEventReader object.
        XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(fileReader);
        
        
        if(args[0].equals("SplitByElement") ){
        //Element To Split by
        String elementName = args[2];
        splitter.readHeader(xmlEventReader);
        splitter.readFooter(xmlEventReader);
        splitter.SplitByElement(fileInputPath, elementName, fileOutputPath);
        }
        else if(args[0].equals("SplitBySize")){ 
        //Size of Each split File 
        long byteSize = Long.parseLong(args[2]);
        splitter.SplitBySize(fileInputPath, byteSize, fileOutputPath);
        }
        
    }
    
}
