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

import org.camunda.bpm.model.bpmn.builder.ProcessBuilder;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
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
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.ParticipantRef;
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
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.ModelException;
import org.camunda.bpm.model.xml.ModelParseException;
import org.camunda.bpm.model.xml.ModelValidationException;
import org.camunda.bpm.model.xml.impl.instance.ModelElementInstanceImpl;
import org.camunda.bpm.model.xml.impl.util.IoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.ACTIVITI_NS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;

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

  public static ProcessBuilder createProcess(String processId) {
    return BPMNProcessBuilderDomainDelegate.createProcess().id(processId);
  }

  public static ProcessBuilder createExecutableProcess() {
    return BPMNProcessBuilderDomainDelegate.createProcess().executable();
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
    BpmnParadigm.registerTypes(bpmnModelBuilder);
    BpmnDomain.registerTypes(bpmnModelBuilder);
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
    ModelElementInstanceImpl.registerType(bpmnModelBuilder);
    CategoryValueRef.registerType(bpmnModelBuilder);
    CorrelationPropertyRef.registerType(bpmnModelBuilder);
    DataInputRefs.registerType(bpmnModelBuilder);
    DataOutputRefs.registerType(bpmnModelBuilder);
    EndPointRef.registerType(bpmnModelBuilder);
    ErrorRef.registerType(bpmnModelBuilder);
    EventDefinitionRef.registerType(bpmnModelBuilder);
    ExtensionElementsImpl.registerType(bpmnModelBuilder);
    Incoming.registerType(bpmnModelBuilder);
    InMessageRef.registerType(bpmnModelBuilder);
    InnerParticipantRef.registerType(bpmnModelBuilder);
    InputSetRefs.registerType(bpmnModelBuilder);
    InterfaceRef.registerType(bpmnModelBuilder);
    LoopDataInputRef.registerType(bpmnModelBuilder);
    LoopDataOutputRef.registerType(bpmnModelBuilder);
    MessageFlowRef.registerType(bpmnModelBuilder);
    OptionalInputRefs.registerType(bpmnModelBuilder);
    OptionalOutputRefs.registerType(bpmnModelBuilder);
    OuterParticipantRef.registerType(bpmnModelBuilder);
    OutMessageRef.registerType(bpmnModelBuilder);
    Outgoing.registerType(bpmnModelBuilder);
    OptionalInputRefs.registerType(bpmnModelBuilder);
    OptionalOutputRefs.registerType(bpmnModelBuilder);
    OuterParticipantRef.registerType(bpmnModelBuilder);
    OutMessageRef.registerType(bpmnModelBuilder);
    Outgoing.registerType(bpmnModelBuilder);
    ParticipantRef.registerType(bpmnModelBuilder);
    ScriptImpl.registerType(bpmnModelBuilder);
    Source.registerType(bpmnModelBuilder);
    SourceRef.registerType(bpmnModelBuilder);
    SupportedInterfaceRef.registerType(bpmnModelBuilder);
    Supports.registerType(bpmnModelBuilder);
    Target.registerType(bpmnModelBuilder);
    TargetRef.registerType(bpmnModelBuilder);
    TextImpl.registerType(bpmnModelBuilder);
    WhileExecutingInputRefs.registerType(bpmnModelBuilder);
    WhileExecutingOutputRefs.registerType(bpmnModelBuilder);
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
