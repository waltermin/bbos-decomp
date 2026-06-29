package net.rim.tools.compiler.codfile;

import java.util.Vector;
import net.rim.tools.compiler.io.StructuredOutputStream;

public final class ClassDefLocal extends ClassDef {
   private ClassDef _superClass;
   private int _staticStart;
   private Routine _clinit;
   private Routine _init;
   private int _createSize;
   private int _secureIndex;
   private int _virtualRoutinesOffset;
   private CodfileArray _virtualRoutines;
   private int _nonVirtualRoutinesOffset;
   private CodfileArray _nonVirtualRoutines;
   private int _staticRoutinesOffset;
   private CodfileArray _staticRoutines;
   private int _startCodeOffset;
   private int _endCodeOffset;
   private int _attributes;
   private int _interfacesOffset;
   private CodfileArray _interfaces;
   private int _fieldDefsOffset;
   private int _staticFieldDefsOffset;
   private int _fieldDefsOffset2;
   private int _staticFieldDefsOffset2;
   public static final int HEADER_SIZE = 40;

   private final void init() {
      this._createSize = -1;
      this._startCodeOffset = 65535;
      this._endCodeOffset = 0;
   }

   public ClassDefLocal(DataSection dataSection, String packageName, String className) {
      super(dataSection, packageName, className);
      this.init();
   }

   public ClassDefLocal(DataSection dataSection, int offset) {
      super(dataSection, offset);
      this.init();
   }

   @Override
   public final void write(StructuredOutputStream out) {
      out.writeSlack(2);
      this.setOffset(out);
      super._packageName.writeOffset(out);
      super._className.writeOffset(out);
      this._superClass.writeAbsoluteClassDef(out);
      out.writeShort(this._staticStart);
      if (this._clinit.getOffset() == -1) {
         out.writeShort(0);
      } else {
         this._clinit.writeLocalOffset(out);
      }

      if (this._init.getOffset() == -1) {
         out.writeShort(0);
      } else {
         this._init.writeLocalOffset(out);
      }

      out.writeShort(this._createSize);
      out.writeShort(this._secureIndex);
      out.writeShort(0);
      out.writeShort(this._startCodeOffset);
      out.writeShort(this._endCodeOffset);
      this.writeAttributes(out);
      out.writeShort(this._virtualRoutinesOffset - super._offset);
      out.writeShort(this._nonVirtualRoutinesOffset - super._offset);
      out.writeShort(this._staticRoutinesOffset - super._offset);
      out.writeShort(this._fieldDefsOffset - super._offset);
      out.writeShort(this._staticFieldDefsOffset - super._offset);
      out.writeShort(this._interfacesOffset - super._offset);
      out.writeShort(this._fieldDefsOffset2 - super._offset);
      out.writeShort(this._staticFieldDefsOffset2 - super._offset);
      this._virtualRoutinesOffset = out.getOffset();
      if (this._virtualRoutines != null) {
         this._virtualRoutines.writeOffsets(out);
      }

      this._nonVirtualRoutinesOffset = out.getOffset();
      if (this._nonVirtualRoutines != null) {
         this._nonVirtualRoutines.writeOffsets(out);
      }

      this._staticRoutinesOffset = out.getOffset();
      if (this._staticRoutines != null) {
         this._staticRoutines.writeOffsets(out);
      }

      this._fieldDefsOffset = out.getOffset();
      if (super._fieldDefs != null) {
         super._fieldDefs.write(out);
      }

      this._staticFieldDefsOffset = out.getOffset();
      if (super._staticFieldDefs != null) {
         super._staticFieldDefs.write(out);
      }

      this._interfacesOffset = out.getOffset();
      if (this._interfaces != null) {
         this._interfaces.writeAbsoluteOrdinals(out);
      }

      this._fieldDefsOffset2 = out.getOffset();
      if (super._fieldDefs != null) {
         int num = super._fieldDefs.size();

         for (int i = 0; i < num; i++) {
            FieldDefLocal f = (FieldDefLocal)super._fieldDefs.elementAt(i);
            f.writeAttributes(out);
         }
      }

      this._staticFieldDefsOffset2 = out.getOffset();
      if (super._staticFieldDefs != null) {
         int num = super._staticFieldDefs.size();

         for (int i = 0; i < num; i++) {
            FieldDefLocal f = (FieldDefLocal)super._staticFieldDefs.elementAt(i);
            f.writeAttributes(out);
         }
      }

      this.setExtent(out);
   }

   public final void writeAttributes(StructuredOutputStream out) {
      int attributes = this._attributes;
      out.writeShort(attributes & 65535);
   }

   @Override
   public final void writeOrdinal(StructuredOutputStream out) {
      this.writeRelativeOrdinal(out);
   }

   @Override
   public final void writeRelativeOrdinal(StructuredOutputStream out) {
      out.writeByte(super._ordinal);
   }

   @Override
   public final void writeAbsoluteClassDef(StructuredOutputStream out) {
      this.writeModuleOrdinal(out);
      out.writeByte(super._ordinal);
   }

