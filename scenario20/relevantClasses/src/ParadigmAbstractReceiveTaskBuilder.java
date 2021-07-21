package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.instance.paradigm.messaging.Message;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Operation;
public abstract class ParadigmAbstractReceiveTaskBuilder<B extends ParadigmAbstractReceiveTaskBuilder<B>> extends CommonsAbstractReceiveTaskBuilder<B> {
    /**
     * Sets the implementation of the receive task.
     *
     * @param implementation
     * 		the implementation to set
     * @return the builder object
     */
    public B implementation(String implementation) {
        element.setImplementation(implementation);
        return myself;
    }

    /**
     * Sets the receive task instantiate attribute to true.
     *
     * @return the builder object
     */
    public B instantiate() {
        element.setInstantiate(true);
        return myself;
    }

    /**
     * Sets the message of the send task.
     *
     * @param message
     * 		the message to set
     * @return the builder object
     */
    public B message(Message message) {
        element.setMessage(message);
        return myself;
    }

    /**
     * Sets the message with the given message name. If already a message
     * with this name exists it will be used, otherwise a new message is created.
     *
     * @param messageName
     * 		the name of the message
     * @return the builder object
     */
    public B message(String messageName) {
        Message message = findMessageForName(messageName);
        return message(message);
    }

    /**
     * Sets the operation of the send task.
     *
     * @param operation
     * 		the operation to set
     * @return the builder object
     */
    public B operation(Operation operation) {
        element.setOperation(operation);
        return myself;
    }
}