package recursos;

import java.util.Locale;
import java.util.ResourceBundle;

public class Internacionalizacion {
    private static ResourceBundle mensajes;

    public static void setIdioma(String idioma) {
        try {
            Locale locale = switch (idioma) {
                case "EN" -> new Locale("en", "US");
                case "FR" -> new Locale("fr", "FR");
                default -> new Locale("es", "ES"); 
            };
            mensajes = ResourceBundle.getBundle("recursos.messages", locale);
        } catch (Exception e) {
            System.err.println("Error cargando archivos de idioma: " + e.getMessage());
            mensajes = ResourceBundle.getBundle("recursos.messages", new Locale("es", "ES")); // Español por defecto
        }
    }

    public static String getTexto(String clave) {
        return mensajes.getString(clave);
    }
}