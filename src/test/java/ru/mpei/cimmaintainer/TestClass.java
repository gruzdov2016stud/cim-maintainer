package ru.mpei.cimmaintainer;

import lombok.SneakyThrows;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Test;
import ru.mpei.cimmaintainer.binder.ElementsBinder;
import ru.mpei.cimmaintainer.converter.CimToFileSaver;
import ru.mpei.cimmaintainer.converter.RdfMaintainer;
import ru.mpei.cimmaintainer.converter.SldFromCimBuilder;
import ru.mpei.cimmaintainer.converter.SldToCimConverter;
import ru.mpei.cimmaintainer.dto.Viezdnoe.SingleLineDiagram;
import ru.mpei.cimmaintainer.mapper.JsonMapper;

import java.util.Map;

public class TestClass {

    @SneakyThrows
    @Test
    public void test()  {
        JsonMapper jsonMapper = new JsonMapper();
        SingleLineDiagram sld = jsonMapper.mapJsonToSld("src/test/resources/Viezdnoe.json");
        ElementsBinder.bind(sld);

        JsonMapper deviceMapper = new JsonMapper();
        Map<String, String> deviceDirectory = deviceMapper.mapDeviceJsonToSld("src/test/resources/DeviceDirectory.json");

        JsonMapper voltageLevelMapper = new JsonMapper();
        Map<String, String> voltageLevel = voltageLevelMapper.mapVoltageLevelJsonToSld("src/test/resources/VoltageLevelDirectory.json");

        SldToCimConverter converter = new SldToCimConverter();
        converter.convert(sld, deviceDirectory, voltageLevel);

        String cimModel = converter.getResult(RDFFormat.RDFXML);

        CimToFileSaver.writeXML(cimModel);
        Model model = RdfMaintainer.rdfMaintainer(
                "D:\\YandexDisk\\Project\\CIM_OWL\\cim-maintainer\\src\\test\\resources\\cim-model.xml",
                "https://iec.ch/TC57/2013/CIM-schema-cim16#");
        SldFromCimBuilder.sparQL(model);
        System.out.println();
    }

}
