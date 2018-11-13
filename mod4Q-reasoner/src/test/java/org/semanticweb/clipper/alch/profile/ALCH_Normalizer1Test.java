package org.semanticweb.clipper.alch.profile;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;

/**
 * Created by bato on 7/17/2016.
 */
public class ALCH_Normalizer1Test {
    OWLOntologyManager manager;

    @Before
    public void SetUp() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
    }
    //Extract the stats regarding the structure of TBox Axioms after normalizing the onotologies in the repository with normalizer1
    @Test
    public void testExtractStatsFromFilesInRepository() throws OWLOntologyCreationException {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        boolean skip=true;

        String strName="00001.owl";

        System.out.println("Filename,Top->or(...),All.Top ->A,All.A->B,Exist.Top->A,Exist.A->B,and(...)->or(...),and(...)->A,and(...)->Bottom,A->or(...),A->B,A->Exist.B,A->All.B,Other");
        for (File fileEntry : folder.listFiles()) {

            String filename=fileEntry.getName().toString();

            OWLOntology onto=null;
            OWLOntology normalizedOnto=null;

            if(filename.equals(strName))
                skip=false;

            if(!skip) {
                ALCH_Normalizer1 newNormalizer = new ALCH_Normalizer1();

                onto = manager.loadOntologyFromOntologyDocument(fileEntry);

                normalizedOnto = newNormalizer.normalize(onto);

                int cntTopOnTheLeftWithDisjunction = 0;
                int cntForAllOnTheLeftWithTop = 0;
                int cntForAllOnTheLeft = 0;
                int cntExistOnTheLeftWithTop = 0;
                int cntExistOnTheLeft = 0;
                int cntNF1 = 0;
                int cntConjunctsOnTheLeft = 0;
                int cntConjunctsOnTheLeftBottomOnTheRight = 0;
                int cntDisjunctionOnTheRight = 0;
                int cntSimple = 0;
                int cntExistOnTheRight = 0;
                int cntForAllOnTheRight = 0;
                int cntOther = 0;

                for (OWLAxiom ax : normalizedOnto.getTBoxAxioms(false)) {
                    if (ax.getAxiomType() == AxiomType.SUBCLASS_OF) {
                        OWLSubClassOfAxiom Axiom = (OWLSubClassOfAxiom) ax;
                        OWLClassExpression subclass = Axiom.getSubClass();
                        OWLClassExpression superclass = Axiom.getSuperClass();

                        if (subclass.isOWLThing() && superclass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                            cntTopOnTheLeftWithDisjunction++;
                        } else if (subclass.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                            cntForAllOnTheLeft++;
                            OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) subclass;
                            if (all.getFiller().isOWLThing()) {
                                cntForAllOnTheLeftWithTop++;
                            }
                        } else if (subclass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
                            cntExistOnTheLeft++;
                            OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subclass;
                            if (some.getFiller().isOWLThing()) {
                                cntExistOnTheLeftWithTop++;
                            }
                        } else if (subclass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF &&
                                superclass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                            cntNF1++;
                        } else if (subclass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF &&
                                superclass.isOWLNothing()) {
                            cntConjunctsOnTheLeftBottomOnTheRight++;
                        } else if (subclass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
                            cntConjunctsOnTheLeft++;
                        } else if (superclass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                            cntDisjunctionOnTheRight++;
                        } else if (subclass.getClassExpressionType() == ClassExpressionType.OWL_CLASS &&
                                superclass.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                            cntSimple++;
                        } else if (superclass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
                            cntExistOnTheRight++;
                        } else if (superclass.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                            cntForAllOnTheRight++;
                        } else {
                            cntOther++;
                            System.out.println(ax.toString());
                        }
                    }
                }
                System.out.println(
                        filename + ", " + cntTopOnTheLeftWithDisjunction +
                                "," + cntForAllOnTheLeftWithTop +
                                "," + cntForAllOnTheLeft +
                                "," + cntExistOnTheLeftWithTop +
                                "," + cntExistOnTheLeft +
                                "," + cntNF1 +
                                "," + cntConjunctsOnTheLeft +
                                "," + cntConjunctsOnTheLeftBottomOnTheRight +
                                "," + cntDisjunctionOnTheRight +
                                "," + cntSimple +
                                "," + cntExistOnTheRight +
                                "," + cntForAllOnTheRight +
                                "," + cntOther);

                manager=null;
                manager = OWLManager.createOWLOntologyManager();

            }
        }
    }
}