package net.rim.tools.compiler.types;

import java.util.Vector;
import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.analysis.InstructionCode;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.InterfaceMethodRef;
import net.rim.tools.compiler.codfile.Member;
import net.rim.tools.compiler.codfile.Routine;
import net.rim.tools.compiler.codfile.RoutineDomestic;
import net.rim.tools.compiler.codfile.RoutineLocal;
import net.rim.tools.compiler.codfile.TypeList;
import net.rim.tools.compiler.exec.MyArrays;
import net.rim.tools.compiler.util.CompileException;

public final class Method extends NameAndType {
   private NameAndType[] _parameters;
   private int _parmLocalCount;
   private InstructionCode _body;
   private Method _overrides;
   private Method[] _overriddenBy;
   private int _icallIndex;
   private Method _lastInvokeInterfaceMethod;
   private boolean _takesThisParm;
   private boolean _implementsInterfaceMethod;
   private boolean _inVtable;
   private TypeList[] _protoTypeLists;
   private InterfaceMethodRef[] _interfaceMethodRefs;
   private int _special;

   public Method(ClassType classType, String name, Type returnType, int numParms, int modifiers) {
      super(name, returnType, classType, modifiers | 8, -1);
      if (numParms > 0) {
         this._parameters = new NameAndType[numParms];
      }

      if ((modifiers & 2) == 0 || (modifiers & 16) != 0) {
         this._takesThisParm = true;
         this._parmLocalCount = 1;
      }
   }

   public final void markInVtable(boolean inVtable) {
      this._inVtable = inVtable;
   }

   public final boolean isInVtable() {
      return this._inVtable;
   }

   public final boolean isVirtualCall(ClassType classType) {
      boolean ret = this._inVtable;
      if (ret && super._offset != -1 && super._classType.getOverride() != null) {
         boolean[] isOverride = classType.getOverride();
         if (isOverride != null) {
            ret = isOverride[super._offset];
         }
      }

      return ret;
   }

   public final void addParameter(int index, String name, Type type) {
      NameAndType parmType = new NameAndType(name, type, super._classType, 1024, this._parmLocalCount);
      this._parameters[index] = parmType;
      this._parmLocalCount = this._parmLocalCount + type.getLocalCount();
      if (this._parmLocalCount > 255) {
         throw new CompileException(((StringBuffer)(new Object("Too many parameters in method "))).append(this.getName()).toString());
      }
   }

   public final int getParmLocalCount() {
      return this._parmLocalCount;
   }

   public final int getNumParms() {
      return this._parameters == null ? 0 : this._parameters.length;
   }

   public final NameAndType getParm(int parmNo) {
      return this._parameters[parmNo];
   }

   public final Type getParmType(int parmNo) {
      return this.getParm(parmNo).getType();
   }

   public final boolean hasReturnValue() {
      return super._type != null;
   }

   public final Type getReturnType() {
      return this.hasReturnValue() ? super._type : null;
   }

   public final int getMaxStack() {
      return this._body.getMaxStack();
   }

   public final boolean isSamePrototype(Method other) {
      int num = this.getNumParms();
      if (num != other.getNumParms()) {
         return false;
      }

      if (!super.equals(other)) {
         return false;
      }

      for (int i = 0; i < num; i++) {
         Type parmType = this.getParmType(i);
         Type otherParmType = other.getParmType(i);
         if (!parmType.equals(otherParmType)) {
            return false;
         }
      }

      return true;
   }

   public final boolean isVirtual() {
      return !this.is(1049106);
   }

   public final void setBody(InstructionCode body) {
      this._body = body;
   }

   public final InstructionCode getBody() {
      return this._body;
   }

   private final void addOverride(Method override) {
      int last = 0;
      if (this._overriddenBy == null) {
         this._overriddenBy = new Method[last + 1];
      } else {
         last = this._overriddenBy.length;
         this._overriddenBy = MyArrays.resize(this._overriddenBy, last + 1);
      }

      this._overriddenBy[last] = override;
   }

   public final void setOverride(Compiler compiler, Method parent) {
      if (this._overrides != parent) {
         this._overrides = parent;
         parent.addOverride(this);
         if (parent.is(268566528)) {
            this.setReachable(compiler);
         }
      }
   }

   public final void setImplements(Compiler compiler, Method ifaceMethod) {
      this._implementsInterfaceMethod = true;
      ifaceMethod.addOverride(this);
      if (ifaceMethod.is(270663680)) {
         this.setReachable(compiler);
      }
   }

