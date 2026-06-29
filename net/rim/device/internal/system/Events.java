package net.rim.device.internal.system;

import net.rim.device.api.system.AlertListener;
import net.rim.device.api.system.AlertListener2;
import net.rim.device.api.system.CBPacketHeader;
import net.rim.device.api.system.CBPacketListener;
import net.rim.device.api.system.DirectConnectListener;
import net.rim.device.api.system.GANStatusListener;
import net.rim.device.api.system.HolsterListener;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.ModemListener;
import net.rim.device.api.system.PhoneCallListener;
import net.rim.device.api.system.PhoneControlListener;
import net.rim.device.api.system.PhoneTimerListener;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.RadioListener;
import net.rim.device.api.system.RadioPacketListener;
import net.rim.device.api.system.RealtimeClockListener;
import net.rim.device.api.system.SMSPacketHeader;
import net.rim.device.api.system.SMSPacketListener;
import net.rim.device.api.system.StylusListener;
import net.rim.device.api.system.SystemListener;
import net.rim.device.api.system.SystemListener2;
import net.rim.device.api.system.SystemListener3;
import net.rim.device.api.system.TrackwheelListener;
import net.rim.device.api.system.UDPPacketHeader;
import net.rim.device.api.system.UDPPacketListener;
import net.rim.device.api.system.USBPortListener;
import net.rim.device.api.system.USBPortListener2;
import net.rim.device.api.system.WLANExtendedListener;
import net.rim.device.api.system.WLANListenerInternal;
import net.rim.device.api.system.WLANScanListener;
import net.rim.device.internal.crypto.vpn.VPNListener;
import net.rim.device.internal.io.NativeSocketListener;
import net.rim.vm.Message;

public final class Events {
   public static final void dispatchSystemEvent(int event, int subMessage, int dataLength, Object object0, SystemListener listener) {
      switch (event) {
         case 258:
            listener.batteryLow();
            return;
         case 259:
            listener.powerOff();
            return;
         case 260:
            listener.powerUp();
            return;
         case 261:
            if (listener instanceof SystemListener2) {
               SystemListener2 sl2 = (SystemListener2)listener;
               sl2.powerOffRequested(1);
               return;
            }
            break;
         case 263:
            listener.batteryGood();
            return;
         case 265:
            listener.batteryStatusChange(subMessage);
            return;
         case 289:
         case 290:
            if (listener instanceof SystemListener2) {
               SystemListener2 sl2 = (SystemListener2)listener;
               sl2.cradleMismatch(event == 289);
               return;
            }
            break;
         case 307:
            if (listener instanceof SystemListener3) {
               SystemListener3 sl3 = (SystemListener3)listener;
               sl3.batteryDoorOpened();
               return;
            }
            break;
         case 308:
            if (listener instanceof SystemListener3) {
               SystemListener3 sl3 = (SystemListener3)listener;
               sl3.batteryDoorClosed();
               return;
            }
            break;
         case 320:
            if (listener instanceof SystemListener2) {
               SystemListener2 sl2 = (SystemListener2)listener;
               sl2.backlightStateChange(dataLength != 0);
               return;
            }
            break;
         case 511:
            if (listener instanceof SystemListener2) {
               SystemListener2 sl2 = (SystemListener2)listener;
               sl2.fastReset();
               return;
            }
            break;
         case 3587:
            if (listener instanceof SystemListener3) {
               SystemListener3 sl3 = (SystemListener3)listener;
               sl3.usbMSMediumChanged(subMessage);
            }
            break;
         case 3588:
            if (listener instanceof SystemListener2) {
               SystemListener2 sl2 = (SystemListener2)listener;
               sl2.usbConnectionStateChange(subMessage);
               return;
            }
      }
   }

   public static final boolean dispatchKeyEvent(int event, char key, int keycode, int time, KeyListener listener) {
      switch (event) {
         case 513:
            return listener.keyDown(keycode, time);
         case 514:
            return listener.keyRepeat(keycode, time);
         case 515:
            return listener.keyUp(keycode, time);
         case 520:
            return listener.keyStatus(keycode, time);
         case 32768:
            return listener.keyChar(key, keycode & 65535, time);
         default:
            return false;
      }
   }

   public static final boolean dispatchStylusEvent(int event, int x, int y, int status, int time, StylusListener listener) {
      switch (event) {
         case 6655:
            return false;
         case 6656:
         default:
            return listener.stylusDown(x, y, status, time);
         case 6657:
            return listener.stylusDrag(x, y, status, time);
         case 6658:
            return listener.stylusUp(x, y, status, time);
         case 6659:
            return listener.stylusTap(x, y, status, time);
         case 6660:
            return listener.stylusTapHold(x, y, status, time);
         case 6661:
            return listener.stylusDoubleTap(x, y, status, time);
      }
   }

