# *PiDroid*

**PiDroid** A Simple Application that allows a Raspberry Pi (raspberian) to communicate with an Android device (4.0+). 
PiDroid will be used to take pictures or video and store it on a RaspberryPi and in turn stream Pictures and Video onto a Raspberry Pi.
This will be part of a Museum Piece where participants will record video and pictures on how "skin" has affected their worldview.


This project is an extension of Public Payphone [http://leimertphonecompany.net] which currently can record and play audio
and upload pictures to Twitter. The Pi currently listens to dialpad presses and uses these to trigger the above activities.
Part of the GPIO dialpad code is at [https://github.com/sabzo/piphone-cam/blob/master/tommyCam.py]

This latest code is part of a 

## User Stories

* [x] Raspberry Pi (Pi) creates Access Point and acts as a Group Owner
* [x] Android detects nearby Pi displays connection name on Screen
* [x] Android triggers connection by a Click event, pbc (PBC). This ensures no specific key code is needed
* [ ] Android takes pictures and stores them on Pi
* [ ] Android can stream pictures and video from raspberry pi

## Optional
* [ ] Android can stream video (Miracast) to nearby WiFi enabled devices

## Notes
- Ensure that the WiFi device, usually wlan0 on Linux, has a Realtek chip. 
This program relies heavily on `iwpriv wlan0 p2p_set` which has custom realtek commands.
https://rtl8192cu.googlecode.com/hg-history/bdd3a2265bdd6a92f24cef3d52fa594b2844c9c1/document/RTK_Wi-Fi_Direct_Programming_guide.pdf


## License

    Copyright [2015] [Sabelo Mhlambi]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
