package net.rim.tools.compiler.types;

import java.util.Vector;
import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.Host;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.ClassDefDomestic;
import net.rim.tools.compiler.codfile.ClassDefLocal;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.FieldDef;
import net.rim.tools.compiler.codfile.Routine;
import net.rim.tools.compiler.codfile.TypeItem;
import net.rim.tools.compiler.codfile.TypeList;
import net.rim.tools.compiler.exec.MyArrays;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.util.DuplicateException;
import net.rim.tools.compiler.util.StringHelper;

public final class ClassType extends ReferenceType {
   private ClassType _baseClassType;
   private ClassType[] _baseInterfaces;
   private Vector _interfaces;
   private Vector _vtable;
   private boolean[] _isOverride;
   private Field[] _fields;
   private int _numFields;
   private Method[] _methods;
   private int _numMethods;
   private int _baseSize;
   private int _numInstance;
   private int _numStatic;
   private int _maxTypeListSize;
   private int _codeWeight;
   private int _dataWeight;
   private int _vtableWeight;
   private int _fieldWeight;
   private String _packageName;
   private String _fullName;
   private int _modifiers;
   private boolean _defined;
   private boolean _extended;
   private Object _classInfo;
   private static StringBuffer _stringBuffer = (StringBuffer)(new Object());
   private static final int NUM_RESERVED_STATICS = 2;

   public ClassType(String name, String packageName) {
      super(name);
      this._packageName = packageName;
      this._numStatic = 2;
   }

   @Override
   public final void setTypeModule(TypeModule typeModule) {
      super.setTypeModule(typeModule);
      typeModule.addDataWeight(this._dataWeight);
      typeModule.addCodeWeight(this._codeWeight);
      typeModule.addVtableWeight(this._vtableWeight);
      typeModule.addFieldWeight(this._fieldWeight);
   }

   public final String getPackageName() {
      return this._packageName;
   }

   @Override
   public final String getFullName() {
      if (this._fullName == null) {
         if (this._packageName != null && this._packageName.length() != 0) {
            this._fullName = ((StringBuffer)(new Object())).append(this._packageName).append('.').append(super._typeName).toString();
         } else {
            this._fullName = super._typeName;
         }
      }

      return this._fullName;
   }

   public static final String extractPackageName(String name) {
      int dot = name.lastIndexOf(46);
      return dot != -1 ? name.substring(0, dot) : null;
   }

   @Override
   public final int getTypeId() {
      return 7;
   }

   public final int getBaseSize() {
      return this._baseSize;
   }

   public final boolean inVtable(int offset) {
      return offset >= 0 && offset < this._vtable.size();
   }

   public final void addModifiers(int modifiers) {
      this._modifiers |= modifiers;
   }

   public final void clearModifiers(int modifiers) {
      this._modifiers &= ~modifiers;
   }

   public final void setBaseClass(ClassType baseClassType) {
      this._baseClassType = baseClassType;
   }

   public final ClassType getBaseClassType() {
      return this._baseClassType;
   }

   public final boolean implementsInterface(ClassType implementedClassType) {
      if (this._interfaces != null) {
         int n = this._interfaces.size();

         for (int i = 0; i < n; i++) {
            ClassType ifaceClassType = (ClassType)this._interfaces.elementAt(i);
            if (implementedClassType.equals(ifaceClassType)) {
               return true;
            }
         }
      }

      return false;
   }

   private final void addInterface(ClassType ifaceClassType) {
      if (this._interfaces == null) {
         this._interfaces = (Vector)(new Object());
      }

      if (!this.implementsInterface(ifaceClassType)) {
         this._interfaces.addElement(ifaceClassType);
         if (ifaceClassType.is(67108864)) {
            this.addModifiers(67108864);
         }
      }
   }

   private final void addInterfaceArray(ClassType[] interfaces) {
      if (interfaces != null) {
         int n = interfaces.length;

         for (int i = 0; i < n; i++) {
            this.addInterface(interfaces[i]);
         }
      }
   }

   private final void addInterfaceVector(Vector interfaces) {
      if (interfaces != null) {
         int n = interfaces.size();

         for (int i = 0; i < n; i++) {
            ClassType ifaceClassType = (ClassType)interfaces.elementAt(i);
            this.addInterface(ifaceClassType);
         }
      }
   }

   public final void allocateBaseInterfaces(int num) {
      if (this._baseInterfaces == null && num > 0) {
         this._baseInterfaces = new ClassType[num];
      }
   }

