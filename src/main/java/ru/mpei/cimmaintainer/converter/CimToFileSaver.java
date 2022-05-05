package ru.mpei.cimmaintainer.converter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CimToFileSaver {
    public static void writeXML(String cimModel){
        try (FileOutputStream fos = new FileOutputStream(
                "D:\\YandexDisk\\Project\\CIM_OWL\\cim-maintainer\\src\\test\\resources\\cim-model.xml")){
            byte[] buffer = cimModel.getBytes(StandardCharsets.UTF_8);
            fos.write(buffer, 0, buffer.length);
            System.out.println("Файл записан");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
