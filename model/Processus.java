package model;
import java.util.*;

public class Processus {
    public String name;
    public List<Product> input;
    public List<Product> output;
    public int cost;

    public Processus(String name, List<Product> input, List<Product> output, int delay){
        this.name = name;
        this.input = input;
        this.output = output;
        this.cost = delay;
    }

    public boolean isPossible(Node node){
        for (int i = 0; i < input.size(); ++i){
            boolean ok = false;
            for (int j = 0; j < node.products.size(); ++j){
                if (node.products.get(j).name.compareTo(input.get(i).name) == 0
                && node.products.get(j).quantity >= input.get(i).quantity){
                    ok = true;
                    break;
                }
            }
            if (!ok)
                return false;
        }
        return true;
    }

    @Override
    public String toString(){
        String str = name + "\n";
        for (int i = 0; i < input.size(); ++i){
            str += input.get(i).quantity + " " + input.get(i).name + "\n";
        }
        str += "=>\n";
        for (int i = 0; i < output.size(); ++i){
            str += output.get(i).quantity + " " + output.get(i).name + "\n";
        }
        return str;
    }

    @Override
    public boolean equals(Object o){
        if (o == null)
            return false;
        if (o instanceof Processus){
            Processus p = (Processus)o;
            return (p.name.compareTo(name) == 0);
        } else {
            return false;
        }
    }
}
