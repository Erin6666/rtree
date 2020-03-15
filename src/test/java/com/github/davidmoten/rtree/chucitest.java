package com.github.davidmoten.rtree;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.Skyline;
import static com.github.davidmoten.rtree.Entries.entry;
import static com.github.davidmoten.rtree.geometry.Geometries.*;

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

public class chucitest {
    private static Rectangle r(float n, float m) {
        return rectangle(n, m, n + 1, m + 1);
    }
    @Test
    public void exampletest() throws FileNotFoundException,IOException{
        File file = new File("/Users/chuci/github/rtree/target/dataset1.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        RTree<Integer, Point> tree = RTree.create();
        int count = 0;
        BufferedReader br2 = new BufferedReader(new FileReader(file));
        while ((st = br2.readLine()) != null){
            String[] m = st.split(" ");
            double x = Float.parseFloat(m[0]);
            double y = Float.parseFloat(m[1]);
            tree = tree.add(count, point(x, y));
            count++;
        }
        tree.visualize(1000,1000)
                .save("target/originalTree.png");
        Skyline sl = new Skyline(tree);
        sl.calculateSL();
        ArrayList skyline = sl.getSL();
        System.out.println(skyline);
        RTree<Integer, Point> new_rtree =  RTree.create();
        for (Object each:skyline){
            new_rtree = new_rtree.add(1, point(((EntryDefault) each).geometry().mbr().x1(),((EntryDefault) each).geometry().mbr().y1()));
        }
        new_rtree.visualize(1000,1000)
                .save("target/SkylineTree.png");

    }
}