   public static final boolean dispatchTrackwheelEvent(int event, int magnitude, int status, int time, TrackwheelListener listener) {
      switch (event) {
         case 515:
         case 518:
            return false;
         case 516:
         default:
            return listener.trackwheelClick(status, time);
         case 517:
            return listener.trackwheelUnclick(status, time);
         case 519:
            return listener.trackwheelRoll(magnitude, status, time);
      }
   }

   public static final void dispatchRealtimeClockEvent(int event, RealtimeClockListener listener) {
      switch (event) {
         case 769:
            listener.clockUpdated();
            return;
         case 773:
            if (listener instanceof RealtimeClockListener2) {
               RealtimeClockListener2 rtl2 = (RealtimeClockListener2)listener;
               rtl2.networkTimeUpdated();
            }
      }
   }

   public static final void dispatchUSBPortEvent(int event, int subMessage, int dataLength, USBPortListener usbListener) {
      int channel = usbListener.getChannel();
      if (channel == -1 || subMessage == channel) {
         switch (event) {
            case 3585:
               usbListener.connectionRequested();
               return;
            case 3586:
               usbListener.connected();
               return;
            case 3592:
               usbListener.disconnected();
               if (usbListener instanceof USBPortListener2) {
                  ((USBPortListener2)usbListener).disconnected(subMessage);
                  return;
               }
               break;
            case 3600:
               usbListener.dataSent();
               return;
            case 3616:
               usbListener.dataNotSent();
               return;
            case 3648:
               usbListener.dataReceived(dataLength);
               return;
            case 3712:
               if (usbListener instanceof USBPortListener2) {
                  ((USBPortListener2)usbListener).connectionAuthenticationRequired(subMessage);
               }
         }
      }
   }

   private static final RadioPacketListener getListener(RadioListener listener, int data1) {
      switch (data1) {
         case 1:
            if (listener instanceof UDPPacketListener) {
               return (UDPPacketListener)listener;
            }
            break;
         case 2:
            if (listener instanceof SMSPacketListener) {
               return (SMSPacketListener)listener;
            }
            break;
         case 3:
            if (listener instanceof ICMPPacketListener) {
               return (ICMPPacketListener)listener;
            }
            break;
         case 4:
         case 32:
            if (listener instanceof TCPPacketListener) {
               return (TCPPacketListener)listener;
            }
      }

      return null;
   }

   public static final void dispatchRadioPacketEvent(int event, int subMessage, int data0, int data1, Object object0, Object object1, RadioListener listener) {
      switch (event) {
         case 1537:
            byte[] data = (byte[])object1;
            switch (data1) {
               case 1:
                  if (object0 instanceof UDPPacketHeader) {
                     UDPPacketHeader uph = (UDPPacketHeader)object0;
                     if (listener instanceof UDPPacketListener) {
                        UDPPacketListener upl = (UDPPacketListener)listener;
                        upl.packetReceived(uph, data);
                        return;
                     }
                  }

                  return;
               case 2:
                  if (object0 instanceof SMSPacketHeader) {
                     SMSPacketHeader smh = (SMSPacketHeader)object0;
                     if (listener instanceof SMSPacketListener) {
                        SMSPacketListener spl = (SMSPacketListener)listener;
                        spl.packetReceived(smh, data);
                        return;
                     }
                  }

                  return;
               case 3:
                  if (object0 instanceof ICMPPacketHeader) {
                     ICMPPacketHeader icmph = (ICMPPacketHeader)object0;
                     if (listener instanceof ICMPPacketListener) {
                        ICMPPacketListener ipl = (ICMPPacketListener)listener;
                        ipl.packetReceived(icmph, data);
                        return;
                     }
                  }

                  return;
               case 4:
               case 32:
                  if (object0 instanceof TCPPacketHeader) {
                     TCPPacketHeader tcph = (TCPPacketHeader)object0;
                     if (listener instanceof TCPPacketListener) {
                        TCPPacketListener tpl = (TCPPacketListener)listener;
                        tpl.packetReceived(tcph, data);
                        return;
                     }
                  }

                  return;
               case 8:
                  if (object0 instanceof CBPacketHeader) {
                     CBPacketHeader cbh = (CBPacketHeader)object0;
                     if (listener instanceof CBPacketListener) {
                        CBPacketListener cbpl = (CBPacketListener)listener;
                        cbpl.packetReceived(cbh, data);
                        return;
                     }
                  }

                  return;
               default:
                  return;
            }
         case 1538:
            RadioPacketListener rplx = getListener(listener, data1);
            if (rplx != null) {
               rplx.packetSent(subMessage, data0);
               return;
            }
            break;
         case 1539:
            RadioPacketListener var10 = getListener(listener, data1);
            if (var10 != null) {
               var10.packetNotSent(subMessage, data0);
               return;
            }
            break;
         case 1544:
            RadioPacketListener rpl = getListener(listener, data1);
            if (rpl != null) {
               rpl.packetStatus(subMessage, data0);
               return;
            }
            break;
         case 1546:
            if (listener instanceof SMSPacketListener) {
               SMSPacketListener spl = (SMSPacketListener)listener;
               spl.packetDelivered(subMessage, data0, data1);
            }
      }
   }

