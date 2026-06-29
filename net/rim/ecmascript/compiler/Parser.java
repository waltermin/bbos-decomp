package net.rim.ecmascript.compiler;

import java.util.Hashtable;
import net.rim.ecmascript.util.Resources;

class Parser implements TokenConstants, OpcodeConstants {
   private Compiler _c;
   private Tokenizer _t;
   boolean _allowIn = true;
   Function _currFunc;
   boolean _statementAlreadyPushed;
   private StatementLabel _statementStack;
   private Hashtable _statementLabelLookup = (Hashtable)(new Object());
   private byte _cmpEqOp = 11;
   private byte _cmpNeOp = 16;

   Parser(Compiler c, Tokenizer t) {
      this._c = c;
      this._t = t;
      if (this._c.getVersion() == 120) {
         this._cmpEqOp = 17;
         this._cmpNeOp = 18;
      }
   }

   private int getRegExp(boolean haveEquals) {
      return this._t.getRegExp(haveEquals);
   }

   private boolean matchToken(int tok) {
      if (this._t.peekToken() == tok) {
         this._t.getToken();
         return true;
      } else {
         return false;
      }
   }

   void parse() {
      this._currFunc = new Function(this._c, null, "[global code]", false);
      NodeTemp temp = this.genTemp();
      this._currFunc.setGlobalReturn(temp.getId());
      this._currFunc.addCode(111);
      this._currFunc.addCode(122, temp.getId());
      this._c.addGlobalCode(this._currFunc);
      if (this._c.compilingForFunctionConstructor()) {
         this.functionDeclaration(true);
      } else {
         this.sourceElements(false);
      }

      this._currFunc.setTokenRange(0, this._t.getLastGetTokenStreamIndex() + 1);
      this._currFunc.addCode(46, temp.getId());
      this._currFunc.genReturn(true);
   }

   private void expect(int token) {
      this.expect(token, this._t.getToken());
   }

   private boolean canOmitSemi() {
      int tok = this._t.peekToken();
      if (this._t.lineTerminatorPreceedsToken() && tok != 120) {
         return true;
      } else {
         return tok == 130 ? true : tok == 0;
      }
   }

   private void expectSemi() {
      if (!this.canOmitSemi()) {
         this.expect(120);
      }
   }

   private void expect(int want, int have) {
      if (want != have) {
         throw new CompileError(Resources.getString(6), this._t.tokenToExpectingString(want), this._t.tokenToString(have));
      }
   }

   private void sourceElements(boolean stopOnBrace) {
      while (true) {
         switch (this._t.peekToken()) {
            case 0:
               return;
            case 11:
               this.functionDeclaration(false);
               break;
            case 130:
               if (stopOnBrace) {
                  return;
               }
            default:
               this.statement();
         }
      }
   }

   private void addLineNumber() {
      int tokenIndex = this._t.getLastPeekTokenStreamIndex();
      if (tokenIndex >= 65535) {
         tokenIndex = 65535;
      }

      int lineNumber = this._t.getLineNumber();
      if (lineNumber >= 65535) {
         int var3 = '\uffff';
      }

      this._currFunc.addCode(-117, (tokenIndex << 16) + this._t.getLineNumber());
   }

   private Function functionDeclaration(boolean isExpression) {
      this.expect(11);
      int tokenStart = this._t.getLastGetTokenStreamIndex();
      int tok = this._t.getToken();
      String id = null;
      if (tok == 203) {
         id = this._t.tokenIdentifier();
         if (!isExpression) {
            this._currFunc.addLocal(id);
         }

         this.expect(111);
      } else if (isExpression) {
         this.expect(111, tok);
         id = null;
      } else {
         this.expect(203, tok);
      }

      Function newFunc = new Function(this._c, this._currFunc, id, isExpression);
      if (isExpression) {
         this._currFunc = this._currFunc.addFunctionExpression(newFunc);
      } else {
         this._currFunc = this._currFunc.addFunctionDeclaration(newFunc);
      }

      if (!this.matchToken(112)) {
         while (true) {
            this.expect(203);
            this._currFunc.addParm(this._t.tokenIdentifier());
            tok = this._t.getToken();
            if (tok == 112) {
               break;
            }

            this.expect(115, tok);
         }
      }

      StatementLabel statementStack = this._statementStack;
      this._statementStack = null;
      this.expect(126);
      this.sourceElements(true);
      this.expect(130);
      this._statementStack = statementStack;
      switch (this._c.getVersion()) {
         default:
            if (this._t.peekToken() == 11) {
               this.expectSemi();
            }
         case 0:
         case 100:
         case 110:
            this._currFunc.setTokenRange(tokenStart, this._t.getLastGetTokenStreamIndex() + 1);
            this._currFunc.genReturn(false);
            Function f = this._currFunc;
            this._currFunc = this._currFunc.getParent();
            return f;
      }
   }

