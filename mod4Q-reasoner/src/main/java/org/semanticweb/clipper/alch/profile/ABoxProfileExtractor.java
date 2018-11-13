// JRDFox(c) Copyright University of Oxford, 2013. All Rights Reserved.

package org.semanticweb.clipper.alch.profile;

import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.semanticweb.clipper.alch.Types.DBCommandBase;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import uk.ac.ox.cs.JRDFox.JRDFoxException;
import uk.ac.ox.cs.JRDFox.Prefixes;
import uk.ac.ox.cs.JRDFox.store.DataStore;
import uk.ac.ox.cs.JRDFox.store.Resource;
import uk.ac.ox.cs.JRDFox.store.TupleIterator;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import java.sql.*;


public class ABoxProfileExtractor {
    private Connection conn;

    public static Collection<Set<Resource>> computeProfiles(String tboxFile, String aboxFile) {

        final OWLOntology ontology;
        try {
            ontology = OWLManager.createOWLOntologyManager().loadOntologyFromOntologyDocument(new File(tboxFile));
        } catch (OWLOntologyCreationException e) {
            throw new RuntimeException(e);
        }

        // We now create the data store. RDFox supports different types of stores described in DataStore.StoreType,
        // each with the option of native equality reasoning.
        DataStore store = null;
        try {
            store = new DataStore(DataStore.StoreType.ParallelSimpleNN);
            System.out.println("Setting the number of threads...");
            store.setNumberOfThreads(2);

            System.out.println("Importing RDF data...");
            store.importFiles(new File[]{new File(aboxFile)});
            System.out.println("Number of tuples after import: " + store.getTriplesCount());
            return compute(ontology, store);

        } catch (JRDFoxException e) {
            throw new RuntimeException(e);
        } finally {
            // When no longer needed, the data store should be disposed so that all related resources are released.
            store.dispose();
        }
    }

    /*computes profiles found in the database without reasoning*/
    public static Collection<Set<Resource>> computeProfiles(String aboxFile) {

        // We now create the data store. RDFox supports different types of stores described in DataStore.StoreType,
        // each with the option of native equality reasoning.
        DataStore store = null;
        try {
            store = new DataStore(DataStore.StoreType.ParallelSimpleNN);
            System.out.println("Setting the number of threads...");
            store.setNumberOfThreads(2);

            System.out.println("Importing RDF data...");
            store.importFiles(new File[]{new File(aboxFile)});
            System.out.println("Number of tuples after import: " + store.getTriplesCount());
            return compute(store);

        } catch (JRDFoxException e) {
            throw new RuntimeException(e);
        } finally {
            // When no longer needed, the data store should be disposed so that all related resources are released.
            store.dispose();
        }
    }



    public static Collection<Set<Resource>> computeProfiles(OWLOntology tbox, OWLOntology abox) {
        // We now create the data store. RDFox supports different types of stores described in DataStore.StoreType,
        // each with the option of native equality reasoning.
        DataStore store = null;
        try {
            store = new DataStore(DataStore.StoreType.ParallelSimpleNN);
            System.out.println("Setting the number of threads...");
            store.setNumberOfThreads(2);

            System.out.println("Importing RDF data...");
            final StringDocumentTarget target = new StringDocumentTarget();
            OWLManager.createOWLOntologyManager().saveOntology(abox, new TurtleOntologyFormat(), target);
            final String text = target.toString();
            store.importText(text);

            System.out.println("Number of tuples after import: " + store.getTriplesCount());
            return compute(tbox, store);

        } catch (JRDFoxException | OWLOntologyStorageException e) {
            throw new RuntimeException(e);
        } finally {
            // When no longer needed, the data store should be disposed so that all related resources are released.
            store.dispose();
        }
    }

