package com.arcane.service

import com.arcane.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.sf.sevenzipjbinding.*
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream
import java.io.File
import java.io.RandomAccessFile
import java.util.*

class SevenZipArchiveService : ArchiveService {
    
    override fun canHandle(type: ArchiveType): Boolean {
        return type == ArchiveType.SEVEN_Z
    }
    
    override suspend fun listEntries(file: File): List<ArchiveEntry> {
        val entries = mutableListOf<ArchiveEntry>()
        
        RandomAccessFile(file, "r").use { randomAccessFile ->
            val inStream = RandomAccessFileInStream(randomAccessFile)
            val inArchive = SevenZip.openInArchive(null, inStream)
            
            try {
                val itemCount = inArchive.numberOfItems
                
                for (i in 0 until itemCount) {
                    val path = inArchive.getStringProperty(i, PropID.PATH) ?: ""
                    val size = inArchive.getProperty(i, PropID.SIZE) as? Long ?: 0L
                    val isDirectory = inArchive.getProperty(i, PropID.IS_FOLDER) as? Boolean ?: false
                    val lastModified = inArchive.getProperty(i, PropID.LAST_MODIFICATION_TIME) as? Date
                    
                    entries.add(ArchiveEntry(
                        name = path.split("/").last(),
                        path = path,
                        size = size,
                        isDirectory = isDirectory,
                        lastModified = lastModified,
                        parent = if (path.contains("/")) {
                            path.substring(0, path.lastIndexOf("/"))
                        } else null
                    ))
                }
            } finally {
                inArchive.close()
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
            
            RandomAccessFile(file, "r").use { randomAccessFile ->
                val inStream = RandomAccessFileInStream(randomAccessFile)
                val inArchive = SevenZip.openInArchive(null, inStream)
                
                try {
                    val itemCount = inArchive.numberOfItems
                    val indicesToExtract = if (selectedEntries != null) {
                        (0 until itemCount).filter { i ->
                            val path = inArchive.getStringProperty(i, PropID.PATH) ?: ""
                            selectedEntries.contains(path)
                        }.toIntArray()
                    } else {
                        (0 until itemCount).toList().toIntArray()
                    }
                    
                    var processedFiles = 0
                    val totalFiles = indicesToExtract.size
                    
                    inArchive.extract(indicesToExtract, false, object : IArchiveExtractCallback {
                        override fun getStream(index: Int, extractAskMode: ExtractAskMode): ISequentialOutStream? {
                            val path = inArchive.getStringProperty(index, PropID.PATH) ?: ""
                            val isDirectory = inArchive.getProperty(index, PropID.IS_FOLDER) as? Boolean ?: false
                            
                            val outputFile = File(outputDir, path)
                            
                            if (isDirectory) {
                                outputFile.mkdirs()
                                return null
                            }
                            
                            outputFile.parentFile?.mkdirs()
                            
                            return object : ISequentialOutStream {
                                val output = outputFile.outputStream()
                                
                                override fun write(data: ByteArray): Int {
                                    output.write(data)
                                    return data.size
                                }
                            }
                        }
                        
                        override fun prepareOperation(extractAskMode: ExtractAskMode) {}
                        
                        override fun setOperationResult(extractOperationResult: ExtractOperationResult) {
                            processedFiles++
                            // Note: This callback runs synchronously, so we can't emit progress here
                        }
                        
                        override fun setTotal(total: Long) {}
                        
                        override fun setCompleted(completeValue: Long) {}
                    })
                    
                    emit(ExtractionProgress(
                        status = ExtractionStatus.COMPLETED,
                        progress = 1f,
                        totalFiles = totalFiles,
                        processedFiles = processedFiles
                    ))
                } finally {
                    inArchive.close()
                }
            }
        } catch (e: Exception) {
            emit(ExtractionProgress(
                status = ExtractionStatus.ERROR,
                progress = 0f,
                errorMessage = e.message
            ))
        }
    }
}
