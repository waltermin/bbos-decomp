package net.rim.tools.compiler.types;

import net.rim.tools.compiler.vm.Constants;

public final class Modifier implements Constants {
   public static final int NONE;
   public static final int NATIVE;
   public static final int STATIC;
   public static final int FIELD;
   public static final int METHOD;
   public static final int CONSTRUCTOR;
   public static final int ABSTRACT;
   public static final int FINAL;
   public static final int PUBLIC;
   public static final int PROTECTED;
   public static final int PRIVATE;
   public static final int LOCAL;
   public static final int INTERFACE;
   public static final int TRANSIENT;
   public static final int VOLATILE;
   public static final int STRICT;
   public static final int SYNCHRONIZED;
   public static final int LOCAL_CODE;
   public static final int FROM_LIBRARY;
   public static final int BRITTLE;
   public static final int INCLUSIVE;
   public static final int CLASSINIT;
   public static final int EXPORTED;
   public static final int LOCAL_DATA;
   public static final int INITIALIZED;
   public static final int SYNTHETIC;
   public static final int UNGROUPABLE;
   public static final int VERIFY_ERROR;
   public static final int REACHABLE;
   public static final int RESOLVED;
   public static final int OPTIMIZED;
   public static final int POPULATED;
   public static final int VISIBILITY;
   public static final int INTERNAL_STATE;
   public static final int REQUIRED;
   public static final int ACC_PUBLIC;
   public static final int ACC_PRIVATE;
   public static final int ACC_PROTECTED;
   public static final int ACC_STATIC;
   public static final int ACC_FINAL;
   public static final int ACC_SYNCHRONIZED;
   public static final int ACC_VOLATILE;
   public static final int ACC_TRANSIENT;
   public static final int ACC_NATIVE;
   public static final int ACC_INTERFACE;
   public static final int ACC_ABSTRACT;
   public static final int ACC_STRICT;

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
