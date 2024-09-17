import java.util.*;
import java.io.*;

public class BCP {

    private String nomeArquivo = "";
    private int prioridadeArquivo;
    private int contadorDePrograma = 0;
    private String cabecalho;

    private String estado; // EXECUTANDO, PRONTO, BLOQUEADO
    private int processosAposBloqueio = 0;
    private String statusBloqueado = "";

    private int regX = 0;
    private int regY = 0;

    private final String[] segmentoTexto;

    public BCP(String nomeArquivoEntrada) throws FileNotFoundException {

        Scanner scanner = new Scanner(new File(nomeArquivoEntrada));
        this.segmentoTexto = new String[21];
        this.cabecalho = scanner.next();

        int i = 0;
        while (scanner.hasNext()) {
            this.segmentoTexto[i] = scanner.next();
            if (this.segmentoTexto[i].equals("SAIDA")) break;
            i++;
        }
    }

    public int getPrioridadeArquivo() {
        return this.prioridadeArquivo;
    }

    public void setPrioridadeArquivo(int prioridade) {
        this.prioridadeArquivo = prioridade;
    }

    public String getNomeArquivo() {
        return this.nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getStatusBloqueado() {
        return this.statusBloqueado;
    }

    public void setStatusBloqueado(String statusBloqueado) {
        this.statusBloqueado = statusBloqueado;
    }

    public void atualizarProcessosAposBloqueio(int num) {
        this.processosAposBloqueio = num;
    }

    public int getProcessosAposBloqueio() {
        return this.processosAposBloqueio;
    }

    public String getCabecalho() {
        return this.cabecalho;
    }

    public int getContadorDePrograma() {
        return this.contadorDePrograma;
    }

    public String getSegmentoTexto(int i) {
        return this.segmentoTexto[i];
    }
    public String getComando() {
        return this.segmentoTexto[this.contadorDePrograma];
    }

    public int getRegX() {
        return this.regX;
    }

    public int getRegY() {
        return this.regY;
    }

    public void setContadorDePrograma(int contadorDePrograma) {
        this.contadorDePrograma = contadorDePrograma;
    }

    public void incrementaContadorDePrograma() {
        this.contadorDePrograma++;
    }

    public void setEstadoProcesso(String estado) {
        this.estado = estado;
    }

    public String getEstadoProcesso() {
        return this.estado;
    }

    public void setRegX(int regX) {
        this.regX = regX;
    }

    public void setRegY(int regY) {
        this.regY = regY;
    }

    // Método para verificar se o processo pode ser desbloqueado e se poder desbloquear
    public boolean tentarDesbloquear() {
        if (!(this.processosAposBloqueio == 2)) return false;
        atualizarProcessosAposBloqueio(0);
        setEstadoProcesso("PRONTO");
        return true;
    }

    public void atualizarBloqueio() {
        if (!getStatusBloqueado().contains("E/S INICIADA")) {
            atualizarProcessosAposBloqueio(getProcessosAposBloqueio() + 1);
        }
    }
}
