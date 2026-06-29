package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.analysis.InstructionTarget;
import net.rim.tools.compiler.exec.MyArrays;
import net.rim.tools.compiler.io.StructuredOutputStream;
import net.rim.tools.compiler.util.CompilerProperties;
import net.rim.tools.compiler.vm.Constants;
import net.rim.tools.compiler.vm.OpcodeSizes;

public final class Code extends CodfileItem implements Constants {
   private int _numOpcodes;
   private byte[] _opcodes;
   private int[] _operands;
   private short[] _referenceIndex;
   private CodfileLabel[] _labels;
   private short _numReferences;
   private Object[] _references;

   private final int getTarget(CodfileLabel label, StructuredOutputStream out) {
      int target = super._offset + label.getOffset() - out.getOffset();
      if (target >= -32768 && target < 32768) {
         return target;
      } else {
         throw new Object("branch target out of range");
      }
   }

   private final void writeAsTableswitch(StructuredOutputStream out, int[] ops, CodfileLabel[] labels, int oprand) {
      out.writeByte(47);
      if (oprand != 0) {
         oprand = -1;
      } else {
         oprand = numTableEntries(ops);
      }

      out.writeShort(oprand);
      int low = ops[0] - 1;
      out.writeInt(low);
      int numCases = ops.length;
      low--;

      for (int j = 0; j < numCases; j++) {
         int diff = ops[j] - low;

         while (--diff > 0) {
            int target = this.getTarget(labels[0], out);
            out.writeShort(target);
         }

         int target = this.getTarget(labels[j + 1], out);
         out.writeShort(target);
         low = ops[j];
      }
   }

   private final void writeAsLookupswitch(StructuredOutputStream out, boolean canBeShort, int[] ops, CodfileLabel[] labels, int oprand) {
      int opcode = canBeShort ? 163 : 164;
      out.writeByte(opcode);
      int numCases = ops.length;
      if (oprand != 0) {
         oprand = -1;
      } else {
         oprand = numCases;
      }

      out.writeShort(oprand);

      for (int j = 0; j < numCases; j++) {
         if (canBeShort) {
            out.writeShort(ops[j]);
         } else {
            out.writeInt(ops[j]);
         }

         int target = this.getTarget(labels[j + 1], out);
         out.writeShort(target);
      }

      int target = this.getTarget(labels[0], out);
      out.writeShort(target);
   }

