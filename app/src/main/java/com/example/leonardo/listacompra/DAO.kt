package com.example.leonardo.listacompra

/*
*
*
@Entity(tableName = "produtos")
data class Produto (
	@PrimaryKey(autoGenerate = true) var uid: Int = 0,
	@ColumnInfo(name = "prod_nome") var  nome: String = "",
	@ColumnInfo(name = "prod_descricao") var  descricao: String = "",
	@ColumnInfo(name = "prod_preco") var  preco: Double = 0.0,
	@ColumnInfo(name = "prod_imagem") var  imagem: String = ""
)

@Entity(tableName = "lista_compras")
data class Lista (
	@PrimaryKey(autoGenerate = true) var uid: Int = 0,
	@ColumnInfo(name = "list_nome") var  nome: String = "",
	@ColumnInfo(name = "list_prod_id") var  prod_id: Int = 0,
	@ColumnInfo(name = "list_preco") var  preco: Double = 0.0,

)


@Dao
interface RoomDAO {
	@Insert(onConflict = onConflictStrategy.REPLACE)
	fun insertProduto(prod: Produto)

	@Update
	fun updateProduto(prod: Produto)

	@Query("Select * from produto")
	fun produtos(): Array<Produto>

}

@Database(entities = [( Produtos :: class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
	abstract fun romDao() :RoomDAO
}


class MainActivity :AppCompatActivity(){

	override fun onCreate(savedInstanceState: Bundle?){
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		val db = Room.databaseBuider(
			applicationContext,
			AppDatabase::class.java, "room-database"
		).allowMainThreadQueries().build()
	}

}



*
* */