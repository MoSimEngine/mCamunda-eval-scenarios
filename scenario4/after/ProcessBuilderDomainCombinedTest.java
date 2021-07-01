/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.model.bpmn.builder;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
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
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.Activity;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.CallActivity;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.Task;
import org.camunda.bpm.model.bpmn.instance.paradigm.artifacts.Association;
import org.camunda.bpm.model.bpmn.instance.paradigm.artifacts.AssociationDirection;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.Definitions;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.Documentation;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EndEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.StartEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.Gateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.looping.MultiInstanceLoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.paradigm.subprocesses.SubProcess;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.*;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;


/**
 * @author Sebastian Menski
 */
public class ProcessBuilderDomainCombinedTest {


    public static final String TIMER_DATE = "2011-03-11T12:13:14Z";
    public static final String TIMER_DURATION = "P10D";
    public static final String TIMER_CYCLE = "R3/PT10H";
    public static final String FAILED_JOB_RETRY_TIME_CYCLE = "R5/PT1M";
    private static ModelElementType taskType;
    private static ModelElementType gatewayType;
    private static ModelElementType eventType;
    private static ModelElementType processType;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    ProcessBuilderTestUtils utils = new ProcessBuilderParadigmTest();
    private BpmnModelInstance modelInstance;

    @BeforeClass
    public static void getElementTypes() {
        Model model = Bpmn.createEmptyModel().getModel();
        taskType = model.getType(Task.class);
        gatewayType = model.getType(Gateway.class);
        eventType = model.getType(Event.class);
        processType = model.getType(Process.class);
    }

    @After
    public void validateModel() throws IOException {
        if (modelInstance != null) {
            Bpmn.validateModel(modelInstance);
        }
    }

    @Test
    public void testGetElement() {
        // Make sure this method is publicly available
        Process process = Bpmn.createProcess().getElement();
        assertThat(process).isNotNull();
    }

