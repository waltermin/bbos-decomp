package net.rim.tools.compiler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.rim.tools.compiler.analysis.InstructionCode;
import net.rim.tools.compiler.analysis.InstructionTarget;
import net.rim.tools.compiler.classfile.ByteCodeInstructions;
import net.rim.tools.compiler.types.ArrayType;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.Method;
import net.rim.tools.compiler.types.NameAndType;
import net.rim.tools.compiler.types.Type;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.util.DuplicateException;
import net.rim.tools.compiler.util.FileHelper;
import net.rim.tools.compiler.vm.Constants;

public class GenerateResources implements Constants {
   private Compiler _compiler;
   private JadSupport _jad;
   private Vector _resourceBinaries;
   private boolean _makingMIDlet;
   private String _className;
   private ClassType _resourceClassType;
   private NameAndType _resourceField;
   private NameAndType _propertyField;
   private NameAndType _appIconField;
   private byte[] _appIconData;
   private NameAndType _localVar;
   private Type _intType;
   private Type _byteType;
   private Type _byteArrayType;
   private Vector _parmVector;
   private ClassType _stringClassType;
   private Method _stringEquals;
   private ClassType _integerClassType;
   private Method _integerInit;
   private ClassType _hashtableClassType;
   private Method _hashtableInit;
   private Method _hashtablePut;
   private int _ip;
   private ByteCodeInstructions _block;
   private static String INIT_NAME = "<init>";
   private static String CLINIT_NAME = "<clinit>";
   private static final int METHOD_MAX_STACK = 3;
   private static final int METHOD_MAX_LOCALS = 2;
   private static final int INIT_MAX_STACK = 4;
   private static final int INIT_MAX_LOCALS = 1;
   private static final int CLINIT_MAX_LOCALS = 0;
   private static final int CLINIT_MAX_STACK = 3;
   private static final int POPULATE_MAX_LOCALS = 1;
   private static final int POPULATE_MAX_STACK = 4;
   private static final int MAX_RESOURCE_SLICE = 8192;

   public GenerateResources(Compiler compiler, String moduleName, JadSupport jad, Vector resourceBinaries) {
      this._compiler = compiler;
      this._className = ((StringBuffer)(new Object("com.rim.resources."))).append(moduleName).append("RIMResources").toString();
      this._jad = jad;
      this._resourceBinaries = resourceBinaries;
      this._makingMIDlet = compiler.isMakingMIDlet();
   }

   public String getClassName() {
      return this._className;
   }

   private int longOff(NameAndType field) {
      return field.getSize() == 8 ? 2 : 0;
   }

   private int libOff(Method method) {
      return method.is(131072) ? 1 : 0;
   }

   private int libOff(ClassType classType) {
      return classType.is(131072) ? 1 : 0;
   }

   private ClassType findClassType(String className) throws CompileException {
      ClassType classType = this._compiler.findClassType(className);
      if (classType == null) {
         throw new CompileException(this._className, ((StringBuffer)(new Object("Unable to find type: "))).append(className).toString());
      }

      classType.setReachable(this._compiler, false);
      classType.resolve(this._compiler);
      return classType;
   }

   private Method findMethod(ClassType classType, String methodName, Type type, Vector types) throws CompileException {
      Method method = classType.findMethod(this._compiler, methodName, type, types, false, false);
      if (method == null) {
         throw new CompileException(
            this._className, ((StringBuffer)(new Object("Class: "))).append(classType.getFullName()).append(" has no member: ").append(methodName).toString()
         );
      } else {
         return method;
      }
   }

   private ClassType getStringClassType() {
      if (this._stringClassType == null) {
         this._stringClassType = this.findClassType("java.lang.String");
         this._parmVector.setSize(0);
         this._parmVector.addElement(this._compiler.getObjectClass());
         this._stringEquals = this.findMethod(this._stringClassType, "equals", this._compiler.getBooleanType(), this._parmVector);
      }

      return this._stringClassType;
   }

