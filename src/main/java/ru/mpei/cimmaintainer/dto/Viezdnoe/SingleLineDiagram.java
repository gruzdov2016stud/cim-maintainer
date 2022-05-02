package ru.mpei.cimmaintainer.dto.Viezdnoe;

import lombok.Getter;
import lombok.Setter;
import ru.mpei.cimmaintainer.dto.Viezdnoe.Elements;
import ru.mpei.cimmaintainer.dto.Viezdnoe.Links;

import java.util.List;

@Getter @Setter
public class SingleLineDiagram {
    private List<Links> links;
    private List<Elements> elements;
}
