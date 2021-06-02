package model;
import java.util.*;

public class State implements Comparable{
    public State parent = null;
    public int cycle = 0;
    public List<Stock> opti;
    public List<Stock> stocks = new ArrayList<>();
    public List<Processus> applied = new ArrayList<>();
    
    public State(List<Stock> s, List<Stock> opti) {
        for (int i = 0; i < s.size(); ++i){
            this.stocks.add(new Stock(s.get(i)));
        }
        this.opti = opti;
    }

    public State(State state, List<Stock> opti){
        this.cycle = state.cycle;
        this.opti = opti;
        for (int i = 0; i < state.stocks.size(); ++i){
            this.stocks.add(new Stock(state.stocks.get(i)));
        }
    }

    public void applyInputProcess(Processus p){
        for (int i = 0; i < p.input.size(); ++i){
            stocks.get(stocks.indexOf(p.input.get(i))).qty -= p.input.get(i).qty;
            if(stocks.get(stocks.indexOf(p.input.get(i))).qty == 0)
                stocks.remove(p.input.get(i));
        }
        applied.add(p);
    }

    public void applyOutputProcess(){
        int cost = 0;
        for (int i = 0; i < applied.size(); ++i){
            if (cost < applied.get(i).nbCycle)
                cost = applied.get(i).nbCycle;
            for (int k = 0; k < applied.get(i).output.size(); ++k){
                if(stocks.contains(applied.get(i).output.get(k))){
                    stocks.get(stocks.indexOf(applied.get(i).output.get(k))).qty += applied.get(i).output.get(k).qty;
                } else {
                    stocks.add(new Stock(applied.get(i).output.get(k)));
                }
            }
        }
        this.cycle += cost;
    }

    public int getScore(){
        int score = 0;
        for (int i = 0; i < opti.size(); ++i){
            if(stocks.contains(opti.get(i)))
                score += stocks.get(stocks.indexOf(opti.get(i))).qty;
        }
        return score;
    }

    public boolean compareStock(State s){
        for (int i = 0; i < stocks.size(); ++i){
            if (!s.stocks.contains(stocks.get(i)) ||
            s.stocks.get(s.stocks.indexOf(stocks.get(i))).qty != stocks.get(i).qty)
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o){
        if(o ==null)
            return false;
        if(!(o instanceof State))
            return false;
        State state = (State)o;
        return compareStock(state) && cycle == state.cycle;
    }

    @Override
    public int compareTo(Object o) {
        if(o ==null)
            return -1;
        if(!(o instanceof State))
            return -1;
        State state = (State)o;
        int score1 = getScore();
        int score2 = state.getScore();
        if(score1 == score2)
            return cycle - state.cycle;
        return score2 - score1;
    }
    @Override
    public String toString(){
        String str = cycle + "\n";
        for (int i = 0; i < stocks.size(); ++i){
            str += stocks.get(i) + "\n";
        }
        return str;
    }
}
