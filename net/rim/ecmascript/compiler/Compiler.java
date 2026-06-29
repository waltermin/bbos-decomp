package net.rim.ecmascript.compiler;

import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.runtime.Value;
import net.rim.ecmascript.util.IndexHash;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class Compiler {
   private PrintStream _debugOut;
   private int _flags;
   private Function _globalCode;
   private IndexHash _idTable;
   private IndexHash _stringTable;
   private IndexHash _doubleTable;
   private int[] _integerTable;
   private int _integerTableSize;
   private Tokenizer _tokenizer;
   private String _source;
   private int _version;
   private int _labelId;
   private Vector _labels = (Vector)(new Object());
   private CompiledScript _globalCompiledCode;
   private static int _versionMajor = 0;
   private static final int VERSION_MINOR = 8;
   private static int _versionMinor = 8;
   public static final int PRINT_COMPACT_SOURCE = 1;
   public static final int DISCARD_GLOBAL_RETURN = 2;
   public static final int OMIT_COMPILED_SOURCE = 4;
   public static final int OMIT_COMPILED_SOURCE_AND_LINE_NUMBERS = 8;
   public static final int DEBUG_TOKENS = Integer.MIN_VALUE;
   public static final int DEBUG_LONG_TOKENS = 1073741824;
   public static final int DEBUG_PARSER = 536870912;
   public static final int DEBUG_DUMP_IR = 268435456;
   public static final int DEBUG_DUMP_LST = 134217728;
   public static final int FOR_EVAL = 67108864;
   public static final int FOR_FUNCTION_CONSTRUCTOR = 33554432;
   public static final int FOR_DEBUG = 16777216;

   public Compiler(String source) {
      this(source, 0);
   }

   public Compiler(String source, int version) {
      this(source, version, System.out, 0);
   }

   public Compiler(String source, int version, PrintStream debugOut, int flags) {
      this._source = source;
      this._version = version;
      this._debugOut = debugOut;
      this._flags = flags;
      this._idTable = new IndexHash();
      this._stringTable = new IndexHash();
      this._doubleTable = new IndexHash();
      this._integerTable = new int[256];
      _versionMinor = 8;
   }

   public static int getMajorVersion() {
      return _versionMajor;
   }

   public static int getMinorVersion() {
      return _versionMinor;
   }

   public CompiledScript compile() {
      if (this._source == null) {
         throw new CompileError(Resources.getString(1));
      }

      this._tokenizer = new Tokenizer(this, this._source);
      this._source = null;

      try {
         new Parser(this, this._tokenizer).parse();
         if ((this._flags & 268435456) != 0) {
            this.println("===== BEFORE OPTIMIZER =====", 0);
            this._globalCode.dump();
            this.println("", 0);
         }

         this._globalCode.generate();
         if ((this._flags & 268435456) != 0) {
            this.println("===== AFTER OPTIMIZER =====", 0);
            this._globalCode.dump();
            this.println("", 0);
            this.println("===== DONE =====", 0);
         }

         if ((this._flags & 402653184) != 0) {
            this.dump();
            this.println("", 0);
         }

         CompiledScript code = this.fini();
         this._globalCompiledCode = code;
         if ((this._flags & 134217728) != 0) {
            ByteCode.dumpCompiledCode(this, code, 0);
            this.println("", 0);
         }

         if ((this._flags & -2147483648) != 0) {
            this._tokenizer.dump(code, 0);
         }

         if ((this._flags & 1) != 0) {
            this._tokenizer.dump(code, 1);
         }

         return code;
      } catch (CompileError ce) {
         ce.setTokenInfo(this.compilingForEval(), this._tokenizer.getLine(), this._tokenizer.getLineNumber(), this._tokenizer.getColumn());
         throw ce;
      }
   }

   void addLabel(Label l) {
      l.setId(this._labelId++);
      this._labels.addElement(l);
   }

   Label getLabelFromId(int id) {
      return (Label)this._labels.elementAt(id);
   }

   void replaceBlock(Block b1, Block b2) {
      for (int i = this._labels.size() - 1; i >= 0; i--) {
         Label l = (Label)this._labels.elementAt(i);
         if (l.getBlock() == b1) {
            l.setBlock(b2);
         }
      }
   }

   int getVersion() {
      return this._version;
   }

   private String[] hashToArray(IndexHash ht) {
      String[] sa = new Object[ht.size()];
      Enumeration e = ht.elements();
      int i = 0;

      while (e.hasMoreElements()) {
         sa[i++] = Misc.stringIntern((String)e.nextElement());
      }

      return sa;
   }

   private double[] hashToDoubleArray(IndexHash ht) {
      double[] sa = new double[ht.size()];
      Enumeration e = ht.elements();
      int i = 0;

      while (e.hasMoreElements()) {
         sa[i++] = e.nextElement();
      }

      return sa;
   }

   private CompiledScript fini() {
      String[] idArray = this.hashToArray(this._idTable);
      String[] stringArray = this.hashToArray(this._stringTable);
      double[] doubleArray = this.hashToDoubleArray(this._doubleTable);
      byte[] tokenStream = this._tokenizer.getTokenStream();
      long[] intArray = new long[this._integerTableSize];

      for (int i = this._integerTableSize - 1; i >= 0; i--) {
         intArray[i] = Value.makeIntegerValue(this._integerTable[i]);
      }

      CompiledScript code = this._globalCode.fini(idArray, stringArray, doubleArray, intArray, tokenStream);
      this._idTable = null;
      this._stringTable = null;
      this._doubleTable = null;
      this._integerTable = null;
      return code;
   }

   int addId(String name) {
      return this._idTable.add(name);
   }

   int getIdArguments() {
      return this._idTable.getIndex("arguments");
   }

   int getIdEval() {
      return this._idTable.getIndex("eval");
   }

   String getId(int id) {
      return (String)this._idTable.get(id);
   }

   int getIdIndex(String name) {
      return this._idTable.getIndex(name);
   }

   int addString(String name) {
      return this._stringTable.add(name);
   }

   String getString(int id) {
      return (String)this._stringTable.get(id);
   }

   int getStringIndex(String name) {
      return this._stringTable.getIndex(name);
   }

   int addDouble(Double name) {
      return this._doubleTable.add(name);
   }

   int addInteger(int value) {
      int length = this._integerTableSize;

      for (int i = length - 1; i >= 0; i--) {
         if (value == this._integerTable[i]) {
            return i;
         }
      }

      if (length >= 127) {
         return -1;
      }

      this._integerTable[length] = value;
      this._integerTableSize = length + 1;
      return length;
   }

   Double getDouble(int index) {
      return (Double)this._doubleTable.get(index);
   }

   int getInteger(int index) {
      return this._integerTable[index];
   }

   void addGlobalCode(Function f) {
      this._globalCode = f;
   }

   Function getGlobalCode() {
      return this._globalCode;
   }

   public CompiledScript getGlobalCompiledCode() {
      return this._globalCompiledCode;
   }

   boolean getOmitCompiledLineNumbers() {
      return (this._flags & 16777216) != 0 ? false : (this._flags & 8) != 0;
   }

   boolean getOmitCompiledSource() {
      return (this._flags & 16777216) != 0 ? false : (this._flags & 12) != 0;
   }

   boolean wantDebugParser() {
      return (this._flags & 536870912) != 0;
   }

   boolean compilingForEval() {
      return (this._flags & 67108864) != 0;
   }

   boolean compilingForDebug() {
      return (this._flags & 16777216) != 0;
   }

   boolean compilingForFunctionConstructor() {
      return (this._flags & 33554432) != 0;
   }

   boolean needGlobalReturn() {
      if ((this._flags & 67108864) != 0) {
         return true;
      } else if ((this._flags & 16777216) != 0) {
         return true;
      } else {
         return (this._flags & 33554432) != 0 ? true : (this._flags & 2) == 0;
      }
   }

   void print4(int i) {
      String s = Integer.toString(i++);
      int len = s.length();

      while (len++ < 4) {
         this.print("0");
      }

      this.print(s);
   }

   void print(Object o) {
      this._debugOut.print(o);
   }

   void println(Object o) {
      this.println(o, 0);
   }

   void println(Object o, int indent) {
      this._debugOut.println(o);

      for (int i = 0; i < indent; i++) {
         this._debugOut.print(" ");
      }
   }

   void dump() {
      Enumeration e = this._idTable.elements();
      if (e.hasMoreElements()) {
         int i = 0;
         this.println("IDTable: ", 4);

         do {
            this.print4(i++);
            this.print(": ");
            this.println(e.nextElement(), 4);
         } while (e.hasMoreElements());

         this.println("", 0);
      }

      e = this._stringTable.elements();
      if (e.hasMoreElements()) {
         int i = 0;
         this.println("stringTable: ", 4);

         do {
            this.print4(i++);
            this.print(": ");
            this.println(e.nextElement(), 4);
         } while (e.hasMoreElements());

         this.println("", 0);
      }

      e = this._doubleTable.elements();
      if (e.hasMoreElements()) {
         int i = 0;
         this.println("doubleTable: ", 4);

         do {
            this.print4(i++);
            this.print(": ");
            this.println(e.nextElement(), 4);
         } while (e.hasMoreElements());

         this.println("", 0);
      }
   }
}
