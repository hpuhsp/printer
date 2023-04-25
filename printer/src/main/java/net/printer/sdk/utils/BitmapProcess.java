package net.printer.sdk.utils;

/**
 * @Description:
 * @Author: Hsp
 * @Email: 1101121039@qq.com
 * @CreateTime: 2023/4/25 14:46
 * @UpdateRemark: 更新说明：
 */

import android.graphics.Bitmap;
import android.graphics.Matrix;

import java.util.ArrayList;
import java.util.List;

public class BitmapProcess {
    enum PrinterWidth {
        Pos80, Pos76, Pos58;
    }

    public static Bitmap compressBmpByPrinterWidth(Bitmap bitmap, PrinterWidth printerWidth) {
        Bitmap bitmapOrg = bitmap;
        Bitmap resizedBitmap = null;
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        int w = 576;
        switch (printerWidth) {
            case Pos80:
                w = 576;
                break;
            case Pos76:
                w = 508;
                break;
            case Pos58:
                w = 384;
                break;
            default:
                w = 576;
                break;
        }
        if (width <= w)
            return bitmapOrg;
        int newWidth = w;
        int newHeight = height * w / width;
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    public static Bitmap compressBmpByYourWidth(Bitmap bitmap, int w) {
        Bitmap bitmapOrg = bitmap;
        Bitmap resizedBitmap = null;
        int width = bitmapOrg.getWidth();
        int height = bitmapOrg.getHeight();
        if (width <= w)
            return bitmapOrg;
        int newWidth = w;
        int newHeight = height * w / width;
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        resizedBitmap = Bitmap.createBitmap(bitmapOrg, 0, 0, width,
                height, matrix, true);
        return resizedBitmap;
    }

    enum RotateType {
        Rotate90, Rotate180, Rotate270;
    }

    public static Bitmap rotateBmp(Bitmap bitmap, RotateType rotateType) {
        Bitmap bitmapOrg = bitmap;
        Matrix matrix = new Matrix();
        float degrees = 0.0F;
        switch (rotateType) {
            case Rotate90:
                degrees = 90.0F;
                break;
            case Rotate180:
                degrees = 180.0F;
                break;
            case Rotate270:
                degrees = 270.0F;
                break;
        }
        matrix.postRotate(degrees);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmapOrg, 0, 0, bitmapOrg.getWidth(), bitmapOrg.getHeight(), matrix, true);
        return bitmap2;
    }

    public static List<Bitmap> cutBitmap(int h, Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        boolean full = (height % h == 0);
        int n = (height % h == 0) ? (height / h) : (height / h + 1);
        List<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Bitmap b;
            if (full) {
                b = Bitmap.createBitmap(bitmap, 0, i * h, width, h);
            } else if (i == n - 1) {
                b = Bitmap.createBitmap(bitmap, 0, i * h, width, height - i * h);
            } else {
                b = Bitmap.createBitmap(bitmap, 0, i * h, width, h);
            }
            bitmaps.add(b);
        }
        return bitmaps;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, boolean isOriginal) {
        Bitmap BitmapOrg = bitmap;
        Bitmap resizedBitmap = null;
        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        if (width <= w)
            return bitmap;
        if (!isOriginal) {
            int newWidth = w;
            int newHeight = height * w / width;
            float scaleWidth = newWidth / width;
            float scaleHeight = newHeight / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                    height, matrix, true);
        } else {
            resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, w, height);
        }
        return resizedBitmap;
    }
}

