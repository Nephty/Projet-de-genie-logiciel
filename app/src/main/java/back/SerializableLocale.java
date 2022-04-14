package back;

import java.io.Serializable;
import java.util.Locale;

public class SerializableLocale implements Serializable {
    private final Locale locale;

    public SerializableLocale(Locale locale) {
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }
}
