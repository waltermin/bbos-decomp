package net.rim.device.api.crypto.certificate;

import net.rim.device.api.crypto.certificate.status.CertificateStatusListener;
import net.rim.device.api.crypto.certificate.status.CertificateStatusRequest;

public class CertificateStatusProviderFacade {
   private static CertificateStatusProviderFacade _certificateStatusProvider;
   static final int REQUEST_START;
   static final int REQUEST_COMPLETE;
   static final int REQUEST_DISMISS;
   static final int REQUEST_CANCEL;
   static final int REQUEST_ERROR;

   public static boolean available() {
      return _certificateStatusProvider != null;
   }

   public static boolean queryStatusAvailability() {
      return _certificateStatusProvider != null && _certificateStatusProvider.queryStatusAvailability0();
   }

   public static boolean queryStatusAvailability(Certificate[] certChain, boolean checkEntireChain) {
      return _certificateStatusProvider != null && _certificateStatusProvider.queryStatusAvailability0(certChain, checkEntireChain);
   }

   public static int requestCertificateStatus(CertificateStatusRequest request, CertificateStatusListener listener, boolean allowDismiss, boolean allowDetails) {
      return _certificateStatusProvider.requestCertificateStatus0(request, listener, allowDismiss, allowDetails);
   }

   public static int fetchCertificateStatus(CertificateStatusRequest request, CertificateStatusListener listener) {
      return _certificateStatusProvider.fetchCertificateStatus0(request, listener);
   }

   boolean queryStatusAvailability0() {
      throw null;
   }

   boolean queryStatusAvailability0(Certificate[] _1, boolean _2) {
      throw null;
   }

   int requestCertificateStatus0(CertificateStatusRequest _1, CertificateStatusListener _2, boolean _3, boolean _4) {
      throw null;
   }

   int fetchCertificateStatus0(CertificateStatusRequest _1, CertificateStatusListener _2) {
      throw null;
   }

   static {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: ldc_w "net.rim.device.api.crypto.certificate.CertificateStatusProviderFacadeImpl"
      // 03: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 06: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 09: checkcast net/rim/device/api/crypto/certificate/CertificateStatusProviderFacade
      // 0c: putstatic net/rim/device/api/crypto/certificate/CertificateStatusProviderFacade._certificateStatusProvider Lnet/rim/device/api/crypto/certificate/CertificateStatusProviderFacade;
      // 0f: return
      // 10: astore 0
      // 11: return
      // 12: astore 0
      // 13: new java/lang/Object
      // 16: dup
      // 17: aload 0
      // 18: invokevirtual java/lang/InstantiationException.toString ()Ljava/lang/String;
      // 1b: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1e: athrow
      // 1f: astore 0
      // 20: new java/lang/Object
      // 23: dup
      // 24: aload 0
      // 25: invokevirtual java/lang/IllegalAccessException.toString ()Ljava/lang/String;
      // 28: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 2b: athrow
      // try (0 -> 5): 6 null
      // try (0 -> 5): 8 null
      // try (0 -> 5): 15 null
   }
}
