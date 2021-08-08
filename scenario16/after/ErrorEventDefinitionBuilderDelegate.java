package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.BaseElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Error;

import java.util.Collection;

public class ErrorEventDefinitionBuilderDelegate<B extends ErrorEventDefinitionBuilderDelegate<B, E>, E extends BaseElement> extends AbstractBpmnModelElementBuilder<B,E>{

    public ErrorEventDefinitionBuilderDelegate(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    protected ErrorEventDefinition findErrorDefinitionForCode(String errorCode) {
        Collection<ErrorEventDefinition> definitions = modelInstance.getModelElementsByType(ErrorEventDefinition.class);
        for (ErrorEventDefinition definition : definitions) {
            Error error = definition.getError();
            if (error != null && error.getErrorCode().equals(errorCode)) {
                return definition;
            }
        }
        return null;
    }

    protected ErrorEventDefinition createEmptyErrorEventDefinition() {
        ErrorEventDefinition errorEventDefinition = createInstance(ErrorEventDefinition.class);
        return errorEventDefinition;
    }

    protected ErrorEventDefinition createErrorEventDefinition(String errorCode) {
        return createErrorEventDefinition(errorCode, null);
    }

    protected ErrorEventDefinition createErrorEventDefinition(String errorCode, String errorMessage) {
        Error error = findErrorForNameAndCode(errorCode, errorMessage);
        ErrorEventDefinition errorEventDefinition = createEmptyErrorEventDefinition();
        errorEventDefinition.setError(error);
        return errorEventDefinition;
    }
}