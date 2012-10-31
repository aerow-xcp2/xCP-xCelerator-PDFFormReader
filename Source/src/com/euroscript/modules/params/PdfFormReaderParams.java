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
package com.euroscript.modules.params;

import com.euroscript.util.StringUtils;

/**
 * The class PdfFormReaderParams defines all the parameters
 * to be provided to the BOF module "PdfFormReader".
 */
public class PdfFormReaderParams
{
    /**
     * Default constructor. Initialize array of values.
     */
    public PdfFormReaderParams()
    {
        fieldNames = new String[FIELD_NAMES_MAX_COUNT];
        
    }

    /**
     * @return the objectId
     */
    public final String getObjectId()
    {
        return objectId;
    }

    /**
     * @param objectId
     *            the objectId to set
     */
    public final void setObjectId(final String objectId)
    {
        this.objectId = objectId;
    }

    /**
     * @return the format
     */
    public final String getFormat()
    {
        return format;
    }

    /**
     * @param format
     *            the format to set
     */
    public final void setFormat(final String format)
    {
        this.format = format;
    }

    /**
     * @return the fieldNames array
     */
    public final String[] getFieldNames()
    {
        return fieldNames;
    }

    /**
     * @param fieldNames
     *            the fieldNames array to set
     */
    public final void setFieldNames(String[] fieldNames)
    {
        this.fieldNames = fieldNames;
    }

    /**
     * @param fieldName
     *            to add to fieldNames array.
     */
    public final void appendFieldNames(final String fieldName)
    {
        for (int i = 0; i < fieldNames.length; i++) {
            String name = fieldNames[i];
            if (StringUtils.isEmptyOrNull(name)) {
                fieldNames[i] = fieldName;
                break;
            }
        }
    }

    /**
     * @return the cleanupAfterExtraction
     */
    public final Boolean getCleanupAfterExtraction()
    {
        return cleanupAfterExtraction;
    }

    /**
     * @param cleanupAfterExtraction
     *            the cleanupAfterExtraction to set
     */
    public final void setCleanupAfterExtraction(Boolean cleanupAfterExtraction)
    {
        this.cleanupAfterExtraction = cleanupAfterExtraction;
    }

    /**
     * @param dateTimePattern
     *            : the Date&Time pattern
     */
    public final void setDateTimePattern(String dateTimePattern)
    {
        this.dateTimePattern = dateTimePattern;
    }

    /**
     * @return the Date&Time pattern
     */
    public final String getDateTimePattern()
    {
        return dateTimePattern;
    }

    /**
     * Object ID of the document handling the PDF form.
     */
    private String objectId;

    /**
     * Format of the rendition (or primary content)
     * corresponding to the PDF form.
     */
    private String format;

    /**
     * List of fields to be extracted from the PDF Form.
     */
    private String[] fieldNames;

    /**
     * Datetime pattern used to read dates in the PDF Form.
     */
    private String dateTimePattern;

    /**
     * Enable/Disable the deletion of the PDF form file after the processing.
     */
    private Boolean cleanupAfterExtraction;

    /**
     * Maximum number of fields.
     */
    private final int FIELD_NAMES_MAX_COUNT = 1000;
}