   @Override
   public final void write(StructuredOutputStream out) {
      byte[] opcodes = this._opcodes;
      int[] operands = this._operands;
      this.setOffset(out);
      int num = this._numOpcodes;
      int op01xx = 0;

      label104:
      for (int i = 0; i < num; i++) {
         int opcode = (opcodes[i] & 255) + op01xx;
         op01xx = 0;
         boolean canBeShort = false;
         switch (opcode) {
            case 162:
               out.writeByte(opcode);
               switch (opcode) {
                  case 1:
                     Routine var36 = (Routine)this.getReference(i, 0);
                     var36.writeMemberAddress(out, operands[i] != 0);
                     out.writeByte(var36.getLocalCount());
                     continue;
                  case 2:
                     InterfaceMethodRef methodRef = (CompilerProperties)this.getReference(i, 0);
                     methodRef.writeOffset(out);
                     out.writeByte(this.getOperandShort1(i));
                     out.writeShort(this.getOperandShort2(i));
                     continue;
                  case 3:
                  case 4:
                  case 5:
                  case 6:
                     Routine var35 = (Routine)this.getReference(i, 0);
                     var35.writeOffset(out);
                     out.writeByte(var35.getLocalCount());
                     continue;
                  case 7:
                  case 8:
                  case 105:
                  case 106:
                  case 107:
                  case 108:
                  case 109:
                  case 110:
                  case 111:
                  case 112:
                     ClassDef classDef = (ClassDef)this.getReference(i, 0);
                     Member member = (Member)this.getReference(i, 1);
                     member.writeStaticOffset(out, classDef);
                     continue;
                  case 9:
                  case 10:
                  case 11:
                     Routine routine = (Routine)this.getReference(i, 0);
                     out.writeByte(routine.getLocalCount());
                     ((RoutineLocal)routine).writeNativeInvoke(out);
                     continue;
                  case 12:
                  case 13:
                     Routine var33 = (Routine)this.getReference(i, 0);
                     var33.writeOffset(out);
                     continue;
                  case 15:
                  case 17:
                     out.writeByte(this.getOperandByte1(i));
                     out.writeByte(this.getOperandByte2(i));
                     out.writeByte(this.getOperandByte3(i));
                     continue;
                  case 19:
                  case 168:
                  case 184:
                  case 186:
                  case 191:
                  case 193:
                     ClassDef var30 = (ClassDef)this.getReference(i, 0);
                     var30.writeOrdinal(out);
                     continue;
                  case 21:
                  case 36:
                     out.writeByte(operands[i]);
                     continue;
                  case 22:
                  case 37:
                  case 50:
                  case 52:
                  case 54:
                  case 72:
                  case 74:
                  case 76:
                     out.writeShort(operands[i]);
                     continue;
                  case 23:
                  case 38:
                     out.writeInt(operands[i]);
                     continue;
                  case 25:
                  case 26:
                  case 28:
                  case 29:
                  case 93:
                  case 94:
                  case 95:
                  case 96:
                  case 97:
                  case 98:
                  case 99:
                  case 100:
                  case 101:
                  case 102:
                  case 103:
                  case 104:
                     ((Member)this.getReference(i, 0)).writeMemberAddress(out, operands[i] != 0);
                     continue;
                  case 39:
                     Long l = (Long)this.getReference(i, 0);
                     out.writeLong(l);
                     continue;
                  case 40: {
                     Literal literal = (Literal)this.getReference(i, 0);
                     literal.writeOffset(out);
                     continue;
                  }
                  case 42: {
                     Literal literal = (Literal)this.getReference(i, 0);
                     out.writeShort(literal.length());
                     literal.writeOffset(out);
                     continue;
                  }
                  case 45:
                     CodfileData codfileData = (CodfileData)this.getReference(i, 0);
                     out.writeByte(operands[i]);
                     out.writeShort(codfileData.length());
                     codfileData.writeOffset(out);
                     continue;
                  case 47:
                     int[] ops = (int[])this.getReference(i, 0);
                     CodfileLabel[] labels = (CodfileLabel[])this.getReference(i, 1);
                     int numCases = labels.length;
                     if (operands[i] != 0) {
                        numCases = -1;
                     }

                     out.writeShort(numCases);
                     out.writeInt(ops[1]);
                     numCases = labels.length;
                     int j = 0;

                     while (true) {
                        if (j >= numCases) {
                           continue label104;
                        }

                        int var47 = this.getTarget(labels[j], out);
                        out.writeShort(var47);
                        j++;
                     }
                  case 49:
                  case 51:
                  case 53:
                  case 71:
                  case 73:
                  case 75:
                     out.writeByte(operands[i]);
                     continue;
                  case 120:
                     out.writeByte(this.getOperandShort1(i));
                     out.writeByte(this.getOperandShort2(i));
                     continue;
                  case 121:
                     out.writeShort(this.getOperandShort1(i));
                     out.writeShort(this.getOperandShort2(i));
                     continue;
                  case 145:
                  case 146:
                  case 147:
                  case 148:
                  case 149:
                  case 150:
                  case 151:
                  case 152:
                  case 153:
                  case 154:
                  case 155:
                  case 156:
                  case 157:
                  case 158:
                  case 159:
                  case 160:
                     CodfileLabel var22 = (CodfileLabel)this.getReference(i, 0);
                     if (operands[i] == 0) {
                        int targetx = this.getTarget(var22, out);
                        out.writeByte(targetx);
                     } else {
                        int targetx = 4;
                        out.writeByte(targetx);
                        int around = 162;
                        out.writeByte(around);
                        targetx = this.getTarget(var22, out);
                        out.writeShort(targetx);
                     }
                     continue;
                  case 161:
                     CodfileLabel var21 = (CodfileLabel)this.getReference(i, 0);
                     int var43 = this.getTarget(var21, out);
                     out.writeByte(var43);
                     continue;
                  case 162:
                     CodfileLabel var20 = (CodfileLabel)this.getReference(i, 0);
                     int var42 = this.getTarget(var20, out);
                     out.writeShort(var42);
                     continue;
                  case 165:
                     out.writeByte(operands[i]);
                     continue;
                  case 166:
                     out.writeByte(this.getOperandByte1(i));
                     out.writeByte(this.getOperandByte2(i));
                     out.writeByte(this.getOperandByte3(i));
                     continue;
                  case 169:
                  case 185:
                  case 187:
                  case 192:
                  case 194:
                     ClassDef var29 = (ClassDef)this.getReference(i, 0);
                     var29.writeAbsoluteClassDef(out);
                     continue;
                  case 170:
                     ClassDef var28 = (ClassDef)this.getReference(i, 0);
                     var28.writeOrdinal(out);
                     out.writeByte(this.getOperandShort1(i));
                     out.writeByte(this.getOperandShort2(i));
                     continue;
                  case 171:
                     ClassDef var27 = (ClassDef)this.getReference(i, 0);
                     var27.writeAbsoluteClassDef(out);
                     out.writeByte(this.getOperandShort1(i));
                     out.writeByte(this.getOperandShort2(i));
                     continue;
                  case 189:
                  case 190:
                     out.writeByte(this.getOperandShort1(i));
                     out.writeByte(this.getOperandShort2(i));
                     continue;
                  case 195:
                     ClassDef var26 = (ClassDef)this.getReference(i, 0);
                     CodfileLabel var19 = (CodfileLabel)this.getReference(i, 1);
                     var26.writeOrdinal(out);
                     int var41 = this.getTarget(var19, out);
                     out.writeShort(var41);
                     continue;
                  case 196:
                     ClassDef var25 = (ClassDef)this.getReference(i, 0);
                     CodfileLabel label = (CodfileLabel)this.getReference(i, 1);
                     var25.writeAbsoluteClassDef(out);
                     int target = this.getTarget(label, out);
                     out.writeShort(target);
                     continue;
                  case 197:
                     CodfileLabel var17 = (CodfileLabel)this.getReference(i, 0);
                     out.writeByte(this.getOperandShort1(i));
                     out.writeByte(this.getOperandShort2(i));
                     int var39 = this.getTarget(var17, out);
                     out.writeShort(var39);
                     continue;
                  case 198:
                  case 200:
                     ClassDef var24 = (ClassDef)this.getReference(i, 0);
                     var24.writeOrdinal(out);
                     out.writeByte(operands[i]);
                     continue;
                  case 199:
                  case 201:
                     ClassDef var23 = (ClassDef)this.getReference(i, 0);
                     var23.writeAbsoluteClassDef(out);
                     out.writeByte(operands[i]);
                     continue;
                  case 216:
                     op01xx = 256;
                     continue;
                  case 222:
                     Routine var32 = (Routine)this.getReference(i, 0);
                     int offset = var32.getVTableOffset(operands[i] != 0);
                     int parmAndOffset = var32.getAddress() << 2;
                     int parm = var32.getLocalCount();
                     parmAndOffset |= parm - 1;
                     out.writeByte(parmAndOffset);
                     continue;
                  case 282:
                     Literal[] la = (Literal[])this.getReference(i, 0);
                     out.writeShort(la.length);

                     for (int j = 0; j < la.length; j++) {
                        la[j].writeOffset(out);
                     }
                  default:
                     continue;
               }
            case 163:
            default:
               canBeShort = true;
            case 164:
               int[] ops = (int[])this.getReference(i, 0);
               CodfileLabel[] labels = (CodfileLabel[])this.getReference(i, 1);
               int caseSize = canBeShort ? 2 : 4;
               int cases = ops.length * (caseSize + 2);
               int lku_size = 5 + cases;
               cases = numTableEntries(ops);
               cases = 2 * cases;
               int tbl_size = 7 + cases;
               if (tbl_size <= lku_size && operands[i] == 0) {
                  this.writeAsTableswitch(out, ops, labels, operands[i]);
               } else {
                  this.writeAsLookupswitch(out, canBeShort, ops, labels, operands[i]);
               }
         }
      }

      this.setExtent(out);
   }

