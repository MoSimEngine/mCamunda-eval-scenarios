package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ConditionalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;
public class ParadigmAbstractConditionalEventDefinitionBuilder<B extends ParadigmAbstractConditionalEventDefinitionBuilder<B>> extends AbstractRootElementBuilder<B, ConditionalEventDefinition> {
    public ParadigmAbstractConditionalEventDefinitionBuilder(BpmnModelInstance modelInstance, ConditionalEventDefinition element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Finishes the building of a conditional event definition.
     *
     * @param <T>
     * 		
     * @return the parent event builder
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <T extends AbstractFlowNodeBuilder> T conditionalEventDefinitionDone() {
        return ((T) (((Event) (element.getParentElement())).builder()));
    }
}