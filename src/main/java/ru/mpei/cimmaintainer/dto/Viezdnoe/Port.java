package ru.mpei.cimmaintainer.dto.Viezdnoe;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;

import java.util.List;

@Getter @Setter
public class Port extends Identifier {
    private String name;
    private List<String> links;
    private List<PortFields> fields;
    private Links link;
    private Port port;
}
