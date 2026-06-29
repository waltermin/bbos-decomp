package net.rim.tools.compiler;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Vector;
import net.rim.device.api.system.CodeModuleManager;
import net.rim.device.internal.system.MIDletSecurity;
import net.rim.tools.compiler.analysis.InstructionCode;
import net.rim.tools.compiler.classfile.Attribute;
import net.rim.tools.compiler.classfile.AttributeCode;
import net.rim.tools.compiler.classfile.AttributeList;
import net.rim.tools.compiler.classfile.AttributeStackMap;
import net.rim.tools.compiler.classfile.AttributeStackMapTable;
import net.rim.tools.compiler.classfile.Classfile;
import net.rim.tools.compiler.classfile.ClassfileExceptionHandler;
import net.rim.tools.compiler.classfile.ClassfileField;
import net.rim.tools.compiler.classfile.ClassfileMethod;
import net.rim.tools.compiler.classfile.TypeDescriptor;
import net.rim.tools.compiler.codfile.Bytes;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.Codfile;
import net.rim.tools.compiler.codfile.DataBytes;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.ExportedData;
import net.rim.tools.compiler.codfile.TypeList;
import net.rim.tools.compiler.exec.CharacterHelper;
import net.rim.tools.compiler.io.Diagnose;
import net.rim.tools.compiler.io.DigestInputStream;
import net.rim.tools.compiler.io.StructuredInputStream;
import net.rim.tools.compiler.types.BaseType;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.Constant;
import net.rim.tools.compiler.types.Field;
import net.rim.tools.compiler.types.Method;
import net.rim.tools.compiler.types.Modifier;
import net.rim.tools.compiler.types.NameAndType;
import net.rim.tools.compiler.types.NullType;
import net.rim.tools.compiler.types.Type;
import net.rim.tools.compiler.types.TypeModule;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.util.CompilerProperties;
import net.rim.tools.compiler.util.DuplicateException;
import net.rim.tools.compiler.util.ExecutionTimer;
import net.rim.tools.compiler.util.Exported;
import net.rim.tools.compiler.util.FileHelper;
import net.rim.tools.compiler.util.StringHelper;
import net.rim.tools.compiler.vm.Optimization;
import net.rim.tools.jar.JarEntry;
import net.rim.tools.jar.JarInputStream;
import net.rim.tools.jar.Manifest;

public class Compiler implements Optimization {
   private NullType _nullType;
   private BaseType _voidType;
   private BaseType _booleanType;
   private BaseType _byteType;
   private BaseType _shortType;
   private BaseType _charType;
   private BaseType _intType;
   private BaseType _longType;
   private BaseType _floatType;
   private BaseType _doubleType;
   private int _timeStamp;
   private int _optimization;
   private boolean _traceback;
   private boolean _preverified;
   private boolean _warning;
   private boolean _optimizePackage;
   private boolean _makingMIDlet;
   private boolean _noVerifyErr;
   private boolean _inclusive;
   private Vector _typeModules;
   private Hashtable _classes;
   private Vector _classesReferenced = (Vector)(new Object());
   private int _classesReferencedIndex;
   private Vector _classTypesUsed = (Vector)(new Object());
   private Vector _methodsUsed = (Vector)(new Object());
   private int _methodsUsedIndex;
   private Vector _potentialMIDlets;
   private Vector _timers;
   private CompilerProperties _properties;
   private Vector _finalStatics;
   private Vector _exportedStaticMethods;
   private Vector _exportedStaticData;
   private Vector _exportedFields;
   private Vector _exportedStrings;
   private Vector _objectClassVTable;
   private ClassType _objectClass;
   private ClassType _stringClass;
   private ClassType _stringBufferClass;
   private JadSupport _jad;
   private Vector _jarFiles;
   private Vector _resourceBinaries;
   private String _packageName;
   private String _codeName;
   private String _moduleName;
   private String _moduleVersion;
   private ClassType _resourceClassType;
   private int _codeFull;
   private int _dataFull;
   private int _vtableFull;
   private int _fieldFull;
   private int _sliceSize;
   private Host _host;
   private Vector _objectClassMethods;
   private byte[] _midletPolicy;
   private byte[] _signerCertEncoding;
   private static final int CODE_FULL = 63488;
   private static final int DATA_FULL = 61440;
   private static final int VTABLE_FULL = 61440;
   private static final int FIELD_FULL = 61440;
   private static final int SLICE_SIZE = 8192;
   private static final int MAX_STACK = 8000;
   private static final int MAX_LOCALS = 8000;
   private static final int FIXED_TIMESTAMP = 961042278;
   public static final int VERBOSITY_QUIET = 0;
   public static final int VERBOSITY_NORMAL = 1;
   public static final int VERBOSITY_NOISY = 2;
   public static int _verbosity;
   public static final String rapc_jarFiles = "rapc_jarFiles";
   public static final String rapc_resourceBinaries = "rapc_resourceBinaries";
   public static final String rapc_jad = "jad";
   public static final String rapc_rapc = "rapc";
   public static final String underscore = "_";
   public static final int RESULT_SUCCESS = 0;
   public static final int RESULT_ERROR = -1;
   public static final int RESULT_FAILURE = 97;
   public static final int RESULT_FAILURE_JAR_SIZE = 904;
   public static final int RESULT_FAILURE_ATTRIBUTE = 905;
   public static final int RESULT_FAILURE_DESCRIPTOR = 906;
   public static final int RESULT_FAILURE_JAR_BAD = 907;
   public static final int RESULT_FAILURE_BAD_CONF_PROF = 908;
   public static final int RESULT_FAILURE_AUTHENTICATION = 909;
   public static final int RESULT_FAILURE_AUTHORIZATION = 910;
   private static Vector _parmTypes = (Vector)(new Object());
   private static final boolean _convertPNG = false;

