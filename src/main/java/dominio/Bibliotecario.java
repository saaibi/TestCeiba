package dominio;

import java.util.Calendar;
import java.util.Date;

import dominio.excepcion.PrestamoException;
import dominio.repositorio.RepositorioLibro;
import dominio.repositorio.RepositorioPrestamo;

public class Bibliotecario {

	public static final String EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE = "El libro no se encuentra disponible";
	public static final String LOS_LIBROS_PALINDROMOS_SOLO_SE_PUEDEN_UTILIZAR_EN_BIBLIOTECA = "los libros palíndromos solo se pueden utilizar en la biblioteca";

	private RepositorioLibro repositorioLibro;
	private RepositorioPrestamo repositorioPrestamo;

	public Bibliotecario(RepositorioLibro repositorioLibro, RepositorioPrestamo repositorioPrestamo) {
		this.repositorioLibro = repositorioLibro;
		this.repositorioPrestamo = repositorioPrestamo;

	}

	public void prestar(String isbn, String nombreUsuario) {
		if (!esPrestado(isbn)) {
			if (!esPalindromo(isbn)) {
				repositorioPrestamo.agregar(agregarPrestamo(isbn, nombreUsuario));
			} else {
				throw new PrestamoException(LOS_LIBROS_PALINDROMOS_SOLO_SE_PUEDEN_UTILIZAR_EN_BIBLIOTECA);
			}
		} else {
			throw new PrestamoException(EL_LIBRO_NO_SE_ENCUENTRA_DISPONIBLE);
		}

	}

	public boolean esPrestado(String isbn) {
		if (repositorioPrestamo.obtenerLibroPrestadoPorIsbn(isbn) != null) {
			return true;
		}
		return false;
	}

	public boolean esPalindromo(String isbn) {
		if (isbn.length() <= 1) {
			return true;
		} else {
			if (isbn.charAt(0) == isbn.charAt(isbn.length() - 1))
				return esPalindromo(isbn.substring(1, isbn.length() - 1));
			else
				return false;
		}
	}

	public Prestamo agregarPrestamo(String isbn, String nombreUsuario) {
		Libro libro = repositorioLibro.obtenerPorIsbn(isbn);
		Date fechaSolicitud = new Date();
		Date fechaEntregaMaxima = null;

		if (sumaNumeros(isbn))
			return new Prestamo(fechaSolicitud, libro, calcularFechaEntregaMax(fechaSolicitud), nombreUsuario);
		else
			return new Prestamo(fechaSolicitud, libro, fechaEntregaMaxima, nombreUsuario);

	}

	public boolean sumaNumeros(String isbn) {
		int sum = 0;
		for (char digit : isbn.toCharArray())
			if (Character.isDigit(digit))
				sum += Integer.parseInt(String.valueOf(digit));
		return sum > 30 ? true : false;
	}

	public Date calcularFechaEntregaMax(Date fechaSolicitud) {
		Calendar fechaEntregaMaxima = Calendar.getInstance();
		Calendar fechaInicial = Calendar.getInstance();
		fechaInicial.setTime(fechaSolicitud);
		fechaEntregaMaxima.setTime(fechaSolicitud);

		fechaEntregaMaxima.add(Calendar.DAY_OF_YEAR, getNumeroDias(fechaInicial));

		return fechaEntregaMaxima.getTime();
	}

	private int getNumeroDias(Calendar fechaInicial) {
		int dias = 14;
		for (int i = 0; i <= dias; i++) {
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				dias++;
			}
			fechaInicial.add(Calendar.DATE, 1);
		}
		return dias;
	}

}
