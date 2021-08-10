package org.camunda.bpm.model.bpmn.impl.instance.camunda;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaScript;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaTaskListener;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.TimerEventDefinition;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.camunda.bpm.model.xml.type.child.ChildElement;
import org.camunda.bpm.model.xml.type.child.ChildElementCollection;
import org.camunda.bpm.model.xml.type.child.SequenceBuilder;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_CLASS;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_DELEGATE_EXPRESSION;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_EVENT;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_EXPRESSION;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ELEMENT_TASK_LISTENER;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;
public class DomainCamundaTaskListenerImpl extends CommonsCamundaTaskListenerImpl {
    protected static ChildElementCollection<TimerEventDefinition> timeoutCollection;

    public static void registerType(ModelBuilder modelBuilder) {
        ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(CamundaTaskListener.class, CAMUNDA_ELEMENT_TASK_LISTENER).namespaceUri(CAMUNDA_NS).instanceProvider(new ModelTypeInstanceProvider<CamundaTaskListener>() {
            public CamundaTaskListener newInstance(ModelTypeInstanceContext instanceContext) {
                return new CamundaTaskListenerImpl(instanceContext);
            }
        });
        CommonsCamundaTaskListenerImpl.camundaEventAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_EVENT).namespace(CAMUNDA_NS).build();
        CommonsCamundaTaskListenerImpl.camundaClassAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_CLASS).namespace(CAMUNDA_NS).build();
        CommonsCamundaTaskListenerImpl.camundaExpressionAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_EXPRESSION).namespace(CAMUNDA_NS).build();
        CommonsCamundaTaskListenerImpl.camundaDelegateExpressionAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_DELEGATE_EXPRESSION).namespace(CAMUNDA_NS).build();
        SequenceBuilder sequenceBuilder = typeBuilder.sequence();
        CommonsCamundaTaskListenerImpl.camundaFieldCollection = sequenceBuilder.elementCollection(CamundaField.class).build();
        CommonsCamundaTaskListenerImpl.camundaScriptChild = sequenceBuilder.element(CamundaScript.class).build();
        DomainCamundaTaskListenerImpl.timeoutCollection = sequenceBuilder.element(TimerEventDefinition.class).build();
        typeBuilder.build();
    }

    public DomainCamundaTaskListenerImpl(ModelTypeInstanceContext instanceContext) {
        super(instanceContext);
    }

    public Collection<TimerEventDefinition> getTimeouts() {
        return DomainCamundaTaskListenerImpl.timeoutCollection.get(this);
    }
}