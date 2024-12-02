import pickle
import tensorflow as tf
import pandas as pd
from tensorflow.keras.preprocessing.sequence import pad_sequences

#----------------------------------------------------------------------------------------#

model = tf.keras.models.load_model("models/model_klasifikasi_pidana.h5", compile=False)

# open file tokenizer.pkl
with open('models/tokenizer.pkl', 'rb') as file:
   tokenizer = pickle.load(file)


#----------------------------------------------------------------------------------------#

df_kuhp = pd.read_csv("data/dataset_csv/dataset_kuhp.csv")
df_classfication = pd.read_csv("data/dataset_csv/data_classfication.csv")

sub_classification = list(df_classfication['0'])

def predict_pasal(input_text):
   input_seq = tokenizer.texts_to_sequences([input_text])
   input_padded = pad_sequences(input_seq, maxlen=300, padding='post', truncating='post')
   
   prediction = model.predict(input_padded)
   predicted_class = sub_classification[prediction.argmax()]
   
   pasal = df_kuhp[df_kuhp["SUB_KLASIFIKASI"] == predicted_class]
   pasal = pasal[["PASAL", "ISI_PASAL"]]
   
   return pasal.to_dict('records')

#----------------------------------------------------------------------------------------#
