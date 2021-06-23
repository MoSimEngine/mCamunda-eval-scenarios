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

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.ACTIVITI_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.ScriptImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.Source;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.SourceRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.SupportedInterfaceRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.Supports;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.Target;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.TargetRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.TextImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.WhileExecutingInputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.WhileExecutingOutputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.domain.conversations.SubConversationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.SignalEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.SignalImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.TerminateEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced.TimerEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.TimeCycleImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.TimeDateImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.TimeDurationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.To;
import org.camunda.bpm.model.bpmn.impl.instance.domain.expressions.Transformation;
import org.camunda.bpm.model.bpmn.impl.instance.domain.humaninteraction.RenderingImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.humaninteraction.UserTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.ProcessImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnDiagramImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnEdgeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnLabelImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnLabelStyleImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnPlaneImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmndi.BpmnShapeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.CategoryValueRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.CorrelationPropertyRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.DataInputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.DataOutputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.EndPointRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.ErrorRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.EventDefinitionRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.ExtensionElementsImpl;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.InMessageRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.Incoming;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.InnerParticipantRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.InputSetRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.InterfaceRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.LoopDataInputRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.LoopDataOutputRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.MessageFlowRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.OptionalInputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.OptionalOutputRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.OutMessageRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.OuterParticipantRef;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.Outgoing;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.OutputSetRefs;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.ParticipantRef;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaConnectorIdImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaConnectorImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaConstraintImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaEntryImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaErrorEventDefinitionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaExecutionListenerImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaExpressionImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaFailedJobRetryTimeCycleImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaFieldImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaFormDataImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaFormFieldImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaFormPropertyImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaInImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaInputOutputImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaInputParameterImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaListImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaMapImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaOutImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaOutputParameterImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaPotentialStarterImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaPropertiesImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaPropertyImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaScriptImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaStringImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaTaskListenerImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaValidationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.camunda.CamundaValueImpl;
import org.camunda.bpm.model.bpmn.impl.instance.dc.BoundsImpl;
import org.camunda.bpm.model.bpmn.impl.instance.dc.FontImpl;
import org.camunda.bpm.model.bpmn.impl.instance.dc.PointImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.DiagramElementImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.DiagramImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.EdgeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.LabelImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.LabeledEdgeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.LabeledShapeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.NodeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.PlaneImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.ShapeImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.StyleImpl;
import org.camunda.bpm.model.bpmn.impl.instance.di.WaypointImpl;
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
import org.camunda.bpm.model.bpmn.impl.instance.domain.humaninteraction.ManualTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.ChildLaneSet;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.LaneImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.processes.LaneSetImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.resources.human.HumanPerformerImpl;
import org.camunda.bpm.model.bpmn.impl.instance.domain.resources.human.PotentialOwnerImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ActivityImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.BusinessRuleTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.CallActivityImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ReceiveTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ScriptTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.SendTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.ServiceTaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.activities.TaskImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.artifacts.TextAnnotationImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.ThrowEventImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.subprocesses.SubProcessImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.artifacts.ArtifactImpl;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.artifacts.AssociationImpl;
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
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.subprocesses.TransactionImpl;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.Definitions;
import org.camunda.bpm.model.bpmn.instance.domain.processes.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.ModelException;
import org.camunda.bpm.model.xml.ModelParseException;
import org.camunda.bpm.model.xml.ModelValidationException;
import org.camunda.bpm.model.xml.impl.instance.ModelElementInstanceImpl;
import org.camunda.bpm.model.xml.impl.util.IoUtil;

/**
 * <p>Provides access to the camunda BPMN model api.</p>
 *
 * @author Daniel Meyer
 *
 */
public class Bpmn {

  /** the singleton instance of {@link Bpmn}. If you want to customize the behavior of Bpmn,
   * replace this instance with an instance of a custom subclass of {@link Bpmn}. */
  public static Bpmn INSTANCE = new Bpmn();

  /** the parser used by the Bpmn implementation. */
  private BpmnParser bpmnParser = new BpmnParser();
  private final ModelBuilder bpmnModelBuilder;

  /** The {@link Model}
   */
  private Model bpmnModel;

