package com.project.morestore.fragments

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.project.morestore.R
import com.project.morestore.adapters.FeedbackPhotosAdapter
import com.project.morestore.databinding.FragmentFeedbackPhotosBinding
import com.project.morestore.dialogs.FeedbackCompleteDialog
import com.project.morestore.models.FeedbackItem
import com.project.morestore.mvpviews.FeedbackPhotoView
import com.project.morestore.presenters.FeedbackPhotoPresenter
import com.project.morestore.singletones.ChatMedia
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FeedbackPhotoFragment :MvpAppCompatFragment(), FeedbackPhotoView{
    private lateinit var views :FragmentFeedbackPhotosBinding
    private val presenter by moxyPresenter {
        val args = requireArguments()
        FeedbackPhotoPresenter(
            requireActivity().applicationContext,
            args.getLong(FeedbackProductFragment.PRODUCT_ID),
            args.getByte(RatingFragment.RATE, 5),
            args.getString(FeedbackFragment.FEEDBACK, "")
        )
    }
    private val adapter = FeedbackPhotosAdapter{
        if(it !is FeedbackItem.AddPhoto) return@FeedbackPhotosAdapter
        if(args.isChat)
          filePicker.launch(arrayOf("image/*", "video/mp4"))
        else
           pickPhoto()
    }
    private val photoPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode != Activity.RESULT_OK || it.data == null) return@registerForActivityResult
        presenter.addPhoto(listOf(it.data!!.data!!))
    }
    private val args: FeedbackPhotoFragmentArgs by navArgs()
    private lateinit var filePicker: ActivityResultLauncher<Array<String>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentFeedbackPhotosBinding.inflate(inflater).also { views = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        views.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        views.photos.adapter = adapter
        adapter.setItems(listOf(FeedbackItem.AddPhoto, FeedbackItem.Description))
        views.send.setOnClickListener {
            views.progressBar.isVisible = true
            if(args.isChat)
             saveMediaUris(adapter.getItems())
            else
               presenter.createFeedback()
        }
        initFilePicker()
        initActionButton()
        presenter.addPhoto(ChatMedia.mediaUris.orEmpty())
    }

    //IMPLEMENTATION
    override fun showPhotos(items: List<FeedbackItem>) = adapter.setItems(items)

    override fun changeSendText() {
        if(args.isChat.not())
        views.send.setText(R.string.feedback_photos_send)
    }

    override fun showSuccess(review :Boolean) {
        views.progressBar.isVisible = false
        FeedbackCompleteDialog(requireContext(), review){
            findNavController().popBackStack(R.id.sellerProfileFragment, false)
        }.show()
    }

    override fun mediaUrisSaved() {
        findNavController().popBackStack()
    }

    //PRIVATE
    private fun pickPhoto(){
        Intent().apply {
            type = "image/*"
            action = ACTION_GET_CONTENT
        }
            .also { photoPickerLauncher.launch(it) }
    }

    private fun initFilePicker(){
        filePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()){ uri ->
           presenter.addPhoto(listOf(uri))
        }
    }

    private fun saveMediaUris(items: List<FeedbackItem>){
        val feedbackItemsPhoto = items.filterIsInstance<FeedbackItem.Photo>()
        presenter.saveMediaUris(feedbackItemsPhoto.map{it.photo})
    }

    private fun initActionButton(){
        if(args.isChat)
            views.send.text = "Добавить"
    }
}