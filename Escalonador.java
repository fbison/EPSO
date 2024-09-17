import java.util.*;
import java.io.*;

public class Escalonador {

    private double interrompidos;
    private double executados;
    private double numProcessos;

    private int quantumTestado;
    private int nQuantum = 0;
    private int numEntradas;

    private final ArrayList<BCP> BCPs = new ArrayList<>();
    private final Processos listaDeProcessos = new Processos();
    private final TabelaProcessos tabelaDeProcessos = new TabelaProcessos();
    private final ModuloArquivos escreve = new ModuloArquivos();
    private RandomAccessFile logFile;

    public void carregarProcessos() throws IOException {

        String arq;

        for (int i = this.numEntradas; i >= 1; i--) {

            if (i < 10) arq = String.format("%02d", i);
            else arq = Integer.toString(i);
            String nomeArquivo = arq + ".txt";
            BCP bcp = new BCP("./Programas/" + nomeArquivo);
            bcp.setNomeArquivo(nomeArquivo);
            BCPs.add(bcp);
        }

        LePrioridades prioridades = new LePrioridades("./Programas/prioridades.txt", this.numEntradas);
        BCPs.sort(new ComparadorNomeProcesso());

        // Definir prioridades nos BCPs
        for (int i = 0; i < BCPs.size(); i++) {
            BCP bcp = BCPs.get(i);
            bcp.setPrioridadeArquivo(prioridades.prioridades[i]);
        }

        BCPs.sort(new ComparadorPrioridade());

        // Adicionar os BCPs às listas de processos prontos e tabela de processos
        for (BCP bcp : BCPs) {
            listaDeProcessos.getProntos().add(bcp);
            tabelaDeProcessos.getTabelaProcessos().add(bcp);
            addLineToLogFile("Carregando " + bcp.getCabecalho());
        }

        numProcessos = tabelaDeProcessos.getTabelaProcessos().size();
    }