   private void statement() {
      this._currFunc.resetStackDepth();
      boolean statementAlreadyPushed = this._statementAlreadyPushed;
      this._statementAlreadyPushed = false;
      int tok = this._t.peekToken();
      switch (tok) {
         case 0:
            throw new CompileError(Resources.getString(25));
         case 1:
            this.addLineNumber();
            this.breakStatement();
            return;
         case 4:
            this.addLineNumber();
            this.continueStatement();
            return;
         case 7:
         case 10:
         case 17:
         case 20:
         case 24:
         case 25:
            this.addLineNumber();
            this.labelledStatement(statementAlreadyPushed);
            return;
         case 11:
            Function f = this.functionDeclaration(true);
            Node expr = new NodeFunction(f, true);
            this.expectSemi();
            expr.generateAndSaveIfNecessary(this._currFunc);
            return;
         case 12:
            this.addLineNumber();
            this.ifStatement();
            return;
         case 16:
            this.addLineNumber();
            this.returnStatement();
            return;
         case 19:
            this.addLineNumber();
            this.throwStatement();
            return;
         case 22:
         case 29:
            this.addLineNumber();
            this.variableStatement(tok == 29);
            return;
         case 35:
            this.addLineNumber();
            this.debuggerStatement();
            return;
         case 120:
            this.emptyStatement();
            return;
         case 126:
            this.block();
            return;
         default:
            this.addLineNumber();
            this.expressionStatement();
      }
   }

   private void block() {
      this.expect(126);

      while (!this.matchToken(130)) {
         this.statement();
      }
   }

   private void variableStatement(boolean isConst) {
      this.variableDeclarationList().generateAndDiscard(this._currFunc);
      this.expectSemi();
   }

   private Node variableDeclarationList() {
      boolean isConst = false;
      if (!this.matchToken(29)) {
         this.expect(22);
      } else {
         isConst = true;
      }

      Node rc = this.variableDeclaration(isConst);

      while (this.matchToken(115)) {
         rc = new NodeComma(rc, this.variableDeclaration(isConst));
      }

      return rc;
   }

   private Node variableDeclaration(boolean isConst) {
      this.expect(203);
      String name = this._t.tokenIdentifier();
      Node rc = new NodeId(this._currFunc.addLocal(name));
      if (isConst) {
         this._currFunc.addConst(name);
      }

      if (this.matchToken(139)) {
         if (isConst) {
            return new NodeConst(rc, this.assignmentExpression());
         }

         rc = new NodeAssignment(rc, this.assignmentExpression());
      }

      return rc;
   }

   private void emptyStatement() {
      this.expect(120);
   }

   private void ifStatement() {
      this.expect(12);
      this.expect(111);
      Label falseLabel = new Label(this._c);
      Label trueLabel = new Label(this._c);
      this.expression().genIf(this._currFunc, trueLabel, falseLabel);
      this._currFunc.genLabel(trueLabel);
      this.expect(112);
      this.statement();
      if (this.matchToken(8)) {
         Label endLabel = new Label(this._c);
         this._currFunc.genGoto(endLabel);
         this._currFunc.genLabel(falseLabel);
         this.statement();
         this._currFunc.genLabel(endLabel);
      } else {
         this._currFunc.genLabel(falseLabel);
      }
   }

   private void labelledStatement(boolean statementAlreadyPushed) {
      if (!statementAlreadyPushed) {
         this.pushStatementLabel(null);
      }

      switch (this._t.peekToken()) {
         case 7:
            this._statementStack.addLoopLabels();
            this.doStatement();
            break;
         case 10:
            this._statementStack.addLoopLabels();
            this.forStatement();
            break;
         case 17:
            this._statementStack.addBreakLabel();
            this.switchStatement();
            break;
         case 20:
            this._statementStack.addTryLabels(this.genTemp().getId());
            this.tryStatement();
            break;
         case 24:
            this._statementStack.addLoopLabels();
            this.whileStatement();
            break;
         case 25:
            this._statementStack.setIsWith();
            this.withStatement();
      }

      if (!statementAlreadyPushed) {
         this.popStatementLabel();
      }
   }

