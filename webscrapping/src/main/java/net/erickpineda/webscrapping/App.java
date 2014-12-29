package net.erickpineda.webscrapping;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author Erick Pineda - Web Scrapping a la Wikipedia
 *
 */
public class App {
	public static void main(String[] args) throws IOException {
		Document document = Jsoup.connect(
				"http://ca.wikipedia.org/wiki/Llista_de_capitals_europees")
				.get();

		Elements inputs = document.select("table");
		mostrarTabla(inputs);

	}

	public static void mostrarTabla(Elements entrada) {
		for (Element elemento : entrada)
			System.out.println(elemento);
	}
}
