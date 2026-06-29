package net.rim.tools.compiler.codfile;

import net.rim.tools.compiler.io.StructuredOutputStream;

public final class FieldDefLocal extends FieldDef {
   private int _attributes;

   public FieldDefLocal(ClassDef classDef, Identifier name, TypeList typeList, boolean isStatic) {
      super(classDef, name, typeList, isStatic);
   }

   @Override
   protected final int addFixup(StructuredOutputStream out, ClassDef classDef) {
      int ordinal = -1;
      if (super._isStatic) {
         return super.addFixup(out, classDef);
      }

      if (super._ordinal > 255) {
         return super.addFixup(out, super._classDef);
      }

      Object obj = out.getCookie();
      if (obj != null) {
         if (super._fixups == null) {
            super._fixups = new FixupTableEntry(1);
            super._fixups.setRef(new MemberRefLocal(super._classDef, this));
         }

         super._fixups.addFixup(out.getOffset());
         ordinal = super._fixups.getOrdinal();
      }

      return ordinal;
   }

   @Override
   public final void write(StructuredOutputStream out) {
      this.setOffset(out);
      super._name.writeOffset(out);
      super._typeList.writeOffset(out);
      if (super._isStatic) {
         this.writeAddress(out);
      }

      this.setExtent(out);
   }

   @Override
   public final void writeStaticOffset(StructuredOutputStream out, ClassDef classDef) {
      if (!super._isStatic) {
         super.writeStaticOffset(out, classDef);
      } else if (classDef == super._classDef) {
         classDef.writeOrdinal(out);
         this.writeAddress(out);
      } else {
         int ordinal = this.addFixup(out, classDef);
         out.writeShort(-1);
         out.writeShort(ordinal);
      }
   }

   public final void writeAttributes(StructuredOutputStream out) {
      int attributes = this._attributes;
      out.writeByte(attributes & 0xFF);
   }

   @Override
   public final void writeFixups(DataSection dataSection) {
      if (super._isStatic) {
         super.writeFixups(dataSection);
      } else {
         if (super._fixups != null) {
            if (super._ordinal > 255) {
               super.writeFixups(dataSection);
               return;
            }

            dataSection.addFieldLocalFixup(super._fixups);
         }
      }
   }

   public final void setAttributes(int attributes) {
      this._attributes |= attributes;
   }
}
