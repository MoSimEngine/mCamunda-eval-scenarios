package org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced;

import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.Condition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ConditionalEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EventDefinition;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.child.SequenceBuilder;

public class ParadigmConditionalEventDefinitionRegister {
    public static void registerType(ModelBuilder modelBuilder) {
        ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(ConditionalEventDefinition.class, BpmnModelConstants.BPMN_ELEMENT_CONDITIONAL_EVENT_DEFINITION)
                .namespaceUri(BpmnModelConstants.BPMN20_NS)
                .extendsType(EventDefinition.class)
                .instanceProvider(new ModelElementTypeBuilder.ModelTypeInstanceProvider<ConditionalEventDefinition>() {

                    @Override
                    public ConditionalEventDefinition newInstance(ModelTypeInstanceContext instanceContext) {
                        return new ConditionalEventDefinitionImpl(instanceContext);
                    }
                });

        SequenceBuilder sequenceBuilder = typeBuilder.sequence();

        ConditionalEventDefinitionImpl.conditionChild = sequenceBuilder.element(Condition.class)
                .required()
                .build();

        /** camunda extensions */

        ConditionalEventDefinitionImpl.camundaVariableName = typeBuilder.stringAttribute(BpmnModelConstants.CAMUNDA_ATTRIBUTE_VARIABLE_NAME)
                .namespace(BpmnModelConstants.CAMUNDA_NS)
                .build();

        ConditionalEventDefinitionImpl.camundaVariableEvents = typeBuilder.stringAttribute(BpmnModelConstants.CAMUNDA_ATTRIBUTE_VARIABLE_EVENTS)
                .namespace(BpmnModelConstants.CAMUNDA_NS)
                .build();

        typeBuilder.build();
    }
}