   public final void setReachable(Compiler compiler) {
      if (!this.is(268435456)) {
         this.addModifiers(268435456);
         if (!this.is(131072)) {
            compiler.useMethod(this);
            super._classType.setReachable(compiler, true);
         }

         if (this._overriddenBy != null) {
            int num = this._overriddenBy.length;

            for (int i = 0; i < num; i++) {
               Method override = this._overriddenBy[i];
               override.setReachable(compiler);
            }
         }
      }
   }

   public final boolean virtualRequired(Compiler compiler) {
      if (!this.isVirtual()) {
         return false;
      } else if (this._implementsInterfaceMethod) {
         return true;
      } else if (this.is(32)) {
         return true;
      } else {
         return super._classType.getBaseClassType() == null && this.is(64) && !this.is(131072)
            ? false
            : !super._classType.is(65536) || this.is(524288) || this._overrides != null || this._overriddenBy != null;
      }
   }

   private final boolean canAccess(Compiler compiler, Method other) {
      if (other.is(128)) {
         return true;
      }

      ClassType otherClass = other.getClassType();
      ClassType thisClass = this.getClassType();
      String otherPackage = otherClass.getPackageName();
      String thisPackage = thisClass.getPackageName();
      if (otherPackage == null && thisPackage == null) {
         return true;
      }

      if (otherPackage != null && thisPackage != null && otherPackage.equals(thisPackage)) {
         return true;
      }

      if (other.is(256)) {
         return true;
      }

      compiler.generateWarning(
         false,
         thisClass.getFullName(),
         ((StringBuffer)(new Object("Method ")))
            .append(this.getName())
            .append(" does not override ")
            .append(otherClass.getFullName())
            .append(this.getName())
            .toString()
      );
      return false;
   }

   public final void addToTable(Compiler compiler, Vector vtable) {
      this.markInVtable(true);
      int num = vtable.size();

      for (int i = num - 1; i >= 0; i--) {
         Method other = (Method)vtable.elementAt(i);
         if (this.isSamePrototype(other) && this.canAccess(compiler, other)) {
            this.setOverride(compiler, other);
            this.setOffset(i);
            vtable.setElementAt(this, i);
            return;
         }
      }

      this.setOffset(num);
      vtable.addElement(this);
   }

   public final int findInTable(Vector vtable) {
      int num = vtable.size();

      for (int i = num - 1; i >= 0; i--) {
         Method other = (Method)vtable.elementAt(i);
         if (this.isSamePrototype(other)) {
            return i;
         }
      }

      return -1;
   }

   private final void resolveType(Compiler compiler, Type type) {
      if (type instanceof ArrayType) {
         ArrayType aType = (ArrayType)type;
         type = aType.getMostBaseType();
      }

      if (type instanceof ClassType) {
         ClassType classType = (ClassType)type;
         classType.setReachable(compiler, !this.is(131072));
      }
   }

   public final void resolve(Compiler compiler) {
      if (!this.is(536870912) && this.is(268435456)) {
         this.addModifiers(536870912);
         if (this.is(16)) {
            super._classType.resolve(compiler);
         }

         this.resolveType(compiler, super._type);
         if (this._parameters != null) {
            int num = this._parameters.length;

            for (int i = 0; i < num; i++) {
               Type parm = this.getParmType(i);
               this.resolveType(compiler, parm);
            }
         }

         if (!this.is(131072) && this._body != null) {
            this._body.resolve(compiler);
         }
      }
   }

   public final void optimize(Compiler compiler) {
      if (!this.is(1073741824) && this.is(268435456)) {
         this.addModifiers(1073741824);
         if (this._body != null) {
            this._body.optimize(compiler);
         }
      }
   }

   public final TypeList getProtoTypeList(TypeModule typeModule) {
      if (this._protoTypeLists == null) {
         int count = typeModule.getCount();
         this._protoTypeLists = new TypeList[count];
      }

      int ordinal = typeModule.getOrdinal();
      if (this._protoTypeLists[ordinal] == null) {
         Type thisType = null;
         if (this._takesThisParm) {
            thisType = super._classType;
         }

         this._protoTypeLists[ordinal] = Type.getTypeList(typeModule, thisType, this._parameters);
      }

      return this._protoTypeLists[ordinal];
   }

   final boolean suppressMemberName(Compiler compiler) {
      if (this.is(137363473)) {
         return false;
      } else {
         return !this.is(2) && this._inVtable ? false : this.is(512) || !this.is(384) && compiler.isOptimizePackage();
      }
   }

