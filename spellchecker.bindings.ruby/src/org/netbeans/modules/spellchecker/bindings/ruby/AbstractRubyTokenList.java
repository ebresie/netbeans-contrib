/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.spellchecker.bindings.ruby;

import javax.swing.event.ChangeListener;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.netbeans.api.lexer.TokenHierarchy;
import org.netbeans.api.lexer.TokenId;
import org.netbeans.api.lexer.TokenSequence;
import org.netbeans.modules.spellchecker.spi.language.TokenList;
import org.openide.ErrorManager;

/**
 * Tokenize Ruby text for spell checking. Based on corresponding
 * JavaTokenList by Jan Lahoda.
 *
 * @todo Check spelling in documentation sections
 * @todo Suppress spelling checks on :rdoc: modifiers
 * @todo Remove surrounding +, _, * on spelling words
 * @todo Spell check string literals?
 * @todo Spell check constant names and method names?
 *
 *
 *
 * @author Tor Norbye
 */
// TODO - rename AbstractTokenList
public abstract class AbstractRubyTokenList implements TokenList {
    private Document doc;

    /** Creates a new instance of RubyTokenList */
    AbstractRubyTokenList(Document doc) {
        this.doc = doc;
    }

    public void setStartOffset(int offset) {
        currentBlockText = null;
        currentOffsetInComment = (-1);
        this.startOffset = this.nextBlockStart = offset;
    }

    public int getCurrentWordStartOffset() {
        return currentWordOffset;
    }

    public CharSequence getCurrentWordText() {
        return currentWord;
    }

    public boolean nextWord() {
        boolean hasNext = nextWordImpl();

        while (hasNext && (currentWordOffset + currentWord.length()) < startOffset) {
            hasNext = nextWordImpl();
        }

        return hasNext;
    }

    private int[] findNextSpellSpan() throws BadLocationException {
        TokenHierarchy<Document> h = TokenHierarchy.get(doc);
        TokenSequence<? extends TokenId> ts = h.tokenSequence();

        return findNextSpellSpan(ts, nextBlockStart);
    }

    /** Given a sequence of Ruby tokens, return the next span of eligible comments */
    protected abstract int[] findNextSpellSpan(TokenSequence<? extends TokenId> ts, int offset) throws BadLocationException;

    private boolean nextWordImpl() {
        try {
            while (true) {
                if (currentBlockText == null) {
                    int[] span = findNextSpellSpan();

                    if (span[0] == (-1)) {
                        return false;
                    }

                    currentBlockStart = span[0];
                    currentBlockText = doc.getText(span[0], span[1] - span[0]);
                    currentOffsetInComment = 0;

                    nextBlockStart = span[1];
                }

                String pairTag = null;
                Pair<CharSequence, Integer> data = wordBroker(currentBlockText, currentOffsetInComment, false);

                while (data != null) {
                    currentOffsetInComment = data.b + data.a.length();

                    if (pairTag == null) {
                        if (Character.isLetter(data.a.charAt(0)) && !isIdentifierLike(data.a)) {
                            //TODO: check for identifiers:
                            currentWordOffset = currentBlockStart + data.b;
                            currentWord = data.a;
                            return true;
                        }

                        switch (data.a.charAt(0)) {
                            case '<':
                                if (startsWith(data.a, "<a ")) {
                                    pairTag = "</a>";
                                }
                                if (startsWith(data.a, "<code>")) {
                                    pairTag = "</code>";
                                }
                                if (startsWith(data.a, "<pre>")) {
                                    pairTag = "</pre>";
                                }
                                break;
                            case '{':
                                pairTag = "}";
                                break;
                        }
                    } else {
                        if (pairTag.contentEquals(data.a)) {
                            pairTag = null;
                        }
                    }

                    data = wordBroker(currentBlockText, currentOffsetInComment, false);
                }

                currentBlockText = null;
            }
        } catch (BadLocationException e) {
            ErrorManager.getDefault().notify(e);
            return false;
        }
    }

    static boolean startsWith(CharSequence where, String withWhat) {
        if (where.length() >= withWhat.length()) {
            return withWhat.contentEquals(where.subSequence(0, withWhat.length()));
        }

        return false;
    }

    static boolean isIdentifierLike(CharSequence s) {
        boolean hasCapitalsInside = false;
        boolean hasUnderlinesInside = false;
        int offset = 1;

        while (offset < s.length() && !hasCapitalsInside) {
            char c = s.charAt(offset);
            if (c == '_') {
                hasUnderlinesInside = true;
            } else {
                hasCapitalsInside |= Character.isUpperCase(s.charAt(offset));
            }

            offset++;
        }

        return hasCapitalsInside || hasUnderlinesInside;
    }
    private int currentBlockStart;
    private int nextBlockStart;
    private String currentBlockText;
    private int currentOffsetInComment;
    private int currentWordOffset;
    private CharSequence currentWord;
    private int startOffset;

    static boolean isLetter(char c) {
        return Character.isLetter(c) || c == '\'';
    }

    static Pair<CharSequence, Integer> wordBroker(CharSequence start, int offset, boolean treatSpecialCharactersAsLetterInsideWords) {
        int state = 0;
        int offsetStart = offset;

        while (start.length() > offset) {
            char current = start.charAt(offset);

            switch (state) {
                case 0:
                    if (isLetter(current)) {
                        state = 1;
                        offsetStart = offset;
                        break;
                    }
                    if (current == '@' || current == '#') {
                        state = 2;
                        offsetStart = offset;
                        break;
                    }
                    if (current == '<') {
                        state = 3;
                        offsetStart = offset;
                        break;
                    }
                    if (current == '\n' || current == '}') {
                        return new Pair<CharSequence, Integer>(start.subSequence(offset, offset + 1), offset);
                    }
                    if (current == '{') {
                        state = 4;
                        offsetStart = offset;
                        break;
                    }
                    break;
                case 1:
                    if (!isLetter(current) && ((current != '.' && current != '#') || !treatSpecialCharactersAsLetterInsideWords)) {
                        return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset), offsetStart);
                    }

                    break;
                case 2:
                    if (!isLetter(current)) {
                        return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset), offsetStart);
                    }

                    break;
                case 3:
                    if (current == '>') {
                        return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset + 1), offsetStart);
                    }

                    break;
                case 4:
                    if (current == '@') {
                        state = 2;
                        break;
                    }

                    offset--;
                    state = 0;
                    break;
            }

            offset++;
        }

        if (offset > offsetStart) {
            return new Pair<CharSequence, Integer>(start.subSequence(offsetStart, offset), offsetStart);
        } else {
            return null;
        }
    }

    public void addChangeListener(ChangeListener l) {
        //ignored...
    }

    public void removeChangeListener(ChangeListener l) {
        //ignored...
    }
}