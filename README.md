# ezFFTW

A wrapper for the FFTW library built on top of bytedecos JavaCPP preset that hides the cumbersome native pointering for ez (easy) use. [See bytedeco's code here](https://github.com/bytedeco/javacpp-presets/tree/master/fftw). [See FFTW code here](https://github.com/FFTW/fftw3).

[![Build Status](https://travis-ci.org/hageldave/ezFFTW.svg?branch=master)](https://travis-ci.org/hageldave/ezFFTW)
[![Coverage Status](https://coveralls.io/repos/github/hageldave/ezFFTW/badge.svg?branch=master)](https://coveralls.io/github/hageldave/ezFFTW?branch=master)

## Examples

### Image Band Pass
(from [examples.Filtering](../blob/master/src/test/java/hageldave/ezfftw/dp/example/Filtering.java))
```java
static void bandPassImageFilter(InputStream input, OutputStream output, String outFormat){
  try {
    // load image
    BufferedImage loadedimg = ImageIO.read(input);
    final int width = loadedimg.getWidth();
    final int height = loadedimg.getHeight();
    // make sampler for image
    RealValuedSampler sampler = new RealValuedSampler() {
      @Override
      public double getValueAt(long... coordinates) {
        // we know coordinates will be 2D and in range of image dimensions
        int rgb = loadedimg.getRGB((int)coordinates[0], (int)coordinates[1]);
        // return average grey in range [0,1]
        return ((rgb>>16&0xff)+(rgb>>8&0xff)+(rgb&0xff))/(3*255.0); 
      }
    };
    // make fft storage (RowMajorArrayAccessor implements sampler and writer)
    RowMajorArrayAccessor realPart = new RowMajorArrayAccessor(
        new double[width*height], width,height);
    RowMajorArrayAccessor imagPart = new RowMajorArrayAccessor(
        new double[width*height], width,height);
    // execute fft
    FFT.fft(sampler, realPart.combineToComplexWriter(imagPart), width, height);
    // make sampler that will filter out frequencies
    ComplexValuedSampler filterSampler = new ComplexValuedSampler() {
      @Override
      public double getValueAt(boolean imaginary, long... coordinates) {
        // get coordinates with centered DC
        double x = ( ((coordinates[0]+ width/2)% width)- width/2 )*2.0/width;
        double y = ( ((coordinates[1]+height/2)%height)-height/2 )*2.0/height;
        // get length (corresponds to frequency)
        double l = Math.sqrt(x*x+y*y);
        // define band pass frequencies to be in ]0.1, 0.4[
        if(l > 0.1 && l < 0.4){
          return imaginary ? imagPart.getValueAt(coordinates[0],coordinates[1]) 
                  : realPart.getValueAt(coordinates[0],coordinates[1]);
        } else return 0;
      }
    };
    // make target image for writing result
    BufferedImage filteredImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    RealValuedWriter imgWriter = new RealValuedWriter() {
      @Override
      public void setValueAt(double value, long... coordinates) {
        value /= width*height; // remove scaling (FFTW does it like this)
        value = Math.max(0, Math.min(value, 1)); // clamp value between [0,1]
        int byteval = (int)(value*255);
        int argb = 0xff000000|(byteval<<16)|(byteval<<8)|byteval; // make greyscale argb
        filteredImg.setRGB((int)coordinates[0], (int)coordinates[1], argb);
      }
    };
    // execute inverse fft
    FFT.ifft(filterSampler, imgWriter, width, height);
    ImageIO.write(filteredImg, outFormat, output);
  } catch (IOException e){
    e.printStackTrace();
  }
}
```
