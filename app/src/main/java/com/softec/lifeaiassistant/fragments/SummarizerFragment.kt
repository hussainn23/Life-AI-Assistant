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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummarizerFragment(private val context: AppCompatActivity) :
    AppFragmentLoader(R.layout.layout_fragment_home) {

    private lateinit var binding: FragmentSummarizerBinding
    private lateinit var adapter: ChatsAdapter
    private val chatTextList = mutableListOf<ChatDataClass>()

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

    }

    private fun doSomeWork() {
        binding.editTextMessage.requestFocus()

        binding.editTextMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.buttonSend.isEnabled = !TextUtils.isEmpty(s?.toString()?.trim())
            }
        })



        adapter = ChatsAdapter(context, chatTextList)
        binding.rcvProgress.adapter = adapter
        binding.buttonSend.setOnClickListener {
            Log.e(TAG, "onCreate: Send btn Clicked")
            buttonSendChat()
        }
    }

    private fun buttonSendChat() {
        val promptText = binding.editTextMessage.text.toString().trim()
        binding.editTextMessage.text.clear()

        chatTextList.add(ChatDataClass(promptText, false))
        adapter.notifyItemInserted(chatTextList.size - 1)
        binding.rcvProgress.scrollToPosition(chatTextList.size - 1)
        //binding.progressIndicator.visibility = View.VISIBLE

        CoroutineScope(Dispatchers.Main).launch {
            val query =
                "You are a helpful assistant. Summarize the following text in the form of bullet points.\n" +
                        "\n" +
                        "Text: $promptText\n" +
                        "\n" +
                        "Summarize the key points and return them in a bullet point list.\n" +
                        "Respond only with the bullet points, no additional explanations."
            val chatResponseText = GetChatResponseText.getResponse(query)
            chatTextList.add(ChatDataClass(chatResponseText, true))
            adapter.notifyItemInserted(chatTextList.size - 1)
            binding.rcvProgress.scrollToPosition(chatTextList.size - 1)
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

