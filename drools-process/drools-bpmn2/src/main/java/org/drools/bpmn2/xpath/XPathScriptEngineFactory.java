package org.drools.bpmn2.xpath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

public class XPathScriptEngineFactory implements ScriptEngineFactory {

    private static List<String> names;
    private static List<String> extensions;
    private static List<String> mimeTypes;
    static {
        names = new ArrayList<String>(1);
        names.add("xpath");
        names = Collections.unmodifiableList(names);
        extensions = names;
        mimeTypes = new ArrayList<String>(0);
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
    
    public String getEngineName() {
        return "xpath";
    }

    public String getEngineVersion() {
        return "1.0";
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public String getLanguageName() {
        return "jaxp";
    }

    public String getLanguageVersion() {
        return "1.4";
    }

    public String getMethodCallSyntax(String obj, String m, String... args) {
        throw new UnsupportedOperationException("getMethodCallSyntax");
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public List<String> getNames() {
        return names;
    }

    public String getOutputStatement(String str) {
        throw new UnsupportedOperationException("getOutputStatement");
    }

    public String getParameter(String key) {
        if (key.equals(ScriptEngine.ENGINE)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
            return getEngineVersion();
        } else if (key.equals(ScriptEngine.NAME)) {
            return getEngineName();
        } else if (key.equals(ScriptEngine.LANGUAGE)) {
            return getLanguageName();
        } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return getLanguageVersion();
        } else if (key.equals("THREADING")) {
            return "MULTITHREADED";
        } else {
            return null;
        }
    }

    public String getProgram(String... statements) {
        throw new UnsupportedOperationException("getProgram");
    }

    public ScriptEngine getScriptEngine() {
        XPathScriptEngine engine = new XPathScriptEngine();
        engine.setFactory(this);
        return engine;
    }
    
}
