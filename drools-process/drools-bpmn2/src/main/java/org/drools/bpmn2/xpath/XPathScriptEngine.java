package org.drools.bpmn2.xpath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;
import javax.xml.xpath.XPathVariableResolver;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class XPathScriptEngine extends AbstractScriptEngine implements Compilable {

    private ScriptEngineFactory factory;

    private XPathFactory xpathFactory;

    public static final String XPATH_RESULT_TYPE = "com.sun.script.xpath.resultType";

    public static final String XPATH_INPUT_SRC = "com.sun.script.xpath.inputSource";

    public static final String XMLNS_COLON = "xmlns:";

    public static final String XPATH_CONTEXT_PREFIX = "context";

    public static final String XPATH_CONTEXT_URI = "http://www.sun.com/java/jsr223/xpath/context";

    public XPathScriptEngine() {
        xpathFactory = XPathFactory.newInstance();
    }

    private class XPathCompiledScript extends CompiledScript {

        private XPathExpression expr;

        XPathCompiledScript(XPathExpression expr) {
            this.expr = expr;
        }

        public ScriptEngine getEngine() {
            return XPathScriptEngine.this;
        }

        public Object eval(ScriptContext ctx) throws ScriptException {
            return evalXPath(expr, ctx);
        }
    }

    public CompiledScript compile(String script) throws ScriptException {
        XPathExpression expr = compileXPath(script, context);
        return new XPathCompiledScript(expr);
    }

    public CompiledScript compile(Reader reader) throws ScriptException {
        return compile(readFully(reader));
    }

    public Object eval(String str, ScriptContext ctx) throws ScriptException {
        XPathExpression expr = compileXPath(str, ctx);
        return evalXPath(expr, ctx);
    }

    public Object eval(Reader reader, ScriptContext ctx) throws ScriptException {
        return eval(readFully(reader), ctx);
    }

    public ScriptEngineFactory getFactory() {
        synchronized (this) {
            if (factory == null) {
                factory = new XPathScriptEngineFactory();
            }
        }
        return factory;
    }

    public Bindings createBindings() {
        return new SimpleBindings();
    }

    void setFactory(ScriptEngineFactory factory) {
        this.factory = factory;
    }

    private static Object findVariable(QName qname, ScriptContext ctx) {
        String name;
        int scope;

        if (XPATH_CONTEXT_URI.equals(qname.getNamespaceURI())) {
            name = qname.getLocalPart();
            synchronized (ctx) {
                scope = ctx.getAttributesScope(name);
                if (scope != -1) {
                    return ctx.getAttribute(name, scope);
                }
            }
        }
        if (qname.getPrefix() == null || "".equals(qname.getPrefix())) {
            name = qname.getLocalPart();
        } else {
            name = qname.getPrefix() + ":" + qname.getLocalPart();
        }
        synchronized (ctx) {
            scope = ctx.getAttributesScope(name);
            if (scope != -1) {
                return ctx.getAttribute(name, scope);
            }
        }
        return null;
    }

    private static NamespaceContext makeNamespaceContext(ScriptContext ctx) {

        final Map<String, String> namespaces = new HashMap<String, String>();
        for (int scope : ctx.getScopes()) {
            Bindings bind = ctx.getBindings(scope);
            if (bind != null) {
                // TODO collectNamespaces(namespaces, bind);
            }
        }
        Object def = ctx.getAttribute(XMLConstants.XMLNS_ATTRIBUTE);
        if (def instanceof String) {
            namespaces.put(XMLConstants.DEFAULT_NS_PREFIX, (String) def);
        }

        namespaces.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        namespaces.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);

        namespaces.put(XPATH_CONTEXT_PREFIX, XPATH_CONTEXT_URI);

        return new NamespaceContext() {

            public String getNamespaceURI(String prefix) {
                if (prefix == null) {
                    throw new IllegalArgumentException();
                }
                String uri = namespaces.get(prefix);
                if (uri == null) {
                    return XMLConstants.NULL_NS_URI;
                } else {
                    return uri;
                }
            }

            public String getPrefix(String namespaceURI) {
                if (namespaceURI == null) {
                    throw new IllegalArgumentException();
                }
                for (String prefix : namespaces.keySet()) {
                    String uri = namespaces.get(prefix);
                    if (namespaceURI.equals(uri)) {
                        return prefix;
                    }
                }
                return null;
            }

            public Iterator<String> getPrefixes(String namespaceURI) {
                if (namespaceURI == null) {
                    throw new IllegalArgumentException();
                }
                List<String> list = new ArrayList<String>();
                for (String prefix : namespaces.keySet()) {
                    String uri = namespaces.get(prefix);
                    if (namespaceURI.equals(uri)) {
                        list.add(prefix);
                    }
                }
                return Collections.unmodifiableList(list).iterator();
            }
        };
    }

    private static XPathFunction makeXPathFunction(
            final Constructor<XPathFunction> ctr, int arity) {
        if (ctr.getParameterTypes().length != arity) {
            return null;
        }
        return new XPathFunction() {
            @SuppressWarnings("unchecked")
            public Object evaluate(List args) {
                try {
                    return ctr.newInstance(args.toArray());
                } catch (Exception exp) {
                    throw new RuntimeException(exp);
                }
            }
        };
    }

    private static XPathFunction makeXPathFunction(final Method method, int arity) {
        int modifiers = method.getModifiers();
        int numArgs = method.getParameterTypes().length;
        if (Modifier.isStatic(modifiers) && numArgs == arity) {
            return new XPathFunction() {
                @SuppressWarnings("unchecked")
                public Object evaluate(List args) {
                    try {
                        return method.invoke(null, args.toArray());
                    } catch (Exception exp) {
                        throw new RuntimeException(exp);
                    }
                }
            };
        } else if ((numArgs + 1) == arity) {
            return new XPathFunction() {
                @SuppressWarnings("unchecked")
                public Object evaluate(List args) {
                    List tmp = args.subList(1, args.size());
                    try {
                        return method.invoke(args.get(0), tmp.toArray());
                    } catch (Exception exp) {
                        throw new RuntimeException(exp);
                    }
                }
            };
        } else {
            return null;
        }
    }

    private static XPathFunction makeXPathFunction(final String funcName,
            final Invocable invocable) {
        return new XPathFunction() {
            @SuppressWarnings("unchecked")
            public Object evaluate(List args) {
                try {
                    return invocable.invokeFunction(funcName, args.toArray());
                } catch (Exception exp) {
                    throw new RuntimeException(exp);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    private static XPathFunction makeXPathFunction(QName qname, Object obj, int arity) {
        if (obj == null) {
            return null;
        } else if (obj instanceof XPathFunction) {
            return (XPathFunction) obj;
        } else if (obj instanceof Method) {
            return makeXPathFunction((Method) obj, arity);
        } else if (obj instanceof Constructor) {
            return makeXPathFunction((Constructor<XPathFunction>) obj, arity);
        } else if (obj instanceof Invocable) {
            return makeXPathFunction(qname.getLocalPart(), (Invocable) obj);
        } else {
            return null;
        }
    }

    private XPathExpression compileXPath(String str, final ScriptContext ctx)
            throws ScriptException {
        try {
            XPath xpath = xpathFactory.newXPath();
            xpath.setXPathVariableResolver(new XPathVariableResolver() {
                public Object resolveVariable(QName qname) {
                    return findVariable(qname, ctx);
                }
            });
            xpath.setXPathFunctionResolver(new XPathFunctionResolver() {
                public XPathFunction resolveFunction(QName qname, int arity) {
                    Object obj = findVariable(qname, ctx);
                    return makeXPathFunction(qname, obj, arity);
                }
            });
            int begin = str.indexOf("getProcessProperty") > -1 ? 19 : 0;
            if (begin > 0) {
              String objectDataRef = str.substring(begin + 1, str.indexOf(")") - 1);
              Document objectData = (Document) ctx.getAttribute(objectDataRef);
              ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
              Source xmlSource = new DOMSource(objectData);
              Result outputTarget = new StreamResult(outputStream);
              TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
              InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
              ctx.setReader(new InputStreamReader(is));
              str = str.substring(str.indexOf(")") + 1);
            }
            xpath.setNamespaceContext(makeNamespaceContext(ctx));
            XPathExpression xpe = xpath.compile(str);
            return xpe;
        } catch (Exception exp) {
            throw new ScriptException(exp);
        }
    }

    private Object getVariable(ScriptContext ctx, String name) {
        synchronized (ctx) {
            int scope = ctx.getAttributesScope(name);
            if (scope != -1) {
                return ctx.getAttribute(name, scope);
            }
        }
        return null;
    }

    private Object evalXPath(XPathExpression expr, final ScriptContext ctx) throws ScriptException {
        try {
            Object resultType = getVariable(ctx, XPATH_RESULT_TYPE);
            Object input = getVariable(ctx, XPATH_INPUT_SRC);
            InputSource src;
            if (input == null) {
                src = new InputSource(ctx.getReader());
            } else {
                if (input instanceof InputSource) {
                    src = (InputSource) input;
                } else if (input instanceof String) {
                    src = new InputSource((String) input);
                } else if (input instanceof Reader) {
                    src = new InputSource((Reader) input);
                } else if (input instanceof InputStream) {
                    src = new InputSource((InputStream) input);
                } else {
                    src = null;
                }
            }
            resultType = XPathConstants.BOOLEAN;
            if (resultType instanceof QName) {
                return (src != null) ? expr.evaluate(src, (QName) resultType)
                        : expr.evaluate(input, (QName) resultType);
            } else {
                return (src != null) ? expr.evaluate(src) : expr
                        .evaluate(input);
            }
        } catch (Exception exp) {
            throw new ScriptException(exp);
        }
    }

    private String readFully(Reader reader) throws ScriptException {
        char[] arr = new char[8 * 1024];
        StringBuilder buf = new StringBuilder();
        int numChars;
        try {
            while ((numChars = reader.read(arr, 0, arr.length)) > 0) {
                buf.append(arr, 0, numChars);
            }
        } catch (IOException exp) {
            throw new ScriptException(exp);
        }
        return buf.toString();
    }
    
}
