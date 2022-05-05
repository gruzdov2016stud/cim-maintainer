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
import java.util.*;

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
        List<List<Elements>> groupsOfConnectivity = groupConnectivityElemetsByGraphAnalyzer(sld);
        ConnectivityNode(groupsOfConnectivity);
    }


    private void convertElementToRdfResource(
            Elements element,
            Map<String, String> devices,
            Map<String, String> voltage
    ) {
        modelBuilder
                .subject("cim:" + element.getId())
                .add("cim:IdentifiedObject.mRID", element.getId())
        /**каждому оборудованию подстанции присвоим соответствующий класс напряжения*/
//                .add("cim:ConductingEquipment.BaseVoltage", "cim:".concat(voltage.get(element.getVoltageLevel()))) //Для полученния rdf:resource="#110kV"
                .add("cim:ConductingEquipment.BaseVoltage", "cim:".concat(element.getVoltageLevel()));//Для полученния rdf:resource="#14"
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
                                .subject("cim:".concat(UUID.randomUUID().toString()))
                                .add("cim:IdentifiedObject.mRID", UUID.randomUUID().toString())
                                .add(RDF.TYPE, "cim:PowerTransformerEnd")
                                .add("cim:TransformerEnd.BaseVoltage", voltage.get(element.getVoltageLevel()))
                                .add("cim:PowerTransformerEnd.PowerTransformer", element.getOperationName())
                                .add("cim:Terminal.ConnectivityNode", port.getLinks().get(0))
                                .add("cim:Terminal.ConductingEquipment", "cim:" + element.getId());
                    }
                }
    }

    ///////////////////////////////////////////////////////////////////////////
    // todo Методы
    ///////////////////////////////////////////////////////////////////////////
    private void ConnectivityNode(List<List<Elements>> groupsOfConnectivity) {
        int i = 0;
        while (i < groupsOfConnectivity.size()) {
            modelBuilder
                    .subject("cim:" + groupsOfConnectivity.get(i).get(0).getId())
                    .add("cim:IdentifiedObject.name", i)
                    .add("cim:IdentifiedObject.projectID", "Substation Viezdnoe")
                    .add(RDF.TYPE, "cim:ConnectivityNode")
                    .add("cim:ConnectivityNode.BaseVoltage", groupsOfConnectivity.get(i).get(0).getVoltageLevel());
            int j = 0;
            while (j < groupsOfConnectivity.get(i).get(0).getPorts().size()) {
                if (groupsOfConnectivity.get(i).get(0).getPorts().get(j).getLinks().size() != 0) {
                    if (groupsOfConnectivity.get(i).get(0).getPorts().get(j).getLink().getSourcePort().getElement().getType().equals("directory")) {
                        modelBuilder
                                .subject("cim:" + groupsOfConnectivity.get(i).get(0).getPorts().get(j).getId())
                                .add("cim:IdentifiedObject.mRID", groupsOfConnectivity.get(i).get(0).getPorts().get(j).getId())
                                .add("cim:IdentifiedObject.name", groupsOfConnectivity.get(i).get(0).getPorts().get(j).getName())
                                .add(RDF.TYPE, "cim:Terminal")
                                .add("cim:TerminalConnectivityNode", groupsOfConnectivity.get(i).get(0).getId())
                                .add("cim:Terminal.ConductingEquipment", "cim:" + groupsOfConnectivity.get(i).get(0).getPorts().get(j).getLink().getSourcePort().getElement().getId());
                    }
                }
                j++;
            }
            i++;
        }
    }


    private List<List<Elements>> groupConnectivityElemetsByGraphAnalyzer(SingleLineDiagram sld) {
        /**Множество в котором не будет повторяющихся значений, пока мы обходи граф*/
        Set<String> visitedElementsIds = new HashSet<>();
        /**Группа List<Elements> groupOfConnectivityElements */
        List<List<Elements>> groupsOfConnectivityElements = new LinkedList<>();

        sld.getElements().stream()
                .filter(e -> "connectivity".equals(e.getType())) // Создаем массив из connectivity
                .filter(e -> !visitedElementsIds.contains(e.getId())) // Отбираем не посещённых
                .forEach(e -> { // Итеррируемся по отобранным элементам
                    /** Создание очереди.Из элементов, которые встретили пока шли по графу.
                     * Добавили только один элемент*/
                    Deque<Elements> connectivityElements = new LinkedList<>() {{
                        add(e);
                    }}; // Deque - стек у которого забираем последнего
                    /**Группа из коннективити, которые надо преобразовать в один коннективити*/
                    List<Elements> groupOfConnectivityElements = new LinkedList<>();
                    walkThroughSingelDiagram(connectivityElements, visitedElementsIds, groupOfConnectivityElements);
                    groupsOfConnectivityElements.add(groupOfConnectivityElements);

                });
        return groupsOfConnectivityElements;
    }

    /**
     * Рекурентный метод - вызывает сам себя
     */
    private void walkThroughSingelDiagram(
            Deque<Elements> connectivityElements,
            Set<String> visitedElementsIds,
            List<Elements> groupOfConnectivityElements
    ) {
        Elements connectivity = connectivityElements.pop();
        /**Если мы не посещали это множество, то добавляем ID. Когда мы достаём элемент connectivity,
         * то помещаем как посещенный  */
        visitedElementsIds.add(connectivity.getId());
        groupOfConnectivityElements.add(connectivity);
        connectivity.getPorts().forEach(p -> {
            Links link = p.getLink();
            if (link == null) return;
            Elements sibling = link.getSourcePortId().equals(p.getId()) ? link.getTarget() : link.getSource();
            /**Проверка на connectivity и мы не посетили очередь */
            if ("connectivity".equals(sibling.getType()) && !visitedElementsIds.contains(sibling.getId())) {
                connectivityElements.add(sibling);
                walkThroughSingelDiagram(connectivityElements, visitedElementsIds, groupOfConnectivityElements);
            }
        });

    }


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



