package net.rim.tools.compiler.types;

import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.codfile.Member;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public class NameAndType implements Constants {
   String _name;
   Type _type;
   ClassType _classType;
   int _modifiers;
   int _offset;
   private Member[] _members;
   public static final int OFFSET_UNKNOWN;

   public NameAndType(String name, Type type, ClassType classType, int modifiers, int offset) {
      this._name = name;
      this._type = type;
      this._classType = classType;
      this._modifiers = modifiers;
      this._offset = offset;
   }

   public final String getName() {
      return this._name;
   }

   public final void setType(Type type) {
      this._type = type;
   }

   public final Type getType() {
      return this._type;
   }

   public final ClassType getClassType() {
      return this._classType;
   }

   public void populate(Compiler compiler) {
   }

   final Member getMember(int ordinal, int count) {
      if (this._members == null) {
         this._members = new Member[count];
      }

      return this._members[ordinal];
   }

   final Member setMember(Member member, int ordinal) {
      this._members[ordinal] = member;
      return member;
   }

   public Member getMember(Compiler compiler, TypeModule typeModule) {
      throw new CompileException(((StringBuffer)(new Object("no member associated with variable: "))).append(this._name).toString());
   }

   public final int getSize() {
      return this._type.getSize();
   }

   public final void setOffset(int offset) {
      this._offset = offset;
   }

   public final boolean hasOffset() {
      return this._offset != -1;
   }

   public final int getOffset() {
      return this._offset;
   }

   public int getAbsoluteOffset() {
      return this._offset;
   }

   public final void addModifiers(int modifiers) {
      this._modifiers |= modifiers;
   }

   public final void clearModifiers(int modifiers) {
      this._modifiers &= ~modifiers;
   }

   public final int getModifiers() {
      return this._modifiers;
   }

   public final boolean is(int flag) {
      return (flag & this._modifiers) != 0;
   }

   public boolean isAnd(int flag) {
      return (flag & this._modifiers) == flag;
   }

   @Override
   public boolean equals(Object o) {
      if (!(o instanceof NameAndType)) {
         return false;
      }

      NameAndType other = (NameAndType)o;
      if (this == other) {
         return true;
      }

      if (!this._name.equals(other.getName())) {
         return false;
      }

      if (this._type != other.getType()) {
         return false;
      }

      int diff = this._modifiers ^ other._modifiers;
      return (diff & 1048606) == 0;
   }

   @Override
   public int hashCode() {
      return this._name.hashCode() * 31 + this._type.getFullName().hashCode();
   }
}
