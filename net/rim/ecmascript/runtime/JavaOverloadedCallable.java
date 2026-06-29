package net.rim.ecmascript.runtime;

import java.util.Vector;

class JavaOverloadedCallable extends HostFunction {
   private JavaCallable[] _overloads;
   private int[] _overloadIndexes;
   private Vector _overloadVector;
   private JavaClass _javaClass;
   private int _nOverloads;

   JavaOverloadedCallable(String type, JavaClass jc, String name, boolean isConstructor) {
      super(type, name, 0, isConstructor);
      this._javaClass = jc;
      this._overloadVector = (Vector)(new Object());
      this._nOverloads = 0;
   }

   void addOverload(JavaCallable overload) {
      for (int i = 0; i < this._nOverloads; i++) {
         if (((JavaCallable)this._overloadVector.elementAt(i)).getName() == overload.getName()) {
            return;
         }
      }

      this._overloadVector.addElement(overload);
      this._nOverloads++;
   }

   void doneOverloading() {
      this._overloads = new JavaCallable[this._nOverloads];

      for (int i = this._nOverloads - 1; i >= 0; i--) {
         this._overloads[i] = (JavaCallable)this._overloadVector.elementAt(i);
      }

      this._overloadIndexes = new int[this._nOverloads];
      this._overloadVector = null;
   }

   JavaClass getJavaClass() {
      return this._javaClass;
   }

   JavaCallable findOverload() {
      if (this._nOverloads == 1) {
         return this._overloads[0];
      }

      int nPossibles = 0;
      int nParms = this.getNumParms();

      for (int i = 0; i < this._nOverloads; i++) {
         JavaCallable jc = this._overloads[i];
         if (jc.getRequiredNumParms() == nParms) {
            this._overloadIndexes[nPossibles] = i;
            nPossibles++;
         }
      }

      if (nPossibles == 1) {
         return this._overloads[this._overloadIndexes[0]];
      } else {
         throw ThrownValue.internalError("NYI: Java Overloading");
      }
   }

   int getNumOverloads() {
      return this._nOverloads;
   }

   JavaCallable getOverload(int i) {
      return (JavaCallable)this._overloadVector.elementAt(i);
   }
}
