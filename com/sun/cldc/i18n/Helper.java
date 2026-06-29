package com.sun.cldc.i18n;

import com.sun.cldc.i18n.j2me.TextProcessingRegistry;
import com.sun.cldc.i18n.j2me.Universal_Reader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import net.rim.device.api.util.StringUtilities;
import net.rim.vm.WeakReference;

public final class Helper {
   private static String ISO8859_1 = "ISO-8859-1";
   private static String ASCII = "US-ASCII";
   private static String Universal = "Universal";
   private static String defaultEncoding = ISO8859_1;
   private static String defaultMEPath = "com.sun.cldc.i18n.j2me.";
   private static String _readerName;
   private static Class _readerClass;
   private static String _writerName;
   private static Class _writerClass;
   private static String _lastReaderEncoding;
   private static WeakReference _lastReaderWR = new WeakReference(null);
   private static Object _lastReaderLock = new Object();
   private static WeakReference _universalReaderWR = new WeakReference(null);
   private static Helper$LocalStrongReferences _strongReferences = new Helper$LocalStrongReferences();
   public static final long PERSISTENT_CONTENT_LISTENER_ID;
   private static String _lastWriterEncoding;
   private static WeakReference _lastWriterWR = new WeakReference(null);
   private static Object _lastWriterLock = new Object();
   private static String[] ALIAS_ASCII = new String[]{"ASCII", "US-ASCII"};
   private static String[] ALIAS_UTF8 = new String[]{"UTF8", "UTF-8"};
   private static String[] ALIAS_ISO8859_1 = new String[]{"ISO8859_1", "ISO-8859-1"};
   private static String[] ALIAS_UTF16BE = new String[]{"UnicodeBigUnmarked", "UTF-16BE"};
   private static String[] ALIAS_UTF16LE = new String[]{"UnicodeLittleUnmarked", "UTF-16LE"};
   private static String[] ALIAS_UTF7 = new String[]{"UTF-7", "UTF7"};

   public static final int getSuggestedLocale(String enc) {
      return TextProcessingRegistry.getInstance().getSuggestedLocale(enc);
   }

   public static final String getSuggestedTypeface(String enc) {
      return TextProcessingRegistry.getInstance().getSuggestedTypeface(enc);
   }

   public static final String getSuggestedTypeface(int localeCode) {
      return TextProcessingRegistry.getInstance().getSuggestedTypeface(localeCode);
   }

   public static final String getSuggestedEncoding(int localeCode) {
      return TextProcessingRegistry.getInstance().getSuggestedEncoding(localeCode);
   }

   public static final String getDefaultEncoding() {
      return defaultEncoding;
   }

   public static final boolean isSupportedEncoding(String enc) {
      String alias = resolveAlias(enc);
      return alias != null ? TextProcessingRegistry.getInstance().isSupported(alias, 0) : false;
   }

   public static final String[] getSupportedEncodings() {
      return TextProcessingRegistry.getInstance().getSupported(0);
   }

   public static final Reader getStreamReader(InputStream is) {
      try {
         return getStreamReader(is, defaultEncoding);
      } catch (UnsupportedEncodingException x) {
         throw new RuntimeException("Missing default encoding " + defaultEncoding);
      }
   }

   public static final Reader getStreamReader(InputStream is, String name) {
      if (is != null && name != null) {
         name = resolveAlias(name);
         StreamReader fr = getStreamReaderPrim(mapEncoding(name));
         return fr.open(is, name);
      } else {
         throw new NullPointerException();
      }
   }

   private static final StreamReader getStreamReaderPrim(String name) {
      if (name == null) {
         throw new NullPointerException();
      }

      try {
         if (!name.equals(_readerName)) {
            String className = defaultMEPath + name + "_Reader";
            _readerClass = Class.forName(className);
            _readerName = name;
         }

         return (StreamReader)_readerClass.newInstance();
      } catch (ClassNotFoundException x) {
         throw new UnsupportedEncodingException("Encoding " + name + " not found");
      } catch (InstantiationException x) {
         throw new RuntimeException("InstantiationException " + x.getMessage());
      } catch (IllegalAccessException x) {
         throw new RuntimeException("IllegalAccessException " + x.getMessage());
      } catch (ClassCastException x) {
         throw new RuntimeException("ClassCastException " + x.getMessage());
      }
   }

   public static final Writer getStreamWriter(OutputStream os) {
      try {
         return getStreamWriter(os, defaultEncoding);
      } catch (UnsupportedEncodingException x) {
         throw new RuntimeException("Missing default encoding " + defaultEncoding);
      }
   }

   public static final Writer getStreamWriter(OutputStream os, String name) {
      if (os != null && name != null) {
         name = resolveAlias(name);
         StreamWriter sw = getStreamWriterPrim(mapEncoding(name));
         return sw.open(os, name);
      } else {
         throw new NullPointerException();
      }
   }

   private static final StreamWriter getStreamWriterPrim(String name) {
      if (name == null) {
         throw new NullPointerException();
      }

      try {
         if (!name.equals(_writerName)) {
            String className = defaultMEPath + name + "_Writer";
            _writerClass = Class.forName(className);
            _writerName = name;
         }

         return (StreamWriter)_writerClass.newInstance();
      } catch (ClassNotFoundException x) {
         throw new UnsupportedEncodingException("Encoding " + name + " not found");
      } catch (InstantiationException x) {
         throw new RuntimeException("InstantiationException " + x.getMessage());
      } catch (IllegalAccessException x) {
         throw new RuntimeException("IllegalAccessException " + x.getMessage());
      } catch (ClassCastException x) {
         throw new RuntimeException("ClassCastException " + x.getMessage());
      }
   }

