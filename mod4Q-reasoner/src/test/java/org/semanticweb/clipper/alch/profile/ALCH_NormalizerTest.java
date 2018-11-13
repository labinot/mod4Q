package org.semanticweb.clipper.alch.profile;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.clipper.hornshiq.ontology.ClipperAxiom;
import org.semanticweb.clipper.hornshiq.ontology.ClipperHornSHIQOntology;
import org.semanticweb.clipper.hornshiq.ontology.ClipperHornSHIQOntologyConverter;
import org.semanticweb.clipper.hornshiq.profile.HornALCHIQNormalizer;
import org.semanticweb.clipper.hornshiq.profile.HornALCHIQTransNormalizer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;


public class ALCH_NormalizerTest {

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
    public void setUp() {

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

    /*Disjoint(A,B) --passed
    --------------------------------
    and(A,B)->Nothing
     */
    @Test
    public void testNormalizeDisjointClassesAxioms1() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        axioms.add(factory.getOWLDisjointClassesAxiom(A, B));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*Disjoint(or(A,B),C) --passed
    --------------------------------
    A->Fresh
    B->Fresh
    and(Fresh,C)-> Bottom
     */
    @Test
    public void testNormalizeDisjointClassesAxioms2() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        axioms.add(factory.getOWLDisjointClassesAxiom(factory.getOWLObjectUnionOf(A, B), C));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

    }

