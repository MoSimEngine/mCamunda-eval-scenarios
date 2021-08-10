package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;
public abstract class ParadigmAbstractBoundaryEventBuilder<B extends CommonsAbstractBoundaryEventBuilder<B>> extends CommonsAbstractBoundaryEventBuilder<B> {
    protected ParadigmAbstractBoundaryEventBuilder(BpmnModelInstance modelInstance, BoundaryEvent element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    @Override
    protected void setWaypointsWithSourceAndTarget(BpmnEdge edge, FlowNode edgeSource, FlowNode edgeTarget) {
        BpmnShape source = findBpmnShape(edgeSource);
        BpmnShape target = findBpmnShape(edgeTarget);
        if ((source != null) && (target != null)) {
            Bounds sourceBounds = source.getBounds();
            Bounds targetBounds = target.getBounds();
            double sourceX = sourceBounds.getX();
            double sourceY = sourceBounds.getY();
            double sourceWidth = sourceBounds.getWidth();
            double sourceHeight = sourceBounds.getHeight();
            double targetX = targetBounds.getX();
            double targetY = targetBounds.getY();
            double targetHeight = targetBounds.getHeight();
            Waypoint w1 = createInstance(Waypoint.class);
            w1.setX(sourceX + (sourceWidth / 2));
            w1.setY(sourceY + sourceHeight);
            Waypoint w2 = createInstance(Waypoint.class);
            w2.setX(sourceX + (sourceWidth / 2));
            w2.setY((sourceY + sourceHeight) + CommonsAbstractBaseElementBuilder.SPACE);
            Waypoint w3 = createInstance(Waypoint.class);
            w3.setX(targetX);
            w3.setY(targetY + (targetHeight / 2));
            edge.addChildElement(w1);
            edge.addChildElement(w2);
            edge.addChildElement(w3);
        }
    }
}