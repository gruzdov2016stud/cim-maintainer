package ru.mpei.cimmaintainer;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.junit.jupiter.api.Test;
import ru.mpei.cimmaintainer.converter.SldToCimConverter;
import ru.mpei.cimmaintainer.dto.Viezdnoe.SingleLineDiagram;
import ru.mpei.cimmaintainer.mapper.JsonMapper;

import java.util.Map;

public class TestClass {

    @Test
    public void test() {
        JsonMapper jsonMapper = new JsonMapper();
        SingleLineDiagram sld = jsonMapper.mapJsonToSld("src/test/resources/Viezdnoe.json");

        JsonMapper deviceMapper = new JsonMapper();
        Map<String, String> deviceDirectory = deviceMapper.mapDeviceJsonToSld("src/test/resources/DeviceDirectory.json");

        JsonMapper voltageLevelMapper = new JsonMapper();
        Map<String, String> voltageLevel = voltageLevelMapper.mapVoltageLevelJsonToSld("src/test/resources/VoltageLevelDirectory.json");

        SldToCimConverter converter = new SldToCimConverter();

        converter.convert(sld, deviceDirectory, voltageLevel);
        String cimModel = converter.getResult(RDFFormat.RDFXML);

        System.out.println();
    }

}
