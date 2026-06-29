package net.rim.device.apps.api.transmission.rim;

import java.util.Enumeration;
import java.util.Vector;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.IntVector;
import net.rim.device.apps.api.transmission.Parameters;
import net.rim.device.apps.api.utility.serialization.Converter;

public final class CMIMEConverterRegistry implements CMIMEConstants {
   private static final long CONVERTERS = -8340287525872651265L;
   private static final long CONVERTER_PRIORITIES = -1857441766889925113L;
   private static final long CMIME_TEXT_PLAIN_CONVERTER_SINGLETON = -963689262970357577L;
   private static final long CMIME_UNKNOWN_CONVERTER_SINGLETON = 5760752211972640782L;
   private static Vector _converters;
   private static IntVector _converterRanks;
   public static final int MIN_RANK = 1;
   public static final int MAX_RANK = 10;

   public static final synchronized void addConverter(Converter converter, int rank) {
      if (rank >= 1 && rank <= 10 && converter != null) {
         int size = _converterRanks.size();
         if (!_converters.contains(converter)) {
            boolean done = false;

            for (int i = 0; i < size && !done; i++) {
               int currentRank = _converterRanks.elementAt(i);
               if (rank < currentRank) {
                  _converterRanks.insertElementAt(rank, i);
                  _converters.insertElementAt(converter, i);
                  done = true;
               }
            }

            if (!done) {
               _converterRanks.addElement(rank);
               _converters.addElement(converter);
            }
         }
      } else {
         throw new Object("rank must be between MIN_RANK and MAX_RANK");
      }
   }

   public static final Converter getDefaultConverter() {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      Converter c = (Converter)ar.getOrWaitFor(5760752211972640782L);
      if (c == null) {
         c = new CMIMEUnknownConverter();
         ar.put(5760752211972640782L, c);
      }

      return c;
   }

   public static final synchronized Converter getDefaultConverter(Parameters parameters) {
      int size = _converters.size();

      for (int i = size - 1; i >= 0; i--) {
         Converter converter = (Converter)_converters.elementAt(i);
         if (converter.canConvert(parameters)) {
            return converter;
         }
      }

      return getDefaultConverter();
   }

   public static final synchronized Converter getDefaultConverter(String contentType) {
      int size = _converters.size();

      for (int i = size - 1; i >= 0; i--) {
         Converter converter = (Converter)_converters.elementAt(i);
         if (converter.canConvert(contentType)) {
            return converter;
         }
      }

      return getDefaultConverter();
   }

   public static final synchronized int getPreferredConversion(String[] contentTypes) {
      int maxRankSoFar = 0;
      int maxRankConversion = -1;

      for (int i = 0; i < contentTypes.length; i++) {
         Converter converter = null;
         String contentType = contentTypes[i];
         int size = _converters.size();

         for (int j = size - 1; j >= 0; j--) {
            Converter var9 = _converters.elementAt(j);
            if (((Converter)var9).canConvert(contentType)) {
               int rank = _converterRanks.elementAt(j);
               if (rank > maxRankSoFar) {
                  maxRankSoFar = rank;
                  maxRankConversion = i;
                  break;
               }
            }
         }
      }

      return maxRankConversion;
   }

   public static final synchronized Enumeration enumerateConverters(String contentType) {
      Vector results = (Vector)(new Object());
      int size = _converters.size();

      for (int i = size - 1; i >= 0; i--) {
         Converter converter = (Converter)_converters.elementAt(i);
         if (converter.canConvert(contentType)) {
            results.addElement(converter);
         }
      }

      return results.elements();
   }

   static {
      ApplicationRegistry ar = ApplicationRegistry.getApplicationRegistry();
      _converters = ar.getVector(-8340287525872651265L);
      _converterRanks = ar.getIntVector(-1857441766889925113L);
      Converter c = (Converter)ar.getOrWaitFor(-963689262970357577L);
      if (c == null) {
         c = new CMIMETextPlainConverter();
         ar.put(-963689262970357577L, c);
      }

      addConverter(c, 3);
   }
}
