## Machine Learning PeduliPasal


## Introduction
This project aims to build a Machine Learning model that can predict the type of criminal offense through inputted cases. This model is expected to help law enforcement in increasing the effectiveness of investigations by utilizing historical data of criminal cases.

## Dataset
The dataset used is an open source dataset containing data on Indonesian court decision documents. The documents are available in xml format. The contents of the xml file are clear text taken from PDF files of court decision documents publicly available on the website [Supreme Court of Indonesia Decisions] 

Link Github: https://github.com/ir-nlp-csui/indo-law/tree/main

Link Mahkamah Agung: https://putusan3.mahkamahagung.go.id/

## Model
<ol>
  <li>
  Input:
    <ul>
      <li>
         <strong>input_ids</strong>: Urutan token yang telah di-encode menjadi bilangan bulat.
      </li>
      <li>
         <strong>attention_mask</strong>: Mask untuk menunjukkan token mana yang valid dan mana yang harus diabaikan.
      </li>
    </ul>
  </li>
  <li>
  Pre-trained IndoBERT:
    <ul>
      <li>
         Model IndoBERT digunakan untuk menghasilkan representasi vektor untuk setiap token dalam input.
      </li>
    </ul>
  </li>
  <li>
  Fully Connected Layers:
    <ul>
      <li>
         Lapisan-lapisan fully-connected digunakan untuk memetakan representasi vektor dari IndoBERT ke ruang fitur yang lebih rendah dan kemudian ke kelas keluaran.
      </li>
      <li>
         Regularisasi L1/L2 digunakan untuk mencegah overfitting.
      </li>
      <li>
        Dropout digunakan untuk mengurangi overfitting.
      </li>
    </ul>
  </li>
  <li>
  Output:
    <ul>
      <li>
         Probabilitas untuk setiap kelas.
      </li>
    </ul>
  </li>
<ol>


#### Run app

```
python src/app.py
```

##### Endpoint ``` /predict_pasal ```

###### Request 

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

###### Response
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
}
```

*Note: pasal is Array of Objects


