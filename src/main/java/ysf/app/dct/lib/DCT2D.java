package ysf.app.dct.lib;

public class DCT2D {

    private static void DCT1D(float[] in, float[] out, int patchsize, float[][] dctbasis, TransformMode transMode) throws Exception {

        // forward transform
        if ( transMode == TransformMode.FORWARD ) {
            for (int j = 0; j < patchsize; j ++) {
                out[j] = 0;
                for (int i = 0; i < patchsize; i ++) {
                    out[j] += in[i] * dctbasis[j][i];
                }
            }
        }
        // reverse transform
        else if (transMode == TransformMode.BACKWARD) {
            for (int j = 0; j < patchsize; j ++) {
                out[j] = 0;
                for (int i = 0; i < patchsize; i ++) {
                    out[j] += in[i] * dctbasis[i][j];
                }
            }
        } else {
            throw new Exception("Unknown Transform mode");
        }
    }

    public static void CalculateDCT2D(float[][] patch, DCTBasisMode baseMode, TransformMode mode) throws Exception {
        int PATCHSIZE = 0;
        float[][] DCTBASIS;
        switch (baseMode) {
            case Mode16:
                PATCHSIZE = 16;
                DCTBASIS = DCTConst.getDCTbasis16();
                break;
            case Mode8:
                PATCHSIZE = 8;
                DCTBASIS = DCTConst.getDCTbasis8();
                break;
            default:
                throw new Exception("Unknown basis mode");
        }

        float[][] tmp1 = new float[PATCHSIZE][PATCHSIZE];
        float[][] tmp2 = new float[PATCHSIZE][PATCHSIZE];

        // transform row by row
        for (int j = 0; j < PATCHSIZE; j ++) {
            DCT1D(patch[j], tmp1[j], PATCHSIZE, DCTBASIS, mode);
        }

        // transform column by column
        // (by transposing the matrix,
        // transforming row by row, and
        // transposing again the matrix.)
        for (int j = 0; j < PATCHSIZE; j ++) {
            for (int i = 0; i < PATCHSIZE; i ++)
                tmp2[j][i] = tmp1[i][j];
        }
        for (int j = 0; j < PATCHSIZE; j ++) {
            DCT1D(tmp2[j], tmp1[j], PATCHSIZE, DCTBASIS, mode);
        }
        for (int j = 0; j < PATCHSIZE; j ++) {
            for (int i = 0; i < PATCHSIZE; i ++)
                patch[j][i] = tmp1[i][j];
        }
    }
}
