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
package org.camunda.bpm.model.bpmn;

import org.camunda.bpm.model.bpmn.impl.instance.domain.auditingandmonitoring.AuditingImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.auditingandmonitoring.MonitoringImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.collaboration.CollaborationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.collaboration.ParticipantAssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.collaboration.ParticipantImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.collaboration.ParticipantMultiplicityImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.CallConversationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.ConversationAssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.ConversationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.ConversationLinkImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.ConversationNodeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.GlobalConversationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.SubConversationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.BoundaryEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.CancelEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.CompensateEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.ConditionalEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.ErrorEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.EscalationEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.EscalationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.IntermediateCatchEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.IntermediateThrowEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.LinkEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.MessageEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.SignalEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.SignalImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.TerminateEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.TimerEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.ActivationConditionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.CompletionConditionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.ConditionExpressionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.ConditionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.DataPath;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.ExpressionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.FlowNodeRef;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.FormalExpressionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.From;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.LoopCardinalityImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.MessagePath;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.TimeCycleImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.TimeDateImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.TimeDurationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.To;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.Transformation;
import org.camunda.bpm.model.bpmn.impl.instance.domain.humaninteraction.ManualTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.humaninteraction.RenderingImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.humaninteraction.UserTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.ChildLaneSet;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.LaneImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.LaneSetImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.ProcessImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.resources.human.HumanPerformerImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.resources.human.PotentialOwnerImpl;
import org.camunda.bpm.model.xml.ModelBuilder;

/**
 * <p>Provides access to the camunda BPMN model api.</p>
 *
 * @author Daniel Meyer
 *
 */
public class BpmnDomain {

  public static void registerTypes(ModelBuilder bpmnModelBuilder) {
    ActivationConditionImpl.registerType(bpmnModelBuilder);
    AuditingImpl.registerType(bpmnModelBuilder);
    BoundaryEventImpl.registerType(bpmnModelBuilder);
    CallConversationImpl.registerType(bpmnModelBuilder);
    CancelEventDefinitionImpl.registerType(bpmnModelBuilder);
    ChildLaneSet.registerType(bpmnModelBuilder);
    CollaborationImpl.registerType(bpmnModelBuilder);
    CompensateEventDefinitionImpl.registerType(bpmnModelBuilder);
    ConditionImpl.registerType(bpmnModelBuilder);
    ConditionalEventDefinitionImpl.registerType(bpmnModelBuilder);
    CompletionConditionImpl.registerType(bpmnModelBuilder);
    ConditionExpressionImpl.registerType(bpmnModelBuilder);
    ConversationAssociationImpl.registerType(bpmnModelBuilder);
    ConversationImpl.registerType(bpmnModelBuilder);
    ConversationLinkImpl.registerType(bpmnModelBuilder);
    ConversationNodeImpl.registerType(bpmnModelBuilder);
    DataPath.registerType(bpmnModelBuilder);
    ErrorEventDefinitionImpl.registerType(bpmnModelBuilder);
    EscalationImpl.registerType(bpmnModelBuilder);
    EscalationEventDefinitionImpl.registerType(bpmnModelBuilder);
    ExpressionImpl.registerType(bpmnModelBuilder);
    FlowNodeRef.registerType(bpmnModelBuilder);
    FormalExpressionImpl.registerType(bpmnModelBuilder);
    From.registerType(bpmnModelBuilder);
    GlobalConversationImpl.registerType(bpmnModelBuilder);
    HumanPerformerImpl.registerType(bpmnModelBuilder);
    IntermediateCatchEventImpl.registerType(bpmnModelBuilder);
    IntermediateThrowEventImpl.registerType(bpmnModelBuilder);
    LaneImpl.registerType(bpmnModelBuilder);
    LaneSetImpl.registerType(bpmnModelBuilder);
    LinkEventDefinitionImpl.registerType(bpmnModelBuilder);
    LoopCardinalityImpl.registerType(bpmnModelBuilder);
    ManualTaskImpl.registerType(bpmnModelBuilder);
    MessageEventDefinitionImpl.registerType(bpmnModelBuilder);
    MessagePath.registerType(bpmnModelBuilder);
    MonitoringImpl.registerType(bpmnModelBuilder);
    ParticipantAssociationImpl.registerType(bpmnModelBuilder);
    ParticipantImpl.registerType(bpmnModelBuilder);
    ParticipantMultiplicityImpl.registerType(bpmnModelBuilder);
    PotentialOwnerImpl.registerType(bpmnModelBuilder);
    ProcessImpl.registerType(bpmnModelBuilder);
    RenderingImpl.registerType(bpmnModelBuilder);
    SignalEventDefinitionImpl.registerType(bpmnModelBuilder);
    SignalImpl.registerType(bpmnModelBuilder);
    SubConversationImpl.registerType(bpmnModelBuilder);
    TerminateEventDefinitionImpl.registerType(bpmnModelBuilder);
    TimeCycleImpl.registerType(bpmnModelBuilder);
    TimeDateImpl.registerType(bpmnModelBuilder);
    TimeDurationImpl.registerType(bpmnModelBuilder);
    TimerEventDefinitionImpl.registerType(bpmnModelBuilder);
    To.registerType(bpmnModelBuilder);
    Transformation.registerType(bpmnModelBuilder);
    UserTaskImpl.registerType(bpmnModelBuilder);
  }
}
