package modelo;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {
	
	public static final Object bloqueoModificacion = new Object(); // 🔹 Definir el objeto de bloqueo
	
	// Declaración de atributos privados de la clase "personaDAO"
	private File archivo; // Archivo donde se almacenarán los datos de los contactos
	private persona persona; // Objeto "persona" que se gestionará
	
	// Constructor público de la clase "personaDAO" que recibe un objeto "persona" como parámetro
	public personaDAO(persona persona) {
		this.persona = persona; // Asigna el objeto "persona" recibido al atributo de la clase
		archivo = new File("c:/gestionContactos"); // Establece la ruta donde se alojará el archivo
		// Llama al método para preparar el archivo
		prepararArchivo();
	}
	
	// Método privado para gestionar el archivo utilizando la clase File
	private void prepararArchivo() {
		// Verifica si el directorio existe
		if (!archivo.exists()) { // Si el directorio no existe, se crea
			archivo.mkdir();
		}
		
		// Accede al archivo "datosContactos.csv" dentro del directorio especificado
		archivo = new File(archivo.getAbsolutePath(), "datosContactos.csv");
		// Verifica si el archivo existe
		if (!archivo.exists()) { // Si el archivo no existe, se crea
			try {
				archivo.createNewFile();
				//Prepara el encabezado para el archivo de csv
				String encabezado=String.format("%s;%s;%s;%s;%s", "NOMBRE", "TELEFONO", "EMAIL", "CATEGORIA","FAVORITO");
				//persona.datosContacto(encabezado);
				escribir(encabezado);
			} catch (IOException e) {
				e.printStackTrace(); // Maneja la excepción de entrada/salida
			}
		}
	}
	
	private void escribir(String texto){
	    // Crea un objeto FileWriter para escribir en el archivo de contactos  
		FileWriter escribir;
		try {
			escribir = new FileWriter(archivo.getAbsolutePath(), true);
			escribir.write(texto + "\n"); // Escribe los datos del contacto en el archivo
			
			escribir.close(); // Cierra el archivo para guardar los cambios correctamente
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Método público para escribir un contacto nuevo en el archivo
	public boolean escribirArchivo() {
	    try {
	        // Verifica si el contacto existe antes de escribir
	        if (existeContacto(persona.getNombre(), persona.getTelefono(), persona.getEmail(), persona.getCategoria())) {
	            System.out.println("Contacto duplicado detectado. No se guardará.");
	            return false; // Si el contacto ya existe no se escribe
	        }

	     // Abre el archivo en modo "append" para agregar nuevos contactos
	        FileWriter escribir = new FileWriter(archivo.getAbsolutePath(), true);
	        escribir.write(persona.datosContacto() + System.lineSeparator()); // Escribe los datos del contacto en el archivo
	        escribir.close(); // Cierra el archivo para asegurar que los cambios se guarden correctamente

	        return true; // Indica que el contacto fue guardado exitosamente
	    } catch (IOException e) {
	        e.printStackTrace(); // Manejo de error en caso de problemas con la escritura
	        return false;
	    }
	}
	
	// Metodo para leer los contactos desde el archivo y almacenarlos en una lista
	public List<persona> leerArchivo() throws IOException {
	    List<persona> personas = new ArrayList<>(); // Lista donde se almacenaran los contactos
	    FileReader leer = new FileReader(archivo.getAbsolutePath());
	    StringBuilder contactos = new StringBuilder();
	    int c;
	    boolean primeraLinea = true; // Bandera para identificar y omitir el encabezado

	    // Leer el archivo caracter por caracter y almacenarlo en un StringBuilder
	    while ((c = leer.read()) != -1) {
	        contactos.append((char) c);
	    }
	    
	    leer.close(); // Cerrar el archivo despues de la lectura

	    String[] datos = contactos.toString().split("\n"); // Separa los contactos por líneas

	    System.out.println("Contactos registrados en el archivo:");

	    // Procesa cada línea del archivo y la converte en un objeto persona
	    for (String contacto : datos) {
	        if (primeraLinea) { 
	            primeraLinea = false; // Omite el encabezado
	            System.out.println("Encabezado detectado y omitido: " + contacto);
	            continue;
	        }

	        if (contacto.trim().isEmpty()) {
	            System.out.println("Línea vacía detectada. Ignorada.");
	            continue;
	        }

	        String[] campos = contacto.split(";"); 

	        if (campos.length == 5) { // Validar que tenga la estructura correcta
	            persona p = new persona(campos[0].trim(), campos[1].trim(), campos[2].trim(), campos[3].trim(), Boolean.parseBoolean(campos[4].trim()));
	            personas.add(p); // Agregar la persona a la lista
	            System.out.println("Cargado: " + p.getNombre() + " - " + p.getTelefono());
	        } else {
	            System.out.println("Contacto con formato incorrecto. Ignorado.");
	        }
	    }

	    return personas; // Retornar la lista con los contactos cargados
	}

	// Método público para guardar los contactos modificados o eliminados
	public synchronized void actualizarContactos(List<persona> personas) throws IOException {
	    try (FileWriter escritor = new FileWriter(archivo.getAbsolutePath())) {
	        //Escribe el encabezado en el archivo para mantener la estructura
	    	escritor.write("Nombre;Teléfono;Email;Categoría;Favorito" + System.lineSeparator()); // Mantiene el encabezado

	        // Itera sobre la lista de contactos y guarda solo los válidos
	        for (persona p : personas) {
	            if (!p.getNombre().trim().isEmpty() && !p.getTelefono().trim().isEmpty()) { // Evita registros vacíos
	                escritor.write(p.datosContacto() + System.lineSeparator());
	            }
	        }
	    }
	}
	
	// Método para verificar si un contacto ya existe en el archivo
	public boolean existeContacto(String nombre, String telefono, String email, String categoria) throws IOException {
	    List<persona> personas = leerArchivo(); // Obtiene la lista de contactos guardados

	    if (personas.isEmpty()) { // Si no hay contactos, retornar falso
	        System.out.println("No hay contactos registrados.");
	        return false;
	    }

	    for (persona p : personas) {
	        if (p.getNombre() == null || p.getTelefono() == null || p.getEmail() == null) continue;

	        // Normalizar los datos eliminando espacios y caracteres invisibles
	        String nombreExistente = p.getNombre().trim().replaceAll("\\s+", " ").toLowerCase();
	        String telefonoExistente = p.getTelefono().trim().replaceAll("\\s+", "");
	        String emailExistente = p.getEmail().trim().replaceAll("\\s+", "").toLowerCase();

	        String nombreNuevo = nombre.trim().replaceAll("\\s+", " ").toLowerCase();
	        String telefonoNuevo = telefono.trim().replaceAll("\\s+", "");
	        String emailNuevo = email.trim().replaceAll("\\s+", "").toLowerCase();

	        //System.out.println("📌 Comparando con: " + nombreExistente + " - " + telefonoExistente + " - " + emailExistente + " - " + categoriaExistente);
	       
	        // Compara los datos normalizados para detectar duplicados
	        if (nombreExistente.equals(nombreNuevo) && telefonoExistente.equals(telefonoNuevo) &&
	            emailExistente.equals(emailNuevo)) {
	            System.out.println("❌ Contacto duplicado detectado: " + nombreNuevo);
	            return true; // Retorna verdadero si ya existe el contacto
	        }
	    }
	    return false; // Si no se encontraron coincidencias, el contacto es nuevo
	}

	// Método para limpiar el archivo y reescribir solo los contactos válidos
	public void limpiarArchivo() throws IOException {
	    List<persona> personas = leerArchivo(); // Leer contactos antes de limpiar el archivo

	    try (FileWriter escritor = new FileWriter(archivo.getAbsolutePath())) {
	        // Escribie el encabezado para mantener la estructura del archivo
	    	escritor.write("Nombre;Teléfono;Email;Categoría;Favorito" + System.lineSeparator()); // Mantener encabezado

	        // Recorre la lista de contactos y guardar solo los que tienen datos validos
	        for (persona p : personas) {
	            if (!p.getNombre().trim().isEmpty() && !p.getTelefono().trim().isEmpty()) {
	                escritor.write(p.datosContacto() + System.lineSeparator());
	            }
	        }
	    }
	}
}