   public static final boolean dispatchPhoneCallEvent(int event, int subMessage, int data0, PhoneCallListener listener) {
      switch (event) {
         case 1553:
            listener.callIncoming(subMessage);
            return true;
         case 1554:
            listener.callWaiting(subMessage);
            return true;
         case 1555:
            listener.callConnected(subMessage);
            return true;
         case 1556:
            listener.callFailed(subMessage, data0);
            return true;
         case 1557:
            listener.callDisconnected(subMessage);
            return true;
         case 1558:
            listener.callHeld(subMessage);
            return true;
         case 1559:
            listener.callResumed(subMessage);
            return true;
         case 1560:
            listener.callAdded(subMessage);
            return true;
         case 1561:
            listener.callRemoved(subMessage);
            return true;
         case 1562:
            listener.callManipulateFailed(subMessage, data0);
            return true;
         case 1563:
            listener.callDelivered(subMessage);
            return true;
         case 1564:
            listener.callInitiated(subMessage);
            return true;
         case 1574:
            listener.callDisplayUpdated(subMessage);
            return true;
         case 1600:
            listener.callOTAStatusUpdated(subMessage, data0);
            return true;
         case 1605:
            listener.callVoicePrivacyUpdated(subMessage, data0 == 0);
            return true;
         case 1668:
            listener.callTransferred(subMessage, data0);
            return true;
         case 5001:
            listener.callTransferStateUpdated(subMessage, data0);
            return true;
         case 5003:
            listener.dtmfData(subMessage);
            return true;
         default:
            return false;
      }
   }

   public static final boolean dispatchPhoneTimerEvent(int event, int subMessage, int data0, PhoneTimerListener listener) {
      switch (event) {
         case 1568:
            listener.callTimerUpdated(subMessage, data0);
            return true;
         default:
            return false;
      }
   }

   public static final boolean dispatchPhoneControlEvent(int event, int subMessage, int data0, int data1, Object object0, PhoneControlListener listener) {
      switch (event) {
         case 1569:
            listener.ssRequestFailed(subMessage, data1 & 0xFF, (data1 & 256) == 256);
            return true;
         case 1570:
            listener.ssRequestSucceeded(subMessage, data0 & 65535, data0 >> 16, data1 & 0xFF, (data1 & 256) == 256, (data1 & 65536) == 65536);
            return true;
         case 1571:
            listener.ssRequestRejected((data1 & 256) == 256);
            return true;
         case 1572:
            listener.ssRequestReleased((data1 & 256) == 256);
            return true;
         case 1573:
            listener.ssRequestInvalidPassword();
            return true;
         case 1589:
            listener.featureReady();
            return true;
         case 1606:
            listener.ssUpdated(data0, data1);
            return true;
         case 1607:
         case 1608:
            listener.ssUssDisplay((byte[])object0, data0, event == 1608);
            return true;
         case 1609:
            listener.ssNotification(subMessage);
            return true;
         case 1610:
            listener.ssPasswordRequested(subMessage);
            return true;
         case 1665:
         case 1666:
         case 1667:
            listener.responseEnableFDN(event);
            return true;
         case 1669:
            listener.voiceLineChanged(subMessage);
            return true;
         case 5000:
            listener.alternateLinesUpdated();
            return true;
         case 5002:
            listener.voicemailCountUpdated(subMessage, data0);
            return true;
         default:
            return false;
      }
   }

   public static final void dispatchRadioEngineeringEvent(int event, int subMessage, int data0, Object object0, EngineeringDataListener listener) {
      switch (event) {
         case 1616:
            listener.engServiceProgramEvent(subMessage);
            return;
         case 1697:
            listener.engOTASPResponse((byte[])object0);
         default:
            return;
         case 2057:
            listener.engResponseMasterReset(data0);
            return;
         case 61441:
         case 61697:
            listener.engDataChanged();
            return;
         case 61442:
         case 61698:
            listener.engDataInitialized();
            return;
         case 61456:
         case 61712:
            listener.engDataLogworthy(subMessage);
      }
   }

