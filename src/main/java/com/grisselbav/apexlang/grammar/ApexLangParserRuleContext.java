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

import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;

/**
 * Custom parser rule context that holds a token stream for start rules.
 * The token stream is set instantiating an ApexLangDocument.
 */
public class ApexLangParserRuleContext extends ParserRuleContext {
    private CommonTokenStream tokenStream;

    /**
     * Default constructor
     */
    public ApexLangParserRuleContext() {
    }

    /**
     * Constructor.
     *
     * @param parent the parent
     * @param invokingStateNumber the invoker state number
     */
    public ApexLangParserRuleContext(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }

    /**
     * Gets the token stream. If empty gets the token stream of the parent.
     * Should never return null in a valid parse tree instantiated via ApexLangDocument.
     *
     * @return the token stream
     */
    public CommonTokenStream getTokenStream() {
        if (tokenStream == null) {
            return getParent() != null ? ((ApexLangParserRuleContext) getParent()).getTokenStream() : tokenStream;
        } else {
            return tokenStream;
        }
    }

    /**
     * Gets the token stream of the root node. This is usually file.
     *
     * @return the token stream of the root node (apxFile).
     */
    public CommonTokenStream getRootTokenStream() {
        ApexLangParserRuleContext context = this;
        while (context.getParent() != null) {
            context = (ApexLangParserRuleContext) context.getParent();
        }
        return context.getTokenStream();
    }

    /**
     * Sets the token stream. Called for start rules such as an apxFile.
     *
     * @param tokenStream the token stream produced by the ApexLangLexerBase.
     */
    public void setTokenStream(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }
}