   public Compiler(Object host, CompilerProperties properties) {
      this._optimization = 11007;
      this._preverified = true;
      this._nullType = new NullType();
      this._voidType = new BaseType("void", 0, 10);
      this._booleanType = new BaseType("boolean", 1, 1);
      this._byteType = new BaseType("byte", 1, 2);
      this._shortType = new BaseType("short", 2, 4);
      this._charType = new BaseType("char", 2, 3);
      this._intType = new BaseType("int", 4, 5);
      this._longType = new BaseType("long", 8, 6);
      this._floatType = new BaseType("float", 4, 11);
      this._doubleType = new BaseType("double", 8, 12);
      this._host = (Host)host;
      this._typeModules = (Vector)(new Object());
      this._classes = (Hashtable)(new Object(64));
      this._properties = properties;
      this._jarFiles = properties.getVector("rapc_jarFiles");
      this._resourceBinaries = properties.getVector("rapc_resourceBinaries");
      this._codeName = this._properties.getQuotedProperty("codename");
      if (this._codeName == null) {
         this._codeName = "_";
         this._moduleName = "_";
      } else {
         this._moduleName = this.validateModuleName(FileHelper.removePathPrefix(this._codeName));
      }

      if (this._properties.getProperty("midlet") != null) {
         this._makingMIDlet = true;
      }

      if (this._properties.getProperty("noverifyerr") != null) {
         this._noVerifyErr = true;
      }

      this._timeStamp = 961042278;
      this._traceback = false;
      _verbosity = 1;
      if (this._properties.getProperty("verbose") != null) {
         _verbosity = 2;
         this._traceback = true;
      }

      if (this._properties.getProperty("traceback") != null) {
         this._traceback = true;
      }

      if (this._properties.getProperty("quiet") != null) {
         _verbosity = 0;
      }

      if (this._properties.getProperty("VERBOSE") != null) {
         _verbosity = 2;
         this._traceback = true;
      }

      if (this._properties.getProperty("warning") != null) {
         this._warning = true;
      }

      if (this._properties.getProperty("inclusive") != null) {
         this._inclusive = true;
      }

      this.parseOptimizations("optimize", true);
      this.parseOptimizations("nooptimize", false);
      if (this._properties.getProperty("optimizepackage") != null) {
         this._optimizePackage = true;
      }

      if (this._properties.getProperty("nopreverified") != null) {
         this._preverified = false;
      }

      this._codeFull = 63488;
      String tmp = this._properties.getProperty("codefull");
      if (tmp != null) {
         label313:
         try {
            this._codeFull = Integer.parseInt(tmp);
         } finally {
            break label313;
         }
      }

      this._dataFull = 61440;
      tmp = this._properties.getProperty("datafull");
      if (tmp != null) {
         label309:
         try {
            this._dataFull = Integer.parseInt(tmp);
         } finally {
            break label309;
         }
      }

      this._vtableFull = 61440;
      tmp = this._properties.getProperty("vtablefull");
      if (tmp != null) {
         label305:
         try {
            this._vtableFull = Integer.parseInt(tmp);
         } finally {
            break label305;
         }
      }

      this._fieldFull = 61440;
      tmp = this._properties.getProperty("fieldfull");
      if (tmp != null) {
         label301:
         try {
            this._fieldFull = Integer.parseInt(tmp);
         } finally {
            break label301;
         }
      }

      this._sliceSize = 8192;
      tmp = this._properties.getProperty("slicesize");
      if (tmp != null) {
         try {
            this._sliceSize = Integer.parseInt(tmp);
         } finally {
            return;
         }
      }
   }

   private String validateModuleName(String name) {
      int n = name.length();
      StringBuffer buf = (StringBuffer)(new Object(n + 1));
      char ch = name.charAt(0);
      if (!CharacterHelper.isJavaIdentifierStart(ch)) {
         buf.append('_');
      }

      for (int i = 0; i < n; i++) {
         ch = name.charAt(i);
         if (CharacterHelper.isJavaIdentifierPart(ch)) {
            buf.append(ch);
         } else {
            buf.append('$');
            buf.append(Integer.toHexString(ch));
         }
      }

      if (buf.length() > 124) {
         buf.setLength(124);
      }

      return buf.toString();
   }

   public final boolean isPreverified() {
      return this._preverified;
   }

   public final void generateWarning(boolean possibleError, String fileName, String message) {
      if (fileName != null) {
         Diagnose.out.print(((StringBuffer)(new Object())).append(fileName).append(": ").toString());
      }

      Diagnose.out.println(((StringBuffer)(new Object("Warning!: "))).append(message).toString());
   }

   public final boolean isOptimizePackage() {
      return this._optimizePackage;
   }

   public final boolean isMakingMIDlet() {
      return this._makingMIDlet;
   }

   public final boolean getTraceback() {
      return this._traceback;
   }

   public final int getOptimization() {
      return this._optimization;
   }

   public final void referenceClass(ClassType classType) {
      Vector v = this._classesReferenced;
      int num = v.size();

      for (int i = this._classesReferencedIndex; i < num; i++) {
         if (classType.compareTo((ClassType)v.elementAt(i)) < 0) {
            v.insertElementAt(classType, i);
            return;
         }
      }

      v.addElement(classType);
   }

   public final void useClassType(ClassType classType) {
      Vector v = this._classTypesUsed;
      int num = v.size();

      for (int i = 0; i < num; i++) {
         if (classType.compareTo((ClassType)v.elementAt(i)) < 0) {
            v.insertElementAt(classType, i);
            return;
         }
      }

      v.addElement(classType);
   }

   public final void useMethod(Method method) {
      Vector v = this._methodsUsed;
      int num = v.size();

      for (int i = this._methodsUsedIndex; i < num; i++) {
         if (method.getClassType().compareTo(((Method)v.elementAt(i)).getClassType()) < 0) {
            v.insertElementAt(method, i);
            return;
         }
      }

      v.addElement(method);
   }

   public final void addPotentialMIDlet(ClassType classType) {
      if (this._makingMIDlet) {
         if (this._potentialMIDlets == null) {
            this._potentialMIDlets = (Vector)(new Object());
         }

         this._potentialMIDlets.addElement(classType);
      }
   }

   public final Type getNullType() {
      return this._nullType;
   }

   public final Type getVoidType() {
      return this._voidType;
   }

   public final Type getBooleanType() {
      return this._booleanType;
   }

   public final Type getByteType() {
      return this._byteType;
   }

   public final Type getCharType() {
      return this._charType;
   }

   public final Type getShortType() {
      return this._shortType;
   }

   public final Type getIntType() {
      return this._intType;
   }

   public final Type getLongType() {
      return this._longType;
   }

   public final Type getFloatType() {
      return this._floatType;
   }

   public final Type getDoubleType() {
      return this._doubleType;
   }

   public final ClassType getObjectClass() {
      return this._objectClass;
   }

   public final ClassType getStringClass() {
      return this._stringClass;
   }

   public final ClassType getStringBufferClass() {
      return this._stringBufferClass;
   }

   public final int augmentFieldModifiers(ClassType classType, int modifiers) {
      if (classType.is(131072)) {
         modifiers |= 131072;
      }

      if (classType.is(524288)) {
         modifiers |= 524288;
      }

      if ((modifiers & 2) == 0) {
         modifiers |= 4;
      }

      return modifiers;
   }

   public final int augmentMethodModifiers(ClassType classType, int modifiers) {
      if (classType.is(131072)) {
         modifiers |= 131072;
      }

      if (classType.is(524288)) {
         modifiers |= 524288;
      }

      if ((modifiers & 16) == 0 && classType.is(64)) {
         modifiers |= 64;
      }

      return modifiers | 8;
   }

   public final int augmentClassModifiers(int modifiers) {
      if (this._inclusive) {
         modifiers |= 524288;
      }

      return modifiers;
   }

   private void setJad(JadSupport jad) {
      this._jad = jad;
   }

   public final boolean checkStaticMethodForExport(String name, Method method) {
      int location = this._exportedStaticMethods.indexOf(name);
      if (location == -1) {
         return false;
      }

      Object obj = this._exportedStaticMethods.elementAt(location);
      if (!(obj instanceof Object)) {
         return false;
      }

      this._exportedStaticMethods.setElementAt(new Exported(name, method), location);
      return true;
   }

   public final boolean checkStaticDataForExport(String name, ClassType classType, int index) {
      int location = this._exportedStaticData.indexOf(name);
      if (location == -1) {
         return false;
      }

      Object obj = this._exportedStaticData.elementAt(location);
      if (!(obj instanceof Object)) {
         return false;
      }

      this._exportedStaticData.setElementAt(new Exported(name, classType, index), location);
      return true;
   }

