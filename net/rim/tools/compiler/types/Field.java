package net.rim.tools.compiler.types;

import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.analysis.InstructionResolver;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.FieldDef;
import net.rim.tools.compiler.codfile.FieldDefDomestic;
import net.rim.tools.compiler.codfile.FieldDefLocal;
import net.rim.tools.compiler.codfile.Member;
import net.rim.tools.compiler.codfile.TypeList;

public final class Field extends NameAndType {
   private Constant _expr;

   public Field(String name, Type type, ClassType classType, int modifiers, int offset, Constant expr) {
      super(name, type, classType, modifiers, offset);
      this._expr = expr;
   }

   public final boolean hasValue() {
      return this._expr != null;
   }

   public final long getValue() {
      return this._expr.getValue();
   }

   public final String getStringValue() {
      return this._expr.getString();
   }

   public final void allocateStatic() {
      if (!this.is(131072) && !this.hasOffset() && this.is(2)) {
         super._offset = super._classType.allocateStatic(super._type);
      }
   }

   @Override
   public final int getAbsoluteOffset() {
      int offset = -1;
      if (!this.is(131072)) {
         if (super._classType.is(8650752) && this.hasOffset() && this.is(4)) {
            return super._classType.getBaseSize() + super._offset;
         }
      } else if (this.is(262144)) {
         if (this.is(2)) {
            return super._offset;
         }

         offset = super._classType.getBaseSize() + super._offset;
      }

      return offset;
   }

   @Override
   public final void populate(Compiler compiler) {
      if (this.is(4)) {
         FieldDefLocal fieldDef = (FieldDefLocal)this.getMember(compiler, super._classType.getTypeModule());
         fieldDef.setAddress(this.getAbsoluteOffset());
      } else {
         if (this.hasOffset()) {
            TypeModule typeModule = super._classType.getTypeModule();
            FieldDefLocal fieldDef = (FieldDefLocal)this.getMember(compiler, typeModule);
            int offset = super._offset;
            fieldDef.setAddress(offset);
            if (this.hasValue()) {
               DataSection dataSection = typeModule.getDataSection();
               if (this._expr.isString()) {
                  String s = this._expr.getString();
                  dataSection.addInitializedStaticDataString(offset, s, InstructionResolver.isUnicode(s));
                  return;
               }

               dataSection.addInitializedStaticData(offset, this._expr.getValue(), super._type.getLocalCount());
            }
         }
      }
   }

   @Override
   public final Member getMember(Compiler compiler, TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      Member member = this.getMember(ordinal, typeModule.getCount());
      if (member == null) {
         boolean suppress = false;
         ClassDef classDef = super._classType.getClassDef(typeModule);
         TypeList typeList = Type.getTypeList(typeModule, super._type);
         DataSection dataSection = typeModule.getDataSection();
         boolean isStatic = this.is(2);
         FieldDef fieldDef = classDef.makeFieldDef(dataSection, super._name, suppress, typeList, isStatic);
         if (!this.is(131072)) {
            if (!(fieldDef instanceof FieldDefDomestic)) {
               if (fieldDef instanceof FieldDefLocal) {
                  FieldDefLocal fieldDefLocal = (FieldDefLocal)fieldDef;
                  fieldDefLocal.setAttributes(Modifier.toCodfileProtectionAttribute(super._modifiers));
               }
            } else {
               FieldDefDomestic fieldDefDomestic = (FieldDefDomestic)fieldDef;
               fieldDefDomestic.setSibling((FieldDefLocal)this.getMember(compiler, super._classType.getTypeModule()));
            }
         }

         member = this.setMember(fieldDef, ordinal);
      }

      return member;
   }
}
