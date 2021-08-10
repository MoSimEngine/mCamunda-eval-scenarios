package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;
public abstract class ParadigmAbstractErrorEventDefinitionBuilder<B extends ParadigmAbstractErrorEventDefinitionBuilder<B>> extends AbstractRootElementBuilder<B, ErrorEventDefinition> {
    public ParadigmAbstractErrorEventDefinitionBuilder(BpmnModelInstance modelInstance, ErrorEventDefinition element, Class<?> selfType) {
        super(modelInstance, element, selfType);
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