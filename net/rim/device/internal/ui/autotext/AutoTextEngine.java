package net.rim.device.internal.ui.autotext;

import java.util.Enumeration;
import net.rim.device.api.i18n.Locale;
import net.rim.device.api.system.ApplicationRegistry;
import net.rim.device.api.ui.autotext.AutoText;

public final class AutoTextEngine extends AutoText {
   private AutoTextCollection _collection = new AutoTextCollection();

   public AutoTextEngine() {
      this.enableSynchronization();
      ApplicationRegistry.getApplicationRegistry().put(-8979623067881713245L, this);
   }

   private static final String cleanUpWord(String word) {
      word = word.trim();
      return word.toLowerCase();
   }

   @Override
   public final Object add(String replacedString, String replacementStringPattern, int replacementCase, Locale locale) {
      return this._collection.add(cleanUpWord(replacedString), replacementStringPattern, replacementCase, locale.getCode());
   }

   @Override
   public final void remove(String replacedString, Locale locale) {
      this._collection.remove(cleanUpWord(replacedString), locale.getCode());
   }

   @Override
   public final String getReplacedString(Object entry) {
      return !(entry instanceof AutoTextEntry) ? null : ((AutoTextEntry)entry).getFindString();
   }

   @Override
   public final String getReplacementStringPattern(Object entry) {
      return !(entry instanceof AutoTextEntry) ? null : ((AutoTextEntry)entry).getReplaceString();
   }

   @Override
   public final int getReplacementCase(Object entry) {
      return !(entry instanceof AutoTextEntry) ? 0 : ((AutoTextEntry)entry).getCase();
   }

   @Override
   public final int getLocaleCode(Object entry) {
      return !(entry instanceof AutoTextEntry) ? 0 : ((AutoTextEntry)entry).getLocaleCode();
   }

   @Override
   public final Object checkWord(String word) {
      return word == null ? null : this._collection.get(cleanUpWord(word));
   }

   @Override
   public final Enumeration getAllKeys() {
      return this._collection.getAllKeys();
   }

   @Override
   public final int getWordCount() {
      return this._collection.getWordCount();
   }

   @Override
   public final String[] getMacroChoices() {
      return AutoTextMacro.getMacroChoices();
   }

   @Override
   public final String getMacroText(int index) {
      return AutoTextMacro.getMacroRep(index);
   }

   @Override
   public final boolean isClauseSeparator(char c) {
      return ClauseSeparator.isClauseSeparator(c);
   }

   @Override
   public final String getClauseSeparatorString() {
      return ClauseSeparator.getClauseSeparatorString();
   }

   @Override
   public final boolean isNoAutoPeriodCharacter(char c) {
      return NoAutoPeriodCharacter.isNoAutoPeriodCharacter(c);
   }

   @Override
   public final String getNoAutoPeriodCharacterString() {
      return NoAutoPeriodCharacter.getNoAutoPeriodCharacterString();
   }

   @Override
   public final boolean isSentenceTerminator(char c) {
      return SentenceTerminator.isSentenceTerminator(c);
   }

   @Override
   public final int getDataVersion() {
      return AutoTextDataVersion.getInstance().getVersion();
   }

   @Override
   public final void setDataVersion(int version) {
      AutoTextDataVersion.getInstance().setVersion(version);
   }

   public final void enableSynchronization() {
      this._collection.enableSynchronization();
   }

   @Override
   public final int size() {
      return this._collection.size();
   }

   @Override
   public final boolean contains(Object element) {
      return this._collection.contains(element);
   }

   @Override
   public final Enumeration getElements() {
      return this._collection.getElements();
   }

   @Override
   public final int getElements(Object[] elements) {
      return this._collection.getElements(elements);
   }

   @Override
   public final void addCollectionListener(Object listener) {
      this._collection.addCollectionListener(listener);
   }

   @Override
   public final void removeCollectionListener(Object listener) {
      this._collection.removeCollectionListener(listener);
   }
}
