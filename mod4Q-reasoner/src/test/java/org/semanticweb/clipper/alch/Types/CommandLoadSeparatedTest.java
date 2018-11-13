package org.semanticweb.clipper.alch.Types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bato on 1/14/18.
 */
public class CommandLoadSeparatedTest {

    @Test
    public void test() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String incremental = " -incremental=0";

        String tboxPath = "/home/bato/data/ijcai18/ontologies/lubm/lubm.owl";

        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/lubm/lubm50_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ tboxPath + aboxPath+ strConfig)
                        .split("\\ "));

    }

    /*===============================================================================
    * IJCAI tests
    * ===============================================================================*/

    @Test
    public void DBPediaTest() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String incremental = " -incremental=0";

        String tboxPath = "/home/bato/data/ijcai18/ontologies/dbpedia/dbpedia.owl";

        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/dbpedia/dbpedia_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ tboxPath + aboxPath+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + aboxPath+strConfig)
                        .split("\\ "));

    }

    @Test
    public void npdTest() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String incremental = " -incremental=0";

        String tboxPath = "/home/bato/data/ijcai18/ontologies/npd/npd.owl";

        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/npd/npd_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ tboxPath + aboxPath+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + aboxPath+strConfig)
                        .split("\\ "));

    }

    @Test
    public void imdbTest() {
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";

        String incremental = " -incremental=0";

        String tboxPath = "/home/bato/data/ijcai18/ontologies/imdb/imdb.owl";

        String aboxPath = " -rdf=/home/bato/data/ijcai18/ontologies/imdb/imdb_data.ttl";


        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ tboxPath + aboxPath+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + tboxPath + aboxPath+strConfig)
                        .split("\\ "));

    }


}