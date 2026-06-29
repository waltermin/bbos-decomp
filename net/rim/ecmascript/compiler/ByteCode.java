package net.rim.ecmascript.compiler;

import net.rim.ecmascript.runtime.CompiledScript;
import net.rim.ecmascript.util.Misc;

public class ByteCode implements OpcodeConstants {
   private int _opBuffLength = 100;
   private int _opBuffIndex = 0;
   private byte[] _opBuff = new byte[this._opBuffLength];
   private int[] _operandBuff = new int[this._opBuffLength];
   private Compiler _c;
   private byte[] _ops;
   private int[] _operands;

   ByteCode(Compiler c) {
      this._c = c;
   }

   ByteCode copy() {
      ByteCode bc = new ByteCode(this._c);
      bc._ops = new byte[this._opBuffIndex];
      System.arraycopy(this._opBuff, 0, bc._ops, 0, this._opBuffIndex);
      bc._operands = new int[this._opBuffIndex];
      System.arraycopy(this._operandBuff, 0, bc._operands, 0, this._opBuffIndex);
      this._opBuffIndex = 0;
      return bc;
   }

   void addCode(int op, int operand) {
      if (this._opBuffIndex >= this._opBuffLength) {
         this._opBuffLength += 10;
         this._opBuff = Misc.byteArrayResize(this._opBuff, this._opBuffLength);
         this._operandBuff = Misc.intArrayResize(this._operandBuff, this._opBuffLength);
      }

      this._opBuff[this._opBuffIndex] = (byte)op;
      this._operandBuff[this._opBuffIndex] = operand;
      this._opBuffIndex++;
   }

   void addCode(int op) {
      this.addCode(op, -1);
   }

   int generate(Function f, byte[] buffer, int index) {
      for (int i = 0; i < this._ops.length; i++) {
         int operand = this._operands[i];
         int op = this._ops[i];
         switch (OperandTypes.get(op)) {
            case -1:
            case 19:
               break;
            case 0:
            case 15: {
               int op2 = operand >> 24;
               operand &= 16777215;
               index = GenerateCode.genByteOrShortOp(op, operand, buffer, index);
               index = GenerateCode.genByte(op2, buffer, index);
               break;
            }
            case 1:
            case 2:
            case 3:
            case 7:
            case 11:
            case 17:
               index = GenerateCode.genByteOrShortOp(op, operand, buffer, index);
               break;
            case 4:
            case 14:
               index = GenerateCode.genIntOp(op, operand, buffer, index);
               break;
            case 5:
            case 6:
            case 9:
            case 12:
            case 18:
            case 20:
            case 23:
            case 24:
               index = GenerateCode.genShortOp(op, operand, buffer, index);
               break;
            case 8:
            case 25:
            case 27: {
               int op2 = operand >> 24;
               operand &= 16777215;
               index = GenerateCode.genShortOp(op, operand, buffer, index);
               index = GenerateCode.genByte(op2, buffer, index);
               break;
            }
            case 10:
            case 21:
               index = GenerateCode.genByteOp(op, operand, buffer, index);
               break;
            case 13:
               index = GenerateCode.genLabelOp(this._c, op, false, operand, buffer, index);
               break;
            case 16:
               index = GenerateCode.genByte(op, buffer, index);
               break;
            case 22:
               index = GenerateCode.genLabelOp(this._c, op, true, operand, buffer, index);
               break;
            case 26:
            default:
               int tokenOffset = (char)(operand >> 16);
               int lineNum = (char)operand;
               if (f != null) {
                  f.addLineNumber(index, lineNum, tokenOffset);
               }

               if (this._c.compilingForDebug()) {
                  index = GenerateCode.genByte(op, buffer, index);
                  index = GenerateCode.genShort(operand, buffer, index);
               }
         }
      }

      return index;
   }

