package net.rim.tools.compiler.analysis;

import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.classfile.AttributeStackMap;
import net.rim.tools.compiler.classfile.AttributeStackMapTable;
import net.rim.tools.compiler.classfile.ByteCodeExceptionHandler;
import net.rim.tools.compiler.classfile.ByteCodeInstructions;
import net.rim.tools.compiler.classfile.ClassfileExceptionHandler;
import net.rim.tools.compiler.classfile.ConstantPool;
import net.rim.tools.compiler.classfile.JavaByteCodes;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.Code;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.ExceptionHandler;
import net.rim.tools.compiler.codfile.RoutineLocal;
import net.rim.tools.compiler.codfile.StackMap;
import net.rim.tools.compiler.codfile.TypeList;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.Method;
import net.rim.tools.compiler.types.Type;
import net.rim.tools.compiler.types.TypeModule;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public final class InstructionCode implements Constants {
   private Method _method;
   private boolean _badCode;
   private boolean _synthetic;
   private ConstantPool _constantPool;
   private ClassfileExceptionHandler[] _handlers;
   private AttributeStackMapTable _stackMaps;
   private int _maxStack;
   private int _maxLocals;
   private byte[] _bytes;
   private int _length;
   private int _codeWeight;
   private int _byteCodeWeight;
   private ByteCodeInstructions _blocks;
   private static final int PROLOG_IP = -8;
   private static JavaByteCodes _javaByteCodes = new JavaByteCodes();
   private static InstructionResolver _instructionResolver = new InstructionResolver();
   private static InstructionPopulater _instructionPopulater = new InstructionPopulater();

   public InstructionCode(Method method, int maxStack, int maxLocals, byte[] bytes, ConstantPool constantPool) {
      this._method = method;
      this._maxStack = maxStack;
      this._maxLocals = maxLocals;
      this._bytes = bytes;
      this._length = bytes == null ? 0 : bytes.length;
      this._constantPool = constantPool;
   }

   public final void setSynthetic() {
      this._synthetic = true;
   }

   public final void setBadCode(boolean badCode) {
      this._badCode = badCode;
   }

   public final int getMaxStack() {
      return this._maxStack;
   }

   public final int addLocal() {
      return this._maxLocals++;
   }

   public final void setHandlers(ClassfileExceptionHandler[] handlers) {
      this._handlers = handlers;
   }

   public final void setStackMaps(AttributeStackMapTable stackMaps) {
      this._stackMaps = stackMaps;
   }

   public final void setBlocks(ByteCodeInstructions blocks) {
      this._blocks = blocks;
   }

   private final void annotateBlocks(Compiler compiler) {
      if (this._stackMaps != null) {
         int num = this._stackMaps.getNumStackMaps();

         for (int i = 0; i < num; i++) {
            AttributeStackMap sme = this._stackMaps.getStackMap(i);
            int offset = sme.getCodeOffset();
            InstructionStackEntry entry = new InstructionStackEntry(sme.getTypes());
            this._blocks.addStackEntry(offset, entry);
         }

         this._stackMaps = null;
      }
   }

   private final void populateBlocks(Compiler compiler) {
      if (this._handlers != null) {
         int num = this._handlers.length;
         this._blocks.allocateExceptionHandlers(num);

         for (int i = 0; i < num; i++) {
            ClassfileExceptionHandler h = this._handlers[i];
            ClassType classType = h.getClassType();
            if (classType != null) {
               classType.setReachable(compiler, true);
            }

            this._blocks.addExceptionHandler(i, h.getStart(), h.getEnd(), h.getHandler(), classType);
         }

         this._handlers = null;
      }
   }

   private final void prolog(Compiler compiler, ClassType classType) {
      ByteCodeInstructions block = this._blocks;
      int ip = -8;
      if (this._length > 0) {
         int parmLocalCount = this._method.getParmLocalCount();
         if (this._maxLocals > 255 || this._maxStack > 255 || parmLocalCount > 255) {
            int[] values = new int[]{this._maxLocals >> 8, parmLocalCount >> 8, this._maxStack >> 8};
            if (this._blocks.getNumExceptionHandlers() > 0) {
               block.addInstructionInts(ip++, 17, values, false);
            } else {
               block.addInstructionInts(ip++, 15, values, false);
            }
         } else if (this._blocks.getNumExceptionHandlers() > 0) {
            block.addInstruction(ip++, 16);
         } else {
            block.addInstruction(ip++, 14);
         }
      } else if (!this._method.is(1) && !this._badCode) {
         block.addInstruction(ip++, 250);
      }

      if (this._method.is(32768) && !this._method.is(1)) {
         if (this._method.is(2)) {
            block.addInstructionType(ip++, 19, classType);
         } else {
            block.addInstruction(ip++, 18);
         }
      }

      if (this._method.is(1048576)) {
         ClassType baseClassType = classType.getBaseClassType();
         if (baseClassType != null && baseClassType != compiler.getObjectClass()) {
            if (baseClassType.is(131072)) {
               block.addInstructionType(ip++, 187, baseClassType);
            } else {
               block.addInstructionType(ip++, 186, baseClassType);
            }
         }

         block.addInstructionType(ip++, 19, classType);
         block.addInstruction(ip++, 20);
      }

      if (this._method.is(1)) {
         int opcode = 10;
         if (this._method.hasReturnValue()) {
            int returnSize = this._method.getReturnType().getSize();
            if (returnSize == 8) {
               opcode = 11;
            } else {
               opcode = 9;
            }
         }

         block.addInstructionNameAndType(ip++, opcode, classType, this._method);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final void resolve(Compiler compiler) throws CompileException {
      if (this._synthetic) {
         this._blocks.mergeArray();
      } else {
         int optimization = compiler.getOptimization();
         if (this._method.is(1048576)) {
            optimization |= 4;
         }

         ClassType classType = this._method.getClassType();
         this._blocks = new ByteCodeInstructions(this._length);
         this.prolog(compiler, classType);

         CompileException ce;
         try {
            try {
               synchronized (_instructionResolver) {
                  _instructionResolver.init(compiler, classType, this._method, this._blocks, this._length);
                  this.annotateBlocks(compiler);
                  if (this._bytes != null) {
                     synchronized (_javaByteCodes) {
                        _javaByteCodes.walk(optimization, this._bytes, this._constantPool, _instructionResolver);
                        _javaByteCodes.fini();
                     }
                  }

                  this._constantPool = null;
                  this._bytes = null;
                  this.populateBlocks(compiler);
                  if (!this._method.is(33)) {
                     _instructionResolver.resolveBadLabel();
                  }

                  _instructionResolver.fini();
               }

               int num = this._blocks.getNumTargets();
               if (num > 0) {
                  InstructionTarget target = this._blocks.getTarget(0);
                  if (target.getIp() == 0) {
                     InstructionStackEntry entry = target.getStackEntry();
                     if (entry != null) {
                        boolean ok = true;
                        int parmCount = this._method.getParmLocalCount();
                        int entryCount = entry.getNumTypes();
                        if (entryCount > this._maxLocals) {
                           ok = false;
                        } else if (parmCount != entryCount && parmCount < entryCount) {
                           Type[] types = entry.getTypes();

                           for (int i = parmCount; i < entryCount && ok; i++) {
                              ok = types[i].getTypeId() == 10;
                           }
                        }

                        if (!ok) {
                           target.setIp(-9);
                           this._method.addModifiers(134217728);
                        }
                     }
                  }
               }

               this._blocks.mergeArray();
               return;
            } catch (CompileException var18) {
               ce = var18;
            }
         } catch (Throwable var19) {
            if (compiler.getTraceback()) {
               var19.printStackTrace();
            }

            throw new CompileException(
               classType.getFullName(), ((StringBuffer)(new Object())).append(var19.getMessage()).append(" in ").append(this._method.getName()).toString()
            );
         }

         throw ce;
      }
   }

   public final void optimize(Compiler compiler) {
      ClassType classType = this._method.getClassType();
      this._codeWeight = 14;
      this._byteCodeWeight = this._blocks.setOffsets(0, true);
      this._codeWeight = this._codeWeight + this._byteCodeWeight;
      int num = this._blocks.getNumExceptionHandlers();
      if (num > 0) {
         this._codeWeight = this._codeWeight + 2 + num * ExceptionHandler.getSize();
      }

      this._codeWeight = this._codeWeight + StackMap.getSize() * this._blocks.getNumStackEntries();
      classType.addCodeWeight(this._codeWeight);
   }

   public final void populate(Compiler compiler, TypeModule typeModule) throws CompileException {
      RoutineLocal routine = (RoutineLocal)this._method.getMember(compiler, typeModule);
      routine.setByteCodeWeight(this._byteCodeWeight);
      Code code = routine.getCode();
      ClassType classType = this._method.getClassType();
      code.allocateOpcodes(this._blocks.getNumInstructions());
      synchronized (_instructionPopulater) {
         _instructionPopulater.init(compiler, this._method, typeModule);
         this._blocks.walkBlocks(_instructionPopulater);
         _instructionPopulater.fini();
      }

      code.resolveBranches();
      this._length = code.computeBranches();
      int num = this._blocks.getNumExceptionHandlers();
      routine.allocateExceptionHandlers(num);

      for (int i = 0; i < num; i++) {
         ByteCodeExceptionHandler bh = this._blocks.getExceptionHandler(i);
         ClassType execptionType = bh.getClassType();
         ClassDef classDef;
         if (execptionType != null) {
            classDef = execptionType.getClassDef(typeModule);
         } else {
            classDef = typeModule.getNullClassDef(typeModule);
         }

         ExceptionHandler exceptionHandler = new ExceptionHandler(bh.getStart().getLabel(), bh.getEnd().getLabel(), bh.getHandler().getLabel(), classDef);
         routine.addExceptionHandler(exceptionHandler);
      }

      DataSection dataSection = typeModule.getDataSection();
      num = this._blocks.getNumStackEntries();
      if (num > 255) {
         throw new CompileException(
            classType.getFullName(),
            ((StringBuffer)(new Object("Error!: control flow verification information too large: "))).append(this._method.getName()).toString()
         );
      }

      routine.allocateStackMaps(num);
      num = this._blocks.getNumTargets();

      for (int var13 = 0; var13 < num; var13++) {
         InstructionTarget target = this._blocks.getTarget(var13);
         InstructionStackEntry entry = target.getStackEntry();
         if (entry != null) {
            TypeList typeList = Type.getTypeList(typeModule, null, entry.getTypes(), entry.getNumTypes(), !this._method.is(134217728));
            routine.addStackMap(new StackMap(target.getLabel(), dataSection, typeList));
         }
      }

      routine.setNumLocals(this._maxLocals);
      routine.setNumStack(this._maxStack);
      classType.setMaxTypeListSize(this._maxLocals + this._maxStack);
      this._blocks = null;
   }
}
