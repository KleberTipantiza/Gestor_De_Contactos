package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import vista.ventana;
import modelo.*;
import recursos.Internacionalizacion;

public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {
	private ventana delegado;
	private String nombres, email, telefono, categoria=""; // Variables para almacenar datos del contacto.
	private persona persona;
	private List<persona> contactos;
	private boolean favorito = false; 
	private DefaultTableModel modeloTabla;
	private int indiceSeleccionado;
	private modelo.persona personaSeleccionada;
	
	// Constructor de la lógica de la ventana, inicializa componentes y eventos
	public logica_ventana(ventana delegado) {
	    this.delegado = delegado;
	    this.modeloTabla = delegado.modeloTabla;
	    
	    // Limpieza del archivo antes de cargar contactos
	    try {
	        new personaDAO(new persona()).limpiarArchivo();
	    } catch (IOException e) {
	        System.out.println("Error al limpiar archivo: " + e.getMessage());
	    }
	    
	    // Carga los contactos almacenados en la interfaz al inicializar.
	    cargarContactosRegistrados();
	    cargarContactosEnTabla();
	    
	    //Vincular el boton con la accion 
	    this.delegado.btn_exportar.addActionListener(e -> exportarContactosCSV());
	    this.delegado.btn_add.addActionListener(this);
	    this.delegado.btn_eliminar.addActionListener(this);
	    this.delegado.btn_modificar.addActionListener(this);
	    this.delegado.tablaContactos.getSelectionModel().addListSelectionListener(this);
	    this.delegado.cmb_categoria.addItemListener(this);
	    this.delegado.chb_favorito.addItemListener(this);
	    
	    // Agregar evento de teclado en los campos de entrada
	    this.delegado.txt_nombres.addKeyListener(new KeyAdapter() {
	    	@Override
	    	public void keyPressed(KeyEvent e) {
	    		if(e.getKeyCode() == KeyEvent.VK_ENTER) { 
	    			agregarContacto(); // Agrega un contacto al presionar "Enter"
	    		}
	    	}
	   
	    });
	    
	    this.delegado.txt_telefono.addKeyListener(new KeyAdapter() {
	    	@Override
	    	public void keyPressed(KeyEvent e) {
	    		if(e.getKeyCode() == KeyEvent.VK_ENTER) { // Detecta "Enter"
	    			agregarContacto();
	    		}
	    	}
	    });
	    
	    this.delegado.txt_email.addKeyListener(new KeyAdapter() {
	    	 @Override
	     	public void keyPressed(KeyEvent e) {
	     		if(e.getKeyCode() == KeyEvent.VK_ENTER) { // Detecta "Enter"
	     			agregarContacto();
	     		}
	     	}
	    });
	    
	    // Eveneto para la busqueda de contactos
	    this.delegado.txt_buscar.addKeyListener(new KeyAdapter() {
	        @Override
	        public void keyPressed(KeyEvent e) {
	            if (e.getKeyCode() == KeyEvent.VK_ENTER) { // Detecta "Enter"
	                buscarContacto(delegado.txt_buscar.getText()); // Llama a la búsqueda en segundo plano
	            }
	        }
	    });
	}
	
	// Metodo para obtener los valores ingresados en la interfaz gráfica
	private void incializacionCampos() {
		nombres = delegado.txt_nombres.getText();
		email = delegado.txt_email.getText();
		telefono = delegado.txt_telefono.getText();
	}

	// Metodo para cargar los contactos almacenados desde un archivo.
	private void cargarContactosRegistrados() {
	    try {
	        contactos = new personaDAO(new persona()).leerArchivo(); // Lee los contactos almacenados utilizando una instancia de personaDAO.
	        modeloTabla.setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
	        // Omite la primera línea si es el encabezado
	        for (int i = 0; i < contactos.size(); i++) {
	            if (i == 0 && contactos.get(i).getNombre() != null && contactos.get(i).getNombre().equalsIgnoreCase("Nombre")) {
	                continue; // Salta el encabezado para evitar que aparezca como contacto
	            }
	            Object[] fila = {
	                    contactos.get(i).getNombre(),
	                    contactos.get(i).getTelefono(),
	                    contactos.get(i).getEmail(),
	                    contactos.get(i).getCategoria(),
	                    contactos.get(i).isFavorito() ? "Sí" : "No"
	            };
	            modeloTabla.addRow(fila); // Agrega cada contacto como una nueva fila al modelo de la tabla
	        }
	    } catch (IOException e) {
	        JOptionPane.showMessageDialog(delegado, "Existen problemas al cargar todos los contactos"); // Muestra un mensaje de error si ocurre una excepción
	    }
	}
		
	// Metodo para actualizar la tabla con los contactos almacenados
	private void cargarContactosEnTabla() {
	    modeloTabla.setRowCount(0); // 🔹 Limpiar la tabla antes de actualizar
	    // Recorre la lista de contactos y agrega solo los que tienen informacion valida
	    for (persona p : contactos) {
	        if (!p.getNombre().trim().isEmpty() && !p.getTelefono().trim().isEmpty()) { // Evita filas vacías
	            modeloTabla.addRow(new Object[]{
	            		p.getNombre(), 
	            		p.getTelefono(), 
	            		p.getEmail(), 
	            		p.getCategoria(), 
	            		p.isFavorito() ? "Sí" : "No"
    			});
	        } else {
	            System.out.println("Contacto vacío detectado en la tabla. Ignorado.");
	        }
	    }
	}

	// Metodo privado para limpiar los campos de entrada en la GUI y reiniciar variables.
	private void limpiarCampos() {
		// Limpia los campos de nombres, email y teléfono en la GUI.
	    delegado.txt_nombres.setText("");
	    delegado.txt_telefono.setText("");
	    delegado.txt_email.setText("");
	    // Reinicia las variables de categoría y favorito.
	    categoria = "";
	    favorito = false;
	    // Desmarca la casilla de favorito y establece la categoría por defecto.
	    delegado.chb_favorito.setSelected(favorito);
	    delegado.cmb_categoria.setSelectedIndex(0);
	    // Reinicia las variables con los valores actuales de la GUI.
	    incializacionCampos();
	    // Recarga los contactos en la lista de contactos de la GUI.
	    cargarContactosRegistrados();
	}

	// Metodo que maneja los eventos de acción (clic) en los botones.
	@Override
	public void actionPerformed(ActionEvent e) {
	    incializacionCampos(); // Inicializa las variables con los valores actuales de la GUI.

	    if (e.getSource() == delegado.btn_add) {
	        if (!nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) {
	            if (!categoria.equals(Internacionalizacion.getTexto("category.select")) && !categoria.isEmpty()) {
	                
	                // Verifica si el contacto ya existe antes de mostrar el mensaje
	                try {
	                    boolean contactoExiste = new personaDAO(new persona()).existeContacto(nombres, telefono, email, categoria);

	                    if (contactoExiste) {
	                        JOptionPane.showMessageDialog(delegado, "El contacto ya existe.");
	                        return; // Detiene la ejecución si el contacto es duplicado
	                    }

	                    // Crea y guarda el nuevo contacto en el archivo
	                    persona nuevaPersona = new persona(nombres, telefono, email, categoria, favorito);
	                    new personaDAO(nuevaPersona).escribirArchivo();
	                    limpiarCampos();
	                    mostrarNotificacion("Contacto guardado con éxito."); // Notificacion en tiempo real
	                } catch (IOException ex) {
	                    JOptionPane.showMessageDialog(delegado, "Error al verificar duplicados.");
	                    ex.printStackTrace();
	                }

	            } else {
	                JOptionPane.showMessageDialog(delegado, "Debe seleccionar una categoría válida.");
	                return; // Detiene la ejecución si la categoria no es valida
	            }
	        } else {
	            JOptionPane.showMessageDialog(delegado, "Todos los campos deben estar llenos.");
	            return; // Detiene la ejecución si los campos estan vacios
	        }
	        actualizarEstadisticas(); // Actualiza las estadísticas despues de agregar un contacto
	    } else if (e.getSource() == delegado.btn_eliminar) {
	        eliminarContacto(); // Llama al metodo eliminar
	    } else if (e.getSource() == delegado.btn_modificar) {
	        modificarContacto(); // Llama al metodo modificar
	    }
	}

	// Metodo para agregar un contacto desde el teclado al presionar "Enter" 
	private void agregarContacto() {
	    incializacionCampos(); // Captura los valores ingresados en la interfaz

	    if (!nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) { // Validar que los campos esten llenos
	        if (!categoria.equals("Elija una Categoria") && !categoria.isEmpty()) { // Verifica que la categoria sea valida
	           
	        	// Ejecuta la verificacion en segundo plano para evitar bloqueos en la interfaz
	        	SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
	                @Override
	                protected Boolean doInBackground() {
	                    try {
	                        return new personaDAO(new persona()).existeContacto(nombres, telefono, email, categoria);
	                    } catch (IOException e) {
	                        e.printStackTrace();
	                        return false;
	                    }
	                }

	                @Override
	                protected void done() {
	                    try {
	                        boolean contactoExiste = get(); // Espera el resultado antes de registrar

	                        if (contactoExiste) {
	                            JOptionPane.showMessageDialog(delegado, "El contacto ya existe.");
	                            return; // Detiene la ejecución si el contacto es duplicado
	                        }

	                        // Si el contacto no existe, se crea y se guarda
	                        persona nuevaPersona = new persona(nombres, telefono, email, categoria, favorito);
	                        personaDAO dao = new personaDAO(nuevaPersona);

	                        if (dao.escribirArchivo()) {
	                            contactos.add(nuevaPersona); //  Agrega a la lista de contactos
	                            modeloTabla.addRow(new Object[]{
	                                nuevaPersona.getNombre(),
	                                nuevaPersona.getTelefono(),
	                                nuevaPersona.getEmail(),
	                                nuevaPersona.getCategoria(),
	                                nuevaPersona.isFavorito() ? "Sí" : "No"
	                            });
	                            mostrarNotificacion("Contacto guardado con éxito."); // Notificacion en tiempo real
	                            actualizarEstadisticas(); // Actualiza la informacion de contactos
	                            limpiarCampos(); // Limpia los campos para ingresar otro contacto
	                        } else {
	                            JOptionPane.showMessageDialog(delegado, "Error al guardar el contacto.");
	                        }
	                    } catch (Exception e) {
	                        e.printStackTrace();
	                    }
	                }
	            };
	            worker.execute(); // Ejecuta el proceso de verificacion en segundo plano
	        } else {
	            JOptionPane.showMessageDialog(delegado, "Debes elegir una categoría válida.");
	            return; // Detiene la ejecucion si la categoria no es valida
	        }
	    } else {
	        JOptionPane.showMessageDialog(delegado, "⚠ Todos los campos deben estar llenos.");
	        return; // Detiene la ejecucion si los campos estan vacios
	    }
	}
	
	// Metodo que maneja la seleccion de contactos en la tabla
	@Override
	public void valueChanged(ListSelectionEvent e) {
	    if (!e.getValueIsAdjusting()) { // Ignora cambios intermedios
	        if (e.getSource() == delegado.tablaContactos.getSelectionModel()) {
	            int selectedRow = delegado.tablaContactos.getSelectedRow();
	            if (selectedRow != -1) {
	                // Obtener los datos de la fila seleccionada del modelo de la tabla
	                String nombre = (String) delegado.modeloTabla.getValueAt(selectedRow, 0);
	                String telefono = (String) delegado.modeloTabla.getValueAt(selectedRow, 1);
	                String email = (String) delegado.modeloTabla.getValueAt(selectedRow, 2);
	                String categoria = (String) delegado.modeloTabla.getValueAt(selectedRow, 3);
	                String favoritoStr = (String) delegado.modeloTabla.getValueAt(selectedRow, 4);
	                boolean favorito = favoritoStr.equals("Sí");

	                // Carga los datos del contacto en los campos de texto
	                delegado.txt_nombres.setText(nombre);
	                delegado.txt_telefono.setText(telefono);
	                delegado.txt_email.setText(email);
	                delegado.cmb_categoria.setSelectedItem(categoria);
	                delegado.chb_favorito.setSelected(favorito);

	                // Guarda el contacto seleccionado para futuras modificaciones o eliminaciones
	                if (selectedRow < contactos.size()) {
	                    personaSeleccionada = contactos.get(selectedRow);
	                    indiceSeleccionado = selectedRow;
	                }
	            } else {
	                // Si no hay ninguna fila seleccionada, limpia los campos
	                limpiarCampos();
	                personaSeleccionada = null;
	                indiceSeleccionado = -1;
	            }
	        }
	    }
	}

	// Metodo para cargar los datos del contacto seleccionado en los campos de la GUI.
	private void cargarContacto(int index) {
		
	    delegado.txt_nombres.setText(contactos.get(index).getNombre());
	    delegado.txt_telefono.setText(contactos.get(index).getTelefono());
	    delegado.txt_email.setText(contactos.get(index).getEmail());
	    delegado.chb_favorito.setSelected(contactos.get(index).isFavorito());
	    delegado.cmb_categoria.setSelectedItem(contactos.get(index).getCategoria());
	}

	// Método que maneja los eventos de cambio de estado en los componentes cmb_categoria y chb_favorito.
	@Override
	public void itemStateChanged(ItemEvent e) {
	    if (e.getSource() == delegado.cmb_categoria) {
	        Object seleccionada = delegado.cmb_categoria.getSelectedItem();
	        
	        // Verifica que la seleccion no sea nula y que no sea la opcion por defecto
	        if (seleccionada != null && !seleccionada.toString().equals(Internacionalizacion.getTexto("category.select"))) {
	            categoria = seleccionada.toString();
	        } else {
	            categoria = ""; // Evita errores si la selección es inválida
	        }
	    } else if (e.getSource() == delegado.chb_favorito) { // Cambio en el estado de favorito
	        favorito = delegado.chb_favorito.isSelected();
	    }
	}
	
	// Metodo para calcular y actualizar las estadisticas de contactos con soporte de idioma
	private void actualizarEstadisticas() {
	    int total = contactos.size(); // Obtener el total de contactos registrados
	    int favoritos = 0, familia = 0, amigos = 0, trabajo = 0;

	    // Recorre la lista de contactos y cuenta las categorias y favoritos
	    for (modelo.persona contacto : contactos) {
	        if (contacto.isFavorito()) {
	            favoritos++; // Contar los contactos marcados como favoritos
	        }
	        switch (contacto.getCategoria()) {
	            case "Familia": 
	                familia++; 
	                break;
	            case "Amigos": 
	                amigos++; 
	                break;
	            case "Trabajo": 
	                trabajo++; 
	                break;
	        }
	    }

	    // Formatear los datos de categorias con soporte de idioma
	    String categorias = String.format("%s: %d | %s: %d | %s: %d", 
	        Internacionalizacion.getTexto("category.family"), 
	        familia, 
	        Internacionalizacion.getTexto("category.friends"), 
	        amigos, 
	        Internacionalizacion.getTexto("category.work"), 
	        trabajo);

	    // Actualizar las estadisticas en la ventana principal
	    delegado.actualizarEstadisticas(total, favoritos, categorias);
	}

	
	// Metodo para exportar contactos a un archivo CSV de forma segura y en segundo plano
	private final Object lock = new Object(); // Control de acceso al archivo

	private void exportarContactosCSV() {
	    String archivo = "contactos_exportados.csv"; // Nombre del archivo

	    SwingWorker<Void, Integer> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() {
	            synchronized (lock) { // Sincronización para evitar corrupcion de datos
	                try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
	                    writer.write("Nombre,Teléfono,Email,Categoría,Favorito"); // Escribir encabezado
	                    writer.newLine();

	                    int totalContactos = contactos.size();
	                    for (int i = 0; i < totalContactos; i++) {
	                        // Formatea los datos de cada contacto
	                        String favoritoStr = contactos.get(i).isFavorito() ? "Sí" : "No";
	                        writer.write(String.format("%s,%s,%s,%s,%s",
	                            contactos.get(i).getNombre(),
	                            contactos.get(i).getTelefono(),
	                            contactos.get(i).getEmail(),
	                            contactos.get(i).getCategoria(),
	                            favoritoStr));
	                        writer.newLine();

	                        // Actualiza la barra de progreso
	                        publish((i + 1) * 100 / totalContactos);
	                    }
	                } catch (IOException e) {
	                    JOptionPane.showMessageDialog(delegado, "Error al exportar contactos: " + e.getMessage());
	                }
	            }
	            return null;
	        }

	        @Override
	        protected void process(List<Integer> chunks) {
	            int progreso = chunks.get(chunks.size() - 1);  // Obtiene el ultimo valor de progreso
	            delegado.barraProgreso.setValue(progreso); // Actualiza visualmente la barra de progreso
	        }

	        @Override
	        protected void done() {
	        	mostrarNotificacion("Exportación completada. Archivo: " + archivo); // Notificacion de exito en tiempo real
	        }
	    };
	    worker.execute(); // Ejecuta la exportación en segundo plano
	}
	
	// Metodo para eliminar un contacto seleccionado de la tabla y actualizar el archivo
	private void eliminarContacto() {
	    int filaSeleccionada = delegado.tablaContactos.getSelectedRow(); // Obtener la fila seleccionada

	    if (filaSeleccionada != -1) { // Verificar si hay una seleccion valida
	        int confirmacion = JOptionPane.showConfirmDialog(delegado, "¿Seguro que deseas eliminar este contacto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
	        
	        if (confirmacion == JOptionPane.YES_OPTION) { // Confirmar eliminacion
	            persona contactoAEliminar = contactos.get(filaSeleccionada);
	            contactos.remove(filaSeleccionada); // Elimina el contacto de la lista local

	            try {
	                new personaDAO(new persona()).actualizarContactos(contactos); // Actualizar el archivo CSV
	                
	                modeloTabla.setRowCount(0); // Vacia la tabla antes de recargar los datos
	               
	                cargarContactosRegistrados(); // Recarga la lista actualizada en la tabla

	                mostrarNotificacion("Contacto eliminado correctamente."); // Muestra la notificación de exito
	            } catch (IOException e) { //nManejo de error
	                JOptionPane.showMessageDialog(delegado, "Error al eliminar el contacto.");
	                e.printStackTrace();
	            }
	        }
	    } else {
	        JOptionPane.showMessageDialog(delegado, "Debes seleccionar un contacto para eliminar."); // Mensaje de advertencia si no hay seleccion
	    }
	}

	// Metodo para modificar un contacto seleccionado en la tabla
	private void modificarContacto() {
	    int filaSeleccionada = delegado.tablaContactos.getSelectedRow(); // Obtiene la fila seleccionada

	    if (filaSeleccionada != -1) { // Verifica si hay una selección valida
	        synchronized (personaDAO.bloqueoModificacion) { // Bloquea el acceso simultaneo para evitar conflictos de datos
	            int confirmacion = JOptionPane.showConfirmDialog(delegado, "¿Seguro que deseas modificar este contacto?", "Confirmar modificación", JOptionPane.YES_NO_OPTION);
	            
	            if (confirmacion == JOptionPane.YES_OPTION) { // Confirmar modificacion
	                incializacionCampos(); // Obtiene los nuevos valores ingresados en la interfaz
	                
	                if (!nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) { // Valida que los campos esten llenos
	                    persona contactoModificado = new persona(nombres, telefono, email, categoria, favorito);
	                    
	                    contactos.set(filaSeleccionada, contactoModificado); // Actualiza el contacto en la lista local

	                    try {
	                        new personaDAO(new persona()).actualizarContactos(contactos); // Guarda los cambios en el archivo CSV
	                        
	                        modeloTabla.setRowCount(0); // Vacia la tabla antes de recargar los datos
	                        
	                        cargarContactosRegistrados(); // Recargar lista de contactos actualizada en la tabla

	                        limpiarCampos(); // Limpia los campos después de la modificacion
	                        mostrarNotificacion("Contacto modificado correctamente."); // Notificacion de modificacion exitosa
	                    } catch (IOException e) {
	                        JOptionPane.showMessageDialog(delegado, "Error al modificar el contacto."); // Manejo de error
	                        e.printStackTrace();
	                    }
	                } else {
	                    JOptionPane.showMessageDialog(delegado, "Todos los campos deben estar llenos para modificar."); // Manejo de error
	                }
	            }
	        }
	    } else {
	        JOptionPane.showMessageDialog(delegado, "Debes seleccionar un contacto para modificar."); // Advertencia si no hay seleccion
	    }
	}

	
	// Metodo para buscar contactos en la lista
	private void buscarContacto(String criterio) {
	    SwingWorker<List<persona>, Void> worker = new SwingWorker<>() {
	        @Override
	        protected List<persona> doInBackground() {
	            List<persona> resultados = new ArrayList<>(); // Lista para almacenar resultados
	         // Buscar coincidencias en nombre, telefono o email
	            for (persona p : contactos) {
	                if (p.getNombre().toLowerCase().contains(criterio.toLowerCase()) ||
	                    p.getTelefono().contains(criterio) ||
	                    p.getEmail().toLowerCase().contains(criterio.toLowerCase())) {
	                    resultados.add(p);
	                }
	            }
	            return resultados;
	        }

	        @Override
	        protected void done() {
	            try {
	                List<persona> resultados = get(); // Obtener resultados procesados
	                SwingUtilities.invokeLater(() -> actualizarTabla(resultados)); // Actualiza la tabla sin bloquear la UI
	            } catch (Exception e) {
	                e.printStackTrace(); // Manejo de errores
	            }
	        }
	    };

	    worker.execute(); // Iniciar la búsqueda en segundo plano
	}
	
	
	// Metodo para actualizar la tabla con los contactos filtrados por busqueda
	private void actualizarTabla(List<persona> resultados) {
	    modeloTabla.setRowCount(0); // Limpia la tabla antes de cargar los nuevos resultados

	    for (persona p : resultados) { // Agregar los contactos encontrados a la tabla
	        modeloTabla.addRow(new Object[]{
	        		p.getNombre(), 
	        		p.getTelefono(), 
	        		p.getEmail(), 
	        		p.getCategoria(), 
	        		p.isFavorito() ? "Sí" : "No"});
	    }
	}
	// Metodo para mostrar una notificacion en la interfaz por unos segundos
	private void mostrarNotificacion(String mensaje) {
	    new Thread(() -> {
	        SwingUtilities.invokeLater(() -> {
	            delegado.lblNotificacion.setText(mensaje); // Mostrar el mensaje
	        });

	        try {
	            Thread.sleep(3000); // Mantiene la notificacion visible por 3 segundos
	        } catch (InterruptedException e) {
	            e.printStackTrace(); // Manejo de excepción si ocurre un error en el hilo
	        }

	        SwingUtilities.invokeLater(() -> delegado.lblNotificacion.setText("")); // Ocultar la notificacion despues del tiempo definido
	    }).start();
	}
}