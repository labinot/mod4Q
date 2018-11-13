package org.semanticweb.clipper.alch.ontology;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bato on 8/2/2016.
 */
public class BrowserTest {
    @Test
    public void testPrint2FileAxiomCount() throws Exception {
        Browser browseOntology = new Browser();
        browseOntology.printAxiomCount4File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl","C:/Users/bato/Desktop/stats");
    }

    @Test
    public void testprintAxiomCount4Folder() throws Exception{
        Browser browseOntology = new Browser();
        browseOntology.printAxiomCount4Folder("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/process/", "C:/Users/bato/Desktop/stats/AxiomCount.txt");
    }

    @Test
    public void testPrint2FileAxiomTyoes4File() throws Exception {
        Browser browseOntology = new Browser();
        browseOntology.printAxiomTypes4File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl", "C:/Users/bato/Desktop/stats", false);
    }

    @Test
    public void testPrint2FileAxiomTyoes4Folder() throws Exception {
        Browser browseOntology = new Browser();
        browseOntology.printAxiomTypes4Folder("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/", "C:/Users/bato/Desktop/stats/AxiomTypes.txt");
    }


    @Test
    public void testAxiomsWithComplexConcepts4File() throws Exception {
        Browser browseOntology = new Browser();
        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

        ArrayList<String> arrAxioms=browseOntology.getAxiomsWithComplexExpressions4File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00001.owl", false);

//        Collections.sort(arrAxioms);

        Path outputFile = Paths.get("C:/Users/bato/Desktop/stats/testi.txt");
        Files.write(outputFile, arrAxioms, Charset.forName("UTF-8"));
    }

    /*THIS METHOD EXTRACTS THE COMPLEX* AXIOM OF INPUTED FOLDER WITH ONTOLOGIES TO SOME DESIRED FOLDER DENTOED IN THE VARIABLE PATH BELOW*/
    @Test
    public void testAxiomsWithComplexConcepts4Folder() throws Exception {

        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/");

        Browser browseOntology = new Browser();

/*        int startingFromFile=25;
        int numberOfFile2Process=1;

        int cnt=0;*/

        for (File fileEntry : folder.listFiles()) {
/*          if(cnt-startingFromFile>=numberOfFile2Process) {
                break;
            }
            else if(cnt>=startingFromFile) {
                cnt++;
            }else {
                cnt++;
                continue;
            }
*/
            System.out.println(fileEntry.getName().toString());
            ArrayList<String> arrAxioms=browseOntology.getAxiomsWithComplexExpressions4File(fileEntry.getPath(), false);
/*
            for(String str:arrAxioms){
                System.out.println(str);
            }
*/
            Path outputFile = Paths.get("C:/Users/bato/Desktop/stats/ComplexAxioms" + fileEntry.getName().toString());
            Files.write(outputFile, arrAxioms, Charset.defaultCharset());
        }

    }

