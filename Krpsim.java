import java.util.*;
import model.*;
import helper.*;

public class Krpsim {
    public static int nbCycle;
    public static List<Stock> stocks = new ArrayList<>();
    public static List<Processus> processus = new ArrayList<>();
    public static List<Stock> optimize = new ArrayList<>();
    public static State initialState;
    public static PriorityQueue<State> openList = new PriorityQueue<>();
    public static List<State> closedList = new ArrayList<>();
    public static State finalState;

    private static void log(String str){
        System.out.println(str);
    }

    private static void printData(){
        log("* * * * * STOCK: " + stocks.size() + " * * * * *");
        for (int i = 0; i < stocks.size(); ++i){
            System.out.println(stocks.get(i));
        }
        log("* * * * * PROCESSUS: " + processus.size() + " * * * * *");
        for (int i = 0; i < processus.size(); ++i){
            System.out.println(processus.get(i));
        }
        log("* * * * * OPTIMIZE: " + optimize.size() + " * * * * *");
        for (int i = 0; i < optimize.size(); ++i){
            System.out.println(optimize.get(i));
        }
    }

    private static boolean possible(List<Stock> needed, List<Stock> aviable){
        for (int i = 0; i < needed.size(); ++i){
            boolean ok = false;
            for (int j = 0; j < aviable.size(); ++j){
                if (needed.get(i).equals(aviable.get(j))
                    && aviable.get(j).qty >= needed.get(i).qty)
                    ok = true;
            }
            if(!ok)
                return false;
        }
        return true;
    }

    private static List<Processus> getAviableProcessus(List<Stock> l){
        List<Processus> p = new ArrayList<>();
        for (int i = 0; i < processus.size(); ++i){
            if (possible(processus.get(i).input, l))
                p.add(processus.get(i));
        }
        return p;
    }

    private static void getNextState(State s){
        Random rand = new Random();
        for (int i = 0; i < 40; ++i){
            List<Processus> aviable = getAviableProcessus(s.stocks);
            State newState = new State(s, optimize);
            //while avaible processus
            while(aviable.size() != 0){

                newState.applyInputProcess(aviable.get(rand.nextInt(aviable.size())));
                aviable = getAviableProcessus(newState.stocks);
                try {
                    //Thread.sleep(1000);
                } catch(Exception e){}
            }
            newState.applyOutputProcess();
            if(!closedList.contains(newState) && !openList.contains(newState) && newState.cycle <= nbCycle)
                openList.add(newState);
        }
    }

    private static void solve(){
        //Set initial state
        initialState = new State(stocks, optimize);
        finalState = initialState;
        openList.add(initialState);
        while (openList.size() != 0){
            System.out.println("WHILE: " + openList.size());
            State s = openList.poll();
            
            //get next states
            getNextState(s);
            //set best state
            System.out.println(s);
            System.out.println(finalState);
            if (s.compareStock(finalState)){
                System.out.println(s.compareStock(finalState) + " " + s.compareTo(finalState));
                if(s.compareTo(finalState) < 0)
                    finalState = s;
            } else {
                System.out.println(s.compareStock(finalState) + " " + s.compareTo(finalState));
                if(s.compareTo(finalState) < 0 && s.getScore() != finalState.getScore())
                    finalState = s;
            }
            //remove previous state
            closedList.add(s);
        }
    }

    private static void removeUselessProcessus(){
        List<Stock> l = new ArrayList<>();
        for (int i = 0; i < optimize.size(); ++i) {
            l.add(optimize.get(i));
        }
        //for each process
        for (int i = 0; i < processus.size(); ++i){
            //for each stock needed
            for(int k = 0; k < l.size(); ++k){
                //if process output contains needed
                if (processus.get(i).output.contains(l.get(k))) {
                    //add process input to needed
                    boolean nw = false;
                    for (int j = 0; j < processus.get(i).input.size(); ++j){
                        if(!l.contains(processus.get(i).input.get(j))){
                            l.add(processus.get(i).input.get(j));
                            nw = true;
                        }
                    }
                    if (nw){
                        i = -1;
                        break;
                    }
                }
            }
        }
        List<Processus> p = new ArrayList<>();
        //for each process
        for (int i = 0; i < processus.size(); ++i){
            boolean ok = false;
            //for each stock needed
            for(int k = 0; k < l.size(); ++k){
                //if process output contains needed
                if (processus.get(i).output.contains(l.get(k))) {
                    ok = true;
                    break;
                }
            }
            if(!ok){
                System.out.println("REMOVE: " + processus.get(i).name);
                p.add(processus.get(i));
            }
        }
        for (int i = 0; i < p.size(); ++i) {
            processus.remove(p.get(i));
        }
    }

    private static void prepProcessing(){
        removeUselessProcessus();
    }

    private static void parseFile(String path) throws Exception{
        Parser parser = new Parser();
        parser.parseFile(path, stocks, processus, optimize);
    }

    private static void checkError(String[]args) throws Exception{
        if (args.length != 2)
            throw new Exception("The program takes two args <file> <nb_cycle>");
        nbCycle = Integer.parseInt(args[1]);
    }
    public static void main(String[]args){
        try {
            checkError(args);
            parseFile(args[0]);
            printData();
            prepProcessing();
            System.out.println("* * * * * * * * * * * * * * * * * * *");
            printData();
            solve();
            System.out.println(finalState);
        } catch(Exception e) {
            System.out.println(e);
            System.out.println(e.getMessage());
        }
    }
}