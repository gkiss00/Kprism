import java.util.*;
import model.*;
import java.io.*;

public class KrpsimVerif {
    public static final String UTF8_BOM = "\uFEFF";
    public static List<Product> startProducts = new ArrayList<>();
    public static List<Processus> processus = new ArrayList<>();
    public static List<String> optimize = new ArrayList<>();
    public static Node best;
    public static List<Product> finalProducts = new ArrayList<>();

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

    private static Processus getGoodProcess(String name)throws Exception{
        for (int i = 0; i < processus.size(); ++i){
            if (processus.get(i).name.compareTo(name) == 0)
                return processus.get(i);
        }
        throw new Exception("No process named " + name);
    }

    private static void slow(){
        try {
            for (int i = 0; i < 3; ++i){
                Thread.sleep(300);
                System.out.print(". ");
            }
            System.out.println(".");
        }catch (Exception e){

        }
    }

    private static void checkIfCorrect() throws Exception{
        for (int i = 0; i < finalProducts.size(); ++i){
            if (!best.products.contains(finalProducts.get(i)))
                throw new Exception("The final product doesn't corresp");
        }
    }

    private static void verif(String[]args) throws Exception{
        boolean first = true;
        File f = new File(args[1]);
        boolean finalState = false;
        int cost = 0;
        FileInputStream fis = new FileInputStream(args[1]);
        BufferedReader r = new BufferedReader(new InputStreamReader(fis,
                "UTF16"));
        for (String line = ""; (line = r.readLine()) != null;) {
            line = line.trim();
            if (first == true){
                //s = removeUTF8BOM(s);
                first = false;
            } else {
                System.out.println(line);
                if(line.compareTo("* * * * * * * * * *") == 0){
                    finalState = true;
                } else if (line.length() == 0 || line.charAt(0) == '#'){
                    //PASS
                } else if (!finalState){
                    //Check if process can be executed
                    slow();
                    String[] arg = line.split("\\|");
                    Processus p = getGoodProcess(arg[1].trim());
                    best.applyProcessSecure(p);
                    if(Integer.parseInt(arg[0].trim()) != cost)
                        throw new Exception("Cost value is incorrect");
                    cost += p.cost;
                } else {
                    String[] arg = line.split("=>");
                    Product p = new Product(arg[0].trim(), Integer.parseInt(arg[1].trim()));
                    finalProducts.add(p);
                }
            }
        }
        r.close();
        checkIfCorrect();
        System.out.println("Ok");
    }

    private static void getInput(String[]args) throws Exception{
        if (args.length != 2)
            throw new Exception("Program must take two args <file> <verif>");
        File f = new File(args[0]);
        Scanner scan = new Scanner(f);
        while(scan.hasNextLine()){
            String line = scan.nextLine().trim();
            parseLine(line);
        }
        Node n = new Node(null, startProducts, 0);
        n.setH(optimize);
        best = n;
        scan.close();
    }
    public static void main(String[]args){
        try {
            getInput(args);
            verif(args);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
