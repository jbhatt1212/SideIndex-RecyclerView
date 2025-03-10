package com.example.sideindexrecyclerview

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var sideIndexView: SideIndexView
    private lateinit var adapter: ContactAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var floatingLetterView: TextView


    private val contactList = listOf(
        Contact("Alice"), Contact("Bob"), Contact("Charlie"), Contact("David"),
        Contact("Eve"), Contact("Frank"), Contact("Grace"), Contact("Hannah"),
        Contact("Isaac"), Contact("Jack"), Contact("Kenny"), Contact("Laura"),
        Contact("Mandy"), Contact("Nancy"), Contact("Oscar"), Contact("Paul"),
        Contact("Quinn"), Contact("Rachel"), Contact("Sam"), Contact("Tina"),
        Contact("Uma"), Contact("Victor"), Contact("Wendy"), Contact("Xander"),
        Contact("Yasmine"), Contact("Zane"),Contact("121313")
    ).sortedBy { it.name }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recycler_view)
        sideIndexView = findViewById(R.id.side_index_view)
        floatingLetterView = findViewById(R.id.floating_letter)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = ContactAdapter(contactList)
        recyclerView.adapter = adapter


        sideIndexView.setOnLetterClickListener { letter ->
            val position = contactList.indexOfFirst { it.name.startsWith(letter, ignoreCase = true) }
            if (position != -1) {
                layoutManager.scrollToPositionWithOffset(position, 0)

                // Show floating letter
                floatingLetterView.text = letter
                floatingLetterView.visibility = View.VISIBLE
                floatingLetterView.alpha = 1f

                // Fade out effect
                floatingLetterView.animate().alpha(0f).setDuration(1000).withEndAction {
                    floatingLetterView.visibility = View.GONE
                }
            }
        }
        }
    }
