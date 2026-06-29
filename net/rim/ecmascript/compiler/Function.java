package net.rim.ecmascript.compiler;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.util.IndexHash;
import net.rim.ecmascript.util.IntVector;

class Function {
   private Function _parent;
   private String _id;
   private IntVector _parms;
   private Vector _functionDeclarations;
   private Vector _functionExpressions;
   private byte[] _code;
   private int _withNesting;
   private int _index;
   private int _maxStackDepth;
   private int _stackDepth;
   private int _nextTempId;
   private IndexHash _localTable;
   private int _lastRealLocal;
   private IndexHash _constTable;
   private Compiler _c;
   private Vector _blocks;
   private Label _currLabel;
   private ByteCode _currCode;
   private IntVector _lineNumbers;
   private IntVector _lineOffsets;
   private IntVector _lineTokenOffsets;
   private int _tokenStart;
   private int _tokenEnd;
   private int _globalReturn;
   private boolean _evalCalled;
   private boolean _needsScope;
   private boolean _isFunctionExpression;

   Function(Compiler c, Function parent, String id, boolean isFunctionExpression) {
      this._c = c;
      this._parent = parent;
      if (parent != null) {
         this._withNesting = parent._withNesting;
      }

      this._id = id;
      this._localTable = new IndexHash();
      this._constTable = new IndexHash();
      this._parms = new IntVector();
      this._functionDeclarations = new Vector();
      this._functionExpressions = new Vector();
      this._blocks = new Vector();
      this._currLabel = new Label(this._c);
      this._currCode = new ByteCode(c);
      this._lineNumbers = new IntVector();
      this._lineOffsets = new IntVector();
      this._lineTokenOffsets = new IntVector();
      this._isFunctionExpression = isFunctionExpression;
      this._globalReturn = -1;
   }

   void setNeedsScope() {
      this._needsScope = true;
   }

   void setEvalCalled() {
      this._evalCalled = true;
   }

   boolean isEvalCalled() {
      return this._evalCalled;
   }

   boolean isNested() {
      return this._parent != null && !this._parent.isGlobalCode();
   }

   boolean isFunctionExpression() {
      return this._isFunctionExpression;
   }

   void setTokenRange(int start, int end) {
      this._tokenStart = start;
      this._tokenEnd = end;
   }

   void setCode(byte[] code) {
      this._code = code;
   }

   int getIndex() {
      return this._index;
   }

   Function getParent() {
      return this._parent;
   }

   Vector getBlocks() {
      return this._blocks;
   }

   void pushWith() {
      this._withNesting++;
   }

   void popWith() {
      this._withNesting--;
   }

   int addLocal(String id) {
      this._localTable.add(id);
      return this.addId(id);
   }

   void addConst(String id) {
      this._constTable.add(id);
   }

   boolean isConst(int id) {
      return this._constTable.getIndex(this._c.getId(id)) != -1;
   }

   int getLocalIndex(int id) {
      String name = this._c.getId(id);
      return this._localTable.getIndex(name);
   }

   String getLocal(int index) {
      return (String)this._localTable.get(index);
   }

   void addParm(String id) {
      int index = this.addLocal(id);
      this._parms.addElement(index);
   }

   Function addFunctionDeclaration(Function f) {
      f._index = this._functionDeclarations.size();
      this._functionDeclarations.addElement(f);
      return f;
   }

   Function addFunctionExpression(Function f) {
      f._index = this._functionExpressions.size();
      this._functionExpressions.addElement(f);
      return f;
   }

   Compiler getCompiler() {
      return this._c;
   }

   String getRegExp(int index) {
      return this._c.getString(index);
   }

   int addString(String value) {
      return this._c.addString(value);
   }

   String getString(int index) {
      return this._c.getString(index);
   }

   int addId(String value) {
      return this._c.addId(value);
   }

   String getId(int index) {
      return this._c.getId(index);
   }

   int addDouble(Double value) {
      return this._c.addDouble(value);
   }

   int addInteger(int value) {
      return this._c.addInteger(value);
   }

   Double getDouble(int index) {
      return this._c.getDouble(index);
   }

   int getInteger(int index) {
      return this._c.getInteger(index);
   }

   void addCode(int op) {
      this.addCode(op, -1);
   }

   void addCode(int op, int operand) {
      if (this._currLabel != null) {
         this._currCode.addCode(op, operand);
         switch (op) {
            case -113:
            case 5:
            case 6:
            case 8:
               this._stackDepth -= operand;
               break;
            default:
               this._stackDepth = this._stackDepth + OpcodeStackEffect.get(op);
         }

         if (this._stackDepth > this._maxStackDepth) {
            this._maxStackDepth = this._stackDepth;
         }
      }
   }