   public final void allocateOpcodes(int num) {
      this._opcodes = new byte[num];
      this._operands = new int[num];
      this._referenceIndex = new short[num];
   }

   public final void addOpcode(int opcode) {
      switch (opcode) {
         case 0:
         case 14:
         case 16:
         case 18:
         case 20:
         case 24:
         case 27:
         case 30:
         case 31:
         case 32:
         case 33:
         case 34:
         case 35:
         case 44:
         case 55:
         case 56:
         case 57:
         case 58:
         case 59:
         case 60:
         case 61:
         case 62:
         case 63:
         case 64:
         case 65:
         case 66:
         case 67:
         case 68:
         case 69:
         case 70:
         case 77:
         case 78:
         case 79:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         case 85:
         case 86:
         case 87:
         case 88:
         case 89:
         case 90:
         case 91:
         case 92:
         case 113:
         case 114:
         case 115:
         case 116:
         case 117:
         case 118:
         case 119:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 167:
         case 172:
         case 173:
         case 174:
         case 175:
         case 176:
         case 177:
         case 178:
         case 179:
         case 180:
         case 181:
         case 182:
         case 183:
         case 188:
         case 202:
         case 203:
         case 204:
         case 205:
         case 206:
         case 207:
         case 208:
         case 209:
         case 210:
         case 211:
         case 212:
         case 213:
         case 215:
         case 216:
         case 217:
         case 218:
         case 221:
         case 223:
         case 250:
         case 256:
         case 257:
         case 258:
         case 259:
         case 260:
         case 261:
         case 262:
         case 263:
         case 264:
         case 265:
         case 266:
         case 267:
         case 268:
         case 269:
         case 270:
         case 271:
         case 272:
         case 273:
         case 274:
         case 275:
         case 276:
         case 277:
         case 278:
         case 279:
         case 280:
         case 281:
            this._opcodes[this._numOpcodes] = (byte)opcode;
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, int operand) {
      switch (opcode) {
         case 21:
         case 22:
         case 23:
         case 36:
         case 37:
         case 38:
         case 49:
         case 50:
         case 51:
         case 52:
         case 53:
         case 54:
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 76:
         case 165:
            this._opcodes[this._numOpcodes] = (byte)opcode;
            this._operands[this._numOpcodes] = operand;
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, Literal literal) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 40:
         case 42:
            this._referenceIndex[this._numOpcodes] = this.addReference(literal);
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, Literal[] la) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      this._referenceIndex[this._numOpcodes] = this.addReference(la);
      this._numOpcodes++;
   }

