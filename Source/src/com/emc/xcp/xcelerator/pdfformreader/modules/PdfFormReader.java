/**
 * =====================================================================
 * Project: PdfFormReader
 *
 * History (only for major revisions):
 * Date         Author        Reason for revision
 * 2011-02-20   B. MITTAU     Creation
 * =====================================================================
 * Copyright (c) 2012 euroscript Systems
 * =====================================================================
 */
package com.emc.xcp.xcelerator.pdfformreader.modules;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfType;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfTime;
import com.documentum.fc.impl.util.StringUtil;
import com.emc.xcp.xcelerator.pdfformreader.modules.params.PdfFormReaderParams;
import com.emc.xcp.xcelerator.pdfformreader.modules.params.PdfFormReaderReturn;
import com.emc.xcp.xcelerator.pdfformreader.pdfform.FormField;
import com.emc.xcp.xcelerator.pdfformreader.pdfform.PdfFormExtractor;
import com.emc.xcp.xcelerator.pdfformreader.util.StringUtils;
import com.itextpdf.text.DocumentException;

public class PdfFormReader extends DfSingleDocbaseModule implements IDfModule, IPdfFormReader {
    /**
     * One of the main methods of "PDF Form Reader" module. This method reads the PDF Form and update the metadata of the document.
     * @param params
     *            List of parameters
     */
    public void readPdfFormToAttributes(final PdfFormReaderParams params) throws DfException {
        LOG.info("=== Enter in module '" + MODULE_NAME + "' ===");
        try {
            // Initialization of the module (retrieval of calling parameters, and Documentum session)
            initModule(params);
            // Proceed with the data extraction
            process();
            // Update the metadata of the document
            updateDocumentMetadata();
            // Clean up temporay file
            cleanUp();
        } catch (final Exception exc) {
            LOG.error("ERROR during the processing.", exc);
        }
        finally {
            releaseSession(session);
        }
        LOG.info("=== End of module '" + MODULE_NAME + "' ===");
    }

    /**
     * One of the main methods of "PDF Form Reader" module. This method reads the PDF Form and returns the extracted data (fields names and fields values).
     * @param params
     *            List of parameters
     * @return an object PdfFormReaderReturn containing the data extracted for the PDF form
     */
    public PdfFormReaderReturn readPdfFormToVariables(final PdfFormReaderParams params) throws DfException {
        LOG.info("=== Enter in module '" + MODULE_NAME + "' ===");
        PdfFormReaderReturn extractedData = new PdfFormReaderReturn();
        try {
            // Initialization of the module (retrieval of calling parameters, and Documentum session)
            initModule(params);
            // Proceed with the data extraction
            process();
            // Prepare the object PdfFormReaderReturn containing the data extracted for the PDF form
            extractedData = prepareModuleResponse();
            // Clean up temporay file
            cleanUp();
            LOG.info("Number of values returned by the module: " + extractedData.getNumberOfValues());
        } catch (final Exception exc) {
            LOG.error("ERROR during the processing.", exc);
        }
        finally {
            releaseSession(session);
        }
        LOG.info("=== End of module '" + MODULE_NAME + "' ===");
        return extractedData;
    }
    
    /**
     * One of the main methods of "PDF Form Reader" module. This method reads the PDF Form and returns the extracted data (fields names and fields values).
     * @param params
     *            List of parameters
     * @return an object PdfFormReaderReturn containing the data extracted for the PDF form
     */
    public List<String> readPdfFormToVariablesList(final PdfFormReaderParams params) throws DfException {
        LOG.info("=== Enter in module '" + MODULE_NAME + "' ===");
        ArrayList<String> extractedDataList = new ArrayList<String>();
        try {
            // Initialization of the module (retrieval of calling parameters, and Documentum session)
            initModule(params);
            // Proceed with the data extraction
            process();
            // Prepare the object PdfFormReaderReturn containing the data extracted for the PDF form
            extractedDataList = (ArrayList<String>)prepareModuleResponseList();
            // Clean up temporay file
            cleanUp();
            LOG.info("Number of values returned by the module: " + extractedDataList.size());
        } catch (final Exception exc) {
            LOG.error("ERROR during the processing.", exc);
        }
        finally {
            releaseSession(session);
        }
        LOG.info("=== End of module '" + MODULE_NAME + "' ===");
        return extractedDataList;
    }

