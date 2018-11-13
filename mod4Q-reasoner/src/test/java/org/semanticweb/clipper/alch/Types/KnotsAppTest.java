package org.semanticweb.clipper.alch.Types;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by bato on 8/9/2016.
 */
public class KnotsAppTest {
    @Test
    public void test() {
        KnotsApp
                .main("-v=2 rewrite -o /Users/xiao/Dropbox/krrepos/xiao/drafts/idmus/sa/data/BacteriaGeneInteractions.owl"
                        .split("\\ "));
    }

    @Test
    public void testQuery() {

        KnotsApp
                .main(("-v=8 query -rewriter=old  src/test/resources/university.owl " +
                        "-sparql src/test/resources/university-q1.sparql")
                        .split("\\ "));
    }

    @Test
    public void testRewriteTboxAndQuery() {

        KnotsApp
                .main("-v=8 -rewriter=old rewrite -tq src/test/resources/university.owl -sparql src/test/resources/university-q1.sparql"
                        .split("\\ "));
    }

    @Test
    public void testRewriteAll() {

        KnotsApp
                .main("-v=8 -rewriter=old rewrite -oq src/test/resources/university.owl -sparql src/test/resources/university-q1.sparql"
                        .split("\\ "));
    }

    @Test
    public void testRewriteABoxOnly() {

        KnotsApp
                .main("-v=8 -rewriter=old rewrite -a src/test/resources/university.owl -sparql src/test/resources/university-q1.sparql"
                        .split("\\ "));
    }

    @Test
    public void testRewriteTBoxOnly() {

        KnotsApp
                .main("-v=8 -rewriter=old rewrite -t src/test/resources/university.owl"
                        .split("\\ "));
    }

    @Test
    public void testRewriteOntologyOnly() {

        KnotsApp
                .main("-v=8 -rewriter=old rewrite -o src/test/resources/university.owl"
                        .split("\\ "));
    }


    @Test
    public void testCompletion() {

        KnotsApp.main("-v=8 -rewriter=old rewrite src/test/resources/lubm-ex-20/LUBM-ex-20.owl -o".split("\\ "));
    }

    @Test
    public void testRewriteAboxes() {
        for (int i = 0; i <= 14; i++) {
            for (int j = 0; j < 1; j++) {
                System.out.println(String.format("%d, %d", i, j));
                KnotsApp.main(String.format(
                        "-v=0 -rewriter=old rewrite src/test/resources/lubm-ex-20/University%d_%d.owl -a", j, i).split(
                        "\\ "));
            }
        }
    }

    @Test
    public void testRewriteAbox() {
        long t1 = System.currentTimeMillis();
        int j = 0;
        int i = 0;
        KnotsApp.main(String.format(
                "-v=0 rewrite -rewriter=old src/test/resources/lubm-ex-20/University%d_%d.owl -a", j, i).split("\\ "));
        long t2 = System.currentTimeMillis();
        System.out.println("TIME: " + (t2 - t1));


    }

    @Test
    public void testRewriteNPD() {
        KnotsApp
                .main("-v=8 rewrite -o /Users/xiao/npd-v2.owl -d /Users/xiao/npd-v2.dl"
                        .split(" "));
    }

    public static void main(String[] args) {
        new KnotsAppTest().testQuery();
        //new ClipperAppTest().testRewriteAboxes();
        //new ClipperAppTest().testRewriteTboxAndQuery();
        //new ClipperAppTest().testRewriteAbox();
        //new ClipperAppTest().testRewriteTBoxOnly();
        //new ClipperAppTest().testRewriteOntologyOnly();
        //new ClipperAppTest().testRewriteAll();
    }
}