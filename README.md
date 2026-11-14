# FAISAL CAKE MAKER

Pink-themed customer cake designer for Android and mobile browsers.

## Customer features
- Live preview of round, heart, square and tall cakes
- Cake size selector from 1 to 10 pounds
- Eight cake colours and six writing colours
- Add a name and a custom message to the cake
- Elegant script, classic and modern text styles
- Sprinkles, flowers, berries, pearls, candles and minimal decoration
- Save a PNG design image; in the Android app it is saved to Pictures/FAISAL Cake Maker
- Reset the design at any time

## Product scope
This is a customer-only design tool. It has no admin panel, AI service, account, payment gateway, price list or online ordering. The designer works offline after installation.

## Preview in Acode
1. Open `index.html` from this repository in Acode.
2. Preview it in a browser.
3. Change the cake style and options; use **Save Cake Picture** to download the PNG.

## Build Android APK with GitHub Actions
A push to the `main` branch starts the workflow in `.github/workflows/build-apk.yml`.
1. Open the repository's **Actions** tab.
2. Select **Build FAISAL CAKE MAKER APK** and open the newest run.
3. Wait for the build to finish successfully.
4. Download the `FAISAL-CAKE-MAKER-APK` artifact ZIP.
5. Extract it and install `app-debug.apk` on your Android phone.

The workflow creates a debug APK for testing. This is not a Play Store-signed release. Build success must be confirmed in the Actions tab; source upload alone does not mean an APK has been built.
