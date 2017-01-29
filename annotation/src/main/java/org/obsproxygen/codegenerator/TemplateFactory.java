package org.obsproxygen.codegenerator;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by thku on 28.12.16.
 */
public class TemplateFactory {


    private static Template TEMPLATE;
    private static final String TEMPLATE_PATH = "/templates/observable.ftl";


    public static synchronized Template getTemplate() throws IOException {
        if (TEMPLATE == null) {

            final InputStream resourceAsStream = TemplateFactory.class
                    .getResourceAsStream(TEMPLATE_PATH);
            TEMPLATE = new Template("name", new InputStreamReader(resourceAsStream),
                    new Configuration(Configuration.VERSION_2_3_22));
        }
        return TEMPLATE;
    }
}