   private ClassType getIntegerClassType() {
      if (this._integerClassType == null) {
         this._integerClassType = this.findClassType("java.lang.Integer");
         this._parmVector.setSize(0);
         this._parmVector.addElement(this._intType);
         this._integerInit = this.findMethod(this._integerClassType, INIT_NAME, null, this._parmVector);
      }

      return this._integerClassType;
   }

   private ClassType getHashtableClassType() {
      if (this._hashtableClassType == null) {
         this.getStringClassType();
         this._hashtableClassType = this.findClassType("java.util.Hashtable");
         this._parmVector.setSize(0);
         this._parmVector.addElement(this._intType);
         this._hashtableInit = this.findMethod(this._hashtableClassType, INIT_NAME, null, this._parmVector);
         this._parmVector.setSize(0);
         ClassType objectClassType = this._compiler.getObjectClass();
         this._parmVector.addElement(objectClassType);
         this._parmVector.addElement(objectClassType);
         this._hashtablePut = this.findMethod(this._hashtableClassType, "put", objectClassType, this._parmVector);
      }

      return this._hashtableClassType;
   }

   private Method createInit() {
      int modifiers = this._compiler.augmentMethodModifiers(this._resourceClassType, 144);
      Method method = new Method(this._resourceClassType, INIT_NAME, null, 0, modifiers);
      InstructionCode code = new InstructionCode(method, 4, 1, null, null);
      code.setSynthetic();
      ByteCodeInstructions blocks = new ByteCodeInstructions(7);
      this._block = blocks;
      this._ip = 0;
      ClassType baseClassType = this._resourceClassType.getBaseClassType();
      this._parmVector.setSize(0);
      this.getHashtableClassType();
      this._parmVector.addElement(this._hashtableClassType);
      this._parmVector.addElement(this._hashtableClassType);
      this._parmVector.addElement(this._byteArrayType);
      Method baseMethod = this.findMethod(baseClassType, INIT_NAME, null, this._parmVector);
      this._block.addInstruction(this._ip++, 14);
      this._block.addInstruction(this._ip++, 63);
      this._block.addInstructionNameAndType(this._ip++, 109 + this.longOff(this._resourceField), this._resourceClassType, this._resourceField);
      this._block.addInstructionNameAndType(this._ip++, 109 + this.longOff(this._propertyField), this._resourceClassType, this._propertyField);
      if (this._appIconField != null) {
         this._block.addInstructionNameAndType(this._ip++, 109 + this.longOff(this._appIconField), this._resourceClassType, this._appIconField);
      } else {
         this._block.addInstruction(this._ip++, 34);
      }

      this._block.addInstructionNameAndType(this._ip++, 5 + this.libOff(baseMethod), baseClassType, baseMethod);
      this._block.addInstruction(this._ip++, 31);
      this._block = null;
      code.setBlocks(blocks);
      method.setBody(code);
      return method;
   }

   private void createHashtableInit(NameAndType table, int capacity) {
      this.getHashtableClassType();
      this._block.addInstructionType(this._ip++, 184 + this.libOff(this._hashtableClassType), this._hashtableClassType);
      this._block.addInstruction(this._ip++, 207);
      this._block.addInstruction(this._ip++, 36, capacity > 127 ? 127 : capacity);
      this._block.addInstructionNameAndType(this._ip++, 5 + this.libOff(this._hashtableInit), this._hashtableClassType, this._hashtableInit);
      this._block.addInstructionNameAndType(this._ip++, 105 + this.longOff(table), this._resourceClassType, table);
   }

