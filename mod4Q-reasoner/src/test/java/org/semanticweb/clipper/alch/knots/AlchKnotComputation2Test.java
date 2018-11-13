package org.semanticweb.clipper.alch.knots;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom;
import org.semanticweb.clipper.alch.profile.ALCH_ClassAxiom1;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer1;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;


public class AlchKnotComputation2Test {

    private OWLOntologyManager manager;
    private OWLDataFactory factory;

    @Before
    public void SetUp() throws OWLOntologyCreationException {
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
    }

    @Test
    public void testComputeKnots_perFile1() throws Exception {

        File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl");

        AlchKnotComputation2 knotComputationEngine = new AlchKnotComputation2();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);

        knotComputationEngine.verboseLevel=4;
        knotComputationEngine.initializeKnots(onto);
        knotComputationEngine.computeKnots();

        onto =null;

        System.out.println(knotComputationEngine.knots.size());
    }

    @Test
    public void testComputeKnots_perFiles() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        boolean skip=true;

        String strName="00796.owl";

        for (File fileEntry : folder.listFiles()) {

            String filename=fileEntry.getName().toString();

            OWLOntology onto=null;
            OWLOntology normalizedOnto=null;

            if(filename.equals(strName))
                skip=false;

            if(!skip) {
                AlchKnotComputation2 knotEngine = new AlchKnotComputation2();

                System.out.print(fileEntry.getName());
                onto = manager.loadOntologyFromOntologyDocument(fileEntry);

                knotEngine.verboseLevel=0;
                knotEngine.initializeKnots(onto);
                knotEngine.computeKnots();

                onto = null;
            }
        }
    }

    @Test
    public void testGetConceptDeterministicHierarchy() throws Exception {

        File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl");

        AlchKnotComputation2 knotComputationEngine = new AlchKnotComputation2();
        ALCH_Normalizer1 normalizer = new ALCH_Normalizer1();

        OWLOntology onto =manager.loadOntologyFromOntologyDocument(file);
        OWLOntology normalizedOnto = normalizer.normalize(onto);

        System.out.println(onto.getClassesInSignature(true).size()+" classes in the input ontology");
        System.out.println(normalizedOnto.getClassesInSignature(true).size()+" classes in the normalized ontology");

        System.out.println("Deterministic hierarchy of concepts");
        System.out.println("===================================");
        System.out.println("Concept | Deterministic Closure of Concept");

        knotComputationEngine.verboseLevel=0;
        knotComputationEngine.initializeKnots(onto);

        HashMap<String,ArrayList<String>> deteministicHierarchy =knotComputationEngine.getConceptDeterministicHierarchy(normalizedOnto);

        for(Map.Entry<String,ArrayList<String>> entry:deteministicHierarchy.entrySet()) {
            String printout=getSimplifiedConcept(entry.getKey())+" *=* ";

            for(String consequent:entry.getValue()){
                if(consequent.indexOf("Some")==0||consequent.indexOf("All")==0) {
                    printout=printout+consequent.substring(0,consequent.indexOf("(")+1);
                    printout=printout+getSimplifiedConcept(consequent.substring(consequent.indexOf("("), consequent.indexOf("|")))+".";
                    printout=printout+getSimplifiedConcept(consequent.substring(consequent.indexOf("|"),consequent.length()-1))+"), ";
                }
                else{
                    printout=printout+getSimplifiedConcept(consequent)+ ", ";
                }
            }

            System.out.println(printout);
        }

        System.out.println("Non-Deterministic hierarchy of concepts");
        System.out.println("===================================");
        System.out.println("Concept | Non-Deterministic Closure of Concept");


        HashMap<String,ArrayList<String>> NONdeteministicHierarchy =knotComputationEngine.getNonDeterministicHierarchyOfConcepts(normalizedOnto);

        for(Map.Entry<String,ArrayList<String>> entry:NONdeteministicHierarchy.entrySet()) {
            if(entry.getValue().size()>0) {
                String printout = getSimplifiedConcept(entry.getKey()) + " *=* ";


                for (String consequent : entry.getValue()) {
                    if (consequent.indexOf("Some") == 0 || consequent.indexOf("All") == 0) {
                        printout = printout + consequent.substring(0, consequent.indexOf("(") + 1);
                        printout = printout + getSimplifiedConcept(consequent.substring(consequent.indexOf("("), consequent.indexOf("|"))) + ".";
                        printout = printout + getSimplifiedConcept(consequent.substring(consequent.indexOf("|"), consequent.length() - 1)) + "), ";
                    } else {
                        printout = printout + getSimplifiedConcept(consequent) + ", ";
                    }
                }
                System.out.println(printout);
            }
        }

        onto=null;
        normalizedOnto=null;
    }

    @Test
    public void testComputeKnots01() throws Exception {

        AlchKnotComputation2 knotComputationEngine=new AlchKnotComputation2();

        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLIndividual a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));

        OWLClass A=factory.getOWLClass(IRI.create("www.ex.com/#A"));
        OWLClass A1=factory.getOWLClass(IRI.create("www.ex.com/#A1"));
        OWLClass A2=factory.getOWLClass(IRI.create("www.ex.com/#A2"));

        OWLClass B=factory.getOWLClass(IRI.create("www.ex.com/#B"));
        OWLClass B1=factory.getOWLClass(IRI.create("www.ex.com/#B1"));
        OWLClass B2=factory.getOWLClass(IRI.create("www.ex.com/#B2"));

        OWLClass C=factory.getOWLClass(IRI.create("www.ex.com/#C"));
        OWLClass C1=factory.getOWLClass(IRI.create("www.ex.com/#C1"));
        OWLClass C2=factory.getOWLClass(IRI.create("www.ex.com/#C2"));

        OWLClass D=factory.getOWLClass(IRI.create("www.ex.com/#D"));
        OWLClass D1=factory.getOWLClass(IRI.create("www.ex.com/#D1"));
        OWLClass D2=factory.getOWLClass(IRI.create("www.ex.com/#D2"));

        OWLClass E=factory.getOWLClass(IRI.create("www.ex.com/#E"));
        OWLClass E1=factory.getOWLClass(IRI.create("www.ex.com/#E1"));
        OWLClass E2=factory.getOWLClass(IRI.create("www.ex.com/#E2"));

        OWLObjectProperty r= factory.getOWLObjectProperty(IRI.create("www.ex.com/#r"));
        OWLObjectProperty r1=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r1"));
        OWLObjectProperty r2=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r2"));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r1,r));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r2,r));

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        axioms.add(factory.getOWLSubClassOfAxiom(A, B));
        axioms.add(factory.getOWLSubClassOfAxiom(A, C));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectUnionOf(A1, A2)));

        axioms.add(factory.getOWLSubClassOfAxiom(A1, factory.getOWLObjectSomeValuesFrom(r,D)));
        axioms.add(factory.getOWLSubClassOfAxiom(A2, factory.getOWLObjectAllValuesFrom(r,D)));

        OWLOntology customOnto = manager.createOntology(axioms);

        knotComputationEngine.verboseLevel=4;
        knotComputationEngine.initializeKnots(customOnto);


        System.out.println("Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.deterministicAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Non Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.nondeterminsticAxioms){
            System.out.println(cl.toString()+"\n");
        }

        System.out.println("\n" + "\n" + "Initial Set of Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()
                    +". DetProcess:"+k.isProccessForDeterministicConsequences()
                    +". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()
                    +". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.computeKnots();

        System.out.println("\n"+"\n"+"Computed Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()
                            +". DetProcess:"+k.isProccessForDeterministicConsequences()
                            +". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()
                            +". SuccessorProcess:"+k.isProccessSuccessors()
                            +"\n"+k.root.getIniConcepts().toString()
                            +"\n"+k.root.getConcepts().toString()
                            +"\n"+k.successors.toString()
            );
        }
    }

    @Test
    public void testComputeKnots02() throws Exception {

        AlchKnotComputation2 knotComputationEngine=new AlchKnotComputation2();

        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLIndividual a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));

        OWLClass A=factory.getOWLClass(IRI.create("www.ex.com/#A"));
        OWLClass A1=factory.getOWLClass(IRI.create("www.ex.com/#A1"));
        OWLClass A2=factory.getOWLClass(IRI.create("www.ex.com/#A2"));

        OWLClass B=factory.getOWLClass(IRI.create("www.ex.com/#B"));
        OWLClass B1=factory.getOWLClass(IRI.create("www.ex.com/#B1"));
        OWLClass B2=factory.getOWLClass(IRI.create("www.ex.com/#B2"));

        OWLClass C=factory.getOWLClass(IRI.create("www.ex.com/#C"));
        OWLClass C1=factory.getOWLClass(IRI.create("www.ex.com/#C1"));
        OWLClass C2=factory.getOWLClass(IRI.create("www.ex.com/#C2"));

        OWLClass D=factory.getOWLClass(IRI.create("www.ex.com/#D"));
        OWLClass D1=factory.getOWLClass(IRI.create("www.ex.com/#D1"));
        OWLClass D2=factory.getOWLClass(IRI.create("www.ex.com/#D2"));

        OWLClass E=factory.getOWLClass(IRI.create("www.ex.com/#E"));
        OWLClass E1=factory.getOWLClass(IRI.create("www.ex.com/#E1"));
        OWLClass E2=factory.getOWLClass(IRI.create("www.ex.com/#E2"));

        OWLObjectProperty r= factory.getOWLObjectProperty(IRI.create("www.ex.com/#r"));
        OWLObjectProperty r1=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r1"));
        OWLObjectProperty r2=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r2"));

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        axioms.add(factory.getOWLSubClassOfAxiom(A, B));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectUnionOf(A1, A2)));

        axioms.add(factory.getOWLSubClassOfAxiom(A1, factory.getOWLObjectSomeValuesFrom(r, D)));
        axioms.add(factory.getOWLSubClassOfAxiom(A1, factory.getOWLObjectAllValuesFrom(r1, D1)));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(r, D),C));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectAllValuesFrom(r1, D1),E));

        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r, r1));

        OWLOntology customOnto = manager.createOntology(axioms);

        knotComputationEngine.verboseLevel=4;
        knotComputationEngine.initializeKnots(customOnto);


        System.out.println("Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.deterministicAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Non Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.nondeterminsticAxioms){
            System.out.println(cl.toString()+"\n");
        }

        System.out.println("\n" + "\n" + "Initial Set of Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.computeKnots();

        System.out.println("\n"+"\n"+"Computed Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println("Droped:"+k.getDroped()+". DetProcess:"+k.isProccessForDeterministicConsequences()+". NonDetProcess:"+k.isProccessForNonDeterministicConsequences()+". SuccessorProcess:"+k.isProccessSuccessors()
                            +"\n"+k.root.getIniConcepts().toString()
                            +"\n"+k.root.getConcepts().toString()
                            +"\n"+k.successors.toString()
            );
        }
    }

    @Test
    public void testComputeKnots03() throws Exception {

        AlchKnotComputation2 knotComputationEngine=new AlchKnotComputation2();

        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLIndividual a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));

        OWLClass A=factory.getOWLClass(IRI.create("www.ex.com/#A"));
        OWLClass A1=factory.getOWLClass(IRI.create("www.ex.com/#A1"));
        OWLClass A2=factory.getOWLClass(IRI.create("www.ex.com/#A2"));

        OWLClass B=factory.getOWLClass(IRI.create("www.ex.com/#B"));
        OWLClass B1=factory.getOWLClass(IRI.create("www.ex.com/#B1"));
        OWLClass B2=factory.getOWLClass(IRI.create("www.ex.com/#B2"));

        OWLClass C=factory.getOWLClass(IRI.create("www.ex.com/#C"));
        OWLClass C1=factory.getOWLClass(IRI.create("www.ex.com/#C1"));
        OWLClass C2=factory.getOWLClass(IRI.create("www.ex.com/#C2"));

        OWLClass D=factory.getOWLClass(IRI.create("www.ex.com/#D"));
        OWLClass D1=factory.getOWLClass(IRI.create("www.ex.com/#D1"));
        OWLClass D2=factory.getOWLClass(IRI.create("www.ex.com/#D2"));

        OWLClass E=factory.getOWLClass(IRI.create("www.ex.com/#E"));
        OWLClass E1=factory.getOWLClass(IRI.create("www.ex.com/#E1"));
        OWLClass E2=factory.getOWLClass(IRI.create("www.ex.com/#E2"));

        OWLObjectProperty r= factory.getOWLObjectProperty(IRI.create("www.ex.com/#r"));
        OWLObjectProperty r1=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r1"));
        OWLObjectProperty r2=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r2"));

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectUnionOf(A1, A2)));
        axioms.add(factory.getOWLSubClassOfAxiom(A1,B));
        axioms.add(factory.getOWLSubClassOfAxiom(B,C));
        axioms.add(factory.getOWLSubClassOfAxiom(B, factory.getOWLObjectUnionOf(B1, B2)));

        axioms.add(factory.getOWLSubClassOfAxiom(C, factory.getOWLObjectSomeValuesFrom(r, B)));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(r1, B), factory.getOWLObjectAllValuesFrom(r1, A)));

        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r1,r));

        OWLOntology customOnto = manager.createOntology(axioms);

        knotComputationEngine.verboseLevel=4;
        knotComputationEngine.initializeKnots(customOnto);


        System.out.println("Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.deterministicAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Non Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.nondeterminsticAxioms){
            System.out.println(cl.toString()+"\n");
        }

        System.out.println("\n" + "\n" + "Initial Set of Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println(k.hashCode()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.computeKnots();

        System.out.println("\n"+"\n"+"Computed Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println(k.hashCode()+
                                "\n"+k.root.getIniConcepts().toString()
                                +"\n"+k.root.getConcepts().toString()
                                +"\n"+k.successors.toString()
            );
        }
    }

    @Test
    public void testComputeKnots04() throws Exception {

        AlchKnotComputation2 knotComputationEngine=new AlchKnotComputation2();

        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLIndividual a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));

        OWLClass A=factory.getOWLClass(IRI.create("www.ex.com/#A"));
        OWLClass A1=factory.getOWLClass(IRI.create("www.ex.com/#A1"));
        OWLClass A2=factory.getOWLClass(IRI.create("www.ex.com/#A2"));

        OWLClass B=factory.getOWLClass(IRI.create("www.ex.com/#B"));
        OWLClass B1=factory.getOWLClass(IRI.create("www.ex.com/#B1"));
        OWLClass B2=factory.getOWLClass(IRI.create("www.ex.com/#B2"));

        OWLClass C=factory.getOWLClass(IRI.create("www.ex.com/#C"));
        OWLClass C1=factory.getOWLClass(IRI.create("www.ex.com/#C1"));
        OWLClass C2=factory.getOWLClass(IRI.create("www.ex.com/#C2"));

        OWLClass D=factory.getOWLClass(IRI.create("www.ex.com/#D"));
        OWLClass D1=factory.getOWLClass(IRI.create("www.ex.com/#D1"));
        OWLClass D2=factory.getOWLClass(IRI.create("www.ex.com/#D2"));

        OWLClass E=factory.getOWLClass(IRI.create("www.ex.com/#E"));
        OWLClass E1=factory.getOWLClass(IRI.create("www.ex.com/#E1"));
        OWLClass E2=factory.getOWLClass(IRI.create("www.ex.com/#E2"));

        OWLObjectProperty r= factory.getOWLObjectProperty(IRI.create("www.ex.com/#r"));
        OWLObjectProperty r1=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r1"));
        OWLObjectProperty r2=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r2"));

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLThing(), factory.getOWLObjectUnionOf(B, C)));

        OWLOntology customOnto = manager.createOntology(axioms);

        knotComputationEngine.verboseLevel=4;
        knotComputationEngine.initializeKnots(customOnto);


        System.out.println("Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.deterministicAxioms){
            System.out.println(cl.toString());
        }

        System.out.println("Non Deterministic Axioms");
        System.out.println("=====================");
        for(ALCH_ClassAxiom1 cl: knotComputationEngine.nondeterminsticAxioms){
            System.out.println(cl.toString()+"\n");
        }

        System.out.println("\n" + "\n" + "Initial Set of Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println(k.hashCode()
                    +"\n"+k.root.getIniConcepts().toString()
                    +"\n"+k.root.getConcepts().toString()+"\n");
        }

        knotComputationEngine.computeKnots();

        System.out.println("\n"+"\n"+"Computed Knots");
        for(Knot1 k:knotComputationEngine.knots){
            System.out.println(k.hashCode()+
                            "\n"+k.root.getIniConcepts().toString()
                            +"\n"+k.root.getConcepts().toString()
                            +"\n"+k.successors.toString()
            );
        }
    }

    @Test
    public void testGetAboxRoleGuessesExponent() throws Exception{
        AlchKnotComputation2 knotComputationEngine=new AlchKnotComputation2();

        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLIndividual a = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));
        OWLIndividual b = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#b"));
        OWLIndividual c = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#c"));
        OWLIndividual d = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#d"));

        OWLClass A=factory.getOWLClass(IRI.create("www.ex.com/#A"));
        OWLClass A1=factory.getOWLClass(IRI.create("www.ex.com/#A1"));
        OWLClass A2=factory.getOWLClass(IRI.create("www.ex.com/#A2"));

        OWLClass B=factory.getOWLClass(IRI.create("www.ex.com/#B"));
        OWLClass C=factory.getOWLClass(IRI.create("www.ex.com/#C"));
        OWLClass D=factory.getOWLClass(IRI.create("www.ex.com/#D"));
        OWLClass E=factory.getOWLClass(IRI.create("www.ex.com/#E"));

        OWLObjectProperty r= factory.getOWLObjectProperty(IRI.create("www.ex.com/#r"));
        OWLObjectProperty r1=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r1"));
        OWLObjectProperty r2=factory.getOWLObjectProperty(IRI.create("www.ex.com/#r2"));

        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r1,r));
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r2, r));

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(r,a,b));
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(r1, a, c));
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(r, a, d));
        axioms.add(factory.getOWLSubClassOfAxiom(A, B));
        axioms.add(factory.getOWLSubClassOfAxiom(A, C));
        axioms.add(factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectUnionOf(A1, A2)));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectSomeValuesFrom(r2, D), A1));
