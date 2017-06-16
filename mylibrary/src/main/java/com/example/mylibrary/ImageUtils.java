package com.example.mylibrary;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 图像处理bitmap&Camera
 * Created by chenbangoo on 2017/4/17.
 */
public class ImageUtils {

    // 所需的全部权限
    public static final String[] PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    public static final int SET_PERCODE = 10;  //设置权限回调代码
    /**
     * 跳转到相册
     */
    public static void openAlbum(Activity activity, int flag) {
        Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {// 相机被卸载时不会崩溃
            activity.startActivityForResult(takePictureIntent, flag);
        }
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
                .sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math
                .floor(w / minSideLength), Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    /**
     * 将文件转换为bitmap
     * @param dst
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromFile(File dst, int width, int height) {
        if (null != dst && dst.exists()) {
            BitmapFactory.Options opts = null;
            if (width > 0 && height > 0) {
                opts = new BitmapFactory.Options();
                opts.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(dst.getPath(), opts);
                // 计算图片缩放比例
                final int minSideLength = Math.min(width, height);
                opts.inSampleSize = computeSampleSize(opts, minSideLength,
                        width * height);
                opts.inJustDecodeBounds = false;
                opts.inInputShareable = true;
                opts.inPurgeable = true;
            }
            try {
                return BitmapFactory.decodeFile(dst.getPath(), opts);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 跳转到相机（6.0及以下）
     *
     * @param activity
     */
    public static void openbeforeCamera(Activity activity, Uri uri, int flag) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        activity.startActivityForResult(takePictureIntent, flag);
    }

