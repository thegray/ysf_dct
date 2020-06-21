package ysf.app.dct.lib;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.GrayF32;
import boofcv.struct.image.Planar;
import org.springframework.stereotype.Service;

import static boofcv.alg.misc.GImageStatistics.meanDiffSq;
import java.awt.image.BufferedImage;

@Service
public class PSNR {

    public double Calculate(BufferedImage original, BufferedImage compressed) {

        int height = original.getHeight();
        int width = original.getWidth();

        Planar<GrayF32> oriImg = new Planar<>(GrayF32.class, width, height, 3);
        ConvertBufferedImage.convertFrom(original, oriImg, true);

        Planar<GrayF32> compImg = new Planar<>(GrayF32.class, width, height, 3);
        ConvertBufferedImage.convertFrom(compressed, compImg, true);

        double mse = meanDiffSq(oriImg, compImg);

        System.out.println("MSE: " + mse);

        if (mse == 0) {
            return 100.0;
        }

        double max_pixel = 255.0;

        return 20 * Math.log10(max_pixel / Math.sqrt(mse));
    }

}
