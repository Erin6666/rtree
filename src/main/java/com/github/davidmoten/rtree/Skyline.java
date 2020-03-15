package com.github.davidmoten.rtree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.EntryDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;
import com.github.davidmoten.rtree.internal.LeafDefault;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

import static com.github.davidmoten.rtree.geometry.Geometries.point;

public class Skyline {
    private RTree tree;
    private ArrayList<EntryDefault> SkylineList;
    private int count;
    private double x_max;
    private  double y_max;
    public Skyline(RTree t){
        this.tree = t;

    }
    public void setCount(int count){
        this.count = count;
    }
    public void setMaxValue(int x, int y){
        this.x_max = x;
        this.y_max = y;
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

    public int getCount(){
        return this.count;
    }

    public double getX_max(){
        return this.x_max;
    }

    public double getY_max(){
        return this.y_max;
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

    public void add(Point p){
        ArrayList<EntryDefault> L = new ArrayList<>();
        ArrayList<EntryDefault> S = getSL();
        boolean flag = false;
        for(EntryDefault item: S){
            if(p.x() > item.geometry().mbr().x1() && p.y() > item.geometry().mbr().y1()){
                flag = false;
                break;
            }
            else if(p.x() < item.geometry().mbr().x1() && p.y() < item.geometry().mbr().y1()){
                L.add(item);
            }
            flag = true;
        }

        for(EntryDefault item: L){
            S.remove(item);
        }
        if(flag){
            this.count += 1;
            EntryDefault ed = new EntryDefault(getCount(), point(p.x(),p.y()));
            this.tree.add(getCount(), point(p.x(),p.y()));
            S.add(ed);
        }

    }
    public void delete(Point p){
        Observable leaf = this.tree.search(p);
        this.tree.delete(leaf, true);

        for(EntryDefault entry: SkylineList){
            if(entry.geometry().mbr().x1() == p.x() && entry.geometry().mbr().y1() == p.y()){
                SkylineList.remove(entry);
                break;
            }
        }

        PriorityQueue<Point> pq = ElectionRange(p);
        ArrayList<Point> L = new ArrayList<>();

        while(!pq.isEmpty()){
            Point curr_p = pq.poll();
            boolean flag = true;
            for(Point item: L){
                if(curr_p.x() > item.x() && curr_p.y() > item.y()){
                    flag = false;
                    break;
                }
            }

            if(flag){
                L.add(curr_p);
            }
        }

        for(Point item: L){
            count += 1;
            EntryDefault ed = new EntryDefault(getCount(), point(item.x(),item.y()));
            SkylineList.add(ed);
        }


    }

    public PriorityQueue<Point> ElectionRange(Point p){
        PriorityQueue<Point> pq = new PriorityQueue<>(Comparator.comparingDouble(x -> (double) Math.sqrt(x.x() + Math.sqrt(x.y()))));
//        Object obj = tree.entries();
        Object obj = tree.root().get();
        for(EntryDefault entry: SkylineList){

            if(entry.geometry().mbr().x1() > p.x() && entry.geometry().mbr().x1() < x_max){
                x_max = entry.geometry().mbr().x1();
            }
            if(entry.geometry().mbr().y1() > p.y() && entry.geometry().mbr().y1() < y_max){
                y_max = entry.geometry().mbr().y1();
            }

        }

        FindRangePoint(obj,pq,  p.x(), p.y(), x_max, y_max);
        return pq;
    }

    public void FindRangePoint(Object obj, PriorityQueue<Point> pq, double x_0, double y_0, double x_1, double y_1){
        if(obj.getClass().equals((LeafDefault.class))){
            double x = ((LeafDefault) obj).geometry().mbr().x1();
            double y = ((LeafDefault) obj).geometry().mbr().y1();
            if(x > x_0 && x < x_1 && y > y_0 && y < y_1){
                Point p = point(x,y);
                pq.add(p);
            }
        }
        else{
            for (Object child:((NonLeafDefault) obj).children()){
                FindRangePoint(child, pq, x_0, y_0, x_1, y_1);
            }
        }
    }

}
