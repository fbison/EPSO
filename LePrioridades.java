import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class LePrioridades {
    public int[] prioridades;
    public LePrioridades(String arquivo, int qntProcessos)throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(arquivo));
        this.prioridades = new int[qntProcessos];
        int i = 0;
        while(scanner.hasNextInt()){
            this.prioridades[i]= scanner.nextInt();
            i++;
        }
    }
}
