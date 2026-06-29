package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.CompileError;
import net.rim.ecmascript.compiler.Compiler;

class ESFunctionPrototype$Constructor extends Constructor {
   ESFunctionPrototype$Constructor() {
      super("Function", GlobalObject.getInstance().functionPrototype);
   }

   @Override
   public long run() throws ThrownValue {
      StringBuffer fd = (StringBuffer)(new Object());
      if (this.getVersion() == 120) {
         fd.append("function (");
      } else {
         fd.append("function anonymous(");
      }

      int i = 0;

      int nParms;
      for (nParms = this.getNumParms(); i < nParms - 2; i++) {
         fd.append(Convert.toString(this.getParm(i)));
         fd.append(",");
      }

      if (i < nParms - 1) {
         fd.append(Convert.toString(this.getParm(i)));
         i++;
      }

      fd.append(") {");
      if (i < nParms) {
         fd.append(Convert.toString(this.getParm(i)));
      }

      fd.append("}");
      GlobalObject global = this.getGlobalInstance();
      Compiler compiler = new Compiler(fd.toString(), this.getVersion(), global.debugOut, global.compilerFlags + 33554432);
      ScopeChain scope = new ScopeChain(null, global, global);
      ESFunction function;
      if (global.getAllowDelayedCompilation()) {
         function = new ESFunction(compiler, scope);
      } else {
         try {
            function = new ESFunction(compiler.compile().getFirstFunction(), scope);
            global.linkGlobals(compiler.getGlobalCompiledCode());
         } catch (CompileError ce) {
            throw ThrownValue.syntaxError(ce.toString());
         }
      }

      return Value.makeObjectValue(function);
   }
}