    /**
     * 跳转打开相机（包含7.0及以下）
     * @param activity
     * @param uri
     * @param flag
     */
    public static void openCamera(Activity activity, Uri uri, int flag){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //7.0以上
            openCamera70(activity,uri,flag);
        }else {    //7.0以下
            openbeforeCamera(activity,uri,flag);
        }
    }

    /**
     * 检查是否有sdcard
     * @return
     */
    public static boolean hasSdcard() {
        Log.e("test", "hasSdcard: " + Environment.getExternalStorageState());
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * bitmap编码到base64字符串
     *
     * @param bitmap
     * @return String
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, baos);
        //字节数组
        byte[] bytes = baos.toByteArray();
        //通过Base64编码
        byte[] dd = Base64.encode(bytes, Base64.DEFAULT);
        return new String(dd);
    }


    /**
     * bitmap图片压缩
     *  * @param bitmap   源图片
     * @param width    想要的宽度
     * @param height   想要的高度
     * @param isAdjust 是否自动调整尺寸, true图片就不会拉伸，false严格按照你的尺寸压缩
     * @return Bitmap
     */
    public static Bitmap reduce(Bitmap bitmap, int width, int height, boolean isAdjust) {
        if (bitmap.getWidth() < width && bitmap.getHeight() < height) {
            return bitmap;
        }
        float sx = new BigDecimal(width).divide(new BigDecimal(bitmap.getWidth()), 4, BigDecimal.ROUND_DOWN).floatValue();
        float sy = new BigDecimal(height).divide(new BigDecimal(bitmap.getHeight()), 4, BigDecimal.ROUND_DOWN).floatValue();
        if (isAdjust) {
            sx = (sx < sy ? sx : sy);
            sy = sx;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 7.0相机打开方式
     * @param activity
     * @param flag
     * @return
     */
    public static void openCamera70(Activity activity,Uri uri, int flag) {
        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CAREMA
        activity.startActivityForResult(intent, flag);
    }

    /**
     * 7.0保存方式
     * @param activity
     * @param file
     * @return
     */
    public static Uri getSaveUri70(Activity activity,File file){
        //兼容android7.0 使用共享文件的形式
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        Uri uri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        return uri;
    }

    /**
     * 7.0以前保存方式
     * @param file
     * @return
     */
    public static Uri getSaveUri(File file) {
        Uri uri = Uri.fromFile(file);
        return uri;
    }

    /**
     * 保存文件uri路径
     * @param activity
     * @param file
     * @return
     */
    public static Uri getSaveUriAll(Activity activity,File file){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //7.0以上
            return getSaveUri70(activity,file);
        }else {    //7.0以下
            return getSaveUri(file);
        }
    }




    /**
     * 跳转到系统裁切界面（默认为1：1）
     *(模拟器使用7.1成功，7.0使用失败)
     * @param activity
     * @param uri
     * @param isReturnIntent  选择数据保存方式 true （intent）  false （uri）
     * @param cropWidth，cropHeight 裁剪宽高
     */
    public static void cropPhoto(Activity activity, Uri uri,float cropWidth,float cropHeight, int flag,Boolean isReturnIntent) {
        //跳转到android系统自带的图片裁剪工具
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", true);
        //比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //裁剪宽高
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        //返回data数据（intent）false 不返回  true 返回(通常只能返回小数据，大数据返回容易挂掉，
        // 因此直接用false 然后uri来传递数据)
        //如果为true 则是用intent来传递数据 此处uri的值不会改变
        intent.putExtra("return-data", isReturnIntent);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        activity.startActivityForResult(intent, flag);
    }
    /**
     * 跳转到系统裁切界面（自定义裁剪比例）
     *(模拟器使用7.1成功，7.0使用失败)
     * @param activity
     * @param uri
     * @param isReturnIntent  选择数据保存方式 true （intent）  false （uri）
     * @param cropWidth，cropHeight 裁剪宽高
     */
    public static void cropPhoto(Activity activity, Uri uri,float cropWidth,float cropHeight,int aspectX,int aspectY,int flag,Boolean isReturnIntent) {
        //跳转到android系统自带的图片裁剪工具
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        //设置裁剪
        intent.putExtra("crop", true);
        //比例
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        //裁剪宽高
        intent.putExtra("outputX", cropWidth);
        intent.putExtra("outputY", cropHeight);
        //返回data数据（intent）false 不返回  true 返回(通常只能返回小数据，大数据返回容易挂掉，
        // 因此直接用false 然后uri来传递数据)
        //如果为true 则是用intent来传递数据 此处uri的值不会改变
        intent.putExtra("return-data", isReturnIntent);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        activity.startActivityForResult(intent, flag);
    }

    /**
     * 保存bitmap图像到本地Uri
     *
     * @param bitMap
     * @return返回一个file类型的uri
     */
    public static Uri saveBitMapToUri(Bitmap bitMap) {
        //保存的文件file
        File imageFile = saveBitMapToFile(bitMap);
        return Uri.fromFile(imageFile);
    }

    /**
     * 保存bitmap图像到本地文件
     *
     * @param bitMap
     * @return返回一个file类型的uri
     */
    public static File saveBitMapToFile(Bitmap bitMap) {
        //保存的文件file
        File imageFile = createImageFile();
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            /**
             * 将图像压缩--图像格式--图像压缩质量--输出流
             */
            bitMap.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.flush();
            fos.close();
            bitMap.recycle();
            return imageFile;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri bitmap2uri(Bitmap bitmap, Activity activity) {
        return Uri.parse(MediaStore.Images.Media.insertImage(activity.getContentResolver(), bitmap, null, null));
    }

    public static Bitmap uri2bitmap(Uri uri, Activity activity) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static File uri2File(Uri uri){
        File file = null;
        try {
            file = new File(new URI(uri.toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 创建一个保存图片的文件
     * @return
     */
    public static File createImageFile() {
        //获取保存到的文件夹路劲
        File dir = new File(Environment.getExternalStorageDirectory() + "/DCIM");
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(dir, getPhotoPath());//
        return file;
    }

    /**
     * 保存文件的名字
     * @return
     */
    public static String getPhotoPath(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmmss");//格式大小写有区别
        String sysDatetime = fmt.format(Calendar.getInstance().getTime());//2016年02月25日  13:23:40
        String imageFileName = "JPEG_" + sysDatetime + ".jpg";
        return imageFileName;
    }

    /**
     * 创建一个文件用于存放拍摄的照片
     *
     * @return
     * @throws IOException
     */
    public static File createImageFile(String fileName,File storageDir) {
        if (!storageDir.exists())
            storageDir.mkdirs();
        File file = new File(storageDir, fileName);//localTempImgDir和localTempImageFileName是自己定义的名字
        return file;
    }

    /**
     * 检查权限是否配置
     */
    public static void checkPermissions(Activity context,int permissionCode){
            // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isHave = lacksPermissions(context,PERMISSIONS);
            if (isHave){
                // 如果没有授予该权限，就去提示用户请求
                showDialogTipUserRequestPermission(context,permissionCode);
            }else {
                Toast.makeText(context, "权限获取成功", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 提示用户该请求权限的弹出框
    private static void showDialogTipUserRequestPermission(final Activity context, final int permissionCode) {
        new AlertDialog.Builder(context)
                .setTitle("读取手机全限不可用")
                .setMessage("由于应用需要读取手机中的权限，为保持您的正常使用，请开启此权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startRequestPermission(context,permissionCode);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }

    // 判断权限集合（是否缺少）
    public static boolean lacksPermissions(Context context,String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(context,permission)) {
                return true;
            }
        }
        return false;
    }

    // 判断是否缺少权限
    private static boolean lacksPermission(Context context,String permission) {
        return ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    // 开始提交请求权限
    private static void startRequestPermission(Activity context,int permissionCode) {
        ActivityCompat.requestPermissions(context, PERMISSIONS, permissionCode);
    }

    public static void showDialogTipUserGoToAppSettting(final Activity context, final int flag) {
        new AlertDialog.Builder(context)
                .setTitle("读取手机状态不可用")
                .setMessage("请在-应用设置-权限-中，允许应用需要读取手机权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting(context,flag);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCancelable(false).show();
    }

    // 提示用户去应用设置界面手动开启权限

    // 跳转到当前应用的设置界面
    public static void goToAppSetting(Activity context, int flag) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivityForResult(intent, flag);
    }

    /**
     * 权限申请判断回调
     * @param content
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public static void callBackResPermission(Activity content,int requestCode, String[] permissions,int[] grantResults,int permissionCode){
        if (requestCode == permissionCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = content.shouldShowRequestPermissionRationale(permissions[0]);
                    Log.d("tag","111111111111111");
                    if (!b) {
                        Log.d("tag","22222222222222");
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSettting(content,SET_PERCODE);
                    } else{
                        Log.d("tag","22222222222222");
                        content.finish();
                    }

                } else {
                    Toast.makeText(content, "权限获取成功", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 转换 content:// uri
     *
     * @param imageFile
     * @return
     */
    public Uri getImageContentUri(Context context,File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
