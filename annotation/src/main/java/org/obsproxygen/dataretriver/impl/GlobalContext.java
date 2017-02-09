package org.obsproxygen.dataretriver.impl;

import static org.obsproxygen.dataretriver.impl.GenerateObservableAnnotationProcessor.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Thomas.Kummer on 07.02.2017.
 */
public class GlobalContext {

    private final Map<String, Object> templateData = new HashMap<>();
    private List<Map<String, Object>> properties = new ArrayList<>();
    private final Map<String, GetterType> hasGetter = new HashMap<>();

    public GlobalContext() {
        this.properties = new ArrayList<>();
        templateData.put("properties",properties);
    }

    public Map<String, Object> getTemplateData() {
        return templateData;
    }

    public List<Map<String, Object>> getProperties() {
        return properties;
    }

    public Map<String, GetterType> getHasGetter() {
        return hasGetter;
    }
}
