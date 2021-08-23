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

/**
 * @author Sebastian Menski
 */
public abstract class AbstractFlowNodeBuilder<B extends AbstractFlowNodeBuilder<B, E>, E extends FlowNode> extends ParadigmAbstractFlowNodeBuilder<B, E> {


  protected AbstractFlowNodeBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
    super(modelInstance, element, selfType);
  }


  public AbstractFlowNodeBuilder compensationDone(){
    if (compensateBoundaryEvent instanceof BoundaryEvent) {
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
}
