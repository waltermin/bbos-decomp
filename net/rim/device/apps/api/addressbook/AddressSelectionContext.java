package net.rim.device.apps.api.addressbook;

import net.rim.device.apps.api.framework.model.Recognizer;
import net.rim.device.apps.api.framework.verb.Verb;

public final class AddressSelectionContext {
   private String _listScreenTitle;
   private String _findLabel;
   private String _pickDialogTitle;
   private String _badAddressError;
   private Recognizer[] _recognizers;
   private Verb[] _useOnceVerbs;
   private String _initialSearchPattern;
   private Object _context;
   private Object _selectedSource;
   private String[] _useEntryPrefixes;
   private int _preferredDefaultIndex;
   public static final long CONTEXT_ID;

   public AddressSelectionContext(String listScreenTitle, String pickDialogTitle, String badAddressError, Recognizer recognizer, Verb[] useOnceVerbs) {
      this._listScreenTitle = listScreenTitle;
      this._pickDialogTitle = pickDialogTitle;
      this._badAddressError = badAddressError;
      this._recognizers = new Object[]{recognizer};
      if (useOnceVerbs != null) {
         this._useOnceVerbs = useOnceVerbs;
      }
   }

   public AddressSelectionContext(String listScreenTitle, String pickDialogTitle, String badAddressError, Recognizer[] recognizers, Verb[] useOnceVerbs) {
      this._listScreenTitle = listScreenTitle;
      this._pickDialogTitle = pickDialogTitle;
      this._badAddressError = badAddressError;
      this._recognizers = recognizers;
      if (useOnceVerbs != null) {
         this._useOnceVerbs = useOnceVerbs;
      }
   }

   public final String getListScreenTitle() {
      return this._listScreenTitle;
   }

   public final String getPickDialogTitle() {
      return this._pickDialogTitle;
   }

   public final String getBadAddressError() {
      return this._badAddressError;
   }

   public final Recognizer[] getRecognizers() {
      return this._recognizers;
   }

   public final Verb[] getUseOnceVerbs() {
      return this._useOnceVerbs;
   }

   public final void setInitialSearchPattern(String newPattern) {
      this._initialSearchPattern = newPattern;
   }

   public final String getInitialSearchPattern() {
      return this._initialSearchPattern;
   }

   public final void setContext(Object newContext) {
      this._context = newContext;
   }

   public final Object getContext() {
      return this._context;
   }

   public final void setSelectedSource(Object selectedSource) {
      this._selectedSource = selectedSource;
   }

   public final Object getSelectedSource() {
      return this._selectedSource;
   }

   public final void setUseEntryPrefixes(String[] prefixes) {
      this._useEntryPrefixes = prefixes;
   }

   public final String[] getUseEntryPrefixes() {
      return this._useEntryPrefixes;
   }

   public final void setFindLabel(String label) {
      this._findLabel = label;
   }

   public final String getFindLabel() {
      return this._findLabel;
   }

   public final void setPreferredDefaultIndex(int preferredDefaultIndex) {
      if (preferredDefaultIndex < 0 || preferredDefaultIndex >= this._useOnceVerbs.length) {
         preferredDefaultIndex = 0;
      }

      this._preferredDefaultIndex = preferredDefaultIndex;
   }

   public final int getPreferredDefaultIndex() {
      return this._preferredDefaultIndex;
   }
}
