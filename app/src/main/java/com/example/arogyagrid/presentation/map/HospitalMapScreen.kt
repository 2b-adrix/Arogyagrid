package com.example.arogyagrid.presentation.map

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arogyagrid.core.AppLanguage
import com.example.arogyagrid.core.AppThemeMode
import com.example.arogyagrid.core.LanguageManager
import com.example.arogyagrid.core.ThemeManager
import com.example.arogyagrid.core.UserSession
import com.example.arogyagrid.data.remote.NetworkClient
import com.example.arogyagrid.presentation.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.MapTileProviderBasic
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

data class MapHospital(
    val id: String,
    val name: String,
    val type: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val status: String,
    val isCritical: Boolean,
    val availableStock: Int? = null,
    val contactPhone: String? = null
)

data class MedicineOption(
    val id: String,
    val nameHi: String,
    val nameEn: String
)

enum class MapScreenMode {
    SPLIT,      // Top balanced map (210dp) + Full facility cards list
    FULL_MAP,   // Maximize map view with floating bottom card
    LIST_ONLY   // Maximize list view with compact top route strip
}

@Composable
fun HospitalMapScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val currentLang by LanguageManager.currentLanguage.collectAsState()
    val themeMode by ThemeManager.themeMode.collectAsState()
    val isDark = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
    }

    // Configure Osmdroid User-Agent to avoid OSM tile server rate-limiting
    remember {
        Configuration.getInstance().apply {
            load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
            userAgentValue = "ArogyaGridHealthcareApp/1.0 (Android; contact@arogyagrid.in)"
        }
        true
    }

    // 1. Humanitarian OpenStreetMap (HOT)
    val osmHotTileSource = remember {
        XYTileSource(
            "OpenStreetMapHOT",
            0, 19, 256, ".png",
            arrayOf(
                "https://a.tile.openstreetmap.fr/hot/",
                "https://b.tile.openstreetmap.fr/hot/",
                "https://c.tile.openstreetmap.fr/hot/"
            ),
            "© OpenStreetMap contributors, Tiles style by Humanitarian OpenStreetMap Team hosted by OpenStreetMap France",
            TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE)
        )
    }

    // 2. Official OpenStreetMap Standard Tiles
    val osmStandardTileSource = remember {
        XYTileSource(
            "OpenStreetMapStandard",
            0, 19, 256, ".png",
            arrayOf("https://tile.openstreetmap.org/"),
            "© OpenStreetMap contributors",
            TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE)
        )
    }

    // 3. OpenTopoMap
    val osmTopoTileSource = remember {
        XYTileSource(
            "OpenTopoMap",
            0, 17, 256, ".png",
            arrayOf(
                "https://a.tile.opentopomap.org/",
                "https://b.tile.opentopomap.org/",
                "https://c.tile.opentopomap.org/"
            ),
            "Kartendaten: © OpenStreetMap-Mitwirkende, SRTM | Kartendarstellung: © OpenTopoMap (CC-BY-SA)",
            TileSourcePolicy(2, TileSourcePolicy.FLAG_NO_BULK or TileSourcePolicy.FLAG_NO_PREVENTIVE)
        )
    }

    var selectedTileSourceIndex by remember { mutableIntStateOf(0) }
    var selectedHospitalId by remember { mutableStateOf<String?>("FAC-JPR-001") }
    var mapViewInstance by remember { mutableStateOf<MapView?>(null) }

    // Screen Layout Mode: SPLIT (default), FULL_MAP, or LIST_ONLY
    var screenMode by remember { mutableStateOf(MapScreenMode.SPLIT) }

    // Top Settings Menu Expanded state
    var settingsMenuExpanded by remember { mutableStateOf(false) }
    // Layer selector popup expanded state
    var layerMenuExpanded by remember { mutableStateOf(false) }

    // Live Database State
    var hospitals by remember { mutableStateOf<List<MapHospital>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Medicine filter list with bilingual names
    val medicineOptions = remember {
        listOf(
            MedicineOption("ALL", "समस्त पंजीकृत संस्थान", "All Registered Facilities"),
            MedicineOption("MED-202", "ओआरएस पैकेट्स (आपातकालीन)", "ORS Sachets (Emergency)"),
            MedicineOption("MED-101", "एमोक्सिसिलिन 500mg", "Amoxicillin 500mg"),
            MedicineOption("MED-201", "ऑक्सीजन सिलेंडर (D-Type)", "Oxygen Cylinder (D-Type)"),
            MedicineOption("MED-104", "सेप्ट्रियाक्सोन 1g इंजेक्शन", "Ceftriaxone 1g Inj"),
            MedicineOption("MED-301", "मेटफॉर्मिन 500mg", "Metformin 500mg"),
            MedicineOption("MED-401", "पैरासिटामोल सिरप 125mg", "Paracetamol Syrup 125mg")
        )
    }
    var selectedMedicineIndex by remember { mutableIntStateOf(0) }
    var medicineDropdownExpanded by remember { mutableStateOf(false) }

    // Current origin hospital for distance routing (default: PHC Amer FAC-JPR-011)
    val originFacilityId = remember { "FAC-JPR-011" } // PHC Amer

    // Fetch live hospital dataset from Neon PostgreSQL Backend
    fun fetchLiveHospitals(medId: String) {
        isLoading = true
        errorMessage = null
        coroutineScope.launch {
            try {
                if (medId == "ALL") {
                    val res = withContext(Dispatchers.IO) { NetworkClient.apiService.getFacilities("Jaipur") }
                    if (res.isSuccessful && res.body()?.data != null) {
                        val facList = res.body()!!.data!!
                        val originLat = 26.9855
                        val originLon = 75.8513
                        hospitals = facList.map { f ->
                            val lat = f.latitude ?: 26.8924
                            val lon = f.longitude ?: 75.8173
                            val dist = calculateHaversine(originLat, originLon, lat, lon)
                            MapHospital(
                                id = f.facilityId,
                                name = f.name,
                                type = f.type.replace("_", " "),
                                latitude = lat,
                                longitude = lon,
                                distanceKm = Math.round(dist * 10.0) / 10.0,
                                status = if (f.facilityId == "FAC-JPR-001" || f.facilityId == "FAC-JPR-005") {
                                    if (currentLang == AppLanguage.HINDI) "अधिशेष स्टॉक केंद्र" else "Surplus Stock Hub"
                                } else {
                                    if (currentLang == AppLanguage.HINDI) "सक्रिय संस्थान" else "Active Facility"
                                },
                                isCritical = f.facilityId == "FAC-JPR-011" || f.facilityId == "FAC-JPR-006",
                                contactPhone = f.contactPhone
                            )
                        }.sortedBy { it.distanceKm }
                    } else {
                        errorMessage = "Backend connection failed. Status: ${res.code()}"
                    }
                } else {
                    val res = withContext(Dispatchers.IO) {
                        NetworkClient.apiService.getNearestSurplus(originFacilityId, medId)
                    }
                    if (res.isSuccessful && res.body()?.data != null) {
                        val nearestList = res.body()!!.data!!
                        hospitals = nearestList.map { n ->
                            MapHospital(
                                id = n.facilityId,
                                name = n.facilityName,
                                type = if (currentLang == AppLanguage.HINDI) "आपातकालीन आपूर्ति केंद्र" else "Candidate Surplus Hub",
                                latitude = n.latitude,
                                longitude = n.longitude,
                                distanceKm = n.distanceKm,
                                status = "${n.availableStock} ${if (currentLang == AppLanguage.HINDI) "यूनिट उपलब्ध" else "Units Available"}",
                                isCritical = false,
                                availableStock = n.availableStock,
                                contactPhone = n.contactPhone
                            )
                        }
                    } else {
                        hospitals = getFallbackHospitalList(currentLang)
                    }
                }
            } catch (_: Exception) {
                hospitals = getFallbackHospitalList(currentLang)
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedMedicineIndex, currentLang) {
        val selectedMed = medicineOptions[selectedMedicineIndex]
        fetchLiveHospitals(selectedMed.id)
    }

    DisposableEffect(Unit) {
        onDispose {
            mapViewInstance?.onPause()
        }
    }

    fun animateMapToFacility(targetLat: Double, targetLon: Double, zoom: Double = 13.5) {
        mapViewInstance?.let { mv ->
            mv.post {
                mv.controller.animateTo(GeoPoint(targetLat, targetLon))
                mv.controller.setZoom(zoom)
            }
        }
    }

    val activeHospital = hospitals.find { it.id == selectedHospitalId }
    val estMinutes = activeHospital?.let { Math.max(8, (it.distanceKm * 2.1).toInt()) } ?: 22

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── 1. Official National Tricolor Ribbon (Tiranga) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(TirangaSaffron))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(androidx.compose.ui.graphics.Color.White))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(TirangaGreen))
        }

        // ── 2. Clean, Organized Header (Unified, No Duplicate Logos) ──
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.s("राजस्थान सरकार • स्वास्थ्य विभाग", "GOVT. OF RAJASTHAN • HEALTH DEPT"),
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TirangaSaffron,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = LanguageManager.s("आपातकालीन औषधि ग्रीन कॉरिडोर", "Emergency Medicine Green Corridor"),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Map Layer Switcher
                    Box {
                        IconButton(
                            onClick = { layerMenuExpanded = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = "Map Layers",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = layerMenuExpanded,
                            onDismissRequest = { layerMenuExpanded = false }
                        ) {
                            listOf("HOT नक्शा (Humanitarian)", "Standard OSM", "Topo नक्शा (Elevation)").forEachIndexed { index, name ->
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                name,
                                                color = if (selectedTileSourceIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (selectedTileSourceIndex == index) FontWeight.Bold else FontWeight.Normal
                                            )
                                            if (selectedTileSourceIndex == index) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedTileSourceIndex = index
                                        val targetSource = when (index) {
                                            0 -> osmHotTileSource
                                            1 -> osmStandardTileSource
                                            else -> osmTopoTileSource
                                        }
                                        mapViewInstance?.let { mv ->
                                            mv.setTileSource(targetSource)
                                            mv.invalidate()
                                        }
                                        layerMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Settings & Language Menu Dropdown (3-Dots)
                    Box {
                        IconButton(
                            onClick = { settingsMenuExpanded = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                Icons.Default.MoreVert,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = settingsMenuExpanded,
                            onDismissRequest = { settingsMenuExpanded = false }
                        ) {
                            // ── Language Selection ──
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "हिन्दी (Hindi)",
                                            color = if (currentLang == AppLanguage.HINDI) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (currentLang == AppLanguage.HINDI) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (currentLang == AppLanguage.HINDI) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Translate, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    LanguageManager.setLanguage(AppLanguage.HINDI)
                                    settingsMenuExpanded = false
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            "English",
                                            color = if (currentLang == AppLanguage.ENGLISH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (currentLang == AppLanguage.ENGLISH) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (currentLang == AppLanguage.ENGLISH) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                onClick = {
                                    LanguageManager.setLanguage(AppLanguage.ENGLISH)
                                    settingsMenuExpanded = false
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // ── Theme Selection ──
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "सिस्टम थीम (System)" else "System Theme",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (themeMode == AppThemeMode.SYSTEM) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.SettingsBrightness, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.SYSTEM)
                                    settingsMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "लाइट मोड (Light)" else "Light Mode",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (themeMode == AppThemeMode.LIGHT) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.LightMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.LIGHT)
                                    settingsMenuExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "डार्क मोड (Dark)" else "Dark Mode",
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (themeMode == AppThemeMode.DARK) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                leadingIcon = { Icon(Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                                onClick = {
                                    ThemeManager.setThemeMode(AppThemeMode.DARK)
                                    settingsMenuExpanded = false
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            // ── Account / Login / Logout ──
                            if (UserSession.isLoggedIn) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "लॉगआउट (${UserSession.userName ?: "उपयोगकर्ता"})" else "Logout (${UserSession.userName ?: "User"})",
                                            color = StatusRed,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = StatusRed) },
                                    onClick = {
                                        settingsMenuExpanded = false
                                        UserSession.logout()
                                        onLogout()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) "लॉगिन करें (Sign In)" else "Sign In (Login)",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                    onClick = {
                                        settingsMenuExpanded = false
                                        onNavigateToLogin()
                                    }
                                )
                            }
                        }
                    }
                }

                // ── 3. High-Contrast Official Emergency Corridor Alert Strip ──
                // Compact single-line official dispatch strip that never clashes in dark or light mode
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(3.dp),
                            color = if (isDark) StatusOrange.copy(alpha = 0.3f) else StatusOrangeBg,
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, StatusOrange)
                        ) {
                            Text(
                                text = "GC-82 • CMHO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) androidx.compose.ui.graphics.Color(0xFFFDBA74) else StatusOrange,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LanguageManager.s("SMS ➔ पीएचसी आमेर आपातकालीन रूट • 500 ORS • ~22 मिनट एक्सप्रेस क्लीयरेंस", "SMS ➔ PHC Amer Emergency Route • 500 ORS • ~22 min Express Transit"),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── 4. Unified Filter & View Mode Control Toolbar ──
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Medicine Selector Dropdown
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedButton(
                            onClick = { medicineDropdownExpanded = true },
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Icon(
                                Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            val curMed = medicineOptions[selectedMedicineIndex]
                            Text(
                                text = if (currentLang == AppLanguage.HINDI) curMed.nameHi else curMed.nameEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        DropdownMenu(
                            expanded = medicineDropdownExpanded,
                            onDismissRequest = { medicineDropdownExpanded = false }
                        ) {
                            medicineOptions.forEachIndexed { index, option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            if (currentLang == AppLanguage.HINDI) option.nameHi else option.nameEn,
                                            fontSize = 12.sp,
                                            color = if (selectedMedicineIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (selectedMedicineIndex == index) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedMedicineIndex = index
                                        medicineDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // View Mode Segmented Switcher (Split, Full Map, List)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(6.dp))
                            .height(36.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Split Mode
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (screenMode == MapScreenMode.SPLIT) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                                .clickable { screenMode = MapScreenMode.SPLIT }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.VerticalSplit,
                                contentDescription = "Split",
                                modifier = Modifier.size(15.dp),
                                tint = if (screenMode == MapScreenMode.SPLIT) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Full Map Mode
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (screenMode == MapScreenMode.FULL_MAP) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                                .clickable { screenMode = MapScreenMode.FULL_MAP }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = "Full Map",
                                modifier = Modifier.size(15.dp),
                                tint = if (screenMode == MapScreenMode.FULL_MAP) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // List Only Mode
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(5.dp))
                                .background(if (screenMode == MapScreenMode.LIST_ONLY) MaterialTheme.colorScheme.primary else androidx.compose.ui.graphics.Color.Transparent)
                                .clickable { screenMode = MapScreenMode.LIST_ONLY }
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.FormatListBulleted,
                                contentDescription = "List Only",
                                modifier = Modifier.size(15.dp),
                                tint = if (screenMode == MapScreenMode.LIST_ONLY) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Refresh Button
                    IconButton(
                        onClick = { fetchLiveHospitals(medicineOptions[selectedMedicineIndex].id) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // ── 5. Map Section (Visible in SPLIT and FULL_MAP modes) ──
        if (screenMode != MapScreenMode.LIST_ONLY) {
            val mapModifier = if (screenMode == MapScreenMode.FULL_MAP) {
                Modifier.fillMaxWidth().weight(1f)
            } else {
                Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            }

            Box(modifier = mapModifier) {
                Card(
                    shape = RoundedCornerShape(if (screenMode == MapScreenMode.FULL_MAP) 0.dp else 8.dp),
                    border = if (screenMode == MapScreenMode.FULL_MAP) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(2.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            factory = { ctx ->
                                val initialSource = when (selectedTileSourceIndex) {
                                    0 -> osmHotTileSource
                                    1 -> osmStandardTileSource
                                    else -> osmTopoTileSource
                                }
                                val tileProvider = MapTileProviderBasic(ctx.applicationContext, initialSource)
                                MapView(ctx, tileProvider).apply {
                                    setMultiTouchControls(true)
                                    minZoomLevel = 5.0
                                    maxZoomLevel = 19.0
                                    val jaipurAmer = GeoPoint(26.9855, 75.8513)
                                    controller.setZoom(13.0)
                                    controller.setCenter(jaipurAmer)
                                    post {
                                        controller.setZoom(13.0)
                                        controller.animateTo(jaipurAmer)
                                    }
                                    mapViewInstance = this
                                    onResume()
                                }
                            },
                            update = { mapView ->
                                mapView.overlays.clear()

                                val originGeoPoint = GeoPoint(26.9855, 75.8513)

                                // Origin Marker (PHC Amer)
                                val originMarker = Marker(mapView).apply {
                                    position = originGeoPoint
                                    title = if (currentLang == AppLanguage.HINDI) "पीएचसी आमेर (मांग केंद्र)" else "PHC Amer (Origin Facility)"
                                    snippet = "Lat: 26.9855, Lon: 75.8513"
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                }
                                mapView.overlays.add(originMarker)

                                // Draw Route Polyline from PHC Amer to selected candidate facility
                                val targetHospital = hospitals.find { it.id == selectedHospitalId }
                                if (targetHospital != null && (targetHospital.latitude != 26.9855 || targetHospital.longitude != 75.8513)) {
                                    val targetGeoPoint = GeoPoint(targetHospital.latitude, targetHospital.longitude)
                                    val routePolyline = Polyline(mapView).apply {
                                        outlinePaint.color = Color.parseColor("#0F766E")
                                        outlinePaint.strokeWidth = 9f
                                        outlinePaint.strokeCap = Paint.Cap.ROUND
                                        outlinePaint.pathEffect = DashPathEffect(floatArrayOf(24f, 12f), 0f)
                                        addPoint(originGeoPoint)
                                        addPoint(targetGeoPoint)
                                        title = "Green Corridor: PHC Amer ➔ ${targetHospital.name} (${targetHospital.distanceKm} km)"
                                    }
                                    mapView.overlays.add(routePolyline)
                                }

                                // Add hospital markers
                                hospitals.forEach { h ->
                                    val marker = Marker(mapView)
                                    marker.position = GeoPoint(h.latitude, h.longitude)
                                    marker.title = h.name
                                    marker.snippet = "${h.type} • ${h.status} • ${h.distanceKm} km"
                                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    marker.setOnMarkerClickListener { m, _ ->
                                        selectedHospitalId = h.id
                                        m.showInfoWindow()
                                        true
                                    }
                                    mapView.overlays.add(marker)
                                }
                                mapView.invalidate()
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Floating Active Route Card on Map
                        if (activeHospital != null && (activeHospital.latitude != 26.9855 || activeHospital.longitude != 75.8513)) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(8.dp),
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                shadowElevation = 3.dp,
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(MaterialTheme.colorScheme.primary))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .background(StatusGreen, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${LanguageManager.s("कॉरिडोर", "Corridor")}: PHC Amer ➔ ${activeHospital.name.take(14)}...",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${activeHospital.distanceKm} ${LanguageManager.s("किमी", "km")} • ~$estMinutes ${LanguageManager.s("मिनट एक्सप्रेस", "min express")}",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        // Map Quick Controls: Zoom In, Zoom Out, Recenter, Fullscreen Toggle
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(
                                    end = 8.dp,
                                    bottom = if (screenMode == MapScreenMode.FULL_MAP) 120.dp else 8.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SmallFloatingActionButton(
                                onClick = {
                                    screenMode = if (screenMode == MapScreenMode.FULL_MAP) MapScreenMode.SPLIT else MapScreenMode.FULL_MAP
                                },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    if (screenMode == MapScreenMode.FULL_MAP) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                    contentDescription = "Toggle Fullscreen Map",
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            SmallFloatingActionButton(
                                onClick = { mapViewInstance?.controller?.zoomIn() },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Zoom In", modifier = Modifier.size(16.dp))
                            }

                            SmallFloatingActionButton(
                                onClick = { mapViewInstance?.controller?.zoomOut() },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", modifier = Modifier.size(16.dp))
                            }

                            SmallFloatingActionButton(
                                onClick = {
                                    val jaipurAmer = GeoPoint(26.9855, 75.8513)
                                    mapViewInstance?.controller?.animateTo(jaipurAmer)
                                    mapViewInstance?.controller?.setZoom(13.5)
                                },
                                shape = CircleShape,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.MyLocation, contentDescription = "Center on PHC Amer", modifier = Modifier.size(16.dp))
                            }
                        }

                        // In FULL MAP mode: Floating Quick Facility Action Card at bottom
                        if (screenMode == MapScreenMode.FULL_MAP && activeHospital != null) {
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                shape = RoundedCornerShape(10.dp),
                                shadowElevation = 6.dp,
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .background(
                                                    if (activeHospital.isCritical) StatusRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                                    RoundedCornerShape(4.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.LocalHospital,
                                                contentDescription = null,
                                                tint = if (activeHospital.isCritical) StatusRed else MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = activeHospital.name,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${activeHospital.type} • ${activeHospital.distanceKm} ${LanguageManager.s("किमी", "km")} • ~$estMinutes ${LanguageManager.s("मिनट", "min")}",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(2.dp),
                                            color = if (activeHospital.isCritical) StatusRed.copy(alpha = 0.2f) else StatusGreen.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = activeHospital.status,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (activeHospital.isCritical) (if (isDark) androidx.compose.ui.graphics.Color(0xFFFCA5A5) else StatusRed) else (if (isDark) androidx.compose.ui.graphics.Color(0xFF86EFAC) else StatusGreen),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (activeHospital.contactPhone != null) {
                                            OutlinedButton(
                                                onClick = {
                                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${activeHospital.contactPhone}"))
                                                    context.startActivity(dialIntent)
                                                },
                                                shape = RoundedCornerShape(4.dp),
                                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                modifier = Modifier.weight(1f).height(32.dp)
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(LanguageManager.s("कॉल करें", "Call"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                val uri = Uri.parse("geo:${activeHospital.latitude},${activeHospital.longitude}?q=${Uri.encode(activeHospital.name)}")
                                                val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                                                try {
                                                    context.startActivity(intent)
                                                } catch (_: Exception) {
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                                }
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1f).height(32.dp)
                                        ) {
                                            Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(LanguageManager.s("नेविगेशन", "Navigate"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }

                                        Button(
                                            onClick = { screenMode = MapScreenMode.SPLIT },
                                            shape = RoundedCornerShape(4.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(1.2f).height(32.dp)
                                        ) {
                                            Text(LanguageManager.s("सूची देखें ➔", "View List ➔"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // In LIST_ONLY mode: Show compact Corridor Strip with button to view map
            Surface(
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(StatusGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${LanguageManager.s("सक्रिय कॉरिडोर", "Active Corridor")}: PHC Amer ➔ ${activeHospital?.name ?: "Sawai ManSingh Hospital"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${activeHospital?.distanceKm ?: 10.9} ${LanguageManager.s("किमी", "km")} • ~$estMinutes ${LanguageManager.s("मिनट ग्रीन लाइट क्लीयरेंस", "min green corridor clearance")}",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    TextButton(
                        onClick = { screenMode = MapScreenMode.SPLIT },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(LanguageManager.s("नक्शा देखें ↗", "View Map ↗"), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        // ── 6. Facility Details List (Shown in SPLIT and LIST_ONLY modes) ──
        if (screenMode != MapScreenMode.FULL_MAP) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            LanguageManager.s("लाइव संस्थान डेटा लोड हो रहा है...", "Loading live facility data..."),
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (errorMessage != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = LanguageManager.s("डेटा लोड करने में विफल", "Failed to load live routing data"),
                            fontWeight = FontWeight.Bold,
                            color = StatusRed,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = errorMessage!!, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(onClick = { fetchLiveHospitals(medicineOptions[selectedMedicineIndex].id) }) {
                            Text(LanguageManager.s("पुनः प्रयास करें (Retry)", "Retry Connection"), fontSize = 11.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedMedicineIndex == 0) {
                                    LanguageManager.s("निकटवर्ती चिकित्सा संस्थान (Haversine Proximity)", "Nearby Healthcare Facilities (Haversine Proximity)")
                                } else {
                                    LanguageManager.s("अधिशेष आपूर्ति केंद्र (Surplus Hubs)", "Candidate Surplus Hubs")
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${LanguageManager.s("कुल", "Total")} ${hospitals.size} ${LanguageManager.s("केंद्र", "centers")}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    items(hospitals) { hospital ->
                        val isSelected = selectedHospitalId == hospital.id
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(6.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedHospitalId = hospital.id
                                    animateMapToFacility(hospital.latitude, hospital.longitude)
                                }
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                if (hospital.isCritical) StatusRed.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                                shape = RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.LocalHospital,
                                            contentDescription = null,
                                            tint = if (hospital.isCritical) StatusRed else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(modifier = Modifier.weight(1f)) {
                                        Column {
                                            Text(
                                                text = hospital.name,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = "${hospital.type} • ${hospital.distanceKm} ${LanguageManager.s("किमी (पीएचसी आमेर से)", "km (from PHC Amer)")}",
                                                fontSize = 10.5.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(2.dp),
                                        color = if (hospital.isCritical) StatusRed.copy(alpha = 0.2f) else StatusGreen.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = hospital.status,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (hospital.isCritical) (if (isDark) androidx.compose.ui.graphics.Color(0xFFFCA5A5) else StatusRed) else (if (isDark) androidx.compose.ui.graphics.Color(0xFF86EFAC) else StatusGreen),
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                if (hospital.contactPhone != null) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "${LanguageManager.s("हेल्पलाइन / संपर्क:", "Helpline / Phone:")} ${hospital.contactPhone}",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            selectedHospitalId = hospital.id
                                            animateMapToFacility(hospital.latitude, hospital.longitude)
                                            if (screenMode == MapScreenMode.LIST_ONLY) {
                                                screenMode = MapScreenMode.SPLIT
                                            }
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(1.1f).height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.onPrimary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            LanguageManager.s("ग्रीन कॉरिडोर रूट", "Green Corridor"),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }

                                    if (hospital.contactPhone != null) {
                                        OutlinedButton(
                                            onClick = {
                                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hospital.contactPhone}"))
                                                context.startActivity(dialIntent)
                                            },
                                            shape = RoundedCornerShape(4.dp),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                            modifier = Modifier.weight(0.7f).height(34.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                LanguageManager.s("कॉल करें", "Call"),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val uri = Uri.parse("geo:${hospital.latitude},${hospital.longitude}?q=${Uri.encode(hospital.name)}")
                                            val intent = Intent(Intent.ACTION_VIEW, uri).apply { setPackage("com.google.android.apps.maps") }
                                            try {
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                            }
                                        },
                                        shape = RoundedCornerShape(4.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.weight(0.9f).height(34.dp)
                                    ) {
                                        Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            LanguageManager.s("नेविगेशन", "Navigate"),
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Client-side Haversine distance calculator in km
private fun calculateHaversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

private fun getFallbackHospitalList(lang: AppLanguage): List<MapHospital> {
    val isHi = lang == AppLanguage.HINDI
    return listOf(
        MapHospital(
            id = "FAC-JPR-001",
            name = if (isHi) "सवाई मानसिंह (SMS) अस्पताल, जयपुर" else "Sawai ManSingh (SMS) Hospital, Jaipur",
            type = if (isHi) "सुपर-स्पेशियलिटी जिला हब" else "Tertiary Super-Specialty Hub",
            latitude = 26.8924,
            longitude = 75.8173,
            distanceKm = 10.9,
            status = if (isHi) "3,731 यूनिट अधिशेष (Surplus)" else "3,731 Units Available",
            isCritical = false,
            availableStock = 3731,
            contactPhone = "0141-2560291"
        ),
        MapHospital(
            id = "FAC-JPR-005",
            name = if (isHi) "आरयूएचएस मेडिकल कॉलेज अस्पताल, जयपुर" else "RUHS Medical College & Hospital, Jaipur",
            type = if (isHi) "राज्य मेडिकल हब" else "State Medical Hub",
            latitude = 26.8042,
            longitude = 75.8315,
            distanceKm = 20.3,
            status = if (isHi) "1,850 यूनिट उपलब्ध" else "1,850 Units Available",
            isCritical = false,
            availableStock = 1850,
            contactPhone = "0141-2795600"
        ),
        MapHospital(
            id = "FAC-JPR-008",
            name = if (isHi) "सामुदायिक स्वास्थ्य केंद्र (CHC) आमेर" else "CHC Amer, Jaipur",
            type = if (isHi) "सामुदायिक स्वास्थ्य केंद्र" else "Community Health Centre",
            latitude = 26.9880,
            longitude = 75.8540,
            distanceKm = 0.8,
            status = if (isHi) "सामान्य स्टॉक" else "Normal Inventory",
            isCritical = false,
            availableStock = 420,
            contactPhone = "0141-2530122"
        ),
        MapHospital(
            id = "FAC-JPR-007",
            name = if (isHi) "सामुदायिक स्वास्थ्य केंद्र (CHC) चौमूं" else "CHC Chomu, Jaipur",
            type = if (isHi) "उत्तरी कॉरिडोर हब" else "Northern Corridor Hub",
            latitude = 27.1685,
            longitude = 75.7235,
            distanceKm = 24.5,
            status = if (isHi) "पर्याप्त भंडार" else "Sufficient Stock",
            isCritical = false,
            availableStock = 890,
            contactPhone = "01423-220015"
        ),
        MapHospital(
            id = "FAC-JPR-011",
            name = if (isHi) "प्राथमिक स्वास्थ्य केंद्र (PHC) आमेर" else "PHC Amer, Jaipur",
            type = if (isHi) "अनुरोधकर्ता केंद्र (मांग)" else "Requesting Origin Facility",
            latitude = 26.9855,
            longitude = 75.8513,
            distanceKm = 0.0,
            status = if (isHi) "अति-संकट (12 यूनिट शेष)" else "Critical Stockout (12 left)",
            isCritical = true,
            availableStock = 12,
            contactPhone = "0141-2530340"
        )
    )
}

