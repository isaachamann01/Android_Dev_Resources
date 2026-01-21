## You asked:

sorry can you give me the code again in kotlin?

---

## ChatGPT Replied:

Absolutely — here’s the **clean, idiomatic Kotlin version** for a CivTAK plugin using the **Map Activity as the ViewModelStoreOwner**.

I’ll keep it minimal and safe.

* * *

1️⃣ Get the ViewModel from the Map Activity (Kotlin)
----------------------------------------------------

    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.ViewModelStoreOwner
    import com.atakmap.android.maps.MapView
    import android.app.Activity
    
    private lateinit var viewModel: MyPluginViewModel
    
    private fun getViewModel(): MyPluginViewModel {
        if (!::viewModel.isInitialized) {
            val ctx = MapView.getMapView().context
    
            if (ctx !is ViewModelStoreOwner) {
                throw IllegalStateException("Map Activity is not a ViewModelStoreOwner")
            }
    
            viewModel = ViewModelProvider(ctx)[MyPluginViewModel::class.java]
        }
    
        return viewModel
    }
    

* * *

2️⃣ Use it inside your `showPane` logic
---------------------------------------

    private fun showPane(p: Pane?, ratio: Double): Pane {
        var pane = p
    
        if (pane == null) {
            val mainLayout = PluginLayoutInflater.inflate(
                pluginContext,
                R.layout.main_layout,
                null
            )
    
            val vm = getViewModel()
    
            val statusText = mainLayout.findViewById<TextView>(R.id.actionBarVisibilityTextView)
    
            vm.state.collectInUi { state ->
                statusText.text = state.text
            }
    
            pane = PaneBuilder(mainLayout)
                .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, ratio)
                .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, ratio)
                .build()
        }
    
        if (!uiService.isPaneVisible(pane)) {
            uiService.showPane(pane, null)
        }
    
        return pane
    }
    

> ⚠️ `collectInUi {}` is a placeholder — see below.

* * *

3️⃣ Collect ViewModel state safely (helper)
-------------------------------------------

Because you’re in a plugin (not a Fragment), use the **Activity lifecycle**:

    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.lifecycleScope
    import androidx.lifecycle.repeatOnLifecycle
    import kotlinx.coroutines.launch
    
    fun <T> MyPluginViewModel.collectInUi(
        block: (UiState) -> Unit
    ) {
        val activity = MapView.getMapView().context as androidx.lifecycle.LifecycleOwner
    
        activity.lifecycleScope.launch {
            activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
                state.collect { block(it) }
            }
        }
    }
    

* * *

4️⃣ Minimal ViewModel (for completeness)
----------------------------------------

    class MyPluginViewModel : ViewModel() {
    
        private val _state = MutableStateFlow(UiState())
        val state: StateFlow<UiState> = _state
    
        fun updateText(text: String) {
            _state.value = UiState(text = text)
        }
    }
    

* * *

5️⃣ Important reminders (CivTAK-specific)
-----------------------------------------

*   ✔ `MapView.getMapView().context` is your Activity
    
*   ✔ Activity = ViewModelStoreOwner
    
*   ❌ Pane / View ≠ ViewModelStoreOwner
    
*   ❌ Don’t store Views or Context in ViewModel
    

* * *

TL;DR
-----

    val owner = MapView.getMapView().context as ViewModelStoreOwner
    val vm = ViewModelProvider(owner)[MyPluginViewModel::class.java]
    

That’s the **exact Kotlin equivalent** of what you need.

If you want, next I can:

*   Refactor your pasted Java method into **clean Kotlin + ViewBinding**
    
*   Show **where to start/stop collecting state when pane opens/closes**
    
*   Add **HTTP fetch → UI update** end-to-end in your pane
    

Just say which.

---

## You asked:

can you give me an example of using this pattern with this function having a button that edits some text on the panel?

---

## ChatGPT Replied:

Yep — here’s a **drop-in style Kotlin example** that matches your `showPane(p, ratio)` pattern:

*   Inflate `main_layout`
    
*   Grab **Map Activity** as the `ViewModelStoreOwner`
    
*   Get your `MyPluginViewModel`
    
*   Wire a **button** that updates ViewModel state
    
