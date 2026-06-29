package net.rim.device.internal.EScreens;

import java.util.Random;
import java.util.Vector;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.RadioException;
import net.rim.device.api.system.RadioPacketHeader;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.component.SeparatorField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.text.IPTextFilter;
import net.rim.device.api.util.Arrays;
import net.rim.device.api.util.NumberUtilities;
import net.rim.device.cldc.io.dns.DNSListener;
import net.rim.device.cldc.io.dns.DNSResolverIPv4;
import net.rim.device.internal.system.ICMPPacketHeader;
import net.rim.device.internal.system.RadioInternal;
import net.rim.device.internal.ui.component.IPEditField;

public class EScreenSession extends MainScreen implements Runnable, DNSListener, ListFieldCallback {
   private int _curState;
   private EScreenSessionManager _manager;
   private int _sessionIndex;
   private int _timerId;
   private int _timeoutCounter;
   private String[] _counterHeaders;
   private int[] _counters;
   protected String[] _results;
   protected int _numCounters;
   protected int _numResults;
   protected byte[] _ipAddress;
   protected int _apnId;
   private int _dnsId;
   protected IPEditField _ipField;
   protected EditField _apnField;
   protected ListField _listField;
   private Vector _fields;
   public static final int STATE_IDLE = 0;
   public static final int STATE_ACTIVE = 1;
   public static final int STATE_DONE = 2;
   public static final int STATE_OPEN = 3;
   private static final String[] STATE_STRS = new String[]{"Idle", "Active", "Done", "Open", "Idle"};
   public static final int IM_STATE = 0;
   public static final int TIMEOUT_NOW = -1;
   protected static final int MENU_REFRESH = 1;
   protected static final int MENU_START = 3;
   protected static final int MENU_STOP = 4;
   protected static Random _rand = new Random();
   protected static final int UDP_HEADER = 0;
   protected static final int ICMP_HEADER = 1;

   public void init(EScreenSessionManager manager, int si) {
      this._manager = manager;
      this._sessionIndex = si;
      this._timerId = -1;
      this._fields = new Vector(5);
      this._ipField = new IPEditField("IP: ", null);
      this._ipField.setFilter(new IPTextFilter(1));
      this._apnField = new EditField("APN: ", null);
      this._fields.addElement(this._ipField);
      this._fields.addElement(this._apnField);
      this.addParameterFields(this._fields);
      int size = this._fields.size();

      for (int i = 0; i < size; i++) {
         this.add((Field)this._fields.elementAt(i));
      }

      this.add(new SeparatorField());
      this._listField = new ListField();
      this._listField.setCallback(this);
      this.add(this._listField);
      this.setTitle(this.getSessionType() + " Session");
   }

   protected void onUnDisplay() {
      for (int i = this._fields.size() - 1; i >= 0; i--) {
         if (((Field)this._fields.elementAt(i)).isDirty()) {
            return;
         }
      }

      this._manager.removeSession(this._sessionIndex);
   }

   public void stop() {
      this.stopTimeoutCounter();
      this.setState(2);
   }

   public void start() {
      throw null;
   }

   public String getSessionType() {
      throw null;
   }

   public void addParameterFields(Vector _1) {
      throw null;
   }

   public void handleTimeout() {
      throw null;
   }

   protected void setState(int state) {
      this._curState = state;
      this.invalidateLine(0);
   }

   protected void setCounterHeaders(String[] headers) {
      this._numCounters = headers != null ? headers.length : 0;
      this._counterHeaders = headers;
      this._counters = new int[this._numCounters];
      this.setNumLines();
   }

   protected void setNumResults(int numResults) {
      this._numResults = numResults;
      this._results = new String[numResults];
      this.setNumLines();
   }

   protected byte[] getIpAddress() {
      return this._ipAddress;
   }

   protected String getIpField() {
      return this._ipField.getText();
   }

   protected int getApnId() {
      return this._apnId;
   }

   public void appendSummary(StringBuffer strBuf) {
      strBuf.append(this.getSessionType());
      char ch;
      switch (this._curState) {
         case -1:
            ch = '?';
            break;
         case 0:
         default:
            ch = 'i';
            break;
         case 1:
            ch = 'a';
            break;
         case 2:
            ch = 'd';
            break;
         case 3:
            ch = 'o';
      }

      strBuf.append(' ');
      strBuf.append(ch);
      strBuf.append(' ');
      strBuf.append(this._ipField.getText());
   }

   protected synchronized void stopTimeoutCounter() {
      if (this._timerId != -1) {
         UiApplication.getUiApplication().cancelInvokeLater(this._timerId);
         this._timerId = -1;
      }
   }

   protected synchronized void setTimeoutCounter(int secs) {
      if (this._timerId == -1) {
         this._timerId = UiApplication.getUiApplication().invokeLater(this, 1000, true);
         if (this._timerId == -1) {
            Dialog.alert("Unable to start timer.");
            this.stop();
         }
      }

      this._timeoutCounter = secs;
   }

   protected boolean isFromDestination(byte[] ipAddress) {
      return Arrays.equals(ipAddress, this._ipAddress);
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   protected boolean verifyParameters() {
      this._apnId = 0;
      String userApnFieldEntry = this._apnField.getText();
      if (userApnFieldEntry != null && userApnFieldEntry.length() != 0) {
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            this._apnId = getApnIdFromApn(userApnFieldEntry);
            var8 = false;
         } finally {
            if (var8) {
               Dialog.alert("Invalid APN");
               return false;
            }
         }
      }

      String ip = this._ipField.getText();
      if (ip != null && ip.length() != 0) {
         try {
            this._ipAddress = intToByteArray(IPEditField.parseIpAddr(ip));
            return true;
         } finally {
            this._dnsId = DNSResolverIPv4.instance().getAddressByHostname(ip, this, this._apnId);
            return false;
         }
      } else {
         Dialog.alert("Invalid IP Address");
         return false;
      }
   }

