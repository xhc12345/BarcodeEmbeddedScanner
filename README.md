# BarcodeEmbeddedScanner

---

### What is it?

This is a project intended to run (and only tested) on the [Vuzix Blade](https://www.vuzix.com/products/vuzix-blade-smart-glasses-upgraded "Link not guaranteed to work. Product discontinued") augmented reality glass.

It is an application that scans for a QR code made for a Hue light, decodes it using a [sdk provided by Vuzix](https://github.com/Vuzix/sdk-barcode "Link not guaranteed to work"), then send command to the light.

If the scanned result contain information (in the correct format) that corresponds to a Hue light, it will then use those info to attempt to communicate with that light, and give commands such as turning on/off, change brightness, and change color.

---




### How to run it?

You will need to have a Vuzix Blade with Android 22. It is highly recommended that you install [Vuzix View](https://www.vuzix.com/pages/vuzix-view-software "Link not guaranteed to work") too. Vuzix View basically allows you to interact with the glass through the computer, given a glass is connected.

You will also need to set up a Hue bridge with a Hue light, then connect the bridge to your local area network.

There are two parts:

1. [Get the Hue lights ready to receive commands.](#1.-get-the-hue-lights-ready-to-receive-commands)
2. [Get the android app ready on Blade.](#2.-get-the-android-app-ready-to-on-blade)


##### 1. Get the Hue lights ready to receive commands

There are 3 things needed to identify and interact with a specific Hue light:

* `IP address` of the bridge that the light is connected to
  * Download the official Hue app on your phone to [get the bridge IP](https://developers.meethue.com/develop/get-started-2/#:~:text=All%20working%20%E2%80%94%20Go%20to%20the,of%20the%20bridge%20will%20show. "If link not working, Google how to do it").
* `hue-application-key`: a key required by the bridge for every REST API call, used to verify that a valid app is making the call. Needs to be in every request header.
  * Once created, this key should(?) allow anything to make request.
* `rid`: the unique and inherit ID of the light, a very long string
  1. Download and open Postman
  2. Disable SSL cerficate verification for Postman in `Settings->General` (Sidenote: This app disables SSL cert as well in `NukeSSLCerts` class. It's recommended that the production app re-enables it. )
  3. Send a `POST` request `https://<bridge ip address>/api`, with Body of `{"devicetype":"app_name#instance_name", "generateclientkey":true}`. This should give you a response saying that "link button not pressed". Press the button on the Hue bridge.
  4. Send the request from step 3 again. This time should hopefully return "success". Save the `username` and `clientkey` returned, and use that `username` as `hue-application-key` for this app. From now on, every request to the Hue system needs to include `hue-application-key:<hue-application-key>` in its Header.
  5. Sends a `GET` request to `https://<bridge ip address>/clip/v2/resource/device`. In the return JSON data, the `services` list contains all the lights connected to the bridge. Pin-point down which light is the one you are targeting by method of elimination, and save its `rid`.

For a more detailed walkthrough with screenshots, folllow [this official tutorial.](https://developers.meethue.com/develop/hue-api-v2/getting-started/) Play around with the light using Postman to get familiar!

Using a text-to-QR converter, create QR code of those 3 informations based on this format:

```
{
    "bridge-address":<string of ip address>,
    "rid":<string of rid>,
    "hue-application-key":<string of hue-application-key>
}
```

Upon scanning this QR code, the app converts this JSON object to a custom Java object. If the format is not correct, then it will fail to create and no requests can be send.


##### 2. Get the Android app ready to on Blade

1. Install Android Studio.
2. Import this project into Android Studio.
   (Sidenote: running this project does not require creating a Blade virtual device. However, development work may benefit from doing so. [Follow this guide](https://intercom.help/vuzix/en/articles/5954637-overview "Link not guaranteed to work") to create a Blade VM. Remeber, Blade runs on Android 5.1, meaning that you have to use Android sdk 22 for any projects.)
3. The app should come with the QR code scanning library `sdk-barcode.jar` already. Make sure it's there in `app/libs` folder, and that `app/gradle.build` includes it as a impletation file in depedencies.
4. Build the project in Android Studio. If no build error, connect the glass via USB and run the app directly.
5. Connect the Blade glass to the same network as the Hue bridge system.

After all that has been done, you can finally scan the QR code you've created, and it should be able to send a "turn on" command to the light you created the QR code for.