    // failed
    @Test
    public void testNormalizeDisjointAxiomUnion() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        classexpressions.add(factory.getOWLObjectIntersectionOf(A1, A2));
        classexpressions.add(factory.getOWLObjectUnionOf(B1, B2));
        axioms.add(factory.getOWLDisjointUnionAxiom(C, classexpressions));                  //dy axioma

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        System.out.println("Normalized Onotlogy");

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*passed*/
    @Test
    public void testNormalizeClassAssertion() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));                              //add as is
        axioms.add(factory.getOWLClassAssertionAxiom(factory.getOWLObjectIntersectionOf(A, B), b));       //has to add in addition fressh -> A and B

        OWLOntology ontology = manager.createOntology(axioms);


        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        System.out.println("Normalized Onotlogy");

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

    }

    /*passed -1 case*/
    @Test
    public void testNormalizePropertyAxioms() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        /*the visit method that treat subclassof axioms does recognize DataSomeValuesFrom class expressions, in order for this method to work
        * a treatment for this category should be added*/
        axioms.add(factory.getOWLDataPropertyDomainAxiom(r_d, C));                          //return exists r_d top -> C
        /*OK*/
        axioms.add(factory.getOWLObjectPropertyDomainAxiom(s, B));                          //return exists s.top -> B
        axioms.add(factory.getOWLEquivalentObjectPropertiesAxiom(r, s));                    //should get r -> s and s->r
        axioms.add(factory.getOWLObjectPropertyRangeAxiom(r, A));                           // top -> forall r.A
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(s, a, b));                    //return as is
        axioms.add(factory.getOWLSubObjectPropertyOfAxiom(r, s));                           //return as is
        axioms.add(factory.getOWLDataPropertyAssertionAxiom(r_d, a, "labi"));

        OWLOntology ontology = manager.createOntology(axioms);

        System.out.println("Input Onotlogy");

        for (OWLAxiom ax : ontology.getLogicalAxioms()) {
            System.out.println(ax);
        }

        /*ALCH_Profile profile = new ALCH_Profile();
        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);
        assertTrue(report.isInProfile());*/

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        System.out.println("Normalized Onotlogy");

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

    }

    /*passed*/
    @Test
    public void testNormalizeEquivalentClassesAxioms() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        axioms.add(factory.getOWLEquivalentClassesAxiom(B, C));                            //has to add subclassOF(B,C) and subclassOF(C,B)

        OWLOntology ontology = manager.createOntology(axioms);

        System.out.println("Input Onotlogy");

        for (OWLAxiom ax : ontology.getLogicalAxioms()) {
            System.out.println(ax);
        }

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        System.out.println("Normalized Onotlogy");

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

    }

    /*passed - should add some more tesst cases to this for cardinality etc*/
    @Test
    public void testNormalizeDissallowedAxioms() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        Set<OWLClassExpression> classexpressions = new HashSet<OWLClassExpression>();

        axioms.add(factory.getOWLDeclarationAxiom(A));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(B));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(C));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s));                                      //return as is
        axioms.add(factory.getOWLDeclarationAxiom(r_d));                                    //return as is
        axioms.add(factory.getOWLDeclarationAxiom(s_d));                                    //return as is

        axioms.add(factory.getOWLNegativeObjectPropertyAssertionAxiom(r, a, b));            //return null
        axioms.add(factory.getOWLAsymmetricObjectPropertyAxiom(r));                         //return null
        axioms.add(factory.getOWLReflexiveObjectPropertyAxiom(r));                          //return null
        axioms.add(factory.getOWLNegativeDataPropertyAssertionAxiom(s_d, b, factory.getOWLLiteral("hehe")));     //null
        axioms.add(factory.getOWLDifferentIndividualsAxiom(a, b));                          //null
        axioms.add(factory.getOWLDisjointDataPropertiesAxiom(r_d, s_d));                    //null
        axioms.add(factory.getOWLDisjointObjectPropertiesAxiom(r, s));                      //null
        axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(s));                         //null

        axioms.add(factory.getOWLSymmetricObjectPropertyAxiom(r));                        //null

        OWLOntology ontology = manager.createOntology(axioms);

        System.out.println("Input Onotlogy");

        for (OWLAxiom ax : ontology.getAxioms()) {
            System.out.println(ax);
        }

        /*ALCH_Profile profile = new ALCH_Profile();
        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);
        assertTrue(report.isInProfile());*/

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(0, normalizedOnt.getLogicalAxiomCount());

        System.out.println("Normalized Onotlogy");

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }

    }

    // A -> B   passed
    @Test
    public void testNormalize001() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLSubClassOfAxiom(A, B));

        OWLOntology ontology = manager.createOntology(axioms);

        System.out.println("Input Onotlogy");

        for (OWLAxiom ax : ontology.getAxioms()) {
            System.out.println(ax);
        }

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        System.out.println("Normalized Onotlogy");

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    // A -> B -check again
    @Test
    public void testNormalize001A() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));

        axioms.add(factory.getOWLSubClassOfAxiom(A, B));



        OWLAnnotationSubject subject;
        IRI iri = IRI.create("http://example.org/ABC");
        OWLAnnotationProperty property = factory.getOWLAnnotationProperty(iri);

        OWLAnnotation owlAnnotation = factory.getOWLAnnotation(property, iri);
        axioms.add(factory.getOWLDeclarationAxiom(property));
        axioms.add(factory.getOWLAnnotationAssertionAxiom(iri, owlAnnotation));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }



        HornALCHIQTransNormalizer normalizer1 = new HornALCHIQTransNormalizer();
        OWLOntology normalizedOnt1 = normalizer1.normalize(normalizedOnt);

        HornALCHIQNormalizer normalizer2 = new HornALCHIQNormalizer();
        OWLOntology normalizedOnt3 = normalizer2.normalize(normalizedOnt1);

        ClipperHornSHIQOntologyConverter converter = new ClipperHornSHIQOntologyConverter();
        ClipperHornSHIQOntology onto_bs = converter.convert(normalizedOnt3);

        for (ClipperAxiom ax : onto_bs.getAllAxioms()) {
            System.out.println(ax);
        }


    }

    // and(A, B) -> C -passed
    @Test
    public void testNormalize002() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(A, B), C));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    // C -> and(A,B) -passed
    @Test
    public void testNormalize003() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(C, factory.getOWLObjectIntersectionOf(A, B)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    // or(A1, A2) -> and(B1, B2) -passed
    //consider avoiding the introduction of fresh classes in this case
    @Test
    public void testNormalize004() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));

        axioms.add(factory.getOWLDeclarationAxiom(B1));
        axioms.add(factory.getOWLDeclarationAxiom(B2));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(A1, A2),
                factory.getOWLObjectIntersectionOf(B1, B2)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(4, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // or(A1, some(r, A2)) -> and(B1, B2) -passed (consider removing an extra fresh class for the existential concept
    @Test
    public void testNormalize005() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));
        axioms.add(factory.getOWLDeclarationAxiom(B1));
        axioms.add(factory.getOWLDeclarationAxiom(B2));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        // A1 and r some A2 subclass B1 and B2
        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectUnionOf(A1, factory.getOWLObjectSomeValuesFrom(r, A2)),
                factory.getOWLObjectIntersectionOf(B1, B2)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(5, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // not B -> not A - passed
    @Test
    public void testNormalize006() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));

        // not B -> not A
        axioms.add(factory.getOWLSubClassOfAxiom( //
                factory.getOWLObjectComplementOf(B), //
                factory.getOWLObjectComplementOf(A)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

//        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        //assertEquals(1, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // not(and(A1, A2)) -> not(or(B1, B2)) - passed
    @Test
    public void testNormalize007() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));
        axioms.add(factory.getOWLDeclarationAxiom(B1));
        axioms.add(factory.getOWLDeclarationAxiom(B2));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        // not(and(A1, A2)) -> not(or(B1, B2))
        axioms.add(factory.getOWLSubClassOfAxiom( //
                factory.getOWLObjectComplementOf(//
                        factory.getOWLObjectIntersectionOf(A1, A2)), //
                factory.getOWLObjectComplementOf(//
                        factory.getOWLObjectUnionOf(B1, B2))));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

