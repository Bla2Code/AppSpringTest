package com.appspring.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * Helper-класс, содержащий вспомогательные методы для использования в связке с SpringFramework BeanUtils
 *
 * @see BeanUtils
 */
@Slf4j
public class BeanUtilsHelper {

    /**
     * Метод получает массив из имен полей объекта, значения которых равны null.
     *
     * @param source        - объект источник, имена полей которого необходимо получить
     * @param ignoredFields - дополнительный список игнорируемых полей
     * @return String[] - массив имен неинициализированных полей объекта
     */
    public static String[] getNullPropertyNames(Object source, String... ignoredFields) {
        var wrappedSource = new BeanWrapperImpl(source);
        var foundFields = Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .collect(toList());
        foundFields.addAll(Arrays.asList(ignoredFields));
        return foundFields.toArray(new String[0]);
    }
}