package ysf.app.dct.util;

import boofcv.alg.sfm.DepthSparse3D;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.ImageBase;
import boofcv.struct.image.Planar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Component
public class ImageUtils {

    private StorageUtil stoUtils;

    @Autowired
    public ImageUtils(StorageUtil su) {
        this.stoUtils = su;
    }

    public BufferedImage PlanarToBufImage(ImageBase inp, int type) {
        BufferedImage out = new BufferedImage(inp.getWidth(), inp.getHeight(), type);
        ConvertBufferedImage.convertTo(inp, out,true);
        return out;
    }

    public File SaveBufImage(BufferedImage img, String prefix, String directory) throws IOException {
//        File pwd = new File("./results");
        File path;
        if (directory == "results") {
            path = new File(stoUtils.getResultsPath());
        } else {
            path = new File(stoUtils.getUploadsPath());
        }

        File outfile = File.createTempFile(prefix, ".jpg", path);
//        System.out.println("GET NAME: " + outfile.getName());
        ImageIO.write(img, "JPG", outfile);
//        String outPath = outfile.getAbsolutePath();
//        System.out.println("Image saved at: " + outPath);
        return outfile;
    }
}