    private static Collection<Set<Resource>> compute(OWLOntology ontology, DataStore store) throws JRDFoxException {
        Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;
        System.out.println("Retrieving all properties before materialisation.");

        TupleIterator tupleIterator = store.compileQuery("SELECT DISTINCT ?x ?z WHERE{ ?x a ?z }", prefixes);

        try {
            System.out.println("Adding the ontology to the store...");
            store.importOntology(ontology);
            store.applyReasoning();
            Map<Resource, Set<Resource>> profiles = new HashMap<>();

            int numberOfRows = 0;
            System.out.println();
            System.out.println("=======================================================================================");
            int arity = tupleIterator.getArity();
            // We iterate trough the result tuples
            for (long multiplicity = tupleIterator.open(); multiplicity != 0; multiplicity = tupleIterator.advance()) {
                // We iterate trough the terms of each tuple
                final Resource individual = tupleIterator.getResource(0);
                final Resource concept = tupleIterator.getResource(1);
                if (!profiles.containsKey(individual)) {
                    profiles.put(individual, new HashSet<>());
                }

                profiles.get(individual).add(concept);

            }
            System.out.println("---------------------------------------------------------------------------------------");

            //profiles.forEach((k, v) -> System.out.println(k + " -> " + v));

            System.out.println("=======================================================================================");
            System.out.println();

            final HashSet<Set<Resource>> sets = new HashSet<>(profiles.values());

            System.out.println("=======================================================================================");

            System.out.println("All profiles");

            final Set<Set<Resource>> finalProfile =
                    sets.parallelStream()
                            .filter(s -> sets.stream()
                                    .noneMatch(t -> t.containsAll(s) && !t.equals(s)) // largest
                            )
                            .collect(toSet());

            finalProfile.forEach(System.out::println);

            return finalProfile;
        } finally {
            // When no longer needed, the iterator should be disposed so that all related resources are released.
            tupleIterator.dispose();
        }
    }

    /*computes profiles found in the database without reasoning*/
    public void loadTriplesInDB(String aboxFile) throws SQLException {
        conn = createConnection();
        String sql="";
        PreparedStatement insertCA = conn.prepareStatement("insert into concept_assertions values (?,?)");
        PreparedStatement insertInPA = conn.prepareStatement("insert into in_properties values (?,?)");
        PreparedStatement insertOutPA = conn.prepareStatement("insert into out_properties values (?,?)");
        Statement stmt = null;
		try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        //clean DB for loading
        prepareDB(stmt);


        // We now create the data store. RDFox supports different types of stores described in DataStore.StoreType,
        // each with the option of native equality reasoning.
        DataStore store = null;
        try {
            store = new DataStore(DataStore.StoreType.ParallelSimpleNN);
            System.out.println("Setting the number of threads...");
            store.setNumberOfThreads(2);

            System.out.println("Importing RDF data...");
            store.importFiles(new File[]{new File(aboxFile)});
            System.out.println("Number of tuples after import: " + store.getTriplesCount());
            //return compute(store);

            Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;

            System.out.println("Retrieving all properties before materialisation.");

            TupleIterator classIterator = store.compileQuery("SELECT DISTINCT ?x ?y WHERE { ?x a ?y }", prefixes);


            //statements processed since the last batch execution
            int cnt=0;
            //number of batch statements to be processed
            int batchSize=10000;

            for (long multiplicity = classIterator.open(); multiplicity != 0; multiplicity = classIterator.advance()) {
                cnt++;
                // We iterate trough the terms of each tuple
                final Resource subject = classIterator.getResource(0);
                final Resource predicate = classIterator.getResource(1);
                //final Resource object = tupleIterator.getResource(2);
                insertCA.setString(1,subject.toString());
                insertCA.setString(2,predicate.toString());
                insertCA.addBatch();

                //execute the statements and reset the counter
                if (cnt % batchSize == 0) { insertCA.executeBatch(); cnt=0;}
            }
            //execute the leftover statements
            insertCA.executeBatch();


            TupleIterator inPropertyIterator =
                    store.compileQuery("SELECT DISTINCT ?x ?property WHERE { \n" +
                                                                        "    ?x a ?y .\n" +
                                                                        "    ?x ?property ?value .\n" +
                                                                        "}", prefixes);

            for (long multiplicity = inPropertyIterator.open(); multiplicity != 0; multiplicity = inPropertyIterator.advance()) {
                cnt++;
                // We iterate trough the terms of each tuple
                final Resource subject = inPropertyIterator.getResource(0);
                final Resource predicate = inPropertyIterator.getResource(1);
                //final Resource object = tupleIterator.getResource(2);
                insertInPA.setString(1,subject.toString());
                insertInPA.setString(2,predicate.toString());
                insertInPA.addBatch();

                //execute the statements and reset the counter
                if (cnt % batchSize == 0) { insertInPA.executeBatch(); cnt=0;}
            }
            //execute the leftover statements
            insertInPA.executeBatch();


            TupleIterator outPropertyIterator =
                    store.compileQuery("SELECT DISTINCT ?x ?property WHERE { \n" +
                                                                        "    ?x a ?y .\n" +
                                                                        "    ?value ?property ?x .\n" +
                                                                        "}", prefixes);

            for (long multiplicity = outPropertyIterator.open(); multiplicity != 0; multiplicity = outPropertyIterator.advance()) {
                cnt++;
                // We iterate trough the terms of each tuple
                final Resource subject = outPropertyIterator.getResource(0);
                final Resource predicate = outPropertyIterator.getResource(1);
                //final Resource object = tupleIterator.getResource(2);
                insertOutPA.setString(1,subject.toString());
                insertOutPA.setString(2,predicate.toString());
                insertOutPA.addBatch();

                //execute the statements and reset the counter
                if (cnt % batchSize == 0) { insertOutPA.executeBatch(); cnt=0;}
            }
            //execute the leftover statements
            insertOutPA.executeBatch();

        } catch (JRDFoxException e) {
            throw new RuntimeException(e);
        } finally {
            // When no longer needed, the data store should be disposed so that all related resources are released.
            store.dispose();
        }

        //gather statistics
        gatherStatistics(stmt);

    }

