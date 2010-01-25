package org.drools.process.workitem.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

public class ArchiveWorkItemHandler implements WorkItemHandler {

	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String archive = (String) workItem.getParameter("Archive");
		List<File> files = (List<File>) workItem.getParameter("Files");
		
		try {
	        OutputStream outputStream = new FileOutputStream( new File( archive) );	        
	        ArchiveOutputStream os = new ArchiveStreamFactory().createArchiveOutputStream("tar", outputStream);
	        
			if (files != null) {
				for (File file: files) {
			        final TarArchiveEntry entry = new TarArchiveEntry("testdata/test1.xml");
			        entry.setModTime(0);
			        entry.setSize(file.length());
			        entry.setUserId(0);
			        entry.setGroupId(0);
			        entry.setMode(0100000);
			        os.putArchiveEntry(entry);
			        IOUtils.copy(new FileInputStream(file), os);
				}
			}
	        os.closeArchiveEntry();
	        os.close();
			manager.completeWorkItem(workItem.getId(), null);
		} catch (Throwable t) {
			t.printStackTrace();
			manager.abortWorkItem(workItem.getId());
		}
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do nothing, this work item cannot be aborted
	}

}