    @Test
    public void checkDLRenderingOfOWLAxioms() throws Exception {
        OWLOntologyManager manager=OWLManager.createOWLOntologyManager();;
        OWLDataFactory factory=manager.getOWLDataFactory();

        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

        OWLIndividual a=factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a"));;
        OWLIndividual b=factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#b"));;
        OWLIndividual a1=factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a1"));;
        OWLIndividual a2=factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#a2"));;
        OWLIndividual b1=factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#b1"));;
        OWLIndividual b2=factory.getOWLNamedIndividual(IRI.create("http://www.example.org/#b2"));;

        OWLClass A=factory.getOWLClass(IRI.create("http://www.example.org/#A"));;
        OWLClass B=factory.getOWLClass(IRI.create("http://www.example.org/#B"));;
        OWLClass C=factory.getOWLClass(IRI.create("http://www.example.org/#C"));;
        OWLClass D=factory.getOWLClass(IRI.create("http://www.example.org/#D"));;
        OWLClass A1=factory.getOWLClass(IRI.create("http://www.example.org/#A1"));;
        OWLClass A2=factory.getOWLClass(IRI.create("http://www.example.org/#A2"));;
        OWLClass B1=factory.getOWLClass(IRI.create("http://www.example.org/#B1"));;
        OWLClass B2=factory.getOWLClass(IRI.create("http://www.example.org/#B2"));;
        OWLClass C1=factory.getOWLClass(IRI.create("http://www.example.org/#C1"));;
        OWLClass C2=factory.getOWLClass(IRI.create("http://www.example.org/#C2"));;
        OWLClass D1=factory.getOWLClass(IRI.create("http://www.example.org/#D1"));;
        OWLClass D2=factory.getOWLClass(IRI.create("http://www.example.org/#D2"));;

        OWLLiteral lit=factory.getOWLLiteral(5);

        OWLObjectProperty r=factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        OWLObjectProperty s=factory.getOWLObjectProperty(IRI.create("http://www.example.org/#s"));
        OWLObjectProperty r1=factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r1"));

        OWLDataProperty r_d=factory.getOWLDataProperty(IRI.create("http://www.example.org/#r_d"));
        OWLDataRange d_range = factory.getOWLDatatype(IRI.create("^^int"));

        Set<OWLIndividual> individuals = new HashSet<OWLIndividual>();
        individuals.add(a);
        individuals.add(b);
        individuals.add(a1);
        individuals.add(b2);
        /*ClassExpressionType.OWL_CLASS
        ClassExpressionType.DATA_SOME_VALUES_FROM
        ClassExpressionType.DATA_ALL_VALUES_FROM
        ClassExpressionType.DATA_HAS_VALUE
        ClassExpressionType.DATA_EXACT_CARDINALITY
        ClassExpressionType.DATA_MAX_CARDINALITY
        ClassExpressionType.DATA_MIN_CARDINALITY
        ClassExpressionType.OBJECT_ONE_OF
        ClassExpressionType.OBJECT_COMPLEMENT_OF
        ClassExpressionType.OBJECT_HAS_SELF
        ClassExpressionType.OBJECT_HAS_VALUE
        ClassExpressionType.OBJECT_EXACT_CARDINALITY
        ClassExpressionType.OBJECT_MAX_CARDINALITY
        ClassExpressionType.OBJECT_MIN_CARDINALITY
        ClassExpressionType.OBJECT_INTERSECTION_OF
        ClassExpressionType.OBJECT_UNION_OF
        ClassExpressionType.OBJECT_SOME_VALUES_FROM
        ClassExpressionType.OBJECT_ALL_VALUES_FROM*/



        System.out.println("SimpleCase:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, B)));
        System.out.println("ObjectSomeValuesFrom LHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectSomeValuesFrom(r, B))));
        System.out.println("DataSomeValuesFrom RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLDataSomeValuesFrom(r_d, d_range))));
        System.out.println("ObjectAllValuesFrom:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectAllValuesFrom(r, B))));
        System.out.println("DataAllValuesFrom RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLDataSomeValuesFrom(r_d, d_range))));
        System.out.println("DataHasValue RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLDataHasValue(r_d, lit))));
        System.out.println("ObjectHasValue RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectHasValue(s, b))));
        System.out.println("ObjectExactCardinality:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectExactCardinality(5, r, B))));
        System.out.println("DataExactCardinality RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLDataExactCardinality(5, r_d, d_range))));
        System.out.println("ObjectMaxCardinality:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectMaxCardinality(5, r, B))));
        System.out.println("DataMaxCardinality RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLDataMaxCardinality(5, r_d, d_range))));
        System.out.println("ObjectMinCardinality:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectMinCardinality(5, r, B))));
        System.out.println("DataMinCardinality RHS:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLDataMinCardinality(3, r_d, d_range))));
        System.out.println("ObjectOneOf:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectOneOf(individuals))));
        System.out.println("ObjectComplementOf:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectComplementOf(B))));
        System.out.println("ObjectHasSelf:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectHasSelf(s))));
        System.out.println("ObjectIntersectionOf:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectIntersectionOf(B, C, D))));
        System.out.println("ObjectUnionOf:"+renderer.render(
                factory.getOWLSubClassOfAxiom(A, factory.getOWLObjectUnionOf(B,C,D))));

    }

    @Test
    public void testBrowseAxioddms4File() throws OWLOntologyCreationException, IOException {
        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();
        File file= new File("C:/Users/bato/Desktop/LUBM/LUBM0/univ-bench.owl");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);

/*        System.out.println("getAxioms()"+onto.getAxioms().size());
        System.out.println("getAxiomCount()"+onto.getAxiomCount());
        System.out.println("getABoxAxioms(false)"+onto.getABoxAxioms(false).size());
        System.out.println("getABoxAxioms(true):"+onto.getABoxAxioms(true).size());
        System.out.println("getTBoxAxioms(false)"+onto.getTBoxAxioms(false).size());
        System.out.println("getTBoxAxioms(true):"+onto.getTBoxAxioms(true).size());
        System.out.println("getLogicalAxiomCount()" + onto.getLogicalAxiomCount());*/

        for(OWLAxiom ax:onto.getTBoxAxioms(true)){
            System.out.println(ax.getNNF().toString());
        }

        //onto = normalizer.normalize(onto);
/*        System.out.println("After Normalization");
        System.out.println("getAxioms()"+onto.getAxioms().size());
        System.out.println("onto.getAxiomCount()"+onto.getAxiomCount());
        System.out.println("getABoxAxioms(false)"+onto.getABoxAxioms(false).size());
        System.out.println("getABoxAxioms(true):"+onto.getABoxAxioms(true).size());
        System.out.println("getTBoxAxioms(false)"+onto.getTBoxAxioms(false).size());
        System.out.println("getTBoxAxioms(true):"+onto.getTBoxAxioms(true).size());
        System.out.println("getLogicalAxiomCount()" + onto.getLogicalAxiomCount());*/

        Browser browseOntology = new Browser();


        ArrayList<String> arrAxioms = browseOntology.browseAxiomsOfOntology(onto,false,true,true);
        Path outputFile = Paths.get("C:/Users/bato/Desktop/DL_Axioms_" + file.getName().toString());
        Files.write(outputFile, arrAxioms, Charset.forName("UTF-8"));
    }

    @Test
    public void testBrowseIndividuals() throws OWLOntologyCreationException, IOException{
        File inputFile= new File("/home/bato/data/ijcai2017/city/dlvhex_encoding/wien_250_restaurants_close_2_metro.owl");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology onto = manager.loadOntologyFromOntologyDocument(inputFile);

        ArrayList<String> outputLines = new ArrayList<String>();

        for (OWLNamedIndividual individual : onto.getIndividualsInSignature()) {
            outputLines.add("adom(\""+individual.toString()+"\").");
        }

        ShortFormProvider sfp=new SimpleShortFormProvider();

        for(OWLAxiom a:onto.getABoxAxioms(true)){
            if(a.getAxiomType()==AxiomType.CLASS_ASSERTION){
                OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                System.out.println("p"
                        +sfp.getShortForm(ax.getClassExpression().asOWLClass())
                        +"(\""+ax.getIndividual().toString()+"\").");

            }
            if(a.getAxiomType()==AxiomType.OBJECT_PROPERTY_ASSERTION){
                OWLObjectPropertyAssertionAxiom ax =(OWLObjectPropertyAssertionAxiom) a;
                outputLines.add("p"
                        +sfp.getShortForm(ax.getProperty().asOWLObjectProperty())
                        +"(\""+ax.getSubject().toString()+"\",\""+ax.getObject().toString()+"\").");
            }
        }

        Path outputFile = Paths.get("/home/bato/data/ijcai2017/city/dlvhex_encoding/wien_250_restaurants_close_2_metro_new.hex");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));

        return;
    }

    @Test
    public void testFileReadIn() throws OWLOntologyCreationException, IOException{
        long t1 = System.currentTimeMillis();
        FileReader reader = new FileReader("/home/bato/data/ijcai2017/city/asp_encoding/queries/input_wien_250_q1.lp");
        int character;

        while ((character = reader.read()) != -1) {
            System.out.print((char) character);
        }
        reader.close();
        long t2 = System.currentTimeMillis();

        System.out.println("TIME: " + (t2 - t1));
        return;
    }



}