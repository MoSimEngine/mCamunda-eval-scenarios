package org.camunda.bpm.model.bpmn.builder;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaIn;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaOut;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.CallActivity;
public class CommonsAbstractCallActivityBuilder<B extends CommonsAbstractCallActivityBuilder<B>> extends AbstractActivityBuilder<B, CallActivity> {
    protected CommonsAbstractCallActivityBuilder(BpmnModelInstance modelInstance, CallActivity element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    /**
     * Sets a "camunda in" parameter to pass a variable from the super process instance to the sub process instance
     *
     * @param source
     * 		the name of variable in the super process instance
     * @param target
     * 		the name of the variable in the sub process instance
     * @return the builder object
     */
    public B camundaIn(String source, String target) {
        CamundaIn param = modelInstance.newInstance(CamundaIn.class);
        param.setCamundaSource(source);
        param.setCamundaTarget(target);
        addExtensionElement(param);
        return myself;
    }

    /**
     * Sets a "camunda out" parameter to pass a variable from a sub process instance to the super process instance
     *
     * @param source
     * 		the name of variable in the sub process instance
     * @param target
     * 		the name of the variable in the super process instance
     * @return the builder object
     */
    public B camundaOut(String source, String target) {
        CamundaOut param = modelInstance.newInstance(CamundaOut.class);
        param.setCamundaSource(source);
        param.setCamundaTarget(target);
        addExtensionElement(param);
        return myself;
    }
}