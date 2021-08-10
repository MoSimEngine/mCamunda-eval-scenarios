package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.EscalationEventDefinition;
public abstract class DomainAbstractBoundaryEventBuilder<B extends CommonsAbstractBoundaryEventBuilder<B>> extends ParadigmAbstractBoundaryEventBuilder<B> {
    protected DomainAbstractBoundaryEventBuilder(BpmnModelInstance modelInstance, BoundaryEvent element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Set if the boundary event cancels the attached activity.
     *
     * @param cancelActivity
     * 		true if the boundary event cancels the activiy, false otherwise
     * @return the builder object
     */
    public B cancelActivity(Boolean cancelActivity) {
        element.setCancelActivity(cancelActivity);
        return myself;
    }

    /**
     * Sets a catch all error definition.
     *
     * @return the builder object
     */
    public B error() {
        ErrorEventDefinition errorEventDefinition = createInstance(ErrorEventDefinition.class);
        element.getEventDefinitions().add(errorEventDefinition);
        return myself;
    }

    /**
     * Sets an error definition for the given error code. If already an error
     * with this code exists it will be used, otherwise a new error is created.
     *
     * @param errorCode
     * 		the code of the error
     * @return the builder object
     */
    public B error(String errorCode) {
        return error(errorCode, null);
    }

    /**
     * Sets an error definition for the given error code. If already an error
     * with this code exists it will be used, otherwise a new error with the
     * given error message is created.
     *
     * @param errorCode
     * 		the code of the error
     * @param errorMessage
     * 		the error message that is used when a new error needs
     * 		to be created
     * @return the builder object
     */
    public B error(String errorCode, String errorMessage) {
        ErrorEventDefinition errorEventDefinition = createErrorEventDefinition(errorCode, errorMessage);
        element.getEventDefinitions().add(errorEventDefinition);
        return myself;
    }

    /**
     * Creates an error event definition with an unique id
     * and returns a builder for the error event definition.
     *
     * @return the error event definition builder object
     */
    public ErrorEventDefinitionBuilder errorEventDefinition(String id) {
        ErrorEventDefinition errorEventDefinition = createEmptyErrorEventDefinition();
        if (id != null) {
            errorEventDefinition.setId(id);
        }
        element.getEventDefinitions().add(errorEventDefinition);
        return new ErrorEventDefinitionBuilder(modelInstance, errorEventDefinition);
    }

    /**
     * Creates an error event definition
     * and returns a builder for the error event definition.
     *
     * @return the error event definition builder object
     */
    public ErrorEventDefinitionBuilder errorEventDefinition() {
        ErrorEventDefinition errorEventDefinition = createEmptyErrorEventDefinition();
        element.getEventDefinitions().add(errorEventDefinition);
        return new ErrorEventDefinitionBuilder(modelInstance, errorEventDefinition);
    }

    /**
     * Sets a catch all escalation definition.
     *
     * @return the builder object
     */
    public B escalation() {
        EscalationEventDefinition escalationEventDefinition = createInstance(EscalationEventDefinition.class);
        element.getEventDefinitions().add(escalationEventDefinition);
        return myself;
    }

    /**
     * Sets an escalation definition for the given escalation code. If already an escalation
     * with this code exists it will be used, otherwise a new escalation is created.
     *
     * @param escalationCode
     * 		the code of the escalation
     * @return the builder object
     */
    public B escalation(String escalationCode) {
        EscalationEventDefinition escalationEventDefinition = createEscalationEventDefinition(escalationCode);
        element.getEventDefinitions().add(escalationEventDefinition);
        return myself;
    }
}