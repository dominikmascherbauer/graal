/*
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of
 * conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other materials provided
 * with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
// Checkstyle: stop
//@formatter:off
package com.oracle.truffle.llvm.runtime.debug.debugexpr.parser.antlr;

// DO NOT MODIFY - generated from DebugExpression.g4 using "mx create-parsers"

import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenSource;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.oracle.truffle.llvm.runtime.ArithmeticOperation;
import com.oracle.truffle.llvm.runtime.debug.debugexpr.nodes.DebugExprNodeFactory;
import com.oracle.truffle.llvm.runtime.debug.debugexpr.nodes.DebugExprNodeFactory.CompareKind;
import com.oracle.truffle.llvm.runtime.debug.debugexpr.nodes.DebugExprTypeofNode;
import com.oracle.truffle.llvm.runtime.debug.debugexpr.nodes.DebugExpressionPair;
import com.oracle.truffle.llvm.runtime.debug.debugexpr.parser.DebugExprType;
import com.oracle.truffle.llvm.runtime.nodes.api.LLVMExpressionNode;

@SuppressWarnings("all")
public class DebugExpressionParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, DIGIT=31, 
		CR=32, LF=33, SINGLECOMMA=34, QUOTE=35, IDENT=36, NUMBER=37, FLOATNUMBER=38, 
		CHARCONST=39, LAPR=40, ASTERISC=41, SIGNED=42, UNSIGNED=43, INT=44, LONG=45, 
		SHORT=46, FLOAT=47, DOUBLE=48, CHAR=49, TYPEOF=50, WS=51;
	public static final int
		RULE_debugExpr = 0, RULE_primExpr = 1, RULE_designator = 2, RULE_actPars = 3, 
		RULE_unaryExpr = 4, RULE_unaryOP = 5, RULE_castExpr = 6, RULE_multExpr = 7, 
		RULE_addExpr = 8, RULE_shiftExpr = 9, RULE_relExpr = 10, RULE_eqExpr = 11, 
		RULE_andExpr = 12, RULE_xorExpr = 13, RULE_orExpr = 14, RULE_logAndExpr = 15, 
		RULE_logOrExpr = 16, RULE_expr = 17, RULE_dType = 18, RULE_baseType = 19;
	public static final String[] ruleNames = {
		"debugExpr", "primExpr", "designator", "actPars", "unaryExpr", "unaryOP", 
		"castExpr", "multExpr", "addExpr", "shiftExpr", "relExpr", "eqExpr", "andExpr", 
		"xorExpr", "orExpr", "logAndExpr", "logOrExpr", "expr", "dType", "baseType"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "')'", "'['", "']'", "'.'", "'->'", "','", "'sizeof'", "'+'", "'-'", 
		"'~'", "'!'", "'/'", "'%'", "'>>'", "'<<'", "'<'", "'>'", "'<='", "'>='", 
		"'=='", "'!='", "'&'", "'^'", "'|'", "'&&'", "'||'", "'?'", "':'", "'void'", 
		"'long'", null, "'\r'", "'\n'", "'''", "'\"'", null, null, null, null, 
		"'('", "'*'", "'signed'", "'unsigned'", "'int'", "'LONG'", "'short'", 
		"'float'", "'double'", "'char'", "'typeof'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, "DIGIT", "CR", "LF", "SINGLECOMMA", 
		"QUOTE", "IDENT", "NUMBER", "FLOATNUMBER", "CHARCONST", "LAPR", "ASTERISC", 
		"SIGNED", "UNSIGNED", "INT", "LONG", "SHORT", "FLOAT", "DOUBLE", "CHAR", 
		"TYPEOF", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "DebugExpression.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }



	private LLVMExpressionNode astRoot = null;
	private DebugExprNodeFactory NF = null;

	public boolean IsCast() {
	    TokenSource tokenSource = _input.getTokenSource();
		Token peek = tokenSource.nextToken();
		if (peek.getType() == LAPR) {
		    while(peek.getType() == ASTERISC) peek = tokenSource.nextToken();
		    int tokenType = peek.getType();
		    if(tokenType == SIGNED || tokenType == UNSIGNED || tokenType == INT || tokenType == LONG
		        || tokenType == CHAR || tokenType == SHORT || tokenType == FLOAT || tokenType == DOUBLE
		        || tokenType == TYPEOF ) return true;
		}
		return false;
	}

	public void setNodeFactory(DebugExprNodeFactory nodeFactory) {
		if (NF == null) NF = nodeFactory;
	}

	public int GetErrors() {
		return _syntaxErrors;
	}

	public LLVMExpressionNode GetASTRoot() {return astRoot; }


	public DebugExpressionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class DebugExprContext extends ParserRuleContext {
		public ExprContext expr;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DebugExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_debugExpr; }
	}

	public final DebugExprContext debugExpr() throws RecognitionException {
		DebugExprContext _localctx = new DebugExprContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_debugExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p = null;
			  
			{
			{
			setState(41);
			_localctx.expr = expr();
			 p = _localctx.expr.p; 
			}
			if(_syntaxErrors == 0) astRoot = p.getNode();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PrimExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public Token t;
		public ExprContext expr;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(DebugExpressionParser.IDENT, 0); }
		public TerminalNode NUMBER() { return getToken(DebugExpressionParser.NUMBER, 0); }
		public TerminalNode FLOATNUMBER() { return getToken(DebugExpressionParser.FLOATNUMBER, 0); }
		public TerminalNode CHARCONST() { return getToken(DebugExpressionParser.CHARCONST, 0); }
		public PrimExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primExpr; }
	}

	public final PrimExprContext primExpr() throws RecognitionException {
		PrimExprContext _localctx = new PrimExprContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_primExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(59);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENT:
				{
				setState(46);
				_localctx.t = match(IDENT);
				 _localctx.p =  NF.createVarNode(_localctx.t.getText()); 
				}
				break;
			case NUMBER:
				{
				setState(48);
				_localctx.t = match(NUMBER);
				 _localctx.p =  NF.createIntegerConstant(Integer.parseInt(_localctx.t.getText())); 
				}
				break;
			case FLOATNUMBER:
				{
				setState(50);
				_localctx.t = match(FLOATNUMBER);
				 _localctx.p =  NF.createFloatConstant(Float.parseFloat(_localctx.t.getText())); 
				}
				break;
			case CHARCONST:
				{
				setState(52);
				_localctx.t = match(CHARCONST);
				 _localctx.p =  NF.createCharacterConstant(_localctx.t.getText()); 
				}
				break;
			case LAPR:
				{
				setState(54);
				match(LAPR);
				setState(55);
				_localctx.expr = expr();
				setState(56);
				match(T__0);
				 _localctx.p =  _localctx.expr.p; 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DesignatorContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public PrimExprContext primExpr;
		public ExprContext expr;
		public ActParsContext actPars;
		public Token t;
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public PrimExprContext primExpr() {
			return getRuleContext(PrimExprContext.class,0);
		}
		public ActParsContext actPars() {
			return getRuleContext(ActParsContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(DebugExpressionParser.IDENT, 0); }
		public DesignatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_designator; }
	}

	public final DesignatorContext designator() throws RecognitionException {
		DesignatorContext _localctx = new DesignatorContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_designator);
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair idxPair = null;
			  List <DebugExpressionPair> list;
			  DebugExpressionPair prev = null;
			  
			setState(65);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(62);
				_localctx.primExpr = primExpr();
				 prev = _localctx.primExpr.p; 
				}
				break;
			}
			setState(84);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				{
				setState(67);
				match(T__1);
				setState(68);
				_localctx.expr = expr();
				 idxPair = _localctx.expr.p; 
				setState(70);
				match(T__2);
				 _localctx.p =  NF.createArrayElement(prev, idxPair); 
				}
				break;
			case LAPR:
				{
				{
				setState(73);
				_localctx.actPars = actPars();
				 list = _localctx.actPars.l; 
				}
				 _localctx.p =  NF.createFunctionCall(prev, list); 
				}
				break;
			case T__3:
				{
				setState(78);
				match(T__3);
				{
				setState(79);
				_localctx.t = match(IDENT);
				}
				 _localctx.p =  NF.createObjectMember(prev, _localctx.t.getText()); 
				}
				break;
			case T__4:
				{
				setState(81);
				match(T__4);
				{
				setState(82);
				_localctx.t = match(IDENT);
				}
				 _localctx.p =  NF.createObjectPointerMember(prev, _localctx.t.getText()); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ActParsContext extends ParserRuleContext {
		public List l;
		public ExprContext expr;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ActParsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actPars; }
	}

	public final ActParsContext actPars() throws RecognitionException {
		ActParsContext _localctx = new ActParsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_actPars);
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair p2 = null;
			  _localctx.l =  new LinkedList<DebugExpressionPair>();
			  
			setState(87);
			match(LAPR);
			{
			setState(88);
			_localctx.expr = expr();
			 p1 = _localctx.expr.p; 
			}
			 _localctx.l.add(p1); 
			setState(92);
			match(T__5);
			{
			setState(93);
			_localctx.expr = expr();
			 p2 = _localctx.expr.p; 
			}
			 _localctx.l.add(p2); 
			setState(97);
			match(T__0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public DesignatorContext designator;
		public UnaryOPContext unaryOP;
		public CastExprContext castExpr;
		public DTypeContext dType;
		public DesignatorContext designator() {
			return getRuleContext(DesignatorContext.class,0);
		}
		public UnaryOPContext unaryOP() {
			return getRuleContext(UnaryOPContext.class,0);
		}
		public CastExprContext castExpr() {
			return getRuleContext(CastExprContext.class,0);
		}
		public DTypeContext dType() {
			return getRuleContext(DTypeContext.class,0);
		}
		public UnaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryExpr; }
	}

	public final UnaryExprContext unaryExpr() throws RecognitionException {
		UnaryExprContext _localctx = new UnaryExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_unaryExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair prev = null;
			  char kind = '\0';
			  DebugExprType typeP = null;
			  
			setState(120);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
			case T__3:
			case T__4:
			case IDENT:
			case NUMBER:
			case FLOATNUMBER:
			case CHARCONST:
			case LAPR:
				{
				setState(100);
				_localctx.designator = designator();
				 prev = _localctx.designator.p; 
				 _localctx.p =  prev; 
				}
				break;
			case T__7:
			case T__8:
			case T__9:
			case T__10:
			case ASTERISC:
				{
				{
				setState(104);
				_localctx.unaryOP = unaryOP();
				 kind = _localctx.unaryOP.kind; 
				}
				{
				setState(107);
				_localctx.castExpr = castExpr();
				 prev = _localctx.castExpr.p; 
				}
				 _localctx.p =  NF.createUnaryOpNode(prev, kind); 
				}
				break;
			case T__6:
				{
				setState(112);
				match(T__6);
				setState(113);
				match(LAPR);
				{
				setState(114);
				_localctx.dType = dType();
				 typeP = _localctx.dType.ty; 
				}
				setState(117);
				match(T__0);
				 _localctx.p =  NF.createSizeofNode(typeP); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UnaryOPContext extends ParserRuleContext {
		public char kind;
		public Token t;
		public UnaryOPContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unaryOP; }
	}

	public final UnaryOPContext unaryOP() throws RecognitionException {
		UnaryOPContext _localctx = new UnaryOPContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_unaryOP);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			_localctx.t = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << ASTERISC))) != 0)) ) {
				_localctx.t = _errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			 _localctx.kind =  _localctx.t.getText().charAt(0); 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class CastExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public DTypeContext dType;
		public Token t;
		public UnaryExprContext unaryExpr;
		public UnaryExprContext unaryExpr() {
			return getRuleContext(UnaryExprContext.class,0);
		}
		public DTypeContext dType() {
			return getRuleContext(DTypeContext.class,0);
		}
		public TerminalNode IDENT() { return getToken(DebugExpressionParser.IDENT, 0); }
		public CastExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_castExpr; }
	}

	public final CastExprContext castExpr() throws RecognitionException {
		CastExprContext _localctx = new CastExprContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_castExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExprType typeP = null;
			  DebugExprTypeofNode typeNode = null;
			  DebugExpressionPair prev;
			  
			if(IsCast())
			{
			setState(127);
			match(LAPR);
			setState(136);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				{
				setState(128);
				_localctx.dType = dType();
				 typeP = _localctx.dType.ty; 
				}
				break;
			case 2:
				{
				setState(131);
				match(TYPEOF);
				setState(132);
				match(LAPR);
				setState(133);
				_localctx.t = match(IDENT);
				 typeNode = NF.createTypeofNode(_localctx.t.getText()); 
				setState(135);
				match(T__0);
				}
				break;
			}
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__0) {
				{
				setState(138);
				match(T__0);
				}
			}

			}
			setState(141);
			_localctx.unaryExpr = unaryExpr();
			prev = _localctx.unaryExpr.p;
			 if (typeP != null) { _localctx.p =  NF.createCastIfNecessary(prev, typeP); }
			                                                      if (typeNode != null) { _localctx.p =  NF.createPointerCastNode(prev, typeNode);} 
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MultExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public CastExprContext castExpr;
		public List<CastExprContext> castExpr() {
			return getRuleContexts(CastExprContext.class);
		}
		public CastExprContext castExpr(int i) {
			return getRuleContext(CastExprContext.class,i);
		}
		public MultExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_multExpr; }
	}

	public final MultExprContext multExpr() throws RecognitionException {
		MultExprContext _localctx = new MultExprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_multExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(149);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LAPR) {
				{
				setState(146);
				_localctx.castExpr = castExpr();
				 prev = _localctx.castExpr.p; 
				}
			}

			setState(169);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ASTERISC:
				{
				setState(151);
				match(ASTERISC);
				{
				setState(152);
				_localctx.castExpr = castExpr();
				 p1 = _localctx.castExpr.p; 
				}
				 _localctx.p =  NF.createArithmeticOp(ArithmeticOperation.MUL, prev, p1); 
				}
				break;
			case T__11:
				{
				setState(157);
				match(T__11);
				{
				setState(158);
				_localctx.castExpr = castExpr();
				 p1 = _localctx.castExpr.p; 
				}
				 _localctx.p =  NF.createDivNode(prev, p1); 
				}
				break;
			case T__12:
				{
				setState(163);
				match(T__12);
				{
				setState(164);
				_localctx.castExpr = castExpr();
				 p1 = _localctx.castExpr.p; 
				}
				 _localctx.p =  NF.createDivNode(prev, p1); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AddExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public MultExprContext multExpr;
		public List<MultExprContext> multExpr() {
			return getRuleContexts(MultExprContext.class);
		}
		public MultExprContext multExpr(int i) {
			return getRuleContext(MultExprContext.class,i);
		}
		public AddExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addExpr; }
	}

	public final AddExprContext addExpr() throws RecognitionException {
		AddExprContext _localctx = new AddExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_addExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(175);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__11) | (1L << T__12) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(172);
				_localctx.multExpr = multExpr();
				 prev = _localctx.multExpr.p; 
				}
			}

			setState(189);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				{
				setState(177);
				match(T__7);
				{
				setState(178);
				_localctx.multExpr = multExpr();
				 p1 = _localctx.multExpr.p; 
				}
				 _localctx.p =  NF.createArithmeticOp(ArithmeticOperation.ADD, prev, p1); 
				}
				break;
			case T__8:
				{
				setState(183);
				match(T__8);
				{
				setState(184);
				_localctx.multExpr = multExpr();
				 p1 = _localctx.multExpr.p; 
				}
				 _localctx.p =  NF.createArithmeticOp(ArithmeticOperation.SUB, prev, p1); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ShiftExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public AddExprContext addExpr;
		public List<AddExprContext> addExpr() {
			return getRuleContexts(AddExprContext.class);
		}
		public AddExprContext addExpr(int i) {
			return getRuleContext(AddExprContext.class,i);
		}
		public ShiftExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_shiftExpr; }
	}

	public final ShiftExprContext shiftExpr() throws RecognitionException {
		ShiftExprContext _localctx = new ShiftExprContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_shiftExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(192);
				_localctx.addExpr = addExpr();
				 prev = _localctx.addExpr.p; 
				}
			}

			setState(209);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__13:
				{
				setState(197);
				match(T__13);
				{
				setState(198);
				_localctx.addExpr = addExpr();
				 p1 = _localctx.addExpr.p; 
				}
				 _localctx.p =  NF.createShiftLeft(prev, p1); 
				}
				break;
			case T__14:
				{
				setState(203);
				match(T__14);
				{
				setState(204);
				_localctx.addExpr = addExpr();
				 p1 = _localctx.addExpr.p; 
				}
				 _localctx.p =  NF.createShiftRight(prev, p1); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class RelExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public ShiftExprContext shiftExpr;
		public List<ShiftExprContext> shiftExpr() {
			return getRuleContexts(ShiftExprContext.class);
		}
		public ShiftExprContext shiftExpr(int i) {
			return getRuleContext(ShiftExprContext.class,i);
		}
		public RelExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relExpr; }
	}

	public final RelExprContext relExpr() throws RecognitionException {
		RelExprContext _localctx = new RelExprContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_relExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(215);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(212);
				_localctx.shiftExpr = shiftExpr();
				 prev = _localctx.shiftExpr.p; 
				}
			}

			setState(241);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__15:
				{
				setState(217);
				match(T__15);
				{
				setState(218);
				_localctx.shiftExpr = shiftExpr();
				 p1 = _localctx.shiftExpr.p; 
				}
				 _localctx.p =  NF.createCompareNode(prev, CompareKind.LT, p1); 
				}
				break;
			case T__16:
				{
				setState(223);
				match(T__16);
				{
				setState(224);
				_localctx.shiftExpr = shiftExpr();
				 p1 = _localctx.shiftExpr.p; 
				}
				 _localctx.p =  NF.createCompareNode(prev, CompareKind.GT, p1); 
				}
				break;
			case T__17:
				{
				setState(229);
				match(T__17);
				{
				setState(230);
				_localctx.shiftExpr = shiftExpr();
				 p1 = _localctx.shiftExpr.p; 
				}
				 _localctx.p =  NF.createCompareNode(prev, CompareKind.LE, p1); 
				}
				break;
			case T__18:
				{
				setState(235);
				match(T__18);
				{
				setState(236);
				_localctx.shiftExpr = shiftExpr();
				 p1 = _localctx.shiftExpr.p; 
				}
				 _localctx.p =  NF.createCompareNode(prev, CompareKind.GE, p1); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EqExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public RelExprContext relExpr;
		public List<RelExprContext> relExpr() {
			return getRuleContexts(RelExprContext.class);
		}
		public RelExprContext relExpr(int i) {
			return getRuleContext(RelExprContext.class,i);
		}
		public EqExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eqExpr; }
	}

	public final EqExprContext eqExpr() throws RecognitionException {
		EqExprContext _localctx = new EqExprContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_eqExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(247);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(244);
				_localctx.relExpr = relExpr();
				 prev = _localctx.relExpr.p; 
				}
			}

			setState(261);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__19:
				{
				setState(249);
				match(T__19);
				{
				setState(250);
				_localctx.relExpr = relExpr();
				 p1 = _localctx.relExpr.p; 
				}
				 _localctx.p =  NF.createCompareNode(prev, CompareKind.EQ, p1); 
				}
				break;
			case T__20:
				{
				setState(255);
				match(T__20);
				{
				setState(256);
				_localctx.relExpr = relExpr();
				 p1 = _localctx.relExpr.p; 
				}
				 _localctx.p =  NF.createCompareNode(prev, CompareKind.NE, p1); 
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AndExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public EqExprContext eqExpr;
		public List<EqExprContext> eqExpr() {
			return getRuleContexts(EqExprContext.class);
		}
		public EqExprContext eqExpr(int i) {
			return getRuleContext(EqExprContext.class,i);
		}
		public AndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExpr; }
	}

	public final AndExprContext andExpr() throws RecognitionException {
		AndExprContext _localctx = new AndExprContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_andExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(267);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(264);
				_localctx.eqExpr = eqExpr();
				 prev = _localctx.eqExpr.p; 
				}
			}

			{
			setState(269);
			match(T__21);
			{
			setState(270);
			_localctx.eqExpr = eqExpr();
			 p1 = _localctx.eqExpr.p; 
			}
			 _localctx.p =  NF.createArithmeticOp(ArithmeticOperation.AND, prev, p1); 
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class XorExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public AndExprContext andExpr;
		public List<AndExprContext> andExpr() {
			return getRuleContexts(AndExprContext.class);
		}
		public AndExprContext andExpr(int i) {
			return getRuleContext(AndExprContext.class,i);
		}
		public XorExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xorExpr; }
	}

	public final XorExprContext xorExpr() throws RecognitionException {
		XorExprContext _localctx = new XorExprContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_xorExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(279);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(276);
				_localctx.andExpr = andExpr();
				 prev = _localctx.andExpr.p; 
				}
			}

			{
			setState(281);
			match(T__22);
			{
			setState(282);
			_localctx.andExpr = andExpr();
			 p1 = _localctx.andExpr.p; 
			}
			 _localctx.p =  NF.createArithmeticOp(ArithmeticOperation.XOR, prev, p1); 
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class OrExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public XorExprContext xorExpr;
		public List<XorExprContext> xorExpr() {
			return getRuleContexts(XorExprContext.class);
		}
		public XorExprContext xorExpr(int i) {
			return getRuleContext(XorExprContext.class,i);
		}
		public OrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExpr; }
	}

	public final OrExprContext orExpr() throws RecognitionException {
		OrExprContext _localctx = new OrExprContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_orExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(288);
				_localctx.xorExpr = xorExpr();
				 prev = _localctx.xorExpr.p; 
				}
			}

			{
			setState(293);
			match(T__23);
			{
			setState(294);
			_localctx.xorExpr = xorExpr();
			 p1 = _localctx.xorExpr.p; 
			}
			 _localctx.p =  NF.createArithmeticOp(ArithmeticOperation.OR, prev, p1); 
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LogAndExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public OrExprContext orExpr;
		public List<OrExprContext> orExpr() {
			return getRuleContexts(OrExprContext.class);
		}
		public OrExprContext orExpr(int i) {
			return getRuleContext(OrExprContext.class,i);
		}
		public LogAndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logAndExpr; }
	}

	public final LogAndExprContext logAndExpr() throws RecognitionException {
		LogAndExprContext _localctx = new LogAndExprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_logAndExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(300);
				_localctx.orExpr = orExpr();
				 prev = _localctx.orExpr.p; 
				}
			}

			{
			setState(305);
			match(T__24);
			{
			setState(306);
			_localctx.orExpr = orExpr();
			 p1 = _localctx.orExpr.p; 
			}
			 _localctx.p =  NF.createLogicalAndNode(prev, p1); 
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LogOrExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public LogAndExprContext logAndExpr;
		public List<LogAndExprContext> logAndExpr() {
			return getRuleContexts(LogAndExprContext.class);
		}
		public LogAndExprContext logAndExpr(int i) {
			return getRuleContext(LogAndExprContext.class,i);
		}
		public LogOrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_logOrExpr; }
	}

	public final LogOrExprContext logOrExpr() throws RecognitionException {
		LogOrExprContext _localctx = new LogOrExprContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_logOrExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair p1 = null;
			  DebugExpressionPair prev = null;
			  
			setState(315);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(312);
				_localctx.logAndExpr = logAndExpr();
				 prev = _localctx.logAndExpr.p; 
				}
			}

			{
			setState(317);
			match(T__25);
			{
			setState(318);
			_localctx.logAndExpr = logAndExpr();
			 p1 = _localctx.logAndExpr.p; 
			}
			 _localctx.p =  NF.createLogicalOrNode(prev, p1); 
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public DebugExpressionPair p;
		public LogOrExprContext logOrExpr;
		public ExprContext expr;
		public LogOrExprContext logOrExpr() {
			return getRuleContext(LogOrExprContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_expr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExpressionPair pThen = null;
			  DebugExpressionPair pElse = null;
			  DebugExpressionPair prev = null;
			  
			setState(327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__11) | (1L << T__12) | (1L << T__13) | (1L << T__14) | (1L << T__15) | (1L << T__16) | (1L << T__17) | (1L << T__18) | (1L << T__19) | (1L << T__20) | (1L << T__21) | (1L << T__22) | (1L << T__23) | (1L << T__24) | (1L << T__25) | (1L << LAPR) | (1L << ASTERISC))) != 0)) {
				{
				setState(324);
				_localctx.logOrExpr = logOrExpr();
				 prev = _localctx.logOrExpr.p; 
				}
			}

			{
			setState(329);
			match(T__26);
			{
			setState(330);
			_localctx.expr = expr();
			 pThen = _localctx.expr.p; 
			}
			setState(333);
			match(T__27);
			{
			setState(334);
			_localctx.expr = expr();
			 pElse = _localctx.expr.p; 
			}
			 _localctx.p =  NF.createTernaryNode(prev, pThen, pElse); 
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DTypeContext extends ParserRuleContext {
		public DebugExprType ty;
		public BaseTypeContext baseType;
		public Token t;
		public BaseTypeContext baseType() {
			return getRuleContext(BaseTypeContext.class,0);
		}
		public TerminalNode NUMBER() { return getToken(DebugExpressionParser.NUMBER, 0); }
		public DTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_dType; }
	}

	public final DTypeContext dType() throws RecognitionException {
		DTypeContext _localctx = new DTypeContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_dType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  DebugExprType tempTy = null;
			  
			setState(343);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__28) | (1L << T__29) | (1L << LAPR) | (1L << SIGNED) | (1L << UNSIGNED) | (1L << INT) | (1L << SHORT) | (1L << FLOAT) | (1L << DOUBLE) | (1L << CHAR))) != 0)) {
				{
				setState(340);
				_localctx.baseType = baseType();
				 tempTy = _localctx.baseType.ty; 
				}
			}

			setState(354);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ASTERISC:
				{
				setState(345);
				match(ASTERISC);
				 _localctx.ty =  tempTy.createPointer(); 
				}
				break;
			case T__1:
				{
				setState(347);
				match(T__1);
				setState(351);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case NUMBER:
					{
					setState(348);
					_localctx.t = match(NUMBER);
					 _localctx.ty =  tempTy.createArrayType(Integer.parseInt(_localctx.t.getText()));
					}
					break;
				case T__2:
					{
					 _localctx.ty =  tempTy.createArrayType(-1); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(353);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BaseTypeContext extends ParserRuleContext {
		public DebugExprType ty;
		public DTypeContext dType;
		public DTypeContext dType() {
			return getRuleContext(DTypeContext.class,0);
		}
		public BaseTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_baseType; }
	}

	public final BaseTypeContext baseType() throws RecognitionException {
		BaseTypeContext _localctx = new BaseTypeContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_baseType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{

			  _localctx.ty =  null;
			  boolean signed = false;
			  
			setState(396);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,28,_ctx) ) {
			case 1:
				{
				setState(357);
				match(LAPR);
				setState(358);
				_localctx.dType = dType();
				setState(359);
				match(T__0);
				 _localctx.ty =  _localctx.dType.ty; 
				}
				break;
			case 2:
				{
				setState(362);
				match(T__28);
				 _localctx.ty =  DebugExprType.getVoidType(); 
				}
				break;
			case 3:
				{
				setState(368);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case SIGNED:
					{
					setState(364);
					match(SIGNED);
					 signed = true; 
					}
					break;
				case UNSIGNED:
					{
					setState(366);
					match(UNSIGNED);
					 signed = false; 
					}
					break;
				case T__29:
				case INT:
				case SHORT:
				case CHAR:
					break;
				default:
					break;
				}
				setState(378);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case CHAR:
					{
					setState(370);
					match(CHAR);
					 _localctx.ty =  DebugExprType.getIntType(8, signed); 
					}
					break;
				case SHORT:
					{
					setState(372);
					match(SHORT);
					 _localctx.ty =  DebugExprType.getIntType(16, signed); 
					}
					break;
				case INT:
					{
					setState(374);
					match(INT);
					 _localctx.ty =  DebugExprType.getIntType(32, signed); 
					}
					break;
				case T__29:
					{
					setState(376);
					match(T__29);
					 _localctx.ty =  DebugExprType.getIntType(64, signed); 
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				break;
			case 4:
				{
				setState(380);
				match(CHAR);
				 _localctx.ty =  DebugExprType.getIntType(8, false); 
				}
				break;
			case 5:
				{
				setState(382);
				match(SHORT);
				 _localctx.ty =  DebugExprType.getIntType(16, true); 
				}
				break;
			case 6:
				{
				setState(384);
				match(INT);
				 _localctx.ty =  DebugExprType.getIntType(32, true); 
				}
				break;
			case 7:
				{
				setState(388);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__29) {
					{
					setState(386);
					match(T__29);
					 _localctx.ty =  DebugExprType.getIntType(64, true); 
					}
				}

				{
				setState(390);
				match(DOUBLE);
				 _localctx.ty =  DebugExprType.getFloatType(128); 
				}
				}
				break;
			case 8:
				{
				setState(392);
				match(FLOAT);
				 _localctx.ty =  DebugExprType.getFloatType(32); 
				}
				break;
			case 9:
				{
				setState(394);
				match(DOUBLE);
				 _localctx.ty =  DebugExprType.getFloatType(64); 
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\65\u0191\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3>\n\3\3\4\3\4\3\4\3\4\5\4D\n\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5"+
		"\4W\n\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3"+
		"\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\5\6{\n\6\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\5\b\u008b\n\b\3\b\5\b\u008e\n\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\5\t\u0098"+
		"\n\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\5\t\u00ac\n\t\3\n\3\n\3\n\3\n\5\n\u00b2\n\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\3\n\3\n\3\n\3\n\5\n\u00c0\n\n\3\13\3\13\3\13\3\13\5\13\u00c6"+
		"\n\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13"+
		"\u00d4\n\13\3\f\3\f\3\f\3\f\5\f\u00da\n\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\5"+
		"\f\u00f4\n\f\3\r\3\r\3\r\3\r\5\r\u00fa\n\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\r\3\r\3\r\3\r\5\r\u0108\n\r\3\16\3\16\3\16\3\16\5\16\u010e\n\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\5\17\u011a\n\17\3\17"+
		"\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\5\20\u0126\n\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\5\21\u0132\n\21\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\3\22\3\22\3\22\3\22\5\22\u013e\n\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\3\23\3\23\3\23\3\23\5\23\u014a\n\23\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\5\24\u015a\n\24\3\24\3\24"+
		"\3\24\3\24\3\24\3\24\5\24\u0162\n\24\3\24\5\24\u0165\n\24\3\25\3\25\3"+
		"\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u0173\n\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u017d\n\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\5\25\u0187\n\25\3\25\3\25\3\25\3\25\3\25\3\25\5\25"+
		"\u018f\n\25\3\25\2\2\26\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(\2"+
		"\3\4\2\n\r++\2\u01ad\2*\3\2\2\2\4=\3\2\2\2\6?\3\2\2\2\bX\3\2\2\2\ne\3"+
		"\2\2\2\f|\3\2\2\2\16\177\3\2\2\2\20\u0093\3\2\2\2\22\u00ad\3\2\2\2\24"+
		"\u00c1\3\2\2\2\26\u00d5\3\2\2\2\30\u00f5\3\2\2\2\32\u0109\3\2\2\2\34\u0115"+
		"\3\2\2\2\36\u0121\3\2\2\2 \u012d\3\2\2\2\"\u0139\3\2\2\2$\u0145\3\2\2"+
		"\2&\u0155\3\2\2\2(\u0166\3\2\2\2*+\b\2\1\2+,\5$\23\2,-\b\2\1\2-.\3\2\2"+
		"\2./\b\2\1\2/\3\3\2\2\2\60\61\7&\2\2\61>\b\3\1\2\62\63\7\'\2\2\63>\b\3"+
		"\1\2\64\65\7(\2\2\65>\b\3\1\2\66\67\7)\2\2\67>\b\3\1\289\7*\2\29:\5$\23"+
		"\2:;\7\3\2\2;<\b\3\1\2<>\3\2\2\2=\60\3\2\2\2=\62\3\2\2\2=\64\3\2\2\2="+
		"\66\3\2\2\2=8\3\2\2\2>\5\3\2\2\2?C\b\4\1\2@A\5\4\3\2AB\b\4\1\2BD\3\2\2"+
		"\2C@\3\2\2\2CD\3\2\2\2DV\3\2\2\2EF\7\4\2\2FG\5$\23\2GH\b\4\1\2HI\7\5\2"+
		"\2IJ\b\4\1\2JW\3\2\2\2KL\5\b\5\2LM\b\4\1\2MN\3\2\2\2NO\b\4\1\2OW\3\2\2"+
		"\2PQ\7\6\2\2QR\7&\2\2RW\b\4\1\2ST\7\7\2\2TU\7&\2\2UW\b\4\1\2VE\3\2\2\2"+
		"VK\3\2\2\2VP\3\2\2\2VS\3\2\2\2W\7\3\2\2\2XY\b\5\1\2YZ\7*\2\2Z[\5$\23\2"+
		"[\\\b\5\1\2\\]\3\2\2\2]^\b\5\1\2^_\7\b\2\2_`\5$\23\2`a\b\5\1\2ab\3\2\2"+
		"\2bc\b\5\1\2cd\7\3\2\2d\t\3\2\2\2ez\b\6\1\2fg\5\6\4\2gh\b\6\1\2hi\b\6"+
		"\1\2i{\3\2\2\2jk\5\f\7\2kl\b\6\1\2lm\3\2\2\2mn\5\16\b\2no\b\6\1\2op\3"+
		"\2\2\2pq\b\6\1\2q{\3\2\2\2rs\7\t\2\2st\7*\2\2tu\5&\24\2uv\b\6\1\2vw\3"+
		"\2\2\2wx\7\3\2\2xy\b\6\1\2y{\3\2\2\2zf\3\2\2\2zj\3\2\2\2zr\3\2\2\2{\13"+
		"\3\2\2\2|}\t\2\2\2}~\b\7\1\2~\r\3\2\2\2\177\u0080\b\b\1\2\u0080\u0081"+
		"\b\b\1\2\u0081\u008a\7*\2\2\u0082\u0083\5&\24\2\u0083\u0084\b\b\1\2\u0084"+
		"\u008b\3\2\2\2\u0085\u0086\7\64\2\2\u0086\u0087\7*\2\2\u0087\u0088\7&"+
		"\2\2\u0088\u0089\b\b\1\2\u0089\u008b\7\3\2\2\u008a\u0082\3\2\2\2\u008a"+
		"\u0085\3\2\2\2\u008a\u008b\3\2\2\2\u008b\u008d\3\2\2\2\u008c\u008e\7\3"+
		"\2\2\u008d\u008c\3\2\2\2\u008d\u008e\3\2\2\2\u008e\u008f\3\2\2\2\u008f"+
		"\u0090\5\n\6\2\u0090\u0091\b\b\1\2\u0091\u0092\b\b\1\2\u0092\17\3\2\2"+
		"\2\u0093\u0097\b\t\1\2\u0094\u0095\5\16\b\2\u0095\u0096\b\t\1\2\u0096"+
		"\u0098\3\2\2\2\u0097\u0094\3\2\2\2\u0097\u0098\3\2\2\2\u0098\u00ab\3\2"+
		"\2\2\u0099\u009a\7+\2\2\u009a\u009b\5\16\b\2\u009b\u009c\b\t\1\2\u009c"+
		"\u009d\3\2\2\2\u009d\u009e\b\t\1\2\u009e\u00ac\3\2\2\2\u009f\u00a0\7\16"+
		"\2\2\u00a0\u00a1\5\16\b\2\u00a1\u00a2\b\t\1\2\u00a2\u00a3\3\2\2\2\u00a3"+
		"\u00a4\b\t\1\2\u00a4\u00ac\3\2\2\2\u00a5\u00a6\7\17\2\2\u00a6\u00a7\5"+
		"\16\b\2\u00a7\u00a8\b\t\1\2\u00a8\u00a9\3\2\2\2\u00a9\u00aa\b\t\1\2\u00aa"+
		"\u00ac\3\2\2\2\u00ab\u0099\3\2\2\2\u00ab\u009f\3\2\2\2\u00ab\u00a5\3\2"+
		"\2\2\u00ac\21\3\2\2\2\u00ad\u00b1\b\n\1\2\u00ae\u00af\5\20\t\2\u00af\u00b0"+
		"\b\n\1\2\u00b0\u00b2\3\2\2\2\u00b1\u00ae\3\2\2\2\u00b1\u00b2\3\2\2\2\u00b2"+
		"\u00bf\3\2\2\2\u00b3\u00b4\7\n\2\2\u00b4\u00b5\5\20\t\2\u00b5\u00b6\b"+
		"\n\1\2\u00b6\u00b7\3\2\2\2\u00b7\u00b8\b\n\1\2\u00b8\u00c0\3\2\2\2\u00b9"+
		"\u00ba\7\13\2\2\u00ba\u00bb\5\20\t\2\u00bb\u00bc\b\n\1\2\u00bc\u00bd\3"+
		"\2\2\2\u00bd\u00be\b\n\1\2\u00be\u00c0\3\2\2\2\u00bf\u00b3\3\2\2\2\u00bf"+
		"\u00b9\3\2\2\2\u00c0\23\3\2\2\2\u00c1\u00c5\b\13\1\2\u00c2\u00c3\5\22"+
		"\n\2\u00c3\u00c4\b\13\1\2\u00c4\u00c6\3\2\2\2\u00c5\u00c2\3\2\2\2\u00c5"+
		"\u00c6\3\2\2\2\u00c6\u00d3\3\2\2\2\u00c7\u00c8\7\20\2\2\u00c8\u00c9\5"+
		"\22\n\2\u00c9\u00ca\b\13\1\2\u00ca\u00cb\3\2\2\2\u00cb\u00cc\b\13\1\2"+
		"\u00cc\u00d4\3\2\2\2\u00cd\u00ce\7\21\2\2\u00ce\u00cf\5\22\n\2\u00cf\u00d0"+
		"\b\13\1\2\u00d0\u00d1\3\2\2\2\u00d1\u00d2\b\13\1\2\u00d2\u00d4\3\2\2\2"+
		"\u00d3\u00c7\3\2\2\2\u00d3\u00cd\3\2\2\2\u00d4\25\3\2\2\2\u00d5\u00d9"+
		"\b\f\1\2\u00d6\u00d7\5\24\13\2\u00d7\u00d8\b\f\1\2\u00d8\u00da\3\2\2\2"+
		"\u00d9\u00d6\3\2\2\2\u00d9\u00da\3\2\2\2\u00da\u00f3\3\2\2\2\u00db\u00dc"+
		"\7\22\2\2\u00dc\u00dd\5\24\13\2\u00dd\u00de\b\f\1\2\u00de\u00df\3\2\2"+
		"\2\u00df\u00e0\b\f\1\2\u00e0\u00f4\3\2\2\2\u00e1\u00e2\7\23\2\2\u00e2"+
		"\u00e3\5\24\13\2\u00e3\u00e4\b\f\1\2\u00e4\u00e5\3\2\2\2\u00e5\u00e6\b"+
		"\f\1\2\u00e6\u00f4\3\2\2\2\u00e7\u00e8\7\24\2\2\u00e8\u00e9\5\24\13\2"+
		"\u00e9\u00ea\b\f\1\2\u00ea\u00eb\3\2\2\2\u00eb\u00ec\b\f\1\2\u00ec\u00f4"+
		"\3\2\2\2\u00ed\u00ee\7\25\2\2\u00ee\u00ef\5\24\13\2\u00ef\u00f0\b\f\1"+
		"\2\u00f0\u00f1\3\2\2\2\u00f1\u00f2\b\f\1\2\u00f2\u00f4\3\2\2\2\u00f3\u00db"+
		"\3\2\2\2\u00f3\u00e1\3\2\2\2\u00f3\u00e7\3\2\2\2\u00f3\u00ed\3\2\2\2\u00f4"+
		"\27\3\2\2\2\u00f5\u00f9\b\r\1\2\u00f6\u00f7\5\26\f\2\u00f7\u00f8\b\r\1"+
		"\2\u00f8\u00fa\3\2\2\2\u00f9\u00f6\3\2\2\2\u00f9\u00fa\3\2\2\2\u00fa\u0107"+
		"\3\2\2\2\u00fb\u00fc\7\26\2\2\u00fc\u00fd\5\26\f\2\u00fd\u00fe\b\r\1\2"+
		"\u00fe\u00ff\3\2\2\2\u00ff\u0100\b\r\1\2\u0100\u0108\3\2\2\2\u0101\u0102"+
		"\7\27\2\2\u0102\u0103\5\26\f\2\u0103\u0104\b\r\1\2\u0104\u0105\3\2\2\2"+
		"\u0105\u0106\b\r\1\2\u0106\u0108\3\2\2\2\u0107\u00fb\3\2\2\2\u0107\u0101"+
		"\3\2\2\2\u0108\31\3\2\2\2\u0109\u010d\b\16\1\2\u010a\u010b\5\30\r\2\u010b"+
		"\u010c\b\16\1\2\u010c\u010e\3\2\2\2\u010d\u010a\3\2\2\2\u010d\u010e\3"+
		"\2\2\2\u010e\u010f\3\2\2\2\u010f\u0110\7\30\2\2\u0110\u0111\5\30\r\2\u0111"+
		"\u0112\b\16\1\2\u0112\u0113\3\2\2\2\u0113\u0114\b\16\1\2\u0114\33\3\2"+
		"\2\2\u0115\u0119\b\17\1\2\u0116\u0117\5\32\16\2\u0117\u0118\b\17\1\2\u0118"+
		"\u011a\3\2\2\2\u0119\u0116\3\2\2\2\u0119\u011a\3\2\2\2\u011a\u011b\3\2"+
		"\2\2\u011b\u011c\7\31\2\2\u011c\u011d\5\32\16\2\u011d\u011e\b\17\1\2\u011e"+
		"\u011f\3\2\2\2\u011f\u0120\b\17\1\2\u0120\35\3\2\2\2\u0121\u0125\b\20"+
		"\1\2\u0122\u0123\5\34\17\2\u0123\u0124\b\20\1\2\u0124\u0126\3\2\2\2\u0125"+
		"\u0122\3\2\2\2\u0125\u0126\3\2\2\2\u0126\u0127\3\2\2\2\u0127\u0128\7\32"+
		"\2\2\u0128\u0129\5\34\17\2\u0129\u012a\b\20\1\2\u012a\u012b\3\2\2\2\u012b"+
		"\u012c\b\20\1\2\u012c\37\3\2\2\2\u012d\u0131\b\21\1\2\u012e\u012f\5\36"+
		"\20\2\u012f\u0130\b\21\1\2\u0130\u0132\3\2\2\2\u0131\u012e\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134\7\33\2\2\u0134\u0135\5"+
		"\36\20\2\u0135\u0136\b\21\1\2\u0136\u0137\3\2\2\2\u0137\u0138\b\21\1\2"+
		"\u0138!\3\2\2\2\u0139\u013d\b\22\1\2\u013a\u013b\5 \21\2\u013b\u013c\b"+
		"\22\1\2\u013c\u013e\3\2\2\2\u013d\u013a\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u013f\3\2\2\2\u013f\u0140\7\34\2\2\u0140\u0141\5 \21\2\u0141\u0142\b"+
		"\22\1\2\u0142\u0143\3\2\2\2\u0143\u0144\b\22\1\2\u0144#\3\2\2\2\u0145"+
		"\u0149\b\23\1\2\u0146\u0147\5\"\22\2\u0147\u0148\b\23\1\2\u0148\u014a"+
		"\3\2\2\2\u0149\u0146\3\2\2\2\u0149\u014a\3\2\2\2\u014a\u014b\3\2\2\2\u014b"+
		"\u014c\7\35\2\2\u014c\u014d\5$\23\2\u014d\u014e\b\23\1\2\u014e\u014f\3"+
		"\2\2\2\u014f\u0150\7\36\2\2\u0150\u0151\5$\23\2\u0151\u0152\b\23\1\2\u0152"+
		"\u0153\3\2\2\2\u0153\u0154\b\23\1\2\u0154%\3\2\2\2\u0155\u0159\b\24\1"+
		"\2\u0156\u0157\5(\25\2\u0157\u0158\b\24\1\2\u0158\u015a\3\2\2\2\u0159"+
		"\u0156\3\2\2\2\u0159\u015a\3\2\2\2\u015a\u0164\3\2\2\2\u015b\u015c\7+"+
		"\2\2\u015c\u0165\b\24\1\2\u015d\u0161\7\4\2\2\u015e\u015f\7\'\2\2\u015f"+
		"\u0162\b\24\1\2\u0160\u0162\b\24\1\2\u0161\u015e\3\2\2\2\u0161\u0160\3"+
		"\2\2\2\u0162\u0163\3\2\2\2\u0163\u0165\7\5\2\2\u0164\u015b\3\2\2\2\u0164"+
		"\u015d\3\2\2\2\u0165\'\3\2\2\2\u0166\u018e\b\25\1\2\u0167\u0168\7*\2\2"+
		"\u0168\u0169\5&\24\2\u0169\u016a\7\3\2\2\u016a\u016b\b\25\1\2\u016b\u018f"+
		"\3\2\2\2\u016c\u016d\7\37\2\2\u016d\u018f\b\25\1\2\u016e\u016f\7,\2\2"+
		"\u016f\u0173\b\25\1\2\u0170\u0171\7-\2\2\u0171\u0173\b\25\1\2\u0172\u016e"+
		"\3\2\2\2\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173\u017c\3\2\2\2\u0174"+
		"\u0175\7\63\2\2\u0175\u017d\b\25\1\2\u0176\u0177\7\60\2\2\u0177\u017d"+
		"\b\25\1\2\u0178\u0179\7.\2\2\u0179\u017d\b\25\1\2\u017a\u017b\7 \2\2\u017b"+
		"\u017d\b\25\1\2\u017c\u0174\3\2\2\2\u017c\u0176\3\2\2\2\u017c\u0178\3"+
		"\2\2\2\u017c\u017a\3\2\2\2\u017d\u018f\3\2\2\2\u017e\u017f\7\63\2\2\u017f"+
		"\u018f\b\25\1\2\u0180\u0181\7\60\2\2\u0181\u018f\b\25\1\2\u0182\u0183"+
		"\7.\2\2\u0183\u018f\b\25\1\2\u0184\u0185\7 \2\2\u0185\u0187\b\25\1\2\u0186"+
		"\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0188\3\2\2\2\u0188\u0189\7\62"+
		"\2\2\u0189\u018f\b\25\1\2\u018a\u018b\7\61\2\2\u018b\u018f\b\25\1\2\u018c"+
		"\u018d\7\62\2\2\u018d\u018f\b\25\1\2\u018e\u0167\3\2\2\2\u018e\u016c\3"+
		"\2\2\2\u018e\u0172\3\2\2\2\u018e\u017e\3\2\2\2\u018e\u0180\3\2\2\2\u018e"+
		"\u0182\3\2\2\2\u018e\u0186\3\2\2\2\u018e\u018a\3\2\2\2\u018e\u018c\3\2"+
		"\2\2\u018f)\3\2\2\2\37=CVz\u008a\u008d\u0097\u00ab\u00b1\u00bf\u00c5\u00d3"+
		"\u00d9\u00f3\u00f9\u0107\u010d\u0119\u0125\u0131\u013d\u0149\u0159\u0161"+
		"\u0164\u0172\u017c\u0186\u018e";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}
