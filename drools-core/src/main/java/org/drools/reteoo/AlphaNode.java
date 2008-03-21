package org.drools.reteoo;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.Externalizable;

import org.drools.FactException;
import org.drools.RuleBaseConfiguration;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;
import org.drools.util.FactEntry;
import org.drools.util.FactHashTable;
import org.drools.util.Iterator;

/**
 * <code>AlphaNodes</code> are nodes in the <code>Rete</code> network used
 * to apply <code>FieldConstraint<.code>s on asserted fact
 * objects where the <code>FieldConstraint</code>s have no dependencies on any other of the facts in the current <code>Rule</code>.
 *
 *  @see AlphaNodeFieldConstraint
 *
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class AlphaNode extends RightTupleSource
    implements
    RightTupleSinkNode,
    NodeMemory {

    /**
     *
     */
    private static final long        serialVersionUID = 400L;

    /** The <code>FieldConstraint</code> */
    private AlphaNodeFieldConstraint constraint;

    private RightTupleSinkNode       previousRightTupleSinkNode;
    private RightTupleSinkNode       nextRightTupleSinkNode;

    public AlphaNode() {

    }

    /**
     * Construct an <code>AlphaNode</code> with a unique id using the provided
     * <code>FieldConstraint</code> and the given <code>ObjectSource</code>.
     * Set the boolean flag to true if the node is supposed to have local
     * memory, or false otherwise. Memory is optional for <code>AlphaNode</code>s
     * and is only of benefic when adding additional <code>Rule</code>s at runtime.
     *
     * @param id Node's ID
     * @param constraint Node's constraints
     * @param objectSource Node's object source
     * @param hasMemory true if node shall be configured with local memory. False otherwise.
     */
    public AlphaNode(final int id,
                     final AlphaNodeFieldConstraint constraint,
                     final RightTupleSource objectSource,
                     final BuildContext context) {
        super( id,
               objectSource,
               context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.constraint = constraint;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        constraint = (AlphaNodeFieldConstraint) in.readObject();
        previousRightTupleSinkNode = (RightTupleSinkNode) in.readObject();
        nextRightTupleSinkNode = (RightTupleSinkNode) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( constraint );
        out.writeObject( previousRightTupleSinkNode );
        out.writeObject( nextRightTupleSinkNode );
    }

    /**
     * Retruns the <code>FieldConstraint</code>
     *
     * @return <code>FieldConstraint</code>
     */
    public AlphaNodeFieldConstraint getConstraint() {
        return this.constraint;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.source.addObjectSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) throws FactException {
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
        if ( this.constraint.isAllowed( factHandle,
                                        workingMemory,
                                        memory.context ) ) {

            this.sink.propagateAssertFact( factHandle,
                                           context,
                                           workingMemory );
        }
    }

    public void retractRightTuple(RightTuple rightTuple,
                                  PropagationContext context,
                                  InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException( "AlphaNode.retractRightTuple is not supported." );
    }

    public void updateSink(final RightTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
        // get the objects from the parent
        RightTupleSinkUpdateAdapter adapter = new RightTupleSinkUpdateAdapter( sink,
                                                                               this.constraint );
        this.source.updateSink( adapter,
                                context,
                                workingMemory );
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeObjectSink( (RightTupleSink) node );
        }
        if ( !this.isInUse() ) {
            for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
                workingMemories[i].clearNodeMemory( this );
            }
        }
        this.source.remove( context,
                            builder,
                            this,
                            workingMemories );
    }

    /**
     * Creates a HashSet for the AlphaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        AlphaMemory memory = new AlphaMemory();
        memory.context = this.constraint.createContextEntry();
        return memory;
    }

    /**
     * @inheritDoc
     */
    protected void addObjectSink(final RightTupleSink objectSink) {
        super.addObjectSink( objectSink );
    }

    public String toString() {
        return "[AlphaNode(" + this.id + ") constraint=" + this.constraint + "]";
    }

    public int hashCode() {
        return this.source.hashCode() * 17 + ((this.constraint != null) ? this.constraint.hashCode() : 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof AlphaNode) ) {
            return false;
        }

        final AlphaNode other = (AlphaNode) object;

        return this.source.equals( other.source ) && this.constraint.equals( other.constraint );
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public RightTupleSinkNode getNextRightTupleSinkNode() {
        return this.nextRightTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextRightTupleSinkNode(final RightTupleSinkNode next) {
        this.nextRightTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public RightTupleSinkNode getPreviousRightTupleSinkNode() {
        return this.previousRightTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousRightTupleSinkNode(final RightTupleSinkNode previous) {
        this.previousRightTupleSinkNode = previous;
    }

    public static class AlphaMemory
        implements
        Externalizable {
        private static final long serialVersionUID = -5852576405010023458L;

        public ContextEntry       context;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = (ContextEntry) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     * @author mproctor
     *
     */
    private static class RightTupleSinkUpdateAdapter
        implements
        RightTupleSink {
        private final RightTupleSink           sink;
        private final AlphaNodeFieldConstraint constraint;

        public RightTupleSinkUpdateAdapter(final RightTupleSink sink,
                                           final AlphaNodeFieldConstraint constraint) {
            this.sink = sink;
            this.constraint = constraint;
        }

        public void assertFact(final InternalFactHandle handle,
                               final PropagationContext propagationContext,
                               final InternalWorkingMemory workingMemory,
                               final ContextEntry contextEntry) {

            if ( this.constraint.isAllowed( handle,
                                            workingMemory,
                                            contextEntry ) ) {
                this.sink.assertObject( handle,
                                        propagationContext,
                                        workingMemory );
            }
        }

        public void retractRightTuple(final RightTuple rightTuple,
                                      final PropagationContext context,
                                      final InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "RightTupleSinkUpdateAdapter.retractFact is not supported." );
        }

        public int getId() {
            return 0;
        }

        public void assertObject(InternalFactHandle factHandle,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "RightTupleSinkUpdateAdapter.assertFact is not supported." );
        }
    }
}
