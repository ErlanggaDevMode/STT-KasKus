package com.koperasiku.app.presentation.ui.components

import android.content.Context
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.Font
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.koperasiku.app.core.extensions.toIndonesianDateTime
import com.koperasiku.app.core.extensions.toRupiah
import com.koperasiku.app.domain.model.Transaksi
import java.io.File
import java.io.FileOutputStream

object ReceiptPrinter {

    fun generateReceiptPdf(context: Context, transaksi: Transaksi): File? {
        try {
            val fileName = "Struk_${transaksi.nomorTransaksi}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            val document = Document()
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            val fontTitle = Font(Font.FontFamily.HELVETICA, 16f, Font.BOLD, BaseColor.DARK_GRAY)
            val fontBodyBold = Font(Font.FontFamily.HELVETICA, 10f, Font.BOLD, BaseColor.BLACK)
            val fontBody = Font(Font.FontFamily.HELVETICA, 10f, Font.NORMAL, BaseColor.BLACK)
            val fontSmall = Font(Font.FontFamily.HELVETICA, 8f, Font.NORMAL, BaseColor.GRAY)

            val titlePara = Paragraph("KOPERASIKU", fontTitle)
            titlePara.alignment = Element.ALIGN_CENTER
            document.add(titlePara)

            val addressPara = Paragraph("Jl. Raya Koperasi No. 45, Jakarta", fontSmall)
            addressPara.alignment = Element.ALIGN_CENTER
            document.add(addressPara)

            val deviderPara = Paragraph("----------------------------------------------------------------", fontBody)
            deviderPara.alignment = Element.ALIGN_CENTER
            document.add(deviderPara)

            document.add(Paragraph("No Struk: ${transaksi.nomorTransaksi}", fontBody))
            document.add(Paragraph("Tanggal : ${transaksi.createdAt.toIndonesianDateTime()}", fontBody))
            document.add(Paragraph("Kasir   : ${transaksi.userId}", fontBody))
            if (!transaksi.anggotaId.isNullOrEmpty()) {
                document.add(Paragraph("Anggota : ${transaksi.anggotaId}", fontBody))
            }
            document.add(Paragraph("Metode  : ${transaksi.jenisPembayaran}", fontBody))
            document.add(deviderPara)

            val table = PdfPTable(4)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(4f, 1f, 2f, 2f))

            table.addCell(PdfPCell(Paragraph("Nama Produk", fontBodyBold)).apply { border = PdfPCell.NO_BORDER })
            table.addCell(PdfPCell(Paragraph("Qty", fontBodyBold)).apply { border = PdfPCell.NO_BORDER })
            table.addCell(PdfPCell(Paragraph("Harga", fontBodyBold)).apply { border = PdfPCell.NO_BORDER })
            table.addCell(PdfPCell(Paragraph("Subtotal", fontBodyBold)).apply { border = PdfPCell.NO_BORDER })

            transaksi.items.forEach { item ->
                table.addCell(PdfPCell(Paragraph(item.produkNama ?: "Produk", fontBody)).apply { border = PdfPCell.NO_BORDER })
                table.addCell(PdfPCell(Paragraph(item.kuantitas.toString(), fontBody)).apply { border = PdfPCell.NO_BORDER })
                table.addCell(PdfPCell(Paragraph(item.hargaSatuan.toRupiah(), fontBody)).apply { border = PdfPCell.NO_BORDER })
                table.addCell(PdfPCell(Paragraph(item.subtotal.toRupiah(), fontBody)).apply { border = PdfPCell.NO_BORDER })
            }
            document.add(table)
            document.add(deviderPara)

            val totalTable = PdfPTable(2)
            totalTable.widthPercentage = 100f
            totalTable.setWidths(floatArrayOf(5f, 5f))

            totalTable.addCell(PdfPCell(Paragraph("TOTAL BELANJA:", fontBodyBold)).apply { border = PdfPCell.NO_BORDER })
            totalTable.addCell(PdfPCell(Paragraph(transaksi.totalBelanja.toRupiah(), fontBodyBold)).apply { border = PdfPCell.NO_BORDER })

            totalTable.addCell(PdfPCell(Paragraph("BAYAR:", fontBody)).apply { border = PdfPCell.NO_BORDER })
            totalTable.addCell(PdfPCell(Paragraph(transaksi.nominalBayar.toRupiah(), fontBody)).apply { border = PdfPCell.NO_BORDER })

            totalTable.addCell(PdfPCell(Paragraph("KEMBALIAN:", fontBody)).apply { border = PdfPCell.NO_BORDER })
            totalTable.addCell(PdfPCell(Paragraph(transaksi.nominalKembali.toRupiah(), fontBody)).apply { border = PdfPCell.NO_BORDER })

            document.add(totalTable)
            document.add(deviderPara)

            val footerPara = Paragraph("Terima Kasih Atas Kunjungan Anda!", fontBodyBold)
            footerPara.alignment = Element.ALIGN_CENTER
            document.add(footerPara)

            document.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
