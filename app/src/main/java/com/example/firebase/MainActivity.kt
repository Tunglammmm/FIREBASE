package com.example.firebaseapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.firebase.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnDangKy = findViewById<Button>(R.id.btnDangKy)
        val btnDangNhap = findViewById<Button>(R.id.btnDangNhap)
        val btnHienThi = findViewById<Button>(R.id.btnHienThi)
        val tvData = findViewById<TextView>(R.id.tvData)

        // Đăng ký tài khoản
        btnDangKy.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                // Lưu thông tin vào Firebase Realtime Database
                                val userRef = database.reference.child("users").child(userId)
                                userRef.setValue(User(email, password))
                            }
                            Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        // Đăng nhập tài khoản
        btnDangNhap.setOnClickListener {
            val email = edtEmail.text.toString()
            val password = edtPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        // Hiển thị dữ liệu từ Firebase Realtime Database
        btnHienThi.setOnClickListener {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                val userRef = database.reference.child("users").child(userId)
                userRef.get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        val user = snapshot.getValue(User::class.java)
                        tvData.text = "Email: ${user?.email} \nMật khẩu: ${user?.password}"
                    } else {
                        tvData.text = "Không có dữ liệu!"
                    }
                }.addOnFailureListener {
                    tvData.text = "Lỗi khi tải dữ liệu!"
                }
            } else {
                Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// Model User để lưu vào Firebase Realtime Database
data class User(val email: String = "", val password: String = "")


