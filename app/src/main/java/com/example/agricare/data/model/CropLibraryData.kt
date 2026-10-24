package com.example.agricare.data.model

import com.example.R

object CropLibraryData {
    val curatedGuides = listOf(
        CropGuide(
            id = "rice_blast",
            cropName = "Rice",
            diseaseTitle = "Rice Blast",
            scientificName = "Magnaporthe oryzae",
            category = "Fungal Disease",
            summary = "A common fungal disease that affects rice plants, especially during humid weather. It can reduce yield significantly if not controlled early.",
            symptoms = listOf(
                "Spindle-shaped or diamond-shaped lesions with grayish-white centers",
                "Brownish reddish borders on leaves and neck rot on panicles",
                "Severe leaf desiccation giving field a burned or blasted appearance"
            ),
            similarProblems = listOf(
                "Bacterial leaf blight (longer yellow lesions)",
                "Brown spot (oval spots with yellow halo)",
                "Nitrogen leaf scorch"
            ),
            safeManagement = listOf(
                "Avoid excessive chemical nitrogen fertilizer applications",
                "Maintain 2-3 inches of standing water during vegetative stage to inhibit spore propagation",
                "Spray approved fungicide like Tricyclazole 75 WP or Carbendazim at recommended local label rates upon first appearance of leaf lesions",
                "Burn or deeply bury infected crop residue after harvest"
            ),
            prevention = listOf(
                "Plant certified blast-resistant varieties such as BRRI dhan-28 or BRRI dhan-29",
                "Ensure recommended plant-to-plant spacing (20cm x 15cm) for adequate airflow",
                "Treat seeds with hot water (52-54°C for 15 min) or certified bio-fungicide prior to sowing"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "Bangladesh Rice Research Institute (BRRI) & DAE Plant Protection Wing"
        ),
        CropGuide(
            id = "tomato_early_blight",
            cropName = "Tomato",
            diseaseTitle = "Early Blight",
            scientificName = "Alternaria solani",
            category = "Fungal Disease",
            summary = "Destructive foliar disease causing dark concentric target-like rings on older leaves, progressing upwards during warm damp spells.",
            symptoms = listOf(
                "Brown to black spots with concentric rings resembling a target board",
                "Yellowing tissue surrounding lesions causing premature leaf drop",
                "Sunken dark cankers on lower stems and dark rot at stem end of tomato fruit"
            ),
            similarProblems = listOf(
                "Septoria leaf spot (smaller spots with black specks)",
                "Bacterial speck",
                "Tomato Late Blight"
            ),
            safeManagement = listOf(
                "Prune bottom 6-10 inches of leaves once tomato plants are established",
                "Avoid overhead sprinkler watering; irrigate at root base or via furrow",
                "Apply copper-based fungicide or Mancozeb 75 WP according to authorized agricultural label directions"
            ),
            prevention = listOf(
                "Practice 3-year crop rotation away from solanaceous plants (potato, eggplant, chili)",
                "Mulch soil around plants with clean straw to prevent soil-splash pathogens",
                "Disinfect pruning shears between handling different rows"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "Horticulture Research Centre (HRC), BARI"
        ),
        CropGuide(
            id = "potato_late_blight",
            cropName = "Potato",
            diseaseTitle = "Late Blight",
            scientificName = "Phytophthora infestans",
            category = "Fungal Disease",
            summary = "The infamous water-mold pathogen capable of destroying entire potato foliage fields within days in cool foggy winter conditions.",
            symptoms = listOf(
                "Water-soaked dark pale green to purplish-black spots on leaf tips and margins",
                "Delicate white downy fungal mildew visible on leaf undersides in morning dew",
                "Foul rotting odor in severely infected potato fields"
            ),
            similarProblems = listOf(
                "Early blight (concentric rings, drier lesions)",
                "Frost damage",
                "Blackleg"
            ),
            safeManagement = listOf(
                "Inspect fields daily during cloudy or foggy weather with temperatures between 15-22°C",
                "Apply protective systemic fungicides (e.g. Cymoxanil + Mancozeb) before heavy dew or fog sets in",
                "Cut haulms (vines) 10-12 days before tuber harvest if foliar blight appears"
            ),
            prevention = listOf(
                "Plant certified disease-free seed tubers (e.g., Diamant, Cardinal, BARI Alu-7)",
                "High hilling of ridges to create an 8-inch soil barrier over developing tubers",
                "Never dump cull potatoes near active fields"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "Tuber Crops Research Centre (TCRC), BARI"
        ),
        CropGuide(
            id = "eggplant_shoot_borer",
            cropName = "Eggplant",
            diseaseTitle = "Shoot and Fruit Borer",
            scientificName = "Leucinodes orbonalis",
            category = "Pest",
            summary = "The most severe pest of eggplant across Bangladesh, attacking tender growing shoots in early growth and boring into fruits later.",
            symptoms = listOf(
                "Drooping, wilting, and dried shoot tips on young brinjal plants",
                "Holes bored into fruits plugged with larval excreta (frass)",
                "Fruit deformity and internal decay making harvest unmarketable"
            ),
            similarProblems = listOf(
                "Bacterial wilt (whole plant wilting without borer holes)",
                "Stem rot"
            ),
            safeManagement = listOf(
                "Clip off and destroy all wilted shoots weekly below the borer entrance point",
                "Install sex pheromone traps (Lucinlure) at 20-25 traps per hectare for pest monitoring",
                "Spray Neem oil (5ml/L water + mild soap) or spinosad under verified agronomist guidance"
            ),
            prevention = listOf(
                "Cultivate tolerant varieties or Bt-Brinjal varieties approved by BARI",
                "Remove and burn plant stalks immediately following the final picking",
                "Intercrop with coriander or fennel to encourage natural parasitoid wasps"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "Entomology Division, BARI Gazipur"
        ),
        CropGuide(
            id = "chili_anthracnose",
            cropName = "Chili",
            diseaseTitle = "Anthracnose / Fruit Rot",
            scientificName = "Colletotrichum capsici",
            category = "Fungal Disease",
            summary = "Affects chili pods during ripening, causing sunken circular lesions with concentric rings of salmon-pink spore masses in moist weather.",
            symptoms = listOf(
                "Circular sunken spots with black margins on ripe red and green chili fruits",
                "Concentric rings with salmon-colored gelatinous spore tendrils",
                "Dieback of twigs starting from tip downward with straw-colored withered bark"
            ),
            similarProblems = listOf(
                "Sunscald (bleached dry papery fruit surface)",
                "Blossom end rot (calcium deficiency)"
            ),
            safeManagement = listOf(
                "Promptly pick and bury diseased fruits away from the crop plot",
                "Spray systemic fungicide (Azoxystrobin or Propiconazole) at fruit-set stage at label dosage",
                "Avoid picking fruits when morning dew is present to prevent spreading spores"
            ),
            prevention = listOf(
                "Use hot-water treated seeds or soak in certified fungicide before nursery sowing",
                "Ensure wide furrow drainage during monsoon rains to avoid standing water",
                "Rotate with non-solanaceous crops such as maize, mustard, or pulses"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "Department of Agricultural Extension (DAE) Bangladesh"
        ),
        CropGuide(
            id = "jute_stem_rot",
            cropName = "Jute",
            diseaseTitle = "Stem Rot",
            scientificName = "Macrophomina phaseolina",
            category = "Fungal Disease",
            summary = "Major fungal disease of deshi and tosha jute in Bangladesh, causing brown lesions on stems and root decay in waterlogged soils.",
            symptoms = listOf(
                "Dark brown oblong lesions on stem collar and upper nodes",
                "Stems crack and shred into thin fibers prematurely with black fungal sclerotia",
                "Leaves turn yellow, wither, and drop prematurely"
            ),
            similarProblems = listOf(
                "Anthracnose of jute",
                "Water stagnation chlorosis"
            ),
            safeManagement = listOf(
                "Improve drainage ditches in the plot to prevent soil saturation",
                "Apply potash fertilizer in split doses to strengthen plant fiber resistance",
                "Spray Carbendazim 50 WP (1g/L water) if lesions appear in juvenile crop"
            ),
            prevention = listOf(
                "Sow certified disease-resistant varieties such as BJRI Deshi Pat-5 or O-9897",
                "Seed treatment with Trichoderma viride or Carbendazim before sowing",
                "Follow crop rotation with transplant Aman rice"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "Bangladesh Jute Research Institute (BJRI)"
        ),
        CropGuide(
            id = "maize_fall_armyworm",
            cropName = "Maize",
            diseaseTitle = "Fall Armyworm",
            scientificName = "Spodoptera frugiperda",
            category = "Pest",
            summary = "Invasive destructive caterpillar attacking maize whorls, tassels, and ears across South Asia.",
            symptoms = listOf(
                "Window-pane feeding damage on young leaves resembling shotgun holes",
                "Large amounts of coarse sawdust-like frass inside the central leaf whorl",
                "Inverted Y-shape mark on caterpillar head and four dots in square on eighth abdominal segment"
            ),
            similarProblems = listOf(
                "Corn earworm",
                "Stem borer"
            ),
            safeManagement = listOf(
                "Handpick and crush egg masses and young caterpillars in smallholder plots",
                "Drop dry sand or wood ash mixed with chili powder into whorls to suffocate young larvae",
                "Apply microbial insecticide (Bacillus thuringiensis or NPV) or Emamectin Benzoate 5 SG at label rate into the whorl"
            ),
            prevention = listOf(
                "Avoid staggered planting dates in the same block",
                "Install sex pheromone traps at sowing to monitor adult moth arrival",
                "Conserve predatory bugs and earwigs by minimizing broad-spectrum synthetic sprays"
            ),
            imageRes = R.drawable.rice_blast_leaf,
            sourceAttribution = "CIMMYT & Bangladesh Agricultural Research Institute (BARI)"
        )
    )
}
