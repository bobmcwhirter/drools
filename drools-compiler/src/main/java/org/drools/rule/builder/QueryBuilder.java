package org.drools.rule.builder;

import org.drools.RuntimeDroolsException;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.base.FieldFactory;
import org.drools.base.ValueType;
import org.drools.base.evaluators.Operator;
import org.drools.base.extractors.ArrayElementReader;
import org.drools.compiler.DescrBuildError;
import org.drools.lang.descr.QueryDescr;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.spi.FieldValue;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.ObjectType;

public class QueryBuilder {
    public Pattern build(final RuleBuildContext context,
                         final QueryDescr queryDescr) {
        ObjectType objectType = new ClassObjectType( DroolsQuery.class );
        final Pattern pattern = new Pattern( context.getNextPatternId(),
                                             0, // offset is 0 by default
                                             objectType,
                                             null );
        
        final InternalReadAccessor extractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, objectType, "name", null, true );

        final FieldValue field = FieldFactory.getFieldValue( queryDescr.getName(),
                                                             ValueType.STRING_TYPE );

        final LiteralConstraint constraint = new LiteralConstraint( extractor,
                                                                    context.getConfiguration().getEvaluatorRegistry().getEvaluator( ValueType.STRING_TYPE,
                                                                                                                                    Operator.EQUAL ),
                                                                    field );
        
        PatternBuilder.registerReadAccessor( context, objectType, "name", constraint );

        // adds appropriate constraint to the pattern
        pattern.addConstraint( constraint );

        InternalReadAccessor arrayExtractor = PatternBuilder.getFieldReadAccessor( context, queryDescr, objectType, "arguments", null, true );

        String[] params = queryDescr.getParameters();
        String[] types = queryDescr.getParameterTypes();
        int i = 0;
        try {
            for ( i = 0; i < params.length; i++ ) {
                Declaration declr = pattern.addDeclaration( params[i] );
                
                // this bit is different, notice its the ArrayElementReader that we wire up to, not the declaration.
                ArrayElementReader reader = new ArrayElementReader( arrayExtractor,
                                        i,
                                        context.getDialect().getTypeResolver().resolveType( types[i] ) );
                PatternBuilder.registerReadAccessor( context, objectType, "arguments", reader );
                
                declr.setReadAccessor( reader );
            }
        } catch ( ClassNotFoundException e ) {
            context.getErrors().add( new DescrBuildError( context.getParentDescr(),
                                                          queryDescr,
                                                          e,
                                                          "Unable to resolve type '" + types[i] + " for parameter" + params[i] ) );
        }
        return pattern;
    }
}
