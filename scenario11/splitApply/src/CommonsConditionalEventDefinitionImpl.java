package org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.events.EventDefinitionImpl;
import org.camunda.bpm.model.bpmn.instance.Condition;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.EventDefinition;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.camunda.bpm.model.xml.type.child.ChildElement;
public class CommonsConditionalEventDefinitionImpl extends EventDefinitionImpl implements EventDefinition {
    protected static ChildElement<Condition> conditionChild;

    protected static Attribute<String> camundaVariableName;

    protected static Attribute<String> camundaVariableEvents;

    public CommonsConditionalEventDefinitionImpl(ModelTypeInstanceContext context) {
        super(context);
    }
}