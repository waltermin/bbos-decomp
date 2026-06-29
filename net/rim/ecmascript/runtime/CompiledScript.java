package net.rim.ecmascript.runtime;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import net.rim.device.api.util.Arrays;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.compiler.Tokenizer;
import net.rim.ecmascript.util.IntVector;
import net.rim.ecmascript.util.Misc;

public class CompiledScript implements DebugScript {
   byte compilerVersionMajor;
   byte compilerVersionMinor;
   String[] ids;
   String[] strings;
   double[] doubles;
   long[] integers;
   byte[] tokenStream;
   CompiledScript[] functionDeclarations;
   CompiledScript[] functionExpressions;
   char version;
   char id;
   int[] locals;
   int[] parmLocations;
   char numParms;
   char lastRealLocal;
   char maxStack;
   char numTemps;
   byte[] code;
   char[] lineNumbers;
   char[] lineOffsets;
   char[] lineTokenOffsets;
   char tokenStart;
   char tokenEnd;
   int[] globals;
   boolean needsScope;
   boolean sensible;
   boolean forDebug;
   boolean _hasBreak;
   boolean[] _lineBreakpoints;
   CompiledScript _parent;
   static Object _syncObj = new Object();
   static byte[] _byteBuffer = new byte[0];
   static char[] _charBuffer = new char[0];

   public void serialize(OutputStream stream) {
      DataOutputStream out = (DataOutputStream)(new Object(stream));
      writeStringArray(out, this.ids);
      writeStringArray(out, this.strings);
      writeDoubleArray(out, this.doubles);
      writeLongAsIntegerArray(out, this.integers);
      if (this.tokenStream != null && this.tokenStream.length >= 65535) {
         writeByteArray(out, null);
         serializeScript(out, this);
         writeBigByteArray(out, this.tokenStream);
      } else {
         writeByteArray(out, this.tokenStream);
         serializeScript(out, this);
      }
   }

   public boolean getNeedsScope() {
      return this.needsScope;
   }

   public boolean getIsSensible() {
      return this.sensible;
   }

   public int getVersion() {
      return this.version;
   }

   public int getCompilerVersionMajor() {
      return this.compilerVersionMajor;
   }

   public int getCompilerVersionMinor() {
      return this.compilerVersionMinor;
   }

   public int getLastRealLocal() {
      return this.lastRealLocal;
   }

   public CompiledScript getFirstFunction() {
      return this.functionExpressions != null && this.functionExpressions.length != 0 ? this.functionExpressions[0] : null;
   }

   public String getLocalName(int operand) {
      return this.ids[this.locals[operand]];
   }

   public String getId(int operand) {
      return operand == 65535 ? "" : this.ids[operand];
   }

   public String getId() {
      return this.getId(this.id);
   }

   public String getString(int operand) {
      return this.strings[operand];
   }

   public double getDouble(int operand) {
      return this.doubles[operand];
   }

   public int getInteger(int operand) {
      return (int)this.integers[operand];
   }

   public int getNumFunctionExpressions() {
      return this.functionExpressions != null ? this.functionExpressions.length : 0;
   }

   public CompiledScript getFunctionExpression(int operand) {
      return this.functionExpressions[operand];
   }

   public int getNumFunctionDeclarations() {
      return this.functionDeclarations != null ? this.functionDeclarations.length : 0;
   }

   public CompiledScript getFunctionDeclaration(int operand) {
      return this.functionDeclarations[operand];
   }

   public boolean hasTokens() {
      return this.tokenStream != null;
   }

   public boolean hasLineNumbers() {
      return this.lineNumbers != null;
   }

   public int getNumLines() {
      return this.lineOffsets == null ? 0 : this.lineOffsets.length;
   }

   public int getLineOffset(int operand) {
      return this.lineOffsets[operand];
   }

   public int getLineNumber(int operand) {
      return this.lineNumbers[operand];
   }

