package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
public class DomainErrorEventDefinitionBuilder extends CommonsErrorEventDefinitionBuilder {
    public DomainErrorEventDefinitionBuilder(BpmnModelInstance modelInstance, ErrorEventDefinition element) {
        super(modelInstance, element, ErrorEventDefinitionBuilder.class);
    }
}