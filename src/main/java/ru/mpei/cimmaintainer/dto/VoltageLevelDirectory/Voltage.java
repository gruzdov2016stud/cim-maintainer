package ru.mpei.cimmaintainer.dto.VoltageLevelDirectory;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;
import ru.mpei.cimmaintainer.dto.Value;

import java.util.List;

@Getter
@Setter
public class Voltage extends Identifier {
    private Value value;
    private String directoryId;
}
