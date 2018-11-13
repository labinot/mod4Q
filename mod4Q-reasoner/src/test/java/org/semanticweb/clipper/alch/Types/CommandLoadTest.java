package org.semanticweb.clipper.alch.Types;


import org.junit.Test;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import org.postgresql.Driver;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.clipper.alch.knots.AlchKnotComputation1;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;

/**
 * Created by bato on 8/8/2016.
 */
public class CommandLoadTest {

    @Test
    public void testLoad() throws Exception {


        KnotsApp
                .main(String
                        .format("load C:/Users/bato/Desktop/OxfordRepository/Input/isg/ontologies/UID/00009.owl -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres")
                        .split("\\ "));




    }

    @Test
    public void testKnots() throws Exception {


        KnotsApp
                .main(String
                        .format("knots C:/Users/bato/Desktop/OxfordRepository/Input/isg/ontologies/UID/00009.owl -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres")
                        .split("\\ "));
    }

    @Test
    public void testGen() throws Exception {

        KnotsApp
                .main(String
                        .format("gen C:/Users/bato/Desktop/OxfordRepository/Input/isg/ontologies/UID/00007.owl -jdbcUrl=jdbc:postgresql://localhost/types -user=postgres")
                        .split("\\ "));
    }


    @Test
    public void ComputeSpecificFiles() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        /*not loadable:
          00351.owl,00506.owl,00507.owl,00427.owl*/

        /*runtime:
          00018.owl,00021.owl,00284.owl,00020.owl,00014.owl,00350.owl,00557.owl */

        /*ArrayList<String> listOfFiles =
                new ArrayList<String>(Arrays.asList( "00018.owl","00021.owl",//runtime both
                                                     "00284.owl","00020.owl",//runtime both
                                                     "00014.owl","00350.owl",//runtime both
                                                     "00557.owl",            //runtime
                                                     "00172.owl","00479.owl",
                                                     "00324.owl","00275.owl",
                                                     "00480.owl","00518.owl", //518
                                                     "00002.owl","00120.owl",
                                                     "00561.owl","00355.owl",
                                                     "00609.owl","00176.owl",
                                                     "00560.owl","00450.owl",
                                                     "00301.owl","00362.owl",
                                                     "00363.owl","00118.owl",
                                                     "00610.owl","00612.owl",
                                                     "00613.owl","00283.owl",//283
                                                     "00318.owl","00508.owl",
                                                     "00348.owl","00285.owl",//285
                                                     "00590.owl","00001.owl",
                                                     "00406.owl","00556.owl",
                                                     "00163.owl","00167.owl",
                                                     "00353.owl","00352.owl",
                                                     "00354.owl","00636.owl",
                                                     "00407.owl","00024.owl",//24
                                                     "00009.owl","00112.owl",
                                                     "00430.owl","00431.owl",//430,431
                                                     "00008.owl","00010.owl",
                                                     "00410.owl","00783.owl",
                                                     "00320.owl","00346.owl",
                                                     "00343.owl","00319.owl",
                                                     "00344.owl","00345.owl",
                                                     "00781.owl","00782.owl",
                                                     "00007.owl","00773.owl",
                                                     "00596.owl","00597.owl"));*/


