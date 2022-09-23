# AModStudio3

AMod Studio is APK Modding Studio.
i wanted to create AndroidStudio like IDE so that on a single click, Modified APK file can be recompiled, signed , zipaligned and pushed to device and launched.
from the time i started this project [15-16 JUNE 2022] , i didnt find any software with that features. now as i have been working on this project since months, i dont want to know if there is already exists similar project.
#### TODO vs COMPLETED:
- `DONE` : Single Click > Recompile -> ZipAlign -> Sign -> Push to Device -> Launch
- `DONE` : Multiple Device Support.
- `DONE` : Side By Side Smali to Java Preview on Save File.
- `DONE` : Smali Syntax Highlighting
- `DONE` : SplitApk zip Support: By Merging All Splitted Apk's `[Still Buggy]`
- `DONE` : IntelliJ like Dark Themed Modular GUI.
- `TODO` : ReadMe
  - `TODO` : Screenshot of GUI
  - `TODO` : Project Structure for Code Contributers
  - `TODO` : A Basic How To:
- `[TODO]` : LogCat And Run Window.
  - `DONE` : Basic Logcat Window
  - `DONE` : Color Errors, Warnings..
  - `DONE` : Basic Run Winodw..
  - `TODO` : Search Option
  - `TODO` : Select Error/Warning/.. only
  - `TODO` : Select Package.
  - `TODO` : Select Device Support
- `TODO` : Method And Field Parser Selector Bar.
- `[WORKING ON]` : Smali Syntax Checking before compilation.
- `[WORKING ON]` : Reference Finder and Static Analysis
- `[WORKING ON]` : in-place APK Editor (dexEditor.java)
- `[WORKING ON]` : Manual Renaming of Methods And Functions for Complete DeObsfucation
- `TODO` : Python Plugin Interface
- `TODO` : Custom Code Snippet Injection Tool.
- `TODO` : package Remover with Reference Finder.
#### MINOR IMPROVEMENTS:
- `TODO` : Decompile With Deobsfucation.
#### BUGS And PROBLEMS:
- `[Fixed]` : Nox Running But Not Showing in Device List.. Fixed using "adb connect"
- `[BUG]` : Split Apk crashes when run.
- `PROBLEM` : Font on tabbed File Viewer Not Effective . WHY?
- `COMPILE ERROR` : resource is not private : `SOLUTION` : replace @android/ with @*android/ for the resource 
- `COMPILE ERROR` : No resource identifier found for attribute ‘style’ in package ‘android’ : `SOLUTION` : remove android: from xml attribute name

