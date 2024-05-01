package com.ghulammustafa.smd_a2



import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MentorAdapter(private var mentorList: List<mentorcredentials> = ArrayList()) :
    RecyclerView.Adapter<MentorAdapter.MentorViewHolder>() {

    inner class MentorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val descriptionTextView: TextView = itemView.findViewById(R.id.description)
        val statusTextView: TextView = itemView.findViewById(R.id.status)
        val priceTextView: TextView = itemView.findViewById(R.id.price)
        val profilePhotoImageView: ImageView = itemView.findViewById(R.id.profilephoto)

        init {
            // Set click listener for the item view
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            // Get the position of the clicked item
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                // Retrieve the mentor at that position
                val mentor = mentorList[position]
                // Start BioPageActivity and pass mentor's information
                val intent = Intent(itemView.context, BioPageActivity::class.java)
                intent.putExtra("mentor", mentor)
                itemView.context.startActivity(intent)
            }
        }

        fun bind(mentor: mentorcredentials) {
            nameTextView.text = mentor.name
            descriptionTextView.text = mentor.description
            statusTextView.text = mentor.status
            priceTextView.text = mentor.price

            // Load profile picture using Glide library into profilephoto ImageView
            if (!mentor.profilePictureUri.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(mentor.profilePictureUri)
                    .into(profilePhotoImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MentorViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.mentor_row, parent, false)
        return MentorViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MentorViewHolder, position: Int) {
        val mentor = mentorList[position]
        holder.bind(mentor)
    }

    override fun getItemCount(): Int {
        return mentorList.size
    }

    fun setMentorList(mentors: List<mentorcredentials>) {
        mentorList = mentors
        notifyDataSetChanged()
    }
    fun setFilteredList(mentorList: MutableList<mentorcredentials>)
    {
        this.mentorList=mentorList
        notifyDataSetChanged()
    }
}
