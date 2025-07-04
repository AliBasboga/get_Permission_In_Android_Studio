package com.example.yemekkitabi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.example.yemekkitabi.databinding.FragmentListBinding
import com.example.yemekkitabi.databinding.FragmentTarifBinding
import com.google.android.material.snackbar.Snackbar


class TarifFragment : Fragment() {
    private var _binding: FragmentTarifBinding? = null
    private val binding get() = _binding!!
    private lateinit var premissonLancuher : ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private  var secilenGorsel : Uri?= null
    private  var secilenBitmap : Bitmap?= null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTarifBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.kaydetButton.setOnClickListener {

        }
        binding.silButton.setOnClickListener {

        }

        binding.imageView.setOnClickListener {
            // 33 >= büyükse
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)) {
                        Snackbar.make(view, "Galeriye erişim için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver",
                            View.OnClickListener{
                                premissonLancuher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        ).show()
                    }else{
                        premissonLancuher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                    }
                }else{
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
                /****/
            }else{
                //33 küçkse
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        Snackbar.make(view, "Galeriye erişim için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("İzin Ver",
                            View.OnClickListener{
                                premissonLancuher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        ).show()
                    }else{
                        premissonLancuher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }else{
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                }
            }
        }

        arguments?.let {
            val bilgi = TarifFragmentArgs.fromBundle(it).bilgi
            if (bilgi == "yeni") {
                ///yeni tarif eklenecek
                binding.silButton.isEnabled = false
                binding.kaydetButton.isEnabled = true
                binding.isimText.setText("")
                binding.malzemeText.setText("")
            }else{
                ///eski tarif gösterilecek
                binding.silButton.isEnabled = true
                binding.kaydetButton.isEnabled = false
            }
        }
        return view
    }

    fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null) {
                     secilenGorsel = intentFromResult.data
                    try {
                        if (Build.VERSION.SDK_INT >= 28) {
                            val source =  ImageDecoder.createSource(requireActivity().contentResolver,secilenGorsel!!)
                            secilenBitmap = ImageDecoder.decodeBitmap(source)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }else{
                            secilenBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver,secilenGorsel)
                            binding.imageView.setImageBitmap(secilenBitmap)
                        }
                    }catch (e: Exception) {
                       println(e.localizedMessage)
                    }
                }
            }
        }
        premissonLancuher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(requireContext(), "İzin gerekli", Toast.LENGTH_LONG).show()
                }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
