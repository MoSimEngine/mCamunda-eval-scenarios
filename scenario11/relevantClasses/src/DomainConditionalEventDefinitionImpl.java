package org.camunda.bpm.model.bpmn.impl.instance.domain.events.advanced;
import java.util.List;
import org.camunda.bpm.model.bpmn.instance.Condition;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.impl.util.StringUtil;
public class DomainConditionalEventDefinitionImpl extends CommonsConditionalEventDefinitionImpl {
    public DomainConditionalEventDefinitionImpl(ModelTypeInstanceContext context) {
        super(context);
    }

    @Override
    public Condition getCondition() {
        return CommonsConditionalEventDefinitionImpl.conditionChild.getChild(this);
    }

    @Override
    public void setCondition(Condition condition) {
        CommonsConditionalEventDefinitionImpl.conditionChild.setChild(this, condition);
    }

    @Override
    public String getCamundaVariableName() {
        return CommonsConditionalEventDefinitionImpl.camundaVariableName.getValue(this);
    }

    @Override
    public void setCamundaVariableName(String variableName) {
        CommonsConditionalEventDefinitionImpl.camundaVariableName.setValue(this, variableName);
    }

    @Override
    public String getCamundaVariableEvents() {
        return CommonsConditionalEventDefinitionImpl.camundaVariableEvents.getValue(this);
    }

    @Override
    public void setCamundaVariableEvents(String variableEvents) {
        CommonsConditionalEventDefinitionImpl.camundaVariableEvents.setValue(this, variableEvents);
    }

    @Override
    public List<String> getCamundaVariableEventsList() {
        String variableEvents = CommonsConditionalEventDefinitionImpl.camundaVariableEvents.getValue(this);
        return StringUtil.splitCommaSeparatedList(variableEvents);
    }

    @Override
    public void setCamundaVariableEventsList(List<String> variableEventsList) {
        String variableEvents = StringUtil.joinCommaSeparatedList(variableEventsList);
        CommonsConditionalEventDefinitionImpl.camundaVariableEvents.setValue(this, variableEvents);
    }
}