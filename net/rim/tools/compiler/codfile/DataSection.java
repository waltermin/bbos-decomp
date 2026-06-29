package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class DataSection extends CodfileItem {
   private Codfile _codfile;
   private int _attributes;
   private int _version;
   private CodfileVector _modules;
   private CodfileVector _siblings;
   private CodfileVector _aliases;
   private int _icallIndex;
   private int _staticSize;
   private Module _nullModule;
   private ClassDef _nullClassDef;
   private ClassRef _nullClassRef;
   private Routine _nullRoutine;
   private InterfaceMethodRef _nullInterfaceMethodRef;
   private EntryPoint _entryPoint;
   private EntryPoint _alternateEntryPoint;
   private CodfileVector _exports;
   private DataBytes _dataBytes;
   private CodfileVector _initializedStaticData;
   private CodfileVector _initializedStaticDataString;
   private CodfileVector _classDefs;
   private int _classDefsOffset2;
   private CodfileVector _classRefs;
   private CodfileVector _interfaceMethodRefs;
   private TypeLists _typeLists;
   private CodfileVector _methodFixupTable;
   private CodfileVector _staticMethodFixupTable;
   private CodfileVector _vcallMethodFixupTable;
   private CodfileVector _classDefCodeFixupTable;
   private CodfileVector _fieldFixupTable;
   private CodfileVector _fieldLocalFixupTable;
   private CodfileVector _staticFieldFixupTable;
   private CodfileVector _moduleCodeFixupTable;
   private boolean _implied_staticMethod_fixups;
   private boolean _implied_staticfield_fixups;
   private boolean _implied_method_fixups;
   private boolean _implied_clsref_fixups;
   private boolean _fixups_have_rettype;

   public DataSection(Codfile codfile) {
      this._codfile = codfile;
      this._modules = new CodfileVector();
      this._siblings = new CodfileVector();
      this._aliases = new CodfileVector(2);
      this._exports = new CodfileVector();
      this._dataBytes = new DataBytes();
      this._initializedStaticData = new CodfileVector(2);
      this._typeLists = new TypeLists();
      this._interfaceMethodRefs = new CodfileVector(2);
      this._classDefs = new CodfileVector();
      this._classRefs = new CodfileVector();
      this._methodFixupTable = new CodfileVector(2, true);
      this._staticMethodFixupTable = new CodfileVector(2, true);
      this._vcallMethodFixupTable = new CodfileVector(2, true);
      this._classDefCodeFixupTable = new CodfileVector(2, true);
      this._fieldFixupTable = new CodfileVector(2, true);
      this._fieldLocalFixupTable = new CodfileVector(2, true);
      this._staticFieldFixupTable = new CodfileVector(2, true);
      this._fixups_have_rettype = false;
      this._implied_method_fixups = true;
      this._implied_staticfield_fixups = true;
      this._implied_staticMethod_fixups = true;
      this._implied_clsref_fixups = true;
      this._moduleCodeFixupTable = new CodfileVector(2, true);
      Identifier empty = this._dataBytes.getNullIdentifier();
      this._nullClassDef = new ClassDefNull(this, empty, empty);
      this._nullModule = new ModuleNull(this);
      this._nullClassDef.setModule(this._nullModule);
      this._nullClassRef = new ClassRef(this, this._nullClassDef);
      this._nullRoutine = new RoutineNull(this._nullClassDef, this);
      this._nullInterfaceMethodRef = new InterfaceMethodRef(this, this._nullRoutine);
      this._version = 6;
      if (this._version >= 6) {
         this._fixups_have_rettype = true;
         this._implied_method_fixups = false;
         this._implied_staticfield_fixups = false;
         this._implied_staticMethod_fixups = false;
         this._implied_clsref_fixups = false;
      }
   }

   private final void setRoutineFixupFormat(CodfileVector ft) {
      int num = ft.size();

      for (int i = 0; i < num; i++) {
         FixupTableEntry entry = (FixupTableEntry)ft.elementAt(i);
         RoutineRef ref = (RoutineRef)entry.getRef();
         ref.setWriteRet(this);
      }
   }

   public final void assignClassRefOrdinals() {
      int num = this._classRefs.size();

      for (int i = 0; i < num; i++) {
         ClassRef classRef = (ClassRef)this._classRefs.elementAt(i);
         classRef.setOrdinal(i);
      }
   }

   public final void harvestRoutines() {
      ModuleLocal module = (ModuleLocal)this._modules.firstElement();
      module.harvestRoutines();
   }

   public final void writeAttributes(StructuredOutputStream out) {
      out.writeByte(this._attributes);
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      this.writeAttributes(out);
      out.writeByte(this._version);
      out.writeShort(this._icallIndex);
      out.writeByte(this._modules.size());
      out.writeByte(this._classDefs.size());
      this._exports.writeOffset(out);
      this._dataBytes.writeOffset(out);
      this._initializedStaticData.writeOffset(out);
      out.writeShort(this._classDefsOffset2);
      this._typeLists.writeOffset(out);
      this._interfaceMethodRefs.writeOffset(out);
      this._classRefs.writeOffset(out);
      this._methodFixupTable.writeOffset(out);
      this._staticMethodFixupTable.writeOffset(out);
      this._vcallMethodFixupTable.writeOffset(out);
      this._classDefCodeFixupTable.writeOffset(out);
      this._aliases.writeOffset(out);
      this._fieldFixupTable.writeOffset(out);
      this._fieldLocalFixupTable.writeOffset(out);
      this._staticFieldFixupTable.writeOffset(out);
      this._moduleCodeFixupTable.writeOffset(out);
      out.writeShort(this._staticSize);
      this._entryPoint.write(out);
      this._alternateEntryPoint.write(out);
      this._classDefs.writeOffsets(out);
      this._modules.write(out);
      int numModules = this._modules.size();

      for (int i = 0; i < numModules; i++) {
         Module module = (Module)this._modules.elementAt(i);
         module.writeVersion(out);
      }

      this._siblings.writeOffsets(out);
      this._aliases.writeOffsets(out);
      this._exports.write(out, true);
      this._dataBytes.write(out);
      this._initializedStaticData.write(out, true);
      if (this._initializedStaticDataString != null) {
         this._initializedStaticDataString.write(out, false);
      }

      this._classDefsOffset2 = out.getOffset();
      this._classDefs.write(out, true);
      this._typeLists.write(out);
      this._interfaceMethodRefs.write(out, true);
      if (out.getCookie() != null) {
         for (int i = 0; i < numModules; i++) {
            this.getModule(i).writeFixups(this);
         }

         if (this._implied_staticMethod_fixups && this._implied_method_fixups) {
            this.mergeFixups(this._staticMethodFixupTable, this._methodFixupTable);
         }

         if (this._fixups_have_rettype) {
            this.setRoutineFixupFormat(this._methodFixupTable);
            this.setRoutineFixupFormat(this._staticMethodFixupTable);
            this.setRoutineFixupFormat(this._vcallMethodFixupTable);
         }
      }

      if (this._implied_method_fixups) {
         this.setMemberFixupOrdinals(this._methodFixupTable);
      }

      if (this._implied_staticMethod_fixups) {
         this.setMemberFixupOrdinals(this._staticMethodFixupTable);
      }

      if (this._implied_staticfield_fixups) {
         this.setMemberFixupOrdinals(this._staticFieldFixupTable);
      }

      if (this._implied_clsref_fixups) {
         this.setMemberFixupOrdinals(this._classDefCodeFixupTable);
      }

      this._classRefs.write(out, true);
      this._methodFixupTable.write(out, true);
      this._staticMethodFixupTable.write(out, true);
      this._vcallMethodFixupTable.write(out, true);
      this._classDefCodeFixupTable.write(out, true);
      this._fieldFixupTable.write(out, true);
      this._fieldLocalFixupTable.write(out, true);
      this._staticFieldFixupTable.write(out, true);
      this._moduleCodeFixupTable.write(out, true);
      out.writeSlack(4);
      this.setExtent(out);
   }

   public final void setAttributes(int attributes) {
      this._attributes |= attributes;
   }

   public final boolean getImpliedClsrefFixups() {
      return this._implied_clsref_fixups;
   }

   public final Module getNullModule() {
      return this._nullModule;
   }

   public final Module getModule(int index) {
      return index == 255 ? this._nullModule : (Module)this._modules.elementAt(index);
   }

   public final Module getModule(String name, String version, int timeStamp, boolean isDomestic, boolean isBrittle) {
      if (name.length() >= 128) {
         name = name.substring(0, 127);
      }

      if (version != null && version.length() >= 128) {
         version = version.substring(0, 127);
      }

      int ordinal = this._modules.size();
      if (isBrittle) {
         StringBuffer v = (StringBuffer)(new Object());
         if (version != null) {
            v.append(version);
         }

         v.append('\u0000');
         v.append((char)(timeStamp & 0xFF));
         v.append((char)(timeStamp >> 8 & 0xFF));
         v.append((char)(timeStamp >> 16 & 0xFF));
         v.append((char)(timeStamp >> 24 & 0xFF));
         version = v.toString();
      }

      Module module = null;
      if (ordinal == 0) {
         module = new ModuleLocal(this, name, version, this._classDefs, this._codfile.getRoutines());
      } else if (isDomestic) {
         module = new ModuleDomestic(this, name, version);
      } else {
         module = new ModuleForeign(this, name, version);
      }

      module.setOrdinal(ordinal);
      this._modules.addElement(module);
      return module;
   }

   public final void addSibling(String name) {
      if (name.length() >= 128) {
         name = name.substring(0, 127);
      }

      Literal sibling = this._dataBytes.getLiteral(name, false, false);
      this._siblings.addElement(sibling);
   }

   public final DataBytes getDataBytes() {
      return this._dataBytes;
   }

   public final TypeLists getTypeLists() {
      return this._typeLists;
   }

   public final void addInitializedStaticData(int address, long value, int size) {
      this._initializedStaticData.addElement(new InitializedStaticData(address, (int)value));
      if (size > 1) {
         this._initializedStaticData.addElement(new InitializedStaticData(address + 1, (int)(value >> 32)));
      }
   }

   public final void addInitializedStaticDataString(int address, String value, boolean unicode) {
      CodfileVector v = this._initializedStaticDataString;
      if (v == null) {
         v = new CodfileVector(2);
         v.addElement(new InitializedStaticData(-1, -1));
         this._initializedStaticDataString = v;
      }

      Literal literal = this._dataBytes.getLiteral(value, unicode, true);
      v.addElement(new InitializedStaticData(address, literal));
   }

   public final void addExport(ExportedData export) {
      this._exports.addElementOrdered(export);
   }

   public final void setEntryRoutine(ClassDef classDef, String name, TypeList protoTypeList) {
      Identifier id = null;
      if (name == null) {
         id = this._dataBytes.getNullIdentifier();
      } else {
         id = this._dataBytes.getIdentifier(name);
      }

      if (protoTypeList != this._typeLists.getNullTypeList()) {
         protoTypeList = this._typeLists.getTypeList(protoTypeList, this, false);
      }

      this._entryPoint = new EntryPoint(classDef.getClassRef(this), id, protoTypeList);
   }

   public final void setAlternateEntryRoutine(ClassDef classDef, String name, TypeList protoTypeList) {
      Identifier id = null;
      if (name == null) {
         id = this._dataBytes.getNullIdentifier();
      } else {
         id = this._dataBytes.getIdentifier(name);
      }

      if (protoTypeList != this._typeLists.getNullTypeList()) {
         protoTypeList = this._typeLists.getTypeList(protoTypeList, this, false);
      }

      this._alternateEntryPoint = new EntryPoint(classDef.getClassRef(this), id, protoTypeList);
   }

   public final ClassDef getNullClassDef() {
      return this._nullClassDef;
   }

   public final ClassRef getNullClassRef() {
      return this._nullClassRef;
   }

   public final ClassRef makeClassRef(ClassDef classDef) {
      if (classDef == this._nullClassDef) {
         return this._nullClassRef;
      }

      ClassRef classRef = null;
      int num = this._classRefs.size();

      for (int i = 0; i < num; i++) {
         classRef = (ClassRef)this._classRefs.elementAt(i);
         if (classRef.getClassDef() == classDef) {
            return classRef;
         }
      }

      classRef = new ClassRef(this, classDef);
      this._classRefs.addElementOrdered(classRef);
      return classRef;
   }

   public final InterfaceMethodRef getNullInterfaceMethodRef() {
      return this._nullInterfaceMethodRef;
   }

   public final InterfaceMethodRef makeInterfaceMethodRef(Member member) {
      InterfaceMethodRef imr = new InterfaceMethodRef(this, member);
      this._interfaceMethodRefs.addElement(imr);
      return imr;
   }

   public final void setIcallIndex(int icallIndex) {
      this._icallIndex = icallIndex;
   }

   public final void setStaticSize(int staticSize) {
      this._staticSize = staticSize;
   }

   private final void mergeFixups(CodfileVector from, CodfileVector to) {
      int num = from.size();

      for (int i = 0; i < num; i++) {
         FixupTableEntry entry = (FixupTableEntry)from.elementAt(i);
         to.addElementOrdered(entry);
      }

      from.setSize(0);
   }

   private final void setMemberFixupOrdinals(CodfileVector ft) {
      int num = ft.size();

      for (int i = 0; i < num; i++) {
         FixupTableEntry entry = (FixupTableEntry)ft.elementAt(i);
         entry.setOrdinal(i);
      }
   }

   public final void addMethodFixup(FixupTableEntry entry) {
      if (this._implied_method_fixups) {
         MemberRef ref = (MemberRef)entry.getRef();
         boolean implied = !this.findDupeSignatures(this._methodFixupTable, ref, true);
         if (!implied) {
            this._implied_method_fixups = false;
            int num = this._methodFixupTable.size();

            for (int i = 0; i < num; i++) {
               FixupTableEntry fte = (FixupTableEntry)this._methodFixupTable.elementAt(i);
               fte.setOrdinal(-1);
               fte.setImplied(false);
            }
         }
      }

      entry.setImplied(this._implied_method_fixups);
      if (!this._implied_method_fixups && this._version < 6) {
         entry.setOrdinal(-1);
         this._methodFixupTable.negatePrefix();
         this._methodFixupTable.addElement(entry);
      } else {
         this._methodFixupTable.addElementOrdered(entry);
      }
   }

   public final void addStaticMethodFixup(FixupTableEntry entry, boolean implied) {
      if (this._implied_staticMethod_fixups) {
         if (implied) {
            MemberRef ref = (MemberRef)entry.getRef();
            implied = !this.findDupeSignatures(this._staticMethodFixupTable, ref, false);
         }

         if (!implied) {
            this._implied_staticMethod_fixups = false;
            int num = this._staticMethodFixupTable.size();

            for (int i = 0; i < num; i++) {
               FixupTableEntry fte = (FixupTableEntry)this._staticMethodFixupTable.elementAt(i);
               fte.setOrdinal(-1);
               fte.setImplied(false);
            }
         }
      }

      entry.setImplied(this._implied_staticMethod_fixups);
      if (!this._implied_staticMethod_fixups && this._version < 6) {
         this._staticMethodFixupTable.negatePrefix();
         entry.setOrdinal(-1);
         this._staticMethodFixupTable.addElement(entry);
      } else {
         this._staticMethodFixupTable.addElementOrdered(entry);
      }
   }

   public final void addVcallMethodFixup(FixupTableEntry entry) {
      this._vcallMethodFixupTable.addElementOrdered(entry);
   }

   public final void addClassDefCodeFixup(FixupTableEntry entry) {
      if (!this._implied_clsref_fixups) {
         this._classDefCodeFixupTable.addElementOrdered(entry);
      } else {
         entry.setImplied(true);
         this._classDefCodeFixupTable.addElementOrdered(entry);
      }
   }

   public final void addFieldFixup(FixupTableEntry entry) {
      this._fieldFixupTable.addElementOrdered(entry);
   }

   public final void addFieldLocalFixup(FixupTableEntry entry) {
      this._fieldLocalFixupTable.addElementOrdered(entry);
   }

   private final boolean findDupeSignatures(CodfileVector table, MemberRef ref, boolean skipthis) {
      int num = table.size();
      if (ref.getName().equals("<init>")) {
         return false;
      }

      for (int i = 0; i < num; i++) {
         FixupTableEntry fte = (FixupTableEntry)table.elementAt(i);
         MemberRef currRef = (MemberRef)fte.getRef();
         if (ref.compareSignatures(currRef, skipthis)) {
            return true;
         }
      }

      return false;
   }

   public final void addStaticFieldFixup(FixupTableEntry entry, boolean implied) {
      if (this._implied_staticfield_fixups) {
         if (implied) {
            MemberRef ref = (MemberRef)entry.getRef();
            implied = !this.findDupeSignatures(this._staticFieldFixupTable, ref, false);
         }

         if (!implied) {
            this._implied_staticfield_fixups = false;
            int num = this._staticFieldFixupTable.size();

            for (int i = 0; i < num; i++) {
               FixupTableEntry fte = (FixupTableEntry)this._staticFieldFixupTable.elementAt(i);
               fte.setImplied(false);
            }
         }
      }

      entry.setImplied(this._implied_staticfield_fixups);
      if (!this._implied_staticfield_fixups && this._version < 6) {
         this._staticFieldFixupTable.negatePrefix();
         entry.setOrdinal(-1);
         this._staticFieldFixupTable.addElement(entry);
      } else {
         this._staticFieldFixupTable.addElementOrdered(entry);
      }
   }

   public final boolean fixupsHaveRetType() {
      return this._fixups_have_rettype;
   }
}
