package org.obsproxygen.codegenerator;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

/**
 * Created by thku on 28.12.16.
 */
public class CodeGenerator {

    private static final boolean DEBUG = false;

    private Filer filer;
    private Messager messager;


    public CodeGenerator(Filer filer, Messager messager) {
        this.filer = filer;
        this.messager = messager;
    }

    /**
     * Generate java class with template data
     *
     * @param templateData template data
     */
    public void generateCode(Template template,String outputFileName , Map<String, Object> templateData) {
        try {
                JavaFileObject jfo = filer
                        .createSourceFile(outputFileName);
                Writer writer = jfo.openWriter();
                template.process(templateData, writer);
                writer.flush();
                writer.close();
                if (DEBUG) {
                    final StringWriter out = new StringWriter();
                    template.process(templateData, out);
                    out.flush();
                    System.out.println(out);
                }
        } catch (TemplateException | IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Annotation Processor:" + e.getLocalizedMessage());
            //messager.printMessage(Diagnostic.Kind.ERROR, ExceptionUtils.getFullStackTrace(e));
        }
    }

}