    /**
     * Initialization of the Module: retrieval of calling parameters, and Documentum session.
     * @param params
     *            List of parameters
     * @throws DfException
     *             in case of any error on Documentum side
     */
    private void initModule(final PdfFormReaderParams params) throws DfException {
        // Get all the provided parameters
        objectId = getRequiredArgument(params.getObjectId(), "params.objectId");
        format = params.getFormat();
        fieldNames = params.getFieldNames();
        LOG.debug("fieldNames.length=" + fieldNames.length);

        dateTimePattern = params.getDateTimePattern();
        if (StringUtils.isEmptyOrNull(dateTimePattern)) {
            dateTimePattern = DATE_TIME_DEFAULT_PATTERN;
        }
        cleanupAfterExtraction = params.getCleanupAfterExtraction();

        LOG.info("Working on the document: objectId=" + objectId);
        String formatMessage;
        if (StringUtils.isEmptyOrNull(format)) {
            formatMessage = "params.format is empty. Primary content will be used.";
        } else {
            formatMessage = "format=" + format;
        }
        LOG.info("PDF Form format: " + formatMessage);
        LOG.info("List of fields to be read: fieldNames=" + fieldNames);
        LOG.info("List separator: fieldNamesSeparator=" + fieldNamesSeparator);

        whiteListfieldNames = arrayToArrayList(fieldNames);
        LOG.debug("Number of fields to be read: " + whiteListfieldNames.size());

        LOG.info("Date & time pattern: " + dateTimePattern);
        LOG.info("Cleanup after extraction: " + cleanupAfterExtraction);

        // Retrieve Documentum session
        session = getSession();
        final String docbaseName = session.getDocbaseName();
        LOG.info("docbaseName=" + docbaseName);
        LOG.info("docbaseId=" + session.getDocbaseId());
    }

    /**
     * Clean up temporay file.
     */
    private void cleanUp() {
        if (cleanupAfterExtraction) {
            LOG.info("Temporary file cleaning ...");
            final File tempFile = new File(pdfFormFile);
            if (tempFile.delete()) {
                LOG.info("File deleted: " + pdfFormFile);
            } else {
                LOG.info("Unable to delete file: " + pdfFormFile);
            }
        } else {
            String msg = "Cleanup after extraction is set to false. " + "PDF form file is not deleted: " + pdfFormFile;
            LOG.info(msg);
        }
    }

    /**
     * Main method, in charge of the data extraction.
     * @throws DfException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    private void process() throws DfException, DocumentException, IOException {
        document = (IDfSysObject) session.getObject(new DfId(objectId));
        final String docName = document.getString("object_name");
        LOG.info("docName=" + docName);

        String renditionFormat = format;
        if (StringUtils.isEmptyOrNull(format)) {
            renditionFormat = document.getContentType();
        }

        pdfFormFile = document.getFileEx(null, renditionFormat, 0, false);
        LOG.info("Rendition with format '" + renditionFormat + "' has been exported to: " + pdfFormFile);

        try {
            LOG.info("Extraction of PdfForm data with iText ...");
            // Extract data from PDF Form file using iText library
            fieldList = PdfFormExtractor.extractData(pdfFormFile);

        } catch (final TransformerFactoryConfigurationError e) {
            LOG.error("ERROR during the extraction of data with iText", e);
        }

        final int nbFields = fieldList.size();
        LOG.info("Number of fields extracted: " + nbFields);

        if (whiteListfieldNames.size() > 0) {
            // Only process the fields set in the white list
            fieldList = filterAndReOrderFieldList(fieldList, whiteListfieldNames);
        } else {
            // If the white list is empty, it means that ALL PDF form fields
            // must be retrieved. No nee to filter the list of fields.
        }
    }

    /**
     * Filter the list of fields processed by the module according to a "white list". The returned list is re-ordered like the "white list". Unknown fields are ignored (not retained in the final
     * list).
     * @param fieldList
     *            : the full list of fields read from PDF Form.
     * @param whiteList
     *            : the list of field names to be kept.
     * @return the updated list.
     * @throws DfException
     */
    private final List<FormField> filterAndReOrderFieldList(final List<FormField> fieldList, final List<String> whiteList) throws DfException {
        List<FormField> newFieldList = new ArrayList<FormField>();
        final Iterator<String> fieldsIter = whiteList.iterator();

        try {
            while (fieldsIter.hasNext()) {
                final String fieldName = fieldsIter.next();
                String fieldValue = getFieldValue(fieldList, fieldName);
                if (fieldValue != null) {
                    // This field exist
                    // => Add it to the filtered list
                    FormField field = new FormField();
                    field.setFieldName(fieldName);
                    field.setFieldValue(fieldValue);
                    newFieldList.add(field);
                } else {
                    // This field is unknown => Ignore it
                }
            }
        } catch (final Exception exp) {
            // Any technical error. Do nothing;
        }
        return newFieldList;
    }

