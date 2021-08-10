package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.EscalationEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.ThrowEvent;
public abstract class DomainAbstractThrowEventBuilder<B extends DomainAbstractThrowEventBuilder<B, E>, E extends ThrowEvent> extends AbstractEventBuilder<B, E> {
    protected DomainAbstractThrowEventBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

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
     * Creates an empty message event definition with an unique id
     * and returns a builder for the message event definition.
     *
     * @return the message event definition builder object
     */
    public MessageEventDefinitionBuilder messageEventDefinition() {
        return messageEventDefinition(null);
    }

    /**
     * Creates an empty message event definition with the given id
     * and returns a builder for the message event definition.
     *
     * @param id
     * 		the id of the message event definition
     * @return the message event definition builder object
     */
    public MessageEventDefinitionBuilder messageEventDefinition(String id) {
        MessageEventDefinition messageEventDefinition = createEmptyMessageEventDefinition();
        if (id != null) {
            messageEventDefinition.setId(id);
        }
        element.getEventDefinitions().add(messageEventDefinition);
        return new MessageEventDefinitionBuilder(modelInstance, messageEventDefinition);
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

    /**
     * Sets an event definition for the given Signal name. If a signal with this
     * name already exists it will be used, otherwise a new signal is created.
     * It returns a builder for the Signal Event Definition.
     *
     * @param signalName
     * 		the name of the signal
     * @return the signal event definition builder object
     */
    public SignalEventDefinitionBuilder signalEventDefinition(String signalName) {
        SignalEventDefinition signalEventDefinition = createSignalEventDefinition(signalName);
        element.getEventDefinitions().add(signalEventDefinition);
        return new SignalEventDefinitionBuilder(modelInstance, signalEventDefinition);
    }

    /**
     * Sets an escalation definition for the given escalation code. If already an
     * escalation with this code exists it will be used, otherwise a new
     * escalation is created.
     *
     * @param escalationCode
     * 		the code of the escalation
     * @return the builder object
     */
    public B escalation(String escalationCode) {
        EscalationEventDefinition escalationEventDefinition = createEscalationEventDefinition(escalationCode);
        element.getEventDefinitions().add(escalationEventDefinition);
        return myself;
    }

    public CompensateEventDefinitionBuilder compensateEventDefinition() {
        return compensateEventDefinition(null);
    }

    public CompensateEventDefinitionBuilder compensateEventDefinition(String id) {
        CompensateEventDefinition eventDefinition = createInstance(CompensateEventDefinition.class);
        if (id != null) {
            eventDefinition.setId(id);
        }
        element.getEventDefinitions().add(eventDefinition);
        return new CompensateEventDefinitionBuilder(modelInstance, eventDefinition);
    }
}