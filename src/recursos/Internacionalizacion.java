package recursos;

import java.util.Locale;
import java.util.ResourceBundle;

public class Internacionalizacion {
    private static ResourceBundle mensajes; // Almacena los textos traducidos segun el idioma seleccionado

    // Metodo para establecer el idioma de la aplicacion
    public static void setIdioma(String idioma) {
        try {
        	// Asignar la configuración de idioma segun el codigo recibido
            Locale locale = switch (idioma) {
                case "EN" -> new Locale("en", "US");
                case "FR" -> new Locale("fr", "FR");
                default -> new Locale("es", "ES"); // Español es el idioma predeterminado
            };
            mensajes = ResourceBundle.getBundle("recursos.messages", locale); // Cargar el archivo de traducciones
        } catch (Exception e) {
            System.err.println("Error cargando archivos de idioma: " + e.getMessage());
            mensajes = ResourceBundle.getBundle("recursos.messages", new Locale("es", "ES")); // Español por defecto
        }
    }

    // Metodo para obtener el texto traducido segun la clave proporcionada
    public static String getTexto(String clave) {
        return mensajes.getString(clave); // Retorna el texto correspondiente al idioma activo
    }
}