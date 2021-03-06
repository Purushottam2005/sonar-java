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
package org.sonar.java.ast.visitors;

import com.sonar.sslr.api.AstNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.java.SemanticModelProvider;
import org.sonar.java.resolve.SemanticModel;

import javax.annotation.Nullable;

public class SemanticModelVisitor extends JavaAstVisitor implements SemanticModelProvider {

  private static final Logger LOG = LoggerFactory.getLogger(SemanticModelVisitor.class);

  private SemanticModel semanticModel;

  @Override
  public void visitFile(AstNode astNode) {
    if (astNode == null) {
      // parse error
      semanticModel = null;
      return;
    }

    try {
      semanticModel = SemanticModel.createFor(astNode);
    } catch (Exception e) {
      LOG.error("Unable to create symbol table for " + getContext().getFile(), e);
      semanticModel = null;
      return;
    }
  }

  @Override
  @Nullable
  public SemanticModel semanticModel() {
    return semanticModel;
  }

}
