package com.start3a.memoji.views.EditMemo.Image

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.start3a.memoji.R
import com.start3a.memoji.viewmodel.ImageDetailViewModel
import kotlinx.android.synthetic.main.activity_image_view.*

class ImageViewActivity : AppCompatActivity() {

    private var viewModel: ImageDetailViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)
        supportActionBar?.hide()

        viewModel = application!!.let {
            ViewModelProvider(viewModelStore, ViewModelProvider.AndroidViewModelFactory(it))
                .get(ImageDetailViewModel::class.java)
        }
        viewModel!!.let { vm ->
            vm.imageUri = intent.getStringExtra("imageUri") ?: ""
            vm.imageOriginalPath = intent.getStringExtra("imageOriginal") ?: ""

            // 이미지 출력
            Glide.with(this)
                .load(vm.imageUri)
                .error(
                    // 원본 이미지가 제거되어있을 경우
                    AlternativeImage(vm.imageOriginalPath)
                )
                .into(photo_view)
        }
    }

    // 이미지 출력 에러 처리
    private fun AlternativeImage(imagePath: String) =
        Glide.with(this)
            .load(imagePath)
            .error(R.drawable.icon_error)


    override fun onResume() {
        super.onResume()
        // 액티비티로 돌아올 때마다 상태 바 가리기
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}
