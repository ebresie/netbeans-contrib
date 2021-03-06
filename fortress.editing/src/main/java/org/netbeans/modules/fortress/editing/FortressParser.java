/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.fortress.editing;

import com.sun.fortress.nodes.Node;
import com.sun.fortress.parser.Fortress;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.BadLocationException;
import org.netbeans.modules.gsf.api.OffsetRange;
import org.netbeans.modules.gsf.api.ParseEvent;
import org.netbeans.modules.gsf.api.ParseListener;
import org.netbeans.modules.gsf.api.Parser;
import org.netbeans.modules.gsf.api.ParserFile;
import org.netbeans.modules.gsf.api.ParserResult;
import org.netbeans.modules.gsf.api.PositionManager;
import org.netbeans.modules.gsf.api.Severity;
import org.netbeans.modules.gsf.api.SourceFileReader;
import org.netbeans.modules.gsf.spi.DefaultError;
import org.netbeans.modules.gsf.api.TranslatedSource;
import org.openide.util.Exceptions;
import xtc.parser.ParseError;
import xtc.parser.Result;
import xtc.parser.SemanticValue;

/**
 * Wrapper around com.sun.fortress.parser.Fortress to parse a buffer into an AST.
 *
 * 
 * @author Caoyuan Deng
 * @author Tor Norbye
 */
public class FortressParser implements Parser {

    private final PositionManager positions = createPositionManager();

    public FortressParser() {
    }

    private static String asString(CharSequence sequence) {
        if (sequence instanceof String) {
            return (String) sequence;
        } else {
            return sequence.toString();
        }
    }

