package org.camunda.bpm.model.bpmn.builder;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.TimeCycle;
import org.camunda.bpm.model.bpmn.instance.TimeDate;
import org.camunda.bpm.model.bpmn.instance.TimeDuration;
import org.camunda.bpm.model.bpmn.instance.domain.events.advanced.TimerEventDefinition;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.BaseElement;

public class TimerEventDefinitionBuilder<B extends TimerEventDefinitionBuilder<B, E>, E extends BaseElement> extends AbstractBpmnModelElementBuilder<B, E>{
    protected TimerEventDefinitionBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    protected TimerEventDefinition createTimeCycle(String timerCycle) {
        TimeCycle timeCycle = createInstance(TimeCycle.class);
        timeCycle.setTextContent(timerCycle);
        TimerEventDefinition timerDefinition = createInstance(TimerEventDefinition.class);
        timerDefinition.setTimeCycle(timeCycle);
        return timerDefinition;
    }

    protected TimerEventDefinition createTimeDate(String timerDate) {
        TimeDate timeDate = createInstance(TimeDate.class);
        timeDate.setTextContent(timerDate);
        TimerEventDefinition timerDefinition = createInstance(TimerEventDefinition.class);
        timerDefinition.setTimeDate(timeDate);
        return timerDefinition;
    }

    protected TimerEventDefinition createTimeDuration(String timerDuration) {
        TimeDuration timeDuration = createInstance(TimeDuration.class);
        timeDuration.setTextContent(timerDuration);
        TimerEventDefinition timerDefinition = createInstance(TimerEventDefinition.class);
        timerDefinition.setTimeDuration(timeDuration);
        return timerDefinition;
    }
}