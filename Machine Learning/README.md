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