//        assertEquals(4, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // A -> not B - passed
    @Test
    public void testNormalize008() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));

        axioms.add(factory.getOWLSubClassOfAxiom( //
                A, //
                factory.getOWLObjectComplementOf(B)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

//        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

//        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // A -> or(not B, not C) - passed
    @Test
    public void testNormalize009() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(A,
                factory.getOWLObjectUnionOf(factory.getOWLObjectComplementOf(B), factory.getOWLObjectComplementOf(C))));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        assertTrue(report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        //assertEquals(4, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // C -> not (and (A, B)) - passed
    @Test
    public void testNormalize010() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(C,
                factory.getOWLObjectComplementOf(factory.getOWLObjectIntersectionOf(A, B))));

        OWLOntology ontology = manager.createOntology(axioms);

        //ALCH_Profile profile = new ALCH_Profile();
        //OWLProfileReport report = profile.checkOntology(ontology);
        //assertTrue(report.isInProfile());
        //System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

//        assertEquals(4, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // C = and(A, some(r, B)) - passed
    @Test
    public void testNormalize011() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLEquivalentClassesAxiom(C,
                factory.getOWLObjectIntersectionOf(A, factory.getOWLObjectSomeValuesFrom(r, B))));

        OWLOntology ontology = manager.createOntology(axioms);
        ALCH_Profile profile = new ALCH_Profile();
        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);
        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(4, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*  C -> and(not A, not B) -> passed
        ---------------------------------
        C and B -> bottom
        C and A -> bottom*/
    @Test
    public void testNormalize012() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(
                C,
                factory.getOWLObjectIntersectionOf(factory.getOWLObjectComplementOf(A),
                        factory.getOWLObjectComplementOf(B))));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);
        assertTrue(report.isInProfile());
        System.out.println(report);
        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*add another test here */
    @Test
    public void testNormalize013() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(
                C,
                factory.getOWLObjectIntersectionOf(factory.getOWLObjectComplementOf(A),
                        factory.getOWLObjectComplementOf(B))));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        assertTrue(report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(4, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*A-> All R.(Exists R.(MaxCardinality 1,s,A1)) --passed
    * ----------------------------------------------------
    * returned null with a warning :)
    * */
    @Test
    public void testNormalize014() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(r));
        axioms.add(factory.getOWLDeclarationAxiom(s));

        axioms.add(factory.getOWLSubClassOfAxiom(
                        A,
                        factory.getOWLObjectAllValuesFrom(r,
                                factory.getOWLObjectSomeValuesFrom(r, factory.getOWLObjectMaxCardinality(1, s, A1))))

        );

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);
        assertTrue(!report.isInProfile());
        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(0, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /* Passed, ABox axioms */
    @Test
    public void testNormalize015() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(r));
        axioms.add(factory.getOWLDeclarationAxiom(s));

        axioms.add(factory.getOWLClassAssertionAxiom(A, a));
        axioms.add(factory.getOWLObjectPropertyAssertionAxiom(r, a, b));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /* domain(r) = and(A1, A2)-passed
    --------------------------
        exists R.T -> A1
        exists R.T -> A2
    */
    @Test
    public void testNormalize016() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLObjectPropertyDomainAxiom(r, factory.getOWLObjectIntersectionOf(A1, A2)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        assertTrue(report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*  range(r) = and(A1, A2) - passed
    -----------------------------------
        Top -> forall R.Fresh
        Fresh -> A1
        Fresh -> A2
    */
    @Test
    public void testNormalize017() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLObjectPropertyRangeAxiom(r, factory.getOWLObjectIntersectionOf(A1, A2)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        assertTrue(report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(3, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*  range(r) = or(A1, A2) - passed
    -----------------------------------
        Top -> forall R.Fresh
        Fresh -> or(A1,A2)
    */
    @Test
    public void testNormalize018() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLObjectPropertyRangeAxiom(r, factory.getOWLObjectUnionOf(A1, A2)));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

         for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
             System.out.println(ax);
         }
    }

    /*or(A1,A2,or(A3,A4))-> and(B1, and(B2,B3)) - passed
    ----------------------------------
    12 combinations or Ai->Bj
    */
    @Test
    public void testNormalize019() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(A2));
        axioms.add(factory.getOWLDeclarationAxiom(A3));
        axioms.add(factory.getOWLDeclarationAxiom(B1));
        axioms.add(factory.getOWLDeclarationAxiom(B2));
        axioms.add(factory.getOWLDeclarationAxiom(B3));
        axioms.add(factory.getOWLDeclarationAxiom(B4));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectUnionOf(A1, factory.getOWLObjectUnionOf(A2, A3)),
                factory.getOWLObjectIntersectionOf(B1, B2, factory.getOWLObjectIntersectionOf(B3, B4))));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        // assertFalse(report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        // assertEquals(3, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // data property --> not treated by the normalizer, check the effect of ommiting them
    // domain(r) = A1
    @Test
    public void testNormalize020() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(r_d));

        axioms.add(factory.getOWLDataPropertyDomainAxiom(r_d, A1));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        assertTrue(report.isInProfile());

        System.out.println(report);

         OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //
        // assertEquals(3, normalizedOnt.getLogicalAxiomCount());
        //
        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    /* disjoint(A, B, C) -passed
    ----------------------------
    and(A,B) -> bottom
    and(A,C) -> bottom
    and(B,C) -> bottom
    */
    @Test
    public void testNormalize021() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLDisjointClassesAxiom(A, B, C));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        assertTrue(report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        // assertEquals(6, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /*  Func(r)  -passed
        ----------------
        return nothing
     */
    @Test
    public void testNormalize022() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLFunctionalObjectPropertyAxiom(r));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        //assertTrue(report.isInProfile());

        //System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(0, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    /* InvFunc(r) -passed
    ----------------
       return nothing
    */
    @Test
    public void testNormalize023() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLInverseFunctionalObjectPropertyAxiom(r));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);
        assertTrue(!report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
        assertEquals(0, normalizedOnt.getLogicalAxiomCount());
    }

    /* Symmetric(r) -passed
    -----------------------
       return nothing
    */
    @Test
    public void testNormalize024() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLSymmetricObjectPropertyAxiom(r));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);
        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
        assertEquals(2, normalizedOnt.getLogicalAxiomCount());
    }

    @Test
    public void testNormalize025() throws OWLOntologyCreationException {
        OWLClass wine = factory.getOWLClass(IRI.create("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine"));
        OWLObjectProperty madeFromGrape = factory.getOWLObjectProperty(IRI
                .create("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#Wine"));

        OWLClass nom25 = factory
                .getOWLClass(IRI.create("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#__nom25"));

        OWLClass petiteSyrah = factory.getOWLClass(IRI
                .create("http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine#PetiteSyrah"));
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
        axioms.add(factory.getOWLDeclarationAxiom(wine));
        axioms.add(factory.getOWLDeclarationAxiom(madeFromGrape));
        axioms.add(factory.getOWLDeclarationAxiom(nom25));
        axioms.add(factory.getOWLDeclarationAxiom(petiteSyrah));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(wine,
                factory.getOWLObjectSomeValuesFrom(madeFromGrape, nom25),
                factory.getOWLObjectMaxCardinality(1, madeFromGrape), factory.getOWLThing()), petiteSyrah));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

         assertTrue(!report.isInProfile());

        System.out.println(report);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

    }

    /*
    Exist R.(and(A,Exist(s,B),Exist(s,A1))-> C -passed
    --------------------------------------------------
    Exist R.fresh1->C
    and(A,fresh2,fresh3)->fresh1
    Exist(s,B)->fresh2
    Exist(s,A1)->fresh3
     */

    @Test
    public void testNormalize026() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        axioms.add(factory.getOWLDeclarationAxiom(r));
        axioms.add(factory.getOWLDeclarationAxiom(s));
        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(A1));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));

        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectSomeValuesFrom(
                        r,
                        factory.getOWLObjectIntersectionOf(A, factory.getOWLObjectSomeValuesFrom(s, B),
                                factory.getOWLObjectSomeValuesFrom(s, A1))), C));

        ;

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);
        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
        // assertEquals(2, normalizedOnt.getLogicalAxiomCount());
    }


    @Test
    public void testNormalizeT() throws OWLOntologyCreationException, OWLOntologyStorageException {
        File file = new File("TestData/t.owl");
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        System.out.println(ontology);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);

        assertTrue(report.isInProfile());

        System.out.println(report);
        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

        man.saveOntology(normalizedOnt, IRI.create(new File("TestData/t.owl")));
    }

    @Test
    public void testNormalizeLUBM1() throws OWLOntologyCreationException, OWLOntologyStorageException {
        File file = new File("TestData/horn-univ-bench.owl");
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        System.out.println(ontology);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);

        assertTrue(report.isInProfile());

        System.out.println(report);
        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }

        man.saveOntology(normalizedOnt, IRI.create(new File("TestData/nf-horn-univ-bench.owl")));
    }

    // original
    @Test
    public void testNormalizeLUBM2() throws OWLOntologyCreationException, OWLOntologyStorageException {
        File file = new File("TestData/univ-bench.owl");
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        System.out.println(ontology);

        //OWLProfile profile = new ALCH_Profile();
        OWLProfile profile = new OWL2RLProfile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);
