/**
 * =====================================================================
 * Project: PdfFormReader
 *
 * History (only for major revisions):
 * Date         Author        Reason for revision
 * 2011-02-20   B. MITTAU     Creation
 * 2011-03-29   B. MITTAU     Change data extraction method
 *                              to be compatible with more formats
 * =====================================================================
 * Copyright (c) 2012 euroscript Systems
 * =====================================================================
 */
package com.emc.xcp.xcelerator.pdfformreader.pdfform;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.XfaForm;

public class PdfFormExtractor
{
    /**
     * Extracts data from PDF Form file. The data are return as a list of filedName-fieldValue couples. 
     * This method uses the package 'com.itextpdf.text.pdf.XfaForm' of iText library.
     * @param pdfFormFile
     *            the PDF Form file to be processed
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerFactoryConfigurationError
     * @throws TransformerException
     */
    static public List<FormField> extractDataXfa(final String pdfFormFile) 
        throws IOException, ParserConfigurationException, SAXException, TransformerFactoryConfigurationError, TransformerException
    {
        final List<FormField> fieldList = new ArrayList<FormField>();

        final PdfReader reader = new PdfReader(pdfFormFile);
        final XfaForm xfa = new XfaForm(reader);
        Node node = xfa.getDatasetsNode();
        NodeList list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if ("data".equals(list.item(i).getLocalName())) {
                node = list.item(i);
                break;
            }
        }

        LOG.info("ChildNode #---");
        list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            if ("topmostSubform".equals(list.item(i).getLocalName())) {
                node = list.item(i);
                break;
            }
        }
        list = node.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            final String fieldName = list.item(i).getLocalName();
            final String fieldValue = list.item(i).getTextContent();
            final FormField formField = new FormField();
            formField.setFieldName(fieldName);
            formField.setFieldValue(fieldValue);
            fieldList.add(formField);
        }
        return fieldList;
    }

    /**
     * Extracts data from PDF Form file. The data are return as a list of filedName-fieldValue couples.
     * This method uses only the package 'com.itextpdf.text.pdf.PdfReader' of iText library. 
     * @param pdfFormFile the PDF Form file to be processed
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    static public List<FormField> extractData(final String pdfFormFile) 
        throws DocumentException, IOException
    {
        // Initialize the list of field names to be returned
        final List<FormField> fieldList = new ArrayList<FormField>();
        // Create a reader to extract info
        PdfReader reader = new PdfReader(pdfFormFile);
        // Get the fields from the reader (read-only!!!)
        AcroFields form = reader.getAcroFields();
        // Loop over the fields and get info about them
        Set<String> fields = form.getFields().keySet();

        String fieldName = "";
        String fieldValue = "";

        // Loop through all the fields of the form
        for (String key : fields) {
            fieldName = key;
            fieldValue = form.getField(key);
            // Define a FormField object: fieldName-fieldValue couple
            FormField formField = new FormField();
            formField.setFieldName(fieldName);
            formField.setFieldValue(fieldValue);
            // Add the FormField to the list
            fieldList.add(formField);
        }

        // Return the list of field names
        return fieldList;
    }



    /**
     * Logger.
     */
    private static final Logger LOG = Logger.getLogger(PdfFormExtractor.class);

}
