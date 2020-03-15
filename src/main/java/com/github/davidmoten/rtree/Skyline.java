package com.github.davidmoten.rtree;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.EntryDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;
import com.github.davidmoten.rtree.internal.LeafDefault;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

public class Skyline {
    private RTree tree;
    private ArrayList<EntryDefault> SkylineList;
    public Skyline(RTree t){
        this.tree = t;
    }

    public RTree getRtree(){
        return this.tree;
    }
    public ArrayList<EntryDefault> getSL(){
        return this.SkylineList;
    }

    private double MinDist(Rectangle mbr){
        return Math.sqrt(mbr.x1() * mbr.x1() + mbr.y1() * mbr.y1());
    }

    public void calculateSL(){
        PriorityQueue<ArrayList> pq = new PriorityQueue<>(Comparator.comparingDouble(x -> (double) x.get(0)));
        ArrayList<EntryDefault> S = new ArrayList<>();
        if (!this.tree.root().isPresent()){
            this.SkylineList = S;
            return;
        }
        Object root_next = tree.root().get();
        double md = MinDist((Rectangle) ((Node) root_next).geometry());
        ArrayList temp = new ArrayList(Arrays.asList(md, root_next));
        pq.add(temp);
        int count = 0;

        while (!pq.isEmpty()){
            ArrayList now = pq.poll();
            Object obj = now.get(1);
            if (obj.getClass().equals(NonLeafDefault.class)){
                for (Object each:((NonLeafDefault) obj).children()){
                    double md_each = MinDist((Rectangle) ((Node) each).geometry());
                    boolean flag = true;
                    for (EntryDefault item: S){
                        double item_x = item.geometry().mbr().x1();
                        double item_y = item.geometry().mbr().y1();
                        double child_x = ((Node) each).geometry().mbr().x1();
                        double child_y = ((Node) each).geometry().mbr().y1();
                        if (item_x < child_x && item_y < child_y){
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        pq.add(new ArrayList(Arrays.asList(md_each, each)));
                    }
                }
            } else if (obj.getClass().equals(LeafDefault.class)){
                for (Object each:((LeafDefault) obj).entries()){
                    double md_each = MinDist((Rectangle) ((EntryDefault) each).geometry());
                    boolean flag = true;
                    for (EntryDefault item: S){
                        double item_x = item.geometry().mbr().x1();
                        double item_y = item.geometry().mbr().y1();
                        double child_x = ((EntryDefault) each).geometry().mbr().x1();
                        double child_y = ((EntryDefault) each).geometry().mbr().y1();
                        if (item_x < child_x && item_y < child_y){
                            flag = false;
                            break;
                        }
                    }
                    if (flag){
                        pq.add(new ArrayList(Arrays.asList(md_each, each)));
                    }
                }
            } else if (obj.getClass().equals(EntryDefault.class)){
                boolean flag = true;
                for (EntryDefault item: S){
                    double item_x = item.geometry().mbr().x1();
                    double item_y = item.geometry().mbr().y1();
                    double obj_x = ((EntryDefault) obj).geometry().mbr().x1();
                    double obj_y = ((EntryDefault) obj).geometry().mbr().y1();
                    if (item_x < obj_x && item_y < obj_y){
                        flag = false;
                        break;
                    }
                }
                if (flag){
                    S.add((EntryDefault) obj);
                }
            } else{
                System.out.println("while loop error");
                return;
            }
        }
        this.SkylineList = S;
        return;
    }

    public void add(){

    }
    public void delete(){

    }

}
