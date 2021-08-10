package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ConditionExpression;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.Activity;
import org.camunda.bpm.model.bpmn.instance.paradigm.artifacts.Association;
import org.camunda.bpm.model.bpmn.instance.paradigm.artifacts.AssociationDirection;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.Gateway;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
public abstract class ParadigmAbstractFlowNodeBuilder<B extends CommonsAbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends CommonsAbstractFlowNodeBuilder<B, E> {
    protected ParadigmAbstractFlowNodeBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    protected SequenceFlowBuilder getCurrentSequenceFlowBuilder() {
        if (this.currentSequenceFlowBuilder == null) {
            SequenceFlow sequenceFlow = createSibling(SequenceFlow.class);
            this.currentSequenceFlowBuilder = sequenceFlow.builder();
        }
        return this.currentSequenceFlowBuilder;
    }

    public B condition(String name, String condition) {
        if (name != null) {
            getCurrentSequenceFlowBuilder().name(name);
        }
        ConditionExpression conditionExpression = createInstance(ConditionExpression.class);
        conditionExpression.setTextContent(condition);
        getCurrentSequenceFlowBuilder().condition(conditionExpression);
        return myself;
    }

    protected void connectTargetWithSequenceFlow(FlowNode target) {
        getCurrentSequenceFlowBuilder().from(element).to(target);
        SequenceFlow sequenceFlow = getCurrentSequenceFlowBuilder().getElement();
        createEdge(sequenceFlow);
        this.currentSequenceFlowBuilder = null;
    }

    protected void connectTargetWithAssociation(FlowNode target) {
        Association association = modelInstance.newInstance(Association.class);
        association.setTarget(target);
        association.setSource(element);
        association.setAssociationDirection(AssociationDirection.One);
        element.getParentElement().addChildElement(association);
        createEdge(association);
    }

    public B sequenceFlowId(String sequenceFlowId) {
        getCurrentSequenceFlowBuilder().id(sequenceFlowId);
        return myself;
    }

    public Gateway findLastGateway() {
        FlowNode lastGateway = element;
        while (true) {
            try {
                lastGateway = lastGateway.getPreviousNodes().singleResult();
                if (lastGateway instanceof Gateway) {
                    return ((Gateway) (lastGateway));
                }
            } catch (BpmnModelException e) {
                throw new BpmnModelException("Unable to determine an unique previous gateway of " + lastGateway.getId(), e);
            }
        } 
    }

    @SuppressWarnings("rawtypes")
    public AbstractGatewayBuilder moveToLastGateway() {
        return findLastGateway().builder();
    }

    @SuppressWarnings("rawtypes")
    public AbstractFlowNodeBuilder moveToNode(String identifier) {
        ModelElementInstance instance = modelInstance.getModelElementById(identifier);
        if ((instance != null) && (instance instanceof FlowNode)) {
            return ((FlowNode) (instance)).builder();
        } else {
            throw new BpmnModelException("Flow node not found for id " + identifier);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends AbstractActivityBuilder> T moveToActivity(String identifier) {
        ModelElementInstance instance = modelInstance.getModelElementById(identifier);
        if ((instance != null) && (instance instanceof Activity)) {
            return ((T) (((Activity) (instance)).builder()));
        } else {
            throw new BpmnModelException("Activity not found for id " + identifier);
        }
    }

    /**
     * Sets the Camunda AsyncBefore attribute for the build flow node.
     *
     * @param asyncBefore
     * 		boolean value to set
     * @return the builder object
     */
    public B camundaAsyncBefore(boolean asyncBefore) {
        element.setCamundaAsyncBefore(asyncBefore);
        return myself;
    }

    /**
     * Sets the Camunda asyncBefore attribute to true.
     *
     * @return the builder object
     */
    public B camundaAsyncBefore() {
        element.setCamundaAsyncBefore(true);
        return myself;
    }

    /**
     * Sets the Camunda asyncAfter attribute for the build flow node.
     *
     * @param asyncAfter
     * 		boolean value to set
     * @return the builder object
     */
    public B camundaAsyncAfter(boolean asyncAfter) {
        element.setCamundaAsyncAfter(asyncAfter);
        return myself;
    }

    /**
     * Sets the Camunda asyncAfter attribute to true.
     *
     * @return the builder object
     */
    public B camundaAsyncAfter() {
        element.setCamundaAsyncAfter(true);
        return myself;
    }

    /**
     * Sets the Camunda exclusive attribute to true.
     *
     * @return the builder object
     */
    public B notCamundaExclusive() {
        element.setCamundaExclusive(false);
        return myself;
    }

    /**
     * Sets the camunda exclusive attribute for the build flow node.
     *
     * @param exclusive
     * 		boolean value to set
     * @return the builder object
     */
    public B camundaExclusive(boolean exclusive) {
        element.setCamundaExclusive(exclusive);
        return myself;
    }

    public B camundaJobPriority(String jobPriority) {
        element.setCamundaJobPriority(jobPriority);
        return myself;
    }
}