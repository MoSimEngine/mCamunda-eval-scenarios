package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;

public class ParadigmConditionalEventDefinitionDone<B extends AbstractConditionalEventDefinitionBuilder<B>> {
    private final AbstractConditionalEventDefinitionBuilder<B> abstractConditionalEventDefinitionBuilder;

    public ParadigmConditionalEventDefinitionDone(AbstractConditionalEventDefinitionBuilder<B> abstractConditionalEventDefinitionBuilder) {
        this.abstractConditionalEventDefinitionBuilder = abstractConditionalEventDefinitionBuilder;
    }

    /**
     * Finishes the building of a conditional event definition.
     *
     * @param <T>
     * @return the parent event builder
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends AbstractFlowNodeBuilder> T conditionalEventDefinitionDone() {
        return (T) ((Event) abstractConditionalEventDefinitionBuilder.getElement().getParentElement()).builder();
    }
}