About Me
=========




Features
==========
- Single Click Decompile | Split Apks: will be Merged to single Apk.
- Run Button : Recompile + zipAlign + Sign with test keys + install to selected Device + Launch MainActivity.
- List attached adb Devices. Nox Emulator Support. No Nox Disconnect Problem.
- Smali to Java Convert on Ctrl+S : to check if modified code is correct.


Inbuilt Tools(Used):
====================
- ApkTools by
- smali baksmali
- apksigner
- jadx
- zipAlign

How to import Project:
======================
this is maven based project.( means no GBs of tools download when importing.)
most of used libraries are either in jar form or in java form.
only 4-5 libraries used as maven links(means you will need to download them)
so just download source and open in intellij community addition.

Understanding Codes:
====================
this project has a main GUI class named Editor. which has
    menu bar.
    toolbar.
    status bar ( a progress bar and a JLable)
    3 split panes ( tree view, smali codes and java codes)

    it do not have any codes. just gui.

    then there is a class named super_MenuInterface. it provides easy way to add menus to menu bar.
    so every module (starts with name "mod_" ) extends to it and use its functions to add menu items and onclick handlers.
    in GUI(Editor.java), module is loaded by initializing class under InitModules() method. they are feed with Editor class
    in constructor, so that they can use Editor's GUI components as they wish.
    so when writing new functionalty all you need to define is a class extending super_MenuInterface and init it in Editor's InitModules().
    most important and biggest module is "mod_apkUtils" which do creates most of the menus.

    every module is free to use a static function library named "utils". in this library i write and collects static functions
    that can be copy paste in other projects.

    There is a interface named I_itct. which acts as glue between threads and GUI. it has only one method named "onProgress".

    a Thread (started with name "Thread_") provided this interface(or class implementing this interface) in constructor and then on important events, thread calls its
    onProgress method with custom parameters.  the class which implements this interface, receives these parameters and acts accordingly.

    Listener_statusBarTasks implements this interface to get Decompiling and Recompiling messages and errors to show progress bar on status bar.
    for this purpose, Thread_Decompile and Thread_compileAndInstall provided Listener_statusBarTasks in their constructor.

    full name of I_itct is Interface for Indefinite Time Consuming Task.
    these Threads call calls command lines to tools.

    subc_EditorWindow is subclass of library RSyntaxArea. for providing syntax highlighting scheme change according to file type selected
    in tree view. By default RsyntaxArea do not come with smali syntax highlighting. so i added smali highlighting scheme in class named
    "smaliSyntax.java" , this file is created by open source program named TokenMakerMaker.

    that dark theme and menus in titile bar are provided by library named flatlaf. you just need to write single line to use it.

========================
Remaining Functionality
========================
** adding python plugin interface **
    although you can import this project and then add some functionality. but then this functionality will remain just to you.
    if you publish this on github, then there will be two projects (mine and yours).
    to avoid this situation, plugins interface is needed.
    python plugins are fast at file handling. all we need is to work on smali files. so different plugins can provide more functionality.
    then the problem of collecting that plugins to one place. i have thought that on github's discussion section, everyone can inform about their
    plugins instead of error reporting.

** add remover **
    most of modded apk i downloaded for is to remove ads. so i want to provide simple functions for detection of ads apis. then finding references
    to these apis.

** Frida injection **
    title is self explaining.

** add Resource **
    a option to add new resource with single click. which creates id to public.xml, and entries in other res xmls.

** simple functions **
    a option to inject a static function library. so that single lines can be added to smali to add some functions like toast, debug.print, dialogbox
    write data to file, read data from file. send data to internet... download data from internet. etc. etc.