    /** Parse the given set of files, and notify the parse listener for each transition
     * (compilation results are attached to the events )
     */
    public void parseFiles(Parser.Job job) {
        ParseListener listener = job.listener;
        SourceFileReader reader = job.reader;

        for (ParserFile file : job.files) {
            ParseEvent beginEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, null);
            listener.started(beginEvent);

            ParserResult result = null;

            try {
                CharSequence buffer = reader.read(file);
                String source = asString(buffer);
                int caretOffset = reader.getCaretOffset(file);
                if (caretOffset != -1 && job.translatedSource != null) {
                    caretOffset = job.translatedSource.getAstOffset(caretOffset);
                }
                Context context = new Context(file, listener, source, caretOffset, job.translatedSource);
                result = parseBuffer(context, Sanitize.NONE);
            } catch (IOException ioe) {
                listener.exception(ioe);
            }

            ParseEvent doneEvent = new ParseEvent(ParseEvent.Kind.PARSE, file, result);
            listener.finished(doneEvent);
        }
    }

    protected PositionManager createPositionManager() {
        return new FortressPositionManager();
    }

    /**
     * Try cleaning up the source buffer around the current offset to increase
     * likelihood of parse success. Initially this method had a lot of
     * logic to determine whether a parse was likely to fail (e.g. invoking
     * the isEndMissing method from bracket completion etc.).
     * However, I am now trying a parse with the real source first, and then
     * only if that fails do I try parsing with sanitized source. Therefore,
     * this method has to be less conservative in ripping out code since it
     * will only be used when the regular source is failing.
     * 
     * @todo Automatically close current statement by inserting ";"
     * @todo Handle sanitizing "new ^" from parse errors
     * @todo Replace "end" insertion fix with "}" insertion
     */
    private boolean sanitizeSource(Context context, Sanitize sanitizing) {

        if (sanitizing == Sanitize.MISSING_END) {
            context.sanitizedSource = context.source + "end";
            int start = context.source.length();
            context.sanitizedRange = new OffsetRange(start, start + 4);
            context.sanitizedContents = "";
            return true;
        }

        int offset = context.caretOffset;

        // Let caretOffset represent the offset of the portion of the buffer we'll be operating on
        if ((sanitizing == Sanitize.ERROR_DOT) || (sanitizing == Sanitize.ERROR_LINE)) {
            offset = context.errorOffset;
        }

        // Don't attempt cleaning up the source if we don't have the buffer position we need
        if (offset == -1) {
            return false;
        }

        // The user might be editing around the given caretOffset.
        // See if it looks modified
        // Insert an end statement? Insert a } marker?
        String doc = context.source;
        if (offset > doc.length()) {
            return false;
        }

        try {
            // Sometimes the offset shows up on the next line
            if (FortressUtils.isRowEmpty(doc, offset) || FortressUtils.isRowWhite(doc, offset)) {
                offset = FortressUtils.getRowStart(doc, offset) - 1;
                if (offset < 0) {
                    offset = 0;
                }
            }

            if (!(FortressUtils.isRowEmpty(doc, offset) || FortressUtils.isRowWhite(doc, offset))) {
                if ((sanitizing == Sanitize.EDITED_LINE) || (sanitizing == Sanitize.ERROR_LINE)) {
                    // See if I should try to remove the current line, since it has text on it.
                    int lineEnd = FortressUtils.getRowLastNonWhite(doc, offset);

                    if (lineEnd != -1) {
                        StringBuilder sb = new StringBuilder(doc.length());
                        int lineStart = FortressUtils.getRowStart(doc, offset);
                        int rest = lineStart + 1;

                        sb.append(doc.substring(0, lineStart));
                        sb.append('#');

                        if (rest < doc.length()) {
                            sb.append(doc.substring(rest, doc.length()));
                        }
                        assert sb.length() == doc.length();

                        context.sanitizedRange = new OffsetRange(lineStart, lineEnd);
                        context.sanitizedSource = sb.toString();
                        context.sanitizedContents = doc.substring(lineStart, lineEnd);
                        return true;
                    }
                } else {
                    assert sanitizing == Sanitize.ERROR_DOT || sanitizing == Sanitize.EDITED_DOT;
                    // Try nuking dots/colons from this line
                    // See if I should try to remove the current line, since it has text on it.
                    int lineStart = FortressUtils.getRowStart(doc, offset);
                    int lineEnd = offset - 1;
                    while (lineEnd >= lineStart && lineEnd < doc.length()) {
                        if (!Character.isWhitespace(doc.charAt(lineEnd))) {
                            break;
                        }
                        lineEnd--;
                    }
                    if (lineEnd > lineStart) {
                        StringBuilder sb = new StringBuilder(doc.length());
                        String line = doc.substring(lineStart, lineEnd + 1);
                        int removeChars = 0;
                        int removeEnd = lineEnd + 1;

                        if (line.endsWith(".") || line.endsWith("(")) { // NOI18N

                            removeChars = 1;
                        } else if (line.endsWith(",")) { // NOI18N                            removeChars = 1;

                            removeChars = 1;
                        } else if (line.endsWith(", ")) { // NOI18N

                            removeChars = 2;
                        } else if (line.endsWith(",)")) { // NOI18N
                            // Handle lone comma in parameter list - e.g.
                            // type "foo(a," -> you end up with "foo(a,|)" which doesn't parse - but
                            // the line ends with ")", not "," !
                            // Just remove the comma

                            removeChars = 1;
                            removeEnd--;
                        } else if (line.endsWith(", )")) { // NOI18N
                            // Just remove the comma

                            removeChars = 1;
                            removeEnd -= 2;
                        } else {
                            // Make sure the line doesn't end with one of the JavaScript keywords
                            // (new, do, etc) - we can't handle that!
                            for (String keyword : FortressUtils.FORTRESS_KEYWORDS) { // reserved words are okay

                                if (line.endsWith(keyword)) {
                                    removeChars = 1;
                                    break;
                                }
                            }
                        }

                        if (removeChars == 0) {
                            return false;
                        }

                        int removeStart = removeEnd - removeChars;

                        sb.append(doc.substring(0, removeStart));

                        for (int i = 0; i < removeChars; i++) {
                            sb.append(' ');
                        }

                        if (removeEnd < doc.length()) {
                            sb.append(doc.substring(removeEnd, doc.length()));
                        }
                        assert sb.length() == doc.length();

                        context.sanitizedRange = new OffsetRange(removeStart, removeEnd);
                        context.sanitizedSource = sb.toString();
                        context.sanitizedContents = doc.substring(removeStart, removeEnd);
                        return true;
                    }
                }
            }
        } catch (BadLocationException ble) {
            Exceptions.printStackTrace(ble);
        }

        return false;
    }

    @SuppressWarnings("fallthrough")
    private FortressParserResult sanitize(final Context context,
            final Sanitize sanitizing) {

        switch (sanitizing) {
            case NEVER:
                return createParseResult(context.file, null, null, computeLinesOffset(context.source));

            case NONE:

                // We've currently tried with no sanitization: try first level
                // of sanitization - removing dots/colons at the edited offset.
                // First try removing the dots or double colons around the failing position
                if (context.caretOffset != -1) {
                    return parseBuffer(context, Sanitize.EDITED_DOT);
                }

            // Fall through to try the next trick
            case EDITED_DOT:

                // We've tried editing the caret location - now try editing the error location
                // (Don't bother doing this if errorOffset==caretOffset since that would try the same
                // source as EDITED_DOT which has no better chance of succeeding...)
                if (context.errorOffset != -1 && context.errorOffset != context.caretOffset) {
                    return parseBuffer(context, Sanitize.ERROR_DOT);
                }

            // Fall through to try the next trick
            case ERROR_DOT:

                // We've tried removing dots - now try removing the whole line at the error position
                if (context.errorOffset != -1) {
                    return parseBuffer(context, Sanitize.ERROR_LINE);
                }

            // Fall through to try the next trick
            case ERROR_LINE:

                // Messing with the error line didn't work - we could try "around" the error line
                // but I'm not attempting that now.
                // Finally try removing the whole line around the user editing position
                // (which could be far from where the error is showing up - but if you're typing
                // say a new "def" statement in a class, this will show up as an error on a mismatched
                // "end" statement rather than here
                if (context.caretOffset != -1) {
                    return parseBuffer(context, Sanitize.EDITED_LINE);
                }

            // Fall through to try the next trick
            case EDITED_LINE:
                return parseBuffer(context, Sanitize.MISSING_END);

            // Fall through for default handling
            case MISSING_END:
            default:
                // We're out of tricks - just return the failed parse result
                return createParseResult(context.file, null, null, computeLinesOffset(context.source));
        }
    }

    protected FortressParserResult parseBuffer(final Context context, final Sanitize sanitizing) {
        boolean sanitizedSource = false;
        String source = context.source;
        if (!((sanitizing == Sanitize.NONE) || (sanitizing == Sanitize.NEVER))) {
            boolean ok = sanitizeSource(context, sanitizing);

            if (ok) {
                assert context.sanitizedSource != null;
                sanitizedSource = true;
                source = context.sanitizedSource;
            } else {
                // Try next trick
                return sanitize(context, sanitizing);
            }
        }

        final boolean ignoreErrors = sanitizedSource;

        Reader in = new StringReader(source);
        String fileName = context.file != null ? context.file.getNameExt() : "<current>";
        Fortress parser = new Fortress(in, fileName);
        context.parser = parser;

        if (sanitizing == Sanitize.NONE) {
            context.errorOffset = -1;
        }

        Node node = null;
        try {
            ParseError error = null;
            Result r = parser.pFile(0);
            if (r.hasValue()) {
                SemanticValue v = (SemanticValue) r;
                node = (Node) v.value;

            } else {
                error = r.parseError();
            }

            if (error != null) {
                if (!ignoreErrors) {
                    int start = 0;
                    if (error.index != -1) {
                        start = error.index;
                    }
                    notifyError(context, "SYNTAX_ERROR", error.msg,
                            start, start, sanitizing, Severity.ERROR, new Object[]{error.index, error});
                }

                System.err.println(error.msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // An internal exception thrown by Parser, just catch it and notify
            notifyError(context, "SYNTAX_ERROR", e.getMessage(),
                    0, 0, sanitizing, Severity.ERROR, new Object[]{e});
        } 


        if (node != null) {
            context.sanitized = sanitizing;
            FortressParserResult r = createParseResult(context.file, node, null, computeLinesOffset(source));
            r.setSanitized(context.sanitized, context.sanitizedRange, context.sanitizedContents);
            r.setSource(source);
            return r;
        } else {
            return sanitize(context, sanitizing);
        }
    }

    private FortressParserResult createParseResult(ParserFile file, Node rootNode, ParserResult.AstTreeNode ast, List<Integer> linesOffset) {
        return new FortressParserResult(this, file, rootNode, ast, linesOffset);
    }
    
    private List<Integer> computeLinesOffset(String source) {       
        int length = source.length();

        List<Integer> linesOffset = new ArrayList<Integer>(length / 25);
        linesOffset.add(0);

        int line = 0;
        for (int i = 0; i < length; i++) {
            if (source.charAt(i) == '\n') {
                // \r comes first so are not a problem...
                linesOffset.add(i);
                line++;
            }
        }
        
        return linesOffset;       
    }

    protected void notifyError(Context context, String key, String message,
            int start, int end, Sanitize sanitizing, Severity severity, Object params) {

        DefaultError error = new DefaultError(key, message, null, context.file.getFileObject(),
                start, end, severity);
        if (params != null) {
            if (params instanceof Object[]) {
                error.setParameters((Object[]) params);
            } else {
                error.setParameters(new Object[]{params});
            }
        }

        context.listener.error(error);

        if (sanitizing == Sanitize.NONE) {
            context.errorOffset = end;
        }
    }

    public PositionManager getPositionManager() {
        return positions;
    }

    /** Attempts to sanitize the input buffer */
    public static enum Sanitize {

        /** Only parse the current file accurately, don't try heuristics */
        NEVER,
        /** Perform no sanitization */
        NONE,
        /** Try to remove the trailing . or :: at the caret line */
        EDITED_DOT,
        /** Try to remove the trailing . or :: at the error position, or the prior
         * line, or the caret line */
        ERROR_DOT,
        /** Try to cut out the error line */
        ERROR_LINE,
        /** Try to cut out the current edited line, if known */
        EDITED_LINE,
        /** Attempt to add an "end" to the end of the buffer to make it compile */
        MISSING_END,
    }

    /** Parsing context */
    public static class Context {

        private Fortress parser;
        private final ParserFile file;
        private final ParseListener listener;
        private int errorOffset;
        private String source;
        private String sanitizedSource;
        private OffsetRange sanitizedRange = OffsetRange.NONE;
        private String sanitizedContents;
        private int caretOffset;
        private Sanitize sanitized = Sanitize.NONE;
        private TranslatedSource translatedSource;

        public Context(ParserFile parserFile, ParseListener listener, String source, int caretOffset, TranslatedSource translatedSource) {
            this.file = parserFile;
            this.listener = listener;
            this.source = source;
            this.caretOffset = caretOffset;
            this.translatedSource = translatedSource;
        }

        @Override
        public String toString() {
            return "FortressParser.Context(" + file.toString() + ")"; // NOI18N

        }

        public OffsetRange getSanitizedRange() {
            return sanitizedRange;
        }

        public Sanitize getSanitized() {
            return sanitized;
        }

        public String getSanitizedSource() {
            return sanitizedSource;
        }

        public int getErrorOffset() {
            return errorOffset;
        }
    }
}