    public void escalonar() throws IOException {

        setQuantum();
        criarArquivoLog();
        carregarProcessos();

        int comExecutadas = 0;
        BCP processoBCP = listaDeProcessos.getProntos().getFirst();
        String com = "";
        List<BCP> prontosAposBloqueio = new ArrayList<>();

        addLineToLogFile("Executando processo " + processoBCP.getCabecalho());

        while (tabelaDeProcessos.possuiProcessos()) {


            if (listaDeProcessos.temProntos() && listaDeProcessos.getProntos().contains(processoBCP)) {
                executarProcesso(processoBCP, comExecutadas);
            }

            comExecutadas++;

            if (deveInterromper(com, comExecutadas)) { //Ir para outro processo
                processoBCP = trocarProcesso(processoBCP, prontosAposBloqueio, com, comExecutadas);
                com = processoBCP.getComando();
                prontosAposBloqueio.clear();

                nQuantum++;
                comExecutadas = 0;
                if (com.contains("E/S") || com.contains("SAIDA")) com = "";
            }
        }

        registrarEstatisticas();
    }
    // Funções de suporte
    public void setQuantum() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("./Programas/quantum.txt"));
        this.quantumTestado = scanner.nextInt();
    }

    private void executarProcesso(BCP processoBCP, int comExecutadas) throws IOException {
        String com = processoBCP.getComando();
        executarInstrucao(processoBCP, com);
        executados++;
        processoBCP.incrementaContadorDePrograma();

        if (com.contains("E/S") || com.contains("SAIDA")) {
            interromperProcesso(processoBCP, com, comExecutadas);
        }
    }

    private boolean deveInterromper(String com, int comExecutadas){
        return (comExecutadas == quantumTestado || com.contains("E/S") || com.contains("SAIDA"));
    }

    private BCP trocarProcesso(BCP processoBCP, List<BCP> prontosAposBloqueio, String com, int comExecutadas) throws IOException {

        if (listaDeProcessos.temBloqueados()) {
            atualizarProcessosBloqueados(prontosAposBloqueio);
        }

        if (listaDeProcessos.temProntos() ||
                // ou nenhum processo bloqueado já liberou para saída
                !prontosAposBloqueio.isEmpty()) {
            processoBCP = reordenarProcessos(prontosAposBloqueio, processoBCP, com, comExecutadas);
        }

        return processoBCP;
    }

    private void atualizarProcessosBloqueados(List<BCP> prontosAposBloqueio) {
        prontosAposBloqueio.clear();
        Iterator<BCP> it = listaDeProcessos.getBloqueados().iterator();

        while (it.hasNext()) {
            BCP bloqueado = it.next();
            bloqueado.atualizarBloqueio();
            bloqueado.setStatusBloqueado("BLOQUEADO");
            if (bloqueado.tentarDesbloquear()) {
                prontosAposBloqueio.add(bloqueado);
                it.remove();  // Remover usando o Iterator, evitando a ConcurrentModificationException
            }
        }
    }

    private BCP reordenarProcessos(List<BCP> prontosAposBloqueio, BCP processoBCP, String com, int comExecutadas) throws IOException {
        if (listaDeProcessos.estaPronto(processoBCP)) {
            if (listaDeProcessos.getProntos().size() > 1) interrompidos++;
            addLineToLogFile("Interrompendo " + processoBCP.getCabecalho() + " após " + comExecutadas + " instruções");
        }

        if (listaDeProcessos.getProntos().size() > 1 && !com.contains("SAIDA") && !com.contains("E/S")) {
            processoBCP = listaDeProcessos.getProntos().removeFirst();
            listaDeProcessos.getProntos().addLast(processoBCP);
        }

        if (prontosAposBloqueio != null) {
            for (BCP pronto : prontosAposBloqueio) {
                listaDeProcessos.adicionarPronto(pronto);
            }
        }

        if (listaDeProcessos.temProntos()) {
            processoBCP = listaDeProcessos.reordenarProntos();
        }

        addLineToLogFile("Executando processo " + processoBCP.getCabecalho());
        return processoBCP;
    }

    public void executarInstrucao(BCP processoAtual, String com) throws IOException {
        if (com.contains("E/S")) {
            listaDeProcessos.moverParaBloqueado(processoAtual);
            addLineToLogFile("E/S INICIADA EM " + processoAtual.getCabecalho());
        } else if (com.contains("X=")) {
            processoAtual.setRegX(Integer.parseInt(com.substring(2)));
        } else if (com.contains("Y=")) {
            processoAtual.setRegY(Integer.parseInt(com.substring(2)));
        } else if (com.contains("SAIDA")) {
            tabelaDeProcessos.removerProcesso(processoAtual);
            listaDeProcessos.removerPronto(processoAtual);
        }
    }

    private void interromperProcesso(BCP processoBCP, String com, int comExecutadas) throws IOException {
        interrompidos++;
        comExecutadas++;
        addLineToLogFile("Interrompendo " + processoBCP.getCabecalho() + " após " + (comExecutadas) + " instruções");
        if (com.contains("SAIDA")) {
            addLineToLogFile(processoBCP.getCabecalho() + " terminado. X=" + processoBCP.getRegX() + " Y=" + processoBCP.getRegY());
        }
    }

    private void registrarEstatisticas() throws IOException {
        double mediaTrocas = interrompidos / numProcessos;
        double mediaInstrucoes = executados / nQuantum;
        addLineToLogFile(String.format("MEDIA DE TROCAS: %.2f ", mediaTrocas));
        addLineToLogFile(String.format("MEDIA DE INSTRUCOES: %.2f ", mediaInstrucoes));
        addLineToLogFile("QUANTUM: " + quantumTestado);
    }
    public void criarArquivoLog() throws IOException {
        String quantum = (quantumTestado < 10) ? String.format("%02d", quantumTestado) : Integer.toString(quantumTestado);
        String nomeArquivo = "log" + quantum + ".txt";
        if (new File("./log/" + nomeArquivo).exists()) new File("./log/" + nomeArquivo).delete();
        else new File("./log/").mkdir();
        logFile = new RandomAccessFile("./log/" + nomeArquivo, "rw");
    }

    public RandomAccessFile getLogFile() {
        return this.logFile;
    }

    public void addLineToLogFile(String linha) throws IOException {
        escreve.escreve(logFile, linha);
    }

    public void setNumEntradas(int numEntradas) {
        this.numEntradas = numEntradas;
    }

}