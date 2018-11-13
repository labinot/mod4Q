package org.semanticweb.clipper.alch.Types;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by bato on 1/24/18.
 */
public class oxfordRepo_tests {

    @Test
    public void test(){

    }


    @Test
    public void Compute_Profiles_forall_ALCH_ontologies_with_Aboxes_with_property_assertions_DL2017() throws Exception {
        String strConfig=" -incremental=0 -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

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

        String path="/home/bato/data/ijcai18/ontologies/oxfordRepo/";


        String strStartFrom="00007.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("loadIntegrated " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knotsNew " + file + strConfig)
                                .split("\\ "));
            }
        }
    }


    @Test
    public void Compute_Profiles_forall_ALCH_ontologies_with_Aboxes_without_property_assertions_DL2017() throws Exception {
        String strConfig=" -incremental=0 -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

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

        String path="/home/bato/data/ijcai18/ontologies/oxfordRepo/";


        String strStartFrom="00609.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("loadIntegrated " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knotsNew " + file + strConfig)
                                .split("\\ "));
            }
        }
    }

    @Test
    public void testRepoOntologiesWithDisjunctionsAndABoxes() throws Exception {
        String strConfig=" -incremental=0 -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        //property not named 351,427,506,507,
        //java heap 698,699,
        //too big to process 701,703,712,754


        ArrayList<String> listOfFilesAboxes =
                new ArrayList<String>(Arrays.asList(
                                                "00001.owl","00002.owl","00003.owl","00007.owl","00008.owl","00009.owl","00010.owl","00014.owl",
                                                "00024.owl","00018.owl","00020.owl","00021.owl","00049.owl","00062.owl","00081.owl","00082.owl",
                                                "00106.owl","00111.owl","00112.owl","00114.owl","00116.owl","00110.owl","00118.owl","00120.owl",
                                                "00163.owl","00167.owl","00172.owl","00176.owl","00202.owl","00209.owl","00210.owl","00275.owl",
                                                "00283.owl","00284.owl","00285.owl","00301.owl","00320.owl","00319.owl","00324.owl","00343.owl",
                                                "00346.owl","00344.owl","00345.owl","00318.owl","00348.owl","00350.owl","00352.owl","00353.owl",
                                                "00354.owl","00355.owl","00362.owl","00363.owl","00395.owl","00406.owl","00407.owl","00410.owl",
                                                "00430.owl","00431.owl","00450.owl","00479.owl","00480.owl","00484.owl","00508.owl","00518.owl",
                                                "00541.owl","00557.owl","00556.owl","00560.owl","00561.owl","00566.owl","00590.owl","00596.owl",
                                                "00597.owl","00609.owl","00610.owl","00612.owl","00613.owl","00636.owl","00660.owl","00765.owl",
                                                "00728.owl"//,"00773.owl","00781.owl","00782.owl","00783.owl",
                                                //"00351.owl","00698.owl","00699.owl","00506.owl","00507.owl","00427.owl", not loadable
                                                //"00701.owl","00703.owl","00712.owl","00754.owl"// to big
                        ));




        //to many guessing >2^16
        //"00319.owl","00320.owl","00344.owl","00345.owl","00346.owl","00781.owl","00782.owl","00783.owl","00350.owl","00114.owl",

        // 2^16 guesses
        // "00014.owl","00024.owl"

        //"00427.owl"- property is not a named property
        //"00701.owl",	"00703.owl",	"00712.owl",	"00728.owl",	"00754.owl" (Phenoscope) --to big to process (703) abnormaly many guessings

        String path="/home/bato/data/ijcai18/ontologies/oxfordRepo/";


        String strStartFrom="00728.owl";

        boolean skip=true;

        for (String fileEntry : listOfFilesAboxes) {

            String file = path+fileEntry;

            if(fileEntry.equals(strStartFrom))
                skip=false;

            if(!skip) {
                KnotsApp
                        .main(String
                                .format("loadIntegrated " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knotsNew " + file + strConfig)
                                .split("\\ "));
            }
        }
    }


    //to proces 285
    @Test
    public void testSpecific() throws Exception {
        String strConfig=" -incremental=0 -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String file="/home/bato/data/ijcai18/ontologies/oxfordRepo/00484.owl";

        KnotsApp
                .main(String
                                .format("loadIntegrated " + file + strConfig)
                                .split("\\ "));

                KnotsApp
                        .main(String
                                .format("knotsNew " + file + strConfig)
                                .split("\\ "));

    }


    //incremental test
    @Test
    public void testIncremental() throws Exception {
        String strConfig=" -incremental=0 -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String file="/home/bato/data/ijcai18/ontologies/oxfordRepo/00395.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + file + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + file + strConfig)
                        .split("\\ "));


        strConfig=" -incremental=1 -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        file="/home/bato/data/ijcai18/ontologies/oxfordRepo/00395_1.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + file + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + file + strConfig)
                        .split("\\ "));


    }



}
