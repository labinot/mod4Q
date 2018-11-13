package org.semanticweb.clipper.alch.knots;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bato on 6/8/2016.
 */
public class Knot {
    private boolean droped;
    private boolean proccessForDeterministicConsequences;
    private boolean proccessForNonDeterministicConsequences;
    private boolean proccessSuccessors;
    public Type root; //uses the class Type
    public ArrayList<Successor> successors;

    @Override
    public String toString() {
        return "Knot{" +
                "dropped="+ droped +
                " ,root:=[" + root.toString() +
                "], successors:=[" + successors.toString() +
                "], proccessForDeterministicConsequences=" + proccessForDeterministicConsequences +
                ", proccessForNonDeterministicConsequences=" + proccessForNonDeterministicConsequences +
                ", proccessSuccessor="+proccessSuccessors+
                '}';
    }

    //initialize the knot with a type
    public Knot(Type t) {
        //this.Processed = false;
        this.root = t;
        this.successors =  new ArrayList<Successor>();
        this.droped=false;
        this.proccessForDeterministicConsequences=true;
        this.proccessForNonDeterministicConsequences=true;
        this.proccessSuccessors=true;
    }

    public void addSuccessor(ArrayList<String> roleNames, Type type){
        this.successors.add(new Successor(type,roleNames));
    }

    public boolean getDroped() {
        return droped;
    }

    public void setDroped(boolean drop) {
        this.droped = drop;
    }

    public boolean isProccessForNonDeterministicConsequences() {
        return proccessForNonDeterministicConsequences;
    }

    public void setProccessForNonDeterministicConsequences(boolean proccessForNonDeterministicConsequences) {
        this.proccessForNonDeterministicConsequences = proccessForNonDeterministicConsequences;
    }

    public boolean isProccessForDeterministicConsequences() {
        return proccessForDeterministicConsequences;
    }

    public void setProccessForDeterministicConsequences(boolean proccessForDeterministicConsequences) {
        this.proccessForDeterministicConsequences = proccessForDeterministicConsequences;
    }

    public boolean isProccessSuccessors() {
        return proccessSuccessors;
    }

    public void setProccessSuccessors(boolean proccessSuccessors) {
        this.proccessSuccessors = proccessSuccessors;
    }
}