   protected RadioPacketHeader makeHeader(int type) throws EScreenSession$InvalidInputException {
      if (type == 0) {
         UDPPacketHeader udpHeader = new UDPPacketHeader();
         udpHeader.setDestinationAddress(this._ipAddress);
         udpHeader.setAccessPointNumber(this._apnId);
         return udpHeader;
      } else if (type == 1) {
         ICMPPacketHeader icmpHeader = new ICMPPacketHeader();
         icmpHeader.setDestinationAddress(this._ipAddress);
         icmpHeader.setAccessPointNumber(this._apnId);
         return icmpHeader;
      } else {
         throw new EScreenSession$InvalidInputException();
      }
   }

   protected void incrementCounter(int counter) {
      this._counters[counter]++;
      this.invalidateLine(counter + 1);
   }

   protected void setCounter(int counter, int value) {
      this._counters[counter] = value;
      this.invalidateLine(counter + 1);
   }

   protected int getCounter(int counter) {
      return this._counters[counter];
   }

   protected void setResult(int resultNum, String str, int counter) {
      int index = resultNum % this._numResults;
      StringBuffer strBuf = new StringBuffer(32);
      strBuf.append('#');
      strBuf.append(resultNum + 1);
      strBuf.append(':');
      strBuf.append(' ');
      strBuf.append(str);
      this._results[index] = strBuf.toString();
      this.invalidateLine(2 + index + this._numCounters);
      if (counter >= 0) {
         this.incrementCounter(counter);
      }
   }

   protected void invalidateLine(int index) {
      synchronized (Application.getEventLock()) {
         this._listField.invalidate(index);
      }
   }

   protected void resetStats() {
      this._curState = 0;
      if (this._counters != null) {
         for (int i = this._numCounters - 1; i >= 0; i--) {
            this._counters[i] = 0;
         }
      }

      this._results = null;
      this._numResults = 0;
      this.setNumLines();
   }

   protected void setNumLines() {
      this._listField.setSize(2 + this._numCounters + this._numResults);
   }

   @Override
   public int indexOfList(ListField listField, String prefix, int start) {
      return listField.getSelectedIndex();
   }

   @Override
   public Object get(ListField listField, int index) {
      return "";
   }

   @Override
   public void DNSEvent(int id, int type, Vector results) {
      if (type == 1 && this._dnsId == id) {
         this._ipAddress = (byte[])results.elementAt(0);
         this.start();
         this.setState(1);
      } else {
         Dialog.alert("Invalid ip or hostname");
      }
   }

   @Override
   public int getPreferredWidth(ListField listField) {
      return this.getWidth();
   }

   @Override
   public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
      StringBuffer strBuf = new StringBuffer(32);
      if (index == 0) {
         strBuf.append("Status: ");
         strBuf.append(STATE_STRS[this._curState]);
      } else if (index <= this._numCounters) {
         strBuf.append(this._counterHeaders[--index]);
         NumberUtilities.appendNumber(strBuf, this._counters[index]);
      } else if (index == this._numCounters + 1) {
         strBuf.append("Results follow for: ");
         IPEditField.appendIpAddr(strBuf, this._ipAddress);
      } else {
         String str = this._results[index - this._numCounters - 2];
         if (str != null) {
            strBuf.append(str);
         }
      }

      graphics.drawText(strBuf, 0, strBuf.length(), 0, y, 0, width);
   }

   @Override
   public synchronized void run() {
      if (this._curState == 1) {
         if (this._timeoutCounter == -1 || --this._timeoutCounter <= 0) {
            this.handleTimeout();
         }
      }
   }

   private static final int getApnIdFromApn(String apn) throws RadioException {
      if (apn == null) {
         throw new RadioException("Null APN");
      }

      if (apn.length() == 1) {
         char ch = apn.charAt(0);
         if (ch >= '0' && ch <= '6') {
            return (ch & 0xFF) - 48;
         }
      }

      return RadioInternal.registerAccessPointNumber(apn);
   }

   @Override
   protected void makeMenu(Menu menu, int instance) {
      super.makeMenu(menu, instance);
      menu.add(new EScreenSession$MyMenuItem(this, 1));
      menu.add(new EScreenSession$MyMenuItem(this, this._curState == 1 ? 4 : 3));
   }

   @Override
   public int getState() {
      return this._curState;
   }

   @Override
   protected boolean onSavePrompt() {
      return true;
   }

   public static final byte[] intToByteArray(int v) {
      byte[] a = new byte[4];

      for (int i = 0; i < 4; i++) {
         a[i] = (byte)(v >> 24 - 8 * i & 0xFF);
      }

      return a;
   }

   private String getMenuString(int id) {
      switch (id) {
         case 0:
         case 2:
            return "";
         case 1:
         default:
            return "Refresh";
         case 3:
            return "Start";
         case 4:
            return "Stop";
      }
   }

   private int getMenuPriority(int id) {
      switch (id) {
         case 0:
         case 2:
            return 0;
         case 1:
         default:
            return 1;
         case 3:
            return 0;
         case 4:
            return 0;
      }
   }
}
