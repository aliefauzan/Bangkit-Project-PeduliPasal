from flask import Flask, request, jsonify
from keras.models import load_model

model = load_model('../models/model_klasifikasi_pidana.h5')

app = Flask(__name__)

@app.route("/")
def home():
    return "Hello World!"

@app.route("/predict_pasal", methods=['POST'])
def predict_pasal():
    # model.predict()
    data = request.json
    text = data.get("text")

    print(text)

