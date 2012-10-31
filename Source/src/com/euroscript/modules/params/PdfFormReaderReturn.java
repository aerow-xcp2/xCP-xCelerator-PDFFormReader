/**
 * =====================================================================
 * Project: PdfFormReader
 *
 * History (only for major revisions):
 * Date         Author        Reason for revision
 * 2011-02-20   B. MITTAU     Creation
 * 2012-03-16   B. MITTAU     Add method 'printResultDebug'
 * =====================================================================
 * Copyright (c) 2012 euroscript Systems
 * =====================================================================
 */
package com.euroscript.modules.params;

import org.apache.log4j.Logger;


/**
 * The class PdfFormReaderReturn defines the data returned by the BOF module "PdfFormReader".
 */
public class PdfFormReaderReturn {

    /**
     * Default constructor. Initialize array of values.
     */
    public PdfFormReaderReturn() {
        fieldValues = new String[FIELD_VALUES_MAX_COUNT];
        fieldNames = new String[FIELD_VALUES_MAX_COUNT];
        numberOfValues = 0;
    }

    /**
     * @return the numberOfValues
     */
    public final int getNumberOfValues() {
        return numberOfValues;
    }

    /**
     * @return the fieldValues
     */
    public final String[] getFieldValues() {
        return fieldValues;
    }

    /**
     * @param fieldValues
     *            the fieldValues to set
     */
    public final void setFieldValues(String[] fieldValues) {
        this.fieldValues = fieldValues;
    }

    /**
     * @param value
     *            to add to fieldValues array.
     */
    public final void appendFieldValues(final String value) {
        fieldValues[numberOfValues] = value;
        numberOfValues++;
    }

    /**
     * @return the fieldNames
     */
    public final String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * @param fieldName
     *            the field name.
     * @param fieldValue
     *            the field value.
     */
    public final void appendField(final String fieldName, final String fieldValue) {
        fieldNames[numberOfValues] = fieldName;
        fieldValues[numberOfValues] = fieldValue;
        numberOfValues++;
    }

    /**
     * Finalize the PdfFormReaderReturn object by reducing the size of the array to the number of values precisely.
     */
    public final void save() {
        String[] newArrayNames = new String[numberOfValues];
        String[] newArrayValues = new String[numberOfValues];
        for (int i = 0; i < numberOfValues; i++) {
            newArrayNames[i] = fieldNames[i];
            newArrayValues[i] = fieldValues[i];
        }
        fieldNames = newArrayNames;
        fieldValues = newArrayValues;
    }

    /**
     * List of values extracted from the PDF Form.
     */
    private String[] fieldValues;

    /**
     * List of field names extracted from the PDF Form.
     */
    private String[] fieldNames;

    /**
     * Current number of values in 'fieldNames' and 'fieldValues' arrays.
     */
    private int numberOfValues;

    /**
     * Maximum number of fields.
     */
    private final int FIELD_VALUES_MAX_COUNT = 1000;

    /**
     * Print the PdfFormRederReturn object using a Logger. The format is a couple fieldName and fieldValue on each line.
     * @param LOG Logger used to print data.
     */
    public void printResultDebug(Logger LOG) {
        String name = "";
        String value = "";
        String couple = "";
        for (int i = 0; i < numberOfValues; i++) {
            name = fieldNames[i];
            value = fieldValues[i];
            couple = "Field name: " + name + "   |   " + "Field value: " + value;
            LOG.debug(couple);
        }
    }

    /**
     * Converts a String array to a single String, which contains the list of String values, separated by the separator.
     * @param stringArray The String array to convert.
     * @param separator The separator.
     * @return a single String which contains the list of String values.
     */
    public static String arrayToString(String[] stringArray, String separator) {
        String result = "";
        if (stringArray.length > 0) {
            result = stringArray[0]; // start with the first element
            for (int i = 1; i < stringArray.length; i++) {
                result = result + separator + stringArray[i];
            }
        }
        return result;
    }
}
