package com.arcane.service

import com.arcane.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class TarGzArchiveService : ArchiveService {
    
    override fun canHandle(type: ArchiveType): Boolean {
        return type == ArchiveType.TAR_GZ || type == ArchiveType.TAR
    }
    
    override suspend fun listEntries(file: File): List<ArchiveEntry> {
        val entries = mutableListOf<ArchiveEntry>()
        
        val inputStream = if (file.name.endsWith(".gz")) {
            GzipCompressorInputStream(FileInputStream(file))
        } else {
            FileInputStream(file)
        }
        
        TarArchiveInputStream(inputStream).use { tarInput ->
            var entry: TarArchiveEntry?
            
            while (tarInput.nextTarEntry.also { entry = it } != null) {
                entry?.let {
                    entries.add(mapTarEntry(it))
                }
            }
        }
        
        return entries
    }
    
    override fun extractArchive(
        file: File,
        outputDir: File,
        selectedEntries: List<String>?
    ): Flow<ExtractionProgress> = flow {
        emit(ExtractionProgress(ExtractionStatus.EXTRACTING, 0f))
        
        try {
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            
            val inputStream = if (file.name.endsWith(".gz")) {
                GzipCompressorInputStream(FileInputStream(file))
            } else {
                FileInputStream(file)
            }
            
            TarArchiveInputStream(inputStream).use { tarInput ->
                var entry: TarArchiveEntry?
                var processedFiles = 0
                var totalFiles: Int
                
                // First pass to count total files
                val allEntries = mutableListOf<TarArchiveEntry>()
                while (tarInput.nextTarEntry.also { entry = it } != null) {
                    entry?.let { allEntries.add(it) }
                }
                
                // Filter entries if needed
                val entriesToExtract = if (selectedEntries != null) {
                    allEntries.filter { selectedEntries.contains(it.name) }
                } else {
                    allEntries
                }
                
                totalFiles = entriesToExtract.size
                
                // Second pass with new input stream for extraction
                val extractionInputStream = if (file.name.endsWith(".gz")) {
                    GzipCompressorInputStream(FileInputStream(file))
                } else {
                    FileInputStream(file)
                }
                
                TarArchiveInputStream(extractionInputStream).use { extractTarInput ->
                    while (extractTarInput.nextTarEntry.also { entry = it } != null) {
                        entry?.let { tarEntry ->
                            if (selectedEntries == null || selectedEntries.contains(tarEntry.name)) {
                                emit(ExtractionProgress(
                                    status = ExtractionStatus.EXTRACTING,
                                    progress = processedFiles.toFloat() / totalFiles,
                                    currentFile = tarEntry.name,
                                    totalFiles = totalFiles,
                                    processedFiles = processedFiles
                                ))
                                
                                val outputFile = File(outputDir, tarEntry.name)
                                
                                if (tarEntry.isDirectory) {
                                    outputFile.mkdirs()
                                } else {
                                    outputFile.parentFile?.mkdirs()
                                    
                                    FileOutputStream(outputFile).use { output ->
                                        extractTarInput.copyTo(output)
                                    }
                                }
                                
                                processedFiles++
                            }
                        }
                    }
                }
                
                emit(ExtractionProgress(
                    status = ExtractionStatus.COMPLETED,
                    progress = 1f,
                    totalFiles = totalFiles,
                    processedFiles = processedFiles
                ))
            }
        } catch (e: Exception) {
            emit(ExtractionProgress(
                status = ExtractionStatus.ERROR,
                progress = 0f,
                errorMessage = e.message
            ))
        }
    }
    
    private fun mapTarEntry(entry: TarArchiveEntry): ArchiveEntry {
        return ArchiveEntry(
            name = entry.name.split("/").last(),
            path = entry.name,
            size = entry.size,
            isDirectory = entry.isDirectory,
            lastModified = entry.lastModifiedDate,
            parent = if (entry.name.contains("/")) {
                entry.name.substring(0, entry.name.lastIndexOf("/"))
            } else null
        )
    }
}
