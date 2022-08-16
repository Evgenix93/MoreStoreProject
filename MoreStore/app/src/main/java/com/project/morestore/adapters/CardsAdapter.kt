package com.project.morestore.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.project.morestore.R
import com.project.morestore.databinding.ItemCardBinding
import com.project.morestore.models.Card

class CardsAdapter(private val choose: (List<Card>) -> Unit, private val delete: (Card) -> Unit): RecyclerView.Adapter<CardsAdapter.CardViewHolder>() {
    private var cards = listOf<Card>()
    private var loading = false

    fun updateCards(newCards: List<Card>){
       cards = newCards
       notifyDataSetChanged()

    }

    fun loading(loading: Boolean){
        this.loading = loading
        notifyDataSetChanged()
    }

   inner class CardViewHolder(view: View): RecyclerView.ViewHolder(view) {
       private val binding: ItemCardBinding by viewBinding()

       fun bind(card: Card){

          binding.cardNumberTextView.text = "**** ${card.number.takeLast(4)}"
          if(card.active == 1)
              binding.radioButtonImageView.setImageResource(R.drawable.ic_radiobutton)
           else
               binding.radioButtonImageView.setImageResource(R.drawable.ic_radiobutton_not_checked)

          binding.radioButtonImageView.setOnClickListener{
              if(loading)
                  return@setOnClickListener
              if(card.active == 0) {
                  cards.find{it.active == 1}?.active = 0
                  cards.first{it.id == card.id}.active = 1
                  choose(cards)
              }
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