package org.semanticweb.clipper.alch.Types;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;
import org.semanticweb.clipper.alch.profile.ALCH_Normalizer2;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.util.*;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Parameters(commandNames = { "loadIntegrated" }, separators = "=", commandDescription = "Load integrated ontology to Database")
public class CommandLoadIntegrated extends DBCommandBase {

    private Connection conn;

    public CommandLoadIntegrated(JCommander jc) {
        super(jc);
    }

    @Override
    boolean validate() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    void exec() {
        long t_start;//a variable to store the starting time of the measurment
        boolean incrementalLoad=false;
        conn = createConnection();
        String sql="";

        if(this.incremental=="1")
            incrementalLoad=true;

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        DBLoader dbLoader = new DBLoader(conn);

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology = null;

        ALCH_Normalizer2 normalizer = new ALCH_Normalizer2();

        try {
            for (String ontologyFilePath : this.getOntologyFiles()) {

                File ontologyFile = new File(ontologyFilePath);
                String ontologyFilename = ontologyFile.getName().substring(0,ontologyFile.getName().lastIndexOf("."));

                cleanStatistics(ontologyFilename,stmt);

                t_start=System.currentTimeMillis();//starting the measurment for ontology loading from file via OWLAPI

                    ontology = manager.loadOntologyFromOntologyDocument(ontologyFile);
                    insertSubProcessTime(ontologyFilename,"1- load ontology to OWLAPI",stmt,t_start, System.currentTimeMillis());

                t_start=System.currentTimeMillis();//starting the measurment for ontology normalization process

                    ontology = normalizer.normalize(ontology);
                    insertSubProcessTime(ontologyFilename,"2- ontology normalization",stmt,t_start, System.currentTimeMillis());

                t_start=System.currentTimeMillis();//starting the measurment for ontology normalization process

                    dbLoader.loadIntegratedOntology(ontology,ontologyFilename,incrementalLoad);
                    insertSubProcessTime(ontologyFilename,"3- load integrated ontology to DB",stmt,t_start, System.currentTimeMillis());

                manager.removeOntology(ontology);

                stmt.execute(sql);

                System.err.println(ontologyFile + " loaded!");
            }
            stmt.close();
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void insertSubProcessTime(String ontologyFilename, String process, Statement stmt, long t_start, long t_end) {
        String sql =String
                .format("INSERT INTO stat_subprocess_runtime(filename,runtime_milisec,subprocess) VALUES ( '%1$s', %2$d,'%3$s');",
                        ontologyFilename, System.currentTimeMillis()-t_start,process);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cleanStatistics(String ontologyFilename, Statement stmt) {
        cleanSubProcessTimeStatistics(ontologyFilename,stmt);
        cleanNominalStatistics(ontologyFilename,stmt);
        cleanProfileStatistics(ontologyFilename,stmt);
    }

    private void cleanSubProcessTimeStatistics(String ontologyFilename,Statement stmt) {
        String sql =String
                .format("DELETE FROM stat_subprocess_runtime WHERE filename='%1$s';",ontologyFilename);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cleanNominalStatistics(String ontologyFilename,Statement stmt) {
        String sql =String
                .format("DELETE FROM stat_nominal WHERE filename='%1$s';",ontologyFilename);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cleanProfileStatistics(String ontologyFilename,Statement stmt){
        String sql =String
                .format("DELETE FROM stat_ind_per_profile WHERE filename='%1$s';",ontologyFilename);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql =String
                .format("DELETE FROM stat_per_profile WHERE filename='%1$s';",ontologyFilename);

        try {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
