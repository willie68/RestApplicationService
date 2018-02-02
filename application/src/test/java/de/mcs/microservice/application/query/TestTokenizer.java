/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: TestTokennizer.java
 * EMail: W.Klaas@gmx.de
 * Created: 14.01.2018 wklaa_000
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package de.mcs.microservice.application.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.Monitor;
import de.mcs.microservice.application.query.Tokenizer.TOKEN_OP;
import de.mcs.microservice.application.query.Tokenizer.Token;

/**
 * @author wklaa_000
 *
 */
public class TestTokenizer {

  @Test(expected = QuerySytaxException.class)
  public void testFail1() throws QuerySytaxException {
    Tokenizer.tokenize("((Hallo = Murks) and ((dies = \"ist\") or not(ein=\"Test\"))");
  }

  @Test(expected = QuerySytaxException.class)
  public void testFail2() throws QuerySytaxException {
    Tokenizer.tokenize("(Hallo = Murks) and ((dies = \"ist\")) or not(ein=\"Test\"))");
  }

  /*
   * after + and - only text and parenthesis are allowed
   */
  @Test(expected = QuerySytaxException.class)
  public void testFail3() throws QuerySytaxException {
    Tokenizer.tokenize("+ AND (Hallo = Murks)");
  }

  @Test(expected = QuerySytaxException.class)
  public void testFail4() throws QuerySytaxException {
    Tokenizer.tokenize("- OR (Hallo = Murks)");
  }

  /*
   * plus, minus, gt, lt should be not the last token
   * 
   * @throws QuerySytaxException
   */
  @Test(expected = QuerySytaxException.class)
  public void testFail5() throws QuerySytaxException {
    Tokenizer.tokenize("(Hallo = Murks)+");
  }

  @Test(expected = QuerySytaxException.class)
  public void testFail6() throws QuerySytaxException {
    Tokenizer.tokenize("(Hallo = Murks)-");
  }

  @Test(expected = QuerySytaxException.class)
  public void testFail7() throws QuerySytaxException {
    Tokenizer.tokenize("(Hallo = Murks)>");
  }

  @Test(expected = QuerySytaxException.class)
  public void testFail8() throws QuerySytaxException {
    Tokenizer.tokenize("(Hallo = Murks)<");
  }

  @Test()
  public void testCheckTokenOP() throws QuerySytaxException {
    assertFalse(Tokenizer.AND_TOKEN.is(TOKEN_OP.CP));
    assertTrue(Tokenizer.AND_TOKEN.is(TOKEN_OP.AND));

    Token token = new Token(TOKEN_OP.OR, "|");
    assertFalse(token.is(TOKEN_OP.CP));
    assertTrue(token.is(TOKEN_OP.OR));

    assertFalse(token.is(null));
  }

  @Test()
  public void testCheckIsToken() throws QuerySytaxException {
    assertFalse(Tokenizer.AND_TOKEN.isToken(TOKEN_OP.CP.name()));
    assertTrue(Tokenizer.AND_TOKEN.isToken(TOKEN_OP.AND.name()));

    assertFalse(Tokenizer.AND_TOKEN.isToken(Tokenizer.CP_TOKEN.getShortcut()));
    assertTrue(Tokenizer.AND_TOKEN.isToken(Tokenizer.AND_TOKEN.getShortcut()));

    assertFalse(Tokenizer.AND_TOKEN.isToken(Tokenizer.CP_TOKEN.name()));
    assertTrue(Tokenizer.AND_TOKEN.isToken(Tokenizer.AND_TOKEN.name()));

    Token token = new Token(TOKEN_OP.OR, "|");
    assertFalse(token.isToken(TOKEN_OP.CP.name()));
    assertTrue(token.isToken(TOKEN_OP.OR.name()));

    assertFalse(token.isToken(null));
  }

  @Test
  public void testGE() throws QuerySytaxException {

    List<Token> tokens = Tokenizer.tokenize("(Hallo >= Murks)");
    int i = 0;
    assertToken(TOKEN_OP.OP, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Hallo", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.GE, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Murks", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.CP, tokens.get(i++));
  }

