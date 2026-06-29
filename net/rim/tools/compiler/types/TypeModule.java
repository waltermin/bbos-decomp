package net.rim.tools.compiler.types;

import java.util.Vector;
import net.rim.tools.compiler.Compiler;
import net.rim.tools.compiler.Host;
import net.rim.tools.compiler.codfile.Bytes;
import net.rim.tools.compiler.codfile.ClassDef;
import net.rim.tools.compiler.codfile.Codfile;
import net.rim.tools.compiler.codfile.DataSection;
import net.rim.tools.compiler.codfile.ExportedData;
import net.rim.tools.compiler.codfile.Module;
import net.rim.tools.compiler.util.CompileException;
import net.rim.tools.compiler.vm.Constants;

public final class TypeModule implements Constants {
   private String _name;
   private String _version;
   private int _timeStamp;
   private Codfile _codfile;
   private Vector _classes;
   private StringBuffer _undefinedClasses;
   private int _codeWeight;
   private int _dataWeight;
   private int _vtableWeight;
   private int _fieldWeight;
   private int _icallIndex;
   private int _staticSize;
   private int _maxTypeListSize;
   private Module[] _modules;
   private int _ordinal;
   private int _count;
   private static final String UNDF;

   public TypeModule(String name, String version, int timeStamp, Codfile codfile) {
      this._name = name;
      this._version = version;
      this._timeStamp = timeStamp;
      this._codfile = codfile;
      this._dataWeight = 74;
      this._ordinal = -1;
      this._count = 1;
   }

   public final void setOrdinalCount(int ordinal, int count) {
      this._ordinal = ordinal;
      this._count = count;
      this.getModule(this);
   }

   public final String getName() {
      return this._name;
   }

   public final Codfile getCodfile() {
      return this._codfile;
   }

   public final DataSection getDataSection() {
      return this._codfile.getDataSection();
   }

   public final Module getModule(TypeModule typeModule) {
      if (this._modules == null) {
         this._modules = new Module[typeModule.getCount()];
      }

      int ordinal = typeModule.getOrdinal();
      if (this._modules[ordinal] == null) {
         boolean isDomestic = this._ordinal != -1 && this != typeModule;
         DataSection dataSection = typeModule.getDataSection();
         this._modules[ordinal] = dataSection.getModule(this._name, this._version, this._timeStamp, isDomestic, false);
      }

      return this._modules[ordinal];
   }

   public final int getOrdinal() {
      return this._ordinal;
   }

   public final int getCount() {
      return this._count;
   }

   public final int getNumClasses() {
      Vector v = this._classes;
      return v == null ? 0 : v.size();
   }

   public final void addClass(ClassType classType) {
      classType.setTypeModule(this);
      Vector v = this._classes;
      if (v == null) {
         v = (Vector)(new Object());
         this._classes = v;
      } else {
         int num = v.size();

         for (int i = 0; i < num; i++) {
            if (classType.codfileOrder((ClassType)v.elementAt(i)) < 0) {
               v.insertElementAt(classType, i);
               return;
            }
         }
      }

      v.addElement(classType);
   }

   public final void optimize() {
      Vector v = this._classes;
      if (v == null) {
         throw new CompileException("No classes found");
      }

      int num = v.size();

      for (int i = 0; i < num; i++) {
         ClassType classType = (ClassType)v.elementAt(i);
         classType.getClassDef(this);
      }
   }

   public final void populate(Compiler compiler, int flags) {
      Host host = compiler.getHost();
      Vector v = this._classes;
      int num = v.size();

      for (int i = 0; i < num; i++) {
         ClassType classType = (ClassType)v.elementAt(i);
         classType.populate(compiler);
         host.advanceProgress(-1);
      }

      this._classes = null;
      this._codfile.setFlags(flags);
      this._codfile.setTimeStamp(this._timeStamp);
      this._codfile.setMaxTypeListSize(this._maxTypeListSize);
      DataSection dataSection = this.getDataSection();
      dataSection.setStaticSize(this._staticSize);
      dataSection.setIcallIndex(this._icallIndex);
      if (this._undefinedClasses != null) {
         label31:
         try {
            byte[] data = this._undefinedClasses.toString().getBytes("UTF-8");
            Bytes bytes = dataSection.getDataBytes().getBytes(data, 2, false);
            dataSection.addExport(new ExportedData(dataSection, bytes, ".UNDF"));
         } finally {
            break label31;
         }

         this._undefinedClasses = null;
      }
   }

   public final void addUndefinedClass(String className) {
      int weight = className.length() * 2;
      if (this._undefinedClasses == null) {
         this._undefinedClasses = (StringBuffer)(new Object());
         weight += 13;
      } else {
         this._undefinedClasses.append(',');
         weight++;
      }

      this.addDataWeight(weight);
      this._undefinedClasses.append(className);
   }

   public final ClassDef makeClassDef(TypeModule typeModule, String packageName, String className) {
      return this.getModule(typeModule).makeClassDef(typeModule.getDataSection(), packageName, className);
   }

   public final ClassDef getNullClassDef(TypeModule typeModule) {
      return this.getModule(typeModule).getNullClassDef();
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

   public final void addVtableWeight(int weight) {
      this._vtableWeight += weight;
   }

   public final int getVtableWeight() {
      return this._vtableWeight;
   }

   public final void addFieldWeight(int weight) {
      this._fieldWeight += weight;
   }

   public final int getFieldWeight() {
      return this._fieldWeight;
   }

   public final int getIcallIndex() {
      return this._icallIndex++;
   }

   public final int allocateStaticData(int need) {
      int offset = this._staticSize;
      this._staticSize += need;
      return offset;
   }

   public final void setMaxTypeListSize(int maxTypeListSize) {
      if (maxTypeListSize > this._maxTypeListSize) {
         this._maxTypeListSize = maxTypeListSize;
      }
   }
}