//
//		assertTrue(report.isInProfile());
//
//		System.out.println(report);
//		ALCH_Normalizer normalizer = new ALCH_Normalizer();
//
//		OWLOntology normalizedOnt = normalizer.normalize(ontology);
//
//		for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
//			System.out.println(ax);
//		}
    }

    // original
    @Test
    public void testNormalizeWine() throws OWLOntologyCreationException, OWLOntologyStorageException {
        File file = new File("test-suite/ontology-wine/terminology.owl");

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        System.out.println(ontology);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);

        assertFalse(report.isInProfile());

        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // original
    @Test
    public void testSteel() throws OWLOntologyCreationException, OWLOntologyStorageException {
        File file = new File("test-suite/ontology-steel/steel.owl");

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        System.out.println(ontology);

        OWLProfile profile = new ALCH_Profile();
//		OWLProfile profile = new OWL2RLProfile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);

        //assertFalse(report.isInProfile());

//		ALCH_Normalizer normalizer = new ALCH_Normalizer();
//
//		OWLOntology normalizedOnt = normalizer.normalize(ontology);
//
//		for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
//			System.out.println(ax);
//		}
    }

    @Test
    public void testEqv() throws OWLOntologyCreationException, OWLOntologyStorageException {
        File file = new File("TestData/testNorm.owl");

        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

        System.out.println(ontology);

        OWLProfile profile = new ALCH_Profile();
//		OWLProfile profile = new OWL2RLProfile();

        OWLProfileReport report = profile.checkOntology(ontology);
        System.out.println(report);

        //assertFalse(report.isInProfile());

        ALCH_Normalizer normalizer = new ALCH_Normalizer();

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getLogicalAxioms()) {
            System.out.println(ax);
        }
    }

    // and(A, B) -> and(C,D)-passed
    @Test
    public void testNormalizeL01() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(D));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(A, B), factory.getOWLObjectIntersectionOf(C, D)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    // exist(R, B) -> D -passed
    @Test
    public void testNormalizePhaze2ExistentialQunatifiesOnTheLeft() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));

        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectSomeValuesFrom(r, B),D));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLProfileReport report = profile.checkOntology(ontology);

        System.out.println(report);

        assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        assertEquals(3, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    // exist(R, B) -> D -passed
    @Test
    public void testNormalizePhaze2UniversalQunatifiesOnTheLeft() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));

        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(r));

        axioms.add(factory.getOWLSubClassOfAxiom(
                factory.getOWLObjectAllValuesFrom(r, B), D));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        //OWLProfileReport report = profile.checkOntology(ontology);

        //System.out.println(report);

        //assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(3, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }


    // disjointUnion(A,B) -passed
    //A and B -> bottom
    //C->or(A,B)
    @Test
    public void testNormalize1DisjointClasses() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D;
        D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));

        axioms.add(factory.getOWLDeclarationAxiom(A));
        axioms.add(factory.getOWLDeclarationAxiom(B));
        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(D));


        Set<OWLClassExpression> ex = new HashSet<OWLClassExpression>();
        ex.add(A);
        ex.add(B);
        ex.add(C);
        axioms.add(factory.getOWLDisjointUnionAxiom(D,ex));

        OWLOntology ontology = manager.createOntology(axioms);

        OWLOntology normalizedOnt = normalizer.normalize(ontology);

        for (OWLAxiom ax : normalizedOnt.getTBoxAxioms(true)) {
            System.out.println(ax);
        }
    }


    // C->or(D,neg E) -passed
    /*should add a simplification procedure as per normalization procedure of M.S thesis on phase 4.
    *The goal is to avoid adding an extra fresh concept, hence an extra axiom
    */
    @Test
    public void testNormalize50() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));
        OWLClass E = factory.getOWLClass(IRI.create("http://www.example.org/#E"));

        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(E));

        axioms.add(factory.getOWLSubClassOfAxiom(C,
                                                 factory.getOWLObjectUnionOf(D,
                                                                             factory.getOWLObjectComplementOf(E))));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        //OWLProfileReport report = profile.checkOntology(ontology);
        //System.out.println(report);
        //assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(3, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    // and(C,neg D)-> E -passed
    /*should add a simplification procedure as per normalization procedure of M.S thesis on phase 4.
    *The goal is to avoid adding an extra fresh concept, hence an extra axiom
    */
    @Test
    public void testNormalize51() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));
        OWLClass E = factory.getOWLClass(IRI.create("http://www.example.org/#E"));

        axioms.add(factory.getOWLDeclarationAxiom(C));
        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(E));

        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(C,
                                                                                    factory.getOWLObjectComplementOf(D)),
                                                 E));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        //OWLProfileReport report = profile.checkOntology(ontology);
        //System.out.println(report);
        //assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    /* and(bottom,D)->E -passed
        D->or(E,top) -passed
        ---------------------
        should return 0 logical axioms
    */
    @Test
    public void testNormalizePhaze3DropTrivialAxioms() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));
        OWLClass E = factory.getOWLClass(IRI.create("http://www.example.org/#E"));

        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(E));

        //and(bottom,D)->E
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(factory.getOWLNothing(), D), E));

        //D->or(E,top)
        axioms.add(factory.getOWLSubClassOfAxiom(D, factory.getOWLObjectUnionOf(factory.getOWLThing(), E)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        //OWLProfileReport report = profile.checkOntology(ontology);
        //System.out.println(report);
        //assertTrue(report.isInProfile());

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    /* and(top,D)->E -passed
        D->or(E,bottom) -passed
        --------------------------
        should return
        D->E
    */
    @Test
    public void testNormalizePhaze3SimplificationSteps() throws OWLOntologyCreationException {
        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();

        OWLClass D = factory.getOWLClass(IRI.create("http://www.example.org/#D"));
        OWLClass E = factory.getOWLClass(IRI.create("http://www.example.org/#E"));

        OWLDeclarationAxiom q=factory.getOWLDeclarationAxiom(D);
        OWLDeclarationAxiom d=factory.getOWLDeclarationAxiom(D);

        axioms.add(factory.getOWLDeclarationAxiom(D));
        axioms.add(factory.getOWLDeclarationAxiom(E));

        //and(bottom,D)->E
        axioms.add(factory.getOWLSubClassOfAxiom(factory.getOWLObjectIntersectionOf(factory.getOWLThing(), D),E));

        //D->or(E,top)
        axioms.add(factory.getOWLSubClassOfAxiom(D,factory.getOWLObjectUnionOf(factory.getOWLNothing(),E)));

        OWLOntology ontology = manager.createOntology(axioms);

        ALCH_Profile profile = new ALCH_Profile();

        OWLOntology normalizedOnt = normalizer.normalize(ontology);
        //assertEquals(2, normalizedOnt.getLogicalAxiomCount());

        for (OWLAxiom ax : normalizedOnt.getAxioms()) {
            System.out.println(ax);
        }
    }

    @Test
    public void testCheckAxiomsTypesOfNormalizedOntologies() throws OWLOntologyCreationException, IOException {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();

        outputLines.add("Filename,cntBefore,cntNF1, cntNF10,cntNF11,cntNF12,cntNF2,cntNF3,cntNF4,Other");

        ALCH_Normalizer normalizer = new ALCH_Normalizer();
        OWLOntologyManager man = OWLManager.createOWLOntologyManager();
        OWLOntology inputOnt;
        OWLOntology normalizedOnt;


        int startFrom=1;
        int untilNo=40;

        int i=1;
        for (File fileEntry : folder.listFiles()) {
            if(i<startFrom){
                i++;
                continue;}
            else if(i>untilNo){break;}
            else{i++;}

            System.out.println(fileEntry.getName());

            inputOnt = man.loadOntologyFromOntologyDocument(fileEntry);
            normalizedOnt = normalizer.normalize(inputOnt);

            int cntBefore = inputOnt.getLogicalAxioms().size();//all logical axioms
            int cntNF1 = 0;//AND(Ai)->OR(Bj)
            int cntNF10 = 0;//simple axioms A->B
            int cntNF11 = 0;//A->OR(Bj)
            int cntNF12 = 0;//AND(Ai)->B
            int cntNF2 = 0;//A->Exists
            int cntNF3 = 0;//A->ForAll
            int cntNF4 = 0;//r->s;
            int cntOther = 0;//axioms that are not GCI or RIA

            String strContainer = "";
            inputOnt = null;
            for (OWLAxiom axiom : normalizedOnt.getLogicalAxioms()) {

                strContainer =axiomInNF(axiom);
                if (strContainer=="NF1") {
                    cntNF1++;
                }
                else if(strContainer=="NF10"){
                    cntNF10++;
                }
                else if(strContainer=="NF11"){
                    cntNF11++;
                }
                else if(strContainer=="NF12"){
                    cntNF12++;
                }
                else if(strContainer=="NF2"){
                    cntNF2++;
                }
                else if(strContainer=="NF3"){
                    cntNF3++;
                }
                else if(strContainer=="NF4"){
                    cntNF4++;
                }
                else{
                    cntOther++;
                }
            }
            normalizedOnt = null;
            outputLines.add(fileEntry.getName() + "," +
                    cntBefore + "," +
                    cntNF1 + "," +
                    cntNF10 + "," +
                    cntNF11 + "," +
                    cntNF12 + "," +
                    cntNF2 + "," +
                    cntNF3 + "," +
                    cntNF4 + "," +
                    cntOther);

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/NormalizedOntologyStats0.txt");
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));

        }
    }

    public String axiomInNF(OWLAxiom axiom){
        boolean normalizedAxiom=true;
        String inNF="Other";
        if(axiom.getAxiomType()==AxiomType.SUBCLASS_OF)
        {
            OWLSubClassOfAxiom ax =(OWLSubClassOfAxiom)axiom;
            OWLClassExpression subClass = ax.getSubClass();
            OWLClassExpression superClass = ax.getSuperClass();
            //if axiom is in NF1 and(Ai)->or(Bj)
            if(subClass.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF
                    && superClass.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                normalizedAxiom=true;
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf)subClass;
                OWLObjectUnionOf or = (OWLObjectUnionOf)superClass;
                for(OWLClassExpression cl:and.getOperands()){
                    if(cl.getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                        normalizedAxiom=false;
                    }else if(and.getOperands().size()<2){
                        normalizedAxiom=false;
                    }
                }
                for(OWLClassExpression cl:or.getOperands()){
                    if(cl.getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                        normalizedAxiom=false;
                    }else if(or.getOperands().size()<2){
                        normalizedAxiom=false;
                    }
                }
                if(normalizedAxiom){
                    inNF="NF1";
                }else{
                    System.err.println(axiom);
                }
            }//simple case A->B
            else if(subClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                    &&superClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                inNF="NF10";
            }
            //A->or(C,D)
            else if(subClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                    && superClass.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                normalizedAxiom=true;
                OWLObjectUnionOf or = (OWLObjectUnionOf)superClass;
                for(OWLClassExpression cl:or.getOperands()){
                    if(cl.getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                        normalizedAxiom=false;
                    }else if(or.getOperands().size()<2){
                        normalizedAxiom=false;
                    }
                }
                if(normalizedAxiom){
                    inNF="NF11";
                }else{
                    System.err.println(axiom);
                }
            }
            //and(A,B)->C
            else if(subClass.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF
                    && superClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                normalizedAxiom=true;
                OWLObjectIntersectionOf and = (OWLObjectIntersectionOf)subClass;
                for(OWLClassExpression cl:and.getOperands()){
                    if(cl.getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                        normalizedAxiom=false;
                    }else if(and.getOperands().size()<2){
                        normalizedAxiom=false;
                    }
                }
                if(normalizedAxiom){
                    inNF="NF12";
                }else{
                    System.err.println(axiom);
                }
            }
            //A->Exists(r.B)
            else if(subClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                    && superClass.getClassExpressionType()==ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                normalizedAxiom=true;
                OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom)superClass;
                if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS)
                    normalizedAxiom=false;

                if(normalizedAxiom){
                    inNF="NF2";
                }else{
                    System.err.println(axiom);
                }
            }
            //A->All(r.B)
            else if(subClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                    && superClass.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM){
                normalizedAxiom=true;
                OWLObjectAllValuesFrom some = (OWLObjectAllValuesFrom)superClass;
                if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS)
                    normalizedAxiom=false;

                if(normalizedAxiom){
                    inNF="NF3";
                }else{
                    System.err.println(axiom);
                }
            }
            else{
                System.err.println(axiom);
            }
        }
        else if(axiom.getAxiomType()==AxiomType.SUB_OBJECT_PROPERTY){
            inNF="NF4";
        }
        return inNF;
    }



    public static void main(String[] args) throws OWLOntologyCreationException, OWLOntologyStorageException{
        new ALCH_NormalizerTest().testEqv();
    }
}
