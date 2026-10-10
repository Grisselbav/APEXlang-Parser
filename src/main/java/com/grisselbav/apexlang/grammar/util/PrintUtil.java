/*
 * Copyright 2026 Philipp Salvisberg <philipp.salvisberg@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grisselbav.apexlang.grammar.util;

import ch.islandsql.grammar.util.ParseTreeUtil;
import com.grisselbav.apexlang.grammar.ApexLangParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.Utils;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.Trees;

import java.util.Arrays;
import java.util.List;

/**
 * Parse tree print utilities.
 */
public class PrintUtil {
    /**
     * Gets the label name of an alternative.
     * If an alternative is labelled with "#someLabel" in the grammar, then
     * a subclass named "ApexLangParser$SomeLabelContext" of another
     * rule class (not ParserRuleContext) is created.
     *
     * @param ctx ParserRuleContext to get the alternative label name from.
     * @return Returns the label name or null, if no label is defined.
     */
    public static String getLabelName(ParserRuleContext ctx) {
        if (ctx.getClass().getSuperclass().getSimpleName().equals("ApexLangParserRuleContext")) {
            return null;
        } else {
            String className = ctx.getClass().getName();
            String labelName = className.substring(className.indexOf("$") + 1, className.lastIndexOf("Context"));
            return Character.toLowerCase(labelName.charAt(0)) + labelName.substring(1);
        }
    }

