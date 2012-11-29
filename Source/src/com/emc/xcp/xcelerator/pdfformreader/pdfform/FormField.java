/**
 * =====================================================================
 * Project: PdfFormReader
 *
 * History (only for major revisions):
 * Date         Author        Reason for revision
 * 2011-02-20   B. MITTAU     Creation
 * 2011-03-29   B. MITTAU     Add file method cleanUpFieldName
 * =====================================================================
 * Copyright (c) 2012 euroscript Systems
 * =====================================================================
 */
package com.emc.xcp.xcelerator.pdfformreader.pdfform;

/**
 * This class represent a form field, with its name and its value.
 */
public class FormField {

    /**
     * Field name.
     */
    private String fieldName;

    /**
     * Field value.
     */
    private String fieldValue;

    /**
     * @return the fieldName
     */
    public final String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName
     *            the fieldName to set
     */
    public final void setFieldName(String fieldNameStr) {
        this.fieldName = cleanUpFieldName(fieldNameStr);
    }

    /**
     * @return the fieldValue
     */
    public final String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue
     *            the fieldValue to set
     */
    public final void setFieldValue(final String fieldValueStr) {
        this.fieldValue = fieldValueStr;
    }

    /**
     * Clean-up the name field name by removing prefix (with page number, etc)
     * and brakets.
     * Sample fieldname: "topmostSubform[0].Page1[0].tp_to_address[0]"
     * Sample after clean-up: "tp_to_address"
     * @param fieldValueStr
     *            the full fielname
     * @return the short fieldname, without any prefix and brakets
     */
    private static String cleanUpFieldName(final String fieldValueStr)
    {
        final String LONG_FIELD_NAMES_START = "].";
        final String LONG_FIELD_NAMES_END = "[";
        String cleanFieldName = fieldValueStr;
        int startIndex;
        int endIndex;
        try {
            startIndex = cleanFieldName.lastIndexOf(LONG_FIELD_NAMES_START);
            endIndex = cleanFieldName.lastIndexOf(LONG_FIELD_NAMES_END);
            if ((startIndex > 0) && (endIndex > 0)) {
                startIndex = startIndex + LONG_FIELD_NAMES_START.length();
                cleanFieldName = cleanFieldName.substring(startIndex, endIndex);
            }
        } catch (Exception e) {
            // Do nothing
        }
        return cleanFieldName;
    }
}
