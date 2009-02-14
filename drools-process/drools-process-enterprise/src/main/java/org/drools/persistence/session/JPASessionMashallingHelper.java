package org.drools.persistence.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.marshalling.DefaultMarshaller;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallingConfiguration;

public class JPASessionMashallingHelper {

    private RuleBase             ruleBase;
    private SessionConfiguration conf;
    private StatefulSession      session;
    private Marshaller           marshaller;
    
    /**
     * Exist Info, so load session from here
     * @param info
     * @param ruleBase
     * @param conf
     * @param marshallingConfiguration
     */
    public JPASessionMashallingHelper(SessionInfo info,
                                      RuleBase    ruleBase,
                                      SessionConfiguration conf,
                                      MarshallingConfiguration marshallingConfiguration) {   
        this.ruleBase = session.getRuleBase();
        this.conf = conf;
        this.marshaller = new DefaultMarshaller( ((InternalRuleBase) ruleBase).getConfiguration(),
                                                 marshallingConfiguration );
        loadSnapshot( info.getData() );       
        info.setJPASessionMashallingHelper( this );         
    }    

    /** 
     * new session, don't write now as info will request it on update callback
     * @param info
     * @param session
     * @param conf
     * @param marshallingConfiguration
     */
    public JPASessionMashallingHelper(StatefulSession session,
                                      SessionConfiguration conf,
                                      MarshallingConfiguration marshallingConfiguration) {   
        this.session = session;     
        this.ruleBase = session.getRuleBase();
        this.conf = conf;
        this.marshaller = new DefaultMarshaller( ((InternalRuleBase) ruleBase).getConfiguration(),
                                                 marshallingConfiguration );   
    }

    public byte[] getSnapshot() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            marshaller.write( baos,
                              (InternalRuleBase) ruleBase,
                              session );
        } catch ( IOException e ) {
            throw new RuntimeException( "Unable to get session snapshot",
                                        e );
        }

        return baos.toByteArray();
    }

    public StatefulSession loadSnapshot(byte[] bytes,
                                        StatefulSession session) {
        this.session = session;
        ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
        try {
            marshaller.read( bais,
                             (InternalRuleBase) ruleBase,
                             session );
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to load session snapshot",
                                        e );
        }
        return this.session;
    }

    public StatefulSession loadSnapshot(byte[] bytes) {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream( bytes );
            if ( this.session == null ) {
                this.session = this.ruleBase.readStatefulSession( bais,
                                                                  marshaller );
            } else {
                loadSnapshot( bytes,
                              this.session );
            }
        } catch ( Exception e ) {
            throw new RuntimeException( "Unable to load session snapshot",
                                        e );
        }
        return this.session;
    }

    public StatefulSession getObject() {
        return session;
    }

}
