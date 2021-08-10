package org.camunda.bpm.model.bpmn.impl.instance.paradigm.services;
import org.camunda.bpm.model.bpmn.impl.BpmnModelConstants;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.RootElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.data.ItemDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Error;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder;
import org.camunda.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;
import org.camunda.bpm.model.xml.type.attribute.Attribute;
import org.camunda.bpm.model.xml.type.reference.AttributeReference;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_ERROR_CODE;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_NAME;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_STRUCTURE_REF;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ELEMENT_ERROR;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_ATTRIBUTE_ERROR_MESSAGE;
import static org.camunda.bpm.model.bpmn.impl.BpmnModelConstants.CAMUNDA_NS;
public class ParadigmErrorImpl extends CommonsErrorImpl {
    protected static AttributeReference<ItemDefinition> structureRefAttribute;

    public static void registerType(ModelBuilder modelBuilder) {
        ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(Error.class, BPMN_ELEMENT_ERROR).namespaceUri(BpmnModelConstants.BPMN20_NS).extendsType(RootElement.class).instanceProvider(new ModelTypeInstanceProvider<Error>() {
            public Error newInstance(ModelTypeInstanceContext instanceContext) {
                return new ErrorImpl(instanceContext);
            }
        });
        CommonsErrorImpl.nameAttribute = typeBuilder.stringAttribute(BPMN_ATTRIBUTE_NAME).build();
        CommonsErrorImpl.errorCodeAttribute = typeBuilder.stringAttribute(BPMN_ATTRIBUTE_ERROR_CODE).build();
        CommonsErrorImpl.camundaErrorMessageAttribute = typeBuilder.stringAttribute(CAMUNDA_ATTRIBUTE_ERROR_MESSAGE).namespace(CAMUNDA_NS).build();
        ParadigmErrorImpl.structureRefAttribute = typeBuilder.stringAttribute(BPMN_ATTRIBUTE_STRUCTURE_REF).qNameAttributeReference(ItemDefinition.class).build();
        typeBuilder.build();
    }

    public ParadigmErrorImpl(ModelTypeInstanceContext context) {
        super(context);
    }

    public String getName() {
        return CommonsErrorImpl.nameAttribute.getValue(this);
    }

    public void setName(String name) {
        CommonsErrorImpl.nameAttribute.setValue(this, name);
    }

    public String getErrorCode() {
        return CommonsErrorImpl.errorCodeAttribute.getValue(this);
    }

    public void setErrorCode(String errorCode) {
        CommonsErrorImpl.errorCodeAttribute.setValue(this, errorCode);
    }

    public String getCamundaErrorMessage() {
        return CommonsErrorImpl.camundaErrorMessageAttribute.getValue(this);
    }

    public void setCamundaErrorMessage(String camundaErrorMessage) {
        CommonsErrorImpl.camundaErrorMessageAttribute.setValue(this, camundaErrorMessage);
    }

    public ItemDefinition getStructure() {
        return ParadigmErrorImpl.structureRefAttribute.getReferenceTargetElement(this);
    }

    public void setStructure(ItemDefinition structure) {
        ParadigmErrorImpl.structureRefAttribute.setReferenceTargetElement(this, structure);
    }
}