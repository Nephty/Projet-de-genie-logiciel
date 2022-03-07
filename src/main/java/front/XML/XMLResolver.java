package front.XML;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * XML reader class that can hold the content of an XML file as a <code>List<XMLElement></code>.
 */
public class XMLResolver {
    private ObjectMapper mapper;
    private InputStream inputStream;
    public List<XMLElement> content;

    /**
     * Reads the XML file corresponding to the given filename and fills in the content variable using the content
     * of the XML file. WARNING : this uses System.getProperty("user.dir") + "/src/main/resources/xml/" + filename
     * This could potentially lead to path error, and there probably is a better way around.
     * @param filename - <code>String</code> - the name of the XML file
     * @throws IOException if the file cannot be found
     */
    public XMLResolver(String filename) throws IOException {
        mapper = new XmlMapper();
        inputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/xml/" + filename);
        content = mapper.readValue(inputStream, new TypeReference<>() {});
    }
}
