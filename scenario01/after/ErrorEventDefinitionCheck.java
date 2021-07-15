package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ServiceTask;

public class ErrorEventDefinitionCheck {
    private final CamundaErrorEventDefinitionBuilder camundaErrorEventDefinitionBuilder;

    public ErrorEventDefinitionCheck(CamundaErrorEventDefinitionBuilder camundaErrorEventDefinitionBuilder) {
        this.camundaErrorEventDefinitionBuilder = camundaErrorEventDefinitionBuilder;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public AbstractServiceTaskBuilder errorEventDefinitionDone() {
        return ((ServiceTask) ((ExtensionElements) camundaErrorEventDefinitionBuilder.getElement().getParentElement()).getParentElement()).builder();
    }
}