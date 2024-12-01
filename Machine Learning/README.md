# Machine Learning PeduliPasal

### Run app

```
python src/app.py
```

#### Endpoint ``` /predict_pasal ```

##### Request 

```
   {
      "text": string
   }
```

* Example
```
   {
      "text": "terdakwa [Nama Lengkap Terdakwa] dengan sengaja dan tanpa hak mengambil barang milik [Nama Korban], yaitu [Deskripsi Barang yang Dicuri]. Sebelumnya, terdakwa telah merencanakan aksi pencurian dengan membawa speralatan untuk membobol pintu. Setelah berhasil masuk, terdakwa mengambil barang curian dan langsung melarikan diri. Perbuatan terdakwa ini didukung oleh keterangan saksi [Nama Saksi] yang melihat terdakwa keluar dari rumah korban serta barang bukti berupa [Deskripsi Barang Bukti]. Berdasarkan fakta-fakta tersebut, Jaksa Penuntut Umum berpendapat bahwa terdakwa terbukti secara sah dan meyakinkan melakukan tindak pidana pencurian sebagaimana diatur dalam Pasal 362 KUHP dan menuntut pidana penjara selama [Lama Hukuman] tahun, denda sebesar [Jumlah Denda], dan biaya perkara."
   }
```

##### Response
```
   {
      "pasal": array,
      "text": string
   }
```

* Example
```
{
  "pasal": [
    {
      "ISI_PASAL": "Barang siapa dalam perkelahian tanding merampas nyawa pihak lawan atau melukai tubuhnya, maka diterapkan ketentuan-ketentuan mengenai pembunuhan berencana, pembunuhan atau penganiayaan: 1. jika persyaratan tidak diatur terlebih dahulu; 2. jika perkelahian tanding tidak dilakukan di hadapan saksi kedua belah pihak; 3. jika pelaku dengan sengaja dan meru gikan pihak lawan, bersalah melakukan perbuatan penipuan atau yang me nyimpang dari persyaratan.",
      "PASAL": "Pasal 185"
    },
    {
      "ISI_PASAL": "(1) Para saksi dan dokter yang menghadiri perkelahian tanding, tidak dipidana. (2) Para saksi diancam: Biro Hukum dan Humas Badan Urusan Administrasi Mahkamah Agung-RI /g3/g3  1. dengan pidana penjara paling lama tiga tahun, jika persyaratan tidak diatur terlebih dahulu, atau jika para saksi menghasut para pihak untuk perkelahian tanding; 2. dengan pidana penjara paling lama empat tahun, jika para saksi dengan sengaja dan merugikan salah satu at au kedua belah pihak, bersalah melakukan perbuatan penipuan atau membiarkan para pihak melakukan perbuatan penipuan, atau membiarkan dilakukan penyimpangan daripada syarat-syarat; 3. ketentuan-ketentuan mengenai pembunuhan berencana, pembunuhan atau penganiayaan diterapkan terhadap saksi dalam perkelahian tanding, di mana satu pihak dirampas nyawanya atau menderita karena dilukai tubuhnya, jika ia dengan sengaja dan merugikan pihak itu bersalah melakukan perbuatan penipuan atau membiarkan penyimpangan dari persyaratan yang merugikan yang dikalahkan atau dilukai.  KEJAHATAN YANG MEMBAHAYAKAN KEAM ANAN UMUM BAGI ORANG ATAU BARANG",
      "PASAL": "Pasal 186"
    },
    {
      "ISI_PASAL": "Barang siapa dengan maksud untuk mengun tungkan diri sendiri atau orang lain secara melawan hukum, dengan memakai nama palsu atau martabat palsu, dengan tipu muslihat, ataupun rangkaian kebohongan, menggerakkan orang lain untuk menyerahkan barang sesuatu kepadanya, atau supaya memberi hutang rnaupun menghapuskan piutang diancam karena penipuan dengan pidana penjara paling lama empat tahun.",
      "PASAL": "Pasal 378"
    },
    {
      "ISI_PASAL": "Perbuatan yang dirumuskan dalam pasal 378, jika barang yang diserahkan itu bukan ternak dan harga daripada barang, hutang atau piutang itu tidak lebih dari dua puluh lima rupiah diancam sebagai penipuan ringan dengan pidana penjara paling lama tiga bulan atau pidana denda paling banyak dua ratus lima puluh rupiah.",
      "PASAL": "Pasal 379"
    },
    {
      "ISI_PASAL": "Diancam dengan pidana penjara paling lama lima tahun enan bulan, barang siapa yang mengurangi dengan penipuan hak-hak pemiutang: 1.  dalam hal pelepasan budel, kepailit an atau penyelesaian atau pada waktu diketahui akan terjadi salah satu di antaranya dan kemudian sungguh disusul dengan pelepasan budel. kepailitan atau penyelesaian menarik barang sesuatu dari budel atau menerima pembayaran baik dari hutang yang tak dapat di tagih maupun yang dapat ditagih, dalam hal terakhir dengan diketahuinya bahwa kepailitan atau penyelesaian penghutang sudah dimohonkan, atau akibat rund ingan dengan penghutang; 2.  di waktu verifikasi piutang-piutang dalam hal pelepasan budel, kepailitan atau penyelesaian. mengaku adan ya piutang yang tak ada atau memperbesar jumlah pi utang yang ada. Biro Hukum dan Humas Badan Urusan Administrasi Mahkamah Agung-RI /g3/g3",
      "PASAL": "Pasal 400"
    }
  ],
  "text": "terdakwa [Nama Lengkap Terdakwa] dengan sengaja dan tanpa hak mengambil barang milik [Nama Korban], yaitu [Deskripsi Barang yang Dicuri]. Sebelumnya, terdakwa telah merencanakan aksi pencurian dengan membawa peralatan untuk membobol pintu. Setelah berhasil masuk, terdakwa mengambil barang curian dan langsung melarikan diri. Perbuatan terdakwa ini didukung oleh keterangan saksi [Nama Saksi] yang melihat terdakwa keluar dari rumah korban serta barang bukti berupa [Deskripsi Barang Bukti]. Berdasarkan fakta-fakta tersebut, Jaksa Penuntut Umum berpendapat bahwa terdakwa terbukti secara sah dan meyakinkan melakukan tindak pidana pencurian sebagaimana diatur dalam Pasal 362 KUHP dan menuntut pidana penjara selama [Lama Hukuman] tahun, denda sebesar [Jumlah Denda], dan biaya perkara."
}s
```

*Note: pasal is Array of Objects


