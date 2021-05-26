import model.*;
import helper.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.io.*;

public class Krpsim {
    public static int timeout;
    public static long startTime;
    public static long endTime;
    public static Semaphore semaphore = new Semaphore(1);
    public static List<Product> startProducts = new ArrayList<>();
    public static List<Processus> processus = new ArrayList<>();
    public static List<Node> closedList = new ArrayList<>();
    public static PriorityQueue<Node> openList = new PriorityQueue<>();
    public static List<String> optimize = new ArrayList<>();
    public static Node best;

    private static void ouputTime(){
        System.out.println("#Time: " + 
                            (endTime - startTime) / 1000 +
                            " secondes and " +
                            (endTime - startTime) % 1000 +
                            " millisecondes");
    }

    private static void ouputPath(){
        List<String> list = new ArrayList<>();
        Node actual = best;
        Node prec = actual.parent;
        while(prec != null){
            //System.out.println(prec + " " + actual.process + " " + prec.parent);
            String str = prec.g + " | " + actual.process.name + "\n";
            actual = actual.parent;
            prec = prec.parent;
            list.add(str);
        }
        for (int i = list.size() - 1; i >= 0; --i){
            System.out.println(list.get(i));
        }
    }

    private static void output(){
        try{
            semaphore.acquire();
            ouputTime();
            ouputPath();
            System.out.println("* * * * * * * * * *");
            System.out.println("#total cost: " + best.g);
            System.out.println("#store:");
            System.out.println(best);
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }

    private static boolean checkInfiniteLoop(){
        Node previous = best.parent;
        int c = 0;
        while(previous != null){
            if(best.equalsAt(previous)){
                ++c;
            }
            previous = previous.parent;
        }
        //System.out.println("****BEST***");
        //System.out.println(best);
        try {
        //Thread.sleep(500);

        }catch(Exception e){

        }
        return (c >= 5);
    }

    private static void getBest(Node node){
        //System.out.println("****NODE***");
        //System.out.println(node);
        //System.out.println("****COMP***");
        //System.out.println(best.compareTo(node));
        if (best.compareTo(node) > 0)
            best = node;
        if (best.compareTo(node) == 0){
            if(best.g > node.g)
                best = node;
        }
    }

    private static void getNextNode(Node parent, int index){
        Node next = new Node(parent, processus.get(index));
        next.setH(optimize);
        if(!closedList.contains(next)){
            //System.out.println("add");
            openList.add(next);
        } else {
            //System.out.println("boucle infinie");
            //System.exit(0);
        }
    }

    private static List<Integer> getProcess(Node node){
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < processus.size(); ++i){
            if(processus.get(i).isPossible(node)){
                list.add(i);
            }
        }
        return list;
    }

    private static void run(){
        Thread thread = new Thread(new TimeChecker(timeout, semaphore));
        thread.start();
        startTime = System.currentTimeMillis();
        while(openList.size() != 0){
            //get the first node
            Node node = openList.poll();
            //get all processus possible
            List<Integer> l = getProcess(node);
            //System.out.println("Process possible: " + l.size());
            //get all next states
            for (int i = 0; i < l.size(); ++i){
                getNextNode(node, l.get(i));
            }
            //check for the best
            getBest(node);
            //check infinite loop
            if (checkInfiniteLoop()){
                System.out.println("#Infinite loop");
                break;
            }
            //remove actual state
            if (!closedList.contains(node))
                closedList.add(node);
        }
        endTime = System.currentTimeMillis();
        output();
    }

    private static void parseLine(String line){
        if(line.length() == 0 || line.charAt(0) ==  '#'){

        } else {
            String[] args = line.split("\\|");
            if(args.length == 2){
                if(args[0].trim().compareTo("optimize") == 0){
                    String[] opts = args[1].trim().split(",");
                    for (int i = 0; i < opts.length; ++i){
                        optimize.add(opts[i].split("\\:")[1].trim());
                    }
                } else {
                    startProducts.add(new Product(args[0].trim(), Integer.parseInt(args[1].trim())));
                }
            } else if (args.length == 3) {
                String name = args[0].trim();
                String[] inputs = args[1].split("=>")[0].split(",");
                String[] outputs = args[1].split("=>")[1].split(",");
                int delay = Integer.parseInt(args[2].trim());

                List<Product> l1 = new ArrayList<>();
                List<Product> l2 = new ArrayList<>();
                for (int i = 0; i < inputs.length; ++i){
                    l1.add(new Product(inputs[i].split("\\:")[0].trim(), Integer.parseInt(inputs[i].split("\\:")[1].trim())));
                }
                for (int i = 0; i < outputs.length; ++i){
                    l2.add(new Product(outputs[i].split("\\:")[0].trim(), Integer.parseInt(outputs[i].split("\\:")[1].trim())));
                }
                processus.add(new Processus(name,  l1, l2, delay));
            }
        }
    }

    private static void getInput(String[]args) throws Exception{
        if (args.length != 2)
            throw new Exception("Program must take two args <file> <timeout>");
        timeout = Integer.parseInt(args[1].trim());
        File f = new File(args[0]);
        Scanner scan = new Scanner(f);
        while(scan.hasNextLine()){
            String line = scan.nextLine().trim();
            parseLine(line);
        }
        Node n = new Node(null, startProducts, 0);
        n.setH(optimize);
        best = n;
        openList.add(n);
        scan.close();
    }
    public static void main(String args[]) {
        try {
            getInput(args);
            run();
        } catch (Exception e){
            System.out.println(e + e.getMessage());
        }
    }
}