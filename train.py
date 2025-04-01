from dataset import show_examples, select_examples, insert_examples, list_examples, create_examples
import random
from sklearn.model_selection import train_test_split
import typing
import json

def prepShuffle(): 
    all_examples = {}

    all_examples.update(show_examples)
    all_examples.update(select_examples)
    all_examples.update(insert_examples)
    all_examples.update(list_examples)
    all_examples.update(create_examples)

    pairs = list(all_examples.items())
    random.shuffle(pairs)
    
    return pairs

def splitandsave(array): #this should be the data array

    train, test = train_test_split(array, test_size=0.8, train_size=0.2, random_state=41)

    with open("train_data.json", "w") as file:
        json.dump(train, file)

    with open("test_data.json","w") as file:
        json.dump(test, file)
    
pairs = prepShuffle()

print(pairs[0])
    

