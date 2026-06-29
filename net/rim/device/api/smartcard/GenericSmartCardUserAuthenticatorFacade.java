package net.rim.device.api.smartcard;

import net.rim.device.api.system.UserAuthenticator;

public class GenericSmartCardUserAuthenticatorFacade {
   private static GenericSmartCardUserAuthenticatorFacade getFacade() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 0
      // 02: ldc_w "net.rim.device.api.smartcard.GenericSmartCardUserAuthenticatorFacadeImpl"
      // 05: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 08: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0b: checkcast net/rim/device/api/smartcard/GenericSmartCardUserAuthenticatorFacade
      // 0e: astore 0
      // 0f: aload 0
      // 10: areturn
      // 11: astore 1
      // 12: aload 0
      // 13: areturn
      // 14: astore 1
      // 15: aload 0
      // 16: areturn
      // 17: astore 1
      // 18: aload 0
      // 19: areturn
      // try (2 -> 7): 9 null
      // try (2 -> 7): 12 null
      // try (2 -> 7): 15 null
   }

   public static UserAuthenticator getSmartCardAuthenticator(SmartCard smartCard) {
      GenericSmartCardUserAuthenticatorFacade facade = getFacade();
      return facade == null ? null : facade.getSmartCardUserAuthenticatorImpl(smartCard);
   }

   public static boolean isSmartCardAuthenticatorInstalled() {
      GenericSmartCardUserAuthenticatorFacade facade = getFacade();
      return facade == null ? false : facade.isSmartCardAuthenticatorInstalledImpl();
   }

   protected UserAuthenticator getSmartCardUserAuthenticatorImpl(SmartCard _1) {
      throw null;
   }

   protected boolean isSmartCardAuthenticatorInstalledImpl() {
      throw null;
   }
}
