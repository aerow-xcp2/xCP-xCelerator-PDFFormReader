/**
 * =====================================================================
 * Project: PdfFormReader
 *
 * History (only for major revisions):
 * Date         Author        Reason for revision
 * 2012-03-18   B. MITTAU     Creation
 * 2012-10-24   J. CHADET     Migration to xCP 2.0
 * =====================================================================
 * Copyright (c) 2012 EMC
 * =====================================================================
 */
package com.emc.xcp.xcelerator.pdfformreader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.documentum.fc.client.DfSingleDocbaseModule;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.emc.xcp.xcelerator.pdfformreader.modules.IConstants;
import com.emc.xcp.xcelerator.pdfformreader.modules.PdfFormReader;
import com.emc.xcp.xcelerator.pdfformreader.modules.params.PdfFormReaderParams;

public final class PdfFormReaderXcp extends DfSingleDocbaseModule implements IConstants {
	/**
	 * This  method reads the PDF Form and update the metadata of the document.
	 * @param session
	 *            Documentum sessions, provided by D2.
	 * @param pIDfSysObject
	 *            dm_sysobject on which the server method has executed. It should correspond a the PDF form.
	 * @param paramLocale
	 *            Locale (fr, en, etc) of the current user session within D2 GUI.
	 * @param paramArgumentParser
	 *            Argument parser, provided by D2, to retrieve all calling parameters.
	 * @throws Exception
	 *             if an error occurs.
	 */
	public void readPdfFormToAttributes( PdfFormReaderParams params ) throws Exception {
		IDfSession session = null;
		try {
			 session = getSession();
			LOG.info("Working on the document: objectId=" + params.getObjectId());
			LOG.info("Working on the document: format=" + params.getFormat());
			LOG.info("Working on the document: DateTimePattern=" + params.getDateTimePattern());
			LOG.info("Working on the document: CleanupAfterExtraction()=" + params.getCleanupAfterExtraction());
			LOG.info("Working on the document: getFieldNames=" + params.getFieldNames());
			// Init PdfFormReader
			final PdfFormReader extractPdf = new PdfFormReader();
			extractPdf.init(session.getSessionManager(), session.getDocbaseName());

			
			if (params.getFieldNames().length > 0) {
				LOG.debug("Number of fields to extract: " + params.getFieldNames().length);
			} else {
				LOG.debug("No field name provided, all form fields will be processed.");
			}

			LOG.debug("### readPdfFormToAttributes ### Start data extraction from the PDF form...");
			extractPdf.readPdfFormToAttributes(params);
			LOG.debug("### readPdfFormToAttributes ### Data extraction done!");

			LOG.info("=== End of server method '" + SERVER_METHOD_NAME + "' ===");

		} catch (DfException dfException) {
			LOG.error("Error in " + SERVER_METHOD_NAME, dfException);
			DfLogger.trace(this, dfException.getStackTraceAsString(), null, null);
			try {
				// If a transaction is pending, abort it
				if (session != null && session.isTransactionActive()) {
					session.abortTrans();
				}
			} catch (DfException dfExc) {
				LOG.error("Error in " + this.getClass().getSimpleName() + " on attempt to abort transaction", dfExc);
				LOG.trace(dfExc.getStackTraceAsString());
			}
			throw dfException;
		} catch (Throwable throwableException) {
			LOG.error("Unexpected error caught in the class '" + this.getClass().getSimpleName() + "': ", throwableException);
			try {
				// If a transaction is pending, abort it
				if (session != null && session.isTransactionActive()) {
					session.abortTrans();
				}
			} catch (DfException dfException) {
				LOG.error("Error in " + this.getClass().getSimpleName() + " on attempt to abort transaction", dfException);
				LOG.trace(dfException.getStackTraceAsString());
			}
			throw new Exception(throwableException);
		}
	}
	
	public List<String> readPdfFormToVariables( PdfFormReaderParams params ) throws Exception {
		IDfSession session = null;
		ArrayList<String> fieldValues = new ArrayList<String>();
		try {
			 session = getSession();
			LOG.info("Working on the document: objectId=" + params.getObjectId());
			LOG.info("Working on the document: format=" + params.getFormat());
			LOG.info("Working on the document: DateTimePattern=" + params.getDateTimePattern());
			LOG.info("Working on the document: CleanupAfterExtraction()=" + params.getCleanupAfterExtraction());
			LOG.info("Working on the document: getFieldNames=" + params.getFieldNames());
			// Init PdfFormReader
			final PdfFormReader extractPdf = new PdfFormReader();
			extractPdf.init(session.getSessionManager(), session.getDocbaseName());

			
			if (params.getFieldNames().length > 0) {
				LOG.debug("Number of fields to extract: " + params.getFieldNames().length);
			} else {
				LOG.debug("No field name provided, all form fields will be processed.");
			}

			LOG.debug("### readPdfFormToAttributes ### Start data extraction from the PDF form...");
			fieldValues = (ArrayList<String>)extractPdf.readPdfFormToVariablesList(params);
			LOG.debug("### readPdfFormToAttributes ### Data extraction done!");

			LOG.info("=== End of server method '" + SERVER_METHOD_NAME + "' ===");

		} catch (DfException dfException) {
			LOG.error("Error in " + SERVER_METHOD_NAME, dfException);
			DfLogger.trace(this, dfException.getStackTraceAsString(), null, null);
			try {
				// If a transaction is pending, abort it
				if (session != null && session.isTransactionActive()) {
					session.abortTrans();
				}
			} catch (DfException dfExc) {
				LOG.error("Error in " + this.getClass().getSimpleName() + " on attempt to abort transaction", dfExc);
				LOG.trace(dfExc.getStackTraceAsString());
			}
			throw dfException;
		} catch (Throwable throwableException) {
			LOG.error("Unexpected error caught in the class '" + this.getClass().getSimpleName() + "': ", throwableException);
			try {
				// If a transaction is pending, abort it
				if (session != null && session.isTransactionActive()) {
					session.abortTrans();
				}
			} catch (DfException dfException) {
				LOG.error("Error in " + this.getClass().getSimpleName() + " on attempt to abort transaction", dfException);
				LOG.trace(dfException.getStackTraceAsString());
			}
			throw new Exception(throwableException);
		}
		return fieldValues;
	}
	
	/**
	 * The logger.
	 */
	private static final Logger LOG = Logger.getLogger(PdfFormReaderXcp.class.getName());

}
