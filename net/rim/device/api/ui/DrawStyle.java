package net.rim.device.api.ui;

public interface DrawStyle {
   int HALIGN_MASK = 7;
   int HDEFAULT = 0;
   int LEADING = 2;
   int TRAILING = 1;
   int LEFT = 6;
   int RIGHT = 5;
   int HCENTER = 4;
   int HFULL = 7;
   int VALIGN_MASK = 56;
   int VDEFAULT = 0;
   int TOP = 48;
   int BOTTOM = 40;
   int VCENTER = 32;
   int VFULL = 56;
   int BASELINE = 8;
   int ELLIPSIS = 64;
   int TRUNCATE_BEGINNING = 128;
}
