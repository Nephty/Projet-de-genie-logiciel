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

public class XMLResolver {
    private ObjectMapper mapper;
    private InputStream inputStream;
    public List<XMLElement> content;

    public XMLResolver(String dir) throws IOException {
        mapper = new XmlMapper();
        inputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/xml/" + dir);
        content = mapper.readValue(inputStream, new TypeReference<>() {});
    }
}
