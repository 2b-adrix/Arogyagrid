package com.example.arogyagrid.presentation.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.arogyagrid.R
import com.example.arogyagrid.presentation.theme.*

/**
 * ── 1. Official Government Promotional & Scheme Awareness Carousel (SWAYAM / UMANG Style) ──
 */
data class CampaignSlide(
    val badge: String,
    val title: String,
    val subtitle: String,
    val actionText: String,
    val route: String,
    val gradientColors: List<Color>,
    val iconRes: Int = R.drawable.ic_emblem_lion,
    val heroIllustrationRes: Int = R.drawable.banner_mndy_healthcare
)

@Composable
fun GovtCampaignCarousel(
    onNavigateToScreen: (String) -> Unit = {}
) {
    val slides = remember {
        listOf(
            CampaignSlide(
                badge = "मुख्यमंत्री निःशुल्क दवा योजना (MNDY)",
                title = "राजस्थान राज्य औषधि एवं आवश्यक आपूर्ति ग्रिड",
                subtitle = "जिले के 15 चिकित्सालयों एवं पीएचसी में 711 आवश्यक दवाएं एवं सर्जिकल सामग्री पूर्णतः निःशुल्क उपलब्ध। लाइव स्टॉक स्थिति मॉनिटर करें।",
                actionText = "लाइव इन्वेंटरी देखें (e-Aushadhi) →",
                route = "inventory",
                gradientColors = listOf(Color(0xFF002244), Color(0xFF003366), Color(0xFF0D47A1)),
                iconRes = R.drawable.ic_emblem_lion,
                heroIllustrationRes = R.drawable.banner_mndy_healthcare
            ),
            CampaignSlide(
                badge = "आपातकालीन औषधि ग्रीन कॉरिडोर",
                title = "सवाई मानसिंह (SMS) ➔ पीएचसी आमेर त्वरित आपूर्ति",
                subtitle = "अतिसार प्रकोप प्रभावित आमेर ब्लॉक हेतु 500 यूनिट ओआरएस का 22 मिनट में आपातकालीन स्थानांतरण ग्रीन कॉरिडोर रूट अधिकृत।",
                actionText = "ग्रीन कॉरिडोर रूट नक्शा खोलें →",
                route = "hospital_map",
                gradientColors = listOf(Color(0xFF8B2500), Color(0xFFB45309), Color(0xFFD97706)),
                iconRes = R.drawable.ic_emblem_lion,
                heroIllustrationRes = R.drawable.banner_green_corridor
            ),
            CampaignSlide(
                badge = "इलेक्ट्रॉनिक वैक्सीन इंटेलिजेंस नेटवर्क (eVIN)",
                title = "आईओटी कोल्ड-चेन रियल-टाइम तापमान ट्रैकिंग",
                subtitle = "जयपुर जिले के समस्त आईएलआर एवं डीप फ्रीजर का 24x7 ऑटोमेटेड थर्मल मॉनिटरिंग। तापमान विचलन पर तत्काल ऑटो-अलर्ट।",
                actionText = "कोल्ड-चेन स्थिति जांचें →",
                route = "cold_chain",
                gradientColors = listOf(Color(0xFF064E3B), Color(0xFF046A38), Color(0xFF059669)),
                iconRes = R.drawable.ic_emblem_lion,
                heroIllustrationRes = R.drawable.banner_evin_coldchain
            )
        )
    }

    var currentSlideIndex by remember { mutableIntStateOf(0) }
    val slide = slides[currentSlideIndex]

    Card(
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToScreen(slide.route) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(slide.gradientColors))
                .padding(12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color.White.copy(alpha = 0.2f),
                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = slide.badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Official Seal Image
                    Image(
                        painter = painterResource(id = slide.iconRes),
                        contentDescription = "Emblem",
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = slide.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = slide.subtitle,
                            fontSize = 10.5.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 14.sp,
                            maxLines = 3
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Real Scheme Artwork / Picture
                    Box(
                        modifier = Modifier
                            .size(width = 104.dp, height = 80.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = slide.heroIllustrationRes),
                            contentDescription = "Scheme Illustration Picture",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        slides.indices.forEach { idx ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx == currentSlideIndex) 16.dp else 6.dp, 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (idx == currentSlideIndex) TirangaSaffron else Color.White.copy(alpha = 0.4f))
                                    .clickable { currentSlideIndex = idx }
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = Color.White,
                        modifier = Modifier.clickable { onNavigateToScreen(slide.route) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = slide.actionText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = GovtNavy
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ── 2. Official Citizen & Officer Services Grid (SWAYAM / UMANG Style 8-Department Hub) ──
 */
data class GovtServiceTile(
    val titleHindi: String,
    val subtitleEng: String,
    val icon: ImageVector,
    val badgeColor: Color,
    val iconTint: Color,
    val route: String
)

@Composable
fun GovtServicesGrid(
    onNavigateToScreen: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val services = remember {
        listOf(
            GovtServiceTile("औषधि भंडार", "e-Aushadhi", Icons.Default.Inventory, GovtNavyLight, GovtNavy, "inventory"),
            GovtServiceTile("कोल्ड चेन", "eVIN Monitor", Icons.Default.AcUnit, Color(0xFFE0F2FE), Color(0xFF0369A1), "cold_chain"),
            GovtServiceTile("महामारी सतर्कता", "IDSP Portal", Icons.Default.Coronavirus, StatusRedBg, StatusRed, "outbreak_surveillance"),
            GovtServiceTile("शय्या व आईसीयू", "HMIS Census", Icons.Default.LocalHospital, TirangaSaffronLight, TirangaSaffron, "beds_staff"),
            GovtServiceTile("ग्रीन कॉरिडोर", "Transit Pass", Icons.Default.LocalShipping, TirangaGreenLight, TirangaGreen, "hospital_map"),
            GovtServiceTile("निर्णय सहायता", "CDSS AI", Icons.Default.Healing, GovtNavyLight, GovtNavy, "ai_assistant"),
            GovtServiceTile("पुनर्वितरण", "RMSCL Requisition", Icons.Default.SwapHoriz, StatusOrangeBg, StatusOrange, "redistribution"),
            GovtServiceTile("हेल्पलाइन 104", "Toll-Free Tele", Icons.Default.PhoneInTalk, StatusRedBg, StatusRed, "tel:104")
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(GovtNavy)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "डिजिटल स्वास्थ्य सेवाएं • OFFICIAL SERVICE PORTALS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GovtNavy,
                    letterSpacing = 0.5.sp
                )
            }

            Text(
                text = "सभी सेवाएं (8) →",
                fontSize = 10.sp,
                color = TextMuted,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 4 x 2 Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0..3) {
                        val index = row * 4 + col
                        val service = services[index]
                        Card(
                            shape = RoundedCornerShape(4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, SlateBorder),
                            elevation = CardDefaults.cardElevation(1.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    if (service.route.startsWith("tel:")) {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(service.route))
                                        try { context.startActivity(intent) } catch (_: Exception) {}
                                    } else {
                                        onNavigateToScreen(service.route)
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(service.badgeColor, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = service.icon,
                                        contentDescription = service.titleHindi,
                                        tint = service.iconTint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = service.titleHindi,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextDark,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )

                                Text(
                                    text = service.subtitleEng,
                                    fontSize = 8.sp,
                                    color = TextMuted,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * ── 3. Official Gazette & Government Circulars Notice Board (RMSCL / NHM Rajasthan) ──
 */
data class GazetteCircular(
    val orderNo: String,
    val date: String,
    val subject: String,
    val authority: String
)

@Composable
fun GovtGazetteCirculars() {
    val circulars = remember {
        listOf(
            GazetteCircular(
                orderNo = "पत्रांक: RMSCL/2026/089",
                date = "03 Nov 2026",
                subject = "जयपुर जिले के समस्त प्राथमिक/सामुदायिक स्वास्थ्य केंद्रों में ओआरएस व जिंक का बफर स्टॉक आरक्षित करने बाबत।",
                authority = "प्रबंध निदेशक, आरएमएससीएल"
            ),
            GazetteCircular(
                orderNo = "पत्रांक: IDSP/RJ/114",
                date = "02 Nov 2026",
                subject = "आमेर ब्लॉक में मौसमी संक्रामक बीमारियों की दैनिक सिंड्रोमिक सर्विलांस रिपोर्टिंग अनिवार्य करने के निर्देश।",
                authority = "राज्य महामारी विशेषज्ञ, आईडीएसपी"
            ),
            GazetteCircular(
                orderNo = "पत्रांक: NHM/CC/402",
                date = "01 Nov 2026",
                subject = "इलेक्ट्रॉनिक वैक्सीन इंटेलिजेंस नेटवर्क (eVIN) तापमान विचलन प्रोटोकॉल - मानक संचालन प्रक्रिया (SOP)।",
                authority = "मिशन निदेशक, राष्ट्रीय स्वास्थ्य मिशन"
            )
        )
    }

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(StatusRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "नवीनतम परिपत्र एवं शासनादेश (Official Gazette)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GovtNavy
                    )
                }

                Surface(
                    shape = RoundedCornerShape(3.dp),
                    color = StatusRedBg
                ) {
                    Text(
                        text = "ताज़ा अपडेट्स",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusRed,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            circulars.forEachIndexed { idx, cir ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pdf_document),
                        contentDescription = "PDF Circular",
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = cir.orderNo,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = GovtNavy
                            )
                            Text(
                                text = cir.date,
                                fontSize = 8.5.sp,
                                color = TextMuted
                            )
                        }
                        Text(
                            text = cir.subject,
                            fontSize = 10.5.sp,
                            color = TextDark,
                            lineHeight = 14.sp
                        )
                        Text(
                            text = "हस्ताक्षरकर्ता: ${cir.authority}",
                            fontSize = 8.5.sp,
                            color = TextMuted
                        )
                    }
                }
                if (idx < circulars.size - 1) {
                    HorizontalDivider(
                        color = SlateBorder.copy(alpha = 0.5f),
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * ── 4. Official Government Emergency Helplines & Toll-Free Strip ──
 */
@Composable
fun GovtEmergencyHelplineBanner() {
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Helpline",
                    tint = StatusRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "आपातकालीन स्वास्थ्य हेल्पलाइन नंबर (24x7)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusRed
                    )
                    Text(
                        text = "स्वास्थ्य परामर्श: 104 • एम्बुलेंस सेवा: 108 • राज्य आपदा: 1070",
                        fontSize = 9.sp,
                        color = TextDark
                    )
                }
            }

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:108"))
                    try { context.startActivity(intent) } catch (_: Exception) {}
                },
                colors = ButtonDefaults.buttonColors(containerColor = StatusRed),
                shape = RoundedCornerShape(3.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(28.dp)
            ) {
                Text("108 डायल करें", fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * ── 5. Official NIC & National Health Mission Footer ──
 */
@Composable
fun GovtPortalFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE2E8F0).copy(alpha = 0.5f))
            .border(BorderStroke(0.5.dp, SlateBorder))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_emblem_lion),
                contentDescription = "National Emblem of India",
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_ashoka_chakra),
                contentDescription = "Ashoka Chakra",
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "राष्ट्रीय स्वास्थ्य मिशन • NATIONAL HEALTH MISSION",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GovtNavy,
            letterSpacing = 0.4.sp
        )

        Text(
            text = "चिकित्सा, स्वास्थ्य एवं परिवार कल्याण विभाग • राजस्थान सरकार",
            fontSize = 9.sp,
            color = TextDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        HorizontalDivider(color = SlateBorder, thickness = 0.5.dp, modifier = Modifier.width(160.dp))

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Designed, Developed & Maintained by National Informatics Centre (NIC)",
            fontSize = 8.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Ministry of Electronics & Information Technology • Government of India",
            fontSize = 7.5.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )
        Text(
            text = "पोर्टल संस्करण: v3.4.1 (Rajasthan Health Cloud Instance) • सत्यमेव जयते",
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Medium,
            color = GovtNavy,
            textAlign = TextAlign.Center
        )
    }
}
