package org.semanticweb.clipper.alch.profile;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bato on 1/10/18.
 */
public class ABoxProfileExtractorTest {

    @Test
    public void computeProfiles() throws Exception {
        ABoxProfileExtractor extractor = new ABoxProfileExtractor();
        String abox="/home/bato/data/ijcai18/npd_data.ttl";
        extractor.computeProfiles(abox);
    }

    @Test
    public void npdProfiles() throws Exception {
        ABoxProfileExtractor extractor = new ABoxProfileExtractor();
        String abox="/home/bato/data/ijcai18/npd_data.ttl";
        extractor.computeProfiles(abox);
    }

    @Test
    public void dbpediaProfiles() throws Exception {
        ABoxProfileExtractor extractor = new ABoxProfileExtractor();
        String abox="/home/bato/data/ijcai18/dbpedia_data.ttl";
        extractor.computeProfiles(abox);
    }

    @Test
    public void imdbProfiles() throws Exception {
        ABoxProfileExtractor extractor = new ABoxProfileExtractor();
        String abox="/home/bato/data/ijcai18/imdbd_data.ttl";
        extractor.computeProfiles(abox);
    }

    @Test
    public void writeProfilesToFileNPD() throws Exception {
        ABoxProfileExtractor extractor = new ABoxProfileExtractor();
        String abox="/home/bato/data/ijcai18/npd_data.ttl";
        extractor.loadTriplesInDB(abox);
    }

    @Test
    public void writeProfilesToFileDBPedia() throws Exception {
        ABoxProfileExtractor extractor = new ABoxProfileExtractor();
        String abox="/home/bato/data/ijcai18/dbpedia_data.ttl";
        extractor.loadTriplesInDB(abox);
    }

}