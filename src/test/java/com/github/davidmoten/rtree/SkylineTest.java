package com.github.davidmoten.rtree;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Skyline;
import static com.github.davidmoten.rtree.Entries.entry;
import static com.github.davidmoten.rtree.geometry.Geometries.*;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.internal.EntryDefault;
import com.github.davidmoten.rtree.internal.LeafDefault;
import com.github.davidmoten.rtree.internal.NonLeafDefault;
import rx.Observable;
import org.junit.Test;
import java.io.*;
import java.util.*;

import rx.Observable;
import rx.functions.*;
import rx.schedulers.Schedulers;

public class SkylineTest {
    private static Rectangle r(float n, float m) {
        return rectangle(n, m, n + 1, m + 1);
    }
    private static RTree<Integer, Point> tree;
    private static Skyline sl;
    private static ArrayList skyline;
    @Test
    public void calculateTest() throws FileNotFoundException,IOException{
       // File file = new File("/Users/chuci/github/rtree/target/dataset1.txt");
        File file = new File("src/test/resources/dataset1.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        tree = RTree.create();
        int count = 0;
        BufferedReader br2 = new BufferedReader(new FileReader(file));
        double x_max = Double.MIN_VALUE;
        double y_max = Double.MIN_VALUE;
        while ((st = br2.readLine()) != null){
            String[] m = st.split(" ");
            double x = Double.parseDouble(m[0]);
            double y = Double.parseDouble(m[1]);
            tree = tree.add(1, point(x, y));
            count++;
            if(x > x_max) x_max = x;
            if(y > y_max) y_max = y;
        }
        tree.visualize(1000,1000)
                .save("target/originalTree.png");
        sl = new Skyline(tree);
        sl.setMaxValue(x_max,y_max);
        sl.setCount(count);
        skyline = sl.getSL();
        String filename = "target/SkylineList.txt";
        sl.WriteFile(filename);
        System.out.println("The original skyline is:");
        System.out.println(skyline);
        System.out.println("========================");
        RTree<Integer, Point> new_rtree =  RTree.create();
        for (Object each:skyline){
            new_rtree = new_rtree.add(1, point(((EntryDefault) each).geometry().mbr().x1(),-((EntryDefault) each).geometry().mbr().y1()));
        }
        new_rtree.visualize(1000,1000)
                .save("target/SkylineTree.png");

    }

    @Test
    public void addTest()throws FileNotFoundException,IOException{
        //add point
//        sl = new Skyline(tree, true);
        calculateTest();
        //Read the insert point from file
        String filename = "src/test/resources/insertpoint.txt";
        String st;
        BufferedReader br2 = new BufferedReader(new FileReader(filename));
        ArrayList<Point> insert_point = new ArrayList<>();
        while ((st = br2.readLine()) != null){
            String[] m = st.split(" ");
            double x = Double.parseDouble(m[0]);
            double y = Double.parseDouble(m[1]);

            Point p = Geometries.point(x, y);
            insert_point.add(p);

        }
//        double p_x = 33.919998168945;
//        double p_y = 20.079999923706;
//        Point p =  Geometries.point(p_x, p_y);
        for(Point p: insert_point){
            sl.add(p);
            skyline = sl.getSL();
            tree = sl.getRtree();
            System.out.println("========================");
            System.out.println("The skyline after adding a point is:");
            System.out.println(skyline);

        }

        System.out.println("========================");
        RTree<Integer, Point> new_rtree =  RTree.create();
        for (Object each:skyline){
            new_rtree = new_rtree.add(1, point(((EntryDefault) each).geometry().mbr().x1(),-((EntryDefault) each).geometry().mbr().y1()));
        }
        new_rtree.visualize(1000,1000)
                .save("target/AddSkylineTree.png");

        filename = "target/AddSkylineList.txt";
        sl.WriteFile(filename);




    }

    @Test
    public void deleteTest()throws FileNotFoundException,IOException{
        //delete
//        sl = new Skyline(tree, true);
        calculateTest();
        //Read the delete point from file
        String filename = "src/test/resources/deletepoint.txt";
        String st;
        BufferedReader br2 = new BufferedReader(new FileReader(filename));
        ArrayList<Point> delete_point = new ArrayList<>();
        while ((st = br2.readLine()) != null){
            String[] m = st.split(" ");
            double x = Double.parseDouble(m[0]);
            double y = Double.parseDouble(m[1]);

            Point p = Geometries.point(x, y);
            delete_point.add(p);

        }
//        double p_x = 33.91999816894531;
//        double p_y = 20.079999923706055;
//        Point p = Geometries.point(p_x, p_y);

        for(Point p: delete_point){
            System.out.println("========================");

            sl.delete(p);

            skyline = sl.getSL();
            System.out.println("The skyline after deleting a point is:");
            System.out.println(skyline);
        }

        System.out.println("========================");
        RTree<Integer, Point> new_rtree =  RTree.create();
        for (Object each:skyline){
            new_rtree = new_rtree.add(1, point(((EntryDefault) each).geometry().mbr().x1(),-((EntryDefault) each).geometry().mbr().y1()));
        }
        new_rtree.visualize(1000,1000)
                .save("target/DeleteSkylineTree.png");

        filename = "target/DeleteSkylineList.txt";
        sl.WriteFile(filename);
    }
}
