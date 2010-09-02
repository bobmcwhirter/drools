package org.drools.integrationtests;

import junit.framework.TestCase;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Order;
import org.drools.OrderItem;
import org.drools.Order.OrderStatus;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mockito.Mockito;

public class MiscTest extends TestCase {

    public void testModifyWithRuleflowAndSubNetwork() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_ModifyWithRuleflowAndSubNetwork.drl",
                                                            getClass() ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "test_modifyWithRuleflowAndSubnetwork.rf",
                                                            getClass() ),
                      ResourceType.DRF );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( errors.size() > 0 ) {
            for ( KnowledgeBuilderError error : errors ) {
                System.err.println( error );
            }
            throw new IllegalArgumentException( "Could not parse knowledge." );
        }
        assertFalse( kbuilder.hasErrors() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        // create working memory mock listener
        org.drools.event.rule.WorkingMemoryEventListener wml = Mockito.mock( org.drools.event.rule.WorkingMemoryEventListener.class );
        org.drools.event.rule.AgendaEventListener ael = Mockito.mock( org.drools.event.rule.AgendaEventListener.class );

        ksession.addEventListener( wml );
        ksession.addEventListener( ael );

        Order order = new Order( 1,
                                 "bob" );
        OrderItem item = new OrderItem( order,
                                        1 );
        order.addItem( item );
        order.setStatus( new OrderStatus() );
        order.getStatus().setActive( true );
        org.drools.runtime.rule.FactHandle orderFH = ksession.insert( order );

        Order order2 = new Order( 2,
                                  "bob" );
        OrderItem item2 = new OrderItem( order,
                                         2 );
        order2.addItem( item2 );
        order2.setStatus( new OrderStatus() );
        order2.getStatus().setActive( true );
        org.drools.runtime.rule.FactHandle order2FH = ksession.insert( order2 );

        ksession.startProcess( "ruleflow" );

        int fired = ksession.fireAllRules();
        assertEquals( 4,
                      fired );

        // capture the arguments and check that the retracts happened
        //        ArgumentCaptor<org.drools.event.rule.ObjectRetractedEvent> retracts = ArgumentCaptor.forClass(org.drools.event.rule.ObjectRetractedEvent.class);
        //        verify( wml, times(2) ).objectRetracted( retracts.capture() );
        //        List<org.drools.event.rule.ObjectRetractedEvent> values = retracts.getAllValues();
        //        assertThat( values.get( 0 ).getFactHandle(), is( personFH ) );
        //        assertThat( values.get( 1 ).getFactHandle(), is( petFH ) );
    }

}
