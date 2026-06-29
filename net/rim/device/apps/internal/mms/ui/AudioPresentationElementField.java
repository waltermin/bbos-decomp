package net.rim.device.apps.internal.mms.ui;

import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

final class AudioPresentationElementField extends HorizontalFieldManager implements PresentationElement, MMSAttachment {
   private MMSAttachment _attachment;
   private Player _tunePlayer;
   private VolumeControl _volumeControl;
   private AudioPresentationElementField$AudioButtonField _buttonField;
   private int _flags;
   private boolean _isEditable;
   public static final int FLAG_DISPLAY_CONTROLS;
   public static final int FLAG_MOVE_MODE;
   private static final int ACTION_STOP;
   private static final int ACTION_PLAY;

   public AudioPresentationElementField(MMSAttachment param1, int param2, String param3, boolean param4) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.RuntimeException: parsing failure!
      //   at org.jetbrains.java.decompiler.modules.decompiler.decompose.DomHelper.parseGraph(DomHelper.java:211)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:174)
      //
      // Bytecode:
      // 00: aload 0
      // 01: invokespecial net/rim/device/api/ui/container/HorizontalFieldManager.<init> ()V
      // 04: aload 0
      // 05: iload 4
      // 07: putfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._isEditable Z
      // 0a: aload 0
      // 0b: aload 1
      // 0c: putfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._attachment Lnet/rim/device/apps/internal/mms/api/MMSAttachment;
      // 0f: aload 0
      // 10: iload 2
      // 11: putfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._flags I
      // 14: aload 1
      // 15: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getType ()I 1
      // 1a: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.isAudioType (I)Z
      // 1d: ifeq 7a
      // 20: aload 1
      // 21: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getData ()[B 1
      // 26: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.decrypt ([B)[B
      // 29: astore 5
      // 2b: aload 5
      // 2d: ifnull 7a
      // 30: aload 1
      // 31: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getType ()I 1
      // 36: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMETypeString (I)Ljava/lang/String;
      // 39: astore 6
      // 3b: aload 0
      // 3c: new java/lang/Object
      // 3f: dup
      // 40: aload 5
      // 42: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 45: aload 6
      // 47: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/io/InputStream;Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 4a: putfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._tunePlayer Ljavax/microedition/media/Player;
      // 4d: aload 0
      // 4e: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._tunePlayer Ljavax/microedition/media/Player;
      // 51: ifnull 7a
      // 54: aload 0
      // 55: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._tunePlayer Ljavax/microedition/media/Player;
      // 58: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 5d: aload 0
      // 5e: aload 0
      // 5f: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._tunePlayer Ljavax/microedition/media/Player;
      // 62: ldc_w "javax.microedition.media.control.VolumeControl"
      // 65: invokeinterface javax/microedition/media/Controllable.getControl (Ljava/lang/String;)Ljavax/microedition/media/Control; 2
      // 6a: checkcast java/lang/Object
      // 6d: putfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._volumeControl Ljavax/microedition/media/control/VolumeControl;
      // 70: goto 7a
      // 73: astore 5
      // 75: goto 7a
      // 78: astore 5
      // 7a: aload 0
      // 7b: bipush 1
      // 7c: invokespecial net/rim/device/apps/internal/mms/ui/AudioPresentationElementField.getFlag (I)Z
      // 7f: ifeq f4
      // 82: aload 0
      // 83: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._tunePlayer Ljavax/microedition/media/Player;
      // 86: ifnonnull a1
      // 89: ldc_w "BrokenImage.gif"
      // 8c: invokestatic net/rim/device/api/system/Bitmap.getBitmapResource (Ljava/lang/String;)Lnet/rim/device/api/system/Bitmap;
      // 8f: astore 5
      // 91: aload 0
      // 92: new java/lang/Object
      // 95: dup
      // 96: aload 5
      // 98: invokespecial net/rim/device/api/ui/component/BitmapField.<init> (Lnet/rim/device/api/system/Bitmap;)V
      // 9b: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // 9e: goto c4
      // a1: aload 0
      // a2: new net/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioButtonField
      // a5: dup
      // a6: aload 0
      // a7: bipush 2
      // a9: invokespecial net/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioButtonField.<init> (Lnet/rim/device/apps/internal/mms/ui/AudioPresentationElementField;I)V
      // ac: putfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._buttonField Lnet/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioButtonField;
      // af: aload 0
      // b0: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._tunePlayer Ljavax/microedition/media/Player;
      // b3: aload 0
      // b4: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._buttonField Lnet/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioButtonField;
      // b7: invokeinterface javax/microedition/media/Player.addPlayerListener (Ljavax/microedition/media/PlayerListener;)V 2
      // bc: aload 0
      // bd: aload 0
      // be: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._buttonField Lnet/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioButtonField;
      // c1: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // c4: iload 4
      // c6: ifeq d4
      // c9: new net/rim/device/apps/internal/mms/verbs/DeleteFieldVerb
      // cc: dup
      // cd: aload 0
      // ce: invokespecial net/rim/device/apps/internal/mms/verbs/DeleteFieldVerb.<init> (Lnet/rim/device/api/ui/Field;)V
      // d1: goto d5
      // d4: aconst_null
      // d5: astore 5
      // d7: aload 3
      // d8: ifnonnull e5
      // db: aload 0
      // dc: getfield net/rim/device/apps/internal/mms/ui/AudioPresentationElementField._attachment Lnet/rim/device/apps/internal/mms/api/MMSAttachment;
      // df: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getName ()Ljava/lang/String; 1
      // e4: astore 3
      // e5: aload 0
      // e6: new net/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioLabelField
      // e9: dup
      // ea: aload 0
      // eb: aload 3
      // ec: aload 5
      // ee: invokespecial net/rim/device/apps/internal/mms/ui/AudioPresentationElementField$AudioLabelField.<init> (Lnet/rim/device/apps/internal/mms/ui/AudioPresentationElementField;Ljava/lang/String;Lnet/rim/device/apps/api/framework/verb/Verb;)V
      // f1: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // f4: return
      // try (11 -> 46): 47 null
      // try (11 -> 46): 49 null
   }

   private final boolean getFlag(int flagToCheck) {
      return (this._flags & flagToCheck) == flagToCheck;
   }

   private final void clearFlag(int flagToClear) {
      this._flags &= ~flagToClear;
   }

   private final void setFlag(int flagToSet) {
      this._flags |= flagToSet;
   }

   private final void setFlag(int flag, boolean value) {
      if (value) {
         this.setFlag(flag);
      } else {
         this.clearFlag(flag);
      }
   }

   @Override
   public final void copyTo(MMSPresentationModel target) {
      target.addPresentationElement(this.getName(), this.getType(), this._isEditable);
   }

   @Override
   public final boolean canMove() {
      return this._isEditable;
   }

   @Override
   public final void move(boolean mode) {
      if (this.getFlag(2) != mode) {
         this.setFlag(2, mode);
         this.invalidate();
      }
   }

   @Override
   public final String getName() {
      return this._attachment.getName();
   }

   @Override
   public final int getType() {
      return this._attachment.getType();
   }

   @Override
   public final byte[] getData() {
      return this._attachment.getData();
   }

   @Override
   public final String getCharset() {
      return this._attachment.getCharset();
   }

   @Override
   public final int getDataSize() {
      return this._attachment.getDataSize();
   }

   @Override
   public final void onObscured() {
      super.onObscured();
      this.stopTune();
   }

   @Override
   protected final void onUndisplay() {
      this.stopTune();
      super.onUndisplay();
   }

   private final void startTune() {
      if (this._tunePlayer != null) {
         label24:
         try {
            this._tunePlayer.start();
         } finally {
            break label24;
         }

         MMSModelScreen screen = (MMSModelScreen)this.getScreen();
         if (screen != null) {
            screen.setVolumeControl(this._volumeControl);
         }
      }
   }

   private final void stopTune() {
      if (this._tunePlayer != null) {
         label24:
         try {
            this._tunePlayer.stop();
         } finally {
            break label24;
         }

         MMSModelScreen screen = (MMSModelScreen)this.getScreen();
         if (screen != null) {
            screen.setVolumeControl(null);
         }
      }
   }
}
