package net.rim.tools.compiler.codfile;

import java.util.Vector;
import net.rim.tools.compiler.io.StructuredOutputStream;

public class Routine extends Member {
   protected int _parmLocalCount;
   protected TypeList _protoTypeList;
   private FixupTableEntry _fixups;
   private FixupTableEntry _staticFixups;
   private Vector _specificFixups;
   private RoutineRef _fixupRef;

   public Routine() {
   }

   public Routine(ClassDef classDef, Identifier name, TypeList typeList, TypeList protoTypeList) {
      super(classDef, name, typeList);
      this._protoTypeList = protoTypeList;
      this._parmLocalCount = protoTypeList.getLocalCount();
   }

   public Routine(ClassDef classDef, int offset) {
      super(classDef, offset);
   }

   final void makeSymbolic(DataSection dataSection, boolean includeReturnType, String actualName) {
      if (!super._name.equals(actualName)) {
         super._name = dataSection.getDataBytes().getIdentifier(actualName);
         this._protoTypeList = dataSection.getTypeLists().getTypeList(this._protoTypeList, dataSection, false);
      }

      if (dataSection.fixupsHaveRetType() || includeReturnType) {
         super._typeList = dataSection.getTypeLists().getTypeList(super._typeList, dataSection, false);
      }
   }

   public void makeSymbolic(DataSection dataSection, boolean includeReturnType) {
   }

   protected MemberRef getFixupRef(DataSection dataSection) {
      if (this._fixupRef == null) {
         this._fixupRef = new RoutineRef(super._classDef, super._classDef.getClassRef(dataSection), super._name, this._protoTypeList, super._typeList);
      }

      return this._fixupRef;
   }

   protected void addVirtualFixup(StructuredOutputStream out) {
      Object obj = out.getCookie();
      if (obj != null) {
         if (this._fixups == null) {
            this._fixups = new FixupTableEntry(2);
            this._fixups.setRef(this.getFixupRef((DataSection)obj));
         }

         this._fixups.addFixup(out.getOffset());
      }
   }

   protected int addStaticFixup(StructuredOutputStream out, ClassDef classDef) {
      Object obj = out.getCookie();
      int ordinal = -1;
      FixupTableEntry fixup = null;
      if (classDef == super._classDef) {
         fixup = this._staticFixups;
         if (obj != null) {
            if (fixup == null) {
               DataSection dataSection = (DataSection)obj;
               this.makeSymbolic(dataSection, false);
               fixup = this._staticFixups = new FixupTableEntry(2);
               fixup.setRef(this.getFixupRef(dataSection));
            }

            fixup.addFixup(out.getOffset());
         }

         if (fixup != null) {
            ordinal = fixup.getOrdinal();
         }
      } else {
         if (obj != null && this._specificFixups == null) {
            this._specificFixups = new Vector();
         }

         int num = this._specificFixups == null ? 0 : this._specificFixups.size();

         for (int i = 0; i < num; i++) {
            FixupTableEntry fte = (FixupTableEntry)this._specificFixups.elementAt(i);
            MemberRef memberRef = (MemberRef)fte.getRef();
            if (memberRef.getClassDef() == classDef) {
               fixup = fte;
               break;
            }
         }

         if (obj != null) {
            if (fixup == null) {
               DataSection dataSection = (DataSection)obj;
               this.makeSymbolic(dataSection, false);
               fixup = new FixupTableEntry(2);
               fixup.setRef(new RoutineRef(classDef, classDef.getClassRef(dataSection), super._name, this._protoTypeList, super._typeList));
               this._specificFixups.addElement(fixup);
            }

            fixup.addFixup(out.getOffset());
         }
      }

      return ordinal;
   }

   @Override
   public void writeMemberAddress(StructuredOutputStream out, boolean nullRef) {
      int address = super._address;
      if (nullRef) {
         address = -1;
      }

      if (address == -1) {
         this.addVirtualFixup(out);
      }

      out.writeShort(address);
   }

   public int getVTableOffset(boolean nullRef) {
      int address = super._address;
      if (nullRef) {
         address = -1;
      }

      return address;
   }

   @Override
   public void writeFixups(DataSection dataSection) {
      if (this._fixups != null) {
         dataSection.addVcallMethodFixup(this._fixups);
      }

      boolean implied = true;
      implied = false;
      if (this._staticFixups != null) {
         dataSection.addStaticMethodFixup(this._staticFixups, implied);
      }

      if (this._specificFixups != null) {
         implied = false;
         int num = this._specificFixups.size();

         for (int i = 0; i < num; i++) {
            FixupTableEntry fte = (FixupTableEntry)this._specificFixups.elementAt(i);
            dataSection.addStaticMethodFixup(fte, implied);
         }
      }
   }

   public TypeList getProtoTypeList() {
      return this._protoTypeList;
   }

   public int getLocalCount() {
      return this._parmLocalCount;
   }
}