   public final void setBaseInterface(int index, ClassType ifaceClassType) {
      this._baseInterfaces[index] = ifaceClassType;
   }

   public final int getNumFields() {
      return this._numFields;
   }

   public final Field getField(int index) {
      return this._fields[index];
   }

   public final void allocateFields(int num) {
      if (num > 0) {
         if (this._fields == null) {
            this._fields = new Field[num];
            return;
         }

         if (num >= this._fields.length) {
            this._fields = MyArrays.resize(this._fields, num);
         }
      }
   }

   public final Field addData(Compiler compiler, String memberName, Type type, int modifiers, Constant value) {
      if (!StringHelper.validateIdentifier(memberName)) {
         throw new CompileException(this.getFullName(), ((StringBuffer)(new Object("Invalid member name: "))).append(memberName).toString());
      }

      if (this.findField(compiler, memberName, type, false, false) != null) {
         throw new DuplicateException(this.getFullName(), memberName, super._typeName);
      }

      String fullName = null;
      int offset = -1;
      boolean notLibrary = !this.is(131072);
      if (notLibrary) {
         synchronized (_stringBuffer) {
            String className = this.getFullName();
            _stringBuffer.setLength(0);
            _stringBuffer.ensureCapacity(className.length() + 1 + memberName.length());
            _stringBuffer.append(className);
            _stringBuffer.append('.');
            _stringBuffer.append(memberName);
            fullName = _stringBuffer.toString();
         }

         if ((modifiers & 2) == 0) {
            offset = this._numInstance;
            this._numInstance = this._numInstance + type.getLocalCount();
            if (compiler.checkFieldForExport(fullName, this, this._numFields)) {
               modifiers |= 2097152;
            }
         } else {
            if ((modifiers & 64) == 0 || (modifiers & 134217728) != 0 || value == null) {
               offset = this.allocateStatic(type);
            }

            if (compiler.checkStaticDataForExport(fullName, this, this._numFields)) {
               modifiers |= 2097152;
            }
         }
      }

      Field field = new Field(memberName, type, this, modifiers, offset, value);
      this.allocateFields(this._numFields + 1);
      this._fields[this._numFields++] = field;
      return field;
   }

   public final Field findField(Compiler compiler, String name, Type type, boolean isStatic, boolean recurse) {
      int num = this._numFields;

      for (int i = 0; i < num; i++) {
         Field field = this._fields[i];
         if (field != null && field.getName().equals(name) && field.getType().equals(type)) {
            return field;
         }
      }

      if (this.is(131072)) {
         if (isStatic) {
            Field field = compiler.findStatic(this, type, name);
            if (field != null) {
               this.allocateFields(this._numFields + 1);
               this._fields[this._numFields++] = field;
               return field;
            }
         }

         int modifiers = 128 | (isStatic ? 2 : 4);
         modifiers = compiler.augmentFieldModifiers(this, modifiers);
         int offset = -1;
         Field field = new Field(name, type, this, modifiers, offset, null);
         this.allocateFields(this._numFields + 1);
         this._fields[this._numFields++] = field;
         return field;
      } else {
         if (!recurse) {
            return null;
         }

         Field field = null;
         if (isStatic && this._baseInterfaces != null) {
            num = this._baseInterfaces.length;

            for (int i = 0; i < num; i++) {
               ClassType classType = this._baseInterfaces[i];
               field = classType.findField(compiler, name, type, isStatic, false);
               if (field != null) {
                  break;
               }
            }
         }

         if (field == null && this._baseClassType != null) {
            field = this._baseClassType.findField(compiler, name, type, isStatic, recurse);
         }

         return field;
      }
   }

   public final void allocateMethods(int num) {
      if (this._methods == null && num > 0) {
         this._methods = new Method[num];
      }
   }

   private final Method findDuplicate(Method other) {
      String name = other.getName();
      int parmCount = other.getNumParms();
      int num = this._numMethods;

      label32:
      for (int i = 0; i < num; i++) {
         Method method = this._methods[i];
         if (method.getName().equals(name) && method.getNumParms() == parmCount) {
            for (int j = 0; j < parmCount; j++) {
               if (!method.getParmType(j).equals(other.getParmType(j))) {
                  continue label32;
               }
            }

            return method;
         }
      }

      return null;
   }

