package ysf.app.dct.lib;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ysf.app.dct.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

@Service
public class DCT {

    ImageUtils imgUtils;

    @Autowired
    public DCT(ImageUtils imgUtils) {
        this.imgUtils = imgUtils;
    }

    public static final int BASE8 = 8;
    public static final int BASE16 = 16;

    /* ------------------------------------------------------------------ */

    private void Print4DArray(float[][][][] arr, int p1, int p2, int p3, int p4) {

        System.out.println("Params: " + Arrays.toString(arr));

//        for (int a = 0; a < p1; a++) {
//            for (int b = 0; b < p2; b++) {
//                for (int d = 0; d < p3; d++) {
//                    for (int e = 0; e < p4; e++) {
//                        System.out.printf("%f ", arr[a][b][d][e]);
//                    }
//                    System.out.print("\n");
//                }
//                System.out.print(" ~~ ");
//            }
//            System.out.print(" !! ");
//        }
    }

    public void funcTest() {
        int num_patches = 2;
        int channel = 2;
        int height_p = 5;
        int width_p = 5;

        float[][][][] patches = new float[num_patches][][][];
        for (int p = 0; p < num_patches; p++) {
            patches[p] = new float[channel][][];
            for (int c = 0; c < channel; c++) {
                patches[p][c] = new float[height_p][];
                for (int h = 0; h < height_p; h++) {
                    patches[p][c][h] = new float[width_p];
                }
            }
        }

//        patches[counter_patch][kp][jp][ip] = im[kp*size1 + (j+jp)*width + i + ip];

        int xx = 0;
        for (int a = 0; a < num_patches; a++) {
            for (int b = 0; b < channel; b++) {
                for (int d = 0; d < height_p; d++) {
                    for (int e = 0; e < width_p; e++) {
                        patches[a][b][d][e] = xx;
                        xx++;
                    }
                }
            }
        }

        System.out.println("Patches: " + Arrays.toString(patches));
        Print4DArray(patches, num_patches, channel, height_p, width_p);
    }

    public void boofTest(BufferedImage in) throws IOException {

        Planar<GrayF32> convertedImg = new Planar<>(GrayF32.class, in.getWidth(), in.getHeight(), 3);
        ConvertBufferedImage.convertFrom(in, convertedImg, true);

        Random rand = new Random();
        for( int i = 0; i < convertedImg.getNumBands(); i++ ) {
            for (int y = 0; y < convertedImg.getHeight(); y++) {
                for (int x = 0; x < convertedImg.getWidth(); x++) {
//                    System.out.println("Original "+i+" = "+rgb.getBand(i).get(x,y));
                    float xx = convertedImg.getBand(i).get(x,y);
//                    int rand_int1 = rand.nextInt(25);
                    xx += 100;
                    convertedImg.getBand(i).set(x, y, xx);
                }
            }
        }

        BufferedImage output = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());
        ConvertBufferedImage.convertTo(convertedImg, output,true);

        File pwd = new File(".");
        File outfile = File.createTempFile("aaaaa", ".png", pwd);
        ImageIO.write(output, "PNG", outfile);
        System.out.println("doneee: " + outfile.getAbsolutePath());
    }

    public void testColorTransform(BufferedImage in, BufferedImage out, int width, int height) throws IOException {
        int bands = 3;
//        int[] bandOffsets = {0, 1, 2, 3}; // length == bands, 0 == R, 1 == G, 2 == B and 3 == A
        float[] outsrc = new float[width*height*bands];
        float[] outsrc2 = new float[width*height*4];

        int size1 = width * height;
        System.out.println("w: "+ width+", h:"+height);

        int imagetype = in.getType();
        Raster xx = in.getData();
        DataBuffer yy = xx.getDataBuffer();
        System.out.println("data buffer size: " + yy.getSize());
        for (int asd = 0; asd < yy.getSize(); asd++) {
//            System.out.print(yy.getElemFloat(asd)+" ");
            outsrc2[asd] = yy.getElemFloat(asd);
        }
        System.out.println("end \n");
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

//                System.out.println("try get rgb: "+j+","+i+".");
                Color c = new Color(in.getRGB(i, j));
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
//                int a = c.getAlpha();
//                System.out.println("getrgb: "+j+","+i+"("+r+","+g+","+b+")");

                // fill the buffer
                int idx_pixel0 = j * width + i;
                int idx_pixel1 = 1 * size1 + j * width + i;
                int idx_pixel2 = 2 * size1 + j * width + i;
//                outsrc[idx_pixel0] = or;
//                outsrc[idx_pixel1] = og;
//                outsrc[idx_pixel2] = ob;
                outsrc[idx_pixel0] = r;
                outsrc[idx_pixel1] = g;
                outsrc[idx_pixel2] = b;
            }
        }

        for(int aa = 0; aa < width * height * 4; aa++){
//            System.out.print(outsrc2[aa] + " ");
        }
        System.out.println("end \n");

        out = new BufferedImage(width, height, imagetype);
        WritableRaster raster = out.getRaster();
        raster.setPixels(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), outsrc);
