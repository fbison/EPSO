import java.io.*;

class ModuloArquivos{


	public void escreve(RandomAccessFile arq, String string) throws IOException{

		byte[] string_bytes = string.getBytes();
		
		String entre = "\n";
		byte[] enterEmByte = entre.getBytes();

		arq.write(string_bytes);
		arq.write(enterEmByte);
		// arq.close();

	}

	public void closeFile(RandomAccessFile arq) throws IOException{
		arq.close();
	}
}