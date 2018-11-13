package org.semanticweb.clipper.alch.knots;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer;
import org.semanticweb.clipper.alch.profile.ALCH_RoleAxiom;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by bato on 7/10/2016.
 */
public class AlchKnotComputationTest {

    AlchKnotComputation knotComputationEngine = new AlchKnotComputation();

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
    //private OWLDataProperty r_d;
    //private OWLObjectProperty s;
    //private OWLDataProperty s_d;

    private OWLObjectProperty r1;
    private OWLObjectProperty r2;
    //private OWLObjectProperty q;
    //private OWLObjectProperty t;
    //private OWLObjectProperty u;
    //private OWLObjectProperty v;


    Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
    OWLOntology ontology;

    @Before
    public void setUp() throws OWLOntologyCreationException {

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();

        a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));
/*        b = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#b"));
        c = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#c"));
        a1 = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a1"));
        a2 = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a2"));
        a3 = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a3"));*/
        A = factory.getOWLClass(IRI.create("http://www.example.org/#A"));
        A1 = factory.getOWLClass(IRI.create("http://www.example.org/#A1"));
/*      A2 = factory.getOWLClass(IRI.create("http://www.example.org/#A2"));
        A3 = factory.getOWLClass(IRI.create("http://www.example.org/#A3"));
        A4 = factory.getOWLClass(IRI.create("http://www.example.org/#A4"));*/
        B = factory.getOWLClass(IRI.create("http://www.example.org/#B"));
        B1 = factory.getOWLClass(IRI.create("http://www.example.org/#B1"));
/*        B2 = factory.getOWLClass(IRI.create("http://www.example.org/#B2"));
        B3 = factory.getOWLClass(IRI.create("http://www.example.org/#B3"));
        B4 = factory.getOWLClass(IRI.create("http://www.example.org/#B4"));*/
        C = factory.getOWLClass(IRI.create("http://www.example.org/#C"));
        C1 = factory.getOWLClass(IRI.create("http://www.example.org/#C1"));

        D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));
        D1 = factory.getOWLClass(IRI.create("http://www.example.org/#D1"));

/*        C2 = factory.getOWLClass(IRI.create("http://www.example.org/#C2"));
        C3 = factory.getOWLClass(IRI.create("http://www.example.org/#C3"));
        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        s = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#s"));*/
        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        r1 = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r1"));
        r2 = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r2"));

/*        r_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#r_d"));
        s_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#s_d"));

        o = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#o"));
        p = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#p"));
        q = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#q"));
        t = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#t"));
        u = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#u"));
        v = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#v"));*/

        //Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();
        //declarations
        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(A1));
        //axioms.add(factory.getOWLDeclarationAxiom(A2));
        //axioms.add(factory.getOWLDeclarationAxiom(A3));
        //axioms.add(factory.getOWLDeclarationAxiom(A4));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(B1));

        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(C1));

        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(D1));


        axioms.add(factory.getOWLDeclarationAxiom(r));
        axioms.add(factory.getOWLDeclarationAxiom(r1));
        axioms.add(factory.getOWLDeclarationAxiom(r2));
        //axioms.add(factory.getOWLDeclarationAxiom(r_d));
        //axioms.add(factory.getOWLDeclarationAxiom(s_d));
        //assertion
        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        //axioms.add(factory.getOWLClassAssertionAxiom(B, a));
        //axioms.add(factory.getOWLClassAssertionAxiom(B, b));

        //axioms.add(factory.getOWLClassAssertionAxiom(A4, a1));
        //axioms.add(factory.getOWLClassAssertionAxiom(A3, a1));
        //axioms.add(factory.getOWLClassAssertionAxiom(A1, a1));
        //axioms.add(factory.getOWLClassAssertionAxiom(A2, a1));
        //axioms.add(factory.getOWLClassAssertionAxiom(A1, a2));
        //axioms.add(factory.getOWLClassAssertionAxiom(A2, a2));
        //axioms.add(factory.getOWLClassAssertionAxiom(A4, a2));
        //axioms.add(factory.getOWLClassAssertionAxiom(A3, a2));

        //tbox
        //axioms.add(factory.getOWLSubClassOfAxiom(A,B));
        //axioms.add(factory.getOWLSubClassOfAxiom(B,C));




        //ria
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r1, r));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r2, r));
        ontology = manager.createOntology(axioms);
    }

    @Test
    public void testComputeKnots() throws Exception {

    }

    /*test the overloaded version with ontology*/
    @Test
    public void testComputeKnots1() throws Exception {
    }

    @Test
    public void testDetConsequncesOfRoot() throws Exception {

    }

    @Test
    public void testNondetConsequencesOfRoot() throws Exception {

    }

    @Test
    public void testIntroduceKnotSuccessors() throws Exception {

    }

    @Test
    public void testKnotInTheSet() throws Exception {

    }
