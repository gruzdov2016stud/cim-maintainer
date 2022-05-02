package ru.mpei.cimmaintainer.dto.Viezdnoe;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;

import java.util.List;


@Getter @Setter
public class Elements extends Identifier {
    /**ИД для связи с DeviceDirectory.json*/
    private String directoryEntryId;
    /**Значение уровня напряжения с VoltageLevelDirectory.json*/
    private String voltageLevel;
    /**ИД для связи с VoltageLevelDirectory.json*/
    private String operationName;
    private String projectName;
    /**"directory" либо "connectivity" либо "bus" */
    private String type;
    /**Порты которые присуствую в каждом из  type*/
    private List<Port> ports;
    /**Порты которые присуствую в каждом из  type*/
    private List<Fields> fields;


}
