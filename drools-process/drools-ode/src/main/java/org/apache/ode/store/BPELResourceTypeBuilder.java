package org.apache.ode.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.dd.DeployDocument;
import org.apache.ode.bpel.dd.TDeployment;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.EndpointReference;
import org.apache.ode.bpel.iapi.EndpointReferenceContext;
import org.apache.ode.bpel.iapi.ProcessConf;
import org.apache.ode.bpel.iapi.ProcessState;
import org.apache.ode.utils.DOMUtils;
import org.drools.builder.ResourceConfiguration;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.ResourceTypeBuilder;
import org.drools.compiler.RuleFlowLoadError;
import org.drools.io.InternalResource;
import org.drools.io.Resource;
import org.drools.ode.core.BPELProcess;
import org.drools.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class BPELResourceTypeBuilder implements ResourceTypeBuilder {

	private PackageBuilder packageBuilder;
	
	public void addKnowledgeResource(Resource resource, ResourceType type, ResourceConfiguration configuration) {
		try {
			generatePackage();
			System.out.println("Adding BPEL resource " + resource);
			URL url = ((InternalResource) resource).getURL();
			File file = null;
            if ( "file".equals( url.getProtocol() ) ) {
                file = new File( StringUtils.toURI( url.toString() ).getSchemeSpecificPart() );
            } else {
            	throw new IllegalArgumentException("ODE BPEL resource is not a file! " + resource);
            }
			if ( ((InternalResource) resource).isDirectory() ) {
                List<BPELProcess> processes = readProcesses(file);
                for (BPELProcess process: processes) {
                	packageBuilder.getPackage().addProcess(process);
                }
                
			} else {
				List<BPELProcess> processes = readProcessesFromJar(resource.getInputStream(), file.getName());
				byte[] bytes = getBytesFromFile(file);
                for (BPELProcess process: processes) {
                	process.setBPELJar(bytes);
                	packageBuilder.getPackage().addProcess(process);
                }
			}
			// TODO: handle exceptions
			// throw new IllegalArgumentException("ODE BPEL resource is not a directory! " + resource);
		} catch (Exception e) {
			packageBuilder.getErrors().add(new RuleFlowLoadError("Exception while loading BPEL resource " + resource, e));
		}
	}
	
	private void generatePackage() {
		try {
			packageBuilder.addPackageFromDrl(new StringReader("package org.drools.ode"));
		} catch (DroolsParserException e) {
			// should never occur
            e.printStackTrace( System.err );
		} catch (IOException e) {
			// should never occur
            e.printStackTrace( System.err );
		}
	}
	
	public static List<BPELProcess> readProcessesFromJar(InputStream input, String name) throws IOException {
		// unzip file in temp dir
		ZipInputStream inputStream = new ZipInputStream(input);
		ZipEntry ze = null;
		String tmpDirName =
			System.getProperty("java.io.tmpdir") + File.separator + 
			"Drools_ODE" + File.separator + name + File.separator;
		File tmpDir = new File(tmpDirName);
		tmpDir.mkdirs();
		List<File> tmpFiles = new ArrayList<File>();
		while ((ze = inputStream.getNextEntry()) != null) {
			String tmpFileName = ze.getName();
			int index = tmpFileName.lastIndexOf(File.separator);
			if (index != -1) {
				tmpFileName = tmpFileName.substring(index + 1);
			}
			File tmpFile = new File(tmpDirName + tmpFileName);
			tmpFile.createNewFile();
			FileOutputStream fout = new FileOutputStream(tmpFile);
			for (int c = inputStream.read(); c != -1; c = inputStream.read()) {
				fout.write(c);
			}
			inputStream.closeEntry();
			fout.close();
			tmpFiles.add(tmpFile);
		}
		inputStream.close();
		// parse processes
		return readProcesses(tmpDir);
	}
	
	private static List<BPELProcess> readProcesses(File dir) {
        List<ProcessConf> processes = deploy(dir);
        List<BPELProcess> result = new ArrayList<BPELProcess>();
        for (ProcessConf ODEprocess: processes) {
    		BPELProcess process = new BPELProcess();
    		process.setId(ODEprocess.getProcessId().toString());
    		for (Endpoint endpoint: ODEprocess.getProvideEndpoints().values()) {
        		process.setName(endpoint.serviceName.toString());
        		process.setPackageName(endpoint.serviceName.getNamespaceURI());
    		}
    		process.setProcessConf(ODEprocess);
    		System.out.println("Adding process " + process.getId());
    		result.add(process);
        }
        return result;
	}

	public void setPackageBuilder(PackageBuilder packageBuilder) {
		this.packageBuilder = packageBuilder;
	}

	private static List<ProcessConf> deploy(File deploymentUnitDirectory) {
		int version = 1;
	    // Create the DU and compile/scan it before acquiring lock.
	    final DeploymentUnitDir du = new DeploymentUnitDir(deploymentUnitDirectory);
	    try {
	        du.compile();
	    } catch (CompilationException ce) {
	        throw new ContextException("Deployment failed due to compile errors", ce);
	    }
	
	    du.scan();
	    final DeployDocument dd = du.getDeploymentDescriptor();
	    final List<ProcessConf> processes = new ArrayList<ProcessConf>();
	
        for (TDeployment.Process processDD : dd.getDeploy().getProcessList()) {
            QName pid = toPid(processDD.getName(), version);
            ProcessConfImpl pconf = new ProcessConfImpl(
        		pid, processDD.getName(), version, du, processDD, new Date(),
                calcInitialProperties(processDD), ProcessState.ACTIVE, 
                new EndpointReferenceContext() {
                    public EndpointReference resolveEndpointReference(Element element) {
                        return null;
                    }
                    public EndpointReference convertEndpoint(QName qName, Element element) {
                    	return null;
                	}
                    @SuppressWarnings("unchecked")
        			public Map getConfigLookup(EndpointReference epr) {
                        return Collections.EMPTY_MAP;
                    }
                }
            );
            processes.add(pconf);
        }
        return processes;
	}

    public static Map<QName, Node> calcInitialProperties(TDeployment.Process dd) {
        HashMap<QName, Node> ret = new HashMap<QName, Node>();
        if (dd.getPropertyList().size() > 0) {
            for (TDeployment.Process.Property property : dd.getPropertyList()) {
                Element elmtContent = DOMUtils.getElementContent(property.getDomNode());
                if (elmtContent != null) {
                    // We'll need DOM Level 3
                    Document doc = DOMUtils.newDocument();
                    doc.appendChild(doc.importNode(elmtContent, true));
                    ret.put(property.getName(), doc.getDocumentElement());
                } else
                    ret.put(property.getName(), property.getDomNode().getFirstChild());

            }
        }
        return ret;
    }

    private static QName toPid(QName processType, long version) {
        return new QName(processType.getNamespaceURI(), processType.getLocalPart() + "-" + version);
    }
    
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
        long length = file.length();
        byte[] bytes = new byte[(int)length];
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
        is.close();
        return bytes;
    }
    
}
