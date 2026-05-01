package com.liftley.habitrek.data.ai

import android.util.Log
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.MessageCallback
import com.google.ai.edge.litertlm.SamplerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val TAG = "GemmaSummarizer"

/**
 * Data-source-level wrapper around the LiteRT LM SDK.
 * Same role as a Room DAO or Retrofit service — raw SDK access.
 * Stateless except for the cached engine instance.
 */
@Singleton
class GemmaSummarizer @Inject constructor() {

    private var engine: Engine? = null

    /**
     * Initializes the engine on IO thread. Must NOT run on main thread.
     * Safe to call multiple times — no-ops if already initialized.
     */
    suspend fun initializeEngine(modelPath: String) = withContext(Dispatchers.IO) {
        if (engine != null) return@withContext

        Log.d(TAG, "Initializing engine...")
        val engineConfig = EngineConfig(
            modelPath = modelPath,
            backend = Backend.CPU(),
            maxNumTokens = 512,
            cacheDir = null
        )
        engine = Engine(engineConfig).also { it.initialize() }
        Log.d(TAG, "Engine initialized successfully")
    }

    /**
     * Runs full inference: create conversation → generate → close conversation.
     * Must call [initializeEngine] first.
     */
    suspend fun generate(prompt: String): String = withContext(Dispatchers.IO) {
        val currentEngine = engine ?: throw IllegalStateException("Engine not initialized!")

        Log.d(TAG, "Creating conversation...")
        val oneShotMemory = currentEngine.createConversation(
            ConversationConfig(
                samplerConfig = SamplerConfig(
                    topK = 16,
                    temperature = 0.4,
                    topP = 0.85,
                )
            )
        )
        Log.d(TAG, "Conversation created, sending message...")

        oneShotMemory.use { oneShotMemory ->
            val finalResult = suspendCancellableCoroutine { continuation ->
                val responseBuilder = StringBuilder()

                oneShotMemory.sendMessageAsync(
                    Contents.of(listOf(Content.Text(prompt))),
                    object : MessageCallback {
                        override fun onMessage(message: Message) {
                            responseBuilder.append(message.toString())
                        }

                        override fun onDone() {
                            continuation.resume(responseBuilder.toString())
                        }

                        override fun onError(throwable: Throwable) {
                            continuation.resumeWithException(throwable)
                        }
                    }
                )
            }

            Log.d(TAG, "Result: ${finalResult.take(50)}...")
            finalResult
        }
    }

    fun close() {
        engine?.close()
        engine = null
    }
}
