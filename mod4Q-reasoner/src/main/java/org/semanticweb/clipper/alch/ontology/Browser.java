package org.semanticweb.clipper.alch.ontology;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.owlapi.dlsyntax.DLSyntaxObjectRenderer;
import org.semanticweb.owlapi.io.OWLObjectRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Browser {
    private OWLOntologyManager manager;
    private OWLDataFactory factory;

    public ArrayList<String> getAxiomCountPerFile(String absoluteFilePath) throws OWLOntologyCreationException, IOException {
        File file = new File(absoluteFilePath);

        manager = OWLManager.createOWLOntologyManager();
        OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);

        ArrayList<String> outputLines= new ArrayList<String>();

        outputLines.add(file.getName().toString());
        System.out.println(file.getName().toString());
        outputLines.add("Axiom Count:" + onto.getAxiomCount());
        System.out.println("Axiom Count:" + onto.getAxiomCount());
        outputLines.add("Logical Axiom Count:" + onto.getLogicalAxiomCount());
        System.out.println("Logical Axiom Count:" + onto.getLogicalAxiomCount());
        outputLines.add("Abox Axiom Count(true):" + onto.getABoxAxioms(true).size());
        System.out.println("Abox Axiom Count(true):" + onto.getABoxAxioms(true).size());
        outputLines.add("Abox Axiom Count(false):" + onto.getABoxAxioms(false).size());
        System.out.println("Abox Axiom Count(false):" + onto.getABoxAxioms(false).size());
        outputLines.add("Tbox Axiom Count(true):" + onto.getTBoxAxioms(true).size());
        System.out.println("Tbox Axiom Count(true):" + onto.getTBoxAxioms(true).size());
        outputLines.add("Tbox Axiom Count(false):" + onto.getTBoxAxioms(false).size());
        System.out.println("Tbox Axiom Count(false):" + onto.getTBoxAxioms(false).size());
        outputLines.add("Rbox Axiom Count(true):" + onto.getRBoxAxioms(true).size());
        System.out.println("Rbox Axiom Count(true):" + onto.getRBoxAxioms(true).size());
        outputLines.add("Rbox Axiom Count(false):" + onto.getRBoxAxioms(false).size());
        System.out.println("Rbox Axiom Count(false):" + onto.getRBoxAxioms(false).size());
        return outputLines;
    }

    public void printAxiomCount4File(String absoluteFilePath, String outputFilePath) throws OWLOntologyCreationException, IOException {
        File file = new File(absoluteFilePath);

        ArrayList<String> outputLines;
        outputLines = getAxiomCountPerFile(absoluteFilePath);

        System.out.println(file.getName().toString());
        String outputPath=outputFilePath+"/"+file.getName().toString();

        Path outputFile = Paths.get(outputPath);
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    public void printAxiomCount4Folder(String absoluteFolderPath, String outputFilePath) throws OWLOntologyCreationException, IOException {
        File folder = new File(absoluteFolderPath);

        ArrayList<String> outputLines= new ArrayList<String>();

        for (File fileEntry : folder.listFiles()) {
            outputLines.addAll(getAxiomCountPerFile(fileEntry.getAbsolutePath()));
            outputLines.add("--------------------");
        }
        Path outputFile = Paths.get(outputFilePath);
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    public void printAxiomTypes4Folder(String absoluteFolderPath, String outputFilePath) throws OWLOntologyCreationException, IOException {
        File folder = new File(absoluteFolderPath);

        ArrayList<String> outputLines= new ArrayList<String>();

        for (File fileEntry : folder.listFiles()) {
            System.out.println(fileEntry.getName().toString());
            outputLines.add(fileEntry.getName().toString());
            outputLines.addAll(getAxiomTypesCount4File(fileEntry.getAbsolutePath(), false));
            outputLines.add("--------------------");
            System.out.println("--------------------");
        }
        Path outputFile = Paths.get(outputFilePath);
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    public void printAxiomTypes4File(String absoluteFilePath, String outputFilePath,boolean showDetails) throws OWLOntologyCreationException, IOException {
        File file = new File(absoluteFilePath);

        System.out.println(file.getName().toString());
        ArrayList<String> outputLines;
        outputLines = getAxiomTypesCount4File(absoluteFilePath,showDetails);

        String outputPath=outputFilePath+"/"+file.getName().toString();

        Path outputFile = Paths.get(outputPath);
        Files.write(outputFile, outputLines, Charset.forName("UTF-8"));
    }

    public ArrayList<String> getAxiomTypesCount4File(String absoluteFilePath,boolean showDetails) throws OWLOntologyCreationException, IOException {
        File file = new File(absoluteFilePath);

        manager = OWLManager.createOWLOntologyManager();
        OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);

        ArrayList<String> outputLines= new ArrayList<String>();

        int cntTboxSUBCLASS_OF=0;
        int cntComplexTboxSUBCLASS_OF=0;
        int cntTboxEQUIVALENT_CLASSES=0;
        int cntComplexTboxEQUIVALENT_CLASSES=0;
        int cntTboxDISJOINT_CLASSES=0;
        int cntComplexTboxDISJOINT_CLASSES=0;
        int cntTboxCLASS_ASSERTION=0;
        int cntComplexTboxCLASS_ASSERTION=0;
        int cntTboxSAME_INDIVIDUAL=0;
        int cntComplexTboxSAME_INDIVIDUAL=0;
        int cntTboxDIFFERENT_INDIVIDUALS=0;
        int cntComplexTboxDIFFERENT_INDIVIDUALS=0;
        int cntTboxOBJECT_PROPERTY_ASSERTION=0;
        int cntComplexTboxOBJECT_PROPERTY_ASSERTION=0;
        int cntTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION=0;
        int cntComplexTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION=0;
        int cntTboxDATA_PROPERTY_ASSERTION=0;
        int cntComplexTboxDATA_PROPERTY_ASSERTION=0;
        int cntTboxNEGATIVE_DATA_PROPERTY_ASSERTION=0;
        int cntComplexTboxNEGATIVE_DATA_PROPERTY_ASSERTION=0;
        int cntTboxOBJECT_PROPERTY_DOMAIN=0;
        int cntComplexTboxOBJECT_PROPERTY_DOMAIN=0;
        int cntTboxOBJECT_PROPERTY_RANGE=0;
        int cntComplexTboxOBJECT_PROPERTY_RANGE=0;
        int cntTboxDISJOINT_OBJECT_PROPERTIES=0;
        int cntComplexTboxDISJOINT_OBJECT_PROPERTIES=0;
        int cntTboxSUB_OBJECT_PROPERTY=0;
        int cntComplexTboxSUB_OBJECT_PROPERTY=0;
        int cntTboxEQUIVALENT_OBJECT_PROPERTIES=0;
        int cntComplexTboxEQUIVALENT_OBJECT_PROPERTIES=0;
        int cntTboxINVERSE_OBJECT_PROPERTIES=0;
        int cntComplexTboxINVERSE_OBJECT_PROPERTIES=0;
        int cntTboxSUB_PROPERTY_CHAIN_OF=0;
        int cntComplexTboxSUB_PROPERTY_CHAIN_OF=0;
        int cntTboxFUNCTIONAL_OBJECT_PROPERTY=0;
        int cntComplexTboxFUNCTIONAL_OBJECT_PROPERTY=0;
        int cntTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY=0;
        int cntComplexTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY=0;
        int cntTboxSYMMETRIC_OBJECT_PROPERTY=0;
        int cntComplexTboxSYMMETRIC_OBJECT_PROPERTY=0;
        int cntTboxASYMMETRIC_OBJECT_PROPERTY=0;
        int cntComplexTboxASYMMETRIC_OBJECT_PROPERTY=0;
        int cntTboxTRANSITIVE_OBJECT_PROPERTY=0;
        int cntComplexTboxTRANSITIVE_OBJECT_PROPERTY=0;
        int cntTboxREFLEXIVE_OBJECT_PROPERTY=0;
        int cntComplexTboxREFLEXIVE_OBJECT_PROPERTY=0;
        int cntTboxIRREFLEXIVE_OBJECT_PROPERTY=0;
        int cntComplexTboxIRREFLEXIVE_OBJECT_PROPERTY=0;
        int cntTboxDATA_PROPERTY_DOMAIN=0;
        int cntComplexTboxDATA_PROPERTY_DOMAIN=0;
        int cntTboxDATA_PROPERTY_RANGE=0;
        int cntComplexTboxDATA_PROPERTY_RANGE=0;
        int cntTboxDISJOINT_DATA_PROPERTIES=0;
        int cntComplexTboxDISJOINT_DATA_PROPERTIES=0;
        int cntTboxSUB_DATA_PROPERTY=0;
        int cntComplexTboxSUB_DATA_PROPERTY=0;
        int cntTboxEQUIVALENT_DATA_PROPERTIES=0;
        int cntComplexTboxEQUIVALENT_DATA_PROPERTIES=0;
        int cntTboxFUNCTIONAL_DATA_PROPERTY=0;
        int cntComplexTboxFUNCTIONAL_DATA_PROPERTY=0;
        int cntTboxDATATYPE_DEFINITION=0;
        int cntComplexTboxDATATYPE_DEFINITION=0;
        int cntTboxDISJOINT_UNION=0;
        int cntComplexTboxDISJOINT_UNION=0;
        int cntTboxDECLARATION=0;
        int cntComplexTboxDECLARATION=0;
        int cntTboxSWRL_RULE=0;
        int cntComplexTboxSWRL_RULE=0;
        int cntTboxANNOTATION_ASSERTION=0;
        int cntComplexTboxANNOTATION_ASSERTION=0;
        int cntTboxSUB_ANNOTATION_PROPERTY_OF=0;
        int cntComplexTboxSUB_ANNOTATION_PROPERTY_OF=0;
        int cntTboxANNOTATION_PROPERTY_DOMAIN=0;
        int cntComplexTboxANNOTATION_PROPERTY_DOMAIN=0;
        int cntTboxANNOTATION_PROPERTY_RANGE=0;
        int cntComplexTboxANNOTATION_PROPERTY_RANGE=0;
        int cntTboxHAS_KEY=0;
        int cntComplexTboxHAS_KEY=0;
        int cntTboxOther=0;


        int cntAboxSUBCLASS_OF=0;
        int cntComplexAboxSUBCLASS_OF=0;
        int cntAboxEQUIVALENT_CLASSES=0;
        int cntComplexAboxEQUIVALENT_CLASSES=0;
        int cntAboxDISJOINT_CLASSES=0;
        int cntComplexAboxDISJOINT_CLASSES=0;
        int cntAboxCLASS_ASSERTION=0;
        int cntComplexAboxCLASS_ASSERTION=0;
        int cntAboxSAME_INDIVIDUAL=0;
        int cntComplexAboxSAME_INDIVIDUAL=0;
        int cntAboxDIFFERENT_INDIVIDUALS=0;
        int cntComplexAboxDIFFERENT_INDIVIDUALS=0;
        int cntAboxOBJECT_PROPERTY_ASSERTION=0;
        int cntComplexAboxOBJECT_PROPERTY_ASSERTION=0;
        int cntAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION=0;
        int cntComplexAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION=0;
        int cntAboxDATA_PROPERTY_ASSERTION=0;
        int cntComplexAboxDATA_PROPERTY_ASSERTION=0;
        int cntAboxNEGATIVE_DATA_PROPERTY_ASSERTION=0;
        int cntComplexAboxNEGATIVE_DATA_PROPERTY_ASSERTION=0;
        int cntAboxOBJECT_PROPERTY_DOMAIN=0;
        int cntComplexAboxOBJECT_PROPERTY_DOMAIN=0;
        int cntAboxOBJECT_PROPERTY_RANGE=0;
        int cntComplexAboxOBJECT_PROPERTY_RANGE=0;
        int cntAboxDISJOINT_OBJECT_PROPERTIES=0;
        int cntComplexAboxDISJOINT_OBJECT_PROPERTIES=0;
        int cntAboxSUB_OBJECT_PROPERTY=0;
        int cntComplexAboxSUB_OBJECT_PROPERTY=0;
        int cntAboxEQUIVALENT_OBJECT_PROPERTIES=0;
        int cntComplexAboxEQUIVALENT_OBJECT_PROPERTIES=0;
        int cntAboxINVERSE_OBJECT_PROPERTIES=0;
        int cntComplexAboxINVERSE_OBJECT_PROPERTIES=0;
        int cntAboxSUB_PROPERTY_CHAIN_OF=0;
        int cntComplexAboxSUB_PROPERTY_CHAIN_OF=0;
        int cntAboxFUNCTIONAL_OBJECT_PROPERTY=0;
        int cntComplexAboxFUNCTIONAL_OBJECT_PROPERTY=0;
        int cntAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY=0;
        int cntComplexAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY=0;
        int cntAboxSYMMETRIC_OBJECT_PROPERTY=0;
        int cntComplexAboxSYMMETRIC_OBJECT_PROPERTY=0;
        int cntAboxASYMMETRIC_OBJECT_PROPERTY=0;
        int cntComplexAboxASYMMETRIC_OBJECT_PROPERTY=0;
        int cntAboxTRANSITIVE_OBJECT_PROPERTY=0;
        int cntComplexAboxTRANSITIVE_OBJECT_PROPERTY=0;
        int cntAboxREFLEXIVE_OBJECT_PROPERTY=0;
        int cntComplexAboxREFLEXIVE_OBJECT_PROPERTY=0;
        int cntAboxIRREFLEXIVE_OBJECT_PROPERTY=0;
        int cntComplexAboxIRREFLEXIVE_OBJECT_PROPERTY=0;
        int cntAboxDATA_PROPERTY_DOMAIN=0;
        int cntComplexAboxDATA_PROPERTY_DOMAIN=0;
        int cntAboxDATA_PROPERTY_RANGE=0;
        int cntComplexAboxDATA_PROPERTY_RANGE=0;
        int cntAboxDISJOINT_DATA_PROPERTIES=0;
        int cntComplexAboxDISJOINT_DATA_PROPERTIES=0;
        int cntAboxSUB_DATA_PROPERTY=0;
        int cntComplexAboxSUB_DATA_PROPERTY=0;
        int cntAboxEQUIVALENT_DATA_PROPERTIES=0;
        int cntComplexAboxEQUIVALENT_DATA_PROPERTIES=0;
        int cntAboxFUNCTIONAL_DATA_PROPERTY=0;
        int cntComplexAboxFUNCTIONAL_DATA_PROPERTY=0;
        int cntAboxDATATYPE_DEFINITION=0;
        int cntComplexAboxDATATYPE_DEFINITION=0;
        int cntAboxDISJOINT_UNION=0;
        int cntComplexAboxDISJOINT_UNION=0;
        int cntAboxDECLARATION=0;
        int cntComplexAboxDECLARATION=0;
        int cntAboxSWRL_RULE=0;
        int cntComplexAboxSWRL_RULE=0;
        int cntAboxANNOTATION_ASSERTION=0;
        int cntComplexAboxANNOTATION_ASSERTION=0;
        int cntAboxSUB_ANNOTATION_PROPERTY_OF=0;
        int cntComplexAboxSUB_ANNOTATION_PROPERTY_OF=0;
        int cntAboxANNOTATION_PROPERTY_DOMAIN=0;
        int cntComplexAboxANNOTATION_PROPERTY_DOMAIN=0;
        int cntAboxANNOTATION_PROPERTY_RANGE=0;
        int cntComplexAboxANNOTATION_PROPERTY_RANGE=0;
        int cntAboxHAS_KEY=0;
        int cntComplexAboxHAS_KEY=0;
        int cntAboxOther=0;


        int cntRboxSUBCLASS_OF=0;
        int cntComplexRboxSUBCLASS_OF=0;
        int cntRboxEQUIVALENT_CLASSES=0;
        int cntComplexRboxEQUIVALENT_CLASSES=0;
        int cntRboxDISJOINT_CLASSES=0;
        int cntComplexRboxDISJOINT_CLASSES=0;
        int cntRboxCLASS_ASSERTION=0;
        int cntComplexRboxCLASS_ASSERTION=0;
        int cntRboxSAME_INDIVIDUAL=0;
        int cntComplexRboxSAME_INDIVIDUAL=0;
        int cntRboxDIFFERENT_INDIVIDUALS=0;
        int cntComplexRboxDIFFERENT_INDIVIDUALS=0;
        int cntRboxOBJECT_PROPERTY_ASSERTION=0;
        int cntComplexRboxOBJECT_PROPERTY_ASSERTION=0;
        int cntRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION=0;
        int cntComplexRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION=0;
        int cntRboxDATA_PROPERTY_ASSERTION=0;
        int cntComplexRboxDATA_PROPERTY_ASSERTION=0;
        int cntRboxNEGATIVE_DATA_PROPERTY_ASSERTION=0;
        int cntComplexRboxNEGATIVE_DATA_PROPERTY_ASSERTION=0;
        int cntRboxOBJECT_PROPERTY_DOMAIN=0;
        int cntComplexRboxOBJECT_PROPERTY_DOMAIN=0;
        int cntRboxOBJECT_PROPERTY_RANGE=0;
        int cntComplexRboxOBJECT_PROPERTY_RANGE=0;
        int cntRboxDISJOINT_OBJECT_PROPERTIES=0;
        int cntComplexRboxDISJOINT_OBJECT_PROPERTIES=0;
        int cntRboxSUB_OBJECT_PROPERTY=0;
        int cntComplexRboxSUB_OBJECT_PROPERTY=0;
        int cntRboxEQUIVALENT_OBJECT_PROPERTIES=0;
        int cntComplexRboxEQUIVALENT_OBJECT_PROPERTIES=0;
        int cntRboxINVERSE_OBJECT_PROPERTIES=0;
        int cntComplexRboxINVERSE_OBJECT_PROPERTIES=0;
        int cntRboxSUB_PROPERTY_CHAIN_OF=0;
        int cntComplexRboxSUB_PROPERTY_CHAIN_OF=0;
        int cntRboxFUNCTIONAL_OBJECT_PROPERTY=0;
        int cntComplexRboxFUNCTIONAL_OBJECT_PROPERTY=0;
        int cntRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY=0;
        int cntComplexRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY=0;
        int cntRboxSYMMETRIC_OBJECT_PROPERTY=0;
        int cntComplexRboxSYMMETRIC_OBJECT_PROPERTY=0;
        int cntRboxASYMMETRIC_OBJECT_PROPERTY=0;
        int cntComplexRboxASYMMETRIC_OBJECT_PROPERTY=0;
        int cntRboxTRANSITIVE_OBJECT_PROPERTY=0;
        int cntComplexRboxTRANSITIVE_OBJECT_PROPERTY=0;
        int cntRboxREFLEXIVE_OBJECT_PROPERTY=0;
        int cntComplexRboxREFLEXIVE_OBJECT_PROPERTY=0;
        int cntRboxIRREFLEXIVE_OBJECT_PROPERTY=0;
        int cntComplexRboxIRREFLEXIVE_OBJECT_PROPERTY=0;
        int cntRboxDATA_PROPERTY_DOMAIN=0;
        int cntComplexRboxDATA_PROPERTY_DOMAIN=0;
        int cntRboxDATA_PROPERTY_RANGE=0;
        int cntComplexRboxDATA_PROPERTY_RANGE=0;
        int cntRboxDISJOINT_DATA_PROPERTIES=0;
        int cntComplexRboxDISJOINT_DATA_PROPERTIES=0;
        int cntRboxSUB_DATA_PROPERTY=0;
        int cntComplexRboxSUB_DATA_PROPERTY=0;
        int cntRboxEQUIVALENT_DATA_PROPERTIES=0;
        int cntComplexRboxEQUIVALENT_DATA_PROPERTIES=0;
        int cntRboxFUNCTIONAL_DATA_PROPERTY=0;
        int cntComplexRboxFUNCTIONAL_DATA_PROPERTY=0;
        int cntRboxDATATYPE_DEFINITION=0;
        int cntComplexRboxDATATYPE_DEFINITION=0;
        int cntRboxDISJOINT_UNION=0;
        int cntComplexRboxDISJOINT_UNION=0;
        int cntRboxDECLARATION=0;
        int cntComplexRboxDECLARATION=0;
        int cntRboxSWRL_RULE=0;
        int cntComplexRboxSWRL_RULE=0;
        int cntRboxANNOTATION_ASSERTION=0;
        int cntComplexRboxANNOTATION_ASSERTION=0;
        int cntRboxSUB_ANNOTATION_PROPERTY_OF=0;
        int cntComplexRboxSUB_ANNOTATION_PROPERTY_OF=0;
        int cntRboxANNOTATION_PROPERTY_DOMAIN=0;
        int cntComplexRboxANNOTATION_PROPERTY_DOMAIN=0;
        int cntRboxANNOTATION_PROPERTY_RANGE=0;
        int cntComplexRboxANNOTATION_PROPERTY_RANGE=0;
        int cntRboxHAS_KEY=0;
        int cntComplexRboxHAS_KEY=0;
        int cntRboxOther=0;

        for(OWLAxiom axiom:onto.getTBoxAxioms(true)){
            if(axiom.getAxiomType()==AxiomType.SUBCLASS_OF){
                cntTboxSUBCLASS_OF++;
                OWLSubClassOfAxiom var = (OWLSubClassOfAxiom)axiom;
                if(var.getSubClass().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                 ||var.getSuperClass().getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                    cntComplexTboxSUBCLASS_OF++;
                    if(showDetails) System.out.println(axiom);
                }
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_CLASSES){
                cntTboxEQUIVALENT_CLASSES++;
                OWLEquivalentClassesAxiom var = (OWLEquivalentClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexTboxEQUIVALENT_CLASSES++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }

            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_CLASSES){
                cntTboxDISJOINT_CLASSES++;
                OWLDisjointClassesAxiom var = (OWLDisjointClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexTboxDISJOINT_CLASSES++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.CLASS_ASSERTION){
                cntTboxCLASS_ASSERTION++;
                OWLClassAssertionAxiom var = (OWLClassAssertionAxiom)axiom;
                if(var.getClassExpression().getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                    cntComplexTboxCLASS_ASSERTION++;
                    if(showDetails) System.out.println(axiom);
                }
            }else if(axiom.getAxiomType()==AxiomType.SAME_INDIVIDUAL){
                cntTboxSAME_INDIVIDUAL++;
            }else if(axiom.getAxiomType()==AxiomType.DIFFERENT_INDIVIDUALS){
                cntTboxDIFFERENT_INDIVIDUALS++;
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_ASSERTION){
                cntTboxOBJECT_PROPERTY_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION){
                cntTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION++;
                if(showDetails) System.out.println(axiom);
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_ASSERTION){
                cntTboxDATA_PROPERTY_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION){
                cntTboxNEGATIVE_DATA_PROPERTY_ASSERTION++;
                if(showDetails) System.out.println(axiom);
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_DOMAIN){
                cntTboxOBJECT_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_RANGE){
                cntTboxOBJECT_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_OBJECT_PROPERTIES){
                cntTboxDISJOINT_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_OBJECT_PROPERTY){
                cntTboxSUB_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_OBJECT_PROPERTIES){
                cntTboxEQUIVALENT_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.INVERSE_OBJECT_PROPERTIES){
                cntTboxINVERSE_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_PROPERTY_CHAIN_OF){
                cntTboxSUB_PROPERTY_CHAIN_OF++;
            }else if(axiom.getAxiomType()==AxiomType.FUNCTIONAL_OBJECT_PROPERTY){
                cntTboxFUNCTIONAL_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY){
                cntTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.SYMMETRIC_OBJECT_PROPERTY){
                cntTboxSYMMETRIC_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.ASYMMETRIC_OBJECT_PROPERTY){
                cntTboxASYMMETRIC_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.TRANSITIVE_OBJECT_PROPERTY){
                cntTboxTRANSITIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.REFLEXIVE_OBJECT_PROPERTY){
                cntTboxREFLEXIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.IRREFLEXIVE_OBJECT_PROPERTY){
                cntTboxIRREFLEXIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_DOMAIN){
                cntTboxDATA_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_RANGE){
                cntTboxDATA_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_DATA_PROPERTIES){
                cntTboxDISJOINT_DATA_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_DATA_PROPERTY){
                cntTboxSUB_DATA_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_DATA_PROPERTIES){
                cntTboxEQUIVALENT_DATA_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.FUNCTIONAL_DATA_PROPERTY){
                cntTboxFUNCTIONAL_DATA_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.DATATYPE_DEFINITION){
                cntTboxDATATYPE_DEFINITION++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_UNION){
                cntTboxDISJOINT_UNION++;
                OWLDisjointUnionAxiom var = (OWLDisjointUnionAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexTboxDISJOINT_UNION++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.DECLARATION){
                cntTboxDECLARATION++;
            }else if(axiom.getAxiomType()==AxiomType.SWRL_RULE){
                cntTboxSWRL_RULE++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_ASSERTION){
                cntTboxANNOTATION_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_ANNOTATION_PROPERTY_OF){
                cntTboxSUB_ANNOTATION_PROPERTY_OF++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_PROPERTY_DOMAIN){
                cntTboxANNOTATION_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_PROPERTY_RANGE){
                cntTboxANNOTATION_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.HAS_KEY){
                cntTboxHAS_KEY++;
            }else{
                cntTboxOther++;
            }
        }

        for(OWLAxiom axiom:onto.getABoxAxioms(true)){
            if(axiom.getAxiomType()==AxiomType.SUBCLASS_OF){
                cntAboxSUBCLASS_OF++;
                OWLSubClassOfAxiom var = (OWLSubClassOfAxiom)axiom;
                if(var.getSubClass().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        ||var.getSuperClass().getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                    cntComplexAboxSUBCLASS_OF++;
                    if(showDetails) System.out.println(axiom);
                }
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_CLASSES){
                cntAboxEQUIVALENT_CLASSES++;
                OWLEquivalentClassesAxiom var = (OWLEquivalentClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexAboxEQUIVALENT_CLASSES++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }

            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_CLASSES){
                cntAboxDISJOINT_CLASSES++;
                OWLDisjointClassesAxiom var = (OWLDisjointClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexAboxDISJOINT_CLASSES++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.CLASS_ASSERTION){
                cntAboxCLASS_ASSERTION++;
                OWLClassAssertionAxiom var = (OWLClassAssertionAxiom)axiom;
                if(var.getClassExpression().getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                    cntComplexAboxCLASS_ASSERTION++;
                    if(showDetails) System.out.println(axiom);
                }
            }else if(axiom.getAxiomType()==AxiomType.SAME_INDIVIDUAL){
                cntAboxSAME_INDIVIDUAL++;
            }else if(axiom.getAxiomType()==AxiomType.DIFFERENT_INDIVIDUALS){
                cntAboxDIFFERENT_INDIVIDUALS++;
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_ASSERTION){
                cntAboxOBJECT_PROPERTY_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION){
                cntAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION++;
                if(showDetails) System.out.println(axiom);
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_ASSERTION){
                cntAboxDATA_PROPERTY_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION){
                cntAboxNEGATIVE_DATA_PROPERTY_ASSERTION++;
                if(showDetails) System.out.println(axiom);
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_DOMAIN){
                cntAboxOBJECT_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_RANGE){
                cntAboxOBJECT_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_OBJECT_PROPERTIES){
                cntAboxDISJOINT_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_OBJECT_PROPERTY){
                cntAboxSUB_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_OBJECT_PROPERTIES){
                cntAboxEQUIVALENT_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.INVERSE_OBJECT_PROPERTIES){
                cntAboxINVERSE_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_PROPERTY_CHAIN_OF){
                cntAboxSUB_PROPERTY_CHAIN_OF++;
            }else if(axiom.getAxiomType()==AxiomType.FUNCTIONAL_OBJECT_PROPERTY){
                cntAboxFUNCTIONAL_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY){
                cntAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.SYMMETRIC_OBJECT_PROPERTY){
                cntAboxSYMMETRIC_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.ASYMMETRIC_OBJECT_PROPERTY){
                cntAboxASYMMETRIC_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.TRANSITIVE_OBJECT_PROPERTY){
                cntAboxTRANSITIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.REFLEXIVE_OBJECT_PROPERTY){
                cntAboxREFLEXIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.IRREFLEXIVE_OBJECT_PROPERTY){
                cntAboxIRREFLEXIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_DOMAIN){
                cntAboxDATA_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_RANGE){
                cntAboxDATA_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_DATA_PROPERTIES){
                cntAboxDISJOINT_DATA_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_DATA_PROPERTY){
                cntAboxSUB_DATA_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_DATA_PROPERTIES){
                cntAboxEQUIVALENT_DATA_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.FUNCTIONAL_DATA_PROPERTY){
                cntAboxFUNCTIONAL_DATA_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.DATATYPE_DEFINITION){
                cntAboxDATATYPE_DEFINITION++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_UNION){
                cntAboxDISJOINT_UNION++;
                OWLDisjointUnionAxiom var = (OWLDisjointUnionAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexAboxDISJOINT_UNION++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.DECLARATION){
                cntAboxDECLARATION++;
            }else if(axiom.getAxiomType()==AxiomType.SWRL_RULE){
                cntAboxSWRL_RULE++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_ASSERTION){
                cntAboxANNOTATION_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_ANNOTATION_PROPERTY_OF){
                cntAboxSUB_ANNOTATION_PROPERTY_OF++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_PROPERTY_DOMAIN){
                cntAboxANNOTATION_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_PROPERTY_RANGE){
                cntAboxANNOTATION_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.HAS_KEY){
                cntAboxHAS_KEY++;
            }else{
                cntAboxOther++;
            }
        }

        for(OWLAxiom axiom:onto.getRBoxAxioms(true)){
            if(axiom.getAxiomType()==AxiomType.SUBCLASS_OF){
                cntRboxSUBCLASS_OF++;
                OWLSubClassOfAxiom var = (OWLSubClassOfAxiom)axiom;
                if(var.getSubClass().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        ||var.getSuperClass().getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                    cntComplexRboxSUBCLASS_OF++;
                    if(showDetails) System.out.println(axiom);
                }
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_CLASSES){
                cntRboxEQUIVALENT_CLASSES++;
                OWLEquivalentClassesAxiom var = (OWLEquivalentClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexRboxEQUIVALENT_CLASSES++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }

            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_CLASSES){
                cntRboxDISJOINT_CLASSES++;
                OWLDisjointClassesAxiom var = (OWLDisjointClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexRboxDISJOINT_CLASSES++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.CLASS_ASSERTION){
                cntRboxCLASS_ASSERTION++;
                OWLClassAssertionAxiom var = (OWLClassAssertionAxiom)axiom;
                if(var.getClassExpression().getClassExpressionType()!=ClassExpressionType.OWL_CLASS){
                    cntComplexRboxCLASS_ASSERTION++;
                    if(showDetails) System.out.println(axiom);
                }
            }else if(axiom.getAxiomType()==AxiomType.SAME_INDIVIDUAL){
                cntRboxSAME_INDIVIDUAL++;
            }else if(axiom.getAxiomType()==AxiomType.DIFFERENT_INDIVIDUALS){
                cntRboxDIFFERENT_INDIVIDUALS++;
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_ASSERTION){
                cntRboxOBJECT_PROPERTY_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.NEGATIVE_OBJECT_PROPERTY_ASSERTION){
                cntRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION++;
                if(showDetails) System.out.println(axiom);
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_ASSERTION){
                cntRboxDATA_PROPERTY_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.NEGATIVE_DATA_PROPERTY_ASSERTION){
                cntRboxNEGATIVE_DATA_PROPERTY_ASSERTION++;
                if(showDetails) System.out.println(axiom);
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_DOMAIN){
                cntRboxOBJECT_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.OBJECT_PROPERTY_RANGE){
                cntRboxOBJECT_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_OBJECT_PROPERTIES){
                cntRboxDISJOINT_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_OBJECT_PROPERTY){
                cntRboxSUB_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_OBJECT_PROPERTIES){
                cntRboxEQUIVALENT_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.INVERSE_OBJECT_PROPERTIES){
                cntRboxINVERSE_OBJECT_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_PROPERTY_CHAIN_OF){
                cntRboxSUB_PROPERTY_CHAIN_OF++;
            }else if(axiom.getAxiomType()==AxiomType.FUNCTIONAL_OBJECT_PROPERTY){
                cntRboxFUNCTIONAL_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.INVERSE_FUNCTIONAL_OBJECT_PROPERTY){
                cntRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.SYMMETRIC_OBJECT_PROPERTY){
                cntRboxSYMMETRIC_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.ASYMMETRIC_OBJECT_PROPERTY){
                cntRboxASYMMETRIC_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.TRANSITIVE_OBJECT_PROPERTY){
                cntRboxTRANSITIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.REFLEXIVE_OBJECT_PROPERTY){
                cntRboxREFLEXIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.IRREFLEXIVE_OBJECT_PROPERTY){
                cntRboxIRREFLEXIVE_OBJECT_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_DOMAIN){
                cntRboxDATA_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.DATA_PROPERTY_RANGE){
                cntRboxDATA_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_DATA_PROPERTIES){
                cntRboxDISJOINT_DATA_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_DATA_PROPERTY){
                cntRboxSUB_DATA_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_DATA_PROPERTIES){
                cntRboxEQUIVALENT_DATA_PROPERTIES++;
            }else if(axiom.getAxiomType()==AxiomType.FUNCTIONAL_DATA_PROPERTY){
                cntRboxFUNCTIONAL_DATA_PROPERTY++;
            }else if(axiom.getAxiomType()==AxiomType.DATATYPE_DEFINITION){
                cntRboxDATATYPE_DEFINITION++;
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_UNION){
                cntRboxDISJOINT_UNION++;
                OWLDisjointUnionAxiom var = (OWLDisjointUnionAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS) {
                        cntComplexRboxDISJOINT_UNION++;
                        if(showDetails) System.out.println(axiom);
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.DECLARATION){
                cntRboxDECLARATION++;
            }else if(axiom.getAxiomType()==AxiomType.SWRL_RULE){
                cntRboxSWRL_RULE++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_ASSERTION){
                cntRboxANNOTATION_ASSERTION++;
            }else if(axiom.getAxiomType()==AxiomType.SUB_ANNOTATION_PROPERTY_OF){
                cntRboxSUB_ANNOTATION_PROPERTY_OF++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_PROPERTY_DOMAIN){
                cntRboxANNOTATION_PROPERTY_DOMAIN++;
            }else if(axiom.getAxiomType()==AxiomType.ANNOTATION_PROPERTY_RANGE){
                cntRboxANNOTATION_PROPERTY_RANGE++;
            }else if(axiom.getAxiomType()==AxiomType.HAS_KEY){
                cntRboxHAS_KEY++;
            }else{
                cntRboxOther++;
            }

        }
        if(cntTboxSUBCLASS_OF>0||true) {System.out.println("TboxSUBCLASS_OF="+cntTboxSUBCLASS_OF);outputLines.add("TboxSUBCLASS_OF="+cntTboxSUBCLASS_OF);}
        if(cntComplexTboxSUBCLASS_OF>0||true) {System.out.println("ComplexTboxSUBCLASS_OF="+cntComplexTboxSUBCLASS_OF);outputLines.add("ComplexTboxSUBCLASS_OF="+cntComplexTboxSUBCLASS_OF);}
        if(cntTboxEQUIVALENT_CLASSES>0||true) {System.out.println("TboxEQUIVALENT_CLASSES="+cntTboxEQUIVALENT_CLASSES);outputLines.add("TboxEQUIVALENT_CLASSES="+cntTboxEQUIVALENT_CLASSES);}
        if(cntComplexTboxEQUIVALENT_CLASSES>0||true) {System.out.println("ComplexTboxEQUIVALENT_CLASSES="+cntComplexTboxEQUIVALENT_CLASSES);outputLines.add("ComplexTboxEQUIVALENT_CLASSES="+cntComplexTboxEQUIVALENT_CLASSES);}
        if(cntTboxDISJOINT_CLASSES>0||true) {System.out.println("TboxDISJOINT_CLASSES="+cntTboxDISJOINT_CLASSES);outputLines.add("TboxDISJOINT_CLASSES="+cntTboxDISJOINT_CLASSES);}
        if(cntComplexTboxDISJOINT_CLASSES>0||true) {System.out.println("ComplexTboxDISJOINT_CLASSES="+cntComplexTboxDISJOINT_CLASSES);outputLines.add("ComplexTboxDISJOINT_CLASSES="+cntComplexTboxDISJOINT_CLASSES);}
        if(cntTboxCLASS_ASSERTION>0||true) {System.out.println("TboxCLASS_ASSERTION="+cntTboxCLASS_ASSERTION);outputLines.add("TboxCLASS_ASSERTION="+cntTboxCLASS_ASSERTION);}
        if(cntComplexTboxCLASS_ASSERTION>0||true) {System.out.println("ComplexTboxCLASS_ASSERTION="+cntComplexTboxCLASS_ASSERTION);outputLines.add("ComplexTboxCLASS_ASSERTION="+cntComplexTboxCLASS_ASSERTION);}
        if(cntTboxSAME_INDIVIDUAL>0||true) {System.out.println("TboxSAME_INDIVIDUAL="+cntTboxSAME_INDIVIDUAL);outputLines.add("TboxSAME_INDIVIDUAL="+cntTboxSAME_INDIVIDUAL);}
        if(cntComplexTboxSAME_INDIVIDUAL>0||true) {System.out.println("ComplexTboxSAME_INDIVIDUAL="+cntComplexTboxSAME_INDIVIDUAL);outputLines.add("ComplexTboxSAME_INDIVIDUAL="+cntComplexTboxSAME_INDIVIDUAL);}
        if(cntTboxDIFFERENT_INDIVIDUALS>0||true) {System.out.println("TboxDIFFERENT_INDIVIDUALS="+cntTboxDIFFERENT_INDIVIDUALS);outputLines.add("TboxDIFFERENT_INDIVIDUALS="+cntTboxDIFFERENT_INDIVIDUALS);}
        if(cntComplexTboxDIFFERENT_INDIVIDUALS>0||true) {System.out.println("ComplexTboxDIFFERENT_INDIVIDUALS="+cntComplexTboxDIFFERENT_INDIVIDUALS);outputLines.add("ComplexTboxDIFFERENT_INDIVIDUALS="+cntComplexTboxDIFFERENT_INDIVIDUALS);}
        if(cntTboxOBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("TboxOBJECT_PROPERTY_ASSERTION="+cntTboxOBJECT_PROPERTY_ASSERTION);outputLines.add("TboxOBJECT_PROPERTY_ASSERTION="+cntTboxOBJECT_PROPERTY_ASSERTION);}
        if(cntComplexTboxOBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexTboxOBJECT_PROPERTY_ASSERTION="+cntComplexTboxOBJECT_PROPERTY_ASSERTION);outputLines.add("ComplexTboxOBJECT_PROPERTY_ASSERTION="+cntComplexTboxOBJECT_PROPERTY_ASSERTION);}
        if(cntTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("TboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);outputLines.add("TboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);}
        if(cntComplexTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntComplexTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);outputLines.add("ComplexTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntComplexTboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);}
        if(cntTboxDATA_PROPERTY_ASSERTION>0||true) {System.out.println("TboxDATA_PROPERTY_ASSERTION="+cntTboxDATA_PROPERTY_ASSERTION);outputLines.add("TboxDATA_PROPERTY_ASSERTION="+cntTboxDATA_PROPERTY_ASSERTION);}
        if(cntComplexTboxDATA_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexTboxDATA_PROPERTY_ASSERTION="+cntComplexTboxDATA_PROPERTY_ASSERTION);outputLines.add("ComplexTboxDATA_PROPERTY_ASSERTION="+cntComplexTboxDATA_PROPERTY_ASSERTION);}
        if(cntTboxNEGATIVE_DATA_PROPERTY_ASSERTION>0||true) {System.out.println("TboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntTboxNEGATIVE_DATA_PROPERTY_ASSERTION);outputLines.add("TboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntTboxNEGATIVE_DATA_PROPERTY_ASSERTION);}
        if(cntComplexTboxNEGATIVE_DATA_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexTboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntComplexTboxNEGATIVE_DATA_PROPERTY_ASSERTION);outputLines.add("ComplexTboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntComplexTboxNEGATIVE_DATA_PROPERTY_ASSERTION);}
        if(cntTboxOBJECT_PROPERTY_DOMAIN>0||true) {System.out.println("TboxOBJECT_PROPERTY_DOMAIN="+cntTboxOBJECT_PROPERTY_DOMAIN);outputLines.add("TboxOBJECT_PROPERTY_DOMAIN="+cntTboxOBJECT_PROPERTY_DOMAIN);}
        if(cntComplexTboxOBJECT_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexTboxOBJECT_PROPERTY_DOMAIN="+cntComplexTboxOBJECT_PROPERTY_DOMAIN);outputLines.add("ComplexTboxOBJECT_PROPERTY_DOMAIN="+cntComplexTboxOBJECT_PROPERTY_DOMAIN);}
        if(cntTboxOBJECT_PROPERTY_RANGE>0||true) {System.out.println("TboxOBJECT_PROPERTY_RANGE="+cntTboxOBJECT_PROPERTY_RANGE);outputLines.add("TboxOBJECT_PROPERTY_RANGE="+cntTboxOBJECT_PROPERTY_RANGE);}
        if(cntComplexTboxOBJECT_PROPERTY_RANGE>0||true) {System.out.println("ComplexTboxOBJECT_PROPERTY_RANGE="+cntComplexTboxOBJECT_PROPERTY_RANGE);outputLines.add("ComplexTboxOBJECT_PROPERTY_RANGE="+cntComplexTboxOBJECT_PROPERTY_RANGE);}
        if(cntTboxDISJOINT_OBJECT_PROPERTIES>0||true) {System.out.println("TboxDISJOINT_OBJECT_PROPERTIES="+cntTboxDISJOINT_OBJECT_PROPERTIES);outputLines.add("TboxDISJOINT_OBJECT_PROPERTIES="+cntTboxDISJOINT_OBJECT_PROPERTIES);}
        if(cntComplexTboxDISJOINT_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexTboxDISJOINT_OBJECT_PROPERTIES="+cntComplexTboxDISJOINT_OBJECT_PROPERTIES);outputLines.add("ComplexTboxDISJOINT_OBJECT_PROPERTIES="+cntComplexTboxDISJOINT_OBJECT_PROPERTIES);}
        if(cntTboxSUB_OBJECT_PROPERTY>0||true) {System.out.println("TboxSUB_OBJECT_PROPERTY="+cntTboxSUB_OBJECT_PROPERTY);outputLines.add("TboxSUB_OBJECT_PROPERTY="+cntTboxSUB_OBJECT_PROPERTY);}
        if(cntComplexTboxSUB_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxSUB_OBJECT_PROPERTY="+cntComplexTboxSUB_OBJECT_PROPERTY);outputLines.add("ComplexTboxSUB_OBJECT_PROPERTY="+cntComplexTboxSUB_OBJECT_PROPERTY);}
        if(cntTboxEQUIVALENT_OBJECT_PROPERTIES>0||true) {System.out.println("TboxEQUIVALENT_OBJECT_PROPERTIES="+cntTboxEQUIVALENT_OBJECT_PROPERTIES);outputLines.add("TboxEQUIVALENT_OBJECT_PROPERTIES="+cntTboxEQUIVALENT_OBJECT_PROPERTIES);}
        if(cntComplexTboxEQUIVALENT_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexTboxEQUIVALENT_OBJECT_PROPERTIES="+cntComplexTboxEQUIVALENT_OBJECT_PROPERTIES);outputLines.add("ComplexTboxEQUIVALENT_OBJECT_PROPERTIES="+cntComplexTboxEQUIVALENT_OBJECT_PROPERTIES);}
        if(cntTboxINVERSE_OBJECT_PROPERTIES>0||true) {System.out.println("TboxINVERSE_OBJECT_PROPERTIES="+cntTboxINVERSE_OBJECT_PROPERTIES);outputLines.add("TboxINVERSE_OBJECT_PROPERTIES="+cntTboxINVERSE_OBJECT_PROPERTIES);}
        if(cntComplexTboxINVERSE_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexTboxINVERSE_OBJECT_PROPERTIES="+cntComplexTboxINVERSE_OBJECT_PROPERTIES);outputLines.add("ComplexTboxINVERSE_OBJECT_PROPERTIES="+cntComplexTboxINVERSE_OBJECT_PROPERTIES);}
        if(cntTboxSUB_PROPERTY_CHAIN_OF>0||true) {System.out.println("TboxSUB_PROPERTY_CHAIN_OF="+cntTboxSUB_PROPERTY_CHAIN_OF);outputLines.add("TboxSUB_PROPERTY_CHAIN_OF="+cntTboxSUB_PROPERTY_CHAIN_OF);}
        if(cntComplexTboxSUB_PROPERTY_CHAIN_OF>0||true) {System.out.println("ComplexTboxSUB_PROPERTY_CHAIN_OF="+cntComplexTboxSUB_PROPERTY_CHAIN_OF);outputLines.add("ComplexTboxSUB_PROPERTY_CHAIN_OF="+cntComplexTboxSUB_PROPERTY_CHAIN_OF);}
        if(cntTboxFUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("TboxFUNCTIONAL_OBJECT_PROPERTY="+cntTboxFUNCTIONAL_OBJECT_PROPERTY);outputLines.add("TboxFUNCTIONAL_OBJECT_PROPERTY="+cntTboxFUNCTIONAL_OBJECT_PROPERTY);}
        if(cntComplexTboxFUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxFUNCTIONAL_OBJECT_PROPERTY="+cntComplexTboxFUNCTIONAL_OBJECT_PROPERTY);outputLines.add("ComplexTboxFUNCTIONAL_OBJECT_PROPERTY="+cntComplexTboxFUNCTIONAL_OBJECT_PROPERTY);}
        if(cntTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("TboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);outputLines.add("TboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);}
        if(cntComplexTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntComplexTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);outputLines.add("ComplexTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntComplexTboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);}
        if(cntTboxSYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("TboxSYMMETRIC_OBJECT_PROPERTY="+cntTboxSYMMETRIC_OBJECT_PROPERTY);outputLines.add("TboxSYMMETRIC_OBJECT_PROPERTY="+cntTboxSYMMETRIC_OBJECT_PROPERTY);}
        if(cntComplexTboxSYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxSYMMETRIC_OBJECT_PROPERTY="+cntComplexTboxSYMMETRIC_OBJECT_PROPERTY);outputLines.add("ComplexTboxSYMMETRIC_OBJECT_PROPERTY="+cntComplexTboxSYMMETRIC_OBJECT_PROPERTY);}
        if(cntTboxASYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("TboxASYMMETRIC_OBJECT_PROPERTY="+cntTboxASYMMETRIC_OBJECT_PROPERTY);outputLines.add("TboxASYMMETRIC_OBJECT_PROPERTY="+cntTboxASYMMETRIC_OBJECT_PROPERTY);}
        if(cntComplexTboxASYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxASYMMETRIC_OBJECT_PROPERTY="+cntComplexTboxASYMMETRIC_OBJECT_PROPERTY);outputLines.add("ComplexTboxASYMMETRIC_OBJECT_PROPERTY="+cntComplexTboxASYMMETRIC_OBJECT_PROPERTY);}
        if(cntTboxTRANSITIVE_OBJECT_PROPERTY>0||true) {System.out.println("TboxTRANSITIVE_OBJECT_PROPERTY="+cntTboxTRANSITIVE_OBJECT_PROPERTY);outputLines.add("TboxTRANSITIVE_OBJECT_PROPERTY="+cntTboxTRANSITIVE_OBJECT_PROPERTY);}
        if(cntComplexTboxTRANSITIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxTRANSITIVE_OBJECT_PROPERTY="+cntComplexTboxTRANSITIVE_OBJECT_PROPERTY);outputLines.add("ComplexTboxTRANSITIVE_OBJECT_PROPERTY="+cntComplexTboxTRANSITIVE_OBJECT_PROPERTY);}
        if(cntTboxREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("TboxREFLEXIVE_OBJECT_PROPERTY="+cntTboxREFLEXIVE_OBJECT_PROPERTY);outputLines.add("TboxREFLEXIVE_OBJECT_PROPERTY="+cntTboxREFLEXIVE_OBJECT_PROPERTY);}
        if(cntComplexTboxREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxREFLEXIVE_OBJECT_PROPERTY="+cntComplexTboxREFLEXIVE_OBJECT_PROPERTY);outputLines.add("ComplexTboxREFLEXIVE_OBJECT_PROPERTY="+cntComplexTboxREFLEXIVE_OBJECT_PROPERTY);}
        if(cntTboxIRREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("TboxIRREFLEXIVE_OBJECT_PROPERTY="+cntTboxIRREFLEXIVE_OBJECT_PROPERTY);outputLines.add("TboxIRREFLEXIVE_OBJECT_PROPERTY="+cntTboxIRREFLEXIVE_OBJECT_PROPERTY);}
        if(cntComplexTboxIRREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexTboxIRREFLEXIVE_OBJECT_PROPERTY="+cntComplexTboxIRREFLEXIVE_OBJECT_PROPERTY);outputLines.add("ComplexTboxIRREFLEXIVE_OBJECT_PROPERTY="+cntComplexTboxIRREFLEXIVE_OBJECT_PROPERTY);}
        if(cntTboxDATA_PROPERTY_DOMAIN>0||true) {System.out.println("TboxDATA_PROPERTY_DOMAIN="+cntTboxDATA_PROPERTY_DOMAIN);outputLines.add("TboxDATA_PROPERTY_DOMAIN="+cntTboxDATA_PROPERTY_DOMAIN);}
        if(cntComplexTboxDATA_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexTboxDATA_PROPERTY_DOMAIN="+cntComplexTboxDATA_PROPERTY_DOMAIN);outputLines.add("ComplexTboxDATA_PROPERTY_DOMAIN="+cntComplexTboxDATA_PROPERTY_DOMAIN);}
        if(cntTboxDATA_PROPERTY_RANGE>0||true) {System.out.println("TboxDATA_PROPERTY_RANGE="+cntTboxDATA_PROPERTY_RANGE);outputLines.add("TboxDATA_PROPERTY_RANGE="+cntTboxDATA_PROPERTY_RANGE);}
        if(cntComplexTboxDATA_PROPERTY_RANGE>0||true) {System.out.println("ComplexTboxDATA_PROPERTY_RANGE="+cntComplexTboxDATA_PROPERTY_RANGE);outputLines.add("ComplexTboxDATA_PROPERTY_RANGE="+cntComplexTboxDATA_PROPERTY_RANGE);}
        if(cntTboxDISJOINT_DATA_PROPERTIES>0||true) {System.out.println("TboxDISJOINT_DATA_PROPERTIES="+cntTboxDISJOINT_DATA_PROPERTIES);outputLines.add("TboxDISJOINT_DATA_PROPERTIES="+cntTboxDISJOINT_DATA_PROPERTIES);}
        if(cntComplexTboxDISJOINT_DATA_PROPERTIES>0||true) {System.out.println("ComplexTboxDISJOINT_DATA_PROPERTIES="+cntComplexTboxDISJOINT_DATA_PROPERTIES);outputLines.add("ComplexTboxDISJOINT_DATA_PROPERTIES="+cntComplexTboxDISJOINT_DATA_PROPERTIES);}
        if(cntTboxSUB_DATA_PROPERTY>0||true) {System.out.println("TboxSUB_DATA_PROPERTY="+cntTboxSUB_DATA_PROPERTY);outputLines.add("TboxSUB_DATA_PROPERTY="+cntTboxSUB_DATA_PROPERTY);}
        if(cntComplexTboxSUB_DATA_PROPERTY>0||true) {System.out.println("ComplexTboxSUB_DATA_PROPERTY="+cntComplexTboxSUB_DATA_PROPERTY);outputLines.add("ComplexTboxSUB_DATA_PROPERTY="+cntComplexTboxSUB_DATA_PROPERTY);}
        if(cntTboxEQUIVALENT_DATA_PROPERTIES>0||true) {System.out.println("TboxEQUIVALENT_DATA_PROPERTIES="+cntTboxEQUIVALENT_DATA_PROPERTIES);outputLines.add("TboxEQUIVALENT_DATA_PROPERTIES="+cntTboxEQUIVALENT_DATA_PROPERTIES);}
        if(cntComplexTboxEQUIVALENT_DATA_PROPERTIES>0||true) {System.out.println("ComplexTboxEQUIVALENT_DATA_PROPERTIES="+cntComplexTboxEQUIVALENT_DATA_PROPERTIES);outputLines.add("ComplexTboxEQUIVALENT_DATA_PROPERTIES="+cntComplexTboxEQUIVALENT_DATA_PROPERTIES);}
        if(cntTboxFUNCTIONAL_DATA_PROPERTY>0||true) {System.out.println("TboxFUNCTIONAL_DATA_PROPERTY="+cntTboxFUNCTIONAL_DATA_PROPERTY);outputLines.add("TboxFUNCTIONAL_DATA_PROPERTY="+cntTboxFUNCTIONAL_DATA_PROPERTY);}
        if(cntComplexTboxFUNCTIONAL_DATA_PROPERTY>0||true) {System.out.println("ComplexTboxFUNCTIONAL_DATA_PROPERTY="+cntComplexTboxFUNCTIONAL_DATA_PROPERTY);outputLines.add("ComplexTboxFUNCTIONAL_DATA_PROPERTY="+cntComplexTboxFUNCTIONAL_DATA_PROPERTY);}
        if(cntTboxDATATYPE_DEFINITION>0||true) {System.out.println("TboxDATATYPE_DEFINITION="+cntTboxDATATYPE_DEFINITION);outputLines.add("TboxDATATYPE_DEFINITION="+cntTboxDATATYPE_DEFINITION);}
        if(cntComplexTboxDATATYPE_DEFINITION>0||true) {System.out.println("ComplexTboxDATATYPE_DEFINITION="+cntComplexTboxDATATYPE_DEFINITION);outputLines.add("ComplexTboxDATATYPE_DEFINITION="+cntComplexTboxDATATYPE_DEFINITION);}
        if(cntTboxDISJOINT_UNION>0||true) {System.out.println("TboxDISJOINT_UNION="+cntTboxDISJOINT_UNION);outputLines.add("TboxDISJOINT_UNION="+cntTboxDISJOINT_UNION);}
        if(cntComplexTboxDISJOINT_UNION>0||true) {System.out.println("ComplexTboxDISJOINT_UNION="+cntComplexTboxDISJOINT_UNION);outputLines.add("ComplexTboxDISJOINT_UNION="+cntComplexTboxDISJOINT_UNION);}
        if(cntTboxDECLARATION>0||true) {System.out.println("TboxDECLARATION="+cntTboxDECLARATION);outputLines.add("TboxDECLARATION="+cntTboxDECLARATION);}
        if(cntComplexTboxDECLARATION>0||true) {System.out.println("ComplexTboxDECLARATION="+cntComplexTboxDECLARATION);outputLines.add("ComplexTboxDECLARATION="+cntComplexTboxDECLARATION);}
        if(cntTboxSWRL_RULE>0||true) {System.out.println("TboxSWRL_RULE="+cntTboxSWRL_RULE);outputLines.add("TboxSWRL_RULE="+cntTboxSWRL_RULE);}
        if(cntComplexTboxSWRL_RULE>0||true) {System.out.println("ComplexTboxSWRL_RULE="+cntComplexTboxSWRL_RULE);outputLines.add("ComplexTboxSWRL_RULE="+cntComplexTboxSWRL_RULE);}
        if(cntTboxANNOTATION_ASSERTION>0||true) {System.out.println("TboxANNOTATION_ASSERTION="+cntTboxANNOTATION_ASSERTION);outputLines.add("TboxANNOTATION_ASSERTION="+cntTboxANNOTATION_ASSERTION);}
        if(cntComplexTboxANNOTATION_ASSERTION>0||true) {System.out.println("ComplexTboxANNOTATION_ASSERTION="+cntComplexTboxANNOTATION_ASSERTION);outputLines.add("ComplexTboxANNOTATION_ASSERTION="+cntComplexTboxANNOTATION_ASSERTION);}
        if(cntTboxSUB_ANNOTATION_PROPERTY_OF>0||true) {System.out.println("TboxSUB_ANNOTATION_PROPERTY_OF="+cntTboxSUB_ANNOTATION_PROPERTY_OF);outputLines.add("TboxSUB_ANNOTATION_PROPERTY_OF="+cntTboxSUB_ANNOTATION_PROPERTY_OF);}
        if(cntComplexTboxSUB_ANNOTATION_PROPERTY_OF>0||true) {System.out.println("ComplexTboxSUB_ANNOTATION_PROPERTY_OF="+cntComplexTboxSUB_ANNOTATION_PROPERTY_OF);outputLines.add("ComplexTboxSUB_ANNOTATION_PROPERTY_OF="+cntComplexTboxSUB_ANNOTATION_PROPERTY_OF);}
        if(cntTboxANNOTATION_PROPERTY_DOMAIN>0||true) {System.out.println("TboxANNOTATION_PROPERTY_DOMAIN="+cntTboxANNOTATION_PROPERTY_DOMAIN);outputLines.add("TboxANNOTATION_PROPERTY_DOMAIN="+cntTboxANNOTATION_PROPERTY_DOMAIN);}
        if(cntComplexTboxANNOTATION_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexTboxANNOTATION_PROPERTY_DOMAIN="+cntComplexTboxANNOTATION_PROPERTY_DOMAIN);outputLines.add("ComplexTboxANNOTATION_PROPERTY_DOMAIN="+cntComplexTboxANNOTATION_PROPERTY_DOMAIN);}
        if(cntTboxANNOTATION_PROPERTY_RANGE>0||true) {System.out.println("TboxANNOTATION_PROPERTY_RANGE="+cntTboxANNOTATION_PROPERTY_RANGE);outputLines.add("TboxANNOTATION_PROPERTY_RANGE="+cntTboxANNOTATION_PROPERTY_RANGE);}
        if(cntComplexTboxANNOTATION_PROPERTY_RANGE>0||true) {System.out.println("ComplexTboxANNOTATION_PROPERTY_RANGE="+cntComplexTboxANNOTATION_PROPERTY_RANGE);outputLines.add("ComplexTboxANNOTATION_PROPERTY_RANGE="+cntComplexTboxANNOTATION_PROPERTY_RANGE);}
        if(cntTboxHAS_KEY>0||true) {System.out.println("TboxHAS_KEY="+cntTboxHAS_KEY);outputLines.add("TboxHAS_KEY="+cntTboxHAS_KEY);}
        if(cntComplexTboxHAS_KEY>0||true) {System.out.println("ComplexTboxHAS_KEY="+cntComplexTboxHAS_KEY);outputLines.add("ComplexTboxHAS_KEY="+cntComplexTboxHAS_KEY);}
        if(cntTboxOther>0||true) {System.out.println("TboxOther="+cntTboxOther);outputLines.add("TboxOther="+cntTboxOther);}
        if(cntAboxSUBCLASS_OF>0||true) {System.out.println("AboxSUBCLASS_OF="+cntAboxSUBCLASS_OF);outputLines.add("AboxSUBCLASS_OF="+cntAboxSUBCLASS_OF);}
        if(cntComplexAboxSUBCLASS_OF>0||true) {System.out.println("ComplexAboxSUBCLASS_OF="+cntComplexAboxSUBCLASS_OF);outputLines.add("ComplexAboxSUBCLASS_OF="+cntComplexAboxSUBCLASS_OF);}
        if(cntAboxEQUIVALENT_CLASSES>0||true) {System.out.println("AboxEQUIVALENT_CLASSES="+cntAboxEQUIVALENT_CLASSES);outputLines.add("AboxEQUIVALENT_CLASSES="+cntAboxEQUIVALENT_CLASSES);}
        if(cntComplexAboxEQUIVALENT_CLASSES>0||true) {System.out.println("ComplexAboxEQUIVALENT_CLASSES="+cntComplexAboxEQUIVALENT_CLASSES);outputLines.add("ComplexAboxEQUIVALENT_CLASSES="+cntComplexAboxEQUIVALENT_CLASSES);}
        if(cntAboxDISJOINT_CLASSES>0||true) {System.out.println("AboxDISJOINT_CLASSES="+cntAboxDISJOINT_CLASSES);outputLines.add("AboxDISJOINT_CLASSES="+cntAboxDISJOINT_CLASSES);}
        if(cntComplexAboxDISJOINT_CLASSES>0||true) {System.out.println("ComplexAboxDISJOINT_CLASSES="+cntComplexAboxDISJOINT_CLASSES);outputLines.add("ComplexAboxDISJOINT_CLASSES="+cntComplexAboxDISJOINT_CLASSES);}
        if(cntAboxCLASS_ASSERTION>0||true) {System.out.println("AboxCLASS_ASSERTION="+cntAboxCLASS_ASSERTION);outputLines.add("AboxCLASS_ASSERTION="+cntAboxCLASS_ASSERTION);}
        if(cntComplexAboxCLASS_ASSERTION>0||true) {System.out.println("ComplexAboxCLASS_ASSERTION="+cntComplexAboxCLASS_ASSERTION);outputLines.add("ComplexAboxCLASS_ASSERTION="+cntComplexAboxCLASS_ASSERTION);}
        if(cntAboxSAME_INDIVIDUAL>0||true) {System.out.println("AboxSAME_INDIVIDUAL="+cntAboxSAME_INDIVIDUAL);outputLines.add("AboxSAME_INDIVIDUAL="+cntAboxSAME_INDIVIDUAL);}
        if(cntComplexAboxSAME_INDIVIDUAL>0||true) {System.out.println("ComplexAboxSAME_INDIVIDUAL="+cntComplexAboxSAME_INDIVIDUAL);outputLines.add("ComplexAboxSAME_INDIVIDUAL="+cntComplexAboxSAME_INDIVIDUAL);}
        if(cntAboxDIFFERENT_INDIVIDUALS>0||true) {System.out.println("AboxDIFFERENT_INDIVIDUALS="+cntAboxDIFFERENT_INDIVIDUALS);outputLines.add("AboxDIFFERENT_INDIVIDUALS="+cntAboxDIFFERENT_INDIVIDUALS);}
        if(cntComplexAboxDIFFERENT_INDIVIDUALS>0||true) {System.out.println("ComplexAboxDIFFERENT_INDIVIDUALS="+cntComplexAboxDIFFERENT_INDIVIDUALS);outputLines.add("ComplexAboxDIFFERENT_INDIVIDUALS="+cntComplexAboxDIFFERENT_INDIVIDUALS);}
        if(cntAboxOBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("AboxOBJECT_PROPERTY_ASSERTION="+cntAboxOBJECT_PROPERTY_ASSERTION);outputLines.add("AboxOBJECT_PROPERTY_ASSERTION="+cntAboxOBJECT_PROPERTY_ASSERTION);}
        if(cntComplexAboxOBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexAboxOBJECT_PROPERTY_ASSERTION="+cntComplexAboxOBJECT_PROPERTY_ASSERTION);outputLines.add("ComplexAboxOBJECT_PROPERTY_ASSERTION="+cntComplexAboxOBJECT_PROPERTY_ASSERTION);}
        if(cntAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("AboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);outputLines.add("AboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);}
        if(cntComplexAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntComplexAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);outputLines.add("ComplexAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntComplexAboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);}
        if(cntAboxDATA_PROPERTY_ASSERTION>0||true) {System.out.println("AboxDATA_PROPERTY_ASSERTION="+cntAboxDATA_PROPERTY_ASSERTION);outputLines.add("AboxDATA_PROPERTY_ASSERTION="+cntAboxDATA_PROPERTY_ASSERTION);}
        if(cntComplexAboxDATA_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexAboxDATA_PROPERTY_ASSERTION="+cntComplexAboxDATA_PROPERTY_ASSERTION);outputLines.add("ComplexAboxDATA_PROPERTY_ASSERTION="+cntComplexAboxDATA_PROPERTY_ASSERTION);}
        if(cntAboxNEGATIVE_DATA_PROPERTY_ASSERTION>0||true) {System.out.println("AboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntAboxNEGATIVE_DATA_PROPERTY_ASSERTION);outputLines.add("AboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntAboxNEGATIVE_DATA_PROPERTY_ASSERTION);}
        if(cntComplexAboxNEGATIVE_DATA_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexAboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntComplexAboxNEGATIVE_DATA_PROPERTY_ASSERTION);outputLines.add("ComplexAboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntComplexAboxNEGATIVE_DATA_PROPERTY_ASSERTION);}
        if(cntAboxOBJECT_PROPERTY_DOMAIN>0||true) {System.out.println("AboxOBJECT_PROPERTY_DOMAIN="+cntAboxOBJECT_PROPERTY_DOMAIN);outputLines.add("AboxOBJECT_PROPERTY_DOMAIN="+cntAboxOBJECT_PROPERTY_DOMAIN);}
        if(cntComplexAboxOBJECT_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexAboxOBJECT_PROPERTY_DOMAIN="+cntComplexAboxOBJECT_PROPERTY_DOMAIN);outputLines.add("ComplexAboxOBJECT_PROPERTY_DOMAIN="+cntComplexAboxOBJECT_PROPERTY_DOMAIN);}
        if(cntAboxOBJECT_PROPERTY_RANGE>0||true) {System.out.println("AboxOBJECT_PROPERTY_RANGE="+cntAboxOBJECT_PROPERTY_RANGE);outputLines.add("AboxOBJECT_PROPERTY_RANGE="+cntAboxOBJECT_PROPERTY_RANGE);}
        if(cntComplexAboxOBJECT_PROPERTY_RANGE>0||true) {System.out.println("ComplexAboxOBJECT_PROPERTY_RANGE="+cntComplexAboxOBJECT_PROPERTY_RANGE);outputLines.add("ComplexAboxOBJECT_PROPERTY_RANGE="+cntComplexAboxOBJECT_PROPERTY_RANGE);}
        if(cntAboxDISJOINT_OBJECT_PROPERTIES>0||true) {System.out.println("AboxDISJOINT_OBJECT_PROPERTIES="+cntAboxDISJOINT_OBJECT_PROPERTIES);outputLines.add("AboxDISJOINT_OBJECT_PROPERTIES="+cntAboxDISJOINT_OBJECT_PROPERTIES);}
        if(cntComplexAboxDISJOINT_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexAboxDISJOINT_OBJECT_PROPERTIES="+cntComplexAboxDISJOINT_OBJECT_PROPERTIES);outputLines.add("ComplexAboxDISJOINT_OBJECT_PROPERTIES="+cntComplexAboxDISJOINT_OBJECT_PROPERTIES);}
        if(cntAboxSUB_OBJECT_PROPERTY>0||true) {System.out.println("AboxSUB_OBJECT_PROPERTY="+cntAboxSUB_OBJECT_PROPERTY);outputLines.add("AboxSUB_OBJECT_PROPERTY="+cntAboxSUB_OBJECT_PROPERTY);}
        if(cntComplexAboxSUB_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxSUB_OBJECT_PROPERTY="+cntComplexAboxSUB_OBJECT_PROPERTY);outputLines.add("ComplexAboxSUB_OBJECT_PROPERTY="+cntComplexAboxSUB_OBJECT_PROPERTY);}
        if(cntAboxEQUIVALENT_OBJECT_PROPERTIES>0||true) {System.out.println("AboxEQUIVALENT_OBJECT_PROPERTIES="+cntAboxEQUIVALENT_OBJECT_PROPERTIES);outputLines.add("AboxEQUIVALENT_OBJECT_PROPERTIES="+cntAboxEQUIVALENT_OBJECT_PROPERTIES);}
        if(cntComplexAboxEQUIVALENT_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexAboxEQUIVALENT_OBJECT_PROPERTIES="+cntComplexAboxEQUIVALENT_OBJECT_PROPERTIES);outputLines.add("ComplexAboxEQUIVALENT_OBJECT_PROPERTIES="+cntComplexAboxEQUIVALENT_OBJECT_PROPERTIES);}
        if(cntAboxINVERSE_OBJECT_PROPERTIES>0||true) {System.out.println("AboxINVERSE_OBJECT_PROPERTIES="+cntAboxINVERSE_OBJECT_PROPERTIES);outputLines.add("AboxINVERSE_OBJECT_PROPERTIES="+cntAboxINVERSE_OBJECT_PROPERTIES);}
        if(cntComplexAboxINVERSE_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexAboxINVERSE_OBJECT_PROPERTIES="+cntComplexAboxINVERSE_OBJECT_PROPERTIES);outputLines.add("ComplexAboxINVERSE_OBJECT_PROPERTIES="+cntComplexAboxINVERSE_OBJECT_PROPERTIES);}
        if(cntAboxSUB_PROPERTY_CHAIN_OF>0||true) {System.out.println("AboxSUB_PROPERTY_CHAIN_OF="+cntAboxSUB_PROPERTY_CHAIN_OF);outputLines.add("AboxSUB_PROPERTY_CHAIN_OF="+cntAboxSUB_PROPERTY_CHAIN_OF);}
        if(cntComplexAboxSUB_PROPERTY_CHAIN_OF>0||true) {System.out.println("ComplexAboxSUB_PROPERTY_CHAIN_OF="+cntComplexAboxSUB_PROPERTY_CHAIN_OF);outputLines.add("ComplexAboxSUB_PROPERTY_CHAIN_OF="+cntComplexAboxSUB_PROPERTY_CHAIN_OF);}
        if(cntAboxFUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("AboxFUNCTIONAL_OBJECT_PROPERTY="+cntAboxFUNCTIONAL_OBJECT_PROPERTY);outputLines.add("AboxFUNCTIONAL_OBJECT_PROPERTY="+cntAboxFUNCTIONAL_OBJECT_PROPERTY);}
        if(cntComplexAboxFUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxFUNCTIONAL_OBJECT_PROPERTY="+cntComplexAboxFUNCTIONAL_OBJECT_PROPERTY);outputLines.add("ComplexAboxFUNCTIONAL_OBJECT_PROPERTY="+cntComplexAboxFUNCTIONAL_OBJECT_PROPERTY);}
        if(cntAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("AboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);outputLines.add("AboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);}
        if(cntComplexAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntComplexAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);outputLines.add("ComplexAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntComplexAboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);}
        if(cntAboxSYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("AboxSYMMETRIC_OBJECT_PROPERTY="+cntAboxSYMMETRIC_OBJECT_PROPERTY);outputLines.add("AboxSYMMETRIC_OBJECT_PROPERTY="+cntAboxSYMMETRIC_OBJECT_PROPERTY);}
        if(cntComplexAboxSYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxSYMMETRIC_OBJECT_PROPERTY="+cntComplexAboxSYMMETRIC_OBJECT_PROPERTY);outputLines.add("ComplexAboxSYMMETRIC_OBJECT_PROPERTY="+cntComplexAboxSYMMETRIC_OBJECT_PROPERTY);}
        if(cntAboxASYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("AboxASYMMETRIC_OBJECT_PROPERTY="+cntAboxASYMMETRIC_OBJECT_PROPERTY);outputLines.add("AboxASYMMETRIC_OBJECT_PROPERTY="+cntAboxASYMMETRIC_OBJECT_PROPERTY);}
        if(cntComplexAboxASYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxASYMMETRIC_OBJECT_PROPERTY="+cntComplexAboxASYMMETRIC_OBJECT_PROPERTY);outputLines.add("ComplexAboxASYMMETRIC_OBJECT_PROPERTY="+cntComplexAboxASYMMETRIC_OBJECT_PROPERTY);}
        if(cntAboxTRANSITIVE_OBJECT_PROPERTY>0||true) {System.out.println("AboxTRANSITIVE_OBJECT_PROPERTY="+cntAboxTRANSITIVE_OBJECT_PROPERTY);outputLines.add("AboxTRANSITIVE_OBJECT_PROPERTY="+cntAboxTRANSITIVE_OBJECT_PROPERTY);}
        if(cntComplexAboxTRANSITIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxTRANSITIVE_OBJECT_PROPERTY="+cntComplexAboxTRANSITIVE_OBJECT_PROPERTY);outputLines.add("ComplexAboxTRANSITIVE_OBJECT_PROPERTY="+cntComplexAboxTRANSITIVE_OBJECT_PROPERTY);}
        if(cntAboxREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("AboxREFLEXIVE_OBJECT_PROPERTY="+cntAboxREFLEXIVE_OBJECT_PROPERTY);outputLines.add("AboxREFLEXIVE_OBJECT_PROPERTY="+cntAboxREFLEXIVE_OBJECT_PROPERTY);}
        if(cntComplexAboxREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxREFLEXIVE_OBJECT_PROPERTY="+cntComplexAboxREFLEXIVE_OBJECT_PROPERTY);outputLines.add("ComplexAboxREFLEXIVE_OBJECT_PROPERTY="+cntComplexAboxREFLEXIVE_OBJECT_PROPERTY);}
        if(cntAboxIRREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("AboxIRREFLEXIVE_OBJECT_PROPERTY="+cntAboxIRREFLEXIVE_OBJECT_PROPERTY);outputLines.add("AboxIRREFLEXIVE_OBJECT_PROPERTY="+cntAboxIRREFLEXIVE_OBJECT_PROPERTY);}
        if(cntComplexAboxIRREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexAboxIRREFLEXIVE_OBJECT_PROPERTY="+cntComplexAboxIRREFLEXIVE_OBJECT_PROPERTY);outputLines.add("ComplexAboxIRREFLEXIVE_OBJECT_PROPERTY="+cntComplexAboxIRREFLEXIVE_OBJECT_PROPERTY);}
        if(cntAboxDATA_PROPERTY_DOMAIN>0||true) {System.out.println("AboxDATA_PROPERTY_DOMAIN="+cntAboxDATA_PROPERTY_DOMAIN);outputLines.add("AboxDATA_PROPERTY_DOMAIN="+cntAboxDATA_PROPERTY_DOMAIN);}
        if(cntComplexAboxDATA_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexAboxDATA_PROPERTY_DOMAIN="+cntComplexAboxDATA_PROPERTY_DOMAIN);outputLines.add("ComplexAboxDATA_PROPERTY_DOMAIN="+cntComplexAboxDATA_PROPERTY_DOMAIN);}
        if(cntAboxDATA_PROPERTY_RANGE>0||true) {System.out.println("AboxDATA_PROPERTY_RANGE="+cntAboxDATA_PROPERTY_RANGE);outputLines.add("AboxDATA_PROPERTY_RANGE="+cntAboxDATA_PROPERTY_RANGE);}
        if(cntComplexAboxDATA_PROPERTY_RANGE>0||true) {System.out.println("ComplexAboxDATA_PROPERTY_RANGE="+cntComplexAboxDATA_PROPERTY_RANGE);outputLines.add("ComplexAboxDATA_PROPERTY_RANGE="+cntComplexAboxDATA_PROPERTY_RANGE);}
        if(cntAboxDISJOINT_DATA_PROPERTIES>0||true) {System.out.println("AboxDISJOINT_DATA_PROPERTIES="+cntAboxDISJOINT_DATA_PROPERTIES);outputLines.add("AboxDISJOINT_DATA_PROPERTIES="+cntAboxDISJOINT_DATA_PROPERTIES);}
        if(cntComplexAboxDISJOINT_DATA_PROPERTIES>0||true) {System.out.println("ComplexAboxDISJOINT_DATA_PROPERTIES="+cntComplexAboxDISJOINT_DATA_PROPERTIES);outputLines.add("ComplexAboxDISJOINT_DATA_PROPERTIES="+cntComplexAboxDISJOINT_DATA_PROPERTIES);}
        if(cntAboxSUB_DATA_PROPERTY>0||true) {System.out.println("AboxSUB_DATA_PROPERTY="+cntAboxSUB_DATA_PROPERTY);outputLines.add("AboxSUB_DATA_PROPERTY="+cntAboxSUB_DATA_PROPERTY);}
        if(cntComplexAboxSUB_DATA_PROPERTY>0||true) {System.out.println("ComplexAboxSUB_DATA_PROPERTY="+cntComplexAboxSUB_DATA_PROPERTY);outputLines.add("ComplexAboxSUB_DATA_PROPERTY="+cntComplexAboxSUB_DATA_PROPERTY);}
        if(cntAboxEQUIVALENT_DATA_PROPERTIES>0||true) {System.out.println("AboxEQUIVALENT_DATA_PROPERTIES="+cntAboxEQUIVALENT_DATA_PROPERTIES);outputLines.add("AboxEQUIVALENT_DATA_PROPERTIES="+cntAboxEQUIVALENT_DATA_PROPERTIES);}
        if(cntComplexAboxEQUIVALENT_DATA_PROPERTIES>0||true) {System.out.println("ComplexAboxEQUIVALENT_DATA_PROPERTIES="+cntComplexAboxEQUIVALENT_DATA_PROPERTIES);outputLines.add("ComplexAboxEQUIVALENT_DATA_PROPERTIES="+cntComplexAboxEQUIVALENT_DATA_PROPERTIES);}
        if(cntAboxFUNCTIONAL_DATA_PROPERTY>0||true) {System.out.println("AboxFUNCTIONAL_DATA_PROPERTY="+cntAboxFUNCTIONAL_DATA_PROPERTY);outputLines.add("AboxFUNCTIONAL_DATA_PROPERTY="+cntAboxFUNCTIONAL_DATA_PROPERTY);}
        if(cntComplexAboxFUNCTIONAL_DATA_PROPERTY>0||true) {System.out.println("ComplexAboxFUNCTIONAL_DATA_PROPERTY="+cntComplexAboxFUNCTIONAL_DATA_PROPERTY);outputLines.add("ComplexAboxFUNCTIONAL_DATA_PROPERTY="+cntComplexAboxFUNCTIONAL_DATA_PROPERTY);}
        if(cntAboxDATATYPE_DEFINITION>0||true) {System.out.println("AboxDATATYPE_DEFINITION="+cntAboxDATATYPE_DEFINITION);outputLines.add("AboxDATATYPE_DEFINITION="+cntAboxDATATYPE_DEFINITION);}
        if(cntComplexAboxDATATYPE_DEFINITION>0||true) {System.out.println("ComplexAboxDATATYPE_DEFINITION="+cntComplexAboxDATATYPE_DEFINITION);outputLines.add("ComplexAboxDATATYPE_DEFINITION="+cntComplexAboxDATATYPE_DEFINITION);}
        if(cntAboxDISJOINT_UNION>0||true) {System.out.println("AboxDISJOINT_UNION="+cntAboxDISJOINT_UNION);outputLines.add("AboxDISJOINT_UNION="+cntAboxDISJOINT_UNION);}
        if(cntComplexAboxDISJOINT_UNION>0||true) {System.out.println("ComplexAboxDISJOINT_UNION="+cntComplexAboxDISJOINT_UNION);outputLines.add("ComplexAboxDISJOINT_UNION="+cntComplexAboxDISJOINT_UNION);}
        if(cntAboxDECLARATION>0||true) {System.out.println("AboxDECLARATION="+cntAboxDECLARATION);outputLines.add("AboxDECLARATION="+cntAboxDECLARATION);}
        if(cntComplexAboxDECLARATION>0||true) {System.out.println("ComplexAboxDECLARATION="+cntComplexAboxDECLARATION);outputLines.add("ComplexAboxDECLARATION="+cntComplexAboxDECLARATION);}
        if(cntAboxSWRL_RULE>0||true) {System.out.println("AboxSWRL_RULE="+cntAboxSWRL_RULE);outputLines.add("AboxSWRL_RULE="+cntAboxSWRL_RULE);}
        if(cntComplexAboxSWRL_RULE>0||true) {System.out.println("ComplexAboxSWRL_RULE="+cntComplexAboxSWRL_RULE);outputLines.add("ComplexAboxSWRL_RULE="+cntComplexAboxSWRL_RULE);}
        if(cntAboxANNOTATION_ASSERTION>0||true) {System.out.println("AboxANNOTATION_ASSERTION="+cntAboxANNOTATION_ASSERTION);outputLines.add("AboxANNOTATION_ASSERTION="+cntAboxANNOTATION_ASSERTION);}
        if(cntComplexAboxANNOTATION_ASSERTION>0||true) {System.out.println("ComplexAboxANNOTATION_ASSERTION="+cntComplexAboxANNOTATION_ASSERTION);outputLines.add("ComplexAboxANNOTATION_ASSERTION="+cntComplexAboxANNOTATION_ASSERTION);}
        if(cntAboxSUB_ANNOTATION_PROPERTY_OF>0||true) {System.out.println("AboxSUB_ANNOTATION_PROPERTY_OF="+cntAboxSUB_ANNOTATION_PROPERTY_OF);outputLines.add("AboxSUB_ANNOTATION_PROPERTY_OF="+cntAboxSUB_ANNOTATION_PROPERTY_OF);}
        if(cntComplexAboxSUB_ANNOTATION_PROPERTY_OF>0||true) {System.out.println("ComplexAboxSUB_ANNOTATION_PROPERTY_OF="+cntComplexAboxSUB_ANNOTATION_PROPERTY_OF);outputLines.add("ComplexAboxSUB_ANNOTATION_PROPERTY_OF="+cntComplexAboxSUB_ANNOTATION_PROPERTY_OF);}
        if(cntAboxANNOTATION_PROPERTY_DOMAIN>0||true) {System.out.println("AboxANNOTATION_PROPERTY_DOMAIN="+cntAboxANNOTATION_PROPERTY_DOMAIN);outputLines.add("AboxANNOTATION_PROPERTY_DOMAIN="+cntAboxANNOTATION_PROPERTY_DOMAIN);}
        if(cntComplexAboxANNOTATION_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexAboxANNOTATION_PROPERTY_DOMAIN="+cntComplexAboxANNOTATION_PROPERTY_DOMAIN);outputLines.add("ComplexAboxANNOTATION_PROPERTY_DOMAIN="+cntComplexAboxANNOTATION_PROPERTY_DOMAIN);}
        if(cntAboxANNOTATION_PROPERTY_RANGE>0||true) {System.out.println("AboxANNOTATION_PROPERTY_RANGE="+cntAboxANNOTATION_PROPERTY_RANGE);outputLines.add("AboxANNOTATION_PROPERTY_RANGE="+cntAboxANNOTATION_PROPERTY_RANGE);}
        if(cntComplexAboxANNOTATION_PROPERTY_RANGE>0||true) {System.out.println("ComplexAboxANNOTATION_PROPERTY_RANGE="+cntComplexAboxANNOTATION_PROPERTY_RANGE);outputLines.add("ComplexAboxANNOTATION_PROPERTY_RANGE="+cntComplexAboxANNOTATION_PROPERTY_RANGE);}
        if(cntAboxHAS_KEY>0||true) {System.out.println("AboxHAS_KEY="+cntAboxHAS_KEY);outputLines.add("AboxHAS_KEY="+cntAboxHAS_KEY);}
        if(cntComplexAboxHAS_KEY>0||true) {System.out.println("ComplexAboxHAS_KEY="+cntComplexAboxHAS_KEY);outputLines.add("ComplexAboxHAS_KEY="+cntComplexAboxHAS_KEY);}
        if(cntAboxOther>0||true) {System.out.println("AboxOther="+cntAboxOther);outputLines.add("AboxOther="+cntAboxOther);}
        if(cntRboxSUBCLASS_OF>0||true) {System.out.println("RboxSUBCLASS_OF="+cntRboxSUBCLASS_OF);outputLines.add("RboxSUBCLASS_OF="+cntRboxSUBCLASS_OF);}
        if(cntComplexRboxSUBCLASS_OF>0||true) {System.out.println("ComplexRboxSUBCLASS_OF="+cntComplexRboxSUBCLASS_OF);outputLines.add("ComplexRboxSUBCLASS_OF="+cntComplexRboxSUBCLASS_OF);}
        if(cntRboxEQUIVALENT_CLASSES>0||true) {System.out.println("RboxEQUIVALENT_CLASSES="+cntRboxEQUIVALENT_CLASSES);outputLines.add("RboxEQUIVALENT_CLASSES="+cntRboxEQUIVALENT_CLASSES);}
        if(cntComplexRboxEQUIVALENT_CLASSES>0||true) {System.out.println("ComplexRboxEQUIVALENT_CLASSES="+cntComplexRboxEQUIVALENT_CLASSES);outputLines.add("ComplexRboxEQUIVALENT_CLASSES="+cntComplexRboxEQUIVALENT_CLASSES);}
        if(cntRboxDISJOINT_CLASSES>0||true) {System.out.println("RboxDISJOINT_CLASSES="+cntRboxDISJOINT_CLASSES);outputLines.add("RboxDISJOINT_CLASSES="+cntRboxDISJOINT_CLASSES);}
        if(cntComplexRboxDISJOINT_CLASSES>0||true) {System.out.println("ComplexRboxDISJOINT_CLASSES="+cntComplexRboxDISJOINT_CLASSES);outputLines.add("ComplexRboxDISJOINT_CLASSES="+cntComplexRboxDISJOINT_CLASSES);}
        if(cntRboxCLASS_ASSERTION>0||true) {System.out.println("RboxCLASS_ASSERTION="+cntRboxCLASS_ASSERTION);outputLines.add("RboxCLASS_ASSERTION="+cntRboxCLASS_ASSERTION);}
        if(cntComplexRboxCLASS_ASSERTION>0||true) {System.out.println("ComplexRboxCLASS_ASSERTION="+cntComplexRboxCLASS_ASSERTION);outputLines.add("ComplexRboxCLASS_ASSERTION="+cntComplexRboxCLASS_ASSERTION);}
        if(cntRboxSAME_INDIVIDUAL>0||true) {System.out.println("RboxSAME_INDIVIDUAL="+cntRboxSAME_INDIVIDUAL);outputLines.add("RboxSAME_INDIVIDUAL="+cntRboxSAME_INDIVIDUAL);}
        if(cntComplexRboxSAME_INDIVIDUAL>0||true) {System.out.println("ComplexRboxSAME_INDIVIDUAL="+cntComplexRboxSAME_INDIVIDUAL);outputLines.add("ComplexRboxSAME_INDIVIDUAL="+cntComplexRboxSAME_INDIVIDUAL);}
        if(cntRboxDIFFERENT_INDIVIDUALS>0||true) {System.out.println("RboxDIFFERENT_INDIVIDUALS="+cntRboxDIFFERENT_INDIVIDUALS);outputLines.add("RboxDIFFERENT_INDIVIDUALS="+cntRboxDIFFERENT_INDIVIDUALS);}
        if(cntComplexRboxDIFFERENT_INDIVIDUALS>0||true) {System.out.println("ComplexRboxDIFFERENT_INDIVIDUALS="+cntComplexRboxDIFFERENT_INDIVIDUALS);outputLines.add("ComplexRboxDIFFERENT_INDIVIDUALS="+cntComplexRboxDIFFERENT_INDIVIDUALS);}
        if(cntRboxOBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("RboxOBJECT_PROPERTY_ASSERTION="+cntRboxOBJECT_PROPERTY_ASSERTION);outputLines.add("RboxOBJECT_PROPERTY_ASSERTION="+cntRboxOBJECT_PROPERTY_ASSERTION);}
        if(cntComplexRboxOBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexRboxOBJECT_PROPERTY_ASSERTION="+cntComplexRboxOBJECT_PROPERTY_ASSERTION);outputLines.add("ComplexRboxOBJECT_PROPERTY_ASSERTION="+cntComplexRboxOBJECT_PROPERTY_ASSERTION);}
        if(cntRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("RboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);outputLines.add("RboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);}
        if(cntComplexRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntComplexRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);outputLines.add("ComplexRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION="+cntComplexRboxNEGATIVE_OBJECT_PROPERTY_ASSERTION);}
        if(cntRboxDATA_PROPERTY_ASSERTION>0||true) {System.out.println("RboxDATA_PROPERTY_ASSERTION="+cntRboxDATA_PROPERTY_ASSERTION);outputLines.add("RboxDATA_PROPERTY_ASSERTION="+cntRboxDATA_PROPERTY_ASSERTION);}
        if(cntComplexRboxDATA_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexRboxDATA_PROPERTY_ASSERTION="+cntComplexRboxDATA_PROPERTY_ASSERTION);outputLines.add("ComplexRboxDATA_PROPERTY_ASSERTION="+cntComplexRboxDATA_PROPERTY_ASSERTION);}
        if(cntRboxNEGATIVE_DATA_PROPERTY_ASSERTION>0||true) {System.out.println("RboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntRboxNEGATIVE_DATA_PROPERTY_ASSERTION);outputLines.add("RboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntRboxNEGATIVE_DATA_PROPERTY_ASSERTION);}
        if(cntComplexRboxNEGATIVE_DATA_PROPERTY_ASSERTION>0||true) {System.out.println("ComplexRboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntComplexRboxNEGATIVE_DATA_PROPERTY_ASSERTION);outputLines.add("ComplexRboxNEGATIVE_DATA_PROPERTY_ASSERTION="+cntComplexRboxNEGATIVE_DATA_PROPERTY_ASSERTION);}
        if(cntRboxOBJECT_PROPERTY_DOMAIN>0||true) {System.out.println("RboxOBJECT_PROPERTY_DOMAIN="+cntRboxOBJECT_PROPERTY_DOMAIN);outputLines.add("RboxOBJECT_PROPERTY_DOMAIN="+cntRboxOBJECT_PROPERTY_DOMAIN);}
        if(cntComplexRboxOBJECT_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexRboxOBJECT_PROPERTY_DOMAIN="+cntComplexRboxOBJECT_PROPERTY_DOMAIN);outputLines.add("ComplexRboxOBJECT_PROPERTY_DOMAIN="+cntComplexRboxOBJECT_PROPERTY_DOMAIN);}
        if(cntRboxOBJECT_PROPERTY_RANGE>0||true) {System.out.println("RboxOBJECT_PROPERTY_RANGE="+cntRboxOBJECT_PROPERTY_RANGE);outputLines.add("RboxOBJECT_PROPERTY_RANGE="+cntRboxOBJECT_PROPERTY_RANGE);}
        if(cntComplexRboxOBJECT_PROPERTY_RANGE>0||true) {System.out.println("ComplexRboxOBJECT_PROPERTY_RANGE="+cntComplexRboxOBJECT_PROPERTY_RANGE);outputLines.add("ComplexRboxOBJECT_PROPERTY_RANGE="+cntComplexRboxOBJECT_PROPERTY_RANGE);}
        if(cntRboxDISJOINT_OBJECT_PROPERTIES>0||true) {System.out.println("RboxDISJOINT_OBJECT_PROPERTIES="+cntRboxDISJOINT_OBJECT_PROPERTIES);outputLines.add("RboxDISJOINT_OBJECT_PROPERTIES="+cntRboxDISJOINT_OBJECT_PROPERTIES);}
        if(cntComplexRboxDISJOINT_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexRboxDISJOINT_OBJECT_PROPERTIES="+cntComplexRboxDISJOINT_OBJECT_PROPERTIES);outputLines.add("ComplexRboxDISJOINT_OBJECT_PROPERTIES="+cntComplexRboxDISJOINT_OBJECT_PROPERTIES);}
        if(cntRboxSUB_OBJECT_PROPERTY>0||true) {System.out.println("RboxSUB_OBJECT_PROPERTY="+cntRboxSUB_OBJECT_PROPERTY);outputLines.add("RboxSUB_OBJECT_PROPERTY="+cntRboxSUB_OBJECT_PROPERTY);}
        if(cntComplexRboxSUB_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxSUB_OBJECT_PROPERTY="+cntComplexRboxSUB_OBJECT_PROPERTY);outputLines.add("ComplexRboxSUB_OBJECT_PROPERTY="+cntComplexRboxSUB_OBJECT_PROPERTY);}
        if(cntRboxEQUIVALENT_OBJECT_PROPERTIES>0||true) {System.out.println("RboxEQUIVALENT_OBJECT_PROPERTIES="+cntRboxEQUIVALENT_OBJECT_PROPERTIES);outputLines.add("RboxEQUIVALENT_OBJECT_PROPERTIES="+cntRboxEQUIVALENT_OBJECT_PROPERTIES);}
        if(cntComplexRboxEQUIVALENT_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexRboxEQUIVALENT_OBJECT_PROPERTIES="+cntComplexRboxEQUIVALENT_OBJECT_PROPERTIES);outputLines.add("ComplexRboxEQUIVALENT_OBJECT_PROPERTIES="+cntComplexRboxEQUIVALENT_OBJECT_PROPERTIES);}
        if(cntRboxINVERSE_OBJECT_PROPERTIES>0||true) {System.out.println("RboxINVERSE_OBJECT_PROPERTIES="+cntRboxINVERSE_OBJECT_PROPERTIES);outputLines.add("RboxINVERSE_OBJECT_PROPERTIES="+cntRboxINVERSE_OBJECT_PROPERTIES);}
        if(cntComplexRboxINVERSE_OBJECT_PROPERTIES>0||true) {System.out.println("ComplexRboxINVERSE_OBJECT_PROPERTIES="+cntComplexRboxINVERSE_OBJECT_PROPERTIES);outputLines.add("ComplexRboxINVERSE_OBJECT_PROPERTIES="+cntComplexRboxINVERSE_OBJECT_PROPERTIES);}
        if(cntRboxSUB_PROPERTY_CHAIN_OF>0||true) {System.out.println("RboxSUB_PROPERTY_CHAIN_OF="+cntRboxSUB_PROPERTY_CHAIN_OF);outputLines.add("RboxSUB_PROPERTY_CHAIN_OF="+cntRboxSUB_PROPERTY_CHAIN_OF);}
        if(cntComplexRboxSUB_PROPERTY_CHAIN_OF>0||true) {System.out.println("ComplexRboxSUB_PROPERTY_CHAIN_OF="+cntComplexRboxSUB_PROPERTY_CHAIN_OF);outputLines.add("ComplexRboxSUB_PROPERTY_CHAIN_OF="+cntComplexRboxSUB_PROPERTY_CHAIN_OF);}
        if(cntRboxFUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("RboxFUNCTIONAL_OBJECT_PROPERTY="+cntRboxFUNCTIONAL_OBJECT_PROPERTY);outputLines.add("RboxFUNCTIONAL_OBJECT_PROPERTY="+cntRboxFUNCTIONAL_OBJECT_PROPERTY);}
        if(cntComplexRboxFUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxFUNCTIONAL_OBJECT_PROPERTY="+cntComplexRboxFUNCTIONAL_OBJECT_PROPERTY);outputLines.add("ComplexRboxFUNCTIONAL_OBJECT_PROPERTY="+cntComplexRboxFUNCTIONAL_OBJECT_PROPERTY);}
        if(cntRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("RboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);outputLines.add("RboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);}
        if(cntComplexRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntComplexRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);outputLines.add("ComplexRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY="+cntComplexRboxINVERSE_FUNCTIONAL_OBJECT_PROPERTY);}
        if(cntRboxSYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("RboxSYMMETRIC_OBJECT_PROPERTY="+cntRboxSYMMETRIC_OBJECT_PROPERTY);outputLines.add("RboxSYMMETRIC_OBJECT_PROPERTY="+cntRboxSYMMETRIC_OBJECT_PROPERTY);}
        if(cntComplexRboxSYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxSYMMETRIC_OBJECT_PROPERTY="+cntComplexRboxSYMMETRIC_OBJECT_PROPERTY);outputLines.add("ComplexRboxSYMMETRIC_OBJECT_PROPERTY="+cntComplexRboxSYMMETRIC_OBJECT_PROPERTY);}
        if(cntRboxASYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("RboxASYMMETRIC_OBJECT_PROPERTY="+cntRboxASYMMETRIC_OBJECT_PROPERTY);outputLines.add("RboxASYMMETRIC_OBJECT_PROPERTY="+cntRboxASYMMETRIC_OBJECT_PROPERTY);}
        if(cntComplexRboxASYMMETRIC_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxASYMMETRIC_OBJECT_PROPERTY="+cntComplexRboxASYMMETRIC_OBJECT_PROPERTY);outputLines.add("ComplexRboxASYMMETRIC_OBJECT_PROPERTY="+cntComplexRboxASYMMETRIC_OBJECT_PROPERTY);}
        if(cntRboxTRANSITIVE_OBJECT_PROPERTY>0||true) {System.out.println("RboxTRANSITIVE_OBJECT_PROPERTY="+cntRboxTRANSITIVE_OBJECT_PROPERTY);outputLines.add("RboxTRANSITIVE_OBJECT_PROPERTY="+cntRboxTRANSITIVE_OBJECT_PROPERTY);}
        if(cntComplexRboxTRANSITIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxTRANSITIVE_OBJECT_PROPERTY="+cntComplexRboxTRANSITIVE_OBJECT_PROPERTY);outputLines.add("ComplexRboxTRANSITIVE_OBJECT_PROPERTY="+cntComplexRboxTRANSITIVE_OBJECT_PROPERTY);}
        if(cntRboxREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("RboxREFLEXIVE_OBJECT_PROPERTY="+cntRboxREFLEXIVE_OBJECT_PROPERTY);outputLines.add("RboxREFLEXIVE_OBJECT_PROPERTY="+cntRboxREFLEXIVE_OBJECT_PROPERTY);}
        if(cntComplexRboxREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxREFLEXIVE_OBJECT_PROPERTY="+cntComplexRboxREFLEXIVE_OBJECT_PROPERTY);outputLines.add("ComplexRboxREFLEXIVE_OBJECT_PROPERTY="+cntComplexRboxREFLEXIVE_OBJECT_PROPERTY);}
        if(cntRboxIRREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("RboxIRREFLEXIVE_OBJECT_PROPERTY="+cntRboxIRREFLEXIVE_OBJECT_PROPERTY);outputLines.add("RboxIRREFLEXIVE_OBJECT_PROPERTY="+cntRboxIRREFLEXIVE_OBJECT_PROPERTY);}
        if(cntComplexRboxIRREFLEXIVE_OBJECT_PROPERTY>0||true) {System.out.println("ComplexRboxIRREFLEXIVE_OBJECT_PROPERTY="+cntComplexRboxIRREFLEXIVE_OBJECT_PROPERTY);outputLines.add("ComplexRboxIRREFLEXIVE_OBJECT_PROPERTY="+cntComplexRboxIRREFLEXIVE_OBJECT_PROPERTY);}
        if(cntRboxDATA_PROPERTY_DOMAIN>0||true) {System.out.println("RboxDATA_PROPERTY_DOMAIN="+cntRboxDATA_PROPERTY_DOMAIN);outputLines.add("RboxDATA_PROPERTY_DOMAIN="+cntRboxDATA_PROPERTY_DOMAIN);}
        if(cntComplexRboxDATA_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexRboxDATA_PROPERTY_DOMAIN="+cntComplexRboxDATA_PROPERTY_DOMAIN);outputLines.add("ComplexRboxDATA_PROPERTY_DOMAIN="+cntComplexRboxDATA_PROPERTY_DOMAIN);}
        if(cntRboxDATA_PROPERTY_RANGE>0||true) {System.out.println("RboxDATA_PROPERTY_RANGE="+cntRboxDATA_PROPERTY_RANGE);outputLines.add("RboxDATA_PROPERTY_RANGE="+cntRboxDATA_PROPERTY_RANGE);}
        if(cntComplexRboxDATA_PROPERTY_RANGE>0||true) {System.out.println("ComplexRboxDATA_PROPERTY_RANGE="+cntComplexRboxDATA_PROPERTY_RANGE);outputLines.add("ComplexRboxDATA_PROPERTY_RANGE="+cntComplexRboxDATA_PROPERTY_RANGE);}
        if(cntRboxDISJOINT_DATA_PROPERTIES>0||true) {System.out.println("RboxDISJOINT_DATA_PROPERTIES="+cntRboxDISJOINT_DATA_PROPERTIES);outputLines.add("RboxDISJOINT_DATA_PROPERTIES="+cntRboxDISJOINT_DATA_PROPERTIES);}
        if(cntComplexRboxDISJOINT_DATA_PROPERTIES>0||true) {System.out.println("ComplexRboxDISJOINT_DATA_PROPERTIES="+cntComplexRboxDISJOINT_DATA_PROPERTIES);outputLines.add("ComplexRboxDISJOINT_DATA_PROPERTIES="+cntComplexRboxDISJOINT_DATA_PROPERTIES);}
        if(cntRboxSUB_DATA_PROPERTY>0||true) {System.out.println("RboxSUB_DATA_PROPERTY="+cntRboxSUB_DATA_PROPERTY);outputLines.add("RboxSUB_DATA_PROPERTY="+cntRboxSUB_DATA_PROPERTY);}
        if(cntComplexRboxSUB_DATA_PROPERTY>0||true) {System.out.println("ComplexRboxSUB_DATA_PROPERTY="+cntComplexRboxSUB_DATA_PROPERTY);outputLines.add("ComplexRboxSUB_DATA_PROPERTY="+cntComplexRboxSUB_DATA_PROPERTY);}
        if(cntRboxEQUIVALENT_DATA_PROPERTIES>0||true) {System.out.println("RboxEQUIVALENT_DATA_PROPERTIES="+cntRboxEQUIVALENT_DATA_PROPERTIES);outputLines.add("RboxEQUIVALENT_DATA_PROPERTIES="+cntRboxEQUIVALENT_DATA_PROPERTIES);}
        if(cntComplexRboxEQUIVALENT_DATA_PROPERTIES>0||true) {System.out.println("ComplexRboxEQUIVALENT_DATA_PROPERTIES="+cntComplexRboxEQUIVALENT_DATA_PROPERTIES);outputLines.add("ComplexRboxEQUIVALENT_DATA_PROPERTIES="+cntComplexRboxEQUIVALENT_DATA_PROPERTIES);}
        if(cntRboxFUNCTIONAL_DATA_PROPERTY>0||true) {System.out.println("RboxFUNCTIONAL_DATA_PROPERTY="+cntRboxFUNCTIONAL_DATA_PROPERTY);outputLines.add("RboxFUNCTIONAL_DATA_PROPERTY="+cntRboxFUNCTIONAL_DATA_PROPERTY);}
        if(cntComplexRboxFUNCTIONAL_DATA_PROPERTY>0||true) {System.out.println("ComplexRboxFUNCTIONAL_DATA_PROPERTY="+cntComplexRboxFUNCTIONAL_DATA_PROPERTY);outputLines.add("ComplexRboxFUNCTIONAL_DATA_PROPERTY="+cntComplexRboxFUNCTIONAL_DATA_PROPERTY);}
        if(cntRboxDATATYPE_DEFINITION>0||true) {System.out.println("RboxDATATYPE_DEFINITION="+cntRboxDATATYPE_DEFINITION);outputLines.add("RboxDATATYPE_DEFINITION="+cntRboxDATATYPE_DEFINITION);}
        if(cntComplexRboxDATATYPE_DEFINITION>0||true) {System.out.println("ComplexRboxDATATYPE_DEFINITION="+cntComplexRboxDATATYPE_DEFINITION);outputLines.add("ComplexRboxDATATYPE_DEFINITION="+cntComplexRboxDATATYPE_DEFINITION);}
        if(cntRboxDISJOINT_UNION>0||true) {System.out.println("RboxDISJOINT_UNION="+cntRboxDISJOINT_UNION);outputLines.add("RboxDISJOINT_UNION="+cntRboxDISJOINT_UNION);}
        if(cntComplexRboxDISJOINT_UNION>0||true) {System.out.println("ComplexRboxDISJOINT_UNION="+cntComplexRboxDISJOINT_UNION);outputLines.add("ComplexRboxDISJOINT_UNION="+cntComplexRboxDISJOINT_UNION);}
        if(cntRboxDECLARATION>0||true) {System.out.println("RboxDECLARATION="+cntRboxDECLARATION);outputLines.add("RboxDECLARATION="+cntRboxDECLARATION);}
        if(cntComplexRboxDECLARATION>0||true) {System.out.println("ComplexRboxDECLARATION="+cntComplexRboxDECLARATION);outputLines.add("ComplexRboxDECLARATION="+cntComplexRboxDECLARATION);}
        if(cntRboxSWRL_RULE>0||true) {System.out.println("RboxSWRL_RULE="+cntRboxSWRL_RULE);outputLines.add("RboxSWRL_RULE="+cntRboxSWRL_RULE);}
        if(cntComplexRboxSWRL_RULE>0||true) {System.out.println("ComplexRboxSWRL_RULE="+cntComplexRboxSWRL_RULE);outputLines.add("ComplexRboxSWRL_RULE="+cntComplexRboxSWRL_RULE);}
        if(cntRboxANNOTATION_ASSERTION>0||true) {System.out.println("RboxANNOTATION_ASSERTION="+cntRboxANNOTATION_ASSERTION);outputLines.add("RboxANNOTATION_ASSERTION="+cntRboxANNOTATION_ASSERTION);}
        if(cntComplexRboxANNOTATION_ASSERTION>0||true) {System.out.println("ComplexRboxANNOTATION_ASSERTION="+cntComplexRboxANNOTATION_ASSERTION);outputLines.add("ComplexRboxANNOTATION_ASSERTION="+cntComplexRboxANNOTATION_ASSERTION);}
        if(cntRboxSUB_ANNOTATION_PROPERTY_OF>0||true) {System.out.println("RboxSUB_ANNOTATION_PROPERTY_OF="+cntRboxSUB_ANNOTATION_PROPERTY_OF);outputLines.add("RboxSUB_ANNOTATION_PROPERTY_OF="+cntRboxSUB_ANNOTATION_PROPERTY_OF);}
        if(cntComplexRboxSUB_ANNOTATION_PROPERTY_OF>0||true) {System.out.println("ComplexRboxSUB_ANNOTATION_PROPERTY_OF="+cntComplexRboxSUB_ANNOTATION_PROPERTY_OF);outputLines.add("ComplexRboxSUB_ANNOTATION_PROPERTY_OF="+cntComplexRboxSUB_ANNOTATION_PROPERTY_OF);}
        if(cntRboxANNOTATION_PROPERTY_DOMAIN>0||true) {System.out.println("RboxANNOTATION_PROPERTY_DOMAIN="+cntRboxANNOTATION_PROPERTY_DOMAIN);outputLines.add("RboxANNOTATION_PROPERTY_DOMAIN="+cntRboxANNOTATION_PROPERTY_DOMAIN);}
        if(cntComplexRboxANNOTATION_PROPERTY_DOMAIN>0||true) {System.out.println("ComplexRboxANNOTATION_PROPERTY_DOMAIN="+cntComplexRboxANNOTATION_PROPERTY_DOMAIN);outputLines.add("ComplexRboxANNOTATION_PROPERTY_DOMAIN="+cntComplexRboxANNOTATION_PROPERTY_DOMAIN);}
        if(cntRboxANNOTATION_PROPERTY_RANGE>0||true) {System.out.println("RboxANNOTATION_PROPERTY_RANGE="+cntRboxANNOTATION_PROPERTY_RANGE);outputLines.add("RboxANNOTATION_PROPERTY_RANGE="+cntRboxANNOTATION_PROPERTY_RANGE);}
        if(cntComplexRboxANNOTATION_PROPERTY_RANGE>0||true) {System.out.println("ComplexRboxANNOTATION_PROPERTY_RANGE="+cntComplexRboxANNOTATION_PROPERTY_RANGE);outputLines.add("ComplexRboxANNOTATION_PROPERTY_RANGE="+cntComplexRboxANNOTATION_PROPERTY_RANGE);}
        if(cntRboxHAS_KEY>0||true) {System.out.println("RboxHAS_KEY="+cntRboxHAS_KEY);outputLines.add("RboxHAS_KEY="+cntRboxHAS_KEY);}
        if(cntComplexRboxHAS_KEY>0||true) {System.out.println("ComplexRboxHAS_KEY="+cntComplexRboxHAS_KEY);outputLines.add("ComplexRboxHAS_KEY="+cntComplexRboxHAS_KEY);}
        if(cntRboxOther>0||true) {System.out.println("RboxOther="+cntRboxOther);outputLines.add("RboxOther="+cntRboxOther);}

        return outputLines;
    }

    public ArrayList<String> getAxiomsWithComplexExpressions4File(String absoluteFilePath,boolean showDetails) throws OWLOntologyCreationException, IOException {
        File file = new File(absoluteFilePath);

        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();


        manager = OWLManager.createOWLOntologyManager();
        OWLOntology onto = manager.loadOntologyFromOntologyDocument(file);

        ArrayList<String> outputAxioms= new ArrayList<String>();


        for(OWLAxiom axiom:onto.getTBoxAxioms(true)){


            if(axiom.getAxiomType()==AxiomType.SUBCLASS_OF){

                OWLSubClassOfAxiom var = (OWLSubClassOfAxiom)axiom;
                OWLClassExpression subClass = var.getSubClass().getNNF();
                //OWLClassExpression superClass = axiom.getSuperClass();
                OWLClassExpression superClass = var.getSuperClass().getNNF();

                if(subClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                        &&superClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS) {
                    //do nothing
                }else if(subClass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
                    OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) subClass;
                    Set<OWLClassExpression> operands = and.getOperands();
                    for(OWLClassExpression ex:operands){
                        if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                                //System.out.println("SubClassOf-LHS-Intersection:"+renderer.render(axiom));
                                outputAxioms.add("SubClassOf-LHS-Intersection:"+renderer.render(axiom));
                                break;
                        }
                    }
                }else if(subClass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF){
                    OWLObjectUnionOf and = (OWLObjectUnionOf) subClass;
                    Set<OWLClassExpression> operands = and.getOperands();
                    for(OWLClassExpression ex:operands){
                        if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                            //System.out.println("SubClassOf-LHS-Union:"+renderer.render(axiom));
                            outputAxioms.add("SubClassOf-LHS-Intersection:"+renderer.render(axiom));
                            break;
                        }
                    }
                }else if(superClass.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
                    OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) superClass;
                    Set<OWLClassExpression> operands = and.getOperands();
                    for(OWLClassExpression ex:operands){
                        if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                            //System.out.println("SubClassOf-RHS-Intersection:"+renderer.render(axiom));
                            outputAxioms.add("SubClassOf-RHS-Intersection:"+renderer.render(axiom));
                            break;
                        }
                    }
                }else if(superClass.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                    OWLObjectUnionOf and = (OWLObjectUnionOf) superClass;
                    Set<OWLClassExpression> operands = and.getOperands();
                    for(OWLClassExpression ex:operands){
                        if(ex.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && ex.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                            //System.out.println("SubClassOf-RHS-Union:"+renderer.render(axiom));
                            outputAxioms.add("SubClassOf-RHS-Union:"+renderer.render(axiom));
                            break;
                        }
                    }
                }else if(subClass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                    OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) subClass;
                    if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_EXACT_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                        //System.out.println("SubClassOf-LHS-SomeValuesFrom:"+renderer.render(axiom));
                        outputAxioms.add("SubClassOf-LHS-SomeValuesFrom:"+renderer.render(axiom));
                    }
                }else if(subClass.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                    OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) subClass;
                    if(all.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_EXACT_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                        //System.out.println("SubClassOf-LHS-AllValuesFrom:"+renderer.render(axiom));
                        outputAxioms.add("SubClassOf-LHS-AllValuesFrom:"+renderer.render(axiom));
                    }
                }else if(superClass.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                    OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) superClass;
                    if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_EXACT_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                        //System.out.println("SubClassOf-RHS-SomeValuesFrom:"+renderer.render(axiom));
                        outputAxioms.add("SubClassOf-RHS-SomeValuesFrom:" + renderer.render(axiom));
                    }
                }else if(superClass.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                    OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) superClass;
                    if(all.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_EXACT_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                    && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF
                            ){
                        //System.out.println("SubClassOf-RHS-AllValuesFrom:"+renderer.render(axiom));
                        outputAxioms.add("SubClassOf-RHS-AllValuesFrom:"+renderer.render(axiom));
                    }
                }else if(subClass.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF
                       ||superClass.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF){
                    //System.out.println("SubClassOf-ObjectHasSelf:"+renderer.render(axiom));
                    outputAxioms.add("SubClassOf-ObjectHasSelf:"+renderer.render(axiom));
                }else if(superClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                        ||superClass.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                        ||superClass.getClassExpressionType()==ClassExpressionType.OBJECT_MIN_CARDINALITY
                        ||superClass.getClassExpressionType()==ClassExpressionType.OBJECT_EXACT_CARDINALITY
                        ||superClass.getClassExpressionType()==ClassExpressionType.DATA_MAX_CARDINALITY
                        ||superClass.getClassExpressionType()==ClassExpressionType.DATA_MIN_CARDINALITY
                        ||superClass.getClassExpressionType()==ClassExpressionType.DATA_EXACT_CARDINALITY
                        ||superClass.getClassExpressionType()==ClassExpressionType.DATA_HAS_VALUE
                        ||superClass.getClassExpressionType()==ClassExpressionType.OBJECT_ONE_OF
                        ||superClass.getClassExpressionType()==ClassExpressionType.DATA_SOME_VALUES_FROM
                        ||superClass.getClassExpressionType()==ClassExpressionType.DATA_ALL_VALUES_FROM) {
                    if(subClass.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                            ||subClass.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                            ||subClass.getClassExpressionType()==ClassExpressionType.OBJECT_MIN_CARDINALITY
                            ||subClass.getClassExpressionType()==ClassExpressionType.OBJECT_EXACT_CARDINALITY
                            ||subClass.getClassExpressionType()==ClassExpressionType.DATA_MAX_CARDINALITY
                            ||subClass.getClassExpressionType()==ClassExpressionType.DATA_MIN_CARDINALITY
                            ||subClass.getClassExpressionType()==ClassExpressionType.DATA_EXACT_CARDINALITY
                            ||subClass.getClassExpressionType()==ClassExpressionType.DATA_HAS_VALUE
                            ||subClass.getClassExpressionType()==ClassExpressionType.OBJECT_ONE_OF
                            ||subClass.getClassExpressionType()==ClassExpressionType.DATA_SOME_VALUES_FROM
                            ||subClass.getClassExpressionType()==ClassExpressionType.DATA_ALL_VALUES_FROM){
                    }
                        //Do nothing
                }else{
                    //System.out.println("SubClassOf-Other:"+renderer.render(axiom));
                    outputAxioms.add("SubClassOf-Other:"+renderer.render(axiom));
                }

            }else if(axiom.getAxiomType()==AxiomType.EQUIVALENT_CLASSES){
                OWLEquivalentClassesAxiom var = (OWLEquivalentClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                        //do nothing
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF){
                        OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) ex;
                        Set<OWLClassExpression> operands = and.getOperands();
                        for(OWLClassExpression op:operands) {
                            if (op.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                                //System.out.println("EquivalentClasses-Intersection:"+renderer.render(axiom));
                                outputAxioms.add("EquivalentClasses-Intersection:"+renderer.render(axiom));
                                break;
                            }
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                        OWLObjectUnionOf or = (OWLObjectUnionOf) ex;
                        Set<OWLClassExpression> operands = or.getOperands();
                        for(OWLClassExpression op:operands) {
                            if (op.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                                //System.out.println("EquivalentClasses-Union:"+renderer.render(axiom));
                                outputAxioms.add("EquivalentClasses-Union:"+renderer.render(axiom));
                                break;
                            }
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                        OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ex;
                        if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                            //System.out.println("EquivalentClasses-SomeValuesFrom:" + renderer.render(axiom));
                            outputAxioms.add("EquivalentClasses-SomeValuesFrom:"+renderer.render(axiom));
                            break;
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                        OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) ex;
                        if (all.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                            //System.out.println("EquivalentClasses-AllValuesFrom:" + renderer.render(axiom));
                            outputAxioms.add("EquivalentClasses-AllValuesFrom:"+renderer.render(axiom));
                            break;
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OWL_CLASS
                            || ex.getClassExpressionType()==ClassExpressionType.DATA_EXACT_CARDINALITY
                            || ex.getClassExpressionType()==ClassExpressionType.DATA_MIN_CARDINALITY
                            || ex.getClassExpressionType()==ClassExpressionType.DATA_MAX_CARDINALITY
                            || ex.getClassExpressionType()==ClassExpressionType.DATA_HAS_VALUE
                            || ex.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                            || ex.getClassExpressionType()==ClassExpressionType.OBJECT_MIN_CARDINALITY
                            || ex.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                            || ex.getClassExpressionType()==ClassExpressionType.OBJECT_ONE_OF) {
                        //do nothing
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF||
                            ex.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF){
                        //System.out.println("EquivalentClasses-HasSelf:"+renderer.render(axiom));
                        outputAxioms.add("EquivalentClasses-HasSelf:"+renderer.render(axiom));
                        break;
                    }else{
                        //System.out.println("EquivalentClasses-Other:"+renderer.render(axiom));
                        outputAxioms.add("EquivalentClasses-Other:"+renderer.render(axiom));
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_CLASSES){
                OWLDisjointClassesAxiom var = (OWLDisjointClassesAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                        //do nothing
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF){
                        OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) ex;
                        Set<OWLClassExpression> operands = and.getOperands();
                        for(OWLClassExpression op:operands) {
                            if (op.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                                //System.out.println("DisjointClasses-Intersection:"+renderer.render(axiom));
                                outputAxioms.add("DisjointClasses-Intersection:"+renderer.render(axiom));
                                break;
                            }
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                        OWLObjectUnionOf or = (OWLObjectUnionOf) ex;
                        Set<OWLClassExpression> operands = or.getOperands();
                        for(OWLClassExpression op:operands) {
                            if (op.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                    && op.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                                //System.out.println("DisjointClasses-Union:"+renderer.render(axiom));
                                outputAxioms.add("DisjointClasses-Union:"+renderer.render(axiom));
                                break;
                            }
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                        OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ex;
                        if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                        && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                            //System.out.println("DisjointClasses-SomeValuesFrom:"+renderer.render(axiom));
                            outputAxioms.add("DisjointClasses-SomeValuesFrom:"+renderer.render(axiom));
                            break;
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                        OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) ex;
                        if (all.getFiller().getClassExpressionType() != ClassExpressionType.OWL_CLASS
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                         && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                            //System.out.println("DisjointClasses-AllValuesFrom:"+renderer.render(axiom));
                            outputAxioms.add("DisjointClasses-AllValuesFrom:"+renderer.render(axiom));
                            break;
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_MIN_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_EXACT_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_MAX_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_MIN_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_EXACT_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_HAS_VALUE
                            ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_ONE_OF) {
                        //do nothing
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF||
                            ex.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF) {
                        //System.out.println("DisjointClasses-ObjectHasSelf:"+renderer.render(axiom));
                        outputAxioms.add("DisjointClasses-ObjectHasSelf:" + renderer.render(axiom));
                        break;
                    }else{
                        //System.out.println("DisjointClasses-Other:"+renderer.render(axiom));
                        outputAxioms.add("DisjointClasses-Other:"+renderer.render(axiom));
                        break;
                    }
                }
            }else if(axiom.getAxiomType()==AxiomType.CLASS_ASSERTION){
                OWLClassAssertionAxiom var = (OWLClassAssertionAxiom)axiom;
                OWLClassExpression ex =var.getClassExpression();
                if (ex.getClassExpressionType() == ClassExpressionType.OWL_CLASS) {
                    //do nothing
                }else if (ex.getClassExpressionType() == ClassExpressionType.OBJECT_INTERSECTION_OF) {
                    //System.out.println("ClassAssertion-Intersection:"+renderer.render(axiom));
                    outputAxioms.add("ClassAssertion-Intersection:"+renderer.render(axiom));
                    }
                else if (ex.getClassExpressionType() == ClassExpressionType.OBJECT_UNION_OF) {
                    //System.out.println("ClassAssertion-Union:"+renderer.render(axiom));
                    outputAxioms.add("ClassAssertion-Union:"+renderer.render(axiom));
                } else if (ex.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM) {
                    //System.out.println("ClassAssertion-SomeValuesFrom:"+renderer.render(axiom));
                    outputAxioms.add("ClassAssertion-SomeValuesFrom:"+renderer.render(axiom));
                } else if (ex.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                    //System.out.println("ClassAssertion-AllValuesFrom:"+renderer.render(axiom));
                    outputAxioms.add("ClassAssertion-AllValuesFrom:"+renderer.render(axiom));
                } else if (ex.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                        ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_MIN_CARDINALITY
                        ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_EXACT_CARDINALITY
                        ||ex.getClassExpressionType()==ClassExpressionType.DATA_MAX_CARDINALITY
                        ||ex.getClassExpressionType()==ClassExpressionType.DATA_MIN_CARDINALITY
                        ||ex.getClassExpressionType()==ClassExpressionType.DATA_EXACT_CARDINALITY
                        ||ex.getClassExpressionType()==ClassExpressionType.DATA_HAS_VALUE
                        ) {
                    //do nothing
                } else if (ex.getClassExpressionType() == ClassExpressionType.OBJECT_HAS_SELF ||
                        ex.getClassExpressionType() == ClassExpressionType.OBJECT_HAS_SELF) {
                    //System.out.println("ClassAssertion-ObjectHasSelf:"+renderer.render(axiom));
                    outputAxioms.add("ClassAssertion-ObjectHasSelf:"+renderer.render(axiom));
                } else if (ex.getClassExpressionType() == ClassExpressionType.OBJECT_ONE_OF||
                        ex.getClassExpressionType() == ClassExpressionType.OBJECT_ONE_OF) {
                    //System.out.println("ClassAssertion-ObjectOneOf:"+renderer.render(axiom));
                }else {
                    //System.out.println("ClassAssertion-ObjectOneOf:"+renderer.render(axiom));
                    outputAxioms.add("ClassAssertion-Other:"+renderer.render(axiom));
                }
            }else if(axiom.getAxiomType()==AxiomType.DISJOINT_UNION){
                OWLDisjointUnionAxiom var = (OWLDisjointUnionAxiom)axiom;
                for(OWLClassExpression ex:var.getClassExpressions()){
                    if(ex.getClassExpressionType()==ClassExpressionType.OWL_CLASS){
                        //do nothing
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_INTERSECTION_OF){
                        OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) ex;
                        Set<OWLClassExpression> operands = and.getOperands();
                        for(OWLClassExpression op:operands) {
                            if (op.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                                //System.out.println("DisjointUnion-Intersection:" + renderer.render(axiom));
                                outputAxioms.add("DisjointUnion-Intersection:"+renderer.render(axiom));
                                break;
                            }
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_UNION_OF){
                        OWLObjectUnionOf or = (OWLObjectUnionOf) ex;
                        Set<OWLClassExpression> operands = or.getOperands();
                        for(OWLClassExpression op:operands) {
                            if (op.getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                                && op.getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                                //System.out.println("DisjointUnion-Union:"+renderer.render(axiom));
                                outputAxioms.add("DisjointUnion-Union:"+renderer.render(axiom));
                                break;
                            }
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_SOME_VALUES_FROM){
                        OWLObjectSomeValuesFrom some = (OWLObjectSomeValuesFrom) ex;
                        if(some.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                            && some.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF){
                            //System.out.println("DisjointUnion-SomeValuesFrom:" + renderer.render(axiom));
                            outputAxioms.add("DisjointUnion-SomeValuesFrom:"+renderer.render(axiom));
                            break;
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_ALL_VALUES_FROM) {
                        OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) ex;
                        if (all.getFiller().getClassExpressionType()!=ClassExpressionType.OWL_CLASS
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_EXACT_CARDINALITY
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MIN_CARDINALITY
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_MAX_CARDINALITY
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.DATA_HAS_VALUE
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MIN_CARDINALITY
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_MAX_CARDINALITY
                            && all.getFiller().getClassExpressionType()!=ClassExpressionType.OBJECT_ONE_OF) {
                            //System.out.println("DisjointUnion-AllValuesFrom:" + renderer.render(axiom));
                            outputAxioms.add("DisjointUnion-AllValuesFrom:"+renderer.render(axiom));
                            break;
                        }
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_MAX_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_MIN_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_EXACT_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_MAX_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_MIN_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_EXACT_CARDINALITY
                            ||ex.getClassExpressionType()==ClassExpressionType.DATA_HAS_VALUE
                            ||ex.getClassExpressionType()==ClassExpressionType.OBJECT_ONE_OF

                    ) {
                        //do nothing
                    }else if(ex.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF||
                            ex.getClassExpressionType()==ClassExpressionType.OBJECT_HAS_SELF){
                            //System.out.println("DisjointUnion-ObjectHasSelf:" + renderer.render(axiom));
                            outputAxioms.add("DisjointUnion-ObjectHasSelf:"+renderer.render(axiom));
                            break;
                    }else{
                            //System.out.println("DisjointUnion-Other:" + renderer.render(axiom));
                            outputAxioms.add("DisjointUnion-Other:"+renderer.render(axiom));
                            break;
                    }
                }
            }
        }

        Collections.sort(outputAxioms);

        return outputAxioms;
    }

    public ArrayList<String> browseAxiomsOfOntology(OWLOntology onto, boolean showABoxAxioms, boolean showTBoxAxioms, boolean showRBoxAxioms) {

        OWLObjectRenderer renderer = new DLSyntaxObjectRenderer();

        System.out.println(onto.getOntologyID());

        ArrayList<String> output = new ArrayList<String>();

        if(showTBoxAxioms) {
            output.add("======================================");
            output.add("TBox Axioms");
            output.add("======================================");

            for (OWLAxiom axiom : onto.getTBoxAxioms(true)) {
                System.out.println(renderer.render(axiom));
                output.add(renderer.render(axiom));
            }
        }
        if(showABoxAxioms) {
            output.add("======================================");
            output.add("ABox Axioms");
            output.add("======================================");
            for (OWLAxiom axiom : onto.getABoxAxioms(true)) {
                System.out.println(renderer.render(axiom));
                output.add(renderer.render(axiom));
            }
        }
        if(showRBoxAxioms) {
            output.add("======================================");
            output.add("RBox Axioms");
            output.add("======================================");
            for (OWLAxiom axiom : onto.getRBoxAxioms(true)) {
                System.out.println(renderer.render(axiom));
                output.add(renderer.render(axiom));
            }
        }
        return output;
    }

}
