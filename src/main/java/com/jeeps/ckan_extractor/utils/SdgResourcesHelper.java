package com.jeeps.ckan_extractor.utils;

public class SdgResourcesHelper {

    public static String getFileName(String goal) {
        return goal.replace("Goal ", "sdg_icon_");
    }

    public static String getSdgMoreInfoLink(String goal) {
        return String.format("%s%s", "https://sustainabledevelopment.un.org/sdg", goal.replace("Goal ", ""));
    }

    public static String getSdgTitle(String goal) {
        switch (goal.toLowerCase()) {
            case "goal 1":
                return "End poverty in all its forms everywhere";
            case "goal 2":
                return "End hunger, achieve food security and improved nutrition and promote sustainable agriculture";
            case "goal 3":
                return "Ensure healthy lives and promote well-being for all at all ages";
            case "goal 4":
                return "Ensure inclusive and equitable quality education and promote lifelong learning opportunities for all";
            case "goal 5":
                return "Achieve gender equality and empower all women and girls";
            case "goal 6":
                return "Ensure availability and sustainable management of water and sanitation for all";
            case "goal 7":
                return "Ensure access to affordable, reliable, sustainable and modern energy for all";
            case "goal 8":
                return "Promote sustained, inclusive and sustainable economic growth, full and productive employment and decent work for all";
            case "goal 9":
                return "Build resilient infrastructure, promote inclusive and sustainable industrialization and foster innovation";
            case "goal 10":
                return "Reduce inequality within and among countries";
            case "goal 11":
                return "Make cities and human settlements inclusive, safe, resilient and sustainable";
            case "goal 12":
                return "Ensure sustainable consumption and production patterns";
            case "goal 13":
                return "Take urgent action to combat climate change and its impacts";
            case "goal 14":
                return "Conserve and sustainably use the oceans, seas and marine resources for sustainable development";
            case "goal 15":
                return "Protect, restore and promote sustainable use of terrestrial ecosystems, sustainably manage forests, combat desertification, and halt and reverse land degradation and halt biodiversity loss";
            case "goal 16":
                return "Promote peaceful and inclusive societies for sustainable development, provide access to justice for all and build effective, accountable and inclusive institutions at all levels";
            case "goal 17":
                return "Strengthen the means of implementation and revitalize the Global Partnership for Sustainable Development";
        }
        return "";
    }

    public static String getSdgDescription(String goal) {
        switch (goal.toLowerCase()) {
            case "goal 1":
                return "Goal 1 calls for an end to poverty in all its manifestations, including extreme poverty, over the next 15 years. All people everywhere, including the poorest and most vulnerable, should enjoy a basic standard of living and social protection benefits.";
            case "goal 2":
                return "Goal 2 seeks to end hunger and all forms of malnutrition and to achieve sustainable food production by 2030. It is premised on the idea that everyone should have access to sufficient nutritious food, which will require widespread promotion of sustainable agriculture, a doubling of agricultural productivity, increased investments and properly functioning food markets.";
            case "goal 3":
                return "Goal 3 aims to ensure health and well-being for all at all ages by improving reproductive, maternal and child health; ending the epidemics of major communicable diseases; reducing non-communicable and environmental diseases; achieving universal health coverage; and ensuring access to safe, affordable and effective medicines and vaccines for all.";
            case "goal 4":
                return "Goal 4 focuses on the acquisition of foundational and higher-order skills; greater and more equitable access to technical and vocational education and training and higher education; training throughout life; and the knowledge, skills and values needed to function well and contribute to society.";
            case "goal 5":
                return "Goal 5 aims to empower women and girls to reach their full potential, which requires eliminating all forms of discrimination and violence against them, including harmful practices. It seeks to ensure that they have every opportunity for sexual and reproductive health and reproductive rights; receive due recognition for their unpaid work; have full access to productive resources; and enjoy equal participation with men in political, economic and public life.";
            case "goal 6":
                return "Goal 6 goes beyond drinking water, sanitation and hygiene to also address the quality and sustainability of water resources. Achieving this Goal, which is critical to the survival of people and the planet, means expanding international cooperation and garnering the support of local communities in improving water and sanitation management.";
            case "goal 7":
                return "Goal 7 seeks to promote broader energy access and increased use of renewable energy, including through enhanced international cooperation and expanded infrastructure and technology for clean energy.";
            case "goal 8":
                return "Goal 8 aims to provide opportunities for full and productive employment and decent work for all while eradicating forced labour, human trafficking and child labour.";
            case "goal 9":
                return "Goal 9 focuses on the promotion of infrastructure development, industrialization and innovation. This can be accomplished through enhanced international and domestic financial, technological and technical support, research and innovation, and increased access to information and communication technology.";
            case "goal 10":
                return "Goal 10 calls for reducing inequalities in income, as well as those based on sex, age, disability, race, class, ethnicity, religion and opportunity—both within and among countries. It also aims to ensure safe, orderly and regular migration and addresses issues related to representation of developing countries in global decision-making and development assistance.";
            case "goal 11":
                return "Goal 11 aims to renew and plan cities and other human settlements in a way that fosters community cohesion and personal security while stimulating innovation and employment.";
            case "goal 12":
                return "Goal 12 aims to promote sustainable consumption and production patterns through measures such as specific policies and international agreements on the management of materials that are toxic to the environment.";
            case "goal 13":
                return "Climate change presents the single biggest threat to development, and its widespread, unprecedented effects disproportionately burden the poorest and the most vulnerable. Urgent action is needed not only to combat climate change and its impacts, but also to build resilience in responding to climate-related hazards and natural disasters.";
            case "goal 14":
                return "Goal 14 seeks to promote the conservation and sustainable use of marine and coastal ecosystems, prevent marine pollution and increase the economic benefits to small island developing States and LDCs from the sustainable use of marine resources.";
            case "goal 15":
                return "Goal 15 focuses on managing forests sustainably, restoring degraded lands and successfully combating desertification, reducing degraded natural habitats and ending biodiversity loss. All of these efforts in combination will help ensure that livelihoods are preserved for those that depend directly on forests and other ecosystems, that biodiversity will thrive, and that the benefits of these natural resources will be enjoyed for generations to come.";
            case "goal 16":
                return "Goal 16 envisages peaceful and inclusive societies based on respect for human rights, the rule of law, good governance at all levels, and transparent, effective and accountable institutions. Many countries still face protracted violence and armed conflict, and far too many people are poorly supported by weak institutions and lack access to justice, information and other fundamental freedoms.";
            case "goal 17":
                return "The 2030 Agenda requires a revitalized and enhanced global partnership that mobilizes all available resources from Governments, civil society, the private sector, the United Nations system and other actors. Increasing support to developing countries, in particular LDCs, landlocked developing countries and small island developing States is fundamental to equitable progress for all.";
        }
        return "";
    }
}
