package com.lugares.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lugares.R
import com.lugares.databinding.FragmentUpdateLugarBinding
import com.lugares.model.Lugar
import com.lugares.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {

    private lateinit var lugarViewModel: LugarViewModel

    //Obtiene los argumentos que le pasaron al fragmento...
    private val args by navArgs<UpdateLugarFragmentArgs>()


    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lugarViewModel = ViewModelProvider(this)[LugarViewModel::class.java]
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        //Se toma la informacion del lugar y se coloca en la pantalla...
        binding.etNombre.setText(args.lugar.nombre)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etWeb.setText(args.lugar.web)
        binding.tvLatitud.setText(args.lugar.latitud.toString())
        binding.tvLongitud.setText(args.lugar.longitud.toString())
        binding.tvAltura.setText(args.lugar.altura.toString())

        binding.btActualizar.setOnClickListener { updateLugar() }
        //Progreamar el llamado al recurso correspondiente
        binding.btEmail.setOnClickListener{enviarCorreo()}
        binding.btPhone.setOnClickListener{hacerLlamada()}
        binding.btWhatsapp.setOnClickListener{enviarWhatsApp()}
        binding.btWeb.setOnClickListener{verWeb()}
        binding.btLocation.setOnClickListener{verMapa()}

        setHasOptionsMenu(true)
        return binding.root
    }

    //Esta funcion contrye los valores de un correo y llama al recurso de correo instalado en el celular
    private fun enviarCorreo() {
        val destinatario = binding.etCorreo.text.toString() // Se extrae el correo
        if (destinatario.isNotEmpty()) {   //Se puede usar el recurso
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"  //Para indicar que es un correo electronico
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(destinatario)) //Arreglo de correos

            //Abajo es para poner el asunto en el correo
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos) + " " + binding.etNombre.text
            )

            //A continuacion el cuerpo del mensaje de correo
            intent.putExtra(
                Intent.EXTRA_TEXT,
                getString(R.string.msg_mensaje_correo)
            )

            //Se intenta enviar el correo
            startActivity(intent)


        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_LONG
            ).show()
        }
    }

    //Esta funcion valida permisos, solicita permisos y construye los valores para hacer una llamada al lugar
    private fun hacerLlamada() {
        val telefono = binding.etTelefono.text.toString() // Se extrae el telefono
        if (telefono.isNotEmpty()) {   //Se puede usar el recurso
            val intent = Intent(Intent.ACTION_SEND) //Es una llamada lo que se hará
            intent.data = Uri.parse("tel:$telefono")  //Para indicar que es un correo electronico
            if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED
            ) {


            //Se piden los permisos al usuario
            requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 105)

        } else { //Tenemos permiso de hacer llamada
            //Se realiza la llamada
            requireActivity().startActivity(intent) //Finalmente hace la llamda
        }
    }else
    { //Se muestra un mensaje que falta info...
        Toast.makeText(
            requireContext(),
            getString(R.string.msg_datos), Toast.LENGTH_LONG
        ).show()
    }
}

    //Esta funcion envia un mensaje de WhatsApp si se tiene la aplicacion instalada
    private fun enviarWhatsApp() {
        val telefono = binding.etTelefono.text.toString() // Se extrae el telefono
        if (telefono.isNotEmpty()) {   //Se puede usar el recurso
            val intent = Intent(Intent.ACTION_VIEW) //abre aplicacion
            val uri = "whatsapp://send?phone=506$telefono&text="+
                    getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)  //
            startActivity(intent) //envia el mensaje de whatsapp

        }else
        { //Se muestra un mensaje que falta info...
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }

    //Esta funcion abre un google map
    private fun verWeb() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()

        if (latitud.isFinite() && longitud.isFinite()) {   //Se puede usar el recurso
            val location = Uri.parse("geo://$latitud,$longitud?z=18")
            val intent = Intent(Intent.ACTION_VIEW,location)  // Se abrira Google maps
            startActivity(intent) //envia el mensaje de whatsapp
        }else { //Se muestra un mensaje que falta info...
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }

    //Esta funcion envia un mensaje de WhatsApp si se tiene la aplicacion instalada
    private fun verMapa() {
        val web = binding.etWeb.text.toString() // Se extrae el telefono
        if (web.isNotEmpty()) {   //Se puede usar el recurso
            val webpage = Uri.parse("https://$web")
            val intent = Intent(Intent.ACTION_VIEW,webpage)  // Se abrira un navegador con el sitio web
            startActivity(intent) //envia el mensaje de whatsapp
        }else { //Se muestra un mensaje que falta info...
            Toast.makeText(requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId ==R.id.menu_delete){
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()
        val altura = binding.tvAltura.text.toString().toDouble()
        val lugar = Lugar(args.lugar.id,nombre,correo,telefono,web,latitud, longitud, altura,"","")
        lugarViewModel.updateLugar(lugar)
        Toast.makeText(requireContext(),getString(R.string.msg_actualizado),Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)

    }

    private fun deleteLugar(){
        var builder= AlertDialog.Builder(requireContext())
        builder.setPositiveButton(getString(R.string.si)) {_,_ ->
            lugarViewModel.deleteLugar(args.lugar)
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }
        builder.setNegativeButton(getString(R.string.no)){_,_, ->}
        builder.setTitle(R.string.menu_delete)
        builder.setMessage(getString(R.string.msg_seguro_borrar)+" ${args.lugar.nombre}?")
        builder.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}