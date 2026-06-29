package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class RoutineLocal extends Routine {
   private CodfileArray _stackMaps;
   private int _attributes;
   private int _numLocals;
   private int _numStack;
   private Code _code;
   private CodfileArray _exceptionHandlers;
   private int _byteCodeWeight;
   public static final int HEADER_SIZE = 14;
   public static final int NEW_HEADER_SIZE = 9;
   private static final int HEADER_CODESIZE_OFFSET = 6;
   private static final int NEW_HEADER_CODESIZE_OFFSET = 4;

   private final void init() {
      this._code = new Code();
   }

   public RoutineLocal(ClassDef classDef, Identifier name, TypeList typeList, TypeList protoTypeList) {
      super(classDef, name, typeList, protoTypeList);
      this.init();
   }

   public RoutineLocal(ClassDef classDef, int offset) {
      super(classDef, offset);
      this.init();
   }

   public final void setByteCodeWeight(int byteCodeWeight) {
      this._byteCodeWeight = byteCodeWeight;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final void write(StructuredOutputStream out) {
      int baseOffset = out.getOffset();
      ((ClassDefLocal)super._classDef).ratchetStartCodeOffset(baseOffset);
      int numStackMaps = this.getNumStackMaps();
      boolean isNewStyle = false;
      if (this._attributes <= 255
         && this._byteCodeWeight <= 253
         && numStackMaps <= 3
         && super._parmLocalCount <= 3
         && this._numStack <= 3
         && this._numLocals <= 3
         && this._code.newStyleEnterOk()) {
         isNewStyle = true;
      }

      int offset = baseOffset + numStackMaps * StackMap.getSize() + (isNewStyle ? 9 : 14);
      if (numStackMaps > 0) {
         this._stackMaps.writeRelative(out, offset);
      }

      if (isNewStyle) {
         this._code.setNewStyleEnter();
         super._typeList.writeOffset(out);
         super._protoTypeList.writeOffset(out);
         out.writeByte(this._code.getExtent() + 2 & 0xFF);
         out.writeByte(this._attributes & 0xFF);
         super._name.writeOffset(out);
         int other = (numStackMaps << 6) + (this._numLocals << 4) + (super._parmLocalCount << 2) + (this._numStack << 0);
         out.writeByte(other & 0xFF);
      } else {
         super._name.writeOffset(out);
         super._protoTypeList.writeOffset(out);
         super._typeList.writeOffset(out);
         out.writeShort(this._code.getExtent());
         out.writeShort(this._attributes & 65535);
         out.writeByte(numStackMaps & 0xFF);
         out.writeByte(this._numLocals & 0xFF);
         out.writeByte(super._parmLocalCount & 0xFF);
         out.writeByte(this._numStack & 0xFF);
      }

      this.setOffset(out);

      try {
         this._code.write(out);
      } catch (Throwable var8) {
         throw new Object(
            ((StringBuffer)(new Object()))
               .append(ioe.getMessage())
               .append(" in: ")
               .append(super._classDef.getFullName())
               .append(".")
               .append(super._name.getString())
               .toString()
         );
      }

      int num = this.getNumExceptionHandlers();
      if (num > 0) {
         this._exceptionHandlers.writeRelative(out, offset);
         out.writeShort(65535);
      }

      ((ClassDefLocal)super._classDef).ratchetEndCodeOffset(out.getOffset());
      this.setExtent(out.getOffset() - baseOffset);
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      if (classDef == super._classDef) {
         classDef.writeOrdinal(out);
         out.writeShort(super._offset);
      } else {
         int ordinal = this.addStaticFixup(out, classDef);
         out.writeShort(-1);
         out.writeShort(ordinal);
      }
   }

   public final void writeNativeInvoke(StructuredOutputStream out) {
      out.writeShort(-1);
   }

   public final Code getCode() {
      return this._code;
   }

   public final void setAttributes(int attributes) {
      this._attributes |= attributes;
   }

   public final void setNumLocals(int numLocals) {
      this._numLocals = numLocals;
   }

   public final void setNumStack(int numStack) {
      this._numStack = numStack;
   }

   public final int getNumStackMaps() {
      return this._stackMaps == null ? 0 : this._stackMaps.size();
   }

   public final void allocateStackMaps(int num) {
      if (num > 0 && this._stackMaps == null) {
         this._stackMaps = new CodfileArray(num);
      }
   }

   public final void addStackMap(StackMap stackMap) {
      this._stackMaps.addElement(stackMap);
   }

   public final int getNumExceptionHandlers() {
      return this._exceptionHandlers == null ? 0 : this._exceptionHandlers.size();
   }

   public final void allocateExceptionHandlers(int num) {
      if (num > 0 && this._exceptionHandlers == null) {
         this.setAttributes(64);
         this._exceptionHandlers = new CodfileArray(num);
      }
   }

   public final void addExceptionHandler(ExceptionHandler exceptionHandler) {
      this._exceptionHandlers.addElement(exceptionHandler);
   }
}
