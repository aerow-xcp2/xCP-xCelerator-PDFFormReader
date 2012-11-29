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

import com.documentum.fc.common.DfException;
import com.emc.xcp.xcelerator.pdfformreader.modules.params.PdfFormReaderParams;
import com.emc.xcp.xcelerator.pdfformreader.modules.params.PdfFormReaderReturn;

public interface IPdfFormReader extends IConstants {
    void readPdfFormToAttributes(final PdfFormReaderParams params) throws DfException;
    PdfFormReaderReturn readPdfFormToVariables(final PdfFormReaderParams params) throws DfException;
}
