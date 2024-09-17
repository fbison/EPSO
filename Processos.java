import java.util.*;

public class Processos {

    private ArrayList<BCP> bloqueados = new ArrayList<>();
    private LinkedList<BCP> prontos = new LinkedList<>();

    public ArrayList<BCP> getBloqueados() {
        return this.bloqueados;
    }

    public LinkedList<BCP> getProntos() {
        return this.prontos;
    }

    public void adicionarPronto(BCP processo) {
        processo.setEstadoProcesso("PRONTO");
        prontos.add(processo);
    }

    public void moverParaBloqueado(BCP processo) {
        processo.setEstadoProcesso("BLOQUEADO");
        processo.setStatusBloqueado("E/S INICIADA");
        prontos.remove(processo);
        bloqueados.add(processo);
    }

    public void removerPronto(BCP processo) {
        prontos.remove(processo);
    }

    public void removerBloqueado(BCP processo) {
        bloqueados.remove(processo);
    }

    // Reordenar a lista de prontos após troca de processo
    public BCP reordenarProntos() {
        Collections.sort(prontos, new ComparadorPrioridade()); // se der problema talvez seja isso, verificar
        return prontos.getFirst(); // Retorna o próximo processo para execução
    }

    public boolean temProntos() {
        return !prontos.isEmpty();
    }

    public boolean temBloqueados() {
        return !bloqueados.isEmpty();
    }

    public boolean estaPronto(BCP processo) {
        return prontos.contains(processo);
    }
}