   void pushIntValue(int value) {
      if (value == 0) {
         this.addCode(100);
      } else if (value == 1) {
         this.addCode(101);
      } else if (value >= -128 && value <= 127) {
         this.addCode(99, value);
      } else {
         int index = this.addInteger(value);
         if (index == -1) {
            this.addCode(98, value);
         } else {
            this.addCode(97, index);
         }
      }
   }

   void resetStackDepth() {
      this._stackDepth = 0;
   }

   private void addBlock(Block b) {
      b.attachCode(this._currCode.copy());
      b.setWithNesting(this._withNesting);
      this._blocks.addElement(b);
   }

   void genIf(Label ifTrue, Label ifFalse) {
      if (this._currLabel != null) {
         this.addBlock(new BlockConditional(this._currLabel, ifTrue, ifFalse));
         this._currLabel = null;
      }
   }

   void genTry(Label tryy, Label katch) {
      if (this._currLabel != null) {
         this.addBlock(new BlockTry(this._currLabel, tryy, katch));
         this._currLabel = null;
      }
   }

   BlockGoto genGoto(Label l) {
      return this.genGotoHelper(l);
   }

   BlockGoto genGotoHelper(Label l) {
      if (this._currLabel == null) {
         return new BlockGoto(new Label(this._c), l);
      }

      BlockGoto blk = new BlockGoto(this._currLabel, l);
      this.addBlock(blk);
      this._currLabel = null;
      return blk;
   }

   BlockThrow genThrow() {
      if (this._currLabel == null) {
         return new BlockThrow(new Label(this._c));
      }

      BlockThrow blk = new BlockThrow(this._currLabel);
      this.addBlock(blk);
      this._currLabel = null;
      return blk;
   }

   void genJsr(Label l, int local) {
      if (this._currLabel != null) {
         Label next = new Label(this._c);
         this.addBlock(new BlockJsr(this._c, this._currLabel, l, next, local));
         this._currLabel = next;
      }
   }

   void genLabel(Label l) {
      if (this._currLabel != null) {
         this.genGotoHelper(l);
      }

      l.setIsGenerated();
      this._currLabel = l;
   }

   void genReturn(boolean haveValue) {
      if (this._currLabel != null) {
         this.addBlock(new BlockReturn(haveValue, this._currLabel));
         this._currLabel = null;
      }
   }

   void genRetJsr(int local) {
      if (this._currLabel != null) {
         this.addBlock(new RetBlockJsr(this._currLabel, local));
         this._currLabel = null;
      }
   }

   String getFunctionId(int index) {
      Function f = (Function)this._functionExpressions.elementAt(index);
      return f._id == null ? "" : f._id;
   }

   String getId() {
      return this._id;
   }

   boolean needGlobalReturn() {
      return this.isGlobalCode() && this._c.needGlobalReturn();
   }

   boolean isGlobalCode() {
      return this._globalReturn != -1;
   }

   void setGlobalReturn(int temp) {
      this._globalReturn = temp;
   }

   void saveGlobalReturn() {
      if (this._globalReturn != -1) {
         this.addCode(-128, this._globalReturn);
      }
   }

   void popGlobalReturn() {
      if (this._globalReturn != -1) {
         this.addCode(122, this._globalReturn);
      } else {
         this.addCode(92);
      }
   }

   void dump() {
      this.dump(0);
   }

   void generate() {
      this._lastRealLocal = this._localTable.size();
      new Optimizer(this._c, this).optimize();
      new GenerateCode(this).generate();
      Enumeration e = this._functionDeclarations.elements();
      if (e.hasMoreElements()) {
         do {
            ((Function)e.nextElement()).generate();
         } while (e.hasMoreElements());
      }

      e = this._functionExpressions.elements();
      if (e.hasMoreElements()) {
         do {
            ((Function)e.nextElement()).generate();
         } while (e.hasMoreElements());
      }
   }

