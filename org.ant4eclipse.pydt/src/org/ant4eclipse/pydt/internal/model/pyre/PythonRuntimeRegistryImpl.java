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
package org.ant4eclipse.pydt.internal.model.pyre;

import org.ant4eclipse.core.Assert;
import org.ant4eclipse.core.Lifecycle;
import org.ant4eclipse.core.ant.ExtendedBuildException;
import org.ant4eclipse.core.data.Version;
import org.ant4eclipse.core.logging.A4ELogging;
import org.ant4eclipse.core.util.Utilities;

import org.ant4eclipse.pydt.model.PythonInterpreter;
import org.ant4eclipse.pydt.model.pyre.PythonRuntime;
import org.ant4eclipse.pydt.model.pyre.PythonRuntimeRegistry;
import org.apache.tools.ant.BuildException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a registry for {@link PythonRuntime} instances.
 * 
 * @author Daniel Kasmeroglu (Daniel.Kasmeroglu@Kasisoft.net)
 */
public class PythonRuntimeRegistryImpl implements PythonRuntimeRegistry, Lifecycle {

  private static final String        PROP_INTERPRETER            = "interpreter.";

  private static final String        MSG_CANONICALFILE           = "Failed to get a canonical filesystem location from the path '%s' !";

  private static final String        MSG_DUPLICATERUNTIME        = "An attempt has been made to register a python runtime using the id '%s' with different locations: '%s' <-> '%s' !";

  private static final String        MSG_FAILEDLAUNCHING         = "Failed to launch executable '%s' (returncode %d).\nOutput:\n%sError:\n%s";

  private static final String        MSG_FAILEDTOREADOUTPUT      = "Failed to read the output. Cause: %s";

  private static final String        MSG_INVALIDDEFAULTID        = "A python runtime with the id '%s' needs to be registered first !";

  private static final String        MSG_MISSINGEXECUTABLES      = "The python properties file 'python.properties' lacks executable definitions for key '%s' !";

  private static final String        MSG_MISSINGPYTHONPROPERTIES = "The python properties file 'python.properties' is not available on the classpath (org/ant4eclipse/pydt) !";

  private static final String        MSG_NODEFAULTRUNTIME        = "A default python runtime could not be determined !";

  private static final String        MSG_REGISTEREDRUNTIME       = "Registered runtime with id '%s' for the location '%s'.";

  private static final String        MSG_REPEATEDREGISTRATION    = "A python runtime with the id '%s' and the location '%s' has been registered multiple times !";

  private static final String        MSG_UNSUPPORTEDRUNTIME      = "The python installation with the id '%s' and the location '%s' is not supported !";

  private static final String        MARKER_BEGIN                = "ANT4ECLIPSE-BEGIN";

  private static final String        MARKER_END                  = "ANT4ECLIPSE-END";

  private static final String        NAME_SITEPACKAGES           = "site-packages";

  private Map<String, PythonRuntime> _runtimes                   = new Hashtable<String, PythonRuntime>();

  private String                     _defaultid                  = null;

  private File                       _pythonlister               = null;

  private File                       _listerdir                  = null;

  private File                       _currentdir                 = null;

  private PythonInterpreter[]        _interpreters               = null;

  private boolean                    _initialised                = false;

