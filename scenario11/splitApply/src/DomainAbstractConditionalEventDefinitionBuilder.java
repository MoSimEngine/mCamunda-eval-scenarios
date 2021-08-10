package org.camunda.bpm.model.bpmn.builder;
import java.util.List;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Condition;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.ConditionalEventDefinition;
public class DomainAbstractConditionalEventDefinitionBuilder<B extends ParadigmAbstractConditionalEventDefinitionBuilder<B>> extends ParadigmAbstractConditionalEventDefinitionBuilder<B> {
    public DomainAbstractConditionalEventDefinitionBuilder(BpmnModelInstance modelInstance, ConditionalEventDefinition element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Sets the condition of the conditional event definition.
     *
     * @param conditionText
     * 		the condition which should be evaluate to true or false
     * @return the builder object
     */
    public B condition(String conditionText) {
        Condition condition = createInstance(Condition.class);
        condition.setTextContent(conditionText);
        element.setCondition(condition);
        return myself;
    }

    /**
     * Sets the camunda variable name attribute, that defines on
     * which variable the condition should be evaluated.
     *
     * @param variableName
     * 		the variable on which the condition should be evaluated
     * @return the builder object
     */
    public B camundaVariableName(String variableName) {
        element.setCamundaVariableName(variableName);
        return myself;
    }

    /**
     * Set the camunda variable events attribute, that defines the variable
     * event on which the condition should be evaluated.
     *
     * @param variableEvents
     * 		the events on which the condition should be evaluated
     * @return the builder object
     */
    public B camundaVariableEvents(String variableEvents) {
        element.setCamundaVariableEvents(variableEvents);
        return myself;
    }

    /**
     * Set the camunda variable events attribute, that defines the variable
     * event on which the condition should be evaluated.
     *
     * @param variableEvents
     * 		the events on which the condition should be evaluated
     * @return the builder object
     */
    public B camundaVariableEvents(List<String> variableEvents) {
        element.setCamundaVariableEventsList(variableEvents);
        return myself;
    }
}