    private static Collection<Set<Resource>> compute(DataStore store) throws JRDFoxException {
        Prefixes prefixes = Prefixes.DEFAULT_IMMUTABLE_INSTANCE;
        System.out.println("Retrieving all properties before materialisation.");

        TupleIterator tupleIterator = store.compileQuery("SELECT DISTINCT ?x ?z WHERE{ ?x a ?z }", prefixes);

        try {
            System.out.println("Adding the ontology to the store...");
            //store.importOntology(ontology);
            //store.applyReasoning();
            Map<Resource, Set<Resource>> profiles = new HashMap<>();

            int numberOfRows = 0;
            System.out.println();
            System.out.println("=======================================================================================");
            int arity = tupleIterator.getArity();
            // We iterate trough the result tuples
            for (long multiplicity = tupleIterator.open(); multiplicity != 0; multiplicity = tupleIterator.advance()) {
                // We iterate trough the terms of each tuple
                final Resource individual = tupleIterator.getResource(0);
                final Resource concept = tupleIterator.getResource(1);
                if (!profiles.containsKey(individual)) {
                    profiles.put(individual, new HashSet<>());
                }

                profiles.get(individual).add(concept);

            }
            System.out.println("---------------------------------------------------------------------------------------");

            //profiles.forEach((k, v) -> System.out.println(k + " -> " + v));

            System.out.println("=======================================================================================");
            System.out.println();

            final HashSet<Set<Resource>> sets = new HashSet<>(profiles.values());

            System.out.println("=======================================================================================");

            System.out.println("All profiles");

            final Set<Set<Resource>> finalProfile =
                    sets.parallelStream()
                            .filter(s -> sets.stream()
                                    .noneMatch(t -> t.containsAll(s) && !t.equals(s)) // largest
                            )
                            .collect(toSet());

            System.out.println("Number of Profiles:"+finalProfile.size());
            finalProfile.forEach(System.out::println);


            return finalProfile;
        } finally {
            // When no longer needed, the iterator should be disposed so that all related resources are released.
            tupleIterator.dispose();
        }
    }


    public static void writeProfilesToFile(String tboxFile, String aboxFile, String outputFile) throws Exception {
        final Collection<Set<Resource>> profiles = computeProfiles(tboxFile, aboxFile);

        try (FileWriter os = new FileWriter(new File(outputFile))) {
            for (Set<Resource> p : profiles) {
                final String line = p.stream().map(Object::toString).collect(joining(","));
                os.write(line);
                os.write("\n");
            }
        }
    }

    /**
     * @param args 0 - ontology file (TBox)
     *             1 - ABox turtle file
     *             2 - output file
     */
    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.out.println("Usage: ABoxProfileExtractor tbox.owl abox.ttl output.txt");
            System.exit(-1);
        }

        final String tboxFile = args[0];
        final String aboxFile = args[1];
        final String outputFile = args[2];


        final Collection<Set<Resource>> profiles = computeProfiles(tboxFile, aboxFile);

        try (FileWriter os = new FileWriter(new File(outputFile))) {
            for (Set<Resource> p : profiles) {
                final String line = p.stream().map(Object::toString).collect(joining(","));
                os.write(line);
                os.write("\n");
            }
        }

    }

    protected Connection createConnection() {
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "12345678");
        // props.setProperty("ssl", "true");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost/profiles", props);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return conn;
    }

    protected void prepareDB(Statement stmt) throws SQLException {
        String sql="TRUNCATE triples;";
        stmt.execute(sql);

        sql="TRUNCATE concept_assertions;";
        stmt.execute(sql);

        sql="TRUNCATE in_properties;";
        stmt.execute(sql);

        sql="TRUNCATE out_properties;";
        stmt.execute(sql);
    }

    protected void gatherStatistics(Statement stmt) throws SQLException {
        //Clear stats table
        String sql="TRUNCATE stats";
        stmt.executeQuery(sql);

        sql="TRUNCATE stats";
        stmt.executeQuery(sql);

    }


}