  /**
   * Tries to determine the location of a python interpreter.
   * 
   * @param location
   *          The location of a python installation. Not <code>null</code>.
   * 
   * @return The filesystem location of an interpreter or <code>null</code> if none could be found.
   */
  private File lookupInterpreter(final File location) {
    for (final PythonInterpreter interpreter : _interpreters) {
      final File result = interpreter.lookup(location);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public boolean hasRuntime(final String id) {
    Assert.nonEmpty(id);
    return _runtimes.containsKey(id);
  }

  /**
   * {@inheritDoc}
   */
  public void registerRuntime(final String id, File location, final boolean sitepackages) {

    Assert.nonEmpty(id);
    Assert.notNull(location);

    try {
      location = location.getCanonicalFile();
    } catch (IOException ex) {
      throw new ExtendedBuildException(MSG_CANONICALFILE, ex, location);
    }

    final PythonRuntime existing = getRuntime(id);
    if (existing != null) {

      // check the current setting

      if (!location.equals(existing.getLocation())) {
        // same id for different locations is not allowed
        throw new ExtendedBuildException(MSG_DUPLICATERUNTIME, id, existing.getLocation(), location);
      } else {
        // same record, so skip this registration while creating a message only
        A4ELogging.debug(MSG_REPEATEDREGISTRATION, id, location);
        return;
      }

    }

    // register the new runtime but we need to identify the corresponding libraries
    final File interpreter = lookupInterpreter(location);
    if (interpreter == null) {
      throw new ExtendedBuildException(MSG_UNSUPPORTEDRUNTIME, id, location);
    }

    // launch the python lister script to access the python path
    StringBuffer output = new StringBuffer();
    StringBuffer error = new StringBuffer();
    int result = Utilities.execute(interpreter, output, error, _pythonlister.getAbsolutePath());
    if (result != 0) {
      // returncode is not 0
      A4ELogging.debug(MSG_FAILEDLAUNCHING, interpreter, Integer.valueOf(result), output, error);
      throw new ExtendedBuildException(MSG_UNSUPPORTEDRUNTIME, id, location);
    }

    String[] extraction = extractOutput(output.toString(), sitepackages);
    if (extraction == null) {
      // returncode is 0
      A4ELogging.debug(MSG_FAILEDLAUNCHING, interpreter, Integer.valueOf(result), output, error);
      throw new ExtendedBuildException(MSG_UNSUPPORTEDRUNTIME, id, location);
    }

    // load version number and library records
    final Version version = new Version(extraction[0]);
    final File[] libs = new File[extraction.length - 1];
    for (int i = 0; i < libs.length; i++) {
      libs[i] = new File(extraction[i + 1]);
    }

    final PythonRuntime newruntime = new PythonRuntimeImpl(id, location, version, libs);
    A4ELogging.debug(MSG_REGISTEREDRUNTIME, id, location);
    _runtimes.put(id, newruntime);

  }

  /**
   * This function parses the output and returns the output as a list of strings.
   * 
   * @param content
   *          The output that has to be parsed. Neither <code>null</code> nor empty.
   * @param sitepackages
   *          <code>true</code> <=> Enable support for site packages on the runtime.
   * 
   * @return A list of strings containing the runtime information or <code>null</code> in case of a failure.
   */
  private String[] extractOutput(final String content, final boolean sitepackages) {
    List<String> list = new ArrayList<String>();
    boolean collect = false;
    boolean first = true;
    BufferedReader reader = new BufferedReader(new StringReader(content));
    try {
      String line = reader.readLine();
      while (line != null) {
        line = line.trim();
        if (MARKER_BEGIN.equals(line)) {
          collect = true;
        } else if (MARKER_END.equals(line)) {
          collect = true;
        } else if (collect) {
          if (first) {
            // the first line provides the versioning information
            list.add(line);
            first = false;
          } else {
            // the brackets are just a security precaution just for the case that a path
            // starts or ends with whitespace characters
            int open = line.indexOf('[');
            int close = line.lastIndexOf(']');
            line = line.substring(open + 1, close);
            if (!isHiddenDir(line, sitepackages)) {
              list.add(line);
            }
          }
        }
        line = reader.readLine();
      }
      if (list.size() < 2) {
        // there must be at least the version information and one directory
        return null;
      } else {
        return list.toArray(new String[list.size()]);
      }
    } catch (IOException ex) {
      A4ELogging.debug(MSG_FAILEDTOREADOUTPUT, ex.getMessage());
      return null;
    }
  }

  /**
   * Returns <code>true</code> if the supplied directory path is considered to be hidden. This is the case when the path
   * is just a symbol (f.e. __classpath__ under jython), the path is the working directory of our python script or the
   * path is the current directory.
   * 
   * @param dir
   *          The potential directory used to be tested. Neither <code>null</code> nor empty.
   * @param sitepackages
   *          <code>true</code> <=> Enable support for site packages on the runtime.
   * 
   * @return <code>true</code> <=> The supplied path is not a valid part of the runtime and shall be hidden for that
   *         reason.
   * 
   * @throws IOException
   *           Path calculation failed for some reason.
   */
  private boolean isHiddenDir(final String dir, final boolean sitepackages) throws IOException {
    File file = new File(dir);
    if (!file.exists()) {
      // this directory does not exist (f.e. __classpath__ symbols provided by jython)
      return true;
    }
    if ((!sitepackages) && NAME_SITEPACKAGES.equals(file.getName())) {
      return true;
    }
    file = file.getCanonicalFile();
    return _listerdir.equals(file) || _currentdir.equals(file);
  }

  /**
   * {@inheritDoc}
   */
  public void setDefaultRuntime(final String id) {
    Assert.nonEmpty(id);
    if (!hasRuntime(id)) {
      throw new ExtendedBuildException(MSG_INVALIDDEFAULTID, id);
    }
    _defaultid = id;
  }

  /**
   * {@inheritDoc}
   */
  public PythonRuntime getRuntime() {
    if (_defaultid != null) {
      return _runtimes.get(_defaultid);
    } else if (_runtimes.size() == 1) {
      return _runtimes.values().iterator().next();
    }
    throw new BuildException(MSG_NODEFAULTRUNTIME);
  }

  /**
   * {@inheritDoc}
   */
  public PythonRuntime getRuntime(final String id) {
    Assert.nonEmpty(id);
    return _runtimes.get(id);
  }

  /**
   * {@inheritDoc}
   */
  public PythonInterpreter[] getSupportedInterpreters() {
    return _interpreters;
  }

  /**
   * {@inheritDoc}
   */
  public PythonInterpreter lookupInterpreter(final PythonRuntime runtime) {
    for (final PythonInterpreter interpreter : _interpreters) {
      if (interpreter.lookup(runtime.getLocation()) != null) {
        return interpreter;
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public void dispose() {
    _runtimes.clear();
    Utilities.delete(_pythonlister);
    _defaultid = null;
    _pythonlister = null;
    _listerdir = null;
    _currentdir = null;
    _interpreters = null;
    _initialised = false;
  }

  /**
   * {@inheritDoc}
   */
  public void initialize() {

    // export the python lister script, so it can be executed in order to access the pythonpath
    _pythonlister = Utilities.exportResource("/org/ant4eclipse/pydt/lister.py");
    if (!_pythonlister.isAbsolute()) {
      _pythonlister = _pythonlister.getAbsoluteFile();
    }
    _listerdir = _pythonlister.getParentFile();
    _currentdir = new File(".");
    try {
      _listerdir = _listerdir.getCanonicalFile();
      _currentdir = _currentdir.getCanonicalFile();
    } catch (IOException ex) {
      throw new BuildException(ex);
    }

    // load the python interpreter configurations
    final URL cfgurl = getClass().getResource("/org/ant4eclipse/pydt/python.properties");
    if (cfgurl == null) {
      throw new BuildException(MSG_MISSINGPYTHONPROPERTIES);
    }
    try {
      final Map<String, String> props = Utilities.readProperties(cfgurl);
      final List<PythonInterpreter> interpreters = new ArrayList<PythonInterpreter>();
      for (final Map.Entry<String, String> entry : props.entrySet()) {
        if (entry.getKey().startsWith(PROP_INTERPRETER)) {
          final String name = entry.getKey().substring(PROP_INTERPRETER.length());
          final String[] exes = Utilities.cleanup(entry.getValue().split(","));
          if (exes == null) {
            throw new ExtendedBuildException(MSG_MISSINGEXECUTABLES, entry.getKey());
          }
          Arrays.sort(exes);
          interpreters.add(new PythonInterpreter(name, exes));
        }
      }
      _interpreters = interpreters.toArray(new PythonInterpreter[interpreters.size()]);
      Arrays.sort(_interpreters);
    } catch (IOException ex) {
      throw new BuildException(ex);
    }

    _initialised = true;

  }

  /**
   * {@inheritDoc}
   */
  public boolean isInitialized() {
    return _initialised;
  }

} /* ENDCLASS */
