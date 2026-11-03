package com.example.arogyagrid.domain.ai

data class AiResponse(
    val reply: String,
    val dataSources: List<String>,
    val actionRoute: String? = null,
    val actionLabel: String? = null,
    val confidence: Double = 0.98
)

object HealthcareAiEngine {

    fun generateIntelligentResponse(prompt: String): AiResponse {
        val q = prompt.lowercase().trim()

        return when {
            // ── 1. Surplus & Proximity Transfer for Amer / ORS / Amoxicillin ──
            q.contains("ors") || (q.contains("amer") && q.contains("where")) || q.contains("nearest surplus") -> {
                AiResponse(
                    reply = """
                        [शासकीय ज्ञापन / CDSS MEMORANDUM]
                        पत्रांक: एनएचएम/सीडीएसएस/जयपुर/2026/आपात-041
                        विषय: प्राथमिक स्वास्थ्य केंद्र आमेर (PHC Amer) हेतु ओआरएस (EDL Code: MED-202) आपातकालीन आपूर्ति परामर्श।
                        
                        1. मांग एवं स्थिति विश्लेषण (Facility Situation):
                        • याचक संस्थान: PHC Amer (FAC-JPR-011)
                        • आवश्यक औषधि: ORS Sachets (EDL Code: MED-202)
                        • वर्तमान अवशेष: 12 units (दैनिक औसत खपत दर: 374 यूनिट)
                        • संभावित स्टॉक समाप्ति (Horizon): आगामी 38 hours में पूर्ण अभाव
                        
                        2. अनुमोदित दाता संस्थान (Recommended Donor Facility):
                        • संस्थान का नाम: Sawai ManSingh (SMS) Hospital, Jaipur (FAC-JPR-001)
                        • उपलब्ध बफर स्टॉक: 3,731 units (सुरक्षित निकासी सीमा: 1,200 units)
                        • भौगोलिक दूरी: 10.9 km (हावरसाइन जियो-रूट)
                        • आपातकालीन ट्रांजिट समय: ~22 minutes (जयपुर ग्रीन कॉरिडोर #GC-82)
                        
                        3. क्लिनिकल व प्रशासनिक संस्तुति (Administrative Recommendation):
                        पीएचसी आमेर में अतिसार क्लस्टर नियंत्रण हेतु एसएमएस अस्पताल से 500 units ओआरएस तुरंत अंतर-संस्थानिक स्थानांतरण हेतु मुख्य चिकित्सा एवं स्वास्थ्य अधिकारी (CMHO) के डिजिटल अनुमोदनार्थ प्रस्तावित है।
                    """.trimIndent(),
                    dataSources = listOf("RMSCL e-Aushadhi Database", "NIC Geo-Distance Engine", "IDSP Epidemic Cluster Data"),
                    actionRoute = "hospital_map",
                    actionLabel = "आपातकालीन ग्रीन कॉरिडोर नक्शा देखें →"
                )
            }

            // ── 2. Cold Chain & Temperature Breaches ──
            q.contains("cold") || q.contains("temp") || q.contains("vaccine") || q.contains("excursion") || q.contains("fridge") || q.contains("refrigerator") -> {
                AiResponse(
                    reply = """
                        [तकनीकी बुलेटिन / COLD CHAIN TELEMETRY ADVISORY]
                        पत्रांक: एनएचएम/टीकाकरण/कोल्ड-चेन/जयपुर/2026/088
                        विषय: आईएलआर तापमान विचलन एवं जैविक उत्पाद सुरक्षा प्रतिवेदन।
                        
                        1. विचलन सूचना (Active Critical Excursion):
                        • उपकरण पहचान: आईएलआर यूनिट #1 (CCU-AMR-ILR-01) at PHC Amer
                        • वर्तमान तापमान: 8.4°C (मानक सीमा: +2.0°C to +8.0°C)
                        • विचलन अवधि: 28 मिनट से अनवरत
                        • विद्युत आपूर्ति स्थिति: मुख्य ग्रिड बाधित, सोलर इन्वर्टर कार्यरत
                        • जोखिमग्रस्त वैक्सीन: कोवैक्सीन (450 डोज), रोटावायरस (320 डोज), इंसुलिन (180 शीशियां)
                        
                        2. जिले के अन्य शीत-श्रृंखला केंद्र (Status: 4/5 Units Optimal):
                        • एसएमएस डीप फ्रीजर: -19.4°C (सुरक्षित सीमा: -25°C to -15°C)
                        • एसएमएस सेंट्रल आईएलआर #3: 4.2°C (सुरक्षित)
                        • चौमूं सीएचसी आईएलआर #2: 3.9°C (सुरक्षित)
                        • सांगानेर सीएचसी आईएलआर #1: 5.1°C (सुरक्षित)
                        
                        3. तत्काल मानक संचालन प्रक्रिया (SOP Action):
                        PHC Amer में द्वितीयक जनरेटर सेट चालू किया जाए अथवा एसएमएस केंद्रीय हब से इंसुलेटेड आइस-पैक वैन रवाना कर वैक्सीन को सुरक्षित स्थानांतरित किया जाए।
                    """.trimIndent(),
                    dataSources = listOf("eVIN (Electronic Vaccine Intelligence Network)", "IoT Thermal Gateway"),
                    actionRoute = "cold_chain",
                    actionLabel = "कोल्ड चेन मॉनिटर खोलें →"
                )
            }

            // ── 3. Outbreak & Epidemic Surveillance ──
            q.contains("outbreak") || q.contains("spike") || q.contains("epidemic") || q.contains("diarrhea") || q.contains("cholera") -> {
                AiResponse(
                    reply = """
                        [महामारी निगरानी सतर्कता / EPIDEMIC SURVEILLANCE BULLETIN]
                        पत्रांक: आईडीएसपी/महामारी-अलर्ट/जयपुर/2026/112
                        विषय: आमेर ब्लॉक में तीव्र अतिसार (Acute Diarrheal Disease) प्रकोप चेतावनी।
                        
                        1. सिंड्रोमिक सर्विलांस डेटा (7-Day Rolling Trend):
                        • प्रभावित क्षेत्र: PHC Amer ग्रामीण क्षेत्र (कैचमेंट जनसंख्या: 42,000)
                        • मांग में उछाल: +340% demand surge (सामान्य 85 पैकेट/दिन से बढ़कर 374 पैकेट/दिन)
                        • प्राथमिक संवेदनशील औषधियां: ओआरएस एवं जिंक सल्फेट 20mg गोलियां
                        • स्टॉकout काउंटडाउन: 38 hours शेष
                        
                        2. क्लस्टर 2 निगरानी (Respiratory Illness - Chomu Block):
                        • मांग वृद्धि: +185% (एमोक्सिसिलिन एवं पैरासिटामोल सिरप)
                        • अवशेष समय: 64 hours
                        
                        3. राज्य नियंत्रण कक्ष निर्देश:
                        जिला औषधि भंडार (DDS) एसएमएस से आमेर पीएचसी हेतु बफर कोटा तत्काल रिलीज किया जा चुका है। क्षेत्र में मोबाइल मेडिकल यूनिट (MMU) तैनात करने की अनुशंसा की जाती है।
                    """.trimIndent(),
                    dataSources = listOf("IDSP (Integrated Disease Surveillance Programme)", "IHIP Portal"),
                    actionRoute = "outbreak_surveillance",
                    actionLabel = "महामारी निगरानी डैशबोर्ड खोलें →"
                )
            }

            // ── 4. Amoxicillin / Antibiotic Supply Query ──
            q.contains("amoxicillin") || q.contains("antibiotic") -> {
                AiResponse(
                    reply = """
                        [औषधि उपलब्धता समीक्षा / DRUG INVENTORY AUDIT]
                        पत्रांक: आरएमएससीएल/जयपुर/स्टॉक-समीक्षा/2026/504
                        विषय: एमोक्सिसिलिन 500mg (Amoxicillin 500mg • EDL Code: MED-105) आपूर्ति संतुलन।
                        
                        1. जिलावार स्टॉक स्थिति:
                        • PHC Amer: 45 units शेष (गंभीर कमी • मात्र 2.1 दिन की खपत शेष)
                        • SMS Hospital: 2,800 units अधिशेष (42 दिन का पर्याप्त बफर)
                        • CHC Chomu: 110 units (5.4 दिन का स्टॉक)
                        • Zanana Hospital: 850 units (संतोषजनक बफर)
                        
                        2. पुनर्वितरण प्रस्ताव:
                        एसएमएस अस्पताल से 360 units एमोक्सिसिलिन पीएचसी आमेर को आवंटित करने का प्रस्ताव तैयार है। (प्राथमिकता अंक: 94/100)।
                    """.trimIndent(),
                    dataSources = listOf("RMSCL e-Aushadhi Database", "District Drug Store Records"),
                    actionRoute = "redistribution",
                    actionLabel = "पुनर्वितरण प्रस्ताव समीक्षा करें →"
                )
            }

            // ── 5. District Executive Summary / Jaipur Operations ──
            q.contains("summarize") || q.contains("summary") || q.contains("operations") || q.contains("brief") || q.contains("district") || q.contains("overview") -> {
                AiResponse(
                    reply = """
                        [जिला स्वास्थ्य सारांश / DISTRICT HEALTH OPERATIONS BRIEF]
                        कार्यालय: मुख्य चिकित्सा एवं स्वास्थ्य अधिकारी (CMHO), जयपुर
                        दिनांक: दैनिक प्रशासनिक प्रतिवेदन • समय: लाइव
                        
                        1. संस्थान व शय्या स्थिति:
                        • कुल स्वास्थ्य संस्थान: 15 registered & monitored live (Medical Colleges, DH, CHC, PHC)
                        • कुल शय्या उपयोग दर: 84.03% (4,000 / 4,760 occupied)
                        • उपलब्ध सामान्य व आईसीयू शय्याएं: 760 beds
                        
                        2. औषधि व कोल्ड चेन स्थिति:
                        • अति-गंभीर स्टॉकआउट जोखिम: 2 (PHC Amer: ORS & Amoxicillin)
                        • सक्रिय कोल्ड चेन यूनिट्स: 4/5 मानक सीमा में • 1 तापमान विचलन (Amer ILR 8.4°C)
                        • लंबित अंतर-संस्थानिक स्थानांतरण आदेश: 4 (CMHO अनुमोदन प्रतीक्षित)
                        
                        3. प्राथमिकता निर्देश:
                        क) आमेर पीएचसी हेतु ओआरएस ग्रीन कॉरिडोर पास को अधिकृत करें।
                        ख) आमेर आईएलआर यूनिट हेतु तकनीकी टीम व बैकअप प्रशीतन सुनिश्चित करें।
                    """.trimIndent(),
                    dataSources = listOf("Rajasthan Health Portal", "National Health Mission MIS", "RMSCL Gateway"),
                    actionRoute = "dashboard",
                    actionLabel = "नियंत्रण कक्ष डैशबोर्ड खोलें →"
                )
            }

            // ── 6. Bed Occupancy & Hospital Capacity ──
            q.contains("bed") || q.contains("occupancy") || q.contains("icu") || q.contains("capacity") -> {
                AiResponse(
                    reply = """
                        [शय्या उपलब्धता स्थिति / BED OCCUPANCY INTELLIGENCE]
                        पत्रांक: एनएचएम/अस्पताल-प्रबंधन/जयपुर/2026/301
                        विषय: जयपुर जिले के प्रमुख चिकित्सा संस्थानों में शय्या एवं आईसीयू स्थिति।
                        
                        1. जिला समग्र विवरण:
                        • कुल मॉनिटर शय्याएं: 4,760 beds across 15 facilities
                        • भर्ती मरीज (Occupied): 4,000 beds (84.03% Occupancy)
                        • रिक्त शy्याएं: 760 beds available
                        
                        2. उच्च उपयोग वाले संस्थान (High Occupancy):
                        • SMS Hospital: 1,280 / 1,500 occupied (85.3% • ICU 85% capacity)
                        • JK Lon Pediatric: 690 / 800 occupied (86.2%)
                        • Zanana Maternity: 410 / 500 occupied (82.0%)
                        
                        3. अधिशेष क्षमता वाले संस्थान (Diversion Candidates):
                        • RUHS College: 480 / 700 occupied (220 beds available for diversion)
                        • CHC Sanganer: 60 / 100 occupied (40 beds available)
                    """.trimIndent(),
                    dataSources = listOf("Rajasthan Hospital Management Information System (HMIS)"),
                    actionRoute = "beds_staff",
                    actionLabel = "शय्या व स्टाफ विवरण देखें →"
                )
            }

            // ── 7. Fallback General Healthcare Query ──
            else -> {
                AiResponse(
                    reply = """
                        [एनएचएम निर्णय सहायता प्रणाली / NHM CDSS SYSTEM]
                        पूछताछ विश्लेषण: "$prompt"
                        
                        यह प्रणाली राष्ट्रीय स्वास्थ्य मिशन एवं आरएमएससीएल मानकों के अंतर्गत संचालित है। आप निम्न अधिकृत विषयों पर त्वरित जानकारी प्राप्त कर सकते हैं:
                        • [1] आपातकालीन आपूर्ति व ग्रीन कॉरिडोर: "आमेर के लिए ओआरएस कहां से मिलेगा?"
                        • [2] शीत-श्रृंखला व तापमान विचलन: "कोल्ड चेन तापमान स्थिति और ब्रीच की जांच करें"
                        • [3] महामारी प्रकोप व मांग प्रक्षेपण: "आमेर में अतिसार प्रकोप का विश्लेषण"
                        • [4] अस्पताल शय्या व आईसीयू क्षमता: "जयपुर में बेड ऑक्यूपेंसी व रिक्त शय्याएं"
                        • [5] आवश्यक दवा स्टॉक ऑडिट: "जिले में एमोक्सिसिलिन व जीवनरक्षक दवाओं का स्टॉक"
                        
                        कृपया नीचे दिए गए सुझाव बटन का चयन करें या प्रश्न दर्ज करें।
                    """.trimIndent(),
                    dataSources = listOf("National Health Mission Standard Treatment Guidelines (STG)"),
                    actionRoute = "hospital_map",
                    actionLabel = "सप्लाई रूटिंग मैप खोलें →"
                )
            }
        }
    }
}
