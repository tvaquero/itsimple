package sourceEditor;

import java.io.*;

import com.Ostermiller.Syntax.Lexer.Lexer;
import com.Ostermiller.Syntax.Lexer.Token;

/* PDDLLexer.java is a generated file.  You probably want to
 * edit SQLLexer.lex to make changes.  Use JFlex to generate it.
 * To generate SQLLexer.java
 * Install <a href="http://jflex.de/">JFlex</a> v1.3.2 or later.
 * Once JFlex is in your classpath run<br>
 * <code>java JFlex.Main PDDLLexer.lex</code><br>
 * You will then have a file called PDDLLexer.java
 */

%%

%public
%class PDDLLexer
%implements Lexer
%function getNextToken
%type Token 

%{
    private int lastToken;
    private int nextState=YYINITIAL;
	private StringBuffer commentBuffer = new StringBuffer();
	private int commentNestCount = 0;
	private int commentStartLine = 0;
	private int commentStartChar = 0;
    
    /** 
     * next Token method that allows you to control if whitespace and comments are
     * returned as tokens.
     */
    public Token getNextToken(boolean returnComments, boolean returnWhiteSpace)throws IOException{
        Token t = getNextToken();
        while (t != null && ((!returnWhiteSpace && t.isWhiteSpace()) || (!returnComments && t.isComment()))){
            t = getNextToken();
        }
        return (t); 
    }

    /**
     * Prints out tokens from a file or System.in.
     * If no arguments are given, System.in will be used for input.
     * If more arguments are given, the first argument will be used as
     * the name of the file to use as input
     *
     * @param args program arguments, of which the first is a filename
     */
    public static void main(String[] args) {
        InputStream in;
        try {
            if (args.length > 0){
                File f = new File(args[0]);
                if (f.exists()){
                    if (f.canRead()){
                        in = new FileInputStream(f);
                    } else {
                        throw new IOException("Could not open " + args[0]);
                    }
                } else {
                    throw new IOException("Could not find " + args[0]);
                }                   
            } else {
                in = System.in;
            }       
            PDDLLexer shredder = new PDDLLexer(in);
            Token t;
            while ((t = shredder.getNextToken()) != null) {
                if (t.getID() != OCLToken.WHITE_SPACE){
                    System.out.println(t);
                }
            }
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    } 

    /**
     * Closes the current input stream, and resets the scanner to read from a new input stream.
	 * All internal variables are reset, the old input stream  cannot be reused
	 * (content of the internal buffer is discarded and lost).
	 * The lexical state is set to the initial state.
     * Subsequent tokens read from the lexer will start with the line, char, and column
     * values given here.
     *
     * @param reader The new input.
     * @param yyline The line number of the first token.
     * @param yychar The position (relative to the start of the stream) of the first token.
     * @param yycolumn The position (relative to the line) of the first token.
     * @throws IOException if an IOExecption occurs while switching readers.
     */
    public void reset(java.io.Reader reader, int yyline, int yychar, int yycolumn) throws IOException{
        yyreset(reader);
        this.yyline = yyline;
		this.yychar = yychar;
		this.yycolumn = yycolumn;
	}
%}

%line
%char
%full
%ignorecase

%state COMMENT

keyword=("define"|"domain"|":requirements"|":strips"|":typing"|":adl"|":equality"|":fluents"|":negative-preconditions"|":disjunctive-preconditions"|":existential-preconditions"|":universal-preconditions"|":quantified-preconditions"|":conditional-effects"|":durative-actions"|":derived-predicates"|":timed-initial-literals"|":types"|":predicates"|":functions"|":constants"|":constraints"|":action"|":durative-action"|":parameters"|":vars"|":duration"|":precondition"|":condition"|":effect"|"problem"|":domain"|":objects"|":init"|":goal"|":metric"|":plan-metric"|":derived"|"either"|"exists"|"forall"|"when"|"assign"|"imply"|"increase"|"decrease"|"minimize"|"maximize"|"total-time"|"and"|"or"|"not"|"sometime"|"always"|"preference"|"sometime-after"|"sometime-before"|"always-within"|"at-most-once"|"includes"|"including"|"excludes"|"excluding")
whitespace=([ \r\n\t\f])
identifier=([^ \r\n\t\f\+\-\*\/\<\>\=\~\!\@\#\%\^\&\|\`\'\"\~\?\$\(\)\[\]\,\;\*\.\_0-9][^ \r\n\t\f\+\-\*\/\<\>\=\~\!\@\#\%\^\&\|\`\'\"\~\?\$\(\)\[\]\,\;\:\*\.]*)
digit=([0-9])
digits=({digit}+)
positionalparams=("$"{digits})
separator=([\(\)\[\]\,\;\*]|{positionalparams})
operator=([\+\-\*\/\<\<=\>\>=\<>\=\not\if\then\else\endif\and\or\xor\implies])
integer=({digits})
variable=("?"[^ \(\)\-]*)
string=([\'](([^\r\n\']|[\\][\'])*)[\'])
bitstring=("B"[\']([01]+)[\'])
stringerror=([\'](([^\r\n\']|[\\][\'])*)[\r\n])
bitstringerror1=("B"[\']([^01\r\n]*)[\'])
bitstringerror2=("B"[\'](([^\r\n\']|[\\][\'])*)[\r\n])
floatpoint=(({digits}"."({digits}?)("E"[+-]{digits})?)|(({digits}?)"."{digits}("E"[+-]{digits})?)|({digits}"E"[+-]{digits}))
linecomment=(";;"[^\r\n]*)
%% 

<YYINITIAL> {linecomment} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.COMMENT_END_OF_LINE;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {keyword} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.RESERVED_WORD;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {variable} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.VARIABLE;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {separator} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.SEPARATOR;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {operator} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.OPERATOR;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {bitstring} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.LITERAL_BIT_STRING;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {bitstringerror1} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.ERROR_UNCLOSED_BIT_STRING;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {bitstringerror2} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.ERROR_BAD_BIT_STRING;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {identifier} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.IDENTIFIER;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {integer} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.LITERAL_INTEGER;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {string} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.LITERAL_STRING;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}


<YYINITIAL> {stringerror} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.ERROR_UNCLOSED_STRING;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {floatpoint} {
    nextState = YYINITIAL;
	lastToken = PDDLToken.LITERAL_FLOAT;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL> {whitespace}* {
    nextState = YYINITIAL;
	lastToken = PDDLToken.WHITE_SPACE;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}

<YYINITIAL, COMMENT> [^] {
    nextState = YYINITIAL;
	lastToken = PDDLToken.ERROR;
    String text = yytext();
	PDDLToken t = (new PDDLToken(lastToken,text,yyline,yychar,yychar+text.length(),nextState));
	yybegin(nextState);
	return(t);
}
