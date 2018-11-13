package org.semanticweb.clipper.alch.HermitExamples;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyParser;
import org.junit.Before;
import org.junit.Test;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class DLQueryEngineTest {




    @Test
    public void testGetInstances() throws Exception {

        File fileOntology= new File("/home/bato/data/iswc2017/benchmarks/instances/test/wien_100.owl");

        ALCH_Normalizer2 normalizer=new ALCH_Normalizer2();

        ShortFormProvider sfp = new SimpleShortFormProvider();

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager
                .loadOntologyFromOntologyDocument(fileOntology);

        ontology=normalizer.normalize(ontology);
        // We need a reasoner to do our query answering

        // These two lines are the only relevant difference between this code and the original example
        // This example uses HermiT: http://hermit-reasoner.com/
        Reasoner hermit = new Reasoner(ontology);
        long t1 = System.currentTimeMillis();

        if(hermit.isConsistent())
            System.out.println("Ontology is consistent");

        long t2 = System.currentTimeMillis();

        System.out.println("TIME: " + (t2 - t1));

        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
        DLQueryEngine hermiT = new DLQueryEngine(ontology,hermit,sfp);
        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(hermiT,sfp);


        OWLOntologyFormat format = manager.getOntologyFormat(ontology);
        System.out.println("    format: " + format);

        for (OWLClassExpression cls : ontology.getClassesInSignature()) {
            System.out
                    .println("Instances for Class " + sfp.getShortForm(cls.asOWLClass()).trim());


            dlQueryPrinter.outputInstances(sfp.getShortForm(cls.asOWLClass()).trim());
            System.out.println();
        }
    }

    @Test
    public void testWien250Rule1() throws Exception {

        File fileOntology= new File("/home/bato/data/iswc2017/benchmarks/instances/test/wien_250.owl");

        ALCH_Normalizer2 normalizer=new ALCH_Normalizer2();

        ShortFormProvider sfp = new SimpleShortFormProvider();

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLDataFactory factory = manager.getOWLDataFactory();

        OWLOntology ontology = manager
                .loadOntologyFromOntologyDocument(fileOntology);

        ontology=normalizer.normalize(ontology);

        OWLObjectProperty isLocatedAlong = factory.getOWLObjectProperty(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#isLocatedAlong"));
        OWLObjectProperty reachableAlongOneLine = factory.getOWLObjectProperty(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#reachableAlongOneLine"));
        OWLObjectProperty reachableStation = factory.getOWLObjectProperty(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#reachableStation"));
        OWLObjectProperty isLocatedNext= factory.getOWLObjectProperty(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#isLocatedNext"));


        OWLClass Accomodation  = factory.getOWLClass(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#Accomodation"));
        OWLClass Test = factory.getOWLClass(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#Test"));
        OWLClass Hotel = factory.getOWLClass(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#Hotel"));
        OWLClass RegionalRestaurant = factory.getOWLClass(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#RegionalRestaurant"));
        OWLClass DesirableHotel = factory.getOWLClass(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#DesirableHotel"));

        //just for test
        OWLClass reachableFrom= factory.getOWLClass(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/terms#reachableFrom"));

        OWLNamedIndividual iHaupbahnhof = factory.getOWLNamedIndividual(IRI.create("http://www.kr.tuwien.ac.at/myits/geoconcepts/terms#1010572279"));//Haupbahnhof
        SWRLIndividualArgument iHaupbahnhofArgument = factory.getSWRLIndividualArgument(iHaupbahnhof);

        SWRLVariable varX = factory.getSWRLVariable(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/variables#varX"));
        SWRLVariable varY = factory.getSWRLVariable(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/variables#varY"));
        SWRLVariable varZ = factory.getSWRLVariable(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/variables#varZ"));
        SWRLVariable varV = factory.getSWRLVariable(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/variables#varV"));
        SWRLVariable varW = factory.getSWRLVariable(IRI.create("http://www.kr.tuwien.ac.at/myits.geoconcepts/variables#varW"));

        //rule1
        Set<SWRLAtom> body1= new HashSet<>();
        Set<SWRLAtom> head1= new HashSet<>();

        //rule2
        Set<SWRLAtom> body2= new HashSet<>();
        Set<SWRLAtom> head2= new HashSet<>();

        //rule3
        Set<SWRLAtom> body3= new HashSet<>();
        Set<SWRLAtom> head3= new HashSet<>();

        //rule4
        Set<SWRLAtom> body4= new HashSet<>();
        Set<SWRLAtom> head4= new HashSet<>();


        //Rule1:    reachableAlongOneLine
        body1.add(factory.getSWRLObjectPropertyAtom(isLocatedAlong,varX,varZ));//is located along
        body1.add(factory.getSWRLObjectPropertyAtom(isLocatedAlong,varY,varZ));//is located along
        head1.add(factory.getSWRLObjectPropertyAtom(reachableAlongOneLine,varX,varY));

        SWRLRule r1 = factory.getSWRLRule(body1,head1);

        manager.addAxiom(ontology,r1);

        //Rule2:    reachable
        body2.add(factory.getSWRLObjectPropertyAtom(reachableAlongOneLine,iHaupbahnhofArgument,varZ));//is located along
        body2.add(factory.getSWRLObjectPropertyAtom(reachableAlongOneLine,varY,varZ));//is located along
        body2.add(factory.getSWRLObjectPropertyAtom(isLocatedAlong,iHaupbahnhofArgument,varW));//is located along
        body2.add(factory.getSWRLObjectPropertyAtom(isLocatedAlong,varZ,varV));//is located along
        head2.add(factory.getSWRLObjectPropertyAtom(reachableStation,iHaupbahnhofArgument,varZ));

        SWRLRule r2 = factory.getSWRLRule(body2,head2);

        manager.addAxiom(ontology,r2);

        //Rule:    desirableHotel
        body3.add(factory.getSWRLClassAtom(Hotel,varX));//is located along
        body3.add(factory.getSWRLObjectPropertyAtom(isLocatedNext,varX,varY));//is located along
        body3.add(factory.getSWRLObjectPropertyAtom(reachableStation,varY,varZ));//is located along
        body3.add(factory.getSWRLObjectPropertyAtom(isLocatedNext,varZ,varV));//is located along
        body3.add(factory.getSWRLClassAtom(RegionalRestaurant,varV));//is located along
        head3.add(factory.getSWRLClassAtom(DesirableHotel,varX));

        SWRLRule r3 = factory.getSWRLRule(body3,head3);

        manager.addAxiom(ontology,r3);

        //Rule4:    reachable
        //Rule1:    reachableAlongOneLine
        body4.add(factory.getSWRLObjectPropertyAtom(reachableAlongOneLine,varX,varY));//is located along
        head4.add(factory.getSWRLClassAtom(reachableFrom,varY));

        SWRLRule r4 = factory.getSWRLRule(body4,head4);

        manager.addAxiom(ontology,r4);

        manager.addAxiom(ontology,factory.getOWLSubClassOfAxiom(Hotel, Test));

        for(OWLClass cls:ontology.getClassesInSignature()){
            if(cls.asOWLClass()==Hotel.asOWLClass())
                System.out.println("Hotel Class found");

            if(cls.asOWLClass()==RegionalRestaurant.asOWLClass())
                System.out.println("Regional Restaurant Class found");

            if(cls.asOWLClass()==DesirableHotel.asOWLClass())
                System.out.println("DesirableHotel class found");

            if(cls.asOWLClass()==reachableFrom.asOWLClass())
                System.out.println("reachableFrom class found");
        }


        for(OWLObjectProperty property:ontology.getObjectPropertiesInSignature()){
            if(property.asOWLObjectProperty()==isLocatedAlong.asOWLObjectProperty())
                System.out.println("isLocatedAlong Property found");
            if(property.asOWLObjectProperty()==reachableAlongOneLine.asOWLObjectProperty())
                System.out.println("reachableAlongOneLine Property found");
            if(property.asOWLObjectProperty()==reachableStation.asOWLObjectProperty())
                System.out.println("reachableStation Property found");
            if(property.asOWLObjectProperty()==isLocatedNext.asOWLObjectProperty())
                System.out.println("isLocatedNext Property found");
        }

        for(OWLNamedIndividual ind:ontology.getIndividualsInSignature()){
            if(ind.asOWLNamedIndividual()==iHaupbahnhof.asOWLNamedIndividual())
                System.out.println("Hauptbahnhof Individual found");

        }


/*
%
%	reachableAlongOneLine(X,Y):-isLocatedAlong(X,Z),
%				                isLocatedAlong(Y,Z).

%	reachableStation(X,Z):-reachableAlongOneLine(X,Y),
%					                reachableAlongOneLine(Y,Z),
%					                isLocatedAlong(X,W),
%					                isLocatedAlong(Z,V),
%				    	            X='Hauptbahnhof'.

%	desirableHotel(X):-Hotel(X),
%	  		           isLocatedNext(X,Y),
%	  		           reachableStation(Y,Z),
%	  		           isLocatedNext(Z,V),
%	  		           RegionalRestaurant(V).
*/


        // We need a reasoner to do our query answering

        // These two lines are the only relevant difference between this code and the original example
        // This example uses HermiT: http://hermit-reasoner.com/
        Reasoner hermit = new Reasoner(ontology);
        long t1 = System.currentTimeMillis();

        if(hermit.isConsistent())
            System.out.println("Ontology is consistent");


        hermit.precomputeInferences();

        long t2 = System.currentTimeMillis();

        System.out.println("TIME: " + (t2 - t1));

        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
        DLQueryEngine hermiT = new DLQueryEngine(ontology,hermit,sfp);
        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(hermiT,sfp);

        OWLOntologyFormat format = manager.getOntologyFormat(ontology);
        System.out.println("    format: " + format);

        System.out.println("Instances for Class " + sfp.getShortForm(DesirableHotel.asOWLClass()).trim());
        dlQueryPrinter.outputInstances(sfp.getShortForm(DesirableHotel.asOWLClass()).trim());
        System.out.println();

        System.out.println("Instances for Class " + sfp.getShortForm(reachableFrom.asOWLClass()).trim());
        dlQueryPrinter.outputInstances(sfp.getShortForm(reachableFrom.asOWLClass()).trim());
        System.out.println();

        System.out.println("Instances for Class " + sfp.getShortForm(Hotel.asOWLClass()).trim());
        dlQueryPrinter.outputInstances(sfp.getShortForm(Hotel.asOWLClass()).trim());
        System.out.println();

        System.out.println("Instances for Class " + sfp.getShortForm(Test.asOWLClass()).trim());
        dlQueryPrinter.outputInstances(sfp.getShortForm(Test.asOWLClass()).trim());
        System.out.println();

        System.out.println("Instances for Class " + sfp.getShortForm(Accomodation.asOWLClass()).trim());
        dlQueryPrinter.outputInstances(sfp.getShortForm(Accomodation.asOWLClass()).trim());
        System.out.println();

        System.out.println("Instances for Propery" + sfp.getShortForm(reachableAlongOneLine.asOWLObjectProperty()).trim());
        dlQueryPrinter.outputObjectPropertyInstances(reachableAlongOneLine);
        System.out.println();
    }


    @Test
    public void testSWRLRules() throws OWLOntologyCreationException {

        //Set Up OWLAPI classes, individuals, properties and assertions
        OWLOntologyManager manager;
        OWLDataFactory factory;

        OWLIndividual iA;
        OWLIndividual iB;
        OWLIndividual iC;
        OWLIndividual iD;

        OWLClass A;
        OWLClass B;
        OWLClass C;
        OWLClass R;
        OWLClass S;

        OWLObjectProperty r;

        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();

        iA = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#iA"));
        iB = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#iB"));
        iC = factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#iC"));

        A = factory.getOWLClass(IRI.create("http://www.example.org/#A"));
        B = factory.getOWLClass(IRI.create("http://www.example.org/#B"));
        C = factory.getOWLClass(IRI.create("http://www.example.org/#C"));
        R = factory.getOWLClass(IRI.create("http://www.example.org/#R"));
        S = factory.getOWLClass(IRI.create("http://www.example.org/#S"));

        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));


        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        OWLOntology ontology;

        //declarations
        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(R));
        axioms.add(factory.getOWLDeclarationAxiom(S));

        //assertion
        axioms.add(factory.getOWLClassAssertionAxiom(A, iA));
        axioms.add(factory.getOWLClassAssertionAxiom(B, iB));
        axioms.add(factory.getOWLClassAssertionAxiom(C, iC));
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(r, iA, iB));
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(r, iB, iC));

        //ria
        ontology = manager.createOntology(axioms);


/*
%
%	reachableAlongOneLine(X,Y):-isLocatedAlong(X,Z),
%				                isLocatedAlong(Y,Z).

%	reachableStation(X,Z):-reachableAlongOneLine(X,Y),
%					                reachableAlongOneLine(Y,Z),
%					                isLocatedAlong(X,W),
%					                isLocatedAlong(Z,V),
%				    	            X='Hauptbahnhof'.

%	desirableHotel(X):-Hotel(X),
%	  		           isLocatedNext(X,Y),
%	  		           reachableStation(Y,Z),
%	  		           isLocatedNext(Z,V),
%	  		           RegionalRestaurant(V).
*/

        Set<SWRLAtom> body = new HashSet<>();
        Set<SWRLAtom> head = new HashSet<>();

        SWRLVariable varX = factory.getSWRLVariable(IRI.create("http://www.example.org/#varX"));
        SWRLVariable varY = factory.getSWRLVariable(IRI.create("http://www.example.org/#varY"));
        SWRLVariable varZ = factory.getSWRLVariable(IRI.create("http://www.example.org/#varZ"));

        SWRLIndividualArgument iArgument = factory.getSWRLIndividualArgument(iA);

        body.add(factory.getSWRLObjectPropertyAtom(r, varX, varY));//is located along
        //body.add(factory.getSWRLObjectPropertyAtom(r, varY, varZ));//is located along

        head.add(factory.getSWRLClassAtom(R, varY));

        SWRLRule r1 = factory.getSWRLRule(body, head);

        manager.addAxiom(ontology, r1);


        // We need a reasoner to do our query answering

        // These two lines are the only relevant difference between this code and the original example
        // This example uses HermiT: http://hermit-reasoner.com/
        Reasoner hermit = new Reasoner(ontology);
        long t1 = System.currentTimeMillis();

        if (hermit.isConsistent())
            System.out.println("Ontology is consistent");

        long t2 = System.currentTimeMillis();

        System.out.println("TIME: " + (t2 - t1));


        // Create the DLQueryPrinter helper class. This will manage the
        // parsing of input and printing of results
        ShortFormProvider sfp = new SimpleShortFormProvider();
        DLQueryEngine hermiT = new DLQueryEngine(ontology,hermit,sfp);
        DLQueryPrinter dlQueryPrinter = new DLQueryPrinter(hermiT,sfp);


        OWLOntologyFormat format = manager.getOntologyFormat(ontology);
        System.out.println("    format: " + format);

            //print instances for R
            System.out.println("Instances for Class " + sfp.getShortForm(R.asOWLClass()).trim());

            dlQueryPrinter.outputInstances(sfp.getShortForm(R.asOWLClass()).trim());
            System.out.println();


    }
}
