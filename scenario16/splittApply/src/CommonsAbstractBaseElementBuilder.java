package org.camunda.bpm.model.bpmn.builder;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.BpmnModelException;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.BaseElement;
public abstract class CommonsAbstractBaseElementBuilder<B extends CommonsAbstractBaseElementBuilder<B, E>, E extends BaseElement> extends AbstractBpmnModelElementBuilder<B, E> {
    public static final double SPACE = 50;

    protected CommonsAbstractBaseElementBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    protected <T extends BpmnModelElementInstance> T createInstance(Class<T> typeClass) {
        return modelInstance.newInstance(typeClass);
    }

    protected <T extends BpmnModelElementInstance> T createChild(Class<T> typeClass) {
        return createChild(element, typeClass);
    }

    protected <T extends BpmnModelElementInstance> T createChild(BpmnModelElementInstance parent, Class<T> typeClass) {
        T instance = createInstance(typeClass);
        parent.addChildElement(instance);
        return instance;
    }

    protected <T extends BpmnModelElementInstance> T createSibling(Class<T> typeClass) {
        T instance = createInstance(typeClass);
        element.getParentElement().addChildElement(instance);
        return instance;
    }

    protected <T extends BpmnModelElementInstance> T getCreateSingleChild(Class<T> typeClass) {
        return getCreateSingleChild(element, typeClass);
    }

    protected <T extends BpmnModelElementInstance> T getCreateSingleChild(BpmnModelElementInstance parent, Class<T> typeClass) {
        Collection<T> childrenOfType = parent.getChildElementsByType(typeClass);
        if (childrenOfType.isEmpty()) {
            return createChild(parent, typeClass);
        } else if (childrenOfType.size() > 1) {
            throw new BpmnModelException((((("Element " + parent) + " of type ") + parent.getElementType().getTypeName()) + " has more than one child element of type ") + typeClass.getName());
        } else {
            return childrenOfType.iterator().next();
        }
    }

    protected <T extends BpmnModelElementInstance> T getCreateSingleExtensionElement(Class<T> typeClass) {
        ExtensionElements extensionElements = getCreateSingleChild(ExtensionElements.class);
        return getCreateSingleChild(extensionElements, typeClass);
    }

    /**
     * Add an extension element to the element.
     *
     * @param extensionElement
     * 		the extension element to add
     * @return the builder object
     */
    public B addExtensionElement(BpmnModelElementInstance extensionElement) {
        ExtensionElements extensionElements = getCreateSingleChild(ExtensionElements.class);
        extensionElements.addChildElement(extensionElement);
        return myself;
    }

    protected BpmnPlane findBpmnPlane() {
        Collection<BpmnPlane> planes = modelInstance.getModelElementsByType(BpmnPlane.class);
        return planes.iterator().next();
    }
}