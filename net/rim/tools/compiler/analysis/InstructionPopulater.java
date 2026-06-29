package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.Code;
import net.rim.tools.compiler.codfile.CodfileData;
import net.rim.tools.compiler.codfile.DataBytes;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.FieldDef;
import net.rim.tools.compiler.codfile.InterfaceMethodRef;
import net.rim.tools.compiler.codfile.Literal;
import net.rim.tools.compiler.codfile.Member;
import net.rim.tools.compiler.codfile.Routine;
import net.rim.tools.compiler.codfile.RoutineLocal;
import net.rim.tools.compiler.types.ArrayType;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.Field;
import net.rim.tools.compiler.types.Method;
import net.rim.tools.compiler.types.Type;
import net.rim.tools.compiler.types.TypeModule;

public final class InstructionPopulater extends InstructionWalker {
   private Compiler _compiler;
   private TypeModule _typeModule;
   private Method _method;
   private Code _code;

   InstructionPopulater() {
   }

   final void init(Compiler compiler, Method method, TypeModule typeModule) {
      this._compiler = compiler;
      this._typeModule = typeModule;
      this._method = method;
      this._code = ((RoutineLocal)method.getMember(compiler, typeModule)).getCode();
   }

   final void fini() {
      this._compiler = null;
      this._typeModule = null;
      this._method = null;
      this._code = null;
   }

   @Override
   public final void walkInstruction(InstructionTarget ins) {
      ins.setLabel(this._code.plantLabel());
   }

   @Override
   public final void walkInstruction(Instruction ins) {
      int opcode = ins.getOpcode();
      switch (opcode) {
         case -1:
         case 1:
         case 2:
         case 3:
         case 4:
         case 5:
         case 6:
         case 7:
         case 8:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 15:
         case 17:
         case 19:
         case 25:
         case 26:
         case 28:
         case 29:
         case 39:
         case 40:
         case 41:
         case 42:
         case 43:
         case 45:
         case 46:
         case 47:
         case 48:
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
         case 105:
         case 106:
         case 107:
         case 108:
         case 109:
         case 110:
         case 111:
         case 112:
         case 120:
         case 121:
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
         case 163:
         case 164:
         case 166:
         case 168:
         case 169:
         case 170:
         case 171:
         case 184:
         case 185:
         case 186:
         case 187:
         case 189:
         case 190:
         case 191:
         case 192:
         case 193:
         case 194:
         case 195:
         case 196:
         case 197:
         case 198:
         case 199:
         case 200:
         case 201:
         case 214:
         case 219:
         case 220:
         case 222:
         case 223:
         case 224:
         case 225:
         case 226:
         case 227:
         case 228:
         case 229:
         case 230:
         case 231:
         case 232:
         case 233:
         case 234:
         case 235:
         case 236:
         case 237:
         case 238:
         case 239:
         case 240:
         case 241:
         case 242:
         case 243:
         case 244:
         case 245:
         case 246:
         case 247:
         case 248:
         case 249:
         case 251:
         case 252:
         case 253:
         case 254:
         case 255:
            this.unexpectedInstruction(ins);
            return;
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
         default:
            this._code.addOpcode(opcode);
            return;
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
            this._code.addOpcode(opcode, ins.getOp());
      }
   }

   @Override
   public final void walkInstruction(InstructionBranch ins) {
      Object label = null;
      int opcode = ins.getOpcode();
      switch (opcode) {
         case 144:
            this.unexpectedInstruction(ins);
            return;
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
            label = ins.getBranchTarget(0);
            this._code.addOpcode(opcode, label);
      }
   }

   @Override
   public final void walkInstruction(InstructionInts ins) {
      int opcode = ins.getOpcode();
      switch (opcode) {
         case 15:
         case 17:
            this._code.addOpcode(opcode, ins.getInts());
            return;
         case 47:
         case 163:
         case 164:
            int num = ins.getNumBranchTargets();
            Object[] labels = new Object[num];

            for (int i = 0; i < num; i++) {
               labels[i] = ins.getBranchTarget(i);
            }

            this._code.addOpcode(opcode, ins.getInts(), labels, ins.getVerifyError());
            return;
         default:
            this.unexpectedInstruction(ins);
      }
   }

