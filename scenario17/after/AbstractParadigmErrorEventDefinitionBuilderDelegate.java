package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;

public class AbstractParadigmErrorEventDefinitionBuilderDelegate<B extends AbstractErrorEventDefinitionBuilder<B>> {
    private final AbstractErrorEventDefinitionBuilder<B> abstractErrorEventDefinitionBuilder;

    public AbstractParadigmErrorEventDefinitionBuilderDelegate(AbstractErrorEventDefinitionBuilder<B> abstractErrorEventDefinitionBuilder) {
        this.abstractErrorEventDefinitionBuilder = abstractErrorEventDefinitionBuilder;
    }

    /**
     * Finishes the building of a error event definition.
     *
     * @param <T>
     * @return the parent event builder
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T extends AbstractFlowNodeBuilder> T errorEventDefinitionDone() {
        return (T) ((Event) abstractErrorEventDefinitionBuilder.getElement().getParentElement()).builder();
    }
}