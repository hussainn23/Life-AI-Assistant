package com.softec.lifeaiassistant.fragments

import android.content.ContentValues
import android.content.Context
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.softec.lifeaiassistant.R
import com.softec.lifeaiassistant.customClasses.AppFragmentLoader
import com.softec.lifeaiassistant.customClasses.BoldFormatter
import com.softec.lifeaiassistant.databinding.FragmentSummarizerBinding
import com.softec.lifeaiassistant.geminiClasses.GetChatResponseText
import com.softec.lifeaiassistant.models.ChatDataClass
import com.softec.lifeaiassistant.utils.SharedPrefManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummarizerFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {

    private lateinit var binding: FragmentSummarizerBinding
    private lateinit var adapter: ChatsAdapter
    private var chatTextList = mutableListOf<ChatDataClass>()

    companion object {
        private const val TAG = "GPTChat"
    }


    override fun onCreate() {
        try {
            object : CountDownTimer(500, 500) {
                override fun onTick(l: Long) {
                }

                override fun onFinish() {
                    initiateLayout()
                }
            }.start()
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "initiateData: " + e.message)
        }
    }

    private fun initiateLayout() {
        settingUpBinding()
    }

    private fun settingUpBinding() {
        val base = find<FrameLayout>(R.id.main)
        base.removeAllViews()
        binding = FragmentSummarizerBinding.inflate(context.layoutInflater, base, true)
        binding.root.apply {
            alpha = (0f)
            translationY = 20f
            animate().translationY(0f).alpha(1f).setDuration(500)
                .setInterpolator(OvershootInterpolator()).start()
        }
        doSomeWork()
        binding.switchCheckList.setOnCheckedChangeListener({ _, isChecked ->
            if (isChecked) binding.toolbar.title = "Checklist Generator" else binding.toolbar.title = "Summarizer"
        })
    }

    private fun doSomeWork() {
        binding.editTextMessage.requestFocus()


        chatTextList =
            (SharedPrefManager(context).getChats()?.toList() ?: emptyList()).toMutableList()
        adapter = ChatsAdapter(context, chatTextList)
        binding.rcvProgress.adapter = adapter
        binding.buttonSend.setOnClickListener {
            Log.e(TAG, "onCreate: Send btn Clicked")
            buttonSendChat()
        }
    }

    private fun buttonSendChat() {
        val promptText = binding.editTextMessage.text.toString().trim()
        if (promptText.isEmpty()) {
            return
        }

        hideKeyboard()
        binding.editTextMessage.text.clear()

        chatTextList.add(ChatDataClass(promptText, false))
        adapter.notifyItemInserted(chatTextList.size - 1)
        binding.rcvProgress.scrollToPosition(chatTextList.size - 1)
        //binding.progressIndicator.visibility = View.VISIBLE
        val isGoalListing =  binding.switchCheckList.isChecked
        CoroutineScope(Dispatchers.Main).launch {

            val query = if (!isGoalListing)
                "You are a helpful assistant. Summarize the following text in the form of bullet points.\n" +
                        "\n" +
                        "Text: $promptText\n" +
                        "\n" +
                        "Summarize the key points and return them in a bullet point list.\n" +
                        "Respond only with the bullet points, no additional explanations."
            else "You are a helpful assistant. Based on the goal provided below, generate a checklist of necessary steps to accomplish it.\n" +
                    "\n" +
                    "Goal: $promptText\n" +
                    "\n" +
                    "List the steps as clear and actionable bullet points.\n" +
                    "Respond only with the bullet points, without any additional explanation."

            val chatResponseText = GetChatResponseText.getResponse(query)
            chatTextList.add(ChatDataClass(chatResponseText, true))
            adapter.notifyItemInserted(chatTextList.size - 1)
            binding.rcvProgress.scrollToPosition(chatTextList.size - 1)
            SharedPrefManager(context).saveChatList(chatTextList)
        }


    }

    fun hideKeyboard() {
        // Get the current focus view
        val view = context.currentFocus
        // Get the InputMethodManager system service
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        // If a view is focused, hide the keyboard
        if (view != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    class ChatsAdapter(
        private val context: Context,
        private var chatTextList: List<ChatDataClass>
    ) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = chatTextList[position]
            val formattedText = BoldFormatter.formatBold(item.text)
            holder.tvChat.text = formattedText

            val params = holder.tvChat.layoutParams as FrameLayout.LayoutParams
            if (item.isResponse) {
                holder.tvChat.setBackgroundResource(R.drawable.round_card_3sides)
                params.gravity = Gravity.START
            } else {
                holder.tvChat.setBackgroundResource(R.drawable.round_card_3sides_usr_chat)
                params.gravity = Gravity.END
            }
            holder.tvChat.layoutParams = params
        }

        override fun getItemCount(): Int = chatTextList.size

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvChat: TextView = itemView.findViewById(R.id.text)
            val base: FrameLayout = itemView.findViewById(R.id.base)
        }
    }
}

