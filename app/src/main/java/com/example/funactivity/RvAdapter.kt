package com.example.funactivity
import android.widget.TextView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
class RvAdapter (val context: Context, val userArrayList: ArrayList<User>)
    : RecyclerView.Adapter<RvAdapter.ViewHolder>(){
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val firstNameTV: TextView? = itemView.findViewById(R.id.idTVName)
        var teamNameTV: TextView? = itemView.findViewById(R.id.idTVTeamName)
        var stepsTV: TextView? = itemView.findViewById(R.id.idTVSteps)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.user_rv_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curUser = userArrayList[position]
        holder.firstNameTV?.setText(curUser.name)
        holder.teamNameTV?.setText(curUser.teamName)
        holder.stepsTV?.setText(curUser.steps)
    }

    override fun getItemCount(): Int {
        return userArrayList.size
    }

}