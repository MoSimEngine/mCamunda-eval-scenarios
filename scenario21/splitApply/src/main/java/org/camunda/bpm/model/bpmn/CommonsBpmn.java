package org.camunda.bpm.model.bpmn;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.camunda.bpm.model.bpmn.impl.BpmnParser;
import org.camunda.bpm.model.xml.Model;
import org.camunda.bpm.model.xml.ModelBuilder;
import org.camunda.bpm.model.xml.impl.util.IoUtil;
public class CommonsBpmn {
    /**
     * the singleton instance of {@link Bpmn}. If you want to customize the behavior of Bpmn,
     * replace this instance with an instance of a custom subclass of {@link Bpmn}.
     */
    public static Bpmn INSTANCE = new Bpmn();

    /**
     * the parser used by the Bpmn implementation.
     */
    protected BpmnParser bpmnParser = new BpmnParser();

    protected final ModelBuilder bpmnModelBuilder;

    /**
     * The {@link Model}
     */
    protected Model bpmnModel;

    /**
     * Allows reading a {@link BpmnModelInstance} from a File.
     *
     * @param file
     * 		the {@link File} to read the {@link BpmnModelInstance} from
     * @return the model read
     * @throws BpmnModelException
     * 		if the model cannot be read
     */
    public static BpmnModelInstance readModelFromFile(File file) {
        return Bpmn.INSTANCE.doReadModelFromFile(file);
    }

    /**
     * Allows reading a {@link BpmnModelInstance} from an {@link InputStream}
     *
     * @param stream
     * 		the {@link InputStream} to read the {@link BpmnModelInstance} from
     * @return the model read
     * @throws ModelParseException
     * 		if the model cannot be read
     */
    public static BpmnModelInstance readModelFromStream(InputStream stream) {
        return Bpmn.INSTANCE.doReadModelFromInputStream(stream);
    }

    /**
     * Allows writing a {@link BpmnModelInstance} to a File. It will be
     * validated before writing.
     *
     * @param file
     * 		the {@link File} to write the {@link BpmnModelInstance} to
     * @param modelInstance
     * 		the {@link BpmnModelInstance} to write
     * @throws BpmnModelException
     * 		if the model cannot be written
     * @throws ModelValidationException
     * 		if the model is not valid
     */
    public static void writeModelToFile(File file, BpmnModelInstance modelInstance) {
        Bpmn.INSTANCE.doWriteModelToFile(file, modelInstance);
    }

    /**
     * Allows writing a {@link BpmnModelInstance} to an {@link OutputStream}. It will be
     * validated before writing.
     *
     * @param stream
     * 		the {@link OutputStream} to write the {@link BpmnModelInstance} to
     * @param modelInstance
     * 		the {@link BpmnModelInstance} to write
     * @throws ModelException
     * 		if the model cannot be written
     * @throws ModelValidationException
     * 		if the model is not valid
     */
    public static void writeModelToStream(OutputStream stream, BpmnModelInstance modelInstance) {
        Bpmn.INSTANCE.doWriteModelToOutputStream(stream, modelInstance);
    }

    /**
     * Allows the conversion of a {@link BpmnModelInstance} to an {@link String}. It will
     * be validated before conversion.
     *
     * @param modelInstance
     * 		the model instance to convert
     * @return the XML string representation of the model instance
     */
    public static String convertToString(BpmnModelInstance modelInstance) {
        return Bpmn.INSTANCE.doConvertToString(modelInstance);
    }

    /**
     * Validate model DOM document
     *
     * @param modelInstance
     * 		the {@link BpmnModelInstance} to validate
     * @throws ModelValidationException
     * 		if the model is not valid
     */
    public static void validateModel(BpmnModelInstance modelInstance) {
        Bpmn.INSTANCE.doValidateModel(modelInstance);
    }

    /**
     * Allows creating an new, empty {@link BpmnModelInstance}.
     *
     * @return the empty model.
     */
    public static BpmnModelInstance createEmptyModel() {
        return Bpmn.INSTANCE.doCreateEmptyModel();
    }

    protected BpmnModelInstance doReadModelFromFile(File file) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            return doReadModelFromInputStream(is);
        } catch (FileNotFoundException e) {
            throw new BpmnModelException(("Cannot read model from file " + file) + ": file does not exist.");
        } finally {
            IoUtil.closeSilently(is);
        }
    }

    protected BpmnModelInstance doReadModelFromInputStream(InputStream is) {
        return this.bpmnParser.parseModelFromStream(is);
    }

    protected void doWriteModelToFile(File file, BpmnModelInstance modelInstance) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(file);
            doWriteModelToOutputStream(os, modelInstance);
        } catch (FileNotFoundException e) {
            throw new BpmnModelException(("Cannot write model to file " + file) + ": file does not exist.");
        } finally {
            IoUtil.closeSilently(os);
        }
    }

    protected void doWriteModelToOutputStream(OutputStream os, BpmnModelInstance modelInstance) {
        // validate DOM document
        doValidateModel(modelInstance);
        // write XML
        IoUtil.writeDocumentToOutputStream(modelInstance.getDocument(), os);
    }

    protected String doConvertToString(BpmnModelInstance modelInstance) {
        // validate DOM document
        doValidateModel(modelInstance);
        // convert to XML string
        return IoUtil.convertXmlDocumentToString(modelInstance.getDocument());
    }

    protected void doValidateModel(BpmnModelInstance modelInstance) {
        this.bpmnParser.validateModel(modelInstance.getDocument());
    }

    protected BpmnModelInstance doCreateEmptyModel() {
        return this.bpmnParser.getEmptyModel();
    }

    /**
     *
     *
     * @return the {@link Model} instance to use
     */
    public Model getBpmnModel() {
        return this.bpmnModel;
    }

    public ModelBuilder getBpmnModelBuilder() {
        return this.bpmnModelBuilder;
    }

    /**
     *
     *
     * @param bpmnModel
     * 		the bpmnModel to set
     */
    public void setBpmnModel(Model bpmnModel) {
        this.bpmnModel = bpmnModel;
    }
}