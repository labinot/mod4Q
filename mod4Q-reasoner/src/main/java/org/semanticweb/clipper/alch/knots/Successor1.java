package org.semanticweb.clipper.alch.knots;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by bato on 6/8/2016.
 */
public class Successor1 {

    Type1 typeSet;                                         //represents the set of OWL concepts
    ArrayList<String> roleSet = new ArrayList<String>();  //represents the role hierarchy via which the successor is conncted with the parent

    public Successor1(Type1 typeSet, ArrayList<String> roleSet) {
        this.typeSet = typeSet;
        this.roleSet = roleSet;
    }

    public Type1 getTypeSet() {
        return typeSet;
    }

    public void setTypeSet(Type1 typeSet) {
        this.typeSet = typeSet;
    }

    public ArrayList<String> getRoleSet() {
        return roleSet;
    }

    public void setRoleSet(ArrayList<String> roleSet) {
        this.roleSet = roleSet;
    }

    @Override
    public String toString() {
        return "Successor{" +
                "typeSet=" + typeSet.toString() +
                ", roleSet=" + roleSet.toString() +
                "}\n";
    }
}

