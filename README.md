# MarkdownPilot 📄

**Beautiful Documents Instantly** — A premium, customizable Markdown-to-Document converter for Android. Convert raw Markdown to professional PDFs, MS Word DOCX, clean web HTML, CSV, or packed ZIP archives instantly and **completely offline**.

## Features

### 🎨 Fully Customized Formatting & Styling
Configure beautiful themes and typography to create professional documents instantly:
- **8 Signature Design Palettes** (Classic Navy, Emerald Garden, Crimson Velvet, Sunset Terracotta, Royal Purple, Charcoal Minimal, Steel Blue, and Warm Amber).
- **Custom Fonts**: Supports modern Sans-Serif, traditional Serif, and tech Monospace typefaces.
- **Custom Margins**: Adjustable page padding (Compact, Normal, Wide).
- **Line Spacing**: Set spacing heights from Single (1.0x) to Double (2.0x).
- **Headers & Footers**: Add custom running headers or signature footers with automated page number counters ("Page X").
- **Table Grid Styles**: Choose striped alternate rows, full borders, colored headers, or clean minimalist grids.
- **Level-2 Pagebreaks**: Automatically insert physical page breaks before level-2 subheadings (`## Headline`).

### 📝 Offline Markdown Parsing
Designed with a bulletproof, lightweight Kotlin compiler that runs locally without internet access:
- Standard Heading hierarchy (`#` to `###`)
- Blockquotes (`> text`) with left colored highlight borders
- Bulleted (`-` / `*`) and numbered (`1.`) lists
- Precompiled and shaded Monospace Code blocks
- Full Markdown table arrays
- Inline image rendering

### 🧠 Free AI-Enriched Drafting (Optional)
- **Google Gemini 2.5 Flash** & **Groq (Llama 3)** integration to draft outlines or enhance raw text.
- Full offline fallback compatibility.

## UI Screens
1. **Markdown Workshop**: A clean, distraction-free markdown text editor with a live lines count, template loaders (Sales Reports, resumes/CVs, SDK integrations), layout style boards, format chips, and download options.
2. **AI Writer Chat**: Conversational drafting assistant.
3. **File Hub**: History log of generated documents with instant opening, viewing, and secure sharing options.

## Tech Stack
- Kotlin + Jetpack Compose + Material 3
- Apache POI (DOCX document models)
- Android native Canvas PdfDocument (High-resolution, multi-page vector PDF compiler)
- Room Database (Local History tracking)
- Hilt (Dependency Injection)
- DataStore (Preferences caching)
