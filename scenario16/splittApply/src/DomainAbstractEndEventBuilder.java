package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EndEvent;
public abstract class DomainAbstractEndEventBuilder<B extends DomainAbstractEndEventBuilder<B>> extends AbstractThrowEventBuilder<B, EndEvent> {
    protected DomainAbstractEndEventBuilder(BpmnModelInstance modelInstance, EndEvent element, Class<?> selfType) {
        super(modelInstance, element, selfType);
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
     * with this code exists it will be used, otherwise a new error is created
     * with the given errorMessage.
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
}