        ArrayList<String> listOfFiles =
                new ArrayList<String>(Arrays.asList( "00007.owl",	"00008.owl",	"00009.owl",	"00010.owl",    "00018.owl",	"00020.owl",    "00021.owl",
                                                "00082.owl",    "00110.owl",	"00112.owl",    "00118.owl",	"00120.owl",    "00283.owl",    "00284.owl",
                                                "00324.owl",	"00343.owl",    /*"00636.owl",*/    "00773.owl"));



        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( "00007","00008","00009","00010","00014","00024","00018",
                                                     "00020","00021","00082","00112","00114","00110","00118",
                                                     "00120","00167","00283","00284","00320","00319","00324",
                                                     "00343","00346","00344","00345","00350","00427","00557",
                                                     "00590","00636","00701","00703","00712","00754","00728",
                                                     "00773","00781","00782","00783"
                        ));





                //to many guessing >2^16
                //"00557.owl","00319.owl","00320.owl","00344.owl","00345.owl","00346.owl","00781.owl","00782.owl","00783.owl","00350.owl","00114.owl",

                // 2^16 guesses
                // "00014.owl","00024.owl","00590.owl"

                //"00167.owl"- duplicate key violation
                //"00427.owl"- property is not a named property
                //"00701.owl",	"00703.owl",	"00712.owl",	"00728.owl",	"00754.owl" (Phenoscope) --to big to process (703) abnormaly many guessings

                String path="/media/bato/88C6269AC6268890/Users/bato/Desktop/OxfordRepository/Input/isg/ontologies/UID/";


        String strStartFrom="00007.owl";

        boolean skip=true;

        for (String fileEntry : listOfFiles) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("load " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knots " + file + strConfig)
                                .split("\\ "));
            }
        }
    }

    @Test
    public void Compute_Profiles_forall_Files_with_Aboxes() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        /*not loadable:
          00351.owl,00506.owl,00507.owl,00427.owl*/

        /*runtime:
          00018.owl,00021.owl,00284.owl,00020.owl,00014.owl,00350.owl,00557.owl */

        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( "00007.owl","00008.owl","00009.owl","00010.owl","00014.owl","00024.owl","00018.owl","00020.owl",
                        "00021.owl","00078.owl","00079.owl","00080.owl","00082.owl","00112.owl","00114.owl","00110.owl",
                        "00118.owl","00120.owl","00140.owl","00142.owl","00143.owl","00144.owl","00165.owl","00167.owl",
                        "00168.owl","00232.owl","00256.owl","00283.owl","00284.owl","00290.owl","00320.owl","00319.owl",
                        "00324.owl","00343.owl","00346.owl","00344.owl","00345.owl","00347.owl","00350.owl",/*"00427.owl",*/
                        "00557.owl","00590.owl","00636.owl",/*"00700.owl","00701.owl","00702.owl","00703.owl","00704.owl",*/
                                                /*"00705.owl",*/"00706.owl",/*"00707.owl",*/"00708.owl","00709.owl","00710.owl","00711.owl","00712.owl",
                        "00754.owl","00755.owl","00756.owl","00713.owl","00714.owl","00715.owl","00716.owl","00717.owl",
                        "00718.owl","00720.owl","00719.owl","00721.owl","00722.owl","00723.owl","00724.owl","00725.owl",
                        "00726.owl","00727.owl","00728.owl","00729.owl","00730.owl","00731.owl","00732.owl","00733.owl",
                        "00734.owl","00735.owl","00736.owl","00737.owl","00738.owl","00739.owl","00740.owl","00741.owl",
                        "00742.owl","00743.owl","00744.owl","00745.owl","00746.owl","00771.owl","00747.owl","00748.owl",
                        "00749.owl","00750.owl","00751.owl","00752.owl","00753.owl","00773.owl","00775.owl","00776.owl",
                        "00779.owl","00780.owl","00781.owl","00782.owl","00783.owl")
                );


        //706, 708 ontology took to much time to load...

        //ArrayList<String> listOfFilesAboxes =
        //        new ArrayList<String>(Arrays.asList( "00014.owl"));

        //to many guessing >2^16
        //"00557.owl","00319.owl","00320.owl","00344.owl","00345.owl","00346.owl","00781.owl","00782.owl","00783.owl","00350.owl","00114.owl",

        // 2^16 guesses
        // "00014.owl","00024.owl","00590.owl"

        //"00427.owl"- property is not a named property
        //"00701.owl",	"00703.owl",	"00712.owl",	"00728.owl",	"00754.owl" (Phenoscope) --to big to process (703) abnormaly many guessings

        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00709.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("load " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("profiles " + file + strConfig)
                                .split("\\ "));
            }
        }
    }

    @Test
    public void Compute_Profiles_forall_ALCH_ontologies_with_Aboxes_with_property_assertions_DL2017() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        /*557, 590 -unsat*/

        /*not loadable:
          00351.owl,00506.owl,00507.owl,00427.owl*/

        /*runtime:
          00018.owl,00021.owl,00284.owl,00020.owl,00014.owl,00350.owl,00557.owl */

        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( "00007.owl","00008.owl","00009.owl","00010.owl","00014.owl","00024.owl","00018.owl",
                                                "00020.owl","00021.owl","00082.owl","00112.owl","00114.owl","00110.owl","00118.owl",
                                                "00120.owl","00167.owl","00283.owl","00284.owl","00320.owl","00319.owl","00324.owl",
                                                "00343.owl","00346.owl","00344.owl","00345.owl","00350.owl",/*"00427.owl",*/"00636.owl",
                                                /*"00701.owl","00703.owl","00712.owl","00754.owl","00728.owl",*/
                                                "00773.owl","00781.owl","00782.owl","00783.owl"
                ));

        //ArrayList<String> listOfFilesAboxes =
        //        new ArrayList<String>(Arrays.asList( "00014.owl"));

        //to many guessing >2^16
        //"00319.owl","00320.owl","00344.owl","00345.owl","00346.owl","00781.owl","00782.owl","00783.owl","00350.owl","00114.owl",

        // 2^16 guesses
        // "00014.owl","00024.owl"

        //"00427.owl"- property is not a named property
        //"00701.owl",	"00703.owl",	"00712.owl",	"00728.owl",	"00754.owl" (Phenoscope) --to big to process (703) abnormaly many guessings

        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00007.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("load " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knots " + file + strConfig)
                                .split("\\ "));
            }
        }
    }

    @Test
    public void Compute_Profiles_forall_ALCH_ontologies_with_Aboxes_without_property_assertions_DL2017() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        /*not loadable:
          00351.owl,00427.owl*/

        /*runtime:
          00018.owl,00021.owl,00284.owl,00020.owl,00014.owl,00350.owl,00557.owl */

        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList(
                        "00001.owl", "00002.owl","00003.owl","00049.owl","00062.owl","00081.owl", "00106.owl", "00111.owl",
                        "00116.owl", "00163.owl","00172.owl","00176.owl","00202.owl","00209.owl", "00210.owl", "00275.owl",
                        "00285.owl", "00301.owl","00318.owl","00348.owl",/*"00351.owl",*/"00352.owl", "00353.owl",  "00354.owl",
                        "00355.owl", "00362.owl","00363.owl","00395.owl","00406.owl","00407.owl","00410.owl", "00430.owl",
                        "00431.owl", "00450.owl","00479.owl","00480.owl","00484.owl",/*"00506.owl","00507.owl",*/ "00508.owl",
                        "00518.owl", "00541.owl","00556.owl","00560.owl","00561.owl","00566.owl", "00596.owl",    "00597.owl",
                        "00609.owl", "00610.owl","00612.owl","00613.owl","00660.owl",/*"00698.owl","00699.owl",*/ "00765.owl"
                ));

        //ArrayList<String> listOfFilesAboxes =
        //        new ArrayList<String>(Arrays.asList( "00014.owl"));

        //to many guessing >2^16
        //"00319.owl","00320.owl","00344.owl","00345.owl","00346.owl","00781.owl","00782.owl","00783.owl","00350.owl","00114.owl",

        // 2^16 guesses
        // "00014.owl","00024.owl"

        //"00427.owl"- property is not a named property
        //"00701.owl",	"00703.owl",	"00712.owl",	"00728.owl",	"00754.owl" (Phenoscope) --to big to process (703) abnormaly many guessings

        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00596.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("load " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knots " + file + strConfig)
                                .split("\\ "));
            }
        }
    }


    @Test
    public void testSatisfiability() throws Exception {


        //566,

        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( /*"00007.owl", "00008.owl","00009.owl","00010.owl","00014.owl","00024.owl","00018.owl",
                                                "00020.owl", "00021.owl","00082.owl","00112.owl","00114.owl","00110.owl","00118.owl",
                                                "00120.owl", "00167.owl","00283.owl","00284.owl","00320.owl","00319.owl","00324.owl",
                                                "00343.owl", "00346.owl","00344.owl","00345.owl","00350.owl",*//*"00427.owl",*//*"00557.owl",
                                                "00590.owl", "00636.owl",*//*"00701.owl","00703.owl","00712.owl","00754.owl","00728.owl",*//*
                                                "00773.owl", "00781.owl","00782.owl","00783.owl",
                                                "00001.owl", "00002.owl","00003.owl","00049.owl","00062.owl","00081.owl",                   "00106.owl",        "00111.owl",
                                                "00116.owl", "00163.owl","00172.owl","00176.owl","00202.owl","00209.owl",                   "00210.owl",        "00275.owl",
                                                "00285.owl", "00301.owl","00318.owl","00348.owl",*//*"00351.owl",*//*"00352.owl",               "00353.owl",        "00354.owl",
                                                "00355.owl", "00362.owl","00363.owl","00395.owl","00406.owl","00407.owl","00410.owl",        "00430.owl",
                                                "00431.owl", "00450.owl","00479.owl","00480.owl","00484.owl",*//*"00506.owl","00507.owl",*//*    "00508.owl",
                                                "00518.owl", "00541.owl","00556.owl","00560.owl","00561.owl","00566.owl", "00596.owl",        "00597.owl",
                                                "00609.owl", "00610.owl","00612.owl","00613.owl","00660.owl",*//*"00698.owl","00699.owl",*//*      "00765.owl"*/
                                                "00506.owl",
                                                "00507.owl",
                                                "00351.owl",
                                                "00427.owl"/*,
                                                "00701.owl",
                                                "00703.owl",
                                                "00712.owl",
                                                "00754.owl",
                                                "00728.owl",
                                                "00566.owl",
                                                "00698.owl",
                                                "00699.owl",
                                                "00765.owl"*/));





        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00506.owl";

        boolean skip=true;
        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();


        for (String fileEntry : listOfFilesAboxes) {
            OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
            OWLOntology ontology=null;
            String ontologyFilePath = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                File ontologyFile = new File(ontologyFilePath);
                String ontologyFilename = ontologyFile.getName().substring(0,ontologyFile.getName().lastIndexOf("."));

                ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
                ontology = normalizer.normalize(ontology);
                Reasoner hermit=new Reasoner(ontology);

                // Finally, we output whether the ontology is consistent.
                System.out.print(ontologyFilename+": ");
                System.out.println(hermit.isConsistent());

            }
        }
    }


    @Test
    public void Evaluate_IQ_forall_ALCH_ontologies_with_Aboxes_DL2017() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        /*not loadable:
          00351.owl,00506.owl,00507.owl,00427.owl*/

        /*runtime:
          00018.owl,00021.owl,00284.owl,00020.owl,00014.owl,00350.owl,00557.owl */

        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( "00021.owl",                                                "00018.owl",
                                                "00118.owl",                                                "00283.owl",
                                                "00120.owl",                                                "00007.owl",
                                                "00009.owl",                                                "00010.owl",
                                                "00112.owl",                                                "00008.owl",
                                                "00020.owl",                                                "00284.owl",
                                                //"00427.owl"--could not process
                                                "00636.owl",                                                "00082.owl",
                                                "00110.owl",                                                "00324.owl",
                                                "00343.owl",                                                "00773.owl",
                                                "00014.owl",                                                "00024.owl",
                                                "00590.owl")
                                    );

        //ArrayList<String> listOfFilesAboxes =
        //        new ArrayList<String>(Arrays.asList( "00014.owl"));

        //to many guessing >2^16
        //"00557.owl","00319.owl","00320.owl","00344.owl","00345.owl","00346.owl","00781.owl","00782.owl","00783.owl","00350.owl","00114.owl",

        // 2^16 guesses
        // "00014.owl","00024.owl","00590.owl"

        //"00167.owl"- duplicate key violation
        //"00427.owl"- property is not a named property
        //"00701.owl",	"00703.owl",	"00712.owl",	"00728.owl",	"00754.owl" (Phenoscope) --to big to process (703) abnormaly many guessings

        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00014.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("load " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knots " + file + strConfig)
                                .split("\\ "));
            }
        }
    }


    @Test
    public void test00001() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai18/ontologies/oxfordRepo/00395.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void test00703_profiles() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/dl2017/ontologies/00001.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("profiles " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void delete() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai18/ontologies/myITS/wien_50.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }



    @Test
    public void testNPD_profiles() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai18/npd_data.ttl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("profiles " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testSaveNormalisedOntology() throws OWLOntologyCreationException, OWLOntologyStorageException {
        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList( "00001.owl","00002.owl",
                        "00007.owl","00008.owl","00009.owl","00010.owl","00014.owl","00024.owl","00018.owl",
                        "00020.owl","00021.owl","00082.owl","00112.owl","00114.owl","00110.owl","00118.owl",
                        "00120.owl","00167.owl","00283.owl","00284.owl","00320.owl","00319.owl","00324.owl",
                        "00343.owl","00346.owl","00344.owl","00345.owl","00350.owl",/*"00427.owl",*/"00636.owl",
                                                /*"00701.owl","00703.owl","00712.owl","00754.owl","00728.owl",*/
                        "00773.owl","00781.owl","00782.owl","00783.owl"
                ));

        String path="/home/bato/data/dl2017/ontologies/";


        String strStartFrom="00001.owl";


        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();
        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String filePath = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                normalizeAndSave(filePath,path+"/normalize/alch_nf_"+fileEntry);
            }
        }

    }

    @Test
    public void testNormalizeAndSaveIndividualOnotlogies() throws OWLOntologyCreationException, OWLOntologyStorageException {
        String inFilepath="/home/bato/data/dl2017/ontologies/00004.owl";
        String outFilepath="/home/bato/data/ijcai18/ontologies/oxford/alch_nf_wien_00004.owl";
        normalizeAndSave(inFilepath,outFilepath);
    }

    private void normalizeAndSave(String inFilePath, String outFilePath)
            throws OWLOntologyCreationException, OWLOntologyStorageException {

        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();

        //load the onotlogy
        File ontologyFile = new File(inFilePath);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);

        //normalize it
        ontology = normalizer.normalize(ontology);

        //save it
        OWLOntologyFormat format = manager.getOntologyFormat(ontology);//take the format of the ontology
        File normalizedOntologyFile = new File(outFilePath);
        manager.saveOntology(ontology, format, IRI.create(normalizedOntologyFile.toURI()));

    }
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
    public void testCity() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/Dropbox/Research/data/ijcai2017/city/vienna.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }


    @Test
    public void testCityBM1() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/Dropbox/Research/data/ijcai2017/city/bm1_merge.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testCityALCH() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_abox_tbox_functional.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }


    @Test
    public void testCityWienClose2Metro250m() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_250_close_2_metro.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }


    @Test
    public void testloadOntology() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/ontology/MyITS_ALCH_manchester.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }


    @Test
    public void testCityWien50meters() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_50.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }


    @Test
    public void testCityWien100meters() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_100.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testCityWien150meters() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_150.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testCityWien200meters() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_200.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testCityWien250meters() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai2017/city/benchmarks/instances/test/wien_250.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }


    /*IJCAI18-
    *
    *
    * */

    @Test
    public void testWienNext50m() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai18/ontologies/wien_250.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testNPD_ijcai() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai18/npd/npd_data.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    @Test
    public void testLUBM() throws Exception {
        String strConfig=" -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String path="/home/bato/data/ijcai18/ontologies/lubm/lubm10_merged.owl";

        KnotsApp
                .main(String
                        .format("load " + path + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knots " + path + strConfig)
                        .split("\\ "));
    }

    /*Retreive instances with Hermit*/
    @Test
    public void testHermiT_ClassInstances_ijcai18() throws OWLOntologyCreationException {

        ArrayList<String> listOfFiles =
                new ArrayList<String>(Arrays.asList(
//                        "/home/bato/data/ijcai18/ontologies/lubm/lubm10_merged.owl",
//                        "/home/bato/data/ijcai18/ontologies/lubm/lubm50_merged.owl"
//                        "/home/bato/data/ijcai18/ontologies/lubm/lubm100_merged.owl"
//                        "/home/bato/data/ijcai18/ontologies/lubm/lubm500_merged.owl",
                        "/home/bato/data/ijcai18/ontologies/myITS/wien_50.owl"
//                        "/home/bato/data/ijcai18/ontologies/myITS/wien_100.owl",
//                        "/home/bato/data/ijcai18/ontologies/myITS/wien_150.owl",
//                        "/home/bato/data/ijcai18/ontologies/myITS/wien_200.owl",
//                        "/home/bato/data/ijcai18/ontologies/myITS/wien_250.owl"
//                        "/home/bato/data/ijcai18/ontologies/npd/npd_merged.owl"
                ));

        for(String filePath:listOfFiles) {
            testHermitClassInstances(filePath,true);
        }
    }


    @Test
    public void testOWLAPILoad() throws OWLOntologyCreationException {
        String path="/home/bato/data/ijcai18/ontologies/lubm/lubm100_merged.owl";
        File ontologyFile = new File(path);
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
        System.out.println("Laoding finished successfully");
        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();
        normalizer.normalize(ontology);
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
}