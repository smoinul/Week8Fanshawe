package fanshawe.example.practiceweek8

import android.app.TimePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import fanshawe.example.practiceweek8.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter

class MainActivity : AppCompatActivity() {
    lateinit var myBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(myBinding.root)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setalarm -> {
                changeFragment(setAlarm())
                Toast.makeText(this, "Set Alarm!", Toast.LENGTH_SHORT).show()
            }
            R.id.chooser -> {
                changeFragment(ChooserTest())
                Toast.makeText(this, "Chooser!", Toast.LENGTH_SHORT).show()
            }
            R.id.third -> {
                changeFragment(BlankFragment())
                Toast.makeText(this, "Third!", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainerView, fragment)
        fragmentTransaction.commit()

    }

    fun onSetTime(view: View) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        // Launch Time Picker Dialog
        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                // Show selected time as toast
                Toast.makeText(this, "Selected Time: $time", Toast.LENGTH_SHORT).show()
                setAlarm(selectedHour, selectedMinute)
            }, hour, minute, false)
        timePickerDialog.show()


    }

    private fun setAlarm(hour: Int, minute: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "New Alarm")
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minute)
            putExtra(AlarmClock.EXTRA_SKIP_UI, false) // Optionally skip the UI of the alarm clock
        }
        startActivity(intent)
    }

    fun onCallClicked(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as BlankFragment

        fragment.view?.let { fragmentView ->
            val editText = fragmentView.findViewById<EditText>(R.id.editTextText)
            val callIntent: Intent = Uri.parse("tel:${editText.text.toString()}").let { number ->
                Intent(Intent.ACTION_DIAL, number)
            }
            startActivity(callIntent)
        }
    }

    fun onSendClicked(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as BlankFragment

        fragment.view?.let { fragmentView ->
            val editText = fragmentView.findViewById<EditText>(R.id.editTextText)

            try {
                // to save to file "test.txt" in data/data/packagename/File
                val ofile = openFileOutput("test.txt", MODE_PRIVATE)
                val osw = OutputStreamWriter(ofile)
                osw.write(editText.getText().toString())
                osw.flush()
                osw.close()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            }

            val file = File(this.filesDir, "test.txt")

            // Generate the URI for the file using the FileProvider
            val uri = FileProvider.getUriForFile(
                this,
                "${this.packageName}.provider",
                file
            )

            // Create the share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/*" // Or the appropriate MIME type of the file
                putExtra(Intent.EXTRA_STREAM, uri)
                // Grant temporary read permission to the content URI
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //addCategory(Intent.CATEGORY_OPENABLE)
                //for Intent.ACTION_SEND, the category CATEGORY_OPENABLE is not usually necessary because this action indicates you're sending data to another component, not requesting data that needs to be openable. The recipient app will handle the content URI as it sees fit.
            }

            // Create a chooser intent
            val chooserIntent = Intent.createChooser(shareIntent, "Share File")



            // Try to invoke the intent.
            try {
                startActivity(chooserIntent)
            } catch (e: ActivityNotFoundException) {
                // Define what your app should do if no activity can handle the intent.
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }


        }
    }

    fun onClickWeb(view: View) {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as ChooserTest

        fragment.view?.let { fragmentView ->
            val editText = fragmentView.findViewById<EditText>(R.id.editTextText2)
            val webIntent: Intent = Uri.parse("https://${editText.text.toString()}").let { webpage ->
                Intent(Intent.ACTION_VIEW, webpage)
            }
            startActivity(webIntent)

        }
    }
}