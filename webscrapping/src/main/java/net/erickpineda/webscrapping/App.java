package net.erickpineda.webscrapping;

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
 * @author Erick Pineda - Web Scrapping a la Wikipedia
 *
 */
public class App {

	private static String bd = "europa";
	private static String user = "erick";
	private static String password = "ies2015!";
	private static String host = "localhost";
	private static ConectarBD conecta;
	private static List<String> lista;
	
	private static String nombreFichero = "inserts.sql";
	private static FileWriter fichero = null;
	private static PrintWriter escribirEnFichero = null;
	

	public static void main(String[] args) {

		try {
			App.inicio();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void inicio() throws IOException {
		// Llamo al root del DOM
		Document document = Jsoup.connect(
				"http://ca.wikipedia.org/wiki/Llista_de_capitals_europees")
				.get();

		// Selecciono todas las etiquetas tr
		Elements inputs = document.getElementsByTag("tr");
		
		
		extraerDatosWikipedia(inputs);
		crearFicheroSQL();
		
		for (String s : lista)
			System.out.print(s);

		

		// Efectúo la conexción con la BDD
		conecta = new ConectarBD(host, bd, user, password);
		conecta.conectarBD();

		// Siempre que la tabla no exista, creará una nueva
		conecta.insertarDatos("CREATE TABLE IF NOT EXISTS paises ("
				+ "pais varchar(30), codiiso varchar(4), superficie int(10),"
				+ "habitants int(10), capital varchar(30))");

		// Crear el fichero SQL

		// Ejecutará fichero SQL

		// Consulta sobre la base de datos
		conecta.consultarDatos("SELECT * FROM paises");
		conecta.desconectarDB();
	}

	public static List<String> extraerDatosWikipedia(Elements entrada) {
		lista = new ArrayList<String>();

		for (Element cadaFila : entrada) {

			if (cadaFila.select("td").size() > 0) {
				Elements columnas = cadaFila.getElementsByTag("td");
				int posColumna = 1;
				String insert = "";

				for (Element columna : columnas) {

					if (columna.tagName() == "td") {
						if (posColumna == 2)
							insert = "INSERT INTO paises VALUES('"
									+ columna.text() + "', ";

						if (posColumna == 3)
							insert = "'" + columna.text() + "', ";

						if (posColumna == 4 || posColumna == 5)
							insert = columna.text() + ", ";

						if (posColumna == 6)
							insert = "'" + columna.text() + "');\n";

					}
					lista.add(insert);
					posColumna++;
				}
			}
		}
		return lista;
	}

	public static void crearFicheroSQL() {
		try {
			
			fichero = new FileWriter(nombreFichero);
			escribirEnFichero = new PrintWriter(fichero);
			
			for (int i = 0; i < lista.size(); i++)
				escribirEnFichero.print(lista.get(i));
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (fichero != null)
				try {
					fichero.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static void leerFicheroSQL(){
		
	}
}
