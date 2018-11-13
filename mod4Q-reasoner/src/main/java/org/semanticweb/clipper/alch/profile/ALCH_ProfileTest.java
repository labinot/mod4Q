/**
 * Created by bato on 5/8/2016.
 */
package org.semanticweb.clipper.alch.profile;

        import org.semanticweb.owlapi.model.OWLOntologyManager;
        import org.semanticweb.owlapi.model.OWLOntology;
        import org.semanticweb.owlapi.model.OWLOntologyCreationException;
        import org.semanticweb.owlapi.apibinding.OWLManager;
        import org.semanticweb.owlapi.util.DefaultPrefixManager;
        import org.semanticweb.owlapi.profiles.*;

        import java.io.*;

/*
 * Copyright (C) 2009, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * Author: Matthew Horridge<br>
 * The University of Manchester<br>
 * Information Management Group<br>
 * Date: 03-Aug-2009 Modified by Kien Date: Jan 2011
 */
public class ALCH_ProfileTest {
    public static void main(String[] args) {

        try {
            System.setProperty("entityExpansionLimit", "640000");
            DefaultPrefixManager pm = new DefaultPrefixManager(
                    "http://protege.cim3.net/file/pub/ontologies/tambis/tambis-full.owl#");

            OWLOntologyManager man = OWLManager.createOWLOntologyManager();

            File file = new File("ontology/testBI_no_nominals.owl");

            // Now load the local copy
            OWLOntology ont = man.loadOntologyFromOntologyDocument(file);

            System.out.println(ont);

            System.out.println("Loaaaaaaaaaaaaaaaaaaaaded ontology");
            check(ont, new ALCH_Profile());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

    }

    private static void check(OWLOntology ont, OWLProfile profile) {
        System.out.println("Checking ontology is in " + profile.getName());
        OWLProfileReport report = profile.checkOntology(ont);
        try {
            FileWriter outFile = new FileWriter("TestData/HornSHIQPrifile.txt");
            PrintWriter out = new PrintWriter(outFile);

            // Write text to file
            out.println("CHECKING PROFILE: " + profile.getName());
            out.println(report);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(report);
        System.out.println("--------------------------------------------------------------------------");
    }
}
