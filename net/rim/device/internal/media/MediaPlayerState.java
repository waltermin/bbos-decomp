package net.rim.device.internal.media;

import java.lang.ref.WeakReference;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.util.Arrays;

public class MediaPlayerState {
   private WeakReference[] _players = new WeakReference[0];
   private WeakReference _mediaPlayer;
   private static final long APP_REGISTRY_KEY = -4927398290786462096L;

   public static void setMediaPlayer(MediaPlayerStateInstance player) {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         if (player == null) {
            instance._mediaPlayer = null;
            return;
         }

         instance._mediaPlayer = new WeakReference(player);
      }
   }

   public static void registerPlayer(MediaPlayerStateInstance player) {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference wr = new WeakReference(player);
         Arrays.add(instance._players, wr);
      }

      clean();
   }

   public static void deregisterPlayer(MediaPlayerStateInstance player) {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference[] players = instance._players;
         WeakReference wr = null;

         for (int i = players.length - 1; i >= 0; i--) {
            wr = players[i];
            MediaPlayerStateInstance mps = (MediaPlayerStateInstance)wr.get();
            if (mps != null && mps == player) {
               break;
            }
         }

         if (wr != null) {
            Arrays.remove(instance._players, wr);
         }
      }

      clean();
   }

   private static void clean() {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference[] players = instance._players;

         for (int i = players.length - 1; i >= 0; i--) {
            WeakReference wr = players[i];
            if (wr.get() == null) {
               Arrays.remove(instance._players, wr);
            }
         }

         if (players.length == 0) {
            ApplicationRegistry.getApplicationRegistry().remove(-4927398290786462096L);
         }
      }
   }

   public static boolean areAnyPlayersRegistered() {
      clean();
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference[] players = instance._players;
         return players.length > 0 || isMediaPlayerRegistered();
      } else {
         return false;
      }
   }

   public static boolean isPlaying() {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference[] players = instance._players;

         for (int i = players.length - 1; i >= 0; i--) {
            WeakReference wr = players[i];
            MediaPlayerStateInstance mps = (MediaPlayerStateInstance)wr.get();
            if (mps != null && mps.isPlayerPlaying()) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isMediaPlayerPlaying() {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference wr = instance._mediaPlayer;
         if (wr != null) {
            MediaPlayerStateInstance mps = (MediaPlayerStateInstance)wr.get();
            if (mps != null) {
               return mps.isPlayerPlaying();
            }
         }
      }

      return false;
   }

   public static boolean isMediaPlayerPaused() {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference wr = instance._mediaPlayer;
         if (wr != null) {
            MediaPlayerStateInstance mps = (MediaPlayerStateInstance)wr.get();
            if (mps != null) {
               return mps.isPlayerPaused();
            }
         }
      }

      return false;
   }

   public static String getMediaPlayerURL() {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference wr = instance._mediaPlayer;
         if (wr != null) {
            MediaPlayerStateInstance mps = (MediaPlayerStateInstance)wr.get();
            if (mps != null) {
               return mps.getPlayingURL();
            }
         }
      }

      return null;
   }

   public static boolean isMediaPlayerRegistered() {
      MediaPlayerState instance = getInstance();
      if (instance != null) {
         WeakReference wr = instance._mediaPlayer;
         if (wr != null) {
            MediaPlayerStateInstance mps = (MediaPlayerStateInstance)wr.get();
            if (mps != null) {
               return true;
            }

            return false;
         }
      }

      return false;
   }

   private static MediaPlayerState getInstance() {
      ApplicationRegistry registry = ApplicationRegistry.getApplicationRegistry();
      MediaPlayerState instance = (MediaPlayerState)registry.get(-4927398290786462096L);
      if (instance == null) {
         instance = new MediaPlayerState();
         registry.replace(-4927398290786462096L, instance);
      }

      return instance;
   }
}
