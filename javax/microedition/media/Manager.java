package javax.microedition.media;

import java.io.InputStream;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.media.protocol.DataSource;
import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.system.Audio;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringUtilities;
import net.rim.device.internal.applicationcontrol.ApplicationControl;
import net.rim.device.internal.i18n.CommonResource;
import net.rim.device.internal.media.DataSourceImpl;
import net.rim.device.internal.media.DeprecatedHttpDataSource;
import net.rim.device.internal.media.FileDataSource;
import net.rim.device.internal.media.MediaNatives;
import net.rim.device.internal.media.PlayerRegistry;
import net.rim.device.internal.media.RTSPDataSource;
import net.rim.device.internal.media.RTSPDataSourceFactory;
import net.rim.device.internal.media.StreamDataControl;
import net.rim.device.internal.system.InternalServices;

public final class Manager {
   public static final String TONE_DEVICE_LOCATOR;
   public static final String MIDI_DEVICE_LOCATOR;
   private static final String PME_DEVICE_LOCATOR;
   private static final String RECORD_DEVICE_LOCATOR;
   private static final String NOKIA_RINGTONE_DEVICE_LOCATOR;
   private static final String TONE_CONTENT_TYPE;
   private static SystemTimeBase _systemTimeBase = new SystemTimeBase();
   private static Player _playTonePlayer;

   private Manager() {
   }

   public static final String[] getSupportedContentTypes(String protocol) {
      String[] contentTypes = new String[0];
      if (protocol != null && !protocol.equals("http") && !protocol.equals("file")) {
         if (protocol.equals("device")) {
            Arrays.add(contentTypes, "audio/x-tone-seq");
            return contentTypes;
         }

         if (protocol.equals("capture")) {
            if (Audio.isRecordingCodecSupported(7)) {
               Arrays.add(contentTypes, "audio/amr");
            }

            if (Audio.isRecordingCodecSupported(9)) {
               Arrays.add(contentTypes, "audio/basic");
               return contentTypes;
            }
         } else if (protocol.equals("rtsp") && InternalServices.isSoftwareCapable(13)) {
            if (Audio.isCodecSupported(7)) {
               Arrays.add(contentTypes, "audio/amr");
            }

            if (Audio.isCodecSupported(10)) {
               Arrays.add(contentTypes, "audio/mp4");
               Arrays.add(contentTypes, "audio/aac");
            }

            if (InternalServices.isSoftwareCapable(7)) {
               Arrays.add(contentTypes, "video/mp4");
               Arrays.add(contentTypes, "video/3gpp");
               if (RadioInfo.getNetworkType() == 4) {
                  Arrays.add(contentTypes, "video/3gpp2");
               }

               if (RadioInfo.getNetworkType() != 4) {
                  Arrays.add(contentTypes, "video/x-msvideo");
               }

               Arrays.add(contentTypes, "video/quicktime");
            }
         }
      } else {
         if (protocol == null) {
            Arrays.add(contentTypes, "audio/x-tone-seq");
         }

         Arrays.add(contentTypes, "audio/midi");
         if (Audio.isCodecSupported(3)) {
            Arrays.add(contentTypes, "audio/mpeg");
         }

         if (Audio.isCodecSupported(0)) {
            Arrays.add(contentTypes, "audio/x-wav");
         }

         if (Audio.isCodecSupported(7)) {
            Arrays.add(contentTypes, "audio/amr");
         }

         if (Audio.isCodecSupported(9)) {
            Arrays.add(contentTypes, "audio/basic");
         }

         if (Audio.isCodecSupported(11)) {
            Arrays.add(contentTypes, "audio/x-gsm");
         }

         if (Audio.isCodecSupported(10)) {
            Arrays.add(contentTypes, "audio/mp4");
            Arrays.add(contentTypes, "audio/aac");
         }

         if (MediaNatives.isAudioDecoderCodecSupported(12)) {
            Arrays.add(contentTypes, "audio/x-ms-wma");
         }

         if (Audio.isCodecSupported(13)) {
            Arrays.add(contentTypes, "audio/qcelp");
         }

         if (InternalServices.isSoftwareCapable(7)) {
            Arrays.add(contentTypes, "video/mp4");
            Arrays.add(contentTypes, "video/3gpp");
            if (RadioInfo.getNetworkType() == 4) {
               Arrays.add(contentTypes, "video/3gpp2");
            }

            if (RadioInfo.getNetworkType() != 4) {
               Arrays.add(contentTypes, "video/x-msvideo");
            }

            Arrays.add(contentTypes, "video/quicktime");
         }

         if (MediaNatives.isVideoDecoderCodecSupported(4)) {
            Arrays.add(contentTypes, "video/x-ms-asf");
            Arrays.add(contentTypes, "video/x-ms-wm");
            Arrays.add(contentTypes, "video/x-ms-wmv");
            return contentTypes;
         }
      }

      return contentTypes;
   }

