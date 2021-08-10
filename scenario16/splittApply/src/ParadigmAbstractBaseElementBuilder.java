package org.camunda.bpm.model.bpmn.builder;
import java.util.Collection;
import java.util.Iterator;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BpmnModelElementInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
import org.camunda.bpm.model.bpmn.instance.paradigm.activities.Activity;
import org.camunda.bpm.model.bpmn.instance.paradigm.artifacts.Association;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.BaseElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.Definitions;
import org.camunda.bpm.model.bpmn.instance.paradigm.core.Documentation;
import org.camunda.bpm.model.bpmn.instance.paradigm.events.Event;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowElement;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.FlowNode;
import org.camunda.bpm.model.bpmn.instance.paradigm.flows.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.gateways.Gateway;
import org.camunda.bpm.model.bpmn.instance.paradigm.messaging.Message;
import org.camunda.bpm.model.bpmn.instance.paradigm.services.Error;
import org.camunda.bpm.model.bpmn.instance.paradigm.subprocesses.SubProcess;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
public abstract class ParadigmAbstractBaseElementBuilder<B extends CommonsAbstractBaseElementBuilder<B, E>, E extends BaseElement> extends CommonsAbstractBaseElementBuilder<B, E> {
    protected ParadigmAbstractBaseElementBuilder(BpmnModelInstance modelInstance, E element, Class<?> selfType) {
        super(modelInstance, element, selfType);
    }

    protected <T extends BaseElement> T createInstance(Class<T> typeClass, String identifier) {
        T instance = createInstance(typeClass);
        if (identifier != null) {
            instance.setId(identifier);
            if (instance instanceof FlowElement) {
                ((FlowElement) (instance)).setName(identifier);
            }
        }
        return instance;
    }

    protected <T extends BaseElement> T createChild(Class<T> typeClass, String identifier) {
        return createChild(element, typeClass, identifier);
    }

    protected <T extends BaseElement> T createChild(BpmnModelElementInstance parent, Class<T> typeClass, String identifier) {
        T instance = createInstance(typeClass, identifier);
        parent.addChildElement(instance);
        return instance;
    }

    protected <T extends BaseElement> T createSibling(Class<T> typeClass, String identifier) {
        T instance = createInstance(typeClass, identifier);
        element.getParentElement().addChildElement(instance);
        return instance;
    }

    protected Message findMessageForName(String messageName) {
        Collection<Message> messages = modelInstance.getModelElementsByType(Message.class);
        for (Message message : messages) {
            if (messageName.equals(message.getName())) {
                // return already existing message for message name
                return message;
            }
        }
        // create new message for non existing message name
        Definitions definitions = modelInstance.getDefinitions();
        Message message = createChild(definitions, Message.class);
        message.setName(messageName);
        return message;
    }

    protected Error findErrorForNameAndCode(String errorCode) {
        return findErrorForNameAndCode(errorCode, null);
    }

    protected Error findErrorForNameAndCode(String errorCode, String errorMessage) {
        Collection<Error> errors = modelInstance.getModelElementsByType(Error.class);
        for (Error error : errors) {
            if (errorCode.equals(error.getErrorCode())) {
                // return already existing error
                return error;
            }
        }
        // create new error
        Definitions definitions = modelInstance.getDefinitions();
        Error error = createChild(definitions, Error.class);
        error.setErrorCode(errorCode);
        if ((errorMessage != null) && (!errorMessage.equals(""))) {
            error.setCamundaErrorMessage(errorMessage);
        }
        return error;
    }

    /**
     * Sets the identifier of the element.
     *
     * @param identifier
     * 		the identifier to set
     * @return the builder object
     */
    public B id(String identifier) {
        element.setId(identifier);
        return myself;
    }

    /**
     * Sets the documentation of the element.
     *
     * @param documentation
     * 		the documentation to set
     * @return the builder object
     */
    public B documentation(String documentation) {
        final Documentation child = createChild(element, Documentation.class);
        child.setTextContent(documentation);
        return myself;
    }

