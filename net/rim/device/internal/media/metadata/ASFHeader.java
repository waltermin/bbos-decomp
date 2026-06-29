package net.rim.device.internal.media.metadata;

import javax.microedition.media.control.MetaDataControl;
import net.rim.device.api.media.MetaDataObject;

public final class ASFHeader {
   private MetaDataControl _metaDataControl;
   private long _duration = -1;
   private static final int GUID_SIZE = 16;
   private static final int SIZE_SIZE = 8;
   public static final int BYTES_TO_DETERMINE_HEADER_SIZE = 24;
   private static final int MIN_OBJECT_SIZE = 24;
   private static final int MAX_IMAGE_SIZE = 512000;
   private static final int FILE_PROPERTIES_OBJECT = 1;
   private static final int CONTENT_DESCRIPTION_OBJECT = 2;
   private static final int EXTENDED_CONTENT_DESCRIPTION_OBJECT = 3;
   private static final int UNKNOWN_OBJECT = -1;
   private static final int TYPE_STRING = 0;
   private static final int TYPE_BYTE_ARRAY = 1;
   private static final int TYPE_BOOL = 2;
   private static final int TYPE_DWORD = 3;
   private static final int TYPE_QWORD = 4;
   private static final int TYPE_WORD = 5;

   public ASFHeader(byte[] asfHeader) {
      this.parseHeader(asfHeader);
   }

