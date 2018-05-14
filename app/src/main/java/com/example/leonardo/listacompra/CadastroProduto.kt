package com.example.leonardo.listacompra

import android.app.Activity
import android.arch.persistence.room.Room
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.cadastro_produto.*
import kotlinx.android.synthetic.main.item_lista.*
import java.io.ByteArrayOutputStream
import java.io.File

class CadastroProduto: AppCompatActivity() {


    private val TAKE_PHOTO_REQUEST = 101
    private var mCurrentPhotoPath: String = ""

    private val USER_PROFILE_CAMERA_REQUEST_CODE = 9995
    private val USER_PROFILE_GALLERY_REQUEST_CODE = 9996

    private var imageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastro_produto)

        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "room-database"
        ).allowMainThreadQueries().build()



        var imv = findViewById(R.id.imageView_fotoProduto) as ImageView
        imv.setOnClickListener{

            Toast.makeText(this@CadastroProduto,"Teste Foto",Toast.LENGTH_LONG).show()

        }



        button_cadastrar.setOnClickListener {

            //Toast.makeText(this@CadastroProduto,"Salvar Produto",Toast.LENGTH_LONG).show()

            var nome : String = ""
            var preco : String = ""
            var descricao : String = ""
            if (editText_nome.text.toString().trim().length > 0) {
                nome = editText_nome.text.toString()
            } else {
                Toast.makeText(this@CadastroProduto, "Produto sem nome.", Toast.LENGTH_LONG).show()
                // editText_nome.error = "Escolha um nome para o produto"

            }
             preco = editText_preco.text.toString()
             descricao = editText_descricao.text.toString()

             val produto = Produto(nome = nome, preco = preco, descricao = descricao, imagem = "")

            // val produto = Produto(nome = "produto test1", preco = "123", descricao = "test description", imagem = "")

            db.romDao().insertProduto(produto)
            Toast.makeText(this@CadastroProduto, "Produto cadastrado com sucesso!", Toast.LENGTH_LONG).show()
            val intent = Intent(this@CadastroProduto,MainActivity::class.java)
            startActivity(intent)

        }


    }



    //----**************************


    private fun getProductImage() {
        val getImageFrom = AlertDialog.Builder(this)
        getImageFrom.setTitle("Foto Produto")
        val opsChars = arrayOf<CharSequence>("Câmera", "Galeria")
        getImageFrom.setItems(opsChars) { dialog, which ->
            if (which == 0) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.TITLE, "Foto Produto")
                values.put(MediaStore.Images.Media.DESCRIPTION, "Câmera")
                imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
                startActivityForResult(intent, USER_PROFILE_CAMERA_REQUEST_CODE)
            } else if (which == 1) {
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                pickIntent.type = "image/*"
                startActivityForResult(pickIntent, USER_PROFILE_GALLERY_REQUEST_CODE)
            }
            dialog.dismiss()
        }
        getImageFrom.show()
    }

    private fun convertImageToByte(uri: Uri): ByteArray {
        var data: ByteArray? = null

        val cr = baseContext.contentResolver
        val inputStream = cr.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val  baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        data = baos.toByteArray()
        return data
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == USER_PROFILE_CAMERA_REQUEST_CODE) {
                val arr = convertImageToByte(imageUri!!)
            } else if (requestCode == USER_PROFILE_GALLERY_REQUEST_CODE) {
                imageUri = data?.data
                val arr = convertImageToByte(imageUri!!)
            }
        }
    }



    //-------



    //---------------------
/*
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == TAKE_PHOTO_REQUEST) {
            processCapturedPhoto()
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                        launchCamera()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?,
                                                                    token: PermissionToken?) {
                        AlertDialog.Builder(this@MainActivity)
                                .setTitle(R.string.storage_permission_rationale_title)
                                .setMessage(R.string.storage_permition_rationale_message)
                                .setNegativeButton(android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({ token?.cancelPermissionRequest() })
                                .show()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Snackbar.make(mainContainer!!,
                                R.string.storage_permission_denied_message,
                                Snackbar.LENGTH_LONG)
                                .show()
                    }
                })
                .check()
    }

    private fun launchCamera() {
        val values = ContentValues(1)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        val fileUri = contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if(intent.resolveActivity(packageManager) != null) {
            mCurrentPhotoPath = fileUri.toString()
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            startActivityForResult(intent, TAKE_PHOTO_REQUEST)
        }
    }

    private fun processCapturedPhoto() {
        val cursor = contentResolver.query(Uri.parse(mCurrentPhotoPath),
                Array(1) {android.provider.MediaStore.Images.ImageColumns.DATA},
                null, null, null)
        cursor.moveToFirst()
        val photoPath = cursor.getString(0)
        cursor.close()
        val file = File(photoPath)
        val uri = Uri.fromFile(file)

        val height = resources.getDimensionPixelSize(R.dimen.photo_height)
        val width = resources.getDimensionPixelSize(R.dimen.photo_width)

        val request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(ResizeOptions(width, height))
                .build()
        val controller = Fresco.newDraweeControllerBuilder()
                .setOldController(imgvPhoto?.controller)
                .setImageRequest(request)
                .build()
        imgvPhoto?.controller = controller
    }*/

    //-------------------
}