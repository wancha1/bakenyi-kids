package com.example.ui.sanctuary

/**
 * Registry containing complete rich educational content for all 36 picture book spreads across all 18 books:
 * - Conversational dialogue and spoken phrases with phonetic pronunciation
 * - Kato the Otter's specific learning challenges and discovery prompts
 * - Traditional lakeside chants, paddling rhythms, and lullabies
 */
data class PageContentDetails(
    val katoPromptLukenye: String,
    val katoPromptEnglish: String,
    val culturalSong: String = "",
    val phrases: List<StorybookPhrase> = emptyList()
)

fun enrichSpreadWithFullPageContent(spread: StorybookSpread): StorybookSpread {
    val details = PAGE_CONTENT_REGISTRY[spread.bookId to spread.pageNumberInBook] ?: return spread
    return spread.copy(
        katoPromptLukenye = details.katoPromptLukenye,
        katoPromptEnglish = details.katoPromptEnglish,
        culturalSong = details.culturalSong,
        phrases = details.phrases
    )
}

val PAGE_CONTENT_REGISTRY: Map<Pair<String, Int>, PageContentDetails> = mapOf(
    // ==========================================
    // 1. GREETINGS (Okulamusa)
    // ==========================================
    ("greetings" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya enjuba n'omuntu alamusa!",
        katoPromptEnglish = "Can you spot the rising sun and the person waving hello?",
        culturalSong = "Wasuze otya nno omugenyi? Nzuuno nze nnyanja Kyoga!",
        phrases = listOf(
            StorybookPhrase("Wasuze otya mwana wange?", "Good morning, my child!", "Wah-soo-zeh oh-tyah mwah-nah wahn-geh?", "👵🏽"),
            StorybookPhrase("Nazuze bulungi ssebo!", "I slept peacefully, kind sir!", "Nah-zoo-zeh boo-loon-jee sseh-boh!", "🧒🏽"),
            StorybookPhrase("Gyebale ko n'omulimu!", "Well done on your morning work!", "Jyeh-bah-leh koh noh-moo-lee-moo!", "🛶")
        )
    ),
    ("greetings" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya omwezi n'omuliro ogw'akawungeezi!",
        katoPromptEnglish = "Find the gentle moon and the warm evening hearth fire!",
        culturalSong = "Sula bulungi sula mirembe, enjuba egudde mu mazzi!",
        phrases = listOf(
            StorybookPhrase("Osiibye otya mukwano?", "How has your afternoon been, friend?", "Oh-see-byeh oh-tyah moo-kwah-noh?", "👨🏽"),
            StorybookPhrase("Nsiibye bulungi, kale tusiibule!", "I had a peaceful day, let us say goodbye!", "Nsee-byeh boo-loon-jee, kah-leh too-see-boo-leh!", "👩🏽"),
            StorybookPhrase("Sula bulungi, tulagabana enkya!", "Sleep peacefully, see you tomorrow at dawn!", "Soo-lah boo-loon-jee, too-lah-gah-bah-nah en-kyah!", "🌙")
        )
    ),

    // ==========================================
    // 2. FAMILY (Amaaka)
    // ==========================================
    ("family" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya maama n'omwana amugonze!",
        katoPromptEnglish = "Can you find mother holding her sweet baby?",
        culturalSong = "Maama wange yampa amabeere, nange nkula mirembe!",
        phrases = listOf(
            StorybookPhrase("Maama nkwagala nnyo!", "Mother, I love you dearly!", "Mah-ah-mah n-kwah-gah-lah nnyoh!", "🧒🏽"),
            StorybookPhrase("Taata anzizeeko okuvuba!", "Father is teaching me how to paddle canoes!", "Tah-ah-tah ahn-zee-zeh-koh oh-koo-voo-bah!", "👦🏽"),
            StorybookPhrase("Muto wange yeebase mirembe!", "My baby sibling is sleeping peacefully!", "Moo-toh wahn-geh yeh-eh-bah-seh mee-rem-beh!", "👶🏽")
        )
    ),
    ("family" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya JjaJja ow'amagezi wansi w'omuti!",
        katoPromptEnglish = "Look for wise Grandparent telling stories under the tree shade!",
        culturalSong = "JjaJja atubuuza enfumo z'ekyalo, ffenna ne tuyiga!",
        phrases = listOf(
            StorybookPhrase("JjaJja tusomere ku nfumo!", "Grandmother, tell us a story of the lake!", "Jjah-jjah too-soh-meh-reh koo n-foo-moh!", "👧🏽"),
            StorybookPhrase("Amagezi ga bajjajja tegakaddiwa!", "The wisdom of elders never grows old!", "Ah-mah-geh-zee gah bah-jjah-jjah teh-gah-kahd-dee-wah!", "👴🏽"),
            StorybookPhrase("Ssenga n'akooja bajjidde waka!", "Paternal aunt and uncle have come to visit home!", "Ssen-gah noh-koh-oh-jah bah-jjee-ddeh wah-kah!", "🤝")
        )
    ),

    // ==========================================
    // 3. HOME & HEARTH (Amaka n'Enju)
    // ==========================================
    ("home" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya ensiimu ey'ettaka n'ekyoto ekyaka!",
        katoPromptEnglish = "Can you spot the clay pot cooking over the fire hearth?",
        culturalSong = "Omuliro gwaka te-te-te, emmere eyidde mu nsiimu!",
        phrases = listOf(
            StorybookPhrase("Omuliro gukuuma ebbugumu liri eka!", "The hearth fire keeps our home cozy and warm!", "Oh-moo-lee-roh goo-koo-mah eb-boo-goo-moo!", "🔥"),
            StorybookPhrase("Ensiimu eriko emmere ennungi!", "The clay pot is cooking delicious food!", "En-see-moo eh-ree-koh em-meh-reh!", "🍲"),
            StorybookPhrase("Omukeeka gwanikiddwa wansi!", "The woven reed mat is spread out on the floor!", "Oh-moo-keh-eh-kah gwah-nee-kee-ddwah!", "🧺")
        )
    ),
    ("home" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya olweyo olweyera olugya!",
        katoPromptEnglish = "Find the grass broom that sweeps the front yard clean!",
        culturalSong = "Yera olugya olweyo lukuume eka nga wayonjo!",
        phrases = listOf(
            StorybookPhrase("Olugya lwayonjebbwa bulungi!", "The courtyard has been swept immaculate and clean!", "Oh-loo-gyah lwah-yohn-jeh-bbwah boo-loon-jee!", "🧹"),
            StorybookPhrase("Eseggwanga likuumye ezaali ennungi!", "The elevated granary protects our dry grain harvest!", "Eh-segg-wahn-gah lee-koo-myeh eh-zah-ah-lee!", "🛖"),
            StorybookPhrase("Omusekuzzo gukuba ebinyebwa mu kinu!", "The pestle crushes peanuts inside the wooden mortar!", "Oh-moo-seh-koo-zzoh goo-koo-bah eh-bee-nyeh-bwah!", "🥣")
        )
    ),

    // ==========================================
    // 4. THE VILLAGE (Ekyalo)
    // ==========================================
    ("village" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya engoma ey'ekyalo eyita abantu!",
        katoPromptEnglish = "Can you spot the great village drum calling neighbors together?",
        culturalSong = "Ggwanga muganzi engoma evuga, mutugume ffenna tusisinkane!",
        phrases = listOf(
            StorybookPhrase("Engoma evuga ekubye abatuuze!", "The drum rhythm resonates to call all neighbors!", "En-goh-mah eh-voo-gah eh-koo-byeh ah-bah-too-oo-zeh!", "🪘"),
            StorybookPhrase("Oluguudo luno lutuuka ku nnyanja!", "This village footpath leads right to the lake landing!", "Oh-loo-goo-doh loo-noh loo-too-oo-kah!", "🛤️"),
            StorybookPhrase("Omukungu ayimiridde n'abantu!", "The respected village elder stands among the people!", "Oh-moo-koon-goo ah-yee-mee-ree-ddeh!", "👑")
        )
    ),
    ("village" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya ekibbo eky'ebimera ku mwalo!",
        katoPromptEnglish = "Find the woven basket filled with fruits at the lakeside dock!",
        culturalSong = "Ku mwalo gwa Bakenyi, ensomba n'ebibala biseera mirembe!",
        phrases = listOf(
            StorybookPhrase("Akatale kano kaliko ensomba empya!", "This market has the freshest fish from this morning's catch!", "Ah-kah-tah-leh kah-noh kah-lee-koh en-sohm-bah!", "🧺"),
            StorybookPhrase("Ekibbo kino kirukiddwa n'obulungi!", "This reed basket was woven with great master craft!", "Eh-kee-bboh kee-noh kee-roo-kee-ddwah!", "🛶"),
            StorybookPhrase("Ono omutunzi wa mukwano!", "This trader is an old and trusted clan friend!", "Oh-noh oh-moo-toon-zee wah moo-kwah-noh!", "🧑🏽")
        )
    ),

    // ==========================================
    // 5. ANIMALS (Ebisolo)
    // ==========================================
    ("animals" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya envyubu eri mu mazzi ne Kato ogonze!",
        katoPromptEnglish = "Spot the hippo in the water and playful Kato the otter!",
        culturalSong = "Envyubu ewugira wansi, ogonze asambala ku ngulu!",
        phrases = listOf(
            StorybookPhrase("Envyubu enene ewuga mu mazzi!", "The big river hippo swims peacefully in the lake!", "En-vyoo-boo eh-neh-neh eh-woo-gah!", "🦛"),
            StorybookPhrase("Kato ogonze anyumirwa nnyo amazzi!", "Kato the otter loves paddling through the cool current!", "Kah-toh oh-gohn-zeh ah-nyoo-mee-rwah!", "🦦"),
            StorybookPhrase("Ekikere kibuuka ku maliba g'amayuzi!", "The little green frog leaps across the floating lily pads!", "Eh-kee-keh-reh kee-boo-oo-kah!", "🐸")
        )
    ),
    ("animals" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya ente ey'amayembe amawanvu!",
        katoPromptEnglish = "Can you find the gentle cow with long sweeping horns?",
        culturalSong = "Ente ziri ku ttale, ziryayo omuddo omugonvu!",
        phrases = listOf(
            StorybookPhrase("Ente erina amayembe amawanvu!", "The Ankole cow has majestic long horns!", "En-teh eh-ree-nah ah-mah-yem-beh!", "🐄"),
            StorybookPhrase("Embuzi emeccera ku musaana!", "The little goat grazes contentedly in the sunshine!", "Em-boo-zee eh-meh-cceh-rah!", "🐐"),
            StorybookPhrase("Embwa y'eka ekuuma obulungi!", "The homestead dog keeps faithful watch day and night!", "Em-bwah yeh-kah eh-koo-mah!", "🐕")
        )
    ),

    // ==========================================
    // 6. BIRDS (Enyonyi)
    // ==========================================
    ("birds" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya Engaali ey'ekitiibwa erina engule!",
        katoPromptEnglish = "Find the majestic Crested Crane wearing its golden feather crown!",
        culturalSong = "Engaali nyonyi y'ekitiibwa, ebuuka n'amawulire g'emirembe!",
        phrases = listOf(
            StorybookPhrase("Engaali ezingulukuka n'engule y'amasanga!", "The crested crane dances with its golden crest feathers!", "En-gah-ah-lee eh-zeen-goo-loo-koo-kah!", "🦩"),
            StorybookPhrase("Empungu ebuukira waggulu mu bbanga!", "The fish eagle soars high in the blue open sky!", "Em-poon-goo eh-boo-oo-kee-rah!", "🦅"),
            StorybookPhrase("Enkonge eyimiridde mu bitooke!", "The white egret stands tall among the river papyrus!", "En-kohn-geh eh-yee-mee-ree-ddeh!", "🦢")
        )
    ),
    ("birds" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya ennyonyi ey'ekisoozi eruka essanja!",
        katoPromptEnglish = "Look for the clever weaver bird weaving its suspended grass nest!",
        culturalSong = "Ennyonyi zikuba ennyimba, ez'omusaana ez'essanyu!",
        phrases = listOf(
            StorybookPhrase("Ekinyonyi kiyimba oluyimba olulungi!", "The little songbird sings a sweet morning melody!", "Eh-kee-nyoh-nyee kee-yeem-bah!", "🐦"),
            StorybookPhrase("Ennyonyi ey'ekisoozi eruka n'obukugu!", "The weaver bird crafts its nest with nimble reeds!", "En-nyoh-nyee eh-roo-kah!", "🪺"),
            StorybookPhrase("Kasuku ayogera ebigambo eby'essanyu!", "The colorful parrot repeats friendly words with joy!", "Kah-soo-koo ah-yoh-geh-rah!", "🦜")
        )
    ),

    // ==========================================
    // 7. FISH & RIVER (Ensomba)
    // ==========================================
    ("fish" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya engege ey'amayanja n'eryato erivugibwa!",
        katoPromptEnglish = "Spot the delicious Tilapia and the carved wooden canoe!",
        culturalSong = "Engege ewuga mpolampola, mukene atangaala ekiro!",
        phrases = listOf(
            StorybookPhrase("Engege eno nnene era nzungu!", "This Tilapia is fresh and bountiful from Lake Kyoga!", "En-geh-geh eh-noh nneh-neh!", "🐟"),
            StorybookPhrase("Eryato lino lyakolebwa mu muti omugumu!", "This canoe was carved from a single sturdy timber tree!", "Eh-ryah-toh lee-noh lyah-koh-leh-bwah!", "🛶"),
            StorybookPhrase("Enkasi ekasuka amazzi mpolampola!", "The wooden paddle slices through the clean lake water!", "En-kah-see eh-kah-soo-kah ah-mah-zzeeh!", "🪵")
        )
    ),
    ("fish" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya ettaala ey'ekiro eyaka ku lyato!",
        katoPromptEnglish = "Can you find the glowing fishing lantern illuminating the canoe at night?",
        culturalSong = "Ttaala z'amawungu zikoleera ekiro, mukene yeeyoleke!",
        phrases = listOf(
            StorybookPhrase("Mukene asisinkana ku ttaala ey'ekiro!", "The silverfish gather toward the warm glowing lantern!", "Moo-keh-neh ah-see-seen-kah-nah!", "✨"),
            StorybookPhrase("Ekitimba kigwereddwa munda mu nnyanja!", "The woven net is cast deep into the calm night water!", "Eh-kee-teem-bah kee-gweh-reh-ddwah!", "🕸️"),
            StorybookPhrase("Omusubi asanyukira omutemwa omungi!", "The seasoned fisherman smiles at the bountiful harvest!", "Oh-moo-soo-bee ah-sahn-yoo-kee-rah!", "🧑🏽‍🌾")
        )
    ),

    // ==========================================
    // 8. NATURE & EARTH (Obutonde)
    // ==========================================
    ("nature" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya olusozi olugulumivu n'ejjinja!",
        katoPromptEnglish = "Find the high green hill and the smooth shoreline stone!",
        culturalSong = "Obutonde bw'abakenyi bwa mutindo, ensi yaffe ewa obulamu!",
        phrases = listOf(
            StorybookPhrase("Ettaka lino lya musenyu n'olubumbiro!", "This fertile earth nurtures sweet plants and clay pots!", "Et-tah-kah lee-noh lyah moo-seh-nyoo!", "🌱"),
            StorybookPhrase("Olusozi lulina amayinja amagumu!", "The ancient hill stands strong with granite boulders!", "Oh-loo-soh-zee loo-lee-nah!", "⛰️"),
            StorybookPhrase("Ejjinja lino liseerera olw'amazzi!", "This stone is polished smooth by the lake waves!", "Ej-jeen-jah lee-noh lee-seh-eh-reh-rah!", "🪨")
        )
    ),
    ("nature" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya amayengo ag'ennyanja n'empewo!",
        katoPromptEnglish = "Can you spot the sparkling water waves and the whispering wind?",
        culturalSong = "Empewo ekunta mu nnyanja, amayengo ne gazina!",
        phrases = listOf(
            StorybookPhrase("Amayengo gasembera ku mwalo!", "The gentle waves roll softly onto the sandy shoreline!", "Ah-mah-yen-goh gah-sem-beh-rah!", "🌊"),
            StorybookPhrase("Empewo ewuuma n'empisa z'obutonde!", "The evening breeze whispers through the lakeside reeds!", "Em-peh-woh eh-woo-oo-mah!", "💨"),
            StorybookPhrase("Omusenyu ogw'ennyanja gwa bweru!", "The beach sand is fine and golden beneath our feet!", "Oh-moo-seh-nyoo gwah bweh-roo!", "🏖️")
        )
    ),

    // ==========================================
    // 9. TREES (Emiti)
    // ==========================================
    ("trees" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya omuti gwa Baobab omugonvu ogukuze!",
        katoPromptEnglish = "Find the giant Baobab tree with its mighty trunk and wide shade!",
        culturalSong = "Omuti omunene gwa baobab, gukuuma ekisiikirize ky'abakulu!",
        phrases = listOf(
            StorybookPhrase("Omuti gwa Baobab gukuze emyaka emingi!", "The Baobab tree has stood watch for hundreds of years!", "Oh-moo-tee gwah Bah-oh-bahb goo-koo-zeh!", "🌳"),
            StorybookPhrase("Ekisiikirize kyagwo kiwonya ebbugumu!", "Its wide green canopy shields children from midday heat!", "Eh-kee-see-kee-ree-zeh kyah-gwoh!", "🍃"),
            StorybookPhrase("Emizi gyagwo giyise wansi mu ttaka!", "Its deep roots seek fresh cool springs underground!", "Eh-mee-zee gyah-gwoh gee-yee-seh!", "🪵")
        )
    ),
    ("trees" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya enkindu ey'amabaale n'amaliba!",
        katoPromptEnglish = "Look for the graceful river palm tree with its slender fronds!",
        culturalSong = "Enkindu ziwuuma ku lukalu, zituwa emiguwa gy'okuvuba!",
        phrases = listOf(
            StorybookPhrase("Enkindu ekula ku mabbali g'amazzi!", "The wild palm flourishes alongside the freshwater marshes!", "En-keen-doo eh-koo-lah!", "🌴"),
            StorybookPhrase("Amakoola gano bagalukamu emikeeka!", "These long palm fronds are woven into colorful sitting mats!", "Ah-mah-koh-oh-lah gah-noh!", "🌿"),
            StorybookPhrase("Omuti gwa MVule gukola ebyato ebiwangaala!", "The sacred MVule timber makes canoes that last generations!", "Oh-moo-tee gwah Em-voo-leh!", "🛶")
        )
    ),

    // ==========================================
    // 10. FRUIT (Ebibala)
    // ==========================================
    ("fruit" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya eyyembe eriwoloma n'ettooke ly'amenvu!",
        katoPromptEnglish = "Can you spot the sweet golden mango and the bunch of yellow bananas?",
        culturalSong = "Ebibala ebiwooma, amayembe n'amenvu, birina omusana!",
        phrases = listOf(
            StorybookPhrase("Eyyembe lino liwoomerera nnyo!", "This sun-ripened mango is dripping with sweet juice!", "Ey-yem-beh lee-noh lee-woh-oh-meh-reh-rah!", "🥭"),
            StorybookPhrase("Amenvu gano geegattidde ku kitooke!", "These sweet bananas grew in a golden cluster in our garden!", "Ah-men-voo gah-noh geh-eh-gah-ttee-ddeh!", "🍌"),
            StorybookPhrase("Abaana balya ebibala ne bafuna amaanyi!", "Children eat fresh lakeside fruits and grow strong and happy!", "Ah-bah-ah-nah bah-lyah eh-bee-bah-lah!", "😋")
        )
    ),
    ("fruit" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya epapaali eddungi n'effene!",
        katoPromptEnglish = "Spot the orange papaya and the giant sweet jackfruit!",
        culturalSong = "Epapaali limyuka munda, effene liwooma n'essanyu!",
        phrases = listOf(
            StorybookPhrase("Epapaali lyengedde era litangaala!", "The ripe papaya is orange and sweet as wild honey!", "Eh-pah-pah-ah-lee lyen-geh-ddeh!", "🍈"),
            StorybookPhrase("Effene lino ly'amaanyi era liwooma!", "This jackfruit is heavy, fragrant, and delicious!", "Ef-feh-neh lee-noh lyah-mah-ah-nyee!", "🍉"),
            StorybookPhrase("Enkenene z'omu ttale zinyumira abato!", "Wild meadow berries are the favorite snack of playing children!", "En-keh-neh-neh zoh-moo ttah-leh!", "🍓")
        )
    ),

    // ==========================================
    // 11. FOOD (Emmere)
    // ==========================================
    ("food" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya amatooke amatokose n'ebinyebwa!",
        katoPromptEnglish = "Find the steaming mashed matooke and rich peanut sauce!",
        culturalSong = "Amatooke n'ebinyebwa, kigandula obulamu bw'abato!",
        phrases = listOf(
            StorybookPhrase("Amatooke gaasibiddwa mu ndagala!", "The green bananas were wrapped in fresh banana leaves to steam!", "Ah-mah-toh-oh-keh gah-see-bee-ddwah!", "🍌"),
            StorybookPhrase("Ebinyebwa bino binnyonnyola emmere!", "This groundnut peanut stew makes every meal rich and savory!", "Eh-bee-nyeh-bwah bee-noh!", "🥜"),
            StorybookPhrase("Tukulembedde okulya n'okwebaza!", "We gather our hands around the basket with thanks before eating!", "Too-koo-lem-beh-ddeh oh-koo-lyah!", "🙏🏽")
        )
    ),
    ("food" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya ensomba ensike n'akasooli akasire!",
        katoPromptEnglish = "Spot the golden fried fish and roasted maize cob!",
        culturalSong = "Ensomba ensike ne kalo k'obulo, tuliire awamu n'essanyu!",
        phrases = listOf(
            StorybookPhrase("Ensomba ensike ewooma nnyo!", "The fried tilapia is crisp on the outside and tender inside!", "En-sohm-bah en-see-keh eh-woh-oh-mah!", "🐟"),
            StorybookPhrase("Akasooli kano kookeddwa ku kyoto!", "This sweet corn cob was roasted over the hearth charcoal!", "Ah-kah-soh-oh-lee kah-noh koh-oh-keh-ddwah!", "🌽"),
            StorybookPhrase("Akawunga k'obulo kampa amaanyi g'okuwuga!", "Millet bread gives our arms great strength to paddle the canoe!", "Ah-kah-woon-gah koh-boo-loh!", "🍞")
        )
    ),

    // ==========================================
    // 12. WATER & LAKE (Amazzi)
    // ==========================================
    ("water" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya ensulo ey'amazzi amayonjo ag'okunywa!",
        katoPromptEnglish = "Look for the natural spring flowing with pure drinking water!",
        culturalSong = "Amazzi marungi amazzi malamu, tusiima ennyanja Kyoga!",
        phrases = listOf(
            StorybookPhrase("Amazzi gano malonje era matonnyeze!", "This spring water is cool, clean, and crystal clear!", "Ah-mah-zzeeh gah-noh mah-lohn-jeh!", "💧"),
            StorybookPhrase("Ekita kino kijjuddemu amazzi g'eka!", "This traditional clay gourd holds refreshing water for our home!", "Eh-kee-tah kee-noh kee-jjee-ddeh-moo!", "🍶"),
            StorybookPhrase("Amazzi ge bulamu bw'abantu b'omwalo!", "Clean water is the precious heartbeat of our lakeside village!", "Ah-mah-zzeeh geh boo-lah-moo!", "✨")
        )
    ),
    ("water" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya amayuzi ag'ebimuli ku nnyanja!",
        katoPromptEnglish = "Find the floating water lilies with their blossoming petals!",
        culturalSong = "Amayuzi gazina mu mazzi, amato gatuuka mirembe!",
        phrases = listOf(
            StorybookPhrase("Amayuzi galiko ebimuli ebyeru!", "The water lilies bloom with radiant white and purple petals!", "Ah-mah-yoo-zee gah-lee-koh eh-bee-moo-lee!", "🪷"),
            StorybookPhrase("Entoloma zikuumye ensomba entono!", "The papyrus reed beds shelter schools of baby fish from danger!", "En-toh-loh-mah zee-koo-myeh!", "🌾"),
            StorybookPhrase("Ennyanja Kyoga etangaala mu nsi!", "Lake Kyoga shimmers like a mirror across our homeland!", "En-nyahn-jah Kyoh-gah eh-tahn-gah-ah-lah!", "🌊")
        )
    ),

    // ==========================================
    // 13. WEATHER & SKY (Obudde)
    // ==========================================
    ("weather" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya enjuba eyaka n'omusoke ogw'amabala!",
        katoPromptEnglish = "Can you spot the bright sun and the colorful rainbow arc?",
        culturalSong = "Enjuba eyaka omusoke guvaayo, langi z'eggulu zisaanawo!",
        phrases = listOf(
            StorybookPhrase("Enjuba eyaka ettangaavu ku makya!", "The bright sun illuminates the morning mist with warmth!", "En-joo-bah eh-yah-kah et-tahn-gah-ah-voo!", "☀️"),
            StorybookPhrase("Omusoke gulinze amabala ag'omutindo!", "The rainbow arches across the sky with seven magnificent bands!", "Oh-moo-soh-keh goo-leen-zeh!", "🌈"),
            StorybookPhrase("Olukuusi luleese empewo ey'emirembe!", "The morning clouds bring gentle shadows and cool breezes!", "Oh-loo-koo-oo-see loo-leh-eh-seh!", "⛅")
        )
    ),
    ("weather" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya amatondo g'enkuba n'emunyeenye z'ekiro!",
        katoPromptEnglish = "Look for falling rain drops and the twinkling night stars!",
        culturalSong = "Enkuba etonnya ttondo-ttondo, emunyeenye z'ekiro zisaanawo!",
        phrases = listOf(
            StorybookPhrase("Enkuba etonnya n'amatondo amayonjo!", "The rain falls in rhythmic drops to feed the thirsty crops!", "En-koo-bah eh-tohn-nyah!", "🌧️"),
            StorybookPhrase("Emunyeenye zitangaala mu kiro ekiziyivu!", "Countless stars sparkle brightly in the clear night sky!", "Eh-moo-nyeh-eh-nyeh zee-tahn-gah-ah-lah!", "✨"),
            StorybookPhrase("Omwezi gummulisa omwalo gwonna!", "The full moon lights up the entire canoe landing like day!", "Oh-mweh-zee goom-moo-lee-sah!", "🌕")
        )
    ),

    // ==========================================
    // 14. COLOURS (Langi)
    // ==========================================
    ("colours" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya langi emmyufu n'eya bbululu ku lyato!",
        katoPromptEnglish = "Spot the earthy red ochre and deep water blue on the painted canoe!",
        culturalSong = "Langi z'ettaka myufu, langi z'ennyanja bbululu!",
        phrases = listOf(
            StorybookPhrase("Ettaka lya lulyo limyuka nnyo!", "The sacred termite clay is vibrant red like a sunset!", "Et-tah-kah lyah loo-lyoh lee-myoo-kah!", "🔴"),
            StorybookPhrase("Amazzi g'ennyanja ga bbululu omutangaavu!", "The lake water reflects a deep and tranquil blue!", "Ah-mah-zzeeh gah bboo-loo-loo!", "🔵"),
            StorybookPhrase("Ejjinja lya kitaka lisiikiddwa mu musana!", "The sun-baked river boulder is rich earthy brown!", "Ej-jeen-jah lyah kee-tah-kah!", "🟤")
        )
    ),
    ("colours" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya langi ya kyenvu n'ey'akiragala ku bimera!",
        katoPromptEnglish = "Can you find sun yellow and leaf green on the lakeside plants?",
        culturalSong = "Amakoola ga kiragala, enjuba ya kyenvu, ffenna tusanyuke!",
        phrases = listOf(
            StorybookPhrase("Amakoola gano ga kiragala omugonvu!", "The lush forest leaves are vibrant emerald green!", "Ah-mah-koh-oh-lah gah kee-rah-gah-lah!", "🟢"),
            StorybookPhrase("Enjuba yaka na kyenvu eky'ekitiibwa!", "The golden morning sun shines with radiant yellow warmth!", "En-joo-bah yah-kah nah kyen-voo!", "🟡"),
            StorybookPhrase("Engule y'engaali ya bweru atangaala!", "The feathered crest of the crane is snow white and pure!", "En-goo-leh yeen-gah-ah-lee yah bweh-roo!", "⚪")
        )
    ),

    // ==========================================
    // 15. NUMBERS (Ebibalo)
    // ==========================================
    ("numbers" to 1) to PageContentDetails(
        katoPromptLukenye = "Bala ebyato ebiwugira ku mazzi 1 okutuuka ku 5!",
        katoPromptEnglish = "Count the canoes paddling on the water from 1 to 5!",
        culturalSong = "Emu, bbiri, ssatu, nnya, ttaano! Ebyato bitaano ku nnyanja!",
        phrases = listOf(
            StorybookPhrase("Eryato limu libunze ku nnyanja!", "One canoe glides serenely upon the calm water! (1)", "Eh-ryah-toh lee-moo lee-boon-zeh!", "🛶"),
            StorybookPhrase("Ebyato bibiri bituuse ku mwalo!", "Two canoes have returned safely to the dock! (2)", "Eh-byah-toh bee-bee-ree bee-too-oo-seh!", "🛶"),
            StorybookPhrase("Ensomba ttaano zikwatiddwa!", "Five glistening fish are held in the reed basket! (5)", "En-sohm-bah ttah-ah-noh zee-kwah-tee-ddwah!", "🐟")
        )
    ),
    ("numbers" to 2) to PageContentDetails(
        katoPromptLukenye = "Bala ensigo n'engoma 6 okutuuka ku 10!",
        katoPromptEnglish = "Count the baobab seeds and village drums from 6 to 10!",
        culturalSong = "Mukaaga, musanvu, munaana, mwenda, ikumi! Ebibalo by'amagezi!",
        phrases = listOf(
            StorybookPhrase("Ensigo mukaaga zisimbibwa mu ttaka!", "Six baobab seeds are planted into the fertile soil! (6)", "En-see-goh moo-kah-ah-gah zee-seem-bee-bwah!", "🌱"),
            StorybookPhrase("Engoma munaana zikubira wamu!", "Eight hollow drums resonate in polyrhythmic cadence! (8)", "En-goh-mah moo-nah-ah-nah zee-koo-bee-rah!", "🪘"),
            StorybookPhrase("Engalo zaffe kkumi zikwata amato!", "Our ten agile fingers hold the canoe paddle steady! (10)", "En-gah-loh zah-ffeh kkoo-mee zee-kwah-tah!", "✋🏽")
        )
    ),

    // ==========================================
    // 16. SONGS & LULLABIES (Ennyimba)
    // ==========================================
    ("songs" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya enkasi eyimba n'akawungeezi!",
        katoPromptEnglish = "Spot the rhythm paddle guiding the fishermen's work chant!",
        culturalSong = "Kasiisa mwana wange kasiisa, ekyato kituuke eka n'essanyu!",
        phrases = listOf(
            StorybookPhrase("Abakasiisa bayimba oluyimba lw'amato!", "The canoe rowers sing their timeless water travel chant!", "Ah-bah-kah-see-ee-sah bah-yeem-bah!", "🛶"),
            StorybookPhrase("Engoma ekwata omutindo gw'enkasi!", "The drum heartbeat keeps the paddles in perfect unison!", "En-goh-mah eh-kwah-tah oh-moo-teen-doh!", "🪘"),
            StorybookPhrase("Oluyimba luleeta amaanyi n'essanyu!", "The rowing song fills tired arms with renewed joy!", "Oh-loo-yeem-bah loo-leh-eh-tah ah-mah-ah-nyee!", "🎶")
        )
    ),
    ("songs" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya omwana alikiddwa n'oluyimba olugonvu!",
        katoPromptEnglish = "Can you find the infant being rocked gently to sleep by a lullaby?",
        culturalSong = "Bambalala mwana wange, nnyanja yeebase, omwezi gukwata olubuto!",
        phrases = listOf(
            StorybookPhrase("Maama ayimbira omwana olugonvu!", "Mother sings a whispering lullaby to soothe her little one!", "Mah-ah-mah ah-yeem-bee-rah oh-mwah-nah!", "👩🏽"),
            StorybookPhrase("Omwana aganzise mu mirembe!", "The baby drifts softly into sweet, tranquil dreams!", "Oh-mwah-nah ah-gahn-zee-seh moo mee-rem-beh!", "👶🏽"),
            StorybookPhrase("Ekiro kituleetedde emirembe n'otulo!", "The starry night wraps the whole village in deep peace!", "Eh-kee-roh kee-too-leh-eh-teh-ddeh!", "🌙")
        )
    ),

    // ==========================================
    // 17. DAILY LIFE (Obulamu)
    // ==========================================
    ("dailylife" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya omuntu anaaba mu mazzi ku makya!",
        katoPromptEnglish = "Spot the person refreshing with cool water at sunrise!",
        culturalSong = "Makya matangaavu, abavubi bavaayo n'ebirooto eby'omutindo!",
        phrases = listOf(
            StorybookPhrase("Tuzuukuse n'enjuba ku mwalo!", "We rise refreshed with the golden sun at the water dock!", "Too-zoo-koo-seh nen-joo-bah!", "🌅"),
            StorybookPhrase("Onaabye mu mazzi ag'ennyanja?", "Have you splashed your face in the fresh lake water?", "Oh-nah-ah-byeh moo mah-zzeeh?", "💧"),
            StorybookPhrase("Tugende tukole n'essanyu leero!", "Let us go work together with bright purpose today!", "Too-gen-deh too-koh-leh nes-sahn-yoo!", "🛶")
        )
    ),
    ("dailylife" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya abaana abasoma enfumo ku kyoto!",
        katoPromptEnglish = "Find the children listening to elder folktales by the cozy fire!",
        culturalSong = "Ekiro kituuse ffenna tweyange, emirembe gibe mu maka gaffe!",
        phrases = listOf(
            StorybookPhrase("Tutudde ku kyoto okuwuliriza enfumo!", "We sit cross-legged by the hearth fire listening to legends!", "Too-too-ddeh koo kyoh-toh oh-koo-woo-lee-ree-zah!", "🔥"),
            StorybookPhrase("Emmere y'akawungeezi etufaananyizza!", "The evening meal brings our entire family close together!", "Em-meh-reh yah-kah-woon-geh-zee!", "🍲"),
            StorybookPhrase("Sula bulungi mwana wange!", "Sleep soundly, my precious child!", "Soo-lah boo-loon-jee mwah-nah wahn-geh!", "🌙")
        )
    ),

    // ==========================================
    // 18. COMMUNITY & UBUNTU (Abantu)
    // ==========================================
    ("community" to 1) to PageContentDetails(
        katoPromptLukenye = "Noonya abakulu n'abavubi abakolera awamu!",
        katoPromptEnglish = "Can you spot the elder storytellers and fishermen working together?",
        culturalSong = "Omuntu ye muntu olw'abantu, Bakenyi ffenna tuli bumu!",
        phrases = listOf(
            StorybookPhrase("Abakulu b'ekyalo batubuulirira amagezi!", "The village elders counsel the youth with great kindness!", "Ah-bah-koo-loo beh-kyah-loh bah-too-boo-oo-lee-ree-rah!", "👴🏽"),
            StorybookPhrase("Tukolere awamu mu mulimu gwa Bulungi Bwansi!", "Let us join hands in communal service to uplift our village!", "Too-koh-leh-reh ah-wah-moo!", "🤝"),
            StorybookPhrase("Omugenyi aweebwa ekitiibwa n'emmere!", "Every traveler is welcomed with honor, water, and warm food!", "Oh-moo-geh-nyee ah-weh-eh-bwah!", "🌟")
        )
    ),
    ("community" to 2) to PageContentDetails(
        katoPromptLukenye = "Noonya akabonero k'obumu n'okugabana!",
        katoPromptEnglish = "Find the joyful emblem of community sharing and unity!",
        culturalSong = "Engalo ezirira awamu tezivaamu bavu, ffenna tuli bumu!",
        phrases = listOf(
            StorybookPhrase("Obumu bwe maanyi g'ekyalo kyaffe!", "Our unity is the unyielding strength of our community!", "Oh-boo-moo bweh mah-ah-nyee!", "💖"),
            StorybookPhrase("Buli omu agabana ku ky'afunye!", "Each household shares its harvest so no neighbor hungers!", "Boo-lee oh-moo ah-gah-bah-nah!", "🎁"),
            StorybookPhrase("Essanyu ly'omu lisanyusa bonna!", "The happiness of one child brings radiant joy to all!", "Es-sahn-yoo lyoh-moo lee-sahn-yoo-sah bohn-nah!", "🎉")
        )
    )
)