//        axioms.add(factory.getOWLSubClassOfAxiom(A2, factory.getOWLObjectAllValuesFrom(r,D)));

        OWLOntology customOnto = manager.createOntology(axioms);

        knotComputationEngine.initializeKnots(customOnto);

        for(OWLAxiom ax: customOnto.getABoxAxioms(true)) {
            if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
                OWLObjectPropertyAssertionAxiom aggg =(OWLObjectPropertyAssertionAxiom)ax;
                String strProperty=aggg.getProperty().toString();
                int cnt=0;
                for (ALCH_ClassAxiom1 gci_axiom : knotComputationEngine.deterministicAxioms) {
                    if(gci_axiom.getLeft().get(0).contains("Some(") && strProperty.equals(gci_axiom.getLeft().get(0).substring(5,gci_axiom.getLeft().get(0).indexOf("|"))))
                    {
                        cnt++;
                    }
                }
                if(cnt==1){
                    System.out.println(strProperty+"="+cnt);
                }
            }
        }

    }
//Procedure to check for the guesses one needs to make, in order to compute the initial types in the abox
    //these guesses concern the esitentials on the left and the role assertions, so for each
    //role assertion we need to check how many times (n) that role occurs in the LHS of axioms with qualified
    //existentials on the left and as such we then get 2^n combinations of types for the type that occurs in the
    //domain of the role (the combinations of concepts that could be backpropagated by the axioms mentioned).
    @Test
    public void testGetAboxRoleGuessesExponentPerFile() throws Exception{
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00119.owl");

        AlchKnotComputation2 knotComputationEngine = new AlchKnotComputation2();

        OWLOntology onto = manager.loadOntologyFromOntologyDocument(folder);

        knotComputationEngine.initializeKnots(onto);

        int max=0;
        String maxProperty="";
        for (OWLAxiom ax : onto.getABoxAxioms(true)) {
            if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
                OWLObjectPropertyAssertionAxiom aggg = (OWLObjectPropertyAssertionAxiom) ax;
                String strProperty = aggg.getProperty().toString();
                int cnt = 0;
                for (ALCH_ClassAxiom1 gci_axiom : knotComputationEngine.deterministicAxioms) {
                    if (gci_axiom.getLeft().get(0).contains("Some(")
                            && strProperty.equals(gci_axiom.getLeft().get(0).substring(5, gci_axiom.getLeft().get(0).indexOf("|")))
                            &&!gci_axiom.getLeft().get(0).contains("owl:Thing")) {
                        cnt++;
                    }
                }
                if (cnt>max) {
                    max=cnt;
                    maxProperty=strProperty;
                }
            }
        }
        onto = null;

        System.out.println(folder.getName() + "=" + maxProperty + "=" + max);
    }


    @Test
    public void testGetAboxRoleGuessesExponentForRepository() throws Exception{
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        boolean skip=true;

        String strName="00792.owl";

        for (File fileEntry : folder.listFiles()) {

            String filename=fileEntry.getName().toString();

            OWLOntology onto=null;
            OWLOntology normalizedOnto=null;

            if(filename.equals(strName))
                skip=false;

            if(!skip) {


                AlchKnotComputation2 knotComputationEngine = new AlchKnotComputation2();

                onto = manager.loadOntologyFromOntologyDocument(fileEntry);

                knotComputationEngine.initializeKnots(onto);

                int max=0;
                String maxProperty="";
                for (OWLAxiom ax : onto.getABoxAxioms(true)) {
                    if (ax.getAxiomType() == AxiomType.OBJECT_PROPERTY_ASSERTION) {
                        OWLObjectPropertyAssertionAxiom aggg = (OWLObjectPropertyAssertionAxiom) ax;
                        String strProperty = aggg.getProperty().toString();
                        int cnt = 0;
                        for (ALCH_ClassAxiom1 gci_axiom : knotComputationEngine.deterministicAxioms) {
                            if (gci_axiom.getLeft().get(0).contains("Some(")
                                    && strProperty.equals(gci_axiom.getLeft().get(0).substring(5, gci_axiom.getLeft().get(0).indexOf("|")))
                                    &&!gci_axiom.getLeft().get(0).contains("owl:Thing")) {
                                cnt++;
                            }
                        }
                        if (cnt>max) {
                            max=cnt;
                            maxProperty=strProperty;
                        }
                    }
                }
                onto = null;

                System.out.println(fileEntry.getName() + "=" + maxProperty + "=" + max);
            }
        }
    }


    public String getSimplifiedConcept(String str){
        if(str.contains("#")&&!str.equals("#")){
            return str.substring(str.indexOf("#")+1,str.length()-1);
        }
        else if(str.equals("#"))
        {
            return "#";
        }
        else{
            return str;
        }

    }




}