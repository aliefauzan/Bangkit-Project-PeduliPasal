from flask import Flask, request, jsonify
import pickle
import pandas as pd
import tensorflow as tf
from transformers import TFBertModel, AutoTokenizer
from google.api_core.client_options import ClientOptions
from google.cloud import discoveryengine_v1 as discoveryengine

# TODO(developer): Uncomment these variables before running the sample.
project_id = "pedulipasal"
location = "global"  # Values: "global", "us", "eu"
engine_id = "agentpedulipasal_xxxxxx"

def vertex_search(
    project_id: str = project_id,
    location: str = location,
    engine_id: str = engine_id,
    search_query: str = "1. Berikan penjelasan singkat: Sampaikan implikasi hukum dari pasal-pasal yang relevan menggunakan bahasa yang jelas, ringkas, dan mudah dipahami. 2. Tampilkan daftar pasal terkait: Berikan daftar pasal-pasal pidana Indonesia yang relevan beserta ancaman pidananya (tahun penjara dan/atau denda), dalam format berikut: - Pasal X: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda]. - Pasal Y: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda]. 3. Kategorisasi sub-klasifikasi: Jika sub-klasifikasi hukum tidak dapat ditentukan, tanyakan lebih lanjut kepada pengguna untuk mendapatkan rincian yang lebih spesifik. 4. jawab hanya dengan menggunakan bahasa indonesia yang mudah di mengerti, jangan gunakan bahasa inggris atau bahasa yang lain",
    model_version: str = "gemini-1.0-pro-002/answer_gen/v1",  # specify the model version here
):
    # Specify the client options based on location
    client_options = (
        ClientOptions(api_endpoint=f"{location}-discoveryengine.googleapis.com")
        if location != "global"
        else None
    )

    # Create the Discovery Engine client
    client = discoveryengine.SearchServiceClient(client_options=client_options)

    # Define serving configuration for search
    serving_config = f"projects/{project_id}/locations/{location}/collections/default_collection/engines/{engine_id}/servingConfigs/default_config"

    # Define content search specifications with the model version
    content_search_spec = discoveryengine.SearchRequest.ContentSearchSpec(
        snippet_spec=discoveryengine.SearchRequest.ContentSearchSpec.SnippetSpec(
            return_snippet=True
        ),
        summary_spec=discoveryengine.SearchRequest.ContentSearchSpec.SummarySpec(
            summary_result_count=1,
            include_citations=True,
            model_spec=discoveryengine.SearchRequest.ContentSearchSpec.SummarySpec.ModelSpec(
                version=model_version  # Set the model version
            )
        )
    )

    # Define the search request
    request = discoveryengine.SearchRequest(
        serving_config=serving_config,
        query=search_query,
        page_size=100,
        content_search_spec=content_search_spec,
        query_expansion_spec=discoveryengine.SearchRequest.QueryExpansionSpec(
            condition=discoveryengine.SearchRequest.QueryExpansionSpec.Condition.AUTO,
        ),
        spell_correction_spec=discoveryengine.SearchRequest.SpellCorrectionSpec(
            mode=discoveryengine.SearchRequest.SpellCorrectionSpec.Mode.AUTO
        ),
    )

    # Perform the search query
    response = client.search(request)

    # Process the response and get the summary
    summaries = []
    try:
        if hasattr(response, 'summary'):
            if hasattr(response.summary, 'summary_with_metadata'):
                summary_with_metadata = response.summary.summary_with_metadata
                
                # Extract the summary text directly
                summary_text = getattr(summary_with_metadata, 'summary', None)
                
                if summary_text:
                    # Add the summary text to the results
                    summaries.append(summary_text)
                else:
                    print("Summary text not found.")
            else:
                print("No summary_with_metadata found.")
        else:
            print("No summary attribute found.")
    except Exception as e:
        print(f"Error accessing summary_with_metadata: {e}")
    
    return summaries







MODEL_PATH = "model_2.h5"
LABEL_PATH = "label_encoder.pkl"
model = tf.keras.models.load_model(MODEL_PATH, custom_objects={
    "TFBertModel": TFBertModel
})

tokenizer = AutoTokenizer.from_pretrained("indolem/indobert-base-uncased")

# open file label_encoder.pkl
with open(LABEL_PATH, 'rb') as file:
   label_encoder = pickle.load(file)


# Fungsi Prediksi Pasal
def predict_pasal(input_text):
    tokenized = tokenizer(
        input_text, max_length=128, padding="max_length", truncation=True, return_tensors="tf"
    )

    prediction = model.predict({
        "input_ids": tokenized['input_ids'],
        "attention_mask": tokenized['attention_mask']
    })

    predicted_pasal = label_encoder.inverse_transform(prediction.argmax(axis=1))
    return predicted_pasal[0]

# Flask App
app = Flask(__name__)

@app.route("/")
def home():
    return "Hello World!"

@app.route("/predict_pasal", methods=['POST'])
def route_predict_pasal():
    data = request.get_json()
    text = data.get("text")

    predicted = predict_pasal(text)
    generative_result = vertex_search(search_query=predicted)

    return jsonify({'text': text, 'generative_result': generative_result})

if __name__ == "__main__":
    app.run(debug=True)