   public int getLineTokenOffset(int operand) {
      return this.lineTokenOffsets[operand];
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public int getNextLineTokenOffset(int operand, boolean includeSubscripts) {
      int first = this.lineTokenOffsets[operand];
      boolean var7 = false /* VF: Semaphore variable */;

      int next;
      label26:
      try {
         var7 = true;
         next = this.getLineTokenOffset(operand + 1);
         var7 = false;
      } finally {
         if (var7) {
            next = this.getTokenEnd();
            break label26;
         }
      }

      if (!includeSubscripts) {
         next = this.searchTokenOffsets(this.functionDeclarations, first, next);
         next = this.searchTokenOffsets(this.functionExpressions, first, next);
      }

      return next;
   }

   public int getTokenEnd() {
      return this.tokenEnd;
   }

   public int getTokenStart() {
      return this.tokenStart;
   }

   public int getToken(int i) {
      return this.tokenStream[i] & 0xFF;
   }

   public int getCodeLength() {
      return this.code.length;
   }

   public byte getCode(int i) {
      return this.code[i];
   }

   public int getMaxStack() {
      return this.maxStack;
   }

   public int getNumTemps() {
      return this.numTemps;
   }

   public int getNumLocals() {
      return this.locals.length;
   }

   public int getNumParms() {
      return this.numParms;
   }

   public boolean hasDuplicateParms() {
      return this.parmLocations != null;
   }

   public String getParmLocation(int i) {
      return Integer.toString(this.parmLocations[i]);
   }

   public boolean getLineAndOffsets(int ip, LineAndOffsets rc) {
      if (this.lineOffsets == null) {
         return false;
      }

      if (this.lineNumbers == null) {
         return false;
      }

      rc.line = 0;
      rc.offset = -1;

      for (int i = 0; i < this.lineOffsets.length; i++) {
         int offset = this.lineOffsets[i];
         if (offset > ip) {
            break;
         }

         if (offset > rc.offset) {
            rc.line = this.lineNumbers[i];
            rc.offset = offset;
            if (this._lineBreakpoints != null) {
               rc.hasBreakpoint = this._lineBreakpoints[i];
            }
         }
      }

      return rc.offset != -1;
   }

   String getSourceCodeFromOffset(int offset) {
      return Tokenizer.getSource(this, offset);
   }

   CompiledScript getTopLevelScript() {
      CompiledScript top = this;

      while (true) {
         CompiledScript parent = top._parent;
         if (parent == null) {
            return top;
         }

         top = parent;
      }
   }

   void addLineNumbers(IntVector v) {
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            this.functionDeclarations[i].addLineNumbers(v);
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            this.functionExpressions[i].addLineNumbers(v);
         }
      }