   void optimizeLocals(Function f) {
      Function globalCode = this._c.getGlobalCode();
      int idArguments = this._c.getIdArguments();
      int increment;
      if (f.isGlobalCode()) {
         increment = 3;
      } else {
         increment = 1;
      }

      int i = 0;

      while (i < this._ops.length) {
         int op = this._ops[i];
         switch (OperandTypes.get(op + 1)) {
            case 2:
            case 15:
               int id = this._operands[i] & 16777215;
               if (!f.isConst(id)) {
                  int localIndex = f.getLocalIndex(id);
                  if (localIndex != -1) {
                     this._ops[i] = (byte)(op + increment);
                     this._operands[i] = this._operands[i] & 0xFF000000;
                     this._operands[i] = this._operands[i] | localIndex;
                  } else if (id != idArguments && !f.isEvalCalled() && !f.isNested()) {
                     localIndex = globalCode.getLocalIndex(id);
                     if (localIndex == -1) {
                        id = globalCode.addLocal(f.getId(id));
                        localIndex = globalCode.getLocalIndex(id);
                     }

                     if (localIndex != -1) {
                        this._ops[i] = (byte)(op + 3);
                        this._operands[i] = this._operands[i] & 0xFF000000;
                        this._operands[i] = this._operands[i] | localIndex;
                     }
                  }
               }
            default:
               i++;
         }
      }
   }

   public static boolean requiresScope(int op) {
      switch (op) {
         case -120:
         case 5:
         case 20:
         case 21:
         case 32:
         case 36:
         case 41:
         case 78:
         case 91:
         case 93:
         case 110:
         case 112:
         case 116:
         case 117:
            return true;
         default:
            return false;
      }
   }

   void postOptimize(Function f) {
      for (int i = 0; i < this._ops.length; i++) {
         if (requiresScope(this._ops[i])) {
            f.setNeedsScope();
            return;
         }
      }
   }

   boolean isEmpty() {
      int length = this._ops.length;
      if (length == 0) {
         return true;
      }

      for (int i = 0; i < length; i++) {
         if (this._ops[i] != -117) {
            return false;
         }
      }

      return true;
   }

   static void dumpOneOp(Function f, Compiler c, int op, int operand, int indent) {
      c.print(OpcodeStrings.get(op));
      switch (OperandTypes.get(op)) {
         case -1:
         case 16:
         case 19:
            break;
         case 0:
         case 25:
            int var10 = operand >> 24;
            operand &= 16777215;
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(c.getGlobalCode().getLocal(operand));
            c.print(",");
            c.print(Integer.toString(var10));
            break;
         case 1:
         case 24:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(c.getGlobalCode().getLocal(operand));
            break;
         case 2:
         case 12:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(f.getLocal(operand));
            break;
         case 3:
         case 18:
            c.print(" parms=");
            c.print(Integer.toString(operand));
            break;
         case 4:
         case 10:
            c.print(" ");
            c.print(Integer.toString(operand));
            break;
         case 5:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(f.getFunctionId(operand));
            break;
         case 6:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(f.getDouble(operand));
            break;
         case 7:
         case 20:
            c.print(" ");
            c.print4(operand);
            c.print(" \"");
            c.print(f.getString(operand));
            c.print("\"");
            break;
         case 8:
         case 15: {
            int op2 = operand >> 24;
            operand &= 16777215;
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(f.getLocal(operand));
            c.print(",");
            c.print(Integer.toString(op2));
            break;
         }
         case 13:
         case 22:
            c.print(" L");
            c.print(Integer.toString(operand));
            break;
         case 14:
         default:
            c.print(" L");
            c.print(Integer.toString((char)operand));
            operand = (char)(operand >> 16);
         case 9:
         case 11:
            c.print(" ");
            c.print4(operand);
            c.print(" T");
            c.print4(operand);
            break;
         case 17:
         case 23:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(f.getId(operand));
            break;
         case 21:
            c.print(" ");
            c.print(Integer.toString(f.getInteger(operand)));
            break;
         case 26:
            c.print(" ");
            c.print(Integer.toString((char)operand));
            c.print(" tok:");
            c.print(Integer.toString((char)(operand >> 16)));
            break;
         case 27: {
            int op2 = operand >> 24;
            operand &= 16777215;
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(f.getId(operand));
            c.print(",");
            c.print(Integer.toString(op2));
         }
      }

      c.println("", indent);
   }

