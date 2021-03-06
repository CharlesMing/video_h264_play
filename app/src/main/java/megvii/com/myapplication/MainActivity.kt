package megvii.com.myapplication

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import android.os.Build
import android.view.WindowManager
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    // 本地h264格式文件临时变量
    // private val h264Path = "/mnt/sdcard/test.h264"
    // private val h264Path = "/mnt/sdcard/720pq.h264"
    // private val h264Path = "/mnt/sdcard/3min_1080p.h264"
    // private val h264Path = "/mnt/sdcard/1080p.h264"
    private val h264Path = "/mnt/sdcard/video_file.h264"
    // 硬解码工具类
    var hardMediaDecodeUtil: HardMediaDecodeUtil? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 屏幕常亮
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // Example of a call to a native method
        sample_text.text = stringFromJNI()

        // 权限请求播放视频
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissionExternalR = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            val permissionExternalW = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (permissionExternalR && permissionExternalW) {
                if (hardMediaDecodeUtil == null)  // 初始化解码对象
                    hardMediaDecodeUtil = HardMediaDecodeUtil(h264Path, sv_surface_view, true)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    300
                )
            }
        } else {
            if (hardMediaDecodeUtil == null)  // 初始化解码对象
                hardMediaDecodeUtil = HardMediaDecodeUtil(h264Path, sv_surface_view, true)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 300) {
            val permissionExternalR = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            val permissionExternalW = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            if (permissionExternalR && permissionExternalW) {
                // 初始化解码对象
                hardMediaDecodeUtil = HardMediaDecodeUtil(h264Path, sv_surface_view, true)
            }
        } else {
            Toast.makeText(this, "播放本地文件需要您的授权", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }
}
