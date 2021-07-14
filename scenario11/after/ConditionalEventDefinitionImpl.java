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
package org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced;

import java.util.List;

import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.EventDefinitionImpl;
import org.camunda.bpm.model.bpmn.instance.Condition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ConditionalEventDefinition;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.child.ChildElement;

import org.camunda.bpm.model.xml.impl.util.StringUtil;
import org.camunda.bpm.model.xml.type.attribute.Attribute;

/**
 * The BPMN conditionalEventDefinition element
 *
 * @author Sebastian Menski
 */
public class ConditionalEventDefinitionImpl extends EventDefinitionImpl implements ConditionalEventDefinition {

  protected static ChildElement<Condition> conditionChild;
  protected static Attribute<String> camundaVariableName;
  protected static Attribute<String> camundaVariableEvents;

  public ConditionalEventDefinitionImpl(ModelTypeInstanceContext context) {
    super(context);
  }

  @Override
  public Condition getCondition() {
    return conditionChild.getChild(this);
  }

  @Override
  public void setCondition(Condition condition) {
    conditionChild.setChild(this, condition);
  }

  @Override
  public String getCamundaVariableName() {
    return camundaVariableName.getValue(this);
  }

  @Override
  public void setCamundaVariableName(String variableName) {
    camundaVariableName.setValue(this, variableName);
  }

  @Override
  public String getCamundaVariableEvents() {
    return camundaVariableEvents.getValue(this);
  }

  @Override
  public void setCamundaVariableEvents(String variableEvents) {
    camundaVariableEvents.setValue(this, variableEvents);
  }

  @Override
  public List<String> getCamundaVariableEventsList() {
    String variableEvents = camundaVariableEvents.getValue(this);
    return StringUtil.splitCommaSeparatedList(variableEvents);
  }

  @Override
  public void setCamundaVariableEventsList(List<String> variableEventsList) {
    String variableEvents = StringUtil.joinCommaSeparatedList(variableEventsList);
    camundaVariableEvents.setValue(this, variableEvents);
  }
}