   public static final String[] getSupportedProtocols(String content_type) {
      boolean rtspSupported = InternalServices.isSoftwareCapable(13);
      if (content_type == null) {
         String[] protocols = new String[]{"device", "http", "file"};
         if (InternalServices.isSoftwareCapable(12)) {
            Arrays.add(protocols, "capture");
         }

         if (rtspSupported) {
            Arrays.add(protocols, "rtsp");
         }

         return protocols;
      } else {
         if (content_type.equals("audio/x-tone-seq")) {
            return new String[]{"device"};
         }

         if (content_type.equals("audio/midi")) {
            return new String[]{"http", "file"};
         }

         if (content_type.equals("audio/mpeg")) {
            if (Audio.isCodecSupported(3)) {
               return new String[]{"http", "file"};
            }
         } else if (content_type.equals("audio/x-wav")) {
            if (Audio.isCodecSupported(0)) {
               return new String[]{"http", "file"};
            }
         } else {
            if (content_type.equals("audio/amr")) {
               String[] protocols = new String[0];
               if (Audio.isCodecSupported(7)) {
                  Arrays.add(protocols, "http");
                  Arrays.add(protocols, "file");
               }

               if (Audio.isRecordingCodecSupported(7)) {
                  Arrays.add(protocols, "capture");
               }

               if (rtspSupported) {
                  Arrays.add(protocols, "rtsp");
               }

               return protocols;
            }

            if (content_type.equals("audio/basic")) {
               String[] protocols = new String[0];
               if (Audio.isCodecSupported(9)) {
                  Arrays.add(protocols, "http");
                  Arrays.add(protocols, "file");
               }

               if (Audio.isRecordingCodecSupported(9)) {
                  Arrays.add(protocols, "capture");
               }

               return protocols;
            }

            if (content_type.equals("audio/x-gsm")) {
               if (Audio.isCodecSupported(11)) {
                  return new String[]{"http", "file"};
               }
            } else if (!content_type.equals("audio/mp4")
               && !content_type.equals("audio/3gpp")
               && !content_type.equals("audio/aac")
               && (RadioInfo.getNetworkType() != 4 || !content_type.equals("audio/3gpp2"))) {
               if (content_type.equals("audio/x-ms-wma")) {
                  if (MediaNatives.isAudioDecoderCodecSupported(12)) {
                     return new String[]{"http", "file"};
                  }
               } else if (content_type.equals("audio/qcelp")) {
                  if (Audio.isCodecSupported(13)) {
                     return new String[]{"http", "file"};
                  }
               } else if (!content_type.equals("video/mp4")
                  && !content_type.equals("video/3gpp")
                  && (!content_type.equals("video/x-msvideo") || RadioInfo.getNetworkType() == 4)
                  && !content_type.equals("video/quicktime")
                  && (RadioInfo.getNetworkType() != 4 || !content_type.equals("video/3gpp2"))) {
                  if ((content_type.equals("video/x-ms-asf") || content_type.equals("video/x-ms-wm") || content_type.equals("video/x-ms-wmv"))
                     && MediaNatives.isVideoDecoderCodecSupported(4)) {
                     return new String[]{"http", "file"};
                  }
               } else if (InternalServices.isSoftwareCapable(7)) {
                  if (rtspSupported) {
                     return new String[]{"http", "file", "rtsp"};
                  }

                  return new String[]{"http", "file"};
               }
            } else if (Audio.isCodecSupported(10)) {
               if (rtspSupported) {
                  return new String[]{"http", "file", "rtsp"};
               }

               return new String[]{"http", "file"};
            }
         }

         return new String[0];
      }
   }

