package org.semanticweb.clipper.alch.knots;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bato on 6/8/2016.
 */
public class Type {
    ArrayList<String> initializationConcepts; //initial concepts with which a type is initialized
    ArrayList<String> Concepts;    //union of iniConcepts and derConcepts


    public Type() {
        this.initializationConcepts = new ArrayList<String>();
        this.Concepts = new ArrayList<String>();
    }

    public Type(ArrayList<String> iniConcepts) {
        this.initializationConcepts = iniConcepts;
        //this.derivedConcepts = new ArrayList<String>();
        //clone the values of initializationConcepts to Concepts --represents concepts is an union of iniConcepts and derivedConcepts
        this.Concepts = (ArrayList)iniConcepts.clone();
    }

    public ArrayList<String> getIniConcepts() {

        return this.initializationConcepts;
    }

    public void setIniConcepts(ArrayList<String> iniConcepts) {

        this.initializationConcepts = iniConcepts;
    }

    public ArrayList<String> getConcepts() {
        return this.Concepts;
    }

    public void setConcepts(ArrayList<String> concepts) {

        this.Concepts = concepts;
    }

    @Override
    public String toString() {
        return "Type{" +
                "initializationConcepts=" + initializationConcepts +
                ", Concepts=" + Concepts +
                '}';
    }
}