  /**
   * Allows reading a {@link BpmnModelInstance} from a File.
   *
   * @param file the {@link File} to read the {@link BpmnModelInstance} from
   * @return the model read
   * @throws BpmnModelException if the model cannot be read
   */
  public static BpmnModelInstance readModelFromFile(File file) {
    return INSTANCE.doReadModelFromFile(file);
  }

  /**
   * Allows reading a {@link BpmnModelInstance} from an {@link InputStream}
   *
   * @param stream the {@link InputStream} to read the {@link BpmnModelInstance} from
   * @return the model read
   * @throws ModelParseException if the model cannot be read
   */
  public static BpmnModelInstance readModelFromStream(InputStream stream) {
    return INSTANCE.doReadModelFromInputStream(stream);
  }

  /**
   * Allows writing a {@link BpmnModelInstance} to a File. It will be
   * validated before writing.
   *
   * @param file the {@link File} to write the {@link BpmnModelInstance} to
   * @param modelInstance the {@link BpmnModelInstance} to write
   * @throws BpmnModelException if the model cannot be written
   * @throws ModelValidationException if the model is not valid
   */
  public static void writeModelToFile(File file, BpmnModelInstance modelInstance) {
    INSTANCE.doWriteModelToFile(file, modelInstance);
  }

  /**
   * Allows writing a {@link BpmnModelInstance} to an {@link OutputStream}. It will be
   * validated before writing.
   *
   * @param stream the {@link OutputStream} to write the {@link BpmnModelInstance} to
   * @param modelInstance the {@link BpmnModelInstance} to write
   * @throws ModelException if the model cannot be written
   * @throws ModelValidationException if the model is not valid
   */
  public static void writeModelToStream(OutputStream stream, BpmnModelInstance modelInstance) {
    INSTANCE.doWriteModelToOutputStream(stream, modelInstance);
  }

  /**
   * Allows the conversion of a {@link BpmnModelInstance} to an {@link String}. It will
   * be validated before conversion.
   *
   * @param modelInstance  the model instance to convert
   * @return the XML string representation of the model instance
   */
  public static String convertToString(BpmnModelInstance modelInstance) {
    return INSTANCE.doConvertToString(modelInstance);
  }

  /**
   * Validate model DOM document
   *
   * @param modelInstance the {@link BpmnModelInstance} to validate
   * @throws ModelValidationException if the model is not valid
   */
  public static void validateModel(BpmnModelInstance modelInstance) {
    INSTANCE.doValidateModel(modelInstance);
  }

  /**
   * Allows creating an new, empty {@link BpmnModelInstance}.
   *
   * @return the empty model.
   */
  public static BpmnModelInstance createEmptyModel() {
    return INSTANCE.doCreateEmptyModel();
  }

  public static ProcessBuilder createProcess() {
    BpmnModelInstance modelInstance = INSTANCE.doCreateEmptyModel();
    Definitions definitions = modelInstance.newInstance(Definitions.class);
    definitions.setTargetNamespace(BPMN20_NS);
    definitions.getDomElement().registerNamespace("camunda", CAMUNDA_NS);
    modelInstance.setDefinitions(definitions);
    Process process = modelInstance.newInstance(Process.class);
    definitions.addChildElement(process);

    BpmnDiagram bpmnDiagram = modelInstance.newInstance(BpmnDiagram.class);

    BpmnPlane bpmnPlane = modelInstance.newInstance(BpmnPlane.class);
    bpmnPlane.setBpmnElement(process);

    bpmnDiagram.addChildElement(bpmnPlane);
    definitions.addChildElement(bpmnDiagram);

    return process.builder();
  }

  public static ProcessBuilder createProcess(String processId) {
    return createProcess().id(processId);
  }

  public static ProcessBuilder createExecutableProcess() {
    return createProcess().executable();
  }

  public static ProcessBuilder createExecutableProcess(String processId) {
    return createProcess(processId).executable();
  }


  /**
   * Register known types of the BPMN model
   */
  protected Bpmn() {
    bpmnModelBuilder = ModelBuilder.createInstance("BPMN Model");
    bpmnModelBuilder.alternativeNamespace(ACTIVITI_NS, CAMUNDA_NS);
    doRegisterTypes(bpmnModelBuilder);
    bpmnModel = bpmnModelBuilder.build();
  }

