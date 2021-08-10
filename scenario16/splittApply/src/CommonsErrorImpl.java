package org.camunda.bpm.model.bpmn.impl.instance.paradigm.services;
import org.camunda.bpm.model.bpmn.impl.instance.paradigm.core.RootElementImpl;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.RootElement;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
public class CommonsErrorImpl extends RootElementImpl implements RootElement {
    protected static Attribute<String> nameAttribute;

    protected static Attribute<String> errorCodeAttribute;

    protected static Attribute<String> camundaErrorMessageAttribute;

    public CommonsErrorImpl(ModelTypeInstanceContext context) {
        super(context);
    }
}