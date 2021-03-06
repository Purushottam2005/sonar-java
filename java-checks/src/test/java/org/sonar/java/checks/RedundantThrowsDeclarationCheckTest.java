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
import org.sonar.squid.api.SourceFile;

public class RedundantThrowsDeclarationCheckTest {

  @Rule
  public CheckMessagesVerifierRule checkMessagesVerifier = new CheckMessagesVerifierRule();

  @Test
  public void test() {
    SourceFile file = BytecodeFixture.scan("RedundantThrowsDeclarationCheck", new RedundantThrowsDeclarationCheck());
    checkMessagesVerifier
        .verify(file.getCheckMessages())

        .next()
        .atLine(36)
        .withMessage("Remove the declaration of thrown exception 'java.lang.RuntimeException' which is a runtime exception.")

        .next()
        .atLine(39)
        .withMessage("Remove the declaration of thrown exception 'java.lang.IllegalArgumentException' which is a runtime exception.")

        .next()
        .atLine(42)
        .withMessage("Remove the declaration of thrown exception 'org.sonar.java.checks.targets.RedundantThrowsDeclarationCheck$MyRuntimeException' which is a runtime exception.")

        .next()
        .atLine(45)
        .withMessage("Remove the declaration of thrown exception 'org.sonar.java.checks.targets.RedundantThrowsDeclarationCheck$MyException' which is a subclass of another one.")

        .next()
        .atLine(48)
        .withMessage("Remove the declaration of thrown exception 'java.lang.Error' which is a subclass of another one.")

        .next()
        .atLine(51)
        .withMessage("Remove the redundant 'org.sonar.java.checks.targets.RedundantThrowsDeclarationCheck$MyException' thrown exception declaration(s).")

        .next()
        .atLine(54)
        .withMessage("Remove the declaration of thrown exception 'org.sonar.java.checks.targets.RedundantThrowsDeclarationCheck$MyException' which is a subclass of another one.")

        .next()
        .atLine(57)
        .withMessage("Remove the declaration of thrown exception 'org.sonar.java.checks.targets.RedundantThrowsDeclarationCheck$MyRuntimeException' which is a runtime exception.")

        .next()
        .atLine(60)
        .withMessage(
            "Remove the declaration of thrown exception 'org.sonar.java.checks.targets.RedundantThrowsDeclarationCheck$MyRuntimeException' which is a subclass of another one.");
  }

}
