package ru.mpei.cimmaintainer.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import ru.mpei.cimmaintainer.dto.DeviceDirectory.Device;
import ru.mpei.cimmaintainer.dto.Viezdnoe.SingleLineDiagram;
import ru.mpei.cimmaintainer.dto.VoltageLevelDirectory.Voltage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class JsonMapper {
    private ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @SneakyThrows
    public SingleLineDiagram mapJsonToSld(String filePath) {
        return objectMapper.readValue(
                new File(filePath), SingleLineDiagram.class
        );
    }

    @SneakyThrows
    public Map<String, String> mapDeviceJsonToSld(String filePath) {
        Map<String, String> device = new HashMap<>();
        Device[] deviceType = objectMapper.readValue(new File(filePath), Device[].class);
        for (Device line : deviceType) {
            device.put(line.getId(), defineCimClassForDeviceType(line.getDeviceType()));
        }
        return device;
    }

    @SneakyThrows
    public Map<String, String> mapVoltageLevelJsonToSld(String filePath) {
        Map<String, String> voltage = new HashMap<>();
        Voltage[] voltageType = objectMapper.readValue(new File(filePath), Voltage[].class);
        for (Voltage line : voltageType) {
            voltage.put(line.getDirectoryId(), line.getValue().getEn());
        }
        return voltage;
    }

    private String defineCimClassForDeviceType(String deviceType){
        switch (deviceType){
            case "Load":
                return "EnergyConsumer";

            case "Generator":
                return "RotatingMachine";

            case "CableTransmissionLine2Ports":
            case "CableTransmissionLine":
            case "OverheadTransmissionLine2Ports":
            case "OverheadTransmissionLine":
                return "Line";

            case "DetachablePinConnection":
            case "IndoorCircuitBreaker":
            case "ModularSwitchboardWithFuse":
            case "ModularSwitchboard":
                return "Switch";

            case "Breaker":
                return "Breaker";
            case "Recloser":
                return "Recloser";
            case "Jumper":
                return "Jumper";
            case "Disconnector":
                return "Disconnector";
            case "Fuse":
                return "Fuse";
            case "WaveTrap":
                return "WaveTrap";
            case "GroundDisconnector":
                return "GroundDisconnector";

            case " Arrester":
                return "SurgeArrester";

            case "CouplingCapacitor":
            case "RfFilter":
                return "AuxiliaryEquipment";

            case "DualCurrentLimitingReactor":
            case "CurrentLimitingReactor":
            case "StaticCapacitorBank":
            case "ShuntReactor":
                return "ShuntCompensator";

            case "AutoTransformer":
            case "AutoTransformerWithTapChanger":
            case "ThreeWindingPowerTransformerWithTapChanger":
            case "TwoWindingPowerTransformer":
            case "SplitWindingTransformer":
            case "TwoWindingPowerTransformerWithTapChanger":
            case "ThreeWindingPowerTransformer":
            case "SplitWindingTransformerWithTapChanger":
                return "PowerTransformer";

            case "OpticalCurrentTransformer":
            case "SinglePhaseCurrentTransformer":
            case "ThreePhaseCurrentTransformer":
                return "CurrentTransformer";

            case "TwoWindingVoltageTransformer":
            case "ThreeWindingVoltageTransformer":
            case "OpticalVoltageTransformer":
            case "FourWindingVoltageTransformer":
                return "PotentialTransformer";

            case "EquivalentInjection":
            case "Grounding":
                return "ConductingEquipment";


            default: throw  new RuntimeException("В CIM нету такого типа: "+deviceType);
        }

    }
}
