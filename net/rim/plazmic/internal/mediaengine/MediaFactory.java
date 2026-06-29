package net.rim.plazmic.internal.mediaengine;

import net.rim.plazmic.internal.mediaengine.io.FormatVersionReader;
import net.rim.plazmic.internal.mediaengine.registry.Registry;
import net.rim.plazmic.internal.mediaengine.registry.RegistryImpl;
import net.rim.plazmic.internal.mediaengine.registry.RegistryProvider;
import net.rim.plazmic.internal.mediaengine.service.BasicService;
import net.rim.plazmic.internal.mediaengine.util.Platform;
import net.rim.plazmic.mediaengine.MediaException;
import net.rim.plazmic.mediaengine.io.Connector;

public final class MediaFactory {
   private static Registry _registry;
   private static RegistryProvider _registryProvider = null;
   private static Platform _platform = null;

   private MediaFactory() {
   }

   public static final MediaServices createMediaServices(Object media) throws MediaException {
      String mediaType = !(media instanceof MediaModel) ? media.getClass().getName() : ((MediaModel)media).getMediaClass().getName();

      try {
         return (MediaServices)createService(mediaType, "MEDIA_SERVICES");
      } finally {
         String[] keys = new Object[]{"MEDIA", mediaType};
         if (null != getRegistry().getValue(keys)) {
            MediaServices services = new MediaServices(mediaType);
            services.getService("MAIN_SERVICE");
            return services;
         } else {
            throw new MediaException(8, RegistryImpl.getKey(keys));
         }
      }
   }

   public static final boolean isSupported(String contentType) {
      return null != getRegistry().getValue(new Object[]{"CONTENT", contentType});
   }

   public static final void verifyContentType(String contentType) throws MediaException {
      String[] keys = new Object[]{"CONTENT", contentType};
      if (null == getRegistry().getValue(keys)) {
         throw new MediaException(8, RegistryImpl.getKey(keys));
      }
   }

   public static final Object createService(String mediaType, String serviceId) {
      return createObjectFromRegistry(new Object[]{"MEDIA", mediaType, serviceId});
   }

   public static final ResourceProvider createResourceProvider(String contentType, String version) throws MediaException {
      try {
         return (ResourceProvider)createObjectFromRegistry(new Object[]{"CONTENT", contentType, version});
      } catch (MediaException ex) {
         if (version.compareTo(((RegistryImpl)getRegistry()).getMinSupportedVersion()) < 0) {
            throw new MediaException(2, ex.getMessage(), version);
         } else if (version.compareTo(((RegistryImpl)getRegistry()).getMaxSupportedVersion()) > 0) {
            throw new MediaException(1, ex.getMessage(), version);
         } else {
            throw new MediaException(8, ex.getMessage(), version);
         }
      }
   }

   public static final FormatVersionReader createVersionReader(String contentType) {
      return (FormatVersionReader)createObjectFromRegistry(new Object[]{"CONTENT", contentType, "VERSION_READER"});
   }

   public static final ResourceProvider createResourceProvider(String contentType, byte[] header) {
      try {
         return (ResourceProvider)createObjectFromRegistry(new Object[]{"CONTENT", contentType, "RESOURCE_PROVIDER"});
      } catch (MediaException e) {
         FormatVersionReader versionReader = createVersionReader(contentType);
         String version = versionReader.getVersion(header, 0);
         return createResourceProvider(contentType, version);
      }
   }

   public static final Object createObjectFromRegistry(String[] keys) throws MediaException {
      String className = getRegistry().getValue(keys);
      if (className != null) {
         try {
            return createObject(className);
         } finally {
            throw new MediaException(8, RegistryImpl.getKey(keys));
         }
      } else {
         throw new MediaException(8, RegistryImpl.getKey(keys));
      }
   }

   public static final BasicService createDefaultUI() {
      try {
         return (BasicService)createObjectFromRegistry(new String[]{"FRAMEWORK", "UI"});
      } catch (MediaException e) {
         return null;
      }
   }

   public static final Connector createDefaultConnector() {
      try {
         return (Connector)createObjectFromRegistry(new String[]{"FRAMEWORK", "CONNECTOR"});
      } catch (MediaException e) {
         return null;
      }
   }

   public static final Platform getPlatform() {
      if (_platform == null) {
         label24:
         try {
            _platform = (Platform)createObjectFromRegistry(new String[]{"FRAMEWORK", "PLATFORM"});
         } finally {
            break label24;
         }

         if (_platform == null) {
            throw new Object("PLATFORM must be non-null");
         }
      }

      return _platform;
   }

   public static final Registry getRegistry() {
      if (_registry == null) {
         RegistryProvider registryProvider = _registryProvider != null
            ? _registryProvider
            : (RegistryProvider)createObject("net.rim.plazmic.internal.mediaengine.util.RIMRegistryProvider");
         if (registryProvider == null) {
            throw new Object("Defines.REGISTRY_PROVIDER_CLASS is not found");
         }

         _registry = registryProvider.getRegistry();
      }

      return _registry;
   }

   public static final void setRegistryProvider(RegistryProvider rp) {
      _registryProvider = rp;
   }

   public static final Object createObject(String param0) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aconst_null
      // 01: astore 1
      // 02: aload 0
      // 03: ifnull 37
      // 06: aload 0
      // 07: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
      // 0a: invokevirtual java/lang/Class.newInstance ()Ljava/lang/Object;
      // 0d: astore 1
      // 0e: aload 1
      // 0f: areturn
      // 10: astore 2
      // 11: new java/lang/Object
      // 14: dup
      // 15: aload 2
      // 16: invokevirtual java/lang/InstantiationException.toString ()Ljava/lang/String;
      // 19: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 1c: athrow
      // 1d: astore 2
      // 1e: new java/lang/Object
      // 21: dup
      // 22: aload 2
      // 23: invokevirtual java/lang/IllegalAccessException.toString ()Ljava/lang/String;
      // 26: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 29: athrow
      // 2a: astore 2
      // 2b: new java/lang/Object
      // 2e: dup
      // 2f: aload 2
      // 30: invokevirtual java/lang/ClassNotFoundException.toString ()Ljava/lang/String;
      // 33: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;)V
      // 36: athrow
      // 37: aload 1
      // 38: areturn
      // try (4 -> 8): 10 null
      // try (4 -> 8): 17 null
      // try (4 -> 8): 24 null
   }
}
