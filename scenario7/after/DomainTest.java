package org.camunda.bpm.model.bpmn.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.BOUNDARY_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.CATCH_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.CONDITION_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.PROCESS_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.START_EVENT_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TASK_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_CLASS_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_CONDITION;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_CONDITIONAL_VARIABLE_EVENTS;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_CONDITIONAL_VARIABLE_EVENTS_LIST;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_CONDITIONAL_VARIABLE_NAME;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_DUE_DATE_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_FOLLOW_UP_DATE_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_GROUPS_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_GROUPS_LIST_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_HISTORY_TIME_TO_LIVE;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_PRIORITY_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_PROCESS_TASK_PRIORITY;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_SERVICE_TASK_PRIORITY;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_STARTABLE_IN_TASKLIST;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_STRING_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_USERS_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_USERS_LIST_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_VERSION_TAG;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.USER_TASK_ID;
import static org.junit.Assert.fail;
import java.util.Collection;
import java.util.List;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.TimeCycle;
import org.camunda.bpm.model.bpmn.instance.TimeDate;
import org.camunda.bpm.model.bpmn.instance.TimeDuration;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaExecutionListener;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaIn;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ConditionalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.Escalation;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.EscalationEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.MessageEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.Signal;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.SignalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.UserTask;
import org.camunda.bpm.model.bpmn.instance.domain.processes.Process;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.BeforeClass;
import org.junit.Test;


public class DomainTest extends CommonsTest {
  
  private BpmnModelInstance modelInstance;
  private static ModelElementType processType;
  
  @BeforeClass
  public static void getElementTypes() {
    Model model = Bpmn.createEmptyModel().getModel();
    processType = model.getType(Process.class);
  }
    @Test
  public void testGetElement() {
    // Make sure this method is publicly available
    Process process = Bpmn.createProcess().getElement();
    assertThat(process).isNotNull();
  }
  
  @Test
  public void testProcessCamundaExtensions() {
    modelInstance = Bpmn.createProcess(PROCESS_ID)
      .camundaJobPriority("${somePriority}")
      .camundaTaskPriority(TEST_PROCESS_TASK_PRIORITY)
      .camundaHistoryTimeToLive(TEST_HISTORY_TIME_TO_LIVE)
      .camundaStartableInTasklist(TEST_STARTABLE_IN_TASKLIST)
      .camundaVersionTag(TEST_VERSION_TAG)
      .startEvent()
      .endEvent()
      .done();

    Process process = modelInstance.getModelElementById(PROCESS_ID);
    assertThat(process.getCamundaJobPriority()).isEqualTo("${somePriority}");
    assertThat(process.getCamundaTaskPriority()).isEqualTo(TEST_PROCESS_TASK_PRIORITY);
    assertThat(process.getCamundaHistoryTimeToLive()).isEqualTo(TEST_HISTORY_TIME_TO_LIVE);
    assertThat(process.isCamundaStartableInTasklist()).isEqualTo(TEST_STARTABLE_IN_TASKLIST);
    assertThat(process.getCamundaVersionTag()).isEqualTo(TEST_VERSION_TAG);
  }
  @Test
  public void testProcessStartableInTasklist() {
    modelInstance = Bpmn.createProcess(PROCESS_ID)
      .startEvent()
      .endEvent()
      .done();

    Process process = modelInstance.getModelElementById(PROCESS_ID);
    assertThat(process.isCamundaStartableInTasklist()).isEqualTo(true);
  }
  @Test
  public void testUserTaskCamundaExtensions() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask(TASK_ID)
        .camundaAssignee(TEST_STRING_API)
        .camundaCandidateGroups(TEST_GROUPS_API)
        .camundaCandidateUsers(TEST_USERS_LIST_API)
        .camundaDueDate(TEST_DUE_DATE_API)
        .camundaFollowUpDate(TEST_FOLLOW_UP_DATE_API)
        .camundaFormHandlerClass(TEST_CLASS_API)
        .camundaFormKey(TEST_STRING_API)
        .camundaPriority(TEST_PRIORITY_API)
        .camundaFailedJobRetryTimeCycle(FAILED_JOB_RETRY_TIME_CYCLE)
      .endEvent()
      .done();

