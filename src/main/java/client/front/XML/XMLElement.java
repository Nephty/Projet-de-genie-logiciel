package client.front.XML;

/**
 * Object with a structure name-value that can be used to retrieve values from an XML file.
 */
public class XMLElement {
    public String name;
    public String value;

    @Override
    public String toString() {
        return "Name : " + name + ", Value : " + value;
    }
}
