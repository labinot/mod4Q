package org.semanticweb.clipper.alch.Types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bato on 1/14/18.
 */
public class CommandLoadIntegratedTest {

    @Test
    public void testLoad() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String ontology = "/home/bato/data/ijcai18/ontologies/myITS/wien_50.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + ontology + strConfig)
                        .split("\\ "));

    }

    /*===============================================================================
    * IJCAI tests
    * ===============================================================================*/
    @Test
    public void testMyITSCleanLoad() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String incremental = " -incremental=0";

        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien50.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));


        //now compute for MyITS150
        ontology = "/home/bato/data/ijcai18/ontologies/myITS/wien150.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));


        //now compute for MyITS250
        ontology = "/home/bato/data/ijcai18/ontologies/myITS/wien250.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));

    }

    /*have to think it through how to downscale the ABox to generate
    * wien501 (this should be the smallest)
    * wien502 (this should contain disjunction)
    * wien503 (this should contain some other delta concept)*/
    @Test
    public void testMyITSIncremental() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String incremental = " -incremental=1";

        String ontology = "/home/bato/data/ijcai18/ontologies/myITS/wien50.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));
    }


}