    UserTask userTask = modelInstance.getModelElementById(TASK_ID);
    assertThat(userTask.getCamundaAssignee()).isEqualTo(TEST_STRING_API);
    assertThat(userTask.getCamundaCandidateGroups()).isEqualTo(TEST_GROUPS_API);
    assertThat(userTask.getCamundaCandidateGroupsList()).containsAll(TEST_GROUPS_LIST_API);
    assertThat(userTask.getCamundaCandidateUsers()).isEqualTo(TEST_USERS_API);
    assertThat(userTask.getCamundaCandidateUsersList()).containsAll(TEST_USERS_LIST_API);
    assertThat(userTask.getCamundaDueDate()).isEqualTo(TEST_DUE_DATE_API);
    assertThat(userTask.getCamundaFollowUpDate()).isEqualTo(TEST_FOLLOW_UP_DATE_API);
    assertThat(userTask.getCamundaFormHandlerClass()).isEqualTo(TEST_CLASS_API);
    assertThat(userTask.getCamundaFormKey()).isEqualTo(TEST_STRING_API);
    assertThat(userTask.getCamundaPriority()).isEqualTo(TEST_PRIORITY_API);
    // @layornos hier ist ein Problem.
    assertCamundaFailedJobRetryTimeCycle(userTask);
  }

    protected void assertOnlyOneEscalationExists(String escalationCode) {
      Collection<Escalation> escalations = modelInstance.getModelElementsByType(Escalation.class);
      assertThat(escalations).extracting("escalationCode").containsOnlyOnce(escalationCode);
    }
  
    protected void assertTimerWithCycle(String elementId, String timerCycle) {
      TimerEventDefinition timerEventDefinition =
          assertAndGetSingleEventDefinition(elementId, TimerEventDefinition.class);
      TimeCycle timeCycle = timerEventDefinition.getTimeCycle();
      assertThat(timeCycle).isNotNull();
      assertThat(timeCycle.getTextContent()).isEqualTo(timerCycle);
    }
    protected void assertTimerWithDate(String elementId, String timerDate) {
      TimerEventDefinition timerEventDefinition =  assertAndGetSingleEventDefinition(elementId, TimerEventDefinition.class);
      TimeDate timeDate = timerEventDefinition.getTimeDate();
      assertThat(timeDate).isNotNull();
      assertThat(timeDate.getTextContent()).isEqualTo(timerDate);
    }
  
    protected void assertTimerWithDuration(String elementId, String timerDuration) {
      TimerEventDefinition timerEventDefinition =  assertAndGetSingleEventDefinition(elementId, TimerEventDefinition.class);
      TimeDuration timeDuration = timerEventDefinition.getTimeDuration();
      assertThat(timeDuration).isNotNull();
      assertThat(timeDuration.getTextContent()).isEqualTo(timerDuration);
    }
    protected void assertCompensationEventDefinition(String elementId) {
      assertAndGetSingleEventDefinition(elementId, CompensateEventDefinition.class);
    }
    protected Escalation assertEscalationEventDefinition(String elementId, String escalationCode) {
      EscalationEventDefinition escalationEventDefinition = assertAndGetSingleEventDefinition(elementId, EscalationEventDefinition.class);
      Escalation escalation = escalationEventDefinition.getEscalation();
      assertThat(escalation).isNotNull();
      assertThat(escalation.getEscalationCode()).isEqualTo(escalationCode);
  
      return escalation;
    }
    protected void assertErrorEventDefinitionForErrorVariables(String elementId, String errorCodeVariable, String errorMessageVariable) {
      ErrorEventDefinition errorEventDefinition = assertAndGetSingleEventDefinition(elementId, ErrorEventDefinition.class);
      assertThat(errorEventDefinition).isNotNull();
      if(errorCodeVariable != null) {
        assertThat(errorEventDefinition.getCamundaErrorCodeVariable()).isEqualTo(errorCodeVariable);
      }
      if(errorMessageVariable != null) {
        assertThat(errorEventDefinition.getCamundaErrorMessageVariable()).isEqualTo(errorMessageVariable);
      }
    }
    protected void assertOnlyOneSignalExists(String signalName) {
      Collection<Signal> signals = modelInstance.getModelElementsByType(Signal.class);
      assertThat(signals).extracting("name").containsOnlyOnce(signalName);
    }

    @Test
    public void testCompensateEventDefintionCatchBoundaryEventWithId() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("userTask")
        .boundaryEvent("catch")
          .compensateEventDefinition("foo")
          .waitForCompletion(false)
          .compensateEventDefinitionDone()
        .endEvent("end")
        .done();
  
      CompensateEventDefinition eventDefinition = assertAndGetSingleEventDefinition("catch", CompensateEventDefinition.class);
      assertThat(eventDefinition.getId()).isEqualTo("foo");
    }
  

  

  
    @Test
    public void testCompensateEventDefintionThrowIntermediateEventWithId() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("userTask")
        .intermediateCatchEvent("throw")
          .compensateEventDefinition("foo")
          .activityRef("userTask")
          .waitForCompletion(true)
          .compensateEventDefinitionDone()
        .endEvent("end")
        .done();
  
      CompensateEventDefinition eventDefinition = assertAndGetSingleEventDefinition("throw", CompensateEventDefinition.class);
      assertThat(eventDefinition.getId()).isEqualTo("foo");
    }
  
    @Test
    public void testCompensateEventDefintionReferencesNonExistingActivity() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("userTask")
        .endEvent("end")
        .done();
  
      UserTask userTask = modelInstance.getModelElementById("userTask");
      UserTaskBuilder userTaskBuilder = userTask.builder();
  
      try {
        userTaskBuilder
          .boundaryEvent()
          .compensateEventDefinition()
          .activityRef("nonExistingTask")
          .done();
        fail("should fail");
      } catch (BpmnModelException e) {
        assertThat(e).hasMessageContaining("Activity with id 'nonExistingTask' does not exist");
      }
    }
  
    @Test
    public void testCompensateEventDefintionReferencesActivityInDifferentScope() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("userTask")
        .subProcess()
          .embeddedSubProcess()
          .startEvent()
          .userTask("subProcessTask")
          .endEvent()
          .subProcessDone()
        .endEvent("end")
        .done();
  
      UserTask userTask = modelInstance.getModelElementById("userTask");
      UserTaskBuilder userTaskBuilder = userTask.builder();
  
      try {
        userTaskBuilder
          .boundaryEvent("boundary")
          .compensateEventDefinition()
          .activityRef("subProcessTask")
          .done();
        fail("should fail");
      } catch (BpmnModelException e) {
        assertThat(e).hasMessageContaining("Activity with id 'subProcessTask' must be in the same scope as 'boundary'");
      }
    }
  
    @Test
    public void testConditionalEventDefinitionCamundaExtensions() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateCatchEvent()
        .conditionalEventDefinition(CONDITION_ID)
          .condition(TEST_CONDITION)
          .camundaVariableEvents(TEST_CONDITIONAL_VARIABLE_EVENTS)
          .camundaVariableEvents(TEST_CONDITIONAL_VARIABLE_EVENTS_LIST)
          .camundaVariableName(TEST_CONDITIONAL_VARIABLE_NAME)
        .conditionalEventDefinitionDone()
        .endEvent()
        .done();
  
      ConditionalEventDefinition conditionalEventDef = modelInstance.getModelElementById(CONDITION_ID);
      assertThat(conditionalEventDef.getCamundaVariableEvents()).isEqualTo(TEST_CONDITIONAL_VARIABLE_EVENTS);
      assertThat(conditionalEventDef.getCamundaVariableEventsList()).containsAll(TEST_CONDITIONAL_VARIABLE_EVENTS_LIST);
      assertThat(conditionalEventDef.getCamundaVariableName()).isEqualTo(TEST_CONDITIONAL_VARIABLE_NAME);
    }
  
    @Test
    public void testIntermediateConditionalEventDefinition() {
  
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateCatchEvent(CATCH_ID)
          .conditionalEventDefinition(CONDITION_ID)
              .condition(TEST_CONDITION)
          .conditionalEventDefinitionDone()
        .endEvent()
        .done();
  
      ConditionalEventDefinition eventDefinition = assertAndGetSingleEventDefinition(CATCH_ID, ConditionalEventDefinition.class);
      assertThat(eventDefinition.getId()).isEqualTo(CONDITION_ID);
      assertThat(eventDefinition.getCondition().getTextContent()).isEqualTo(TEST_CONDITION);
    }
  
    @Test
    public void testIntermediateConditionalEventDefinitionShortCut() {
  
      modelInstance = Bpmn.createProcess()
        .startEvent()
          .intermediateCatchEvent(CATCH_ID)
          .condition(TEST_CONDITION)
        .endEvent()
        .done();
  
      ConditionalEventDefinition eventDefinition = assertAndGetSingleEventDefinition(CATCH_ID, ConditionalEventDefinition.class);
      assertThat(eventDefinition.getCondition().getTextContent()).isEqualTo(TEST_CONDITION);
    }
  
    @Test
    public void testBoundaryConditionalEventDefinition() {
  
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask(USER_TASK_ID)
        .endEvent()
          .moveToActivity(USER_TASK_ID)
            .boundaryEvent(BOUNDARY_ID)
              .conditionalEventDefinition(CONDITION_ID)
                .condition(TEST_CONDITION)
              .conditionalEventDefinitionDone()
            .endEvent()
        .done();
  
      ConditionalEventDefinition eventDefinition = assertAndGetSingleEventDefinition(BOUNDARY_ID, ConditionalEventDefinition.class);
      assertThat(eventDefinition.getId()).isEqualTo(CONDITION_ID);
      assertThat(eventDefinition.getCondition().getTextContent()).isEqualTo(TEST_CONDITION);
    }
  
    @Test
    public void testEventSubProcessConditionalStartEvent() {
  
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask()
        .endEvent()
        .subProcess()
          .triggerByEvent()
          .embeddedSubProcess()
          .startEvent(START_EVENT_ID)
            .conditionalEventDefinition(CONDITION_ID)
              .condition(TEST_CONDITION)
            .conditionalEventDefinitionDone()
          .endEvent()
        .done();
  
      ConditionalEventDefinition eventDefinition = assertAndGetSingleEventDefinition(START_EVENT_ID, ConditionalEventDefinition.class);
      assertThat(eventDefinition.getId()).isEqualTo(CONDITION_ID);
      assertThat(eventDefinition.getCondition().getTextContent()).isEqualTo(TEST_CONDITION);
    }
  

  
  

    @Test
    public void testUserTaskCamundaFormFieldWithExistingCamundaFormData() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask(TASK_ID)
          .camundaFormField()
            .camundaId("myFormField_1")
            .camundaLabel("Form Field One")
            .camundaType("string")
            .camundaDefaultValue("myDefaultVal_1")
           .camundaFormFieldDone()
        .endEvent()
        .done();
  
      UserTask userTask = modelInstance.getModelElementById(TASK_ID);
  
      userTask.builder()
        .camundaFormField()
          .camundaId("myFormField_2")
          .camundaLabel("Form Field Two")
          .camundaType("integer")
          .camundaDefaultValue("myDefaultVal_2")
         .camundaFormFieldDone();
  
      assertCamundaFormField(userTask);
    }
    @Test
    public void testUserTaskCamundaFormField() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask(TASK_ID)
          .camundaFormField()
            .camundaId("myFormField_1")
            .camundaLabel("Form Field One")
            .camundaType("string")
            .camundaDefaultValue("myDefaultVal_1")
           .camundaFormFieldDone()
          .camundaFormField()
            .camundaId("myFormField_2")
            .camundaLabel("Form Field Two")
            .camundaType("integer")
            .camundaDefaultValue("myDefaultVal_2")
           .camundaFormFieldDone()
        .endEvent()
        .done();
  
      UserTask userTask = modelInstance.getModelElementById(TASK_ID);
      assertCamundaFormField(userTask);
    }

    @Test
    public void testCompensationStartEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent()
        .subProcess()
          .triggerByEvent()
          .embeddedSubProcess()
          .startEvent("subProcessStart")
          .compensation()
          .endEvent()
        .done();
  
      assertCompensationEventDefinition("subProcessStart");
    }

    @Test
    public void testEscalationEndEventWithExistingEscalation() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("task")
        .endEvent("end").escalation("myEscalationCode")
        .moveToActivity("task")
        .boundaryEvent("boundary").escalation("myEscalationCode")
        .endEvent("boundaryEnd")
        .done();
  
      Escalation boundaryEscalation = assertEscalationEventDefinition("boundary", "myEscalationCode");
      Escalation endEscalation = assertEscalationEventDefinition("end", "myEscalationCode");
  
      assertThat(boundaryEscalation).isEqualTo(endEscalation);
  
      assertOnlyOneEscalationExists("myEscalationCode");
  
    }

    @Test
    public void testIntermediateEscalationThrowEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw").escalation("myEscalationCode")
        .endEvent()
        .done();
  
      assertEscalationEventDefinition("throw", "myEscalationCode");
    }
    @Test
    public void testCatchAllEscalationStartEvent() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
          .endEvent()
          .subProcess()
            .triggerByEvent()
            .embeddedSubProcess()
            .startEvent("subProcessStart")
            .escalation()
            .endEvent()
          .done();
  
      EscalationEventDefinition escalationEventDefinition = assertAndGetSingleEventDefinition("subProcessStart", EscalationEventDefinition.class);
      assertThat(escalationEventDefinition.getEscalation()).isNull();
    }
    @Test
    public void testEscalationEndEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent("end").escalation("myEscalationCode")
        .done();
  
      assertEscalationEventDefinition("end", "myEscalationCode");
    }
  
    @Test
    public void testEscalationStartEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent()
        .subProcess()
          .triggerByEvent()
          .embeddedSubProcess()
          .startEvent("subProcessStart")
          .escalation("myEscalationCode")
          .endEvent()
        .done();
  
      assertEscalationEventDefinition("subProcessStart", "myEscalationCode");
    }

    @Test
    public void testErrorDefinitionsForStartEvent() {
      modelInstance = Bpmn.createProcess()
      .startEvent("start")
        .errorEventDefinition("event")
          .errorCodeVariable("errorCodeVariable")
          .errorMessageVariable("errorMessageVariable")
          .error("errorCode", "errorMessage")
        .errorEventDefinitionDone()
       .endEvent().done();
  
      assertErrorEventDefinition("start", "errorCode", "errorMessage");
      assertErrorEventDefinitionForErrorVariables("start", "errorCodeVariable", "errorMessageVariable");
    }

    @Test
    public void testIntermediateMessageThrowEventWithMessageDefinition() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw1")
        .messageEventDefinition()
          .id("messageEventDefinition")
          .message("message")
          .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
          .camundaType("external")
          .camundaTopic("TOPIC")
        .done();
  
      MessageEventDefinition event = modelInstance.getModelElementById("messageEventDefinition");
      assertThat(event.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);
      assertThat(event.getCamundaTopic()).isEqualTo("TOPIC");
      assertThat(event.getCamundaType()).isEqualTo("external");
      assertThat(event.getMessage().getName()).isEqualTo("message");
    }
    @Test
    public void testIntermediateMessageThrowEventWithTaskPriority() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw1")
        .messageEventDefinition("messageEventDefinition")
          .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
        .done();
  
      MessageEventDefinition event = modelInstance.getModelElementById("messageEventDefinition");
      assertThat(event.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);
    }
  
    @Test
    public void testEndEventWithTaskPriority() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent("end")
        .messageEventDefinition("messageEventDefinition")
          .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
        .done();
  
      MessageEventDefinition event = modelInstance.getModelElementById("messageEventDefinition");
      assertThat(event.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);
    }
    @Test
    public void testMessageEventDefinitionWithID() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw1")
        .messageEventDefinition("messageEventDefinition")
        .done();
  
      MessageEventDefinition event = modelInstance.getModelElementById("messageEventDefinition");
      assertThat(event).isNotNull();
  
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw2")
        .messageEventDefinition().id("messageEventDefinition1")
        .done();
  
      //========================================
      //==============end event=================
      //========================================
      event = modelInstance.getModelElementById("messageEventDefinition1");
      assertThat(event).isNotNull();
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent("end1")
        .messageEventDefinition("messageEventDefinition")
        .done();
  
      event = modelInstance.getModelElementById("messageEventDefinition");
      assertThat(event).isNotNull();
  
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent("end2")
        .messageEventDefinition().id("messageEventDefinition1")
        .done();
  
      event = modelInstance.getModelElementById("messageEventDefinition1");
      assertThat(event).isNotNull();
    }
    @Test
    public void testSignalStartEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent("start").signal("signal")
        .done();
  
      assertSignalEventDefinition("start", "signal");
    }

    @Test
    public void testSignalStartEventWithExistingSignal() {
      modelInstance = Bpmn.createProcess()
        .startEvent("start").signal("signal")
        .subProcess().triggerByEvent()
        .embeddedSubProcess()
        .startEvent("subStart").signal("signal")
        .subProcessDone()
        .done();
  
      Signal signal = assertSignalEventDefinition("start", "signal");
      Signal subSignal = assertSignalEventDefinition("subStart", "signal");
  
      assertThat(signal).isEqualTo(subSignal);
  
      assertOnlyOneSignalExists("signal");
    }
  
    @Test
    public void testIntermediateSignalCatchEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateCatchEvent("catch").signal("signal")
        .done();
  
      assertSignalEventDefinition("catch", "signal");
    }
    @Test
    public void testIntermediateSignalCatchEventWithExistingSignal() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateCatchEvent("catch1").signal("signal")
        .intermediateCatchEvent("catch2").signal("signal")
        .done();
  
      Signal signal1 = assertSignalEventDefinition("catch1", "signal");
      Signal signal2 = assertSignalEventDefinition("catch2", "signal");
  
      assertThat(signal1).isEqualTo(signal2);
  
      assertOnlyOneSignalExists("signal");
    }
  
    @Test
    public void testSignalEndEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent("end").signal("signal")
        .done();
  
      assertSignalEventDefinition("end", "signal");
    }
  
    @Test
    public void testSignalEndEventWithExistingSignal() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .parallelGateway()
        .endEvent("end1").signal("signal")
        .moveToLastGateway()
        .endEvent("end2").signal("signal")
        .done();
  
      Signal signal1 = assertSignalEventDefinition("end1", "signal");
      Signal signal2 = assertSignalEventDefinition("end2", "signal");
  
      assertThat(signal1).isEqualTo(signal2);
  
      assertOnlyOneSignalExists("signal");
    }
  
    @Test
    public void testIntermediateSignalThrowEvent() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw").signal("signal")
        .done();
  
      assertSignalEventDefinition("throw", "signal");
    }
  
    @Test
    public void testIntermediateSignalThrowEventWithExistingSignal() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw1").signal("signal")
        .intermediateThrowEvent("throw2").signal("signal")
        .done();
  
      Signal signal1 = assertSignalEventDefinition("throw1", "signal");
      Signal signal2 = assertSignalEventDefinition("throw2", "signal");
  
      assertThat(signal1).isEqualTo(signal2);
  
      assertOnlyOneSignalExists("signal");
    }
  
    @Test
    public void testIntermediateSignalThrowEventWithPayloadLocalVar() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw")
          .signalEventDefinition("signal")
            .camundaInSourceTarget("source", "target1")
            .camundaInSourceExpressionTarget("${'sourceExpression'}", "target2")
            .camundaInAllVariables("all", true)
            .camundaInBusinessKey("aBusinessKey")
            .throwEventDefinitionDone()
        .endEvent()
        .done();
  
      assertSignalEventDefinition("throw", "signal");
      SignalEventDefinition signalEventDefinition = assertAndGetSingleEventDefinition("throw", SignalEventDefinition.class);
  
      assertThat(signalEventDefinition.getSignal().getName()).isEqualTo("signal");
  
      List<CamundaIn> camundaInParams = signalEventDefinition.getExtensionElements().getElementsQuery().filterByType(CamundaIn.class).list();
      assertThat(camundaInParams.size()).isEqualTo(4);
  
      int paramCounter = 0;
      for (CamundaIn inParam : camundaInParams) {
        if (inParam.getCamundaVariables() != null) {
          assertThat(inParam.getCamundaVariables()).isEqualTo("all");
          if (inParam.getCamundaLocal()) {
            paramCounter++;
          }
        } else if (inParam.getCamundaBusinessKey() != null) {
          assertThat(inParam.getCamundaBusinessKey()).isEqualTo("aBusinessKey");
          paramCounter++;
        } else if (inParam.getCamundaSourceExpression() != null) {
          assertThat(inParam.getCamundaSourceExpression()).isEqualTo("${'sourceExpression'}");
          assertThat(inParam.getCamundaTarget()).isEqualTo("target2");
          paramCounter++;
        } else if (inParam.getCamundaSource() != null) {
          assertThat(inParam.getCamundaSource()).isEqualTo("source");
          assertThat(inParam.getCamundaTarget()).isEqualTo("target1");
          paramCounter++;
        }
      }
      assertThat(paramCounter).isEqualTo(camundaInParams.size());
    }
    @Test
    public void testIntermediateSignalThrowEventWithPayload() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .intermediateThrowEvent("throw")
          .signalEventDefinition("signal")
            .camundaInAllVariables("all")
            .throwEventDefinitionDone()
        .endEvent()
        .done();
  
      SignalEventDefinition signalEventDefinition = assertAndGetSingleEventDefinition("throw", SignalEventDefinition.class);
  
      List<CamundaIn> camundaInParams = signalEventDefinition.getExtensionElements().getElementsQuery().filterByType(CamundaIn.class).list();
      assertThat(camundaInParams.size()).isEqualTo(1);
  
      assertThat(camundaInParams.get(0).getCamundaVariables()).isEqualTo("all");
    }
    @Test
    public void testCamundaTaskListenerByClassName() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClass("start", "aClass")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo("aClass");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("start");
    }
    @Test
    public void testCamundaTaskListenerByClass() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClass("start", this.getClass())
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo(this.getClass().getName());
      assertThat(taskListener.getCamundaEvent()).isEqualTo("start");
    }
  
    @Test
    public void testCamundaTaskListenerByExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerExpression("start", "anExpression")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaExpression()).isEqualTo("anExpression");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("start");
    }
  
    @Test
    public void testCamundaTaskListenerByDelegateExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerDelegateExpression("start", "aDelegate")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaDelegateExpression()).isEqualTo("aDelegate");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("start");
    }
  
    @Test
    public void testCamundaTimeoutCycleTaskListenerByClassName() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClassTimeoutWithCycle("timeout-1", "aClass", "R/PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo("aClass");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNotNull();
      assertThat(timeout.getTimeCycle().getRawTextContent()).isEqualTo("R/PT1H");
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDateTaskListenerByClassName() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClassTimeoutWithDate("timeout-1", "aClass", "2019-09-09T12:12:12")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo("aClass");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNotNull();
      assertThat(timeout.getTimeDate().getRawTextContent()).isEqualTo("2019-09-09T12:12:12");
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDurationTaskListenerByClassName() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClassTimeoutWithDuration("timeout-1", "aClass", "PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo("aClass");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNotNull();
      assertThat(timeout.getTimeDuration().getRawTextContent()).isEqualTo("PT1H");
    }
  
    @Test
    public void testCamundaTimeoutDurationTaskListenerByClass() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClassTimeoutWithDuration("timeout-1", this.getClass(), "PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo(this.getClass().getName());
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNotNull();
      assertThat(timeout.getTimeDuration().getRawTextContent()).isEqualTo("PT1H");
    }
  
    @Test
    public void testCamundaTimeoutCycleTaskListenerByClass() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClassTimeoutWithCycle("timeout-1", this.getClass(), "R/PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo(this.getClass().getName());
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNotNull();
      assertThat(timeout.getTimeCycle().getRawTextContent()).isEqualTo("R/PT1H");
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDateTaskListenerByClass() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerClassTimeoutWithDate("timeout-1", this.getClass(), "2019-09-09T12:12:12")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaClass()).isEqualTo(this.getClass().getName());
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNotNull();
      assertThat(timeout.getTimeDate().getRawTextContent()).isEqualTo("2019-09-09T12:12:12");
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutCycleTaskListenerByExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerExpressionTimeoutWithCycle("timeout-1", "anExpression", "R/PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaExpression()).isEqualTo("anExpression");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNotNull();
      assertThat(timeout.getTimeCycle().getRawTextContent()).isEqualTo("R/PT1H");
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDateTaskListenerByExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerExpressionTimeoutWithDate("timeout-1", "anExpression", "2019-09-09T12:12:12")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaExpression()).isEqualTo("anExpression");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNotNull();
      assertThat(timeout.getTimeDate().getRawTextContent()).isEqualTo("2019-09-09T12:12:12");
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDurationTaskListenerByExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerExpressionTimeoutWithDuration("timeout-1", "anExpression", "PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaExpression()).isEqualTo("anExpression");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNotNull();
      assertThat(timeout.getTimeDuration().getRawTextContent()).isEqualTo("PT1H");
    }
  
    @Test
    public void testCamundaTimeoutCycleTaskListenerByDelegateExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerDelegateExpressionTimeoutWithCycle("timeout-1", "aDelegate", "R/PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaDelegateExpression()).isEqualTo("aDelegate");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNotNull();
      assertThat(timeout.getTimeCycle().getRawTextContent()).isEqualTo("R/PT1H");
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDateTaskListenerByDelegateExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerDelegateExpressionTimeoutWithDate("timeout-1", "aDelegate", "2019-09-09T12:12:12")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaDelegateExpression()).isEqualTo("aDelegate");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNotNull();
      assertThat(timeout.getTimeDate().getRawTextContent()).isEqualTo("2019-09-09T12:12:12");
      assertThat(timeout.getTimeDuration()).isNull();
    }
  
    @Test
    public void testCamundaTimeoutDurationTaskListenerByDelegateExpression() {
      modelInstance = Bpmn.createProcess()
          .startEvent()
            .userTask("task")
              .camundaTaskListenerDelegateExpressionTimeoutWithDuration("timeout-1", "aDelegate", "PT1H")
          .endEvent()
          .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaTaskListener> taskListeners = extensionElements.getChildElementsByType(CamundaTaskListener.class);
      assertThat(taskListeners).hasSize(1);
  
      CamundaTaskListener taskListener = taskListeners.iterator().next();
      assertThat(taskListener.getCamundaDelegateExpression()).isEqualTo("aDelegate");
      assertThat(taskListener.getCamundaEvent()).isEqualTo("timeout");
  
      Collection<TimerEventDefinition> timeouts = taskListener.getTimeouts();
      assertThat(timeouts.size()).isEqualTo(1);
  
      TimerEventDefinition timeout = timeouts.iterator().next();
      assertThat(timeout.getTimeCycle()).isNull();
      assertThat(timeout.getTimeDate()).isNull();
      assertThat(timeout.getTimeDuration()).isNotNull();
      assertThat(timeout.getTimeDuration().getRawTextContent()).isEqualTo("PT1H");
    }
  
    @Test
    public void testCamundaExecutionListenerByClassName() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("task")
        .camundaExecutionListenerClass("start", "aClass")
        .endEvent()
        .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaExecutionListener> executionListeners = extensionElements.getChildElementsByType(CamundaExecutionListener.class);
      assertThat(executionListeners).hasSize(1);
  
      CamundaExecutionListener executionListener = executionListeners.iterator().next();
      assertThat(executionListener.getCamundaClass()).isEqualTo("aClass");
      assertThat(executionListener.getCamundaEvent()).isEqualTo("start");
    }
  
    @Test
    public void testCamundaExecutionListenerByClass() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("task")
        .camundaExecutionListenerClass("start", this.getClass())
        .endEvent()
        .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaExecutionListener> executionListeners = extensionElements.getChildElementsByType(CamundaExecutionListener.class);
      assertThat(executionListeners).hasSize(1);
  
      CamundaExecutionListener executionListener = executionListeners.iterator().next();
      assertThat(executionListener.getCamundaClass()).isEqualTo(this.getClass().getName());
      assertThat(executionListener.getCamundaEvent()).isEqualTo("start");
    }
  
    @Test
    public void testCamundaExecutionListenerByExpression() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("task")
        .camundaExecutionListenerExpression("start", "anExpression")
        .endEvent()
        .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaExecutionListener> executionListeners = extensionElements.getChildElementsByType(CamundaExecutionListener.class);
      assertThat(executionListeners).hasSize(1);
  
      CamundaExecutionListener executionListener = executionListeners.iterator().next();
      assertThat(executionListener.getCamundaExpression()).isEqualTo("anExpression");
      assertThat(executionListener.getCamundaEvent()).isEqualTo("start");
    }
  
    @Test
    public void testCamundaExecutionListenerByDelegateExpression() {
      modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("task")
        .camundaExecutionListenerDelegateExpression("start", "aDelegateExpression")
        .endEvent()
        .done();
  
      UserTask userTask = modelInstance.getModelElementById("task");
      ExtensionElements extensionElements = userTask.getExtensionElements();
      Collection<CamundaExecutionListener> executionListeners = extensionElements.getChildElementsByType(CamundaExecutionListener.class);
      assertThat(executionListeners).hasSize(1);
  
      CamundaExecutionListener executionListener = executionListeners.iterator().next();
      assertThat(executionListener.getCamundaDelegateExpression()).isEqualTo("aDelegateExpression");
      assertThat(executionListener.getCamundaEvent()).isEqualTo("start");
    }

  @Test
  public void testTaskWithCamundaInputOutput() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
        .camundaInputParameter("foo", "bar")
        .camundaInputParameter("yoo", "hoo")
        .camundaOutputParameter("one", "two")
        .camundaOutputParameter("three", "four")
      .endEvent()
      .done();

    UserTask task = modelInstance.getModelElementById("task");
    assertCamundaInputOutputParameter(task);
  }

  @Test
  public void testTaskWithCamundaInputOutputWithExistingExtensionElements() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
        .camundaExecutionListenerExpression("end", "${true}")
        .camundaInputParameter("foo", "bar")
        .camundaInputParameter("yoo", "hoo")
        .camundaOutputParameter("one", "two")
        .camundaOutputParameter("three", "four")
      .endEvent()
      .done();

    UserTask task = modelInstance.getModelElementById("task");
    assertCamundaInputOutputParameter(task);
  }
  @Test
  public void testTaskWithCamundaInputOutputWithExistingCamundaInputOutput() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
        .camundaInputParameter("foo", "bar")
        .camundaOutputParameter("one", "two")
      .endEvent()
      .done();

    UserTask task = modelInstance.getModelElementById("task");

    task.builder()
      .camundaInputParameter("yoo", "hoo")
      .camundaOutputParameter("three", "four");

    assertCamundaInputOutputParameter(task);
  }
  @Test
  public void testTimerStartEventWithDate() {
    modelInstance = Bpmn.createProcess()
      .startEvent("start").timerWithDate(TIMER_DATE)
      .done();

    assertTimerWithDate("start", TIMER_DATE);
  }

  @Test
  public void testTimerStartEventWithDuration() {
    modelInstance = Bpmn.createProcess()
      .startEvent("start").timerWithDuration(TIMER_DURATION)
      .done();

    assertTimerWithDuration("start", TIMER_DURATION);
  }

  @Test
  public void testTimerStartEventWithCycle() {
    modelInstance = Bpmn.createProcess()
      .startEvent("start").timerWithCycle(TIMER_CYCLE)
      .done();

    assertTimerWithCycle("start", TIMER_CYCLE);
  }

  @Test
  public void testIntermediateTimerCatchEventWithDate() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .intermediateCatchEvent("catch").timerWithDate(TIMER_DATE)
      .done();

    assertTimerWithDate("catch", TIMER_DATE);
  }

  @Test
  public void testIntermediateTimerCatchEventWithDuration() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .intermediateCatchEvent("catch").timerWithDuration(TIMER_DURATION)
      .done();

    assertTimerWithDuration("catch", TIMER_DURATION);
  }

  @Test
  public void testIntermediateTimerCatchEventWithCycle() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .intermediateCatchEvent("catch").timerWithCycle(TIMER_CYCLE)
      .done();

    assertTimerWithCycle("catch", TIMER_CYCLE);
  }

  @Test
  public void testTimerBoundaryEventWithDate() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary").timerWithDate(TIMER_DATE)
      .done();

    assertTimerWithDate("boundary", TIMER_DATE);
  }

  @Test
  public void testTimerBoundaryEventWithDuration() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary").timerWithDuration(TIMER_DURATION)
      .done();

    assertTimerWithDuration("boundary", TIMER_DURATION);
  }

  @Test
  public void testTimerBoundaryEventWithCycle() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary").timerWithCycle(TIMER_CYCLE)
      .done();

    assertTimerWithCycle("boundary", TIMER_CYCLE);
  }
  @Test
  public void testCatchAllEscalationBoundaryEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary").escalation()
      .endEvent("boundaryEnd")
      .done();

    EscalationEventDefinition escalationEventDefinition = assertAndGetSingleEventDefinition("boundary", EscalationEventDefinition.class);
    assertThat(escalationEventDefinition.getEscalation()).isNull();
  }
  @Test
  public void testCatchAllErrorStartEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .endEvent()
      .subProcess()
        .triggerByEvent()
        .embeddedSubProcess()
        .startEvent("subProcessStart")
        .error()
        .endEvent()
      .done();

    ErrorEventDefinition errorEventDefinition = assertAndGetSingleEventDefinition("subProcessStart", ErrorEventDefinition.class);
    assertThat(errorEventDefinition.getError()).isNull();
  }
  // liegt in domain weil die assertion domain braucht
  @Test
  public void testErrorStartEventWithoutErrorMessage() {
    modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent()
        .subProcess()
          .triggerByEvent()
          .embeddedSubProcess()
            .startEvent("subProcessStart")
            .error("myErrorCode")
            .endEvent()
        .done();

    assertErrorEventDefinition("subProcessStart", "myErrorCode", null);
  }
  @Test
  public void testErrorBoundaryEventWithoutErrorMessage() {
    modelInstance = Bpmn.createProcess()
        .startEvent()
        .userTask("task")
        .endEvent()
        .moveToActivity("task")
        .boundaryEvent("boundary").error("myErrorCode")
        .endEvent("boundaryEnd")
        .done();

    assertErrorEventDefinition("boundary", "myErrorCode", null);
  }

  @Test
  public void testErrorDefinitionForBoundaryEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary")
        .errorEventDefinition("event")
          .errorCodeVariable("errorCodeVariable")
          .errorMessageVariable("errorMessageVariable")
          .error("errorCode", "errorMessage")
        .errorEventDefinitionDone()
      .endEvent("boundaryEnd")
      .done();

    assertErrorEventDefinition("boundary", "errorCode", "errorMessage");
    assertErrorEventDefinitionForErrorVariables("boundary", "errorCodeVariable", "errorMessageVariable");
  }

  @Test
  public void testErrorDefinitionForBoundaryEventWithoutEventDefinitionId() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary")
        .errorEventDefinition()
          .errorCodeVariable("errorCodeVariable")
          .errorMessageVariable("errorMessageVariable")
          .error("errorCode", "errorMessage")
        .errorEventDefinitionDone()
      .endEvent("boundaryEnd")
      .done();

    Bpmn.writeModelToStream(System.out, modelInstance);

    assertErrorEventDefinition("boundary", "errorCode", "errorMessage");
    assertErrorEventDefinitionForErrorVariables("boundary", "errorCodeVariable", "errorMessageVariable");
  }

  @Test
  public void testErrorEndEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .endEvent("end").error("myErrorCode", "errorMessage")
      .done();

    assertErrorEventDefinition("end", "myErrorCode", "errorMessage");
  }

  @Test
  public void testErrorEndEventWithoutErrorMessage() {
    modelInstance = Bpmn.createProcess()
        .startEvent()
        .endEvent("end").error("myErrorCode")
        .done();

    assertErrorEventDefinition("end", "myErrorCode", null);
  }



  @Test
  public void testErrorStartEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .endEvent()
      .subProcess()
        .triggerByEvent()
        .embeddedSubProcess()
        .startEvent("subProcessStart")
        .error("myErrorCode", "errorMessage")
        .endEvent()
      .done();

    assertErrorEventDefinition("subProcessStart", "myErrorCode", "errorMessage");
  }
  @Test
  public void testNotCancelingBoundaryEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask()
      .boundaryEvent("boundary").cancelActivity(false)
      .done();

    BoundaryEvent boundaryEvent = modelInstance.getModelElementById("boundary");
    assertThat(boundaryEvent.cancelActivity()).isFalse();
  }

  
  @Test
  public void testCatchAllErrorBoundaryEvent() {
    modelInstance = Bpmn.createProcess()
      .startEvent()
      .userTask("task")
      .endEvent()
      .moveToActivity("task")
      .boundaryEvent("boundary").error()
      .endEvent("boundaryEnd")
      .done();

    ErrorEventDefinition errorEventDefinition = assertAndGetSingleEventDefinition("boundary", ErrorEventDefinition.class);
    assertThat(errorEventDefinition.getError()).isNull();
  }
}
