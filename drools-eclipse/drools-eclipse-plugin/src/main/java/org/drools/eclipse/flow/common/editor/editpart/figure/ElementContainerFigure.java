package org.drools.eclipse.flow.common.editor.editpart.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.swt.graphics.Image;

public class ElementContainerFigure extends Figure implements ElementFigure {
    
    private IFigure pane;
    private boolean selected = false;
    private Label label = new Label();
    
    public ElementContainerFigure() {
        setSize(200, 150);
        ScrollPane scrollpane = new ScrollPane();
        pane = new FreeformLayer();
        pane.setLayoutManager(new FreeformLayout());
        setLayoutManager(new StackLayout());
        add(scrollpane);
        IFigure panel = new Layer();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setMajorAlignment(FlowLayout.ALIGN_CENTER);
        panel.setLayoutManager(flowLayout);
        panel.add(label);
    	add(panel);
        scrollpane.setViewport(new FreeformViewport());
        scrollpane.setContents(pane);
        setBorder(new LineBorder(1));
    }

    public Label getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setIcon(Image icon) {
        // Do nothing
    }

    public void setSelected(boolean b) {
        this.selected = b;
        ((LineBorder) getBorder()).setWidth(b ? 3 : 1);
    }

    public void setText(String text) {
        label.setText(text);
    }
    
    public IFigure getPane() {
        return pane;
    }

}