   @Override
   public final Member getMember(Compiler compiler, TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      Member member = this.getMember(ordinal, typeModule.getCount());
      if (member == null) {
         boolean suppress = this.suppressMemberName(compiler);
         DataSection dataSection = typeModule.getDataSection();
         ClassDef classDef = super._classType.getClassDef(typeModule);
         TypeList typeList = Type.getTypeList(typeModule, super._type);
         TypeList protoTypeList = this.getProtoTypeList(typeModule);
         Routine routine = classDef.makeRoutine(dataSection, super._name, suppress, typeList, protoTypeList);
         if (!this.is(131072)) {
            if (!(routine instanceof RoutineDomestic)) {
               if (routine instanceof RoutineLocal) {
                  RoutineLocal routineLocal = (RoutineLocal)routine;
                  routineLocal.setAttributes(Modifier.toCodfileRoutineAttribute(super._modifiers));
               }
            } else {
               RoutineDomestic routineDomestic = (RoutineDomestic)routine;
               routineDomestic.setSibling((RoutineLocal)this.getMember(compiler, super._classType.getTypeModule()));
            }
         } else if (compiler.getObjectClass().inVtable(super._offset)) {
            routine.setAddress(super._offset);
         }

         member = this.setMember(routine, ordinal);
      }

      return member;
   }

   public final InterfaceMethodRef getInterfaceMethodRef(Compiler compiler, TypeModule typeModule) {
      if (this._interfaceMethodRefs == null) {
         int count = typeModule.getCount();
         this._interfaceMethodRefs = new InterfaceMethodRef[count];
      }

      int ordinal = typeModule.getOrdinal();
      if (this._interfaceMethodRefs[ordinal] == null) {
         DataSection dataSection = typeModule.getDataSection();
         InterfaceMethodRef imf = null;
         if (super._classType.isDefined()) {
            Routine routine = (Routine)this.getMember(compiler, typeModule);
            routine.makeSymbolic(typeModule.getDataSection(), true);
            imf = dataSection.makeInterfaceMethodRef(routine);
         } else {
            imf = dataSection.getNullInterfaceMethodRef();
         }

         this._interfaceMethodRefs[ordinal] = imf;
      }

      return this._interfaceMethodRefs[ordinal];
   }

   public final int getIcallIndex(TypeModule typeModule, Method method) {
      if (this._lastInvokeInterfaceMethod != method) {
         this._icallIndex = typeModule.getIcallIndex();
         this._lastInvokeInterfaceMethod = method;
      }

      return this._icallIndex;
   }

   public final boolean populate(Compiler compiler, TypeModule typeModule) {
      if (this.is(-2147352576)) {
         return false;
      }

      if (!this.is(268435456)) {
         return false;
      }

      if (this._body == null) {
         return false;
      }

      this.addModifiers(Integer.MIN_VALUE);
      this.getMember(compiler, typeModule);
      this._body.populate(compiler, typeModule);
      this._body = null;
      return true;
   }

   public final boolean matches(String name, Type type, Vector types) {
      if (!super._name.equals(name)) {
         return false;
      }

      if (super._type != type) {
         return false;
      }

      int numParms = this.getNumParms();
      if (numParms != types.size()) {
         return false;
      }

      for (int i = 0; i < numParms; i++) {
         Type left = this.getParmType(i);
         Type right = (Type)types.elementAt(i);
         if (left != right) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final boolean equals(Object o) {
      if (!(o instanceof Method)) {
         return false;
      }

      Method other = (Method)o;
      if (this == other) {
         return true;
      }

      if (!super._name.equals(other.getName())) {
         return false;
      }

      if (super._type != other.getType()) {
         return false;
      }

      int numParms = this.getNumParms();
      if (numParms != other.getNumParms()) {
         return false;
      }

      for (int i = 0; i < numParms; i++) {
         Type left = this.getParmType(i);
         Type right = other.getParmType(i);
         if (left != right) {
            return false;
         }
      }

      return true;
   }

   @Override
   public final int hashCode() {
      int hash = super._name.hashCode() * 31 + super._type.hashCode();
      int numParms = this.getNumParms();

      for (int i = 0; i < numParms; i++) {
         hash = hash * 31 + this.getParmType(i).hashCode();
      }

      return hash;
   }

   public final void setSpecial(int code) {
      this._special = code;
   }

   public final int getSpecial() {
      return this._special;
   }
}
