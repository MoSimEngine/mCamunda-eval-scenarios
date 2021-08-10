package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFailedJobRetryTimeCycle;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;
public abstract class CommonsAbstractFlowNodeBuilder<B extends CommonsAbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends AbstractFlowElementBuilder<B, E> {
    protected SequenceFlowBuilder currentSequenceFlowBuilder;

    protected boolean compensationStarted;

    protected CommonsAbstractFlowNodeBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Sets the camunda failedJobRetryTimeCycle attribute for the build flow node.
     *
     * @param retryTimeCycle
     * 		the retry time cycle value to set
     * @return the builder object
     */
    public B camundaFailedJobRetryTimeCycle(String retryTimeCycle) {
        CamundaFailedJobRetryTimeCycle failedJobRetryTimeCycle = createInstance(CamundaFailedJobRetryTimeCycle.class);
        failedJobRetryTimeCycle.setTextContent(retryTimeCycle);
        addExtensionElement(failedJobRetryTimeCycle);
        return myself;
    }

    @SuppressWarnings("rawtypes")
    public B camundaExecutionListenerClass(String eventName, Class listenerClass) {
        return camundaExecutionListenerClass(eventName, listenerClass.getName());
    }

    public B camundaExecutionListenerClass(String eventName, String fullQualifiedClassName) {
        CamundaExecutionListener executionListener = createInstance(CamundaExecutionListener.class);
        executionListener.setCamundaEvent(eventName);
        executionListener.setCamundaClass(fullQualifiedClassName);
        addExtensionElement(executionListener);
        return myself;
    }

    public B camundaExecutionListenerExpression(String eventName, String expression) {
        CamundaExecutionListener executionListener = createInstance(CamundaExecutionListener.class);
        executionListener.setCamundaEvent(eventName);
        executionListener.setCamundaExpression(expression);
        addExtensionElement(executionListener);
        return myself;
    }

    public B camundaExecutionListenerDelegateExpression(String eventName, String delegateExpression) {
        CamundaExecutionListener executionListener = createInstance(CamundaExecutionListener.class);
        executionListener.setCamundaEvent(eventName);
        executionListener.setCamundaDelegateExpression(delegateExpression);
        addExtensionElement(executionListener);
        return myself;
    }
}