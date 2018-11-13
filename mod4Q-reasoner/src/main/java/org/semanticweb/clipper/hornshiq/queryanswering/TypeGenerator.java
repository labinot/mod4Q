/**
 * Created by bato on 12/2/2016.
 */
package org.semanticweb.clipper.hornshiq.queryanswering;

        import com.beust.jcommander.JCommander;
        import com.beust.jcommander.Parameters;
        import gnu.trove.iterator.TIntIterator;
        import gnu.trove.set.hash.TIntHashSet;
        import org.semanticweb.clipper.alch.Types.DBCommandBase;
        import org.semanticweb.clipper.hornshiq.ontology.*;
        import org.semanticweb.owlapi.apibinding.OWLManager;
        import org.semanticweb.owlapi.model.*;
        import org.semanticweb.owlapi.util.ShortFormProvider;
        import org.semanticweb.owlapi.util.SimpleShortFormProvider;

        import java.io.File;
        import java.sql.*;
        import java.util.*;

public class TypeGenerator {

    private Connection conn;
    private String user="postgres";
    private String pass="12345678";
    private String JdbcUrl="jdbc:postgresql://localhost/clipperTypes";

    public TypeGenerator(){
        long t1 = System.currentTimeMillis();
        this.conn=createConnection();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }

    protected Connection createConnection() {
        Properties props = new Properties();
        props.setProperty("user", this.user);
        props.setProperty("password", this.pass);
        // props.setProperty("ssl", "true");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(this.JdbcUrl, props);
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return conn;
    }

    public void loadTypesDB(Statement stmt,ClipperHornSHIQOntology clipperOntology) throws SQLException {

        ShortFormProvider sfp = new SimpleShortFormProvider();

        insertOntologyMetadata(stmt, clipperOntology);

        cleanStagingTables(stmt);

        cleanComputationTables(stmt);

        insertConcepts(stmt, sfp, clipperOntology);

        insertObjectRoles(stmt, sfp, clipperOntology);

        insertIndividuals(stmt, clipperOntology);

        insertConceptAssertions(stmt, sfp, clipperOntology);

        insertObjectRoleAssertions(stmt, sfp, clipperOntology);

        insertAxioms(stmt, sfp, clipperOntology);
    }

    public void genTypes(Statement stmt){

    }

    public void retrieveTypes(Statement stmt){

    }

    private void insertOntologyMetadata(Statement stmt,ClipperHornSHIQOntology ontology) throws SQLException {

        String sql =String
                .format("UPDATE ontology_metadata "
                                + "SET value='%1$s' "
                                + "WHERE parameter='Name'",
                        ontology.toString());

        stmt.execute(sql);

/*
        sql =String
                .format("UPDATE ontology_metadata "
                                + "SET value='%1$s' "
                                + "WHERE parameter='Filename'",
                        ontologyFile);
*/

        stmt.execute(sql);
    }

    private void cleanStagingTables(Statement stmt) throws SQLException {
        String sql = "SELECT clean_staging_tables()";
        stmt.execute(sql);
    }

    private void cleanComputationTables(Statement stmt) throws SQLException {
        String sql = "SELECT clean_computation_tables()";
        stmt.execute(sql);
    }

    //done
    private void insertIndividuals(Statement stmt, ClipperHornSHIQOntology ontology)throws SQLException {

        String sql;

        for(ClipperConceptAssertionAxiom ax: ontology.getConceptAssertionAxioms()){

            sql =String
                    .format("INSERT INTO individuals (individual) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM individuals WHERE id=%1$d )",
                            ax.getIndividual());

            stmt.execute(sql);
        }

        for(ClipperPropertyAssertionAxiom ax: ontology.getPropertyAssertionAxioms()){

            sql =String
                    .format("INSERT INTO individuals (individual) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM individuals WHERE id=%1$d )",
                            ax.getIndividual1());

            stmt.execute(sql);

            sql =String
                    .format("INSERT INTO individuals (individual) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM individuals WHERE id=%1$d )",
                            ax.getIndividual1());

            stmt.execute(sql);
        }
    }

