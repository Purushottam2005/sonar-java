/*
 * SonarQube Java
 * Copyright (C) 2012 SonarSource
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.java.checks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.sonar.api.rule.RuleKey;
import org.sonar.check.BelongsToProfile;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.JavaFileScanner;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.BaseTreeVisitor;
import org.sonar.plugins.java.api.tree.CatchTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.NewClassTree;
import org.sonar.plugins.java.api.tree.ThrowStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.TypeCastTree;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Rule(
  key = CatchUsesExceptionWithContextCheck.RULE_KEY,
  priority = Priority.MAJOR)
@BelongsToProfile(title = "Sonar way", priority = Priority.MAJOR)
public class CatchUsesExceptionWithContextCheck extends BaseTreeVisitor implements JavaFileScanner {

  public static final String RULE_KEY = "S1166";
  private final RuleKey ruleKey = RuleKey.of(CheckList.REPOSITORY_KEY, RULE_KEY);

  private static final Set<String> EXCLUDED_EXCEPTION_TYPE = ImmutableSet.of(
    "NumberFormatException",
    "InterruptedExcetpion",
    "ParseException",
    "MalformedURLException");

  private JavaFileScannerContext context;

  private final Set<CatchTree> invalidCatchesCheck = Sets.newHashSet();

  @Override
  public void scanFile(JavaFileScannerContext context) {
    this.context = context;

    scan(context.getTree());
  }

  @Override
  public void visitCatch(CatchTree tree) {
    invalidCatchesCheck.add(tree);

    super.visitCatch(tree);

    if (invalidCatchesCheck.contains(tree)) {
      invalidCatchesCheck.remove(tree);

      if (!isExcludedType(tree.parameter().type())) {
        context.addIssue(tree, ruleKey, "Either log or rethrow this exception along with some contextual information.");
      }
    }
  }

  private boolean isExcludedType(Tree tree) {
    return tree.is(Kind.IDENTIFIER) &&
      EXCLUDED_EXCEPTION_TYPE.contains(((IdentifierTree) tree).name());
  }

  @Override
  public void visitMethodInvocation(MethodInvocationTree tree) {
    super.visitMethodInvocation(tree);

    if (tree.arguments().size() >= 2) {
      handleArguments(tree.arguments());
    }
  }

  private void removeInvalidCatches(String exceptionName) {
    Iterator<CatchTree> it = invalidCatchesCheck.iterator();
    while (it.hasNext()) {
      CatchTree tree = it.next();

      if (exceptionName.equals(tree.parameter().simpleName())) {
        it.remove();
      }
    }
  }

  private void handleArguments(List<ExpressionTree> trees) {
    for (Tree tree : trees) {
      if (tree.is(Kind.IDENTIFIER)) {
        removeInvalidCatches(((IdentifierTree) tree).name());
      }
    }
  }

  @Override
  public void visitThrowStatement(ThrowStatementTree tree) {
    super.visitThrowStatement(tree);

    handleThrowExpression(tree.expression());
  }

  private void handleThrowExpression(ExpressionTree tree) {
    if (tree.is(Kind.IDENTIFIER)) {
      removeInvalidCatches(((IdentifierTree) tree).name());
    } else if (tree.is(Kind.NEW_CLASS)) {
      NewClassTree newClassTree = (NewClassTree) tree;

      handleArguments(newClassTree.arguments());
    } else if (tree.is(Kind.METHOD_INVOCATION)) {
      MethodInvocationTree methodInvocationTree = (MethodInvocationTree) tree;

      handleArguments(methodInvocationTree.arguments());
    } else if (tree.is(Kind.TYPE_CAST)) {
      handleThrowExpression(((TypeCastTree) tree).expression());
    }
  }

}