   public static final void dispatchRadioModemEvent(int event, int subMessage, ModemListener listener) {
      switch (event) {
         case 1593:
         case 1594:
            listener.networkChangeResult(event, subMessage);
         default:
            return;
         case 1680:
            listener.networkSelectionModeChanged(subMessage);
            return;
         case 1681:
            listener.queryNetworkDisplayName(RadioInfo.convertNetworkId(subMessage));
      }
   }

   public static final void dispatchRadioDirectConnectEvent(int event, int subMessage, int data0, int data1, DirectConnectListener listener) {
      switch (event) {
         case 2048:
         default:
            listener.dcCallConnected(subMessage, data0, data1 == 1);
            return;
         case 2049:
            listener.dcCallDisconnected(subMessage, data0, data1);
            return;
         case 2050:
            listener.dcRequestFailed(subMessage, data0, data1);
            return;
         case 2051:
            listener.dcCallStatusUpdated(subMessage, data0);
            return;
         case 2052:
            listener.dcTalkStatusUpdated(subMessage, data0, data1);
            return;
         case 2053:
            listener.dcTalkGroupIdUpdated(subMessage, data0 != 1, data1);
            return;
         case 2054:
            listener.dcCallAlertUpdate(subMessage, data0, data1);
            return;
         case 2055:
            listener.dcServiceUpdated(subMessage, data0 == 0, data1 != 0);
         case 2047:
      }
   }

   public static final void dispatchWLANEvent(int event, int subMessage, int data0, int data1, WLANListenerInternal listener) {
      switch (event) {
         case 4608:
         case 4611:
         case 4612:
         case 4613:
         case 4614:
         case 4615:
         case 4616:
         case 4617:
            break;
         case 4609:
         default:
            listener.radioStatus(true);
            return;
         case 4610:
            listener.radioStatus(false);
            return;
         case 4618:
            listener.networkApChange();
            return;
         case 4619:
            switch (subMessage) {
               case 0:
                  return;
               case 1:
                  listener.networkFound(data0);
                  return;
               case 2:
               default:
                  if (listener instanceof WLANScanListener) {
                     ((WLANScanListener)listener).scanForNetworksComplete();
                     return;
                  }

                  return;
            }
         case 4620:
            listener.networkSuccess();
            return;
         case 4621:
            listener.networkFail(subMessage, data0, data1);
            return;
         case 4622:
            if (listener instanceof WLANExtendedListener) {
               ((WLANExtendedListener)listener).wlanChallengeOccurred(subMessage);
               return;
            }
            break;
         case 4623:
            if (listener instanceof WLANExtendedListener) {
               ((WLANExtendedListener)listener).wlanExtendedInfoChange();
               return;
            }
            break;
         case 4624:
            if (listener instanceof WLANExtendedListener) {
               ((WLANExtendedListener)listener).wlanRecordChangeOccurred(subMessage);
            }
      }
   }

   public static final void dispatchVPNEvent(int event, int subMessage, int data0, int data1, VPNListener listener) {
      switch (event) {
         case 8960:
         case 8961:
         case 8962:
         case 8966:
            listener.vpnStatusChanged(event, subMessage, data0, data1);
      }
   }

   public static final void dispatchGANEvent(int event, int subMessage, int data0, int data1, GANStatusListener listener) {
      switch (event) {
         case 1596:
            listener.ganEventOccurred(subMessage, data0, data1);
      }
   }

   public static final void dispatchHolsterEvent(int event, HolsterListener listener) {
      switch (event) {
         case 1792:
            listener.outOfHolster();
         case 1791:
            return;
         case 1793:
         default:
            listener.inHolster();
      }
   }

   public static final void dispatchAlertEvent(int event, int subMessage, AlertListener listener) {
      switch (event) {
         case 2576:
            listener.vibrateDone(subMessage);
            return;
         case 2577:
            listener.buzzerDone(subMessage);
            return;
         case 2578:
         default:
            listener.audioDone(subMessage);
            return;
         case 2580:
            if (listener instanceof AlertListener2) {
               AlertListener2 al2 = (AlertListener2)listener;
               al2.midiDone(subMessage);
            }
         case 2575:
         case 2579:
      }
   }

   public static final void dispatchNativeSocketEvent(Message message, NativeSocketListener listener) {
   }
}
