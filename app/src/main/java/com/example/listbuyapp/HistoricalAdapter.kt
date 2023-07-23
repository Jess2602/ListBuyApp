package com.example.listbuyapp

import android.app.ProgressDialog
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.listbuyapp.data.History
import com.example.listbuyapp.data.HistoryViewModel
import com.example.listbuyapp.databinding.HistoricalFragmentLayoutBinding
import com.google.android.material.snackbar.Snackbar


class HistoricalAdapter(var historicalList: List<History>) :
    RecyclerView.Adapter<HistoricalAdapter.HistoricalViewHolder>() {
    private lateinit var mHistoryViewModel: HistoryViewModel

    class HistoricalViewHolder(val binding: HistoricalFragmentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoricalAdapter.HistoricalViewHolder {
        val binding = HistoricalFragmentLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoricalViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return historicalList.size
    }

    override fun onBindViewHolder(holder: HistoricalAdapter.HistoricalViewHolder, position: Int) {
        val currentHistoricalList = historicalList[position]
        val binding = holder.binding
        binding.deleteListHistorical.setOnClickListener {
            val activity = it.context as AppCompatActivity
            val historicalListId = currentHistoricalList.id
            val alertDialogBuilder = android.app.AlertDialog.Builder(activity)
            alertDialogBuilder.setTitle("Desea Eliminar la Lista?")
                .setMessage("¿Estás seguro que deseas eliminar la lista del Historial?")
                .setPositiveButton("Eliminar") { dialog, _ ->
                    val progressDialog = ProgressDialog(activity)
                    progressDialog.setMessage("Eliminando Lista del History..")
                    progressDialog.setCancelable(false)
                    progressDialog.show()
                    progressDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)

                    mHistoryViewModel = ViewModelProvider(activity)[HistoryViewModel::class.java]
                    mHistoryViewModel.deleteHistoryById(historicalListId)



                    val rootView = activity.findViewById<View>(android.R.id.content)
                    val snackbar = Snackbar.make(
                        rootView,
                        Html.fromHtml("<b>Se Elimino ${currentHistoricalList.listName_history.toString()}</b>"),
                        Snackbar.LENGTH_LONG
                    )

                    snackbar.setTextColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.white
                        )
                    )
                    snackbar.setBackgroundTint(
                        ContextCompat.getColor(
                            activity,
                            R.color.error
                        )
                    )

                    val drawableFondo = ContextCompat.getDrawable(
                        activity,
                        R.drawable.background_dialog
                    )
                    snackbar.view.background = drawableFondo
                    snackbar.view.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    dialog.dismiss()
                    progressDialog.dismiss()
                    snackbar.show()
                }
                .setNegativeButton("Cancelar", null)
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()

            val positiveButton =
                alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(ContextCompat.getColor(activity, R.color.botonbase))
            val negativeButton =
                alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(ContextCompat.getColor(activity, R.color.botonbase))

            alertDialog.window?.setBackgroundDrawableResource(R.drawable.background_dialog)
        }

        binding.textNameListHistorical.text = currentHistoricalList.listName_history
        binding.textViewCategoryHistorical.text = currentHistoricalList.listCategory_history
        binding.textViewDateHistorical.text = currentHistoricalList.listDate_history
    }

    fun setData(history: List<History>) {
        this.historicalList = history
        notifyDataSetChanged()
    }
}