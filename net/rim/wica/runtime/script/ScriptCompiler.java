package net.rim.wica.runtime.script;

import java.io.InputStream;
import net.rim.device.api.io.NoCopyByteArrayOutputStream;
import net.rim.ecmascript.compiler.Compiler;
import net.rim.ecmascript.runtime.CompiledScript;

public final class ScriptCompiler {
   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static final byte[] compile(byte[] source) {
      byte[] compiled = null;

      try {
         CompiledScript cs = compileScript((String)(new Object(source, "UTF-8")));
         NoCopyByteArrayOutputStream bout = (NoCopyByteArrayOutputStream)(new Object());
         cs.serialize(bout);
         return bout.toByteArray();
      } catch (Throwable var5) {
         throw new CompilerException(e.getMessage());
      }
   }

   public static final CompiledScript compileScript(String source) {
      Compiler c = (Compiler)(new Object(source));
      return c.compile();
   }

   public static final CompiledScript deserialize(byte[] serializedScript) {
      return CompiledScript.deserialize((InputStream)(new Object(serializedScript)));
   }
}
