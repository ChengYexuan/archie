package org.openehr.adl;

import com.nedap.archie.adlparser.ADLParseException;
import com.nedap.archie.adlparser.modelconstraints.BMMConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ModelConstraintImposer;
import com.nedap.archie.adlparser.modelconstraints.ReflectionConstraintImposer;
import com.nedap.archie.adlparser.v2.antlr.AdlLexer;
import com.nedap.archie.adlparser.v2.antlr.AdlParser;
import com.nedap.archie.antlr.errors.ANTLRParserErrors;
import com.nedap.archie.antlr.errors.ArchieErrorListener;
import com.nedap.archie.aom.Archetype;
import com.nedap.archie.aom.utils.ArchetypeParsePostProcesser;
import com.nedap.archie.rminfo.MetaModels;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Parses ADL files to Archetype objects.
 *
 */
public class AdlV2Parser {

    private final MetaModels metaModels;
    private final ModelConstraintImposer modelConstraintImposer;
    private ANTLRParserErrors errors;

    private Lexer lexer;
    private AdlParser parser;
    private AdlV2Listener listener;
    private ParseTreeWalker walker;
    private AdlParser.AdlObjectContext tree;
    public ArchieErrorListener errorListener;

    /**
     * If true, write errors to the console, if false, do not
     */
    private boolean logEnabled = true;

    public AdlV2Parser() {
        this.metaModels = null;
        this.modelConstraintImposer = null;
    }

    /**
     * The ModelConstraintImposer is a bit of a relic from the beginning of Archie
     * It's still very useful to set single/multiple, and in some tools, but not
     * necesarilly here. So, deprecated, if you want it it's available to do yourself
     * @param modelConstraintImposer
     */
    @Deprecated
    public AdlV2Parser(ModelConstraintImposer modelConstraintImposer) {
        this.modelConstraintImposer = modelConstraintImposer;
        this.metaModels = null;
    }


    /**
     * Creates an ADLParser with MetaModel knowledge. This is used to set the isSingle and isMultiple fields correctly
     * in the future, this will be used for more model-specific options, such as defined C_PRIMITIVE_OBJECTS and more
     * @param models
     */
    public AdlV2Parser(MetaModels models) {
        this.metaModels = models;
        this.modelConstraintImposer = null;
    }

    public Archetype parse(String adl) throws ADLParseException {
        return parse(CharStreams.fromString(adl));
    }

    public Archetype parse(InputStream stream) throws ADLParseException, IOException {
        return parse(CharStreams.fromStream(new BOMInputStream(stream), Charset.availableCharsets().get("UTF-8")));
    }

    public Archetype parse(CharStream stream) throws ADLParseException {

        errors = new ANTLRParserErrors();
        errorListener = new ArchieErrorListener(errors);
        errorListener.setLogEnabled(logEnabled);
        Archetype result = null;

        lexer = new AdlLexer(stream);
        lexer.addErrorListener(errorListener);
        parser = new AdlParser(new CommonTokenStream(lexer));
        parser.addErrorListener(errorListener);
        tree = parser.adlObject(); // parse

        try {
            AdlV2Listener listener = new AdlV2Listener(errors, metaModels);
            walker = new ParseTreeWalker();
            walker.walk(listener, tree);
            result = listener.getArchetype();
            //set some values that are not directly in ODIN or ADL
            ArchetypeParsePostProcesser.fixArchetype(result);

            if (modelConstraintImposer != null && result.getDefinition() != null) {
                modelConstraintImposer.imposeConstraints(result.getDefinition());
            } else if (metaModels != null) {
                metaModels.selectModel(result);
                if (metaModels.getSelectedBmmModel() != null) {
                    ModelConstraintImposer imposer = new BMMConstraintImposer(metaModels.getSelectedBmmModel());
                    imposer.setSingleOrMultiple(result.getDefinition());
                } else if (metaModels.getSelectedModelInfoLookup() != null) {
                    ModelConstraintImposer imposer = new ReflectionConstraintImposer(metaModels.getSelectedModelInfoLookup());
                    imposer.setSingleOrMultiple(result.getDefinition());
                }
            }
            return result;
        } finally {
            if (errors.hasErrors()) {
                throw new ADLParseException(errors, result);
            }
        }


    }

    public ANTLRParserErrors getErrors() {
        return errors;
    }

    public Lexer getLexer() {
        return lexer;
    }

    public void setLexer(Lexer lexer) {
        this.lexer = lexer;
    }

    public AdlParser getParser() {
        return parser;
    }

    public void setParser(AdlParser parser) {
        this.parser = parser;
    }

    public AdlV2Listener getListener() {
        return listener;
    }

    public void setListener(AdlV2Listener listener) {
        this.listener = listener;
    }

    public ParseTreeWalker getWalker() {
        return walker;
    }

    public void setWalker(ParseTreeWalker walker) {
        this.walker = walker;
    }

    public AdlParser.AdlObjectContext getTree() {
        return tree;
    }

    public void setTree(AdlParser.AdlObjectContext tree) {
        this.tree = tree;
    }

    public boolean isLogEnabled() {
        return logEnabled;
    }

    public void setLogEnabled(boolean logEnabled) {
        this.logEnabled = logEnabled;
    }
}