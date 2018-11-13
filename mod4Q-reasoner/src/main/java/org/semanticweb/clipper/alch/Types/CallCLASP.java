package org.semanticweb.clipper.alch.Types;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by bato on 4/27/17.
 */
public class CallCLASP {

    long startTime;
    long endTime;
    long duration;


    public long cautiousReasoning(String filename, int timeoutsec) throws InterruptedException {

        OutputStream os;

        startTime = System.currentTimeMillis();

        //clingo -e cautious --quiet=1 --project --time-limit=600 test.lp

        //String[] arguments = new String[] {"clingo", "-e cautious", "--quiet=1","--time-limit="+timeoutsec, filename};

        String command="clingo -e cautious --quiet=1 --project "+filename;

        try {
            CommandLine commandLine=CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(30);
            int exitValue=executor.execute(commandLine);

/*
            Process proc = new ProcessBuilder(arguments).start();

            proc.waitFor();
*/

        } catch (IOException e) {
            e.printStackTrace();
        }

        endTime = System.currentTimeMillis();
        duration = (endTime - startTime);

        return duration;
    }
}
