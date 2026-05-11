import json

with open("../data/equippable-items.json") as source_file:
    data = json.load(source_file)
    filtered_data = [item for item in data if item["expansion"] == 11]
    with open("../data/equippable-items-current-expansion.json", "w") as destination_file:
        json.dump(filtered_data, destination_file, indent=4)

