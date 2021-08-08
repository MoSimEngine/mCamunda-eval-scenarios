package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.CatchEvent;

public class AbstractParadigmCatchEventBuilder<B extends AbstractCatchEventBuilder<B, E>, E extends CatchEvent> extends AbstractEventBuilder<B, E> {
    public AbstractParadigmCatchEventBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Sets the event to be parallel multiple
     *
     * @return the builder object
     */
    public B parallelMultiple() {
        element.isParallelMultiple();
        return myself;
    }

    /**
     * Sets an event definition for the timer with a time date.
     *
     * @param timerDate the time date of the timer
     * @return the builder object
     */
    public B timerWithDate(String timerDate) {
        element.getEventDefinitions().add(createTimeDate(timerDate));

        return myself;
    }

    /**
     * Sets an event definition for the timer with a time duration.
     *
     * @param timerDuration the time duration of the timer
     * @return the builder object
     */
    public B timerWithDuration(String timerDuration) {
        element.getEventDefinitions().add(createTimeDuration(timerDuration));

        return myself;
    }

    /**
     * Sets an event definition for the timer with a time cycle.
     *
     * @param timerCycle the time cycle of the timer
     * @return the builder object
     */
    public B timerWithCycle(String timerCycle) {
        element.getEventDefinitions().add(createTimeCycle(timerCycle));

        return myself;
    }
}
