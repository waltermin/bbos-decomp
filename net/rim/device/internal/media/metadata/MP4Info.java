package net.rim.device.internal.media.metadata;

import java.io.InputStream;
import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.media.MetaDataObject;

public final class MP4Info {
   private long _duration = -1;
   private MetaDataControlImpl _metaData;
   private static final int ATOM_MOOV;
   private static final int ATOM_UDTA;
   private static final int ATOM_META;
   private static final int ATOM_ILST;
   private static final int ATOM_DATA;
   private static final int ATOM_MVHD;
   private static final int DATA_TYPE_INTEGERS;
   private static final int DATA_TYPE_TEXT;
   private static final int DATA_TYPE_JPEG_IMAGE;
   private static final int DATA_TYPE_PNG_IMAGE;
   private static final int MAX_BYTES;

   public MP4Info(InputStream inputStream) {
      this.read(inputStream);
   }

   public final long getDuration() {
      return this._duration;
   }

   public final MetaDataControl getMetaData() {
      return this._metaData != null && this._metaData.size() > 0 ? this._metaData : null;
   }

   private final void read(InputStream param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 0: aload 0
      // 1: aload 1
      // 2: ldc_w 2147483647
      // 5: bipush 0
      // 6: bipush 0
      // 7: invokespecial net/rim/device/internal/media/metadata/MP4Info.readAtom (Ljava/io/InputStream;IIZ)Z
      // a: pop
      // b: return
      // c: astore 2
      // d: return
      // e: astore 2
      // f: return
      // try (0 -> 7): 8 null
      // try (0 -> 7): 10 null
   }

   private final boolean readAtom(InputStream is, int atomLength, int atom, boolean atomIsMetaDataKey) {
      int childAtomLength = 0;

      while (atomLength > 0) {
         childAtomLength = readInt(is);
         if (childAtomLength < 8) {
            return false;
         }

         childAtomLength -= 4;
         atomLength -= 4;
         int childAtom = readInt(is);
         childAtomLength -= 4;
         atomLength -= 4;
         if (childAtomLength > atomLength) {
            if (atomIsMetaDataKey && childAtom == 1684108385) {
               throw new Object();
            }

            childAtomLength = atomLength;
         }

         if (atomIsMetaDataKey && childAtom == 1684108385) {
            String key = getKey(atom);
            if (key != null && childAtomLength >= 8) {
               int flags = readInt(is);
               skipFully(is, 4);
               childAtomLength -= 8;
               atomLength -= 8;
               if (this._metaData == null) {
                  this._metaData = (MetaDataControlImpl)(new Object());
               }

               label319:
               switch (flags) {
                  case 0:
                     if (childAtomLength >= 10240) {
                        skipFully(is, childAtomLength);
                     } else {
                        int firstNumber = 0;
                        int secondNumber = 0;

                        while (childAtomLength > 0 && atomLength > 0) {
                           if (firstNumber == 0) {
                              firstNumber = is.read();
                              if (firstNumber == -1) {
                                 throw new Object();
                              }
                           } else {
                              if (secondNumber != 0) {
                                 skipFully(is, childAtomLength);
                                 break;
                              }

                              secondNumber = is.read();
                              if (secondNumber == -1) {
                                 throw new Object();
                              }
                           }

                           childAtomLength--;
                           atomLength--;
                        }

                        switch (atom) {
                           case -1452841618:
                           case 1735291493:
                              try {
                                 this._metaData.put(key, ID3v1Reader.genreCodeToString(firstNumber - 1));
                                 break label319;
                              } finally {
                                 break label319;
                              }
                           default:
                              this._metaData
                                 .put(
                                    key,
                                    secondNumber != 0
                                       ? ((StringBuffer)(new Object())).append(String.valueOf(firstNumber)).append('/').append(secondNumber).toString()
                                       : String.valueOf(firstNumber)
                                 );
                        }
                     }
                     break;
                  case 1:
                     if (childAtomLength < 131072) {
                        byte[] data = new byte[childAtomLength];
                        if (readArray(is, data, 0, data.length) != childAtomLength) {
                           throw new Object();
                        }

                        try {
                           this._metaData.put(key, (String)(new Object(data, "utf-8")));
                        } finally {
                           break;
                        }
                     } else {
                        skipFully(is, childAtomLength);
                     }
                     break;
                  case 13:
                  case 14:
                     if (childAtomLength < 524288) {
                        byte[] data = new byte[childAtomLength];
                        if (readArray(is, data, 0, data.length) != childAtomLength) {
                           throw new Object();
                        }

                        MetaDataObject coverArt = (MetaDataObject)(new Object());
                        coverArt.setData(data);
                        coverArt.setMIMEType(flags == 14 ? "image/png" : "image/jpeg");
                        coverArt.setPictureType(3);
                        this._metaData.addObject(coverArt);
                     } else {
                        skipFully(is, childAtomLength);
                     }
                     break;
                  default:
                     skipFully(is, childAtomLength);
               }
            } else {
               skipFully(is, childAtomLength);
            }
         } else if (childAtom == 1836476516 && atom == 1836019574) {
            this.readMVHD(is, childAtomLength);
         } else if ((atom != 0 || childAtom != 1836019574)
            && (atom != 1836019574 || childAtom != 1969517665)
            && (atom != 1969517665 || childAtom != 1835365473)
            && (atom != 1835365473 || childAtom != 1768715124)
            && atom != 1768715124) {
            skipFully(is, childAtomLength);
         } else {
            boolean stopParsingAfterThisAtom = false;
            switch (childAtom) {
               case 1835365473:
                  skipFully(is, 4);
                  childAtomLength -= 4;
                  atomLength -= 4;
               case 1768715124:
               case 1836019574:
               case 1969517665:
                  stopParsingAfterThisAtom = true;
               default:
                  if (!this.readAtom(is, childAtomLength, childAtom, atom == 1768715124) || stopParsingAfterThisAtom) {
                     return false;
                  }
            }
         }

         atomLength -= childAtomLength;
      }

      return true;
   }

