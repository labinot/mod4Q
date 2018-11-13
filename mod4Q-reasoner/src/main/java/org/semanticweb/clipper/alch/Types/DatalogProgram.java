package org.semanticweb.clipper.alch.Types;
import com.google.common.collect.Lists;
import it.unical.mat.wrapper.DLVError;
import it.unical.mat.wrapper.DLVInputProgram;
import it.unical.mat.wrapper.DLVInputProgramImpl;
import it.unical.mat.wrapper.DLVInvocation;
import it.unical.mat.wrapper.DLVInvocationException;
import it.unical.mat.wrapper.DLVWrapper;
import it.unical.mat.wrapper.FactHandler;
import it.unical.mat.wrapper.FactResult;
import it.unical.mat.wrapper.ModelBufferedHandler;
import org.semanticweb.clipper.hornshiq.queryanswering.ClipperManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Created by bato on 12/7/2016.
 */
public class DatalogProgram {

    private String Translation2DataLogFile;
    private String Answers2DataLogFile;
    private String dlvPath;
    private String InputFileName;
    private CallCLASP CLASP;

    public String getInputFileName() {
        return InputFileName;
    }

    public void setInputFileName(String InputFileName) {
        this.InputFileName = InputFileName;
    }

    public String getTranslation2DataLogFile() {
        return Translation2DataLogFile;
    }

    public void setTranslation2DataLogFile(String InputDataLogFile) {
        this.Translation2DataLogFile = InputDataLogFile;
    }

    public String getAnswers2DataLogFile() {
        return Answers2DataLogFile;
    }

    public void setAnswers2DataLogFile(String OutputDataLogFile) {
        this.Answers2DataLogFile = OutputDataLogFile;
    }


    public String getDlv_file_path() {
        return dlvPath;
    }

    public void setDlv_file_path(String dlv_path) {
        this.dlvPath = dlv_path;
    }

    public DatalogProgram(){

    }

