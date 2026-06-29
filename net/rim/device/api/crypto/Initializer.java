package net.rim.device.api.crypto;

import net.rim.device.api.crypto.certificate.wtls.WTLSCertificateInitialization;
import net.rim.device.api.crypto.encoder.Crypto1EncoderInitialization;
import net.rim.device.api.crypto.keystore.KeyStoreInitialization1;
import net.rim.device.api.crypto.tls.wtls20.WTLSOptionStore;
import net.rim.device.apps.api.options.OptionsProviderRegistration;

final class Initializer {
   public static final void libMain(String[] args) {
      initialize(new Crypto1AlgorithmInitialization());
      initialize("net.rim.device.api.crypto.Crypto2AlgorithmInitialization");
      initialize("net.rim.device.api.crypto.Crypto3AlgorithmInitialization");
      initialize(new Crypto1EncoderInitialization());
      initialize("net.rim.device.api.crypto.encoder.Crypto2EncoderInitialization");
      initialize("net.rim.device.api.crypto.encoder.Crypto3EncoderInitialization");
      initialize("net.rim.device.api.crypto.encoder.CMSEncoderInitialization");
      initialize("net.rim.device.api.crypto.encoder.PGPEncoderInitialization");
      initialize(new WTLSCertificateInitialization());
      initialize("net.rim.device.api.crypto.certificate.x509.X509CertificateInitialization");
      initialize("net.rim.device.api.crypto.certificate.pgp.PGPCertificateInitialization");
      initialize(new KeyStoreInitialization1());
      initialize("net.rim.device.api.crypto.keystore.KeyStoreInitialization2");
      initialize("net.rim.device.api.crypto.tls.TLSInitialization");
      initialize("net.rim.device.api.crypto.certificate.status.StatusProviderInitialization");
      initialize("net.rim.device.api.crypto.certificate.CertificateOptionsInitialization");
      initialize("net.rim.device.api.crypto.pgp.PGPInitialization");
      initialize("net.rim.device.api.smartcard.SmartCardInitialization");
      new RandomDatabase();
      Initializer$WTLSOptionsInitializer optionsInitializer = new Initializer$WTLSOptionsInitializer(null);
      OptionsProviderRegistration.registerOptionsProvider(optionsInitializer);
      WTLSOptionStore.register();
   }

   private static final void initialize(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 04: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 07: checkcast net/rim/device/api/crypto/Initialization
      // 0a: invokestatic net/rim/device/api/crypto/Initializer.initialize (Lnet/rim/device/api/crypto/Initialization;)V
      // 0d: return
      // 0e: astore 1
      // 0f: return
      // 10: astore 1
      // 11: return
      // 12: astore 1
      // 13: new java/lang/RuntimeException
      // 16: dup
      // 17: aload 1
      // 18: invokevirtual java/lang/InstantiationException.toString ()Ljava/lang/String;
      // 1b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: astore 1
      // 20: new java/lang/RuntimeException
      // 23: dup
      // 24: aload 1
      // 25: invokevirtual java/lang/IllegalAccessException.toString ()Ljava/lang/String;
      // 28: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // try (0 -> 5): 6 null
      // try (0 -> 5): 8 null
      // try (0 -> 5): 10 null
      // try (0 -> 5): 17 null
   }

   private static final void initialize(Initialization initalizer) {
      try {
         initalizer.initialize();
      } finally {
         return;
      }
   }
}
