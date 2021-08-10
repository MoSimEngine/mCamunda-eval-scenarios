package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.UserTask;
public abstract class CommonsAbstractUserTaskBuilder<B extends CommonsAbstractUserTaskBuilder<B>> extends AbstractTaskBuilder<B, UserTask> {
    protected CommonsAbstractUserTaskBuilder(BpmnModelInstance modelInstance, UserTask element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Creates a new camunda form field extension element.
     *
     * @return the builder object
     */
    public CamundaUserTaskFormFieldBuilder camundaFormField() {
        CamundaFormData camundaFormData = getCreateSingleExtensionElement(CamundaFormData.class);
        CamundaFormField camundaFormField = createChild(camundaFormData, CamundaFormField.class);
        return new CamundaUserTaskFormFieldBuilder(modelInstance, element, camundaFormField);
    }

    /**
     * Add a class based task listener with specified event name
     *
     * @param eventName
     * 		- event names to listen to
     * @param fullQualifiedClassName
     * 		- a string representing a class
     * @return the builder object
     */
    @SuppressWarnings("rawtypes")
    public B camundaTaskListenerClass(String eventName, Class listenerClass) {
        return camundaTaskListenerClass(eventName, listenerClass.getName());
    }

    /**
     * Add a class based task listener with specified event name
     *
     * @param eventName
     * 		- event names to listen to
     * @param fullQualifiedClassName
     * 		- a string representing a class
     * @return the builder object
     */
    public B camundaTaskListenerClass(String eventName, String fullQualifiedClassName) {
        CamundaTaskListener executionListener = createInstance(CamundaTaskListener.class);
        executionListener.setCamundaEvent(eventName);
        executionListener.setCamundaClass(fullQualifiedClassName);
        addExtensionElement(executionListener);
        return myself;
    }

    public B camundaTaskListenerExpression(String eventName, String expression) {
        CamundaTaskListener executionListener = createInstance(CamundaTaskListener.class);
        executionListener.setCamundaEvent(eventName);
        executionListener.setCamundaExpression(expression);
        addExtensionElement(executionListener);
        return myself;
    }

    public B camundaTaskListenerDelegateExpression(String eventName, String delegateExpression) {
        CamundaTaskListener executionListener = createInstance(CamundaTaskListener.class);
        executionListener.setCamundaEvent(eventName);
        executionListener.setCamundaDelegateExpression(delegateExpression);
        addExtensionElement(executionListener);
        return myself;
    }
}