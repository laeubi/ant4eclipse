package org.ant4eclipse.jdt.ant;

import java.util.List;

import org.ant4eclipse.core.Assert;
import org.ant4eclipse.jdt.ant.containerargs.JdtClasspathContainerArgumentComponent;
import org.ant4eclipse.jdt.ant.containerargs.JdtClasspathContainerArgumentDelegate;
import org.ant4eclipse.jdt.model.project.JavaProjectRole;
import org.ant4eclipse.jdt.tools.container.JdtClasspathContainerArgument;
import org.ant4eclipse.platform.ant.core.MacroExecutionComponent;
import org.ant4eclipse.platform.ant.core.MacroExecutionValues;
import org.ant4eclipse.platform.ant.core.ScopedMacroDefinition;
import org.ant4eclipse.platform.ant.core.delegate.MacroExecutionDelegate;
import org.ant4eclipse.platform.ant.core.task.AbstractProjectPathTask;
import org.ant4eclipse.platform.model.resource.EclipseProject;
import org.apache.tools.ant.DynamicElement;
import org.apache.tools.ant.taskdefs.MacroDef;
import org.apache.tools.ant.taskdefs.MacroDef.NestedSequential;

/**
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractExecuteJdtProjectTask extends AbstractProjectPathTask implements
    JdtClasspathContainerArgumentComponent, DynamicElement, MacroExecutionComponent<String> {

  private final MacroExecutionDelegate<String>        _macroExecutionDelegate;

  /** - */
  private final JdtClasspathContainerArgumentDelegate _jdtClasspathContainerArgumentDelegate;

  /** - */
  private final JdtExecutorValuesProvider             _executorValuesProvider;

  /**
   *
   */
  public AbstractExecuteJdtProjectTask(final String prefix) {
    Assert.notNull(prefix);

    this._macroExecutionDelegate = new MacroExecutionDelegate<String>(this, prefix);

    this._jdtClasspathContainerArgumentDelegate = new JdtClasspathContainerArgumentDelegate();

    this._executorValuesProvider = new JdtExecutorValuesProvider(this);
  }

  public final NestedSequential createScopedMacroDefinition(final String scope) {
    return this._macroExecutionDelegate.createScopedMacroDefinition(scope);
  }

  public final void executeMacroInstance(final MacroDef macroDef, final MacroExecutionValues macroExecutionValues) {
    this._macroExecutionDelegate.executeMacroInstance(macroDef, macroExecutionValues);
  }

  public final List<ScopedMacroDefinition<String>> getScopedMacroDefinitions() {
    return this._macroExecutionDelegate.getScopedMacroDefinitions();
  }

  public final String getPrefix() {
    return this._macroExecutionDelegate.getPrefix();
  }

  public final void setPrefix(final String prefix) {
    this._macroExecutionDelegate.setPrefix(prefix);
  }

  public final JdtClasspathContainerArgument createJdtClasspathContainerArgument() {
    return this._jdtClasspathContainerArgumentDelegate.createJdtClasspathContainerArgument();
  }

  public final List<JdtClasspathContainerArgument> getJdtClasspathContainerArguments() {
    return this._jdtClasspathContainerArgumentDelegate.getJdtClasspathContainerArguments();
  }

  /**
   * <p>
   * Helper method that returns the {@link JavaProjectRole} role for the set {@link EclipseProject}.
   * </p>
   * 
   * @return the {@link JavaProjectRole} role for the set {@link EclipseProject}.
   */
  protected final JavaProjectRole getJavaProjectRole() {
    return JavaProjectRole.Helper.getJavaProjectRole(getEclipseProject());
  }

  protected final JdtExecutorValuesProvider getExecutorValuesProvider() {
    return this._executorValuesProvider;
  }
}
