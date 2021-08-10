package org.camunda.bpm.model.bpmn.builder;
import java.util.List;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.UserTask;
public abstract class DomainAbstractUserTaskBuilder<B extends CommonsAbstractUserTaskBuilder<B>> extends CommonsAbstractUserTaskBuilder<B> {
    protected DomainAbstractUserTaskBuilder(BpmnModelInstance modelInstance, UserTask element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Sets the implementation of the build user task.
     *
     * @param implementation
     * 		the implementation to set
     * @return the builder object
     */
    public B implementation(String implementation) {
        element.setImplementation(implementation);
        return myself;
    }

    /**
     * camunda extensions
     */
    /**
     * Sets the camunda attribute assignee.
     *
     * @param camundaAssignee
     * 		the assignee to set
     * @return the builder object
     */
    public B camundaAssignee(String camundaAssignee) {
        element.setCamundaAssignee(camundaAssignee);
        return myself;
    }

    /**
     * Sets the camunda candidate groups attribute.
     *
     * @param camundaCandidateGroups
     * 		the candidate groups to set
     * @return the builder object
     */
    public B camundaCandidateGroups(String camundaCandidateGroups) {
        element.setCamundaCandidateGroups(camundaCandidateGroups);
        return myself;
    }

    /**
     * Sets the camunda candidate groups attribute.
     *
     * @param camundaCandidateGroups
     * 		the candidate groups to set
     * @return the builder object
     */
    public B camundaCandidateGroups(List<String> camundaCandidateGroups) {
        element.setCamundaCandidateGroupsList(camundaCandidateGroups);
        return myself;
    }

    /**
     * Sets the camunda candidate users attribute.
     *
     * @param camundaCandidateUsers
     * 		the candidate users to set
     * @return the builder object
     */
    public B camundaCandidateUsers(String camundaCandidateUsers) {
        element.setCamundaCandidateUsers(camundaCandidateUsers);
        return myself;
    }

    /**
     * Sets the camunda candidate users attribute.
     *
     * @param camundaCandidateUsers
     * 		the candidate users to set
     * @return the builder object
     */
    public B camundaCandidateUsers(List<String> camundaCandidateUsers) {
        element.setCamundaCandidateUsersList(camundaCandidateUsers);
        return myself;
    }

    /**
     * Sets the camunda due date attribute.
     *
     * @param camundaDueDate
     * 		the due date of the user task
     * @return the builder object
     */
    public B camundaDueDate(String camundaDueDate) {
        element.setCamundaDueDate(camundaDueDate);
        return myself;
    }

    /**
     * Sets the camunda follow up date attribute.
     *
     * @param camundaFollowUpDate
     * 		the follow up date of the user task
     * @return the builder object
     */
    public B camundaFollowUpDate(String camundaFollowUpDate) {
        element.setCamundaFollowUpDate(camundaFollowUpDate);
        return myself;
    }

    /**
     * Sets the camunda form handler class attribute.
     *
     * @param camundaFormHandlerClass
     * 		the class name of the form handler
     * @return the builder object
     */
    @SuppressWarnings("rawtypes")
    public B camundaFormHandlerClass(Class camundaFormHandlerClass) {
        return camundaFormHandlerClass(camundaFormHandlerClass.getName());
    }

    /**
     * Sets the camunda form handler class attribute.
     *
     * @param camundaFormHandlerClass
     * 		the class name of the form handler
     * @return the builder object
     */
    public B camundaFormHandlerClass(String fullQualifiedClassName) {
        element.setCamundaFormHandlerClass(fullQualifiedClassName);
        return myself;
    }

    /**
     * Sets the camunda form key attribute.
     *
     * @param camundaFormKey
     * 		the form key to set
     * @return the builder object
     */
    public B camundaFormKey(String camundaFormKey) {
        element.setCamundaFormKey(camundaFormKey);
        return myself;
    }

    /**
     * Sets the camunda priority attribute.
     *
     * @param camundaPriority
     * 		the priority of the user task
     * @return the builder object
     */
    public B camundaPriority(String camundaPriority) {
        element.setCamundaPriority(camundaPriority);
        return myself;
    }

    @SuppressWarnings("rawtypes")
    public B camundaTaskListenerClassTimeoutWithCycle(String id, Class listenerClass, String timerCycle) {
        return camundaTaskListenerClassTimeoutWithCycle(id, listenerClass.getName(), timerCycle);
    }

    @SuppressWarnings("rawtypes")
    public B camundaTaskListenerClassTimeoutWithDate(String id, Class listenerClass, String timerDate) {
        return camundaTaskListenerClassTimeoutWithDate(id, listenerClass.getName(), timerDate);
    }

    @SuppressWarnings("rawtypes")
    public B camundaTaskListenerClassTimeoutWithDuration(String id, Class listenerClass, String timerDuration) {
        return camundaTaskListenerClassTimeoutWithDuration(id, listenerClass.getName(), timerDuration);
    }

    public B camundaTaskListenerClassTimeoutWithCycle(String id, String fullQualifiedClassName, String timerCycle) {
        return createCamundaTaskListenerClassTimeout(id, fullQualifiedClassName, createTimeCycle(timerCycle));
    }

    public B camundaTaskListenerClassTimeoutWithDate(String id, String fullQualifiedClassName, String timerDate) {
        return createCamundaTaskListenerClassTimeout(id, fullQualifiedClassName, createTimeDate(timerDate));
    }

    public B camundaTaskListenerClassTimeoutWithDuration(String id, String fullQualifiedClassName, String timerDuration) {
        return createCamundaTaskListenerClassTimeout(id, fullQualifiedClassName, createTimeDuration(timerDuration));
    }

    public B camundaTaskListenerExpressionTimeoutWithCycle(String id, String expression, String timerCycle) {
        return createCamundaTaskListenerExpressionTimeout(id, expression, createTimeCycle(timerCycle));
    }

    public B camundaTaskListenerExpressionTimeoutWithDate(String id, String expression, String timerDate) {
        return createCamundaTaskListenerExpressionTimeout(id, expression, createTimeDate(timerDate));
    }

    public B camundaTaskListenerExpressionTimeoutWithDuration(String id, String expression, String timerDuration) {
        return createCamundaTaskListenerExpressionTimeout(id, expression, createTimeDuration(timerDuration));
    }

    public B camundaTaskListenerDelegateExpressionTimeoutWithCycle(String id, String delegateExpression, String timerCycle) {
        return createCamundaTaskListenerDelegateExpressionTimeout(id, delegateExpression, createTimeCycle(timerCycle));
    }

    public B camundaTaskListenerDelegateExpressionTimeoutWithDate(String id, String delegateExpression, String timerDate) {
        return createCamundaTaskListenerDelegateExpressionTimeout(id, delegateExpression, createTimeDate(timerDate));
    }

    public B camundaTaskListenerDelegateExpressionTimeoutWithDuration(String id, String delegateExpression, String timerDuration) {
        return createCamundaTaskListenerDelegateExpressionTimeout(id, delegateExpression, createTimeDuration(timerDuration));
    }

    protected B createCamundaTaskListenerClassTimeout(String id, String fullQualifiedClassName, TimerEventDefinition timerDefinition) {
        CamundaTaskListener executionListener = createCamundaTaskListenerTimeout(id, timerDefinition);
        executionListener.setCamundaClass(fullQualifiedClassName);
        return myself;
    }

    protected B createCamundaTaskListenerExpressionTimeout(String id, String expression, TimerEventDefinition timerDefinition) {
        CamundaTaskListener executionListener = createCamundaTaskListenerTimeout(id, timerDefinition);
        executionListener.setCamundaExpression(expression);
        return myself;
    }

    protected B createCamundaTaskListenerDelegateExpressionTimeout(String id, String delegateExpression, TimerEventDefinition timerDefinition) {
        CamundaTaskListener executionListener = createCamundaTaskListenerTimeout(id, timerDefinition);
        executionListener.setCamundaDelegateExpression(delegateExpression);
        return myself;
    }

    protected CamundaTaskListener createCamundaTaskListenerTimeout(String id, TimerEventDefinition timerDefinition) {
        CamundaTaskListener executionListener = createInstance(CamundaTaskListener.class);
        executionListener.setAttributeValue(BpmnModelConstants.BPMN_ATTRIBUTE_ID, id, true);
        executionListener.setCamundaEvent("timeout");
        executionListener.addChildElement(timerDefinition);
        addExtensionElement(executionListener);
        return executionListener;
    }
}