    /**
     * Produces a hierarchical parse tree as string.
     *
     * @param root The start node.
     * @return Returns a hierarchical parse tree as string.
     */
    public static String printParseTree(ParseTree root) {
        PrintRuleListener listener = new PrintRuleListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, root);
        return listener.getResult();
    }

    /**
     * Produces a parse tree as string in DOT format.
     *
     * @param root The start node.
     * @return Returns a parse tree as string in DOT format.
     */
    public static String dotParseTree(ParseTree root) {
        DotRuleListener listener = new DotRuleListener();
        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(listener, root);
        return listener.getResult();
    }

    /**
     * Listener to be used to produce a hierarchical representation of the parse tree.
     */
    public static class PrintRuleListener implements ParseTreeListener {
        private final String NL = System.lineSeparator();
        private final StringBuilder sb = new StringBuilder();
        private final List<String> parserRuleNames;
        private final Vocabulary vocabulary;
        private int level = 0;

        /**
         * Constructor.
         */
        public PrintRuleListener() {
            this.parserRuleNames = Arrays.asList(ApexLangParser.ruleNames);
            this.vocabulary = ApexLangParser.VOCABULARY;
        }

        /**
         * Add TerminalNode to the result.
         * Emits the type name of a symbol, followed by colon, followed by the value
         *
         * @param node TerminalNode.
         */
        @Override
        public void visitTerminal(TerminalNode node) {
            printNewLineAndIndent();
            int type = node.getSymbol().getType();
            if (type >= 0) {
                // all symbols except EOF
                sb.append(vocabulary.getSymbolicName(node.getSymbol().getType()));
                sb.append(":");
            }
            sb.append(Utils.escapeWhitespace(Trees.getNodeText(node, parserRuleNames), true));
        }

        /**
         * Not required to produce the result, but must be implemented.
         *
         * @param node ErrorNode.
         */
        @Override
        public void visitErrorNode(ErrorNode node) {
            // empty implementation
        }

        /**
         * Add ParserRuleContext to the result.
         * Every call increases the indentation by 1.
         * Emits the rule name, followed by a colon, followed by the label name (alternative)
         * Emits only the rule name if no label name is defined.
         *
         * @param ctx ParserRuleContext.
         */
        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            printNewLineAndIndent();
            String labelName = ParseTreeUtil.getLabelName(ctx);
            if (labelName == null) {
                sb.append(Utils.escapeWhitespace(Trees.getNodeText(ctx, parserRuleNames), false));
            } else {
                sb.append(parserRuleNames.get(ctx.getRuleIndex()));
                sb.append(":");
                sb.append(labelName);
            }
            level++;
        }

        /**
         * Every call decreases the indentation by 1.
         *
         * @param ctx ParseRuleContext.
         */
        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            level--;
            if (level == 0) {
                sb.append(NL);
            }
        }

        /**
         * Return the result after walking the parse-tree.
         *
         * @return Hierarchical representation of the parse tree as string.
         */
        public String getResult() {
            return sb.toString();
        }

        /**
         * Adds a new line and two characters per indentation level.
         */
        private void printNewLineAndIndent() {
            if (level > 0) {
                sb.append(NL);
            }
            for (int i=0; i<level; i++) {
                sb.append("  ");
            }
        }
    }

    /**
     * Listener to be used to produce a DOT representation of the parse tree.
     * The output can be used to produce a graphical representation of the parse tree
     * via online tools such as
     * <ul>
     * <li> <a href="https://dreampuf.github.io/GraphvizOnline/">GraphvizOnline</a>
     * <li> <a href="https://edotor.net/">Edotor</a>
     * <li> <a href="http://viz-js.com/">Viz.js</a>
     * <li> <a href="http://www.webgraphviz.com/">WebGraphviz</a>
     * </ul>
     * See also <a href="https://www.graphviz.org">Graphviz</a>.
     */
    @SuppressWarnings("FieldCanBeLocal")
    public static class DotRuleListener implements ParseTreeListener {
        private final String NL = System.lineSeparator();
        private final StringBuilder sb = new StringBuilder();
        private final List<String> parserRuleNames;
        private final String BG_COLOR="#f9f9f9"; // "transparent" is also a valid BG_COLR
        private final String CTX_FILL_COLOR="#f6d2f4";
        private final String CTX_FONT_COLOR="#000000";
        private final String TERMINAL_FILL_COLOR="#e5e5e5";
        private final String TERMINAL_FONT_COLOR="#000000";
        private final String FONT_NAME="Helvetica"; // default: Times, others: Helvetica-bold, Times-bold, Times-italic
        private int level = 0;

        /**
         * Constructor.
         */
        public DotRuleListener() {
            this.parserRuleNames = Arrays.asList(ApexLangParser.ruleNames);
        }

        /**
         * Add TerminalNode to the result.
         *
         * @param node TerminalNode.
         */
        @Override
        public void visitTerminal(TerminalNode node) {
            sb.append("  ");
            sb.append('"');
            sb.append(node.hashCode()); // internal instance representation
            sb.append('"');
            sb.append(" [shape=box label=");
            sb.append('"');
            sb.append(Utils.escapeWhitespace(Trees.getNodeText(node, parserRuleNames), true).replace("\\", "\\\\").replace("\"","\\\"")); // human-readable representation
            sb.append('"');
            sb.append(" style=filled fillcolor=");
            sb.append('"');
            sb.append(TERMINAL_FILL_COLOR);
            sb.append('"');
            sb.append(" fontcolor=");
            sb.append('"');
            sb.append(TERMINAL_FONT_COLOR);
            sb.append('"');
            sb.append(" fontname=");
            sb.append('"');
            sb.append(FONT_NAME);
            sb.append('"');
            sb.append("]");
            sb.append(NL);
        }

        /**
         * Not required to produce the result, but must be implemented.
         *
         * @param node ErrorNode.
         */
        @Override
        public void visitErrorNode(ErrorNode node) {
            // empty implementation
        }

        /**
         * Add ParserRuleContext to the result.
         * Every call increases the indentation by 1.
         * Emits the rule name, followed by a colon, followed by the label name (alternative)
         * Emits only the rule name if no label name is defined.
         *
         * @param ctx ParserRuleContext.
         */
        @Override
        public void enterEveryRule(ParserRuleContext ctx) {
            if (level == 0) {
                sb.append("digraph APEXlang {");
                sb.append(NL);
                sb.append("  bgcolor=");
                sb.append('"');
                sb.append(BG_COLOR);
                sb.append('"');
                sb.append(NL);
            }
            level++;
            sb.append("  ");
            sb.append('"');
            sb.append(ctx.hashCode()); // internal instance representation
            sb.append('"');
            sb.append(" [shape=ellipse label=");
            sb.append('"');
            String labelName = PrintUtil.getLabelName(ctx);
            sb.append(Utils.escapeWhitespace(Trees.getNodeText(ctx, parserRuleNames), false));
            if (labelName != null) {
                sb.append(":\\n");
                sb.append(labelName);
            }
            sb.append('"');
            sb.append(" style=filled fillcolor=");
            sb.append('"');
            sb.append(CTX_FILL_COLOR);
            sb.append('"');
            sb.append(" fontcolor=");
            sb.append('"');
            sb.append(CTX_FONT_COLOR);
            sb.append('"');
            sb.append(" fontname=");
            sb.append('"');
            sb.append(FONT_NAME);
            sb.append('"');
            sb.append("]");
            sb.append(NL);
            if (ctx.children != null) {
                for (ParseTree parseTree : ctx.children) {
                    sb.append("  ");
                    sb.append('"');
                    sb.append(ctx.hashCode());
                    sb.append('"');
                    sb.append(" -> ");
                    sb.append('"');
                    sb.append(parseTree.hashCode());
                    sb.append('"');
                    sb.append(NL);
                }
            }
        }

        /**
         * Every call decreases the indentation by 1.
         *
         * @param ctx ParseRuleContext.
         */
        @Override
        public void exitEveryRule(ParserRuleContext ctx) {
            level--;
            if (level == 0) {
                sb.append("}");
                sb.append(NL);
            }
        }

        /**
         * Return the result after walking the parse-tree.
         *
         * @return Hierarchical representation of the parse tree as a string.
         */
        public String getResult() {
            return sb.toString();
        }
    }
}
