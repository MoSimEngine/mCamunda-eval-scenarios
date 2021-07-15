package org.camunda.bpm.model.bpmn.builder;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.TransactionMethod;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaErrorEventDefinition;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaIn;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOut;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.CallActivity;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.SendTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.Task;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.StartEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.GatewayDirection;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.messaging.Message;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Error;
import org.camunda.bpm.model.bpmn.instance.paradigm.subprocesses.SubProcess;
import org.camunda.bpm.model.bpmn.instance.paradigm.subprocesses.Transaction;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.CALL_ACTIVITY_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.EXTERNAL_TASK_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.SERVICE_TASK_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.START_EVENT_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.SUB_PROCESS_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TASK_ID;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_CLASS_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_DELEGATE_EXPRESSION_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_EXPRESSION_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_EXTERNAL_TASK_TOPIC;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_SERVICE_TASK_PRIORITY;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TEST_STRING_API;
import static org.camunda.bpm.model.bpmn.BpmnTestConstants.TRANSACTION_ID;
import static org.junit.Assert.assertEquals;

public class ProcessBuilderParadigmTest extends ProcessBuilderTestUtils {
    public static final String TIMER_DATE = "2011-03-11T12:13:14Z";
    public static final String TIMER_DURATION = "P10D";
    public static final String TIMER_CYCLE = "R3/PT10H";
    public static final String FAILED_JOB_RETRY_TIME_CYCLE = "R5/PT1M";
    protected static ModelElementType taskType;
    protected static ModelElementType processType;
    private static ModelElementType gatewayType;
    private static ModelElementType eventType;
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    protected BpmnModelInstance modelInstance;

    @Test
    public void testCreateProcessWithInclusiveGateway() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask()
                .inclusiveGateway()
                .condition("approved", "${approved}")
                .serviceTask()
                .endEvent()
                .moveToLastGateway()
                .condition("not approved", "${!approved}")
                .scriptTask()
                .endEvent()
                .done();

        ModelElementType inclusiveGwType = modelInstance.getModel().getType(InclusiveGateway.class);