    @Test
    public void testCreateProcessWithStartEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
    }

    @Test
    public void testCreateProcessWithServiceTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .serviceTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithSendTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .sendTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithUserTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithBusinessRuleTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .businessRuleTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithScriptTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .scriptTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithReceiveTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .receiveTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithManualTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .manualTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithParallelGateway() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .parallelGateway()
                .scriptTask()
                .endEvent()
                .moveToLastGateway()
                .userTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(3);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(gatewayType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithExclusiveGateway() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask()
                .exclusiveGateway()
                .condition("approved", "${approved}")
                .serviceTask()
                .endEvent()
                .moveToLastGateway()
                .condition("not approved", "${!approved}")
                .scriptTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(3);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(3);
        assertThat(modelInstance.getModelElementsByType(gatewayType))
                .hasSize(1);
    }

    @Test
    public void testCreateProcessWithForkAndJoin() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask()
                .parallelGateway()
                .serviceTask()
                .parallelGateway()
                .id("join")
                .moveToLastGateway()
                .scriptTask()
                .connectTo("join")
                .userTask()
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(4);
        assertThat(modelInstance.getModelElementsByType(gatewayType))
                .hasSize(2);
    }

    @Test
    public void testCreateProcessWithMultipleParallelTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .parallelGateway("fork")
                .userTask()
                .parallelGateway("join")
                .moveToNode("fork")
                .serviceTask()
                .connectTo("join")
                .moveToNode("fork")
                .userTask()
                .connectTo("join")
                .moveToNode("fork")
                .scriptTask()
                .connectTo("join")
                .endEvent()
                .done();

        assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(2);
        assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(4);
        assertThat(modelInstance.getModelElementsByType(gatewayType))
                .hasSize(2);
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

        utils.assertCamundaFailedJobRetryTimeCycle(userTask);
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

        utils.assertErrorEventDefinition("start", "errorCode", "errorMessage", modelInstance);
        utils.assertErrorEventDefinitionForErrorVariables("start", "errorCodeVariable", "errorMessageVariable", modelInstance);
    }

    @Test
    public void testErrorDefinitionsForStartEventWithoutEventDefinitionId() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start")
                .errorEventDefinition()
                .errorCodeVariable("errorCodeVariable")
                .errorMessageVariable("errorMessageVariable")
                .error("errorCode", "errorMessage")
                .errorEventDefinitionDone()
                .endEvent().done();

        utils.assertErrorEventDefinition("start", "errorCode", "errorMessage", modelInstance);
        utils.assertErrorEventDefinitionForErrorVariables("start", "errorCodeVariable", "errorMessageVariable", modelInstance);
    }

    @Test
    public void testCallActivityCamundaVariableMappingClass() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .callActivity(CALL_ACTIVITY_ID)
                .camundaVariableMappingClass(this.getClass())
                .endEvent()
                .done();

        CallActivity callActivity = modelInstance.getModelElementById(CALL_ACTIVITY_ID);
        assertThat(callActivity.getCamundaVariableMappingClass()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void testSubProcessBuilderWrongScope() {
        try {
            modelInstance = Bpmn.createProcess()
                    .startEvent()
                    .subProcessDone()
                    .endEvent()
                    .done();
            fail("Exception expected");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(BpmnModelException.class);
        }
    }

    @Test
    public void testEventBasedGatewayAsyncAfter() {
        try {
            modelInstance = Bpmn.createProcess()
                    .startEvent()
                    .eventBasedGateway()
                    .camundaAsyncAfter()
                    .done();

            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // happy path
        }

        try {
            modelInstance = Bpmn.createProcess()
                    .startEvent()
                    .eventBasedGateway()
                    .camundaAsyncAfter(true)
                    .endEvent()
                    .done();
            fail("Expected UnsupportedOperationException");
        } catch (UnsupportedOperationException ex) {
            // happy ending :D
        }
    }

    @Test
    public void testMessageStartEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start").message("message")
                .done();

        utils.assertMessageEventDefinition("start", "message", modelInstance);
    }

    @Test
    public void testIntermediateMessageCatchEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch").message("message")
                .done();

        utils.assertMessageEventDefinition("catch", "message", modelInstance);
    }

    @Test
    public void testMessageEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent("end").message("message")
                .done();

        utils.assertMessageEventDefinition("end", "message", modelInstance);
    }

    @Test
    public void testMessageEventDefintionEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent("end")
                .messageEventDefinition()
                .message("message")
                .done();

        utils.assertMessageEventDefinition("end", "message", modelInstance);
    }

    @Test
    public void testIntermediateMessageThrowEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw").message("message")
                .done();

        utils.assertMessageEventDefinition("throw", "message", modelInstance);
    }

    @Test
    public void testIntermediateMessageEventDefintionThrowEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw")
                .messageEventDefinition()
                .message("message")
                .done();

        utils.assertMessageEventDefinition("throw", "message", modelInstance);
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

        utils.assertSignalEventDefinition("start", "signal", modelInstance);
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

        Signal signal = utils.assertSignalEventDefinition("start", "signal", modelInstance);
        Signal subSignal = utils.assertSignalEventDefinition("subStart", "signal", modelInstance);

        assertThat(signal).isEqualTo(subSignal);

        utils.assertOnlyOneSignalExists("signal", modelInstance);
    }

    @Test
    public void testIntermediateSignalCatchEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch").signal("signal")
                .done();

        utils.assertSignalEventDefinition("catch", "signal", modelInstance);
    }

    @Test
    public void testIntermediateSignalCatchEventWithExistingSignal() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch1").signal("signal")
                .intermediateCatchEvent("catch2").signal("signal")
                .done();

        Signal signal1 = utils.assertSignalEventDefinition("catch1", "signal", modelInstance);
        Signal signal2 = utils.assertSignalEventDefinition("catch2", "signal", modelInstance);

        assertThat(signal1).isEqualTo(signal2);

        utils.assertOnlyOneSignalExists("signal", modelInstance);
    }

    @Test
    public void testSignalEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent("end").signal("signal")
                .done();

        utils.assertSignalEventDefinition("end", "signal", modelInstance);
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

        Signal signal1 = utils.assertSignalEventDefinition("end1", "signal", modelInstance);
        Signal signal2 = utils.assertSignalEventDefinition("end2", "signal", modelInstance);

        assertThat(signal1).isEqualTo(signal2);

        utils.assertOnlyOneSignalExists("signal", modelInstance);
    }

    @Test
    public void testIntermediateSignalThrowEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw").signal("signal")
                .done();

        utils.assertSignalEventDefinition("throw", "signal", modelInstance);
    }

    @Test
    public void testIntermediateSignalThrowEventWithExistingSignal() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw1").signal("signal")
                .intermediateThrowEvent("throw2").signal("signal")
                .done();

        Signal signal1 = utils.assertSignalEventDefinition("throw1", "signal", modelInstance);
        Signal signal2 = utils.assertSignalEventDefinition("throw2", "signal", modelInstance);

        assertThat(signal1).isEqualTo(signal2);

        utils.assertOnlyOneSignalExists("signal", modelInstance);
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

        utils.assertSignalEventDefinition("throw", "signal", modelInstance);
        SignalEventDefinition signalEventDefinition = utils.assertAndGetSingleEventDefinition("throw", SignalEventDefinition.class, modelInstance);

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

        SignalEventDefinition signalEventDefinition = utils.assertAndGetSingleEventDefinition("throw", SignalEventDefinition.class, modelInstance);

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
        utils.assertCamundaInputOutputParameter(task);
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
        utils.assertCamundaInputOutputParameter(task);
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

        utils.assertCamundaInputOutputParameter(task);
    }

    @Test
    public void testTimerStartEventWithDate() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start").timerWithDate(TIMER_DATE)
                .done();

        utils.assertTimerWithDate("start", TIMER_DATE, modelInstance);
    }

    @Test
    public void testTimerStartEventWithDuration() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start").timerWithDuration(TIMER_DURATION)
                .done();

        utils.assertTimerWithDuration("start", TIMER_DURATION, modelInstance);
    }

    @Test
    public void testTimerStartEventWithCycle() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start").timerWithCycle(TIMER_CYCLE)
                .done();

        utils.assertTimerWithCycle("start", TIMER_CYCLE, modelInstance);
    }

    @Test
    public void testIntermediateTimerCatchEventWithDate() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch").timerWithDate(TIMER_DATE)
                .done();

        utils.assertTimerWithDate("catch", TIMER_DATE, modelInstance);
    }

    @Test
    public void testIntermediateTimerCatchEventWithDuration() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch").timerWithDuration(TIMER_DURATION)
                .done();

        utils.assertTimerWithDuration("catch", TIMER_DURATION, modelInstance);
    }

    @Test
    public void testIntermediateTimerCatchEventWithCycle() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch").timerWithCycle(TIMER_CYCLE)
                .done();

        utils.assertTimerWithCycle("catch", TIMER_CYCLE, modelInstance);
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

        utils.assertTimerWithDate("boundary", TIMER_DATE, modelInstance);
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

        utils.assertTimerWithDuration("boundary", TIMER_DURATION, modelInstance);
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

        utils.assertTimerWithCycle("boundary", TIMER_CYCLE, modelInstance);
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
    public void testOnlyOneCompensateBoundaryEventAllowed() {
        // given
        UserTaskBuilder builder = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .boundaryEvent("boundary")
                .compensateEventDefinition().compensateEventDefinitionDone()
                .compensationStart()
                .userTask("compensate").name("compensate");

        // then
        thrown.expect(BpmnModelException.class);
        thrown.expectMessage("Only single compensation handler allowed. Call compensationDone() to continue main flow.");

        // when
        builder.userTask();
    }

    @Test
    public void testInvalidCompensationStartCall() {
        // given
        StartEventBuilder builder = Bpmn.createProcess().startEvent();

        // then
        thrown.expect(BpmnModelException.class);
        thrown.expectMessage("Compensation can only be started on a boundary event with a compensation event definition");

        // when
        builder.compensationStart();
    }

    @Test
    public void testInvalidCompensationDoneCall() {
        // given
        AbstractFlowNodeBuilder builder = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .boundaryEvent("boundary")
                .compensateEventDefinition().compensateEventDefinitionDone();

        // then
        thrown.expect(BpmnModelException.class);
        thrown.expectMessage("No compensation in progress. Call compensationStart() first.");

        // when
        builder.compensationDone();
    }

    @Test
    public void testErrorBoundaryEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .endEvent()
                .moveToActivity("task")
                .boundaryEvent("boundary").error("myErrorCode", "errorMessage")
                .endEvent("boundaryEnd")
                .done();

        utils.assertErrorEventDefinition("boundary", "myErrorCode", "errorMessage", modelInstance);

        UserTask userTask = modelInstance.getModelElementById("task");
        BoundaryEvent boundaryEvent = modelInstance.getModelElementById("boundary");
        EndEvent boundaryEnd = modelInstance.getModelElementById("boundaryEnd");

        // boundary event is attached to the user task
        assertThat(boundaryEvent.getAttachedTo()).isEqualTo(userTask);

        // boundary event has no incoming sequence flows
        assertThat(boundaryEvent.getIncoming()).isEmpty();

        // the next flow node is the boundary end event
        List<FlowNode> succeedingNodes = boundaryEvent.getSucceedingNodes().list();
        assertThat(succeedingNodes).containsOnly(boundaryEnd);
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

        utils.assertErrorEventDefinition("boundary", "myErrorCode", null, modelInstance);
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

        utils.assertErrorEventDefinition("boundary", "errorCode", "errorMessage", modelInstance);
        utils.assertErrorEventDefinitionForErrorVariables("boundary", "errorCodeVariable", "errorMessageVariable", modelInstance);
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

        utils.assertErrorEventDefinition("boundary", "errorCode", "errorMessage", modelInstance);
        utils.assertErrorEventDefinitionForErrorVariables("boundary", "errorCodeVariable", "errorMessageVariable", modelInstance);
    }

    @Test
    public void testErrorEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent("end").error("myErrorCode", "errorMessage")
                .done();

        utils.assertErrorEventDefinition("end", "myErrorCode", "errorMessage", modelInstance);
    }

    @Test
    public void testErrorEndEventWithoutErrorMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent("end").error("myErrorCode")
                .done();

        utils.assertErrorEventDefinition("end", "myErrorCode", null, modelInstance);
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

        utils.assertErrorEventDefinition("subProcessStart", "myErrorCode", "errorMessage", modelInstance);
    }

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

        utils.assertErrorEventDefinition("subProcessStart", "myErrorCode", null, modelInstance);
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

        ErrorEventDefinition errorEventDefinition = utils.assertAndGetSingleEventDefinition("subProcessStart", ErrorEventDefinition.class, modelInstance);
        assertThat(errorEventDefinition.getError()).isNull();
    }

    @Test
    public void testEscalationBoundaryEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess("subProcess")
                .endEvent()
                .moveToActivity("subProcess")
                .boundaryEvent("boundary").escalation("myEscalationCode")
                .endEvent("boundaryEnd")
                .done();

        utils.assertEscalationEventDefinition("boundary", "myEscalationCode", modelInstance);

        SubProcess subProcess = modelInstance.getModelElementById("subProcess");
        BoundaryEvent boundaryEvent = modelInstance.getModelElementById("boundary");
        EndEvent boundaryEnd = modelInstance.getModelElementById("boundaryEnd");

        // boundary event is attached to the sub process
        assertThat(boundaryEvent.getAttachedTo()).isEqualTo(subProcess);

        // boundary event has no incoming sequence flows
        assertThat(boundaryEvent.getIncoming()).isEmpty();

        // the next flow node is the boundary end event
        List<FlowNode> succeedingNodes = boundaryEvent.getSucceedingNodes().list();
        assertThat(succeedingNodes).containsOnly(boundaryEnd);
    }

    @Test
    public void testEscalationEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent("end").escalation("myEscalationCode")
                .done();

        utils.assertEscalationEventDefinition("end", "myEscalationCode", modelInstance);
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

        utils.assertEscalationEventDefinition("subProcessStart", "myEscalationCode", modelInstance);
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

        EscalationEventDefinition escalationEventDefinition = utils.assertAndGetSingleEventDefinition("subProcessStart", EscalationEventDefinition.class, modelInstance);
        assertThat(escalationEventDefinition.getEscalation()).isNull();
    }

    @Test
    public void testIntermediateEscalationThrowEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw").escalation("myEscalationCode")
                .endEvent()
                .done();

        utils.assertEscalationEventDefinition("throw", "myEscalationCode", modelInstance);
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

        Escalation boundaryEscalation = utils.assertEscalationEventDefinition("boundary", "myEscalationCode", modelInstance);
        Escalation endEscalation = utils.assertEscalationEventDefinition("end", "myEscalationCode", modelInstance);

        assertThat(boundaryEscalation).isEqualTo(endEscalation);

        utils.assertOnlyOneEscalationExists("myEscalationCode", modelInstance);

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

        utils.assertCompensationEventDefinition("subProcessStart", modelInstance);
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
        utils.assertCamundaFormField(userTask);
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

        utils.assertCamundaFormField(userTask);
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

        CompensateEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition("catch", CompensateEventDefinition.class, modelInstance);
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

        CompensateEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition("throw", CompensateEventDefinition.class, modelInstance);
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

        ConditionalEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition(CATCH_ID, ConditionalEventDefinition.class, modelInstance);
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

        ConditionalEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition(CATCH_ID, ConditionalEventDefinition.class, modelInstance);
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

        ConditionalEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition(BOUNDARY_ID, ConditionalEventDefinition.class, modelInstance);
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

        ConditionalEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition(START_EVENT_ID, ConditionalEventDefinition.class, modelInstance);
        assertThat(eventDefinition.getId()).isEqualTo(CONDITION_ID);
        assertThat(eventDefinition.getCondition().getTextContent()).isEqualTo(TEST_CONDITION);
    }

    @Test
    public void testCreateEventSubProcessError() {
        ProcessBuilder process = Bpmn.createProcess();
        modelInstance = process
                .startEvent()
                .sendTask()
                .endEvent()
                .done();

        EventSubProcessBuilder eventSubProcess = process.eventSubProcess();
        eventSubProcess
                .startEvent()
                .userTask()
                .endEvent();

        try {
            eventSubProcess.subProcessDone();
            fail("eventSubProcess has returned a builder after completion");
        } catch (BpmnModelException e) {
            assertThat(e).hasMessageContaining("Unable to find a parent subProcess.");

        }
    }

    @Test
    public void testCreateEmptyProcess() {
        modelInstance = Bpmn.createProcess()
                .done();

        Definitions definitions = modelInstance.getDefinitions();
        assertThat(definitions).isNotNull();
        assertThat(definitions.getTargetNamespace()).isEqualTo(BPMN20_NS);

        Collection<ModelElementInstance> processes = modelInstance.getModelElementsByType(processType);
        assertThat(processes)
                .hasSize(1);

        Process process = (Process) processes.iterator().next();
        assertThat(process.getId()).isNotNull();
    }

    @Test
    public void testBaseElementDocumentation() {
        modelInstance = Bpmn.createProcess("process")
                .documentation("processDocumentation")
                .startEvent("startEvent")
                .documentation("startEventDocumentation_1")
                .documentation("startEventDocumentation_2")
                .documentation("startEventDocumentation_3")
                .userTask("task")
                .documentation("taskDocumentation")
                .businessRuleTask("businessruletask")
                .subProcess("subprocess")
                .documentation("subProcessDocumentation")
                .embeddedSubProcess()
                .startEvent("subprocessStartEvent")
                .endEvent("subprocessEndEvent")
                .subProcessDone()
                .endEvent("endEvent")
                .documentation("endEventDocumentation")
                .done();

        assertThat(((Process) modelInstance.getModelElementById("process")).getDocumentations().iterator().next().getTextContent()).isEqualTo("processDocumentation");
        assertThat(((UserTask) modelInstance.getModelElementById("task")).getDocumentations().iterator().next().getTextContent()).isEqualTo("taskDocumentation");
        assertThat(((SubProcess) modelInstance.getModelElementById("subprocess")).getDocumentations().iterator().next().getTextContent()).isEqualTo("subProcessDocumentation");
        assertThat(((EndEvent) modelInstance.getModelElementById("endEvent")).getDocumentations().iterator().next().getTextContent()).isEqualTo("endEventDocumentation");

        final Documentation[] startEventDocumentations = ((StartEvent) modelInstance.getModelElementById("startEvent")).getDocumentations().toArray(new Documentation[]{});
        assertThat(startEventDocumentations.length).isEqualTo(3);
        for (int i = 1; i <= 3; i++) {
            assertThat(startEventDocumentations[i - 1].getTextContent()).isEqualTo("startEventDocumentation_" + i);
        }
    }

    @Test
    public void testExtend() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask()
                .id("task1")
                .serviceTask()
                .endEvent()
                .done();

        Assertions.assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(2);

        UserTask userTask = modelInstance.getModelElementById("task1");
        SequenceFlow outgoingSequenceFlow = userTask.getOutgoing().iterator().next();
        FlowNode serviceTask = outgoingSequenceFlow.getTarget();
        userTask.getOutgoing().remove(outgoingSequenceFlow);
        userTask.builder()
                .scriptTask()
                .userTask()
                .connectTo(serviceTask.getId());

        Assertions.assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(4);
    }

    @Test
    public void testMessageBoundaryEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .endEvent()
                .moveToActivity("task") // jump back to user task and attach a boundary event
                .boundaryEvent("boundary").message("message")
                .endEvent("boundaryEnd")
                .done();

        utils.assertMessageEventDefinition("boundary", "message", modelInstance);

        UserTask userTask = modelInstance.getModelElementById("task");
        BoundaryEvent boundaryEvent = modelInstance.getModelElementById("boundary");
        EndEvent boundaryEnd = modelInstance.getModelElementById("boundaryEnd");

        // boundary event is attached to the user task
        assertThat(boundaryEvent.getAttachedTo()).isEqualTo(userTask);

        // boundary event has no incoming sequence flows
        assertThat(boundaryEvent.getIncoming()).isEmpty();

        // the next flow node is the boundary end event
        List<FlowNode> succeedingNodes = boundaryEvent.getSucceedingNodes().list();
        assertThat(succeedingNodes).containsOnly(boundaryEnd);
    }

    @Test
    public void testMultipleBoundaryEvents() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .endEvent()
                .moveToActivity("task") // jump back to user task and attach a boundary event
                .boundaryEvent("boundary1").message("message")
                .endEvent("boundaryEnd1")
                .moveToActivity("task") // jump back to user task and attach another boundary event
                .boundaryEvent("boundary2").signal("signal")
                .endEvent("boundaryEnd2")
                .done();

        utils.assertMessageEventDefinition("boundary1", "message", modelInstance);
        utils.assertSignalEventDefinition("boundary2", "signal", modelInstance);

        UserTask userTask = modelInstance.getModelElementById("task");
        BoundaryEvent boundaryEvent1 = modelInstance.getModelElementById("boundary1");
        EndEvent boundaryEnd1 = modelInstance.getModelElementById("boundaryEnd1");
        BoundaryEvent boundaryEvent2 = modelInstance.getModelElementById("boundary2");
        EndEvent boundaryEnd2 = modelInstance.getModelElementById("boundaryEnd2");

        // boundary events are attached to the user task
        assertThat(boundaryEvent1.getAttachedTo()).isEqualTo(userTask);
        assertThat(boundaryEvent2.getAttachedTo()).isEqualTo(userTask);

        // boundary events have no incoming sequence flows
        assertThat(boundaryEvent1.getIncoming()).isEmpty();
        assertThat(boundaryEvent2.getIncoming()).isEmpty();

        // the next flow node is the boundary end event
        List<FlowNode> succeedingNodes = boundaryEvent1.getSucceedingNodes().list();
        assertThat(succeedingNodes).containsOnly(boundaryEnd1);
        succeedingNodes = boundaryEvent2.getSucceedingNodes().list();
        assertThat(succeedingNodes).containsOnly(boundaryEnd2);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsSequential() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .multiInstance()
                .sequential()
                .cardinality("card")
                .completionCondition("compl")
                .camundaCollection("coll")
                .camundaElementVariable("element")
                .multiInstanceDone()
                .endEvent()
                .done();

        UserTask userTask = modelInstance.getModelElementById("task");
        Collection<MultiInstanceLoopCharacteristics> miCharacteristics =
                userTask.getChildElementsByType(MultiInstanceLoopCharacteristics.class);

        assertThat(miCharacteristics).hasSize(1);

        MultiInstanceLoopCharacteristics miCharacteristic = miCharacteristics.iterator().next();
        assertThat(miCharacteristic.isSequential()).isTrue();
        assertThat(miCharacteristic.getLoopCardinality().getTextContent()).isEqualTo("card");
        assertThat(miCharacteristic.getCompletionCondition().getTextContent()).isEqualTo("compl");
        assertThat(miCharacteristic.getCamundaCollection()).isEqualTo("coll");
        assertThat(miCharacteristic.getCamundaElementVariable()).isEqualTo("element");

    }

    @Test
    public void testMultiInstanceLoopCharacteristicsParallel() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .multiInstance()
                .parallel()
                .multiInstanceDone()
                .endEvent()
                .done();

        UserTask userTask = modelInstance.getModelElementById("task");
        Collection<MultiInstanceLoopCharacteristics> miCharacteristics =
                userTask.getChildElementsByType(MultiInstanceLoopCharacteristics.class);

        assertThat(miCharacteristics).hasSize(1);

        MultiInstanceLoopCharacteristics miCharacteristic = miCharacteristics.iterator().next();
        assertThat(miCharacteristic.isSequential()).isFalse();
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsAsynchronousMultiInstanceAsyncBeforeElement() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .multiInstance()
                .camundaAsyncBefore()
                .parallel()
                .multiInstanceDone()
                .endEvent()
                .done();

        UserTask userTask = modelInstance.getModelElementById("task");
        Collection<MultiInstanceLoopCharacteristics> miCharacteristics =
                userTask.getChildElementsByType(MultiInstanceLoopCharacteristics.class);

        assertThat(miCharacteristics).hasSize(1);

        MultiInstanceLoopCharacteristics miCharacteristic = miCharacteristics.iterator().next();
        assertThat(miCharacteristic.isSequential()).isFalse();
        assertThat(miCharacteristic.isCamundaAsyncAfter()).isFalse();
        assertThat(miCharacteristic.isCamundaAsyncBefore()).isTrue();
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsAsynchronousMultiInstanceAsyncAfterElement() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .multiInstance()
                .camundaAsyncAfter()
                .parallel()
                .multiInstanceDone()
                .endEvent()
                .done();

        UserTask userTask = modelInstance.getModelElementById("task");
        Collection<MultiInstanceLoopCharacteristics> miCharacteristics =
                userTask.getChildElementsByType(MultiInstanceLoopCharacteristics.class);

        assertThat(miCharacteristics).hasSize(1);

        MultiInstanceLoopCharacteristics miCharacteristic = miCharacteristics.iterator().next();
        assertThat(miCharacteristic.isSequential()).isFalse();
        assertThat(miCharacteristic.isCamundaAsyncAfter()).isTrue();
        assertThat(miCharacteristic.isCamundaAsyncBefore()).isFalse();
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

        ErrorEventDefinition errorEventDefinition = utils.assertAndGetSingleEventDefinition("boundary", ErrorEventDefinition.class, modelInstance);
        assertThat(errorEventDefinition.getError()).isNull();
    }

    @Test
    public void testCompensationTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .boundaryEvent("boundary")
                .compensateEventDefinition().compensateEventDefinitionDone()
                .compensationStart()
                .userTask("compensate").name("compensate")
                .compensationDone()
                .endEvent("theend")
                .done();

        // Checking Association
        Collection<Association> associations = modelInstance.getModelElementsByType(Association.class);
        assertThat(associations).hasSize(1);
        Association association = associations.iterator().next();
        assertThat(association.getSource().getId()).isEqualTo("boundary");
        assertThat(association.getTarget().getId()).isEqualTo("compensate");
        assertThat(association.getAssociationDirection()).isEqualTo(AssociationDirection.One);

        // Checking Sequence flow
        UserTask task = modelInstance.getModelElementById("task");
        Collection<SequenceFlow> outgoing = task.getOutgoing();
        assertThat(outgoing).hasSize(1);
        SequenceFlow flow = outgoing.iterator().next();
        assertThat(flow.getSource().getId()).isEqualTo("task");
        assertThat(flow.getTarget().getId()).isEqualTo("theend");

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

        EscalationEventDefinition escalationEventDefinition = utils.assertAndGetSingleEventDefinition("boundary", EscalationEventDefinition.class, modelInstance);
        assertThat(escalationEventDefinition.getEscalation()).isNull();
    }

    @Test
    public void testCompensateEventDefintionCatchStartEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start")
                .compensateEventDefinition()
                .waitForCompletion(false)
                .compensateEventDefinitionDone()
                .userTask("userTask")
                .endEvent("end")
                .done();

        CompensateEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition("start", CompensateEventDefinition.class, modelInstance);
        Activity activity = eventDefinition.getActivity();
        assertThat(activity).isNull();
        assertThat(eventDefinition.isWaitForCompletion()).isFalse();
    }

    @Test
    public void testCompensateEventDefintionCatchBoundaryEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("userTask")
                .boundaryEvent("catch")
                .compensateEventDefinition()
                .waitForCompletion(false)
                .compensateEventDefinitionDone()
                .endEvent("end")
                .done();

        CompensateEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition("catch", CompensateEventDefinition.class, modelInstance);
        Activity activity = eventDefinition.getActivity();
        assertThat(activity).isNull();
        assertThat(eventDefinition.isWaitForCompletion()).isFalse();
    }

    @Test
    public void testCompensateEventDefintionThrowEndEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("userTask")
                .endEvent("end")
                .compensateEventDefinition()
                .activityRef("userTask")
                .waitForCompletion(true)
                .compensateEventDefinitionDone()
                .done();

        CompensateEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition("end", CompensateEventDefinition.class, modelInstance);
        Activity activity = eventDefinition.getActivity();
        assertThat(activity).isEqualTo(modelInstance.getModelElementById("userTask"));
        assertThat(eventDefinition.isWaitForCompletion()).isTrue();
    }

    @Test
    public void testCompensateEventDefintionThrowIntermediateEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("userTask")
                .intermediateThrowEvent("throw")
                .compensateEventDefinition()
                .activityRef("userTask")
                .waitForCompletion(true)
                .compensateEventDefinitionDone()
                .endEvent("end")
                .done();

        CompensateEventDefinition eventDefinition = utils.assertAndGetSingleEventDefinition("throw", CompensateEventDefinition.class, modelInstance);
        Activity activity = eventDefinition.getActivity();
        assertThat(activity).isEqualTo(modelInstance.getModelElementById("userTask"));
        assertThat(eventDefinition.isWaitForCompletion()).isTrue();
    }

    @Test
    public void testCreateEventSubProcess() {
        ProcessBuilder process = Bpmn.createProcess();
        modelInstance = process
                .startEvent()
                .sendTask()
                .endEvent()
                .done();

        EventSubProcessBuilder eventSubProcess = process.eventSubProcess();
        eventSubProcess
                .startEvent()
                .userTask()
                .endEvent();

        SubProcess subProcess = eventSubProcess.getElement();

        // no input or output from the sub process
        assertThat(subProcess.getIncoming().isEmpty());
        assertThat(subProcess.getOutgoing().isEmpty());

        // subProcess was triggered by event
        assertThat(eventSubProcess.getElement().triggeredByEvent());

        // subProcess contains startEvent, sendTask and endEvent
        assertThat(subProcess.getChildElementsByType(StartEvent.class)).isNotNull();
        assertThat(subProcess.getChildElementsByType(UserTask.class)).isNotNull();
        assertThat(subProcess.getChildElementsByType(EndEvent.class)).isNotNull();
    }

    @Test
    public void testCreateEventSubProcessInSubProcess() {
        ProcessBuilder process = Bpmn.createProcess();
        modelInstance = process
                .startEvent()
                .subProcess("mysubprocess")
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .endEvent()
                .subProcessDone()
                .userTask()
                .endEvent()
                .done();

        SubProcess subprocess = modelInstance.getModelElementById("mysubprocess");
        subprocess
                .builder()
                .embeddedSubProcess()
                .eventSubProcess("myeventsubprocess")
                .startEvent()
                .userTask()
                .endEvent()
                .subProcessDone();

        SubProcess eventSubProcess = modelInstance.getModelElementById("myeventsubprocess");

        // no input or output from the sub process
        assertThat(eventSubProcess.getIncoming().isEmpty());
        assertThat(eventSubProcess.getOutgoing().isEmpty());

        // subProcess was triggered by event
        assertThat(eventSubProcess.triggeredByEvent());

        // subProcess contains startEvent, sendTask and endEvent
        assertThat(eventSubProcess.getChildElementsByType(StartEvent.class)).isNotNull();
        assertThat(eventSubProcess.getChildElementsByType(UserTask.class)).isNotNull();
        assertThat(eventSubProcess.getChildElementsByType(EndEvent.class)).isNotNull();
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
}
