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

/**
 * Exception thrown when required argument is not provided.
 */
public final class ArgumentNotProvidedException extends DfException
{

    /**
     * For serialization purposes.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Name of the argument.
     */
    private final String argumentName;

    /**
     * Implementation of the exception.
     * The name of the missing argument is added to the error message.
     * @param argumentName
     *            : Name of the argument.
     */
    private ArgumentNotProvidedException(final String argumentName) {
        super("Required argument [" + argumentName + "] is not provided");
        this.argumentName = argumentName;
    }

    /**
     * Gets name of the argument whic was missing.
     * @return Name of the argument.
     */
    public String getArgumentName() {
        return argumentName;
    }

    /**
     * This method creates the exception.
     * @param argumentName : Name of the missing argument.
     * @return the exception ArgumentNotProvidedException.
     */
    public static ArgumentNotProvidedException createForMissingArgument(final String argumentName)
    {
        return new ArgumentNotProvidedException(argumentName);
    }

}
