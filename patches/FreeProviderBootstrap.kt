package com.zhousl.aether.data

/**
 * Built-in free, keyless OpenAI-compatible providers
 * (Pollinations openai / openai-fast and LLM7).
 */
object FreeProviderBootstrap {
    const val POLLINATIONS_OPENAI_ID = "aether-free-pollinations-openai"
    const val POLLINATIONS_FAST_ID = "aether-free-pollinations-openai-fast"
    const val LLM7_ID = "aether-free-llm7"

    fun defaultFreeProviders(): List<LlmProviderConfig> {
        val pollinations = PiProviderCatalog.resolve("pollinations")
        val pollinationsFast = PiProviderCatalog.resolve("pollinations-fast")
        val llm7 = PiProviderCatalog.resolve("llm7")
        return listOf(
            LlmProviderConfig(
                id = POLLINATIONS_OPENAI_ID,
                providerId = "pollinations",
                name = "openai (Pollinations)",
                piProviderId = pollinations.id,
                apiKey = "",
                baseUrl = pollinations.defaultBaseUrl,
                authMethod = ProviderAuthMethod.Ambient,
                modelId = "openai",
                manualModelIds = listOf("openai", "openai-fast"),
                cachedModels = listOf("openai", "openai-fast"),
                enabledModelIds = listOf("openai", "openai-fast"),
                isEnabled = true,
            ),
            LlmProviderConfig(
                id = POLLINATIONS_FAST_ID,
                providerId = "pollinations-fast",
                name = "openai-fast (Pollinations)",
                piProviderId = pollinationsFast.id,
                apiKey = "",
                baseUrl = pollinationsFast.defaultBaseUrl,
                authMethod = ProviderAuthMethod.Ambient,
                modelId = "openai-fast",
                manualModelIds = listOf("openai-fast", "openai"),
                cachedModels = listOf("openai-fast", "openai"),
                enabledModelIds = listOf("openai-fast", "openai"),
                isEnabled = true,
            ),
            LlmProviderConfig(
                id = LLM7_ID,
                providerId = "llm7",
                name = "LLM7 Free",
                piProviderId = llm7.id,
                apiKey = "",
                baseUrl = llm7.defaultBaseUrl,
                authMethod = ProviderAuthMethod.Ambient,
                modelId = llm7.defaultModelId,
                manualModelIds = listOf(llm7.defaultModelId),
                cachedModels = listOf(llm7.defaultModelId),
                enabledModelIds = listOf(llm7.defaultModelId),
                isEnabled = true,
            ),
        )
    }

    fun mergeInto(existing: List<LlmProviderConfig>): List<LlmProviderConfig> {
        val byId = existing.associateBy { it.id }.toMutableMap()
        for (free in defaultFreeProviders()) {
            if (!byId.containsKey(free.id)) {
                byId[free.id] = free
            }
        }
        return byId.values.toList()
    }
}
