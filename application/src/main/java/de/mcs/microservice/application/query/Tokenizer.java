/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: Tokennizer.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author wklaa_000
 *
 */
public class Tokenizer {

  static final Token CP_TOKEN = new Token(TOKEN_OP.CP, ")");
  static final Token OP_TOKEN = new Token(TOKEN_OP.OP, "(");
  static final Token NOT_TOKEN = new Token(TOKEN_OP.NOT, "!");
  static final Token LE_TOKEN = new Token(TOKEN_OP.LE, "<=");
  static final Token LT_TOKEN = new Token(TOKEN_OP.LT, "<");
  static final Token GT_TOKEN = new Token(TOKEN_OP.GT, ">");
  static final Token GE_TOKEN = new Token(TOKEN_OP.GE, ">=");
  static final Token EQ_TOKEN = new Token(TOKEN_OP.EQ, "=");
  static final Token OR_TOKEN = new Token(TOKEN_OP.OR, "|");
  static final Token AND_TOKEN = new Token(TOKEN_OP.AND, "&");
  static final Token LIKE_TOKEN = new Token(TOKEN_OP.LIKE);
  static final Token CONTAINS_TOKEN = new Token(TOKEN_OP.CONTAINS);
  static final Token PLUS_TOKEN = new Token(TOKEN_OP.PLUS, "+");
  static final Token MINUS_TOKEN = new Token(TOKEN_OP.MINUS, "-");

  public static class Token {
    private String name;
    private String shortcut = null;
    private String value;

    Token(TOKEN_OP op, String shortcut) {
      this(op);
      this.shortcut = shortcut;
    }

    public Token(TOKEN_OP op) {
      this.name = op.name();
    }

    public String name() {
      return name;
    }

    public boolean isToken(String tokenValue) {
      if (tokenValue != null) {
        return tokenValue.equalsIgnoreCase(name) || tokenValue.equalsIgnoreCase(shortcut);
      }
      return false;
    }

    public Token setValue(String value) {
      this.value = value;
      return this;
    }

    public String getValue() {
      return value;
    }

    /**
     * @return the shortcut
     */
    public String getShortcut() {
      return shortcut;
    }

    public boolean is(TOKEN_OP op) {
      if (op != null) {
        return name.equals(op.name());
      }
      return false;
    }

    @Override
    public String toString() {
      return String.format("n: %s, s: %s, v: %s", name, shortcut, value);
    }
  }

  public static enum TOKEN_OP {
    AND, OR, EQ, GT, LT, GE, LE, NOT, OP, CP, TEXT, LIKE, CONTAINS, PLUS, MINUS
  }

  public static Token[] tokenOperations = { AND_TOKEN, OR_TOKEN, EQ_TOKEN, GT_TOKEN, LT_TOKEN, GE_TOKEN, LE_TOKEN,
      NOT_TOKEN, OP_TOKEN, CP_TOKEN, LIKE_TOKEN, CONTAINS_TOKEN, PLUS_TOKEN, MINUS_TOKEN };

  public static List<Token> tokenize(String value) throws QuerySytaxException {
    String tokenString = addWhiteSpaces(value);

    List<Token> tokenList = buildTokenList(tokenString);

    combineTokens(tokenList);

    checkSyntax(tokenList);

    return tokenList;
  }

  /**
   * this method will combine tokens, if necessary. example two tokens like GT
   * EQ (> =) will be combined to a GE (>=) token
   * 
   * @param tokenList
   * @throws QuerySytaxException
   */
  private static void combineTokens(List<Token> tokenList) throws QuerySytaxException {
    for (int i = 0; i < tokenList.size(); i++) {
      Token token = tokenList.get(i);
      // > = -> >=
      if (token.is(TOKEN_OP.GT)) {
        if (tokenList.size() > (i + 1)) {
          if (tokenList.get(i + 1).is(TOKEN_OP.EQ)) {
            tokenList.set(i, GE_TOKEN);
            tokenList.remove(i + 1);
          }
        } else {
          throw new QuerySytaxException("after > must be text or token");
        }
      }
      // < = -> <=
      if (token.is(TOKEN_OP.LT)) {
        if (tokenList.size() > (i + 1)) {
          if (tokenList.get(i + 1).is(TOKEN_OP.EQ)) {
            tokenList.set(i, LE_TOKEN);
            tokenList.remove(i + 1);
          }
        } else {
          throw new QuerySytaxException("after < must be text or token");
        }
      }
      // +text -> AND (text)
      if (token.is(TOKEN_OP.PLUS)) {
        if (tokenList.size() > (i + 1)) {
          Token nextToken = tokenList.get(i + 1);
          if (nextToken.is(TOKEN_OP.TEXT)) {
            tokenList.set(i, AND_TOKEN);
            tokenList.add(i + 2, CP_TOKEN);
            tokenList.add(i + 1, OP_TOKEN);
          } else if (nextToken.is(TOKEN_OP.OP)) {
            tokenList.set(i, AND_TOKEN);
          } else {
            throw new QuerySytaxException("after + must be an text");
          }
        } else {
          throw new QuerySytaxException("after - must be an text");
        }
      }
      // -text -> AND NOT(text)
      if (token.is(TOKEN_OP.MINUS)) {
        if (tokenList.size() > (i + 1)) {
          Token nextToken = tokenList.get(i + 1);
          if (nextToken.is(TOKEN_OP.TEXT)) {
            tokenList.set(i, AND_TOKEN);
            tokenList.add(i + 2, CP_TOKEN);
            tokenList.add(i + 1, OP_TOKEN);
            tokenList.add(i + 1, NOT_TOKEN);
          } else if (nextToken.is(TOKEN_OP.OP)) {
            tokenList.set(i, AND_TOKEN);
            tokenList.add(i + 1, NOT_TOKEN);
          } else {
            throw new QuerySytaxException("after - must be an text");
          }
        } else {
          throw new QuerySytaxException("after - must be an text");
        }
      }
    }
  }

  private static List<Token> buildTokenList(String tokenString) {
    String[] tokens = tokenString.split("\\s(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
    // String[] tokens = tokenString.split("\\s");
    List<Token> tokenList = new ArrayList<>();
    for (String token : tokens) {
      if (!StringUtils.isEmpty(token.trim())) {
        boolean found = false;
        for (Token tokenOp : tokenOperations) {
          if (tokenOp.isToken(token)) {
            tokenList.add(tokenOp);
            found = true;
          }
        }
        if (!found) {
          Token field = new Token(TOKEN_OP.TEXT).setValue(token);
          tokenList.add(field);
        }
      }
    }
    return tokenList;
  }

  private static String addWhiteSpaces(String tokenString) {
    for (Token token : tokenOperations) {
      if (token.getShortcut() != null) {
        String quote = Pattern.quote(token.getShortcut());
        tokenString = tokenString.replaceAll(quote, String.format(" %s ", token.name()));
      }
    }
    return tokenString;
  }

  public static void checkSyntax(List<Token> tokens) throws QuerySytaxException {
    checkParenthesisBalance(tokens);
  }

  private static void checkParenthesisBalance(List<Token> tokens) throws QuerySytaxException {
    int pCount = 0;
    for (Token token : tokens) {
      if (token.is(TOKEN_OP.OP)) {
        pCount++;
      } else if (token.is(TOKEN_OP.CP)) {
        pCount--;
      }
    }
    if (pCount > 0) {
      throw new QuerySytaxException("too many left parenthesis");
    } else if (pCount < 0) {
      throw new QuerySytaxException("too many right parenthesis");
    }
  }
}
