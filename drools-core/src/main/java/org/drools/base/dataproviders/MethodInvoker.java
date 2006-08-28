package org.drools.base.dataproviders;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.base.evaluators.DateFactory;
import org.drools.base.resolvers.LiteralValue;
import org.drools.base.resolvers.ValueHandler;
import org.drools.rule.Declaration;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

public class MethodInvoker {
    private final ValueHandler   instanceValueHandler;
    private final Method         method;
    private final ValueHandler[] valueHandlers;
    private final Declaration[]  requiredDeclarations;

    /**
     * Method invoker for static method
     */
    public MethodInvoker(String methodName,
                         Class clazz,
                         ValueHandler[] valueHandlers,
                         Declaration[] requiredDeclarations) {
        this( methodName, clazz, null, valueHandlers, requiredDeclarations );
        // @todo : defensive errors for referencing non static method
    }
    
    /**
     * Method invoker for an instance
     */
    public MethodInvoker(String methodName,
                         Class clazz,
                         ValueHandler instanceValueHandler,
                         ValueHandler[] valueHandlers,
                         Declaration[] requiredDeclarations) {
        this.instanceValueHandler = instanceValueHandler;
        this.valueHandlers = valueHandlers;
        this.requiredDeclarations = requiredDeclarations;

        this.method = configureMethod( clazz,
                                       methodName,
                                       valueHandlers.length );
    }

    /**
     * work out what method we will be calling at runtime, based on the name and number of parameters.
     */
    private Method configureMethod(Class clazz,
                                   String methodName,
                                   int numOfArgs) {
        Method[] methods = clazz.getMethods();
        for ( int i = 0; i < methods.length; i++ ) {
            if ( methods[i].getName().equals( methodName ) ) {
                if ( methods[i].getParameterTypes().length == numOfArgs ) {
                    return methods[i];
                }
            }
        }
        throw new IllegalArgumentException( "Unable to resolve the method: [" + methodName + "] on class: [" + clazz.getName() + "] with " + numOfArgs + " parameters." );
    }

    public Declaration[] getRequiredDeclarations() {
        return requiredDeclarations;
    }

    public Object invoke(Tuple tuple,
                         WorkingMemory wm,
                         PropagationContext ctx) {

        //get the instance that we are operating on
        Object instance = this.instanceValueHandler.getValue( tuple,
                                                              wm );

        if ( instance == null ) {
            throw new IllegalArgumentException( "Unable to resolve the variable: [" + this.instanceValueHandler + "]" );
        }

        //the types we have to convert the arguments to
        Class[] parameterTypes = this.method.getParameterTypes();

        //the args values that we will pass
        Object[] args = new Object[this.valueHandlers.length];

        //now we need to set all the values, convert if literal
        for ( int i = 0; i < this.valueHandlers.length; i++ ) {
            ValueHandler handler = valueHandlers[i];
            if ( handler instanceof LiteralValue ) {
                String text = (String) handler.getValue( tuple,
                                                         wm );
                Class type = parameterTypes[i];
                if ( type == String.class ) {
                    args[i] = text;
                } else {
                    args[i] = convert( text,
                                       type );
                }
            } else {
                args[i] = handler.getValue( tuple,
                                            wm );
            }
        }

        Object result = null;
        //now the actual invoking of the method
        try {
            result = this.method.invoke( instance,
                                         args );
            // @todo : should wrap and return an exception that the user understands
        } catch ( IllegalArgumentException e ) {
            throw new RuntimeDroolsException( e );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeDroolsException( e );
        } catch ( InvocationTargetException e ) {
            throw new RuntimeDroolsException( e );
        }

        return result;
    }

    /** 
     * Attempt to convert text to the target class type 
     */
    private Object convert(String text,
                           Class type) {
        if ( type == Integer.class || type == int.class ) {
            return new Integer( text );
        } else if ( text == "null" ) {
            return null;
        } else if ( type == Character.class || type == char.class ) {
            return (new Character( text.charAt( 0 ) ));
        } else if ( type == Short.class || type == short.class ) {
            return new Short( text );
        } else if ( type == Long.class || type == long.class ) {
            return new Long( text );
        } else if ( type == Float.class || type == float.class ) {
            return new Float( text );
        } else if ( type == Double.class || type == double.class ) {
            return new Double( text );
        } else if ( type == Boolean.class || type == boolean.class ) {
            return new Boolean( text );
        } else if ( type == Date.class ) {
            return DateFactory.parseDate( text );
        } else if ( type == BigDecimal.class ) {
            return new BigDecimal( text );
        } else if ( type == BigInteger.class ) {
            return new BigInteger( text );
        } else {
            throw new IllegalArgumentException( "Unable to convert [" + text + "] to type: [" + type.getName() + "]" );
        }
    }
}
