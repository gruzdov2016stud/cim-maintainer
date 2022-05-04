package ru.mpei.cimmaintainer.converter;

import lombok.Getter;
import org.eclipse.rdf4j.model.Model;

import org.eclipse.rdf4j.model.util.ModelBuilder;
import org.eclipse.rdf4j.model.vocabulary.RDF;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import ru.mpei.cimmaintainer.dto.Viezdnoe.*;
import ru.mpei.cimmaintainer.writer.RdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class SldToCimConverter {

    private final String cimNamespace = "http://iec.ch/TC57/2013/CIM-schema-cim16#";

    @Getter
    private ModelBuilder modelBuilder = new ModelBuilder();

    public SldToCimConverter() {
        modelBuilder
                .setNamespace(RDF.PREFIX, RDF.NAMESPACE)
                .setNamespace("cim", cimNamespace)
                .subject("cim:BV")
                .add(RDF.TYPE, "cim:BaseVoltage");
    }

    public void convert(SingleLineDiagram sld, Map<String, String> devices, Map<String, String> voltage) {
        sld.getElements().forEach(e -> convertElementToRdfResource(e, devices, voltage));
//        groupConnectivityElemetsByGraphAnalyzer(sld);
    }

    private void convertElementToRdfResource(
            Elements element,
            Map<String, String> devices,
            Map<String, String> voltage
    ) {
        modelBuilder
                .subject("cim:" + element.getId())
                .add("cim:IdentifiedObject.mRID", element.getId());
        if (element.getProjectName() != null)
            modelBuilder.add("cim:IdentifiedObject.name", element.getProjectName());

        /**Связь CIM-ресурсов с Terminal*/
        for (Port port : element.getPorts()) {
            if (element.getType().equals("directory"))
                if (element.getDirectoryEntryId().equals("1eda194d-1c4d-4d98-b23e-803ef281d074")) continue;
            modelBuilder
                    .add("cim:ConductingEquipment.Terminal", "cim:".concat(port.getId()));
        }
        /** todo CIM-ресурсы оборудования*/
        /**каждому оборудованию подстанции присвоим соответствующий класс напряжения*/
        modelBuilder
//                .add("cim:ConductingEquipment.BaseVoltage", "cim:".concat(voltage.get(element.getVoltageLevel()))) //Для полученния rdf:resource="#110kV"
                .add("cim:ConductingEquipment.BaseVoltage", "cim:".concat(element.getVoltageLevel()));//Для полученния rdf:resource="#14"
/*          modelBuilder
                    .add("cim:ConductingEquipment.BaseVoltage", voltage.getValue().getEn());*/
        if (element.getType().equals("directory")) {
            /**каждому оборудованию подстанции присвоим соответствующее имя оборудования */
            modelBuilder.add(RDF.TYPE, "cim:".concat(devices.get(element.getDirectoryEntryId())));
            /**каждому Силовому ТР добавить поле мощности*/
            if (element.getDirectoryEntryId().equals("1eda194d-1c4d-4d98-b23e-803ef281d074"))
                for (Fields fields : element.getFields()) {
                    modelBuilder
                            .add("cim:ConductingEquipment.ApparentPower", fields.getValue());
                }
            /**каждой Обмотки ТР добавим класс напряжения*/
            for (Port ports : element.getPorts()) {
                for (PortFields field : ports.getFields()) {
                    /**Каждому PowerTransformer добавим PowerTransformerEnd */
                    modelBuilder
                            .add("cim:PowerTransformerEnd",
                                    ports.getId() + field.getDirectoryId() + "_"
                                            + voltage.get(field.getDirectoryId()));
                }
            }
        } else modelBuilder.add(RDF.TYPE, "cim:".concat(element.getType()));

        /**CIM-ресурсы Terminal*/
        for (Port port : element.getPorts()) {
            if (port.getLinks() == null || port.getLinks().size() == 0) continue;
            modelBuilder
                    .subject("cim:".concat(port.getId()))
                    .add("cim:IdentifiedObject.mRID", port.getId())
                    .add("cim:IdentifiedObject.name", port.getName())
                    .add("cim:Terminal.ConnectivityNode", port.getLinks().get(0))
                    .add("cim:Terminal.ConductingEquipment", "cim:" + element.getId())
                    .add(RDF.TYPE, "cim:Terminal");
        }
        if (element.getType().equals("directory"))
            if (element.getDirectoryEntryId().equals("1eda194d-1c4d-4d98-b23e-803ef281d074"))
            /**CIM-ресурс PowerTransformerEnd*/
                for (Port port : element.getPorts()) {
                    for (PortFields field : port.getFields()) {
                        modelBuilder
                                .subject("cim:".concat(port.getId() + field.getDirectoryId() + "_" + voltage.get(element.getVoltageLevel())))
                                .add("cim:IdentifiedObject.mRID", port.getId() + field.getDirectoryId() + "_" + voltage.get(element.getVoltageLevel()))
                                .add(RDF.TYPE, "cim:PowerTransformerEnd")
                                .add("cim:TransformerEnd.BaseVoltage",voltage.get(element.getVoltageLevel()))
                                .add("cim:PowerTransformerEnd.PowerTransformer", element.getOperationName())
                                .add("cim:Terminal.ConnectivityNode", port.getLinks().get(0))
                                .add("cim:Terminal.ConductingEquipment", "cim:" + element.getId());
                    }
                }
        if (element.getType().equals("connectivity")) {
            modelBuilder
                    .subject("cim:".concat(element.getId()))
                    .add(RDF.TYPE, "cim:ConnectivityNode")
                    .add("cim:Terminal.ConductingEquipment", "cim:" + element.getId());
        }
    }

//    private void groupConnectivityElemetsByGraphAnalyzer(SingleLineDiagram sld) {
//        sld.getElements().stream()
//                .filter(e -> "connectivity".equals(e.getType()))
//                .forEach(e -> {
//                    /** Создание очереди и задали первый елемент e*/
//                    Deque<Elements> connectivityElements = new LinkedList<>() {{
//                        add(e);
//                    }};
//                    walkThroughSingelDiagram(connectivityElements);
//
//                });
//        System.out.println();
//    }
//
//
//    /**
//     * Рекурентный метод - вызывает сам себя
//     */
//    private void walkThroughSingelDiagram(Deque<Elements> connectivityElements) {
//        Elements connectivity = connectivityElements.pop();
//        connectivity.getPorts().forEach(p -> {
//            String link = p.getLinks().get(0);
//        });
//
//    }


    public String getResult(RDFFormat rdfFormat) {
        Model model = modelBuilder.build();

        if (rdfFormat.equals(RDFFormat.RDFXML)) {
            RdfWriter rdfWriter = new RdfWriter();
            return rdfWriter.writeXml(model);
        } else {
            OutputStream out = null;
            String cim;
            try {
                File tempFile = File.createTempFile("file", ".txt");
                out = new FileOutputStream(tempFile);
                Rio.write(model, out, cimNamespace, rdfFormat);
                cim = Files.readString(Path.of(tempFile.getPath()));
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage());
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return cim;
        }
    }

}



