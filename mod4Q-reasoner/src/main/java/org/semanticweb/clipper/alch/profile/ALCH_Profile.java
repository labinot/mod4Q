/**
 * Created by bato on 5/8/2016.
 */
package org.semanticweb.clipper.alch.profile;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.util.OWLObjectPropertyManager;
import org.semanticweb.owlapi.util.OWLOntologyWalker;

public class ALCH_Profile implements OWLProfile {

    private ALCH_Sub_ClassExpressionChecker sub;

    ALCH_ProfileObjectVisitor profileObjectVisitor;

    private OWLObjectPropertyManager propertyManager;

    public ALCH_Profile() {

        sub = new ALCH_Sub_ClassExpressionChecker(this);
    }

    @Override
    public String getName() {
        return "ALCH";
    }

    @Override
    public IRI getIRI() {
        return null;
    }

    // use it only when you know what you are doing
    void setPropertyManager(OWLObjectPropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    OWLObjectPropertyManager getPropertyManager() {
        if (propertyManager == null)
            propertyManager = profileObjectVisitor.getPropertyManager();
        return propertyManager;
    }

    @Override
    public OWLProfileReport checkOntology(OWLOntology ontology) {
        OWL2DLProfile profile = new OWL2DLProfile();
        //System.out.println(ontology);
        OWLProfileReport report = profile.checkOntology(ontology);


        Set<OWLProfileViolation> violations = new HashSet<OWLProfileViolation>();
        violations.addAll(report.getViolations());

        OWLOntologyWalker walker = new OWLOntologyWalker(
                ontology.getImportsClosure());

        profileObjectVisitor = new ALCH_ProfileObjectVisitor(this, walker,
                ontology.getOWLOntologyManager());

        walker.walkStructure(profileObjectVisitor);

        violations.addAll(profileObjectVisitor.getProfileViolations());
        return new OWLProfileReport(this, violations);
    }

    public ALCH_Sub_ClassExpressionChecker getSubClassExpressionChecker() { return sub; }

}