   static void dumpOneOp(CompiledScript code, int offset, Compiler c, int op, int operand, int indent) {
      c.print(OpcodeStrings.get(op));
      switch (OperandTypes.get(op)) {
         case -1:
         case 16:
         case 19:
            break;
         case 0:
         case 25:
            int var11 = operand >> 24;
            operand &= 16777215;
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(c.getGlobalCompiledCode().getLocalName(operand));
            c.print(",");
            c.print(Integer.toString(var11));
            break;
         case 1:
         case 24:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(c.getGlobalCompiledCode().getLocalName(operand));
            break;
         case 2:
         case 12:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(code.getLocalName(operand));
            break;
         case 3:
         case 18:
            c.print(" parms=");
            c.print(Integer.toString(operand));
            break;
         case 4:
         case 10:
            c.print(" ");
            c.print(Integer.toString(operand));
            break;
         case 5:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(code.getFunctionExpression(operand).getId());
            break;
         case 6:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(Double.toString(code.getDouble(operand)));
            break;
         case 7:
         case 20:
            c.print(" ");
            c.print4(operand);
            c.print(" \"");
            c.print(code.getString(operand));
            c.print("\"");
            break;
         case 8:
         case 15: {
            int op2 = operand >> 24;
            operand &= 16777215;
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(code.getLocalName(operand));
            c.print(",");
            c.print(Integer.toString(op2));
            break;
         }
         case 13:
            c.print(" >>");
            c.print4(operand);
            break;
         case 14:
         default:
            c.print(" >>");
            c.print4((char)operand);
            operand = (char)(operand >> 16);
         case 9:
         case 11:
            c.print(" ");
            c.print4(operand);
            c.print(" T");
            c.print4(operand);
            break;
         case 17:
         case 23:
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(code.getId(operand));
            break;
         case 21:
            c.print(" ");
            c.print(Integer.toString(code.getInteger(operand)));
            break;
         case 22:
            c.print(" >>");
            c.print4(operand + offset + 1);
            break;
         case 26:
            c.print(" ");
            c.print(Integer.toString((char)operand));
            c.print(" tok:");
            c.print(Integer.toString((char)(operand >> 16)));
            break;
         case 27: {
            int op2 = operand >> 24;
            operand &= 16777215;
            c.print(" ");
            c.print4(operand);
            c.print(" ");
            c.print(code.getId(operand));
            c.print(",");
            c.print(Integer.toString(op2));
         }
      }

      c.println("", indent);
   }

   byte lastOp() {
      return this._ops.length == 0 ? -111 : this._ops[this._ops.length - 1];
   }

   void dump(Function f, Compiler c, int indent) {
      c.println("", indent);

      for (int i = 0; i < this._ops.length; i++) {
         dumpOneOp(f, c, this._ops[i], this._operands[i], indent);
      }
   }

   private static void dumpSource(Compiler c, CompiledScript code, int offset, int indent) {
      String src = Tokenizer.getSource(code, offset);
      if (src.length() != 0) {
         c.println("", 0);
         c.println(src, indent);
      }
   }

   static void dump(CompiledScript code, Compiler c, int indent) {
      c.println("", indent);
      int i = 0;

      while (i < code.getCodeLength()) {
         int offset = i;
         if (code.hasTokens()) {
            dumpSource(c, code, i, indent);
         }

         int op = code.getCode(i++);
         int operand = 0;
         switch (OperandTypes.get(op)) {
            case -1:
            case 16:
            case 19:
               break;
            case 0:
            case 15:
               operand = code.getCode(i++) & 255;
               operand |= code.getCode(i++) << 24;
               break;
            case 1:
            case 2:
            case 3:
            case 7:
            case 10:
            case 11:
            case 17:
            case 21:
               operand = code.getCode(i++) & 255;
               break;
            case 4:
            case 14:
               operand = code.getCode(i++) & 255;
               operand <<= 8;
               operand += code.getCode(i++) & 255;
               operand <<= 8;
               operand += code.getCode(i++) & 255;
               operand <<= 8;
               operand += code.getCode(i++) & 255;
               break;
            case 5:
            case 6:
            case 9:
            case 12:
            case 13:
            case 18:
            case 20:
            case 23:
            case 24:
               operand = code.getCode(i++) & 255;
               operand <<= 8;
               operand += code.getCode(i++) & 255;
               break;
            case 8:
            case 25:
            case 27:
               operand = code.getCode(i++) & 255;
               operand <<= 8;
               operand += code.getCode(i++) & 255;
               operand |= code.getCode(i++) << 24;
               break;
            case 22:
            default:
               operand = code.getCode(i++);
               break;
            case 26:
               operand = code.getCode(i++) & 255;
               operand <<= 8;
               operand += code.getCode(i++) & 255;
         }

         c.print4(offset);
         c.print(": ");
         dumpOneOp(code, offset, c, op, operand, indent);
      }
   }

