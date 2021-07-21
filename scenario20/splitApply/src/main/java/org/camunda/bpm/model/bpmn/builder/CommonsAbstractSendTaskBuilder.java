package org.camunda.bpm.model.bpmn.builder;
public abstract class CommonsAbstractSendTaskBuilder<B extends CommonsAbstractSendTaskBuilder<B>> extends AbstractSendTaskBuilder<B> {
    /**
     * camunda extensions
     */
    /**
     * Sets the camunda class attribute.
     *
     * @param camundaClass
     * 		the class name to set
     * @return the builder object
     */
    public B camundaClass(Class delegateClass) {
        return camundaClass(delegateClass.getName());
    }
}