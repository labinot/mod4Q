package org.semanticweb.clipper.alch.Types;

import org.junit.Test;

/**
 * Created by bato on 1/25/18.
 */
public class npd_tests {
    @Test
    public void npdFuppllRun(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String tboxPath = " /home/bato/data/ijcai18/ontologies/npd/npd.owl";
        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/npd/npd_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental + tboxPath + aboxPath + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + strConfig)
                        .split("\\ "));
    }
}
