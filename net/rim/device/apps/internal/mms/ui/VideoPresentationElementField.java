package net.rim.device.apps.internal.mms.ui;

import javax.microedition.media.Player;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.apps.internal.mms.api.AttachmentDataProvider;
import net.rim.device.apps.internal.mms.api.MMSAttachment;
import net.rim.device.apps.internal.mms.api.MMSPresentationModel;

final class VideoPresentationElementField extends HorizontalFieldManager implements PresentationElement, MMSAttachment {
   private Player _videoPlayer;
   private VideoPresentationElementField$VideoButtonField _buttonField;
   private int _flags;
   private MMSAttachment _attachment;
   private boolean _isEditable;
   private AttachmentDataProvider _attachmentProvider;
   private boolean _isForwardLocked;
   public static final int FLAG_DISPLAY_CONTROLS = 1;
   public static final int FLAG_MOVE_MODE = 2;

   public VideoPresentationElementField(MMSAttachment param1, int param2, boolean param3, AttachmentDataProvider param4, boolean param5) {
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
      // 05: iload 3
      // 06: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._isEditable Z
      // 09: aload 0
      // 0a: aload 1
      // 0b: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._attachment Lnet/rim/device/apps/internal/mms/api/MMSAttachment;
      // 0e: aload 0
      // 0f: aload 4
      // 11: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._attachmentProvider Lnet/rim/device/apps/internal/mms/api/AttachmentDataProvider;
      // 14: aload 0
      // 15: iload 5
      // 17: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._isForwardLocked Z
      // 1a: aload 0
      // 1b: iload 2
      // 1c: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._flags I
      // 1f: aload 1
      // 20: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getType ()I 1
      // 25: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.isVideoType (I)Z
      // 28: ifeq 72
      // 2b: aload 1
      // 2c: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getData ()[B 1
      // 31: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.decrypt ([B)[B
      // 34: astore 6
      // 36: aload 6
      // 38: ifnull 72
      // 3b: aload 1
      // 3c: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getType ()I 1
      // 41: invokestatic net/rim/device/apps/internal/mms/MMSUtilities.getMIMETypeString (I)Ljava/lang/String;
      // 44: astore 7
      // 46: aload 0
      // 47: new java/lang/Object
      // 4a: dup
      // 4b: aload 6
      // 4d: invokespecial java/io/ByteArrayInputStream.<init> ([B)V
      // 50: aload 7
      // 52: invokestatic javax/microedition/media/Manager.createPlayer (Ljava/io/InputStream;Ljava/lang/String;)Ljavax/microedition/media/Player;
      // 55: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._videoPlayer Ljavax/microedition/media/Player;
      // 58: aload 0
      // 59: getfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._videoPlayer Ljavax/microedition/media/Player;
      // 5c: ifnull 72
      // 5f: aload 0
      // 60: getfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._videoPlayer Ljavax/microedition/media/Player;
      // 63: invokeinterface javax/microedition/media/Player.realize ()V 1
      // 68: goto 72
      // 6b: astore 6
      // 6d: goto 72
      // 70: astore 6
      // 72: aload 0
      // 73: bipush 1
      // 74: invokespecial net/rim/device/apps/internal/mms/ui/VideoPresentationElementField.getFlag (I)Z
      // 77: ifeq dc
      // 7a: iload 3
      // 7b: ifeq 89
      // 7e: new net/rim/device/apps/internal/mms/verbs/DeleteFieldVerb
      // 81: dup
      // 82: aload 0
      // 83: invokespecial net/rim/device/apps/internal/mms/verbs/DeleteFieldVerb.<init> (Lnet/rim/device/api/ui/Field;)V
      // 86: goto 8a
      // 89: aconst_null
      // 8a: astore 6
      // 8c: aload 0
      // 8d: getfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._videoPlayer Ljavax/microedition/media/Player;
      // 90: ifnonnull ab
      // 93: ldc_w "BrokenImage.gif"
      // 96: invokestatic net/rim/device/api/system/Bitmap.getBitmapResource (Ljava/lang/String;)Lnet/rim/device/api/system/Bitmap;
      // 99: astore 7
      // 9b: aload 0
      // 9c: new java/lang/Object
      // 9f: dup
      // a0: aload 7
      // a2: invokespecial net/rim/device/api/ui/component/BitmapField.<init> (Lnet/rim/device/api/system/Bitmap;)V
      // a5: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // a8: goto c1
      // ab: aload 0
      // ac: new net/rim/device/apps/internal/mms/ui/VideoPresentationElementField$VideoButtonField
      // af: dup
      // b0: aload 0
      // b1: aload 6
      // b3: invokespecial net/rim/device/apps/internal/mms/ui/VideoPresentationElementField$VideoButtonField.<init> (Lnet/rim/device/apps/internal/mms/ui/VideoPresentationElementField;Lnet/rim/device/apps/api/framework/verb/Verb;)V
      // b6: putfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._buttonField Lnet/rim/device/apps/internal/mms/ui/VideoPresentationElementField$VideoButtonField;
      // b9: aload 0
      // ba: aload 0
      // bb: getfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._buttonField Lnet/rim/device/apps/internal/mms/ui/VideoPresentationElementField$VideoButtonField;
      // be: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // c1: aload 0
      // c2: getfield net/rim/device/apps/internal/mms/ui/VideoPresentationElementField._attachment Lnet/rim/device/apps/internal/mms/api/MMSAttachment;
      // c5: invokeinterface net/rim/device/apps/internal/mms/api/MMSAttachment.getName ()Ljava/lang/String; 1
      // ca: astore 7
      // cc: aload 0
      // cd: new net/rim/device/apps/internal/mms/ui/VideoPresentationElementField$VideoLabelField
      // d0: dup
      // d1: aload 0
      // d2: aload 7
      // d4: aload 6
      // d6: invokespecial net/rim/device/apps/internal/mms/ui/VideoPresentationElementField$VideoLabelField.<init> (Lnet/rim/device/apps/internal/mms/ui/VideoPresentationElementField;Ljava/lang/String;Lnet/rim/device/apps/api/framework/verb/Verb;)V
      // d9: invokevirtual net/rim/device/api/ui/Manager.add (Lnet/rim/device/api/ui/Field;)V
      // dc: return
      // try (17 -> 45): 46 null
      // try (17 -> 45): 48 null
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

   private final void startTune() {
      VideoPresentationElementField$MMSMediaPlayerDialog mpd = new VideoPresentationElementField$MMSMediaPlayerDialog(
         this._attachment, this._isEditable, this._attachmentProvider, this._isForwardLocked
      );
      mpd.show();
   }
}
