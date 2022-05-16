package com.project.morestore.fragments

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recording
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.FragmentMakePhotoBinding
import com.project.morestore.mvpviews.PhotoVideoMvpView
import com.project.morestore.presenters.PhotoVideoPresenter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import java.io.File
import java.text.SimpleDateFormat

class MakePhotoFragment : MvpAppCompatFragment(R.layout.fragment_make_photo), PhotoVideoMvpView {
    private val binding: FragmentMakePhotoBinding by viewBinding()
    private val presenter by moxyPresenter { PhotoVideoPresenter(requireContext()) }
    private val args: MakePhotoFragmentArgs by navArgs()
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>
    private var imageCapture: ImageCapture? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar()
        initPermissionLauncher()
        initFilePickerLauncher()
        requestPermission()
        setClickListeners()

    }

    private fun initToolbar() {
        binding.backIconImageView.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initPermissionLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    startCamera()
                } else {
                    Toast.makeText(requireContext(), "нет разрешения", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun initFilePickerLauncher() {
        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if(uri == null) return@registerForActivityResult
                findNavController().navigate(
                    MakePhotoFragmentDirections.actionMakePhotoFragmentToPhotoFinishFragment(
                        uri.toString(),
                        false,
                        args.position
                    )
                )


            }
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

            imageCapture = ImageCapture.Builder().build()

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e("error", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun requestPermission() {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun setClickListeners() {
        binding.takePhotoBtn.setOnClickListener {
            val imageCapture = imageCapture ?: return@setOnClickListener
            presenter.takePhoto(imageCapture)

        }
        binding.imageIcon.setOnClickListener {
            filePickerLauncher.launch(arrayOf("image/*"))
        }
    }

    override fun onPhotoCaptured(file: File) {
      //  Toast.makeText(requireContext(), "фото сделано ${file.absolutePath}", Toast.LENGTH_SHORT)
        //    .show()
        findNavController().navigate(
            MakePhotoFragmentDirections.actionMakePhotoFragmentToPhotoFinishFragment(
                file.path,
                false,
                args.position
            )
        )

    }

    override fun videoStarted(recording: Recording) {

    }

    override fun videoEnded(file: File) {

    }

    override fun error() {

    }

    override fun videoError() {

    }


}