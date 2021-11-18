package com.runle.intro.view

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.runle.intro.databinding.WelcomeFragmentBinding
import com.runle.intro.model.WelcomeModel
import com.runle.intro.util.convertLongArrayListToLongArray
import com.runle.intro.viewholder.StoriesProgressView

/**
 * Created by elha on 10.08.2021.
 */
class WelcomeFragment: Fragment(), StoriesProgressView.StoriesListener, WelcomeView {

    // content image
    private var contentImages = arrayListOf<String>()

    // logo image
    private var logoImages = arrayListOf<String>()

    //title
    private var titles = arrayListOf<String>()

    //description
    private var descriptions = arrayListOf<String>()

    private var counter = 0
    private val resources = arrayListOf<String>()

    private val durations = arrayListOf<Long>()

    var pressTime = 0L
    var limit = 500L

    lateinit var welcomeModels: List<WelcomeModel>

    var progressCount: Int  = 0


    private val onTouchListener: View.OnTouchListener = object : View.OnTouchListener {
        override fun onTouch(v: View?, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    pressTime = System.currentTimeMillis()
                    binding.stories.pause()
                    return false
                }
                MotionEvent.ACTION_UP -> {
                    val now = System.currentTimeMillis()
                    binding.stories.resume()
                    return limit < now - pressTime
                }
            }
            return false
        }
    }


    private val binding: WelcomeFragmentBinding by lazy {
        WelcomeFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding.getStarted.setOnClickListener {
            closeFragment()
        }

        animateBackground(binding.container)
        // iterate through welcome models
        iterateWelcomeModel()
        initStories()
        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        // bind reverse view
        binding.reverse.setOnClickListener { binding.stories.reverse() }
        binding.reverse.setOnTouchListener(onTouchListener)

        // bind skip view
        binding.skip.setOnClickListener { binding.stories.skip() }
        binding.skip.setOnTouchListener(onTouchListener)
    }

    private fun initStories() {
        binding.stories.setStoriesCount(progressCount); // <- set stories
        binding.stories.setStoriesCountWithDurations(convertLongArrayListToLongArray(durations)); // <- set a story duration
        binding.stories.setStoriesListener(this); // <- set listener
        binding.stories.startStories() // <- start progress

        setViewByCounter(0)
    }

    private fun closeFragment() {
        parentFragmentManager.beginTransaction().remove(this).commit()
    }

    private fun animateBackground(constraintLayout: ConstraintLayout) {
        val animationDrawable = constraintLayout.background as AnimationDrawable
        animationDrawable.setEnterFadeDuration(8000)
        animationDrawable.setExitFadeDuration(1600)
        animationDrawable.start()
    }

    private fun iterateWelcomeModel() {
        for (welcomeModel in welcomeModels) {
            contentImages.add(welcomeModel.contentImage)
            titles.add(welcomeModel.title)
            descriptions.add(welcomeModel.description)
            durations.add(welcomeModel.duration)
            logoImages.add(welcomeModel.logo)
        }
    }

    private fun setViewByCounter(counter: Int) {
        // last view
        binding.getStarted.visibility = if (counter + 1 == welcomeModels.size) View.VISIBLE else View.INVISIBLE

        binding.titleWelcome.text = titles[counter]
        binding.description.text = descriptions[counter]
        context?.let {
            Glide.with(it)
                .load(contentImages[counter])
                .into(binding.contentImage)
        }
    }


    override fun onComplete() {
        // start again
        counter = 0
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        setViewByCounter(--counter)
    }

    override fun onNext() {
        setViewByCounter(++counter)
    }

    override fun onDestroy() {
        binding.stories.destroy()
        super.onDestroy()
    }

}