    public BpmnShape createBpmnShape(FlowNode node) {
        BpmnPlane bpmnPlane = findBpmnPlane();
        if (bpmnPlane != null) {
            BpmnShape bpmnShape = createInstance(BpmnShape.class);
            bpmnShape.setBpmnElement(node);
            Bounds nodeBounds = createInstance(Bounds.class);
            if (node instanceof SubProcess) {
                bpmnShape.setExpanded(true);
                nodeBounds.setWidth(350);
                nodeBounds.setHeight(200);
            } else if (node instanceof Activity) {
                nodeBounds.setWidth(100);
                nodeBounds.setHeight(80);
            } else if (node instanceof Event) {
                nodeBounds.setWidth(36);
                nodeBounds.setHeight(36);
            } else if (node instanceof Gateway) {
                nodeBounds.setWidth(50);
                nodeBounds.setHeight(50);
                if (node instanceof ExclusiveGateway) {
                    bpmnShape.setMarkerVisible(true);
                }
            }
            nodeBounds.setX(0);
            nodeBounds.setY(0);
            bpmnShape.addChildElement(nodeBounds);
            bpmnPlane.addChildElement(bpmnShape);
            return bpmnShape;
        }
        return null;
    }

    protected void setCoordinates(BpmnShape shape) {
        BpmnShape source = findBpmnShape(element);
        Bounds shapeBounds = shape.getBounds();
        double x = 0;
        double y = 0;
        if (source != null) {
            Bounds sourceBounds = source.getBounds();
            double sourceX = sourceBounds.getX();
            double sourceWidth = sourceBounds.getWidth();
            x = (sourceX + sourceWidth) + CommonsAbstractBaseElementBuilder.SPACE;
            if (element instanceof FlowNode) {
                FlowNode flowNode = ((FlowNode) (element));
                Collection<SequenceFlow> outgoing = flowNode.getOutgoing();
                if (outgoing.size() == 0) {
                    double sourceY = sourceBounds.getY();
                    double sourceHeight = sourceBounds.getHeight();
                    double targetHeight = shapeBounds.getHeight();
                    y = (sourceY + (sourceHeight / 2)) - (targetHeight / 2);
                } else {
                    SequenceFlow[] sequenceFlows = outgoing.toArray(new SequenceFlow[outgoing.size()]);
                    SequenceFlow last = sequenceFlows[outgoing.size() - 1];
                    BpmnShape targetShape = findBpmnShape(last.getTarget());
                    if (targetShape != null) {
                        Bounds targetBounds = targetShape.getBounds();
                        double lastY = targetBounds.getY();
                        double lastHeight = targetBounds.getHeight();
                        y = (lastY + lastHeight) + CommonsAbstractBaseElementBuilder.SPACE;
                    }
                }
            }
        }
        shapeBounds.setX(x);
        shapeBounds.setY(y);
    }

    /**
     *
     *
     * @deprecated use {@link #createEdge(BaseElement)} instead
     */
    @Deprecated
    public BpmnEdge createBpmnEdge(SequenceFlow sequenceFlow) {
        return createEdge(sequenceFlow);
    }

    public BpmnEdge createEdge(BaseElement baseElement) {
        BpmnPlane bpmnPlane = findBpmnPlane();
        if (bpmnPlane != null) {
            BpmnEdge edge = createInstance(BpmnEdge.class);
            edge.setBpmnElement(baseElement);
            setWaypoints(edge);
            bpmnPlane.addChildElement(edge);
            return edge;
        }
        return null;
    }

    protected void setWaypoints(BpmnEdge edge) {
        BaseElement bpmnElement = edge.getBpmnElement();
        FlowNode edgeSource;
        FlowNode edgeTarget;
        if (bpmnElement instanceof SequenceFlow) {
            SequenceFlow sequenceFlow = ((SequenceFlow) (bpmnElement));
            edgeSource = sequenceFlow.getSource();
            edgeTarget = sequenceFlow.getTarget();
        } else if (bpmnElement instanceof Association) {
            Association association = ((Association) (bpmnElement));
            edgeSource = ((FlowNode) (association.getSource()));
            edgeTarget = ((FlowNode) (association.getTarget()));
        } else {
            throw new RuntimeException("Bpmn element type not supported");
        }
        setWaypointsWithSourceAndTarget(edge, edgeSource, edgeTarget);
    }

