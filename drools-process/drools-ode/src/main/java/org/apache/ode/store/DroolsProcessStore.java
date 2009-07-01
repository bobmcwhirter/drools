package org.apache.ode.store;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessState;
import org.apache.ode.bpel.iapi.ProcessStore;
import org.apache.ode.bpel.iapi.ProcessStoreListener;
import org.drools.RuleBase;
import org.drools.definition.process.Process;
import org.drools.ode.core.BPELProcess;
import org.drools.ode.instance.EmbeddedBPELServer;
import org.drools.rule.Package;
import org.w3c.dom.Node;

public class DroolsProcessStore implements ProcessStore {

    private Map<QName, ProcessConf> processes = new HashMap<QName, ProcessConf>();
    private Map<String, DeploymentUnitDir> deploymentUnits = new HashMap<String, DeploymentUnitDir>();
	private EmbeddedBPELServer server;
	
	public DroolsProcessStore(EmbeddedBPELServer server) {
		this.server = server;
	}
	
	public ProcessConf getProcessConfiguration(QName processId) {
		return processes.get(processId);
	}

	public List<QName> getProcesses() {
		return new ArrayList<QName>(processes.keySet());
	}

	public void setKnowledgeBase(RuleBase ruleBase) {
		for (Package kpackage: ruleBase.getPackages()) {
			for (Process process: kpackage.getRuleFlows().values()) {
				if (BPELProcess.ODE_TYPE.equals(process.getType())) {
					BPELProcess bpelProcess = (BPELProcess) process;
					ProcessConf processConf = bpelProcess.getProcessConf();
					if (bpelProcess.getProcessConf() == null) {
						try {
							List<BPELProcess> processes = BPELResourceTypeBuilder.readProcessesFromJar(
								new ByteArrayInputStream(bpelProcess.getBPELJar()), process.getName());
							for (BPELProcess p: processes) {
								if (p.getId().equals(bpelProcess.getId())) {
									processConf = p.getProcessConf();
									bpelProcess.setProcessConf(processConf);
									break;
								}
							}
						} catch (Throwable t) {
							System.err.println("Could not initialize BPEL process " + process.getId());
							t.printStackTrace();
						}
					}
					if (processConf != null) {
						processes.put(processConf.getProcessId(), processConf);
						server.register(processConf);
					} else {
						System.err.println("BPEL process not found: " + process.getId());
					}
				}
			}
		}
		// TODO: add listener for dynamically added processes
	}

//    protected List<ProcessConfImpl> load(DeploymentUnitDAO dudao) {
//        File dudir = new File(dudao.getDeploymentUnitDir());
//        if (dudir == null || !dudir.exists()) {
//            throw new IllegalArgumentException("Deployed directory " + dudir + " no longer there!");
//        }
//        DeploymentUnitDir dud = new DeploymentUnitDir(dudir);
//        dud.scan();
//
//        ArrayList<ProcessConfImpl> loaded = new ArrayList<ProcessConfImpl>();
//        deploymentUnits.put(dud.getName(), dud);
//        long version = 0;
//        for (ProcessConfDAO p : dudao.getProcesses()) {
//            TDeployment.Process pinfo = dud.getProcessDeployInfo(p.getType());
//            if (pinfo == null) {
//                System.err.println("Cannot load " + p.getPID() + "; cannot find descriptor.");
//                continue;
//            }
//            Map<QName, Node> props = calcInitialProperties(pinfo);
//            ProcessConfImpl pconf = new ProcessConfImpl(
//        		p.getPID(), p.getType(), p.getVersion(), dud, pinfo, 
//        		dudao.getDeployDate(), props, p.getState(), null);
//            version = p.getVersion();
//            processes.put(pconf.getProcessId(), pconf);
//            loaded.add(pconf);
//        }
//        // All processes and the DU have the same version
//        dud.setVersion(version);
//        return loaded;
//    }
//
//    public static Map<QName, Node> calcInitialProperties(TDeployment.Process dd) {
//        HashMap<QName, Node> ret = new HashMap<QName, Node>();
//        if (dd.getPropertyList().size() > 0) {
//            for (TDeployment.Process.Property property : dd.getPropertyList()) {
//                Element elmtContent = DOMUtils.getElementContent(property.getDomNode());
//                if (elmtContent != null) {
//                    // We'll need DOM Level 3
//                    Document doc = DOMUtils.newDocument();
//                    doc.appendChild(doc.importNode(elmtContent, true));
//                    ret.put(property.getName(), doc.getDocumentElement());
//                } else
//                    ret.put(property.getName(), property.getDomNode().getFirstChild());
//
//            }
//        }
//        return ret;
//    }

	public List<QName> listProcesses(String packageName) {
		DeploymentUnitDir du = deploymentUnits.get(packageName);
        if (du == null) {
            return null;
        }
        return toPids(du.getProcessNames(), du.getVersion());
    }

	public Collection<String> getPackages() {
        return new ArrayList<String>(deploymentUnits.keySet());
	}

    private List<QName> toPids(Collection<QName> processTypes, long version) {
        ArrayList<QName> result = new ArrayList<QName>();
        for (QName pqName : processTypes) {
            result.add(toPid(pqName, version));
        }
        return result;
    }

    private QName toPid(QName processType, long version) {
        return new QName(processType.getNamespaceURI(), processType.getLocalPart() + "-" + version);
    }
    
	public void registerListener(ProcessStoreListener listener) {
	}

	public void unregisterListener(ProcessStoreListener arg0) {
	}

    public Collection<QName> deploy(File arg0) {
		throw new UnsupportedOperationException();
	}

	public long getCurrentVersion() {
		throw new UnsupportedOperationException();
	}

	public void setProperty(QName arg0, QName arg1, String arg2) {
		throw new UnsupportedOperationException();
	}

	public void setProperty(QName arg0, QName arg1, Node arg2) {
		throw new UnsupportedOperationException();
	}

	public void setRetiredPackage(String arg0, boolean arg1) {
		throw new UnsupportedOperationException();
	}

	public void setState(QName arg0, ProcessState arg1) {
		throw new UnsupportedOperationException();
	}

	public Collection<QName> undeploy(File arg0) {
		throw new UnsupportedOperationException();
	}

}
