package org.drools.bpel.core;

import org.drools.process.core.timer.Timer;
import org.drools.workflow.core.node.TimerNode;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class BPELWait extends TimerNode implements BPELActivity {

	private static final long serialVersionUID = 4L;

	private SourceLink[] sourceLinks;
    private TargetLink[] targetLinks;

    public SourceLink[] getSourceLinks() {
        return sourceLinks;
    }

    public void setSourceLinks(SourceLink[] sourceLinks) {
        this.sourceLinks = sourceLinks;
    }

    public TargetLink[] getTargetLinks() {
        return targetLinks;
    }

    public void setTargetLinks(TargetLink[] targetLinks) {
        this.targetLinks = targetLinks;
    }

	public void setForExpression(String forExpression) {
		Timer timer = new Timer();
		timer.setDelay(getDelayFor(forExpression));
		setTimer(timer);
	}

	public void setUntilExpression(String untilExpression) {
		Timer timer = new Timer();
		timer.setDelay(getDelayUntil(untilExpression));
		setTimer(timer);
	}
	
	private int getDelayFor(String forExpression) {
		// TODO: BPELPick timer delay
		return 1000;
	}
    
	private int getDelayUntil(String untilExpression) {
		// TODO: BPELPick timer until
		return 1000;
	}
    
}