*   Collect state and **update a TextView on the pane**
    

### 1) ViewModel (holds the text you want to show)

    import androidx.lifecycle.ViewModel
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    
    class MyPluginViewModel : ViewModel() {
        private val _panelText = MutableStateFlow("Hello from ViewModel")
        val panelText: StateFlow<String> = _panelText
    
        fun setPanelText(text: String) {
            _panelText.value = text
        }
    }
    

### 2) Your `showPane` function with a button that edits text on the panel

    import android.view.View
    import android.widget.Button
    import android.widget.TextView
    import androidx.lifecycle.Lifecycle
    import androidx.lifecycle.LifecycleOwner
    import androidx.lifecycle.ViewModelProvider
    import androidx.lifecycle.ViewModelStoreOwner
    import androidx.lifecycle.lifecycleScope
    import androidx.lifecycle.repeatOnLifecycle
    import com.atakmap.android.maps.MapView
    import kotlinx.coroutines.launch
    
    private var pane: Pane? = null
    private var viewModel: MyPluginViewModel? = null
    private var mainLayout: View? = null
    
    private fun showPane(p: Pane?, ratio: Double): Pane {
        var localPane = p
    
        if (localPane == null) {
            // Inflate your plugin panel
            val layout = PluginLayoutInflater.inflate(pluginContext, R.layout.main_layout, null)
            mainLayout = layout
    
            // 1) Get the TAK Map Activity (as ViewModelStoreOwner + LifecycleOwner)
            val ctx = MapView.getMapView().context
            val owner = ctx as? ViewModelStoreOwner
                ?: throw IllegalStateException("Map context is not a ViewModelStoreOwner")
            val lifecycleOwner = ctx as? LifecycleOwner
                ?: throw IllegalStateException("Map context is not a LifecycleOwner")
    
            // 2) Get (or create) the ViewModel from the Activity store
            val vm = viewModel ?: ViewModelProvider(owner)[MyPluginViewModel::class.java].also {
                viewModel = it
            }
    
            // 3) Find views on the panel
            val textView = layout.findViewById<TextView>(R.id.actionBarVisibilityTextView)
            val button = layout.findViewById<Button>(R.id.showActionBarButton)
    
            // 4) Button click updates ViewModel state (NOT the TextView directly)
            button.setOnClickListener {
                vm.setPanelText("Button clicked at ${System.currentTimeMillis()}")
            }
    
            // 5) Observe ViewModel state and render it into the TextView
            // IMPORTANT: to avoid starting multiple collectors if showPane() is called again,
            // only do this the first time you create the layout (inside localPane == null).
            lifecycleOwner.lifecycleScope.launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    vm.panelText.collect { text ->
                        // mainLayout might be nulled on close; guard to avoid crashes
                        textView.text = text
                    }
                }
            }
    
            // 6) Build the Pane
            localPane = PaneBuilder(layout)
                .setMetaValue(Pane.RELATIVE_LOCATION, Pane.Location.Default)
                .setMetaValue(Pane.PREFERRED_WIDTH_RATIO, ratio)
                .setMetaValue(Pane.PREFERRED_HEIGHT_RATIO, ratio)
                .build()
    
            pane = localPane
        }
    
        // Show if not visible
        if (!uiService.isPaneVisible(localPane)) {
            uiService.showPane(localPane, null)
        }
    
        return localPane
    }
    

### What this pattern is doing (in one sentence)

*   **Button click → updates ViewModel state**
    
*   **Collector → updates TextView when state changes**
    

That’s the clean “MVVM” separation: UI triggers intent, ViewModel holds data, UI renders data.

* * *

### Two practical CivTAK gotchas (so you don’t get bitten)

1.  **Don’t start collectors every time** `showPane()` is called — you’ll get duplicated updates.  
    That’s why the collector is inside `if (localPane == null)`.
    
2.  When the pane is closed/unloaded, it’s a good idea to clear view references:
    

    fun onPaneClosed() {
        mainLayout = null
        // (You usually keep the ViewModel though)
    }
    

* * *

If you tell me the IDs you want to use (your “output TextView” + your “Fetch” button), I can adapt the snippet to your exact layout names.

---

