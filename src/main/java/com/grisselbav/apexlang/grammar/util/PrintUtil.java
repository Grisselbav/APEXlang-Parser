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

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;


/**
 * Parse tree print utilities.
 */
public class PrintUtil {
    /**
     * Gets the label name of an alternative.
     * If an alternative is labeled with "#someLabel" in the grammar, then
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
}
