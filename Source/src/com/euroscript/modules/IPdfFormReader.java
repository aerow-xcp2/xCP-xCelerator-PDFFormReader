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
package com.euroscript.modules;

import com.documentum.fc.common.DfException;
import com.euroscript.modules.params.PdfFormReaderParams;
import com.euroscript.modules.params.PdfFormReaderReturn;

public interface IPdfFormReader extends IConstants {
    void readPdfFormToAttributes(final PdfFormReaderParams params) throws DfException;
    PdfFormReaderReturn readPdfFormToVariables(final PdfFormReaderParams params) throws DfException;
}
