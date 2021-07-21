package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ConditionalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.CatchEvent;
public abstract class DomainAbstractCatchEventBuilder<B extends CommonsAbstractCatchEventBuilder<B, E>, E extends CatchEvent> extends ParadigmAbstractCatchEventBuilder<B, E> {
    /**
     * Sets an event definition for the given message name. If already a message
     * with this name exists it will be used, otherwise a new message is created.
     *
     * @param messageName
     * 		the name of the message
     * @return the builder object
     */
    public B message(String messageName) {
        MessageEventDefinition messageEventDefinition = createMessageEventDefinition(messageName);
        element.getEventDefinitions().add(messageEventDefinition);
        return myself;
    }

    /**
     * Sets an event definition for the given signal name. If already a signal
     * with this name exists it will be used, otherwise a new signal is created.
     *
     * @param signalName
     * 		the name of the signal
     * @return the builder object
     */
    public B signal(String signalName) {
        SignalEventDefinition signalEventDefinition = createSignalEventDefinition(signalName);
        element.getEventDefinitions().add(signalEventDefinition);
        return myself;
    }

    public CompensateEventDefinitionBuilder compensateEventDefinition(String id) {
        CompensateEventDefinition eventDefinition = createInstance(CompensateEventDefinition.class);
        if (id != null) {
            eventDefinition.setId(id);
        }
        element.getEventDefinitions().add(eventDefinition);
        return new CompensateEventDefinitionBuilder(modelInstance, eventDefinition);
    }

    public ConditionalEventDefinitionBuilder conditionalEventDefinition(String id) {
        ConditionalEventDefinition eventDefinition = createInstance(ConditionalEventDefinition.class);
        if (id != null) {
            eventDefinition.setId(id);
        }
        element.getEventDefinitions().add(eventDefinition);
        return new ConditionalEventDefinitionBuilder(modelInstance, eventDefinition);
    }
}