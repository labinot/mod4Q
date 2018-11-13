package org.semanticweb.clipper.alch.knots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bato on 6/8/2016.
 */
public class Type1 {
    private String            initializationKey;        //identifies uniquely the knot, contains the sorted array of initial concepts converted to string
    private ArrayList<String> initializationConcepts;   //set of concepts with which the type was initialized
    private ArrayList<String> existentialConcepts;      //existentical concepts consequnce of TBox
    private ArrayList<String> universalConcepts;        //universal concepts consequence of TBox
    private ArrayList<String> deterministicConcepts;    //concepts that come as a consequnce of determinism
    private ArrayList<String> nondeterministicConcepts; //concepts that come as a consequnce of nondeterminism
    private ArrayList<String> concepts;                 //union of all above concepts

    public ArrayList<String> getIniConcepts() {
        return initializationConcepts;
    }

    public ArrayList<String> getExistConcepts() {
        return this.existentialConcepts;
    }

    public ArrayList<String> getUnivConcepts() {
        return this.universalConcepts;
    }

    public ArrayList<String> getDetConcepts() {
        return this.deterministicConcepts;
    }

    public ArrayList<String> getNondetConcepts() {
        return this.nondeterministicConcepts;
    }

    public void addDeterministicConcept(String con) {
        this.concepts.add(con);
        this.deterministicConcepts.add(con);
        if (con.length() >= 3 && con.substring(0, 4).equals("Some"))
            this.existentialConcepts.add(con);
        if (con.length() >= 3 && con.substring(0, 3).equals("All"))
            this.universalConcepts.add(con);
    }

    public void addNonDeterministicConcept(String con) {
        this.concepts.add(con);
        this.nondeterministicConcepts.add(con);
        if (con.length() >= 3 && con.substring(0, 4).equals("Some"))
            this.existentialConcepts.add(con);
        if (con.length() >= 3 && con.substring(0, 3).equals("All"))
            this.universalConcepts.add(con);
    }

    public String getInitializationKey() {
        return this.initializationKey;
    }

    public Type1(ArrayList<String> iniConcepts) {
        ArrayList<String> arrHelper=(ArrayList)iniConcepts.clone();
        Collections.sort(arrHelper);
        this.initializationKey=arrHelper.toString();
        this.initializationConcepts=arrHelper;
        this.concepts = (ArrayList)arrHelper.clone();//clone the values of initializationConcepts to Concepts --represents concepts is an union of iniConcepts and derivedConcepts
        this.deterministicConcepts=new ArrayList<String>();
        this.nondeterministicConcepts=new ArrayList<String>();
        this.existentialConcepts=new ArrayList<String>();
        this.universalConcepts=new ArrayList<String>();
    }

    public ArrayList<String> getConcepts() {
        return this.concepts;
    }

    public void setConcepts(ArrayList<String> concepts) {
        this.concepts = concepts;
    }

    @Override
    public String toString() {
        return "Type{iniConcepts=" + initializationKey +"\n"+
                //",exiConcepts=" + existentialConcepts.toString() +"\n"+
                //",uniConcepts=" + existentialConcepts.toString() +"\n"+
                //",detConcepts=" + existentialConcepts.toString() +"\n"+
                //",ndeConcepts=" + existentialConcepts.toString() +"\n"+
                ",concepts=" + concepts.toString() +"}";
    }
}
