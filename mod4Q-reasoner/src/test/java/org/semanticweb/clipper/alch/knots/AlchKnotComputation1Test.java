package org.semanticweb.clipper.alch.knots;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom;
import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom1;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by bato on 7/12/2016.
 */
public class AlchKnotComputation1Test {
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLIndividual a;
    private OWLIndividual b;
    private OWLIndividual c;
    private OWLIndividual a1;
    private OWLIndividual a2;
    private OWLIndividual a3;
    private OWLClass A;
    private OWLClass B;
    private OWLClass C;
    private OWLClass D;

    private OWLClass A1;
    private OWLClass B1;
    private OWLClass A2;
    private OWLClass B2;
    private OWLClass C2;
    private OWLClass A3;
    private OWLClass B3;
    private OWLClass C3;
    private OWLClass A4;
    private OWLClass B4;
    private OWLClass C4;
    private OWLClass C1;
    private OWLClass D1;

    private OWLObjectProperty r;
    private OWLObjectProperty r1;
    private OWLObjectProperty r2;



    Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
    OWLOntology ontology;
    @Before
    public void SetUp() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();

        a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));

        A = factory.getOWLClass(IRI.create("http://www.example.org/#A"));
        A1 = factory.getOWLClass(IRI.create("http://www.example.org/#A1"));

        B = factory.getOWLClass(IRI.create("http://www.example.org/#B"));
        B1 = factory.getOWLClass(IRI.create("http://www.example.org/#B1"));

        C = factory.getOWLClass(IRI.create("http://www.example.org/#C"));
        C1 = factory.getOWLClass(IRI.create("http://www.example.org/#C1"));

        D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));
        D1 = factory.getOWLClass(IRI.create("http://www.example.org/#D1"));


        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        r1 = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r1"));
        r2 = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r2"));



        //Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();
        //declarations
        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(A1));

        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(B1));

        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(C1));

        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(D1));


        axioms.add(factory.getOWLDeclarationAxiom(r));
        axioms.add(factory.getOWLDeclarationAxiom(r1));
        axioms.add(factory.getOWLDeclarationAxiom(r2));

        //assertion
        axioms.add(factory.getOWLClassAssertionAxiom(A, a));


        //ria
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r1, r));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r2, r));
        ontology = manager.createOntology(axioms);
    }


    @Test
    public void testComputeKnots() throws Exception {

        AlchKnotComputation knotComputationEngine=new AlchKnotComputation();


        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r1, C)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r2, D)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectAllValuesFrom(r, B)));
        axioms.add(factory.getOWLSubClassOfAxiom(B, factory.getOWLObjectUnionOf(C1, D1)));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(C, C1),A1));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(D,D1),B1));

        OWLOntology customOnto = manager.createOntology(axioms);

        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(customOnto);

        knotComputationEngine.verboseLevel=4;
        knotComputationEngine.initializeKnots(customOnto);

        System.out.println("Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom cl: knotComputationEngine.deterministicAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Non Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom cl: knotComputationEngine.nondeterminsticAxioms){
            System.out.println(cl.toString()+"\n");
        }

        System.out.println("\n" + "\n" + "Initial Set of Knots");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.computeKnots();

        System.out.println("\n"+"\n"+"Computed Knots");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                            +"\n"+k.root.getIniConcepts().toString()
                            +"\n"+k.root.getConcepts().toString()
                            +"\n"+k.successors.toString()
            );
        }
    }

    @Test
    public void testComputeKnots_perFile1() throws Exception {

        File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl");

        AlchKnotComputation1 knotComputationEngine = new AlchKnotComputation1();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);

        knotComputationEngine.verboseLevel=2;
        knotComputationEngine.initializeKnots(onto);
        //knotComputationEngine.computeKnots();

        onto =null;

        System.out.println(knotComputationEngine.knots.size());
    }

    @Test
    public void testComputeKnots_perFiles() throws Exception {
        String[] exceptionFiles={"00001.owl","00024.owl","00285.owl","00290.owl","00318.owl","00320.owl","00375.owl","00410.owl","00426.owl","00427","00477","00533.owl","00537"};
//good candidate 427
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        boolean skip=false;

        int intStartFrom=346;

        int cnt=0;
        for (File fileEntry : folder.listFiles()) {

            AlchKnotComputation1 knotEngine = new AlchKnotComputation1();

            cnt++;

            for(String str:exceptionFiles)
            {
                if(fileEntry.getName().toString().equals(str))
                    skip=true;
            }

            if(!skip && cnt>=intStartFrom) {
                System.out.println(fileEntry.getName());
                OWLOntology onto = manager.loadOntologyFromOntologyDocument(fileEntry);

                knotEngine.verboseLevel=1;
                knotEngine.initializeKnots(onto);
                //knotEngine.computeKnots();

                onto = null;

            }else skip=false;
        }
    }
}
