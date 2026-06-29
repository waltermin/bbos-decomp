package net.rim.device.apps.internal.phone.api.ui;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.apps.internal.phone.resource.PhoneResources;

public final class EndCallHintField extends LabelField {
   private static final boolean _smallMonoScreen;
   private static final boolean _largeMonoScreen;
   private static final boolean _monoScreen;
   private static final boolean _hasSendEndKeys;
   private static final int COLOUR_FONT_SIZE;
   private static Font _defaultFont;

   public EndCallHintField() {
      this(null);
   }

   public EndCallHintField(String hintText) {
      super(null);
      this.setPosition(1);
      if (hintText != null) {
         this.setText(hintText);
      } else {
         this.setText(PhoneResources.getResourceBundle(), 458);
      }

      if (!_hasSendEndKeys) {
         if (_smallMonoScreen) {
            this.setFont(Font.getDefault().derive(0, 11, 3));
            return;
         }

         this.setFont(_defaultFont);
      }
   }

   @Override
   protected final void paint(Graphics g) {
      g.fillRect(0, 0, this.getWidth(), this.getHeight());
      int foreground = g.getColor();
      g.setColor(g.getBackgroundColor());
      super.paint(g);
      g.setColor(foreground);
   }

   static {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Could not find destination nodes for stat id {Block}:11 from source 13
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.FlattenStatementsHelper.setEdges(FlattenStatementsHelper.java:563)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.FlattenStatementsHelper.buildDirectGraph(FlattenStatementsHelper.java:50)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.<init>(VarDefinitionHelper.java:151)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarDefinitionHelper.<init>(VarDefinitionHelper.java:52)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarDefinitions(VarProcessor.java:52)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:458)
      //
      // Bytecode:
      // 00: invokestatic net/rim/device/apps/internal/phone/api/PhoneUtilities.smallMonoScreen ()Z
      // 03: putstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._smallMonoScreen Z
      // 06: invokestatic net/rim/device/apps/internal/phone/api/PhoneUtilities.largeMonoScreen ()Z
      // 09: putstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._largeMonoScreen Z
      // 0c: getstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._smallMonoScreen Z
      // 0f: ifne 18
      // 12: getstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._largeMonoScreen Z
      // 15: ifeq 1c
      // 18: bipush 1
      // 19: goto 1d
      // 1c: bipush 0
      // 1d: putstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._monoScreen Z
      // 20: invokestatic net/rim/device/api/ui/Keypad.hasSendEndKeys ()Z
      // 23: putstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._hasSendEndKeys Z
      // 26: getstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._hasSendEndKeys Z
      // 29: ifne 54
      // 2c: getstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._monoScreen Z
      // 2f: ifeq 41
      // 32: invokestatic net/rim/device/api/ui/Font.getDefault ()Lnet/rim/device/api/ui/Font;
      // 35: bipush 0
      // 36: bipush 12
      // 38: bipush 3
      // 3a: invokevirtual net/rim/device/api/ui/Font.derive (III)Lnet/rim/device/api/ui/Font;
      // 3d: putstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._defaultFont Lnet/rim/device/api/ui/Font;
      // 40: return
      // 41: ldc_w "BBMillbank"
      // 44: invokestatic net/rim/device/api/ui/FontFamily.forName (Ljava/lang/String;)Lnet/rim/device/api/ui/FontFamily;
      // 47: astore 0
      // 48: aload 0
      // 49: bipush 1
      // 4a: bipush 16
      // 4c: invokevirtual net/rim/device/api/ui/FontFamily.getFont (II)Lnet/rim/device/api/ui/Font;
      // 4f: putstatic net/rim/device/apps/internal/phone/api/ui/EndCallHintField._defaultFont Lnet/rim/device/api/ui/Font;
      // 52: return
      // 53: astore 0
      // 54: return
      // try (25 -> 33): 34 null
   }
}
