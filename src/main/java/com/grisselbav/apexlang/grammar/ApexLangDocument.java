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

package com.grisselbav.apexlang.grammar;

import ch.islandsql.grammar.util.ParseTreeUtil;
import ch.islandsql.grammar.util.ParserMetrics;
import ch.islandsql.grammar.util.SyntaxErrorEntry;
import ch.islandsql.grammar.util.SyntaxErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CodePointCharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.List;

/**
 * Produces a parse-tree based on the content of an APEXlang file content.
 * Provides methods to navigate the parse tree.
 */
public class ApexLangDocument {
    private final ApexLangParser.ApxFileContext apxFile;
    private final List<SyntaxErrorEntry> syntaxErrors;
    private final ParserMetrics parserMetrics;

    /**
     * Constructor.
     *
     * @param builder The builder instance.
     */
    private ApexLangDocument(Builder builder) {
        CodePointCharStream charStream = CharStreams.fromString(builder.apexlang);
        ApexLangLexer lexer = new ApexLangLexer(charStream);
        SyntaxErrorListener errorListener = new SyntaxErrorListener();
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ApexLangParser parser = new ApexLangParser(tokenStream);
        parser.setProfile(builder.profile);
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        long parserStartTime = System.nanoTime();
        long parserStartMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        this.apxFile = parser.apxFile();
        apxFile.setTokenStream(tokenStream);
        long parserTime = System.nanoTime() - parserStartTime;
        long parserMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - parserStartMemory;
        this.parserMetrics = new ParserMetrics(parserTime, parserMemory, parser.getParseInfo());
        this.syntaxErrors = errorListener.getSyntaxErrors();
    }

    /**
     * Builds a new instance of {@link ApexLangDocument ApexLangDocument} with a fluent API.
     */
    public static class Builder {
        private String apexlang = "";
        private boolean profile = false;

        /**
         * Sets the APEXlang file content to be parsed as string.
         * Default is an empty APEXlang file content.
         *
         * @param apexlang The APEXlang file content as string.
         * @return The builder instance.
         */
        public Builder apexlang(String apexlang) {
            this.apexlang = apexlang != null ? apexlang : "";
            return this;
        }

        /**
         * Sets the flag to collect ANTLR profiling data during lexing/parsing.
         * Default is false; this means no profiling data is gathered during lexing/parsing.
         *
         * @param profile Collect ANTLR profiling data during lexing/parsing?
         * @return The builder instance.
         */
        public Builder profile(boolean profile) {
            this.profile = profile;
            return this;
        }

        /**
         * Builds and returns an ApexLangDocument instance.
         *
         * @return The ApexLangDocument instance.
         */
        public ApexLangDocument build() {
            return new ApexLangDocument(this);
        }
    }

    /**
     * Factory to construct an ApexLangDocument.
     *
     * @param apexlang Content of an APEXlang file as string.
     * @return Constructed ApexLangDocument.
     */
    public static ApexLangDocument parse(String apexlang) {
        return new Builder().apexlang(apexlang).build();
    }

    /**
     * Factory to construct an ApexLangDocument.
     *
     * @param apexlang Content of an APEXlang file as string.
     * @param profile  Collect ANTLR profiling data during lexing/parsing?
     * @return Constructed ApexLangDocument.
     */
    public static ApexLangDocument parse(String apexlang, boolean profile) {
        return new Builder().apexlang(apexlang).profile(profile).build();
    }

    /**
     * Returns the token stream produced by the lexer.
     * Can be used to access hidden tokens.
     *
     * @return Token stream.
     */
    public CommonTokenStream getTokenStream() {
        return this.apxFile.getTokenStream();
    }

    /**
     * Returns the start node of the parse tree.
     *
     * @return ApxFile (start rule).
     */
    public ApexLangParser.ApxFileContext getApxFile() {
        return apxFile;
    }

    /**
     * Gets all nodes that are instances of a desired class.
     * The start node is an apxFile.
     *
     * @param desiredType Desired class (must be a descendant of ParseTree).
     * @param <T>         The return type of the result.
     * @return List of nodes that are instances of the desired class.
     */
    public <T extends ParseTree> List<T> getAllContentsOfType(Class<T> desiredType) {
        return ParseTreeUtil.getAllContentsOfType(apxFile, desiredType);
    }

    /**
     * Gets all syntax error entries for the document.
     * The list is empty if no syntax errors are found.
     *
     * @return Returns a list of syntax errors.
     */
    public List<SyntaxErrorEntry> getSyntaxErrors() {
        return syntaxErrors;
    }

    /**
     * Get the parser metrics gathered during construction with or without profiling.
     *
     * @return The parser metrics gathered during construction with or without profiling.
     */
    public ParserMetrics getParserMetrics() {
        return parserMetrics;
    }
}
