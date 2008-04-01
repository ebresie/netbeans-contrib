package com.sun.fortress.nodes;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import com.sun.fortress.nodes_util.*;
import com.sun.fortress.parser_util.*;
import com.sun.fortress.parser_util.precedence_opexpr.*;
import com.sun.fortress.useful.*;
import edu.rice.cs.plt.tuple.Option;

/**
 * Class While, a component of the Node composite hierarchy.
 * Note: null is not allowed as a value for any field.
 * @version  Generated automatically by ASTGen at Tue Mar 11 23:25:23 CST 2008
 */
public class While extends DelimitedExpr {
    private final Expr _test;
    private final Do _body;

    /**
     * Constructs a While.
     * @throws java.lang.IllegalArgumentException  If any parameter to the constructor is null.
     */
    public While(Span in_span, boolean in_parenthesized, Expr in_test, Do in_body) {
        super(in_span, in_parenthesized);
        if (in_test == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'test' to the While constructor was null");
        }
        _test = in_test;
        if (in_body == null) {
            throw new java.lang.IllegalArgumentException("Parameter 'body' to the While constructor was null");
        }
        _body = in_body;
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public While(Span in_span, Expr in_test, Do in_body) {
        this(in_span, false, in_test, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public While(boolean in_parenthesized, Expr in_test, Do in_body) {
        this(new Span(), in_parenthesized, in_test, in_body);
    }

    /**
     * A constructor with some fields provided by default values.
     */
    public While(Expr in_test, Do in_body) {
        this(new Span(), false, in_test, in_body);
    }

    /**
     * Empty constructor, for reflective access.  Clients are 
     * responsible for manually instantiating each field.
     */
    protected While() {
        _test = null;
        _body = null;
    }

    final public Expr getTest() { return _test; }
    final public Do getBody() { return _body; }

    public <RetType> RetType accept(NodeVisitor<RetType> visitor) { return visitor.forWhile(this); }
    public void accept(NodeVisitor_void visitor) { visitor.forWhile(this); }

    /** Generate a human-readable representation that can be deserialized. */
    public java.lang.String serialize() {
        java.io.StringWriter w = new java.io.StringWriter();
        serialize(w);
        return w.toString();
    }
    /** Generate a human-readable representation that can be deserialized. */
    public void serialize(java.io.Writer writer) {
        outputHelp(new TabPrintWriter(writer, 2), true);
    }

    public void outputHelp(TabPrintWriter writer, boolean lossless) {
        writer.print("While:");
        writer.indent();

        Span temp_span = getSpan();
        writer.startLine();
        writer.print("span = ");
        if (lossless) {
            writer.printSerialized(temp_span);
            writer.print(" ");
            writer.printEscaped(temp_span);
        } else { writer.print(temp_span); }

        boolean temp_parenthesized = isParenthesized();
        writer.startLine();
        writer.print("parenthesized = ");
        writer.print(temp_parenthesized);

        Expr temp_test = getTest();
        writer.startLine();
        writer.print("test = ");
        temp_test.outputHelp(writer, lossless);

        Do temp_body = getBody();
        writer.startLine();
        writer.print("body = ");
        temp_body.outputHelp(writer, lossless);
        writer.unindent();
    }

    /**
     * Implementation of equals that is based on the values of the fields of the
     * object. Thus, two objects created with identical parameters will be equal.
     */
    public boolean equals(java.lang.Object obj) {
        if (obj == null) return false;
        if ((obj.getClass() != this.getClass()) || (obj.hashCode() != this.hashCode())) {
            return false;
        } else {
            While casted = (While) obj;
            boolean temp_parenthesized = isParenthesized();
            boolean casted_parenthesized = casted.isParenthesized();
            if (!(temp_parenthesized == casted_parenthesized)) return false;
            Expr temp_test = getTest();
            Expr casted_test = casted.getTest();
            if (!(temp_test == casted_test || temp_test.equals(casted_test))) return false;
            Do temp_body = getBody();
            Do casted_body = casted.getBody();
            if (!(temp_body == casted_body || temp_body.equals(casted_body))) return false;
            return true;
        }
    }

    /**
     * Implementation of hashCode that is consistent with equals.  The value of
     * the hashCode is formed by XORing the hashcode of the class object with
     * the hashcodes of all the fields of the object.
     */
    public int generateHashCode() {
        int code = getClass().hashCode();
        boolean temp_parenthesized = isParenthesized();
        code ^= temp_parenthesized ? 1231 : 1237;
        Expr temp_test = getTest();
        code ^= temp_test.hashCode();
        Do temp_body = getBody();
        code ^= temp_body.hashCode();
        return code;
    }
}