    public void generateDatalogFacts(Statement stmt) throws SQLException {
        try {
            PrintStream program = new PrintStream(new FileOutputStream(Translation2DataLogFile));

            // ruleForBottomConcept(program);
            factsAssertProfiles2Individuals(program, stmt);
            factsAssertAboxPropertyAssertions(program, stmt);

            program.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void generateDatalogRules(Statement stmt) throws SQLException {
        try {
            PrintStream program = new PrintStream(new FileOutputStream(Translation2DataLogFile));
            //check if dlvpath is set, if not than try setting it through this method
            ensureDlvPath();

            factsActiveDomain(program, stmt);
            factsAssertProfiles2Individuals(program, stmt);
            factsAssertAboxPropertyAssertions(program, stmt);

            // ruleForBottomConcept(program);
            rulesType2Concepts(program,stmt);
            rulesFromProfiles2Types(program,stmt);
            rulesRoleHierarchy(program, stmt);
            constraintsFor(program, stmt);

            program.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //generates the new datalog rewritting that rewrittes the constraints of 1 datalog rewritting as rules and
    //adds constraints for each concepts that is not included in a type
    public void generateDatalogRules2(Statement stmt) throws SQLException {
        try {
            PrintStream program = new PrintStream(new FileOutputStream(Translation2DataLogFile));
            //check if dlvpath is set, if not than try setting it through this method
            ensureDlvPath();

            factsAssertProfiles2Individuals(program, stmt);
            factsAssertAboxPropertyAssertions(program, stmt);

            // ruleForBottomConcept(program);
            rulesType2Concepts(program,stmt);
            rulesFromProfiles2Types(program,stmt);
            rulesRoleHierarchy(program, stmt);
            constraintsFor(program, stmt);

            program.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //generates the new datalog rewritting that rewrittes the constraints of 1 datalog rewritting as rules and
    //adds constraints for each concepts that is not included in a type
    public void generateDatalog4EachConcept(Connection conn) throws SQLException, IOException, InterruptedException {
        Statement stmt = conn.createStatement();
        Statement stmtInner = conn.createStatement();

        String filename="";
        String sql="";
        ResultSet resultSet;
        int concept_id;
        int role_id;
        String concept_name="";
        String role_name="";
        long duration;
        Writer output;

        try {
            CallCLASP CLASP=new CallCLASP();
            PrintStream program = new PrintStream(new FileOutputStream(Translation2DataLogFile));
            //check if dlvpath is set, if not than try setting it through this method
            //ensureDlvPath();

            factsAssertProfiles2Individuals(program, stmt);
            factsAssertAboxPropertyAssertions(program, stmt);

            // ruleForBottomConcept(program);
            rulesType2Concepts(program,stmt);
            rulesFromProfiles2Types(program,stmt);
            rulesRoleHierarchy(program, stmt);
            constraintsFor(program, stmt);

            program.close();

            //first clean exec stats for this filename
            sql =String.
                    format("delete from stat_exec_time_instances_clasp where filename='%1$s'",
                            Translation2DataLogFile);

            stmt.execute(sql);

            sql ="select id as conc_id,concept as conc_name from st_concepts where id not in (0,1);";
            resultSet=stmt.executeQuery(sql);

            while(resultSet.next()) {

                concept_id = resultSet.getInt("conc_id");
                concept_name = resultSet.getString("conc_name");
                filename = Translation2DataLogFile.substring(0, Translation2DataLogFile.length()-3)+"_"+concept_id+".lp";

                //first copy the base rewriting to the file
                Files.copy(Paths.get(Translation2DataLogFile), Paths.get(filename),REPLACE_EXISTING);

                output = new BufferedWriter(new FileWriter(filename,true));  //open file in append mode
                output.append("#show"+" conc_"+concept_id+"/1.");
                output.close();

                //call clasp for current filename with timeout of 600 sec.
                duration=CLASP.cautiousReasoning(filename,600);

                //insert statistics
                    sql =String.
                            format("insert into stat_exec_time_instances_clasp (filename, conceptname, exec_time) values ( '%1$s', '%2$s', %3$d)",
                                    Translation2DataLogFile, concept_name, duration);

                    stmtInner.execute(sql);

                //delete the file
                Path path2File = Paths.get(filename);
                Files.delete(path2File);
            }

/*

            sql ="select id as role_id,rolename as role_name from st_properties;";
            resultSet=stmt.executeQuery(sql);

            while(resultSet.next()) {

                role_id = resultSet.getInt("role_id");
                role_name = resultSet.getString("role_name");
                filename = Translation2DataLogFile.substring(0, Translation2DataLogFile.length()-3)+"_"+role_id+".lp";

                //first copy the base rewriting to the file
                Files.copy(Paths.get(Translation2DataLogFile), Paths.get(filename),REPLACE_EXISTING);

                output = new BufferedWriter(new FileWriter(filename,true));  //open file in append mode
                output.append("#show"+" role_"+role_id+"/2.");
                output.close();

                duration=0;
                //call clasp for current filename with timeout of 600 sec.
                duration=CLASP.cautiousReasoning(filename,600);

                //insert statistics
                sql =String.
                        format("insert into stat_exec_time_instances_clasp (filename, conceptname, exec_time) values ( '%1$s', '%2$s', %3$d)",
                                Translation2DataLogFile, role_name, duration);

                stmtInner.execute(sql);

                //delete the file
                Path path2File = Paths.get(filename);
                Files.delete(path2File);
            }
*/


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    //generates the new datalog rewritting that rewrittes the constraints of 1 datalog rewritting as rules and
    //adds constraints for each concepts that is not included in a type
    //and runs each reachabilityQuery
    public void generateDatalog4EachReachabilityQuery(Connection conn) throws SQLException, IOException, InterruptedException {
        Statement stmt = conn.createStatement();
        Statement stmtInner = conn.createStatement();

        String filename="";
        String sql="";
        ResultSet resultSet;
        ResultSet resultSet2;
        int concept_id;
        int role_id;
        int type_id;
        long duration;
        Writer output;

        try {
            CallCLASP CLASP=new CallCLASP();
            PrintStream program = new PrintStream(new FileOutputStream(Translation2DataLogFile));
            //check if dlvpath is set, if not than try setting it through this method
            //ensureDlvPath();

            factsAssertProfiles2Individuals(program, stmt);
            factsAssertAboxPropertyAssertions(program, stmt);

            // ruleForBottomConcept(program);
            rulesType2Concepts(program,stmt);
            rulesFromProfiles2Types(program,stmt);
            rulesRoleHierarchy(program, stmt);
            constraintsFor(program, stmt);

            program.close();

            //first clean exec stats for this filename
            sql =String.
                    format("delete from stat_exec_time_reachability_queries where filename='%1$s'",
                            Translation2DataLogFile);

            stmt.execute(sql);

            //generate candidate reachability queries
            sql =String.
                    format("select generate_candidate_reachability_queries();");

            stmt.execute(sql);

            //compute reach relation for the generated candidate queries from the step above
            sql =String.
                    format("select compute_reach_relation_for_candidate_reachability_queries();");

            stmt.execute(sql);

            //backpropagate the reachable concept through reach relation
            sql =String.
                    format("select backpropagate_reachable_concept();");

            stmt.execute(sql);

            sql ="select role as role_id,concept as conc_id from tmp_test_reach_role_concept;";
            resultSet=stmt.executeQuery(sql);

            while(resultSet.next()) {

                role_id = resultSet.getInt("role_id");
                concept_id = resultSet.getInt("conc_id");

                filename = Translation2DataLogFile.substring(0, Translation2DataLogFile.length()-3)+"_reach_"+concept_id+"_through_"+role_id+".lp";

                //first copy the base rewriting to the file
                Files.copy(Paths.get(Translation2DataLogFile), Paths.get(filename),REPLACE_EXISTING);

                output = new BufferedWriter(new FileWriter(filename,true));  //open file in append mode

                sql =String.
                        format("select type_id from reaches_concept_through_role where role='%1$d' and concept='%2$d';",role_id,concept_id);
                resultSet2=stmtInner.executeQuery(sql);

                while(resultSet2.next()){
                    type_id=resultSet2.getInt("type_id");
                    output.append("reach(X):-type("+type_id+",X).\n");
                }

                output.append("reach(X):-conc_"+concept_id+"(X).\n");
                output.append("reach(X):-role_"+role_id+"(X,Y),reach(Y).\n");
                output.append("#show reach/1.");
                output.close();

                //call clasp for current filename with timeout of 600 sec.
                duration=CLASP.cautiousReasoning(filename,600);

                //insert statistics
                sql =String.
                        format("insert into stat_exec_time_reachability_queries (filename, query, exec_time) values ( '%1$s', '%2$s', %3$d)",
                                Translation2DataLogFile, "role_"+role_id+"*conc_"+concept_id, duration);

                stmtInner.execute(sql);

                //delete the file
                Path path2File = Paths.get(filename);
                Files.delete(path2File);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * if dlvPath is not set, then we use ~/bin/dlv or /usr/bin/dlv
     */
    private void ensureDlvPath() {

        if (dlvPath != null) {
            if (new File(dlvPath).exists()) {
                return;
            }
            throw new IllegalStateException("DLV file " + dlvPath + " does not exist");
        } else {
            dlvPath = System.getenv("HOME") + "/bin/dlv";
            if (new File(dlvPath).exists()) {
                return;
            }
            dlvPath = "C:\\bin\\dlv.exe";
            if (new File(dlvPath).exists()) {
                return;
            }
            dlvPath = "/usr/bin/dlv";
            if (new File(dlvPath).exists()) {
                return;
            }
            throw new IllegalStateException("dlv path is not set, and not on ~/bin/dlv or /usr/bin/dlv");
        }

    }

    public void factsActiveDomain(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_adom();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }


    public void factsAssertProfiles2Individuals(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_fact_prof();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }

    public void factsAssertAboxPropertyAssertions(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_fact_roles();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));

    }

    public void rulesType2Concepts(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_rules_types2concepts();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }

    //mantas'es rewritting
    public void rulesType2ConceptsNew(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_rules_types2concepts_new();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }

    public void rulesRoleHierarchy(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_rules_role_hierarchy();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }

    public void rulesFromProfiles2Types(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_rules_prof2types();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }

    public void constraintsFor(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_constraints();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }

    //mantas'es rewritting
    public void constraintsForNew(PrintStream program, Statement stmt) throws SQLException {
        ResultSet resultSet;
        String      sql;

        sql ="select get_datalog_constraints_new();";

        resultSet=stmt.executeQuery(sql);

        while(resultSet.next())
            program.println(resultSet.getString(1));
    }


    public ArrayList<String> execInstanceQuery(String prmQuery) throws FileNotFoundException {
        //*this.answers = Lists.newArrayList();

        //*String outPutNotification = "";

        ArrayList<String> answers= Lists.newArrayList();

        DLVInputProgram inputProgram = new DLVInputProgramImpl();
        PrintStream program = new PrintStream(new FileOutputStream(Answers2DataLogFile));

		/* I can add some file to the DLVInputProgram */
        inputProgram.addFile(this.getTranslation2DataLogFile());

        /*check if DLV is located in the indicated path*/
        ensureDlvPath();

        DLVInvocation invocation = DLVWrapper.getInstance().createInvocation(dlvPath);

        // Creates an instance of DLVInputProgram
        if (ClipperManager.getInstance().getVerboseLevel() > 1)
            System.out.println("===========Answers for the query ========");

        try {
            invocation.setInputProgram(inputProgram);
            invocation.setNumberOfModels(1);
            List<String> filters = new ArrayList<String>();
            // filters.add(this.headPredicate);
            filters.add(prmQuery);
            invocation.setFilter(filters, true);
            ModelBufferedHandler modelBufferedHandler = new ModelBufferedHandler(invocation);

			/* In this moment I can start the DLV execution */
            FactHandler factHandler = new FactHandler() {
                @Override
                public void handleResult(DLVInvocation obsd, FactResult res) {
                    String answerString = res.toString();
                    answers.add(answerString);
                }
            };

            invocation.subscribe(factHandler);
            // Roughly datalog program evalutaion
            long dlvBegin = System.currentTimeMillis();
            invocation.run();
            long dlvEng = System.currentTimeMillis();
            //clipperReport.setDatalogRunTime(dlvEng - dlvBegin);
            // Roughly datalog program evalutaion
            if (!modelBufferedHandler.hasMoreModels()) {
                System.out.println("No model");
            }
            invocation.waitUntilExecutionFinishes();
            List<DLVError> k = invocation.getErrors();
            if (k.size() > 0) {
                System.out.println("Number of errors"+k);
            }

        } catch (DLVInvocationException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long starOutputAnswer = System.currentTimeMillis();

        System.out.println("Answers to query:"+prmQuery);

        for(String str :answers){
            System.out.println(str);
        }

        return answers;
    }

}
