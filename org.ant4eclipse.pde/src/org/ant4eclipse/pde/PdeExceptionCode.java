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

  /** the ant attribute is not set */
  @NLSMessage("Attribute '%s' is not set.")
  public static PdeExceptionCode ANT_ATTRIBUTE_NOT_SET;
  
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
