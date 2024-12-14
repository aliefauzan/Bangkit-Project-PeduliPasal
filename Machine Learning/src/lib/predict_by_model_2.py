import pickle
import pandas as pd
import tensorflow as tf
from transformers import TFBertModel, AutoTokenizer

#----------------------------------------------------------------------------------------#

# MODEL_PATH = "d:\Ardi\Bangkit-Project-PeduliPasal\Machine Learning\models\model_2.h5"
# LABEL_PATH = "d:\Ardi\Bangkit-Project-PeduliPasal\Machine Learning\models\label_encoder.pkl"

model = tf.keras.models.load_model("models/model_2.h5", custom_objects={
    "TFBertModel": TFBertModel
})

tokenizer = AutoTokenizer.from_pretrained("indolem/indobert-base-uncased")

# open file label_encoder.pkl
with open("models/label_encoder.pkl", 'rb') as file:
   label_encoder = pickle.load(file)

#----------------------------------------------------------------------------------------#

# df_kuhp = pd.read_csv("data/dataset_csv/dataset_kuhp.csv")

def predict_pasal(input_text):
    tokenized = tokenizer(
        input_text, max_length=128, padding="max_length", truncation=True, return_tensors="tf"
    )
   
    prediction = model.predict({
      "input_ids": tokenized['input_ids'],
      "attention_mask": tokenized['attention_mask']
   })

    predicted_pasal = label_encoder.inverse_transform(prediction.argmax(axis=1))
    
    # pasal = df_kuhp[df_kuhp["SUB_KLASIFIKASI"] == predicted_pasal[0]]
    # pasal = pasal[["PASAL", "ISI_PASAL"]]
    
    # return pasal.to_dict('records')
    return predicted_pasal[0]


# print(predict_pasal('terdakwa [Nama Lengkap Terdakwa] dengan sengaja dan tanpa hak mengambil barang milik [Nama Korban], yaitu [Deskripsi Barang yang Dicuri]. Sebelumnya, terdakwa telah merencanakan aksi pencurian dengan membawa peralatan untuk membobol pintu. Setelah berhasil masuk, terdakwa mengambil barang curian dan langsung melarikan diri. Perbuatan terdakwa ini didukung oleh keterangan saksi [Nama Saksi] yang melihat terdakwa keluar dari rumah korban serta barang bukti berupa [Deskripsi Barang Bukti]. Berdasarkan fakta-fakta tersebut, Jaksa Penuntut Umum berpendapat bahwa terdakwa terbukti secara sah dan meyakinkan melakukan tindak pidana pencurian, dan biaya perkara.'))
#----------------------------------------------------------------------------------------#
