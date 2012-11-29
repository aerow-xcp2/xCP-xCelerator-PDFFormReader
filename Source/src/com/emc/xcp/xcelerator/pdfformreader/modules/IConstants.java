/**
 * =====================================================================
 * Project: PdfFormReader
 *
 * History (only for major revisions):
 * Date         Author        Reason for revision
 * 2011-02-20   B. MITTAU     Creation
 * 2012-03-18   B. MITTAU     Add contants for PdfFormReader server method
 * =====================================================================
 * Copyright (c) 2012 euroscript Systems
 * =====================================================================
 */
package com.emc.xcp.xcelerator.pdfformreader.modules;

/**
 * Constant used by inputs.
 */
public interface IConstants {

    /**
     * Various representations of TRUE value.
     */
    public static final String[] TRUE_VALUES =
            {"OK", "1", "true", "yes", "y", "oui", "o"};

    /**
     * Default date&time pattern.
     */
    public static final String DATE_TIME_DEFAULT_PATTERN = "dd/mm/yyyy hh:mi:ss";

    /**
     * Vendor.
     */
    public static final String MODULE_VENDOR_STRING = "euroscript Systems";

    /**
     * Name of the server method.
     */
    public static final String SERVER_METHOD_NAME = "PdfFormReaderMethod";

    /**
     * PdfFormReader method parameter: rendition format containing the PDF form to process.
     */
    public static final String ARG_FORMAT = "-format";

    /**
     * PdfFormReader method parameter: Date&Time pattern.
     */
    public static final String ARG_DATE_TIME_PATTERN = "-dateTimePattern";

    /**
     * PdfFormReader method parameter: To turn on/off the clean-up of the PDF form file after extraction.
     */
    public static final String ARG_CLEANUP_AFTER_EXTRACTION = "-cleanupAfterExtraction";

    /**
     * PdfFormReader method parameter: list of field names.
     */
    public static final String ARG_FIELD_NAMES = "-fieldNames";

    /**
     * Separator used in the PdfFormReader method parameter "-fieldNames".
     */
    public static String FIELD_NAMES_SEPARATOR = ",";
    
    /**
     * Standard method parameter "id" (which is implicitly provided by D2 when this server method is used in a lifecycle action).
     */
    public static final String ARG_ID = "-id";

    /**
     * Standard method parameter "docbase_name" (which is implicitly provided by D2 when this server method is used in a lifecycle action).
     */
    public static final String ARG_DOCBASE_NAME = "-docbase_name";

    /**
     * Standard method parameter "user_name" (which is implicitly provided by D2 when this server method is used in a lifecycle action).
     */
    public static final String ARG_USER_NAME = "-user_name";

}
