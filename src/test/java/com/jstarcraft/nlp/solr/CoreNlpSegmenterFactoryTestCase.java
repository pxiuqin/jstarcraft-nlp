package com.jstarcraft.nlp.solr;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.jstarcraft.nlp.solr.corenlp.CoreNlpSegmentFactory;

public class CoreNlpSegmenterFactoryTestCase extends NlpSegmenterFactoryTestCase {

    @Override
    protected NlpSegmentFactory getSegmenterFactory() throws Exception {
        Properties keyValues = new Properties();
        keyValues.load(this.getClass().getResourceAsStream("/StanfordCoreNLP-chinese.properties"));
        Map<String, String> configurations = new LinkedHashMap<>();
        for (Entry<Object, Object> keyValue : keyValues.entrySet()) {
            String key = String.valueOf(keyValue.getKey());
            String value = String.valueOf(keyValue.getValue());
            configurations.put(key, value);
        }
        CoreNlpSegmentFactory factory = new CoreNlpSegmentFactory(configurations);
        return factory;
    }

}
