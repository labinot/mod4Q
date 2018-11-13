package org.semanticweb.clipper.alch.Types;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;

import static org.junit.Assert.*;

/**
 * Created by bato on 1/22/18.
 */
public class CommandComputeKnotsNewTest {

    @Test
    public void testLoad(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = "-incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_50.owl";

        KnotsApp
                .main(String
                        .format("loadIntegrated " + incremental+ ontology+ strConfig)
                        .split("\\ "));
    }

    @Test
    public void testCompute(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = "-incremental=0";
        String ontology = " /home/bato/data/ijcai18/ontologies/myITS/wien_50.owl";

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontology+strConfig)
                        .split("\\ "));

    }

    @Test
    public void cautiousReasoning() throws InterruptedException {

        long startTime = System.currentTimeMillis();

        //clingo -e cautious --quiet=1 --project --time-limit=600 test.lp

        String command="clingo -e cautious --quiet=1 --project TestData/knots/trans_iq_00007_2.lp";

        try {
            CommandLine commandLine=CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(30);
            int exitValue=executor.execute(commandLine);
            System.out.println(exitValue);

        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime);
*/
    }

}