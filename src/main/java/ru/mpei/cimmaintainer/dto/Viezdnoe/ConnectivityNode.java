package ru.mpei.cimmaintainer.dto.Viezdnoe;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ConnectivityNode extends Identifier {
    List<Port> ports = new ArrayList<>();
}
