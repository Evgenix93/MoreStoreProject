package com.project.morestore.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentMakePhotoBinding
import com.project.morestore.mvpviews.PhotoVideoMvpView
import com.project.morestore.presenters.PhotoVideoPresenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File

class MakeVideoFragment : MvpAppCompatFragment(R.layout.fragment_make_photo), PhotoVideoMvpView {
    private val binding: FragmentMakePhotoBinding by viewBinding()
    private val presenter by moxyPresenter { PhotoVideoPresenter(requireContext()) }
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private var imageCapture: ImageCapture? = null
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>
    private var counterJob: Job? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setClickListeners()
        initPermissionsLauncher()
        initFilePickerLauncher()
        requestPermissions()


    }

    private fun initViews() {
        binding.videoInfoFrame.isVisible = true

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            videoCapture = VideoCapture.withOutput(recorder)
            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider
                    .bindToLifecycle(this, cameraSelector, preview, videoCapture, imageCapture)
            } catch (exc: Exception) {
                Log.e("mylog", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }


    private fun setClickListeners() {
        binding.takePhotoBtn.setOnTouchListener {  _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    val videoCapture = videoCapture ?: return@setOnTouchListener true
                    Log.d("mylog", "key down")
                    presenter.photoVideoBtnPressed(videoCapture)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val imageCapture = imageCapture ?: return@setOnTouchListener true
                    Log.d("mylog", "key up")
                    recording?.stop()
                    recording = null
                    presenter.photoVideoBtnReleased(imageCapture)
                    true
                }
                else -> {true}

            }
        }

        binding.imageIcon.setOnClickListener {
            filePickerLauncher.launch(arrayOf("video/mp4"))
        }
    }

    private fun initPermissionsLauncher() {
        permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                if (permissions.all { it.value }) {
                    startCamera()
                }

            }
    }

    private fun initFilePickerLauncher() {
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                findNavController().navigate(
                    MakeVideoFragmentDirections.actionMakeVideoFragmentToPhotoFinishFragment(
                        uri.toString(),
                        true
                    )
                )

            }
    }

    private fun requestPermissions() {
        permissionsLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            )
        )
    }

    private fun initTimer(){
        counterJob?.cancel()
        binding.takePhotoBtn.setImageResource(R.drawable.ic_photo_btn_pressed)
        binding.loader.isVisible = true
        counterJob = lifecycleScope.launch {
            for (i in 0 until 30){
                delay(1000)
                binding.loader.progress++
            }
            recording?.stop()
            recording = null

        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        counterJob?.cancel()
    }






    override fun onPhotoCaptured(file: File) {
        Toast.makeText(requireContext(), "фото сделано", Toast.LENGTH_SHORT).show()
        findNavController().navigate(MakeVideoFragmentDirections.actionMakeVideoFragmentToPhotoFinishFragment(file.path, false))

    }

    override fun videoStarted(recording: Recording) {
        this.recording = recording
        initTimer()
        Toast.makeText(requireContext(), "видео начато", Toast.LENGTH_SHORT).show()

    }

    override fun videoEnded(file: File) {
        counterJob?.cancel()
        binding.takePhotoBtn.setImageResource(R.drawable.ic_photo_btn)
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), "видео закончено", Toast.LENGTH_SHORT).show()
        findNavController().navigate(
            MakeVideoFragmentDirections.actionMakeVideoFragmentToPhotoFinishFragment(
                file.path,
                true
            )
        )

    }

    override fun error() {

    }

    override fun videoError() {
        recording?.close()
        recording = null
        binding.takePhotoBtn.setImageResource(R.drawable.ic_photo_btn)
        binding.loader.progress = 0
        binding.loader.isVisible = false
        Toast.makeText(requireContext(), "ошибка видео", Toast.LENGTH_SHORT).show()


    }


}
