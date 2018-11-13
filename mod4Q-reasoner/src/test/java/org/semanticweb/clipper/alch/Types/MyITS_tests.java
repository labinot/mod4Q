package org.semanticweb.clipper.alch.Types;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by bato on 1/24/18.
 */
public class MyITS_tests {

    /*MyITSTests*/
    @Test
    public void testMyITS_50(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_50.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));


        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));

    }

    @Test
    public void testMyITS_100(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_100.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));
    }

    @Test
    public void testMyITS_150(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_150.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));
    }

    @Test
    public void testMyITS_200(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_200.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));
    }

    @Test
    public void testMyITS_250(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_250.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));
    }

    @Test
    public void testMyITS_50_encoding_runtime() throws InterruptedException {

        long startTime = System.currentTimeMillis();
        String command="clingo -e cautious --quiet=1 TestData/knots/ijcai18/trans_iq_wien_50.lp";

        try {
            CommandLine commandLine=CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(30);
            int exitValue=executor.execute(commandLine);
            System.out.println(exitValue);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("wien_50 encoding runtime:"+(System.currentTimeMillis()-startTime)/1000);
    }
}
