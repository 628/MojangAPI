# MojangAPI
Java/Kotlin wrapper for the Mojang API.
## Getting Started
To add this API to your project, import via maven or gradle (you can also build from source if you wish):
### Maven
Add the JitPack Repo:
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
Add the dependency:
```
	<dependency>
	    <groupId>dev.aello</groupId>
	    <artifactId>MojangAPI</artifactId>
	    <version>1.0.0</version>
	</dependency>
```
### Gradle
Add the JitPack Repo in your build.gradle:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
Add the dependency:
```
	dependencies {
	        implementation 'dev.aello:MojangAPI:1.0.0'
	}
```
## Usage
After creating the MojangAPI object, you should check the connection to ensure that the API is operational and you can connect to it:
```
MojangAPI api = new MojangAPI();

if (!api.connect()) {
    // Handle the failed connect attempt...
}
```

Documentation can be found here: <https://docs.aello.dev/mojangapi/dev.aello.mojangapi/-mojang-a-p-i/>
