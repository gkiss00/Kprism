package helper;

import model.*;
import java.util.*;
import java.io.*;

public class Parser {
    public void parseFile(String path, List<Stock> stocks, List<Processus> processus, List<Stock> optimize) throws Exception {
        File f = new File(path);
        Scanner scan = new Scanner(f);
        while(scan.hasNextLine()) {
            String line = scan.nextLine().replaceAll("\\s+", "");
            if(line.indexOf("#") >= 0){
                line = line.substring(0, line.indexOf("#"));
            }
            if(line.compareTo("") == 0){
                //PASS
            } else if (line.indexOf("optimize") == 0) {
                //OPTIMIZE
                addOptimize(line, optimize);
            } else if (line.indexOf("(") >= 0) {
                //PROCESSUS
                
                addProcess(line, processus);
            } else {
                //STOCK
                addStock(line, stocks);
            }
        }
        scan.close();
    }

    private void addStock(String line, List<Stock> stocks) {
        String[] args = line.split(":");
        Stock stock = new Stock(args[0], Integer.parseInt(args[1]));
        if(!stocks.contains(stock))
            stocks.add(stock);
        else {
            if (stock.qty > stocks.get(stocks.indexOf(stock)).qty)
                stocks.get(stocks.indexOf(stock)).qty = stock.qty;
        }
    }

    private void addProcess(String line, List<Processus> processus) {
        boolean p = false;
        List<Integer> delimiter = new ArrayList<>();
        for (int i = 0; i < line.length(); ++i) {
            if (line.charAt(i) == '(')
                p = true;
            if (line.charAt(i) == ')')
                p = false;
            if (line.charAt(i) == ':' && p == false)
                delimiter.add(i);
        }
        List<Stock> input = new ArrayList<>();
        List<Stock> output = new ArrayList<>();
        String name = line.substring(0, delimiter.get(0));
        String inputs = line.substring(delimiter.get(0) + 1, delimiter.get(1));
        String outputs = line.substring(delimiter.get(1) + 1, delimiter.get(2));
        int cycle = Integer.parseInt(line.substring(delimiter.get(2) + 1, line.length()));
        if(inputs.indexOf("(") >= 0) {
            inputs = inputs.substring(inputs.indexOf("(") + 1, inputs.indexOf(")"));
            String[] tmp = inputs.split(";");
            for (int i = 0; i < tmp.length; ++i) {
                String[] args = tmp[i].split(":");
                input.add(new Stock(args[0], Integer.parseInt(args[1])));
            }
        }
        if(outputs.indexOf("(") >= 0){
            outputs = outputs.substring(outputs.indexOf("(") + 1, outputs.indexOf(")"));
            String[] tmp = outputs.split(";");
            for (int i = 0; i < tmp.length; ++i) {
                String[] args = tmp[i].split(":");
                output.add(new Stock(args[0], Integer.parseInt(args[1])));
            }
        }
        processus.add(new Processus(name, input, output, cycle));
    }

    private void addOptimize(String line, List<Stock> optimize){
        String[] args = line.split(":");
        String[] tmp = args[1].substring(args[1].indexOf("(") + 1, args[1].indexOf(")")).split(";");
        for (int i = 0; i < tmp.length; ++i){
            optimize.add(new Stock(tmp[i], 0));
        }
    }
}
