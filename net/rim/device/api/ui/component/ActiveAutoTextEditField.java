package net.rim.device.api.ui.component;

import net.rim.device.api.system.Clipboard;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.util.AbstractString;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.EmoticonStringPattern;
import net.rim.device.api.util.IntHashtable;
import net.rim.device.api.util.MathUtilities;
import net.rim.device.api.util.StringPattern;
import net.rim.device.api.util.StringPattern$Match;
import net.rim.device.api.util.StringPatternContainer;
import net.rim.device.api.util.StringPatternEnumerator;
import net.rim.device.api.util.StringPatternRepository$Internal;
import net.rim.device.internal.ui.FormatParams;
import net.rim.tid.text.AttributedString;
import net.rim.tid.text.AttributedString$Iterator;
import net.rim.vm.Array;
import net.rim.vm.Process;

public class ActiveAutoTextEditField extends AutoTextEditField implements CookieProvider, Runnable, ActiveRegionSupport$ActiveRegionFieldIf {
   private IntHashtable _cookieID;
   private int _regionCount;
   private boolean _invertCookieRegion = true;
   private boolean _snapToCookie = true;
   private int _pendingVersion;
   private StringPatternContainer _patterns;
   private StringPatternEnumerator _enumerator;
   private StringPattern$Match _match = new StringPattern$Match();
   private int[] _pendingOffsets;
   private long[] _pendingCookieID;
   private int _pendingRegionCount;
   private Thread _waitingThread;
   private Thread _activeThread;
   private AttributedString$Iterator _threadSafeIterator;
   private ActiveRegionSupport _arSupport;
   private SmileySupport _smileySupport;
   private boolean _scanForSmileys = true;
   private boolean _inPostResults = false;
   private int[] _sectionLen = new int[3];
   private static final int REGION_INCREMENT;

   public void scanForActiveRegions() {
      this._pendingVersion++;
      if (this._waitingThread == null) {
         ThreadPool.post(this);
      }
   }

   protected boolean regionHasCookie() {
      return this._arSupport.isInCookieRegion(this.getCaretPosition());
   }

   protected boolean regionHasCookie(int region) {
      return region < this._regionCount && region >= 0;
   }

   protected int getRegion() {
      return this.getRegion(this.getCursorPosition() + this.getLabelLength());
   }

   protected int getRegion(int curPos) {
      return this._arSupport.getRegion(curPos);
   }

   protected String getRegionText(int region) {
      return this._arSupport.getRegionText(region, super._text);
   }

   protected synchronized int drawText(Graphics graphics, int x, int y, int offset, int length) {
      return 0;
   }