   public final boolean checkFieldForExport(String name, ClassType classType, int index) {
      int location = this._exportedFields.indexOf(name);
      if (location == -1) {
         return false;
      }

      Object obj = this._exportedFields.elementAt(location);
      if (!(obj instanceof Object)) {
         return false;
      }

      this._exportedFields.setElementAt(new Exported(name, classType, index), location);
      return true;
   }

   public final boolean checkBinaryForExport(String name, byte[] data) {
      int location = this._exportedStrings.indexOf(name);
      if (location == -1) {
         return false;
      }

      Object obj = this._exportedStrings.elementAt(location);
      if (!(obj instanceof Object)) {
         return false;
      }

      this._exportedStrings.setElementAt(new Exported(name, data), location);
      return true;
   }

   public Vector getObjectClassVTable() {
      return this._objectClassVTable;
   }

   public Host getHost() {
      return this._host;
   }

   public Vector getObjectClassMethods() {
      return this._objectClassMethods;
   }

   private int optToMask(String opt) {
      if (opt.equals("nop")) {
         return 8;
      } else if (opt.equals("arrayinit")) {
         return 4;
      } else if (opt.equals("strarrayinit")) {
         return 8192;
      } else if (opt.equals("deadcode")) {
         return 1;
      } else if (opt.equals("checkcast")) {
         return 2;
      } else if (opt.equals("trivial")) {
         return 16;
      } else if (opt.equals("jump")) {
         return 32;
      } else if (opt.equals("accessor")) {
         return 64;
      } else if (opt.equals("mutator")) {
         return 128;
      } else if (opt.equals("pushpop")) {
         return 256;
      } else if (opt.equals("useless_case")) {
         return 512;
      } else if (opt.equals("inner_accessor")) {
         return 2048;
      } else if (opt.equals("bool_ret")) {
         return 4096;
      } else {
         return opt.equals("device_only") ? 15103 : 0;
      }
   }

   private void parseOptimization(String opt, boolean turnOn) {
      if (opt.equals("1")) {
         this._optimization = turnOn ? 11007 : 0;
      } else {
         int mask = this.optToMask(opt);
         if (turnOn) {
            this._optimization |= mask;
         } else {
            this._optimization &= ~mask;
         }
      }
   }

   private void parseOptimizations(String name, boolean turnOn) {
      Object o = this._properties.get(name);
      if (!(o instanceof Object)) {
         if (o instanceof Object) {
            String opt = (String)o;
            this.parseOptimization(opt, turnOn);
         }
      } else {
         Vector v = (Vector)o;

         for (int i = v.size() - 1; i >= 0; i--) {
            String opt = (String)v.elementAt(i);
            this.parseOptimization(opt, turnOn);
         }
      }
   }