   private Method createClinit() {
      int modifiers = this._compiler.augmentMethodModifiers(this._resourceClassType, 1048578);
      Method method = new Method(this._resourceClassType, CLINIT_NAME, null, 0, modifiers);
      InstructionCode code = new InstructionCode(method, 3, 0, null, null);
      code.setSynthetic();
      ByteCodeInstructions blocks = new ByteCodeInstructions();
      this._block = blocks;
      ClassType baseClassType = this._resourceClassType.getBaseClassType();
      this._ip = 0;
      this._block.addInstruction(this._ip++, 14);
      this._block.addInstructionType(this._ip++, 186 + this.libOff(baseClassType), baseClassType);
      this._block.addInstructionType(this._ip++, 19, this._resourceClassType);
      this._block.addInstruction(this._ip++, 20);
      int num = this._resourceBinaries.size();
      if (num > 0 || this._makingMIDlet) {
         num++;
         this.createHashtableInit(this._resourceField, 8 * num / 3 + 1);
      }

      if (this._makingMIDlet) {
         num = this._jad.size();
         this.createHashtableInit(this._propertyField, 8 * num / 3 + 1);
      }

      code.setBlocks(blocks);
      method.setBody(code);
      return method;
   }

   private void completeClinit() {
      this._block.addInstruction(this._ip++, 32);
      this._block = null;
   }

   private ClassType createPopulateClass(int ordinal) throws DuplicateException {
      String className = ((StringBuffer)(new Object())).append(this._className).append("Populator").append(ordinal).toString();
      ClassType popClass = this._compiler.findClassType(className);
      if (popClass.isDefined()) {
         throw new DuplicateException(null, className, popClass.getFullName());
      }

      popClass.setDefined();
      popClass.addModifiers(this._compiler.augmentClassModifiers(64));
      popClass.setBaseClass(this._compiler.getObjectClass());
      popClass.setReachable(this._compiler, false);
      return popClass;
   }

   private Method createPopulateMethod(ClassType popClass) {
      int modifiers = this._compiler.augmentMethodModifiers(popClass, 2);
      Method method = new Method(popClass, "populate", null, 1, modifiers);
      this.getHashtableClassType();
      method.addParameter(0, "localZero", this._hashtableClassType);
      method.setReachable(this._compiler);
      InstructionCode code = new InstructionCode(method, 4, 1, null, null);
      code.setSynthetic();
      ByteCodeInstructions blocks = new ByteCodeInstructions();
      this._block = blocks;
      this._ip = 0;
      this._block.addInstruction(this._ip++, 14);
      code.setBlocks(blocks);
      method.setBody(code);
      return method;
   }

   private void createPopulateInvoke(Method method, NameAndType parm) {
      if (parm.is(1024)) {
         this._block.addInstruction(this._ip++, 63 + parm.getOffset());
      } else {
         this._block.addInstructionNameAndType(this._ip++, 109 + this.longOff(parm), this._resourceClassType, parm);
      }

      ClassType classType = method.getClassType();
      this._block.addInstructionNameAndType(this._ip++, 7 + this.libOff(method), classType, method);
   }

   private void createHashtableStore(ClassType cls, NameAndType table, String name, NameAndType var) {
      cls.addDataWeight(name.length() + 4);
      this.getHashtableClassType();
      if (table.is(1024)) {
         this._block.addInstruction(this._ip++, 63 + table.getOffset());
      } else {
         this._block.addInstructionNameAndType(this._ip++, 109 + this.longOff(table), this._resourceClassType, table);
      }

      this._block.addInstructionString(this._ip++, 40, FileHelper.fixSlashes(name));
      if (var.is(1024)) {
         this._block.addInstruction(this._ip++, 63 + var.getOffset());
      } else {
         this._block.addInstructionNameAndType(this._ip++, 109 + this.longOff(var), this._resourceClassType, var);
      }

      this._block.addInstructionNameAndType(this._ip++, 1, this._hashtableClassType, this._hashtablePut);
      this._block.addInstruction(this._ip++, 205);
   }

   private NameAndType getLocalVar(ClassType cls, InstructionCode code, Type type) {
      if (this._localVar != null && this._localVar.getClassType() == cls) {
         this._localVar.setType(type);
      } else {
         this._localVar = new NameAndType("_local", type, cls, 1024, code.addLocal());
      }

      return this._localVar;
   }