        Assertions.assertThat(modelInstance.getModelElementsByType(eventType))
                .hasSize(3);
        Assertions.assertThat(modelInstance.getModelElementsByType(taskType))
                .hasSize(3);
        Assertions.assertThat(modelInstance.getModelElementsByType(inclusiveGwType))
                .hasSize(1);
    }

    @Test
    public void testCreateInvoiceProcess() {
        modelInstance = Bpmn.createProcess()
                .executable()
                .startEvent()
                .name("Invoice received")
                .camundaFormKey("embedded:app:forms/start-form.html")
                .userTask()
                .name("Assign Approver")
                .camundaFormKey("embedded:app:forms/assign-approver.html")
                .camundaAssignee("demo")
                .userTask("approveInvoice")
                .name("Approve Invoice")
                .camundaFormKey("embedded:app:forms/approve-invoice.html")
                .camundaAssignee("${approver}")
                .exclusiveGateway()
                .name("Invoice approved?")
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("yes", "${approved}")
                .userTask()
                .name("Prepare Bank Transfer")
                .camundaFormKey("embedded:app:forms/prepare-bank-transfer.html")
                .camundaCandidateGroups("accounting")
                .serviceTask()
                .name("Archive Invoice")
                .camundaClass("org.camunda.bpm.example.invoice.service.ArchiveInvoiceService")
                .endEvent()
                .name("Invoice processed")
                .moveToLastGateway()
                .condition("no", "${!approved}")
                .userTask()
                .name("Review Invoice")
                .camundaFormKey("embedded:app:forms/review-invoice.html")
                .camundaAssignee("demo")
                .exclusiveGateway()
                .name("Review successful?")
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("no", "${!clarified}")
                .endEvent()
                .name("Invoice not processed")
                .moveToLastGateway()
                .condition("yes", "${clarified}")
                .connectTo("approveInvoice")
                .done();
    }

    @Test
    public void testTaskCamundaExternalTask() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .serviceTask(EXTERNAL_TASK_ID)
                .camundaExternalTask(TEST_EXTERNAL_TASK_TOPIC)
                .endEvent()
                .done();

        ServiceTask serviceTask = modelInstance.getModelElementById(EXTERNAL_TASK_ID);
        assertThat(serviceTask.getCamundaType()).isEqualTo("external");
        assertThat(serviceTask.getCamundaTopic()).isEqualTo(TEST_EXTERNAL_TASK_TOPIC);
    }

    @Test
    public void testTaskCamundaExternalTaskErrorEventDefinition() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .serviceTask(EXTERNAL_TASK_ID)
                .camundaExternalTask(TEST_EXTERNAL_TASK_TOPIC)
                .camundaErrorEventDefinition().id("id").error("myErrorCode", "errorMessage").expression("expression").errorEventDefinitionDone()
                .endEvent()
                .moveToActivity(EXTERNAL_TASK_ID)
                .boundaryEvent("boundary").error("myErrorCode", "errorMessage")
                .endEvent("boundaryEnd")
                .done();

        ServiceTask externalTask = modelInstance.getModelElementById(EXTERNAL_TASK_ID);
        ExtensionElements extensionElements = externalTask.getExtensionElements();
        Collection<org.camunda.bpm.model.bpmn.instance.camunda.CamundaErrorEventDefinition> errorEventDefinitions = extensionElements.getChildElementsByType(org.camunda.bpm.model.bpmn.instance.camunda.CamundaErrorEventDefinition.class);
        assertThat(errorEventDefinitions).hasSize(1);
        CamundaErrorEventDefinition camundaErrorEventDefinition = errorEventDefinitions.iterator().next();
        assertThat(camundaErrorEventDefinition).isNotNull();
        assertThat(camundaErrorEventDefinition.getId()).isEqualTo("id");
        assertThat(camundaErrorEventDefinition.getCamundaExpression()).isEqualTo("expression");
        assertErrorEventDefinition("boundary", "myErrorCode", "errorMessage", modelInstance);
    }

    @Test
    public void testTaskCamundaExtensions() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .serviceTask(TASK_ID)
                .camundaAsyncBefore()
                .notCamundaExclusive()
                .camundaJobPriority("${somePriority}")
                .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .endEvent()
                .done();

        ServiceTask serviceTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(serviceTask.isCamundaAsyncBefore()).isTrue();
        assertThat(serviceTask.isCamundaExclusive()).isFalse();
        assertThat(serviceTask.getCamundaJobPriority()).isEqualTo("${somePriority}");
        assertThat(serviceTask.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);

        assertCamundaFailedJobRetryTimeCycle(serviceTask);
    }

    @Test
    public void testServiceTaskCamundaExtensions() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .serviceTask(TASK_ID)
                .camundaClass(TEST_CLASS_API)
                .camundaDelegateExpression(TEST_DELEGATE_EXPRESSION_API)
                .camundaExpression(TEST_EXPRESSION_API)
                .camundaResultVariable(TEST_STRING_API)
                .camundaTopic(TEST_STRING_API)
                .camundaType(TEST_STRING_API)
                .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .done();

        ServiceTask serviceTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(serviceTask.getCamundaClass()).isEqualTo(TEST_CLASS_API);
        assertThat(serviceTask.getCamundaDelegateExpression()).isEqualTo(TEST_DELEGATE_EXPRESSION_API);
        assertThat(serviceTask.getCamundaExpression()).isEqualTo(TEST_EXPRESSION_API);
        assertThat(serviceTask.getCamundaResultVariable()).isEqualTo(TEST_STRING_API);
        assertThat(serviceTask.getCamundaTopic()).isEqualTo(TEST_STRING_API);
        assertThat(serviceTask.getCamundaType()).isEqualTo(TEST_STRING_API);
        assertThat(serviceTask.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);

        assertCamundaFailedJobRetryTimeCycle(serviceTask);
    }

    @Test
    public void testServiceTaskCamundaClass() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .serviceTask(TASK_ID)
                .camundaClass(getClass().getName())
                .done();

        ServiceTask serviceTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(serviceTask.getCamundaClass()).isEqualTo(getClass().getName());
    }

    @Test
    public void testSendTaskCamundaExtensions() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .sendTask(TASK_ID)
                .camundaClass(TEST_CLASS_API)
                .camundaDelegateExpression(TEST_DELEGATE_EXPRESSION_API)
                .camundaExpression(TEST_EXPRESSION_API)
                .camundaResultVariable(TEST_STRING_API)
                .camundaTopic(TEST_STRING_API)
                .camundaType(TEST_STRING_API)
                .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .endEvent()
                .done();

        SendTask sendTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(sendTask.getCamundaClass()).isEqualTo(TEST_CLASS_API);
        assertThat(sendTask.getCamundaDelegateExpression()).isEqualTo(TEST_DELEGATE_EXPRESSION_API);
        assertThat(sendTask.getCamundaExpression()).isEqualTo(TEST_EXPRESSION_API);
        assertThat(sendTask.getCamundaResultVariable()).isEqualTo(TEST_STRING_API);
        assertThat(sendTask.getCamundaTopic()).isEqualTo(TEST_STRING_API);
        assertThat(sendTask.getCamundaType()).isEqualTo(TEST_STRING_API);
        assertThat(sendTask.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);

        assertCamundaFailedJobRetryTimeCycle(sendTask);
    }

    @Test
    public void testSendTaskCamundaClass() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .sendTask(TASK_ID)
                .camundaClass(this.getClass())
                .endEvent()
                .done();

        SendTask sendTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(sendTask.getCamundaClass()).isEqualTo(this.getClass().getName());
    }

    @Test
    public void testBusinessRuleTaskCamundaExtensions() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .businessRuleTask(TASK_ID)
                .camundaClass(TEST_CLASS_API)
                .camundaDelegateExpression(TEST_DELEGATE_EXPRESSION_API)
                .camundaExpression(TEST_EXPRESSION_API)
                .camundaResultVariable("resultVar")
                .camundaTopic("topic")
                .camundaType("type")
                .camundaDecisionRef("decisionRef")
                .camundaDecisionRefBinding("latest")
                .camundaDecisionRefVersion("7")
                .camundaDecisionRefVersionTag("0.1.0")
                .camundaDecisionRefTenantId("tenantId")
                .camundaMapDecisionResult("singleEntry")
                .camundaTaskPriority(TEST_SERVICE_TASK_PRIORITY)
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .endEvent()
                .done();

        BusinessRuleTask businessRuleTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(businessRuleTask.getCamundaClass()).isEqualTo(TEST_CLASS_API);
        assertThat(businessRuleTask.getCamundaDelegateExpression()).isEqualTo(TEST_DELEGATE_EXPRESSION_API);
        assertThat(businessRuleTask.getCamundaExpression()).isEqualTo(TEST_EXPRESSION_API);
        assertThat(businessRuleTask.getCamundaResultVariable()).isEqualTo("resultVar");
        assertThat(businessRuleTask.getCamundaTopic()).isEqualTo("topic");
        assertThat(businessRuleTask.getCamundaType()).isEqualTo("type");
        assertThat(businessRuleTask.getCamundaDecisionRef()).isEqualTo("decisionRef");
        assertThat(businessRuleTask.getCamundaDecisionRefBinding()).isEqualTo("latest");
        assertThat(businessRuleTask.getCamundaDecisionRefVersion()).isEqualTo("7");
        assertThat(businessRuleTask.getCamundaDecisionRefVersionTag()).isEqualTo("0.1.0");
        assertThat(businessRuleTask.getCamundaDecisionRefTenantId()).isEqualTo("tenantId");
        assertThat(businessRuleTask.getCamundaMapDecisionResult()).isEqualTo("singleEntry");
        assertThat(businessRuleTask.getCamundaTaskPriority()).isEqualTo(TEST_SERVICE_TASK_PRIORITY);

        assertCamundaFailedJobRetryTimeCycle(businessRuleTask);
    }

    @Test
    public void testBusinessRuleTaskCamundaClass() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .businessRuleTask(TASK_ID)
                .camundaClass(Bpmn.class)
                .endEvent()
                .done();

        BusinessRuleTask businessRuleTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(businessRuleTask.getCamundaClass()).isEqualTo("org.camunda.bpm.model.bpmn.Bpmn");
    }

    @Test
    public void testScriptTaskCamundaExtensions() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .scriptTask(TASK_ID)
                .camundaResultVariable(TEST_STRING_API)
                .camundaResource(TEST_STRING_API)
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .endEvent()
                .done();

        ScriptTask scriptTask = modelInstance.getModelElementById(TASK_ID);
        assertThat(scriptTask.getCamundaResultVariable()).isEqualTo(TEST_STRING_API);
        assertThat(scriptTask.getCamundaResource()).isEqualTo(TEST_STRING_API);

        assertCamundaFailedJobRetryTimeCycle(scriptTask);
    }

    @Test
    public void testStartEventCamundaExtensions() {
        modelInstance = Bpmn.createProcess()
                .startEvent(START_EVENT_ID)
                .camundaAsyncBefore()
                .notCamundaExclusive()
                .camundaFormHandlerClass(TEST_CLASS_API)
                .camundaFormKey(TEST_STRING_API)
                .camundaInitiator(TEST_STRING_API)
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .done();

        StartEvent startEvent = modelInstance.getModelElementById(START_EVENT_ID);
        assertThat(startEvent.isCamundaAsyncBefore()).isTrue();
        assertThat(startEvent.isCamundaExclusive()).isFalse();
        assertThat(startEvent.getCamundaFormHandlerClass()).isEqualTo(TEST_CLASS_API);
        assertThat(startEvent.getCamundaFormKey()).isEqualTo(TEST_STRING_API);
        assertThat(startEvent.getCamundaInitiator()).isEqualTo(TEST_STRING_API);

        assertCamundaFailedJobRetryTimeCycle(startEvent);
    }

    @Test
    public void testCallActivityCamundaExtension() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .callActivity(CALL_ACTIVITY_ID)
                .calledElement(TEST_STRING_API)
                .camundaAsyncBefore()
                .camundaCalledElementBinding("version")
                .camundaCalledElementVersion("1.0")
                .camundaCalledElementVersionTag("ver-1.0")
                .camundaCalledElementTenantId("t1")
                .camundaCaseRef("case")
                .camundaCaseBinding("deployment")
                .camundaCaseVersion("2")
                .camundaCaseTenantId("t2")
                .camundaIn("in-source", "in-target")
                .camundaOut("out-source", "out-target")
                .camundaVariableMappingClass(TEST_CLASS_API)
                .camundaVariableMappingDelegateExpression(TEST_DELEGATE_EXPRESSION_API)
                .notCamundaExclusive()
                .camundaFailedJobRetryTimeCycle(ProcessBuilderDomainCombinedTest.FAILED_JOB_RETRY_TIME_CYCLE)
                .endEvent()
                .done();

        CallActivity callActivity = modelInstance.getModelElementById(CALL_ACTIVITY_ID);
        assertThat(callActivity.getCalledElement()).isEqualTo(TEST_STRING_API);
        assertThat(callActivity.isCamundaAsyncBefore()).isTrue();
        assertThat(callActivity.getCamundaCalledElementBinding()).isEqualTo("version");
        assertThat(callActivity.getCamundaCalledElementVersion()).isEqualTo("1.0");
        assertThat(callActivity.getCamundaCalledElementVersionTag()).isEqualTo("ver-1.0");
        assertThat(callActivity.getCamundaCalledElementTenantId()).isEqualTo("t1");
        assertThat(callActivity.getCamundaCaseRef()).isEqualTo("case");
        assertThat(callActivity.getCamundaCaseBinding()).isEqualTo("deployment");
        assertThat(callActivity.getCamundaCaseVersion()).isEqualTo("2");
        assertThat(callActivity.getCamundaCaseTenantId()).isEqualTo("t2");
        assertThat(callActivity.isCamundaExclusive()).isFalse();

        CamundaIn camundaIn = (CamundaIn) callActivity.getExtensionElements().getUniqueChildElementByType(CamundaIn.class);
        assertThat(camundaIn.getCamundaSource()).isEqualTo("in-source");
        assertThat(camundaIn.getCamundaTarget()).isEqualTo("in-target");

        CamundaOut camundaOut = (CamundaOut) callActivity.getExtensionElements().getUniqueChildElementByType(CamundaOut.class);
        assertThat(camundaOut.getCamundaSource()).isEqualTo("out-source");
        assertThat(camundaOut.getCamundaTarget()).isEqualTo("out-target");

        assertThat(callActivity.getCamundaVariableMappingClass()).isEqualTo(TEST_CLASS_API);
        assertThat(callActivity.getCamundaVariableMappingDelegateExpression()).isEqualTo(TEST_DELEGATE_EXPRESSION_API);
        assertCamundaFailedJobRetryTimeCycle(callActivity);
    }

    @Test
    public void testSubProcessBuilder() {
        BpmnModelInstance modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess(SUB_PROCESS_ID)
                .camundaAsyncBefore()
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .endEvent()
                .subProcessDone()
                .serviceTask(SERVICE_TASK_ID)
                .endEvent()
                .done();

        SubProcess subProcess = modelInstance.getModelElementById(SUB_PROCESS_ID);
        ServiceTask serviceTask = modelInstance.getModelElementById(SERVICE_TASK_ID);
        assertThat(subProcess.isCamundaAsyncBefore()).isTrue();
        assertThat(subProcess.isCamundaExclusive()).isTrue();
        assertThat(subProcess.getChildElementsByType(Event.class)).hasSize(2);
        assertThat(subProcess.getChildElementsByType(Task.class)).hasSize(1);
        assertThat(subProcess.getFlowElements()).hasSize(5);
        assertThat(subProcess.getSucceedingNodes().singleResult()).isEqualTo(serviceTask);
    }

    @Test
    public void testSubProcessBuilderDetached() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess(SUB_PROCESS_ID)
                .serviceTask(SERVICE_TASK_ID)
                .endEvent()
                .done();

        SubProcess subProcess = modelInstance.getModelElementById(SUB_PROCESS_ID);

        subProcess.builder()
                .camundaAsyncBefore()
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .endEvent();

        ServiceTask serviceTask = modelInstance.getModelElementById(SERVICE_TASK_ID);
        assertThat(subProcess.isCamundaAsyncBefore()).isTrue();
        assertThat(subProcess.isCamundaExclusive()).isTrue();
        assertThat(subProcess.getChildElementsByType(Event.class)).hasSize(2);
        assertThat(subProcess.getChildElementsByType(Task.class)).hasSize(1);
        assertThat(subProcess.getFlowElements()).hasSize(5);
        assertThat(subProcess.getSucceedingNodes().singleResult()).isEqualTo(serviceTask);
    }

    @Test
    public void testSubProcessBuilderNested() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess(SUB_PROCESS_ID + 1)
                .camundaAsyncBefore()
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .subProcess(SUB_PROCESS_ID + 2)
                .camundaAsyncBefore()
                .notCamundaExclusive()
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .endEvent()
                .subProcessDone()
                .serviceTask(SERVICE_TASK_ID + 1)
                .endEvent()
                .subProcessDone()
                .serviceTask(SERVICE_TASK_ID + 2)
                .endEvent()
                .done();

        SubProcess subProcess = modelInstance.getModelElementById(SUB_PROCESS_ID + 1);
        ServiceTask serviceTask = modelInstance.getModelElementById(SERVICE_TASK_ID + 2);
        assertThat(subProcess.isCamundaAsyncBefore()).isTrue();
        assertThat(subProcess.isCamundaExclusive()).isTrue();
        assertThat(subProcess.getChildElementsByType(Event.class)).hasSize(2);
        assertThat(subProcess.getChildElementsByType(Task.class)).hasSize(2);
        assertThat(subProcess.getChildElementsByType(SubProcess.class)).hasSize(1);
        assertThat(subProcess.getFlowElements()).hasSize(9);
        assertThat(subProcess.getSucceedingNodes().singleResult()).isEqualTo(serviceTask);

        SubProcess nestedSubProcess = modelInstance.getModelElementById(SUB_PROCESS_ID + 2);
        ServiceTask nestedServiceTask = modelInstance.getModelElementById(SERVICE_TASK_ID + 1);
        assertThat(nestedSubProcess.isCamundaAsyncBefore()).isTrue();
        assertThat(nestedSubProcess.isCamundaExclusive()).isFalse();
        assertThat(nestedSubProcess.getChildElementsByType(Event.class)).hasSize(2);
        assertThat(nestedSubProcess.getChildElementsByType(Task.class)).hasSize(1);
        assertThat(nestedSubProcess.getFlowElements()).hasSize(5);
        assertThat(nestedSubProcess.getSucceedingNodes().singleResult()).isEqualTo(nestedServiceTask);
    }

    @Test
    public void testTransactionBuilder() {
        BpmnModelInstance modelInstance = Bpmn.createProcess()
                .startEvent()
                .transaction(TRANSACTION_ID)
                .camundaAsyncBefore()
                .method(TransactionMethod.Image)
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .endEvent()
                .transactionDone()
                .serviceTask(SERVICE_TASK_ID)
                .endEvent()
                .done();

        Transaction transaction = modelInstance.getModelElementById(TRANSACTION_ID);
        ServiceTask serviceTask = modelInstance.getModelElementById(SERVICE_TASK_ID);
        assertThat(transaction.isCamundaAsyncBefore()).isTrue();
        assertThat(transaction.isCamundaExclusive()).isTrue();
        assertThat(transaction.getMethod()).isEqualTo(TransactionMethod.Image);
        assertThat(transaction.getChildElementsByType(Event.class)).hasSize(2);
        assertThat(transaction.getChildElementsByType(Task.class)).hasSize(1);
        assertThat(transaction.getFlowElements()).hasSize(5);
        assertThat(transaction.getSucceedingNodes().singleResult()).isEqualTo(serviceTask);
    }

    @Test
    public void testTransactionBuilderDetached() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .transaction(TRANSACTION_ID)
                .serviceTask(SERVICE_TASK_ID)
                .endEvent()
                .done();

        Transaction transaction = modelInstance.getModelElementById(TRANSACTION_ID);

        transaction.builder()
                .camundaAsyncBefore()
                .embeddedSubProcess()
                .startEvent()
                .userTask()
                .endEvent();

        ServiceTask serviceTask = modelInstance.getModelElementById(SERVICE_TASK_ID);
        assertThat(transaction.isCamundaAsyncBefore()).isTrue();
        assertThat(transaction.isCamundaExclusive()).isTrue();
        assertThat(transaction.getChildElementsByType(Event.class)).hasSize(2);
        assertThat(transaction.getChildElementsByType(Task.class)).hasSize(1);
        assertThat(transaction.getFlowElements()).hasSize(5);
        assertThat(transaction.getSucceedingNodes().singleResult()).isEqualTo(serviceTask);
    }

    @Test
    public void testScriptText() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .scriptTask("script")
                .scriptFormat("groovy")
                .scriptText("println \"hello, world\";")
                .endEvent()
                .done();

        ScriptTask scriptTask = modelInstance.getModelElementById("script");
        assertThat(scriptTask.getScriptFormat()).isEqualTo("groovy");
        assertThat(scriptTask.getScript().getTextContent()).isEqualTo("println \"hello, world\";");
    }

    @Test
    public void testMessageStartEventWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent("start").message("message")
                .subProcess().triggerByEvent()
                .embeddedSubProcess()
                .startEvent("subStart").message("message")
                .subProcessDone()
                .done();

        Message message = assertMessageEventDefinition("start", "message", modelInstance);
        Message subMessage = assertMessageEventDefinition("subStart", "message", modelInstance);

        assertThat(message).isEqualTo(subMessage);

        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testIntermediateMessageCatchEventWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateCatchEvent("catch1").message("message")
                .intermediateCatchEvent("catch2").message("message")
                .done();

        Message message1 = assertMessageEventDefinition("catch1", "message", modelInstance);
        Message message2 = assertMessageEventDefinition("catch2", "message", modelInstance);

        assertThat(message1).isEqualTo(message2);

        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testMessageEndEventWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .parallelGateway()
                .endEvent("end1").message("message")
                .moveToLastGateway()
                .endEvent("end2").message("message")
                .done();

        Message message1 = assertMessageEventDefinition("end1", "message", modelInstance);
        Message message2 = assertMessageEventDefinition("end2", "message", modelInstance);

        assertThat(message1).isEqualTo(message2);

        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testMessageEventDefinitionEndEventWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .parallelGateway()
                .endEvent("end1")
                .messageEventDefinition()
                .message("message")
                .messageEventDefinitionDone()
                .moveToLastGateway()
                .endEvent("end2")
                .messageEventDefinition()
                .message("message")
                .done();

        Message message1 = assertMessageEventDefinition("end1", "message", modelInstance);
        Message message2 = assertMessageEventDefinition("end2", "message", modelInstance);

        assertThat(message1).isEqualTo(message2);

        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testIntermediateMessageThrowEventWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw1").message("message")
                .intermediateThrowEvent("throw2").message("message")
                .done();

        Message message1 = assertMessageEventDefinition("throw1", "message", modelInstance);
        Message message2 = assertMessageEventDefinition("throw2", "message", modelInstance);

        assertThat(message1).isEqualTo(message2);
        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testIntermediateMessageEventDefintionThrowEventWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .intermediateThrowEvent("throw1")
                .messageEventDefinition()
                .message("message")
                .messageEventDefinitionDone()
                .intermediateThrowEvent("throw2")
                .messageEventDefinition()
                .message("message")
                .messageEventDefinitionDone()
                .done();

        Message message1 = assertMessageEventDefinition("throw1", "message", modelInstance);
        Message message2 = assertMessageEventDefinition("throw2", "message", modelInstance);

        assertThat(message1).isEqualTo(message2);
        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testReceiveTaskMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .receiveTask("receive").message("message")
                .done();

        ReceiveTask receiveTask = modelInstance.getModelElementById("receive");

        Message message = receiveTask.getMessage();
        assertThat(message).isNotNull();
        assertThat(message.getName()).isEqualTo("message");
    }

    @Test
    public void testReceiveTaskWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .receiveTask("receive1").message("message")
                .receiveTask("receive2").message("message")
                .done();

        ReceiveTask receiveTask1 = modelInstance.getModelElementById("receive1");
        Message message1 = receiveTask1.getMessage();

        ReceiveTask receiveTask2 = modelInstance.getModelElementById("receive2");
        Message message2 = receiveTask2.getMessage();

        assertThat(message1).isEqualTo(message2);

        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testSendTaskMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .sendTask("send").message("message")
                .done();

        SendTask sendTask = modelInstance.getModelElementById("send");

        Message message = sendTask.getMessage();
        assertThat(message).isNotNull();
        assertThat(message.getName()).isEqualTo("message");
    }

    @Test
    public void testSendTaskWithExistingMessage() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .sendTask("send1").message("message")
                .sendTask("send2").message("message")
                .done();

        SendTask sendTask1 = modelInstance.getModelElementById("send1");
        Message message1 = sendTask1.getMessage();

        SendTask sendTask2 = modelInstance.getModelElementById("send2");
        Message message2 = sendTask2.getMessage();

        assertThat(message1).isEqualTo(message2);

        assertOnlyOneMessageExists("message", modelInstance);
    }

    @Test
    public void testSubProcessWithCamundaInputOutput() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess("subProcess")
                .camundaInputParameter("foo", "bar")
                .camundaInputParameter("yoo", "hoo")
                .camundaOutputParameter("one", "two")
                .camundaOutputParameter("three", "four")
                .embeddedSubProcess()
                .startEvent()
                .endEvent()
                .subProcessDone()
                .endEvent()
                .done();

        SubProcess subProcess = modelInstance.getModelElementById("subProcess");
        assertCamundaInputOutputParameter(subProcess);
    }

    @Test
    public void testSubProcessWithCamundaInputOutputWithExistingExtensionElements() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess("subProcess")
                .camundaExecutionListenerExpression("end", "${true}")
                .camundaInputParameter("foo", "bar")
                .camundaInputParameter("yoo", "hoo")
                .camundaOutputParameter("one", "two")
                .camundaOutputParameter("three", "four")
                .embeddedSubProcess()
                .startEvent()
                .endEvent()
                .subProcessDone()
                .endEvent()
                .done();

        SubProcess subProcess = modelInstance.getModelElementById("subProcess");
        assertCamundaInputOutputParameter(subProcess);
    }

    @Test
    public void testSubProcessWithCamundaInputOutputWithExistingCamundaInputOutput() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .subProcess("subProcess")
                .camundaInputParameter("foo", "bar")
                .camundaOutputParameter("one", "two")
                .embeddedSubProcess()
                .startEvent()
                .endEvent()
                .subProcessDone()
                .endEvent()
                .done();

        SubProcess subProcess = modelInstance.getModelElementById("subProcess");

        subProcess.builder()
                .camundaInputParameter("yoo", "hoo")
                .camundaOutputParameter("three", "four");

        assertCamundaInputOutputParameter(subProcess);
    }

    @Test
    public void testErrorEndEventWithExistingError() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .userTask("task")
                .endEvent("end").error("myErrorCode", "errorMessage")
                .moveToActivity("task")
                .boundaryEvent("boundary").error("myErrorCode")
                .endEvent("boundaryEnd")
                .done();

        Error boundaryError = assertErrorEventDefinition("boundary", "myErrorCode", "errorMessage", modelInstance);
        Error endError = assertErrorEventDefinition("end", "myErrorCode", "errorMessage", modelInstance);

        assertThat(boundaryError).isEqualTo(endError);

        assertOnlyOneErrorExists("myErrorCode", modelInstance);
    }

    @Test
    public void testInterruptingStartEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent()
                .subProcess()
                .triggerByEvent()
                .embeddedSubProcess()
                .startEvent("subProcessStart")
                .interrupting(true)
                .error()
                .endEvent()
                .done();

        StartEvent startEvent = modelInstance.getModelElementById("subProcessStart");
        assertThat(startEvent).isNotNull();
        assertThat(startEvent.isInterrupting()).isTrue();
    }

    @Test
    public void testNonInterruptingStartEvent() {
        modelInstance = Bpmn.createProcess()
                .startEvent()
                .endEvent()
                .subProcess()
                .triggerByEvent()
                .embeddedSubProcess()
                .startEvent("subProcessStart")
                .interrupting(false)
                .error()
                .endEvent()
                .done();

        StartEvent startEvent = modelInstance.getModelElementById("subProcessStart");
        assertThat(startEvent).isNotNull();
        assertThat(startEvent.isInterrupting()).isFalse();
    }

    @Test
    public void testStartEventCamundaFormField() {
        modelInstance = Bpmn.createProcess()
                .startEvent(START_EVENT_ID)
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

        StartEvent startEvent = modelInstance.getModelElementById(START_EVENT_ID);
        assertCamundaFormField(startEvent);
    }

    @Test
    public void testSetIdAsDefaultNameForFlowElements() {
        BpmnModelInstance instance = Bpmn.createExecutableProcess("process")
                .startEvent("start")
                .userTask("user")
                .endEvent("end")
                .name("name")
                .done();

        String startName = ((FlowElement) instance.getModelElementById("start")).getName();
        assertEquals("start", startName);
        String userName = ((FlowElement) instance.getModelElementById("user")).getName();
        assertEquals("user", userName);
        String endName = ((FlowElement) instance.getModelElementById("end")).getName();
        assertEquals("name", endName);
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


}
