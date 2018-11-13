package org.semanticweb.clipper.alch.Types;

import org.junit.Test;

/**
 * Created by bato on 1/24/18.
 */
public class lubm_tests {

    @Test
    public void lubm10(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String tboxPath = " /home/bato/data/ijcai18/ontologies/lubm/lubm10.owl";
        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/lubm/lubm10_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental + tboxPath + aboxPath + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + strConfig)
                        .split("\\ "));
    }

    @Test
    public void lubm50(){
        //now compute lubm50
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String tboxPath = " /home/bato/data/ijcai18/ontologies/lubm/lubm50.owl";
        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/lubm/lubm50_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental + tboxPath + aboxPath + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + strConfig)
                        .split("\\ "));

    }

    @Test
    public void lubm100(){
        //now compute lubm100
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String tboxPath = " /home/bato/data/ijcai18/ontologies/lubm/lubm100.owl";
        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/lubm/lubm100_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ tboxPath + aboxPath+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + strConfig)
                        .split("\\ "));

    }

}