   private NameAndType createArrayField(String name, Type type) {
      int modifiers = this._compiler.augmentFieldModifiers(this._resourceClassType, 130);
      return this._resourceClassType.addData(this._compiler, name, type, modifiers, null);
   }

   private NameAndType createHashtableField(String name) {
      int modifiers = this._compiler.augmentFieldModifiers(this._resourceClassType, 130);
      return this._resourceClassType.addData(this._compiler, name, this.getHashtableClassType(), modifiers, null);
   }

   private void initInteger(NameAndType var, int value) {
      this.getIntegerClassType();
      this._block.addInstructionType(this._ip++, 184 + this.libOff(this._integerClassType), this._integerClassType);
      this._block.addInstruction(this._ip++, 207);
      this._block.addInstruction(this._ip++, 36, value);
      this._block.addInstructionNameAndType(this._ip++, 5 + this.libOff(this._integerInit), this._integerClassType, this._integerInit);
      if (var.is(1024)) {
         this._block.addInstruction(this._ip++, 85 + var.getOffset());
      } else {
         this._block.addInstructionNameAndType(this._ip++, 105 + this.longOff(var), this._resourceClassType, var);
      }
   }

   private void initArray(ClassType cls, NameAndType var, byte[] values) {
      cls.addDataWeight(values.length + 4);
      this._compiler.checkBinaryForExport(((StringBuffer)(new Object())).append(this._className).append('.').append(var.getName()).toString(), values);
      int typeId = ((ArrayType)var.getType()).getMostBaseType().getTypeId();
      this._block.addInstructionBytes(this._ip++, 45, typeId, values);
      if (var.is(1024)) {
         this._block.addInstruction(this._ip++, 85 + var.getOffset());
      } else {
         this._block.addInstructionNameAndType(this._ip++, 105 + this.longOff(var), this._resourceClassType, var);
      }
   }

   private void initByteArrayValue(ClassType cls, NameAndType var, int value) {
      byte[] values = new byte[]{(byte)value};
      this.initArray(cls, var, values);
   }

