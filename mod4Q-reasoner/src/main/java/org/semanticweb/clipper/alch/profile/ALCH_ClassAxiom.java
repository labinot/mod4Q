package org.semanticweb.clipper.alch.profile;

import org.semanticweb.owlapi.model.*;

import java.util.ArrayList;

/**
 * Created by bato on 6/10/2016.
 */
/*this class can only be instantiated through it's constructor by passing as parameter
* an OWLSubClassOf axiom. And accepts it, only if it's in following normal forms
* and(Ai)->or(Bj), A->or(Bj), and(Ai)->B
* A->B
* A->exist(r.B)
* A->all(r.B)
* */
public class ALCH_ClassAxiom {

    /*left hand side of axiom, each member of an array represents an atomic concept
      connected by AND operator
     */
    private ArrayList<String> left = new ArrayList<String>();

    /*right hand side of axiom, each member of the array represents an atomic concept or bottom
      connected by OR operators (except for bottom which is not linked with OR operator)
     */
    private ArrayList<String> right = new ArrayList<String>();

    public ALCH_ClassAxiom(OWLSubClassOfAxiom ax) {
        OWLClassExpression superClass=ax.getSuperClass().getNNF();
        OWLClassExpression subClass=ax.getSubClass().getNNF();

        //parses only subclasses of normalized GCI ALCH axioms, otherwise issues a warning
        if(subClass.getClassExpressionType()== ClassExpressionType.OWL_CLASS)
            this.left.add(subClass.toString());
        else if(subClass.getClassExpressionType()== ClassExpressionType.OBJECT_INTERSECTION_OF){
            OWLObjectIntersectionOf and = (OWLObjectIntersectionOf)subClass;
            for(OWLClassExpression operand:and.getOperands()){
                if(operand.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                    this.left.add(operand.toString());
                }
                else{
                    System.err.println("Illegal conjunct for normalized ALCH axioms in subclass:"+ax.toString());
                }
            }
        }
        else {System.err.println("Illegal subclass for normalized axioms:"+ax.toString());
        }

        if(superClass.getClassExpressionType()== ClassExpressionType.OWL_CLASS) {
            this.right.add(superClass.toString());
        }
        else if(superClass.getClassExpressionType()== ClassExpressionType.OBJECT_SOME_VALUES_FROM){
            OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom)superClass;
            if(some.getFiller().getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                this.right.add("Some("+some.getProperty().toString() + "|" + some.getFiller().toString()+")");
            }else {
                System.err.println("Illegal Some_Of range for normalized ALCH axioms in superclass:"+ax.toString());
            }
        }
        else if(superClass.getClassExpressionType()== ClassExpressionType.OBJECT_ALL_VALUES_FROM){
            OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom)superClass;
            if(all.getFiller().getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                this.right.add("All("+all.getProperty().toString() + "|" + all.getFiller().toString()+")");
            }else {
                System.err.println("Illegal All_Of range for normalized ALCH axioms in superclass:"+ax.toString());
            }
        }
        else if(superClass.getClassExpressionType()== ClassExpressionType.OBJECT_UNION_OF){
            OWLObjectUnionOf or = (OWLObjectUnionOf)superClass;
            for(OWLClassExpression operand:or.getOperands()){
                if(operand.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                    this.right.add(operand.toString());
                }
                else{
                    System.err.println("Illegal disjunct for normalized ALCH axioms in superclass:"+ax.toString());
                }
            }
        }
        else {
            System.err.println("Illegall superclass for normalized axioms:"+ax.toString());
        }
    }

    public ArrayList<String> getLeft() {
        return this.left;
    }

    public ArrayList<String> getRight() {
        return this.right;
    }

    @Override
    public String toString() {
        String s="";
        if(left.size()>0) {
            s = left.get(0);
            for (int i = 1; i < left.size(); i++) {
                s = s + " and " + left.get(i);
            }
        }

        if(right.size()>0) {
            s = s + " :- " + right.get(0);
            for (int i = 1; i < right.size(); i++) {
                s = s + " or " + right.get(i);
            }
        }
        return s;
    }
}
