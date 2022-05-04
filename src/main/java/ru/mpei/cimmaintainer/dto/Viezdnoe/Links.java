package ru.mpei.cimmaintainer.dto.Viezdnoe;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;

@Getter @Setter
public class Links extends Identifier {
    private String sourceId;
    private String targetId;
    private String sourcePortId;
    private String targetPortId;


    private Port sourcePort;
    private Port targetPort;
    private Elements source;
    private Elements target;
}
