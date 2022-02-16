package front.XML;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class XMLResolver<T> {
    private ObjectMapper mapper;
    private InputStream inputStream;
    private TypeReference<List<T>> typeReference;
    public List<T> content;

    public XMLResolver(String dir) throws IOException {
        mapper = new XmlMapper();
        inputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/xml/" + dir);
        typeReference = new TypeReference<>() {
        };
        content = mapper.readValue(inputStream, typeReference);
        for (T hm : content) {
            System.out.println(hm);
        }
    }
}
