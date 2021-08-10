package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.CallActivity;
public class ParadigmAbstractCallActivityBuilder<B extends CommonsAbstractCallActivityBuilder<B>> extends CommonsAbstractCallActivityBuilder<B> {
    protected ParadigmAbstractCallActivityBuilder(BpmnModelInstance modelInstance, CallActivity element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Sets the called element
     *
     * @param calledElement
     * 		the process to call
     * @return the builder object
     */
    public B calledElement(String calledElement) {
        element.setCalledElement(calledElement);
        return myself;
    }

    /**
     * camunda extensions
     */
    /**
     *
     *
     * @deprecated use camundaAsyncBefore() instead.

    Sets the camunda async attribute to true.
     * @return the builder object
     */
    @Deprecated
    public B camundaAsync() {
        element.setCamundaAsyncBefore(true);
        return myself;
    }

    /**
     *
     *
     * @deprecated use camundaAsyncBefore(isCamundaAsyncBefore) instead

    Sets the camunda async attribute.
     * @param isCamundaAsync
     * 		the async state of the task
     * @return the builder object
     */
    @Deprecated
    public B camundaAsync(boolean isCamundaAsync) {
        element.setCamundaAsyncBefore(isCamundaAsync);
        return myself;
    }

    /**
     * Sets the camunda calledElementBinding attribute
     *
     * @param camundaCalledElementBinding
     * 		the element binding to use
     * @return the builder object
     */
    public B camundaCalledElementBinding(String camundaCalledElementBinding) {
        element.setCamundaCalledElementBinding(camundaCalledElementBinding);
        return myself;
    }

    /**
     * Sets the camunda calledElementVersion attribute
     *
     * @param camundaCalledElementVersion
     * 		the element version to use
     * @return the builder object
     */
    public B camundaCalledElementVersion(String camundaCalledElementVersion) {
        element.setCamundaCalledElementVersion(camundaCalledElementVersion);
        return myself;
    }

    /**
     * Sets the camunda calledElementVersionTag attribute
     *
     * @param camundaCalledElementVersionTag
     * 		the element version to use
     * @return the builder object
     */
    public B camundaCalledElementVersionTag(String camundaCalledElementVersionTag) {
        element.setCamundaCalledElementVersionTag(camundaCalledElementVersionTag);
        return myself;
    }

    /**
     * Sets the camunda calledElementTenantId attribute
     *
     * @param camundaCalledElementTenantId
     * 		the called element tenant id
     * @return the builder object
     */
    public B camundaCalledElementTenantId(String camundaCalledElementTenantId) {
        element.setCamundaCalledElementTenantId(camundaCalledElementTenantId);
        return myself;
    }

    /**
     * Sets the camunda caseRef attribute
     *
     * @param caseRef
     * 		the case to call
     * @return the builder object
     */
    public B camundaCaseRef(String caseRef) {
        element.setCamundaCaseRef(caseRef);
        return myself;
    }

    /**
     * Sets the camunda caseBinding attribute
     *
     * @param camundaCaseBinding
     * 		the case binding to use
     * @return the builder object
     */
    public B camundaCaseBinding(String camundaCaseBinding) {
        element.setCamundaCaseBinding(camundaCaseBinding);
        return myself;
    }

    /**
     * Sets the camunda caseVersion attribute
     *
     * @param camundaCaseVersion
     * 		the case version to use
     * @return the builder object
     */
    public B camundaCaseVersion(String camundaCaseVersion) {
        element.setCamundaCaseVersion(camundaCaseVersion);
        return myself;
    }

    /**
     * Sets the caseTenantId
     *
     * @param tenantId
     * 		the tenant id to set
     * @return the builder object
     */
    public B camundaCaseTenantId(String tenantId) {
        element.setCamundaCaseTenantId(tenantId);
        return myself;
    }

    /**
     * Sets the camunda variableMappingClass attribute. It references on a class which implements the
     * {@link DelegateVariableMapping} interface.
     * Is used to delegate the variable in- and output mapping to the given class.
     *
     * @param camundaVariableMappingClass
     * 		the class name to set
     * @return the builder object
     */
    @SuppressWarnings("rawtypes")
    public B camundaVariableMappingClass(Class camundaVariableMappingClass) {
        return camundaVariableMappingClass(camundaVariableMappingClass.getName());
    }

    /**
     * Sets the camunda variableMappingClass attribute. It references on a class which implements the
     * {@link DelegateVariableMapping} interface.
     * Is used to delegate the variable in- and output mapping to the given class.
     *
     * @param camundaVariableMappingClass
     * 		the class name to set
     * @return the builder object
     */
    public B camundaVariableMappingClass(String fullQualifiedClassName) {
        element.setCamundaVariableMappingClass(fullQualifiedClassName);
        return myself;
    }

    /**
     * Sets the camunda variableMappingDelegateExpression attribute. The expression when is resolved
     * references to an object of a class, which implements the {@link DelegateVariableMapping} interface.
     * Is used to delegate the variable in- and output mapping to the given class.
     *
     * @param camundaVariableMappingDelegateExpression
     * 		the expression which references a delegate object
     * @return the builder object
     */
    public B camundaVariableMappingDelegateExpression(String camundaVariableMappingDelegateExpression) {
        element.setCamundaVariableMappingDelegateExpression(camundaVariableMappingDelegateExpression);
        return myself;
    }
}