    protected void setWaypointsWithSourceAndTarget(BpmnEdge edge, FlowNode edgeSource, FlowNode edgeTarget) {
        BpmnShape source = findBpmnShape(edgeSource);
        BpmnShape target = findBpmnShape(edgeTarget);
        if ((source != null) && (target != null)) {
            Bounds sourceBounds = source.getBounds();
            Bounds targetBounds = target.getBounds();
            double sourceX = sourceBounds.getX();
            double sourceY = sourceBounds.getY();
            double sourceWidth = sourceBounds.getWidth();
            double sourceHeight = sourceBounds.getHeight();
            double targetX = targetBounds.getX();
            double targetY = targetBounds.getY();
            double targetHeight = targetBounds.getHeight();
            Waypoint w1 = createInstance(Waypoint.class);
            if (edgeSource.getOutgoing().size() == 1) {
                w1.setX(sourceX + sourceWidth);
                w1.setY(sourceY + (sourceHeight / 2));
                edge.addChildElement(w1);
            } else {
                w1.setX(sourceX + (sourceWidth / 2));
                w1.setY(sourceY + sourceHeight);
                edge.addChildElement(w1);
                Waypoint w2 = createInstance(Waypoint.class);
                w2.setX(sourceX + (sourceWidth / 2));
                w2.setY(targetY + (targetHeight / 2));
                edge.addChildElement(w2);
            }
            Waypoint w3 = createInstance(Waypoint.class);
            w3.setX(targetX);
            w3.setY(targetY + (targetHeight / 2));
            edge.addChildElement(w3);
        }
    }

    protected BpmnShape findBpmnShape(BaseElement node) {
        Collection<BpmnShape> allShapes = modelInstance.getModelElementsByType(BpmnShape.class);
        Iterator<BpmnShape> iterator = allShapes.iterator();
        while (iterator.hasNext()) {
            BpmnShape shape = iterator.next();
            if (shape.getBpmnElement().equals(node)) {
                return shape;
            }
        } 
        return null;
    }

    protected BpmnEdge findBpmnEdge(BaseElement sequenceFlow) {
        Collection<BpmnEdge> allEdges = modelInstance.getModelElementsByType(BpmnEdge.class);
        Iterator<BpmnEdge> iterator = allEdges.iterator();
        while (iterator.hasNext()) {
            BpmnEdge edge = iterator.next();
            if (edge.getBpmnElement().equals(sequenceFlow)) {
                return edge;
            }
        } 
        return null;
    }

    protected void resizeSubProcess(BpmnShape innerShape) {
        BaseElement innerElement = innerShape.getBpmnElement();
        Bounds innerShapeBounds = innerShape.getBounds();
        ModelElementInstance parent = innerElement.getParentElement();
        while (parent instanceof SubProcess) {
            BpmnShape subProcessShape = findBpmnShape(((SubProcess) (parent)));
            if (subProcessShape != null) {
                Bounds subProcessBounds = subProcessShape.getBounds();
                double innerX = innerShapeBounds.getX();
                double innerWidth = innerShapeBounds.getWidth();
                double innerY = innerShapeBounds.getY();
                double innerHeight = innerShapeBounds.getHeight();
                double subProcessY = subProcessBounds.getY();
                double subProcessHeight = subProcessBounds.getHeight();
                double subProcessX = subProcessBounds.getX();
                double subProcessWidth = subProcessBounds.getWidth();
                double tmpWidth = (innerX + innerWidth) + CommonsAbstractBaseElementBuilder.SPACE;
                double tmpHeight = (innerY + innerHeight) + CommonsAbstractBaseElementBuilder.SPACE;
                if (innerY == subProcessY) {
                    subProcessBounds.setY(subProcessY - CommonsAbstractBaseElementBuilder.SPACE);
                    subProcessBounds.setHeight(subProcessHeight + CommonsAbstractBaseElementBuilder.SPACE);
                }
                if (tmpWidth >= (subProcessX + subProcessWidth)) {
                    double newWidth = tmpWidth - subProcessX;
                    subProcessBounds.setWidth(newWidth);
                }
                if (tmpHeight >= (subProcessY + subProcessHeight)) {
                    double newHeight = tmpHeight - subProcessY;
                    subProcessBounds.setHeight(newHeight);
                }
                innerElement = ((SubProcess) (parent));
                innerShapeBounds = subProcessBounds;
                parent = innerElement.getParentElement();
            } else {
                break;
            }
        } 
    }
}