   private final void readMVHD(InputStream is, int atomLength) {
      if (atomLength >= 20) {
         int version = is.read();
         if (version < 0) {
            throw new Object();
         }

         atomLength--;
         if (version == 1 && atomLength >= 31 || version == 0) {
            skipFully(is, 3 + (8 << version));
            atomLength -= 3 + (8 << version);
            long timeScale = readInt(is) & 4294967295L;
            atomLength -= 4;
            if (timeScale <= 0) {
               timeScale = 600;
            }

            long duration = version == 0 ? readInt(is) & 4294967295L : readLong(is);
            atomLength -= 4 << version;
            if (duration >= 0) {
               long microUnitDuration = duration * 1000000;
               if (microUnitDuration >= duration) {
                  this._duration = microUnitDuration / timeScale;
               } else {
                  long milliUnitDuration = duration * 1000;
                  this._duration = milliUnitDuration >= duration ? milliUnitDuration / timeScale * 1000 : duration / timeScale * 1000000;
               }
            }
         }
      }

      skipFully(is, atomLength);
   }

   private static final String getKey(int atom) {
      switch (atom) {
         case -1455336876:
            return "author";
         case -1453233054:
            return "album";
         case -1453101708:
            return "comment";
         case -1453039239:
            return "date";
         case -1452841618:
         case 1735291493:
            return "genre";
         case -1452838288:
            return "content group description";
         case -1452508814:
            return "text transcription";
         case -1452383891:
            return "title";
         case -1451987089:
            return "encoder";
         case -1451789708:
            return "composer";
         case 1668249202:
            return "";
         case 1668313716:
            return "copyright";
         case 1684370275:
            return "description";
         case 1684632427:
            return "part of a set";
         case 1953655662:
            return "track number";
         default:
            return null;
      }
   }

   private static final int readArray(InputStream is, byte[] b, int offset, int length) {
      int bytesRead = 0;
      int newBytesRead = 0;

      do {
         newBytesRead = is.read(b, offset + bytesRead, length - bytesRead);
         if (newBytesRead <= 0) {
            break;
         }

         bytesRead += newBytesRead;
      } while (bytesRead < length);

      return bytesRead;
   }

   private static final int readInt(InputStream is) {
      int b1 = is.read();
      int b2 = is.read();
      int b3 = is.read();
      int b4 = is.read();
      if ((b1 | b2 | b3 | b4) < 0) {
         throw new Object();
      } else {
         return b1 << 24 | b2 << 16 | b3 << 8 | b4;
      }
   }

   private static final long readLong(InputStream is) {
      return ((long)readInt(is) << 32) + (readInt(is) & 4294967295L);
   }

   private static final void skipFully(InputStream is, int numBytes) {
      while (numBytes > 0) {
         int skipped = (int)is.skip(numBytes);
         if (skipped <= 0) {
            throw new Object();
         }

         numBytes -= skipped;
      }
   }
}
