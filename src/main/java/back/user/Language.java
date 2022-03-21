package back.user;

/**
 * This class represent a language
 */
public class Language {
    public String name;

    public Language(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
