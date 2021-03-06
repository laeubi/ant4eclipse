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
package org.ant4eclipse.ant.pydt.test;

import org.ant4eclipse.lib.core.util.Utilities;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

/**
 * Simple datacontainer used to collect the result of an executed build.
 * 
 * @author Daniel Kasmeroglu (Daniel.Kasmeroglu@Kasisoft.net)
 */
public class BuildResult {

  private BuildListener             _listener;

  private List<String>              _collected;

  private Map<String, List<String>> _collectedbytarget;

  private String                    _wsforward;

  private String                    _wsbackward;

  private String                    _wsdirseparator;

  /**
   * Sets up this result which is generally used as a container for the information that comes up during the build
   * process.
   */
  public BuildResult(File workspacedir, String dirseparator) {
    this._listener = new AntBuildListener();
    this._collected = new ArrayList<String>();
    this._collectedbytarget = new Hashtable<String, List<String>>();
    this._wsforward = workspacedir.getAbsolutePath().replace('\\', '/');
    this._wsbackward = workspacedir.getAbsolutePath().replace('/', '\\');
    this._wsdirseparator = Utilities.replace(this._wsforward, "/", dirseparator);
  }

  /**
   * Returns all output generated by the build process.
   * 
   * @return All output generated by the build process. Not <code>null</code>.
   */
  public String[] getOutput() {
    return this._collected.toArray(new String[this._collected.size()]);
  }

  /**
   * Returns all output generated by the build process within a specific target.
   * 
   * @return All output generated by the build process within a specific target. Not <code>null</code>.
   */
  public String[] getTargetOutput(String target) {
    List<String> content = this._collectedbytarget.get(target);
    if (content == null) {
      return new String[0];
    }
    return content.toArray(new String[content.size()]);
  }

  /**
   * Associates this result with the supplied ant project, so this container will be filled with information when
   * necessary.
   * 
   * @param project
   *          The ant project needed to be monitored. Not <code>null</code>.
   */
  public void assign(Project project) {
    Assert.assertNotNull(project);
    project.addBuildListener(this._listener);
  }

  /**
   * Adds the supplied line to the content. This function performs substitutions in order to replace absolute pathes
   * through variable definitions.
   * 
   * @param list
   *          The list which will be extended. Not <code>null</code>.
   * @param line
   *          The line that has to be added. Not <code>null</code>.
   */
  private void addLine(List<String> list, String line) {
    line = Utilities.replace(line, this._wsforward, "${" + AntProperties.PROP_WORKSPACEDIR + "}");
    line = Utilities.replace(line, this._wsbackward, "${" + AntProperties.PROP_WORKSPACEDIR + "}");
    line = Utilities.replace(line, this._wsdirseparator, "${" + AntProperties.PROP_WORKSPACEDIR + "}");
    list.add(line);
  }

  /**
   * Custom implementation of a listener used to collect the build content.
   */
  private class AntBuildListener implements BuildListener {

    /**
     * {@inheritDoc}
     */
    public void buildFinished(BuildEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    public void buildStarted(BuildEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    public void messageLogged(BuildEvent evt) {
      addLine(BuildResult.this._collected, evt.getMessage());
      if (evt.getTarget() != null) {
        String name = evt.getTarget().getName();
        List<String> list = BuildResult.this._collectedbytarget.get(name);
        if (list == null) {
          list = new ArrayList<String>();
          BuildResult.this._collectedbytarget.put(name, list);
        }
        addLine(list, evt.getMessage());
      }
    }

    /**
     * {@inheritDoc}
     */
    public void targetFinished(BuildEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    public void targetStarted(BuildEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    public void taskFinished(BuildEvent evt) {
    }

    /**
     * {@inheritDoc}
     */
    public void taskStarted(BuildEvent evt) {
    }

  } /* ENDCLASS */

} /* ENDCLASS */
