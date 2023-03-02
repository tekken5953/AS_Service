package app.as_service.view.login

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import app.as_service.R
import app.as_service.util.MakeVibrator
import app.as_service.viewModel.SignUpViewModel
import com.google.android.material.textfield.TextInputLayout

class SignUpDialogBuilder(context: Activity) {
    private val mContext = context


    // 회원가입 다이얼로그
    fun build(signUpViewModel: SignUpViewModel) {
        val builder = AlertDialog.Builder(mContext)
        val dialogView: View = LayoutInflater.from(mContext)
            .inflate(R.layout.dialog_sign_up, null, false)
        builder.setView(dialogView)
        val alertDialog: AlertDialog = builder.create()
        val idInput: TextInputLayout = dialogView.findViewById(R.id.sign_up_email_input)
        val idEt: EditText = dialogView.findViewById(R.id.sign_up_email)
        val pwdEt: EditText = dialogView.findViewById(R.id.sign_up_pwd)
        val rePwdEt: EditText = dialogView.findViewById(R.id.sign_up_repwd)
        val rePwdInput: TextInputLayout = dialogView.findViewById(R.id.sign_up_repwd_input)
        val phoneInput: TextInputLayout = dialogView.findViewById(R.id.sign_up_phone_input)
        val phoneEt: EditText = dialogView.findViewById(R.id.sign_up_phone)
        val createBtn: Button = dialogView.findViewById(R.id.sign_up_ok)
        val cancelBtn: Button = dialogView.findViewById(R.id.sign_up_cancel)

        createBtn.setOnClickListener {
            if (idEt.text.isBlank() || phoneEt.text.isBlank() || pwdEt.text.isBlank() || rePwdEt.text.isBlank()) {
                Toast.makeText(mContext, mContext.getString(R.string.error_not_input), Toast.LENGTH_SHORT)
                    .show()
                MakeVibrator().run {
                    init(mContext)
                    make(300)
                }
            } else if (rePwdInput.error != null || phoneInput.error != null) {
                MakeVibrator().run {
                    init(mContext)
                    make(300)
                }
            } else if (!idEt.text.toString().contains("@")) {
                idInput.error = mContext.getString(R.string.error_email)
                MakeVibrator().run {
                    init(mContext)
                    make(300)
                }
            } else {
                idInput.error = null

                // 회원가입 뷰모델 호출
                signUpViewModel.loadSignUpResult(
                    idEt.text.toString(),
                    phoneEt.text.toString(),
                    pwdEt.text.toString()
                )
                alertDialog.dismiss()
            }
        }

        phoneEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (phoneEt.length() > 11)
                    phoneInput.error = mContext.getString(R.string.error_phone)
                else
                    phoneInput.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        rePwdEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (rePwdEt.text.toString() != pwdEt.text.toString())
                    rePwdInput.error = mContext.getString(R.string.error_pwd_matching)
                else
                    rePwdInput.error = null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        cancelBtn.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
    }


}