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
package org.ant4eclipse.lib.jdt.internal.model.project;


import org.ant4eclipse.lib.core.Assure;
import org.ant4eclipse.lib.jdt.model.project.RawClasspathEntry;

import java.io.File;

/**
 * Encapsulates an entry in a Java project classpath.
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public final class RawClasspathEntryImpl implements RawClasspathEntry {

  /** the path */
  private String  _path;

  /** the entrykind */
  private int     _entryKind;

  /** the outputlocation */
  private String  _outputLocation;

  /** whether or not the entry is exported */
  private boolean _exported = true;

  /**
   * Creates a new instance of type EclipseClasspathEntry
   * 
   * @param entryKind
   * @param path
   */
  public RawClasspathEntryImpl(String entryKind, String path) {
    this(resolveEntryKind(entryKind, path), path, null, false);
  }

  /**
   * Creates a new instance of type EclipseClasspathEntry
   * 
   * @param entryKind
   * @param path
   * @param output
   */
  public RawClasspathEntryImpl(String entryKind, String path, String output) {
    this(resolveEntryKind(entryKind, path), path, output, false);
  }

  /**
   * Creates a new instance of type EclipseClasspathEntry
   * 
   * @param entryKind
   * @param path
   * @param exported
   */
  public RawClasspathEntryImpl(String entryKind, String path, boolean exported) {
    this(resolveEntryKind(entryKind, path), path, null, exported);
  }

  /**
   * Creates a new instance of type EclipseClasspathEntry
   * 
   * @param entryKind
   * @param path
   * @param output
   * @param exported
   */
  public RawClasspathEntryImpl(String entryKind, String path, String output, boolean exported) {
    this(resolveEntryKind(entryKind, path), path, output, exported);
  }

  /**
   * Creates a new instance of type EclipseClasspathEntry
   * 
   * @param entryKind
   * @param path
   */
  public RawClasspathEntryImpl(int entryKind, String path) {
    this(entryKind, path, null, false);
  }

  /**
   * Creates a new instance of type EclipseClasspathEntry
   * 
   * @param entryKind
   * @param path
   * @param output
   * @param exported
   */
  public RawClasspathEntryImpl(int entryKind, String path, String output, boolean exported) {
    Assure.notNull(path);

    this._entryKind = entryKind;
    this._path = path;
    this._outputLocation = output;
    this._exported = exported;

    if ((this._entryKind == CPE_SOURCE) || (this._entryKind == CPE_OUTPUT)) {
      if (this._path != null) {
        this._path = this._path.replace('/', File.separatorChar);
        this._path = this._path.replace('\\', File.separatorChar);
      }
      if (this._outputLocation != null) {
        this._outputLocation = this._outputLocation.replace('/', File.separatorChar);
        this._outputLocation = this._outputLocation.replace('\\', File.separatorChar);
      }
    }
  }

  // public boolean isProjectRelative() {
  // return false;
  // }
  //
  // public EclipseProject getDeclaringEclipseProject() {
  // return null;
  // }
  //
  // public EclipseProject getReferencedEclipseProject() {
  // return null;
  // }
  //
  // public String getProjectRelativePath() {
  // return null;
  // }

  /**
   * {@inheritDoc}
   */
  public int getEntryKind() {
    return this._entryKind;
  }

  /**
   * {@inheritDoc}
   */
  public String getPath() {
    return this._path;
  }

  /**
   * {@inheritDoc}
   */
  public String getOutputLocation() {
    return this._outputLocation;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasOutputLocation() {
    return this._outputLocation != null;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isExported() {
    return this._exported;
  }

  /**
   * Determines the type of classpath entry.
   * 
   * @param entryKind
   *          A textual representation of a classpath type.
   * @param path
   *          A path which content depends on the entry kind.
   * 
   * @return A numerical information specifying the entry kind.
   */
  private static int resolveEntryKind(String entryKind, String path) {
    Assure.notNull(entryKind);
    Assure.notNull(path);

    if ("con".equals(entryKind)) {
      return CPE_CONTAINER;
    } else if ("lib".equals(entryKind)) {
      return CPE_LIBRARY;
    } else if ("src".equals(entryKind) && path.startsWith("/")) {
      return CPE_PROJECT;
    } else if ("src".equals(entryKind) && !path.startsWith("/")) {
      return CPE_SOURCE;
    } else if ("var".equals(entryKind)) {
      return CPE_VARIABLE;
    } else if ("output".equals(entryKind)) {
      return CPE_OUTPUT;
    }

    return -1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("[EclipseClasspathEntry:");
    buffer.append(" path: ");
    buffer.append(this._path);
    buffer.append(" entryKind: ");
    buffer.append(this._entryKind);
    buffer.append(" outputLocation: ");
    buffer.append(this._outputLocation);
    buffer.append(" exported: ");
    buffer.append(this._exported);
    buffer.append("]");
    return buffer.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null) {
      return false;
    }
    if (o.getClass() != getClass()) {
      return false;
    }
    RawClasspathEntryImpl castedObj = (RawClasspathEntryImpl) o;
    return ((this._path == null ? castedObj._path == null : this._path.equals(castedObj._path))
        && (this._entryKind == castedObj._entryKind)
        && (this._outputLocation == null ? castedObj._outputLocation == null : this._outputLocation
            .equals(castedObj._outputLocation)) && (this._exported == castedObj._exported));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int hashCode = 1;
    hashCode = 31 * hashCode + (this._path == null ? 0 : this._path.hashCode());
    hashCode = 31 * hashCode + this._entryKind;
    hashCode = 31 * hashCode + (this._outputLocation == null ? 0 : this._outputLocation.hashCode());
    hashCode = 31 * hashCode + (this._exported ? 1231 : 1237);
    return hashCode;
  }
}