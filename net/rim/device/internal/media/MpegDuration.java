package net.rim.device.internal.media;

final class MpegDuration {
   private static final int[][] mp3srate = new int[][]{
      {11025, 12000, 8000, 0, -804651004, 22050, 24000, 16000, 0, -805044223, 35, -804651007, 51, -804651004, 44100, 48000},
      {0, 0, 0, 0, -804650999, 0, 128, 192, 224, 240, 248, 252, 254, 255, -804782028, 17170432},
      {22050, 24000, 16000, 0, -805044223, 35, -804651007, 51, -804651004, 44100, 48000, 32000, 0, -804650999, 95, 103},
      {44100, 48000, 32000, 0, -804650999, 95, 103, 118, 134, 148, 159, 204, 244, 39, -804782048, 140249440}
   };

   private MpegDuration() {
   }

   public static final long getDuration(byte[] buf, long fileLength) {
      int start = 0;
      int[][] mp3SampleCount = (int[][])null;
      int[] aacSampleCount = null;
      int firstFrameOffset = -1;
      if (buf.length < 7) {
         return -1;
      }

      while (buf.length > start && buf.length - start >= 4) {
         if (buf[start] == -1 && (buf[start + 1] & 224) == 224) {
            if (firstFrameOffset < 0) {
               firstFrameOffset = start;
               fileLength -= start;
            }

            int version = (buf[start + 1] & 24) >> 3;
            int layer = (buf[start + 1] & 6) >> 1;
            if (buf.length - start < 7) {
               break;
            }

            int headerLength;
            switch (version) {
               case -1:
               case 1:
                  headerLength = 0;
                  break;
               case 0:
               case 2:
               default:
                  if ((buf[start + 3] & 192) == 192) {
                     headerLength = 13;
                  } else {
                     headerLength = 21;
                  }
                  break;
               case 3:
                  if ((buf[start + 3] & 192) == 192) {
                     headerLength = 21;
                  } else {
                     headerLength = 36;
                  }
            }

            int sampleRate;
            int frameLength;
            if (layer == 0) {
               sampleRate = (buf[start + 2] & 60) >> 2;
               int blocks = buf[start + 6] & 3;
               if (sampleRate > 11) {
                  return -1;
               }

               if (aacSampleCount == null) {
                  aacSampleCount = new int[12];
               }

               aacSampleCount[sampleRate] += blocks + 1;
               frameLength = (buf[start + 5] & 224) >> 5 | (buf[start + 4] & 255) << 3 | (buf[start + 3] & 3) << 11;
            } else {
               int bitRate = (buf[start + 2] & 240) >> 4;
               sampleRate = (buf[start + 2] & 12) >> 2;
               int padding = (buf[start + 2] & 2) >> 1;
               if (mp3SampleCount == null) {
                  mp3SampleCount = new int[4][4];
               }

               if (layer == 3) {
                  mp3SampleCount[version][sampleRate] = mp3SampleCount[version][sampleRate] + 384;
               } else if (layer == 2) {
                  mp3SampleCount[version][sampleRate] = mp3SampleCount[version][sampleRate] + 1152;
               } else if ((version & 1) != 0) {
                  mp3SampleCount[version][sampleRate] = mp3SampleCount[version][sampleRate] + 1152;
               } else {
                  mp3SampleCount[version][sampleRate] = mp3SampleCount[version][sampleRate] + 576;
               }

               label202:
               switch (version) {
                  case -1:
                  case 1:
                     bitRate = 0;
                     break;
                  case 0:
                  case 2:
                  default:
                     switch (layer) {
                        case 0:
                           bitRate = 0;
                           break label202;
                        case 1:
                        case 2:
                           switch (bitRate) {
                              case 0:
                                 bitRate = 0;
                                 break label202;
                              case 1:
                              default:
                                 bitRate = 8000;
                                 break label202;
                              case 2:
                                 bitRate = 16000;
                                 break label202;
                              case 3:
                                 bitRate = 24000;
                                 break label202;
                              case 4:
                                 bitRate = 32000;
                                 break label202;
                              case 5:
                                 bitRate = 40000;
                                 break label202;
                              case 6:
                                 bitRate = 48000;
                                 break label202;
                              case 7:
                                 bitRate = 56000;
                                 break label202;
                              case 8:
                                 bitRate = 64000;
                                 break label202;
                              case 9:
                                 bitRate = 80000;
                                 break label202;
                              case 10:
                                 bitRate = 96000;
                                 break label202;
                              case 11:
                                 bitRate = 112000;
                                 break label202;
                              case 12:
                                 bitRate = 128000;
                                 break label202;
                              case 13:
                                 bitRate = 144000;
                                 break label202;
                              case 14:
                                 bitRate = 160000;
                                 break label202;
                           }
                        case 3:
                        default:
                           switch (bitRate) {
                              case 0:
                                 bitRate = 0;
                                 break label202;
                              case 1:
                              default:
                                 bitRate = 32000;
                                 break label202;
                              case 2:
                                 bitRate = 48000;
                                 break label202;
                              case 3:
                                 bitRate = 56000;
                                 break label202;
                              case 4:
                                 bitRate = 64000;
                                 break label202;
                              case 5:
                                 bitRate = 80000;
                                 break label202;
                              case 6:
                                 bitRate = 96000;
                                 break label202;
                              case 7:
                                 bitRate = 112000;
                                 break label202;
                              case 8:
                                 bitRate = 128000;
                                 break label202;
                              case 9:
                                 bitRate = 144000;
                                 break label202;
                              case 10:
                                 bitRate = 160000;
                                 break label202;
                              case 11:
                                 bitRate = 176000;
                                 break label202;
                              case 12:
                                 bitRate = 192000;
                                 break label202;
                              case 13:
                                 bitRate = 224000;
                                 break label202;
                              case 14:
                                 bitRate = 256000;
                                 break label202;
                           }
                     }
                  case 3:
                     label198:
                     switch (layer) {
                        case 0:
                           bitRate = 0;
                           break;
                        case 1:
                           switch (bitRate) {
                              case 0:
                                 bitRate = 0;
                                 break label198;
                              case 1:
                              default:
                                 bitRate = 32000;
                                 break label198;
                              case 2:
                                 bitRate = 40000;
                                 break label198;
                              case 3:
                                 bitRate = 48000;
                                 break label198;
                              case 4:
                                 bitRate = 56000;
                                 break label198;
                              case 5:
                                 bitRate = 64000;
                                 break label198;
                              case 6:
                                 bitRate = 80000;
                                 break label198;
                              case 7:
                                 bitRate = 96000;
                                 break label198;
                              case 8:
                                 bitRate = 112000;
                                 break label198;
                              case 9:
                                 bitRate = 128000;
                                 break label198;
                              case 10:
                                 bitRate = 160000;
                                 break label198;
                              case 11:
                                 bitRate = 192000;
                                 break label198;
                              case 12:
                                 bitRate = 224000;
                                 break label198;
                              case 13:
                                 bitRate = 256000;
                                 break label198;
                              case 14:
                                 bitRate = 320000;
                                 break label198;
                           }
                        case 2:
                           switch (bitRate) {
                              case 0:
                                 bitRate = 0;
                                 break label198;
                              case 1:
                              default:
                                 bitRate = 32000;
                                 break label198;
                              case 2:
                                 bitRate = 48000;
                                 break label198;
                              case 3:
                                 bitRate = 56000;
                                 break label198;
                              case 4:
                                 bitRate = 64000;
                                 break label198;
                              case 5:
                                 bitRate = 80000;
                                 break label198;
                              case 6:
                                 bitRate = 96000;
                                 break label198;
                              case 7:
                                 bitRate = 112000;
                                 break label198;
                              case 8:
                                 bitRate = 128000;
                                 break label198;
                              case 9:
                                 bitRate = 160000;
                                 break label198;
                              case 10:
                                 bitRate = 192000;
                                 break label198;
                              case 11:
                                 bitRate = 224000;
                                 break label198;
                              case 12:
                                 bitRate = 256000;
                                 break label198;
                              case 13:
                                 bitRate = 320000;
                                 break label198;
                              case 14:
                                 bitRate = 384000;
                                 break label198;
                           }
                        case 3:
                        default:
                           bitRate = bitRate < 15 ? bitRate * 32000 : 0;
                     }
               }

               sampleRate = mp3srate[version][sampleRate];
               if (sampleRate == 0) {
                  break;
               }

               if (layer == 3) {
                  frameLength = 48 * bitRate / sampleRate + padding;
               } else if (layer == 2) {
                  frameLength = 144 * bitRate / sampleRate + padding;
               } else if ((version & 1) != 0) {
                  frameLength = 144 * bitRate / sampleRate + padding;
               } else {
                  frameLength = 72 * bitRate / sampleRate + padding;
               }
            }

            if (buf.length - start >= headerLength + 8) {
               int xingHeader = getDword(buf, start + headerLength);
               if (xingHeader == 1483304551 || xingHeader == 1231971951) {
                  start += headerLength + 4;
                  long frames = -1;
                  long length = -1;
                  int flags = buf[start + 3];
                  start += 4;
                  if ((flags & 1) != 0) {
                     frames = getDword(buf, start) & 4294967295L;
                     start += 4;
                  }

                  if ((flags & 2) != 0) {
                     length = getDword(buf, start) & 4294967295L;
                     start += 4;
                  } else {
                     length = fileLength;
                  }

                  if (frames > 0 && length > 0) {
                     int bitRate;
                     if (layer == 3) {
                        bitRate = 384;
                     } else if (layer == 2) {
                        bitRate = 1152;
                     } else if ((version & 1) != 0) {
                        bitRate = 1152;
                     } else {
                        bitRate = 576;
                     }

                     if (sampleRate > 0) {
                        if (fileLength >= length - (length >> 4) && fileLength <= length + (length >> 4)) {
                           return frames * bitRate * 1000 / sampleRate * 1000;
                        }

                        return frames * bitRate * 1000 * fileLength / length / sampleRate * 1000;
                     }
                  }
               }
            }

            if (frameLength <= 0) {
               return -1;
            }

            start += frameLength;
         } else {
            start++;
         }
      }

      if (fileLength > 0 && firstFrameOffset >= 0) {
         start -= firstFrameOffset;
         if (start > 0) {
            long time = 0;
            if (mp3SampleCount != null) {
               for (int i = 0; i < 4; i++) {
                  for (int j = 0; j < 4; j++) {
                     if (mp3srate[i][j] != 0) {
                        time += (long)1024000000 * mp3SampleCount[i][j] / mp3srate[i][j];
                     }
                  }
               }
            }

            if (aacSampleCount != null) {
               int[] aacsrate = new int[]{
                  96000,
                  88200,
                  64000,
                  48000,
                  44100,
                  32000,
                  24000,
                  22050,
                  16000,
                  12000,
                  11025,
                  8000,
                  -805044217,
                  33686018,
                  131329,
                  -804781952,
                  589832,
                  655369,
                  720906,
                  786444,
                  917517,
                  983055,
                  1114128,
                  1245202,
                  1441813,
                  1572887,
                  1835034,
                  2031645,
                  2293793,
                  2555941,
                  2883625,
                  3211310,
                  3604532,
                  4063290,
                  4522049,
                  5111881,
                  5701714,
                  6422620,
                  7209064,
                  8061045,
                  9109635,
                  10223763,
                  11468966,
                  12845241,
                  14418128,
                  16187625,
                  18153734,
                  20381990
               };

               for (int i = 0; i <= 11; i++) {
                  time += 1048576000000L * aacSampleCount[i] / aacsrate[i];
               }
            }

            if (time >= 0) {
               return (time >> 10) * fileLength / start;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private static final int getDword(byte[] data, int offset) {
      return (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16 | (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
   }
}
