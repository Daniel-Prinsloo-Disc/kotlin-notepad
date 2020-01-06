package com.udacity.notepad.util.loadable

import android.R
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ProgressDialogFragment : DialogFragment() {
    private var loadingMessage = ""
    private var cancelButtonString: String? = ""
    private var goBackToPreviousPage = false
    override fun onCancel(dialogInterface: DialogInterface) { //go back to the calling activity
        if (activity != null) {
            dismissLoadingDialog()
            activity!!.onBackPressed()
        }
    }

    protected fun dismissLoadingDialog() {
        if (activity is Loadable) {
            (activity as Loadable?)!!.dismissLoadingDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val loadingDialog = ProgressDialog(activity)
        loadingDialog.setTitle("")
        loadingDialog.setMessage(loadingMessage)
        loadingDialog.setCanceledOnTouchOutside(false)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT
            && loadingDialog.window != null
        ) {
            loadingDialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        }
        //cancel button
        if (cancelButtonString != null && cancelButtonString!!.trim { it <= ' ' } != "") {
            loadingDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                cancelButtonString
            ) { dialog, which ->
                if (activity != null) {
                    dismissLoadingDialog()
                    if (goBackToPreviousPage) {
                        activity!!.onBackPressed()
                    }
                }
            }
        }
        return loadingDialog
    }

    companion object {
        fun newInstance(
            message: String,
            cancelButtonString: String?,
            goBackToPreviousPage: Boolean
        ): ProgressDialogFragment {
            val dialogFragment = ProgressDialogFragment()
            dialogFragment.loadingMessage = message
            dialogFragment.cancelButtonString = cancelButtonString
            dialogFragment.goBackToPreviousPage = goBackToPreviousPage
            return dialogFragment
        }
    }
}