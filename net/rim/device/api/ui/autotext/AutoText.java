package net.rim.device.api.ui.autotext;

import java.util.Enumeration;
import net.rim.device.api.collection.CollectionEventSource;
import net.rim.device.api.collection.ReadableSet;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;

public class AutoText implements ReadableSet, CollectionEventSource {
   protected static final long KEY_NAME;
   public static final int SMART_CASE;
   public static final int SPECIFIED_CASE;

   public static final AutoText getAutoText() {
      return (AutoText)ApplicationRegistry.getApplicationRegistry().waitFor(-8979623067881713245L);
   }

   public Object add(String _1, String _2, int _3, Locale _4) {
      throw null;
   }

   public void remove(String _1, Locale _2) {
      throw null;
   }

   public String getReplacedString(Object _1) {
      throw null;
   }

   public String getReplacementStringPattern(Object _1) {
      throw null;
   }

   public int getReplacementCase(Object _1) {
      throw null;
   }

   public int getLocaleCode(Object _1) {
      throw null;
   }

   public Object checkWord(String _1) {
      throw null;
   }

   public Enumeration getAllKeys() {
      throw null;
   }

   public int getWordCount() {
      throw null;
   }

   public String[] getMacroChoices() {
      throw null;
   }

   public String getMacroText(int _1) {
      throw null;
   }

   public boolean isClauseSeparator(char _1) {
      throw null;
   }

   public String getClauseSeparatorString() {
      throw null;
   }

   public boolean isNoAutoPeriodCharacter(char _1) {
      throw null;
   }

   public String getNoAutoPeriodCharacterString() {
      throw null;
   }

   public boolean isSentenceTerminator(char _1) {
      throw null;
   }

   public int getDataVersion() {
      return 0;
   }

   public void setDataVersion(int version) {
   }

   @Override
   public int getElements(Object[] _1) {
      throw null;
   }

   @Override
   public Enumeration getElements() {
      throw null;
   }

   @Override
   public boolean contains(Object _1) {
      throw null;
   }

   @Override
   public int size() {
      throw null;
   }

   @Override
   public void removeCollectionListener(Object _1) {
      throw null;
   }

   @Override
   public void addCollectionListener(Object _1) {
      throw null;
   }
}
