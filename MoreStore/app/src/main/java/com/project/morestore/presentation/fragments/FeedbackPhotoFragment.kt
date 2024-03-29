package com.project.morestore.presentation.fragments

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
import com.project.morestore.presentation.adapters.FeedbackPhotosAdapter
import com.project.morestore.databinding.FragmentFeedbackPhotosBinding
import com.project.morestore.presentation.dialogs.FeedbackCompleteDialog
import com.project.morestore.data.models.FeedbackItem
import com.project.morestore.presentation.mvpviews.FeedbackPhotoView
import com.project.morestore.domain.presenters.FeedbackPhotoPresenter
import com.project.morestore.data.singletones.ChatMedia
import dagger.hilt.android.AndroidEntryPoint
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject

@AndroidEntryPoint
class FeedbackPhotoFragment :MvpAppCompatFragment(), FeedbackPhotoView{
    private lateinit var views :FragmentFeedbackPhotosBinding
    @Inject lateinit var feedbackPhotoPresenter: FeedbackPhotoPresenter

    private val presenter by moxyPresenter {
       feedbackPhotoPresenter
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
        presenter.addPhoto(listOf(it.data!!.data!!), false)
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
        if(args.isChat)
            views.toolbarTitleTextView.text = getString(R.string.addPhotoVideo)
        views.photos.adapter = adapter
        adapter.setItems(listOf(FeedbackItem.AddPhoto(args.isChat), FeedbackItem.Description(args.isChat)))
        views.send.setOnClickListener {
            views.progressBar.isVisible = true
            if(args.isChat)
             saveMediaUris(adapter.getItems())
            else
               presenter.createFeedback(
                   requireArguments().getLong(FeedbackProductFragment.PRODUCT_ID),
                   requireArguments().getByte(RatingFragment.RATE, 5),
                   requireArguments().getString(FeedbackFragment.FEEDBACK, "")
               )
        }
        initFilePicker()
        initActionButton()
        if(args.isChat && ChatMedia.mediaUris.isNullOrEmpty().not())
        presenter.addPhoto(ChatMedia.mediaUris!!, args.isChat)
    }

    //IMPLEMENTATION
    override fun showPhotos(items: List<FeedbackItem>) = adapter.setItems(items)

    override fun changeSendText() {
        if(args.isChat.not())
        views.send.setText(R.string.feedback_photos_send)
    }

    override fun showSuccess(review :Boolean) {
        views.progressBar.isVisible = false
        val title = if(review) getString(R.string.feedback_complete_title) else getString(R.string.feedback_on_moderation)
        FeedbackCompleteDialog(requireContext(), {
            findNavController().popBackStack(R.id.sellerProfileFragment, false)
        }, title = title).show()
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
            uri ?: return@registerForActivityResult
           presenter.addPhoto(listOf(uri), true)
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