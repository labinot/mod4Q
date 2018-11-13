package org.semanticweb.clipper.alch.Types;
import org.semanticweb.clipper.alch.Types.DatalogProgram;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameters;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandNames = { "profiles" }, separators = "=", commandDescription = "Generate profiles from ABox")
public class CommandComputeProfiles extends DBCommandBase {

    private Connection conn;

    public CommandComputeProfiles(JCommander jc) {
        super(jc);
    }

    @Override
    boolean validate() {
        // TODO Auto-generated method stub
        return true;
    }


    @Override
    void exec() {
        long t1 = System.currentTimeMillis();//the start of the main process
        long d1, d2, d3, d4;

        conn = createConnection();
        ResultSet resultSet;
        String filename="";

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }

        try {
            String sql="SELECT value FROM ontology_metadata WHERE parameter='Filename';";
            resultSet=stmt.executeQuery(sql);
            if(resultSet.next()) {
                filename = resultSet.getString("value");
            }

            loadCoreTables(stmt);                           //load the core tables from staging tables
            loadArrayTables(stmt);                          //load the list tables from core tables
            initializeTypes(stmt);                          //initialise hierarchical tables and initial types
            computeDetPlusTypes(stmt);                      //compute types with main algorithm
            extractProfiles(stmt);                          //compute profiles of individuals and save the statistics

            sql ="SELECT temp();";
            stmt.execute(sql);

            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (/*FileNotFoundException*/Exception e) {
            e.printStackTrace();
        }
        long t2 = System.currentTimeMillis();
        System.out.println("Overall running time: " + (t2 - t1));
    }


    private void loadCoreTables(Statement stmt) throws SQLException {
        String sql = "SELECT load_core_tables()";
        stmt.execute(sql);
    }

    private void loadArrayTables(Statement stmt) throws SQLException {
        String sql = "SELECT load_list_tables()";
        stmt.execute(sql);
    }

    private void initializeTypes(Statement stmt) throws SQLException {

        System.out.println("");
        String sql ="SELECT initialize_types()";
        stmt.execute(sql);
    }

    private void computeGuessesPerType(Statement stmt) throws SQLException{
        String procedure="compute_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        //compute the list of potential guesses for each type in the ABox
        //deduplificate the list of identical types, link the individuals with the list of unique types
        String sql = "SELECT gs_compute_guesses_per_concept_profile()";
        stmt.execute(sql);    }

    private void dedupGuessesPerType(Statement stmt) throws SQLException{
        String procedure="deduplicate_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_dedup_guesses_per_type_table()";
        stmt.execute(sql);
    }

    private void eliminateContradictoryGuessesPerType(Statement stmt) throws SQLException{
        String procedure="eliminate_contradictory_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_eliminate_contradictory_guesses_per_type()";
        stmt.execute(sql);
    }

    //generates Abox types, which come from the list of guesses per each type in table guesses_per_type
    private void generateRelevantAboxTypes(Statement stmt) throws SQLException{
        boolean restart=false;
        String procedure="generate_relevant_Abox_types_based_on_guesses";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:" + timeStamp + "\t Process:guesses \tProcedure:" + procedure + "\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_generate_relevant_Abox_types_based_on_guesses()";
        stmt.execute(sql);

        sql ="select cast(value as boolean) as restart from ontology_metadata where parameter='Restart'";

        ResultSet resultSet;

        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            restart=resultSet.getBoolean("restart");

        if(restart)
            generateRelevantAboxTypes(stmt);
    }

    private void intializeGuessedAboxTypes(Statement stmt) throws SQLException {
        String procedure = "initialize_abox_individuals";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:" + timeStamp + "\t Process:guesses \tProcedure:" + procedure + "\t\t\t\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_initialize_abox_individuals()";
        stmt.execute(sql);
    }

    private void divideTypesBasedOnContradictoryGuesses(Statement stmt) throws SQLException{
        String procedure="divide_types_based_on_contradictory_guesses";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql= "SELECT gs_divide_types_based_on_contradictory_guesses()";
        stmt.execute(sql);
    }

