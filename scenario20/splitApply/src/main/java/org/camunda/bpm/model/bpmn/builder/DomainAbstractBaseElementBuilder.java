package org.camunda.bpm.model.bpmn.builder;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.instance.TimeCycle;
import org.camunda.bpm.model.bpmn.instance.TimeDate;
import org.camunda.bpm.model.bpmn.instance.TimeDuration;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.Escalation;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.EscalationEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.Signal;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.BaseElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.Definitions;
import org.camunda.bpm.model.bpmn.instance.paradigm.messaging.Message;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Error;
public abstract class DomainAbstractBaseElementBuilder<B extends CommonsAbstractBaseElementBuilder<B, E>, E extends BaseElement> extends ParadigmAbstractBaseElementBuilder<B, E> {
    protected MessageEventDefinition createMessageEventDefinition(String messageName) {
        Message message = findMessageForName(messageName);
        MessageEventDefinition messageEventDefinition = createInstance(MessageEventDefinition.class);
        messageEventDefinition.setMessage(message);
        return messageEventDefinition;
    }

    protected MessageEventDefinition createEmptyMessageEventDefinition() {
        return createInstance(MessageEventDefinition.class);
    }

    protected Signal findSignalForName(String signalName) {
        Collection<Signal> signals = modelInstance.getModelElementsByType(Signal.class);
        for (Signal signal : signals) {
            if (signalName.equals(signal.getName())) {
                // return already existing signal for signal name
                return signal;
            }
        }
        // create new signal for non existing signal name
        Definitions definitions = modelInstance.getDefinitions();
        Signal signal = createChild(definitions, Signal.class);
        signal.setName(signalName);
        return signal;
    }

    protected SignalEventDefinition createSignalEventDefinition(String signalName) {
        Signal signal = findSignalForName(signalName);
        SignalEventDefinition signalEventDefinition = createInstance(SignalEventDefinition.class);
        signalEventDefinition.setSignal(signal);
        return signalEventDefinition;
    }

    protected ErrorEventDefinition findErrorDefinitionForCode(String errorCode) {
        Collection<ErrorEventDefinition> definitions = modelInstance.getModelElementsByType(ErrorEventDefinition.class);
        for (ErrorEventDefinition definition : definitions) {
            Error error = definition.getError();
            if ((error != null) && error.getErrorCode().equals(errorCode)) {
                return definition;
            }
        }
        return null;
    }

    protected ErrorEventDefinition createEmptyErrorEventDefinition() {
        ErrorEventDefinition errorEventDefinition = createInstance(ErrorEventDefinition.class);
        return errorEventDefinition;
    }

    protected ErrorEventDefinition createErrorEventDefinition(String errorCode) {
        return createErrorEventDefinition(errorCode, null);
    }

    protected ErrorEventDefinition createErrorEventDefinition(String errorCode, String errorMessage) {
        Error error = findErrorForNameAndCode(errorCode, errorMessage);
        ErrorEventDefinition errorEventDefinition = createInstance(ErrorEventDefinition.class);
        errorEventDefinition.setError(error);
        return errorEventDefinition;
    }

    protected Escalation findEscalationForCode(String escalationCode) {
        Collection<Escalation> escalations = modelInstance.getModelElementsByType(Escalation.class);
        for (Escalation escalation : escalations) {
            if (escalationCode.equals(escalation.getEscalationCode())) {
                // return already existing escalation
                return escalation;
            }
        }
        Definitions definitions = modelInstance.getDefinitions();
        Escalation escalation = createChild(definitions, Escalation.class);
        escalation.setEscalationCode(escalationCode);
        return escalation;
    }

    protected EscalationEventDefinition createEscalationEventDefinition(String escalationCode) {
        Escalation escalation = findEscalationForCode(escalationCode);
        EscalationEventDefinition escalationEventDefinition = createInstance(EscalationEventDefinition.class);
        escalationEventDefinition.setEscalation(escalation);
        return escalationEventDefinition;
    }

    protected CompensateEventDefinition createCompensateEventDefinition() {
        CompensateEventDefinition compensateEventDefinition = createInstance(CompensateEventDefinition.class);
        return compensateEventDefinition;
    }

    protected TimerEventDefinition createTimeCycle(String timerCycle) {
        TimeCycle timeCycle = createInstance(TimeCycle.class);
        timeCycle.setTextContent(timerCycle);
        TimerEventDefinition timerDefinition = createInstance(TimerEventDefinition.class);
        timerDefinition.setTimeCycle(timeCycle);
        return timerDefinition;
    }

    protected TimerEventDefinition createTimeDate(String timerDate) {
        TimeDate timeDate = createInstance(TimeDate.class);
        timeDate.setTextContent(timerDate);
        TimerEventDefinition timerDefinition = createInstance(TimerEventDefinition.class);
        timerDefinition.setTimeDate(timeDate);
        return timerDefinition;
    }

    protected TimerEventDefinition createTimeDuration(String timerDuration) {
        TimeDuration timeDuration = createInstance(TimeDuration.class);
        timeDuration.setTextContent(timerDuration);
        TimerEventDefinition timerDefinition = createInstance(TimerEventDefinition.class);
        timerDefinition.setTimeDuration(timeDuration);
        return timerDefinition;
    }
}