   private void doStatement() {
      this.expect(7);
      Label topLabel = new Label(this._c);
      this._currFunc.genLabel(topLabel);
      this.statement();
      this._currFunc.genLabel(this._statementStack.getContinueLabel());
      this.expect(24);
      this.expect(111);
      this.expression().genIf(this._currFunc, topLabel, this._statementStack.getBreakLabel());
      this.expect(112);
      this.expectSemi();
      this._currFunc.genLabel(this._statementStack.getBreakLabel());
   }

   private void whileStatement() {
      this._currFunc.genLabel(this._statementStack.getContinueLabel());
      this.expect(24);
      this.expect(111);
      Label trueLabel = new Label(this._c);
      this.expression().genIf(this._currFunc, trueLabel, this._statementStack.getBreakLabel());
      this.expect(112);
      this._currFunc.genLabel(trueLabel);
      this.statement();
      this._currFunc.genGoto(this._statementStack.getContinueLabel());
      this._currFunc.genLabel(this._statementStack.getBreakLabel());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void forStatement() {
      this.expect(10);
      this.expect(111);
      Node init = null;
      boolean var6 = false /* VF: Semaphore variable */;

      try {
         var6 = true;
         this._allowIn = false;
         switch (this._t.peekToken()) {
            case 22:
               init = this.variableDeclarationList();
               var6 = false;
               break;
            case 120:
               var6 = false;
               break;
            default:
               init = this.expression();
               var6 = false;
         }
      } finally {
         if (var6) {
            this._allowIn = true;
         }
      }

      this._allowIn = true;
      Label trueLabel = new Label(this._c);
      if (!this.matchToken(13)) {
         if (init != null) {
            init.generateAndDiscard(this._currFunc);
         }

         Label testLabel = new Label(this._c);
         this.expect(120);
         this._currFunc.genLabel(testLabel);
         if (!this.matchToken(120)) {
            this.expression().genIf(this._currFunc, trueLabel, this._statementStack.getBreakLabel());
            this.expect(120);
            this._currFunc.genLabel(trueLabel);
         }

         Node increment = null;
         if (!this.matchToken(112)) {
            increment = this.expression();
            this.expect(112);
         }

         this.statement();
         this._currFunc.genLabel(this._statementStack.getContinueLabel());
         if (increment != null) {
            increment.generateAndDiscard(this._currFunc);
         }

         this._currFunc.genGoto(testLabel);
      } else {
         if (init == null) {
            throw new CompileError(Resources.getString(9));
         }

         this.expression().generate(this._currFunc);
         this.expect(112);
         this._currFunc.addCode(29);
         NodeTemp temp = this.genTemp();
         this._currFunc.addCode(122, temp.getId());
         this._currFunc.genLabel(this._statementStack.getContinueLabel());
         new NodeUnaryOp(30, temp).generate(this._currFunc);
         this._currFunc.genIf(trueLabel, this._statementStack.getBreakLabel());
         this._currFunc.genLabel(trueLabel);
         new NodeUnaryOp(31, temp).generate(this._currFunc);
         new NodeAssignment(init, null, Resources.getString(16)).generateAndDiscard(this._currFunc);
         this.statement();
         this._currFunc.genGoto(this._statementStack.getContinueLabel());
      }

      this._currFunc.genLabel(this._statementStack.getBreakLabel());
   }

   private void continueStatement() {
      this.expect(4);
      String labelName = null;
      int tok = this._t.peekToken();
      if (!this._t.lineTerminatorPreceedsToken()) {
         switch (tok) {
            case 120:
               this._t.getToken();
               break;
            case 203:
               this._t.getToken();
               labelName = this._t.tokenIdentifier();
            default:
               this.expectSemi();
         }
      }

      StatementLabel label = this._statementStack;

      while (true) {
         if (label == null) {
            throw new CompileError(Resources.getString(13));
         }

         if (label.getIsWith()) {
            this._currFunc.addCode(94);
         }

         if (label.getIsTry()) {
            this._currFunc.addCode(93);
            this._currFunc.genJsr(label.getFinallyLabel(), label.getJsrLocal());
         }

         if (labelName == null) {
            if (label.getContinueLabel() != null) {
               break;
            }
         } else if (labelName.equals(label.getName())) {
            if (label.getContinueLabel() == null) {
               throw new CompileError(Resources.getString(12), labelName);
            }
            break;
         }

         label = label.getParent();
      }

      this._currFunc.genGoto(label.getContinueLabel());
   }

   private void breakStatement() {
      this.expect(1);
      String labelName = null;
      int tok = this._t.peekToken();
      if (!this._t.lineTerminatorPreceedsToken()) {
         switch (tok) {
            case 120:
               this._t.getToken();
               break;
            case 203:
               this._t.getToken();
               labelName = this._t.tokenIdentifier();
            default:
               this.expectSemi();
         }
      }

      for (StatementLabel label = this._statementStack; label != null; label = label.getParent()) {
         if (label.getIsWith()) {
            this._currFunc.addCode(94);
         }

         if (label.getIsTry()) {
            this._currFunc.addCode(93);
            this._currFunc.genJsr(label.getFinallyLabel(), label.getJsrLocal());
         }

         if (label.getBreakLabel() != null && (labelName == null || labelName.equals(label.getName()))) {
            this._currFunc.genGoto(label.getBreakLabel());
            return;
         }
      }

      throw new CompileError(Resources.getString(14));
   }

   private void debuggerStatement() {
      this.expect(35);
      this.expectSemi();
      this._currFunc.addCode(-116);
   }

   private void returnStatement() {
      if (this._currFunc == this._c.getGlobalCode()) {
         throw new CompileError(Resources.getString(11));
      }

      this.expect(16);
      int tok = this._t.peekToken();
      Node expr = null;
      boolean needTemp = false;
      NodeTemp temp = null;

      for (StatementLabel label = this._statementStack; label != null; label = label.getParent()) {
         if (label.getIsTry() || label.getIsWith()) {
            needTemp = true;
            break;
         }
      }

      if (!this._t.lineTerminatorPreceedsToken()) {
         if (tok == 120) {
            this._t.getToken();
         } else if (!this.canOmitSemi()) {
            expr = this.expression();
            this.expectSemi();
         }
      }

      if (expr != null && needTemp) {
         temp = this.genTemp();
         expr.generate(this._currFunc);
         this._currFunc.addCode(122, temp.getId());
      }

      for (StatementLabel label = this._statementStack; label != null; label = label.getParent()) {
         if (label.getIsWith()) {
            this._currFunc.addCode(94);
         }

         if (label.getIsTry()) {
            this._currFunc.addCode(93);
            this._currFunc.genJsr(label.getFinallyLabel(), label.getJsrLocal());
         }
      }

      if (expr != null) {
         if (needTemp) {
            this._currFunc.addCode(46, temp.getId());
         } else {
            expr.generate(this._currFunc);
         }

         this._currFunc.genReturn(true);
      } else {
         this._currFunc.genReturn(false);
      }
   }

   private void throwStatement() {
      this.expect(19);
      this._t.peekToken();
      if (!this._t.lineTerminatorPreceedsToken()) {
         this.expression().generate(this._currFunc);
         this.expectSemi();
      }

      this._currFunc.genThrow();
   }

   private void withStatement() {
      this.expect(25);
      this.expect(111);
      this.expression().generate(this._currFunc);
      this._currFunc.addCode(112);
      this.expect(112);
      Label l = new Label(this._c);
      this._currFunc.genGoto(l);
      this._currFunc.genLabel(l);
      this._currFunc.pushWith();
      this.statement();
      this._currFunc.addCode(94);
      l = new Label(this._c);
      this._currFunc.genGoto(l);
      this._currFunc.genLabel(l);
      this._currFunc.popWith();
   }

   private void switchStatement() {
      Label nextTestLabel = new Label(this._c);
      Label nextStatementLabel = new Label(this._c);
      Label defaultLabel = null;
      this.expect(17);
      this.expect(111);
      this.expression().generate(this._currFunc);
      NodeTemp temp = this.genTemp();
      this._currFunc.addCode(122, temp.getId());
      this.expect(112);
      this.expect(126);
      this._currFunc.genGoto(nextTestLabel);

      label34:
      while (true) {
         switch (this._t.peekToken()) {
            case 2:
               this._t.getToken();
               this._currFunc.genLabel(nextTestLabel);
               nextTestLabel = new Label(this._c);
               new NodeRelationalOp(17, temp, this.expression()).genIf(this._currFunc, nextStatementLabel, nextTestLabel);
               this.expect(119);
               this._currFunc.genLabel(nextStatementLabel);

               while (true) {
                  switch (this._t.peekToken()) {
                     case 2:
                     case 5:
                     case 130:
                        nextStatementLabel = new Label(this._c);
                        this._currFunc.genGoto(nextStatementLabel);
                        continue label34;
                     default:
                        this.statement();
                  }
               }
            case 5:
               this._t.getToken();
               if (defaultLabel != null) {
                  throw new CompileError(Resources.getString(18));
               }

               this.expect(119);
               defaultLabel = new Label(this._c);
               this._currFunc.genLabel(defaultLabel);
               this._currFunc.genLabel(nextStatementLabel);

               while (true) {
                  switch (this._t.peekToken()) {
                     case 2:
                     case 5:
                     case 130:
                        nextStatementLabel = new Label(this._c);
                        this._currFunc.genGoto(nextStatementLabel);
                        continue label34;
                     default:
                        this.statement();
                  }
               }
            default:
               this._currFunc.genLabel(nextTestLabel);
               if (defaultLabel != null) {
                  this._currFunc.genGoto(defaultLabel);
               }

               this._currFunc.genLabel(nextStatementLabel);
               this._currFunc.genLabel(this._statementStack.getBreakLabel());
               this.expect(130);
               return;
         }
      }
   }

   private void tryStatement() {
      boolean noCatch = true;
      boolean noFinally = true;
      this.expect(20);
      Label endLabel = new Label(this._c);
      Label catchLabel = new Label(this._c);
      Label tryLabel = new Label(this._c);
      this._currFunc.genTry(tryLabel, catchLabel);
      this._currFunc.genLabel(tryLabel);
      this.block();
      this._currFunc.addCode(93);
      this._currFunc.genJsr(this._statementStack.getFinallyLabel(), this._statementStack.getJsrLocal());
      this._currFunc.genGoto(endLabel);
      if (this._t.peekToken() == 3) {
         noCatch = false;
         this.expect(3);
         this.expect(111);
         this.expect(203);
         int id = this._currFunc.addId(this._t.tokenIdentifier());
         this.expect(112);
         this._currFunc.pushWith();
         this._currFunc.genLabel(catchLabel);
         catchLabel = new Label(this._c);
         tryLabel = new Label(this._c);
         this._currFunc.genTry(tryLabel, catchLabel);
         this._currFunc.genLabel(tryLabel);
         this._currFunc.addCode(107);
         this._currFunc.addCode(26);
         this._currFunc.addCode(112);
         this._currFunc.addCode(114, id);
         this._statementStack.setIsWith();
         this.block();
         this._statementStack.clearIsWith();
         this._currFunc.addCode(94);
         this._currFunc.addCode(93);
         this._currFunc.genJsr(this._statementStack.getFinallyLabel(), this._statementStack.getJsrLocal());
         this._currFunc.genGoto(endLabel);
         this._currFunc.popWith();
      }

      this._currFunc.genLabel(catchLabel);
      this._currFunc.genJsr(this._statementStack.getFinallyLabel(), this._statementStack.getJsrLocal());
      this._currFunc.genThrow();
      this._currFunc.genLabel(this._statementStack.getFinallyLabel());
      this._statementStack.clearIsTry();
      if (this._t.peekToken() == 9) {
         noFinally = false;
         this._t.getToken();
         this.block();
      }

      if (noCatch && noFinally) {
         throw new CompileError(Resources.getString(3));
      }

      this._currFunc.genRetJsr(this._statementStack.getJsrLocal());
      this._currFunc.genLabel(endLabel);
   }

   private void expressionStatement() {
      this._t.saveState();
      if (this.matchToken(203)) {
         String statementLabel = this._t.tokenIdentifier();
         if (this.matchToken(119)) {
            this.pushStatementLabel(statementLabel);
            this._statementStack.addBreakLabel();
            this._statementAlreadyPushed = true;
            this.statement();
            Label breakLabel = this._statementStack.getBreakLabel();
            if (!breakLabel.getIsGenerated()) {
               this._currFunc.genLabel(breakLabel);
            }

            this.popStatementLabel();
            return;
         }
      }

      this._t.restoreState();
      Node expr = this.expression();
      this.expectSemi();
      expr.generateAndSaveIfNecessary(this._currFunc);
   }

   private Node primaryExpression() {
      int tok = this._t.peekToken();
      switch (tok) {
         case 18:
            this._t.getToken();
            return new NodeThis();
         case 26:
            this._t.getToken();
            return new NodeNull();
         case 27:
            this._t.getToken();
            return new NodeTrue();
         case 28:
            this._t.getToken();
            return new NodeFalse();
         case 111:
            this._t.getToken();
            Node var4 = this.expression();
            this.expect(112);
            return var4;
         case 117:
            this._t.getToken();
            this.getRegExp(false);
            return new NodeRegExp(this._t.tokenStringLiteral(), this._t.flagsStringLiteral());
         case 118:
            this._t.getToken();
            this.getRegExp(true);
            return new NodeRegExp(this._t.tokenStringLiteral(), this._t.flagsStringLiteral());
         case 122:
            return this.arrayLiteral();
         case 126:
            return this.objectLiteral();
         case 200:
            this._t.getToken();
            return new NodeString(this._t.tokenStringLiteral());
         case 201:
            this._t.getToken();
            return new NodeDouble(this._currFunc, this._t.tokenDouble());
         case 202:
            this._t.getToken();
            return new NodeInteger(this._t.tokenValue());
         case 203:
            this._t.getToken();
            String name = this._t.tokenIdentifier();
            if (this._currFunc != this._c.getGlobalCode() && name.equals("arguments")) {
               return new NodeArguments(this._currFunc.addId(name));
            }

            return new NodeId(this._currFunc.addId(name));
         default:
            throw new CompileError(Resources.getString(26), this._t.tokenToString(tok));
      }
   }

   private Node arrayLiteral() {
      NodeArrayLiteral rc = new NodeArrayLiteral();
      this.expect(122);

      while (true) {
         switch (this._t.peekToken()) {
            case 115:
               this._t.getToken();
               rc.addExpr(null);
               break;
            case 123:
               this._t.getToken();
               return rc;
            default:
               rc.addExpr(this.assignmentExpression());
               int tok = this._t.getToken();
               switch (tok) {
                  case 115:
                     break;
                  case 123:
                     return rc;
                  default:
                     throw new CompileError(Resources.getString(4));
               }
         }
      }
   }

   private Node objectLiteral() {
      NodeObjectLiteral rc = new NodeObjectLiteral();
      this.expect(126);
      int tok = this._t.getToken();
      if (tok != 130) {
         while (true) {
            String name;
            switch (tok) {
               case 199:
                  throw new CompileError(Resources.getString(5));
               case 200:
                  name = this._t.tokenStringLiteral();
                  break;
               case 201:
                  name = this._t.tokenRawString();
                  break;
               case 202:
                  name = this._t.tokenRawString();
                  break;
               case 203:
               default:
                  name = this._t.tokenIdentifier();
            }

            this.expect(119);
            rc.addProperty(this._currFunc, name, this.assignmentExpression());
            switch (this._t.getToken()) {
               case 115:
                  tok = this._t.getToken();
                  break;
               case 130:
                  return rc;
               default:
                  throw new CompileError(Resources.getString(4));
            }
         }
      } else {
         return rc;
      }
   }

   private Node memberExpression(boolean callOK) {
      Node rc;
      switch (this._t.peekToken()) {
         case 11:
            Function f = this.functionDeclaration(true);
            rc = new NodeFunction(f, false);
            break;
         case 15:
            this._t.getToken();
            Node lhs = this.memberExpression(false);
            Node args = null;
            if (this._t.peekToken() == 111) {
               args = this.argumentList();
            }

            if (this._t.peekToken() == 126) {
               rc = new NodeJavaAdapter(lhs, this.objectLiteral(), args);
            } else {
               rc = new NodeNew(lhs, args);
            }
            break;
         default:
            rc = this.primaryExpression();
      }

      while (true) {
         switch (this._t.peekToken()) {
            case 111:
               if (!callOK) {
                  return rc;
               }

               rc = new NodeCall(rc, this.argumentList());
               break;
            case 116:
               this._t.getToken();
               this.expect(203);
               rc = new NodeDot(this._currFunc, rc, null, this._t.tokenIdentifier(), false);
               break;
            case 122:
               this._t.getToken();
               Node expr = this.expression();
               this.expect(123);
               rc = new NodeIndex(rc, expr);
               break;
            default:
               return rc;
         }
      }
   }

   private Node argumentList() {
      this.expect(111);
      if (this.matchToken(112)) {
         return null;
      }

      NodeArgList args = new NodeArgList();

      while (true) {
         args.addExpr(this.assignmentExpression());
         switch (this._t.getToken()) {
            case 112:
               return args;
            case 115:
               break;
            default:
               throw new CompileError(Resources.getString(4));
         }
      }
   }

   private Node postfixExpression() {
      Node rc = this.memberExpression(true);
      int tok = this._t.peekToken();
      if (!this._t.lineTerminatorPreceedsToken()) {
         switch (tok) {
            case 101:
               this._t.getToken();
               rc = new NodePostfix(19, rc);
               break;
            case 133:
               this._t.getToken();
               return new NodePostfix(77, rc);
         }
      }

      return rc;
   }

   private Node unaryExpression() {
      switch (this._t.peekToken()) {
         case 6:
            this._t.getToken();
            return new NodeDelete(this.unaryExpression());
         case 21:
            this._t.getToken();
            return new NodeTypeof(this.unaryExpression());
         case 23:
            this._t.getToken();
            return new NodeVoid(this.unaryExpression());
         case 100:
            this._t.getToken();
            return new NodeUnaryOp(90, this.unaryExpression());
         case 101:
            this._t.getToken();
            return new NodePrefix(19, this.unaryExpression());
         case 103:
            this._t.getToken();
            return new NodeNotOp(this.unaryExpression());
         case 131:
            this._t.getToken();
            return new NodeUnaryOp(2, this.unaryExpression());
         case 132:
            this._t.getToken();
            return new NodeUnaryOp(-122, this.unaryExpression());
         case 133:
            this._t.getToken();
            return new NodePrefix(77, this.unaryExpression());
         default:
            return this.postfixExpression();
      }
   }

   private Node multiplicativeExpression() {
      Node rc = this.unaryExpression();

      while (true) {
         int op;
         switch (this._t.peekToken()) {
            case 106:
               op = 88;
               break;
            case 113:
               op = 89;
               break;
            case 117:
               op = 24;
               break;
            default:
               return rc;
         }

         this._t.getToken();
         rc = new NodeBinaryOp(op, rc, this.unaryExpression());
      }
   }

   private Node additiveExpression() {
      Node rc = this.multiplicativeExpression();

      while (true) {
         int op;
         switch (this._t.peekToken()) {
            case 100:
               op = -126;
               break;
            case 132:
               op = 0;
               break;
            default:
               return rc;
         }

         this._t.getToken();
         rc = new NodeBinaryOp(op, rc, this.multiplicativeExpression());
      }
   }

   private Node shiftExpression() {
      Node rc = this.additiveExpression();

      while (true) {
         int op;
         switch (this._t.peekToken()) {
            case 136:
               op = 87;
               break;
            case 144:
               op = 127;
               break;
            case 146:
               op = -118;
               break;
            default:
               return rc;
         }

         this._t.getToken();
         rc = new NodeBinaryOp(op, rc, this.additiveExpression());
      }
   }

   private Node relationalExpression() {
      Node rc = this.shiftExpression();

      while (true) {
         int op;
         switch (this._t.peekToken()) {
            case 13:
               if (!this._allowIn) {
                  return rc;
               }

               op = 76;
               break;
            case 14:
               op = 83;
               break;
            case 135:
               op = 15;
               break;
            case 138:
               op = 14;
               break;
            case 142:
               op = 13;
               break;
            case 143:
               op = 12;
               break;
            default:
               return rc;
         }

         this._t.getToken();
         rc = new NodeRelationalOp(op, rc, this.shiftExpression());
      }
   }

   private Node equalityExpression() {
      Node rc = this.relationalExpression();

      while (true) {
         int op;
         switch (this._t.peekToken()) {
            case 104:
               op = this._cmpNeOp;
               break;
            case 105:
               op = 18;
               break;
            case 140:
               op = this._cmpEqOp;
               break;
            case 141:
               op = 17;
               break;
            default:
               return rc;
         }

         this._t.getToken();
         rc = new NodeRelationalOp(op, rc, this.relationalExpression());
      }
   }

   private Node bitwiseANDExpression() {
      Node rc = this.equalityExpression();

      while (this.matchToken(108)) {
         rc = new NodeBinaryOp(1, rc, this.equalityExpression());
      }

      return rc;
   }

   private Node bitwiseXORExpression() {
      Node rc = this.bitwiseANDExpression();

      while (this.matchToken(124)) {
         rc = new NodeBinaryOp(4, rc, this.bitwiseANDExpression());
      }

      return rc;
   }

   private Node bitwiseORExpression() {
      Node rc = this.bitwiseXORExpression();

      while (this.matchToken(127)) {
         rc = new NodeBinaryOp(3, rc, this.bitwiseXORExpression());
      }

      return rc;
   }

   private Node logicalANDExpression() {
      Node rc = this.bitwiseORExpression();

      while (this.matchToken(109)) {
         rc = new NodeAnd(this._c, rc, this.bitwiseORExpression());
      }

      return rc;
   }

   private Node logicalORExpression() {
      Node rc = this.logicalANDExpression();

      while (this.matchToken(128)) {
         rc = new NodeOr(this._c, rc, this.logicalANDExpression());
      }

      return rc;
   }

   private Node conditionalExpression() {
      Node rc = this.logicalORExpression();
      if (this.matchToken(121)) {
         Node lhs = this.assignmentExpression();
         this.expect(119);
         Node rhs = this.assignmentExpression();
         rc = new NodeQuestion(this._c, rc, lhs, rhs);
      }

      return rc;
   }

   private Node assignmentExpression() {
      Node rc = this.conditionalExpression();

      while (true) {
         switch (this._t.peekToken()) {
            case 102:
               this._t.getToken();
               rc = new NodeAssignmentOp(-126, rc, this.assignmentExpression());
               break;
            case 107:
               this._t.getToken();
               rc = new NodeAssignmentOp(88, rc, this.assignmentExpression());
               break;
            case 110:
               this._t.getToken();
               rc = new NodeAssignmentOp(1, rc, this.assignmentExpression());
               break;
            case 114:
               this._t.getToken();
               rc = new NodeAssignmentOp(89, rc, this.assignmentExpression());
               break;
            case 118:
               this._t.getToken();
               rc = new NodeAssignmentOp(24, rc, this.assignmentExpression());
               break;
            case 125:
               this._t.getToken();
               rc = new NodeAssignmentOp(4, rc, this.assignmentExpression());
               break;
            case 129:
               this._t.getToken();
               rc = new NodeAssignmentOp(3, rc, this.assignmentExpression());
               break;
            case 134:
               this._t.getToken();
               rc = new NodeAssignmentOp(0, rc, this.assignmentExpression());
               break;
            case 137:
               this._t.getToken();
               rc = new NodeAssignmentOp(87, rc, this.assignmentExpression());
               break;
            case 139:
               this._t.getToken();
               rc = new NodeAssignment(rc, this.assignmentExpression());
               break;
            case 145:
               this._t.getToken();
               rc = new NodeAssignmentOp(127, rc, this.assignmentExpression());
               break;
            case 147:
               this._t.getToken();
               rc = new NodeAssignmentOp(-118, rc, this.assignmentExpression());
               break;
            default:
               return rc.fold(this._currFunc);
         }
      }
   }

   private Node expression() {
      Node rc = this.assignmentExpression();

      while (this.matchToken(115)) {
         rc = new NodeComma(rc, this.assignmentExpression());
      }

      return rc;
   }

   private void pushStatementLabel(String name) {
      if (name != null && this._statementLabelLookup.get(name) != null) {
         throw new CompileError(Resources.getString(2), name);
      }

      this._statementStack = new StatementLabel(this._c, this._statementStack, name);
      if (name != null) {
         this._statementLabelLookup.put(name, this._statementStack);
      }
   }

   private void popStatementLabel() {
      String name = this._statementStack.getName();
      if (name != null) {
         this._statementLabelLookup.remove(name);
      }

      this._statementStack = this._statementStack.getParent();
   }

   private NodeTemp genTemp() {
      return new NodeTemp(this._currFunc.nextTemp());
   }
}