   public static final byte[] charToByteArray(char[] buffer, int offset, int length) {
      try {
         return charToByteArray(buffer, offset, length, defaultEncoding);
      } catch (UnsupportedEncodingException x) {
         throw new RuntimeException("Missing default encoding " + defaultEncoding);
      }
   }

   public static final Object byteToCharArray(byte[] buffer, int offset, int length, String enc) {
      if (enc == null) {
         throw new NullPointerException();
      }

      if (offset < 0 || length < 0 || offset + length > buffer.length || offset + length < 0 || offset > buffer.length) {
         throw new IllegalArgumentException();
      }

      if (length == 0) {
         return new byte[0];
      }

      enc = resolveAlias(enc);
      if (!StringUtilities.strEqualIgnoreCase(ISO8859_1, enc, 1701707776) && !StringUtilities.strEqualIgnoreCase(ASCII, enc, 1701707776)) {
         synchronized (_lastReaderLock) {
            int encId = isUniversal(enc);
            if (encId != -1) {
               Universal_Reader universalReader = (Universal_Reader)_universalReaderWR.get();
               if (universalReader == null) {
                  universalReader = new Universal_Reader();
                  _universalReaderWR.set(universalReader);
                  _strongReferences.storeUniversalReaderStrongReference(universalReader);
               }

               return universalReader.byteToCharArray(encId, buffer, offset, length, enc);
            } else {
               StreamReader lastReader = (StreamReader)_lastReaderWR.get();
               if (lastReader == null || !StringUtilities.strEqualIgnoreCase(_lastReaderEncoding, enc, 1701707776)) {
                  lastReader = getStreamReaderPrim(enc);
                  _lastReaderWR.set(lastReader);
                  _lastReaderEncoding = enc;
                  _strongReferences.storeLastReaderStrongReference(lastReader);
               }

               lastReader.open(new ByteArrayInputStream(buffer, offset, length), _lastReaderEncoding);
               char[] outbuf = null;

               try {
                  int size = lastReader.sizeOf(buffer, offset, length);
                  if (size >= 0) {
                     outbuf = new char[size];
                     lastReader.read(outbuf, 0, size);
                  }

                  lastReader.close();
               } catch (IOException x) {
                  throw new UnsupportedEncodingException("IOException reading reader " + x.getMessage());
               }

               if (outbuf == null) {
                  throw new UnsupportedEncodingException("Data does not match the expected encoding");
               } else {
                  return outbuf;
               }
            }
         }
      } else {
         byte[] outbuf = new byte[length];
         System.arraycopy(buffer, offset, outbuf, 0, length);
         return outbuf;
      }
   }

   public static final byte[] charToByteArray(char[] buffer, int offset, int length, String enc) {
      if (enc == null) {
         throw new NullPointerException();
      }

      if (offset >= 0 && length >= 0 && offset + length <= buffer.length && offset + length >= 0 && offset <= buffer.length) {
         synchronized (_lastWriterLock) {
            enc = resolveAlias(enc);
            StreamWriter lastWriter = (StreamWriter)_lastWriterWR.get();
            if (lastWriter == null || !StringUtilities.startsWithIgnoreCase(enc, _lastWriterEncoding, 1701707776)) {
               lastWriter = getStreamWriterPrim(mapEncoding(enc));
               _lastWriterWR.set(lastWriter);
               _lastWriterEncoding = enc;
               _strongReferences.storeLastWriterStrongReference(lastWriter);
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
            lastWriter.open(os, _lastWriterEncoding);

            try {
               lastWriter.write(buffer, offset, length);
               lastWriter.close();
            } catch (IOException x) {
               throw new UnsupportedEncodingException("IOException writing writer " + x.getMessage());
            }

            if (os.size() < 0) {
               throw new UnsupportedEncodingException("Data does not match the expected encoding or stream is empty");
            } else {
               return os.toByteArray();
            }
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final String mapEncoding(String enc) {
      if (StringUtilities.strEqualIgnoreCase(defaultEncoding, enc, 1701707776)) {
         return Universal;
      } else {
         return TextProcessingRegistry.getInstance().isSupported(enc, 0) ? Universal : enc;
      }
   }

   private static final int isUniversal(String enc) {
      return TextProcessingRegistry.getInstance().getTextProcessingDataID(enc, 0);
   }

   private static final String resolveAlias(String enc) {
      String[] alias;
      switch (enc.length()) {
         case 4:
            alias = ALIAS_UTF8;
            break;
         case 5:
            alias = enc.charAt(4) != '7' ? ALIAS_ASCII : ALIAS_UTF7;
            break;
         case 9:
            alias = ALIAS_ISO8859_1;
            break;
         case 18:
            alias = ALIAS_UTF16BE;
            break;
         case 21:
            alias = ALIAS_UTF16LE;
            break;
         default:
            return enc;
      }

      return StringUtilities.strEqualIgnoreCase(enc, alias[0], 1701707776) ? alias[1] : enc;
   }
}
