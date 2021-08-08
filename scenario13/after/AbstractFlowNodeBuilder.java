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
// PARADIGM
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.BusinessRuleTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.CallActivity;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ReceiveTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ScriptTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.SendTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.ServiceTask;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EndEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;

// DOMAIN
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.CompensateEventDefinition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.ManualTask;
import org.camunda.bpm.model.bpmn.instance.domain.humaninteraction.UserTask;

import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.EventBasedGateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.subprocesses.SubProcess;
import org.camunda.bpm.model.bpmn.instance.paradigm.subprocesses.Transaction;

/**
 * @author Sebastian Menski
 */
public abstract class AbstractFlowNodeBuilder<B extends AbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends ParadigmAbstractFlowNodeBuilder<B, E> {

  public ServiceTaskBuilder serviceTask() {
    return createTargetBuilder(ServiceTask.class);
  }

  public ServiceTaskBuilder serviceTask(String id) {
    return createTargetBuilder(ServiceTask.class, id);
  }

  public SendTaskBuilder sendTask() {
    return createTargetBuilder(SendTask.class);
  }

  public SendTaskBuilder sendTask(String id) {
    return createTargetBuilder(SendTask.class, id);
  }

  public BusinessRuleTaskBuilder businessRuleTask() {
    return createTargetBuilder(BusinessRuleTask.class);
  }

  public BusinessRuleTaskBuilder businessRuleTask(String id) {
    return createTargetBuilder(BusinessRuleTask.class, id);
  }

  public ScriptTaskBuilder scriptTask() {
    return createTargetBuilder(ScriptTask.class);
  }

  public ScriptTaskBuilder scriptTask(String id) {
    return createTargetBuilder(ScriptTask.class, id);
  }

  public ReceiveTaskBuilder receiveTask() {
    return createTargetBuilder(ReceiveTask.class);
  }

  public ReceiveTaskBuilder receiveTask(String id) {
    return createTargetBuilder(ReceiveTask.class, id);
  }

  public EndEventBuilder endEvent() {
    return createTarget(EndEvent.class).builder();
  }

  public EndEventBuilder endEvent(String id) {
    return createTarget(EndEvent.class, id).builder();
  }

  public ParallelGatewayBuilder parallelGateway() {
    return createTarget(ParallelGateway.class).builder();
  }

  public ParallelGatewayBuilder parallelGateway(String id) {
    return createTarget(ParallelGateway.class, id).builder();
  }

  public ExclusiveGatewayBuilder exclusiveGateway() {
    return createTarget(ExclusiveGateway.class).builder();
  }

  public InclusiveGatewayBuilder inclusiveGateway() {
    return createTarget(InclusiveGateway.class).builder();
  }

  public EventBasedGatewayBuilder eventBasedGateway() {
    return createTarget(EventBasedGateway.class).builder();
  }

  public ExclusiveGatewayBuilder exclusiveGateway(String id) {
    return createTarget(ExclusiveGateway.class, id).builder();
  }

  public InclusiveGatewayBuilder inclusiveGateway(String id) {
    return createTarget(InclusiveGateway.class, id).builder();
  }

  public CallActivityBuilder callActivity() {
    return createTarget(CallActivity.class).builder();
  }

  public CallActivityBuilder callActivity(String id) {
    return createTarget(CallActivity.class, id).builder();
  }

  public SubProcessBuilder subProcess() {
    return createTarget(SubProcess.class).builder();
  }

  public SubProcessBuilder subProcess(String id) {
    return createTarget(SubProcess.class, id).builder();
  }

  public TransactionBuilder transaction() {
    Transaction transaction = createTarget(Transaction.class);
    return new TransactionBuilder(modelInstance, transaction);
  }

  public TransactionBuilder transaction(String id) {
    Transaction transaction = createTarget(Transaction.class, id);
    return new TransactionBuilder(modelInstance, transaction);
  }

  protected AbstractFlowNodeBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
    super(modelInstance, element, selfType);
  }


  public AbstractFlowNodeBuilder compensationDone(){
    if (compensateBoundaryEvent != null && compensateBoundaryEvent instanceof BoundaryEvent) {
      return ((BoundaryEvent)compensateBoundaryEvent).getAttachedTo().builder();
    }
    else {
      throw new BpmnModelException("No compensation in progress. Call compensationStart() first.");
    }
  }

  public UserTaskBuilder userTask() {
    return createTargetBuilder(UserTask.class);
  }

  public UserTaskBuilder userTask(String id) {
    return createTargetBuilder(UserTask.class, id);
  }

  public B compensationStart() {
    if (element instanceof BoundaryEvent) {
      BoundaryEvent boundaryEvent = (BoundaryEvent) element;
      for (EventDefinition eventDefinition : boundaryEvent.getEventDefinitions()) {
        if(eventDefinition instanceof CompensateEventDefinition) {
          // if the boundary event contains a compensate event definition then
          // save the boundary event to later return to it and start a compensation

          compensateBoundaryEvent = boundaryEvent;
          compensationStarted = true;

          return myself;
        }
      }
    }

    throw new BpmnModelException("Compensation can only be started on a boundary event with a compensation event definition");
  }

  public ManualTaskBuilder manualTask() {
    return createTargetBuilder(ManualTask.class);
  }

  public ManualTaskBuilder manualTask(String id) {
    return createTargetBuilder(ManualTask.class, id);
  }

  public IntermediateCatchEventBuilder intermediateCatchEvent() {
    return createTarget(IntermediateCatchEvent.class).builder();
  }

  public IntermediateCatchEventBuilder intermediateCatchEvent(String id) {
    return createTarget(IntermediateCatchEvent.class, id).builder();
  }

  public IntermediateThrowEventBuilder intermediateThrowEvent() {
    return createTarget(IntermediateThrowEvent.class).builder();
  }

  public IntermediateThrowEventBuilder intermediateThrowEvent(String id) {
    return createTarget(IntermediateThrowEvent.class, id).builder();
  }


  protected <T extends FlowNode> T createTarget(Class<T> typeClass) {
    return createTarget(typeClass, null);
  }

  protected <T extends FlowNode> T createTarget(Class<T> typeClass, String identifier) {
    T target = createSibling(typeClass, identifier);

    BpmnShape targetBpmnShape = createBpmnShape(target);
    setCoordinates(targetBpmnShape);
    connectTarget(target);
    resizeSubProcess(targetBpmnShape);
    return target;
  }

  protected <T extends AbstractFlowNodeBuilder, F extends FlowNode> T createTargetBuilder(Class<F> typeClass) {
    return createTargetBuilder(typeClass, null);
  }

  @SuppressWarnings("unchecked")
  protected <T extends AbstractFlowNodeBuilder, F extends FlowNode> T createTargetBuilder(Class<F> typeClass, String id) {
    AbstractFlowNodeBuilder builder = createTarget(typeClass, id).builder();

    if (compensationStarted) {
      // pass on current boundary event to return after compensationDone call
      builder.compensateBoundaryEvent = compensateBoundaryEvent;
    }

    return (T) builder;

  }
}
