package org.semanticweb.clipper.alch.Types;

import org.junit.Test;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by bato on 12/21/2016.
 */
public class hermiTAnswersTest {

    @Test
    public void testHermiT_ClassInstances() throws OWLOntologyCreationException {
        /*not loadable:
          00351.owl,00506.owl,00507.owl,00427.owl*/

        /*runtime:
          00018.owl,00021.owl,00284.owl,00020.owl,00014.owl,00350.owl,00557.owl */

        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( //"00001.owl","00002.owl","00014.owl","00024.owl","00018.owl","00020.owl","00021.owl","00781.owl","00782.owl","00783.owl")
                        "00001.owl","00014.owl","00018.owl","00021.owl"
                        //"00007.owl","00008.owl","00009.owl","00010.owl","00014.owl","00024.owl","00018.owl",
                        //"00020.owl","00021.owl","00082.owl","00112.owl","00114.owl","00110.owl","00118.owl",
                        //"00120.owl","00167.owl","00283.owl","00284.owl","00320.owl","00319.owl","00324.owl",
                        //"00343.owl","00346.owl","00344.owl","00345.owl","00350.owl",/*"00427.owl",*/"00636.owl",
                                                /*"00701.owl","00703.owl","00712.owl","00754.owl","00728.owl",*/
                        //"00773.owl","00781.owl","00782.owl","00783.owl"
                ));

        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00001.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                testHermitClassInstances(file,true);
            }
        }
    }

    @Test
    public void testNPD_HermiT_ClassInstances() throws OWLOntologyCreationException {

        String filename="/home/bato/data/ijcai18/ontologies/npd/npd_merged.owl";

        testHermitClassInstances(filename,true);
    }

    @Test
    public void testLubm10_HermiT_ClassInstances() throws OWLOntologyCreationException {

        String filename="/home/bato/data/ijcai18/ontologies/lubm/lubm10_merged.owl";

        testHermitClassInstances(filename,true);
    }

    /*without retreival, only realisation*/
    private void testHermitClassInstances(String path,Boolean normalize) throws OWLOntologyCreationException {
        long t_start;
        long t_end;
        long t_duration_SAT;
        long t_duration_CI;
        boolean satisfiable=false;

        File ontologyFile = new File(path);
        String ontologyFilename = ontologyFile.getName().substring(0,ontologyFile.getName().lastIndexOf("."));
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();

        if(normalize)
            ontology = normalizer.normalize(ontology);

        Reasoner hermit=new Reasoner(ontology);
        System.out.println("OntologyFileName:"+ontologyFilename);

        // Finally, we output whether the ontology is consistent.

/*
        t_start=System.currentTimeMillis();
        satisfiable=hermit.isConsistent();
        hermit.classifyClasses();

        t_end=System.currentTimeMillis();
        t_duration_SAT=t_end-t_start;

        System.out.println("Satisfiable:"+satisfiable);
        System.out.println("SAT Reasoning Time(ms):"+t_duration_SAT+"\t\t Time(s):"+t_duration_SAT/1000);
*/
        t_start=System.currentTimeMillis();

        hermit.flush();

        System.out.println("Starting classification...");

        for(OWLClass c:ontology.getClassesInSignature(true)){

            System.out.println("Class"+c.toString()+":");

            NodeSet<OWLNamedIndividual> w = hermit.getInstances(c, true);

            //for(Node i:w){
            //    System.out.println(i.toString());
            //}
            //System.out.println("----------------------------------------------");

        }
        t_end=System.currentTimeMillis();
        t_duration_CI=t_end-t_start;

        System.out.println("Class Instances(ms):"+t_duration_CI+"\t\t Time(s):"+t_duration_CI/1000);
        System.out.println(">>>");
    }










    /*OLD*/
    @Test
    public void testInsertAnswersFromHermiT4SpecificFiles() throws Exception {

        ArrayList<String> listOfFiles =
                new ArrayList<String>(Arrays.asList("00007.owl", "00008.owl", "00009.owl", "00010.owl", "00018.owl", "00020.owl", "00021.owl",
                        "00082.owl", "00110.owl", "00112.owl", "00118.owl", "00120.owl", "00283.owl", "00284.owl",
                        "00324.owl", "00343.owl", /*"00636.owl",*/ "00773.owl"));


        String path="C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/";

        String strStartFrom="00009.owl";

        boolean skip=true;

        for (String fileEntry : listOfFiles) {

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {

                File file = new File(path + fileEntry);
                hermiTAnswers ans = new hermiTAnswers();
                ans.loadOntology(file);
                ans.printForEachClass();
                ans.insertAnswersFromHermiT();
            }
        }
    }

    @Test
    public void testInsertAnswersFromHermiT4SpecificFile() throws Exception {

        String path="C:/Users/bato/Desktop/OxfordRepository/input/isg/ontologies/UID/";

        String fileEntry="00009.owl";

                File file = new File(path + fileEntry);
                hermiTAnswers ans = new hermiTAnswers();
                ans.loadOntology(file);
                ans.printForEachClass();
                ans.insertAnswersFromHermiT();
    }
}