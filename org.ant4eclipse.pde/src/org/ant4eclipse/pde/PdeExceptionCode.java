/**********************************************************************
 * Copyright (c) 2005-2008 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.pde;

import org.ant4eclipse.core.exception.ExceptionCode;
import org.ant4eclipse.core.nls.NLS;
import org.ant4eclipse.core.nls.NLSMessage;

/**
 * <p>
 * ExceptionCodes for JDT tools.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class PdeExceptionCode extends ExceptionCode {
  
  /** - */
  @NLSMessage("File '%s' doesn't contain a bundle manifest file and will be ignored.")
  public static PdeExceptionCode WARNING_FILE_DOES_NOT_CONTAIN_BUNDLE_MANIFEST_FILE;

  /** - */
  @NLSMessage("File '%s' doesn't contain a feature manifest file and will be ignored.")
  public static PdeExceptionCode WARNING_FILE_DOES_NOT_CONTAIN_FEATURE_MANIFEST_FILE;

  /** the ant attribute is not set */
  @NLSMessage("Attribute '%s' is not set.")
  public static PdeExceptionCode ANT_ATTRIBUTE_NOT_SET;

  /** - */
  @NLSMessage("You have to specify either attribute '%s' or attribute '%s'.")
  public static PdeExceptionCode ANT_ATTRIBUTE_X_OR_Y;

  /** - */
  @NLSMessage("Could not find the feature manifest file for project '%s'.")
  public static PdeExceptionCode FEATURE_MANIFEST_FILE_NOT_FOUND;

  static {
    NLS.initialize(PdeExceptionCode.class);
  }

  /**
   * <p>
   * Creates a new instance of type {@link PdeExceptionCode}.
   * </p>
   * 
   * @param message
   *          the message of the {@link PdeExceptionCode}
   */
  private PdeExceptionCode(final String message) {
    super(message);
  }
}
