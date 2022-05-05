package ru.mpei.cimmaintainer.converter;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;

import java.io.FileInputStream;
import java.io.IOException;


public class RdfMaintainer {

    public static Model rdfMaintainer(String cimFile, String cimUri) throws RDFParseException, IOException {
        FileInputStream fis = new FileInputStream(cimFile);
        Model model = Rio.parse(fis, cimUri, RDFFormat.RDFXML);
        return model;

    }

}