  protected BpmnModelInstance doReadModelFromFile(File file) {
    InputStream is = null;
    try {
      is = new FileInputStream(file);
      return doReadModelFromInputStream(is);

    } catch (FileNotFoundException e) {
      throw new BpmnModelException("Cannot read model from file "+file+": file does not exist.");

    } finally {
      IoUtil.closeSilently(is);

    }
  }

  protected BpmnModelInstance doReadModelFromInputStream(InputStream is) {
    return bpmnParser.parseModelFromStream(is);
  }

  protected void doWriteModelToFile(File file, BpmnModelInstance modelInstance) {
    OutputStream os = null;
    try {
      os = new FileOutputStream(file);
      doWriteModelToOutputStream(os, modelInstance);
    }
    catch (FileNotFoundException e) {
      throw new BpmnModelException("Cannot write model to file "+file+": file does not exist.");
    } finally {
      IoUtil.closeSilently(os);
    }
  }

  protected void doWriteModelToOutputStream(OutputStream os, BpmnModelInstance modelInstance) {
    // validate DOM document
    doValidateModel(modelInstance);
    // write XML
    IoUtil.writeDocumentToOutputStream(modelInstance.getDocument(), os);
  }

  protected String doConvertToString(BpmnModelInstance modelInstance) {
    // validate DOM document
    doValidateModel(modelInstance);
    // convert to XML string
    return IoUtil.convertXmlDocumentToString(modelInstance.getDocument());
  }

  protected void doValidateModel(BpmnModelInstance modelInstance) {
    bpmnParser.validateModel(modelInstance.getDocument());
  }

  protected BpmnModelInstance doCreateEmptyModel() {
    return bpmnParser.getEmptyModel();
  }

