package org.drools.ode.instance;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.wsdl.Definition;
import javax.wsdl.PortType;
import javax.xml.namespace.QName;

import org.apache.axis2.engine.AxisConfiguration;
import org.apache.ode.axis2.ExternalService;
import org.apache.ode.axis2.SoapExternalService;
import org.apache.ode.axis2.httpbinding.HttpExternalService;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactory;
import org.apache.ode.bpel.dao.BpelDAOConnectionFactoryJDBC;
import org.apache.ode.bpel.engine.BpelServerImpl;
import org.apache.ode.bpel.epr.EndpointFactory;
import org.apache.ode.bpel.iapi.BindingContext;
import org.apache.ode.bpel.iapi.BpelEventListener;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.EndpointReferenceContext;
import org.apache.ode.bpel.iapi.Message;
import org.apache.ode.bpel.iapi.MessageExchangeContext;
import org.apache.ode.bpel.iapi.MyRoleMessageExchange;
import org.apache.ode.bpel.iapi.PartnerRoleChannel;
import org.apache.ode.bpel.iapi.PartnerRoleMessageExchange;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.Scheduler;
import org.apache.ode.bpel.memdao.BpelDAOConnectionFactoryImpl;
import org.apache.ode.dao.jpa.BPELDAOConnectionFactoryImpl;
import org.apache.ode.il.EmbeddedGeronimoFactory;
import org.apache.ode.il.MockScheduler;
import org.apache.ode.store.DroolsProcessStore;
import org.apache.ode.utils.DOMUtils;
import org.apache.ode.utils.GUID;
import org.apache.ode.utils.wsdl.WsdlUtils;
import org.drools.WorkingMemory;
import org.hsqldb.jdbc.jdbcDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EmbeddedBPELServer {

    private BpelServerImpl _server;
    private DroolsProcessStore _store;
    private TransactionManager _txManager;
    private DataSource _dataSource;
    private SchedulerWrapper _scheduler;
    private BpelDAOConnectionFactory _daoCF;
    private EndpointReferenceContext _eprContext;
	private MessageExchangeContext _mexContext;
	private BindingContext _bindContext;
	private HashMap<String, QName> _activated = new HashMap<String, QName>();
	protected ExecutorService _executorService;

    public EmbeddedBPELServer(WorkingMemory workingMemory) {
        try {
            _server = new BpelServerImpl();
            createTransactionManager();
            createDataSource();
            createDAOConnection();
            createScheduler();
            if (_daoCF == null) {
                throw new RuntimeException("No DAO");
            }
            _server.setDaoConnectionFactory(_daoCF);
            _server.setInMemDaoConnectionFactory(new BpelDAOConnectionFactoryImpl(_scheduler));
            if (_scheduler == null)
                throw new RuntimeException("No scheduler");
            _store = new DroolsProcessStore(this);
            _server.setScheduler(_scheduler);
            _server.setEndpointReferenceContext(createEndpointReferenceContext());
            _server.setMessageExchangeContext(createMessageExchangeContext());
            _server.setBindingContext(createBindingContext());
            ThreadFactory threadFactory = new ThreadFactory() {
                int threadNumber = 0;
                public Thread newThread(Runnable r) {
                    threadNumber += 1;
                    Thread t = new Thread(r, "ODEServer-"+threadNumber);
                    t.setDaemon(true);
                    return t;
                }
            };
            _executorService = Executors.newCachedThreadPool(threadFactory);
            _server.init();
            _server.start();
            _store.setKnowledgeBase(workingMemory.getRuleBase());
        } catch (Exception except) {
            System.err.println(except.getMessage());
            except.printStackTrace(System.err);
            throw new RuntimeException(except);
        }
    }

    public Collection<QName> deploy(File deploymentUnitDirectory) {
        Collection<QName> pids = _store.deploy(deploymentUnitDirectory);
        for (QName pid: pids)
            _server.register(_store.getProcessConfiguration(pid));
        return pids;
    }
    
    public void register(ProcessConf processConf) {
    	_server.register(processConf);
    }

    public void invoke(QName serviceName, String opName, Element body) throws Exception {
        try {
            String messageId = new GUID().toString();
            MyRoleMessageExchange mex;

            _txManager.begin();
            mex = _server.getEngine().createMessageExchange("" + messageId, serviceName, opName);
            if (mex.getOperation() == null)
                throw new Exception("Did not find operation " + opName + " on service " + serviceName);
            Message request = mex.createMessage(mex.getOperation().getInput().getMessage().getQName());
            request.setMessage(body);
            mex.invoke(request);
            mex.complete();
            _txManager.commit();
        } catch (Exception except) {
              _txManager.rollback();
              throw except;
        }
    }

    public TransactionManager getTransactionManager() {
        return _txManager;
    }

    public void waitForBlocking() {
        try {
            long delay = 1000;
            while (true) {
                // Be warned: ugly hack and not safe for slow CPUs.
                long cutoff = System.currentTimeMillis() - delay;
                if (_scheduler._nextSchedule < cutoff)
                    break;
                Thread.sleep(delay);
            }
        } catch (InterruptedException except) { }
    }

    public void shutdown() throws Exception {
        _server.stop();
        _scheduler.stop();
        _scheduler.shutdown();
    }

    protected TransactionManager createTransactionManager() throws Exception {
        EmbeddedGeronimoFactory factory = new EmbeddedGeronimoFactory();
        _txManager = factory.getTransactionManager();
        _txManager.setTransactionTimeout(30);
        return _txManager;
    }

    protected DataSource createDataSource() throws Exception {
        jdbcDataSource hsqlds = new jdbcDataSource();
        hsqlds.setDatabase("jdbc:hsqldb:mem:" + new GUID().toString());
        hsqlds.setUser("sa");
        hsqlds.setPassword("");
        _dataSource = hsqlds;
        return _dataSource;
    }

    protected Scheduler createScheduler() throws Exception {
        if (_server == null)
            throw new RuntimeException("No BPEL server");
        if (_txManager == null)
            throw new RuntimeException("No transaction manager");
        if (_dataSource == null)
            throw new RuntimeException("No data source");
        _scheduler = new SchedulerWrapper(_server, _txManager, _dataSource);
        return _scheduler;
    }

    protected BpelDAOConnectionFactory createDAOConnection() throws Exception {
        if (_txManager == null)
            throw new RuntimeException("No transaction manager");
        if (_dataSource == null)
            throw new RuntimeException("No data source");

        BpelDAOConnectionFactoryJDBC daoCF = new BPELDAOConnectionFactoryImpl();
        daoCF.setDataSource(_dataSource);
        daoCF.setTransactionManager(_txManager);
        Properties props = new Properties();
        props.put("openjpa.Log", "log4j");
        props.put("openjpa.jdbc.SynchronizeMappings", "buildSchema(ForeignKeys=false)");
        daoCF.init(props);
        _daoCF = daoCF;

        return _daoCF;
    }

    protected EndpointReferenceContext createEndpointReferenceContext() {
        _eprContext = new EndpointReferenceContext() {
            public EndpointReference resolveEndpointReference(Element element) {
            	return EndpointFactory.createEndpoint(element);
            }
            public EndpointReference convertEndpoint(QName qName, Element element) {
            	System.out.println("convertEndpoint " + qName + " " + element);
            	return null;
        	}
            @SuppressWarnings("unchecked")
			public Map getConfigLookup(EndpointReference epr) {
            	System.out.println("getConfigLookup " + epr);
                return Collections.EMPTY_MAP;
            }
        };
        return _eprContext;
    }

    protected MessageExchangeContext createMessageExchangeContext() {
       _mexContext =  new MessageExchangeContext() {
            public void invokePartner(PartnerRoleMessageExchange mex) { 
            	ExternalService service = (ExternalService) mex.getChannel();
                System.out.println("The service to invoke is the external service " + service.getServiceName()+":"+service.getPortName());
                service.invoke(mex);
            }
            public void onAsyncReply(MyRoleMessageExchange myRoleMex) { }
        };
        return _mexContext;
    }

    protected BindingContext createBindingContext() {
        _bindContext = new BindingContext() {
            public EndpointReference activateMyRoleEndpoint(QName processId, Endpoint myRoleEndpoint) {
                final Document doc = DOMUtils.newDocument();
                Element serviceRef = doc.createElementNS(EndpointReference.SERVICE_REF_QNAME.getNamespaceURI(),
                    EndpointReference.SERVICE_REF_QNAME.getLocalPart());
                serviceRef.appendChild(doc.createTextNode(myRoleEndpoint.serviceName.toString()));
                doc.appendChild(serviceRef);
                _activated.put(myRoleEndpoint.toString(), processId);
                return new EndpointReference() {
                    public Document toXML() { return doc; }
                };
            }

            public void deactivateMyRoleEndpoint(Endpoint myRoleEndpoint) {
                _activated.remove(myRoleEndpoint);
            }

            public PartnerRoleChannel createPartnerRoleChannel(QName processId, PortType portType,
                                                               final Endpoint initialPartnerEndpoint) {
            	System.out.println("createPartnerRoleChannel " + initialPartnerEndpoint);
            	ProcessConf pconf = _store.getProcessConfiguration(processId);
                Definition wsdl = pconf.getDefinitionForService(initialPartnerEndpoint.serviceName);
                if (wsdl == null) {
                    throw new ContextException("Cannot find definition for service " + initialPartnerEndpoint.serviceName
                                               + " in the context of process "+processId);
                }
                return createExternalService(pconf, initialPartnerEndpoint.serviceName, initialPartnerEndpoint.portName);
//                
//                final EndpointReference epr = new EndpointReference() {
//                    public Document toXML() {
//                        Document doc = DOMUtils.newDocument();
//                        Element serviceRef = doc.createElementNS(EndpointReference.SERVICE_REF_QNAME.getNamespaceURI(),
//                            EndpointReference.SERVICE_REF_QNAME.getLocalPart());
//                        serviceRef.appendChild(doc.createTextNode(initialPartnerEndpoint.serviceName.toString()));
//                        doc.appendChild(serviceRef);
//                        return doc;
//                    }
//                };
//                _endpoints.put(initialPartnerEndpoint.serviceName.toString(), epr);
//                return new PartnerRoleChannel() {
//                    public EndpointReference getInitialEndpointReference() { return epr; }
//                    public void close() { };
//                };
            }
        };
        return _bindContext;
    }
    
    public ExternalService createExternalService(ProcessConf pconf, QName serviceName, String portName) throws ContextException {
        ExternalService extService = null;
        Definition def = pconf.getDefinitionForService(serviceName);
        try {
            if (WsdlUtils.useHTTPBinding(def, serviceName, portName)) {
                extService = new HttpExternalService(
            		pconf, serviceName, portName, _executorService, _scheduler, _server);
            } else if (WsdlUtils.useSOAPBinding(def, serviceName, portName)) {
                extService = new SoapExternalService(
            		pconf, serviceName, portName, _executorService, new AxisConfiguration(), _scheduler, _server);
            }
        } catch (Exception ex) {
            throw new ContextException("Error creating external service! name:"+serviceName+", port:"+portName, ex);
        }

        // if not SOAP nor HTTP binding
        if (extService==null) {
            throw new ContextException("Only SOAP and HTTP binding supported!");
        }
        return extService;
    }

    public void addEventListener(BpelEventListener listener) {
    	_server.registerBpelEventListener(listener);
    }


    private class SchedulerWrapper implements Scheduler {

        MockScheduler _scheduler;
        long                _nextSchedule;

        SchedulerWrapper(BpelServerImpl server, TransactionManager txManager, DataSource dataSource) {
            ExecutorService executorService = Executors.newCachedThreadPool();
            _scheduler = new MockScheduler(_txManager);
            _scheduler.setExecutorSvc(executorService);
            _scheduler.setJobProcessor(server);
        }

        public String schedulePersistedJob(Map<String,Object>jobDetail,Date when) throws ContextException {
            String jobId = _scheduler.schedulePersistedJob(jobDetail, when);
            _nextSchedule = when == null ?  System.currentTimeMillis() : when.getTime();
            return jobId;
        }

        public String scheduleVolatileJob(boolean transacted, Map<String,Object> jobDetail) throws ContextException {
            String jobId = _scheduler.scheduleVolatileJob(transacted, jobDetail);
            _nextSchedule = System.currentTimeMillis();
            return jobId;
        }

        public void cancelJob(String jobId) throws ContextException {
            _scheduler.cancelJob(jobId);
        }

        public <T> T execTransaction(Callable<T> transaction) throws Exception, ContextException {
            return _scheduler.execTransaction(transaction);
        }

        public <T> Future<T> execIsolatedTransaction(Callable<T> transaction) throws Exception, ContextException {
            return _scheduler.execIsolatedTransaction(transaction);
        }

        public boolean isTransacted() {
            return _scheduler.isTransacted();
        }

        public void start() { _scheduler.start(); }
        public void stop() { _scheduler.stop(); }
        public void shutdown() { _scheduler.shutdown(); }

        public void registerSynchronizer(Synchronizer synch) throws ContextException {
            _scheduler.registerSynchronizer(synch);
        }

        public void setJobProcessor(JobProcessor processor) throws ContextException {
            _scheduler.setJobProcessor(processor);

        }
    }

}