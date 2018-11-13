package org.semanticweb.clipper.alch.Types;
import org.junit.Test;

/**
 * Created by bato on 1/24/18.
 */
public class dbPedia_tests {

    /*MyITSTests*/
    @Test
    public void testDBPedia(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontologyPath = " /home/bato/data/ijcai18/ontologies/dbpedia/dbpedia.owl";
        String dataPath = " -rdf=/home/bato/data/ijcai18/ontologies/dbpedia/dbpedia_data.ttl";

        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ ontologyPath + dataPath+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontologyPath +strConfig)
                        .split("\\ "));
    }

    @Test
    public void testNPD(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontologyPath = " /home/bato/data/ijcai18/ontologies/npd/npd.owl";
        String dataPath = " -rdf=/home/bato/data/ijcai18/ontologies/npd/npd_data.ttl";

        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental+ ontologyPath + dataPath+ strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental+ ontologyPath +strConfig)
                        .split("\\ "));
    }

    @Test
    public void testIMDB(){
        String strConfig = " -jdbcUrl=jdbc:postgresql://localhost/knots -user=postgres";
        String incremental = " -incremental=0";
        String ontologyPath = " /home/bato/data/ijcai18/ontologies/imdb/imdb.owl";
        String dataPath = " -rdf=/home/bato/data/ijcai18/ontologies/imdb/imdb_data.ttl";

        KnotsApp
                .main(String
                        .format("loadSeparated " + incremental + ontologyPath + dataPath + strConfig)
                        .split("\\ "));

        KnotsApp
                .main(String
                        .format("knotsNew " + incremental + ontologyPath + strConfig)
                        .split("\\ "));
    }


}
