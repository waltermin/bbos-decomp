package net.rim.tid.im.spellcheck;

import net.rim.tid.awt.im.InputContext;

public class Registration {
   public static void libMain(String[] args) {
      InputContext.getInstance(false).addIMDescriptor("Spell Check Engine", "net.rim.tid.im.spellcheck.SpellCheckDescriptor", true);
   }
}