   @Override
   public final void walkInstruction(InstructionStringArray ins) {
      int opcode = ins.getOpcode();
      switch (opcode) {
         case 282:
            DataBytes dataBytes = this._typeModule.getDataSection().getDataBytes();
            String[] sa = ins.getStringArray();
            Literal[] la = new Literal[sa.length];

            for (int i = 0; i < sa.length; i++) {
               String s = sa[i];
               la[i] = dataBytes.getLiteral(s, InstructionResolver.isUnicode(s), true);
            }

            this._code.addOpcode(opcode, la);
            return;
         default:
            this.unexpectedInstruction(ins);
      }
   }

   @Override
   public final void walkInstruction(InstructionBytes ins) {
      DataBytes dataBytes = this._typeModule.getDataSection().getDataBytes();
      int opcode = ins.getOpcode();
      int operand = ins.getOp();
      CodfileData bytes = null;
      switch (opcode) {
         case 45:
            bytes = dataBytes.getBytes(ins.getBytes(), operand, true);
            break;
         default:
            this.unexpectedInstruction(ins);
      }

      this._code.addOpcode(opcode, operand, bytes);
   }

   @Override
   public final void walkInstruction(InstructionLong ins) {
      int opcode = ins.getOpcode();
      switch (opcode) {
         case 39:
            this._code.addOpcode(opcode, ins.getOp2());
            return;
         case 120:
         case 121:
            this._code.addOpcode(opcode, ins.getOp(), (int)ins.getOp2());
            return;
         default:
            this.unexpectedInstruction(ins);
      }
   }

   @Override
   public final void walkInstruction(InstructionNameAndType ins) {
      boolean check_lib = false;
      int opcode = ins.getOpcode();
      switch (opcode) {
         case 1:
            Method methodx = (Method)ins.getNameAndType();
            switch (methodx.getSpecial()) {
               case 0:
                  ClassDef classDef = ins.getClassType().getClassDef(this._typeModule);
                  Routine routine = (Routine)methodx.getMember(this._compiler, this._typeModule);
                  int modCode = opcode;
                  boolean needsFixup = true;
                  if (modCode == opcode && !methodx.isVirtualCall(ins.getClassType())) {
                     int var21 = 3;
                     modCode = var21 + methodx.getClassType().getClassDef(this._typeModule).getLibOff();
                     needsFixup = false;
                  }

                  if (modCode != opcode) {
                     opcode = modCode;
                  }

                  if (needsFixup) {
                     DataSection dataSection = this._typeModule.getDataSection();
                     classDef.getClassRef(dataSection);
                     routine.makeSymbolic(dataSection, false);
                  }

                  this._code.addOpcode(opcode, routine, needsFixup);
                  return;
               case 1:
                  this._code.addOpcode(217);
                  return;
               case 2:
               default:
                  this._code.addOpcode(218);
                  return;
            }
         case 2: {
            Method method = (Method)ins.getNameAndType();
            InterfaceMethodRef methodRef = method.getInterfaceMethodRef(this._compiler, this._typeModule);
            int index = method.getIcallIndex(this._typeModule, this._method);
            this._code.addOpcode(opcode, ins.getOp(), index, methodRef);
            return;
         }
         case 3:
         case 5:
         case 12:
            check_lib = true;
         case 4:
         case 6:
         case 9:
         case 10:
         case 11:
         case 13: {
            Method method = (Method)ins.getNameAndType();
            ClassDef classDef = ins.getClassType().getClassDef(this._typeModule);
            if (check_lib) {
               opcode += method.getClassType().getClassDef(this._typeModule).getLibOff();
            }

            Routine routine = (Routine)method.getMember(this._compiler, this._typeModule);
            this._code.addOpcode(opcode, routine, false);
            return;
         }
         case 7:
         case 105:
         case 107:
         case 109:
         case 111:
            check_lib = true;
         case 8:
         case 106:
         case 108:
         case 110:
         case 112:
            ClassDef classDef = ins.getClassType().getClassDef(this._typeModule);
            Member member = ins.getNameAndType().getMember(this._compiler, this._typeModule);
            if (check_lib) {
               opcode += ins.getNameAndType().getClassType().getClassDef(this._typeModule).getLibOff();
            }

            this._code.addOpcode(opcode, classDef, member);
            return;
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
            Field field = (Field)ins.getNameAndType();
            FieldDef fieldDef = (FieldDef)field.getMember(this._compiler, this._typeModule);
            boolean needsFixup = true;
            if (needsFixup) {
               fieldDef.makeSymbolic(this._typeModule.getDataSection());
            }

            this._code.addOpcode(opcode, fieldDef, needsFixup);
            return;
         default:
            this.unexpectedInstruction(ins);
      }
   }