/*
    @Test
    public void testGetRoleHierarchy() throws Exception {
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(o, p));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(p, q));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(u, v));

        OWLOntology customOnto = manager.createOntology(axioms);


        Set<ALCH_RoleAxiom> riaAxioms = knotComputationEngine.getRiaAxioms(customOnto);
        ArrayList<ALCH_RoleAxiom> arrRiaAxioms = new ArrayList<ALCH_RoleAxiom>(riaAxioms);
        HashMap<String,ArrayList<String>> roleHierarchy = knotComputationEngine.getRoleHierarchy(arrRiaAxioms,customOnto);



        System.out.println("Role | Consequences");
        System.out.println("===================");
        for(Map.Entry<String, ArrayList<String>> entry : roleHierarchy.entrySet()){
            System.out.println(entry.getKey().toString()+" | "+entry.getValue().toString());
        }
    }

    @Test
    public void testGetGciAxioms() throws Exception {
        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(ontology);

        for(ALCH_ClassAxiom a: gciAxioms){
            System.out.println(a.toString());
        }
    }

    @Test
    public void testGetGciAxioms1() throws Exception {
        File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl");

        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);
        OWLOntology normalizedOnto=normalizer.normalize(onto);

        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(normalizedOnto);

        for(ALCH_ClassAxiom a: gciAxioms){
            System.out.println(a.toString());
        }
    }


    @Test
    public void testGetRiaAxioms() throws Exception {
        Set<ALCH_RoleAxiom> riaAxioms = knotComputationEngine.getRiaAxioms(ontology);
        for(ALCH_RoleAxiom a: riaAxioms){
            System.out.println(a.toString());
        }
    }

    @Test
    public void testGetRiaAxioms1() throws Exception {
        File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl");

        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);
        OWLOntology normalizedOnto=normalizer.normalize(onto);

        Set<ALCH_RoleAxiom> riaAxioms = knotComputationEngine.getRiaAxioms(normalizedOnto);
        for(ALCH_RoleAxiom a: riaAxioms){
            System.out.println(a.toString());
        }
    }


    @Test
    public void testGetInitializationTypes() throws Exception {
        Set<ArrayList<String>> types = knotComputationEngine.getInitializationTypes(ontology);
        for(ArrayList<String> a: types){
            System.out.println(a.toString());
        }
    }

    /*type extraction from file*/
    @Test
    public void testGetInitializationTypes1() throws Exception {
        File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl");

        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);
        OWLOntology normalizedOnto=normalizer.normalize(onto);

        Set<ArrayList<String>> types = knotComputationEngine.getInitializationTypes(normalizedOnto);

        onto =null;
        normalizedOnto=null;

        for(ArrayList<String> a: types){
            System.out.println(a.toString());
        }
    }

    @Test
    public void testCategorizeAxioms() throws Exception {

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectComplementOf(A), C2));
        axioms.add(factory.getOWLSubClassOfAxiom(A,factory.getOWLObjectSomeValuesFrom(r,B)));
        axioms.add(factory.getOWLSubClassOfAxiom(A,factory.getOWLObjectAllValuesFrom(r, B)));
        //axioms.add(factory.getOWLSubObjectPropertyOfAxiom(p, q));
        //axioms.add(factory.getOWLSubObjectPropertyOfAxiom(u, v));

        OWLOntology customOnto = manager.createOntology(axioms);

        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(customOnto);

        knotComputationEngine.initializeKnots(customOnto);

        System.out.println("Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom cl: knotComputationEngine.deterministicAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Non Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom cl: knotComputationEngine.nondeterminsticAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Existential Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom cl: knotComputationEngine.existentialAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Universal Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom cl: knotComputationEngine.universalAxioms){
            System.out.println(cl.toString());
        }



    }
