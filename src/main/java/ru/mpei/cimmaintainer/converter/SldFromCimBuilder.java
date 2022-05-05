package ru.mpei.cimmaintainer.converter;

import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import ru.mpei.cimmaintainer.dto.JO.Terminal;


@Getter @Setter
public class SldFromCimBuilder {



    public static void sparQL(Model model) {
        String log4jConfPath = "D:\\YandexDisk\\Project\\CIM_OWL\\cim-maintainer\\src\\main\\resources\\log4j.properties";
        PropertyConfigurator.configure(log4jConfPath);

        Terminal terminal = new Terminal();

        Repository repository = new SailRepository(new MemoryStore());
        RepositoryConnection connection = repository.getConnection();
        connection.add(model);

        String queryString = "PREFIX cim: <" + "http://iec.ch/TC57/2013/CIM-schema-cim16#" + "> " +
                "SELECT ?tId ?name ?cnId ?ceId " +
                "WHERE { " +
                " ?t a cim:Terminal ; " +
                " cim:IdentifiedObject.mRID ?tId ; " +
                " cim:IdentifiedObject.name ?name ; " +
                " cim:TerminalConnectivityNode ?cn ; " +
                " cim:Terminal.ConductingEquipment ?ce . " +
                " ?ce cim:IdentifiedObject.mRID ?ceId ." +
                " ?cn cim:IdentifiedObject.mRID ?cnId ." +
                "}";
        TupleQuery query = connection.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
            try (TupleQueryResult result = query.evaluate()){
                for (BindingSet solution : result) {
                    String tId = solution.getValue("tId").stringValue();
                    String cnId = solution.getValue("cnId").stringValue();
                    String ceId = solution.getValue("ceId").stringValue();
                    System.out.println(tId + cnId + ceId);
                }
            }
    }
}