   private static void dumpCompiledCodeName(Compiler c, CompiledScript code) {
      c.print(code.getId());
   }

   static void dumpCompiledCode(Compiler c, CompiledScript code, int indent) {
      c.print("Function: ");
      dumpCompiledCodeName(c, code);
      c.print(" stack=");
      c.print(Integer.toString(code.getMaxStack()));
      c.print(" temps=");
      c.print(Integer.toString(code.getNumTemps()));
      c.print(" lastRealLocal=");
      c.print(Integer.toString(code.getLastRealLocal()));
      c.print(" needsScope=" + code.getNeedsScope());
      c.println(" sensible=" + code.getIsSensible(), indent + 4);
      indent += 4;
      if (code.getNumLocals() != 0) {
         c.println("Locals: ", indent + 4);

         for (int i = 0; i < code.getNumLocals(); i++) {
            c.println(code.getLocalName(i), indent + 4);
         }
      }

      if (code.getNumParms() != 0) {
         c.println("", indent);
         c.print("Parms: ");
         c.println(Integer.toString(code.getNumParms()), indent + 4);
         if (code.hasDuplicateParms()) {
            for (int i = 0; i < code.getNumParms(); i++) {
               c.print4(i);
               c.print(": ");
               c.println(code.getParmLocation(i), indent + 4);
            }
         }
      }

      if (code.getNumFunctionDeclarations() != 0) {
         c.println("", indent);
         c.println("Function Declarations: ", indent + 4);

         for (int i = 0; i < code.getNumFunctionDeclarations(); i++) {
            dumpCompiledCodeName(c, code.getFunctionDeclaration(i));
            c.println("", indent + 4);
         }
      }

      c.println("", indent);
      dump(code, c, indent);
      if (code.getNumFunctionDeclarations() != 0) {
         c.println("Function Declarations: ", indent + 4);

         for (int i = 0; i < code.getNumFunctionDeclarations(); i++) {
            c.print4(i);
            c.print(":");
            dumpCompiledCode(c, code.getFunctionDeclaration(i), indent + 4);
            c.println("", indent + 4);
         }
      }

      if (code.getNumFunctionExpressions() != 0) {
         c.println("Function Expressions: ", indent + 4);

         for (int i = 0; i < code.getNumFunctionExpressions(); i++) {
            c.print4(i);
            c.print(":");
            dumpCompiledCode(c, code.getFunctionExpression(i), indent + 4);
            c.println("", indent + 4);
         }
      }

      c.println("", indent + 4);
      c.println("Token Start: " + Integer.toString(code.getTokenStart()), indent + 4);
      c.println("Token End: " + Integer.toString(code.getTokenEnd()), indent + 4);
      c.println("", indent + 4);
      if (code.hasLineNumbers()) {
         c.println("Line Number Table: ", indent + 4);

         for (int i = 0; i < code.getNumLines(); i++) {
            c.print4(code.getLineOffset(i));
            c.print(":");
            c.print(Integer.toString(code.getLineNumber(i)));
            if (code.hasTokens()) {
               c.print(":(");
               c.print(Integer.toString(code.getLineTokenOffset(i)));
               c.print(")");
            }

            c.println("", indent + 4);
         }
      }
   }
}
