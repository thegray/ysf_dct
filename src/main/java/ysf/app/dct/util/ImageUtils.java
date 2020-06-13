package ysf.app.dct.util;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageUtils {

    public BufferedImage PlanarToBufImage(Planar<GrayF32> inp) {
        BufferedImage out = new BufferedImage(inp.getWidth(), inp.getHeight(), BufferedImage.TYPE_INT_RGB);
        ConvertBufferedImage.convertTo(inp, out,true);
        return out;
    }

    public String SaveBufImagePNG(BufferedImage img, String outputImageName) throws IOException {
        File pwd = new File("./output");
        File outfile = File.createTempFile("output_", ".png", pwd);
//        System.out.println("GET NAME: " + outfile.getName());
        ImageIO.write(img, "PNG", outfile);
        String outpath = outfile.getAbsolutePath();
        System.out.println("Image output saved at: " + outpath);
        return outpath;
    }
}