  @Test
  public void testLE() throws QuerySytaxException {

    List<Token> tokens = Tokenizer.tokenize("(Hallo <= Murks)");
    int i = 0;
    assertToken(TOKEN_OP.OP, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Hallo", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.LE, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Murks", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.CP, tokens.get(i++));
  }

  @Test
  public void testLike() throws QuerySytaxException {

    List<Token> tokens = Tokenizer.tokenize("(Hallo like Murks)");
    outputTokenList(tokens);
    int i = 0;
    assertToken(TOKEN_OP.OP, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Hallo", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.LIKE, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Murks", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.CP, tokens.get(i++));
  }

  @Test
  public void testContains() throws QuerySytaxException {

    List<Token> tokens = Tokenizer.tokenize("(Hallo contains Murks)");
    outputTokenList(tokens);
    int i = 0;
    assertToken(TOKEN_OP.OP, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Hallo", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.CONTAINS, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Murks", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.CP, tokens.get(i++));
  }

  @Test
  public void testPlusMinus() throws QuerySytaxException {

    String query = "Hallo +Murks +(\"willie was here\") -(Willie) and (field like \"waldemar\")";
    List<Token> tokens = Tokenizer.tokenize(query);
    outputTokenList(tokens);
    System.out.println(query);
    int i = 0;
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Hallo", tokens.get(i++).getValue());

    assertToken(TOKEN_OP.AND, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Murks", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));

    assertToken(TOKEN_OP.AND, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("\"willie was here\"", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));

    assertToken(TOKEN_OP.AND, tokens.get(i++));
    assertToken(TOKEN_OP.NOT, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Willie", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));

    assertToken(TOKEN_OP.AND, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("field", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.LIKE, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("\"waldemar\"", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));
  }

  @Test
  public void test2() throws QuerySytaxException {

    List<Token> tokens = Tokenizer.tokenize("(Hallo = Murks) and ((dies = \"ist\") or not(ein=\"Test\"))");
    outputTokenList(tokens);
    int i = 0;
    assertToken(TOKEN_OP.OP, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));

    assertEquals("Hallo", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.EQ, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("Murks", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));
    assertToken(TOKEN_OP.AND, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("dies", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.EQ, tokens.get(i++));
    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("\"ist\"", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));
    assertToken(TOKEN_OP.OR, tokens.get(i++));
    assertToken(TOKEN_OP.NOT, tokens.get(i++));
    assertToken(TOKEN_OP.OP, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("ein", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.EQ, tokens.get(i++));

    assertToken(TOKEN_OP.TEXT, tokens.get(i));
    assertEquals("\"Test\"", tokens.get(i++).getValue());
    assertToken(TOKEN_OP.CP, tokens.get(i++));
    assertToken(TOKEN_OP.CP, tokens.get(i++));
  }

  @Test
  public void testPerformance() throws QuerySytaxException {
    String query = "Hallo +Murks +(\"willie was here\") -(Willie) and (field like \"waldemar\")";
    Monitor monitor = MeasureFactory.start(this, "performance");
    for (int i = 0; i < 100000; i++) {
      List<Token> tokens = Tokenizer.tokenize(query);
    }
    monitor.stop();
    System.out.println(String.format("time in msec: %d", monitor.getAccrued()));
  }

  private void outputTokenList(List<Token> tokens) {
    StringBuilder queryLine = new StringBuilder();
    int level = 0;
    for (Token token : tokens) {
      if (token.is(TOKEN_OP.CP)) {
        level--;
      }
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < level; i++) {
        b.append("  ");
      }
      if (token.is(TOKEN_OP.OP)) {
        level++;
      }
      if (token.is(TOKEN_OP.OP)) {
        queryLine.append('(');
      } else if (token.is(TOKEN_OP.CP)) {
        queryLine.append(')');
      } else {
        queryLine.append(token.name());
      }
      if (token.is(TOKEN_OP.TEXT)) {
        queryLine.append('(');
        queryLine.append(token.getValue());
        queryLine.append(')');
      }
      queryLine.append(" ");
      System.out.printf("%s%s\r", b.toString(), token.toString());
    }
    System.out.println(queryLine.toString());
  }

  void assertToken(TOKEN_OP op, Token token) {
    assertNotNull(token);
    assertTrue(String.format("token %s is not %s", token.name(), op.name()), token.is(op));
  }
}
