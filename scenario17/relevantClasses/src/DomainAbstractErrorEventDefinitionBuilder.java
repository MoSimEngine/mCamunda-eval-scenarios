package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;
public abstract class DomainAbstractErrorEventDefinitionBuilder<B extends DomainAbstractErrorEventDefinitionBuilder<B>> extends AbstractRootElementBuilder<B, ErrorEventDefinition> {
    public DomainAbstractErrorEventDefinitionBuilder(BpmnModelInstance modelInstance, ErrorEventDefinition element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    @Override
    public B id(String identifier) {
        return super.id(identifier);
    }

    /**
     * Sets the error code variable attribute.
     */
    public B errorCodeVariable(String errorCodeVariable) {
        element.setCamundaErrorCodeVariable(errorCodeVariable);
        return myself;
    }

    /**
     * Sets the error message variable attribute.
     */
    public B errorMessageVariable(String errorMessageVariable) {
        element.setCamundaErrorMessageVariable(errorMessageVariable);
        return myself;
    }

    /**
     * Sets the error attribute with errorCode.
     */
    public B error(String errorCode) {
        return error(errorCode, null);
    }

    /**
     * Sets the error attribute with errorCode and errorMessage.
     */
    public B error(String errorCode, String errorMessage) {
        element.setError(findErrorForNameAndCode(errorCode, errorMessage));
        return myself;
    }

    /**
     * Finishes the building of a error event definition.
     *
     * @param <T>
     * 		
     * @return the parent event builder
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends AbstractFlowNodeBuilder> T errorEventDefinitionDone() {
        return ((T) (((Event) (element.getParentElement())).builder()));
    }
}