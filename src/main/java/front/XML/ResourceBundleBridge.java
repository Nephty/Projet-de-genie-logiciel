package front.XML;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ResourceBundleBridge extends ResourceBundle {
    public XMLResolver resolver;

    public ResourceBundleBridge(XMLResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    protected Object handleGetObject(String key) {  // TODO : use the XMLResolver to find all the color values and create the if statements (or switch)
        if (key.equals("test")) return "TEST";
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keySet());
    }
}
