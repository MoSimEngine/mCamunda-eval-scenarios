package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.TimeCycle;
import org.camunda.bpm.model.bpmn.instance.TimeDate;
import org.camunda.bpm.model.bpmn.instance.TimeDuration;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFailedJobRetryTimeCycle;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputOutput;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaInputParameter;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOutputParameter;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.Escalation;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.EscalationEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.Signal;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.BaseElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.messaging.Message;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Error;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ProcessBuilderTestUtils {
    public Message assertMessageEventDefinition(String elementId, String messageName, BpmnModelInstance modelInstance) {
        MessageEventDefinition messageEventDefinition = assertAndGetSingleEventDefinition(elementId, MessageEventDefinition.class, modelInstance);
        Message message = messageEventDefinition.getMessage();
        assertThat(message).isNotNull();
        assertThat(message.getName()).isEqualTo(messageName);

        return message;
    }

    public void assertOnlyOneMessageExists(String messageName, BpmnModelInstance modelInstance) {
        Collection<Message> messages = modelInstance.getModelElementsByType(Message.class);
        assertThat(messages).extracting("name").containsOnlyOnce(messageName);
    }

    public Signal assertSignalEventDefinition(String elementId, String signalName, BpmnModelInstance modelInstance) {
        SignalEventDefinition signalEventDefinition = assertAndGetSingleEventDefinition(elementId, SignalEventDefinition.class, modelInstance);
        Signal signal = signalEventDefinition.getSignal();
        assertThat(signal).isNotNull();
        assertThat(signal.getName()).isEqualTo(signalName);

        return signal;
    }

    public void assertOnlyOneSignalExists(String signalName, BpmnModelInstance modelInstance) {
        Collection<Signal> signals = modelInstance.getModelElementsByType(Signal.class);
        assertThat(signals).extracting("name").containsOnlyOnce(signalName);
    }

    public Error assertErrorEventDefinition(String elementId, String errorCode, String errorMessage, BpmnModelInstance modelInstance) {
        ErrorEventDefinition errorEventDefinition = assertAndGetSingleEventDefinition(elementId, ErrorEventDefinition.class, modelInstance);
        Error error = errorEventDefinition.getError();
        assertThat(error).isNotNull();
        assertThat(error.getErrorCode()).isEqualTo(errorCode);
        assertThat(error.getCamundaErrorMessage()).isEqualTo(errorMessage);

        return error;
    }

    public void assertErrorEventDefinitionForErrorVariables(String elementId, String errorCodeVariable, String errorMessageVariable, BpmnModelInstance modelInstance) {
        ErrorEventDefinition errorEventDefinition = assertAndGetSingleEventDefinition(elementId, ErrorEventDefinition.class, modelInstance);
        assertThat(errorEventDefinition).isNotNull();
        if (errorCodeVariable != null) {
            assertThat(errorEventDefinition.getCamundaErrorCodeVariable()).isEqualTo(errorCodeVariable);
        }
        if (errorMessageVariable != null) {
            assertThat(errorEventDefinition.getCamundaErrorMessageVariable()).isEqualTo(errorMessageVariable);
        }
    }

    public void assertOnlyOneErrorExists(String errorCode, BpmnModelInstance modelInstance) {
        Collection<Error> errors = modelInstance.getModelElementsByType(Error.class);
        assertThat(errors).extracting("errorCode").containsOnlyOnce(errorCode);
    }

    public Escalation assertEscalationEventDefinition(String elementId, String escalationCode, BpmnModelInstance modelInstance) {
        EscalationEventDefinition escalationEventDefinition = assertAndGetSingleEventDefinition(elementId, EscalationEventDefinition.class, modelInstance);
        Escalation escalation = escalationEventDefinition.getEscalation();
        assertThat(escalation).isNotNull();
        assertThat(escalation.getEscalationCode()).isEqualTo(escalationCode);

        return escalation;
    }

    public void assertOnlyOneEscalationExists(String escalationCode, BpmnModelInstance modelInstance) {
        Collection<Escalation> escalations = modelInstance.getModelElementsByType(Escalation.class);
        assertThat(escalations).extracting("escalationCode").containsOnlyOnce(escalationCode);
    }

    public void assertCompensationEventDefinition(String elementId, BpmnModelInstance modelInstance) {
        assertAndGetSingleEventDefinition(elementId, CompensateEventDefinition.class, modelInstance);
    }

    public void assertCamundaInputOutputParameter(BaseElement element) {
        CamundaInputOutput camundaInputOutput = element.getExtensionElements().getElementsQuery().filterByType(CamundaInputOutput.class).singleResult();
        assertThat(camundaInputOutput).isNotNull();

        List<CamundaInputParameter> camundaInputParameters = new ArrayList<>(camundaInputOutput.getCamundaInputParameters());
        assertThat(camundaInputParameters).hasSize(2);

        CamundaInputParameter camundaInputParameter = camundaInputParameters.get(0);
        assertThat(camundaInputParameter.getCamundaName()).isEqualTo("foo");
        assertThat(camundaInputParameter.getTextContent()).isEqualTo("bar");

        camundaInputParameter = camundaInputParameters.get(1);
        assertThat(camundaInputParameter.getCamundaName()).isEqualTo("yoo");
        assertThat(camundaInputParameter.getTextContent()).isEqualTo("hoo");

        List<CamundaOutputParameter> camundaOutputParameters = new ArrayList<>(camundaInputOutput.getCamundaOutputParameters());
        assertThat(camundaOutputParameters).hasSize(2);

        CamundaOutputParameter camundaOutputParameter = camundaOutputParameters.get(0);
        assertThat(camundaOutputParameter.getCamundaName()).isEqualTo("one");
        assertThat(camundaOutputParameter.getTextContent()).isEqualTo("two");

        camundaOutputParameter = camundaOutputParameters.get(1);
        assertThat(camundaOutputParameter.getCamundaName()).isEqualTo("three");
        assertThat(camundaOutputParameter.getTextContent()).isEqualTo("four");
    }

    public void assertTimerWithDate(String elementId, String timerDate, BpmnModelInstance modelInstance) {
        TimerEventDefinition timerEventDefinition = assertAndGetSingleEventDefinition(elementId, TimerEventDefinition.class, modelInstance);
        TimeDate timeDate = timerEventDefinition.getTimeDate();
        assertThat(timeDate).isNotNull();
        assertThat(timeDate.getTextContent()).isEqualTo(timerDate);
    }

    public void assertTimerWithDuration(String elementId, String timerDuration, BpmnModelInstance modelInstance) {
        TimerEventDefinition timerEventDefinition = assertAndGetSingleEventDefinition(elementId, TimerEventDefinition.class, modelInstance);
        TimeDuration timeDuration = timerEventDefinition.getTimeDuration();
        assertThat(timeDuration).isNotNull();
        assertThat(timeDuration.getTextContent()).isEqualTo(timerDuration);
    }

    public void assertTimerWithCycle(String elementId, String timerCycle, BpmnModelInstance modelInstance) {
        TimerEventDefinition timerEventDefinition = assertAndGetSingleEventDefinition(elementId, TimerEventDefinition.class, modelInstance);
        TimeCycle timeCycle = timerEventDefinition.getTimeCycle();
        assertThat(timeCycle).isNotNull();
        assertThat(timeCycle.getTextContent()).isEqualTo(timerCycle);
    }

    @SuppressWarnings("unchecked")
    public <T extends EventDefinition> T assertAndGetSingleEventDefinition(String elementId, Class<T> eventDefinitionType, BpmnModelInstance modelInstance) {
        BpmnModelElementInstance element = modelInstance.getModelElementById(elementId);
        assertThat(element).isNotNull();
        Collection<EventDefinition> eventDefinitions = element.getChildElementsByType(EventDefinition.class);
        assertThat(eventDefinitions).hasSize(1);

        EventDefinition eventDefinition = eventDefinitions.iterator().next();
        assertThat(eventDefinition)
                .isNotNull()
                .isInstanceOf(eventDefinitionType);
        return (T) eventDefinition;
    }

    public void assertCamundaFormField(BaseElement element) {
        assertThat(element.getExtensionElements()).isNotNull();

        CamundaFormData camundaFormData = element.getExtensionElements().getElementsQuery().filterByType(CamundaFormData.class).singleResult();
        assertThat(camundaFormData).isNotNull();

        List<CamundaFormField> camundaFormFields = new ArrayList<>(camundaFormData.getCamundaFormFields());
        assertThat(camundaFormFields).hasSize(2);

        CamundaFormField camundaFormField = camundaFormFields.get(0);
        assertThat(camundaFormField.getCamundaId()).isEqualTo("myFormField_1");
        assertThat(camundaFormField.getCamundaLabel()).isEqualTo("Form Field One");
        assertThat(camundaFormField.getCamundaType()).isEqualTo("string");
        assertThat(camundaFormField.getCamundaDefaultValue()).isEqualTo("myDefaultVal_1");

        camundaFormField = camundaFormFields.get(1);
        assertThat(camundaFormField.getCamundaId()).isEqualTo("myFormField_2");
        assertThat(camundaFormField.getCamundaLabel()).isEqualTo("Form Field Two");
        assertThat(camundaFormField.getCamundaType()).isEqualTo("integer");
        assertThat(camundaFormField.getCamundaDefaultValue()).isEqualTo("myDefaultVal_2");

    }

    public void assertCamundaFailedJobRetryTimeCycle(BaseElement element) {
        assertThat(element.getExtensionElements()).isNotNull();

        CamundaFailedJobRetryTimeCycle camundaFailedJobRetryTimeCycle = element.getExtensionElements().getElementsQuery().filterByType(CamundaFailedJobRetryTimeCycle.class).singleResult();
        assertThat(camundaFailedJobRetryTimeCycle).isNotNull();
        assertThat(camundaFailedJobRetryTimeCycle.getTextContent()).isEqualTo(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE);
    }
}
