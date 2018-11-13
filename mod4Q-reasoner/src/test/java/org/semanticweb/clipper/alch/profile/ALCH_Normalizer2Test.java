package org.semanticweb.clipper.alch.profile;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by bato on 9/21/2016.
 */
public class ALCH_Normalizer2Test {

    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLIndividual a;
    private OWLIndividual b;
    private OWLIndividual c;
    private OWLClass A;
    private OWLClass B;
    private OWLClass C;
    private OWLObjectProperty r;
    private OWLDataProperty r_d;
    private OWLObjectProperty s;
    private OWLDataProperty s_d;
    private OWLClass A1;
    private OWLClass B1;
    private OWLClass C1;
    private OWLClass A2;
    private OWLClass B2;
    private OWLClass C2;
    private OWLClass A3;
    private OWLClass B3;
    private OWLClass C3;
    private OWLClass A4;
    private OWLClass B4;
    private OWLClass C4;
    @Before
    public void setUp() throws Exception {
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        A = factory.getOWLClass(IRI.create("http://www.example.org/#A"));
        A1 = factory.getOWLClass(IRI.create("http://www.example.org/#A1"));
        A2 = factory.getOWLClass(IRI.create("http://www.example.org/#A2"));
        A3 = factory.getOWLClass(IRI.create("http://www.example.org/#A3"));
        A4 = factory.getOWLClass(IRI.create("http://www.example.org/#A4"));
        B = factory.getOWLClass(IRI.create("http://www.example.org/#B"));
        B1 = factory.getOWLClass(IRI.create("http://www.example.org/#B1"));
        B2 = factory.getOWLClass(IRI.create("http://www.example.org/#B2"));
        B3 = factory.getOWLClass(IRI.create("http://www.example.org/#B3"));
        B4 = factory.getOWLClass(IRI.create("http://www.example.org/#B4"));
        C = factory.getOWLClass(IRI.create("http://www.example.org/#C"));
        C1 = factory.getOWLClass(IRI.create("http://www.example.org/#C1"));
        C2 = factory.getOWLClass(IRI.create("http://www.example.org/#C2"));
        C3 = factory.getOWLClass(IRI.create("http://www.example.org/#C3"));
        a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));
        b = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#b"));
        c = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#c"));
        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        s = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#s"));
        r_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#r_d"));
        s_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#s_d"));

    }

    @Test
    public void testNormalize1() throws Exception {
        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> intersection = new HashSet<OWLClassExpression>();

        intersection.add(factory.getOWLObjectComplementOf(A));
        intersection.add(factory.getOWLObjectAllValuesFrom(r, B));
        intersection.add(C);

        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectIntersectionOf(intersection), A4));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    @Test
    public void testNormalize2() throws Exception {
        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> intersection = new HashSet<OWLClassExpression>();

        intersection.add(factory.getOWLObjectComplementOf(A));
        intersection.add(factory.getOWLObjectAllValuesFrom(r, B));

        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectIntersectionOf(intersection), A4));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    @Test
    public void testNormalize3() throws Exception {
        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> intersection = new HashSet<OWLClassExpression>();

        intersection.add(A);
        intersection.add(factory.getOWLObjectSomeValuesFrom(r, B));

        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectIntersectionOf(intersection), A4));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    @Test
    public void testNormalize4() throws Exception {
        ALCH_Normalizer1 normalizer = new ALCH_Normalizer1();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> intersection = new HashSet<OWLClassExpression>();
        Set<OWLClassExpression> intersection2 = new HashSet<OWLClassExpression>();

        intersection2.add(B2);
        intersection2.add(factory.getOWLObjectSomeValuesFrom(s,B3));

        intersection.add(B1);
        intersection.add(factory.getOWLObjectSomeValuesFrom(r,factory.getOWLObjectIntersectionOf(intersection2)));

        axioms.add(factory.getOWLEquivalentClassesAxiom(
                A, factory.getOWLObjectIntersectionOf(intersection)));

        OWLOntology ontology = manager.createOntology(axioms);
        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }



    //Extract the stats regarding the structure of TBox Axioms after normalizing the onotologies in the repository with normalizer1
    @Test
    public void testExtractStatsFromFilesInRepository() throws OWLOntologyCreationException {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        boolean skip = true;

        String strName = "00700.owl";

        System.out.println("Filename,Top->or(...),All.Top ->A,All.A->B,Exist.Top->A,Exist.A->B,and(...)->or(...),and(...)->A,and(...)->Bottom,A->or(...),A->B,A->Exist.B,A->All.B,Other");
        for (File fileEntry : folder.listFiles()) {

            String filename = fileEntry.getName().toString();

            OWLOntology onto = null;
            OWLOntology normalizedOnto = null;

            if (filename.equals(strName))
                skip = false;

            if (!skip) {
                ALCH_Normalizer2 newNormalizer = new ALCH_Normalizer2();

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

                manager = null;
                manager = OWLManager.createOWLOntologyManager();

            }
        }
    }
}