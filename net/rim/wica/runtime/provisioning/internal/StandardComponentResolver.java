package net.rim.wica.runtime.provisioning.internal;

import net.rim.wica.runtime.provisioning.internal.elements.DataElement;
import net.rim.wica.runtime.provisioning.internal.elements.EnumerationElement;
import net.rim.wica.runtime.provisioning.internal.elements.WicletElement;

public class StandardComponentResolver {
   boolean _builtComponents;
   UniqueCodeGenerator _uniqueCodeGenerator;
   WicletElement _wiclet;
   private static final String STANDARD_COMPONENTS_DEFINITION_FILE = "standardcomponents.xml";
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$DataElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$FieldElement;
   static Class class$net$rim$wica$runtime$provisioning$internal$elements$EnumerationElement;

   public StandardComponentResolver(WicletElement wiclet, UniqueCodeGenerator gen) {
      this._uniqueCodeGenerator = gen;
      this._wiclet = wiclet;
   }

   public boolean isStandardComponent(String standardComponentName) {
      return this._uniqueCodeGenerator.isStandardComponent(standardComponentName);
   }

   public DataElement resolveDataElement(String standardComponentName) {
      if (this.isStandardComponent(standardComponentName)) {
         this.buildStandardComponents();
      }

      return this._wiclet.getDataElement(standardComponentName);
   }

   public EnumerationElement resolveEnumerationElement(String standardComponentName) {
      if (this.isStandardComponent(standardComponentName)) {
         this.buildStandardComponents();
      }

      return this._wiclet.getEnumerationElement(standardComponentName);
   }

   public void buildStandardComponents() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield net/rim/wica/runtime/provisioning/internal/StandardComponentResolver._builtComponents Z
      // 04: ifne 50
      // 07: new net/rim/wica/runtime/provisioning/internal/digester/Digester
      // 0a: dup
      // 0b: invokespecial net/rim/wica/runtime/provisioning/internal/digester/Digester.<init> ()V
      // 0e: astore 1
      // 0f: new net/rim/wica/runtime/provisioning/internal/StandardComponentResolver$StandardComponentsRecipe
      // 12: dup
      // 13: aconst_null
      // 14: invokespecial net/rim/wica/runtime/provisioning/internal/StandardComponentResolver$StandardComponentsRecipe.<init> (Lnet/rim/wica/runtime/provisioning/internal/StandardComponentResolver$1;)V
      // 17: astore 2
      // 18: aload 2
      // 19: aload 1
      // 1a: invokevirtual net/rim/wica/runtime/provisioning/internal/StandardComponentResolver$StandardComponentsRecipe.prepare (Lnet/rim/wica/runtime/provisioning/internal/digester/Digester;)Lnet/rim/wica/runtime/provisioning/internal/digester/Digester;
      // 1d: astore 1
      // 1e: aload 1
      // 1f: aload 0
      // 20: getfield net/rim/wica/runtime/provisioning/internal/StandardComponentResolver._wiclet Lnet/rim/wica/runtime/provisioning/internal/elements/WicletElement;
      // 23: invokevirtual net/rim/wica/runtime/provisioning/internal/digester/Digester.push (Ljava/lang/Object;)V
      // 26: invokestatic net/rim/device/resources/Resource.getResourceClass ()Lnet/rim/device/resources/Resource;
      // 29: astore 3
      // 2a: aload 3
      // 2b: ldc_w "standardcomponents.xml"
      // 2e: invokevirtual net/rim/device/resources/Resource.getResource (Ljava/lang/String;)[B
      // 31: astore 4
      // 33: new java/lang/Object
      // 36: dup
      // 37: aload 4
      // 39: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 3c: astore 5
      // 3e: aload 1
      // 3f: aload 5
      // 41: invokevirtual net/rim/wica/runtime/provisioning/internal/digester/Digester.parse (Ljava/io/InputStream;)Ljava/lang/Object;
      // 44: pop
      // 45: aload 0
      // 46: bipush 1
      // 47: putfield net/rim/wica/runtime/provisioning/internal/StandardComponentResolver._builtComponents Z
      // 4a: return
      // 4b: astore 6
      // 4d: return
      // 4e: astore 6
      // 50: return
      // try (31 -> 38): 39 null
      // try (31 -> 38): 41 null
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (Throwable var3) {
         throw new Object(x1.getMessage());
      }
   }
}
