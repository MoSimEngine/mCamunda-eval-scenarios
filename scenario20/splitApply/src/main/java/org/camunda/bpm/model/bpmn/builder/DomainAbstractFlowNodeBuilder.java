package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.ManualTask;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.UserTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;
public abstract class DomainAbstractFlowNodeBuilder<B extends CommonsAbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends ParadigmAbstractFlowNodeBuilder<B, E> {
    protected BoundaryEvent compensateBoundaryEvent;

    public AbstractFlowNodeBuilder compensationDone() {
        if (this.compensateBoundaryEvent != null) {
            return this.compensateBoundaryEvent.getAttachedTo().builder();
        } else {
            throw new BpmnModelException("No compensation in progress. Call compensationStart() first.");
        }
    }

    @SuppressWarnings("unchecked")
    protected <T extends AbstractFlowNodeBuilder, F extends FlowNode> T createTargetBuilder(Class<F> typeClass, String id) {
        AbstractFlowNodeBuilder builder = createTarget(typeClass, id).builder();
        if (this.compensationStarted) {
            // pass on current boundary event to return after compensationDone call
            builder.compensateBoundaryEvent = this.compensateBoundaryEvent;
        }
        return ((T) (builder));
    }

    public UserTaskBuilder userTask() {
        return createTargetBuilder(UserTask.class);
    }

    public UserTaskBuilder userTask(String id) {
        return createTargetBuilder(UserTask.class, id);
    }

    public ManualTaskBuilder manualTask() {
        return createTargetBuilder(ManualTask.class);
    }

    public ManualTaskBuilder manualTask(String id) {
        return createTargetBuilder(ManualTask.class, id);
    }

    public IntermediateCatchEventBuilder intermediateCatchEvent() {
        return createTarget(IntermediateCatchEvent.class).builder();
    }

    public IntermediateCatchEventBuilder intermediateCatchEvent(String id) {
        return createTarget(IntermediateCatchEvent.class, id).builder();
    }

    public IntermediateThrowEventBuilder intermediateThrowEvent() {
        return createTarget(IntermediateThrowEvent.class).builder();
    }

    public IntermediateThrowEventBuilder intermediateThrowEvent(String id) {
        return createTarget(IntermediateThrowEvent.class, id).builder();
    }

    public B compensationStart() {
        if (element instanceof BoundaryEvent) {
            BoundaryEvent boundaryEvent = ((BoundaryEvent) (element));
            for (EventDefinition eventDefinition : boundaryEvent.getEventDefinitions()) {
                if (eventDefinition instanceof CompensateEventDefinition) {
                    // if the boundary event contains a compensate event definition then
                    // save the boundary event to later return to it and start a compensation
                    this.compensateBoundaryEvent = boundaryEvent;
                    this.compensationStarted = true;
                    return myself;
                }
            }
        }
        throw new BpmnModelException("Compensation can only be started on a boundary event with a compensation event definition");
    }

    protected boolean isBoundaryEventWithStartedCompensation() {
        return this.compensationStarted && (this.compensateBoundaryEvent != null);
    }

    protected boolean isCompensationHandler() {
        return (!this.compensationStarted) && (this.compensateBoundaryEvent != null);
    }
}