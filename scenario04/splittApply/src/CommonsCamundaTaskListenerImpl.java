package org.camunda.bpm.model.bpmn.impl.instance.camunda;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.impl.instance.bpmnmodelelement.BpmnModelElementInstanceImpl;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.camunda.bpm.model.xml.type.child.ChildElement;
import org.camunda.bpm.model.xml.type.child.ChildElementCollection;
public class CommonsCamundaTaskListenerImpl extends BpmnModelElementInstanceImpl implements BpmnModelElementInstance {
    protected static Attribute<String> camundaEventAttribute;

    protected static Attribute<String> camundaClassAttribute;

    protected static Attribute<String> camundaExpressionAttribute;

    protected static Attribute<String> camundaDelegateExpressionAttribute;

    protected static ChildElementCollection<CamundaField> camundaFieldCollection;

    protected static ChildElement<CamundaScript> camundaScriptChild;

    public CommonsCamundaTaskListenerImpl(ModelTypeInstanceContext instanceContext) {
        super(instanceContext);
    }

    public String getCamundaEvent() {
        return CommonsCamundaTaskListenerImpl.camundaEventAttribute.getValue(this);
    }

    public void setCamundaEvent(String camundaEvent) {
        CommonsCamundaTaskListenerImpl.camundaEventAttribute.setValue(this, camundaEvent);
    }

    public String getCamundaClass() {
        return CommonsCamundaTaskListenerImpl.camundaClassAttribute.getValue(this);
    }

    public void setCamundaClass(String camundaClass) {
        CommonsCamundaTaskListenerImpl.camundaClassAttribute.setValue(this, camundaClass);
    }

    public String getCamundaExpression() {
        return CommonsCamundaTaskListenerImpl.camundaExpressionAttribute.getValue(this);
    }

    public void setCamundaExpression(String camundaExpression) {
        CommonsCamundaTaskListenerImpl.camundaExpressionAttribute.setValue(this, camundaExpression);
    }

    public String getCamundaDelegateExpression() {
        return CommonsCamundaTaskListenerImpl.camundaDelegateExpressionAttribute.getValue(this);
    }

    public void setCamundaDelegateExpression(String camundaDelegateExpression) {
        CommonsCamundaTaskListenerImpl.camundaDelegateExpressionAttribute.setValue(this, camundaDelegateExpression);
    }

    public Collection<CamundaField> getCamundaFields() {
        return CommonsCamundaTaskListenerImpl.camundaFieldCollection.get(this);
    }

    public CamundaScript getCamundaScript() {
        return CommonsCamundaTaskListenerImpl.camundaScriptChild.getChild(this);
    }

    public void setCamundaScript(CamundaScript camundaScript) {
        CommonsCamundaTaskListenerImpl.camundaScriptChild.setChild(this, camundaScript);
    }
}