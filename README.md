# AsciiAnimationBasedVideoStream
Streaming Ascii Animation Based Videos on Graphical User Interface Using Communication Between Client and Server

## Running Server

### Compilation

```
  gcc server.c -o server -pthread
```

### Starting

```
  ./server -p PORT_NUMBER -s STREAM_NUMBER -ch1 path/to/video_file -ch2 path/to/another/video_file -ch3 ...
```

### Example Server Starting

```
 ./server -p 8080 -s 3 -ch1 ./video1.txt -ch2 ./video2.txt -ch3 ./video3.txt
```

