package com.neophob.sematrix.core.resize;

class BilinearResize extends Resize {

    public BilinearResize() {
        super(ResizeName.QUALITY_RESIZE);
    }

    public int[] resizeImage(int[] buffer, int currentXSize, int currentYSize, int newX, int newY) {
        return resizeBilinear(buffer, currentXSize, currentYSize, newX, newY);
    }

    public int[] resizeBilinear(int[] pixels, int w, int h, int w2, int h2) {
        int[] temp = new int[w2 * h2];
        int a, b, c, d, x, y, index;
        float x_ratio = ((float) (w - 1)) / w2;
        float y_ratio = ((float) (h - 1)) / h2;
        float x_diff, y_diff, blue, red, green;
        int offset = 0;
        for (int i = 0; i < h2; i++) {
            for (int j = 0; j < w2; j++) {
                x = (int) (x_ratio * j);
                y = (int) (y_ratio * i);
                x_diff = (x_ratio * j) - x;
                y_diff = (y_ratio * i) - y;
                index = (y * w + x);
                a = pixels[index];
                b = pixels[index + 1];
                c = pixels[index + w];
                d = pixels[index + w + 1];

                // blue element
                // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                blue = (a & 0xff) * (1 - x_diff) * (1 - y_diff) + (b & 0xff) * (x_diff)
                        * (1 - y_diff) + (c & 0xff) * (y_diff) * (1 - x_diff) + (d & 0xff)
                        * (x_diff * y_diff);

                // green element
                // Yg = Ag(1-w)(1-h) + Bg(w)(1-h) + Cg(h)(1-w) + Dg(wh)
                green = ((a >> 8) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 8) & 0xff)
                        * (x_diff) * (1 - y_diff) + ((c >> 8) & 0xff) * (y_diff) * (1 - x_diff)
                        + ((d >> 8) & 0xff) * (x_diff * y_diff);

                // red element
                // Yr = Ar(1-w)(1-h) + Br(w)(1-h) + Cr(h)(1-w) + Dr(wh)
                red = ((a >> 16) & 0xff) * (1 - x_diff) * (1 - y_diff) + ((b >> 16) & 0xff)
                        * (x_diff) * (1 - y_diff) + ((c >> 16) & 0xff) * (y_diff) * (1 - x_diff)
                        + ((d >> 16) & 0xff) * (x_diff * y_diff);

                temp[offset++] = 0xff000000
                        | // hardcode alpha
                        ((((int) red) << 16) & 0xff0000) | ((((int) green) << 8) & 0xff00)
                        | ((int) blue);
            }
        }
        return temp;
    }
}
