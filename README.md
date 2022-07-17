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
- `[WORKING ON]` : IntelliJ like Dark Themed Modular GUI.**_(newEditor.java)_**
    - `DONE` : Main Frame. With Vertical And Horizontal Docker Bars
    - `DONE` : Tabbed File Viewer
    - `DONE` : Find And Replace (With Regex, Match Case , Whole Word, replace With Regex Groups $1,$2 etc.)
    - `[WORKING ON]` : bring this experimetal GUI to _**main GUI (Editor.java)**_
- `TODO` : Method And Field Parser Selector Bar.
- `TODO` : Smali Syntax Checking before compilation.
- `TODO` : Reference Finder and Static Analysis
- `TODO` : LogCat And Run Window.
- `TODO` : Manual Renaming of Methods And Functions for Complete DeObsfucation
- `TODO` : Python Plugin Interface
- `TODO` : Custom Code Snippet Injection Tool.
- `TODO` : package Remover with Reference Finder.
#### MINOR IMPROVEMENTS:
- `TODO` : use any monospace font.
- `TODO` : Decompile With Deobsfucation.
#### BUGS And PROBLEMS:
- `[Fixed]` : Nox Running But Not Showing in Device List.. Fixed using "adb connect"
- `[BUG]` : Split Apk crashes when run.

