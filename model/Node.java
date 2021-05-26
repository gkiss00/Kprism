package model;
import java.util.*;

public class Node implements Comparable{
    public Node parent;
    public Processus process;
    public int g;
    public int h;
    private int f;
    public List<Product> products = new ArrayList<>();;

    public Node(Node parent, List<Product> products, int cost){
        this.parent = parent;
        this.process = null;
        this.products = products;
        this.g = (parent == null ? (0) : (parent.g)) + cost;
        this.h = 0; // heuristic
        this.f = h + g;
    }

    public Node(Node parent, Processus pro){
        this.parent = parent;
        this.process = pro;
        setProduct(parent, pro);
        this.g = (parent == null ? (0) : (parent.g)) + pro.cost;
        this.h = 0; // heuristic
        this.f = h + g;
    }

    public void setH(List<String> opti){
        h = 0;
        for (int i = 0; i < products.size(); ++i){
            if (opti.contains(products.get(i).name))
                h += products.get(i).quantity;
        }
        f = g + h;
    }

    private void setProduct(Node parent, Processus pro) {
        this.products = new ArrayList<>();
        for (int i = 0; i < parent.products.size(); ++i){
            products.add(new Product(parent.products.get(i)));
        }
        applyProcess(pro);
    }

    private void applyProcess(Processus pro){
        for (int i = 0; i < pro.input.size(); ++i){
            try{
                Product p = getCorrespondingProduct(pro.input.get(i));
                p.quantity -= pro.input.get(i).quantity;
                if(p.quantity == 0)
                    products.remove(p);
            } catch (Exception e) {
                // System.out.println("SHIT");
            }
            
        }
        for (int i = 0; i < pro.output.size(); ++i){
            try{
                Product p = getCorrespondingProduct(pro.output.get(i));
                p.quantity += pro.output.get(i).quantity;
            } catch (Exception e){
                Product p = new Product(pro.output.get(i));
                products.add(p);
            }
        }
    }

    private Product getCorrespondingProduct(Product p)throws Exception{
        for (int i = 0; i < products.size(); ++i){
            if (products.get(i).name.compareTo(p.name) == 0)
                return products.get(i);
        }
        throw new Exception("No product with this name");
    }

    public boolean equalsAt(Node node){
        for (int i = 0; i < products.size(); ++i){
            boolean ok = false;
            for (int j = 0; j < node.products.size(); ++j){
                if(products.get(i).name.compareTo(node.products.get(j).name) == 0
                && products.get(i).quantity >= node.products.get(j).quantity){
                    ok = true;
                    break;
                }
            }
            if(!ok)
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o){
        if(o == null)
            return false;
        if(o instanceof Node){
            Node node = (Node)o;
            for (int i = 0; i < products.size(); ++i){
                if(!node.products.contains(products.get(i)))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        String str = "";
        for (int i = 0; i < products.size(); ++i){
            str += products.get(i).name + " => " + products.get(i).quantity + "\n";
            
        }
        return str;
    }

    @Override
    public int compareTo(Object o) {
        // TODO Auto-generated method stub
        if(o == null)
            return -1;
        if (o instanceof Node){
            Node node = (Node)o;
            return  node.h - h;
        } else {
            return -1;
        }
    }
}
