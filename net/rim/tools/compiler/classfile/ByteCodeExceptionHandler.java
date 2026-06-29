package net.rim.tools.compiler.classfile;

import net.rim.tools.compiler.analysis.InstructionTarget;
import net.rim.tools.compiler.types.ClassType;

public final class ByteCodeExceptionHandler {
   private InstructionTarget _start;
   private InstructionTarget _end;
   private InstructionTarget _handler;
   private ClassType _classType;

   public ByteCodeExceptionHandler(InstructionTarget start, InstructionTarget end, InstructionTarget handler, ClassType classType) {
      this._start = start;
      this._end = end;
      this._handler = handler;
      this._classType = classType;
   }

   public final InstructionTarget getStart() {
      return this._start;
   }

   public final InstructionTarget getEnd() {
      return this._end;
   }

   public final InstructionTarget getHandler() {
      return this._handler;
   }

   public final ClassType getClassType() {
      return this._classType;
   }
}