  protected void doRegisterTypes(ModelBuilder bpmnModelBuilder) {
    ActivationConditionImpl.registerType(bpmnModelBuilder);
    ActivityImpl.registerType(bpmnModelBuilder);
    ArtifactImpl.registerType(bpmnModelBuilder);
    AssignmentImpl.registerType(bpmnModelBuilder);
    AssociationImpl.registerType(bpmnModelBuilder);
    AuditingImpl.registerType(bpmnModelBuilder);
    BaseElementImpl.registerType(bpmnModelBuilder);
    BoundaryEventImpl.registerType(bpmnModelBuilder);
    BusinessRuleTaskImpl.registerType(bpmnModelBuilder);
    CallableElementImpl.registerType(bpmnModelBuilder);
    CallActivityImpl.registerType(bpmnModelBuilder);
    CallConversationImpl.registerType(bpmnModelBuilder);
    CancelEventDefinitionImpl.registerType(bpmnModelBuilder);
    CatchEventImpl.registerType(bpmnModelBuilder);
    CategoryImpl.registerType(bpmnModelBuilder);
    CategoryValueImpl.registerType(bpmnModelBuilder);
    CategoryValueRef.registerType(bpmnModelBuilder);
    ChildLaneSet.registerType(bpmnModelBuilder);
    CollaborationImpl.registerType(bpmnModelBuilder);
    CompensateEventDefinitionImpl.registerType(bpmnModelBuilder);
    ConditionImpl.registerType(bpmnModelBuilder);
    ConditionalEventDefinitionImpl.registerType(bpmnModelBuilder);
    CompletionConditionImpl.registerType(bpmnModelBuilder);
    ComplexBehaviorDefinitionImpl.registerType(bpmnModelBuilder);
    ComplexGatewayImpl.registerType(bpmnModelBuilder);
    ConditionExpressionImpl.registerType(bpmnModelBuilder);
    ConversationAssociationImpl.registerType(bpmnModelBuilder);
    ConversationImpl.registerType(bpmnModelBuilder);
    ConversationLinkImpl.registerType(bpmnModelBuilder);
    ConversationNodeImpl.registerType(bpmnModelBuilder);
    CorrelationKeyImpl.registerType(bpmnModelBuilder);
    CorrelationPropertyBindingImpl.registerType(bpmnModelBuilder);
    CorrelationPropertyImpl.registerType(bpmnModelBuilder);
    CorrelationPropertyRef.registerType(bpmnModelBuilder);
    CorrelationPropertyRetrievalExpressionImpl.registerType(bpmnModelBuilder);
    CorrelationSubscriptionImpl.registerType(bpmnModelBuilder);
    DataAssociationImpl.registerType(bpmnModelBuilder);
    DataInputAssociationImpl.registerType(bpmnModelBuilder);
    DataInputImpl.registerType(bpmnModelBuilder);
    DataInputRefs.registerType(bpmnModelBuilder);
    DataOutputAssociationImpl.registerType(bpmnModelBuilder);
    DataOutputImpl.registerType(bpmnModelBuilder);
    DataOutputRefs.registerType(bpmnModelBuilder);
    DataPath.registerType(bpmnModelBuilder);
    DataStateImpl.registerType(bpmnModelBuilder);
    DataObjectImpl.registerType(bpmnModelBuilder);
    DataObjectReferenceImpl.registerType(bpmnModelBuilder);
    DataStoreImpl.registerType(bpmnModelBuilder);
    DataStoreReferenceImpl.registerType(bpmnModelBuilder);
    DefinitionsImpl.registerType(bpmnModelBuilder);
    DocumentationImpl.registerType(bpmnModelBuilder);
    EndEventImpl.registerType(bpmnModelBuilder);
    EndPointImpl.registerType(bpmnModelBuilder);
    EndPointRef.registerType(bpmnModelBuilder);
    ErrorEventDefinitionImpl.registerType(bpmnModelBuilder);
    ErrorImpl.registerType(bpmnModelBuilder);
    ErrorRef.registerType(bpmnModelBuilder);
    EscalationImpl.registerType(bpmnModelBuilder);
    EscalationEventDefinitionImpl.registerType(bpmnModelBuilder);
    EventBasedGatewayImpl.registerType(bpmnModelBuilder);
    EventDefinitionImpl.registerType(bpmnModelBuilder);
    EventDefinitionRef.registerType(bpmnModelBuilder);
    EventImpl.registerType(bpmnModelBuilder);
    ExclusiveGatewayImpl.registerType(bpmnModelBuilder);
    ExpressionImpl.registerType(bpmnModelBuilder);
    ExtensionElementsImpl.registerType(bpmnModelBuilder);
    ExtensionImpl.registerType(bpmnModelBuilder);
    FlowElementImpl.registerType(bpmnModelBuilder);
    FlowNodeImpl.registerType(bpmnModelBuilder);
    FlowNodeRef.registerType(bpmnModelBuilder);
    FormalExpressionImpl.registerType(bpmnModelBuilder);
    From.registerType(bpmnModelBuilder);
    GatewayImpl.registerType(bpmnModelBuilder);
    GlobalConversationImpl.registerType(bpmnModelBuilder);
    GroupImpl.registerType(bpmnModelBuilder);
    HumanPerformerImpl.registerType(bpmnModelBuilder);
    ImportImpl.registerType(bpmnModelBuilder);
    InclusiveGatewayImpl.registerType(bpmnModelBuilder);
    Incoming.registerType(bpmnModelBuilder);
    InMessageRef.registerType(bpmnModelBuilder);
    InnerParticipantRef.registerType(bpmnModelBuilder);
    InputDataItemImpl.registerType(bpmnModelBuilder);
    InputSetImpl.registerType(bpmnModelBuilder);
    InputSetRefs.registerType(bpmnModelBuilder);
    InteractionNodeImpl.registerType(bpmnModelBuilder);
    InterfaceImpl.registerType(bpmnModelBuilder);
    InterfaceRef.registerType(bpmnModelBuilder);
    IntermediateCatchEventImpl.registerType(bpmnModelBuilder);
    IntermediateThrowEventImpl.registerType(bpmnModelBuilder);
    IoBindingImpl.registerType(bpmnModelBuilder);
    IoSpecificationImpl.registerType(bpmnModelBuilder);
    ItemAwareElementImpl.registerType(bpmnModelBuilder);
    ItemDefinitionImpl.registerType(bpmnModelBuilder);
    LaneImpl.registerType(bpmnModelBuilder);
    LaneSetImpl.registerType(bpmnModelBuilder);
    LinkEventDefinitionImpl.registerType(bpmnModelBuilder);
    LoopCardinalityImpl.registerType(bpmnModelBuilder);
    LoopCharacteristicsImpl.registerType(bpmnModelBuilder);
    LoopDataInputRef.registerType(bpmnModelBuilder);
    LoopDataOutputRef.registerType(bpmnModelBuilder);
    ManualTaskImpl.registerType(bpmnModelBuilder);
    MessageEventDefinitionImpl.registerType(bpmnModelBuilder);
    MessageFlowAssociationImpl.registerType(bpmnModelBuilder);
    MessageFlowImpl.registerType(bpmnModelBuilder);
    MessageFlowRef.registerType(bpmnModelBuilder);
    MessageImpl.registerType(bpmnModelBuilder);
    MessagePath.registerType(bpmnModelBuilder);
    ModelElementInstanceImpl.registerType(bpmnModelBuilder);
    MonitoringImpl.registerType(bpmnModelBuilder);
    MultiInstanceLoopCharacteristicsImpl.registerType(bpmnModelBuilder);
    OperationImpl.registerType(bpmnModelBuilder);
    OperationRef.registerType(bpmnModelBuilder);
    OptionalInputRefs.registerType(bpmnModelBuilder);
    OptionalOutputRefs.registerType(bpmnModelBuilder);
    OuterParticipantRef.registerType(bpmnModelBuilder);
    OutMessageRef.registerType(bpmnModelBuilder);
    Outgoing.registerType(bpmnModelBuilder);
    OutputDataItemImpl.registerType(bpmnModelBuilder);
    OutputSetImpl.registerType(bpmnModelBuilder);
    OutputSetRefs.registerType(bpmnModelBuilder);
    ParallelGatewayImpl.registerType(bpmnModelBuilder);
    ParticipantAssociationImpl.registerType(bpmnModelBuilder);
    ParticipantImpl.registerType(bpmnModelBuilder);
    ParticipantMultiplicityImpl.registerType(bpmnModelBuilder);
    ParticipantRef.registerType(bpmnModelBuilder);
    PartitionElement.registerType(bpmnModelBuilder);
    PerformerImpl.registerType(bpmnModelBuilder);
    PotentialOwnerImpl.registerType(bpmnModelBuilder);
    ProcessImpl.registerType(bpmnModelBuilder);
    PropertyImpl.registerType(bpmnModelBuilder);
    ReceiveTaskImpl.registerType(bpmnModelBuilder);
    RelationshipImpl.registerType(bpmnModelBuilder);
    RenderingImpl.registerType(bpmnModelBuilder);
    ResourceAssignmentExpressionImpl.registerType(bpmnModelBuilder);
    ResourceImpl.registerType(bpmnModelBuilder);
    ResourceParameterBindingImpl.registerType(bpmnModelBuilder);
    ResourceParameterImpl.registerType(bpmnModelBuilder);
    ResourceRef.registerType(bpmnModelBuilder);
    ResourceRoleImpl.registerType(bpmnModelBuilder);
    RootElementImpl.registerType(bpmnModelBuilder);
    ScriptImpl.registerType(bpmnModelBuilder);
    ScriptTaskImpl.registerType(bpmnModelBuilder);
    SendTaskImpl.registerType(bpmnModelBuilder);
    SequenceFlowImpl.registerType(bpmnModelBuilder);
    ServiceTaskImpl.registerType(bpmnModelBuilder);
    SignalEventDefinitionImpl.registerType(bpmnModelBuilder);
    SignalImpl.registerType(bpmnModelBuilder);
    Source.registerType(bpmnModelBuilder);
    SourceRef.registerType(bpmnModelBuilder);
    StartEventImpl.registerType(bpmnModelBuilder);
    SubConversationImpl.registerType(bpmnModelBuilder);
    SubProcessImpl.registerType(bpmnModelBuilder);
    SupportedInterfaceRef.registerType(bpmnModelBuilder);
    Supports.registerType(bpmnModelBuilder);
    Target.registerType(bpmnModelBuilder);
    TargetRef.registerType(bpmnModelBuilder);
    TaskImpl.registerType(bpmnModelBuilder);
    TerminateEventDefinitionImpl.registerType(bpmnModelBuilder);
    TextImpl.registerType(bpmnModelBuilder);
    TextAnnotationImpl.registerType(bpmnModelBuilder);
    ThrowEventImpl.registerType(bpmnModelBuilder);
    TimeCycleImpl.registerType(bpmnModelBuilder);
    TimeDateImpl.registerType(bpmnModelBuilder);
    TimeDurationImpl.registerType(bpmnModelBuilder);
    TimerEventDefinitionImpl.registerType(bpmnModelBuilder);
    To.registerType(bpmnModelBuilder);
    TransactionImpl.registerType(bpmnModelBuilder);
    Transformation.registerType(bpmnModelBuilder);
    UserTaskImpl.registerType(bpmnModelBuilder);
    WhileExecutingInputRefs.registerType(bpmnModelBuilder);
    WhileExecutingOutputRefs.registerType(bpmnModelBuilder);

    /** DC */
    FontImpl.registerType(bpmnModelBuilder);
    PointImpl.registerType(bpmnModelBuilder);
    BoundsImpl.registerType(bpmnModelBuilder);

    /** DI */
    DiagramImpl.registerType(bpmnModelBuilder);
    DiagramElementImpl.registerType(bpmnModelBuilder);
    EdgeImpl.registerType(bpmnModelBuilder);
    org.camunda.bpm.model.bpmn.impl.instance.di.ExtensionImpl.registerType(bpmnModelBuilder);
    LabelImpl.registerType(bpmnModelBuilder);
    LabeledEdgeImpl.registerType(bpmnModelBuilder);
    LabeledShapeImpl.registerType(bpmnModelBuilder);
    NodeImpl.registerType(bpmnModelBuilder);
    PlaneImpl.registerType(bpmnModelBuilder);
    ShapeImpl.registerType(bpmnModelBuilder);
    StyleImpl.registerType(bpmnModelBuilder);
    WaypointImpl.registerType(bpmnModelBuilder);

    /** BPMNDI */
    BpmnDiagramImpl.registerType(bpmnModelBuilder);
    BpmnEdgeImpl.registerType(bpmnModelBuilder);
    BpmnLabelImpl.registerType(bpmnModelBuilder);
    BpmnLabelStyleImpl.registerType(bpmnModelBuilder);
    BpmnPlaneImpl.registerType(bpmnModelBuilder);
    BpmnShapeImpl.registerType(bpmnModelBuilder);

    /** camunda extensions */
    CamundaConnectorImpl.registerType(bpmnModelBuilder);
    CamundaConnectorIdImpl.registerType(bpmnModelBuilder);
    CamundaConstraintImpl.registerType(bpmnModelBuilder);
    CamundaEntryImpl.registerType(bpmnModelBuilder);
    CamundaErrorEventDefinitionImpl.registerType(bpmnModelBuilder);
    CamundaExecutionListenerImpl.registerType(bpmnModelBuilder);
    CamundaExpressionImpl.registerType(bpmnModelBuilder);
    CamundaFailedJobRetryTimeCycleImpl.registerType(bpmnModelBuilder);
    CamundaFieldImpl.registerType(bpmnModelBuilder);
    CamundaFormDataImpl.registerType(bpmnModelBuilder);
    CamundaFormFieldImpl.registerType(bpmnModelBuilder);
    CamundaFormPropertyImpl.registerType(bpmnModelBuilder);
    CamundaInImpl.registerType(bpmnModelBuilder);
    CamundaInputOutputImpl.registerType(bpmnModelBuilder);
    CamundaInputParameterImpl.registerType(bpmnModelBuilder);
    CamundaListImpl.registerType(bpmnModelBuilder);
    CamundaMapImpl.registerType(bpmnModelBuilder);
    CamundaOutputParameterImpl.registerType(bpmnModelBuilder);
    CamundaOutImpl.registerType(bpmnModelBuilder);
    CamundaPotentialStarterImpl.registerType(bpmnModelBuilder);
    CamundaPropertiesImpl.registerType(bpmnModelBuilder);
    CamundaPropertyImpl.registerType(bpmnModelBuilder);
    CamundaScriptImpl.registerType(bpmnModelBuilder);
    CamundaStringImpl.registerType(bpmnModelBuilder);
    CamundaTaskListenerImpl.registerType(bpmnModelBuilder);
    CamundaValidationImpl.registerType(bpmnModelBuilder);
    CamundaValueImpl.registerType(bpmnModelBuilder);
  }

  /**
   * @return the {@link Model} instance to use
   */
  public Model getBpmnModel() {
    return bpmnModel;
  }

  public ModelBuilder getBpmnModelBuilder() {
    return bpmnModelBuilder;
  }

  /**
   * @param bpmnModel the bpmnModel to set
   */
  public void setBpmnModel(Model bpmnModel) {
    this.bpmnModel = bpmnModel;
  }

}
