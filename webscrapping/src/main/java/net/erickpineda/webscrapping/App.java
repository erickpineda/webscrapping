package net.erickpineda.webscrapping;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Erick Pineda - Web Scrapping a la Wikipedia. Programa que extrae y
 *         convierte la información de una tabla html a un fichero sql (URL a la
 *         Wikipedia), crea una conexión con la BDD MySQL e inserta los datos en
 *         una tabla creada.
 */
public class App {
	/**
	 * Nombre que de la base de datos a conectar.
	 */
	private static String bd = "europa";
	/**
	 * Nombre del usuario de la base de datos.
	 */
	private static String user = "myUser";
	/**
	 * Contraseña del usuario de la base de datos.
	 */
	private static String password = "myPass!";
	/**
	 * Nombre de host para el SGBDD.
	 */
	private static String host = "localhost";
	/**
	 * Clase que conecta conecta con la BDD y demás operaciones.
	 */
	private static ConectarBD conecta;
	/**
	 * Será la lista de los INSERTs que se guardarán en un fichero sql.
	 */
	private static List<String> lista;
	/**
	 * Nombre que llevará el fichero a crear.
	 */
	private static String nombreFichero = "src/main/resources/inserts.sql";
	/**
	 * Permite crear un fichero de escritura.
	 */
	private static FileWriter ficheroAEscribir = null;
	/**
	 * Variable que escribirá en el fichero a crear o creado.
	 */
	private static PrintWriter out = null;
	/**
	 * Abre el fichero a leer.
	 */
	private static FileReader ficheroALeer = null;
	/**
	 * Variable que se encarga de ir línea a línea del fichero existente.
	 */
	private static BufferedReader in = null;

	/**
	 * Método principal del programa.
	 */
	public static void main(String[] args) {

		try {
			App.inicio();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Método que reune el correcto orden, del funcionamiento del programa.
	 * 
	 * @throws IOException
	 *             Errores de excepciones.
	 * @throws InterruptedException
	 *             Lanzador de Excepción.
	 */
	public static void inicio() throws IOException, InterruptedException {

		// Llamo al root del DOM para la URL
		Document document = Jsoup.connect(
				"http://ca.wikipedia.org/wiki/Llista_de_capitals_europees")
				.get();

		// Selecciono todas las etiquetas tr
		Elements inputs = document.select("tr");

		// Extraer información de la Wikipedia
		extraerDatosWikipedia(inputs);

		// Crear el fichero SQL
		crearFicheroSQL();

		// Efectúo la conexión con la BDD
		conecta = new ConectarBD(host, bd, user, password);
		conecta.conectarBD();

		// Siempre que la tabla no exista, creará una nueva
		conecta.insertarDatos("CREATE TABLE IF NOT EXISTS paises ("
				+ "pais varchar(30), codiiso varchar(4), superficie int(10), "
				+ "habitants int(10), capital varchar(30));");

		// Borra todos los datos sobre la tabla en la BDD
		conecta.insertarDatos("TRUNCATE TABLE paises");

		// Ejecutará el fichero SQL
		insertarDatosSQL();

		// Consulta sobre la base de datos
		conecta.consultarDatos("SELECT * FROM paises");

		// Cerrar la conexión sobre la BDD
		conecta.desconectarDB();
	}

	/**
	 * Método que se encarga de extraer la información de una tabla html y
	 * guardarla en una lista.
	 * 
	 * @param entrada
	 *            Parámetro que recibe los elementos seleccionados de la tabla.
	 * @return lista Retorna una lista con la información de la tabla html.
	 */
	public static List<String> extraerDatosWikipedia(Elements entrada) {
		lista = new ArrayList<String>();

		for (Element cadaFila : entrada) {

			// Recorrerá cada td en la tabla
			if (cadaFila.select("td").size() > 0) {
				Elements columnas = cadaFila.getElementsByTag("td");
				int posColumna = 1; // Cada columna en la tabla
				String insert = ""; // Guarda la línea de texto, que será un
									// INSERT

				// Recorrerá cada columa por cada fila que exista en la tabla
				for (Element columna : columnas) {

					// Busco las etiquetas de nombre td
					if (columna.tagName() == "td") {
						if (posColumna == 2)
							insert = "INSERT INTO paises VALUES('"
									+ columna.text() + "', ";

						if (posColumna == 3)
							insert = "'" + columna.text() + "', ";

						if (posColumna == 4 || posColumna == 5)
							insert = columna.text().replace(" ", "").toString()
									+ ", ";

						if (posColumna == 6)
							insert = "'" + columna.text() + "');\n";

					}
					lista.add(insert); // Agrego INSERT a la lista
					posColumna++;
				}
			}
		}
		return lista;
	}

	/**
	 * Método que crea un fichero sql, con la información almacenada en la Lista @param
	 * lista.
	 */
	public static void crearFicheroSQL() {
		try {

			ficheroAEscribir = new FileWriter(nombreFichero);
			out = new PrintWriter(ficheroAEscribir);

			// Itera la lista y escribe sobre el fichero creado
			for (int i = 0; i < lista.size(); i++)
				out.print(lista.get(i));

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ficheroAEscribir != null)
				try {
					ficheroAEscribir.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * Método que se encarga de leer un fichero sql y efectuar las operaciones
	 * sobre una BDD.
	 */
	public static void insertarDatosSQL() {

		try {

			String linea; // Línea en fichero
			ficheroALeer = new FileReader(nombreFichero);
			in = new BufferedReader(ficheroALeer);

			if (in != null) {
				linea = in.readLine();

				while (linea != null) {
					conecta.insertarDatos(linea); // INSERT INTO ...etc
					linea = in.readLine();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (ficheroALeer != null)
				try {
					ficheroALeer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
}
