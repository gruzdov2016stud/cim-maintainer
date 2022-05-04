package ru.mpei.cimmaintainer.binder;

import ru.mpei.cimmaintainer.dto.Viezdnoe.Links;
import ru.mpei.cimmaintainer.dto.Viezdnoe.SingleLineDiagram;

import java.util.HashMap;
import java.util.Map;


public class ElementsBinder {
    public static void bind(SingleLineDiagram sld){
        Map<String, Links> linkIdToLinkMap = new HashMap<>();
        /**Добавим в Map все links */
        sld.getLinks().forEach(links -> linkIdToLinkMap.put(links.getId(),links));
        /**Проитеррируемся по всем элеентам и всем портам этих элементов*/
        sld.getElements().forEach(elements -> elements.getPorts().forEach(port -> {
            port.setElement(elements);
            /**Достанем ID links, которые связаны с портами. Также проиницаилизируем лист в Port.java*/
            if(port.getLinks().isEmpty()) return;
            String linkID = port.getLinks().get(0);
            if(linkID == null) return;
            /**Если link есть, то достаем linkID из MAP */
            Links link = linkIdToLinkMap.get(linkID);
            /**Свяжем link с портом*/
            port.setLink(link);

            if(link.getSourcePortId().equals(port.getId())){
                link.setSourcePort(port);
                link.setSource(elements);
            } else{
                link.setTargetPort(port);
                link.setTarget(elements);
            }
        }));



    }
}