   public static final Player createPlayer(String locator) {
      assertPermission();
      if (locator == null) {
         throw new IllegalArgumentException("locator: null");
      }

      int index = locator.indexOf(58);
      if (index == -1) {
         throw new MediaException();
      }

      String scheme = locator.substring(0, index);
      DataSource ds = null;
      if (locator.equals("device://tone")) {
         return PlayerRegistry.createPlayer("audio/x-tone-seq");
      }

      if (!locator.equals("device://midi") && !locator.equals("device://nokia-ringtone") && !locator.equals("device://pme")) {
         if (!scheme.equals("capture")) {
            if (scheme.equals("http")) {
               ds = new DeprecatedHttpDataSource(locator);
            } else if (scheme.equals("file")) {
               ds = new FileDataSource(locator);
            } else {
               if (!scheme.equals("rtsp") || !InternalServices.isSoftwareCapable(13)) {
                  throw new MediaException("Unsupported type.");
               }

               ds = RTSPDataSourceFactory.createNewDataSource(locator, null);
            }
         } else {
            String normalizedType = null;
            int codec;
            if (locator.equals("capture://audio")) {
               normalizedType = "audio/amr";
               codec = 7;
            } else {
               String encodingString = "encoding=";
               int encodingStart = locator.indexOf(encodingString, locator.indexOf(63));
               if (encodingStart < 0) {
                  throw new MediaException();
               }

               int encodingEnd = locator.indexOf(38, encodingStart);
               String encoding = encodingEnd == -1
                  ? locator.substring(encodingStart + encodingString.length())
                  : locator.substring(encodingStart + encodingString.length(), encodingEnd);
               if (!encoding.equals("amr") && !encoding.equals("audio/amr")) {
                  if (!encoding.equals("audio/basic") && !encoding.equals("pcm")) {
                     throw new MediaException("Unsupported record encoding type");
                  }

                  normalizedType = "audio/basic";
                  codec = 9;
               } else {
                  normalizedType = "audio/amr";
                  codec = 7;
               }
            }

            if (!Audio.isRecordingCodecSupported(codec)) {
               throw new MediaException("Unsupported record encoding type");
            }

            try {
               Class clazz = Class.forName("net.rim.device.internal.media.RecordPlayer");
               Player recordPlayer = (Player)clazz.newInstance();
               if (recordPlayer instanceof StreamDataControl) {
                  ((StreamDataControl)recordPlayer).setKeyValue("mimetype", normalizedType);
                  return recordPlayer;
               }
            } catch (Exception e) {
               throw new MediaException("Unable to create Player");
            }
         }
      }

      if (ds != null) {
         return createPlayerImpl(ds);
      } else {
         throw new MediaException("Unsupported type.");
      }
   }

   public static final Player createPlayer(DataSource source) {
      assertPermission();
      return createPlayerImpl(source);
   }

   public static final Player createPlayer(InputStream stream, String type) {
      assertPermission();
      if (stream == null) {
         throw new IllegalArgumentException("stream: null");
      }

      if (type == null) {
         throw new MediaException("type: null");
      }

      DataSourceImpl ds = new DataSourceImpl(null);
      ds.setInputStream(stream);
      ds.setContentType(type);
      return createPlayerImpl(ds);
   }

   private static final Player createPlayerImpl(DataSource source) {
      if (source == null) {
         throw new IllegalArgumentException();
      }

      source.connect();
      String type = StringUtilities.toLowerCase(source.getContentType(), 1701707776);
      type = MIMETypeAssociations.getNormalizedType(type);
      if (type.equals("application/rtsp") && !(source instanceof RTSPDataSource)) {
         throw new MediaException("unsupported content type - " + type);
      }

      Player player = PlayerRegistry.createPlayer(type);
      if (player instanceof StreamDataControl) {
         source.start();

         try {
            StreamDataControl dataControl = (StreamDataControl)player;
            dataControl.setKeyValue("datasource", source);
            dataControl.setKeyValue("mimetype", type);
         } catch (Exception e) {
            throw new MediaException(e.getMessage());
         }
      }

      if (player != null) {
         return player;
      } else {
         throw new MediaException("unsupported content type - " + type);
      }
   }

   public static final void playTone(int note, int duration, int volume) {
      assertPermission();
      if (note >= 0 && note <= 127 && duration > 0) {
         if (_playTonePlayer.getState() == 400) {
            throw new MediaException("Could not play tone");
         }

         _playTonePlayer.deallocate();
         _playTonePlayer.realize();
         volume = MathUtilities.clamp(0, volume, 100);
         VolumeControl vc = (VolumeControl)_playTonePlayer.getControl("VolumeControl");
         if (vc != null) {
            vc.setLevel(volume);
         }

         byte defaultTempo = 30;
         byte defaultResolution = 64;
         byte toneDuration = (byte)(duration * defaultResolution * (defaultTempo << 2) / 240000 & 127);
         if (toneDuration == 0) {
            toneDuration = 1;
         }

         byte[] toneData = new byte[]{-2, 1, -3, defaultTempo, -4, defaultResolution, (byte)note, toneDuration};
         ToneControl tc = (ToneControl)_playTonePlayer.getControl("ToneControl");
         if (tc != null) {
            tc.setSequence(toneData);
         }

         _playTonePlayer.start();
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static final TimeBase getSystemTimeBase() {
      return _systemTimeBase;
   }

   private static final void assertPermission() {
      ApplicationControl.assertMediaPermitted(true, CommonResource.getBundle(), 10177);
   }

   static {
      PlayerRegistry.register("audio/x-tone-seq", "net.rim.device.internal.media.TonePlayer");

      try {
         Class clazz = Class.forName("net.rim.device.internal.media.TonePlayer");
         _playTonePlayer = (Player)clazz.newInstance();
      } catch (Exception var1) {
      }
   }
}