    //done
    private void insertConcepts(Statement stmt, ShortFormProvider sfp, ClipperHornSHIQOntology ontology)throws SQLException {
        String sql;

        TIntIterator iterator = ontology.getAboxConcepts().iterator();

        while (iterator.hasNext()) {
            sql = String
                    .format("INSERT INTO concepts (concept) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM concepts WHERE concept=%1$d )",
                            iterator.next());

            stmt.execute(sql);
        }
    }

    //done
    private void insertObjectRoles(Statement stmt, ShortFormProvider sfp, ClipperHornSHIQOntology ontology)throws SQLException {

        String sql;

        for(ClipperSubPropertyAxiom ax: ontology.getSubPropertyAxioms()){

            sql =String
                    .format("INSERT INTO properties (role) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM properties WHERE role=%1$d )",
                            ax.getRole1());

            stmt.execute(sql);

            sql =String
                    .format("INSERT INTO properties (role) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM properties WHERE role=%1$d )",
                            ax.getRole2());

            stmt.execute(sql);
        }

        for(ClipperPropertyAssertionAxiom ax: ontology.getPropertyAssertionAxioms()){

            sql =String
                    .format("INSERT INTO properties (role) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM properties WHERE role=%1$d )",
                            ax.getRole());

            stmt.execute(sql);
        }

        for(ClipperInversePropertyOfAxiom ax: ontology.getInversePropertyOfAxioms()){

            sql =String
                    .format("INSERT INTO properties (role) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM properties WHERE role=%1$d )",
                            ax.getRole1());

            stmt.execute(sql);

            sql =String
                    .format("INSERT INTO properties (role) "
                                    + "SELECT %1$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM properties WHERE role=%1$d )",
                            ax.getRole2());

            stmt.execute(sql);
        }
    }

    //done
    private void insertConceptAssertions(Statement stmt, ShortFormProvider sfp,ClipperHornSHIQOntology ontology)throws SQLException {

        String sql;

        for(ClipperConceptAssertionAxiom ax: ontology.getConceptAssertionAxioms()){

            sql =String
                    .format("INSERT INTO concept_assertions (individual,concept) "
                                    + "SELECT %1$d,%2$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM concept_assertions WHERE individual=%1$d AND concept=%2$d)",
                            ax.getIndividual(),ax.getConcept());

            stmt.execute(sql);
        }
    }

    //done
    private void insertObjectRoleAssertions(Statement stmt,ShortFormProvider sfp, ClipperHornSHIQOntology ontology) throws SQLException {

        String sql;

        for(ClipperPropertyAssertionAxiom ax: ontology.getPropertyAssertionAxioms()){

            sql =String
                    .format("INSERT INTO property_assertions (role,individual_in_domain,individual_in_range) "
                                    + "SELECT %1$d,%2$d,%3$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM property_assertions WHERE role=%1$d AND individual_in_domain=%2$d AND individual_in_range=%3$d)",
                            ax.getRole(),ax.getIndividual1(),ax.getIndividual2());

            stmt.execute(sql);
        }
    }

    //done
    private void insertAxioms(Statement stmt,ShortFormProvider sfp, ClipperHornSHIQOntology ontology) throws SQLException {
        String sql;

        for(ClipperPropertyAssertionAxiom ax: ontology.getPropertyAssertionAxioms()){

            sql =String
                    .format("INSERT INTO property_assertions (role,individual_in_domain,individual_in_range) "
                                    + "SELECT %1$d,%2$d,%3$d"
                                    + "WHERE NOT EXISTS (SELECT * FROM property_assertions WHERE role=%1$d AND individual_in_domain=%2$d AND individual_in_range=%3$d)",
                            ax.getRole(),ax.getIndividual1(),ax.getIndividual2());

            stmt.execute(sql);
        }
    }
}
