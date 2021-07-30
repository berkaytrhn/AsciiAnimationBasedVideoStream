# AsciiAnimationBasedVideoStream
Streaming Ascii Animation Based Videos on Graphical User Interface Using Communication Between Client and Server

```
Java/Javac Version -> 1.8.291

GCC Version -> 9.3.0
```

## Running Server

### Compilation

```
  gcc server.c -o server -pthread
```

### Starting

```
  ./server -p PORT_NUMBER -s STREAM_NUMBER -ch1 path/to/video_file -ch2 path/to/another/video_file -ch3 ...
```

#### Example Server Starting

```
 ./server -p 8080 -s 3 -ch1 ./video1.txt -ch2 ./video2.txt -ch3 ./video3.txt
```

## Running Client

### Compilation

#### Standard
```
  javac UserInterface.java
```

#### With External '.jar' File(s) of JavaFX

##### Java Version 1.8.*
```
 javac -classpath path/to/your/jar_files UserInterface.java
```
##### Java Version 1.11.* (Tested with OpenJfx 11.0.2)
```
 javac --module-path /path/to/your/jar_files UserInterface.java
```
### Running Client

#### Standard
``` 
 java UserInterface -a ADDRESS -p PORT_NUMBER -ch CHANNEL_ID
```
##### Example Standard Running
```
 java UserInterface -a localhost -p 8080 -ch 2
```

#### With External '.jar' File(s) of JavaFX

##### Java Version 1.8.*
```
  java -classpath path/to/your/jar_files -a ADDRESS -p PORT_NUMBER- ch CHANNEL_ID
```
##### Java Version 1.11.* (Tested with OpenJfx 11.0.2)
```
  java --module-path /path/to/your/jar_files UserInterface -a ADDRESS -p PORT_NUMBER- ch CHANNEL_ID
```


