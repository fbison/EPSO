import java.io.*;
import java.util.*;

public class Main {
	public static void main(String[] args) throws IOException {

		Escalonador escalonador = new Escalonador();
		escalonador.setNumEntradas(10);
		try {
			escalonador.escalonar();
		} catch (FileNotFoundException ex) {
			System.out.println("Erro de execução");
		}

		escalonador.getLogFile().close();
	}
}
