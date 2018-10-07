# imageCropAndCompression

You will learn about how to capture image/choose from gallery, after that passing it to a custom view to crop/zoom/pan and can submit to cloud. Compression algorithm added as a service to compress image if required, where it is good to compress if you are pushing the image on cloud as the current compression code is capable of compressing size of an image from 3.5 MB to 0.2MB with negligible change in quality of image.

File provider is used to genrate URI so only your app has access to image you captured and you can let some other apps read or even write it via a secured ContentProvider where as permissions are revoked when your activity is destroyed.

You will get some eyes on MVVM architecture.

Grid view implemented in recylcer view to show 2 items in a row where one can click on an item to see full image and can zoom/pan on image to see more details.
