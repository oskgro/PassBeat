# TODO: put APK file in repository!!


## Instalation guide

#### Android Emulator
- Ensure SDK Platform for Android 6.0 (API level 23) or higher is installed on your emulator.
- Download the PassBeat.apk file.  
    **Option 1 - drag and drop:**  
    - Run the emulator.  
    - Drag the APK file onto the emulator screen, this will open an APK Installer dialog.  
    - The PassBeat app will appear in the app list after installation.
 
    **Option 2 - command line:**  
    - Copy the APK file to:  
        on Windows:

            C:\Program Files (x86)\Android\sdk\platform-tools (on Windows)
    
	on Mac/Linux:
        
            /Users/your-user-name/Library/Android/sdk/platform-tools (on Mac/Linux)
    - Open the command line and type:  
    	on Windows:

     		adb install "PassBeat.apk"
 		
    	on Mac/Linux:
	
    		./adb install "PassBeat.apk"
		
    - Run the emulator.
    - The installed PassBeat app will be on the menu after installation.


#### Android Device
- Ensure your device is using Android 6.0 (API level 23) or higher.
- Download the PassBeat.apk file.
- Open "My files".
- Navigate to the location of the PassBeat.apk file and tap it.
- If prompted give "My Files" the permission to install unknown apps,  
	- Open "Settings".  
	- Navigate to "Biometrics and security" (or "Security & privacy" on older Android versions).  
	- Navigate to "Install unknown apps".  
	- Find "My files".  
	- Toggle the "Allow from this source" switch from off to on.  
- An instalation pop-up will open. Tap "install" to install the PassBeat app.

For more information about PassBeat check out the [PassBeat website](https://oskgro.github.io/)
