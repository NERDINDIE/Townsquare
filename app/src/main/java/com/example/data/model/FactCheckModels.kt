package com.example.data.model

enum class MisinformationType(val label: String, val description: String) {
    MISLEADING_CONTEXT("Misleading Context", "Accurate data presented with omitted background or misleading framing."),
    OUTDATED_STATISTIC("Outdated Statistic", "Data or figures that were previously accurate but have since been superseded."),
    FABRICATED_DETAIL("Fabricated Detail", "Assertion lacks empirical evidence or official record verification."),
    FALSE_ATTRIBUTION("False Attribution", "Quote or statement attributed to an incorrect speaker or organization."),
    EXAGGERATED_CLAIM("Exaggerated Claim", "Hyperbolic statement that significantly magnifies actual metrics."),
    UNSUBSTANTIATED_SPECULATION("Unsubstantiated Speculation", "Unverified conjecture presented as established fact.")
}

enum class ClaimVerdictCategory(val label: String) {
    FACTUAL("Factual Claims"),
    MISINFORMATION("Misinformation Claims")
}

data class FactCheckClaim(
    val id: String,
    val claimText: String,
    val isFactual: Boolean,
    val misinformationType: MisinformationType? = null,
    val confidenceScore: Int = 95, // 0 to 100%
    val sourceCitation: String,
    val verdictSummary: String,
    val detailedExplanation: String,
    val correctionOrContext: String? = null
)

data class FactCheckReport(
    val targetId: String,
    val targetTitle: String,
    val targetAuthor: String,
    val checkedAtFormatted: String,
    val totalClaimsCount: Int,
    val factualClaimsCount: Int,
    val misinformationClaimsCount: Int,
    val accuracyPercentage: Int,
    val claims: List<FactCheckClaim>
)