/*
    @Test
    public void testdetConsequncesOfRoot() throws Exception {
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r, B)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectAllValuesFrom(s, C)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectUnionOf(A1, A2)));
        axioms.add(factory.getOWLSubClassOfAxiom(A1, A3));

        OWLOntology customOnto = manager.createOntology(axioms);

        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(customOnto);

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

/*        System.out.println("\n"+"\n"+"Initial Set of Knots");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }*/

 //       knotComputationEngine.detConsequncesOfRoot();

/*        System.out.println("\n"+"\n"+"Knots with closed roots");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }*/

//        knotComputationEngine.nondetConsequencesOfRoot();

/*        System.out.println("\n"+"\n"+"Knots with nondet consequences");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }*/

  //      knotComputationEngine.detConsequncesOfRoot();

//        System.out.println("\n"+"\n"+"Knots after deterministicaly completing the nondeterministic consequnces");
/*        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.introduceKnotSuccessors();

        System.out.println("\n"+"\n"+"Knots after introducing successors");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()
                    +"\n"+k.successors.toString()+"\n");
        }

    }*/
/*
    @Test
    public void testIntrocudeChildrenAndNonDetConsequncesOfChildren() throws Exception {
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r, B)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectAllValuesFrom(s, C)));
        axioms.add(factory.getOWLSubClassOfAxiom(B, factory.getOWLObjectUnionOf(A1, A2)));

        OWLOntology customOnto = manager.createOntology(axioms);

        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(customOnto);

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

        knotComputationEngine.detConsequncesOfRoot();

        System.out.println("\n"+"\n"+"Knots with closed roots");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.nondetConsequencesOfRoot();

        System.out.println("\n"+"\n"+"Knots with nondet consequences");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.introduceKnotSuccessors();

        System.out.println("\n" + "\n" + "Knots after introducing successors");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()
                    +"\n"+k.successors.toString()+"\n");
        }

        knotComputationEngine.detConsequncesOfRoot();

        System.out.println("\n"+"\n"+"Knots after runing det consequnces right after adding children");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.nondetConsequencesOfRoot();

        System.out.println("\n" + "\n" + "Knots after runing non-det consequnces right after adding children");
        for(Knot k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

    }*/

    @Test
    public void testComputeKnots001() throws Exception {
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r1, C)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r2, D)));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectAllValuesFrom(r, B)));
        axioms.add(factory.getOWLSubClassOfAxiom(B, factory.getOWLObjectUnionOf(C1, D1)));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(C, C1),A1));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(D,D1),B1));

        OWLOntology customOnto = manager.createOntology(axioms);

        Set<ALCH_ClassAxiom> gciAxioms = knotComputationEngine.getGciAxioms(customOnto);

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

        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);
        OWLOntology normalizedOnto=normalizer.normalize(onto);

        knotComputationEngine.verboseLevel=3;
        knotComputationEngine.initializeKnots(onto);
        knotComputationEngine.computeKnots();

        onto =null;
        normalizedOnto=null;

        System.out.println(knotComputationEngine.knots.size());
    }

    @Test
    public void testComputeKnots_perFiles() throws Exception {
        String[] exceptionFiles={"00001.owl","00024.owl"};

        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        boolean skip=false;

        int intStartFrom=83;

        int cnt=0;
        for (File fileEntry : folder.listFiles()) {

            AlchKnotComputation knotEngine = new AlchKnotComputation();

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
                knotEngine.computeKnots();

                onto = null;

            }else skip=false;
        }
    }
}