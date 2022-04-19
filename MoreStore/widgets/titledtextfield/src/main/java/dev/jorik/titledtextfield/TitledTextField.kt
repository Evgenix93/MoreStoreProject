package dev.jorik.titledtextfield

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.addTextChangedListener

//todo fix save state on multiply instances on layout
class TitledTextField :LinearLayout {
    init{
        LayoutInflater.from(context).inflate(R.layout.widget_titledtextfield, this)
        orientation = VERTICAL
    }
    constructor(context: Context) :super(context)
    constructor(context :Context, attributes :AttributeSet) :super(context, attributes){
        val attrs = context.obtainStyledAttributes(attributes, R.styleable.TitledTextField)
        val titleLine = attrs.getString(R.styleable.TitledTextField_title)
        val hintLine = attrs.getString(R.styleable.TitledTextField_android_hint)
        val inputType = attrs.getInt(R.styleable.TitledTextField_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
        attrs.recycle()
        title.text = titleLine
        field.hint = hintLine
        field.inputType = inputType
    }
    val title :TextView = findViewById(R.id.title)
    val field :EditText = findViewById<EditText>(R.id.field).apply{
        addTextChangedListener {
            setBackgroundResource(
                if(it.isNullOrEmpty()) R.drawable.bg_rect_bordlight_round4
                else R.drawable.bg_rect_borddark_round4
            )
        }
    }
}