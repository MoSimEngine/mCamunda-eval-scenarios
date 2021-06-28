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

import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ActivityImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.BusinessRuleTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.CallActivityImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ReceiveTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ScriptTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.SendTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ServiceTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.TaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.artifacts.ArtifactImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.artifacts.AssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.artifacts.TextAnnotationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.BaseElementImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.DefinitionsImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.DocumentationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.IoBindingImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.IoSpecificationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.PartitionElement;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.RootElementImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.correlations.CorrelationKeyImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.correlations.CorrelationPropertyBindingImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.correlations.CorrelationPropertyImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.correlations.CorrelationPropertyRetrievalExpressionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.correlations.CorrelationSubscriptionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.AssignmentImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataAssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataInputAssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataInputImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataObjectImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataObjectReferenceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataOutputAssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataOutputImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataStateImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataStoreImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.DataStoreReferenceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.InputDataItemImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.InputSetImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.ItemAwareElementImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.ItemDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.OutputDataItemImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.OutputSetImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.data.PropertyImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.CatchEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.EndEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.EventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.EventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.StartEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.ThrowEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.externals.ExtensionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.externals.ImportImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.externals.RelationshipImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.flows.FlowElementImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.flows.FlowNodeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.flows.SequenceFlowImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.gateways.ComplexGatewayImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.gateways.EventBasedGatewayImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.gateways.ExclusiveGatewayImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.gateways.GatewayImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.gateways.InclusiveGatewayImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.gateways.ParallelGatewayImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.group.CategoryImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.group.CategoryValueImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.group.GroupImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.looping.ComplexBehaviorDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.looping.LoopCharacteristicsImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.looping.MultiInstanceLoopCharacteristicsImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.messaging.InteractionNodeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.messaging.MessageFlowAssociationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.messaging.MessageFlowImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.messaging.MessageImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.PerformerImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.ResourceAssignmentExpressionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.ResourceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.ResourceParameterBindingImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.ResourceParameterImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.ResourceRef;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.resources.ResourceRoleImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.services.CallableElementImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.services.EndPointImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.services.ErrorImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.services.InterfaceImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.services.OperationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.services.OperationRef;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.subprocesses.SubProcessImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.subprocesses.TransactionImpl;

import org.camunda.bpm.model.xml.ModelBuilder;


/**
 * <p>Provides access to the camunda BPMN model api.</p>
 *
 * @author Daniel Meyer
 *
 */
public class BpmnParadigm {