   private void generateByteArray(DataOutputStream dout, byte[] data) {
      if (data == null) {
         dout.writeShort(0);
      } else {
         dout.writeShort(data.length);
         dout.write(data);
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void createAppIconField() throws CompileException {
      try {
         int num = this._jad.getNumApplets();
         Applet applet = null;
         ByteArrayOutputStream bout = null;
         DataOutputStream dout = null;
         bout = (ByteArrayOutputStream)(new Object());
         dout = (DataOutputStream)(new Object(bout));
         boolean isEmpty = true;

         for (int i = 0; i < num; i++) {
            applet = this._jad.getApplet(i);
            int size = 0;
            int numIcons = applet.getNumIcons();

            for (int j = 0; j < numIcons; j++) {
               ImageFile icon = applet.getIcon(j);
               if (icon != null) {
                  byte[] data = icon.getData();
                  if (data != null) {
                     size += 2 + data.length;
                  }
               }
            }

            dout.writeShort(size);
            if (size != 0) {
               isEmpty = false;
            }

            int iconOrdinal = 0;

            for (int j = 0; j < numIcons; j++) {
               ImageFile icon = applet.getIcon(j);
               if (icon != null) {
                  byte[] data = icon.getData();
                  if (data != null) {
                     icon.setOrdinal(iconOrdinal++);
                     this.generateByteArray(dout, data);
                  }
               }
            }
         }

         if (isEmpty) {
            this._appIconField = null;
            this._appIconData = null;
         } else {
            this._appIconField = this.createArrayField("_appIcons", this._byteArrayType);
            this._appIconData = bout.toByteArray();
         }
      } catch (Throwable var14) {
         throw new CompileException(this._className, e.toString());
      }
   }

   private void createAppletData(ClassType cls) {
      int num = this._jad.getNumApplets();
      Applet applet = null;
      NameAndType var = null;
      ByteArrayOutputStream bout = null;
      DataOutputStream dout = null;
      ByteArrayOutputStream var14 = new Object();
      dout = (DataOutputStream)(new Object((OutputStream)var14));
      boolean isEmpty = true;

      for (int i = 0; i < num; i++) {
         applet = this._jad.getApplet(i);
         String name = applet.getName();
         if (name == null) {
            dout.writeShort(0);
         } else {
            dout.writeUTF(name);
            isEmpty = false;
         }
      }

      dout.close();
      if (!isEmpty) {
         var = this.createArrayField("_appNames", this._byteArrayType);
         this.initArray(cls, var, ((ByteArrayOutputStream)var14).toByteArray());
      }

      dout.close();
      if (this._appIconField != null) {
         this.initArray(cls, this._appIconField, this._appIconData);
      }

      var14 = new Object();
      dout = (DataOutputStream)(new Object((OutputStream)var14));
      isEmpty = true;

      for (int i = 0; i < num; i++) {
         applet = this._jad.getApplet(i);
         String className = applet.getClassName();
         if (className == null) {
            dout.writeShort(0);
         } else {
            dout.writeUTF(className);
            isEmpty = false;
         }
      }

      dout.close();
      if (!isEmpty) {
         var = this.createArrayField("_appArgs", this._byteArrayType);
         this.initArray(cls, var, ((ByteArrayOutputStream)var14).toByteArray());
      }
   }

   private NameAndType createAppInteger(ClassType cls, InstructionCode code, int ordinal) {
      NameAndType var = this.getLocalVar(cls, code, this._intType);
      this.initInteger(var, ordinal);
      return var;
   }

   private NameAndType createAppDataItem(ClassType cls, InstructionCode code, String id, Object objData) {
      NameAndType var;
      if (id == null) {
         var = this.getLocalVar(cls, code, this._byteArrayType);
      } else {
         var = this.createArrayField(id, this._byteArrayType);
      }

      ByteArrayOutputStream bout = (ByteArrayOutputStream)(new Object());
      DataOutputStream dout = (DataOutputStream)(new Object(bout));
      if (!(objData instanceof byte[])) {
         if (objData != null) {
            dout.writeUTF(objData.toString());
         }
      } else {
         byte[] data = (byte[])objData;
         dout.write(data);
      }

      dout.close();
      this.initArray(cls, var, bout.toByteArray());
      return var;
   }

   private void createAppValue(ClassType cls, InstructionCode code, String id, String tagPrefix, int type) {
      ByteArrayOutputStream bout = (ByteArrayOutputStream)(new Object());
      DataOutputStream dout = (DataOutputStream)(new Object(bout));
      int num = this._jad.getNumApplets();
      boolean isEmpty = true;

      for (int i = 0; i < num; i++) {
         String tag = ((StringBuffer)(new Object())).append(tagPrefix).append(i + 1).toString();
         String value = this._jad.getProperty(tag);
         switch (type) {
            case -1:
            case 4:
               break;
            case 0:
            default:
               if (value == null) {
                  dout.writeByte(0);
               } else {
                  dout.writeByte(Integer.parseInt(value));
                  isEmpty = false;
               }
               break;
            case 1:
               if (value == null) {
                  dout.writeShort(0);
               } else {
                  dout.writeShort(Integer.parseInt(value));
                  isEmpty = false;
               }
               break;
            case 2:
               if (value == null) {
                  dout.writeInt(0);
               } else {
                  dout.writeInt(Integer.parseInt(value));
                  isEmpty = false;
               }
               break;
            case 3:
               if (value == null) {
                  dout.writeLong(0);
               } else {
                  dout.writeLong(Long.parseLong(value));
                  isEmpty = false;
               }
               break;
            case 5:
               if (value == null) {
                  dout.writeShort(0);
               } else {
                  dout.writeUTF(value);
                  isEmpty = false;
               }
         }
      }

      dout.close();
      if (!isEmpty) {
         NameAndType var;
         if (id == null) {
            var = this.getLocalVar(cls, code, this._byteArrayType);
         } else {
            var = this.createArrayField(id, this._byteArrayType);
         }

         this.initArray(cls, var, bout.toByteArray());
      }
   }

   private void instantiateMIDletIf(String className, Object target) {
      this._resourceClassType.addDataWeight(className.length() + 4);
      this.getStringClassType();
      this._block.addInstruction(this._ip++, 64);
      this._block.addInstructionString(this._ip++, 40, className);
      this._block.addInstructionNameAndType(this._ip++, 1, this._stringClassType, this._stringEquals);
      this._block.addInstructionBranch(this._ip++, 147);
      this._block.addBranchTarget((InstructionTarget)target);
   }

   private void instantiateMIDletNew(String className) {
      ClassType midletClassType = this.findClassType(className);
      this._parmVector.setSize(0);
      Method midletInit = this.findMethod(midletClassType, INIT_NAME, null, this._parmVector);
      this._block.addInstructionType(this._ip++, 184 + this.libOff(midletClassType), midletClassType);
      this._block.addInstruction(this._ip++, 207);
      this._block.addInstructionNameAndType(this._ip++, 5 + this.libOff(midletInit), midletClassType, midletInit);
      this._block.addInstruction(this._ip++, 27);
   }

   private void instantiateMIDletThrow() {
      ClassType exceptionClassType = this.findClassType("java.lang.IllegalArgumentException");
      this._parmVector.setSize(0);
      this._parmVector.addElement(this.getStringClassType());
      Method exInit = this.findMethod(exceptionClassType, INIT_NAME, null, this._parmVector);
      this._block.addInstructionType(this._ip++, 184 + this.libOff(exceptionClassType), exceptionClassType);
      this._block.addInstruction(this._ip++, 207);
      this._block.addInstruction(this._ip++, 64);
      this._block.addInstructionNameAndType(this._ip++, 5 + this.libOff(exInit), exceptionClassType, exInit);
      this._block.addInstruction(this._ip++, 188);
   }

   private Method createInstantiateMIDlet() {
      int modifiers = this._compiler.augmentMethodModifiers(this._resourceClassType, 128);
      Method method = new Method(this._resourceClassType, "instantiateMIDlet", this._compiler.getObjectClass(), 1, modifiers);
      method.addParameter(0, "name", this.getStringClassType());
      InstructionCode code = new InstructionCode(method, 3, 2, null, null);
      code.setSynthetic();
      this._ip = 0;
      ByteCodeInstructions blocks = new ByteCodeInstructions();
      this._block = blocks;
      this._block.addInstruction(this._ip++, 14);
      int num = this._jad.getNumApplets();

      for (int i = 0; i < num; i++) {
         Applet applet = this._jad.getApplet(i);
         String className = applet.getClassName();
         if (className != null && className.length() > 0) {
            ClassType classType = this.findClassType(className);
            if (classType.is(128)) {
               int tgtIp = this._ip + 10;
               InstructionTarget target = this._block.plantTarget(tgtIp, false);
               this.instantiateMIDletIf(className, target);
               this.instantiateMIDletNew(className);
               this._ip = tgtIp + 1;
            }
         }
      }

      this.instantiateMIDletThrow();
      this._block = null;
      code.setBlocks(blocks);
      method.setBody(code);
      return method;
   }

   private void sliceResourceBinaries() {
      int num = this._resourceBinaries.size();

      for (int i = num - 1; i >= 0; i--) {
         ResourceFile rf = (ResourceFile)this._resourceBinaries.elementAt(i);
         if (!rf.isEmpty()) {
            if (!this._makingMIDlet && rf instanceof ImageFile) {
               ImageFile image = (ImageFile)rf;
               if (image.isIcon() && image.getOrdinal() != -1) {
                  continue;
               }
            }

            byte[] data = rf.getData();
            int size = data.length;
            if (size > 61440) {
               String relativeName = rf.getRelativeName();
               int offset = 0;
               int length = this._compiler.getSliceSize();

               while (offset < size) {
                  byte[] slice = new byte[length];
                  System.arraycopy(data, offset, slice, 0, length);
                  if (offset == 0) {
                     rf.resetData(slice);
                  }

                  StringBuffer nameBuffer = (StringBuffer)(new Object());
                  nameBuffer.append("__").append(relativeName).append('@').append(offset);
                  this._resourceBinaries.addElement(new ResourceFile(nameBuffer.toString(), slice, true));
                  offset += length;
                  if (offset + length > size) {
                     length = size - offset;
                  }
               }
            }
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public ClassType generateResourceClass(String extensions, String languages) throws CompileException, DuplicateException {
      this._parmVector = (Vector)(new Object());
      this._intType = this._compiler.getIntType();
      this._byteType = this._compiler.getByteType();
      this._byteArrayType = this._byteType.getArrayType();
      this._resourceClassType = this._compiler.findClassType(this._className);
      if (this._resourceClassType.isDefined()) {
         throw new DuplicateException(null, this._className, this._resourceClassType.getFullName());
      }

      this._resourceClassType.setDefined();
      this._resourceClassType.addModifiers(this._compiler.augmentClassModifiers(192));
      ClassType baseClassType = this.findClassType("net.rim.device.resources.Resource");
      this._resourceClassType.setBaseClass(baseClassType);
      this._resourceClassType.setReachable(this._compiler, false);
      this._resourceField = this.createHashtableField("_resources");
      this._propertyField = this.createHashtableField("_properties");
      if (this._jad.getNumApplets() > 0) {
         this.createAppIconField();
         if (this._makingMIDlet) {
            this._resourceClassType.allocateMethods(3);
            this._resourceClassType.addMethod(this._compiler, this.createInstantiateMIDlet());
         } else {
            this._resourceClassType.allocateMethods(2);
         }
      }

      this.sliceResourceBinaries();
      this._resourceClassType.addMethod(this._compiler, this.createInit());
      Method clinit = this.createClinit();
      this._resourceClassType.addMethod(this._compiler, clinit);
      InstructionCode clinitCode = clinit.getBody();
      ByteCodeInstructions clinitBlock = this._block;
      NameAndType numApplets = this.createArrayField("_appCount", this._byteArrayType);
      this.initByteArrayValue(this._resourceClassType, numApplets, this._jad.getNumApplets());
      byte[] extensionBytes = extensions.getBytes();

      label437:
      try {
         extensionBytes = extensions.getBytes("UTF-8");
      } finally {
         break label437;
      }

      NameAndType resourceExtensions = this.createArrayField("_resourceExtensions", this._byteArrayType);
      this.initArray(this._resourceClassType, resourceExtensions, extensionBytes);
      if (languages != null && languages.length() > 0) {
         byte[] languageBytes = languages.getBytes();

         label430:
         try {
            languageBytes = languages.getBytes("UTF-8");
         } finally {
            break label430;
         }

         NameAndType languageResources = this.createArrayField("_languageResources", this._byteArrayType);
         this.initArray(this._resourceClassType, languageResources, languageBytes);
      }

      try {
         if (this._jad.getNumApplets() > 0) {
            this.createAppletData(this._resourceClassType);
            int num = ResourceIds.ordinalPrefixIds.length;

            for (int i = 0; i < num; i++) {
               String prefix = ResourceIds.ordinalPrefixIds[i];
               if (prefix != null) {
                  this.createAppValue(this._resourceClassType, clinitCode, prefix, ResourceIds.ordinalPrefix[i], ResourceIds.ordinalTypes[i]);
               }
            }
         }

         Enumeration propertyTags = this._jad.keys();

         while (propertyTags.hasMoreElements()) {
            String tag = (String)propertyTags.nextElement();
            String id = ResourceIds.getId(tag);
            if (this._makingMIDlet) {
               NameAndType var = this.createAppDataItem(this._resourceClassType, clinitCode, id, this._jad.getProperty(tag));
               this.createHashtableStore(this._resourceClassType, this._propertyField, tag, var);
            } else if (id != null
               && !id.startsWith(ResourceIds.midletPrefix)
               && !id.equals(ResourceIds.manifestVersionIds[0])
               && !id.equals(ResourceIds.jadRequiredIds[0])) {
               this.createAppDataItem(this._resourceClassType, clinitCode, id, this._jad.getProperty(tag));
            }
         }

         if (this._makingMIDlet) {
            ResourceFile manifest = this._jad.getManifest();
            NameAndType var = this.createAppDataItem(this._resourceClassType, clinitCode, "_manifest", manifest.getData());
            this.createHashtableStore(this._resourceClassType, this._resourceField, manifest.getRelativeName(), var);
         }

         int dataFull = this._compiler.getDataFull() - 4096;
         boolean shortName = false;
         int num = this._resourceBinaries.size();
         if (num > 0) {
            this._block = null;
            Vector popMethods = (Vector)(new Object());
            ClassType popClass = null;
            Method popMethod = null;
            InstructionCode popCode = null;
            NameAndType localZero = null;
            boolean brittle = false;

            for (int i = 0; i < num; i++) {
               if (popMethod == null) {
                  this._localVar = null;
                  popClass = this.createPopulateClass(popMethods.size());
                  popMethod = this.createPopulateMethod(popClass);
                  popClass.addMethod(this._compiler, popMethod);
                  popMethods.addElement(popMethod);
                  popCode = popMethod.getBody();
                  localZero = popMethod.getParm(0);
               }

               ResourceFile rf = (ResourceFile)this._resourceBinaries.elementAt(i);
               if (!rf.isEmpty()) {
                  NameAndType var = null;
                  if (!this._makingMIDlet && rf instanceof ImageFile) {
                     ImageFile image = (ImageFile)rf;
                     if (image.isIcon() && image.getOrdinal() != -1) {
                        var = this.createAppInteger(popClass, popCode, image.getOrdinal());
                     }
                  }

                  if (var == null) {
                     var = this.createAppDataItem(popClass, popCode, null, rf.getData());
                  }

                  String relativeName = rf.getRelativeName();
                  String name = FileHelper.removePathPrefix(relativeName);
                  if (shortName && !this._makingMIDlet && !relativeName.equals(name)) {
                     if (rf.isSlice()) {
                        name = ((StringBuffer)(new Object("__"))).append(name).toString();
                     }

                     this.createHashtableStore(popClass, localZero, name, var);
                     if (!brittle) {
                        this.createHashtableStore(popClass, localZero, relativeName, var);
                     }
                  } else {
                     this.createHashtableStore(popClass, localZero, relativeName, var);
                  }
               }

               if (i == num - 1 || popClass.getDataWeight() + ((ResourceFile)this._resourceBinaries.elementAt(i + 1)).getData().length > dataFull) {
                  if (this._block != null) {
                     this._block.addInstruction(this._ip++, 31);
                  }

                  popMethod = null;
                  this._block = null;
               }
            }

            this._block = clinitBlock;
            num = popMethods.size();

            for (int var39 = 0; var39 < num; var39++) {
               popMethod = (Method)popMethods.elementAt(var39);
               this.createPopulateInvoke(popMethod, this._resourceField);
            }
         }
      } catch (Throwable var34) {
         throw new CompileException(this._className, e.toString());
      }

      this.completeClinit();
      this._resourceClassType.resolve(this._compiler);
      return this._resourceClassType;
   }
}
