# IMPORTANT
This is not the original repository. The [original repo](https://github.com/Andrerm124/SnapTools_OpenSource) can be found on the original creator [Andrerm124](https://github.com/Andrerm124/) GitHub.

# SnapTools


A reupload of the SnapTools Private Repository. Unfortunately a fresh repository is required as there were many commits with private information such as developer keys and certificates.

Check out the [SnapTools Website](https://snaptools.io) for a brief rundown of the project!

I will be going through the project and adding additional javadocs as the project deserves a more professional format. If there is any specific code or file that you wish for me to clear up, please feel free to [Open An Issue](https://github.com/Andrerm124/SnapTools_OpenSource/issues/new) thread and I'll do my best to provide information on the topic, however be sure to check out the Points Of Interest section below for the more important systems in the app.

It should be noted that this source is not intended to be built directly into a directly functional APK, it will require adjustments as certain required files have been redacted due to containing private information (Such as [google_services.json](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/google-services.json), [RedactedClasses](https://github.com/Andrerm124/SnapTools_OpenSource/tree/master/app/src/main/java/com/ljmu/andre/snaptools/RedactedClasses), and the certificates located in the [Debug](https://github.com/Andrerm124/SnapTools_OpenSource/tree/master/app/src/debug/assets) and [Release](https://github.com/Andrerm124/SnapTools_OpenSource/tree/master/app/src/release/assets) Assets folders)

# SnapTools Media
[![Shtuff](https://img.youtube.com/vi/mIkM8KTjoWs/0.jpg)](https://www.youtube.com/watch?v=mIkM8KTjoWs)

A user created promotional video by [John Luke](https://www.youtube.com/channel/UCVQavYHPmuzDu5eELNC3oWg)

# Points of Interest
### Framework/ModulePack System
[HookManager](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/src/main/java/com/ljmu/andre/snaptools/HookManager.java)

The entry point for Xposed based code. 
*Todo: Add more comments.*

[FrameworkManager](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/src/main/java/com/ljmu/andre/snaptools/Framework/FrameworkManager.java)

Responsible for managing the loading and injecting of ModulePacks.

[ModulePack](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/src/main/java/com/ljmu/andre/snaptools/Framework/ModulePack.java)

The interface between the framework and the dynamically loaded ModulePackImpl code.

[ModulePackImpl](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/src/pack/java/com/ljmu/andre/snaptools/ModulePack/ModulePackImpl.java)

An implementation of ModulePack with the loading and injection systems in place to manage the internal hooking code.

[HookResolver](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/src/pack/java/com/ljmu/andre/snaptools/ModulePack/HookResolver.java)

Responsible for efficiently generating and caching references to Hooks and HookClasses.

### General
[MainActivity](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/app/src/main/java/com/ljmu/andre/snaptools/MainActivity.java)

The majority of the application initialisation and EventBus subscriptions.

### Custom IntelliJ Plugins

The following are some support plugins that I created with the intent of automating or making workflow easier. If people are interested I will make separate repositories for these plugins.

- [StringEncryptorPlugin](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/StringEncryptorPlugin.jar) - A plugin that provides an **Encrypt Strings** function in the right click menu (And can also be bound to a key). This function will automatically replace string literals with the **decryptString(...)** function (Or reverse an already encrypted string), however the encryption key has been hardcoded into the plugin as it was only intended for personal use. If this library is sought after, let me know and I'll fix it up!
- [ApkVersionPlugin](https://github.com/Andrerm124/SnapTools_OpenSource/blob/master/ApkVersionPlugin.jar) - A plugin that scans for **@FrameworkVersion(versionInt)** annotations on methods and displays an error to the developer when there are no conditional checks for when the build version of the app is higher than the build version of the method being called. Again, this has hardcoded values as I never anticipated making the plugin public source. If this is something that interests you, let me know and I'll do what I can!
- [MappingMerger](https://github.com/Andrerm124/SnapTools_OpenSource/tree/master/app/mapping_merger) - As proguard doesn't offer the ability to maintain static and incremental obfuscation, I had to create my own scripts to automatically merge the previously generated proguard file with the **pre-proguard mappings** file (The file that tells proguard what to name each function) and have the ability to re-include any lost mappings from previous builds as proguard strips out unused mappings that we may need for alternate builds. An implementation (Although crude) can be seen [HERE](https://github.com/Andrerm124/SnapTools_OpenSource/blob/cfa45c5adec117f2b57977688450e909f29211ca/app/build.gradle#L187)
### Parting Words

Finally I would like to take a short moment to say that I have enjoyed every moment of this project thoroughly, even though at times it can be extremely stressful. The community, the friends I've made, and the things I've learnt from this venture has been truly exceptional and hopefully it has been as enjoyable for those that got to experience it as it was for me.