   CompiledScript fini(String[] ids, String[] strings, double[] doubles, long[] integers, byte[] tokenStream) {
      int[] locals = new int[this._localTable.size()];
      Enumeration e = this._localTable.elements();
      int i = 0;

      while (e.hasMoreElements()) {
         locals[i++] = this._c.getIdIndex((String)e.nextElement());
      }

      boolean wackyParms = false;
      int[] parmLocations = new int[this._parms.size()];

      for (int var17 = 0; var17 < this._parms.size(); var17++) {
         int j = this.getLocalIndex(this._parms.elementAt(var17));
         if (var17 != j) {
            wackyParms = true;
         }

         parmLocations[var17] = j;
      }

      CompiledScript[] functionDeclarations = new CompiledScript[this._functionDeclarations.size()];
      i = 0;
      boolean hasNestedFunction = false;
      e = this._functionDeclarations.elements();
      if (e.hasMoreElements()) {
         do {
            Function f = (Function)e.nextElement();
            functionDeclarations[i++] = f.fini(ids, strings, doubles, integers, tokenStream);
            hasNestedFunction = true;
         } while (e.hasMoreElements());
      }

      CompiledScript[] functionExpressions = new CompiledScript[this._functionExpressions.size()];
      e = this._functionExpressions.elements();
      i = 0;
      if (e.hasMoreElements()) {
         do {
            Function f = (Function)e.nextElement();
            functionExpressions[i++] = f.fini(ids, strings, doubles, integers, tokenStream);
            hasNestedFunction = true;
         } while (e.hasMoreElements());
      }

      char[] lineTokenOffsets;
      if (this._c.getOmitCompiledSource()) {
         tokenStream = null;
         lineTokenOffsets = null;
      } else {
         lineTokenOffsets = this._lineTokenOffsets.toCharArray();
      }

      char[] lineOffsets;
      char[] lineNumbers;
      if (this._c.getOmitCompiledLineNumbers()) {
         lineNumbers = null;
         lineOffsets = null;
      } else {
         lineNumbers = this._lineNumbers.toCharArray();
         lineOffsets = this._lineOffsets.toCharArray();
      }

      return new CompiledScript(
         this._c.getVersion(),
         this._id == null ? -1 : this._c.getIdIndex(this._id),
         locals,
         this._lastRealLocal,
         wackyParms ? parmLocations : null,
         parmLocations.length,
         functionDeclarations,
         functionExpressions,
         ids,
         strings,
         doubles,
         integers,
         this._maxStackDepth + this._nextTempId,
         this._nextTempId,
         this._code,
         lineNumbers,
         lineOffsets,
         lineTokenOffsets,
         tokenStream,
         this._tokenStart,
         this._tokenEnd,
         this._needsScope | this._evalCalled | hasNestedFunction,
         this._c.compilingForDebug()
      );
   }

   int nextTemp() {
      return this._nextTempId++;
   }

   int getMaxStackDepth() {
      return this._maxStackDepth;
   }

   void addLineNumber(int offset, int lineNum, int tokenOffset) {
      this._lineNumbers.addElement(lineNum);
      this._lineOffsets.addElement(offset);
      this._lineTokenOffsets.addElement(tokenOffset);
   }

   private void dumpNumPrefix(Compiler c, int i) {
      c.print4(i);
      c.print(": ");
   }

   void dump(int indent) {
      this._c.print("Function: ");
      if (this._id == null) {
         this._c.print("null");
      } else {
         this._c.print(this._id);
      }

      this._c.print(" stack=");
      this._c.print(Integer.toString(this._maxStackDepth));
      indent += 4;
      this._c.println(", size=" + (this._code == null ? "???" : Integer.toString(this._code.length)), indent);
      int size = this._parms.size();
      if (size != 0) {
         int i = 0;
         this._c.println("Parms: ", indent + 4);

         do {
            int parm = this._parms.elementAt(i);
            this.dumpNumPrefix(this._c, i++);
            this._c.print(this._c.getId(parm));
            this._c.println("", indent + 4);
         } while (i < size);

         this._c.println("", indent);
      }

      Enumeration e = this._localTable.elements();
      if (e.hasMoreElements()) {
         int i = 0;
         this._c.println("Variables: ", indent + 4);

         do {
            String var = (String)e.nextElement();
            this.dumpNumPrefix(this._c, i++);
            this._c.print(var);
            this._c.print("\tid=");
            this._c.print4(this._c.getIdIndex(var));
            this._c.println("", indent + 4);
         } while (e.hasMoreElements());

         this._c.println("", indent);
      }

      for (int i = 0; i < this._blocks.size(); i++) {
         ((Block)this._blocks.elementAt(i)).dump(this, this._c, indent + 4);
      }

      e = this._functionDeclarations.elements();
      if (e.hasMoreElements()) {
         int i = 0;
         this._c.println("Function Declarations: ", indent + 4);

         do {
            this.dumpNumPrefix(this._c, i++);
            ((Function)e.nextElement()).dump(indent + 4);
         } while (e.hasMoreElements());

         this._c.println("", indent);
      }

      e = this._functionExpressions.elements();
      if (e.hasMoreElements()) {
         int i = 0;
         this._c.println("Function Expressions: ", indent + 4);

         do {
            this.dumpNumPrefix(this._c, i++);
            ((Function)e.nextElement()).dump(indent + 4);
         } while (e.hasMoreElements());

         this._c.println("", indent);
      }

      indent -= 4;
      this._c.println("", indent);
   }
}
