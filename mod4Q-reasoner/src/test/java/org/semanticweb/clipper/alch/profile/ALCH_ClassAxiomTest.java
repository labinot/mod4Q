package org.semanticweb.clipper.alch.profile;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import static org.junit.Assert.*;

/**
 * Created by bato on 6/11/2016.
 */
public class ALCH_ClassAxiomTest {

        private OWLOntologyManager manager;
        private OWLDataFactory factory;
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

    ALCH_ClassAxiom axiom;

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
        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        s = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#s"));
        r_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#r_d"));
        s_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#s_d"));
    }
    //A->B
    @Test
    public void test1() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(A, B));
        System.out.println(alchAx.toString());
    }

    //and(A,C)->B
    @Test
    public void test2() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(A,C), B));
        System.out.println(alchAx.toString());
    }

    //and(A,A1)->and(B,C)
    @Test
    public void test3() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(A,A1),factory.getOWLObjectIntersectionOf(B,C)));
        System.out.println(alchAx.toString());
    }

    //or(A,A1)->or(B,C)-should return illegal
    @Test
    public void test4() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(A, A1),factory.getOWLObjectUnionOf(B, C)));
        System.out.println(alchAx.toString());
    }

    //some(R.A)->B -should return illegal
    @Test
    public void test5() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(r, A1),factory.getOWLObjectUnionOf(B,C)));
        System.out.println(alchAx.toString());
    }

    //and(B,C)->some(R.A) -should return legal
    @Test
    public void test6() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(B, C),factory.getOWLObjectSomeValuesFrom(r, A1)));
        System.out.println(alchAx.toString());
    }

    //and(B',C)->some(R.A) -should return illegal
    @Test
    public void test7() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(factory.getOWLObjectComplementOf(B), C), factory.getOWLObjectSomeValuesFrom(r, A1)));
        System.out.println(alchAx.toString());
    }

    //and(and(B,B1),C)->some(R.A) -should return illegal
    @Test
    public void test8() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(factory.getOWLObjectIntersectionOf(B, B1), C), factory.getOWLObjectSomeValuesFrom(r, A1)));
        System.out.println(alchAx.toString());
    }

/*------------------*/
//all(R.A)->B -should return illegal
@Test
public void test9() throws Exception {
    ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(r, A1),factory.getOWLObjectUnionOf(B, C)));
    System.out.println(alchAx.toString());
}

    //and(B,C)->all(R.A) -should return legal
    @Test
    public void test10() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(B, C),factory.getOWLObjectAllValuesFrom(r, A1)));
        System.out.println(alchAx.toString());
    }

    //and(B',C)->all(R.A) -should return illegal
    @Test
    public void test11() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(factory.getOWLObjectComplementOf(B), C), factory.getOWLObjectAllValuesFrom(r, A1)));
        System.out.println(alchAx.toString());
    }

    //C->or(all(R.A),B) -should return illegal disjunct on the right
    @Test
    public void test12() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(C,
                factory.getOWLObjectUnionOf(factory.getOWLObjectSomeValuesFrom(r, A), B)));
        System.out.println(alchAx.toString());
    }

    //bottom->and(A',B)-should return illegal on the right
    @Test
    public void test13() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLNothing(),
                factory.getOWLObjectIntersectionOf(factory.getOWLObjectSomeValuesFrom(r, A), B)));
        System.out.println(alchAx.toString());
    }

    //top->bottom-should return illegal on the right
    @Test
    public void test14() throws Exception {
        ALCH_ClassAxiom alchAx = new ALCH_ClassAxiom(factory.getOWLSubClassOfAxiom(factory.getOWLThing(),factory.getOWLNothing()));
        System.out.println(alchAx.toString());
    }


}


