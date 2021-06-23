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
package org.camunda.bpm.model.bpmn.instance.paradigm.flows;

import org.camunda.bpm.model.bpmn.Query;
import org.camunda.bpm.model.bpmn.builder.AbstractFlowNodeBuilder;

import java.util.Collection;

/**
 * The BPMN flowNode element
 *
 * @author Sebastian Menski
 */
public interface FlowNode extends FlowElement {

  @SuppressWarnings("rawtypes")
  AbstractFlowNodeBuilder builder();

  Collection<SequenceFlow> getIncoming();

  Collection<SequenceFlow> getOutgoing();

  Query<FlowNode> getPreviousNodes();

  Query<FlowNode> getSucceedingNodes();

  boolean isCamundaAsyncBefore();

  void setCamundaAsyncBefore(boolean isCamundaAsyncBefore);

  boolean isCamundaAsyncAfter();

  void setCamundaAsyncAfter(boolean isCamundaAsyncAfter);

  boolean isCamundaExclusive();

  void setCamundaExclusive(boolean isCamundaExclusive);

  String getCamundaJobPriority();

  void setCamundaJobPriority(String jobPriority);

}
