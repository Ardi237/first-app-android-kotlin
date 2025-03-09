package com.example.ardi_project

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ardi_project.adapters.FriendsAdapter
import com.example.ardi_project.api.RetrofitClient
import com.example.ardi_project.models.Friend
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FriendsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddFriend: FloatingActionButton
    private lateinit var friendsAdapter: FriendsAdapter
    private var friendsList: MutableList<Friend> = mutableListOf()
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend)

//        recyclerView = findViewById(R.id.recyclerViewFriends)
//        fabAddFriend = findViewById(R.id.fabAddFriend)

        setupRecyclerView()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = friendsAdapter

        fabAddFriend.setOnClickListener {
            startActivity(Intent(this, AddFriendActivity::class.java))
        }

        fetchFriends()
    }

    private fun fetchFriends() {
        RetrofitClient.instance.getFriends().enqueue(object : Callback<List<Friend>> {
            override fun onResponse(call: Call<List<Friend>>, response: Response<List<Friend>>) {
                if (response.isSuccessful) {
                    friendsList.clear()
                    response.body()?.let { friendsList.addAll(it) }
                    friendsAdapter.notifyDataSetChanged()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@FriendsActivity, "Error: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Friend>>, t: Throwable) {
                Log.e("API_ERROR", "Error: ${t.message}", t)
                Toast.makeText(this@FriendsActivity, "Koneksi gagal: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setupRecyclerView() {
        friendsAdapter = FriendsAdapter(
            friendsList,
            onItemClick = { friend ->
//                val intent = Intent(this, DetailFriendActivity::class.java).apply {
//                    putExtra("friend_id", friend.id)
//                    putExtra("friend_name", friend.name)
//                    putExtra("friend_birth_date", friend.birth_date)
//                    putExtra("friend_image", friend.imageUrl)
//                }
                startActivity(intent)
            },
            onDeleteClick = { friend ->
                showDeleteConfirmation(friend)
            }
        )
    }

    fun showDeleteConfirmation(friend: Friend) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin menghapus teman ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                deleteFriend(friend)
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteFriend(friend: Friend) {
        RetrofitClient.instance.deleteFriend(friend.id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    friendsList.remove(friend)
                    friendsAdapter.notifyDataSetChanged()
                    Toast.makeText(this@FriendsActivity, "Teman berhasil dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@FriendsActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@FriendsActivity, "Gagal menghapus teman", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchFriends()
    }
}