   @Override
   public final void walkInstruction(InstructionString ins) {
      DataBytes dataBytes = this._typeModule.getDataSection().getDataBytes();
      int opcode = ins.getOpcode();
      Literal literal = null;
      switch (opcode) {
         case 40:
         case 42:
            String str = ins.getString();
            if (str.length() == 0) {
               this._code.addOpcode(223);
               return;
            }

            literal = dataBytes.getLiteral(ins.getString(), opcode == 42, true);
            this._code.addOpcode(opcode, literal);
            return;
         default:
            this.unexpectedInstruction(ins);
      }
   }

   @Override
   public final void walkInstruction(InstructionType ins) {
      boolean check_lib = false;
      Object label = null;
      Type type = ins.getType();
      int opcode = ins.getOpcode();
      switch (opcode) {
         case 166:
            ArrayType var17 = (ArrayType)type;
            this._code.addOpcode(opcode, ins.getOp(), var17.getNesting(), var17.getMostBaseType().getTypeId());
            return;
         case 168:
            check_lib = true;
         case 169:
            ArrayType arrayType = (ArrayType)type;
            ClassType var12 = (ClassType)arrayType.getMostBaseType();
            ClassDef var21 = var12.getClassDef(this._typeModule);
            if (check_lib) {
               opcode += var21.getLibOff();
            }

            this._code.addOpcode(opcode, var21, 0);
            return;
         case 170:
            check_lib = true;
         case 171:
            ArrayType var15 = (ArrayType)type;
            ClassType var11 = (ClassType)var15.getMostBaseType();
            ClassDef var20 = var11.getClassDef(this._typeModule);
            if (check_lib) {
               opcode += var20.getLibOff();
            }

            this._code.addOpcode(opcode, ins.getOp(), var15.getNesting(), var20);
            return;
         case 184:
         case 186:
         case 191:
         case 193:
            check_lib = true;
         case 19:
         case 185:
         case 187:
         case 192:
         case 194:
            ClassType classType = (ClassType)type;
            ClassDef classDef = classType.getClassDef(this._typeModule);
            if (check_lib) {
               opcode += classDef.getLibOff();
            }

            this._code.addOpcode(opcode, classDef, 0);
            return;
         case 189:
         case 190:
            ArrayType var14 = (ArrayType)type;
            this._code.addOpcode(opcode, var14.getNesting(), var14.getMostBaseType().getTypeId());
            return;
         case 195:
            check_lib = true;
         case 196:
            ClassType var9 = (ClassType)type;
            ClassDef var18 = var9.getClassDef(this._typeModule);
            label = ins.getBranchTarget(0);
            if (check_lib) {
               opcode += var18.getLibOff();
            }

            this._code.addOpcode(opcode, var18, label);
            return;
         case 197:
            ArrayType var13 = (ArrayType)type;
            label = ins.getBranchTarget(0);
            this._code.addOpcode(opcode, var13.getNesting(), var13.getMostBaseType().getTypeId(), label);
            return;
         case 198:
         case 200:
            check_lib = true;
         case 199:
         case 201:
            ArrayType arrayType = (ArrayType)type;
            ClassType classType = (ClassType)arrayType.getMostBaseType();
            ClassDef classDef = classType.getClassDef(this._typeModule);
            if (check_lib) {
               opcode += classDef.getLibOff();
            }

            this._code.addOpcode(opcode, classDef, arrayType.getNesting());
            return;
         default:
            this.unexpectedInstruction(ins);
      }
   }
}