    /**
     * Update the metadata of the document with the data extracted from the PDF Form.
     * @throws DfException
     */
    private final void updateDocumentMetadata() throws DfException {
        final Iterator<FormField> fieldsIter = fieldList.iterator();
        int i = 0;

        while (fieldsIter.hasNext()) {
            final FormField field = fieldsIter.next();
            final String fieldName = field.getFieldName();
            final String fieldValue = field.getFieldValue();

            LOG.debug("Field #" + i + ": " + fieldName + "=" + fieldValue);
            if (!(fieldName == null)) {
                final String attrName = fieldName;
                setAttrObject(document, attrName.toLowerCase(), fieldValue, dateTimePattern);
            } else {
                LOG.debug("=> Skip 'null' field");
            }
            i++;
        }
        LOG.debug("Save document ...");
        document.save();
        LOG.debug("Metadata updated !!");
    }

    /**
     * Fill the list of values returned by the module with the data extracted from the PDF Form.
     * @return the PdfFormReaderReturn object to be returned by the module.
     */
    private PdfFormReaderReturn prepareModuleResponse() {
        PdfFormReaderReturn returnObject = new PdfFormReaderReturn();
        final Iterator<FormField> fieldsIter = fieldList.iterator();
        int i = 0;

        while (fieldsIter.hasNext()) {
            final FormField field = fieldsIter.next();
            final String fieldName = field.getFieldName();
            final String fieldValue = field.getFieldValue();
            LOG.debug("Field #" + i + ": " + fieldName + "=" + fieldValue);

            returnObject.appendField(fieldName, fieldValue);
            i++;
        }
        returnObject.save();
        LOG.debug("Object is ready !!");
        return returnObject;
    }
    
    /**
     * Fill the list of values returned by the module with the data extracted from the PDF Form.
     * @return the PdfFormReaderReturn object to be returned by the module.
     */
    private List<String> prepareModuleResponseList() {
        ArrayList<String> returnObject = new ArrayList<String>();
        final Iterator<FormField> fieldsIter = fieldList.iterator();
        int i = 0;

        while (fieldsIter.hasNext()) {
            final FormField field = fieldsIter.next();
            final String fieldName = field.getFieldName();
            final String fieldValue = field.getFieldValue();
            LOG.debug("Field #" + i + ": " + fieldName + "=" + fieldValue);

            returnObject.add(fieldValue);
            i++;
        }
        return returnObject;
    }

