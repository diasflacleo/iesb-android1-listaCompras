package com.example.leonardo.listacompra

import android.arch.persistence.room.*
import java.io.FileDescriptor

@Entity(tableName = "produto")
data class Produto (

    @PrimaryKey(autoGenerate = true) var uid: Int = 0,
    @ColumnInfo(name = "produto_nome") var nome: String = "",
    @ColumnInfo(name = "produto_preco") var preco: String = "",
    @ColumnInfo(name = "produto_descricao") var descricao: String = "",
    @ColumnInfo(name = "produto_imagem") var imagem: String = ""
    )
 /*   var nome : String? = null
    var preco : Double? = null
    var descricao : String? = null
    //var imagem : String = ""


    constructor(nome: String, preco: Double, descricao: String){
            this.nome = nome
            this.preco = preco
            this.descricao = descricao
    }*/

    /*fun Produto(_nome: String, _descricao: String) {
        val nome: String = _nome
        get() = nome

        var descricao: String = _descricao
        get() = descricao
        set(value) {
            field = value
        }



    }*/


//------------------------------------------

    /*CONVERTE BASE64

    You can use the Base64 Android class:

            String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
    You'll have to convert your image into a byte array though. Here's an example:

    Bitmap bm = BitmapFactory.decodeFile("/path/to/image.jpg");
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
    byte[] b = baos.toByteArray();
    * Update *

    If you're using an older SDK library (because you want it to work on phones with older versions of the OS) you won't have the Base64 class packaged in (since it just came out in API level 8 aka version 2.2).

    Check this article out for a work-around:

    http://androidcodemonkey.blogspot.com/2010/03/how-to-base64-encode-decode-android.html

    -----------------------------------------------------------------------------------------

    PHOTO NO ANDROID

    https://developer.android.com/training/camera/photobasics.html


    https://stackoverflow.com/questions/5991319/capture-image-from-camera-and-display-in-activity*/



    //--------------------------------



@Dao
interface RoomDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduto(prod: Produto)

    @Update
    fun updateProduto(prod: Produto)

    @Delete
    fun deleteProduto(prod: Produto)

    @Query("SELECT * FROM produto")
    fun produtos(): Array<Produto>

}




@Database(entities = [(Produto::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun romDao(): RoomDAO
}