package vista;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controlador.logica_ventana;
import recursos.Internacionalizacion;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JList;

public class ventana extends JFrame {

	public JPanel contentPane; 
	public JTextField txt_nombres; 
	public JTextField txt_telefono;
	public JTextField txt_email;
	public JTextField txt_buscar;
	public JCheckBox chb_favorito;
	public JComboBox cmb_categoria;
	public JButton btn_add;
	public JButton btn_modificar;
	public JButton btn_eliminar;
	private JTabbedPane tabbedPane;
	private JPanel panelContactos;  
    private JPanel panelEstadisticas; 
    private JLabel lbl_totalContactos;
    private JLabel lbl_favoritos;
    private JLabel lbl_porCategoria;
    public JTable tablaContactos;
    public DefaultTableModel modeloTabla;
    public JButton btn_exportar;
    public JProgressBar barraProgreso;
	private JLabel lbl_etiqueta1;
	private JLabel lbl_etiqueta2;
	private JLabel lbl_etiqueta3;
	private JLabel lbl_etiqueta4;
    
	public static void main(String[] args) {
		 // Invoca el método invokeLater de la clase EventQueue para ejecutar la creación de la interfaz de usuario en un hilo de despacho de eventos (Event Dispatch Thread).
	    EventQueue.invokeLater(new Runnable() {
	        public void run() {
	            try {
	                // Dentro de este método, se crea una instancia de la clase ventana, que es la ventana principal de la aplicación.
	                ventana frame = new ventana();
	                // Establece la visibilidad de la ventana como verdadera, lo que hace que la ventana sea visible para el usuario.
	                frame.setVisible(true);
	            } catch (Exception e) {
	                // En caso de que ocurra una excepción durante la creación o visualización de la ventana, se imprime la traza de la pila de la excepción.
	                e.printStackTrace();
	            }
	        }
	    });
	}

	public ventana() {
		Internacionalizacion.setIdioma("ES"); // Español por defecto
		setTitle(Internacionalizacion.getTexto("window.title")); 
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setBounds(100, 100, 1026, 748);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); 
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		//Crear el JTabbedPane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(20, 20, 970, 700);
		
		//Crear los paneles para cada pestaña
		panelContactos = new JPanel();
		panelEstadisticas = new JPanel();
		
		//Asegurar que se pueda modificar la posicion manualmente
		panelContactos.setLayout(null);
		panelEstadisticas.setLayout(null);
		
		//Agregar pestañas al JTabbedPane
		tabbedPane.addTab("Contactos", panelContactos);
		tabbedPane.addTab("Estadisticas", panelEstadisticas);
		
		//JTabbed agregado a contentPane
		contentPane.add(tabbedPane);
		
		//Crear el modelo de la tabla con las columnas
		modeloTabla = new DefaultTableModel();
		modeloTabla.addColumn("Nombre");
		modeloTabla.addColumn("Telefono");
		modeloTabla.addColumn("Email");
		modeloTabla.addColumn("Categoria");
		modeloTabla.addColumn("Favorito");
		
		//Instanciar la tabla con el modelo
		tablaContactos = new JTable(modeloTabla);
		
		//Agregar la tabla dentro de un JScrollPane para desplazar
		JScrollPane scrollTabla = new JScrollPane(tablaContactos);
		scrollTabla.setBounds(50, 230, 800, 300);
		panelContactos.add(scrollTabla);
		
		//1. Creación y configuración de etiquetas para los campos de entrada.
		lbl_etiqueta1 = new JLabel(Internacionalizacion.getTexto("label.name"));
		lbl_etiqueta1.setBounds(261, 72, 80, 13);
		lbl_etiqueta1.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelContactos.add(lbl_etiqueta1); 
		
		lbl_etiqueta2 = new JLabel(Internacionalizacion.getTexto("label.phone"));
		lbl_etiqueta2.setBounds(261, 120, 80, 13);
		lbl_etiqueta2.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelContactos.add(lbl_etiqueta2);
		
		lbl_etiqueta3 = new JLabel(Internacionalizacion.getTexto("label.email"));
		lbl_etiqueta3.setBounds(261, 170, 80, 13);
		lbl_etiqueta3.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelContactos.add(lbl_etiqueta3);
		
		lbl_etiqueta4 = new JLabel(Internacionalizacion.getTexto("label.search"));
		lbl_etiqueta4.setFont(new Font("Tahoma", Font.BOLD, 15));
		lbl_etiqueta4.setBounds(539, 20, 109, 13);
		panelContactos.add(lbl_etiqueta4);
		
		// Creación y configuración de campos de texto para ingresar nombres, teléfonos y correos electrónicos.
		txt_nombres = new JTextField();
		txt_nombres.setBounds(344, 62, 266, 33);
		txt_nombres.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panelContactos.add(txt_nombres);
		txt_nombres.setColumns(10);
		
		txt_telefono = new JTextField();
		txt_telefono.setBounds(344, 110, 266, 33);
		txt_telefono.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_telefono.setColumns(10);
		panelContactos.add(txt_telefono);
		
		txt_email = new JTextField();
		txt_email.setBounds(344, 160, 266, 33);
		txt_email.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_email.setColumns(10);
		panelContactos.add(txt_email);
		
		txt_buscar = new JTextField();
		txt_buscar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		txt_buscar.setColumns(10);
		txt_buscar.setBounds(658, 10, 275, 33);
		panelContactos.add(txt_buscar);
		
