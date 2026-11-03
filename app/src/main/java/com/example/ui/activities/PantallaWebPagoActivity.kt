package com.example.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.MyApplicationTheme

class PantallaWebPagoActivity : ComponentActivity() {

    private var webViewInstance: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val tipoPago = intent.getStringExtra("TIPO_PAGO") ?: "consultoria"

        setContent {
            MyApplicationTheme {
                PantallaWebPagoContent(
                    tipoPago = tipoPago,
                    onBack = { finish() },
                    onOpenCodigoActivacion = {
                        val intent = Intent(this, CodigoActivacionActivity::class.java)
                        startActivity(intent)
                    },
                    onBindWebView = { webViewInstance = it }
                )
            }
        }
    }

    override fun onDestroy() {
        webViewInstance?.destroy()
        webViewInstance = null
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PantallaWebPagoContent(
    tipoPago: String,
    onBack: () -> Unit,
    onOpenCodigoActivacion: () -> Unit,
    onBindWebView: (WebView) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var currentWebView by remember { mutableStateOf<WebView?>(null) }

    val tituloBarra = when (tipoPago) {
        "diploma" -> "Trámite de Diploma"
        "donacion" -> "Aportación Voluntaria"
        else -> "Club de Consultoría"
    }

    val subtitle = when (tipoPago) {
        "diploma" -> "Emisión oficial de certificado con firma"
        "donacion" -> "Soporte técnico y sostenimiento del servidor"
        else -> "Acceso ilimitado al diario y chat de desahogo"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = tituloBarra,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = subtitle,
                            fontSize = 11.sp,
                            color = Color(0xFF80CBC4)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("btn_volver_pasarela")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            currentWebView?.reload()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Recargar",
                            tint = Color(0xFF80CBC4)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0D1F1D)
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color(0xFF0D1F1D),
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (tipoPago == "consultoria") {
                        OutlinedButton(
                            onClick = onOpenCodigoActivacion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_ir_canjear_codigo"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFFFD54F)
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFFD54F))
                            )
                        ) {
                            Icon(Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "¿Tienes un código de activación? Ingrésalo aquí",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFF81C784),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Entorno seguro verificado con encriptación SSL",
                            fontSize = 11.sp,
                            color = Color(0xFFA5D6A7)
                        )
                    }
                }
            }
        },
        containerColor = Color(0xFF0D1F1D)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF0D1F1D))
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("webview_pasarela_pago"),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(0xFF0D1F1D.toInt())

                        settings.apply {
                            javaScriptEnabled = true
                            domStorageEnabled = true
                            loadWithOverviewMode = true
                            useWideViewPort = true
                            cacheMode = WebSettings.LOAD_DEFAULT
                            mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                        }

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                // Invoca el script para desplegar la sección requerida ('consultoria', 'diploma' o 'donacion')
                                view?.evaluateJavascript("javascript:cambiarPantalla('$tipoPago');", null)
                            }

                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                // Permite que enlaces de PayPal sigan cargando dentro del WebView
                                return false
                            }
                        }

                        webChromeClient = WebChromeClient()

                        loadUrl("file:///android_asset/pasarela_pago.html")

                        currentWebView = this
                        onBindWebView(this)
                    }
                },
                update = { webView ->
                    webView.evaluateJavascript("javascript:cambiarPantalla('$tipoPago');", null)
                }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0D1F1D).copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFF4ADE80),
                        strokeWidth = 3.dp
                    )
                }
            }
        }
    }
}