      for (int i = 0; i < this.lineNumbers.length; i++) {
         v.addElement(this.lineNumbers[i]);
      }
   }

   void clearAllBreakpoints() {
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            this.functionDeclarations[i].clearAllBreakpoints();
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            this.functionExpressions[i].clearAllBreakpoints();
         }
      }

      this._lineBreakpoints = null;
      this._hasBreak = false;
   }

   boolean clearBreakpoint(int line) {
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            if (this.functionDeclarations[i].clearBreakpoint(line)) {
               return true;
            }
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            if (this.functionExpressions[i].clearBreakpoint(line)) {
               return true;
            }
         }
      }

      if (this.lineOffsets == null) {
         return false;
      }

      if (this._lineBreakpoints == null) {
         return false;
      }

      boolean allCleared = true;
      boolean breakCleared = false;

      for (int i = 0; i < this.lineOffsets.length; i++) {
         if (this.lineNumbers[i] == line) {
            breakCleared = this._lineBreakpoints[i];
            this._lineBreakpoints[i] = false;
         } else if (this._lineBreakpoints[i]) {
            allCleared = false;
         }
      }

      if (allCleared) {
         this._lineBreakpoints = null;
      }

      return breakCleared;
   }

   boolean setBreakpoint(int line) {
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            if (this.functionDeclarations[i].setBreakpoint(line)) {
               return true;
            }
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            if (this.functionExpressions[i].setBreakpoint(line)) {
               return true;
            }
         }
      }

      if (this.lineOffsets == null) {
         return false;
      }

      if (this._lineBreakpoints == null) {
         this._lineBreakpoints = new boolean[this.lineOffsets.length];
      }

      for (int i = 0; i < this.lineOffsets.length; i++) {
         if (this.lineNumbers[i] == line) {
            this._lineBreakpoints[i] = true;
            return true;
         }
      }

      return false;
   }

   public boolean haveBreakpoint(int line) {
      if (this._lineBreakpoints == null) {
         return false;
      }

      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            if (this.functionDeclarations[i].haveBreakpoint(line)) {
               return true;
            }
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            if (this.functionExpressions[i].haveBreakpoint(line)) {
               return true;
            }
         }
      }

      for (int i = 0; i < this.lineNumbers.length; i++) {
         if (this.lineNumbers[i] == line) {
            return this._lineBreakpoints[i];
         }
      }

      return false;
   }

   @Override
   public CompiledScript getCompiledScript() {
      return this;
   }

   @Override
   public int getClosestLine(int line) {
      int closest = -1;
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            int cl = this.functionDeclarations[i].getClosestLine(line);
            if (closest == -1 || cl != -1 && cl < closest) {
               closest = cl;
            }
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            int cl = this.functionExpressions[i].getClosestLine(line);
            if (closest == -1 || cl != -1 && cl < closest) {
               closest = cl;
            }
         }
      }

      for (int i = 0; i < this.lineNumbers.length; i++) {
         if (this.lineNumbers[i] >= line) {
            int cl = this.lineNumbers[i];
            if (closest == -1 || cl < closest) {
               return cl;
            }
         }
      }

      return closest;
   }

   @Override
   public String getSourceLine(int line) {
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            String rc = this.functionDeclarations[i].getSourceLine(line);
            if (rc != null) {
               return rc;
            }
         }
      }

      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            String rc = this.functionExpressions[i].getSourceLine(line);
            if (rc != null) {
               return rc;
            }
         }
      }

      for (int i = 0; i < this.lineOffsets.length; i++) {
         if (this.lineNumbers[i] == line) {
            return Tokenizer.getSource(this, this.lineOffsets[i], false);
         }
      }

      return null;
   }

   @Override
   public int[] getLineNumbers() {
      IntVector v = new IntVector();
      this.addLineNumbers(v);
      int[] ia = new int[v.size()];

      for (int i = v.size() - 1; i >= 0; i--) {
         ia[i] = v.elementAt(i);
      }

      Arrays.sort(ia, 0, ia.length);
      return ia;
   }

   private static void writeSmallInt(DataOutputStream out, int v) {
      if (v < 127 && v > -128) {
         out.writeByte((byte)v);
      } else if (v < 32767 && v > -32768) {
         out.writeByte(-128);
         out.writeShort((short)v);
      } else {
         out.writeByte(127);
         out.writeInt(v);
      }
   }

   private static int readSmallInt(DataInputStream in) {
      int v = in.readByte();
      if (v == -128) {
         return in.readShort();
      } else {
         return v == 127 ? in.readInt() : v;
      }
   }

   private static void writeSmallChar(DataOutputStream out, int v) {
      if (v < 255 && v > 0) {
         out.writeByte((byte)v);
      } else {
         out.writeByte(255);
         out.writeChar((char)v);
      }
   }

   private int searchTokenOffsets(CompiledScript[] scripts, int first, int next) {
      if (scripts == null) {
         return next;
      }

      for (int i = scripts.length - 1; i >= 0; i--) {
         CompiledScript script = scripts[i];
         char[] offsets = script.lineTokenOffsets;
         if (offsets != null) {
            for (int j = offsets.length - 1; j >= 0; j--) {
               int test = offsets[j];
               if (test > first && test < next) {
                  next = test;
               }
            }
         }
      }

      return next;
   }

   private static char readSmallChar(DataInputStream in) {
      int v = in.readByte() & 255;
      return v == 255 ? in.readChar() : (char)v;
   }

   private static void writeIntArray(DataOutputStream out, int[] a) {
      arraySizeCheck(a == null || a.length < 65535);
      if (a == null) {
         out.writeChar(65535);
      } else {
         int length = a.length;
         out.writeChar((char)length);

         for (int i = 0; i < length; i++) {
            writeSmallInt(out, a[i]);
         }
      }
   }

   private static int[] readIntArray(DataInputStream in) {
      int length = in.readChar();
      if (length == 65535) {
         return null;
      }

      int[] a = new int[length];

      for (int i = 0; i < length; i++) {
         a[i] = readSmallInt(in);
      }

      return a;
   }

   public CompiledScript(
      int _version,
      int _id,
      int[] _locals,
      int _lastRealLocal,
      int[] _parmLocations,
      int _numParms,
      CompiledScript[] _functionDeclarations,
      CompiledScript[] _functionExpressions,
      String[] _ids,
      String[] _strings,
      double[] _doubles,
      long[] _integers,
      int _maxStack,
      int _numTemps,
      byte[] _code,
      char[] _lineNumbers,
      char[] _lineOffsets,
      char[] _lineTokenOffsets,
      byte[] _tokenStream,
      int _tokenStart,
      int _tokenEnd,
      boolean _needsScope,
      boolean _forDebug
   ) {
      this.compilerVersionMajor = (byte)Compiler.getMajorVersion();
      this.compilerVersionMinor = (byte)Compiler.getMinorVersion();
      this.version = (char)_version;
      this.id = (char)_id;
      this.locals = _locals;
      this.lastRealLocal = (char)_lastRealLocal;
      this.parmLocations = _parmLocations;
      this.numParms = (char)_numParms;
      this.functionDeclarations = _functionDeclarations;
      if (this.functionDeclarations != null) {
         for (int i = this.functionDeclarations.length - 1; i >= 0; i--) {
            this.functionDeclarations[i]._parent = this;
         }
      }

      this.functionExpressions = _functionExpressions;
      if (this.functionExpressions != null) {
         for (int i = this.functionExpressions.length - 1; i >= 0; i--) {
            this.functionExpressions[i]._parent = this;
         }
      }

      this.ids = _ids;
      this.strings = _strings;
      this.doubles = _doubles;
      this.integers = _integers;
      this.maxStack = (char)(1 + _maxStack);
      this.numTemps = (char)_numTemps;
      this.code = _code;
      this.lineNumbers = _lineNumbers;
      this.lineOffsets = _lineOffsets;
      this.lineTokenOffsets = _lineTokenOffsets;
      this.tokenStream = _tokenStream;
      this.tokenStart = (char)_tokenStart;
      this.tokenEnd = (char)_tokenEnd;
      this.needsScope = _needsScope;
      if (!this.needsScope && this.parmLocations == null) {
         this.sensible = true;
      }

      this.forDebug = _forDebug;
   }

   private CompiledScript() {
   }

   public static CompiledScript deserialize(InputStream stream) {
      return deserialize(stream, true);
   }

   public static CompiledScript deserialize(InputStream stream, boolean verify) {
      synchronized (_syncObj) {
         DataInputStream in = (DataInputStream)(new Object(stream));
         String[] ids = readStringArray(in);
         String[] strings = readStringArray(in);
         double[] doubles = readDoubleArray(in);
         long[] integers = readLongAsIntegerArray(in);
         byte[] tokenStream = readByteArray(in);
         CompiledScript script = deserializeScript(in, ids, strings, doubles, integers, tokenStream);
         if (tokenStream == null && in.available() != 0) {
            tokenStream = readBigByteArray(in);
         }

         if (verify) {
            try {
               new Verifier(script.locals == null ? 0 : script.locals.length).verify(script);
            } catch (VerifyError ve) {
               throw new Object("invalid script");
            }
         }

         _byteBuffer = new byte[0];
         _charBuffer = new char[0];
         return script;
      }
   }

   private static void serializeScript(DataOutputStream out, CompiledScript script) {
      out.writeByte(script.compilerVersionMajor);
      out.writeByte(script.compilerVersionMinor);
      int numFunctionsDeclarations = script.getNumFunctionDeclarations();
      writeSmallChar(out, numFunctionsDeclarations);

      for (int i = 0; i < numFunctionsDeclarations; i++) {
         serializeScript(out, script.functionDeclarations[i]);
      }

      int numFunctionsExpressions = script.getNumFunctionExpressions();
      writeSmallChar(out, numFunctionsExpressions);

      for (int i = 0; i < numFunctionsExpressions; i++) {
         serializeScript(out, script.functionExpressions[i]);
      }

      writeSmallChar(out, script.version);
      writeSmallChar(out, script.id);
      writeIntArray(out, script.locals);
      writeIntArray(out, script.parmLocations);
      writeSmallChar(out, script.numParms);
      writeSmallChar(out, script.lastRealLocal);
      writeSmallChar(out, script.maxStack - 1);
      writeSmallChar(out, script.numTemps);
      writeByteArray(out, script.code);
      writeCharArray(out, script.lineNumbers);
      writeCharArray(out, script.lineOffsets);
      writeCharArray(out, script.lineTokenOffsets);
      out.writeChar(script.tokenStart);
      out.writeChar(script.tokenEnd);
      writeIntArray(out, script.globals);
      byte b = 0;
      if (script.needsScope) {
         b = (byte)(b | 1);
      }

      if (script.sensible) {
         b = (byte)(b | 2);
      }

      if (script.forDebug) {
         b = (byte)(b | 4);
      }

      out.writeByte(b);
   }

   private static CompiledScript deserializeScript(DataInputStream in, String[] ids, String[] strings, double[] doubles, long[] integers, byte[] tokenStream) {
      CompiledScript script = new CompiledScript();
      script.compilerVersionMajor = in.readByte();
      script.compilerVersionMinor = in.readByte();
      if (!GlobalObject.versionCheck(script)) {
         return script;
      }

      script.ids = ids;
      script.strings = strings;
      script.doubles = doubles;
      script.integers = integers;
      script.tokenStream = tokenStream;
      int numFunctionDeclarations = readSmallChar(in);
      if (numFunctionDeclarations > 0) {
         script.functionDeclarations = new CompiledScript[numFunctionDeclarations];

         for (int i = 0; i < numFunctionDeclarations; i++) {
            CompiledScript decl = deserializeScript(in, ids, strings, doubles, integers, tokenStream);
            decl._parent = script;
            script.functionDeclarations[i] = decl;
         }
      }

      int numFunctionExpressions = readSmallChar(in);
      if (numFunctionExpressions > 0) {
         script.functionExpressions = new CompiledScript[numFunctionExpressions];

         for (int i = 0; i < numFunctionExpressions; i++) {
            CompiledScript expr = deserializeScript(in, ids, strings, doubles, integers, tokenStream);
            expr._parent = script;
            script.functionExpressions[i] = expr;
         }
      }

      script.version = readSmallChar(in);
      script.id = readSmallChar(in);
      script.locals = readIntArray(in);
      script.parmLocations = readIntArray(in);
      script.numParms = readSmallChar(in);
      script.lastRealLocal = readSmallChar(in);
      script.maxStack = (char)(1 + readSmallChar(in));
      script.numTemps = readSmallChar(in);
      script.code = readByteArray(in);
      script.lineNumbers = readCharArray(in);
      script.lineOffsets = readCharArray(in);
      script.lineTokenOffsets = readCharArray(in);
      script.tokenStart = in.readChar();
      script.tokenEnd = in.readChar();
      script.globals = readIntArray(in);
      byte b = in.readByte();
      if ((b & 1) != 0) {
         script.needsScope = true;
      }

      if ((b & 2) != 0) {
         script.sensible = true;
      }

      if ((b & 4) != 0) {
         script.forDebug = true;
      }

      return script;
   }

   private static void arraySizeCheck(boolean ok) {
      if (!ok) {
         throw new Object("Script too large");
      }
   }

   private static void writeStringArray(DataOutputStream out, String[] a) {
      arraySizeCheck(a == null || a.length < 65535);
      if (a == null) {
         out.writeChar(65535);
      } else {
         int length = a.length;
         out.writeChar((char)length);

         for (int i = 0; i < length; i++) {
            writeString(out, a[i]);
         }
      }
   }

   private static String[] readStringArray(DataInputStream in) {
      int length = in.readChar();
      if (length == 65535) {
         return null;
      }

      String[] a = new Object[length];

      for (int i = 0; i < length; i++) {
         a[i] = Misc.stringIntern(readString(in));
      }

      return a;
   }

   private static void writeString(DataOutputStream out, String s) {
      if (s == null) {
         writeSmallInt(out, Integer.MAX_VALUE);
      } else {
         int length = s.length();

         for (int i = 0; i < length; i++) {
            if (s.charAt(i) > 255) {
               writeSmallInt(out, -length);

               for (int var4 = 0; var4 < length; var4++) {
                  writeSmallChar(out, s.charAt(var4));
               }

               return;
            }
         }

         writeSmallInt(out, length);

         for (int i = 0; i < length; i++) {
            out.writeByte((byte)s.charAt(i));
         }
      }
   }

   private static String readString(DataInputStream in) {
      int length = readSmallInt(in);
      if (length == Integer.MAX_VALUE) {
         return null;
      }

      if (length >= 0) {
         if (length > _byteBuffer.length) {
            _byteBuffer = new byte[length];
         }

         in.readFully(_byteBuffer, 0, length);
         return (String)(new Object(_byteBuffer, 0, length));
      } else {
         length = -length;
         if (length > _charBuffer.length) {
            _charBuffer = new char[length];
         }

         for (int i = 0; i < length; i++) {
            _charBuffer[i] = readSmallChar(in);
         }

         return (String)(new Object(_charBuffer, 0, length));
      }
   }

   private static char[] readCharArray(DataInputStream in) {
      int length = in.readChar();
      if (length == 65535) {
         return null;
      }

      char[] a = new char[length];

      for (int i = 0; i < length; i++) {
         a[i] = in.readChar();
      }

      return a;
   }

   private static double[] readDoubleArray(DataInputStream in) {
      int length = in.readChar();
      if (length == 65535) {
         return null;
      }

      double[] a = new double[length];

      for (int i = 0; i < length; i++) {
         a[i] = Double.longBitsToDouble(in.readLong());
      }

      return a;
   }

   private static void writeCharArray(DataOutputStream out, char[] a) {
      arraySizeCheck(a == null || a.length < 65535);
      if (a == null) {
         out.writeChar(65535);
      } else {
         int length = a.length;
         out.writeChar((char)length);

         for (int i = 0; i < length; i++) {
            out.writeChar(a[i]);
         }
      }
   }

   private static byte[] readBigByteArray(DataInputStream in) {
      int length = in.readInt();
      if (length == Integer.MAX_VALUE) {
         return null;
      }

      if (length > in.available()) {
         throw new Object("Unexpected EOF");
      }

      byte[] a = new byte[length];
      in.readFully(a);
      return a;
   }

   private static void writeBigByteArray(DataOutputStream out, byte[] a) {
      arraySizeCheck(a == null || a.length < Integer.MAX_VALUE);
      if (a == null) {
         out.writeInt(Integer.MAX_VALUE);
      } else {
         int length = a.length;
         out.writeInt(length);

         for (int i = 0; i < length; i++) {
            out.writeByte(a[i]);
         }
      }
   }

   private static byte[] readByteArray(DataInputStream in) {
      int length = in.readChar();
      if (length == 65535) {
         return null;
      }

      byte[] a = new byte[length];
      in.readFully(a);
      return a;
   }

   private static void writeByteArray(DataOutputStream out, byte[] a) {
      arraySizeCheck(a == null || a.length < 65535);
      if (a == null) {
         out.writeChar(65535);
      } else {
         int length = a.length;
         out.writeChar((char)length);

         for (int i = 0; i < length; i++) {
            out.writeByte(a[i]);
         }
      }
   }

   private static long[] readLongAsIntegerArray(DataInputStream in) {
      int length = in.readChar();
      if (length == 65535) {
         return null;
      }

      long[] a = new long[length];

      for (int i = 0; i < length; i++) {
         a[i] = Value.makeIntegerValue(in.readInt());
      }

      return a;
   }

   private static void writeLongAsIntegerArray(DataOutputStream out, long[] a) {
      arraySizeCheck(a == null || a.length < 65535);
      if (a == null) {
         out.writeChar(65535);
      } else {
         int length = a.length;
         out.writeChar((char)length);

         for (int i = 0; i < length; i++) {
            out.writeInt(Value.getIntegerValue(a[i]));
         }
      }
   }

   private static void writeDoubleArray(DataOutputStream out, double[] a) {
      arraySizeCheck(a == null || a.length < 65535);
      if (a == null) {
         out.writeChar(65535);
      } else {
         int length = a.length;
         out.writeChar((char)length);

         for (int i = 0; i < length; i++) {
            out.writeLong(Double.doubleToLongBits(a[i]));
         }
      }
   }
}
