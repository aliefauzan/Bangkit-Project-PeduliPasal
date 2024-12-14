from typing import List

from google.api_core.client_options import ClientOptions
from google.cloud import discoveryengine_v1 as discoveryengine

# TODO(developer): Uncomment these variables before running the sample.
project_id = "pedulipasal"
location = "global"          # Values: "global", "us", "eu"
engine_id = "agentpedulipasal_1733849041444"
# search_query = "narkotika"

def vertex_search(
    project_id: str=project_id,
    location: str=location,
    engine_id: str=engine_id,
    search_query: str="",
) -> List:
    #  For more information, refer to:
    # https://cloud.google.com/generative-ai-app-builder/docs/locations#specify_a_multi-region_for_your_data_store
    client_options = (
        ClientOptions(api_endpoint=f"{location}-discoveryengine.googleapis.com")
        if location != "global"
        else None
    )

    # Create a client
    client = discoveryengine.SearchServiceClient(client_options=client_options)

    # The full resource name of the search app serving config
    serving_config = f"projects/{project_id}/locations/{location}/collections/default_collection/engines/{engine_id}/servingConfigs/default_config"

    # Optional - only supported for unstructured data: Configuration options for search.
    # Refer to the `ContentSearchSpec` reference for all supported fields:
    # https://cloud.google.com/python/docs/reference/discoveryengine/latest/google.cloud.discoveryengine_v1.types.SearchRequest.ContentSearchSpec
    content_search_spec = discoveryengine.SearchRequest.ContentSearchSpec(
        # For information about snippets, refer to:
        # https://cloud.google.com/generative-ai-app-builder/docs/snippets
        snippet_spec=discoveryengine.SearchRequest.ContentSearchSpec.SnippetSpec(
            return_snippet=True
        ),
        # For information about search summaries, refer to:
        # https://cloud.google.com/generative-ai-app-builder/docs/get-search-summaries
        summary_spec=discoveryengine.SearchRequest.ContentSearchSpec.SummarySpec(
            summary_result_count=5,
            include_citations=True,
            ignore_adversarial_query=True,
            ignore_non_summary_seeking_query=True,
            model_prompt_spec=discoveryengine.SearchRequest.ContentSearchSpec.SummarySpec.ModelPromptSpec(
                preamble="\"Jawablah pertanyaan berikut: '{input}'. Jawab dengan to the point, tidak memberikan catatan, tambahan, atau saran lain. Hanya jawab pertanyaan yang diminta.\"\nSub-klasifikasi: pemalsuan, pencurian, kejahatan-terhadap-keamanan-negara, penghinaan, penadahan, penipuan, tidak-diketahui, kejahatan-terhadap-ketertiban-umum, penggelapan, penganiayaan, lalu-lintas, perjudian, perusakan, kejahatan-terhadap-kesusilaan, pembunuhan, kealfaan-mengakibatkan-kematian-luka, pemerasan-dan-pengancaman, kejahatan-terhadap-asal-usul-perkawinan, senjata-api, mata-uang, sumpah-palsu-dan-keterangan-palsu, kejahatan-terhadap-kemerdekaan-orang-lain, kehutanan, pra-peradilan.\n\nTolong klasifikasikan deskripsi kasus berikut: '{input}' berdasarkan sub-klasifikasi di atas. Output dapat berupa satu atau lebih sub-klasifikasi saja, tanpa tambahan kata atau kalimat lain.\n\nBerikan pengantar bahwa kasus tersebut termasuk sub-klasifikasi dari {sub_klassifikasi}.\nTampilkan juga daftar pasal-pasal pidana Indonesia terbaru yang terkait dengan sub-klasifikasi tersebut, beserta ancaman pidana berupa tahun penjara dan denda. Jangan memberikan catatan, tambahan, atau paragraf lain. Format pasal sebagai berikut:\n\nPasal X: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda].\nPasal Y: [Deskripsi singkat], Ancaman: [Tahun Penjara], Denda: [Jumlah Denda]."
            ),
            model_spec=discoveryengine.SearchRequest.ContentSearchSpec.SummarySpec.ModelSpec(
                version="stable",
            ),
        ),
    )

    # Refer to the `SearchRequest` reference for all supported fields:
    # https://cloud.google.com/python/docs/reference/discoveryengine/latest/google.cloud.discoveryengine_v1.types.SearchRequest
    request = discoveryengine.SearchRequest(
        serving_config=serving_config,
        query=search_query,
        page_size=10,
        content_search_spec=content_search_spec,
        query_expansion_spec=discoveryengine.SearchRequest.QueryExpansionSpec(
            condition=discoveryengine.SearchRequest.QueryExpansionSpec.Condition.AUTO,
        ),
        spell_correction_spec=discoveryengine.SearchRequest.SpellCorrectionSpec(
            mode=discoveryengine.SearchRequest.SpellCorrectionSpec.Mode.AUTO
        ),
    )

    response = client.search(request)

    pasal = convert_to_dict(response)

    return pasal 

def convert_to_dict(response):
    dictionaries_pasal = []

    for result in response:
        document = result.document
        derived_data = document.derived_struct_data

        for key, value in derived_data.items():
            if key == 'snippets':
                for key, value in value[0].items():
                    if value != "NO_SNIPPET_AVAILABLE" or value != "SUCCESS" or value != "No snippet is available for this page.":
                        dictionaries_pasal.append({
                            'value': value
                        })

    return dictionaries_pasal