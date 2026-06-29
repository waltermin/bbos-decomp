package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.CompileError;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.compiler.Tokenizer;
import net.rim.ecmascript.util.Misc;
import net.rim.ecmascript.util.Resources;

public class ESFunction extends RedirectedObject {
   private String _compileError;
   private Compiler _compiler;
   private CompiledScript _code;
   private ScopeChain _scope;
   private long[] _protoValue;
   ESObject _protoObject;
   private GlobalObject _globalObject;

   private void init(CompiledScript code, Compiler compiler, ScopeChain scope) {
      this.setFunctionType(1);
      this._protoValue = null;
      this._compiler = compiler;
      this._code = code;
      this._scope = scope;
      this._globalObject = GlobalObject.getInstance();
   }

   ESFunction(Compiler compiler, ScopeChain s) {
      super("Function", GlobalObject.getInstance().functionPrototype);
      this.init(null, compiler, s);
   }

   ESFunction(CompiledScript c, ScopeChain s) {
      super("Function", GlobalObject.getInstance().functionPrototype);
      this.init(c, null, s);
   }

   ESFunction(CompiledScript c, ScopeChain s, boolean nullPrototypeOK) {
      super("Function", GlobalObject.getInstance().functionPrototype, nullPrototypeOK);
      this.init(c, null, s);
   }

   @Override
   public long requestFieldValue(String name) throws BuildArgumentsException, GetFunctionLengthException {
      if (name == "prototype") {
         if (this._protoValue == null) {
            ESObject protoObject = new ESObject();
            protoObject.addField("constructor", 6, Value.makeObjectValue(this));
            this._protoValue = Misc.newMixedArray(1);
            this._protoValue[0] = Value.makeObjectValue(protoObject);
            this._protoObject = protoObject;
         }

         return this._protoValue[0];
      } else if (name == "length") {
         switch (GlobalObject.getInstance().version) {
            case 120:
               throw new GetFunctionLengthException(this);
            default:
               return Value.makeIntegerValue(this.getCode().numParms);
         }
      } else if (name == "arity") {
         return Value.makeIntegerValue(this.getCode().numParms);
      } else if (name == "name") {
         return Value.makeStringValue(this.getCode().getId());
      } else if (name == "arguments") {
         throw new BuildArgumentsException(this);
      } else {
         return Value.DEFAULT;
      }
   }

   @Override
   public int notifyFieldDeleted(String name) {
      return name != "prototype" && name != "length" && name != "arity" && name != "name" ? 2 : 1;
   }

   @Override
   public boolean notifyFieldChanged(String name, long value) {
      if (name == "prototype") {
         if (this._protoValue == null) {
            this._protoValue = Misc.newMixedArray(1);
         }

         this._protoValue[0] = value;
         this._protoObject = Value.checkIfObjectValue(value);
         return false;
      } else {
         return name != "length" && name != "arity" && name != "name";
      }
   }

   CompiledScript getCode() throws ThrownValue {
      if (this._code == null) {
         if (this._compiler != null) {
            try {
               Compiler compiler = this._compiler;
               this._compiler = null;
               this._code = compiler.compile().getFirstFunction();
               GlobalObject.getInstance().linkGlobals(compiler.getGlobalCompiledCode());
            } catch (CompileError ce) {
               this._compileError = ce.toString();
            }
         }

         if (this._code == null && this._compileError != null) {
            throw ThrownValue.syntaxError(this._compileError);
         }
      }

      return this._code;
   }

   ScopeChain getScope() {
      return this._scope;
   }

   boolean hasInstance(long objValue) throws ThrownValue {
      ESObject v = Value.checkIfObjectValue(objValue);
      if (v == null) {
         return false;
      }

      long vValue = this.getField("prototype");
      ESObject o = Value.checkIfObjectValue(vValue);
      if (o == null) {
         throw ThrownValue.typeError(Resources.getString(63), Convert.toString(vValue), Convert.toString(Value.makeObjectValue(this)));
      }

      do {
         v = v.getPrototype();
         if (v == null) {
            return false;
         }
      } while (o != v);

      return true;
   }

   String getSource() {
      return Tokenizer.getSource(this.getCode());
   }

   void setGlobalObject(GlobalObject global) {
      this._globalObject = global;
   }

   GlobalObject getGlobalObject() {
      return this._globalObject;
   }
}
