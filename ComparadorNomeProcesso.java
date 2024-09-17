import java.util.*;

public class ComparadorNomeProcesso implements Comparator<BCP>{

    @Override 
    public int compare(BCP o1, BCP o2){
        if(o1.getNomeArquivo().length() < o2.getNomeArquivo().length()) return o1.getNomeArquivo().length() - o2.getNomeArquivo().length();
        return o1.getNomeArquivo().compareTo(o2.getNomeArquivo());
    }
}