    /**
     * Converts an array of strings to a List of strings.
     * @param fieldNames
     *            : String array to be converted to a List of strings.
     * @return the List of strings.
     */
    private List<String> arrayToArrayList(String[] fieldNames) {
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < fieldNames.length; i++) {
            if (!StringUtils.isEmptyOrNull(fieldNames[i])) {
                list.add(fieldNames[i]);
            }
        }
        return list;
    }

    /**
     * Get the value of a field, based on its name.
     * @param fieldList
     *            : the list of fields (name and value pairs)
     * @param fieldName
     *            : name of the field
     * @return the value of the field 'fielNmae', or null if the field is not found in the list.
     */
    private String getFieldValue(List<FormField> fieldList, String fieldName) {
        if (fieldName == null) {
            return null;
        } else {
            String value = null;
            final Iterator<FormField> fieldsIter = fieldList.iterator();

            try {
                while (fieldsIter.hasNext() && (value == null)) {
                    final FormField field = fieldsIter.next();
                    if (fieldName.equals(field.getFieldName())) {
                        value = field.getFieldValue();
                    }
                }
            } catch (final Exception exp) {
                // Any technical error. Do nothing.
            }
            return value;
        }
    }

    /**
     * Generic method to get an attribute from an object without it type.
     * @param attrName
     *            The name of the attribute to update.
     * @param attrValue
     *            The value to set.
     * @param pattern
     *            The date and time patterne to use for TIME attributes.
     * @throws DfException
     *             In case of technical error.
     */
    private void setAttrObject(final IDfSysObject document, final String attrName, final String attrValue, final String pattern) throws DfException {
        try {
            // Get the attribute type.
            final int type = document.getAttrDataType(attrName);
            // First if we work on a repeating attribute.
            if (document.isAttrRepeating(attrName)) {
                // Repeating attributes not managed
                LOG.warn("Repeating attributes are not managed. Skip attribute '" + attrName + "'");
            }
            // Single attribute.
            else {
                setSingleAttr(document, type, attrName, attrValue, pattern);
            }

            // Any technical error.
        } catch (final DfException exp) {
            String msg = "Unknown error occurs during the processing" + " of attribute '" + attrName + "' with value '" + attrValue + "'. " + "Exception: " + exp.getMessage();
            LOG.warn(msg);
        }
    }

    /**
     * Set a single attribute in the object.
     * @param doc
     *            The document to be updated.
     * @param type
     *            The type of the attribute.
     * @param attrName
     *            The name of the attribute to set.
     * @param value
     *            The value to set.
     * @param pattern
     *            The date&time pattern for TIME attributes.
     * @throws DfException
     *             In case of technical error.
     */
    private void setSingleAttr(final IDfSysObject doc, final int type, final String attrName, final String value, final String pattern) throws DfException {
        try {
            switch (type) {
            // Type boolean
            case IDfType.DF_BOOLEAN:
                doc.setBoolean(attrName, getBooleanValue(value, TRUE_VALUES));
                break;
            // Type double
            case IDfType.DF_DOUBLE:
                // when empty string, use 0.0
                if ("".equals(value)) {
                    doc.setDouble(attrName, Double.valueOf(0));
                } else {
                    doc.setDouble(attrName, new Double(value));
                }
                break;
            // Type id
            case IDfType.DF_ID:
                if ("".equals(value)) {
                    doc.setId(attrName, new DfId(DfId.DF_NULLID_STR));
                } else {
                    doc.setId(attrName, new DfId(value));
                }
                break;
            // Type integer
            case IDfType.DF_INTEGER:
                // when empty string, use 0
                if ("".equals(value)) {
                    doc.setInt(attrName, Integer.valueOf(0));
                } else {
                    doc.setInt(attrName, Integer.valueOf(value.replaceAll(" ", "")));
                }
                break;
            // Type String, no transformation to perform
            case IDfType.DF_STRING:
                doc.setString(attrName, value);
                break;
            // Type Time
            case IDfType.DF_TIME:
                // when empty string, use 'nulldate'
                if ("".equals(value)) {
                    doc.setTime(attrName, DfTime.DF_NULLDATE);
                } else {
                    doc.setTime(attrName, new DfTime(value, pattern));
                }
                break;
            // Other type, throws an error.
            default:
                String msg = "Type '" + type + "' for attribute '" + attrName + "' is unknown.";
                LOG.warn(msg);
            }
            // Any technical error.
        } catch (final DfException exp) {
            String msg = "Unknown error occurs during the update" + " of attribute '" + attrName + "' with value '" + value + "'. Exception: " + exp.getMessage();
            LOG.warn(msg);
        }
    }

    /**
     * Convert a String value to boolean, according to a list of values representing True.
     * @param valuetoTest
     * @param trueValues
     * @return
     */
    private final boolean getBooleanValue(final String valuetoTest, final String[] trueValues) {
        // Initialize the returned value
        boolean isTrue = false;

        if (!StringUtil.isEmptyOrNull(valuetoTest)) {

            // The value is not null,
            // Check if it equals of one value from values
            for (int i = 0; i < trueValues.length; i++) {
                // If the parameter "valuetoTest" is equal to one of the
                // list elements (ignoring the case), then the value is TRUE
                if (valuetoTest.trim().equalsIgnoreCase(trueValues[i])) {
                    isTrue = true;
                    break;
                }
            }
        } else {
            // The value is null or empty, return False
            return false;
        }
        return isTrue;
    }

    /**
     * Validates argument is provided and returns value if it is. Exception will be thrown otherwise, telling that parameteter specified with "label" has not been provided. Empty string is treated as
     * a non-provided value.
     * @param value
     *            : the name of argument.
     * @param label
     *            : the label of the argument (for error message).
     * @return the argument value.
     * @throws ArgumentNotProvidedException
     *             in case of missing argum
     */
    private String getRequiredArgument(final String value, final String label) throws ArgumentNotProvidedException {
        assertProvided(value, label);
        return value;
    }

    /**
     * Validates argument is provided, exception will be thrown otherwise, telling that parameteter spicified with "label" has not been provided. Empty string is treated as a non-provided value.
     * @param value
     *            : the name of argument.
     * @param label
     *            : the label of the argument (for error message).
     * @throws ArgumentNotProvidedException
     *             in case of missing argument.
     */
    private void assertProvided(final String value, final String label) throws ArgumentNotProvidedException {
        if (StringUtil.isEmptyOrNull(value)) {
            throw ArgumentNotProvidedException.createForMissingArgument(label);
        }
    }

    public void cleanupResources() throws DfServiceException {
        // TODO Auto-generated method stub
    }

    public String getName() {
        return MODULE_NAME;
    }

    public IDfSession getSession(final String arg0) throws DfServiceException {
        IDfSessionManager sMgr = null;
        IDfSession session = null;
        final String docbaseName = getDocbaseName();
        sMgr = getSessionManager();
        session = sMgr.getSession(docbaseName);
        return session;
    }

    public String getVendorString() {
        return MODULE_VENDOR_STRING;
    }

    public String getVersion() {
        return MODULE_VERSION;
    }

    public boolean isCompatible(final String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean supportsFeature(final String arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(com.emc.xcp.xcelerator.pdfformreader.modules.PdfFormReader.class.getName());

    /**
     * Module name.
     */
    private final String MODULE_NAME = "PdfFormReader";

    /**
     * Module version.
     */
    private final String MODULE_VERSION = "1.0";

    /**
     * Documentum session.
     */
    private IDfSession session;

    /**
     * Object ID of the document handling the PDF form.
     */
    private String objectId;

    /**
     * Format of the rendition (or primary content) corresponding to the PDF form.
     */
    private String format;

    /**
     * List of fields to be extracted from the PDF Form.
     */
    private String[] fieldNames;

    /**
     * Seprator used in the list 'fieldNames'.
     */
    private String fieldNamesSeparator;

    /**
     * List of fields to be extracted from the PDF Form.
     */
    private List<String> whiteListfieldNames;

    /**
     * Datetime pattern.
     */
    private String dateTimePattern;

    /**
     * Enable/Disable the demetion of the PDF form file after the processing.
     */
    private Boolean cleanupAfterExtraction;

    /**
     * List of form fields (name + value).
     */
    private List<FormField> fieldList = new ArrayList<FormField>();

    /**
     * Full path to the PDF Form file.
     */
    private String pdfFormFile;

    /**
     * PDF Form document.
     */
    private IDfSysObject document;
}
