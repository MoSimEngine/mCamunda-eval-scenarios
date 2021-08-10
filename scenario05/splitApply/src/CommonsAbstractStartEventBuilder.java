package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.CatchEvent;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.StartEvent;
public abstract class CommonsAbstractStartEventBuilder<B extends AbstractCatchEventBuilder<B, E>, E extends CatchEvent> extends AbstractCatchEventBuilder<B, StartEvent> {
    protected CommonsAbstractStartEventBuilder(BpmnModelInstance modelInstance, StartEvent element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Creates a new camunda form field extension element.
     *
     * @return the builder object
     */
    public CamundaStartEventFormFieldBuilder camundaFormField() {
        CamundaFormData camundaFormData = getCreateSingleExtensionElement(CamundaFormData.class);
        CamundaFormField camundaFormField = createChild(camundaFormData, CamundaFormField.class);
        return new CamundaStartEventFormFieldBuilder(modelInstance, element, camundaFormField);
    }
}