import java.util.Comparator;

public class ComparadorPrioridade implements Comparator<BCP> {
    @Override
    public int compare(BCP o1, BCP o2){
        return Integer.compare(o2.getPrioridadeArquivo(), o1.getPrioridadeArquivo());
    }
}
