package net.erickpineda.webscrapping;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class ConectarBD {
	/**
	 * Conector hacia el SGBDD.
	 */
	private static Connection conexion;
	/**
	 * Nombre que de la base de datos a conectar.
	 */
	private String bd;
	/**
	 * Nombre del usuario de la base de datos.
	 */
	private String user;
	/**
	 * Contraseña del usuario de la base de datos.
	 */
	private String password;
	/**
	 * Nombre de host para el SGBDD.
	 */
	private String host;
	/**
	 * Enlaza la conexión del host y bdd hacia el servidor.
	 */
	private String server;

	/**
	 * Constructor de la conexión hacia el SGBDD.
	 * 
	 * @param _host
	 *            Será el nombre que llevará host a conectar.
	 * @param bdd
	 *            Nombre de la BDD a conectarse.
	 * @param usuario
	 *            Nombre del usuario que ingresa a la BDD.
	 * @param pass
	 *            Contraseña del usuario de la BDD.
	 */
	public ConectarBD(final String _host, final String bdd,
			final String usuario, final String pass) {

		this.host = _host;
		this.bd = bdd;
		this.user = usuario;
		this.password = pass;
		this.server = "jdbc:mysql://" + host + "/" + bd;

	}

	/**
	 * Método que reúne la líneas de conexión hacia el SGBDD.
	 * 
	 * @throws InterruptedException
	 *             Lanzador de Excepción.
	 */
	public void conectarBD() throws InterruptedException {

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conexion = (Connection) DriverManager.getConnection(server, user,
					password);

			System.out.println("-> Conexión a base de datos " + server
					+ " ... OK\n");

			Thread.sleep(2000);

		} catch (ClassNotFoundException e) {

			e.printStackTrace();
			System.out
					.println("-> Error cargando el Driver MySQL JDBC ... FAIL");

			Thread.sleep(2000);

		} catch (SQLException e) {

			e.printStackTrace();
			System.out.println("-> Imposible realizar conexión con " + server
					+ " ... FAIL");

			Thread.sleep(2000);
		}
	}

	/**
	 * Método que realiza consultas SELECT sobre la BDD, recibiendo un String
	 * como parámetro.
	 * 
	 * @param miConsulta
	 *            Parámetro que será una SELECT, pasado como String.
	 */
	public void consultarDatos(final String miConsulta) {

		Statement s;
		ResultSet rs; // Puntero o cursor a la fila actual

		try {
			s = (Statement) conexion.createStatement();
			rs = s.executeQuery(miConsulta);

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
					System.out.print(" " + rs.getString(i) + " |");
				System.out.println();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que se encarga de insertar datos a la base de datos.
	 * 
	 * @param miConsulta
	 *            Parámetro que será un INSERT, pasado como String.
	 */
	public void insertarDatos(final String miConsulta) {

		PreparedStatement s;

		try {
			s = (PreparedStatement) conexion.prepareStatement(miConsulta);
			s.execute(miConsulta);

		} catch (SQLException e) {
			try {
				conexion.rollback(); // Rollback si el INSERT es erróneo
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	/**
	 * Método que cierra la conexión de la BDD.
	 */
	public void desconectarDB() {

		try {
			conexion.close();
		} catch (SQLException e) {
			System.out.println("-> Cerrar conexión con " + server + " ... OK");
			e.printStackTrace();
			System.out.println("-> Imposible cerrar conexión ... FAIL");
		}
	}
}
