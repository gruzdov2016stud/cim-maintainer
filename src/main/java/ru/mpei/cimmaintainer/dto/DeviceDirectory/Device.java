package ru.mpei.cimmaintainer.dto.DeviceDirectory;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;
import ru.mpei.cimmaintainer.dto.Value;
import java.util.List;
@Getter
@Setter
public class Device extends Identifier {
    private String deviceType;
    private Value name;
    private List<FieldsDevice> fields;
}
