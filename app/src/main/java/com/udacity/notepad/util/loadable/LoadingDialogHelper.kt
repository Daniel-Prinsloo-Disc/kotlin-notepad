package com.udacity.notepad.util.loadable

import android.os.Build
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.udacity.notepad.R

class LoadingDialogHelper private constructor() {
    fun getInstance(): LoadingDialogHelper? {
        if (dialogHelper == null) {
            dialogHelper = LoadingDialogHelper()
        }
        return dialogHelper
    }

    fun showLoadingDialog(fragmentActivity: FragmentActivity) {
        showLoadingDialog(fragmentActivity, R.string.loading)
    }

    fun showLoadingDialog(fragmentActivity: FragmentActivity, titleId: Int) {
        showLoadingDialog(fragmentActivity, fragmentActivity.getString(titleId))
    }

    fun showLoadingDialog(
        fragmentActivity: FragmentActivity,
        message: String
    ) {
        showLoadingDialogWithCancelButton(fragmentActivity, message, "", false)
    }

    fun showLoadingDialogWithCancelButton(
        fragmentActivity: FragmentActivity,
        message: String,
        cancelButtonString: String?,
        goBackToPreviousPage: Boolean
    ) { //belt and braces to check if the Activity is still available
        if (isNotDestroed(fragmentActivity)) {
            val supportFragmentManager =
                fragmentActivity.supportFragmentManager
            //and check that the fragment manager is also not destroyed
            if (!supportFragmentManager.isDestroyed) {
                val previous =
                    supportFragmentManager.findFragmentByTag(PROGRESS_DIALOG_ID)
                if (null != previous) {
                    val transaction =
                        supportFragmentManager.beginTransaction()
                    Log.d(
                        "$TAG - Progress",
                        "Removing previous..."
                    )
                    transaction.remove(previous)
                    transaction.commitAllowingStateLoss()
                }
                val fragment = ProgressDialogFragment.newInstance(
                    message, cancelButtonString,
                    goBackToPreviousPage
                )
                /*
                 * Do not use show, rather use add and commitAllowingStateLoss to avoid the problem described at
                 * http://stackoverflow.com/questions/12105064/actions-in-onactivityresult-and-error-can-not-perform-this-action-after-onsavei
                 * Also see http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
                 */
                val transaction =
                    supportFragmentManager.beginTransaction()
                transaction.add(fragment, PROGRESS_DIALOG_ID)
                transaction.commitAllowingStateLoss()
                //make sure fragment details are committed now - if not and calls come back in here you can end up with
//a permanent dialog on the screen because the find above fails as the first commit has not yet been
//actioned
                supportFragmentManager.executePendingTransactions()
                Log.d(
                    "$TAG - Progress",
                    "Showed progress dialog"
                )
            }
        }
    }

    private fun isNotDestroed(fragmentActivity: FragmentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            !fragmentActivity.isDestroyed
        } else fragmentActivity.isChangingConfigurations || fragmentActivity.isFinishing
    }

    fun dismissLoadingDialog(fragmentActivity: FragmentActivity) {
        val supportFragmentManager =
            fragmentActivity.supportFragmentManager
        if (!supportFragmentManager.isDestroyed) {
            val dialog =
                supportFragmentManager.findFragmentByTag(PROGRESS_DIALOG_ID)
            if (null != dialog) {
                Log.d("$TAG - Progress", "Removing...")
                (dialog as DialogFragment).dismissAllowingStateLoss()
            }
        }
    }

    companion object {
        val TAG = LoadingDialogHelper::class.java.simpleName
        private const val PROGRESS_DIALOG_ID = "PROGRESS_DIALOG_ID"
        private var dialogHelper: LoadingDialogHelper? = null
        val instance: LoadingDialogHelper
            get() {
                if (dialogHelper == null) {
                    dialogHelper = LoadingDialogHelper()
                }
                return dialogHelper!!
            }
    }
}