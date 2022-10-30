# :red_circle::green_circle::large_blue_circle: RGBControllerLED :large_blue_circle::green_circle::red_circle:
:coffee: Application for controlling RGB LEDs in the car interior via Bluetooth
## Screenshots :camera_flash:
![Screenshot1](https://user-images.githubusercontent.com/113792486/198884387-81768512-bc46-46df-b7ca-0fc2ab45363f.jpg)
![Screenshot2](https://user-images.githubusercontent.com/113792486/198884395-72527299-b261-4932-964d-ccdbfe883234.jpg)
![Screenshot3](https://user-images.githubusercontent.com/113792486/198884397-e1c1bd65-bbbb-47d6-837c-fab07a0aa139.jpg)
## About :question:
This program was written at the request of my friend, who is engaged in the repair of Honda cars :car: (including non-standard interior lighting).

The application interacts with the STM32f103c8t6 controller using the HC-05 bluetooth module. Used diodes - WS2812b.

I cannot post a program (code) for the microcontroller, since this is someone else's work. :pensive: So I'm happy to share with you what I have! :smile:
## Exchange protocol :question:
**The exchange protocol consists in sending parcels of 6 bytes to the microcontroller:**  
**1.** The first byte means a command to receive light settings (***equals 0x01***).  
**2.** The second byte is responsible for the number of the controlled channel   
(***0x00*** means entire saloon, ***0x01*** - steering wheel, ***0x02*** - front left door, ***0x03*** - front right door, 
***0x04*** - rear left door, ***0x05*** - rear right door, ***0x06*** - front left seat, ***0x07*** - front right seat, ***0x08*** - rear left seat, 
***0x09*** - rear right seat, ***0x10*** - climate control panel).    
**3.** The third byte is the number of the LED in the channel for individual control (_at the moment, the function is not implemented, so all diodes are controlled at once_, byte ***equals 0x00***).      
**4.**  The fourth byte - red color value from 0 to 255.        
**5.**  The fifth byte - green color value from 0 to 255.   
**6.**  The sixth byte - blue color value from 0 to 255.    
## How to launch :question:
- You can directly download builded apk from outputs (minimum Android version 7.0).   
##### :warning: So far, I don’t know how you can correctly launch the project on your computer, I haven’t figured it out yet ... I would be glad if you share the method. For now, you can just look at the code or download the apk file... :warning:
### Enjoy! :+1:
