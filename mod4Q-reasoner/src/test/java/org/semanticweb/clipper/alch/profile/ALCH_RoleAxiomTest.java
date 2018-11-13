package org.semanticweb.clipper.alch.profile;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import static org.junit.Assert.*;

/**
 * Created by bato on 7/9/2016.
 */
public class ALCH_RoleAxiomTest {
    private OWLOntologyManager manager;
    private OWLDataFactory factory;
    private OWLClass A;
    private OWLClass B;
    private OWLClass C;
    private OWLObjectProperty r;
    private OWLDataProperty r_d;
    private OWLObjectProperty s;
    private OWLDataProperty s_d;
    @Before
    public void setUp(){
        manager = OWLManager.createOWLOntologyManager();
        factory = manager.getOWLDataFactory();
        A = factory.getOWLClass(IRI.create("http://www.example.org/#A"));
        B = factory.getOWLClass(IRI.create("http://www.example.org/#B"));
        C = factory.getOWLClass(IRI.create("http://www.example.org/#C"));
        r = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#r"));
        s = factory.getOWLObjectProperty(IRI.create("http://www.example.org/#s"));
        r_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#r_d"));
        s_d = factory.getOWLDataProperty(IRI.create("http://www.example.org/#s_d"));
    }

    @Test
    public void testInitialize1() throws Exception {
        ALCH_RoleAxiom ax= new ALCH_RoleAxiom(factory.getOWLSubObjectPropertyOfAxiom(r,s));
        System.out.println(ax.toString());
    }

    @Test
    public void testGetRight() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }
}