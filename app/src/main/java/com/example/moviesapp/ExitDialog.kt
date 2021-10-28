package com.example.moviesapp

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlin.math.log
import kotlin.system.exitProcess

class ExitDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setView(R.layout.dialog_exit)
            .setNegativeButton(getString(R.string.exit_text)) { dialog, which ->
                exitProcess(-1)
            }
            .setPositiveButton(getString(R.string.stay_text)) { dialog, which ->
                dismiss()
            }
            .create()
    }
}