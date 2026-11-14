# FAISAL CAKE MAKER

A pink-themed, mobile-friendly cake design customizer.

## Features
- Live cake preview
- Round, heart, square and tall cake shapes
- Cake weight selector (1–10 pounds)
- Cake colour and writing colour
- Customer name and message on the cake
- Elegant, classic and modern writing styles
- Sprinkles, flowers, berries, pearls, candles or minimal decoration
- Save the design as a PNG image
- Works locally in a browser; no AI, account, admin panel, payment or server required

## Run on Android with Acode
1. Open this repository in GitHub and download the project.
2. Open `index.html` in Acode.
3. Preview it in a browser. Internet is not required after the file is saved.

## APK status
The repository currently contains the web app source, not a compiled Android APK. To create an installable APK, package this web app in an Android WebView wrapper and build/sign it using a suitable Android packaging workflow.


## Build Android APK automatically
Every push to `main` starts the GitHub Actions workflow in `.github/workflows/build-apk.yml`.
1. Open the repository's **Actions** tab.
2. Select **Build FAISAL CAKE MAKER APK** and open the latest run.
3. When the run finishes successfully, download the `FAISAL-CAKE-MAKER-APK` artifact.
4. Extract the ZIP and install `app-debug.apk` on your Android phone. If Android asks, allow installation from that source.

The APK is a debug build for testing. A signed release APK should be created separately before public distribution.
