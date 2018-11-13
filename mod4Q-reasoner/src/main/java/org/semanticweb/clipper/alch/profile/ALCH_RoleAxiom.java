package org.semanticweb.clipper.alch.profile;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;

/**
 * Created by bato on 6/10/2016.
 */
public class ALCH_RoleAxiom {

    /*left hand side of axiom, contains the name of one role*/
    String left;

    /*right hand side of axiom, contains the name of one role*/
    String right;

    //public ALCH_RoleAxiom(String left, String right){
    //    this.left = left;
    //    this.right = right;
    //}

    public ALCH_RoleAxiom(OWLSubObjectPropertyOfAxiom ax) {
        OWLObjectPropertyExpression superProperty=ax.getSuperProperty();
        OWLObjectPropertyExpression subProperty=ax.getSubProperty();

        //TODO: check if property is simple, if yes parse otherwise issue a warning
        this.left=subProperty.toString();
        this.right=superProperty.toString();
    }


    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public String toString() {
        return left+" :- "+right;
    }
}
