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
     * @return the token stream of the root node (file).
     */
    public CommonTokenStream getRootTokenStream() {
        ApexLangParserRuleContext context = this;
        while (context.getParent() != null) {
            context = (ApexLangParserRuleContext) context.getParent();
        }
        return context.getTokenStream();
    }

    /**
     * Sets the token stream. Called for start rules such a file.
     *
     * @param tokenStream the token stream produced by the ApexLangLexerBase.
     */
    public void setTokenStream(CommonTokenStream tokenStream) {
        this.tokenStream = tokenStream;
    }
}