  public static void registerTypes(ModelBuilder bpmnModelBuilder) {
    ActivityImpl.registerType(bpmnModelBuilder);
    ArtifactImpl.registerType(bpmnModelBuilder);
    AssignmentImpl.registerType(bpmnModelBuilder);
    AssociationImpl.registerType(bpmnModelBuilder);
    BaseElementImpl.registerType(bpmnModelBuilder);
    BusinessRuleTaskImpl.registerType(bpmnModelBuilder);
    CallableElementImpl.registerType(bpmnModelBuilder);
    CallActivityImpl.registerType(bpmnModelBuilder);
    CatchEventImpl.registerType(bpmnModelBuilder);
    CategoryImpl.registerType(bpmnModelBuilder);
    CategoryValueImpl.registerType(bpmnModelBuilder);
    ComplexBehaviorDefinitionImpl.registerType(bpmnModelBuilder);
    ComplexGatewayImpl.registerType(bpmnModelBuilder);
    CorrelationKeyImpl.registerType(bpmnModelBuilder);
    CorrelationPropertyBindingImpl.registerType(bpmnModelBuilder);
    CorrelationPropertyImpl.registerType(bpmnModelBuilder);
    CorrelationPropertyRetrievalExpressionImpl.registerType(bpmnModelBuilder);
    CorrelationSubscriptionImpl.registerType(bpmnModelBuilder);
    DataAssociationImpl.registerType(bpmnModelBuilder);
    DataInputAssociationImpl.registerType(bpmnModelBuilder);
    DataInputImpl.registerType(bpmnModelBuilder);
    DataOutputAssociationImpl.registerType(bpmnModelBuilder);
    DataOutputImpl.registerType(bpmnModelBuilder);
    DataStateImpl.registerType(bpmnModelBuilder);
    DataObjectImpl.registerType(bpmnModelBuilder);
    DataObjectReferenceImpl.registerType(bpmnModelBuilder);
    DataStoreImpl.registerType(bpmnModelBuilder);
    DataStoreReferenceImpl.registerType(bpmnModelBuilder);
    DefinitionsImpl.registerType(bpmnModelBuilder);
    DocumentationImpl.registerType(bpmnModelBuilder);
    EndEventImpl.registerType(bpmnModelBuilder);
    EndPointImpl.registerType(bpmnModelBuilder);
    ErrorImpl.registerType(bpmnModelBuilder);
    EventBasedGatewayImpl.registerType(bpmnModelBuilder);
    EventDefinitionImpl.registerType(bpmnModelBuilder);
    EventImpl.registerType(bpmnModelBuilder);
    ExclusiveGatewayImpl.registerType(bpmnModelBuilder);
    ExtensionImpl.registerType(bpmnModelBuilder);
    FlowElementImpl.registerType(bpmnModelBuilder);
    FlowNodeImpl.registerType(bpmnModelBuilder);
    GatewayImpl.registerType(bpmnModelBuilder);
    GroupImpl.registerType(bpmnModelBuilder);
    ImportImpl.registerType(bpmnModelBuilder);
    InclusiveGatewayImpl.registerType(bpmnModelBuilder);
    InputDataItemImpl.registerType(bpmnModelBuilder);
    InputSetImpl.registerType(bpmnModelBuilder);
    InteractionNodeImpl.registerType(bpmnModelBuilder);
    InterfaceImpl.registerType(bpmnModelBuilder);
    IoBindingImpl.registerType(bpmnModelBuilder);
    IoSpecificationImpl.registerType(bpmnModelBuilder);
    ItemAwareElementImpl.registerType(bpmnModelBuilder);
    ItemDefinitionImpl.registerType(bpmnModelBuilder);
    LoopCharacteristicsImpl.registerType(bpmnModelBuilder);
    MessageFlowAssociationImpl.registerType(bpmnModelBuilder);
    MessageFlowImpl.registerType(bpmnModelBuilder);
    MessageImpl.registerType(bpmnModelBuilder);
    MultiInstanceLoopCharacteristicsImpl.registerType(bpmnModelBuilder);
    OperationImpl.registerType(bpmnModelBuilder);
    OperationRef.registerType(bpmnModelBuilder);
    OutputDataItemImpl.registerType(bpmnModelBuilder);
    OutputSetImpl.registerType(bpmnModelBuilder);
    ParallelGatewayImpl.registerType(bpmnModelBuilder);
    PartitionElement.registerType(bpmnModelBuilder);
    PerformerImpl.registerType(bpmnModelBuilder);
    PropertyImpl.registerType(bpmnModelBuilder);
    ReceiveTaskImpl.registerType(bpmnModelBuilder);
    RelationshipImpl.registerType(bpmnModelBuilder);
    ResourceAssignmentExpressionImpl.registerType(bpmnModelBuilder);
    ResourceImpl.registerType(bpmnModelBuilder);
    ResourceParameterBindingImpl.registerType(bpmnModelBuilder);
    ResourceParameterImpl.registerType(bpmnModelBuilder);
    ResourceRef.registerType(bpmnModelBuilder);
    ResourceRoleImpl.registerType(bpmnModelBuilder);
    RootElementImpl.registerType(bpmnModelBuilder);
    ScriptTaskImpl.registerType(bpmnModelBuilder);
    SendTaskImpl.registerType(bpmnModelBuilder);
    SequenceFlowImpl.registerType(bpmnModelBuilder);
    ServiceTaskImpl.registerType(bpmnModelBuilder);
    StartEventImpl.registerType(bpmnModelBuilder);
    SubProcessImpl.registerType(bpmnModelBuilder);
    TaskImpl.registerType(bpmnModelBuilder);
    TextAnnotationImpl.registerType(bpmnModelBuilder);
    ThrowEventImpl.registerType(bpmnModelBuilder);
    TransactionImpl.registerType(bpmnModelBuilder);
  }
}
