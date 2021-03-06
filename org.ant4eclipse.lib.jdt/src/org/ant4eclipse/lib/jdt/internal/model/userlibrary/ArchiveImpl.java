/**********************************************************************
 * Copyright (c) 2005-2009 ant4eclipse project team.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Nils Hartmann, Daniel Kasmeroglu, Gerd Wuetherich
 **********************************************************************/
package org.ant4eclipse.lib.jdt.internal.model.userlibrary;

import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.jdt.model.userlibrary.Archive;

import java.io.File;

/**
 * Simple library entry within the user library configuration file.
 * 
 * @author Daniel Kasmeroglu (daniel.kasmeroglu@kasisoft.net)
 */
public class ArchiveImpl implements Archive {

  /** the path */
  private File   _path;

  /** the javadoc */
  private String _javadoc;

  /** the source */
  private File   _source;

  /**
   * Creates an archive entry provding some infos regarding a classpath entry.
   * 
   * @param path
   *          The location of the classes. Maybe a directory or a Jar.
   */
  public ArchiveImpl(File path) {
    this(path, null, null);
  }

  /**
   * Creates an archive entry provding some infos regarding a classpath entry.
   * 
   * @param path
   *          The location of the classes. Maybe a directory or a Jar.
   * @param source
   *          The location of corresponding sources.
   * @param javadoc
   *          The location of the javadocs as an url.
   */
  public ArchiveImpl(File path, File source, String javadoc) {
    Assure.exists("path", path);
    this._path = path;
    setSource(source);
    setJavaDoc(javadoc);
  }

  /**
   * Changes the source entry for this archive.
   * 
   * @param newsource
   *          The new source entry for this archive.
   */
  public void setSource(File newsource) {
    // TODO: Should we log this?
    // if (newsource != null && !newsource.exists()) {
    // LoggerFactory.instance().getLogger().warn(
    // "Source '" + newsource + "' of archive '" + _path
    // + "' does not exist!");
    // }

    this._source = newsource;
  }

  /**
   * Changes the javadoc entry for this archive.
   * 
   * @param newjavadoc
   *          The new javadoc entry.
   */
  public void setJavaDoc(String newjavadoc) {
    // TODO: Should we throw a exceptiion here?
    // if (newjavadoc != null) {
    // Assert.assertTrue(newjavadoc.length() > 0,
    // "javadoc.length() > 0 has to be true");
    // }

    this._javadoc = newjavadoc;
  }

  /**
   * {@inheritDoc}
   */
  public File getPath() {
    return this._path;
  }

  /**
   * {@inheritDoc}
   */
  public File getSource() {
    return this._source;
  }

  /**
   * {@inheritDoc}
   */
  public String getJavaDoc() {
    return this._javadoc;
  }

} /* ENDCLASS */