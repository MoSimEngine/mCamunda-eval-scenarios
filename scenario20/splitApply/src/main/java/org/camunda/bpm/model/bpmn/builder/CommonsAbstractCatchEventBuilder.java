package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.CatchEvent;
public abstract class CommonsAbstractCatchEventBuilder<B extends CommonsAbstractCatchEventBuilder<B, E>, E extends CatchEvent> extends AbstractCatchEventBuilder<B, E> {
    public CompensateEventDefinitionBuilder compensateEventDefinition() {
        return compensateEventDefinition(null);
    }

    public ConditionalEventDefinitionBuilder conditionalEventDefinition() {
        return conditionalEventDefinition(null);
    }

    public B condition(String condition) {
        conditionalEventDefinition().condition(condition);
        return myself;
    }
}