   public final void addMethod(Compiler compiler, Method method) {
      Method otherMethod = this.findDuplicate(method);
      if (otherMethod != null) {
         if (method.getType() == otherMethod.getType()) {
            compiler.generateWarning(
               false, this.getFullName(), ((StringBuffer)(new Object("Duplicate definition found for method: "))).append(method.getName()).toString()
            );
         } else {
            compiler.generateWarning(
               true, this.getFullName(), ((StringBuffer)(new Object("Duplicate method only differs by return type: "))).append(method.getName()).toString()
            );
         }

         method.addModifiers(134217728);
         otherMethod.addModifiers(134217728);
      }

      if (this._methods == null) {
         this.allocateMethods(1);
      } else if (this._numMethods == this._methods.length) {
         this._methods = MyArrays.resize(this._methods, this._numMethods + 1);
      }

      this._methods[this._numMethods++] = method;
      if (!method.is(131072) && method.isAnd(544)) {
         method.addModifiers(134217728);
      }

      String name = method.getName();
      if (!this.is(2048) && method.is(18)) {
         boolean check = super._typeName.equals("MIDletMain") && name.equals("main");
         if (!compiler.isMakingMIDlet()) {
            check = !check;
         }

         if (check && compiler.checkStaticMethodForExport(name, method)) {
            method.addModifiers(2097152);
         }
      }

      if (!method.is(131072) && (method.is(137887744) || method.is(16) && (method.getNumParms() == 0 || method.is(33554432)))) {
         method.setReachable(compiler);
      }
   }

   private final Method lookupMethod(String name, Type type, Vector types) {
      int num = this._numMethods;

      for (int i = 0; i < num; i++) {
         Method method = this._methods[i];
         if (method.matches(name, type, types)) {
            return method;
         }
      }

      return null;
   }

