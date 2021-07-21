package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.ThrowEvent;
public abstract class CommonsAbstractThrowEventBuilder<B extends CommonsAbstractThrowEventBuilder<B, E>, E extends ThrowEvent> extends AbstractThrowEventBuilder<B, E> {
    /**
     * Creates an empty message event definition with an unique id
     * and returns a builder for the message event definition.
     *
     * @return the message event definition builder object
     */
    public MessageEventDefinitionBuilder messageEventDefinition() {
        return messageEventDefinition(null);
    }

    public CompensateEventDefinitionBuilder compensateEventDefinition() {
        return compensateEventDefinition(null);
    }
}