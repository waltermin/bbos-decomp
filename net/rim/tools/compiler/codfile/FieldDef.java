package net.rim.tools.compiler.codfile;

import java.util.Vector;
import net.rim.tools.compiler.io.StructuredOutputStream;

public class FieldDef extends Member {
   protected boolean _isStatic;
   protected FixupTableEntry _fixups;
   private Vector _specificFixups;

   protected FieldDef(ClassDef classDef, Identifier name, TypeList typeList, boolean isStatic) {
      super(classDef, name, typeList);
      this._isStatic = isStatic;
      this._specificFixups = null;
   }

   public void makeSymbolic(DataSection dataSection) {
   }

   protected int addFixup(StructuredOutputStream out, ClassDef classDef) {
      Object obj = out.getCookie();
      int ordinal = -1;
      FixupTableEntry fixup = null;
      if (classDef == super._classDef) {
         fixup = this._fixups;
         if (obj != null) {
            if (fixup == null) {
               DataSection dataSection = (DataSection)obj;
               this.makeSymbolic(dataSection);
               fixup = this._fixups = new FixupTableEntry(2);
               fixup.setRef(new MemberRef(super._classDef, super._classDef.getClassRef(dataSection), super._name, super._typeList));
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
               this.makeSymbolic(dataSection);
               fixup = new FixupTableEntry(2);
               fixup.setRef(new MemberRef(classDef, classDef.getClassRef(dataSection), super._name, super._typeList));
               this._specificFixups.addElement(fixup);
            }

            fixup.addFixup(out.getOffset());
         }
      }

      return ordinal;
   }

   @Override
   public void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      if (!this._isStatic) {
         out.writeByte(-1);
         out.writeByte(-1);
      } else {
         classDef.writeOrdinal(out);
         this.writeAddress(out);
      }
   }

   @Override
   public void writeMemberAddress(StructuredOutputStream out, boolean nullRef) {
      if (!this._isStatic && !nullRef && super._address != -1) {
         this.writeAddressByte(out);
      } else {
         if (!this._isStatic) {
            this.addFixup(out, super._classDef);
         }

         out.writeByte(-1);
      }
   }

   @Override
   public void writeFixups(DataSection dataSection) {
      if (this._fixups != null) {
         if (this._isStatic) {
            dataSection.addStaticFieldFixup(this._fixups, true);
         } else {
            dataSection.addFieldFixup(this._fixups);
         }
      }

      if (this._specificFixups != null) {
         int num = this._specificFixups.size();

         for (int i = 0; i < num; i++) {
            FixupTableEntry fte = (FixupTableEntry)this._specificFixups.elementAt(i);
            if (this._isStatic) {
               dataSection.addStaticFieldFixup(fte, false);
            } else {
               dataSection.addFieldFixup(fte);
            }
         }
      }
   }
}
