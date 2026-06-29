package net.rim.ecmascript.runtime;

import net.rim.ecmascript.compiler.CompileError;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.util.Resources;

class GlobalProperties$2 extends HostFunction {
   GlobalProperties$2(String x0, String x1, int x2) {
      super(x0, x1, x2);
   }

   @Override
   public long run() {
      if (!this.getContext().evalAllowed()) {
         throw ThrownValue.evalError(Resources.getString(44));
      }

      long parm = this.getParm(0);
      String codeString;
      switch (Value.getType(parm)) {
         case 5:
            codeString = Value.getStringValue(parm);
            break;
         default:
            codeString = null;
      }

      if (codeString == null) {
         return parm;
      }

      codeString = ((StringBuffer)(new Object())).append(codeString).append("\n").toString();
      GlobalObject global = this.getGlobalInstance();

      CompiledScript code;
      try {
         code = new Compiler(codeString, this.getVersion(), global.debugOut, global.compilerFlags + 67108864).compile();
      } catch (CompileError ce) {
         throw ThrownValue.syntaxError(ce.toString());
      }

      try {
         return Context.run(this.getContext().makeEvalContext(global, code));
      } catch (ThrownValue th) {
         throw ThrownValue.errorInEval(th);
      }
   }
}
