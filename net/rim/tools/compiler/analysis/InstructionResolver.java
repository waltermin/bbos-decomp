package net.rim.tools.compiler.analysis;

import java.util.Vector;
import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.classfile.ByteCodeInstructions;
import net.rim.tools.compiler.classfile.ConstantPoolArrayData;
import net.rim.tools.compiler.classfile.ConstantPoolClass;
import net.rim.tools.compiler.classfile.ConstantPoolFieldRef;
import net.rim.tools.compiler.classfile.ConstantPoolInteger;
import net.rim.tools.compiler.classfile.ConstantPoolInterfaceMethodRef;
import net.rim.tools.compiler.classfile.ConstantPoolLong;
import net.rim.tools.compiler.classfile.ConstantPoolMethodRef;
import net.rim.tools.compiler.classfile.ConstantPoolString;
import net.rim.tools.compiler.classfile.TypeDescriptor;
import net.rim.tools.compiler.io.Diagnose;
import net.rim.tools.compiler.types.ArrayType;
import net.rim.tools.compiler.types.BaseType;
import net.rim.tools.compiler.types.ClassType;
import net.rim.tools.compiler.types.Constant;
import net.rim.tools.compiler.types.Field;
import net.rim.tools.compiler.types.Method;
import net.rim.tools.compiler.types.Type;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public final class InstructionResolver implements Constants {
   private int _newOrdinal;
   private Vector _parmTypes = (Vector)(new Object());
   private boolean[] _boundaries;
   private Compiler _compiler;
   private ClassType _classType;
   private Method _method;
   private ByteCodeInstructions _block;
   private static final int NO_MAPPING;
   private static final int[] _opcodeMapping = new int[]{
      204,
      34,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      175,
      177,
      -1,
      -1,
      176,
      172,
      174,
      173,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      181,
      183,
      -1,
      -1,
      182,
      178,
      179,
      180,
      205,
      206,
      207,
      209,
      210,
      208,
      211,
      212,
      213,
      122,
      123,
      256,
      257,
      124,
      125,
      258,
      259,
      126,
      127,
      260,
      261,
      128,
      129,
      262,
      263,
      130,
      131,
      264,
      265,
      118,
      119,
      266,
      267,
      138,
      139,
      140,
      141,
      142,
      143,
      132,
      133,
      134,
      135,
      136,
      137,
      -1,
      116,
      268,
      269,
      117,
      270,
      271,
      272,
      273,
      274,
      275,
      276,
      277,
      113,
      115,
      114,
      144,
      278,
      279,
      280,
      281,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      24,
      30,
      24,
      30,
      27,
      -1,
      109,
      105,
      99,
      95,
      1,
      5,
      7,
      2,
      -1,
      -1,
      -1,
      -1,
      167,
      188,
      -1,
      -1,
      202,
      203,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      712179968,
      -1975817147,
      16806977,
      9314166,
      12956929,
      524292,
      1950361096,
      -1390016512,
      1953383787,
      638058541,
      407727021,
      -1390016512,
      15487851,
      1806509576,
      1701869908,
      671613043,
      1886999668,
      671613029,
      134247284,
      688350017,
      1936172870,
      1883310080,
      158832,
      -831373048,
      1886999673,
      1091043429,
      1769108596,
      6635362,
      1869562376,
      2035556734,
      134243696,
      1419170370,
      134219840,
      555421763,
      1869566068,
      1124597868,
      1948326672,
      1819242320,
      2729795,
      454050568,
      1971745825,
      1124597861,
      1948326672,
      1281717638,
      134244112,
      555421763,
      134266996,
      2729795,
      698827528,
      7300648,
      698827528,
      1920234561,
      1698652777,
      1124597875,
      1698965927,
      1124597862,
      743319975,
      1124597861,
      743319975,
      134266981,
      1412015939,
      6647929,
      698827528,
      134244000,
      1668506948,
      421357938,
      1866729472,
      2035580653,
      134243696,
      459649348,
      1850017792,
      1158152292,
      2978926,
      1869366792,
      1886999569,
      1174929509,
      1948828789,
      123823627,
      6630478,
      -998947320,
      -1488731838,
      1697402409,
      1967523840,
      698827716,
      6630478,
      -998947320,
      6630478,
      1679902728,
      134220652,
      1818501448,
      134247179,
      1797960,
      274942216,
      1665730560,
      1697402384,
      1883834368,
      1078593536,
      134278227,
      1416040524,
      6647929,
      1729121288,
      6649222,
      1650543624,
      1275592843,
      1724211817,
      -1991505920,
      15487775,
      529091592,
      1701869908,
      1309147251,
      134243628,
      1097149518,
      2035573870,
      134243696,
      1931699534,
      15098345,
      -998945272,
      1694627666,
      1968048128,
      1886999748,
      1309147237,
      1806509711,
      1920233029,
      7562601,
      646925832,
      407727021,
      1309147251,
      1914382479,
      477389685,
      1309147251,
      192161935,
      1929863526,
      -1890711552,
      1819308097,
      134247170,
      -767389874,
      138433686,
      1309147251,
      1953383823,
      1936025970,
      -1890711552,
      1818501448,
      134247179,
      1665765198,
      1697402384,
      1309147251,
      274942351,
      1309147251,
      1744981391,
      134247357,
      1079283534,
      134247176,
      2035584846,
      7562608,
      -1735438840,
      1818501448,
      134247179,
      1939377998,
      -1890711552,
      1717912767,
      1309147251,
      7585679,
      1255624200,
      1953383744,
      1309147181,
      4241623,
      1138314760,
      134228391,
      1724242254,
      1325924382,
      -1025568995,
      1649346560,
      -1488731286,
      1325924393,
      1136880226,
      38611367,
      7585128,
      1784827656,
      698827715,
      1650545750,
      1325924478,
      1325924464,
      134230640,
      106655823,
      1884227584,
      -330103205,
      1917782016,
      2031972,
      1869959432,
      8135540,
      294343176,
      743335353,
      1376256101,
      1935999479,
      1750272000,
      1886999708,
      1393033317,
      1392994668,
      1393033452,
      -1308119441,
      1697402494,
      -1319958528,
      709036910,
      1683713605,
      1393033348,
      1409810668,
      134219840,
      1701869908,
      2035550208,
      743335280,
      1409810533,
      1936027769,
      -833353728,
      1806524935,
      190187520,
      1165583977,
      1667698,
      -1905306104,
      1701869908,
      -1017772032,
      2080899097,
      8325120,
      1967554312,
      2131230916,
      1885011,
      1702200840,
      1955858432,
      -1744306038,
      1311352643,
      134243628,
      1679902872,
      134220652,
      212496800,
      1857114995,
      -1610088316,
      128872307,
      15954753,
      1869848584,
      -1488779346,
      1697402409,
      111544320,
      111544320,
      1992389,
      1423484936,
      6647929,
      1943578632,
      11077632,
      -814173688,
      134244000,
      2035564728,
      134243696,
      1383284920,
      -2080284357,
      12519424,
      1699004168,
      -1089994650,
      134244000,
      -1073217344,
      2043572801,
      1103104000,
      679071346,
      15487747,
      1967308808,
      1124820582,
      134228391,
      698827712,
      -2034235392,
      134243701,
      1992389,
      -1402680824,
      15796224,
      8482569,
      1166110473,
      2978926,
      504102922,
      1712062591,
      1124537099,
      201337255,
      290266215,
      7562646,
      1987013900,
      1829503077,
      1164277359,
      1099578220,
      1879834740,
      201328551,
      1986817907,
      1930166373,
      1702259823,
      1632380226,
      201362274,
      1986817907,
      -1764605339,
      234910565,
      355341824,
      204347182,
      128872307,
      773193843,
      773271061,
      9514522,
      773139990,
      773467671,
      773193954,
      773271061,
      527305631,
      369134894,
      388896046,
      1846255406,
      14822943,
      773139990,
      1936486320,
      4221486,
      773139990,
      1936486320,
      198955054,
      355341824,
      1819258926,
      -607113613,
      1627991563,
      2120836649,
      355341824,
      1819258926,
      -607113613,
      522268171,
      10122105,
      773139990,
      1936486320,
      198955054,
      14368558,
      773139990,
      1936486320,
      198955054,
      1717852974,
      369131113,
      -1339157202,
      779316335,
      772529104,
      1595277453,
      1869373205,
      1230169697,
      1869848653,
      7538606,
      1595236118,
      -429822100,
      -1605547694,
      128872307,
      1977503859,
      806949228,
      -915400704,
      -915400704,
      198954580,
      1075511411,
      56062836,
      639500392,
      1850043309,
      503328116,
      1298902310,
      503345944,
      503317288,
      2043572801,
      1701869908,
      1950424576,
      1651077748,
      7562559,
      -1507507682,
      1109262342,
      1936427372,
      1816337920,
      1126039556,
      743319975,
      1126039653,
      2035558823,
      503342448,
      -1975817147,
      503346241,
      1936172870,
      558374400,
      1930128484,
      1665736192,
      1226702864,
      743313507,
      1226702949,
      1277034608,
      9134689,
      1744981278,
      1293811901,
      2035578977,
      1405642096,
      1293811948,
      8287677,
      1697402398,
      1699618304,
      1642689315,
      1310589158,
      2121869017,
      192179781,
      1884233216,
      1917787648,
      2031972,
      1806520350,
      1697402597,
      575872512,
      12192885,
      1651069726,
      503350380,
      661551443,
      503340290,
      1124847443,
      503327143,
      503376979,
      1701869908,
      2035555840,
      7562608,
      654201886,
      503345196,
      -2044854148,
      1884251509,
      1955864064,
      -1457651574,
      12525056,
      510051614,
      1724194304,
      503345950,
      11297994,
      643692062,
      520123456,
      679643500,
      1634077556,
      520123143,
      1317177708,
      1919505936,
      1377793396,
      1935999479,
      -1989402880,
      1534087042,
      520123142,
      1451395436,
      1970565737,
      32985631,
      520123237,
      -1736275604,
      1818501448,
      520123147,
      -1081964180,
      1936090436,
      -1989402880,
      1381889666,
      1935999479,
      1857101568,
      1969890560,
      -848343013,
      1982136441,
      1730873121,
      455606444,
      1952675186,
      455606300,
      1952675186,
      1806509596,
      762605125,
      1914382336,
      477389685,
      7566376,
      1970412328,
      1109161059,
      671127250,
      1668641307,
      273423476,
      455606375,
      1952675186,
      1697402396,
      1415867969,
      6647929,
      1970412328,
      1344042083,
      292320734,
      455606283,
      1952675186,
      138433564,
      1914382336,
      477389685,
      1701869908,
      1914382336,
      477389685,
      191569751,
      1914382336,
      477389685,
      1819243424,
      671091574,
      1668641307,
      111549556,
      1914382336,
      477389685,
      7592104,
      1970412328,
      -1071877021,
      1914382336,
      477389685,
      -831372864,
      1881669753,
      671150911,
      747480,
      1534709312,
      1090550128,
      40661104,
      -831373056,
      1062189689,
      -1569766715,
      9991012,
      1920234561,
      1698652777,
      1953775872,
      1063414130,
      1806509669,
      1090525261,
      1769108596,
      644169570,
      407727021,
      2120376660,
      1953775872,
      1063414130,
      1806509669,
      2035554381,
      1090545008,
      1769108596,
      1281703778,
      -582064895,
      1650545675,
      1950417022,
      1651077748,
      -1991482049,
      1765824031,
      1417568865,
      8282721,
      1920234561,
      1698652777,
      1090520742,
      1769108596,
      -932888734,
      454050560,
      1867543585,
      1124101231,
      1948326672,
      1819242320,
      1712026664,
      38602593,
      1721810280,
      454050560,
      1867543585,
      -668439441,
      1124076391,
      1948326672,
      1819242320,
      2043572801,
      272826495,
      1349787931,
      1131179887,
      1124084135,
      1948326672,
      1819242320,
      762605125,
      454050560,
      1867543585,
      273443951,
      272826471,
      1349787931,
      1298952047,
      -1598199806,
      272826470,
      1349787931,
      1315729263,
      1849779500,
      1886999652,
      272826469,
      1349787931,
      1416392559,
      12218231,
      555421763,
      1869566068,
      1179932012,
      272826424,
      1349787931,
      -1167298705,
      454050560,
      1867543585,
      12545135,
      555421763,
      1869566068,
      1721810796,
      454050560,
      1867543585,
      12610671,
      2729795,
      1126803267,
      9968481,
      1143580483,
      1124099685,
      1698965927,
      1836008550,
      6101861,
      1143580483,
      1866884709,
      7254284,
      1143580483,
      -1991481755,
      -1488781281,
      1717912617
   };
   private static final int[] _ifMapping = new int[]{
      147,
      150,
      156,
      154,
      152,
      158,
      145,
      148,
      155,
      153,
      151,
      157,
      146,
      149,
      -1,
      -804650806,
      204,
      34,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1,
      -1
   };

   InstructionResolver() {
   }

   final void init(Compiler compiler, ClassType classType, Method method, ByteCodeInstructions blocks, int length) {
      this._compiler = compiler;
      this._classType = classType;
      this._method = method;
      this._boundaries = new boolean[++length];
      this._boundaries[length - 1] = true;
      this._block = blocks;
   }

   final void fini() {
      this._parmTypes.setSize(0);
      this._boundaries = null;
      this._compiler = null;
      this._classType = null;
      this._method = null;
      this._block = null;
   }

   public final int getMaxStack() {
      return this._method.getMaxStack();
   }

   private final InstructionTarget getBranchTarget(int offset) {
      return this._block.plantTarget(offset, true);
   }

   private final Type findType(String typeName) {
      TypeDescriptor descriptor = new TypeDescriptor(typeName);
      Type type = Type.translateType(this._compiler, descriptor);
      if (descriptor.getCharsRemaining() != 0) {
         throw new CompileException(
            this._classType.getFullName(), ((StringBuffer)(new Object("Invalid type descriptor: "))).append(descriptor.getString()).toString()
         );
      }

      Type resolveType = type;
      if (resolveType instanceof ArrayType) {
         ArrayType arrayType = (ArrayType)resolveType;
         resolveType = arrayType.getMostBaseType();
      }

      if (resolveType instanceof ClassType) {
         ClassType classType = (ClassType)resolveType;
         classType.setReachable(this._compiler, true);
         classType.resolve(this._compiler);
         if (!classType.isDefined()) {
            this._compiler
               .generateWarning(
                  false,
                  this._classType.getFullName(),
                  ((StringBuffer)(new Object("Reference to undefined class: "))).append(classType.getFullName()).toString()
               );
            this._classType.addModifiers(134217728);
            this._method.addModifiers(134217728);
         }
      }

      return type;
   }

   private final ClassType findClassType(String className) {
      ClassType classType = this._compiler.findClassType(className);
      classType.setReachable(this._compiler, true);
      classType.resolve(this._compiler);
      if (!classType.isDefined()) {
         this._compiler
            .generateWarning(
               false, this._classType.getFullName(), ((StringBuffer)(new Object("Reference to undefined class: "))).append(classType.getFullName()).toString()
            );
         this._classType.addModifiers(134217728);
         this._method.addModifiers(134217728);
      }

      return classType;
   }

   private final Type resolveClass(ConstantPoolClass cpc, boolean classRequired) {
      String typeName = cpc.getName();
      Type type = null;
      if (!classRequired && typeName.charAt(0) == '[') {
         type = this.findType(typeName);
      } else {
         ClassType classType = this.findClassType(typeName);
         if (classType.is(131072)) {
            this._classType.addDataWeight(8 + typeName.length());
         }

         type = classType;
      }

      cpc.setType(type);
      return type;
   }

   private final Field resolveField(ConstantPoolFieldRef cpf, boolean isStatic) {
      ClassType classType = cpf.getClassType();
      if (classType == null) {
         classType = (ClassType)this.resolveClass(cpf.getConstantPoolClass(), true);
      }

      Type fieldType = this.findType(cpf.getType());
      String fieldName = cpf.getName();
      Field field = classType.findField(this._compiler, fieldName, fieldType, isStatic, true);
      if (field != null) {
         if (!isStatic && field.getOffset() >= 512) {
            throw new CompileException(classType.getFullName(), ((StringBuffer)(new Object("Field offset too large for: "))).append(field.getName()).toString());
         }
      } else {
         this._method.addModifiers(134217728);
         int modifiers = 134217856;
         modifiers |= isStatic ? 2 : 4;
         int offset = -1;
         if (fieldName.startsWith("RIM_pragma")) {
            modifiers |= 64;
            field = new Field(fieldName, fieldType, classType, modifiers, offset, new Constant(""));
         } else {
            field = new Field(fieldName, fieldType, classType, modifiers, offset, null);
            if (classType.isDefined()) {
               this._compiler
                  .generateWarning(
                     false,
                     classType.getFullName(),
                     ((StringBuffer)(new Object("No definition found for member: "))).append(fieldType.getFullName()).append(' ').append(fieldName).toString()
                  );
            }
         }
      }

      if (field.is(131072)) {
         this._classType.addDataWeight(6 + fieldName.length());
      }

      cpf.setField(field);
      return field;
   }

   private final Method resolveMethod(ClassType classType, String name, String desc, boolean virtual, boolean isStatic) {
      Method method = null;
      synchronized (this._parmTypes) {
         this._parmTypes.setSize(0);
         TypeDescriptor prototype = new TypeDescriptor(desc);
         Type.translateTypes(this._compiler, prototype, this._parmTypes);
         Type type = Type.translateType(this._compiler, prototype);
         if (prototype.getCharsRemaining() != 0) {
            throw new CompileException(
               this._classType.getFullName(),
               ((StringBuffer)(new Object("Invalid type descriptor '"))).append(prototype.getString()).append("' for method: ").append(name).toString()
            );
         }

         method = classType.findMethod(this._compiler, name, type, this._parmTypes, isStatic, true);
         if (method != null) {
            ClassType methodClassType = method.getClassType();
            if (virtual && methodClassType.is(2048) && classType.is(32) && methodClassType != classType) {
               Method miranda = this._compiler.mirandize(classType, method);
               classType.addMethod(this._compiler, miranda);
               miranda.setImplements(this._compiler, method);
               method = miranda;
            }

            method.setReachable(this._compiler);
            if (method.is(131072)) {
               method.resolve(this._compiler);
            }
         } else {
            this._method.addModifiers(134217728);
            int num = this._parmTypes.size();
            method = new Method(classType, name, type, num, 134217856);

            for (int i = 0; i < num; i++) {
               method.addParameter(i, null, (Type)this._parmTypes.elementAt(i));
            }

            if (classType.isDefined()) {
               this._compiler
                  .generateWarning(
                     false, classType.getFullName(), ((StringBuffer)(new Object("No definition found for method: "))).append(method.getName()).toString()
                  );
            }
         }
      }

      if (method.is(131072)) {
         this._classType.addDataWeight(6 + name.length());
      }

      return method;
   }

   private final Method resolveMethod(ConstantPoolMethodRef cpm, boolean virtual, boolean isStatic) {
      ClassType classType = cpm.getClassType();
      if (classType == null) {
         classType = (ClassType)this.resolveClass(cpm.getConstantPoolClass(), true);
      }

      Method method = this.resolveMethod(classType, cpm.getName(), cpm.getType(), virtual, isStatic);
      cpm.setMethod(method);
      return method;
   }

   private final Method resolveMethod(ConstantPoolInterfaceMethodRef cpi) {
      ClassType classType = cpi.getClassType();
      if (classType == null) {
         classType = (ClassType)this.resolveClass(cpi.getConstantPoolClass(), true);
      }

      Method method = this.resolveMethod(classType, cpi.getName(), cpi.getType(), false, false);
      cpi.setMethod(method);
      return method;
   }

   private final int longOff(Field field) {
      return field.getSize() == 8 ? 2 : 0;
   }

   private final int libOff(ClassType classType) {
      return !classType.is(131072) && classType.isDefined() ? 0 : 1;
   }

   private final int libOff(ClassType referenceClassType, ClassType memberClassType) {
      return this.libOff(referenceClassType) == 0 && this.libOff(memberClassType) == 0 && referenceClassType == memberClassType ? 0 : 1;
   }

   private final int libOff(Method method) {
      return method.is(131072) ? 1 : 0;
   }

   private final int libOff(Method method, ClassType classType) {
      return this.libOff(method) == 0 && this.libOff(method.getClassType(), classType) == 0 ? 0 : 1;
   }

   private final int wideOff(int index) {
      return index > 255 ? 1 : 0;
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolLong cpl) {
      this._boundaries[ip] = true;
      long op = cpl.getValue();
      if (cpl.isDouble()) {
         this._block.addInstruction(ip, 215);
      }

      this._block.addInstructionLong(ip, 39, op);
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolInteger cpi) {
      this._boundaries[ip] = true;
      int op = cpi.getValue();
      if (cpi.isFloat()) {
         this._block.addInstruction(ip, 215);
      }

      this._block.addInstruction(ip, 38, op);
   }

   public static final boolean isUnicode(String s) {
      int len = s.length();

      for (int i = 0; i < len; i++) {
         char c = s.charAt(i);
         if ((c & '\uff00') != 0) {
            return true;
         }
      }

      return false;
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolString str) {
      this._boundaries[ip] = true;
      String s = str.getString();
      if (isUnicode(s)) {
         this._block.addInstructionString(ip, 42, s);
         this._classType.addDataWeight(s.length() * 2 + 4);
      } else {
         this._block.addInstructionString(ip, 40, s);
         this._classType.addDataWeight(s.length() + 4);
      }
   }

   public final void walkByteCode(int ip, int opcode, String[] sa) {
      this._boundaries[ip] = true;
      int dataWeight = 0;

      for (int i = sa.length - 1; i >= 0; i--) {
         String s = sa[i];
         dataWeight += 4;
         if (isUnicode(s)) {
            dataWeight += 2 * s.length();
         } else {
            dataWeight += s.length();
         }
      }

      this._block.addInstruction(ip, 216);
      this._block.addInstructionStringArray(ip, 282, sa);
      this._classType.addDataWeight(dataWeight);
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolClass cpc, int op, boolean isNew) {
      this._boundaries[ip] = true;
      if (isNew && this._compiler.isPreverified()) {
         this._newOrdinal++;
         int num = this._block.getNumTargets();

         for (int i = 0; i < num; i++) {
            InstructionTarget target = this._block.getTarget(i);
            InstructionStackEntry entry = target.getStackEntry();
            if (entry != null) {
               entry.fixupUninitializedOffsets(ip, this._newOrdinal);
            }
         }
      }

      Type type = cpc.getType();
      if (type == null) {
         type = this.resolveClass(cpc, isNew);
      }

      ClassType classType = null;
      ArrayType arrayType = null;
      switch (opcode) {
         case 187:
            classType = (ClassType)type;
            this._block.addInstructionType(ip, 184 + this.libOff(classType), classType, this._newOrdinal);
            break;
         case 189:
            if (type instanceof ClassType) {
               classType = (ClassType)type;
               this._block.addInstructionType(ip, 168 + this.libOff(classType), classType.getArrayType());
               return;
            }

            if (type instanceof ArrayType) {
               arrayType = type.getArrayType();
               type = arrayType.getMostBaseType();
               if (type instanceof BaseType) {
                  this._block.addInstructionType(ip, 166, arrayType, 1);
                  return;
               }

               if (type instanceof ClassType) {
                  classType = (ClassType)type;
                  this._block.addInstructionType(ip, 170 + this.libOff(classType), arrayType, 1);
                  return;
               }
            }
            break;
         case 192:
            if (type instanceof ClassType) {
               classType = (ClassType)type;
               this._block.addInstructionType(ip, 193 + this.libOff(classType), classType);
               return;
            }

            if (type instanceof ArrayType) {
               arrayType = (ArrayType)type;
               type = arrayType.getMostBaseType();
               if (type instanceof BaseType) {
                  this._block.addInstructionType(ip, 190, arrayType);
                  return;
               }

               if (type instanceof ClassType) {
                  classType = (ClassType)type;
                  this._block.addInstructionType(ip, 200 + this.libOff(classType), arrayType);
                  return;
               }
            }
            break;
         case 193:
            if (type instanceof ClassType) {
               classType = (ClassType)type;
               this._block.addInstructionType(ip, 191 + this.libOff(classType), classType);
               return;
            }

            if (type instanceof ArrayType) {
               arrayType = (ArrayType)type;
               type = arrayType.getMostBaseType();
               if (type instanceof BaseType) {
                  this._block.addInstructionType(ip, 189, arrayType);
                  return;
               }

               if (type instanceof ClassType) {
                  classType = (ClassType)type;
                  this._block.addInstructionType(ip, 198 + this.libOff(classType), arrayType);
                  return;
               }
            }
            break;
         case 197:
            arrayType = (ArrayType)type;
            type = arrayType.getMostBaseType();
            if (type instanceof BaseType) {
               this._block.addInstructionType(ip, 166, arrayType, op);
               return;
            }

            if (type instanceof ClassType) {
               classType = (ClassType)type;
               this._block.addInstructionType(ip, 170 + this.libOff(classType), arrayType, op);
               return;
            }
      }
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolFieldRef cpf) {
      this._boundaries[ip] = true;
      boolean isStatic = opcode == 178 || opcode == 179;
      Field field = cpf.getField();
      if (field == null) {
         field = this.resolveField(cpf, isStatic);
      }

      ClassType classType = cpf.getClassType();
      switch (opcode) {
         case 177:
            throw new CompileException(this._classType.getFullName(), ((StringBuffer)(new Object("Invalid opcode at offset: "))).append(ip).toString());
         case 178:
         case 179:
            if (field.hasValue() && field.isAnd(33554626)) {
               Type type = field.getType();
               if (!(type instanceof BaseType)) {
                  String s = field.getStringValue();
                  this._block.addInstructionString(ip, 40, s);
                  this._classType.addDataWeight(s.length() + 4);
                  return;
               } else {
                  int typeId = type.getTypeId();
                  if (typeId == 11 || typeId == 12) {
                     this._block.addInstruction(ip, 215);
                  }

                  if (field.getSize() == 8) {
                     this._block.addInstructionLong(ip, 39, field.getValue());
                     return;
                  } else {
                     this._block.addInstruction(ip, 38, (int)field.getValue());
                     return;
                  }
               }
            } else {
               this._block
                  .addInstructionNameAndType(ip, _opcodeMapping[opcode] + this.longOff(field) + this.libOff(classType, field.getClassType()), classType, field);
               field.allocateStatic();
               return;
            }
         case 180:
         case 181:
         default:
            this._block.addInstructionNameAndType(ip, _opcodeMapping[opcode] + this.longOff(field), classType, field);
      }
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolMethodRef cpm) {
      this._boundaries[ip] = true;
      Method method = cpm.getMethod();
      if (method == null) {
         method = this.resolveMethod(cpm, opcode == 182, opcode == 184);
      }

      ClassType classType = cpm.getClassType();
      switch (opcode) {
         case 181:
            throw new CompileException(this._classType.getFullName(), ((StringBuffer)(new Object("Invalid opcode at offset: "))).append(ip).toString());
         case 182:
         default:
            this._block.addInstructionNameAndType(ip, _opcodeMapping[opcode], classType, method);
            return;
         case 183:
            this._block.addInstructionNameAndType(ip, _opcodeMapping[opcode] + this.libOff(method), classType, method);
            return;
         case 184:
            this._block.addInstructionNameAndType(ip, _opcodeMapping[opcode] + this.libOff(method, classType), classType, method);
      }
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolInterfaceMethodRef cpm, int nargs) {
      this._boundaries[ip] = true;
      Method method = cpm.getMethod();
      if (method == null) {
         method = this.resolveMethod(cpm);
      }

      ClassType classType = cpm.getClassType();
      if (method.is(131072) && method.getClassType() == this._compiler.getObjectClass()) {
         opcode = 182;
         classType = method.getClassType();
      }

      switch (opcode) {
         case 182:
            this._block.addInstructionNameAndType(ip, _opcodeMapping[opcode], classType, method);
            return;
         case 185:
            this._block.addInstructionNameAndType(ip, _opcodeMapping[opcode], classType, method, nargs);
            return;
         default:
            throw new CompileException(this._classType.getFullName(), ((StringBuffer)(new Object("Invalid opcode at offset: "))).append(ip).toString());
      }
   }

   public final void walkByteCode(int ip, int opcode, int[] operands) {
      this._boundaries[ip] = true;
      int op = 0;
      int newOp = -1;
      if (opcode < _opcodeMapping.length) {
         newOp = _opcodeMapping[opcode];
      }

      if (newOp != -1) {
         if (newOp > 255) {
            this._block.addInstruction(ip, 216);
         }

         if (opcode == 175 || opcode == 174) {
            this._block.addInstruction(ip, 215);
         }

         this._block.addInstruction(ip, newOp);
      } else {
         switch (opcode) {
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
               this._block.addInstruction(ip, 36, opcode - 3);
               return;
            case 9:
            case 10:
               this._block.addInstructionLong(ip, 39, opcode - 9);
               return;
            case 11:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 36);
               return;
            case 12:
            case 13:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 38, 127 + opcode - 12 << 23);
               return;
            case 14:
               this._block.addInstruction(ip, 215);
               this._block.addInstructionLong(ip, 39, 0);
               return;
            case 15:
               this._block.addInstruction(ip, 215);
               this._block.addInstructionLong(ip, 39, 4607182418800017408L);
               return;
            case 16:
               this._block.addInstruction(ip, 36, operands[0]);
               return;
            case 17:
               this._block.addInstruction(ip, 37, operands[0]);
               return;
            case 18:
            case 19:
               this._block.addInstruction(ip, 38, operands[0]);
               return;
            case 23:
               this._block.addInstruction(ip, 215);
            case 21:
               op = operands[0];
               if (op <= 7) {
                  this._block.addInstruction(ip, 55 + op);
                  return;
               }

               this._block.addInstruction(ip, 49 + this.wideOff(op), op);
               return;
            case 24:
               this._block.addInstruction(ip, 215);
            case 22:
               op = operands[0];
               this._block.addInstruction(ip, 53 + this.wideOff(op), op);
               return;
            case 25:
               op = operands[0];
               if (op <= 7) {
                  this._block.addInstruction(ip, 63 + op);
                  return;
               }

               this._block.addInstruction(ip, 51 + this.wideOff(op), op);
               return;
            case 26:
            case 27:
            case 28:
            case 29:
               this._block.addInstruction(ip, 55 + opcode - 26);
               return;
            case 30:
            case 31:
            case 32:
            case 33:
               this._block.addInstruction(ip, 53, opcode - 30);
               return;
            case 34:
            case 35:
            case 36:
            case 37:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 55 + opcode - 34);
               return;
            case 38:
            case 39:
            case 40:
            case 41:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 53, opcode - 38);
               return;
            case 42:
            case 43:
            case 44:
            case 45:
               this._block.addInstruction(ip, 63 + opcode - 42);
               return;
            case 48:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 175);
               return;
            case 49:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 177);
               return;
            case 56:
               this._block.addInstruction(ip, 215);
            case 54:
               op = operands[0];
               if (op <= 7) {
                  this._block.addInstruction(ip, 77 + op);
                  return;
               }

               this._block.addInstruction(ip, 71 + this.wideOff(op), op);
               return;
            case 57:
               this._block.addInstruction(ip, 215);
            case 55:
               op = operands[0];
               this._block.addInstruction(ip, 75 + this.wideOff(op), op);
               return;
            case 58:
               op = operands[0];
               if (op <= 7) {
                  this._block.addInstruction(ip, 85 + op);
                  return;
               }

               this._block.addInstruction(ip, 73 + this.wideOff(op), op);
               return;
            case 59:
            case 60:
            case 61:
            case 62:
               this._block.addInstruction(ip, 77 + opcode - 59);
               return;
            case 63:
            case 64:
            case 65:
            case 66:
               this._block.addInstruction(ip, 75, opcode - 63);
               return;
            case 67:
            case 68:
            case 69:
            case 70:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 77 + opcode - 67);
               return;
            case 71:
            case 72:
            case 73:
            case 74:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 75, opcode - 71);
               return;
            case 75:
            case 76:
            case 77:
            case 78:
               this._block.addInstruction(ip, 85 + opcode - 75);
               return;
            case 81:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 181);
               return;
            case 82:
               this._block.addInstruction(ip, 215);
               this._block.addInstruction(ip, 183);
               return;
            case 132:
               op = operands[1];
               int lcl = operands[0];
               if (op < 128 && op >= -128 && lcl <= 255) {
                  this._block.addInstructionLong(ip, 120, lcl, op);
                  return;
               }

               this._block.addInstructionLong(ip, 121, lcl, op);
               return;
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
               InstructionTarget var13 = this.getBranchTarget(operands[0]);
               this._block.addInstructionBranch(ip, _ifMapping[opcode - 153]);
               this._block.addBranchTarget(var13);
               return;
            case 167:
            case 200: {
               InstructionTarget target = this.getBranchTarget(operands[0]);
               this._block.addInstructionBranch(ip, 161);
               this._block.addBranchTarget(target);
               return;
            }
            case 170:
               int num = operands.length - 3;
               int[] cases = new int[num];
               boolean canBeShort = true;
               op = operands[1];

               for (int i = 0; i < num; i++) {
                  if (op != (short)op) {
                     canBeShort = false;
                  }

                  cases[i] = op++;
               }

               boolean malformed = operands[1] > operands[2];
               if (malformed) {
                  this._compiler
                     .generateWarning(
                        false,
                        this._classType.getFullName(),
                        ((StringBuffer)(new Object("Malformed tableswitch opcode found in: "))).append(this._method.getName()).toString()
                     );
               }

               this._block.addInstructionInts(ip, canBeShort ? 163 : 164, cases, malformed);
               this._block.addBranchTarget(this.getBranchTarget(operands[0]));

               for (int i = 3; i < operands.length; i++) {
                  this._block.addBranchTarget(this.getBranchTarget(operands[i]));
               }
               break;
            case 171:
               int num = operands[1];
               int[] cases = new int[num];
               boolean canBeShort = true;

               for (int i = 0; i < num; i++) {
                  op = operands[i * 2 + 2];
                  if (op != (short)op) {
                     canBeShort = false;
                  }

                  cases[i] = op;
               }

               boolean malformed = num < 0 || num > 4096;
               if (malformed) {
                  this._compiler
                     .generateWarning(
                        false,
                        this._classType.getFullName(),
                        ((StringBuffer)(new Object("Malformed lookupswitch opcode found in: "))).append(this._method.getName()).toString()
                     );
               }

               this._block.addInstructionInts(ip, canBeShort ? 163 : 164, cases, malformed);
               this._block.addBranchTarget(this.getBranchTarget(operands[0]));

               for (int i = 3; i < operands.length; i += 2) {
                  this._block.addBranchTarget(this.getBranchTarget(operands[i]));
               }
               break;
            case 177:
               if (this._method.is(1048576)) {
                  this._block.addInstruction(ip, 32);
                  return;
               }

               this._block.addInstruction(ip, 31);
               return;
            case 188:
               this._block.addInstruction(ip, 165, operands[0]);
               return;
            case 198:
            case 199: {
               InstructionTarget target = this.getBranchTarget(operands[0]);
               this._block.addInstructionBranch(ip, 159 + opcode - 198);
               this._block.addBranchTarget(target);
               return;
            }
            default:
               throw new CompileException(this._classType.getFullName(), ((StringBuffer)(new Object("Invalid opcode at offset: "))).append(ip).toString());
         }
      }
   }

   public final void walkByteCode(int ip, int opcode, ConstantPoolArrayData cpa) {
      this._boundaries[ip] = true;
      byte[] data = cpa.getBytes();
      ConstantPoolFieldRef cpf = cpa.getFieldRef();
      if (cpf != null) {
         this._compiler.checkBinaryForExport(((StringBuffer)(new Object())).append(cpf.getClassName()).append('.').append(cpf.getName()).toString(), data);
      }

      int typeId = cpa.getArrayType();
      this._block.addInstructionBytes(ip, 45, typeId, data);
      this._classType.addDataWeight(data.length + 4);
   }

   public final void resolveBadLabel() {
      int num = this._block.getNumTargets();

      for (int i = 0; i < num; i++) {
         InstructionTarget target = this._block.getTarget(i);
         if (!this._boundaries[target.getIp()]) {
            this._block.markBadTarget(i, target);
         }

         InstructionStackEntry entry = target.getStackEntry();
         if (entry == null) {
            if (target.getOp() != 0) {
               throw new CompileException(
                  this._classType.getFullName(), ((StringBuffer)(new Object("Missing stack map at label: "))).append(target.getIp()).toString()
               );
            }
         } else {
            entry.verifyUninitializedOffsets();
            if (target.getOp() == 0) {
               if (Compiler._verbosity >= 2) {
                  Diagnose.out
                     .println(
                        ((StringBuffer)(new Object("Funny Stackmap in: ")))
                           .append(this._classType.getFullName())
                           .append(".")
                           .append(this._method.getName())
                           .toString()
                     );
               }

               this._method.addModifiers(134217728);
            }
         }
      }
   }
}
