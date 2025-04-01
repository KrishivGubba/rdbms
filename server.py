from fastapi import FastAPI, HTTPException, Body
from pydantic import BaseModel
from typing import Optional
import os
from langchain_huggingface import HuggingFaceEndpoint
from langchain.prompts import PromptTemplate
from langchain.chains import LLMChain
from dotenv import load_dotenv

app = FastAPI()

# Pydantic model for the request
class QueryRequest(BaseModel):
    query: str
    model_name: Optional[str] = "mistralai/Mistral-7B-Instruct-v0.2"  # Default model

# Set up Hugging Face API token - you'll need to set this environment variable
# You can get a free token at https://huggingface.co/settings/tokens
# HF_API_TOKEN = os.environ.get("HF_API_TOKEN")
load_dotenv()

HF_API_TOKEN = os.getenv("HF_API_TOKEN")

if not HF_API_TOKEN:
    print("Warning: HF_API_TOKEN environment variable not set. You'll need to set this for production use.")

# Base prompt template for your database query translator
base_prompt = """
You are an expert at decoding natural language into queries for my custom database.

These are the possible commands that a user can make to my database:

PLEASE PAY ATTENTION TO THE SYNTAX, EVERY SPACE AND EVERY COMMA MATTERS

- select:
  format:
    a) select * from <tablename>
    b) select <colname1>,<colname2>,... from <tablename>
  This is used to select data from a specific table.
  Examples:
    - "select * from employees"
    - "select name,salary from employees"
 
- insert:
  format:
    insert into <tablename> values (<val1>, <val2>, ...)
  Every column within the table needs a corresponding value. There are no default values.
  NOTE: if you are using STRING datatype, you do not have to put quotation marks around the
  input.
  NOTE: for the boolean type, the possibilities are true and false. Case matters.
  Example:
    - "insert into employees values 101, John Doe, Engineering, 75000, 2022-03-15"

- create:
  format:
    create table <tablename> <dtype> <colname>, <dtype> <colname>, ...
  The possible datatypes are BOOLEAN, INT, FLOAT and STRING
  If only column names are provided, make an educated guess about appropriate datatypes.
  Example:
    - "create table employees INT id, STRING name, STRING department, INT salary, STRING hire_date"

- list:
  format:
    list
  This will list all tables in the database.

- show:
  format:
    show <tablename>
  This shows the structure (columns and datatypes) of a specific table.

Your task is to take natural language input and convert it to the appropriate database command using ONLY the formats specified above. If the request is ambiguous or can't be properly translated using the available commands, then your output must be EXIT.

Always return ONLY the database command without additional explanation or text.  THIS IS VERY IMPORTANT!!

DO NOT ADD ANY OTHER TEXT, SIMPLY RETURN THE COMMAND!

examples of wrong output:

The correct answer is: select * from employees

example of RIGHT input:

select * from employees

Here is the natural language input from the user:

{query}
"""

@app.get("/translate-query/")
async def translate_query(request: QueryRequest = Body(...)):
    # Check if API token is available
    if not HF_API_TOKEN:
        raise HTTPException(status_code=500, detail="Hugging Face API token not configured")
    
    try:
        # Initialize the Hugging Face model with LangChain
        llm = HuggingFaceEndpoint(
            huggingfacehub_api_token=HF_API_TOKEN,
            repo_id=request.model_name,
            max_length=512,
            temperature=0.1,  # Low temperature for more deterministic outputs
        )
        print(base_prompt)
        # Create the prompt template
        prompt_template = PromptTemplate(
            input_variables=["query"],
            template=base_prompt
        )
        
        # Create the chain
        chain = LLMChain(llm=llm, prompt=prompt_template)
        
        # Run the chain
        result = chain.run(query=request.query)
        
        # Clean up the result (remove any extra whitespace or newlines)
        result = result.strip()
        
        return {"translated_query": result}
    
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error processing query: {str(e)}")

@app.get("/")
async def root():
    return {"message": "Database Query Translator API is running. Use /translate-query/ endpoint."}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=2200)