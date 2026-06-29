package net.rim.tools.compiler.types;

import net.rim.tools.compiler.vm.Constants;

public final class Modifier implements Constants {
   public static final int NONE = 0;
   public static final int NATIVE = 1;
   public static final int STATIC = 2;
   public static final int FIELD = 4;
   public static final int METHOD = 8;
   public static final int CONSTRUCTOR = 16;
   public static final int ABSTRACT = 32;
   public static final int FINAL = 64;
   public static final int PUBLIC = 128;
   public static final int PROTECTED = 256;
   public static final int PRIVATE = 512;
   public static final int LOCAL = 1024;
   public static final int INTERFACE = 2048;
   public static final int TRANSIENT = 4096;
   public static final int VOLATILE = 8192;
   public static final int STRICT = 16384;
   public static final int SYNCHRONIZED = 32768;
   public static final int LOCAL_CODE = 65536;
   public static final int FROM_LIBRARY = 131072;
   public static final int BRITTLE = 262144;
   public static final int INCLUSIVE = 524288;
   public static final int CLASSINIT = 1048576;
   public static final int EXPORTED = 2097152;
   public static final int LOCAL_DATA = 8388608;
   public static final int INITIALIZED = 16777216;
   public static final int SYNTHETIC = 33554432;
   public static final int UNGROUPABLE = 67108864;
   public static final int VERIFY_ERROR = 134217728;
   public static final int REACHABLE = 268435456;
   public static final int RESOLVED = 536870912;
   public static final int OPTIMIZED = 1073741824;
   public static final int POPULATED = Integer.MIN_VALUE;
   public static final int VISIBILITY = 896;
   public static final int INTERNAL_STATE = -251658240;
   public static final int REQUIRED = 285212672;
   public static final int ACC_PUBLIC = 1;
   public static final int ACC_PRIVATE = 2;
   public static final int ACC_PROTECTED = 4;
   public static final int ACC_STATIC = 8;
   public static final int ACC_FINAL = 16;
   public static final int ACC_SYNCHRONIZED = 32;
   public static final int ACC_VOLATILE = 64;
   public static final int ACC_TRANSIENT = 128;
   public static final int ACC_NATIVE = 256;
   public static final int ACC_INTERFACE = 512;
   public static final int ACC_ABSTRACT = 1024;
   public static final int ACC_STRICT = 2048;

   public static final int translateClassfileAccessFlags(int accessFlags) {
      int modifiers = 0;
      if ((accessFlags & 1) != 0) {
         modifiers |= 128;
      }

      if ((accessFlags & 2) != 0) {
         modifiers |= 512;
      }

      if ((accessFlags & 4) != 0) {
         modifiers |= 256;
      }

      if ((accessFlags & 8) != 0) {
         modifiers |= 2;
      }

      if ((accessFlags & 16) != 0) {
         modifiers |= 64;
      }

      if ((accessFlags & 32) != 0) {
         modifiers |= 32768;
      }

      if ((accessFlags & 64) != 0) {
         modifiers |= 8192;
      }

      if ((accessFlags & 128) != 0) {
         modifiers |= 4096;
      }

      if ((accessFlags & 256) != 0) {
         modifiers |= 1;
      }

      if ((accessFlags & 512) != 0) {
         modifiers |= 2048;
      }

      if ((accessFlags & 1024) != 0) {
         modifiers |= 32;
      }

      if ((accessFlags & 2048) != 0) {
         modifiers |= 16384;
      }

      return modifiers;
   }

   public static final int translateCodfileAttributes(int attributes) {
      int modifiers = 0;
      if ((attributes & 1) != 0) {
         modifiers |= 128;
      }

      if ((attributes & 2) != 0) {
         modifiers |= 512;
      }

      if ((attributes & 4) != 0) {
         modifiers |= 256;
      }

      if ((attributes & 8) != 0) {
         modifiers |= 64;
      }

      return modifiers;
   }

   public static final int translateCodfileClassAttributes(int attributes) {
      int modifiers = translateCodfileAttributes(attributes);
      if ((attributes & 32) != 0) {
         modifiers |= 2048;
      }

      if ((attributes & 16) != 0) {
         modifiers |= 32;
      }

      if ((attributes & 64) != 0) {
         modifiers |= 134217728;
      }

      return modifiers;
   }

   public static final int toCodfileProtectionAttribute(int modifiers) {
      int result = 0;
      if ((modifiers & 64) != 0) {
         result |= 8;
      }

      if ((modifiers & 128) != 0) {
         result |= 1;
      }

      if ((modifiers & 256) != 0) {
         result |= 4;
      }

      if ((modifiers & 512) != 0) {
         result |= 2;
      }

      if ((modifiers & 2097152) != 0) {
         result |= 65536;
      }

      return result;
   }

   public static final int toCodfileClassAttribute(int modifiers) {
      int result = toCodfileProtectionAttribute(modifiers);
      if ((modifiers & 2048) != 0) {
         result |= 32;
      }

      if ((modifiers & 32) != 0) {
         result |= 16;
      }

      if ((modifiers & 134217728) != 0) {
         result |= 64;
      }

      return result;
   }

   public static final int toCodfileRoutineAttribute(int modifiers) {
      int result = toCodfileProtectionAttribute(modifiers);
      if ((modifiers & 2) != 0) {
         result |= 16;
      }

      if ((modifiers & 1048576) != 0) {
         result |= 256;
      }

      if ((modifiers & 16) != 0) {
         result |= 128;
      }

      if ((modifiers & 32) != 0) {
         result |= 32;
      }

      return result;
   }
}