   private final Method recursiveLookupMethod(String name, Type type, Vector types, boolean isStatic) {
      Method method = this.lookupMethod(name, type, types);
      if (this.is(131072)) {
         return method;
      }

      if (method == null && !isStatic && this.is(2048) && this._baseInterfaces != null) {
         int num = this._baseInterfaces.length;

         for (int i = 0; i < num; i++) {
            ClassType classType = this._baseInterfaces[i];
            method = classType.recursiveLookupMethod(name, type, types, isStatic);
            if (method != null) {
               break;
            }
         }
      }

      if (method == null && this._baseClassType != null) {
         method = this._baseClassType.recursiveLookupMethod(name, type, types, isStatic);
      }

      return method;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public final Method findMethod(Compiler compiler, String name, Type type, Vector types, boolean isStatic, boolean recursive) {
      Method method = null;
      if (recursive) {
         method = this.recursiveLookupMethod(name, type, types, isStatic);
      } else {
         method = this.lookupMethod(name, type, types);
      }

      if (method == null) {
         ClassType classType = this;

         while (!classType.is(131072) && classType._baseClassType != null) {
            classType = classType._baseClassType;
         }

         if (classType.is(131072)) {
            boolean inVtable = true;
            if (classType._baseClassType == null && classType == compiler.getObjectClass()) {
               Vector vt = compiler.getObjectClassVTable();
               int index = vt.indexOf(name);
               if (index == -1) {
                  inVtable = false;
                  vt = compiler.getObjectClassMethods();
                  index = vt.indexOf(name);
                  if (index == -1) {
                     return null;
                  }
               }
            }

            label87:
            try {
               int modifiers = 128 | (isStatic ? 2 : 0);
               modifiers = compiler.augmentMethodModifiers(classType, modifiers);
               int num = types.size();
               method = new Method(classType, name, type, num, modifiers);

               for (int i = 0; i < num; i++) {
                  method.addParameter(i, null, (Type)types.elementAt(i));
               }

               method.markInVtable(inVtable);
               classType.addMethod(compiler, method);
            } catch (Throwable var14) {
               if (compiler.getTraceback()) {
                  e.printStackTrace();
               }
               break label87;
            }
         }
      }

      return method;
   }

   public final void setReachable(Compiler compiler, boolean checkSecureIndex) {
      if (!this.is(268435456)) {
         this.addModifiers(268435456);
         compiler.referenceClass(this);
      }
   }

   public final void setDefined() {
      this._defined = true;
   }

   public final boolean isDefined() {
      return this._defined;
   }

   public final boolean is(int flag) {
      return (flag & this._modifiers) != 0;
   }

   public final boolean isAnd(int flag) {
      return (flag & this._modifiers) == flag;
   }

   public final int allocateStatic(Type type) {
      int offset = this._numStatic;
      this._numStatic = this._numStatic + type.getLocalCount();
      return offset;
   }

   public final void setMaxTypeListSize(int maxTypeListSize) {
      if (maxTypeListSize > this._maxTypeListSize) {
         this._maxTypeListSize = maxTypeListSize;
      }
   }

   private final boolean findClassDefinition(Compiler compiler) {
      Host host = compiler.getHost();
      String fullName = this.getFullName();
      this._classInfo = host.getClassInfo(fullName);
      if (this._classInfo != null) {
         String moduleName = host.getModuleName(this._classInfo);
         String moduleVersion = host.getModuleVersion(this._classInfo);
         TypeModule typeModule = compiler.findInputTypeModule(moduleName, moduleVersion, 0);
         this.setTypeModule(typeModule);
         int modifiers = Modifier.translateCodfileClassAttributes(host.getClassAttributes(this._classInfo));
         modifiers = compiler.augmentClassModifiers(modifiers | 131072);
         this.addModifiers(modifiers);
         ClassType objectClass = compiler.getObjectClass();
         if (this != objectClass) {
            this.setBaseClass(objectClass);
         }

         this.setDefined();
         return true;
      } else {
         return false;
      }
   }

   public final void resolve(Compiler compiler) {
      boolean potentialMIDlet = false;
      boolean isObject = false;
      if (!this.is(536870912) && this.is(268435456)) {
         this.addModifiers(536870912);
         if (!this.isDefined() && !this.findClassDefinition(compiler)) {
            compiler.generateWarning(false, this.getFullName(), "No definition found");
         } else {
            if (!StringHelper.validateIdentifier(super._typeName)) {
               throw new CompileException(this.getFullName(), ((StringBuffer)(new Object("Invalid class name: "))).append(super._typeName).toString());
            }

            if (this._baseClassType == null && this == compiler.getObjectClass() && this.is(131072)) {
               isObject = true;
               this.addModifiers(1073741824);
               if (this._numMethods == 0) {
                  compiler.prepopulateObjectVTableMethods();
               }

               this._baseSize = 0;
               Vector vt = compiler.getObjectClassVTable();
               this._vtable = (Vector)(new Object(vt.size()));
               this._vtable.setSize(vt.size());
               this._isOverride = new boolean[vt.size()];

               for (int i = 0; i < this._isOverride.length; i++) {
                  this._isOverride[i] = true;
               }

               int n = this._numMethods;

               for (int var23 = 0; var23 < n; var23++) {
                  Method method = this._methods[var23];
                  String name = null;
                  name = method.getName();
                  int index = vt.indexOf(name);
                  if (index != -1) {
                     method.setOffset(index);
                     this._vtable.setElementAt(method, index);
                  }
               }
            } else if (this._baseClassType != null) {
               this._baseClassType.setReachable(compiler, !this.is(131072));
               this._baseClassType.resolve(compiler);
               if (!this._baseClassType.isDefined()) {
                  this.addModifiers(134217728);
               }

               if (!this.is(2048)) {
                  this._baseClassType._extended = true;
                  this.addInterfaceVector(this._baseClassType._interfaces);
                  this._vtable = this.cloneVTable(this._baseClassType._vtable);
                  if (!this.is(131072) && compiler.isMakingMIDlet() && this._baseClassType.getFullName().equals("javax.microedition.midlet.MIDlet")) {
                     potentialMIDlet = true;
                     compiler.addPotentialMIDlet(this);
                  }
               } else {
                  this._vtable = (Vector)(new Object());
               }
            } else {
               this._vtable = (Vector)(new Object());
            }

            if (this._baseClassType == null) {
               this._baseSize = 0;
            } else {
               this._baseSize = this._baseClassType._baseSize + this._baseClassType._numInstance;
            }

            if (this._baseInterfaces != null) {
               int removeCount = 0;
               int n = this._baseInterfaces.length;

               for (int i = 0; i < n; i++) {
                  ClassType ifaceClassType = this._baseInterfaces[i];
                  ifaceClassType.setReachable(compiler, !this.is(131072));
                  ifaceClassType.resolve(compiler);
                  this.addInterface(ifaceClassType);
                  if (!ifaceClassType.isDefined()) {
                     this.addModifiers(134217728);
                     compiler.generateWarning(
                        true,
                        this.getFullName(),
                        ((StringBuffer)(new Object("Implements undefined interface: "))).append(ifaceClassType.getFullName()).toString()
                     );
                     this._baseInterfaces[i] = null;
                     removeCount++;
                  }
               }

               if (removeCount > 0) {
                  if (removeCount >= n) {
                     this._baseInterfaces = null;
                  } else {
                     ClassType[] bi = new ClassType[n - removeCount];
                     int j = 0;

                     for (int var25 = 0; var25 < n; var25++) {
                        ClassType ifaceClassType = this._baseInterfaces[var25];
                        if (ifaceClassType != null) {
                           bi[j++] = ifaceClassType;
                        }
                     }

                     this._baseInterfaces = bi;
                  }
               }
            }

            if (this._interfaces != null) {
               for (int i = 0; i < this._interfaces.size(); i++) {
                  ClassType ifaceClassType = (ClassType)this._interfaces.elementAt(i);
                  this.addInterfaceArray(ifaceClassType._baseInterfaces);
               }
            }

            if (!this.is(131072)) {
               boolean isLocal = false;
               if (this._baseClassType == null || this._baseClassType == compiler.getObjectClass() || this._baseClassType.is(65536)) {
                  isLocal = true;
                  if (this._interfaces != null) {
                     int n = this._interfaces.size();

                     for (int i = 0; i < n && isLocal; i++) {
                        ClassType ifaceClassType = (ClassType)this._interfaces.elementAt(i);
                        if (!ifaceClassType.is(65536)) {
                           isLocal = false;
                        }
                     }
                  }
               }

               if (isLocal) {
                  this.addModifiers(65536);
               } else {
                  int n = this._numMethods;

                  for (int i = 0; i < n; i++) {
                     Method method = this._methods[i];
                     if (method.isVirtual()) {
                        this._methods[i].setReachable(compiler);
                     }
                  }
               }

               if (this._baseClassType == null || this._baseClassType == compiler.getObjectClass() || this._baseClassType.is(8388608)) {
                  this.addModifiers(8388608);
               }
            }

            if (!isObject) {
               if (!this.is(131072)) {
                  boolean foundInit = false;
                  int n = this._numMethods;

                  for (int i = 0; i < n; i++) {
                     Method method = this._methods[i];
                     if (method.isVirtual()) {
                        method.addToTable(compiler, this._vtable);
                     } else if (method.is(16) && method.getNumParms() == 0) {
                        foundInit = true;
                        if (potentialMIDlet && !method.is(128)) {
                           compiler.generateWarning(false, this.getFullName(), "Default MIDlet constructor should be public.");
                        }
                     }
                  }

                  if (!foundInit) {
                     for (int var30 = 0; var30 < n; var30++) {
                        Method method = this._methods[var30];
                        if (method.is(16)) {
                           method.setReachable(compiler);
                        }
                     }
                  }

                  if (this.is(2048)) {
                     n = this._vtable.size();

                     for (int var32 = 0; var32 < n; var32++) {
                        Method method = (Method)this._vtable.elementAt(var32);
                        method.setOffset(-1);
                     }
                  } else if (this._interfaces != null) {
                     n = this._interfaces.size();

                     for (int var31 = 0; var31 < n; var31++) {
                        ClassType ifaceClassType = (ClassType)this._interfaces.elementAt(var31);
                        int num = ifaceClassType._numMethods;

                        for (int j = 0; j < num; j++) {
                           Method method = ifaceClassType._methods[j];
                           if (!method.is(1048576)) {
                              int location = method.findInTable(this._vtable);
                              Method myMethod = null;
                              if (location != -1) {
                                 myMethod = (Method)this._vtable.elementAt(location);
                              } else if (this.is(32)) {
                                 myMethod = compiler.mirandize(this, method);
                                 this.addMethod(compiler, myMethod);
                                 myMethod.addToTable(compiler, this._vtable);
                              }

                              if (myMethod != null) {
                                 myMethod.setImplements(compiler, method);
                              }
                           }
                        }
                     }
                  }
               }

               int n = this._numFields;

               for (int i = 0; i < n; i++) {
                  Type fieldType = this._fields[i].getType();
                  if (fieldType instanceof ArrayType) {
                     ArrayType arrayType = (ArrayType)fieldType;
                     fieldType = arrayType.getMostBaseType();
                  }

                  if (fieldType instanceof ClassType) {
                     ClassType fieldClassType = (ClassType)fieldType;
                     fieldClassType.setReachable(compiler, !this.is(131072));
                  }
               }

               if (this == compiler.getStringClass()) {
                  Type intType = compiler.getIntType();
                  Vector types = (Vector)(new Object(1));
                  Method method = this.lookupMethod("length", intType, types);
                  if (method != null) {
                     method.setSpecial(1);
                  }

                  types.addElement(intType);
                  method = this.lookupMethod("charAt", compiler.getCharType(), types);
                  if (method != null) {
                     method.setSpecial(2);
                  }

                  this._isOverride = new boolean[this._vtable.size()];
                  return;
               }

               if (this == compiler.getStringBufferClass()) {
                  Vector types = (Vector)(new Object(1));
                  Method method = this.lookupMethod("<init>", null, types);
                  if (method != null) {
                     method.setSpecial(3);
                  }

                  types.addElement(compiler.getStringClass());
                  method = this.lookupMethod("<init>", null, types);
                  if (method != null) {
                     method.setSpecial(4);
                  }

                  method = this.lookupMethod("append", this, types);
                  if (method != null) {
                     method.setSpecial(5);
                  }
               }
            }
         }
      }
   }

   public final boolean[] getOverride() {
      return this._isOverride;
   }

   private final Vector cloneVTable(Vector vtable) {
      int i = 0;
      if (vtable != null) {
         i = vtable.size();
      }

      Vector clone = (Vector)(new Object(i));
      clone.setSize(i);

      while (--i >= 0) {
         clone.setElementAt(vtable.elementAt(i), i);
      }

      return clone;
   }

   public final void optimize(Compiler compiler) {
      if (!this.is(1073741824) && this.is(268435456)) {
         this.addModifiers(1073741824);
         if (!this.is(131072) && this.isDefined()) {
            this._dataWeight += 40;
            int n = this._numMethods;
            this._dataWeight += n * 2;

            for (int i = 0; i < n; i++) {
               Method method = this._methods[i];
               this._dataWeight = this._dataWeight + method.getName().length();
            }

            this._fieldWeight = 3;
            n = this._numFields;

            for (int var12 = 0; var12 < n; var12++) {
               Field field = this._fields[var12];
               Type fieldType = field.getType();
               boolean isStatic = field.is(2);
               if (isStatic) {
                  if (field.hasOffset()) {
                     this._dataWeight += 7;
                     this._dataWeight = this._dataWeight + field.getName().length();
                  }
               } else {
                  this._dataWeight += 5;
                  if (fieldType instanceof ReferenceType) {
                     this._fieldWeight++;
                  }
               }
            }

            if (this._baseInterfaces != null) {
               this._dataWeight = this._dataWeight + this._baseInterfaces.length * 2;
            }

            if (!this.is(2048)) {
               Vector vtable;
               if (this._baseClassType != null) {
                  this._fieldWeight = this._fieldWeight + this._baseClassType.getFieldWeight();
                  this._baseClassType.optimize(compiler);
                  this._baseSize = this._baseClassType._baseSize + this._baseClassType._numInstance;
                  vtable = this.cloneVTable(this._baseClassType._vtable);
               } else {
                  vtable = (Vector)(new Object());
               }

               if (!this._extended && !this.is(96) && !this.is(524288)) {
                  this.addModifiers(64);
                  n = this._numMethods;

                  for (int var13 = 0; var13 < n; var13++) {
                     Method method = this._methods[var13];
                     if (!method.is(48)) {
                        method.addModifiers(64);
                     }
                  }
               }

               int first_vfindex = vtable.size();
               n = this._numMethods;

               for (int var14 = 0; var14 < n; var14++) {
                  Method method = this._methods[var14];
                  if (method.is(268435456) && method.virtualRequired(compiler)) {
                     method.addToTable(compiler, vtable);
                  } else {
                     method.markInVtable(false);
                  }
               }

               n = vtable.size();
               this._vtableWeight = 10 + n * 4;
               if (this._interfaces != null) {
                  this._interfaces = null;
               }

               this._vtable = vtable;
               n = vtable.size();
               if (n > 0) {
                  if (!this.is(524288)) {
                     this._isOverride = new boolean[n];
                     n = this._numMethods;

                     for (int var15 = 0; var15 < n; var15++) {
                        Method method = this._methods[var15];
                        if (method.isInVtable()) {
                           int offset = method.getOffset();
                           if (offset != -1) {
                              this.propagateOverride(offset);
                           }
                        }
                     }
                  } else if (this.isAnd(64)) {
                     this._isOverride = new boolean[n];

                     for (ClassType base = this._baseClassType; base != null && !base.is(131072); base = base._baseClassType) {
                        boolean[] isOverride = base._isOverride;
                        if (isOverride != null) {
                           return;
                        }

                        n = base._vtable.size();
                        isOverride = new boolean[n];

                        for (int var16 = 0; var16 < n; var16++) {
                           isOverride[var16] = true;
                        }

                        base._isOverride = isOverride;
                     }
                  }
               }
            }
         }
      }
   }

   private final void propagateOverride(int offset) {
      for (ClassType base = this._baseClassType; base != null; base = base._baseClassType) {
         boolean[] isOverride = base._isOverride;
         if (isOverride == null) {
            return;
         }

         if (offset >= isOverride.length) {
            return;
         }

         isOverride[offset] = true;
      }
   }

   public final void addDataWeight(int weight) {
      this._dataWeight += weight;
   }

   public final int getDataWeight() {
      return this._dataWeight;
   }

   public final void addCodeWeight(int weight) {
      this._codeWeight += weight;
   }

   public final int getCodeWeight() {
      return this._codeWeight;
   }

   public final int getVtableWeight() {
      return this._vtableWeight;
   }

   public final int getFieldWeight() {
      return this._fieldWeight;
   }

   @Override
   public final ClassDef getClassDef(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      ClassDef classDef = this.getClassDef(ordinal, typeModule.getCount());
      if (classDef == null) {
         if (!this.isDefined()) {
            DataSection dataSection = typeModule.getDataSection();
            classDef = dataSection.getNullModule().makeClassDef(dataSection, this._packageName, super._typeName);
            typeModule.addUndefinedClass(this.getFullName());
         } else {
            classDef = super._typeModule.makeClassDef(typeModule, this._packageName, super._typeName);
            if (!this.is(131072)) {
               if (!(classDef instanceof ClassDefDomestic)) {
                  ((ClassDefLocal)classDef).setAttributes(Modifier.toCodfileClassAttribute(this._modifiers));
               } else {
                  ClassDefDomestic classDefDomestic = (ClassDefDomestic)classDef;
                  classDefDomestic.setSibling((ClassDefLocal)this.getClassDef(super._typeModule));
               }
            }
         }

         this.setClassDef(classDef, ordinal);
      }

      return classDef;
   }

   @Override
   final TypeItem makeTypeItem(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeItem typeItem = this.getTypeItem(ordinal, typeModule.getCount());
      if (typeItem == null) {
         typeItem = new TypeItem(this.getClassDef(typeModule));
         this.setTypeItem(typeItem, ordinal);
      }

      return typeItem;
   }

   @Override
   final TypeList getTypeList(TypeModule typeModule) {
      int ordinal = typeModule.getOrdinal();
      TypeList typeList = this.getTypeList(ordinal, typeModule.getCount());
      if (typeList == null) {
         if (!this.isDefined()) {
            typeList = new TypeList(-1);
         } else {
            typeList = new TypeList(this.makeTypeItem(typeModule));
         }

         this.setTypeList(typeList, ordinal);
      }

      return typeList;
   }

   public final void populate(Compiler compiler) {
      if (this.is(131072)) {
         this._fields = null;
         this._methods = null;
         this._vtable = null;
      } else if (!this.is(Integer.MIN_VALUE)) {
         if (this.isDefined()) {
            this.addModifiers(Integer.MIN_VALUE);
            ClassDefLocal classDef = (ClassDefLocal)this.getClassDef(super._typeModule);
            classDef.setSecureIndex(255);
            DataSection dataSection = super._typeModule.getDataSection();
            if (this._baseClassType != null) {
               classDef.setSuperClass(this._baseClassType.getClassDef(super._typeModule), dataSection);
            } else {
               classDef.setSuperClass(super._typeModule.getNullClassDef(super._typeModule), dataSection);
            }

            if (this._baseInterfaces != null) {
               int n = this._baseInterfaces.length;
               if (n > 0) {
                  classDef.allocateInterfaces(n);

                  for (int i = 0; i < n; i++) {
                     ClassType ifaceClassType = this._baseInterfaces[i];
                     classDef.addInterface(ifaceClassType.getClassDef(super._typeModule), dataSection);
                  }
               }

               this._baseInterfaces = null;
            }

            int staticStart = super._typeModule.allocateStaticData(this._numStatic);
            int numStaticFields = 0;
            int numMemberFields = 0;
            int n = this._numFields;

            for (int i = 0; i < n; i++) {
               Field field = this._fields[i];
               boolean isStatic = field.is(2);
               if (isStatic) {
                  if (field.hasOffset()) {
                     field.setOffset(staticStart + field.getOffset());
                     numStaticFields++;
                  }
               } else {
                  numMemberFields++;
               }

               field.populate(compiler);
            }

            classDef.setStaticStart(staticStart);
            classDef.allocateFieldDefs(numStaticFields, true);
            classDef.allocateFieldDefs(numMemberFields, false);
            n = this._numFields;

            for (int var17 = 0; var17 < n; var17++) {
               Field field = this._fields[var17];
               boolean isStatic = field.is(2);
               if (!isStatic || field.hasOffset()) {
                  FieldDef fieldDef = (FieldDef)field.getMember(compiler, super._typeModule);
                  classDef.addFieldDef(fieldDef, isStatic);
               }
            }

            this._fields = null;
            int numStaticMethods = 0;
            int numVirtualMethods = 0;
            int numNonVirtualMethods = 0;
            n = this._numMethods;

            for (int var18 = 0; var18 < n; var18++) {
               Method method = this._methods[var18];
               if (method.populate(compiler, super._typeModule)) {
                  if (method.is(18)) {
                     numStaticMethods++;
                  } else if (method.isInVtable()) {
                     numVirtualMethods++;
                  } else {
                     numNonVirtualMethods++;
                  }
               } else {
                  this._methods[var18] = null;
               }
            }

            classDef.allocateVirtualRoutines(numVirtualMethods);
            classDef.allocateNonVirtualRoutines(numNonVirtualMethods);
            classDef.allocateStaticRoutines(numStaticMethods);
            boolean foundClinit = false;
            boolean foundInit = false;
            n = this._numMethods;

            for (int var19 = 0; var19 < n; var19++) {
               Method method = this._methods[var19];
               if (method != null) {
                  Routine routine = (Routine)method.getMember(compiler, super._typeModule);
                  if (method.is(1048576)) {
                     if (!foundClinit) {
                        foundClinit = true;
                        classDef.setClinit(routine);
                     } else {
                        classDef.setClinit(classDef.getNullRoutine());
                     }

                     classDef.addStaticRoutine(routine);
                  } else if (method.is(16)) {
                     if (method.getNumParms() == 0) {
                        if (!foundInit) {
                           foundInit = true;
                           classDef.setInit(routine);
                        } else {
                           classDef.setInit(classDef.getNullRoutine());
                        }
                     }

                     classDef.addStaticRoutine(routine);
                  } else if (method.is(18)) {
                     classDef.addStaticRoutine(routine);
                  } else if (method.isInVtable()) {
                     routine.setAddress(method.getAbsoluteOffset());
                     classDef.addVirtualRoutine(routine);
                  } else {
                     classDef.addNonVirtualRoutine(routine);
                  }
               }
            }

            this._methods = null;
            this._vtable = null;
            if (!foundClinit) {
               classDef.setClinit(classDef.getNullRoutine());
            }

            if (!foundInit) {
               classDef.setInit(classDef.getNullRoutine());
            }

            super._typeModule.setMaxTypeListSize(this._maxTypeListSize);
         }
      }
   }

   public final int codfileOrder(ClassType other) {
      int nameCompare = super._typeName.compareTo(other._typeName);
      if (nameCompare == 0) {
         if (this._packageName == null) {
            if (other._packageName != null) {
               return -1;
            }
         } else {
            if (other._packageName == null) {
               return 1;
            }

            nameCompare = this._packageName.compareTo(other._packageName);
         }
      }

      return nameCompare;
   }

   public final int compareTo(ClassType other) {
      int packageCompare = 0;
      if (this._packageName == null) {
         if (other._packageName == null) {
            packageCompare = 0;
         } else {
            packageCompare = -1;
         }
      } else if (other._packageName == null) {
         packageCompare = 1;
      } else {
         packageCompare = this._packageName.compareTo(other._packageName);
      }

      return packageCompare == 0 ? super._typeName.compareTo(other._typeName) : packageCompare;
   }

   @Override
   public final boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if (!(o instanceof ClassType)) {
         return false;
      }

      ClassType other = (ClassType)o;
      return this.compareTo(other) == 0;
   }

   @Override
   public final int hashCode() {
      int hash = super._typeName.hashCode();
      if (this._packageName != null) {
         hash = hash * 31 + this._packageName.hashCode();
      }

      return hash;
   }
}
