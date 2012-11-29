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
package com.emc.xcp.xcelerator.pdfformreader.util;

import org.apache.log4j.Logger;

public class StringUtils {

    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(StringUtils.class);

    /**
     * Verify if a string is Empty or Null.
     * @param paramString
     *            String to verify
     * @return boolean true if empty
     */
    public static boolean isEmptyOrNull(final String paramString) {
        return paramString == null || paramString.length() == 0
                || paramString.trim().equalsIgnoreCase("")
                || paramString.trim().equalsIgnoreCase("NULL");
    }

}