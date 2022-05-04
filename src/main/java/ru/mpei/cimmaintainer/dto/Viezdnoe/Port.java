package ru.mpei.cimmaintainer.dto.Viezdnoe;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Identifier;

import java.util.LinkedList;
import java.util.List;

@Getter @Setter
public class Port extends Identifier {
    private String name;
    private List<String> links = new LinkedList<>();
    private List<PortFields> fields;

    private Links link;
    /**
     *     Ссылка на элемент чьим портом он является
     */
    private Elements element;
}