   private void readFinalStatics(String param1, InputStream param2) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index 2 out of bounds for length 1
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.removeExceptionInstructionsEx(FinallyProcessor.java:1052)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.verifyFinallyEx(FinallyProcessor.java:502)
      //   at org.jetbrains.java.decompiler.modules.decompiler.FinallyProcessor.iterateGraph(FinallyProcessor.java:90)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:185)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/tools/compiler/Compiler._finalStatics Ljava/util/Vector;
      // 04: ifnonnull 12
      // 07: aload 0
      // 08: new java/lang/Object
      // 0b: dup
      // 0c: invokespecial java/util/Vector.<init> ()V
      // 0f: putfield net/rim/tools/compiler/Compiler._finalStatics Ljava/util/Vector;
      // 12: aload 2
      // 13: ifnull 51
      // 16: new java/lang/Object
      // 19: dup
      // 1a: aload 2
      // 1b: invokespecial java/io/DataInputStream.<init> (Ljava/io/InputStream;)V
      // 1e: astore 3
      // 1f: aload 0
      // 20: getfield net/rim/tools/compiler/Compiler._finalStatics Ljava/util/Vector;
      // 23: astore 4
      // 25: aload 4
      // 27: aload 3
      // 28: invokevirtual java/io/DataInputStream.readUTF ()Ljava/lang/String;
      // 2b: invokevirtual java/util/Vector.addElement (Ljava/lang/Object;)V
      // 2e: goto 25
      // 31: astore 4
      // 33: aload 3
      // 34: invokevirtual java/io/DataInputStream.close ()V
      // 37: goto 51
      // 3a: astore 5
      // 3c: aload 3
      // 3d: invokevirtual java/io/DataInputStream.close ()V
      // 40: aload 5
      // 42: athrow
      // 43: astore 3
      // 44: new net/rim/tools/compiler/util/CompileException
      // 47: dup
      // 48: aload 1
      // 49: aload 3
      // 4a: invokevirtual java/lang/Throwable.getMessage ()Ljava/lang/String;
      // 4d: invokespecial net/rim/tools/compiler/util/CompileException.<init> (Ljava/lang/String;Ljava/lang/String;)V
      // 50: athrow
      // 51: return
      // try (15 -> 23): 23 null
      // try (15 -> 24): 27 null
      // try (27 -> 28): 27 null
      // try (10 -> 32): 32 null
   }

   public void prepopulateObjectVTableMethods() {
      String HASHCODE = "hashCode";
      String EQUALS = "equals";
      String TOSTRING = "toString";
      Vector vt = this._objectClassVTable;
      ClassType classType = this._objectClass;
      classType.addModifiers(131072);
      Vector parms = (Vector)(new Object());
      int n = vt.size();

      for (int i = 0; i < n; i++) {
         Type returnType = null;
         parms.setSize(0);
         String name = (String)vt.elementAt(i);
         if (HASHCODE.equals(name)) {
            returnType = this._intType;
         } else if (EQUALS.equals(name)) {
            returnType = this._booleanType;
            parms.addElement(classType);
         } else {
            if (!TOSTRING.equals(name)) {
               throw new CompileException(null, ((StringBuffer)(new Object("unrecognized method in java.lang.object: "))).append(name).toString());
            }

            returnType = this._stringClass;
         }

         int modifiers = 128;
         modifiers = this.augmentMethodModifiers(classType, modifiers);
         int num = parms.size();
         Method method = new Method(classType, name, returnType, num, modifiers);

         for (int j = 0; j < num; j++) {
            method.addParameter(j, null, (Type)parms.elementAt(j));
         }

         method.markInVtable(true);
         classType.addMethod(this, method);
      }
   }

   private void processProperties() {
      String fileName = "/jar2cod.def";
      InputStream rdr = this.getClass().getResourceAsStream(fileName);
      if (rdr != null) {
         this._properties.readDefFile(fileName, rdr);
         rdr.close();
      }

      this._exportedStaticMethods = this._properties.parseVector("exports", true);
      this._exportedStaticData = this._properties.parseVector("statics", true);
      this._exportedFields = this._properties.parseVector("fields", false);
      this._exportedStrings = this._properties.parseVector("strings", false);
      ClassType classType = this.findClassType("net.rim.vm.UnGroupable");
      classType.addModifiers(67110912);
      this._objectClassMethods = this._properties.parseVector("rootclassmethods", true);
      this._objectClassVTable = this._properties.parseVector("rootclassvtable", true);
      this._objectClass = this.findClassType("java.lang.Object");
      this._objectClass.setReachable(this, false);
      this._stringClass = this.findClassType("java.lang.String");
      this._stringBufferClass = this.findClassType("java.lang.StringBuffer");
      if (this._makingMIDlet) {
         this.findClassType("javax.microedition.midlet.MIDletMain").setReachable(this, false);
      }
   }

   public Field findStatic(ClassType classType, Type type, String name) {
      if (this._finalStatics == null) {
         String fileName = "/static.def";
         InputStream in = this.getClass().getResourceAsStream(fileName);
         this.readFinalStatics(fileName, in);
      }

      Vector v = this._finalStatics;
      String match = ((StringBuffer)(new Object())).append('P').append(classType.getPackageName()).toString();
      int index = v.indexOf(match);
      if (index == -1) {
         return null;
      }

      match = ((StringBuffer)(new Object())).append('C').append(classType.getName()).toString();
      int num = v.size();
      index++;

      while (index < num) {
         String element = (String)v.elementAt(index);
         char ch = element.charAt(0);
         if (ch == 'P') {
            return null;
         }

         if (element.equals(match)) {
            break;
         }

         index++;
      }

      match = ((StringBuffer)(new Object())).append('F').append(name).append(':').append(type.encodeType()).append('=').toString();
      index++;

      while (index < num) {
         String element = (String)v.elementAt(index);
         char ch = element.charAt(0);
         if (ch == 'P' || ch == 'C') {
            return null;
         }

         if (element.startsWith(match)) {
            String value = element.substring(match.length());
            Constant constant = null;
            if (type.getTypeId() == 7) {
               constant = new Constant(value);
            } else {
               constant = new Constant(Long.parseLong(value));
            }

            int modifiers = this.augmentFieldModifiers(classType, 33554626);
            return new Field(name, type, classType, modifiers, -1, constant);
         }

         index++;
      }

      return null;
   }

   public ClassType findClassType(String fullName) {
      ClassType classType = null;
      int sep = fullName.lastIndexOf(46);
      String packageName = null;
      String className = fullName;
      classType = (ClassType)this._classes.get(fullName);
      if (classType != null) {
         return classType;
      }

      String localPackageName = null;
      if (sep == -1 && this._packageName != null && this._packageName.length() > 0) {
         localPackageName = ((StringBuffer)(new Object())).append(this._packageName).append(".").append(fullName).toString();
         classType = (ClassType)this._classes.get(localPackageName);
         if (classType != null) {
            return classType;
         }
      }

      if (sep != -1) {
         packageName = CharacterHelper.intern(fullName.substring(0, sep));
         className = fullName.substring(sep + 1);
      }

      classType = new ClassType(className, packageName);
      this._classes.put(fullName, classType);
      if (localPackageName != null) {
         this._classes.put(localPackageName, classType);
      }

      return classType;
   }

   public Method mirandize(ClassType classType, Method method) {
      int modifiers = method.getModifiers() & 251527167;
      int num = method.getNumParms();
      Method other = new Method(classType, method.getName(), method.getReturnType(), num, modifiers);

      for (int i = 0; i < num; i++) {
         NameAndType parm = method.getParm(i);
         other.addParameter(i, parm.getName(), parm.getType());
      }

      other.setBody(new InstructionCode(other, 0, other.getParmLocalCount(), null, null));
      return other;
   }

   private void resolve() {
      int progressCounter = 0;
      int progressPercent = -1;
      boolean generatedResources = false;

      while (true) {
         int classesReferencedCount = this._classesReferenced.size();
         int methodsUsedCount = this._methodsUsed.size();
         int percent = progressCounter * 100 / (classesReferencedCount + methodsUsedCount + 1);
         if (percent > progressPercent) {
            this._host.advanceProgress(-1);
            progressPercent = percent;
         }

         progressCounter++;
         if (this._classesReferencedIndex < classesReferencedCount) {
            ClassType classType = (ClassType)this._classesReferenced.elementAt(this._classesReferencedIndex++);
            classType.resolve(this);
         } else if (this._methodsUsedIndex < methodsUsedCount) {
            Method method = (Method)this._methodsUsed.elementAt(this._methodsUsedIndex++);
            method.resolve(this);
         } else {
            if (generatedResources) {
               return;
            }

            if (this._makingMIDlet) {
               this._stringClass.setReachable(this, false);
               ClassType midletMain = this.findClassType("javax.microedition.midlet.MIDletMain");
               midletMain.setReachable(this, false);
               synchronized (_parmTypes) {
                  _parmTypes.setSize(0);
                  _parmTypes.addElement(this._stringClass.getArrayType());
                  midletMain.findMethod(this, "main", null, _parmTypes, true, false);
                  _parmTypes.setSize(0);
               }
            }

            this._jad.fixupProperties(this, this._makingMIDlet, false, this._potentialMIDlets);
            this._potentialMIDlets = null;
            this._jad.generateManifest(this._makingMIDlet);
            this._resourceClassType = (ClassType)this._jad.generateResourceClass();
            generatedResources = true;
            this._midletPolicy = this._jad.getPolicy();
            this._signerCertEncoding = this._jad.getSignerCertEncoding();
            this._jad = null;
         }
      }
   }

   private TypeModule findInputTypeModule(String moduleName) {
      TypeModule typeModule = null;
      int num = this._typeModules.size();

      for (int i = 0; i < num; i++) {
         typeModule = (TypeModule)this._typeModules.elementAt(i);
         if (typeModule.getName().equals(moduleName)) {
            return typeModule;
         }
      }

      return null;
   }

   public TypeModule findInputTypeModule(String moduleName, String moduleVersion, int timeStamp) {
      TypeModule typeModule = this.findInputTypeModule(moduleName);
      if (typeModule == null) {
         typeModule = new TypeModule(moduleName, moduleVersion, timeStamp, null);
         this._typeModules.addElement(typeModule);
      }

      return typeModule;
   }

   private TypeModule makeOutputTypeModule(int index) {
      boolean target36 = false;
      String moduleName = FileHelper.makeMultiName(this._moduleName, index, null);
      TypeModule typeModule = new TypeModule(moduleName, this._moduleVersion, this._timeStamp, new Codfile(target36));
      this._typeModules.addElement(typeModule);
      return typeModule;
   }

   public int getSliceSize() {
      return this._sliceSize;
   }

   public int getDataFull() {
      return this._dataFull;
   }

   private boolean willFit(TypeModule typeModule, ClassType classType) {
      if (typeModule.getNumClasses() > 254) {
         return false;
      } else {
         int dataWeight = classType.getDataWeight();
         int codeWeight = classType.getCodeWeight();
         int vtableWeight = classType.getVtableWeight();
         int fieldWeight = classType.getFieldWeight();
         if (typeModule.getDataWeight() + dataWeight > this._dataFull) {
            return false;
         } else if (typeModule.getCodeWeight() + codeWeight > this._codeFull) {
            return false;
         } else {
            return typeModule.getVtableWeight() + vtableWeight > this._vtableFull ? false : typeModule.getFieldWeight() + fieldWeight <= this._fieldFull;
         }
      }
   }

   private TypeModule[] optimize() {
      this._classes = null;
      Vector v = this._classesReferenced;
      int num = v.size();

      for (int i = 0; i < num; i++) {
         ClassType classType = (ClassType)v.elementAt(i);
         if (!classType.is(131072) && classType.isDefined()) {
            this.useClassType(classType);
            classType.optimize(this);
         }
      }

      v = this._classesReferenced = null;
      v = this._methodsUsed;
      num = v.size();

      for (int var16 = 0; var16 < num; var16++) {
         Method method = (Method)v.elementAt(var16);
         method.optimize(this);
      }

      v = this._methodsUsed = null;
      Vector typeModuleVector = (Vector)(new Object());
      int bucket = 0;
      TypeModule typeModule = this.makeOutputTypeModule(bucket);
      typeModuleVector.addElement(typeModule);
      this._nullType.setTypeModule(typeModule);
      if (this._resourceClassType != null) {
         typeModule.addClass(this._resourceClassType);
         this._resourceClassType = null;
      }

      v = this._classTypesUsed;
      num = v.size();

      for (int var17 = 0; var17 < num; var17++) {
         ClassType classType = (ClassType)v.elementAt(var17);
         if (classType.getTypeModule() == null) {
            if (this.willFit(typeModule, classType)) {
               typeModule.addClass(classType);
            } else {
               TypeModule previous = null;
               int numPrevious = typeModuleVector.size() - 1;

               for (int j = 0; j < numPrevious; j++) {
                  previous = (TypeModule)typeModuleVector.elementAt(j);
                  if (this.willFit(previous, classType)) {
                     previous.addClass(classType);
                     break;
                  }

                  previous = null;
               }

               if (previous == null) {
                  typeModule = this.makeOutputTypeModule(++bucket);
                  typeModuleVector.addElement(typeModule);
                  typeModule.addClass(classType);
               }
            }
         }
      }

      v = this._classTypesUsed = null;
      num = typeModuleVector.size();
      TypeModule[] typeModules = new TypeModule[num];

      for (int var18 = 0; var18 < num; var18++) {
         typeModule = (TypeModule)typeModuleVector.elementAt(var18);
         typeModule.setOrdinalCount(var18, num);
         typeModules[var18] = typeModule;
         typeModule.optimize();
      }

      for (int var19 = 0; var19 < num; var19++) {
         DataSection dataSection = typeModules[var19].getDataSection();

         for (int j = 0; j < num; j++) {
            dataSection.addSibling(typeModules[j].getName());
         }
      }

      return typeModules;
   }

   private void populate(TypeModule[] typeModules) {
      int numExports = this._exportedStrings.size();
      boolean[] diagnosedExport = new boolean[numExports];
      int num = typeModules.length;

      for (int i = 0; i < num; i++) {
         int flags = 0;
         if (this._makingMIDlet) {
            flags = 2;
         }

         if (this._noVerifyErr) {
            flags |= 64;
         }

         TypeModule typeModule = typeModules[i];
         DataSection dataSection = typeModule.getDataSection();
         Object exportObject = null;
         Exported exported = null;
         if (this._exportedStaticMethods.size() > 0) {
            exportObject = this._exportedStaticMethods.elementAt(0);
         }

         if (!(exportObject instanceof Exported)) {
            if (exportObject != null) {
               exportObject = null;
            }
         } else {
            exported = (Exported)exportObject;
            Method method = exported.getMethod();
            if (i == 0) {
               ClassDef classDef = method.getClassType().getClassDef(typeModule);
               TypeList protoTypeList = method.getProtoTypeList(typeModule);
               dataSection.setEntryRoutine(classDef, method.getName(), protoTypeList);
            } else {
               exportObject = null;
            }
         }

         if (exportObject == null) {
            dataSection.setEntryRoutine(dataSection.getNullClassDef(), null, dataSection.getTypeLists().getNullTypeList());
            flags |= 1;
            flags &= -3;
         }

         exportObject = null;
         if (this._exportedStaticMethods.size() > 1) {
            exportObject = this._exportedStaticMethods.elementAt(1);
         }

         if (!(exportObject instanceof Exported)) {
            if (exportObject != null) {
               exportObject = null;
            }
         } else {
            exported = (Exported)exportObject;
            Method method = exported.getMethod();
            if (!method.is(131072)) {
               if (i == 0) {
                  ClassDef classDef = method.getClassType().getClassDef(typeModule);
                  TypeList protoTypeList = method.getProtoTypeList(typeModule);
                  dataSection.setAlternateEntryRoutine(classDef, method.getName(), protoTypeList);
               } else {
                  exportObject = null;
               }
            } else {
               exportObject = null;
            }
         }

         if (exportObject == null) {
            dataSection.setAlternateEntryRoutine(dataSection.getNullClassDef(), null, dataSection.getTypeLists().getNullTypeList());
         }

         for (int exportIndex = 0; exportIndex < numExports; exportIndex++) {
            DataBytes dataBytes = dataSection.getDataBytes();
            exportObject = this._exportedStrings.elementAt(exportIndex);
            if (exportObject != null) {
               if (!(exportObject instanceof Exported)) {
                  Object var26 = exportObject;
                  if (this._warning && !diagnosedExport[exportIndex]) {
                     this.generateWarning(
                        false, null, ((StringBuffer)(new Object("No definition found for exported string: "))).append((String)var26).toString()
                     );
                  }

                  diagnosedExport[exportIndex] = true;
               } else {
                  exported = (Exported)exportObject;
                  String name = exported.getName();
                  name = name.substring(name.lastIndexOf(46) + 1);
                  if (i == 0 || name.startsWith("_security")) {
                     Bytes bytes = dataBytes.getBytes(exported.getData(), 2, false);
                     dataSection.addExport(new ExportedData(dataSection, bytes, name));
                  }
               }
            }
         }

         typeModule.populate(this, flags);
      }

      this._nullType = null;
      this._voidType = null;
      this._booleanType = null;
      this._byteType = null;
      this._shortType = null;
      this._charType = null;
      this._intType = null;
      this._longType = null;
      this._floatType = null;
      this._doubleType = null;
      this._objectClassVTable = null;
      this._objectClass = null;
      this._stringClass = null;
      this._stringBufferClass = null;
      this._exportedStaticMethods = null;
      this._exportedStaticData = null;
      this._exportedFields = null;
      this._exportedStrings = null;
      this._objectClassVTable = null;
   }

   private void processPragma(String fileName, ClassType classType, String pragmaName, String value) {
      if (pragmaName.equals("RIM_pragma_exclusive")) {
         classType.clearModifiers(524288);
         int num = classType.getNumFields();

         for (int i = 0; i < num; i++) {
            classType.getField(i).clearModifiers(524288);
         }
      } else if (pragmaName.equals("RIM_pragma_inclusive")) {
         classType.addModifiers(524288);
         int num = classType.getNumFields();

         for (int i = 0; i < num; i++) {
            classType.getField(i).addModifiers(524288);
         }
      }
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private Classfile parseClassfile(String jarName, String inName, InputStream in, int length, TypeModule typeModule, boolean parsingImport) {
      String fileName = inName;
      if (jarName != null) {
         fileName = ((StringBuffer)(new Object())).append(jarName).append('(').append(inName).append(')').toString();
      }

      byte[] inBytes = StructuredInputStream.readAll(in, length, fileName);
      Classfile classFile = null;

      try {
         classFile = new Classfile(inBytes, parsingImport);
         if (!parsingImport && classFile.hasAttribute(AttributeList.NAME_SOURCEFILE)) {
            fileName = classFile.getAttribute(AttributeList.NAME_SOURCEFILE).getSourceFileName();
         }

         String className = classFile.getFullClassName();
         if (className == null) {
            throw new CompileException(fileName, ((StringBuffer)(new Object("No class name found in classfile: "))).append(inName).toString());
         }

         int classModifiers = Modifier.translateClassfileAccessFlags(classFile.getAccessFlags());
         this._packageName = null;
         if (!parsingImport) {
            String tnm = FileHelper.removeExtension(inName, FileHelper.ext_class).replace('/', '.').replace('\\', '.');
            if (!tnm.endsWith(className)) {
               this.generateWarning(
                  false,
                  fileName,
                  ((StringBuffer)(new Object("Class name: "))).append(className).append(" does not match file name: ").append(inName).toString()
               );
               classModifiers |= 134217728;
               className = tnm;
            }

            this._packageName = ClassType.extractPackageName(className);
            if (_verbosity >= 1) {
               Diagnose.out.println(((StringBuffer)(new Object("Parsing classfile: "))).append(inName).toString());
            }
         }

         ClassType classType = this.findClassType(className);
         if (classType.isDefined()) {
            throw new DuplicateException(fileName, className, classType.getFullName());
         }

         classType.setDefined();
         classModifiers = this.augmentClassModifiers(classModifiers);
         classType.addModifiers(classModifiers);
         String baseName = classFile.getFullBaseClassName();
         if (baseName != null) {
            ClassType baseClassType = this.findClassType(baseName);
            classType.setBaseClass(baseClassType);
         }

         int num = classFile.getNumInterfaces();
         classType.allocateBaseInterfaces(num);

         for (int i = 0; i < num; i++) {
            classType.setBaseInterface(i, this.findClassType(classFile.getFullInterfaceName(i)));
         }

         num = classFile.getNumFields();
         classType.allocateFields(num);

         for (int var49 = 0; var49 < num; var49++) {
            ClassfileField classfileField = classFile.getField(var49);
            String fieldName = classfileField.getName();
            int modifiers = Modifier.translateClassfileAccessFlags(classfileField.getAccessFlags());
            if (classfileField.hasAttribute(AttributeList.NAME_SYNTHETIC)) {
               modifiers |= 33554432;
            }

            modifiers = this.augmentFieldModifiers(classType, modifiers);
            if ((modifiers & 8256) == 8256) {
               throw new CompileException(fileName, ((StringBuffer)(new Object("Invalid modifier combination for field: "))).append(fieldName).toString());
            }

            if (classType.is(2048)) {
               int mask = 12288;
               int want = 0;
               if ((modifiers & mask) != want) {
                  throw new CompileException(
                     fileName, ((StringBuffer)(new Object("Invalid modifier combination for interface field: "))).append(fieldName).toString()
                  );
               }

               int var62 = 962;
               int var68 = 194;
               if ((modifiers & var62) != var68) {
                  this.generateWarning(false, fileName, ((StringBuffer)(new Object("Invalid modifiers for interface field: "))).append(fieldName).toString());
                  modifiers |= 134217728;
               }
            }

            TypeDescriptor descriptor = classfileField.getDescriptor();
            Type type = Type.translateType(this, descriptor);
            if (descriptor.getCharsRemaining() != 0) {
               throw new CompileException(
                  fileName,
                  ((StringBuffer)(new Object("Invalid type descriptor '"))).append(descriptor.getString()).append("' for field: ").append(fieldName).toString()
               );
            }

            Constant value = null;
            if (!parsingImport) {
               Attribute attr = classfileField.getAttribute("ConstantValue");
               if (attr != null) {
                  if ((modifiers & 2) == 0) {
                     if ((modifiers & 64) == 64) {
                        this.generateWarning(
                           true, fileName, ((StringBuffer)(new Object("Constant value final member data is not static: "))).append(fieldName).toString()
                        );
                     }
                  } else if (!(type instanceof BaseType)) {
                     if (!(type instanceof ClassType)) {
                        throw new CompileException(
                           fileName, ((StringBuffer)(new Object("Invalid type for static constant field: "))).append(fieldName).toString()
                        );
                     }

                     ClassType fieldClassType = (ClassType)type;
                     if (!fieldClassType.equals(this.getStringClass())) {
                        throw new CompileException(
                           fileName, ((StringBuffer)(new Object("Invalid type for static constant field: "))).append(fieldName).toString()
                        );
                     }

                     String str = attr.getConstantString();
                     if ((modifiers & 512 | 64) == 576 && fieldName.startsWith("RIM_pragma")) {
                        this.processPragma(fileName, classType, fieldName, str);
                     }

                     value = new Constant(str);
                  } else {
                     int typeId = type.getTypeId();
                     switch (typeId) {
                        case 0:
                        case 7:
                        case 8:
                        case 9:
                        case 10:
                           throw new CompileException(
                              fileName, ((StringBuffer)(new Object("Invalid type for static constant field: "))).append(fieldName).toString()
                           );
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 11:
                        default:
                           value = new Constant(attr.getConstantValue(typeId == 11));
                           break;
                        case 6:
                        case 12:
                           value = new Constant(attr.getConstantValueLong(typeId == 12));
                     }
                  }
               }
            }

            classType.addData(this, fieldName, type, modifiers, value);
         }

         num = classFile.getNumMethods();
         classType.allocateMethods(num);

         for (int var50 = 0; var50 < num; var50++) {
            ClassfileMethod classfileMethod = classFile.getMethod(var50);
            String methodName = classfileMethod.getName();
            int modifiers = Modifier.translateClassfileAccessFlags(classfileMethod.getAccessFlags());
            if (classfileMethod.hasAttribute("Synthetic")) {
               modifiers |= 33554432;
            }

            if (methodName.equals("<init>")) {
               modifiers |= 16;
               int mask = 32832;
               if ((modifiers & mask) != 0) {
                  throw new CompileException(fileName, "Invalid modifier combination for constructor.");
               }
            } else if (methodName.equals("<clinit>")) {
               if (classfileMethod.getDescriptor().getString().equals("()V")) {
                  modifiers = 1048578;
               }
            } else {
               if (!StringHelper.validateIdentifier(methodName)) {
                  throw new CompileException(fileName, ((StringBuffer)(new Object("Invalid method name: "))).append(methodName).toString());
               }

               if ((modifiers & 32) != 0) {
                  int mask = 49152;
                  if ((modifiers & mask) != 0) {
                     throw new CompileException(
                        fileName, ((StringBuffer)(new Object("Invalid modifier combination for method: "))).append(methodName).toString()
                     );
                  }
               }
            }

            modifiers = this.augmentMethodModifiers(classType, modifiers);
            Method method = null;
            synchronized (_parmTypes) {
               _parmTypes.setSize(0);
               TypeDescriptor prototype = classfileMethod.getDescriptor();
               Type.translateTypes(this, prototype, _parmTypes);
               Type type = Type.translateType(this, prototype);
               if (prototype.getCharsRemaining() != 0) {
                  throw new CompileException(
                     fileName,
                     ((StringBuffer)(new Object("Invalid type descriptor '")))
                        .append(prototype.getString())
                        .append("' for method: ")
                        .append(methodName)
                        .toString()
                  );
               }

               int n = _parmTypes.size();
               method = new Method(classType, methodName, type, n, modifiers);

               for (int j = 0; j < n; j++) {
                  method.addParameter(j, null, (Type)_parmTypes.elementAt(j));
               }

               _parmTypes.setSize(0);
            }

            if (!parsingImport) {
               Attribute exceptions = classfileMethod.getAttribute(AttributeList.NAME_EXCEPTIONS);
               if (exceptions != null) {
                  int var54 = exceptions.getNumExceptions();

                  for (int var51 = 0; var51 < var54; var51++) {
                     ClassType var73 = this.findClassType(exceptions.getExceptionClassName(var51));
                  }
               }

               if (!classfileMethod.hasAttribute(AttributeList.NAME_CODE)) {
                  int plc = method.getParmLocalCount();
                  method.setBody(new InstructionCode(method, 0, plc, null, null));
                  if (!method.is(33)) {
                     this.generateWarning(false, fileName, ((StringBuffer)(new Object("Method has no code attribute: "))).append(method.getName()).toString());
                  }
               } else {
                  boolean badCode = false;
                  if (method.is(1)) {
                     this.generateWarning(
                        false, fileName, ((StringBuffer)(new Object("Native method has code attribute: "))).append(method.getName()).toString()
                     );
                     method.addModifiers(134217728);
                     badCode = true;
                  }

                  if (method.is(32)) {
                     this.generateWarning(
                        false, fileName, ((StringBuffer)(new Object("Abstract method has code attribute: "))).append(method.getName()).toString()
                     );
                     method.addModifiers(134217728);
                     badCode = true;
                  }

                  AttributeCode ac = (AttributeCode)classfileMethod.getAttribute(AttributeList.NAME_CODE);
                  int codeLength = 0;
                  byte[] code = ac.getCode();
                  if (code != null) {
                     codeLength = code.length;
                     if (codeLength >= 65536) {
                        throw new CompileException(fileName, ((StringBuffer)(new Object("Code attribute too large: "))).append(method.getName()).toString());
                     }
                  }

                  int maxStack = ac.getMaxStack();
                  if (maxStack > 8000) {
                     maxStack = 8000;
                  }

                  int maxLocals = ac.getMaxLocals();
                  if (maxLocals > 8000) {
                     maxLocals = 8000;
                  }

                  InstructionCode bc = new InstructionCode(method, maxStack, maxLocals, code, classFile.getConstantPool());
                  bc.setBadCode(badCode);
                  int var55 = ac.getNumHandlers();
                  if (var55 > 0) {
                     ClassfileExceptionHandler[] handlerTable = ac.getHandlers();

                     for (int var52 = 0; var52 < var55; var52++) {
                        ClassfileExceptionHandler h = handlerTable[var52];
                        int start = h.getStart();
                        int end = h.getEnd();
                        if (end <= start || end > codeLength) {
                           throw new CompileException(
                              fileName, ((StringBuffer)(new Object("invalid exception handler range in: "))).append(method.getName()).toString()
                           );
                        }

                        int handler = h.getHandler();
                        if (handler >= codeLength) {
                           throw new CompileException(
                              fileName, ((StringBuffer)(new Object("invalid exception handler offset in: "))).append(method.getName()).toString()
                           );
                        }

                        ClassType exceptionClassType = null;
                        String typeName = h.getTypeName();
                        if (typeName == null) {
                           exceptionClassType = null;
                        } else {
                           exceptionClassType = this.findClassType(typeName);
                        }

                        h.setClassType(exceptionClassType);
                     }

                     bc.setHandlers(handlerTable);
                  }

                  if (ac.hasAttribute(AttributeList.NAME_STACKMAP) && this.isPreverified()) {
                     AttributeStackMapTable stackMapTable = (AttributeStackMapTable)ac.getAttribute(AttributeList.NAME_STACKMAP);
                     var55 = stackMapTable.getNumStackMaps();

                     for (int var53 = 0; var53 < var55; var53++) {
                        AttributeStackMap sme = stackMapTable.getStackMap(var53);
                        if (sme.getLocalSize() > maxLocals) {
                           throw new CompileException(
                              fileName, ((StringBuffer)(new Object("Invalid locals map in method: "))).append(method.getName()).toString()
                           );
                        }

                        if (sme.getStackSize() > maxStack) {
                           this.generateWarning(false, fileName, ((StringBuffer)(new Object("stack map too big: "))).append(method.getName()).toString());
                           method.addModifiers(134217728);
                        }

                        Type.translateStackMap(this, classType, maxLocals, sme, code);
                     }

                     bc.setStackMaps(stackMapTable);
                  }

                  method.setBody(bc);
               }
            }

            classType.addMethod(this, method);
         }

         classType.setReachable(this, false);
         this._packageName = null;
         return classFile;
      } catch (Throwable var40) {
         if (_verbosity >= 2) {
            var40.printStackTrace();
         }

         CompileException ce;
         if (!(var40 instanceof CompileException)) {
            String msg = var40.getMessage();
            if (msg == null) {
               ce = new CompileException(inName, "Invalid class file");
            } else {
               ce = new CompileException(inName, ((StringBuffer)(new Object("Invalid class file: "))).append(msg).toString());
            }
         } else {
            ce = (CompileException)var40;
         }

         throw ce;
      }
   }

   private void parseJar(String jarName, JarInputStream jarStream) {
      boolean foundManifest = false;
      if (this._makingMIDlet) {
         Manifest manifest = (Manifest)jarStream.getManifest();
         if (manifest != null) {
            if (_verbosity >= 1) {
               Diagnose.out.println("Parsing manifest");
            }

            this._jad.parseManifest(this, jarName, manifest);
            foundManifest = true;
            manifest = null;
         }
      }

      JarEntry entry = null;

      while ((var12 = jarStream.getNextJarEntry()) != null) {
         if (!var12.isDirectory()) {
            String entryName = var12.getName();
            int nameLen = entryName.length();
            int size = var12.getSize();
            if (FileHelper.checkExtension(entryName, FileHelper.ext_class) != -1) {
               this.parseClassfile(jarName, entryName, jarStream, size, null, false);
               this._host.advanceProgress(-1);
            } else if (nameLen == 20 && entryName.regionMatches(true, 0, "META-INF/MANIFEST.MF", 0, nameLen)) {
               if (this._makingMIDlet) {
                  if (_verbosity >= 1) {
                     Diagnose.out.println(((StringBuffer)(new Object("Parsing manifest: "))).append(entryName).toString());
                  }

                  Manifest manifest = new Manifest(jarStream);
                  this._jad.parseManifest(this, jarName, manifest);
                  foundManifest = true;
                  manifest = null;
               }
            } else {
               if (_verbosity >= 1) {
                  Diagnose.out.println(((StringBuffer)(new Object("Reading resource: "))).append(entryName).toString());
               }

               ResourceFile rf = null;
               if (size > 4194304) {
                  throw new Object(((StringBuffer)(new Object())).append(entryName).append(": too large: ").append(size).append(" bytes").toString());
               }

               if (FileHelper.checkExtensions(entryName, FileHelper.ext_images) != -1) {
                  ImageFile imageFile = new ImageFile(entryName, jarStream, size);
                  rf = imageFile;
               } else {
                  rf = new ResourceFile(entryName, jarStream, size);
               }

               if (this._resourceBinaries.indexOf(rf) == -1) {
                  this._resourceBinaries.addElement(rf);
               }
            }
         }

         jarStream.closeEntry();
      }

      if (this._makingMIDlet && !foundManifest) {
         throw new CompileException(907, null, ((StringBuffer)(new Object("Missing manifest: "))).append(jarName).toString());
      }
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void write(TypeModule[] typeModules) {
      int numCods = typeModules.length;
      StringBuffer errorBuffer = (StringBuffer)(new Object());

      for (int i = 0; i < numCods; i++) {
         TypeModule typeModule = typeModules[i];
         typeModule.getCodfile().count(typeModule.getName(), errorBuffer);
      }

      if (errorBuffer.length() > 0) {
         if (this._properties.getQuotedProperty("nolimit") == null) {
            throw new Object(errorBuffer.toString());
         }

         Diagnose.out.println(errorBuffer.toString());
      }

      OutputStream[] outs = new Object[numCods];
      boolean var12 = false /* VF: Semaphore variable */;

      try {
         var12 = true;

         for (int var15 = 0; var15 < numCods; var15++) {
            String outName = FileHelper.makeMultiName(this._codeName, var15, FileHelper.ext_cod);
            outs[var15] = this._host.openOutput(outName);
            this._host.advanceProgress(-1);
            typeModules[var15].getCodfile().write(outs[var15]);
            outs[var15].close();
            outs[var15] = null;
         }

         var12 = false;
      } finally {
         if (var12) {
            int var14 = 0;

            while (true) {
               if (var14 >= numCods) {
                  outs = null;
               } else {
                  OutputStream out = outs[var14];
                  if (out != null) {
                     out.close();
                  }

                  var14++;
               }
            }
         }
      }

      for (int var16 = 0; var16 < numCods; var16++) {
         OutputStream out = outs[var16];
         if (out != null) {
            out.close();
         }
      }

      outs = null;
   }

   private void parseJads() {
      String rapcName = this._properties.getQuotedProperty("rapc");
      if (rapcName != null) {
         this._jad.parseJad(this, rapcName);
      }

      String jadName = this._properties.getQuotedProperty("jad");
      if (jadName != null) {
         this._jad.parseJad(this, jadName);
      }

      byte[] jadBytes = (byte[])this._properties.get("jadContent");
      if (jadBytes != null) {
         this._jad.parseJad(CharacterHelper.utf8ToString(jadBytes));
      }

      String jadString = (String)this._properties.get("jadString");
      if (jadString != null) {
         this._jad.parseJad(jadString);
      }

      this._moduleVersion = this._jad.getProperty("MIDlet-Version");
   }

   private void compile() {
      ExecutionTimer timer_parse = new ExecutionTimer("parse", this._timers);
      this.processProperties();
      boolean jadsParsed = false;
      if (this._jarFiles != null) {
         int n = this._jarFiles.size();

         for (int i = 0; i < n; i++) {
            String jarName = null;
            InputStream inputStream = null;
            jarName = (String)this._jarFiles.elementAt(i);
            inputStream = this._host.openInput(jarName);
            DigestInputStream digestStream = null;
            if (i == 0) {
               digestStream = new DigestInputStream(MIDletSecurity.getMIDletSignatureDigest(), inputStream);
               inputStream = digestStream;
            }

            JarInputStream jarStream = new JarInputStream(inputStream, false);
            inputStream = null;
            this.parseJar(jarName, jarStream);
            jarStream.close();
            JarInputStream var17 = null;
            if (!jadsParsed) {
               jadsParsed = true;
               this.parseJads();
            }

            if (digestStream != null) {
               int length = digestStream.getLength();
               if (length != this._jad.getJarSize()) {
                  throw new CompileException(904, jarName, "jar size does not match MIDlet-Jar-Size attribute");
               }

               this._jad.verifySignature(digestStream.getDigest());
               Object var15 = null;
            }
         }

         this._jarFiles = null;
      }

      if (!jadsParsed) {
         this.parseJads();
      }

      timer_parse.stop();
      ExecutionTimer timer_analysis = new ExecutionTimer("analysis", this._timers);
      this._host.advanceProgress(0);
      if (_verbosity >= 1) {
         Diagnose.out.println("Resolving");
      }

      this.resolve();
      if (_verbosity >= 1) {
         Diagnose.out.println("Optimizing");
      }

      TypeModule[] typeModules = this.optimize();
      this._host.advanceProgress(1);
      if (_verbosity >= 1) {
         Diagnose.out.println("Populating");
      }

      this.populate(typeModules);
      timer_analysis.stop();
      ExecutionTimer timer_write = new ExecutionTimer("write", this._timers);
      this.write(typeModules);
      this._host.advanceProgress(2);
      timer_write.stop();
   }

   private void handleResources() {
   }

   public boolean getRequiresSigning() {
      return this._midletPolicy != null;
   }

   public byte[] getSignerCertEncoding() {
      return this._signerCertEncoding;
   }

   public void performSigning(Vector codfiles) {
      int n = codfiles.size();

      for (int i = n - 1; i >= 0; i--) {
         byte[] codfile = (byte[])codfiles.elementAt(i);
         byte[] signatureTrailer = MIDletSecurity.genMIDletTrailer(codfile, this._midletPolicy);
         byte[] policyTrailer = CodeModuleManager.makeTrailer(2, 0, this._midletPolicy);
         byte[][] trailers = new byte[][]{policyTrailer, signatureTrailer};
         codfile = CodeModuleManager.appendTrailers(codfile, trailers);
         codfiles.setElementAt(codfile, i);
      }
   }

   public static Compiler compile(Object cookie, CompilerProperties properties) {
      Compiler compiler = null;
      Vector timers = (Vector)(new Object());
      ExecutionTimer timer_total = new ExecutionTimer("total", timers);
      Host host = (Host)cookie;
      Diagnose.out = host.openDiagnose();
      compiler = new Compiler(host, properties);
      compiler._timers = timers;
      JadSupport jad = new JadSupport(properties);
      compiler.setJad(jad);
      String resourceClassName = jad.getResourceClassName(compiler, compiler._moduleName);
      JadSupport var11 = null;
      if (resourceClassName != null) {
         properties.setProperty("properties", resourceClassName);
      }

      compiler.compile();
      timer_total.stop();
      if (_verbosity > 0) {
         Diagnose.out.println("No errors.");
      }

      if (properties.getProperty("timing") != null) {
         Diagnose.out.print(" Timing:");
         Diagnose.out.println(timers);
      }

      return compiler;
   }
}
