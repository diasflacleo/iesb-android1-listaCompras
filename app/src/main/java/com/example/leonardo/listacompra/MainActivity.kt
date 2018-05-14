package com.example.leonardo.listacompra

import android.arch.persistence.room.Room
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.leonardo.listacompra.R.layout.toolbar_main
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar_main.*
import java.text.NumberFormat
import java.util.*
import android.support.v7.widget.Toolbar
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private var listCompras = ArrayList<Produto>()

    var comprasAdapter = ComprasAdapter(this, listCompras)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var tb_main : android.support.v7.widget.Toolbar = findViewById(R.id.toolbar_app)
        tb_main.setTitleTextColor(Color.WHITE)
        setSupportActionBar(tb_main)


        // dummy data pra ser apagado depois
        /*listCompras.add(Produto(0,"Samsung J7 PRO", "800.0", "Smartphone"))
        listCompras.add(Produto(1,"Playstation 4", "1000.0", "Console"))
        listCompras.add(Produto(2,"Uncharted 4 - A Thief's End", "70.0", "Jogo"))
        listCompras.add(Produto(3,"Samsung J6 PRO", "600.0", "Smartphone"))
        listCompras.add(Produto(4,"Playstation 3", "900.0", "Console"))
        listCompras.add(Produto(5,"Uncharted 3", "50.0", "Jogo"))
        listCompras.add(Produto(6,"Samsung J5 PRO", "800.0", "Smartphone"))
        listCompras.add(Produto(7,"Playstation 2", "7000.0", "Console"))
        listCompras.add(Produto(8, "Uncharted 2", "30.0", "Jogo"))*/

        //-----------------------
        val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "room-database"
        ).allowMainThreadQueries().build()

        //busca os a lista de produtos do banco de dados e carrega na lista de compras.
        var prods: Array<Produto> = db.romDao().produtos()

        for (prod in prods){
         listCompras.add(Produto(prod.uid,prod.nome, prod.preco, prod.descricao))
        }



        //-------------------------------
        comprasAdapter = ComprasAdapter(this, listCompras)
        listView_lista.adapter = comprasAdapter
        listView_lista.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            Toast.makeText(this, "Clicou em ' " + listCompras[position].nome+ " '", Toast.LENGTH_SHORT).show()
        }



        listView_lista.onItemLongClickListener = AdapterView.OnItemLongClickListener{ adapterView, view, position, id ->

            Toast.makeText(this, "excluir o item ' " + listCompras[position].nome+ " '", Toast.LENGTH_SHORT).show()

            var alertBuilder = AlertDialog.Builder(this)

            with (alertBuilder){
                setTitle("Excluir Item")
                setMessage("Deseja excluir o '"+listCompras[position].nome+ "' ?")

                    setPositiveButton("Sim"){
                        dialog, which ->

                        //-----------------------
                        removerItemLista(view, position)
                        db.romDao().deleteProduto(listCompras[position])
                        Toast.makeText(this@MainActivity, "Produto excluído com sucesso!", Toast.LENGTH_LONG).show()

                        //-------------------------
                        //listCompras.removeAt(position)
                        // depois de removido, a lista é atualizada
                        //comprasAdapter.notifyDataSetChanged()
                     //   dialog.dismiss()

                    }

                    setNegativeButton("Não"){
                        dialog, which ->

                        dialog.dismiss()
                    }

            }


            val alert = alertBuilder.create()
            //alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            alert.show()

            var buttonYes: Button = alert.getButton(DialogInterface.BUTTON_POSITIVE)
            buttonYes.setBackgroundColor(Color.parseColor("#339933"))
            buttonYes.setTextColor(Color.WHITE)

            var buttonNo: Button = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
            buttonNo.setBackgroundColor(Color.parseColor("#990000"))
            buttonNo.setTextColor(Color.WHITE)

            true

        }



    }

    //-------------------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        var id: Int = item!!.itemId
        if (id == R.id.cadastrar_item){
            Toast.makeText(this,"test",Toast.LENGTH_LONG)
            val intent = Intent(this@MainActivity,CadastroProduto::class.java)
            startActivity(intent)

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    //-------------------------------




    //----------
    fun removerItemLista(view: View,  position: Int ){
        var animation: Animation = AnimationUtils.loadAnimation(view.context, R.anim.splash_fade_out)
        view.startAnimation(animation)
        var handle: Handler = Handler()
        handle.postDelayed( {

                listCompras.removeAt(position)
                // depois de removido, a lista é atualizada
                comprasAdapter.notifyDataSetChanged()
                animation.cancel()


        },1000)
    }

    //---------


    inner class ComprasAdapter : BaseAdapter {

        private var listaCompra = ArrayList<Produto>()
        private var context: Context? = null

        constructor(context: Context, listaCompra: ArrayList<Produto>) : super() {
            this.listaCompra = listaCompra
            this.context = context
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

            val view: View?
            val vh: ViewHolder

            if (convertView == null) {
                view = layoutInflater.inflate(R.layout.item_lista, parent, false)
                vh = ViewHolder(view)
                view.tag = vh

            } else {
                view = convertView
                vh = view.tag as ViewHolder
            }

            vh.textView_nome.text = listaCompra[position].nome
            //Formatando o dinheiro para R$ (Real)
            var formatter = NumberFormat.getCurrencyInstance(Locale("pt","BR"))
            var moneyFormatted = formatter.format(listaCompra[position].preco.toDouble())
            vh.textView_preco.text = moneyFormatted

            vh.textView_descricao.text = listaCompra[position].descricao

            return view
        }

        override fun getItem(position: Int): Any {
            return listaCompra[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listaCompra.size
        }
    }

    private class ViewHolder(view: View?) {
        val textView_nome: TextView
        val textView_preco: TextView
        val textView_descricao: TextView

        init {
            this.textView_nome = view?.findViewById(R.id.textView_nome) as TextView
            this.textView_preco = view?.findViewById(R.id.textView_preco) as TextView
            this.textView_descricao = view?.findViewById(R.id.textView_descricao) as TextView

        }
    }


    override fun onResume() {
        super.onResume()
        comprasAdapter.notifyDataSetChanged()
    }

}
