package model;
import java.util.*;

public class Processus {
    public String name;
    public int nbCycle;
    public List<Stock> input;
    public List<Stock> output;

    public Processus(String name, List<Stock> input, List<Stock> output, int nbCycle){
        this.name = name;
        this.input = input;
        this.output = output;
        this.nbCycle = nbCycle;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof Processus))
            return false;
        Processus processsus = (Processus)obj;
        return (processsus.name.compareTo(name) == 0);
    }

    @Override
    public String toString(){
        String str = name;
        str += "\ninputs:\n";
        for (int i = 0; i < input.size(); ++i) {
            str +=  "\t" + input.get(i) + "\n";
        }
        str += "\noutputs:\n";
        for (int i = 0; i < output.size(); ++i) {
            str +=  "\t" + output.get(i) + "\n";
        }
        str += nbCycle;
        return str;
    }
}