//        raster.setPixels(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), outsrc2);

        File pwd = new File(".");
        File outfile = File.createTempFile("aaaaa", ".png", pwd);
        ImageIO.write(out, "PNG", outfile);
        System.out.println("doneee: " + outfile.getAbsolutePath());
    }

    public void testCreateImage() throws IOException {
        int w = 300;
        int h = 300;

        Color[] colors = new Color[] { Color.red, Color.green, Color.blue };

        BufferedImage img = new BufferedImage(w, h, 1);

        int dx = w / colors.length;

        for (int i = 0; i < colors.length; i++) {
            for (int x = i *dx; (x < (i + 1) * dx) && (x < w) ; x++) {
                for (int y = 0; y < h; y++) {
                    img.setRGB(x, y, colors[i].getRGB());
                }
            }
        }

        File pwd = new File(".");
        File out = File.createTempFile("rgba_", ".png", pwd);
        System.out.println("Create file: " + out.getAbsolutePath());
        ImageIO.write(img, "PNG", out);
//        return ImageIO.createImageInputStream(out);
    }

    /* ------------------------------------------------------------------ */

    public Planar<GrayF32> ColorTransform(Planar<GrayF32> preparedImg, TransformMode transformMode) {

        int w = preparedImg.getWidth();
        int h = preparedImg.getHeight();

        Planar<GrayF32> calcImg = new Planar<>(GrayF32.class, w, h, 3);

        float[][] DCTbasis3x3 = DCTConst.getDCTbasis3();

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                float temp_r = preparedImg.getBand(0).get(i, j);
                float temp_g = preparedImg.getBand(1).get(i, j);
                float temp_b = preparedImg.getBand(2).get(i, j);

                float calc_r, calc_g, calc_b;
                if (transformMode == TransformMode.FORWARD) {
                    calc_r = (temp_r * DCTbasis3x3[0][0]) + (temp_g * DCTbasis3x3[0][1]) + (temp_b * DCTbasis3x3[0][2]);
                    calc_g = (temp_r * DCTbasis3x3[1][0]) + (temp_g * DCTbasis3x3[1][1]) + (temp_b * DCTbasis3x3[1][2]);
                    calc_b = (temp_r * DCTbasis3x3[2][0]) + (temp_g * DCTbasis3x3[2][1]) + (temp_b * DCTbasis3x3[2][2]);
                } else {
                    calc_r = (temp_r * DCTbasis3x3[0][0]) + (temp_g * DCTbasis3x3[1][0]) + (temp_b * DCTbasis3x3[2][0]);
                    calc_g = (temp_r * DCTbasis3x3[0][1]) + (temp_g * DCTbasis3x3[1][1]) + (temp_b * DCTbasis3x3[2][1]);
                    calc_b = (temp_r * DCTbasis3x3[0][2]) + (temp_g * DCTbasis3x3[1][2]) + (temp_b * DCTbasis3x3[2][2]);
                }

                calcImg.getBand(0).set(i, j, calc_r);
                calcImg.getBand(1).set(i, j, calc_g);
                calcImg.getBand(2).set(i, j, calc_b);
            }
        }

        return calcImg;
    }

    private void Image2Patches(Planar<GrayF32> decImg, float[][][][] patches, int width_p, int height_p) {

        int width = decImg.getWidth();
        int height = decImg.getHeight();
        int channel = decImg.getNumBands();

        int counter_patch = 0;
        // loop over each patch
        for (int j = 0; j < height - height_p + 1; j++) {
            for (int i = 0; i < width - width_p + 1; i++) {
                // loop over each pixels in patch
                for (int cp = 0; cp < channel; cp++) {
                    for (int jp = 0; jp < height_p; jp++) {
                        for (int ip = 0; ip < width_p; ip++) {
                            patches[counter_patch][cp][jp][ip] = decImg.getBand(cp).get(i + ip, j + jp);
                        }
                    }
                }
                counter_patch++;
            }
        } // end of loop patches

    }

    private Planar<GrayF32> Patches2Image(float[][][][] patches, int width, int height, int channel, int width_p, int height_p) {

        Planar<GrayF32> img = new Planar<>(GrayF32.class, width, height, 3);
        Planar<GrayF32> weight = new Planar<>(GrayF32.class, width, height, 3);
        // init the array
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                for (int c = 0; c < channel; c++) {
                    img.getBand(c).set(i, j, 0);
                    weight.getBand(c).set(i, j, 0);
                }
            }
        }

        int counter_patch = 0;
        // Loop over the patch positions
        for (int h = 0; h < height - height_p + 1; h++) {
            for (int w = 0; w < width - width_p + 1; w++) {
                // loop over the pixels in the patch
                for (int cp = 0; cp < channel; cp++) {
                    for (int y = 0; y < height_p; y++) {
                        for (int x = 0; x < width_p; x++) {

                            float temp = img.getBand(cp).get(w + x, h + y);
                            temp += patches[counter_patch][cp][y][x];
                            img.getBand(cp).set(w + x, h + y, temp);

                            float wtemp = weight.getBand(cp).get(w + x, h + y);
                            wtemp += 1;
                            weight.getBand(cp).set(w + x, h + y, wtemp);
                        }
                    }
                }
                counter_patch++;
            }
        } // end of patches loop

        // Normalize by the weight
        for (int jn = 0; jn < height; jn++) {
            for (int in = 0; in < width; in++) {
                for (int cn = 0; cn < channel; cn++) {
                    float temp = img.getBand(cn).get(in, jn);
                    int div = (int) weight.getBand(cn).get(in, jn);
                    if (div == 0) {
                        System.out.printf("[Patches2Image][Normalization] DIVIDER IS ZERO c:%d x:%d y:%d \n", cn, in, jn);
                    } else {
                        float res = temp / div;
                        img.getBand(cn).set(in, jn, res);
                    }
                }
            }
        }// end of normalize loop

        return img;
    }

    private void Thresholding(float[][][][] patches, int num_patches, int channel, int height_p, int width_p, float threshold) {
        for (int p = 0; p < num_patches; p ++) {
            for (int k = 0; k < channel; k++) {
                for (int j = 0; j < height_p; j++) {
                    for (int i = 0; i < width_p; i++) {
                        if (Math.abs(patches[p][k][j][i]) < threshold) {
                            patches[p][k][j][i] = 0;
                        }
                    }
                }
            }
        }
    }

    /* ------------------------------------- MAIN DCT FUNCTION ------------------------------------------- */

    public Planar<GrayF32> DCTdenoising(Planar<GrayF32> preparedImg, int sigma, DCTBasisMode baseMode, int height, int width, int imgType) throws Exception {

        float THRESHOLD = 3f * sigma;
//        int originalImageType = originalImage.getType();

        // TODO: support 1 channel image
        int channel = 3;

        // DCT window size
        int width_p, height_p;
        switch (baseMode) {
            case Mode16:
                width_p = BASE16;
                height_p = BASE16;
                break;
            case Mode8:
                width_p = BASE8;
                height_p = BASE8;
                break;
            default:
                System.out.println("Unknown BaseMode");
                throw new Exception("Unknown BaseMode Called");
        }

        // Get image dimensions
//        int height = originalImage.getHeight();
//        int width = originalImage.getWidth();

        int num_patches = (width - width_p + 1) * (height - height_p + 1);

        float[][][][] patches = new float[num_patches][channel][height_p][width_p];

        // TODO: support 1 channel image
        if (channel == 3) {

        }

//        Planar<GrayF32> preparedImg = new Planar<>(GrayF32.class, width, height, 3);
//        ConvertBufferedImage.convertFrom(originalImage, preparedImg, true);

        long startTime = System.nanoTime();
        // color transform forward
        Planar<GrayF32> decImage = this.ColorTransform(preparedImg, TransformMode.FORWARD);
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        double seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("Colortransform forward: %.4f s\n", seconds);

        startTime = System.nanoTime();
        // image to patches
        this.Image2Patches(decImage, patches, width_p, height_p);
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("Image2Patches: %.4f s\n", seconds);


        startTime = System.nanoTime();
        // 2D DCT forward
        for (int p = 0; p < num_patches; p ++) {
            for (int k = 0; k < channel; k ++) {
                DCT2D.Calculate(patches[p][k], baseMode, TransformMode.FORWARD);
            }
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("DCT Calc forward: %.4f s\n", seconds);

        startTime = System.nanoTime();
        // Thresholding
        this.Thresholding(patches, num_patches, channel, height_p, width_p, THRESHOLD);
        //
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("Thresholding: %.4f s\n", seconds);

        startTime = System.nanoTime();
        // 2D DCT inverse
        for (int p = 0; p < num_patches; p ++) {
            for (int k = 0; k < channel; k ++) {
                DCT2D.Calculate(patches[p][k], baseMode, TransformMode.BACKWARD);
            }
        }
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("DCT calc inverse: %.4f s\n", seconds);

        startTime = System.nanoTime();
        // Decompose the image into patches
        Planar<GrayF32> patches2ImageResult = this.Patches2Image(patches, width, height, channel, width_p, height_p);
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("Patches2Image: %.4f s\n", seconds);

        startTime = System.nanoTime();
        // inverse 3-point DCT transform in the color dimension
        Planar<GrayF32> finalResult = this.ColorTransform(patches2ImageResult, TransformMode.BACKWARD);
        endTime = System.nanoTime();
        duration = (endTime - startTime);
        seconds = (double)duration / 1_000_000_000.0;
        System.out.printf("Color trans inverse: %.4f s\n", seconds);

        // debug
//        startTime = System.nanoTime();
//        this.debugFunc(finalResult);
//        endTime = System.nanoTime();
//        duration = (endTime - startTime);
//        seconds = (double)duration / 1_000_000_000.0;
//        System.out.printf("Debug: %.4f s\n", seconds);

//        return imgUtils.PlanarToBufImage(finalResult, imgType);
        return finalResult;
    }

    private void debugFunc(Planar<GrayF32> in) throws IOException {
        // function to split each band of a planar object and save each splat bands to images
        Planar<GrayF32> b0 = new Planar<>(GrayF32.class, in.getWidth(), in.getHeight(), in.getNumBands());
        Planar<GrayF32> b1 = new Planar<>(GrayF32.class, in.getWidth(), in.getHeight(), in.getNumBands());
        Planar<GrayF32> b2 = new Planar<>(GrayF32.class, in.getWidth(), in.getHeight(), in.getNumBands());
        for (int j = 0; j < in.getHeight(); j++) {
            for (int i = 0; i < in.getWidth(); i++) {
                float temp0 = in.getBand(0).get(i, j);
                float temp1 = in.getBand(1).get(i, j);
                float temp2 = in.getBand(2).get(i, j);

                b0.getBand(0).set(i, j, temp0);
                b0.getBand(1).set(i, j, 0f);
                b0.getBand(2).set(i, j, 0f);

                b1.getBand(0).set(i, j, 0f);
                b1.getBand(1).set(i, j, temp1);
                b1.getBand(2).set(i, j, 0f);

                b2.getBand(0).set(i, j, 0f);
                b2.getBand(1).set(i, j, 0f);
                b2.getBand(2).set(i, j, temp2);
            }
        }

        BufferedImage i0 = imgUtils.PlanarToBufImage(b0, 5);
        BufferedImage i1 = imgUtils.PlanarToBufImage(b1, 5);
        BufferedImage i2 = imgUtils.PlanarToBufImage(b2, 5);

        imgUtils.SaveBufImage(i0, "Debug", "uploads");
        imgUtils.SaveBufImage(i1, "Debug", "uploads");
        imgUtils.SaveBufImage(i2, "Debug", "uploads");
    }
}


