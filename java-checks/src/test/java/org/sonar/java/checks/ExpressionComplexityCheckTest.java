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

import com.sonar.sslr.squid.checks.CheckMessagesVerifierRule;
import org.junit.Rule;
import org.junit.Test;
import org.sonar.java.JavaAstScanner;
import org.sonar.squid.api.SourceFile;

import java.io.File;

public class ExpressionComplexityCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void detected() {
    SourceFile file = JavaAstScanner.scanSingleFile(new File("src/test/files/checks/ExpressionComplexityCheck.java"), new ExpressionComplexityCheck());
    checkMessagesVerifier.verify(file.getCheckMessages())
        .next().atLine(3).withMessage("Reduce the number of conditional operators (4) used in the expression (maximum allowed 3).")
        .next().atLine(5).withMessage("Reduce the number of conditional operators (4) used in the expression (maximum allowed 3).")
        .next().atLine(6).withMessage("Reduce the number of conditional operators (5) used in the expression (maximum allowed 3).")
        .next().atLine(11).withMessage("Reduce the number of conditional operators (6) used in the expression (maximum allowed 3).");
  }

  @Test
  public void custom() {
    ExpressionComplexityCheck check = new ExpressionComplexityCheck();
    check.max = 4;

    SourceFile file = JavaAstScanner.scanSingleFile(new File("src/test/files/checks/ExpressionComplexityCheck.java"), check);
    checkMessagesVerifier.verify(file.getCheckMessages())
        .next().atLine(6).withMessage("Reduce the number of conditional operators (5) used in the expression (maximum allowed 4).")
        .next().atLine(11).withMessage("Reduce the number of conditional operators (6) used in the expression (maximum allowed 4).");
  }

}