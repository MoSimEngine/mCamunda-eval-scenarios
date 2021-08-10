package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
public abstract class DomainAbstractErrorEventDefinitionBuilder<B extends ParadigmAbstractErrorEventDefinitionBuilder<B>> extends ParadigmAbstractErrorEventDefinitionBuilder<B> {
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
}