import java.util.*;

public class TabelaProcessos {

    ArrayList<BCP> tabelaProcessos = new ArrayList<>();

    public ArrayList<BCP> getTabelaProcessos() {
        return tabelaProcessos;
    }

    public void adicionarProcesso(BCP processo) {
        tabelaProcessos.add(processo);
    }

    public void removerProcesso(BCP processo) {
        tabelaProcessos.remove(processo);
    }

    public boolean possuiProcessos() {
        return !tabelaProcessos.isEmpty();
    }

    public int size() {
        return tabelaProcessos.size();
    }
}