		// Creación de una casilla de verificación para indicar si un contacto es favorito.
		chb_favorito = new JCheckBox(Internacionalizacion.getTexto("label.favorite")); 
		chb_favorito.setBounds(684, 116, 169, 21); 
		chb_favorito.setFont(new Font("Tahoma", Font.PLAIN, 15));
		panelContactos.add(chb_favorito); 

		cmb_categoria = new JComboBox();
		cmb_categoria.setBounds(684, 143, 169, 21);
		panelContactos.add(cmb_categoria);

		// Agregar las categorías traducidas
		cmb_categoria.addItem(Internacionalizacion.getTexto("category.select"));
		cmb_categoria.addItem(Internacionalizacion.getTexto("category.family"));
		cmb_categoria.addItem(Internacionalizacion.getTexto("category.friends"));
		cmb_categoria.addItem(Internacionalizacion.getTexto("category.work"));

		//Creacion de botones
		btn_add = new JButton(Internacionalizacion.getTexto("button.add"));
		btn_add.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_add.setBounds(684, 92, 169, 21);
		panelContactos.add(btn_add);
		
		btn_modificar = new JButton(Internacionalizacion.getTexto("button.modify"));
		btn_modificar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_modificar.setBounds(261, 564, 109, 21);
		panelContactos.add(btn_modificar);
		
		btn_eliminar = new JButton(Internacionalizacion.getTexto("button.delete"));
		btn_eliminar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_eliminar.setBounds(443, 564, 114, 21);
		panelContactos.add(btn_eliminar);
		
		//Agregar un boton para exportar los contactos y una barra de progreso
		btn_exportar = new JButton(Internacionalizacion.getTexto("button.export"));
		btn_exportar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		btn_exportar.setBounds(636, 564, 132, 21); 
		panelContactos.add(btn_exportar);

		barraProgreso = new JProgressBar(0, 100); 
		barraProgreso.setBounds(63, 605, 870, 33);
		barraProgreso.setStringPainted(true); 
		barraProgreso.setValue(0);
		barraProgreso.setVisible(true);
		panelContactos.add(barraProgreso);		
		
		//Agregar etiquetas a la pestaña de estadisticas
		lbl_totalContactos = new JLabel("Total de Contactos: ");
		lbl_totalContactos.setBounds(50, 50, 300, 25);
		lbl_totalContactos.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelEstadisticas.add(lbl_totalContactos);
		
		lbl_favoritos = new JLabel("Contactos favoritos: ");
		lbl_favoritos.setBounds(50, 90, 300, 25);
		lbl_favoritos.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelEstadisticas.add(lbl_favoritos);
		
		lbl_porCategoria = new JLabel("Contactos por categoria: ");
		lbl_porCategoria.setBounds(50, 130, 500, 25);
		lbl_porCategoria.setFont(new Font("Tahoma", Font.BOLD, 15));
		panelEstadisticas.add(lbl_porCategoria);
		
		JComboBox<String> cmbIdioma = new JComboBox<>(new String[] {"Español", "Inglés", "Francés"});
		cmbIdioma.setBounds(50, 20, 150, 30); // Ajusta la posición y tamaño
		panelContactos.add(cmbIdioma); // Agrega al panel adecuado
		cmbIdioma.addActionListener(e -> {
		    String seleccionado = ((String) cmbIdioma.getSelectedItem()).toLowerCase();
		    switch (seleccionado) {
		        case "inglés" -> Internacionalizacion.setIdioma("EN");
		        case "francés" -> Internacionalizacion.setIdioma("FR");
		        default -> Internacionalizacion.setIdioma("ES");
		    }
		    actualizarTextos(); // Método para cambiar los textos de los componentes
		});

		
		//Instanciar el controlador para usar el delegado
		logica_ventana lv=new logica_ventana(this);
	}
	
	public void actualizarEstadisticas(int total, int favoritos, String categorias) {
		lbl_totalContactos.setText("Total de Contactos: " + total);
		lbl_favoritos.setText("Contactos favoritos: " + favoritos);
		lbl_porCategoria.setText("Contactos por categoria: " + categorias);
	}
	
	public void actualizarTextos() {
	    setTitle(Internacionalizacion.getTexto("window.title"));
	    lbl_etiqueta1.setText(Internacionalizacion.getTexto("label.name"));
	    lbl_etiqueta2.setText(Internacionalizacion.getTexto("label.phone"));
	    lbl_etiqueta3.setText(Internacionalizacion.getTexto("label.email"));
	    lbl_etiqueta4.setText(Internacionalizacion.getTexto("label.search"));
	    
	    btn_add.setText(Internacionalizacion.getTexto("button.add"));
	    btn_modificar.setText(Internacionalizacion.getTexto("button.modify"));
	    btn_eliminar.setText(Internacionalizacion.getTexto("button.delete"));
	    btn_exportar.setText(Internacionalizacion.getTexto("button.export"));

	    chb_favorito.setText(Internacionalizacion.getTexto("label.favorite"));
	    cmb_categoria.removeAllItems();
	    cmb_categoria.addItem(Internacionalizacion.getTexto("category.select"));
	    cmb_categoria.addItem(Internacionalizacion.getTexto("category.family"));
	    cmb_categoria.addItem(Internacionalizacion.getTexto("category.friends"));
	    cmb_categoria.addItem(Internacionalizacion.getTexto("category.work"));
	    cmb_categoria.repaint();
	    cmb_categoria.revalidate();
	}
	
}
