package org.semanticweb.clipper.alch.knots;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bato on 6/8/2016.
 */
public class Knot1 {
    private boolean droped;
    private boolean proccessForDeterministicConsequences;
    private boolean proccessForNonDeterministicConsequences;
    private boolean proccessSuccessors;
    public Type1 root; //uses the class Type
    public ArrayList<Successor1> successors;

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
    public Knot1(Type1 t) {
        //this.Processed = false;
        this.root = t;
        this.successors =  new ArrayList<Successor1>();
        this.droped=false;
        this.proccessForDeterministicConsequences=true;
        this.proccessForNonDeterministicConsequences=true;
        this.proccessSuccessors=true;
    }

    public void addSuccessor(ArrayList<String> roleNames, Type1 type){
        this.successors.add(new Successor1(type,roleNames));
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
