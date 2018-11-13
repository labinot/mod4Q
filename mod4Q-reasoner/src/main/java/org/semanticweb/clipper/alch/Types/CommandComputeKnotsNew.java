package org.semanticweb.clipper.alch.Types;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;

@Parameters(commandNames = { "knotsNew" }, separators = "=", commandDescription = "Generate knots")
public class CommandComputeKnotsNew extends DBCommandBase {

    long t_start;
    String ontologyFilename="";
    private Connection conn;

    public CommandComputeKnotsNew(JCommander jc) {
        super(jc);
    }

    @Override
    boolean validate() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    void exec() {
        conn = createConnection();
        ResultSet resultSet;

        Statement stmt = null;

            try {
                stmt = conn.createStatement();
                String sql="SELECT value FROM ontology_metadata WHERE parameter='Filename';";
                resultSet=stmt.executeQuery(sql);
                if(resultSet.next()) {
                    ontologyFilename = resultSet.getString("value");
                }
                DBReasoner dbReasoner = new DBReasoner(conn,true,ontologyFilename);

                dbReasoner.extractProfiles();                          //initialise hierarchical tables and initial types
                dbReasoner.matchProfiles();                             //updates the profile id of the extracted profiles as per once stored, when there is a match
                dbReasoner.storeNewProfiles();;                         //unmatched (new) profiled are stored
                dbReasoner.precomputeBaseTypes();                      //optimization step where some guesses are pruned away

                if(dbReasoner.checkMaxGuesses()<=10) {
                    dbReasoner.generateBaseTypesToAlgorithm();              //we generate condidate types from optimized table comp_guesses_per_type
                    dbReasoner.storeLinkBetweenProfilesAndBaseTypes();      //we update the link with ini types in the stored table of profiles
                    dbReasoner.inputBaseTypesToAlgorithm();                 //we initialize the algorithm with base_types + stored types and stored knots
                    dbReasoner.computeKnots();                              //unfolding of the knot table
                    dbReasoner.storeExistingTypes();                        //stores the types and knots for incremental reasoning
                    //dbReasoner.runDatalogEncoding4EachConcept();
                    dbReasoner.runReachabilityQueries();
                }else{
                    System.err.println("Number of guesses to high:"+dbReasoner.checkMaxGuesses()+" aborting further processing for ontology "+ontologyFilename);
                }
                stmt.close();

            } catch (SQLException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (IOException e1){
                e1.printStackTrace();
            }
            //saveDatalogEncoding(stmt);

            //invoke datalog program and initialize it with datalog representation from DB
            //recordAllAnswers2IQ4Concepts(stmt);

        System.out.println("Overall running time: " + (System.currentTimeMillis()-t_start));
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
}
