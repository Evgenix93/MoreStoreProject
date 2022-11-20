package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCardBinding
import com.project.morestore.data.models.Card

class CardsAdapter(private val choose: (List<Card>) -> Unit, private val delete: (Card) -> Unit): RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {
    private var cards = listOf<Card>()
    private var loading = false

    fun updateCards(newCards: List<Card>){
       cards = newCards
       notifyItemRangeChanged(0, cards.lastIndex)

    }

    fun loading(loading: Boolean){
        this.loading = loading
        notifyItemRangeChanged(0, cards.lastIndex)
    }

   inner class CardViewHolder(view: View): RecyclerView.ViewHolder(view) {
       private val binding: ItemCardBinding by viewBinding()

       fun bind(card: Card){

          binding.cardNumberTextView.text = "**** ${card.number.takeLast(4)}"
          if(cards.find{ it.active == 1}?.id == card.id)
              binding.radioButtonImageView.setImageResource(R.drawable.ic_radiobutton)
           else
               binding.radioButtonImageView.setImageResource(R.drawable.ic_radiobutton_not_checked)

          binding.radioButtonImageView.setOnClickListener{
              if(loading)
                  return@setOnClickListener

                  cards.filter{it.active == 1}.forEach { it.active = 0 }
                  card.active = if(card.active == 0) 1 else 0
                  choose(cards)
          }
          binding.trashImageView.setOnClickListener{
              delete(card)
          }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(cards[position])

    }

    override fun getItemCount(): Int {
        return cards.size
    }
}