package org.semanticweb.clipper.alch.Types;

import com.beust.jcommander.JCommander;
import org.semanticweb.clipper.hornshiq.queryanswering.ClipperManager;

public class KnotsApp {

    /**
     * @param args
     */
    public static void main(String[] args) {
        new KnotsApp(args);
    }

    public KnotsApp(String[] args) {
        CommandLineArgs co = new CommandLineArgs();
        JCommander jc = new JCommander(co);

        CommandHelp commandHelp = new CommandHelp(jc);

        CommandLoad commandLoad = new CommandLoad(jc);

        CommandLoadIntegrated commandLoadIntegrated = new CommandLoadIntegrated(jc);

        CommandLoadSeparated commandLoadSeparated = new CommandLoadSeparated(jc);

        CommandInitDB commandInitDB = new CommandInitDB(jc);

        CommandComputeTypes commandComputeTypes = new CommandComputeTypes(jc);

        CommandComputeProfiles commandComputeProfiles = new CommandComputeProfiles(jc);

        CommandComputeKnots commandComputeKnots = new CommandComputeKnots(jc);

        CommandComputeKnotsNew commandComputeKnotsNew = new CommandComputeKnotsNew(jc);

        jc.setProgramName("knots.sh");

        jc.parse(args);

        String cmd = null;
        try {
            cmd = jc.getParsedCommand();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            commandHelp.exec();
        }

        if (cmd == null) {
            commandHelp.exec();
        } else if (cmd.equals("load")) {
            commandLoad.exec();
        } else if (cmd.equals("loadIntegrated")) {
            commandLoadIntegrated.exec();
        } else if (cmd.equals("loadSeparated")) {
            commandLoadSeparated.exec();
        } else if (cmd.equals("init")) {
            commandInitDB.exec();
        } else if (cmd.equals("help")) {
            commandHelp.exec();
        } else if (cmd.equals("gen")) {
            commandComputeTypes.exec();
        } else if (cmd.equals("profiles")) {
            commandComputeProfiles.exec();
        } else if (cmd.equals("knots")) {
            commandComputeKnots.exec();
        } else if (cmd.equals("knotsNew")) {
            commandComputeKnotsNew.exec();
        }
    }
}
