package com.example.nuevo_parchaosr.activities.user

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.nuevo_parchaosr.activities.BasicFragment
import com.example.nuevo_parchaosr.activities.acces.LoginActivity
import com.example.nuevo_parchaosr.databinding.FragmentUserBinding
import com.example.nuevo_parchaosr.services.FireBaseService
import com.example.nuevo_parchaosr.utils.PermissionHelper
import com.example.nuevo_parchaosr.utils.dialogs.AlertUtils
import com.google.firebase.auth.FirebaseAuth
import lombok.Getter

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@Getter
class UserFragment : BasicFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var binding: FragmentUserBinding
    private var isEditable = false

    var auth = FirebaseAuth.getInstance()
    private val fireBaseService = FireBaseService()
    var permissionHelper = PermissionHelper()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(inflater)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fireBaseService.obtenerUsuarioPorId(auth.currentUser!!.uid) { usuario ->

        }
        binding.CerrarCuenta.setOnClickListener {
            val positiveListener = DialogInterface.OnClickListener { dialog, which ->
                FirebaseAuth.getInstance().signOut();
                val intent = Intent(activity, LoginActivity::class.java)
                startActivity(intent)
            }
            val negativeListener = DialogInterface.OnClickListener { dialog, which ->
                Log.e("Negativo", "Usuario continua en linea")
            }
            AlertUtils.showMaterialCloseSessionDialog(
                requireContext(),
                positiveListener,
                negativeListener
            )
        }

        binding.EditarCuentaButton.setOnClickListener {
            if (isEditable) {

            } else {

            }
            isEditable = !isEditable


        }

    }

}

