package net.erickpineda.webscrapping;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;

public class ConectarBD {
	private static Connection conexion;
	private String bd;
	private String user;
	private String password;
	private String host;
	private String server;

	public ConectarBD(final String _host, final String bdd,
			final String usuario, final String pass) {

		this.host = _host;
		this.bd = bdd;
		this.user = usuario;
		this.password = pass;
		this.server = "jdbc:mysql://" + host + "/" + bd;
	}

	public void conectarBD() {

		try {
			Class.forName("com.mysql.jdbc.Driver");

			conexion = (Connection) DriverManager.getConnection(server, user,
					password);

			System.out
					.println("Conexión a base de datos " + server + " ... OK");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Error cargando el Driver MySQL JDBC ... FAIL");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Imposible realizar conexión con " + server
					+ " ... FAIL");
		}
	}

	public void consultarDatos(final String miConsulta) {

		Statement s;
		ResultSet rs;

		try {
			s = (Statement) conexion.createStatement();
			rs = s.executeQuery(miConsulta);

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
					System.out.print(rs.getString(i) + " | ");
				System.out.println();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertarDatos(final String miConsulta) {

		Statement s;

		try {
			s = (Statement) conexion.createStatement();
			s.executeUpdate(miConsulta);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void desconectarDB() {

		try {
			conexion.close();
		} catch (SQLException e) {
			System.out.println("Cerrar conexión con " + server + " ... OK");
			e.printStackTrace();
			System.out.println("Imposible cerrar conexión ... FAIL");
		}
	}
}
