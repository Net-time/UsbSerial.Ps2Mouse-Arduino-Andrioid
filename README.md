Ps/2Mouse<->Arduino<->Android [![Build Status](https://travis-ci.org/felHR85/UsbSerial.svg?branch=master)](https://travis-ci.org/felHR85/UsbSerial) [![](https://jitpack.io/v/felHR85/UsbSerial.svg)](https://jitpack.io/#felHR85/UsbSerial) [![AndroidArsenal](https://img.shields.io/badge/Android%20Arsenal-UsbSerial-green.svg?style=true)](https://android-arsenal.com/details/1/4162) [![Join the chat at https://gitter.im/UsbSerial/Lobby](https://badges.gitter.im/UsbSerial/Lobby.svg)](https://gitter.im/UsbSerial/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge) 
=========


UsbSerial Android application exampel fork only, library files not to be updated.  
Purpose of repositary is to get a range of input options to an Android application.  
Why? , Android devices can only communicate wit USB-devices that exists in the kernals drivers.<br/>
![PoC](https://github.com/user-attachments/assets/78d562d1-ef63-478e-9894-040c76ab1597)
However Android devices do accept some USB to serial adaptors, I use a AzDelivery Arduino UNO as adapter.<br/>
Whith this I get full access to the Arduino GPIO and the mechanical PS/2 mouse connected to it.<br/>
Why a PS/2 mouse? It has 3 directional sensors that can easily be connected to some rotating mechanical thing.<br/>
Also it has 3 buttons that you can do stuff with.<br/>

Modifications to the original example file so far:<br/>
Manifest, added some items to make it remember USB_PERMISSION.<br/>
	forced single instance as Android otherwise starts a new instance when the Arduino is plugged in.<br/>
MainActivity: moved initial focus to the Send-button so that the onscreen keyboard does not hide the Toast-messages.<br/>

Current status:<br/>
**Android application recives from PS/2:<br/>**
left-right<br/>
up-down<br/>
scrolldown-scrollup<br/>
btnLeft, !btnLeft<br/>
btnRight, !btnRight<br/>
btnMiddle, !btnMiddle<br/>

**Arduino accepts commands:<br/>**
13on, 13off (Onboard led on GPIO 13)<br/>

Hardware instructions and original Arduino source at: 
https://www.instructables.com/Hack-a-Mouse-With-Arduino-2500-Ft-Wireless-PS2/

For information on the library visit the master.
Text below is cloned from the master and will be edited/deleted


Support UsbSerial
--------------------------------------
[If UsbSerial helped you with your projects please consider donating a little sum](https://www.paypal.me/felhr)\
[Or consider buying DroidTerm Pro: A Usb serial port terminal using UsbSerial](https://play.google.com/store/apps/details?id=com.felhr.droidtermpro)

Devices Supported
--------------------------------------
[CP210X devices](http://www.silabs.com/products/mcu/pages/usbtouartbridgevcpdrivers.aspx) Default: 9600,8,1,None,flow off

[CDC devices](https://en.wikipedia.org/wiki/USB_communications_device_class) Default 115200,8,1,None,flow off

[FTDI devices](http://www.ftdichip.com/FTProducts.htm) Default: 9600,8,1,None,flow off

[PL2303 devices](http://www.prolific.com.tw/US/ShowProduct.aspx?p_id=225&pcid=41) Default 9600,8,1,None,flow off

[CH34x devices](https://www.olimex.com/Products/Breadboarding/BB-CH340T/resources/CH340DS1.PDF) Default 9600,8,1,None,flow off

[CP2130 SPI-USB](http://www.silabs.com/products/interface/usb-bridges/classic-usb-bridges/Pages/usb-to-spi-bridge.aspx)

Known Issue
--------------------------------------
Due to a bug in Android itself, it's highly recommended to **not** use it with a device running [Android 5.1.1 Lollipop](https://en.wikipedia.org/wiki/Android_version_history#Android_5.1_Lollipop_(API_22)). See issue [#142](https://github.com/felHR85/UsbSerial/issues/142) for more details.












