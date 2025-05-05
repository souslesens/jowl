package com.souslesens.Jowl.services;



/*import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;*/
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;


@Service
public class PlantUmlServiceImpl {

    public  String toSVG(String str) throws Exception{
        String source = "@startuml\n";
        source += "Bob -> Alice : hello\n";
        source += "@enduml\n";

  /*      SourceStringReader reader = new SourceStringReader(source);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
// Write the first image to "os"o

        String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

// The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));*/
        String svg="";
        return svg;
    }


}
