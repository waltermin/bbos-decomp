package net.rim.ecmascript.runtime;

import java.util.Vector;
import net.rim.ecmascript.util.Misc;
import net.rim.vm.Reflect;

class JavaClass extends RedirectedObject {
   private String _name;
   private String _shortName;
   private Vector _instanceFields;
   private Vector _instanceNames;
   private HostFunction _constructor;
   private ESObject _instanceMethods = new ESObject();
   private int _class;
   private boolean _haveFields;

   JavaClass(String name, String shortName, Class clazz) {
      this(name, shortName, Reflect.getClassClass(clazz));
   }

   JavaClass(String name, String shortName, int clazz) {
      super("JavaClass", GlobalObject.getInstance().objectPrototype);
      this._name = name;
      this._shortName = shortName;
      this._instanceFields = (Vector)(new Object());
      this._instanceNames = (Vector)(new Object());
      this._class = clazz;
      this.getConstructors();
   }

   int getJavaClass() {
      return this._class;
   }

   long getInstanceMethod(String name) {
      return this.getMethodValue(this._instanceMethods, name, false);
   }

   HostFunction getConstructor() {
      return this._constructor;
   }

   static JavaClass findClass(Object o) {
      String name = Reflect.getClassName(Reflect.getObjectClass(o));
      JavaClass jc = (JavaClass)GlobalObject.getInstance().javaClassLookupTable.get(name);
      return jc != null ? jc : JavaPackage.createClass(name);
   }

   void defineFields(JavaObject obj) {
      if (!this._haveFields) {
         this.getFields();
         this._haveFields = true;
      }

      for (int i = this._instanceFields.size() - 1; i >= 0; i--) {
         JavaField f = (JavaField)this._instanceFields.elementAt(i);
         obj.addField((String)this._instanceNames.elementAt(i), 5, Value.makeObjectValue(f));
      }
   }

   private void addFunction(ESObject obj, HostFunction hf) {
      if (hf instanceof JavaMethod) {
         JavaMethod jm = (JavaMethod)hf;
         obj.addField(jm.getShortName(), 5, Value.makeObjectValue(jm));
         obj.addField(jm.getName(), 5, Value.makeObjectValue(jm));
      } else {
         JavaOverloadedMethod jom = (JavaOverloadedMethod)hf;
         String name = jom.getName();
         obj.addField(name, 5, Value.makeObjectValue(jom));

         for (int j = jom.getNumOverloads() - 1; j >= 0; j--) {
            JavaMethod jm = (JavaMethod)jom.getOverload(j);
            obj.addField(jm.getName(), 5, Value.makeObjectValue(jm));
         }

         jom.doneOverloading();
      }
   }

   @Override
   void enumerate(Vector v) {
      this._instanceMethods.enumerate(v);
      super.enumerate(v);
   }

   private static String buildParmDescriptor(String name, int[] parms) {
      StringBuffer b = (StringBuffer)(new Object());
      b.append(name);
      b.append("(");
      int nParms = parms.length;
      if (nParms != 0) {
         b.append(Reflect.getClassName(parms[0]));

         for (int i = 1; i < nParms; i++) {
            b.append(",");
            b.append(Reflect.getClassName(parms[i]));
         }
      }

      b.append(")");
      return Misc.stringIntern(b.toString());
   }

   private void getFields() {
      for (int f : Reflect.getFields(this._class, false)) {
         boolean isStatic = Reflect.isStaticField(this._class, f);
         String name = Reflect.getFieldName(this._class, f);
         if (isStatic) {
            if (!this.hasOwnField(name)) {
               this.addField(name, 5, Value.makeObjectValue(JavaField.makeJavaField(this._class, f)));
            }
         } else if (!this._instanceNames.contains(name)) {
            this._instanceNames.addElement(name);
            this._instanceFields.addElement(JavaField.makeJavaField(this._class, f));
         }
      }
   }

   private void getMethods(ESObject obj, String name, boolean wantStatic) {
      JavaOverloadedMethod jom = new JavaOverloadedMethod(this, name);

      for (int m : Reflect.getMethods(name, this._class, false)) {
         boolean isStatic = Reflect.isStaticMethod(this._class, m);
         if (isStatic ? wantStatic : !wantStatic) {
            int returnType = Reflect.getReturnType(this._class, m);
            int[] parms = Reflect.getParameterTypes(this._class, m);
            jom.addOverload(new JavaMethod(this, name, buildParmDescriptor(name, parms), this._class, m, returnType, parms));
         }
      }

      HostFunction hf = jom.unOverload();
      if (hf != null) {
         this.addFunction(obj, hf);
      }
   }

   private void getConstructors() {
      int[] constructors = Reflect.getConstructors(this._class, false);
      if ((Reflect.getClassAttributes(this._class) & 32) != 0) {
         this._constructor = new JavaInterface(this._class);
      } else if (constructors.length != 0) {
         int numConstructors = constructors.length;
         if (numConstructors == 1) {
            int c = constructors[0];
            int[] parms = Reflect.getParameterTypes(this._class, c);
            this._constructor = new JavaConstructor(this, buildParmDescriptor("", parms), this._class, c, parms);
            this.addField(this._constructor.getName(), 5, Value.makeObjectValue(this._constructor));
         } else {
            JavaOverloadedConstructor joc = new JavaOverloadedConstructor(this, this._shortName);
            this._constructor = joc;

            for (int c : constructors) {
               int[] parms = Reflect.getParameterTypes(this._class, c);
               JavaConstructor overload = new JavaConstructor(this, buildParmDescriptor("", parms), this._class, c, parms);
               this.addField(overload.getName(), 5, Value.makeObjectValue(overload));
               joc.addOverload(overload);
            }

            joc.doneOverloading();
         }
      }
   }

   static JavaField getJavaField(long value, String name) {
      if (Value.getType(value) != 2) {
         Object o = Value.checkIfObjectValue(value);
         if (o != null && o instanceof JavaField) {
            return (JavaField)o;
         }
      }

      return null;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      if (!this._haveFields) {
         this.getFields();
         this._haveFields = true;
      }

      long fieldValue = this.noRedirectGetField(name);
      JavaField f = getJavaField(fieldValue, name);
      if (f != null) {
         f.put(null, value);
      }

      return true;
   }

   private long getMethodValue(ESObject obj, String name, boolean wantStatic) {
      long value = obj.noRedirectGetField(name);
      if (value != Value.UNDEFINED) {
         return value;
      }

      int paren = name.indexOf(40);
      String baseName = name;
      if (paren != -1) {
         baseName = Misc.stringIntern(name.substring(0, paren));
      }

      this.getMethods(obj, baseName, wantStatic);
      return obj.noRedirectGetField(name);
   }

   @Override
   public long requestFieldValue(String name) {
      long value = this.getOwnField(name);
      if (Value.getType(value) == 2) {
         if (!this._haveFields) {
            this.getFields();
            this._haveFields = true;
            value = this.getOwnField(name);
         }

         if (Value.getType(value) == 2) {
            value = this.getMethodValue(this, name, true);
         }
      }

      JavaField f = Value.checkIfJavaFieldValue(value);
      return f != null ? f.get(null) : value;
   }

   @Override
   public long defaultStringValue() {
      return Value.makeStringValue(((StringBuffer)(new Object("[JavaClass "))).append(this._name).append("]").toString());
   }

   void export(ESObject toObject) {
      toObject.putField(this._shortName, Value.makeObjectValue(this));
   }

   String getName() {
      return this._name;
   }
}