   @Override
   public synchronized Object getCookieWithFocus() {
      return this._arSupport.getCookieWithFocus(this.getCaretPosition());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void run() {
      Process.waitForIdle(1000);
      int version = this.initialize();
      if (version >= 0) {
         boolean var13 = false /* VF: Semaphore variable */;

         label77: {
            try {
               var13 = true;
               if (this.scanForActiveRegions(version)) {
                  this.postResults(version);
                  this.invalidate();
                  var13 = false;
               } else {
                  var13 = false;
               }
               break label77;
            } catch (IndexOutOfBoundsException var17) {
               var13 = false;
            } finally {
               if (var13) {
                  synchronized (this) {
                     this._activeThread = null;
                     this.notify();
                  }
               }
            }

            synchronized (this) {
               this._activeThread = null;
               this.notify();
               return;
            }
         }

         synchronized (this) {
            this._activeThread = null;
            this.notify();
         }
      }
   }

   @Override
   public boolean isCookieValid(int id) {
      return true;
   }

   @Override
   public Object getCookie(int id) {
      if (this._cookieID != null && this._cookieID.containsKey(id) && id >= 0) {
         long[] cookieIDs = (long[])this._cookieID.get(id);
         int numCookies = cookieIDs.length;
         if (numCookies > 1) {
            Object[] cookies = new Object[numCookies];

            for (int i = 0; i < numCookies; i++) {
               cookies[i] = this._arSupport.createCookie(super._text, cookieIDs[i]);
            }

            return cookies;
         } else {
            return numCookies == 1 ? this._arSupport.createCookie(super._text, cookieIDs[0]) : null;
         }
      } else {
         return null;
      }
   }

   @Override
   public boolean regionsHaveSameCookie(int regionId1, int regionId2) {
      return false;
   }

   @Override
   protected void drawFocus(Graphics graphics, boolean on) {
      if (this.isSelecting() || !this._invertCookieRegion) {
         XYRect focusRect = Ui.getTmpXYRect();
         super.getFocusRect(focusRect);
         graphics.pushContext(focusRect, 0, 0);
         Ui.returnTmpXYRect(focusRect);
         super.drawFocus(graphics, on);
         graphics.popContext();
      } else if (!this.regionHasCookie()) {
         XYRect focusRect = Ui.getTmpXYRect();
         super.getFocusRect(focusRect);
         graphics.pushContext(focusRect, 0, 0);
         Ui.returnTmpXYRect(focusRect);
         super.drawFocus(graphics, on);
         graphics.popContext();
      } else {
         this.highlightSelectedArea(graphics, on, this._arSupport.getRunStart(), this._arSupport.getRunEnd());
         XYRect focusRect = Ui.getTmpXYRect();
         super.getFocusRect(focusRect);
         int y = focusRect.y;
         int height = focusRect.height >> 2;
         y += focusRect.height - height;
         this.drawHighlightRegion(graphics, 1, on, focusRect.x, y, focusRect.width, height);
         Ui.returnTmpXYRect(focusRect);
      }
   }

   @Override
   public synchronized void getFocusRect(XYRect rect) {
      super.getFocusRect(rect);
      this._arSupport.init();
      this._arSupport.getFocusRect(rect, this);
   }

   @Override
   public void selectionCopy(Clipboard cb) {
      if (!this.isSelecting() && this.regionHasCookie() && this._invertCookieRegion) {
         cb.put(new AttributedString(super._text, this._arSupport.getRunStart(), this._arSupport.getRunEnd()));
      } else {
         super.selectionCopy(cb);
      }
   }

   @Override
   public void selectionDelete() {
      if (!this.isSelecting() && this.regionHasCookie() && this._invertCookieRegion) {
         int start = this._arSupport.getRunStart();
         int end = this._arSupport.getRunEnd();
         int length = end - start;
         this.setCursorPosition(end - this.getLabelLength(), this.isCursorPositionSet() ? 0 : Integer.MIN_VALUE);
         this.backspace(length, 0);
      } else {
         super.selectionDelete();
      }
   }

   @Override
   public boolean isSelectionCopyable() {
      return !this.isSelecting() && this._invertCookieRegion ? this.regionHasCookie() : super.isSelectionCopyable();
   }

   @Override
   public boolean isSelectionDeleteable() {
      return this.isSelecting() || !this._invertCookieRegion ? super.isSelectionDeleteable() : !this.isStyle(9007199254740992L) && this.regionHasCookie();
   }

   @Override
   protected int scrollHorizontally(int amount) {
      int result = super.scrollHorizontally(amount);
      this._snapToCookie = false;
      return result;
   }

   @Override
   protected int scrollVertically(int amount) {
      if (!this._snapToCookie || this.isSelecting()) {
         return super.scrollVertically(amount);
      }

      if (amount == 0) {
         return 0;
      }

      amount = this._arSupport.scrollVertically(amount, this, this.getCursorLine()._line);
      if (amount != 0) {
         return super.scrollVertically(amount + this._arSupport.getAdjustedAmount());
      }

      super.scrollVertically(this._arSupport.getAdjustedAmount());
      if (this._arSupport.endsOnCookie()) {
         int newPos = MathUtilities.clamp(this._arSupport.getRunStart(), this.getCaretPosition(), this._arSupport.getRunEnd() - 1);
         this.setSelection(newPos, true, newPos);
      }

      return 0;
   }

   public ActiveAutoTextEditField(String label, String initialValue, int maxNumChars, long style, StringPatternContainer patterns) {
      super(label, initialValue, maxNumChars, style);
      this._threadSafeIterator = super._text.getIterator();
      this._arSupport = new ActiveRegionSupport(super._text.getIterator(), this);
      this._smileySupport = new SmileySupport(this);
      this._pendingOffsets = new int[16];
      this._pendingCookieID = new long[8];
      this._pendingRegionCount = 0;
      if (patterns != null) {
         for (int index = 0; index < patterns.size(); index++) {
            Object pattern = patterns.getAt(index);
            if (pattern instanceof EmoticonStringPattern) {
               this._smileySupport.setPattern((EmoticonStringPattern)pattern);
               if (patterns.size() > 1) {
                  StringPattern[] elements = new StringPattern[patterns.size() - 1];

                  for (int index1 = 0; index1 < patterns.size(); index1++) {
                     if (index1 != index) {
                        elements[index1 < index ? index1 : index1 - 1] = (StringPattern)patterns.getAt(index1);
                     }
                  }

                  this._patterns = new StringPatternContainer(elements);
                  this._enumerator = new StringPatternEnumerator(null, this._patterns);
               }
               break;
            }
         }
      }

      if (this._enumerator == null) {
         this._patterns = StringPatternRepository$Internal.getStringPatterns();
         this._enumerator = new StringPatternEnumerator(null, this._patterns);
      }
   }

   @Override
   protected void makeContextMenu(ContextMenu contextMenu, int instance) {
      super.makeContextMenu(contextMenu, instance);
      if (instance != 65537) {
         Object cookie = this.getCookieWithFocus();
         MenuItem defaultItem = ActiveRegionSupport.addCookieMenuItems(this, cookie, contextMenu, null);
         if (defaultItem != null && this._invertCookieRegion) {
            contextMenu.setDefaultItem(defaultItem);
         }

         if (cookie == null) {
            AbstractString text = this.getTextAbstractString(true);
            int curIndex = this.getCursorPosition();
            ActiveRegionSupport.addCookieMenuItems(this, text, 0, curIndex, text.length(), this._patterns, contextMenu, null, instance);
         }
      }
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
   }

   @Override
   protected void paint(Graphics graphics) {
      super.paint(graphics);
   }

   @Override
   protected boolean keyDown(int keycode, int time) {
      if (Keypad.key(keycode) == 21) {
         Object cookie = this.getCookieWithFocus();
         ActiveFieldCookie afc = null;
         if (cookie instanceof Object[]) {
            Object[] cookies = (Object[])cookie;

            for (int i = 0; i < cookies.length; i++) {
               Object var10000 = cookies[i];
               if (cookies[i] instanceof ActiveFieldCookie) {
                  afc = (ActiveFieldCookie)var10000;
                  break;
               }
            }
         } else if (cookie instanceof ActiveFieldCookie) {
            afc = (ActiveFieldCookie)cookie;
         }

         if (afc != null && afc.invokeApplicationKeyVerb()) {
            return true;
         }
      }

      return super.keyDown(keycode, time);
   }

   @Override
   protected void onFocus(int direction) {
      super.onFocus(direction);
      this._invertCookieRegion = true;
      this._snapToCookie = true;
   }

   @Override
   protected void notifyTextChanged(FormatParams aParams, boolean aIsInsertionOrDeletion) {
      super.notifyTextChanged(aParams, aIsInsertionOrDeletion);
      if (this._scanForSmileys && !this._inPostResults && this.getComposedTextStart() == this.getComposedTextEnd() && aIsInsertionOrDeletion) {
         this._smileySupport.scanForSmileys(aParams);
      }

      this._arSupport.init();
      if (aIsInsertionOrDeletion && !this._inPostResults) {
         this.scanForActiveRegions();
      }
   }

   public ActiveAutoTextEditField(String label, String initialValue) {
      this(label, initialValue, 1000000);
   }

   @Override
   protected boolean keyChar(char key, int status, int time) {
      this._scanForSmileys = key == ' ';
      boolean ret = super.keyChar(key, status, time);
      this._scanForSmileys = true;
      return ret;
   }

   private synchronized int initialize() {
      if (this._waitingThread != null) {
         return -1;
      }

      for (; this._activeThread != null; this._waitingThread = null) {
         this._waitingThread = Thread.currentThread();

         try {
            this.wait();
         } catch (InterruptedException e) {
            this._waitingThread = null;
            return -1;
         }
      }

      AbstractString text = this.getTextAbstractString(true);
      this._enumerator.reset(text, 0, text.length());
      this._pendingRegionCount = 0;
      this._activeThread = Thread.currentThread();
      return this._pendingVersion;
   }

   private boolean scanForActiveRegions(int version) {
      boolean changed = false;
      int labelLength = this.getLabelLength();
      this._threadSafeIterator.set(0, this._threadSafeIterator.text().length());
      boolean hasActiveRegion = this.nextActiveRegion(this._threadSafeIterator);
      int regionIndex = 0;

      while (this._enumerator.hasMoreMatches() && version == this._pendingVersion) {
         if (this._pendingRegionCount * 2 == this._pendingOffsets.length) {
            Array.resize(this._pendingOffsets, (this._pendingRegionCount + 8) * 2);
            Array.resize(this._pendingCookieID, this._pendingRegionCount + 8);
         }

         this._enumerator.nextMatch(this._match);
         int beginIndex = labelLength + this._match.beginIndex;
         int endIndex = labelLength + this._match.endIndex;
         int composedStart = this.getComposedTextStart();
         int composedEnd = this.getComposedTextEnd();
         if (composedEnd != composedStart && beginIndex < composedEnd && endIndex > composedStart) {
            this._sectionLen[0] = Math.max(composedStart - beginIndex, 0);
            this._sectionLen[1] = composedEnd - composedStart;
            this._sectionLen[2] = Math.max(endIndex - composedEnd, 0);
         } else {
            this._sectionLen[0] = endIndex - beginIndex;
            this._sectionLen[1] = this._sectionLen[2] = 0;
         }

         endIndex = beginIndex;

         for (int i = 0; i < 3; i++) {
            beginIndex = endIndex;
            endIndex = beginIndex + this._sectionLen[i];
            if (this._sectionLen[i] != 0 && i != 1) {
               if (!changed) {
                  if (!hasActiveRegion) {
                     changed = true;
                  } else {
                     if (this._cookieID == null) {
                        changed = true;
                     } else {
                        long[] cookieIDs = (long[])this._cookieID.get((int)((this._threadSafeIterator.runXAttrib() & 65504) >> 5) - 1);
                        if (cookieIDs == null) {
                           changed = true;
                        } else {
                           boolean alreadyExists = false;

                           for (int j = 0; j < cookieIDs.length; j++) {
                              if (cookieIDs[j] == this._match.id) {
                                 alreadyExists = true;
                                 break;
                              }
                           }

                           if (!alreadyExists
                              || this._threadSafeIterator.pos() != beginIndex
                              || this._threadSafeIterator.pos() + this._threadSafeIterator.runLength() != endIndex) {
                              changed = true;
                           }

                           if (regionIndex + 1 != cookieIDs.length && !changed) {
                              regionIndex++;
                              break;
                           }
                        }
                     }

                     regionIndex = 0;
                     hasActiveRegion = this._threadSafeIterator.next() && this.nextActiveRegion(this._threadSafeIterator);
                  }
               }

               this._pendingOffsets[this._pendingRegionCount * 2] = beginIndex;
               this._pendingOffsets[this._pendingRegionCount * 2 + 1] = endIndex;
               this._pendingCookieID[this._pendingRegionCount] = this._match.id;
               this._pendingRegionCount++;
            }
         }
      }

      if (!changed && hasActiveRegion) {
         changed = true;
      }

      return changed;
   }

   private boolean nextActiveRegion(AttributedString$Iterator iterator) {
      while ((iterator.runXAttrib() & 65504) == 0) {
         if (!iterator.next()) {
            return false;
         }
      }

      return true;
   }

   private boolean prevActiveRegion(AttributedString$Iterator iterator) {
      while ((iterator.runXAttrib() & 65504) == 0) {
         if (!iterator.prev()) {
            return false;
         }
      }

      return true;
   }

   private void postResults(int version) {
      if (version == this._pendingVersion) {
         this._inPostResults = true;
         synchronized (this) {
            this.startLayoutUpdate();
            AttributedString$Iterator iterator = new AttributedString(super._text).getIterator();
            iterator.set(0, iterator.text().length());

            for (boolean hasActiveRegion = this.nextActiveRegion(iterator);
               hasActiveRegion;
               hasActiveRegion = iterator.next() && this.nextActiveRegion(iterator)
            ) {
               super._text.setAttrib(iterator.pos(), iterator.pos() + iterator.runLength(), 0, 786432);
            }

            super._text.setAttrib(0, super._text.length(), 0, 0, 0, 65504);
            if (this._cookieID == null) {
               this._cookieID = new IntHashtable(this._pendingCookieID.length);
            } else {
               this._cookieID.clear();
            }

            int lastStart = -1;
            int lastEnd = -1;
            int lastIndex = -1;

            for (int i = 0; i < this._pendingRegionCount; i++) {
               int currStart = this._pendingOffsets[2 * i];
               int currEnd = this._pendingOffsets[2 * i + 1];
               if (lastStart == currStart && lastEnd == currEnd) {
                  long[] cookieIDs = (long[])this._cookieID.get(lastIndex);
                  if (cookieIDs != null) {
                     Arrays.add(cookieIDs, this._pendingCookieID[i]);
                  }
               } else {
                  super._text.setAttrib(this._pendingOffsets[2 * i], this._pendingOffsets[2 * i + 1], 786432, 786432, i + 1 << 5, 65504);
                  this._cookieID.put(i, new long[]{this._pendingCookieID[i]});
                  lastStart = currStart;
                  lastEnd = currEnd;
                  lastIndex = i;
               }
            }

            this._regionCount = this._cookieID.size();
            this.endLayoutUpdate();
         }

         this._inPostResults = false;
      }
   }

   @Override
   protected boolean keyControl(char key, int status, int time) {
      if (key == 128 && this.isSymbolScreenAllowed() && !this.getScreen().isGlobal()) {
         this._smileySupport.showSymbolScreen();
         return true;
      } else {
         return super.keyControl(key, status, time);
      }
   }

   @Override
   public void actionPerformed(int action, Object parameter) {
      if ((action & 0xFF) > 0) {
         switch (action & 0xFF) {
            case 113:
               if (this.isSymbolScreenAllowed() && !this.getScreen().isGlobal()) {
                  this._smileySupport.showSymbolScreen();
               }

               return;
         }
      }

      super.actionPerformed(action, parameter);
   }

   @Override
   protected boolean isSymbolScreenAllowed() {
      return this._smileySupport.getSmileyFacility() != null ? this.isEditable() : super.isSymbolScreenAllowed();
   }

   @Override
   public String getText() {
      return this._smileySupport != null && this._smileySupport.isSmileyAvailable()
         ? this._smileySupport.getDecodedString(this.getLabelLength(), super._text.length())
         : super.getText();
   }

   private AbstractString getTextAbstractString(boolean internal) {
      return internal ? super.getTextAbstractString() : this.getTextAbstractString();
   }

   @Override
   public AbstractString getTextAbstractString() {
      return !this._smileySupport.isSmileyAvailable()
         ? super.getTextAbstractString()
         : this._smileySupport.getDecodedTextAbstractString(this.getLabelLength(), super._text.length());
   }

   @Override
   public String getText(int offset, int length) {
      return !this._smileySupport.isSmileyAvailable() ? super.getText(offset, length) : this._smileySupport.getDecodedString(offset, offset + length);
   }

   @Override
   public int getDecodedTextLength(int start, int end) {
      return this._smileySupport != null && this._smileySupport.isSmileyAvailable() ? this._smileySupport.getDecodedStringLength(start, end) : end - start;
   }

   @Override
   public boolean paste(Clipboard cb) {
      return super.paste(cb);
   }

   @Override
   public int insert(String text, int context, boolean stripInvalid, boolean validateText) {
      return super.insert(text, context, stripInvalid, validateText);
   }

   @Override
   public synchronized void setText(String text) {
      super.setText(text);
   }

   public ActiveAutoTextEditField(String label, String initialValue, int maxNumChars) {
      this(label, initialValue, maxNumChars, 4503599627370496L);
   }

   public ActiveAutoTextEditField(String label, String initialValue, int maxNumChars, long style) {
      this(label, initialValue, maxNumChars, style, null);
   }

   @Override
   protected void fieldChangeNotify(int context) {
      super.fieldChangeNotify(context);
      if (this.isDirty()) {
         this._invertCookieRegion = false;
         this._snapToCookie = false;
      }
   }
}