   public final void addOpcode(int opcode, int op, CodfileData bytes) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      this._operands[this._numOpcodes] = op;
      this._referenceIndex[this._numOpcodes] = this.addReference(bytes);
      this._numOpcodes++;
   }

   public final void addOpcode(int opcode, int op1, int op2) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 120:
         case 121:
         case 189:
         case 190:
            this.setOperandShorts(op1, op2);
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, long op) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      this._referenceIndex[this._numOpcodes] = this.addReference(new Object(op));
      this._numOpcodes++;
   }

   public final void addOpcode(int opcode, ClassDef classDef, Member member) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 7:
         case 8:
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
            this._referenceIndex[this._numOpcodes] = this.addReference(classDef);
            this.addReference(member);
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, Member member, boolean nullRef) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 1:
         case 3:
         case 4:
         case 5:
         case 6:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 25:
         case 26:
         case 28:
         case 29:
         case 93:
         case 94:
         case 95:
         case 96:
         case 97:
         case 98:
         case 99:
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 222:
            this._referenceIndex[this._numOpcodes] = this.addReference(member);
            this._operands[this._numOpcodes] = nullRef ? 1 : 0;
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, ClassDef classDef, int operand) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 19:
         case 168:
         case 169:
         case 184:
         case 185:
         case 186:
         case 187:
         case 191:
         case 192:
         case 193:
         case 194:
         case 198:
         case 199:
         case 200:
         case 201:
            this._referenceIndex[this._numOpcodes] = this.addReference(classDef);
            this._operands[this._numOpcodes] = operand;
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, int numDims, int nesting, int typeId) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 166:
            this.setOperandBytes(numDims, nesting, typeId);
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, int op1, int op2, Object ref) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 2:
         case 170:
         case 171:
         case 197:
            this._referenceIndex[this._numOpcodes] = this.addReference(ref);
            this.setOperandShorts(op1, op2);
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, ClassDef classDef, Object label) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 195:
         case 196:
         default:
            this._referenceIndex[this._numOpcodes] = this.addReference(classDef);
            this.addReference(label);
         case 194:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, int[] ops) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 15:
         case 17:
            this.setOperandBytes(ops[0], ops[1], ops[2]);
         default:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, Object label) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         case 162:
         default:
            this._referenceIndex[this._numOpcodes] = this.addReference(label);
         case 144:
            this._numOpcodes++;
      }
   }

   public final void addOpcode(int opcode, int[] ops, Object[] labels, boolean verifyError) {
      this._opcodes[this._numOpcodes] = (byte)opcode;
      switch (opcode) {
         case 47:
         case 163:
         case 164:
            this._referenceIndex[this._numOpcodes] = this.addReference(ops);
            this.addReference(labels);
            this._operands[this._numOpcodes] = verifyError ? 1 : 0;
         default:
            this._numOpcodes++;
      }
   }

   public final boolean newStyleEnterOk() {
      if (this._numOpcodes > 0) {
         switch (this._opcodes[0]) {
            case 15:
            case 17:
               return false;
         }
      }

      return true;
   }

   public final void setNewStyleEnter() {
      if (this._numOpcodes > 0) {
         switch (this._opcodes[0]) {
            case 14:
            case 16:
               this._opcodes[0] = -35;
         }
      }
   }

   private final void setOperandShorts(int s1, int s2) {
      this._operands[this._numOpcodes] = s2 << 16 | s1 & 65535;
   }

   private final int getOperandShort1(int index) {
      return this._operands[index] & 65535;
   }

   private final int getOperandShort2(int index) {
      return this._operands[index] >> 16;
   }

   private final void setOperandBytes(int index, int b1, int b2, int b3) {
      this._operands[index] = b3 << 16 | (b2 & 0xFF) << 8 | b1 & 0xFF;
   }

   private final void setOperandBytes(int b1, int b2, int b3) {
      this.setOperandBytes(this._numOpcodes, b1, b2, b3);
   }

   private final int getOperandByte1(int index) {
      return this._operands[index] & 0xFF;
   }

   private final int getOperandByte2(int index) {
      return this._operands[index] >> 8 & 0xFF;
   }

   private final int getOperandByte3(int index) {
      return this._operands[index] >> 16;
   }

   private final short addReference(Object object) {
      short used = this._numReferences;
      if (this._references == null) {
         this._references = new Object[1];
      } else if (this._numReferences == this._references.length) {
         this._references = MyArrays.resize(this._references, this._references.length * 2);
      }

      this._references[this._numReferences++] = object;
      return used;
   }

   private final void setReference(int index, int relative, Object object) {
      this._references[this._referenceIndex[index] + relative] = object;
   }

   private final Object getReference(int index, int relative) {
      return this._references[this._referenceIndex[index] + relative];
   }

   public final CodfileLabel plantLabel() {
      int index = 0;
      if (this._labels == null) {
         this._labels = new CodfileLabel[1];
      } else {
         index = this._labels.length;
         this._labels = MyArrays.resize(this._labels, this._labels.length + 1);
      }

      CodfileLabel label = new CodfileLabel(this._numOpcodes, -1);
      this._labels[index] = label;
      return label;
   }

   public static final int getOpcodeSize(int opcode, int operand, int[] ops, boolean assumeFar, boolean siblingFormat) {
      int size = 0;
      int caseSize = 4;
      int cases = 0;
      switch (opcode) {
         case 47:
            if (ops != null) {
               cases = ops[0] * 2;
            }

            size += 7 + cases;
            break;
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
            size += OpcodeSizes.sizes[opcode];
            if (operand != 0 || assumeFar) {
               size += 3;
            }
            break;
         case 161:
            size += OpcodeSizes.sizes[opcode];
            if (assumeFar) {
               size++;
            }
            break;
         case 163:
            caseSize = 2;
         case 164:
            if (ops != null) {
               cases = ops.length * (caseSize + 2);
            }

            size = 5 + cases;
            cases = numTableEntries(ops);
            int alt_size = 7 + cases * 2;
            if (siblingFormat && alt_size < size) {
               size = alt_size;
            }
            break;
         default:
            size += OpcodeSizes.sizes[opcode];
      }

      return size;
   }

   private static final int numTableEntries(int[] ops) {
      if (ops == null) {
         return 0;
      }

      int cases = ops.length;
      int num = cases + 1;
      int kill = num * 8;

      for (int j = 1; j < cases; j++) {
         if (ops[j] <= ops[j - 1]) {
            return kill;
         }

         int diff = ops[j] - ops[j - 1] - 1;
         if (diff < 0) {
            return kill;
         }

         if (diff > kill) {
            return kill;
         }

         num += diff;
      }

      return num;
   }

   public static final int getStringArrayInitSize(int opcode, int numOperands) {
      return 3 + 2 * numOperands;
   }

   private final int getOpcodeSize(int index) {
      int op01xx = 0;
      if (index > 0 && (this._opcodes[index - 1] & 255) == 216) {
         op01xx = 256;
      }

      int[] ops = null;
      int opcode = (this._opcodes[index] & 255) + op01xx;
      switch (opcode) {
         case 47:
         case 163:
         case 164:
            ops = (int[])this.getReference(index, 0);
         default:
            return getOpcodeSize(opcode, this._operands[index], ops, false, true);
         case 282:
            return getStringArrayInitSize(opcode, ((Literal[])this.getReference(index, 0)).length);
      }
   }

   private final int setOffsets(int offset, int start) {
      CodfileLabel label = null;
      int labelOrdinal = -1;
      int labelIndex = 0;
      if (this._labels != null && labelIndex < this._labels.length) {
         do {
            label = this._labels[labelIndex++];
            labelOrdinal = label.getOrdinal();
         } while (labelOrdinal < start && labelIndex < this._labels.length);
      }

      int num = this._numOpcodes;

      for (int i = start; i < num; i++) {
         if (label != null && labelOrdinal == i) {
            label.setOffset(offset);
            label = null;
            labelOrdinal = -1;
            if (labelIndex < this._labels.length) {
               label = this._labels[labelIndex++];
               labelOrdinal = label.getOrdinal();
            }
         }

         offset += this.getOpcodeSize(i);
      }

      while (label != null) {
         label.setOffset(offset);
         label = null;
         if (labelIndex < this._labels.length) {
            label = this._labels[labelIndex++];
         }
      }

      return offset;
   }

   private final int invertSense(int opcode) {
      switch (opcode) {
         case 144:
            return 0;
         case 145:
            return 148;
         case 146:
            return 149;
         case 147:
            return 150;
         case 148:
            return 145;
         case 149:
            return 146;
         case 150:
            return 147;
         case 151:
            return 157;
         case 152:
            return 158;
         case 153:
            return 155;
         case 154:
            return 156;
         case 155:
            return 153;
         case 156:
            return 154;
         case 157:
            return 151;
         case 158:
            return 152;
         case 159:
         default:
            return 160;
         case 160:
            return 159;
      }
   }

   public final int computeBranches() {
      int length;
      boolean changed;
      do {
         changed = false;
         int offset = 0;
         length = this.setOffsets(offset, 0);
         int num = this._numOpcodes;
         int op01xx = 0;

         for (int i = 0; i < num; i++) {
            int opcode = (this._opcodes[i] & 255) + op01xx;
            op01xx = 0;
            switch (opcode) {
               case 145:
               case 146:
               case 147:
               case 148:
               case 149:
               case 150:
               case 151:
               case 152:
               case 153:
               case 154:
               case 155:
               case 156:
               case 157:
               case 158:
               case 159:
               case 160:
                  if (this._operands[i] != 0) {
                     break;
                  }
               case 161:
                  CodfileLabel target = (CodfileLabel)this._references[this._referenceIndex[i]];
                  int delta = target.getOffset() - (offset + 1);
                  if (delta < -128 || delta >= 128) {
                     if (opcode == 161) {
                        this._opcodes[i] = -94;
                     } else {
                        this._opcodes[i] = (byte)this.invertSense(opcode);
                        this._operands[i] = 1;
                     }

                     length = this.setOffsets(offset, i);
                     changed = true;
                  }
                  break;
               case 216:
                  op01xx = 256;
            }

            offset += this.getOpcodeSize(i);
         }
      } while (changed);

      return length;
   }

   public final void resolveBranches() {
      int num = this._numOpcodes;
      int op01xx = 0;

      for (int i = 0; i < num; i++) {
         int opcode = (this._opcodes[i] & 255) + op01xx;
         op01xx = 0;
         switch (opcode) {
            case 47:
            case 163:
            case 164:
               Object[] targets = (Object[])this.getReference(i, 1);
               int count = targets.length;
               CodfileLabel[] labels = new CodfileLabel[count];

               for (int j = 0; j < count; j++) {
                  InstructionTarget var11 = (InstructionTarget)targets[j];
                  labels[j] = var11.getLabel();
               }

               this.setReference(i, 1, labels);
               break;
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 197: {
               InstructionTarget target = (InstructionTarget)this.getReference(i, 0);
               this.setReference(i, 0, target.getLabel());
               break;
            }
            case 195:
            case 196: {
               InstructionTarget target = (InstructionTarget)this.getReference(i, 1);
               this.setReference(i, 1, target.getLabel());
               break;
            }
            case 216:
               op01xx = 256;
         }
      }
   }
}