   public final void setSuperClass(ClassDef superClass, DataSection dataSection) {
      superClass.makeSymbolic(dataSection);
      this._superClass = superClass;
   }

   public final void allocateInterfaces(int num) {
      if (num > 0 && this._interfaces == null) {
         this._interfaces = new CodfileArray(num);
      }
   }

   public final void addInterface(ClassDef classDef, DataSection dataSection) {
      classDef.makeSymbolic(dataSection);
      this._interfaces.addElement(classDef);
   }

   public final void setStaticStart(int staticStart) {
      this._staticStart = staticStart;
   }

   public final void setSecureIndex(int secureIndex) {
      this._secureIndex = secureIndex;
   }

   public final void setClinit(Routine routine) {
      this._clinit = routine;
   }

   public final void setInit(Routine routine) {
      this._init = routine;
   }

   public final void ratchetStartCodeOffset(int start) {
      if (start < this._startCodeOffset) {
         this._startCodeOffset = start;
      }
   }

   public final void ratchetEndCodeOffset(int end) {
      if (end > this._endCodeOffset) {
         this._endCodeOffset = end;
      }
   }

   public final void setAttributes(int attributes) {
      this._attributes |= attributes;
   }

   @Override
   public final FieldDef createFieldDef(Identifier name, TypeList typeList, boolean isStatic) {
      return new FieldDefLocal(this, name, typeList, isStatic);
   }

   @Override
   public final FieldDef makeFieldDef(DataSection dataSection, String name, boolean suppress, TypeList typeList, boolean isStatic) {
      String id = suppress ? null : name;
      DataBytes dataBytes = dataSection.getDataBytes();
      typeList = dataSection.getTypeLists().getTypeList(typeList, dataSection, false);
      return this.createFieldDef(dataBytes.getIdentifier(id), typeList, isStatic);
   }

   @Override
   public final Routine createRoutine(Identifier name, TypeList typeList, TypeList protoTypeList) {
      return new RoutineLocal(this, name, typeList, protoTypeList);
   }

   @Override
   public final Routine makeRoutine(DataSection dataSection, String name, boolean suppress, TypeList typeList, TypeList protoTypeList) {
      String id = suppress ? null : name;
      DataBytes dataBytes = dataSection.getDataBytes();
      TypeLists typeLists = dataSection.getTypeLists();
      typeList = typeLists.getTypeList(typeList, dataSection, false);
      protoTypeList = typeLists.getTypeList(protoTypeList, dataSection, false);
      return (RoutineLocal)this.createRoutine(dataBytes.getIdentifier(id), typeList, protoTypeList);
   }

   @Override
   public final int getLibOff() {
      return 0;
   }

   public final void allocateVirtualRoutines(int num) {
      if (num > 0 && this._virtualRoutines == null) {
         this._virtualRoutines = new CodfileArray(num);
      }
   }

   public final void addVirtualRoutine(Routine routine) {
      routine.setOrdinal(this._virtualRoutines.size());
      this._virtualRoutines.addElement(routine);
      if (routine.getOffset() != -1) {
         super._module.addRoutine(routine);
      }
   }

   public final void allocateNonVirtualRoutines(int num) {
      if (num > 0 && this._nonVirtualRoutines == null) {
         this._nonVirtualRoutines = new CodfileArray(num);
      }
   }

   public final void addNonVirtualRoutine(Routine routine) {
      routine.setOrdinal(this._nonVirtualRoutines.size());
      this._nonVirtualRoutines.addElement(routine);
      if (routine.getOffset() != -1) {
         super._module.addRoutine(routine);
      }
   }

   public final void allocateStaticRoutines(int num) {
      if (num > 0 && this._staticRoutines == null) {
         this._staticRoutines = new CodfileArray(num);
      }
   }

   public final void addStaticRoutine(Routine routine) {
      routine.setOrdinal(this._staticRoutines.size());
      this._staticRoutines.addElement(routine);
      if (routine.getOffset() != -1) {
         super._module.addRoutine(routine);
      }
   }

   public final void harvestRoutines(Vector routines) {
      boolean gotOne = false;
      if (this._virtualRoutines != null) {
         int num = this._virtualRoutines.size();

         for (int i = 0; i < num; i++) {
            gotOne = true;
            routines.addElement(this._virtualRoutines.elementAt(i));
         }
      }

      if (this._nonVirtualRoutines != null) {
         int num = this._nonVirtualRoutines.size();

         for (int i = 0; i < num; i++) {
            gotOne = true;
            routines.addElement(this._nonVirtualRoutines.elementAt(i));
         }
      }

      if (this._staticRoutines != null) {
         int num = this._staticRoutines.size();

         for (int i = 0; i < num; i++) {
            gotOne = true;
            routines.addElement(this._staticRoutines.elementAt(i));
         }
      }

      if (!gotOne) {
         routines.addElement(super._nullRoutine);
      }
   }
}