    private void eliminateDeterministicGuessesPerType(Statement stmt) throws SQLException{
        String procedure="eliminate_deterministic_guesses_per_type";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());
        System.out.println("Time:"+timeStamp+"\t Process:guesses \tProcedure:"+procedure+"\t\t Calling Procedure:Main");// \t\t\tcallingLevel:0 \t childTaskID:0");

        String sql = "SELECT gs_eliminate_deterministic_guesses_per_type()";
        stmt.execute(sql);
    }

    private void computeDetPlusTypes(Statement stmt) throws SQLException {
        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        System.out.println("=================================================================================");
        System.out.println("Compute Deterministic Types plus consequnces from Doman and Range");

        //close types under domain restriction
        consequences_from_domain_restrictions(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close types under range restriction
        consequences_from_range_restrictions(stmt, executionID, callingLevel, childTaskID, callingProcedure);
        //close  under simple axioms A->B & and(A1,..,An)->B
        rule_det(stmt, executionID, callingLevel, childTaskID, callingProcedure);
    }

    /*this method implement the core calling mechanism for the main algorithm as well as Dlitisation process*/
    private void extractProfiles(Statement stmt) throws SQLException {

        int executionID=0;
        int callingLevel=0;
        int childTaskID=0;
        String callingProcedure="main";

        ResultSet resultSet;

        String sql="SELECT max(execution_id) AS execution_id FROM log;";
        resultSet=stmt.executeQuery(sql);
        if(resultSet.next())
            executionID=resultSet.getInt("execution_id");

        //TODO: add here the execution id

        System.out.println("Execution id:" + executionID);
        System.out.println("=================================================================================");


        //for each individual in abox get's the list of guesses
        computeGuessesPerType(stmt);

        //insert statistics
        sql="SELECT insert_statistics();";
        stmt.executeQuery(sql);

        //todo:call stats for number of profiles

        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        //dedupGuessesPerType(stmt);

        //eleminates contradictory guesses (those that violate disjointness axioms when joined with concept_list)
        //eliminateContradictoryGuessesPerType(stmt);

        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        //dedupGuessesPerType(stmt);

        //eleminates contradictory guesses s.t. G1 and G1 fire some disjointness axiom
        //divideTypesBasedOnContradictoryGuesses(stmt);

        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        //dedupGuessesPerType(stmt);

        //eleminates guesses that can be derived deterministically
        //eliminateDeterministicGuessesPerType(stmt);

        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        //dedupGuessesPerType(stmt);

        //second run after having derived more deterministic concepts in previous step
        //eliminateContradictoryGuessesPerType(stmt);

        //deduplicates types with the same concept_list from computeDetPlusTypes and concept_guesses from computeGuessesPerType
        //dedupGuessesPerType(stmt);
    }

    /*this method computes the consequences that come from domain restrictions*/
    private void consequences_from_domain_restrictions(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="consequences_from_domain_restrictions";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main \t\tProcedure:"+procedure+"\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT consequences_from_domain_restrictions( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequences that come from range restrictions*/
    private void consequences_from_range_restrictions(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {
        String procedure="consequences_from_range_restrictions";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:" + timeStamp + "\t Process:main \t\tProcedure:" + procedure + "\t Calling Procedure:" + callingProcedure);// + " \t callingLevel:" + callingLevel + " \t childTaskID:" + childTaskId);

        String sql =String
                .format("SELECT consequences_from_range_restrictions( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }

    /*this method computes the consequnces that come as a result of firing axioms with conjunctions on lhs*/
    private void rule_det(Statement stmt,int executionID, int callingLevel,int childTaskId, String callingProcedure) throws SQLException {

        String procedure="rule_det";
        String timeStamp = new SimpleDateFormat("HH:mm:ss:ms").format(Calendar.getInstance().getTime());

        System.out.println("Time:"+timeStamp+"\t Process:main\t\tProcedure:"+procedure+"\t\t\t\t Calling Procedure:"+callingProcedure);//+" \tcallingLevel:"+callingLevel+" \t childTaskID:"+childTaskId);

        String sql =String
                .format("SELECT rule_det( %1$d, %2$d, %3$d, '%4$s');", executionID, callingLevel, childTaskId, callingProcedure);

        stmt.execute(sql);
    }


}
