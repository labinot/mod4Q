package org.semanticweb.clipper.alch.ontology;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by bato on 6/15/2016.
 */
public class ALCH_Type_ExtractorTest {

    ALCH_ABoxTypes extractor=new ALCH_ABoxTypes() ;


    public static void download(String sourceUrl,String targetDirectory) throws MalformedURLException, IOException {
        URL url = new URL(sourceUrl);

        String fileName = url.getFile();

        Path targetPath = new File(targetDirectory + fileName).toPath();

        Files.copy(url.openStream(), targetPath,
                StandardCopyOption.REPLACE_EXISTING);

    }

    @Test
    public void downloadOxfordRepository() throws Exception{
        //download files
        String url_base="http://www.cs.ox.ac.uk/isg/ontologies/UID/";
        for(int i=1;i<798;i++){
            String filename="0000";
            filename = filename + i + ".owl";
            if(filename.length()>9){
                filename=filename.substring(filename.length()-9);
            }
            else if (filename.length()<9)
                throw new IllegalArgumentException();

            String url=url_base+filename;

            this.download(url,"C:/Users/bato/Desktop/OxfordRepository/input");
        }
    }


    @Test
    public void testIndividualFiles() throws Exception {

        for (int i=786;i<=787;i++){
            File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/00"+i+".owl");

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

            OWLOntology ontologyAssertions = extractor.extractTypes(ontology);

            ontology = null;

            Set<OWLAxiom> Axioms = ontologyAssertions.getABoxAxioms(true);

            ontologyAssertions = null;

            ArrayList<String> outputLines = new ArrayList<String>();

            for (OWLAxiom a : Axioms) {
                if (a instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                    //outputLines.add(ax.getIndividual() + "," + ax.getClassExpression());

                    if(ax.getIndividual().toString().indexOf("#")==-1){
                        outputLines.add(" Erroneous individual: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }
                    else if(ax.getIndividual().toString().indexOf("#")==ax.getIndividual().toString().length()){
                        outputLines.add(" Erroneous individual: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }

                    if(ax.getClassExpression().toString().indexOf("#")==-1 && !ax.getClassExpression().toString().equals("owl:Thing")) {
                        outputLines.add(" Erroneous class: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }
                    else if(ax.getClassExpression().toString().indexOf("#")==ax.getClassExpression().toString().length()) {
                        outputLines.add(" Erroneous class: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }
                }
            }

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/" + file.getName().substring(0, file.getName().length() - 4) + "_output.txt");
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));

        }
    }

    //iterator with costum bounds
    @Test
    public void extractABoxAxiomsForSpecificFiles() throws Exception {

        for (int i=1;i<=787;i++){

            if(i==291 ||i==699 ||i==769||i==785) {
                continue;
            }

            String str="0000"+i;
            str = str.substring(str.length()-5);

            File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/"+str+".owl");

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

            Set<OWLAxiom> aBox=ontology.getABoxAxioms(true);

            ArrayList<String> outputLines = new ArrayList<String>();

            for (OWLAxiom ax : aBox) {
                outputLines.add(" Erroneous class: " + ax.toString());
            }

            if(outputLines.size()>0)
                System.out.println(str+".owl:"+outputLines.size());

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/" + file.getName()+".txt");
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
        }
    }

    //iterator with costum bounds
    @Test
    public void extractABoxAxiomsForAllFiles() throws Exception {

        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        for (File fileEntry : folder.listFiles()){

            File file = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/"+fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(file);

            Set<OWLAxiom> aBox=ontology.getABoxAxioms(true);

            ArrayList<String> outputLines = new ArrayList<String>();

            for (OWLAxiom ax : aBox) {
                if(ax.toString().indexOf("ClassAssertion(")!=0
                        &&ax.toString().indexOf("ObjectPropertyAssertion(")!=0
                        &&ax.toString().indexOf("DataPropertyAssertion(")!=0
                        &&ax.toString().indexOf("DifferentIndividuals(")!=0
                        &&ax.toString().indexOf("SameIndividual")!=0)
                    outputLines.add(ax.toString());
            }

            if(outputLines.size()>0)
                System.out.println(fileEntry.getName()+":"+outputLines.size());

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/" + file.getName()+".txt");
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
        }
    }

    /*returns individuals that are not named in the standard manner
    * additionally it returns the classes*/
    @Test
    public void testForHashtagInIndividual() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            OWLOntology ontologyAssertions = extractor.extractTypes(ontology);

            ontology = null;

            Set<OWLAxiom> Axioms = ontologyAssertions.getABoxAxioms(true);

            ontologyAssertions = null;

            ArrayList<String> outputLines = new ArrayList<String>();

            for (OWLAxiom a : Axioms) {
                if (a instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                    //outputLines.add(ax.getIndividual() + "," + ax.getClassExpression());

                    if(ax.getIndividual().toString().indexOf("#")==-1){
                        outputLines.add(" Erroneous individual: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }
                    else if(ax.getIndividual().toString().indexOf("#")==ax.getIndividual().toString().length()){
                        outputLines.add(" Erroneous individual: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }

                    if(ax.getClassExpression().toString().indexOf("#")==-1 && !ax.getClassExpression().toString().equals("owl:Thing")) {
                        outputLines.add(" Erroneous class: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }
                    else if(ax.getClassExpression().toString().indexOf("#")==ax.getClassExpression().toString().length()) {
                        outputLines.add(" Erroneous class: " + ax.getIndividual() + " , " + ax.getClassExpression());
                    }
                }
            }

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/" + fileEntry.getName().substring(0, fileEntry.getName().length() - 4) + "_output.txt");
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
        }

    }

    @Test
    public void testAggregateClassExpressionsFromExpressions() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();
        outputLines.add("FileName,Class,ObjectIntersectionOf,ObjectUnionOf,ObjectComplementOf,ObjectOneOf,ObjectSomeValuesFrom,ObjectAllValuesFrom,ObjectHasValue,ObjectHasSelf,ObjectMinCardinality,ObjectMaxCardinality,ObjectExactCardinality,DataSomeValuesFrom,DataAllValuesFrom,DataHasValue,DataMinCardinality,DataMaxCardinality,DataExactCardinality,Other");

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            Set<OWLAxiom> aBox=ontology.getABoxAxioms(true);

            ontology=null;

            int cntClass=0,
                cntObjectIntersectionOf=0,
                cntObjectUnionOf=0,
                cntObjectComplementOf=0,
                cntObjectOneOf=0,
                cntObjectSomeValuesFrom=0,
                cntObjectAllValuesFrom=0,
                cntObjectHasValue=0,
                cntObjectHasSelf=0,
                cntObjectMinCardinality=0,
                cntObjectMaxCardinality=0,
                cntObjectExactCardinality=0,
                cntDataSomeValuesFrom=0,
                cntDataAllValuesFrom=0,
                cntDataHasValue=0,
                cntDataMinCardinality=0,
                cntDataMaxCardinality=0,
                cntDataExactCardinality=0,
                cntOther = 0;

            for (OWLAxiom a : aBox) {
                if (a instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;

                    if(ax.getClassExpression().getClassExpressionType().toString()=="Class"){
                        cntClass++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectIntersectionOf"){
                        cntObjectIntersectionOf++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectUnionOf"){
                        cntObjectUnionOf++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectComplementOf"){
                        cntObjectComplementOf++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectOneOf"){
                        cntObjectOneOf++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectSomeValuesFrom"){
                        cntObjectSomeValuesFrom++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectAllValuesFrom"){
                        cntObjectAllValuesFrom++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectHasValue"){
                        cntObjectHasValue++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectHasSelf"){
                        cntObjectHasSelf++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectMinCardinality"){
                        cntObjectMinCardinality++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectMaxCardinality"){
                        cntObjectMaxCardinality++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="ObjectExactCardinality"){
                        cntObjectExactCardinality++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="DataSomeValuesFrom"){
                        cntDataSomeValuesFrom++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="DataAllValuesFrom"){
                        cntDataAllValuesFrom++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="DataHasValue"){
                        cntDataHasValue++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="DataMinCardinality"){
                        cntDataMinCardinality++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="DataMaxCardinality"){
                        cntDataMaxCardinality++;
                    }else if(ax.getClassExpression().getClassExpressionType().toString()=="DataExactCardinality"){
                        cntDataExactCardinality++;
                    }else {
                        cntOther++;
                    }
                }
            }
            outputLines.add(fileEntry.getName() + "," +
                    cntClass + "," +
                    cntObjectIntersectionOf + "," +
                    cntObjectUnionOf + "," +
                    cntObjectComplementOf + "," +
                    cntObjectOneOf + "," +
                    cntObjectSomeValuesFrom + "," +
                    cntObjectAllValuesFrom + "," +
                    cntObjectHasValue + "," +
                    cntObjectHasSelf + "," +
                    cntObjectMinCardinality + "," +
                    cntObjectMaxCardinality + "," +
                    cntObjectExactCardinality + "," +
                    cntDataSomeValuesFrom + "," +
                    cntDataAllValuesFrom + "," +
                    cntDataHasValue + "," +
                    cntDataMinCardinality + "," +
                    cntDataMaxCardinality + "," +
                    cntDataExactCardinality + "," +
                    cntOther);
        }

        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/stats.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    /*This method returns the number of simple (atomic) classes used in class assertion axioms (it neglects complex class expressions)*/
    @Test
    public void testAggregateNumClasses() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();
        outputLines.add("FileName,ClassNo");

        for (File fileEntry : folder.listFiles()) {

            Set<String> Classes = new HashSet<>();

            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            Set<OWLAxiom> aBox=ontology.getABoxAxioms(true);

            ontology=null;

            for (OWLAxiom a : aBox) {
                if (a instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                        if(ax.getClassExpression().getClassExpressionType().toString()=="Class") {
                            Classes.add(ax.getClassExpression().toString());
                        }
                }
            }
            outputLines.add(fileEntry.getName() + "," + Classes.size());
        }

        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/statsClassessTypes.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    @Test
    public void testNonStandardIndividuals() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            OWLOntology ontologyAssertions = extractor.extractTypes(ontology);

            ontology = null;

            Set<OWLAxiom> Axioms = ontologyAssertions.getABoxAxioms(true);

            ontologyAssertions = null;

            ArrayList<String> outputLines = new ArrayList<String>();

            for (OWLAxiom a : Axioms) {
                if (a instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                    //outputLines.add(ax.getIndividual() + "," + ax.getClassExpression());

                    if((ax.getIndividual().toString().indexOf(":genid")>0)) {
                        outputLines.add(ax.getIndividual().toString());
                    }
                }
            }

            if(outputLines.size()>0)
                System.out.println(fileEntry.getName()+":"+outputLines.size());

            //Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/" + fileEntry.getName().substring(0, fileEntry.getName().length() - 4) + "_output.txt");
            //Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
        }
    }

    /*
    * this methods extracts the statistic regarding the number of individuals in each owl file of the given
    * directory
    * */
    @Test
    public void testAggregateIndividualsInAssertionAxioms() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();
        outputLines.add("FileName, NumberOfIndividuals, NumberOfUniqueIndividuals");

        int startFrom=0;
        int numOfFilesToProcess=800; //if this is bigger than the numbers of files in the directory (that's OK)
        int iterator=0;

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            iterator++;

            if(iterator>numOfFilesToProcess)
                break;

            if(iterator>=startFrom) {
                OWLOntologyManager man = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

                Set<OWLAxiom> aBox = ontology.getABoxAxioms(true);

                ontology = null;

                Set<String> aBoxUniqueIndividuals = new HashSet<String>();
                ArrayList<String> aBoxIndividuals = new ArrayList<String>();

                for (OWLAxiom a : aBox) {
                    if (a instanceof OWLClassAssertionAxiom) {
                        OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                        aBoxUniqueIndividuals.add(ax.getIndividual().toString());
                        aBoxIndividuals.add(ax.getIndividual().toString());
                    }
                }

                outputLines.add(fileEntry.getName() + "," + aBoxIndividuals.size() + "," + aBoxUniqueIndividuals.size());
            }
        }
        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/NumberOfIndividuals.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    /*
     *      this methods pulls all type of abox axioms from a group of files
    * */
    @Test
    public void testGetAxiomTypes() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();

        int startFrom=0;
        int numOfFilesToProcess=800; //if this is bigger than the numbers of files in the directory (that's OK)
        int iterator=0;

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            iterator++;

            if(iterator>numOfFilesToProcess)
                break;

            if(iterator>=startFrom) {
                OWLOntologyManager man = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

                Set<OWLAxiom> aBox = ontology.getABoxAxioms(true);

                ontology = null;

                for (OWLAxiom a : aBox) {
                        outputLines.add(a.getAxiomType().toString());
                }
            }
        }
        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/ABoxAxiomTypes.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

/*
 *      this methods pulls all type of tbox axioms from a group of files
* */
    @Test
    public void testGetTboxAxiomTypes() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();

        int startFrom=0;
        int numOfFilesToProcess=800; //if this is bigger than the numbers of files in the directory (that's OK)
        int iterator=0;

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            iterator++;

            if(iterator>numOfFilesToProcess)
                break;

            if(iterator>=startFrom) {
                OWLOntologyManager man = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

                Set<OWLAxiom> aBox = ontology.getTBoxAxioms(true);

                ontology = null;

                for (OWLAxiom a : aBox) {
                    outputLines.add(a.getAxiomType().toString());
                }
            }
        }
        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/TBoxAxiomTypes.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }


    /*
     *      this methods aggregates the number of tbox axiom types per file
    * */
    @Test
    public void testAggregateTBoxAxiomTypes() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();

        outputLines.add("Filename,TboxAxioms, SubClassOf, EquivalentClasses, DisjointClasses, DisjointUnion, TransitiveObjectProperty, FunctionalObjectProperty, ReflexiveObjectProperty, SymmetricObjectProperty,    IrrefexiveObjectProperty, AsymmetricObjectProperty,   InverseFunctionalObjectProperty, InverseObjectProperties,    ObjectPropertyDomain, ObjectPropertyRange,        SubObjectPropertyOf,  EquivalentObjectProperties, DisjointObjectProperties, DataPropertyDomain,         DataPropertyRange, FunctionalDataProperty,     SubDataPropertyOf, EquivalentDataProperties,   DisjointDataProperties, Other");

        int startFrom=0;
        int numOfFilesToProcess=800; //if this is bigger than the numbers of files in the directory (that's OK)
        int iterator=0;

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            iterator++;

            if(iterator>numOfFilesToProcess)
                break;

            int cntTboxAxioms=0;
            int cntSubClassOf=0;
            int cntEquivalentClasses=0;
            int cntDisjointClasses=0;
            int cntDisjointUnion=0;
            int cntTransitiveObjectProperty=0;
            int cntFunctionalObjectProperty=0;
            int cntReflexiveObjectProperty=0;
            int cntSymmetricObjectProperty=0;
            int cntIrrefexiveObjectProperty=0;
            int cntAsymmetricObjectProperty=0;
            int cntInverseFunctionalObjectProperty=0;
            int cntInverseObjectProperties=0;
            int cntObjectPropertyDomain=0;
            int cntObjectPropertyRange=0;
            int cntSubObjectPropertyOf=0;
            int cntEquivalentObjectProperties=0;
            int cntDisjointObjectProperties=0;
            int cntDataPropertyDomain=0;
            int cntDataPropertyRange=0;
            int cntFunctionalDataProperty=0;
            int cntSubDataPropertyOf=0;
            int cntEquivalentDataProperties=0;
            int cntDisjointDataProperties=0;
            int cntOther=0;

            if(iterator>=startFrom) {
                OWLOntologyManager man = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

                Set<OWLAxiom> tBox = ontology.getTBoxAxioms(true);

                ontology = null;

                for (OWLAxiom a : tBox) {
                    cntTboxAxioms++;
                    if(a.getAxiomType().toString()=="SubClassOf"){cntSubClassOf++;}
                    else if(a.getAxiomType().toString()=="EquivalentClasses"){cntEquivalentClasses++;}
                    else if(a.getAxiomType().toString()=="DisjointClasses"){cntDisjointClasses++;}
                    else if(a.getAxiomType().toString()=="DisjointUnion"){cntDisjointUnion++;}
                    else if(a.getAxiomType().toString()=="TransitiveObjectProperty"){cntTransitiveObjectProperty++;}
                    else if(a.getAxiomType().toString()=="FunctionalObjectProperty"){cntFunctionalObjectProperty++;}
                    else if(a.getAxiomType().toString()=="ReflexiveObjectProperty"){cntReflexiveObjectProperty++;}
                    else if(a.getAxiomType().toString()=="SymmetricObjectProperty"){cntSymmetricObjectProperty++;}
                    else if(a.getAxiomType().toString()=="IrrefexiveObjectProperty"){cntIrrefexiveObjectProperty++;}
                    else if(a.getAxiomType().toString()=="AsymmetricObjectProperty"){cntAsymmetricObjectProperty++;}
                    else if(a.getAxiomType().toString()=="InverseFunctionalObjectProperty"){cntInverseFunctionalObjectProperty++;}
                    else if(a.getAxiomType().toString()=="InverseObjectProperties"){cntInverseObjectProperties++;}
                    else if(a.getAxiomType().toString()=="ObjectPropertyDomain"){cntObjectPropertyDomain++;}
                    else if(a.getAxiomType().toString()=="ObjectPropertyRange"){cntObjectPropertyRange++;}
                    else if(a.getAxiomType().toString()=="SubObjectPropertyOf"){cntSubObjectPropertyOf++;}
                    else if(a.getAxiomType().toString()=="EquivalentObjectProperties"){cntEquivalentObjectProperties++;}
                    else if(a.getAxiomType().toString()=="DisjointObjectProperties"){cntDisjointObjectProperties++;}
                    else if(a.getAxiomType().toString()=="DataPropertyDomain"){cntDataPropertyDomain++;}
                    else if(a.getAxiomType().toString()=="DataPropertyRange"){cntDataPropertyRange++;}
                    else if(a.getAxiomType().toString()=="FunctionalDataProperty"){cntFunctionalDataProperty++;}
                    else if(a.getAxiomType().toString()=="SubDataPropertyOf"){cntSubDataPropertyOf++;}
                    else if(a.getAxiomType().toString()=="EquivalentDataProperties"){cntEquivalentDataProperties++;}
                    else if(a.getAxiomType().toString()=="DisjointDataProperties"){cntDisjointDataProperties++;}
                    else{cntOther++;}

                }

                outputLines.add(fileEntry.getName() + "," +
                                cntTboxAxioms + "," +
                                cntSubClassOf + "," +
                                cntEquivalentClasses + "," +
                                cntDisjointClasses + "," +
                                cntDisjointUnion + "," +
                                cntTransitiveObjectProperty + "," +
                                cntFunctionalObjectProperty + "," +
                                cntReflexiveObjectProperty + "," +
                                cntSymmetricObjectProperty + "," +
                                cntIrrefexiveObjectProperty + "," +
                                cntAsymmetricObjectProperty + "," +
                                cntInverseFunctionalObjectProperty + "," +
                                cntInverseObjectProperties + "," +
                                cntObjectPropertyDomain + "," +
                                cntObjectPropertyRange + "," +
                                cntSubObjectPropertyOf + "," +
                                cntEquivalentObjectProperties + "," +
                                cntDisjointObjectProperties + "," +
                                cntDataPropertyDomain + "," +
                                cntDataPropertyRange + "," +
                                cntFunctionalDataProperty + "," +
                                cntSubDataPropertyOf + "," +
                                cntEquivalentDataProperties + "," +
                                cntDisjointDataProperties + "," +
                                cntOther);
            }
        }
        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/TBoxAxiomTypesStats.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    /*
     *      this methods aggregates the number of assertion axiom types per file
    * */
    @Test
    public void testAggregateAssertionAxiomTypes() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();

        outputLines.add("Filename,DataPropertyAssertion,SameIndividual,ClassAssertion,DifferentIndividuals,ObjectPropertyAssertion,Other");

        int startFrom=0;
        int numOfFilesToProcess=800; //if this is bigger than the numbers of files in the directory (that's OK)
        int iterator=0;

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            iterator++;

            if(iterator>numOfFilesToProcess)
                break;

            int cntDataPropertyAssertion=0;
            int cntSameIndividual=0;
            int cntClassAssertion=0;
            int cntDifferentIndividuals=0;
            int cntObjectPropertyAssertion=0;
            int cntOther=0;

            if(iterator>=startFrom) {
                OWLOntologyManager man = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

                Set<OWLAxiom> aBox = ontology.getABoxAxioms(true);

                ontology = null;

                for (OWLAxiom a : aBox) {
                    if(a.getAxiomType().toString()=="DataPropertyAssertion"){
                        cntDataPropertyAssertion++;}
                    else if(a.getAxiomType().toString()=="SameIndividual"){
                        cntSameIndividual++;}
                    else if(a.getAxiomType().toString()=="ClassAssertion"){
                        cntClassAssertion++;}
                    else if(a.getAxiomType().toString()=="DifferentIndividuals"){
                        cntDifferentIndividuals++;}
                    else if(a.getAxiomType().toString()=="ObjectPropertyAssertion"){
                        cntObjectPropertyAssertion++;}
                    else {
                        cntOther++;}
                }

                outputLines.add(fileEntry.getName() + "," +
                                cntDataPropertyAssertion + "," +
                                cntSameIndividual + "," +
                                cntClassAssertion + "," +
                                cntDifferentIndividuals + "," +
                                cntObjectPropertyAssertion + "," +
                                cntOther);
            }
        }
        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/ABoxAxiomTypesStats.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    /*
    * This methods derives all class assertion axioms found in some ontology
    * */

    @Test
    public void testReturnClassAssertionAxioms() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/ProcessIndividualFile");


        int startFrom=0;
        int numOfFilesToProcess=800; //if this is bigger than the numbers of files in the directory (that's OK)
        int iterator=0;

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            Set<String> outputLines = new HashSet<String>();

            iterator++;

            if(iterator>numOfFilesToProcess)
                break;

            if(iterator>=startFrom) {
                OWLOntologyManager man = OWLManager.createOWLOntologyManager();
                OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

                Set<OWLAxiom> aBox = ontology.getABoxAxioms(true);

                ontology = null;

                for (OWLAxiom a : aBox) {
                    if (a instanceof OWLClassAssertionAxiom) {
                        OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                        outputLines.add("Full Class Assertion:"+a.toString()+", Individual:"+ax.getIndividual().toString()+", ClassExpression:"+ax.getClassExpression().toString());
                    }
                }
                Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/AboxClassAssertions"+fileEntry.getName().toString());
                Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
            }
        }

    }


    /*
    * This methods derives all class assertion axioms found in some ontology
    * */

    @Test
    public void testReturnTBoxAxiomsPerFolder() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/ProcessIndividualFile");


        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());
            Set<String> outputLines = new HashSet<String>();

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            for (OWLAxiom a : ontology.getTBoxAxioms(true)) {
                outputLines.add(a.toString());
            }

            ontology = null;

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/TBoxAxioms"+fileEntry.getName().toString());
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
        }

    }



    @Test
    public void testNonStandardClasses() throws Exception {
        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            OWLOntology ontologyAssertions = extractor.extractTypes(ontology);

            ontology = null;

            Set<OWLAxiom> Axioms = ontologyAssertions.getABoxAxioms(true);

            ontologyAssertions = null;

            ArrayList<String> outputLines = new ArrayList<String>();

            for (OWLAxiom a : Axioms) {
                if (a instanceof OWLClassAssertionAxiom) {
                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;

                    if(ax.getClassExpression().toString().indexOf("owl:Thing")<0 &&
                            (ax.getClassExpression().toString().indexOf("<http:")<0 || ax.getClassExpression().toString().indexOf(" ")>0 || ax.getClassExpression().toString().indexOf(")")>0)) {
                        outputLines.add(ax.getClassExpression().toString());
                    }
                }
            }

            if(outputLines.size()>0)
                System.out.println(fileEntry.getName()+":"+outputLines.size());

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/" + fileEntry.getName().substring(0, fileEntry.getName().length() - 4) + "_output.txt");
            Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
        }
    }

