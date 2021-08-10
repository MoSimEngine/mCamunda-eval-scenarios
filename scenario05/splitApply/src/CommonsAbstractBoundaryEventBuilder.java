package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.CatchEvent;
public abstract class CommonsAbstractBoundaryEventBuilder<B extends AbstractCatchEventBuilder<B, E>, E extends CatchEvent> extends AbstractCatchEventBuilder<B, BoundaryEvent> {
    protected CommonsAbstractBoundaryEventBuilder(BpmnModelInstance modelInstance, BoundaryEvent element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    @Override
    protected void setCoordinates(BpmnShape shape) {
        BpmnShape source = findBpmnShape(element);
        Bounds shapeBounds = shape.getBounds();
        double x = 0;
        double y = 0;
        if (source != null) {
            Bounds sourceBounds = source.getBounds();
            double sourceX = sourceBounds.getX();
            double sourceWidth = sourceBounds.getWidth();
            double sourceY = sourceBounds.getY();
            double sourceHeight = sourceBounds.getHeight();
            double targetHeight = shapeBounds.getHeight();
            x = (sourceX + sourceWidth) + (DomainAbstractBaseElementBuilder.SPACE / 4);
            y = ((sourceY + sourceHeight) - (targetHeight / 2)) + DomainAbstractBaseElementBuilder.SPACE;
        }
        shapeBounds.setX(x);
        shapeBounds.setY(y);
    }
}