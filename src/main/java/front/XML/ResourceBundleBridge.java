package front.XML;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class ResourceBundleBridge extends ResourceBundle {
    public XMLResolver resolver;
    public ArrayList<String> keys;

    public ResourceBundleBridge(XMLResolver resolver) {
        this.resolver = resolver;
        ArrayList<String> keys = new ArrayList<>();
        for (XMLElement e : resolver.content) {
            keys.add(e.name);
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        for (XMLElement e : resolver.content) {
            if (key.equals(e.name)) return e.value;
        }
        return null;
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(keys);
    }

    public ArrayList<String> getXMLKeys() {
        return keys;
    }

    public String get(String key) {
        if (handleGetObject(key) == null) return null;
        return handleGetObject(key).toString();
    }


}
