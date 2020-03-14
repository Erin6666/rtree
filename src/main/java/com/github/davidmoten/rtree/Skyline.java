package com.github.davidmoten.rtree;
import com.github.davidmoten.rtree.internal.EntryDefault;

import java.util.ArrayList;

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

    public void calculateSL(){

    }

    public void add(){

    }
    public void delete(){

    }

}
