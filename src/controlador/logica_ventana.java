package controlador;

import java.awt.Font;
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

import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import vista.ventana;
import modelo.*;
import recursos.Internacionalizacion;

//Definición de la clase logica_ventana que implementa tres interfaces para manejar eventos.
public class logica_ventana implements ActionListener, ListSelectionListener, ItemListener {
	private ventana delegado;
	private String nombres, email, telefono, categoria=""; // Variables para almacenar datos del contacto.
	private persona persona;
	private List<persona> contactos;
	private boolean favorito = false; 
	private DefaultTableModel modeloTabla;
	private int indiceSeleccionado;
	private modelo.persona personaSeleccionada;
	
	// Constructor que inicializa la clase y configura los listener de eventos para los componentes de la GUI.
	public logica_ventana(ventana delegado) {
	    this.delegado = delegado;
	    this.modeloTabla = delegado.modeloTabla;
	    
	    // Carga los contactos almacenados al inicializar.
	    cargarContactosRegistrados();
	    cargarContactosEnTabla(); //Cargar los contactos en la tabla
	    
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
	    			agregarContacto();
	    		}
	    	}
	   
	    });
	    
	    this.delegado.txt_telefono.addKeyListener(new KeyAdapter() {
	    	@Override
	    	public void keyPressed(KeyEvent e) {
	    		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	    			agregarContacto();
	    		}
	    	}
	    });
	    
	    this.delegado.txt_email.addKeyListener(new KeyAdapter() {
	    	 @Override
	     	public void keyPressed(KeyEvent e) {
	     		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
	     			agregarContacto();
	     		}
	     	}
	    });
	}

	
	// Método privado para inicializar las variables con los valores ingresados en la GUI.
	private void incializacionCampos() {
		nombres = delegado.txt_nombres.getText();
		email = delegado.txt_email.getText();
		telefono = delegado.txt_telefono.getText();
	}

	// Método privado para cargar los contactos almacenados desde un archivo.
	private void cargarContactosRegistrados() {
	    try {
	        contactos = new personaDAO(new persona()).leerArchivo(); // Lee los contactos almacenados utilizando una instancia de personaDAO.
	        modeloTabla.setRowCount(0); // Limpia la tabla antes de cargar nuevos datos
	        // Omitir la primera línea si es el encabezado (ya se maneja en leerArchivo, pero es una precaución)
	        for (int i = 0; i < contactos.size(); i++) {
	            if (i == 0 && contactos.get(i).getNombre() != null && contactos.get(i).getNombre().equalsIgnoreCase("Nombre")) {
	                continue; // Saltar la primera fila si es un encabezado
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
	
	//Nuevo método para cargar contactos en la JTable
	private void cargarContactosEnTabla() {
	    modeloTabla.setRowCount(0); //Limpiar la tabla antes de agregar nuevos datos.
	    for (int i = 0; i < contactos.size(); i++) {
	        if (i == 0 && contactos.get(i).getNombre().equalsIgnoreCase("Nombre")) {
	            continue; //Saltar la primera fila si es un encabezado
	        }

	        Object[] fila = { 
	            contactos.get(i).getNombre(),
	            contactos.get(i).getTelefono(),
	            contactos.get(i).getEmail(),
	            contactos.get(i).getCategoria(),
	            contactos.get(i).isFavorito() ? "Sí" : "No"
	        };
	        modeloTabla.addRow(fila);
	    }
	}

	// Método privado para limpiar los campos de entrada en la GUI y reiniciar variables.
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

	// Método que maneja los eventos de acción (clic) en los botones.
	@Override
	public void actionPerformed(ActionEvent e) {
	    incializacionCampos(); // Inicializa las variables con los valores actuales de la GUI.

	    // Verifica si el evento proviene del botón "Agregar".
	    if (e.getSource() == delegado.btn_add) {
	        if ((!nombres.equals("")) && (!telefono.equals("")) && (!email.equals(""))) {
	            // Nueva validación: evitar que la categoría sea la opción de selección por defecto
	            if ((!categoria.equals(Internacionalizacion.getTexto("category.select"))) && (!categoria.isEmpty())) {
	                persona = new persona(nombres, telefono, email, categoria, favorito);
	                new personaDAO(persona).escribirArchivo();
	                limpiarCampos();
	                JOptionPane.showMessageDialog(delegado, "Contacto Registrado!!!");
	            } else {
	                JOptionPane.showMessageDialog(delegado, "Debes seleccionar una categoría válida.");
	                return; // Evita que el contacto se registre
	            }
	        } else {
	            JOptionPane.showMessageDialog(delegado, "Todos los campos deben ser llenados!!!");
	        }
	        actualizarEstadisticas();
	    } else if (e.getSource() == delegado.btn_eliminar) {
	        // Implementar eliminación de contactos
	    } else if (e.getSource() == delegado.btn_modificar) {
	        // Implementar modificación de contactos
	    }
	}

	//Metodo para agregar desde el teclado 
	private void agregarContacto() {
	    incializacionCampos();

	    if (!nombres.isEmpty() && !telefono.isEmpty() && !email.isEmpty()) {
	        if (!categoria.equals("Elija una Categoria") && !categoria.isEmpty()) {
	            persona nuevaPersona = new persona(nombres, telefono, email, categoria, favorito);
	            if (new personaDAO(nuevaPersona).escribirArchivo()) { // Verifica si la escritura fue exitosa
	                contactos.add(nuevaPersona); // Añade la nueva persona a la lista local
	                Object[] nuevaFila = {
	                        nuevaPersona.getNombre(),
	                        nuevaPersona.getTelefono(),
	                        nuevaPersona.getEmail(),
	                        nuevaPersona.getCategoria(),
	                        nuevaPersona.isFavorito() ? "Sí" : "No"
	                };
	                modeloTabla.addRow(nuevaFila); // Añade la nueva fila al modelo de la tabla
	                limpiarCampos();
	                JOptionPane.showMessageDialog(delegado, "Contacto Registrado!!!");
	                actualizarEstadisticas();
	            } else {
	                JOptionPane.showMessageDialog(delegado, "Error al guardar el contacto.");
	            }
	        } else {
	            JOptionPane.showMessageDialog(delegado, "Elija una categoría!!!");
	        }
	    } else {
	        JOptionPane.showMessageDialog(delegado, "Todos los campos deben estar llenos!!!");
	    }
	}
	
	// Método que maneja los eventos de selección en la lista de contactos.
	@Override
	public void valueChanged(ListSelectionEvent e) {
	    if (!e.getValueIsAdjusting()) { // Ignora eventos intermedios
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

	                // Cargar estos datos en los campos de texto
	                delegado.txt_nombres.setText(nombre);
	                delegado.txt_telefono.setText(telefono);
	                delegado.txt_email.setText(email);
	                delegado.cmb_categoria.setSelectedItem(categoria);
	                delegado.chb_favorito.setSelected(favorito);

	                // También necesitamos guardar el índice del contacto seleccionado en nuestra lista 'contactos'
	                // para poder modificarlo o eliminarlo correctamente.
	                if (selectedRow < contactos.size()) {
	                    personaSeleccionada = contactos.get(selectedRow);
	                    indiceSeleccionado = selectedRow;
	                }
	            } else {
	                // Si no hay ninguna fila seleccionada, limpia los campos
	                limpiarCampos(); // O podrías tener una versión de limpiarCampos que no recargue la tabla
	                personaSeleccionada = null;
	                indiceSeleccionado = -1;
	            }
	        }
	        // Ya no necesitamos la lógica para delegado.lst_contactos
	    }
	}

	// Método privado para cargar los datos del contacto seleccionado en los campos de la GUI.
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
	        
	        // Verificar si el objeto seleccionado no es null antes de llamar toString()
	        if (seleccionada != null && !seleccionada.toString().equals(Internacionalizacion.getTexto("category.select"))) {
	            categoria = seleccionada.toString();
	        } else {
	            categoria = ""; // Evita errores si la selección aún es inválida
	        }
	    } else if (e.getSource() == delegado.chb_favorito) {
	        favorito = delegado.chb_favorito.isSelected();
	    }
	}
	
	//Metodo para actualizar las estadisticas
	private void actualizarEstadisticas() {
		int total = contactos.size();
		int favoritos = 0;
		int familia = 0, amigos = 0, trabajo = 0;
		
		for (modelo.persona contacto : contactos) {
			if (contacto.isFavorito()) {
				favoritos++;
			}
			switch (contacto.getCategoria()) {
				case "Familia" : familia++;
				break;
				case "Amigos" : amigos++;
				break;
				case "Trabajo" : trabajo++;
				break;
			}
		}
		String categorias = String.format("Familia: %d | Amigos: %d | Trabajo: %d", familia, amigos, trabajo); //Formatear la distribucion de categorias
		
		delegado.actualizarEstadisticas(total, favoritos, categorias); //Actualizar la interfaz de la Ventana 
	}
	
	//Metodos para exportar los contactos
	private void exportarContactosCSV() {
	    String archivo = "contactos_exportados.csv"; // Nombre del archivo

	    SwingWorker<Void, Integer> worker = new SwingWorker<>() {
	        @Override
	        protected Void doInBackground() {
	            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
	                writer.write("Nombre,Teléfono,Email,Categoría,Favorito");
	                writer.newLine();

	                int totalContactos = contactos.size();
	                for (int i = 0; i < totalContactos; i++) {
	                    String favoritoStr = contactos.get(i).isFavorito() ? "Sí" : "No";
	                    writer.write(String.format("%s,%s,%s,%s,%s",
	                        contactos.get(i).getNombre(),
	                        contactos.get(i).getTelefono(),
	                        contactos.get(i).getEmail(),
	                        contactos.get(i).getCategoria(),
	                        favoritoStr));
	                    writer.newLine();

	                    // 🔹 Actualizar barra de progreso
	                    publish((i + 1) * 100 / totalContactos);
	                    Thread.sleep(50); // Simulación de retraso para visualizar el avance
	                }
	            } catch (IOException | InterruptedException e) {
	                JOptionPane.showMessageDialog(delegado, "Error al exportar contactos.");
	            }
	            return null;
	        }

	        @Override
	        protected void process(List<Integer> chunks) {
	            int progreso = chunks.get(chunks.size() - 1);
	            delegado.barraProgreso.setValue(progreso);
	        }

	        @Override
	        protected void done() {
	            JOptionPane.showMessageDialog(delegado, "Exportación completada. Archivo: " + archivo);
	        }
	    };
	    worker.execute();
	}	
}