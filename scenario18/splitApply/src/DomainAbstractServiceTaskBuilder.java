package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ServiceTask;
public abstract class DomainAbstractServiceTaskBuilder<B extends ParadigmAbstractServiceTaskBuilder<B>> extends ParadigmAbstractServiceTaskBuilder<B> {
    protected DomainAbstractServiceTaskBuilder(BpmnModelInstance modelInstance, ServiceTask element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Creates an error event definition for this service task and returns a builder for the error event definition.
     * This is only meaningful when the {@link #camundaType(String)} attribute has the value <code>external</code>.
     *
     * @return the error event definition builder object
     */
    public CamundaErrorEventDefinitionBuilder camundaErrorEventDefinition() {
        ErrorEventDefinition camundaErrorEventDefinition = createInstance(CamundaErrorEventDefinition.class);
        addExtensionElement(camundaErrorEventDefinition);
        return new CamundaErrorEventDefinitionBuilder(modelInstance, camundaErrorEventDefinition);
    }
}