/*
* This method extracts the types out of abox class assertions per file and saves the file to specfied directory at the bottom
* */
    @Test
    public void testExtractTypes() throws Exception {

        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();

        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            Set<OWLAxiom> Axioms = ontology.getABoxAxioms(true);

            ontology = null;

            HashMap<String,HashSet<String>> individualConceptsMAP = new HashMap<String,HashSet<String>>();

            for (OWLAxiom a : Axioms) {
                if (a instanceof OWLClassAssertionAxiom) {

                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                    String individual;
                    HashSet<String> concepts = new HashSet<String>();

                    individual = ax.getIndividual().toString();
                    concepts.add(ax.getClassExpression().toString());

                    if(individualConceptsMAP.containsKey(ax.getIndividual().toString())){
                        individualConceptsMAP.get(individual).add(ax.getClassExpression().toString());
                    }
                    else {
                        individualConceptsMAP.put(individual,concepts);
                    }
                }
            }

            //Hash Set that will hold the types
            HashSet<String> Types = new HashSet<String>();

            for (HashSet<String> concepts : individualConceptsMAP.values()) {
                //sort the elements on the list
                //helper array to facilitate sorting
                ArrayList<String> helperArr = new ArrayList(concepts);
                Collections.sort(helperArr);
                //add to the types (duplicates will be ignored this way)
                Types.add(helperArr.toString());
            }

            Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/Types" + fileEntry.getName().substring(0, fileEntry.getName().length() - 4) + "_output.txt");
            Files.write(outputFile, Types, Charset.forName("UTF-8"));
        }
    }

    @Test
    public void testTypesStats() throws Exception {

        File folder = new File("C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID");

        Set<String> outputLines = new HashSet<String>();
        outputLines.add("FileName,NumOfTypes");

        for (final File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName());

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            OWLOntology ontology = man.loadOntologyFromOntologyDocument(fileEntry);

            Set<OWLAxiom> Axioms = ontology.getABoxAxioms(true);

            ontology = null;

            HashMap<String,HashSet<String>> individualConceptsMAP = new HashMap<String,HashSet<String>>();

            for (OWLAxiom a : Axioms) {
                if (a instanceof OWLClassAssertionAxiom) {

                    OWLClassAssertionAxiom ax = (OWLClassAssertionAxiom) a;
                    String individual;
                    HashSet<String> concepts = new HashSet<String>();

                    individual = ax.getIndividual().toString();
                    concepts.add(ax.getClassExpression().toString());

                    if(individualConceptsMAP.containsKey(ax.getIndividual().toString())){
                        individualConceptsMAP.get(individual).add(ax.getClassExpression().toString());
                    }
                    else {
                        individualConceptsMAP.put(individual,concepts);
                    }
                }
            }

            //Hash Set that will hold the types
            HashSet<String> Types = new HashSet<String>();

            for (HashSet<String> concepts : individualConceptsMAP.values()) {
                //sort the elements on the list
                //helper array to facilitate sorting
                ArrayList<String> helperArr = new ArrayList(concepts);
                Collections.sort(helperArr);
                //add to the types (duplicates will be ignored this way)
                Types.add(helperArr.toString());
            }

            outputLines.add(fileEntry.getName() + "," + Types.size());
        }
        Path outputFile = Paths.get("C:/Users/bato/Desktop/OxfordRepository/output/TypeStats.txt");
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

}