   public static final int getHeaderSize(byte[] first24bytes) {
      if (first24bytes != null && first24bytes.length >= 24 && startsWithHeaderObjectGUID(first24bytes, 0)) {
         return getObjectSize(first24bytes, 16);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public final long getDuration() {
      return this._duration;
   }

   public final MetaDataControl getMetaData() {
      return this._metaDataControl;
   }

   private static final int getObjectSize(byte[] data, int offset) {
      long size = readLittleEndianLong(data, offset);
      if (size >= 24 && size <= Integer.MAX_VALUE) {
         return (int)size;
      } else {
         throw new IllegalArgumentException();
      }
   }

   private static final int getObjectType(byte[] data, int offset) {
      if (startsWithGUID(data, offset, -1934893919, 43335, 4559, -8150388600796589211L)) {
         return 1;
      } else if (startsWithGUID(data, offset, 1974609459, 26254, 4559, -6424102663316386196L)) {
         return 2;
      } else {
         return startsWithGUID(data, offset, -758078400, 58119, 4562, -7498492688998684592L) ? 3 : -1;
      }
   }

   private final void parseHeader(byte[] param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: new net/rim/device/internal/media/metadata/MetaDataControlImpl
      // 03: dup
      // 04: invokespecial net/rim/device/internal/media/metadata/MetaDataControlImpl.<init> ()V
      // 07: astore 2
      // 08: aload 1
      // 09: invokestatic net/rim/device/internal/media/metadata/ASFHeader.getHeaderSize ([B)I
      // 0c: istore 3
      // 0d: bipush 30
      // 0f: istore 4
      // 11: iload 4
      // 13: iload 3
      // 14: if_icmpge 8a
      // 17: aload 1
      // 18: iload 4
      // 1a: invokestatic net/rim/device/internal/media/metadata/ASFHeader.getObjectType ([BI)I
      // 1d: istore 5
      // 1f: iinc 4 16
      // 22: aload 1
      // 23: iload 4
      // 25: invokestatic net/rim/device/internal/media/metadata/ASFHeader.getObjectSize ([BI)I
      // 28: bipush 16
      // 2a: isub
      // 2b: bipush 8
      // 2d: isub
      // 2e: istore 6
      // 30: iinc 4 8
      // 33: iload 4
      // 35: iload 3
      // 36: if_icmpge 7b
      // 39: iload 6
      // 3b: ifle 7b
      // 3e: iload 5
      // 40: tableswitch 32 0 3 59 32 42 52
      // 60: aload 0
      // 61: aload 1
      // 62: iload 4
      // 64: invokespecial net/rim/device/internal/media/metadata/ASFHeader.readFilePropertiesObject ([BI)V
      // 67: goto 7b
      // 6a: aload 1
      // 6b: iload 4
      // 6d: aload 2
      // 6e: invokestatic net/rim/device/internal/media/metadata/ASFHeader.readContentDescriptionObject ([BILnet/rim/device/internal/media/metadata/MetaDataControlImpl;)V
      // 71: goto 7b
      // 74: aload 1
      // 75: iload 4
      // 77: aload 2
      // 78: invokestatic net/rim/device/internal/media/metadata/ASFHeader.readExtendedContentDescriptionObject ([BILnet/rim/device/internal/media/metadata/MetaDataControlImpl;)V
      // 7b: iload 4
      // 7d: iload 6
      // 7f: iadd
      // 80: istore 4
      // 82: goto 11
      // 85: astore 3
      // 86: goto 8a
      // 89: astore 3
      // 8a: aload 2
      // 8b: invokevirtual net/rim/device/internal/media/metadata/MetaDataControlImpl.size ()I
      // 8e: ifle 96
      // 91: aload 0
      // 92: aload 2
      // 93: putfield net/rim/device/internal/media/metadata/ASFHeader._metaDataControl Ljavax/microedition/media/control/MetaDataControl;
      // 96: return
      // try (4 -> 52): 52 null
      // try (4 -> 52): 54 null
   }

   private final void readFilePropertiesObject(byte[] data, int offset) {
      if ((readLittleEndianInt(data, offset + 64) & 1) == 0) {
         long playDuration = readLittleEndianLong(data, offset + 40);
         if (playDuration >= 0) {
            playDuration /= 10;
            long preroll = readLittleEndianLong(data, offset + 56);
            if (preroll > 0) {
               playDuration -= preroll * 1000;
            }

            if (playDuration >= 0) {
               this._duration = playDuration;
            }
         }
      }
   }

   private static final void readExtendedContentDescriptionObject(byte[] data, int offset, MetaDataControlImpl metaData) {
      int contentDescriptorsCount = readLittleEndianUnsignedShort(data, offset);
      offset += 2;

      for (int i = 0; i < contentDescriptorsCount; i++) {
         int length = readLittleEndianUnsignedShort(data, offset);
         offset += 2;
         String descriptorName = readString(data, offset, length);
         String key = mapDescriptorNameToKey(descriptorName);
         offset += length;
         int dataType = readLittleEndianUnsignedShort(data, offset);
         offset += 2;
         length = readLittleEndianUnsignedShort(data, offset);
         offset += 2;
         if (key != null) {
            if (key.length() == 0) {
               switch (dataType) {
                  case 1:
                     if (descriptorName.equals("WM/Picture")) {
                        readPicture(data, offset, length, metaData);
                     }
               }
            } else if (!metaData.containsKey(key)) {
               String value = null;
               switch (dataType) {
                  case 0:
                     value = readString(data, offset, length);
                     break;
                  case 3:
                     if (length == 4) {
                        value = String.valueOf(readLittleEndianInt(data, offset) & 4294967295L);
                     }
                     break;
                  case 5:
                     if (length == 2) {
                        value = String.valueOf(readLittleEndianUnsignedShort(data, offset));
                     }
               }

               if (value != null && value.length() > 0) {
                  metaData.put(key, value);
               }
            }
         }

         offset += length;
      }
   }

   private static final void readContentDescriptionObject(byte[] data, int offset, MetaDataControlImpl metaData) {
      int stringOffset = offset + 10;

      for (int field = 0; field < 5; field++) {
         int fieldLength = readLittleEndianUnsignedShort(data, offset + field * 2);
         if (fieldLength > 0) {
            String value = readString(data, stringOffset, fieldLength);
            if (value != null && value.length() > 0) {
               String key = null;
               switch (field) {
                  case -1:
                     break;
                  case 0:
                  default:
                     key = "title";
                     break;
                  case 1:
                     key = "author";
                     break;
                  case 2:
                     key = "copyright";
                     break;
                  case 3:
                     key = "description";
                     break;
                  case 4:
                     key = "rating";
               }

               metaData.put(key, value);
            }

            stringOffset += fieldLength;
         }
      }
   }

   private static final int getStringLength(byte[] data, int offset, int maxLength) {
      int length;
      for (length = 0; maxLength-- > 0; length++) {
         if (data[offset++] == 0 && (length & 1) == 0 && maxLength > 0 && data[offset] == 0) {
            return length;
         }
      }

      return length;
   }

   private static final String readString(byte[] data, int offset, int length) {
      length = getStringLength(data, offset, length);
      if (length > 0) {
         try {
            return new String(data, offset, length, "UTF-16LE");
         } finally {
            ;
         }
      } else {
         return "";
      }
   }

   private static final void readPicture(byte[] data, int offset, int length, MetaDataControlImpl metaData) {
      if (length > 0) {
         MetaDataObject image = new MetaDataObject();
         image.setPictureType(data[offset++]);
         if (--length > 4) {
            int imageSize = readLittleEndianInt(data, offset);
            offset += 4;
            length -= 4;
            if (imageSize > 0 && length > 0 && imageSize < length && imageSize <= 512000) {
               int stringLength = getStringLength(data, offset, length) + 2;
               if (stringLength < length) {
                  if (stringLength > 2) {
                     image.setMIMEType(readString(data, offset, stringLength));
                  }

                  offset += stringLength;
                  length -= stringLength;
                  if (length > 0) {
                     stringLength = getStringLength(data, offset, length) + 2;
                     if (stringLength < length) {
                        if (stringLength > 2) {
                           image.setDescription(readString(data, offset, stringLength));
                        }

                        offset += stringLength;
                        length -= stringLength;
                        byte[] imageBytes = new byte[imageSize];
                        System.arraycopy(data, offset, imageBytes, 0, imageSize);
                        image.setData(imageBytes);
                        if (image.getPictureType() == -1) {
                           image.setPictureType(0);
                        }

                        metaData.addObject(image);
                     }
                  }
               }
            }
         }
      }
   }

   private static final int readLittleEndianUnsignedShort(byte[] data, int offset) {
      return (data[offset + 1] & 0xFF) << 8 | data[offset] & 0xFF;
   }

   private static final int readLittleEndianInt(byte[] data, int offset) {
      return (data[offset + 3] & 0xFF) << 24 | (data[offset + 2] & 0xFF) << 16 | (data[offset + 1] & 0xFF) << 8 | data[offset] & 0xFF;
   }

   private static final long readLittleEndianLong(byte[] data, int offset) {
      return (data[offset + 7] & 255) << 56
         | (data[offset + 6] & 255) << 48
         | (data[offset + 5] & 255) << 40
         | (data[offset + 4] & 255) << 32
         | (data[offset + 3] & 255) << 24
         | (data[offset + 2] & 255) << 16
         | (data[offset + 1] & 255) << 8
         | data[offset] & 255;
   }

   private static final boolean startsWithHeaderObjectGUID(byte[] data, int offset) {
      return startsWithGUID(data, offset, 1974609456, 26254, 4559, -6424102663316386196L);
   }

   private static final boolean startsWithGUID(byte[] data, int offset, int guid1, int guid2, int guid3, long guid4) {
      return data != null
         && data.length - offset >= 16
         && readLittleEndianInt(data, offset) == guid1
         && readLittleEndianUnsignedShort(data, offset + 4) == guid2
         && readLittleEndianUnsignedShort(data, offset + 6) == guid3
         && (
               (data[offset + 8] & 255) << 56
                  | (data[offset + 9] & 255) << 48
                  | (data[offset + 10] & 255) << 40
                  | (data[offset + 11] & 255) << 32
                  | (data[offset + 12] & 255) << 24
                  | (data[offset + 13] & 255) << 16
                  | (data[offset + 14] & 255) << 8
                  | data[offset + 15] & 255
            )
            == guid4;
   }

   private static final String mapDescriptorNameToKey(String name) {
      if (name != null && name.length() >= 4) {
         switch (name.charAt(3)) {
            case '@':
            case 'D':
            case 'F':
            case 'H':
            case 'J':
            case 'K':
            case 'N':
            case 'Q':
            case 'U':
            case 'V':
            case 'X':
               break;
            case 'A':
            default:
               if (name.equals("WM/AlbumTitle")) {
                  return "album";
               }

               if (name.equals("WM/AlbumArtist")) {
                  return "orchestra";
               }

               if (name.equals("WM/AlbumSortOrder")) {
                  return "album sort order";
               }

               if (name.equals("WM/ArtistSortOrder")) {
                  return "performer sort order key";
               }

               if (name.equals("WM/AudioFileURL")) {
                  return "official audio file URL";
               }

               if (name.equals("WM/AudioSourceURL")) {
                  return "official audio source URL";
               }

               if (name.equals("WM/AuthorURL")) {
                  return "official artist URL";
               }
               break;
            case 'B':
               if (name.equals("WM/BeatsPerMinute")) {
                  return "BPM";
               }
               break;
            case 'C':
               if (name.equals("WM/Comments")) {
                  return "comment";
               }

               if (name.equals("WM/Composer")) {
                  return "composer";
               }

               if (name.equals("WM/Conductor")) {
                  return "conductor";
               }

               if (name.equals("WM/ContentGroupDescription")) {
                  return "content group description";
               }

               if (name.equals("WM/ContentDistributor")) {
                  return "content distributor";
               }
               break;
            case 'E':
               if (name.equals("WM/EncodedBy")) {
                  return "encoder";
               }

               if (name.equals("WM/EncodingSettings")) {
                  return "encoder settings";
               }
               break;
            case 'G':
               if (name.equals("WM/Genre")) {
                  return "genre";
               }
               break;
            case 'I':
               if (name.equals("WM/InitialKey")) {
                  return "initial key";
               }

               if (name.equals("WM/ISRC")) {
                  return "ISRC";
               }
               break;
            case 'L':
               if (name.equals("WM/Lyrics")) {
                  return "text transcription";
               }

               if (name.equals("WM/Language")) {
                  return "language";
               }
               break;
            case 'M':
               if (name.equals("WM/ModifiedBy")) {
                  return "remixed by";
               }

               if (name.equals("WM/Mood")) {
                  return "mood";
               }
               break;
            case 'O':
               if (name.equals("WM/OriginalAlbumTitle")) {
                  return "original album";
               }

               if (name.equals("WM/OriginalArtist")) {
                  return "original artist";
               }

               if (name.equals("WM/OriginalFilename")) {
                  return "original filename";
               }

               if (name.equals("WM/OriginalLyricist")) {
                  return "original text writer";
               }

               if (name.equals("WM/OriginalReleaseYear")) {
                  return "original release year";
               }
               break;
            case 'P':
               if (name.equals("WM/Picture")) {
                  return "";
               }

               if (name.equals("WM/PartOfSet")) {
                  return "part of a set";
               }

               if (name.equals("WM/PlaylistDelay")) {
                  return "playlist delay";
               }

               if (name.equals("WM/Publisher")) {
                  return "publisher";
               }

               if (name.equals("WM/Provider")) {
                  return "metadata provider";
               }

               if (name.equals("WM/ParentalRating")) {
                  return "parental rating";
               }

               if (name.equals("WM/Producer")) {
                  return "producer";
               }

               if (name.equals("WM/Period")) {
                  return "period";
               }

               if (name.equals("WM/PromotionURL")) {
                  return "promotion URL";
               }
               break;
            case 'R':
               if (name.equals("WM/RadioStationName")) {
                  return "Internet radio station name";
               }

               if (name.equals("WM/RadioStationOwner")) {
                  return "Internet radio station owner";
               }
               break;
            case 'S':
               if (name.equals("WM/SetSubTitle")) {
                  return "set subtitle";
               }

               if (name.equals("WM/SubTitle")) {
                  return "subtitle";
               }
               break;
            case 'T':
               if (name.equals("WM/TrackNumber")) {
                  return "track number";
               }

               if (name.equals("WM/TitleSortOrder")) {
                  return "title sort order";
               }

               if (name.equals("WM/ToolName")) {
                  return "tool name";
               }

               if (name.equals("WM/ToolVersion")) {
                  return "tool version";
               }
               break;
            case 'W':
               if (name.equals("WM/Writer")) {
                  return "text writer";
               }
               break;
            case 'Y':
               if (name.equals("WM/Year")) {
                  return "date";
               }
         }

         return null;
      } else {
         return null;
      }
   }
}
