package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.StartEvent;
public abstract class ParadigmAbstractStartEventBuilder<B extends CommonsAbstractStartEventBuilder<B>> extends CommonsAbstractStartEventBuilder<B> {
    protected ParadigmAbstractStartEventBuilder(BpmnModelInstance modelInstance, StartEvent element, Class<?> selfType) {
        super(modelInstance, element, selfType);
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
     * @deprecated use camundaAsyncBefore(isCamundaAsyncBefore) instead.

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
     * Sets the camunda form handler class attribute.
     *
     * @param camundaFormHandlerClass
     * 		the class name of the form handler
     * @return the builder object
     */
    public B camundaFormHandlerClass(String camundaFormHandlerClass) {
        element.setCamundaFormHandlerClass(camundaFormHandlerClass);
        return myself;
    }

    /**
     * Sets the camunda form key attribute.
     *
     * @param camundaFormKey
     * 		the form key to set
     * @return the builder object
     */
    public B camundaFormKey(String camundaFormKey) {
        element.setCamundaFormKey(camundaFormKey);
        return myself;
    }

    /**
     * Sets the camunda initiator attribute.
     *
     * @param camundaInitiator
     * 		the initiator to set
     * @return the builder object
     */
    public B camundaInitiator(String camundaInitiator) {
        element.setCamundaInitiator(camundaInitiator);
        return myself;
    }

    /**
     * Sets whether the start event is interrupting or not.
     */
    public B interrupting(boolean interrupting) {
        element.setInterrupting(interrupting);
        return myself;
    }
}