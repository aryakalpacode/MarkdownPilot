package com.markdownpilot.app.engine

import android.util.Log
import com.markdownpilot.app.domain.model.*
import com.markdownpilot.app.engine.docs.*
import com.markdownpilot.app.engine.markdown.MarkdownParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocOrchestrator @Inject constructor(
    private val pdfEngine: PdfEngine,
    private val wordEngine: WordEngine,
    private val textDocEngine: TextDocEngine
) {
    companion object { private const val TAG = "DocOrchestrator" }

    private val _state = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val state: StateFlow<GenerationState> = _state

    /** Convert raw markdown string to any supported format with full styling customisation. */
    suspend fun convertMarkdown(
        markdown: String,
        format: DocFormat,
        style: DocStyleConfig = DocStyleConfig()
    ): Result<String> {
        _state.value = GenerationState.Building(format, 0.2f)
        return try {
            val plan = MarkdownParser.parse(markdown)
            _state.value = GenerationState.Building(format, 0.5f)
            
            val filePath = when (format) {
                DocFormat.PDF -> pdfEngine.generate(plan, emptyMap(), style)
                DocFormat.DOCX -> wordEngine.generate(plan, emptyMap(), style)
                DocFormat.HTML -> textDocEngine.generateHtml(plan, emptyMap(), style)
                DocFormat.MD -> textDocEngine.generateMarkdown(plan)
                DocFormat.CSV -> textDocEngine.generateCsv(plan)
                DocFormat.ZIP -> {
                    _state.value = GenerationState.Building(DocFormat.PDF, 0.4f)
                    val pdfPath = pdfEngine.generate(plan, emptyMap(), style)
                    _state.value = GenerationState.Building(DocFormat.DOCX, 0.7f)
                    val docxPath = wordEngine.generate(plan, emptyMap(), style)
                    _state.value = GenerationState.Building(DocFormat.HTML, 0.85f)
                    val htmlPath = textDocEngine.generateHtml(plan, emptyMap(), style)
                    
                    textDocEngine.createZip(listOf(pdfPath, docxPath, htmlPath), plan.title.replace(Regex("[^a-zA-Z0-9]"), "_").take(30))
                }
                else -> pdfEngine.generate(plan, emptyMap(), style)
            }
            
            _state.value = GenerationState.Done(filePath, format, java.io.File(filePath).name)
            Result.success(filePath)
        } catch (e: Exception) {
            Log.e(TAG, "Markdown conversion failed", e)
            _state.value = GenerationState.Error(e.message ?: "Unknown error during conversion")
            Result.failure(e)
        } finally {
            _state.value = GenerationState.Idle
        }
    }

    fun resetState() { _state.value = GenerationState.Idle }
}
